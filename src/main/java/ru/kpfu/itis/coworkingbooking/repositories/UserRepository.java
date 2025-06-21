package ru.kpfu.itis.coworkingbooking.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kpfu.itis.coworkingbooking.models.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByToken(UUID token);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    List<User> findByRole(User.Role role);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.id IN " +
           "(SELECT DISTINCT b.user.id FROM Booking b WHERE b.status = :status)")
    List<User> findActiveUsersByRole(@Param("role") User.Role role, @Param("status") String status);
    
    @Query("SELECT u FROM User u WHERE u.id IN " +
           "(SELECT r.user.id FROM Review r GROUP BY r.user.id HAVING COUNT(r) > :minReviews)")
    List<User> findUsersWithMoreThanReviews(@Param("minReviews") Long minReviews);
} 