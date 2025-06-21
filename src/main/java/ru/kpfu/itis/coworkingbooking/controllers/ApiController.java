package ru.kpfu.itis.coworkingbooking.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.itis.coworkingbooking.models.Space;
import ru.kpfu.itis.coworkingbooking.models.User;
import ru.kpfu.itis.coworkingbooking.services.SpaceService;
import ru.kpfu.itis.coworkingbooking.services.UserService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiController {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);
    
    @Autowired
    private SpaceService spaceService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/spaces")
    public ResponseEntity<Map<String, Object>> getAllSpaces() {
        logger.info("Получен запрос GET /api/spaces");
        try {
            List<Space> spaces = spaceService.findAll();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", spaces);
            response.put("count", spaces.size());
            logger.info("Успешно возвращено {} пространств", spaces.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Ошибка при получении списка пространств", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Ошибка при получении данных");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/spaces/{id}")
    public ResponseEntity<Map<String, Object>> getSpaceById(@PathVariable Long id) {
        try {
            Optional<Space> space = spaceService.findById(id);
            Map<String, Object> response = new HashMap<>();
            
            if (space.isPresent()) {
                response.put("success", true);
                response.put("data", space.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("error", "Пространство не найдено");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            logger.error("Ошибка при получении пространства: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Ошибка при получении данных");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/spaces/available")
    public ResponseEntity<Map<String, Object>> getAvailableSpaces() {
        logger.info("Получен запрос GET /api/spaces/available");
        try {
            List<Space> spaces = spaceService.findAvailable();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", spaces);
            response.put("count", spaces.size());
            logger.info("Успешно возвращено {} доступных пространств", spaces.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Ошибка при получении доступных пространств", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Ошибка при получении данных");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/spaces/search")
    public ResponseEntity<Map<String, Object>> searchSpaces(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        try {
            List<Space> spaces;
            
            if (location != null && !location.trim().isEmpty()) {
                spaces = spaceService.findByLocation(location);
            } else if (name != null && !name.trim().isEmpty()) {
                spaces = spaceService.findByName(name);
            } else if (minPrice != null && maxPrice != null) {
                spaces = spaceService.findByPriceRange(minPrice, maxPrice);
            } else {
                spaces = spaceService.findAvailable();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", spaces);
            response.put("count", spaces.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Ошибка при поиске пространств", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Ошибка при поиске");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/spaces")
    public ResponseEntity<Map<String, Object>> createSpace(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam BigDecimal price,
            @RequestParam String location,
            Authentication authentication) {
        try {
            if (authentication == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Требуется аутентификация");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            if (user.getRole() != User.Role.ADMIN) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Недостаточно прав");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }
            
            Space space = spaceService.createSpace(name, description, price, location, null);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", space);
            response.put("message", "Пространство создано успешно");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Ошибка при создании пространства", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Ошибка при создании пространства");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/spaces/{id}")
    public ResponseEntity<Map<String, Object>> updateSpace(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam BigDecimal price,
            @RequestParam String location,
            Authentication authentication) {
        try {
            if (authentication == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Требуется аутентификация");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            if (user.getRole() != User.Role.ADMIN) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Недостаточно прав");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }
            
            Space space = spaceService.updateSpace(id, name, description, price, location, null);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", space);
            response.put("message", "Пространство обновлено успешно");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Ошибка при обновлении пространства: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Ошибка при обновлении пространства");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/spaces/{id}")
    public ResponseEntity<Map<String, Object>> deleteSpace(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            if (authentication == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Требуется аутентификация");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            if (user.getRole() != User.Role.ADMIN) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Недостаточно прав");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }
            
            spaceService.deleteById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Пространство удалено успешно");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Ошибка при удалении пространства: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Ошибка при удалении пространства");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        logger.info("Получен запрос GET /api/test");
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "API работает!");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
} 