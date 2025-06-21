package ru.kpfu.itis.coworkingbooking.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kpfu.itis.coworkingbooking.models.Review;
import ru.kpfu.itis.coworkingbooking.models.Space;
import ru.kpfu.itis.coworkingbooking.models.User;
import ru.kpfu.itis.coworkingbooking.repositories.ReviewRepository;
import ru.kpfu.itis.coworkingbooking.repositories.SpaceRepository;
import ru.kpfu.itis.coworkingbooking.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReviewService {
    
    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SpaceRepository spaceRepository;
    
    public List<Review> findAll() {
        return reviewRepository.findAll();
    }
    
    public List<Review> findByUserId(Long userId) {
        return reviewRepository.findByUserId(userId);
    }
    
    public List<Review> findBySpaceId(Long spaceId) {
        return reviewRepository.findBySpaceId(spaceId);
    }
    
    public List<Review> findByRating(Integer rating) {
        return reviewRepository.findByRating(rating);
    }
    
    public List<Review> findBySpaceIdOrderByCreatedAtDesc(Long spaceId) {
        return reviewRepository.findBySpaceIdOrderByCreatedAtDesc(spaceId);
    }
    
    public List<Review> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
        return reviewRepository.findByCreatedAtBetween(start, end);
    }
    
    public List<Review> findRecentReviewsForSpace(Long spaceId, Integer limit) {
        return reviewRepository.findRecentReviewsForSpace(spaceId, limit);
    }
    
    public Double getAverageRatingForSpace(Long spaceId) {
        return reviewRepository.getAverageRatingForSpace(spaceId);
    }
    
    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }
    
    public Review createReview(Long userId, Long spaceId, Integer rating, String comment) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            Space space = spaceRepository.findById(spaceId)
                    .orElseThrow(() -> new RuntimeException("Пространство не найдено"));

            Review review = new Review(user, space, rating, comment);
            Review savedReview = reviewRepository.save(review);
            
            logger.info("Создан отзыв: пользователь {}, пространство {}, рейтинг {}", 
                       userId, spaceId, rating);
            
            return savedReview;
        } catch (Exception e) {
            logger.error("Ошибка при создании отзыва: пользователь {}, пространство {}", userId, spaceId, e);
            throw e;
        }
    }
    
    public Review updateReview(Long reviewId, Integer rating, String comment) {
        try {
            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new RuntimeException("Отзыв не найден"));
            
            review.setRating(rating);
            review.setComment(comment);
            Review savedReview = reviewRepository.save(review);
            
            logger.info("Обновлен отзыв: {}", reviewId);
            
            return savedReview;
        } catch (Exception e) {
            logger.error("Ошибка при обновлении отзыва: {}", reviewId, e);
            throw e;
        }
    }
    
    public void deleteReview(Long id) {
        try {
            reviewRepository.deleteById(id);
            logger.info("Отзыв удален: {}", id);
        } catch (Exception e) {
            logger.error("Ошибка при удалении отзыва: {}", id, e);
            throw e;
        }
    }
    
    public boolean hasUserReviewedSpace(Long userId, Long spaceId) {
        List<Review> reviews = reviewRepository.findByUserId(userId);
        return reviews.stream().anyMatch(review -> review.getSpace().getId().equals(spaceId));
    }
}
