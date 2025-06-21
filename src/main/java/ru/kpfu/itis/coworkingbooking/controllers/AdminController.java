package ru.kpfu.itis.coworkingbooking.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kpfu.itis.coworkingbooking.models.Booking;
import ru.kpfu.itis.coworkingbooking.models.Space;
import ru.kpfu.itis.coworkingbooking.models.User;
import ru.kpfu.itis.coworkingbooking.services.BookingService;
import ru.kpfu.itis.coworkingbooking.services.SpaceService;
import ru.kpfu.itis.coworkingbooking.services.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SpaceService spaceService;
    
    @Autowired
    private BookingService bookingService;
    
    @GetMapping("")
    public String adminDashboard(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            if (user.getRole() != User.Role.ADMIN) {
                return "redirect:/profile";
            }
            
            model.addAttribute("user", user);
            model.addAttribute("totalUsers", userService.findAll().size());
            model.addAttribute("totalSpaces", spaceService.findAll().size());
            model.addAttribute("totalBookings", bookingService.findAll().size());
            
            return "admin/dashboard";
        } catch (Exception e) {
            logger.error("Ошибка при загрузке админ-панели", e);
            return "error";
        }
    }
    
    @GetMapping("/users")
    public String adminUsers(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            if (user.getRole() != User.Role.ADMIN) {
                return "redirect:/profile";
            }
            
            model.addAttribute("user", user);
            model.addAttribute("users", userService.findAll());
            
            return "admin/users";
        } catch (Exception e) {
            logger.error("Ошибка при загрузке списка пользователей", e);
            return "error";
        }
    }
    
    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            User admin = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            if (admin.getRole() != User.Role.ADMIN) {
                return "redirect:/profile";
            }
            
            User userToEdit = userService.findAll().stream()
                    .filter(u -> u.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Пользователь для редактирования не найден"));
            
            model.addAttribute("user", admin);
            model.addAttribute("userToEdit", userToEdit);
            
            return "admin/editUser";
        } catch (Exception e) {
            logger.error("Ошибка при загрузке формы редактирования пользователя", e);
            return "error";
        }
    }
    
    @PostMapping("/users/edit/{id}")
    public String editUser(@PathVariable Long id,
                          @RequestParam("username") String username,
                          @RequestParam("email") String email,
                          @RequestParam("role") String role,
                          @RequestParam(value = "password", required = false) String password,
                          RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String adminUsername = authentication.getName();
            
            User admin = userService.findByUsername(adminUsername)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            if (admin.getRole() != User.Role.ADMIN) {
                return "redirect:/profile";
            }
            
            User userToEdit = userService.findAll().stream()
                    .filter(u -> u.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Пользователь для редактирования не найден"));
            
            userToEdit.setUsername(username);
            userToEdit.setEmail(email);
            userToEdit.setRole(User.Role.valueOf(role.toUpperCase()));
            
            if (password != null && !password.trim().isEmpty()) {
                userToEdit.setPassword(password);
            }
            
            userService.save(userToEdit);
            redirectAttributes.addFlashAttribute("success", "Пользователь успешно обновлен");
            
        } catch (Exception e) {
            logger.error("Ошибка при обновлении пользователя", e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении пользователя");
        }
        
        return "redirect:/admin/users";
    }
    
    @GetMapping("/spaces")
    public String adminSpaces(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            if (user.getRole() != User.Role.ADMIN) {
                return "redirect:/profile";
            }
            
            model.addAttribute("user", user);
            model.addAttribute("spaces", spaceService.findAll());
            
            return "admin/spaces";
        } catch (Exception e) {
            logger.error("Ошибка при загрузке списка пространств", e);
            return "error";
        }
    }
    
    @GetMapping("/spaces/create")
    public String createSpaceForm(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            User admin = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            if (admin.getRole() != User.Role.ADMIN) {
                return "redirect:/profile";
            }
            
            model.addAttribute("user", admin);
            
            return "admin/createSpace";
        } catch (Exception e) {
            logger.error("Ошибка при загрузке формы создания пространства", e);
            return "error";
        }
    }
    
    @PostMapping("/spaces/create")
    public String createSpace(@RequestParam("name") String name,
                             @RequestParam("description") String description,
                             @RequestParam("location") String location,
                             @RequestParam("price") Double price,
                             @RequestParam("availability") Boolean availability,
                             RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String adminUsername = authentication.getName();
            
            User admin = userService.findByUsername(adminUsername)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            if (admin.getRole() != User.Role.ADMIN) {
                return "redirect:/profile";
            }
            
            Space newSpace = new Space();
            newSpace.setName(name);
            newSpace.setDescription(description);
            newSpace.setLocation(location);
            newSpace.setPrice(java.math.BigDecimal.valueOf(price));
            newSpace.setAvailability(availability);
            
            spaceService.save(newSpace);
            redirectAttributes.addFlashAttribute("success", "Пространство успешно создано");
            
        } catch (Exception e) {
            logger.error("Ошибка при создании пространства", e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при создании пространства");
        }
        
        return "redirect:/admin/spaces";
    }
    
    @GetMapping("/spaces/edit/{id}")
    public String editSpaceForm(@PathVariable Long id, Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            User admin = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            if (admin.getRole() != User.Role.ADMIN) {
                return "redirect:/profile";
            }
            
            Space spaceToEdit = spaceService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Пространство для редактирования не найдено"));
            
            model.addAttribute("user", admin);
            model.addAttribute("spaceToEdit", spaceToEdit);
            
            return "admin/editSpace";
        } catch (Exception e) {
            logger.error("Ошибка при загрузке формы редактирования пространства", e);
            return "error";
        }
    }
    
    @PostMapping("/spaces/edit/{id}")
    public String editSpace(@PathVariable Long id,
                           @RequestParam("name") String name,
                           @RequestParam("description") String description,
                           @RequestParam("location") String location,
                           @RequestParam("price") Double price,
                           @RequestParam("availability") Boolean availability,
                           RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String adminUsername = authentication.getName();
            
            User admin = userService.findByUsername(adminUsername)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            if (admin.getRole() != User.Role.ADMIN) {
                return "redirect:/profile";
            }
            
            Space spaceToEdit = spaceService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Пространство для редактирования не найдено"));
            
            spaceToEdit.setName(name);
            spaceToEdit.setDescription(description);
            spaceToEdit.setLocation(location);
            spaceToEdit.setPrice(java.math.BigDecimal.valueOf(price));
            spaceToEdit.setAvailability(availability);
            
            spaceService.save(spaceToEdit);
            redirectAttributes.addFlashAttribute("success", "Пространство успешно обновлено");
            
        } catch (Exception e) {
            logger.error("Ошибка при обновлении пространства", e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении пространства");
        }
        
        return "redirect:/admin/spaces";
    }
    
    @PostMapping("/spaces/toggle/{id}")
    public String toggleSpaceAvailability(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String adminUsername = authentication.getName();
            
            User admin = userService.findByUsername(adminUsername)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            if (admin.getRole() != User.Role.ADMIN) {
                return "redirect:/profile";
            }
            
            spaceService.toggleAvailability(id);
            redirectAttributes.addFlashAttribute("success", "Статус доступности пространства изменен");
            
        } catch (Exception e) {
            logger.error("Ошибка при изменении доступности пространства", e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при изменении доступности пространства");
        }
        
        return "redirect:/admin/spaces";
    }
    
    @GetMapping("/bookings")
    public String adminBookings(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            if (user.getRole() != User.Role.ADMIN) {
                return "redirect:/profile";
            }
            
            model.addAttribute("user", user);
            model.addAttribute("bookings", bookingService.findAll());
            
            return "admin/bookings";
        } catch (Exception e) {
            logger.error("Ошибка при загрузке списка бронирований", e);
            return "error";
        }
    }
    
    @GetMapping("/bookings/edit/{id}")
    public String editBookingForm(@PathVariable Long id, Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            User admin = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            if (admin.getRole() != User.Role.ADMIN) {
                return "redirect:/profile";
            }
            
            Booking bookingToEdit = bookingService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Бронирование для редактирования не найдено"));
            
            model.addAttribute("user", admin);
            model.addAttribute("bookingToEdit", bookingToEdit);
            model.addAttribute("users", userService.findAll());
            model.addAttribute("spaces", spaceService.findAll());
            
            return "admin/editBooking";
        } catch (Exception e) {
            logger.error("Ошибка при загрузке формы редактирования бронирования", e);
            return "error";
        }
    }
    
    @PostMapping("/bookings/edit/{id}")
    public String editBooking(@PathVariable Long id,
                             @RequestParam("userId") Long userId,
                             @RequestParam("spaceId") Long spaceId,
                             @RequestParam("startTime") String startTime,
                             @RequestParam("endTime") String endTime,
                             @RequestParam("status") String status,
                             RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String adminUsername = authentication.getName();
            
            User admin = userService.findByUsername(adminUsername)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            if (admin.getRole() != User.Role.ADMIN) {
                return "redirect:/profile";
            }
            
            Booking bookingToEdit = bookingService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Бронирование для редактирования не найдено"));
            
            User user = userService.findAll().stream()
                    .filter(u -> u.getId().equals(userId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            Space space = spaceService.findById(spaceId)
                    .orElseThrow(() -> new RuntimeException("Пространство не найдено"));
            
            bookingToEdit.setUser(user);
            bookingToEdit.setSpace(space);
            bookingToEdit.setStartTime(java.time.LocalDateTime.parse(startTime));
            bookingToEdit.setEndTime(java.time.LocalDateTime.parse(endTime));
            bookingToEdit.setStatus(Booking.BookingStatus.valueOf(status.toLowerCase()));
            
            bookingService.updateBookingStatus(id, Booking.BookingStatus.valueOf(status.toLowerCase()));
            redirectAttributes.addFlashAttribute("success", "Бронирование успешно обновлено");
            
        } catch (Exception e) {
            logger.error("Ошибка при обновлении бронирования", e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении бронирования");
        }
        
        return "redirect:/admin/bookings";
    }
    
    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            User admin = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            if (admin.getRole() != User.Role.ADMIN) {
                return "redirect:/profile";
            }
            
            userService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Пользователь успешно удален");
            
        } catch (Exception e) {
            logger.error("Ошибка при удалении пользователя", e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении пользователя");
        }
        
        return "redirect:/admin/users";
    }
    
    @PostMapping("/spaces/delete/{id}")
    public String deleteSpace(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            User admin = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            if (admin.getRole() != User.Role.ADMIN) {
                return "redirect:/profile";
            }
            
            spaceService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Пространство успешно удалено");
            
        } catch (Exception e) {
            logger.error("Ошибка при удалении пространства", e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении пространства");
        }
        
        return "redirect:/admin/spaces";
    }
    
    @PostMapping("/bookings/delete/{id}")
    public String deleteBooking(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            User admin = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            if (admin.getRole() != User.Role.ADMIN) {
                return "redirect:/profile";
            }
            
            bookingService.deleteBooking(id);
            redirectAttributes.addFlashAttribute("success", "Бронирование успешно удалено");
            
        } catch (Exception e) {
            logger.error("Ошибка при удалении бронирования", e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении бронирования");
        }
        
        return "redirect:/admin/bookings";
    }
} 