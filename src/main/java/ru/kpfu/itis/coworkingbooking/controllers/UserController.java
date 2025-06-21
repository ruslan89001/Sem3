package ru.kpfu.itis.coworkingbooking.controllers;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kpfu.itis.coworkingbooking.models.User;
import ru.kpfu.itis.coworkingbooking.services.BookingService;
import ru.kpfu.itis.coworkingbooking.services.ReviewService;
import ru.kpfu.itis.coworkingbooking.services.UserService;

@Controller
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "logout", required = false) String logout,
                       Model model) {
        if (error != null) {
            model.addAttribute("error", "Неверное имя пользователя или пароль");
        }
        if (logout != null) {
            model.addAttribute("message", "Вы успешно вышли из системы");
        }
        return "login";
    }
    
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user,
                          BindingResult bindingResult,
                          @RequestParam("password") String password,
                          @RequestParam("confirmPassword") String confirmPassword,
                          RedirectAttributes redirectAttributes) {
        try {
            if (bindingResult.hasErrors()) {
                return "register";
            }
            
            if (!password.equals(confirmPassword)) {
                bindingResult.rejectValue("password", "error.password", "Пароли не совпадают");
                return "register";
            }
            
            if (userService.existsByUsername(user.getUsername())) {
                bindingResult.rejectValue("username", "error.username", "Пользователь с таким именем уже существует");
                return "register";
            }
            
            if (userService.existsByEmail(user.getEmail())) {
                bindingResult.rejectValue("email", "error.email", "Пользователь с таким email уже существует");
                return "register";
            }
            
            User savedUser = userService.registerUser(user.getUsername(), password, user.getEmail());

            try {
                Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), password)
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                redirectAttributes.addFlashAttribute("success", "Регистрация прошла успешно! Добро пожаловать!");
                return "redirect:/profile";
            } catch (Exception e) {
                logger.error("Ошибка при автоматическом входе после регистрации", e);
                redirectAttributes.addFlashAttribute("success", "Регистрация прошла успешно! Теперь вы можете войти в систему.");
                return "redirect:/login";
            }
            
        } catch (Exception e) {
            logger.error("Ошибка при регистрации пользователя: {}", user.getUsername(), e);
            bindingResult.reject("error.registration", "Ошибка при регистрации. Попробуйте еще раз.");
            return "register";
        }
    }
    
    @GetMapping("/profile")
    public String profile(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            model.addAttribute("user", user);
            model.addAttribute("bookings", bookingService.findByUserId(user.getId()));
            model.addAttribute("reviews", reviewService.findByUserId(user.getId()));
            
            return "profile";
        } catch (Exception e) {
            logger.error("Ошибка при загрузке профиля", e);
            return "error";
        }
    }
    
    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam("email") String email,
                               @RequestParam("currentPassword") String currentPassword,
                               @RequestParam("newPassword") String newPassword,
                               RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            if (!email.equals(user.getEmail()) && !userService.existsByEmail(email)) {
                user.setEmail(email);
            }

            if (newPassword != null && !newPassword.trim().isEmpty()) {
                user.setPassword(newPassword);
            }
            
            userService.save(user);
            redirectAttributes.addFlashAttribute("success", "Профиль обновлен успешно");
            
        } catch (Exception e) {
            logger.error("Ошибка при обновлении профиля", e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении профиля");
        }
        
        return "redirect:/profile";
    }
} 