package ru.kpfu.itis.coworkingbooking.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.kpfu.itis.coworkingbooking.services.UserService;

@Controller
public class TestController {
    
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/test")
    public String test(Model model) {
        try {
            logger.info("Тестовый запрос выполнен");
            model.addAttribute("message", "Приложение работает!");

            model.addAttribute("userCount", "Проверка выполнена");
            
            return "test";
        } catch (Exception e) {
            logger.error("Ошибка в тестовом контроллере", e);
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
    
    @GetMapping("/test-db")
    public String testDb(Model model) {
        try {
            logger.info("Тест базы данных");

            var adminUser = userService.findByUsername("admin");
            if (adminUser.isPresent()) {
                var user = adminUser.get();
                model.addAttribute("adminExists", true);
                model.addAttribute("adminUsername", user.getUsername());
                model.addAttribute("adminEmail", user.getEmail());
                model.addAttribute("adminRole", user.getRole());
                model.addAttribute("adminPassword", user.getPassword());
            } else {
                model.addAttribute("adminExists", false);
            }
            
            return "test-db";
        } catch (Exception e) {
            logger.error("Ошибка в тесте базы данных", e);
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
    
    @GetMapping("/test-login")
    public String testLogin(Model model) {
        try {
            logger.info("Тест входа");

            var adminUser = userService.findByUsername("admin");
            if (adminUser.isPresent()) {
                var user = adminUser.get();
                model.addAttribute("adminExists", true);
                model.addAttribute("adminUsername", user.getUsername());
                model.addAttribute("adminEmail", user.getEmail());
                model.addAttribute("adminRole", user.getRole());
                model.addAttribute("adminPassword", user.getPassword());

                try {
                    var userDetails = userService.loadUserByUsername("admin");
                    model.addAttribute("userDetailsLoaded", true);
                    model.addAttribute("userDetailsAuthorities", userDetails.getAuthorities());
                } catch (Exception e) {
                    model.addAttribute("userDetailsLoaded", false);
                    model.addAttribute("userDetailsError", e.getMessage());
                    logger.error("Ошибка при загрузке UserDetails", e);
                }
            } else {
                model.addAttribute("adminExists", false);
            }
            
            return "test-login";
        } catch (Exception e) {
            logger.error("Ошибка в тесте входа", e);
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
} 