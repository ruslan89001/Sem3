package ru.kpfu.itis.coworkingbooking.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kpfu.itis.coworkingbooking.models.Booking;
import ru.kpfu.itis.coworkingbooking.models.Booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    List<Booking> findByUserId(Long userId);
    
    List<Booking> findBySpaceId(Long spaceId);
    
    List<Booking> findByStatus(BookingStatus status);
    
    List<Booking> findByUserIdAndStatus(Long userId, BookingStatus status);
    
    List<Booking> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT b FROM Booking b WHERE b.space.id = :spaceId AND b.status = :status " +
           "AND ((b.startTime BETWEEN :startTime AND :endTime) OR " +
           "(b.endTime BETWEEN :startTime AND :endTime) OR " +
           "(b.startTime <= :startTime AND b.endTime >= :endTime))")
    List<Booking> findConflictingBookings(@Param("spaceId") Long spaceId, 
                                         @Param("startTime") LocalDateTime startTime, 
                                         @Param("endTime") LocalDateTime endTime,
                                         @Param("status") BookingStatus status);
    
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.startTime >= :fromDate " +
           "ORDER BY b.startTime DESC")
    List<Booking> findUserFutureBookings(@Param("userId") Long userId, 
                                        @Param("fromDate") LocalDateTime fromDate);
} 