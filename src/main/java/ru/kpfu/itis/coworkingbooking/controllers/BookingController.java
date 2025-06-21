package ru.kpfu.itis.coworkingbooking.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/bookings")
public class BookingController {
    
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SpaceService spaceService;
    
    @GetMapping
    public String myBookings(Model model, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            List<Booking> bookings = bookingService.findByUserId(user.getId());
            model.addAttribute("bookings", bookings);
            
            return "bookings";
        } catch (Exception e) {
            logger.error("Ошибка при загрузке бронирований", e);
            return "error";
        }
    }
    
    @GetMapping("/create/{spaceId}")
    public String createBookingForm(@PathVariable Long spaceId, Model model) {
        try {
            Space space = spaceService.findById(spaceId)
                    .orElseThrow(() -> new RuntimeException("Пространство не найдено"));
            
            model.addAttribute("space", space);
            model.addAttribute("currentDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            
            return "createBooking";
        } catch (Exception e) {
            logger.error("Ошибка при загрузке формы бронирования", e);
            return "error";
        }
    }
    
    @PostMapping("/create/{spaceId}")
    public String createBooking(@PathVariable Long spaceId,
                               @RequestParam("startTime") String startTime,
                               @RequestParam("endTime") String endTime,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            Space space = spaceService.findById(spaceId)
                    .orElseThrow(() -> new RuntimeException("Пространство не найдено"));

            LocalDateTime startDateTime = LocalDateTime.parse(startTime);
            LocalDateTime endDateTime = LocalDateTime.parse(endTime);
            
            Booking booking = bookingService.createBooking(user.getId(), spaceId, startDateTime, endDateTime);
            
            redirectAttributes.addFlashAttribute("success", "Бронирование создано успешно!");
            
        } catch (Exception e) {
            logger.error("Ошибка при создании бронирования", e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при создании бронирования: " + e.getMessage());
        }
        
        return "redirect:/bookings";
    }
    
    @PostMapping("/{bookingId}/cancel")
    public String cancelBooking(@PathVariable Long bookingId,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            Booking booking = bookingService.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Бронирование не найдено"));

            if (!booking.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
                redirectAttributes.addFlashAttribute("error", "У вас нет прав для отмены этого бронирования");
                return "redirect:/bookings";
            }
            
            bookingService.cancelBooking(bookingId);
            
            redirectAttributes.addFlashAttribute("success", "Бронирование отменено успешно!");
            return "redirect:/bookings";
            
        } catch (Exception e) {
            logger.error("Ошибка при отмене бронирования", e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при отмене бронирования");
            return "redirect:/bookings";
        }
    }
    
    @GetMapping("/{bookingId}")
    public String bookingDetails(@PathVariable Long bookingId, Model model, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            Booking booking = bookingService.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Бронирование не найдено"));

            if (!booking.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
                return "redirect:/bookings";
            }
            
            model.addAttribute("booking", booking);
            return "bookingDetails";
            
        } catch (Exception e) {
            logger.error("Ошибка при загрузке деталей бронирования", e);
            return "error";
        }
    }
} 