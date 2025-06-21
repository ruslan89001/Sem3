package ru.kpfu.itis.coworkingbooking.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kpfu.itis.coworkingbooking.models.Review;
import ru.kpfu.itis.coworkingbooking.models.Space;
import ru.kpfu.itis.coworkingbooking.models.User;
import ru.kpfu.itis.coworkingbooking.services.ReviewService;
import ru.kpfu.itis.coworkingbooking.services.SpaceService;
import ru.kpfu.itis.coworkingbooking.services.UserService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/reviews")
public class ReviewController {
    
    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);
    
    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SpaceService spaceService;
    
    @GetMapping("/space/{spaceId}")
    public String spaceReviews(@PathVariable Long spaceId, Model model) {
        try {
            Space space = spaceService.findById(spaceId)
                    .orElseThrow(() -> new RuntimeException("Пространство не найдено"));
            
            List<Review> reviews = reviewService.findBySpaceId(spaceId);
            Double averageRating = reviewService.getAverageRatingForSpace(spaceId);
            
            model.addAttribute("space", space);
            model.addAttribute("reviews", reviews);
            model.addAttribute("averageRating", averageRating);
            
            return "reviews";
        } catch (Exception e) {
            logger.error("Ошибка при загрузке отзывов для пространства: {}", spaceId, e);
            return "error";
        }
    }
    
    @PostMapping("/space/{spaceId}")
    public String addReview(@PathVariable Long spaceId,
                           @RequestParam Integer rating,
                           @RequestParam String comment,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            Review review = reviewService.createReview(user.getId(), spaceId, rating, comment);
            
            redirectAttributes.addFlashAttribute("success", "Отзыв добавлен успешно!");
            return "redirect:/reviews/space/" + spaceId;
            
        } catch (Exception e) {
            logger.error("Ошибка при добавлении отзыва", e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при добавлении отзыва");
            return "redirect:/reviews/space/" + spaceId;
        }
    }

    @PostMapping("/ajax/space/{spaceId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addReviewAjax(@PathVariable Long spaceId,
                                                            @RequestParam Integer rating,
                                                            @RequestParam String comment,
                                                            Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            Review savedReview = reviewService.createReview(user.getId(), spaceId, rating, comment);

            List<Review> reviews = reviewService.findBySpaceId(spaceId);
            Double averageRating = reviewService.getAverageRatingForSpace(spaceId);
            
            response.put("success", true);
            response.put("message", "Отзыв добавлен успешно!");
            response.put("review", Map.of(
                "id", savedReview.getId(),
                "username", savedReview.getUser().getUsername(),
                "rating", savedReview.getRating(),
                "comment", savedReview.getComment(),
                "createdAt", savedReview.getCreatedAt().toString()
            ));
            response.put("averageRating", averageRating);
            response.put("totalReviews", reviews.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Ошибка при добавлении отзыва через AJAX", e);
            response.put("success", false);
            response.put("message", "Ошибка при добавлении отзыва: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/ajax/space/{spaceId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getReviewsAjax(@PathVariable Long spaceId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Review> reviews = reviewService.findBySpaceId(spaceId);
            Double averageRating = reviewService.getAverageRatingForSpace(spaceId);
            
            response.put("success", true);
            response.put("reviews", reviews);
            response.put("averageRating", averageRating);
            response.put("totalReviews", reviews.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Ошибка при получении отзывов через AJAX", e);
            response.put("success", false);
            response.put("message", "Ошибка при получении отзывов");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/create/{spaceId}")
    public String createReviewForm(@PathVariable Long spaceId, Model model, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            Space space = spaceService.findById(spaceId)
                    .orElseThrow(() -> new RuntimeException("Пространство не найдено"));

            if (reviewService.hasUserReviewedSpace(user.getId(), spaceId)) {
                model.addAttribute("error", "Вы уже оставляли отзыв для этого пространства");
                return "redirect:/reviews/space/" + spaceId;
            }
            
            model.addAttribute("space", space);
            model.addAttribute("review", new Review());
            
            return "createReview";
        } catch (Exception e) {
            logger.error("Ошибка при загрузке формы отзыва", e);
            return "error";
        }
    }
    
    @PostMapping("/create")
    public String createReview(@RequestParam Long spaceId,
                              @RequestParam Integer rating,
                              @RequestParam String comment,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            if (reviewService.hasUserReviewedSpace(user.getId(), spaceId)) {
                redirectAttributes.addFlashAttribute("error", "Вы уже оставляли отзыв для этого пространства");
                return "redirect:/reviews/space/" + spaceId;
            }
            
            Review review = reviewService.createReview(user.getId(), spaceId, rating, comment);
            
            redirectAttributes.addFlashAttribute("success", "Отзыв добавлен успешно!");
            return "redirect:/reviews/space/" + spaceId;
            
        } catch (Exception e) {
            logger.error("Ошибка при создании отзыва", e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при создании отзыва: " + e.getMessage());
            return "redirect:/reviews/create/" + spaceId;
        }
    }
    
    @PostMapping("/{reviewId}/delete")
    public String deleteReview(@PathVariable Long reviewId,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            Review review = reviewService.findById(reviewId)
                    .orElseThrow(() -> new RuntimeException("Отзыв не найден"));

            if (!review.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
                redirectAttributes.addFlashAttribute("error", "У вас нет прав для удаления этого отзыва");
                return "redirect:/reviews/space/" + review.getSpace().getId();
            }
            
            Long spaceId = review.getSpace().getId();
            reviewService.deleteReview(reviewId);
            
            redirectAttributes.addFlashAttribute("success", "Отзыв удален успешно!");
            return "redirect:/reviews/space/" + spaceId;
            
        } catch (Exception e) {
            logger.error("Ошибка при удалении отзыва", e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении отзыва");
            return "redirect:/reviews";
        }
    }
    
    @GetMapping("/my")
    public String myReviews(Model model, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            List<Review> reviews = reviewService.findByUserId(user.getId());
            model.addAttribute("reviews", reviews);
            
            return "myReviews";
        } catch (Exception e) {
            logger.error("Ошибка при загрузке отзывов пользователя", e);
            return "error";
        }
    }
    
    @GetMapping("/edit/{reviewId}")
    public String editReviewForm(@PathVariable Long reviewId, Model model, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            Review review = reviewService.findById(reviewId)
                    .orElseThrow(() -> new RuntimeException("Отзыв не найден"));

            if (!review.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
                return "redirect:/reviews/my";
            }
            
            model.addAttribute("review", review);
            model.addAttribute("space", review.getSpace());
            
            return "editReview";
        } catch (Exception e) {
            logger.error("Ошибка при загрузке формы редактирования отзыва", e);
            return "error";
        }
    }
    
    @PostMapping("/edit/{reviewId}")
    public String editReview(@PathVariable Long reviewId,
                            @RequestParam Integer rating,
                            @RequestParam String comment,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            Review review = reviewService.findById(reviewId)
                    .orElseThrow(() -> new RuntimeException("Отзыв не найден"));

            if (!review.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
                redirectAttributes.addFlashAttribute("error", "У вас нет прав для редактирования этого отзыва");
                return "redirect:/reviews/my";
            }
            
            Review updatedReview = reviewService.updateReview(reviewId, rating, comment);
            
            redirectAttributes.addFlashAttribute("success", "Отзыв обновлен успешно!");
            return "redirect:/reviews/space/" + updatedReview.getSpace().getId();
            
        } catch (Exception e) {
            logger.error("Ошибка при обновлении отзыва", e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении отзыва: " + e.getMessage());
            return "redirect:/reviews/edit/" + reviewId;
        }
    }
} 