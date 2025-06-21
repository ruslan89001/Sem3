package ru.kpfu.itis.coworkingbooking.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kpfu.itis.coworkingbooking.models.User;
import ru.kpfu.itis.coworkingbooking.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@Primary
public class UserService implements UserDetailsService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            logger.info("Попытка загрузки пользователя: {}", username);
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isPresent()) {
                User foundUser = user.get();
                logger.info("Пользователь найден: {} (роль: {}, пароль: {}, authorities: {})", 
                    username, foundUser.getRole(), foundUser.getPassword(), foundUser.getAuthorities());
                return foundUser;
            } else {
                logger.warn("Пользователь не найден: {}", username);
                throw new UsernameNotFoundException("Пользователь не найден: " + username);
            }
        } catch (Exception e) {
            logger.error("Ошибка при загрузке пользователя: {}", username, e);
            throw new UsernameNotFoundException("Ошибка при загрузке пользователя", e);
        }
    }
    
    public User registerUser(String username, String password, String email) {
        try {
            if (userRepository.existsByUsername(username)) {
                logger.warn("Попытка регистрации с существующим именем пользователя: {}", username);
                throw new RuntimeException("Пользователь с таким именем уже существует");
            }
            
            if (userRepository.existsByEmail(email)) {
                logger.warn("Попытка регистрации с существующим email: {}", email);
                throw new RuntimeException("Пользователь с таким email уже существует");
            }

            User user = new User(username, password, email, User.Role.USER);
            User savedUser = userRepository.save(user);
            
            logger.info("Зарегистрирован новый пользователь: {}", username);
            return savedUser;
        } catch (Exception e) {
            logger.error("Ошибка при регистрации пользователя: {}", username, e);
            throw e;
        }
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> findByToken(UUID token) {
        return userRepository.findByToken(token);
    }
    
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    public List<User> findByRole(User.Role role) {
        return userRepository.findByRole(role);
    }
    
    public List<User> findActiveUsersByRole(User.Role role) {
        return userRepository.findActiveUsersByRole(role, "confirmed");
    }
    
    public List<User> findUsersWithMoreThanReviews(Long minReviews) {
        return userRepository.findUsersWithMoreThanReviews(minReviews);
    }
    
    public User save(User user) {
        return userRepository.save(user);
    }
    
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
