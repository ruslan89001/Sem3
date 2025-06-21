package ru.kpfu.itis.coworkingbooking.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kpfu.itis.coworkingbooking.models.Space;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long> {
    
    List<Space> findByAvailability(boolean availability);
    
    List<Space> findByLocationContainingIgnoreCase(String location);
    
    List<Space> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    List<Space> findByNameContainingIgnoreCase(String name);

    @Query("SELECT s FROM Space s WHERE s.id IN " +
           "(SELECT b.space.id FROM Booking b GROUP BY b.space.id " +
           "HAVING COUNT(b) = (SELECT MAX(bookingCount) FROM " +
           "(SELECT COUNT(b2) as bookingCount FROM Booking b2 GROUP BY b2.space.id)))")
    List<Space> findPopularSpaces();

    @Query("SELECT s FROM Space s WHERE s.id IN " +
           "(SELECT r.space.id FROM Review r GROUP BY r.space.id " +
           "HAVING AVG(r.rating) = (SELECT MAX(avgRating) FROM " +
           "(SELECT AVG(r2.rating) as avgRating FROM Review r2 GROUP BY r2.space.id)))")
    List<Space> findHighlyRatedSpaces();

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.space.id = :spaceId")
    Double getAverageRating(@Param("spaceId") Long spaceId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.space.id = :spaceId")
    Long getReviewCount(@Param("spaceId") Long spaceId);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.space.id = :spaceId")
    Long getBookingCount(@Param("spaceId") Long spaceId);
} 