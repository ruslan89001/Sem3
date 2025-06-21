package ru.kpfu.itis.coworkingbooking.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kpfu.itis.coworkingbooking.models.Booking;
import ru.kpfu.itis.coworkingbooking.models.Space;
import ru.kpfu.itis.coworkingbooking.models.User;
import ru.kpfu.itis.coworkingbooking.repositories.BookingRepository;
import ru.kpfu.itis.coworkingbooking.repositories.SpaceRepository;
import ru.kpfu.itis.coworkingbooking.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookingService {
    
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SpaceRepository spaceRepository;
    
    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }
    
    public List<Booking> findByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }
    
    public List<Booking> findBySpaceId(Long spaceId) {
        return bookingRepository.findBySpaceId(spaceId);
    }
    
    public List<Booking> findByStatus(Booking.BookingStatus status) {
        return bookingRepository.findByStatus(status);
    }
    
    public List<Booking> findByUserIdAndStatus(Long userId, Booking.BookingStatus status) {
        return bookingRepository.findByUserIdAndStatus(userId, status);
    }
    
    public List<Booking> findConflictingBookings(Long spaceId, LocalDateTime startTime, LocalDateTime endTime) {
        return bookingRepository.findConflictingBookings(spaceId, startTime, endTime, Booking.BookingStatus.confirmed);
    }
    
    public List<Booking> findUserFutureBookings(Long userId, LocalDateTime fromDate) {
        return bookingRepository.findUserFutureBookings(userId, fromDate);
    }
    
    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }
    
    public Booking createBooking(Long userId, Long spaceId, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            Space space = spaceRepository.findById(spaceId)
                    .orElseThrow(() -> new RuntimeException("Пространство не найдено"));

            if (!space.isAvailability()) {
                throw new RuntimeException("Пространство недоступно для бронирования");
            }

            List<Booking> conflicts = findConflictingBookings(spaceId, startTime, endTime);
            if (!conflicts.isEmpty()) {
                throw new RuntimeException("Выбранное время уже занято");
            }
            
            Booking booking = new Booking(user, space, startTime, endTime);
            Booking savedBooking = bookingRepository.save(booking);
            
            logger.info("Создано бронирование: пользователь {}, пространство {}, время {} - {}", 
                       userId, spaceId, startTime, endTime);
            
            return savedBooking;
        } catch (Exception e) {
            logger.error("Ошибка при создании бронирования: пользователь {}, пространство {}", userId, spaceId, e);
            throw e;
        }
    }
    
    public Booking updateBookingStatus(Long bookingId, Booking.BookingStatus status) {
        try {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Бронирование не найдено"));
            
            booking.setStatus(status);
            Booking savedBooking = bookingRepository.save(booking);
            
            logger.info("Обновлен статус бронирования: {} -> {}", bookingId, status);
            
            return savedBooking;
        } catch (Exception e) {
            logger.error("Ошибка при обновлении статуса бронирования: {}", bookingId, e);
            throw e;
        }
    }
    
    public void cancelBooking(Long bookingId) {
        try {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Бронирование не найдено"));
            
            booking.setStatus(Booking.BookingStatus.cancelled);
            bookingRepository.save(booking);
            
            logger.info("Бронирование отменено: {}", bookingId);
        } catch (Exception e) {
            logger.error("Ошибка при отмене бронирования: {}", bookingId, e);
            throw e;
        }
    }
    
    public void deleteBooking(Long id) {
        try {
            bookingRepository.deleteById(id);
            logger.info("Бронирование удалено: {}", id);
        } catch (Exception e) {
            logger.error("Ошибка при удалении бронирования: {}", id, e);
            throw e;
        }
    }
}
