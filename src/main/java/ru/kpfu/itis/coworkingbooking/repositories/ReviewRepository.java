package ru.kpfu.itis.coworkingbooking.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kpfu.itis.coworkingbooking.models.Review;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    List<Review> findByUserId(Long userId);
    
    List<Review> findBySpaceId(Long spaceId);
    
    List<Review> findByRating(Integer rating);
    
    List<Review> findBySpaceIdOrderByCreatedAtDesc(Long spaceId);
    
    List<Review> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT r FROM Review r WHERE r.space.id = :spaceId " +
           "ORDER BY r.createdAt DESC LIMIT :limit")
    List<Review> findRecentReviewsForSpace(@Param("spaceId") Long spaceId, 
                                          @Param("limit") Integer limit);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.space.id = :spaceId")
    Double getAverageRatingForSpace(@Param("spaceId") Long spaceId);
} 