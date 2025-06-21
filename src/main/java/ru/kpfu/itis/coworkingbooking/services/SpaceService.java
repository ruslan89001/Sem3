package ru.kpfu.itis.coworkingbooking.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.kpfu.itis.coworkingbooking.models.Space;
import ru.kpfu.itis.coworkingbooking.repositories.SpaceRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class SpaceService {
    
    private static final Logger logger = LoggerFactory.getLogger(SpaceService.class);
    private static final String UPLOAD_DIR = "uploads/images/";
    
    @Autowired
    private SpaceRepository spaceRepository;
    
    public List<Space> findAll() {
        return spaceRepository.findAll();
    }
    
    public List<Space> findAvailable() {
        return spaceRepository.findByAvailability(true);
    }
    
    public List<Space> findByLocation(String location) {
        return spaceRepository.findByLocationContainingIgnoreCase(location);
    }
    
    public List<Space> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return spaceRepository.findByPriceBetween(minPrice, maxPrice);
    }
    
    public List<Space> findByName(String name) {
        return spaceRepository.findByNameContainingIgnoreCase(name);
    }
    
    public List<Space> findPopularSpaces() {
        return spaceRepository.findPopularSpaces();
    }
    
    public List<Space> findHighlyRatedSpaces() {
        return spaceRepository.findHighlyRatedSpaces();
    }
    
    public Optional<Space> findById(Long id) {
        return spaceRepository.findById(id);
    }
    
    public Space save(Space space) {
        try {
            Space savedSpace = spaceRepository.save(space);
            logger.info("Пространство сохранено: {}", savedSpace.getName());
            return savedSpace;
        } catch (Exception e) {
            logger.error("Ошибка при сохранении пространства: {}", space.getName(), e);
            throw e;
        }
    }
    
    public Space createSpace(String name, String description, BigDecimal price, String location, MultipartFile image) {
        try {
            String imageFileName = null;
            if (image != null && !image.isEmpty()) {
                imageFileName = saveImage(image);
            }
            
            Space space = new Space(name, description, price, location, imageFileName);
            return save(space);
        } catch (IOException e) {
            logger.error("Ошибка при сохранении изображения для пространства: {}", name, e);
            throw new RuntimeException("Ошибка при сохранении изображения", e);
        } catch (Exception e) {
            logger.error("Ошибка при создании пространства: {}", name, e);
            throw e;
        }
    }
    
    public Space updateSpace(Long id, String name, String description, BigDecimal price, String location, MultipartFile image) {
        try {
            Optional<Space> existingSpace = findById(id);
            if (existingSpace.isPresent()) {
                Space space = existingSpace.get();
                space.setName(name);
                space.setDescription(description);
                space.setPrice(price);
                space.setLocation(location);
                
                if (image != null && !image.isEmpty()) {
                    String imageFileName = saveImage(image);
                    space.setImage(imageFileName);
                }
                
                return save(space);
            } else {
                throw new RuntimeException("Пространство не найдено");
            }
        } catch (IOException e) {
            logger.error("Ошибка при сохранении изображения для пространства: {}", id, e);
            throw new RuntimeException("Ошибка при сохранении изображения", e);
        } catch (Exception e) {
            logger.error("Ошибка при обновлении пространства: {}", id, e);
            throw e;
        }
    }
    
    public void deleteById(Long id) {
        try {
            spaceRepository.deleteById(id);
            logger.info("Пространство удалено: {}", id);
        } catch (Exception e) {
            logger.error("Ошибка при удалении пространства: {}", id, e);
            throw e;
        }
    }
    
    public void toggleAvailability(Long id) {
        try {
            Optional<Space> space = findById(id);
            if (space.isPresent()) {
                Space s = space.get();
                s.setAvailability(!s.isAvailability());
                save(s);
                logger.info("Изменена доступность пространства: {} -> {}", id, s.isAvailability());
            }
        } catch (Exception e) {
            logger.error("Ошибка при изменении доступности пространства: {}", id, e);
            throw e;
        }
    }
    
    private String saveImage(MultipartFile file) throws IOException {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + fileExtension;

            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);
            
            logger.info("Изображение сохранено: {}", filename);
            return filename;
        } catch (IOException e) {
            logger.error("Ошибка при сохранении изображения", e);
            throw e;
        }
    }
    
    public Double getAverageRating(Long spaceId) {
        return spaceRepository.getAverageRating(spaceId);
    }
    
    public Long getReviewCount(Long spaceId) {
        return spaceRepository.getReviewCount(spaceId);
    }
    
    public List<Space> sortByPopularity(List<Space> spaces) {
        spaces.sort((s1, s2) -> {
            Long bookings1 = spaceRepository.getBookingCount(s1.getId());
            Long bookings2 = spaceRepository.getBookingCount(s2.getId());
            return bookings2.compareTo(bookings1); // По убыванию
        });
        return spaces;
    }
    
    public List<Space> sortByRating(List<Space> spaces) {
        spaces.sort((s1, s2) -> {
            Double rating1 = spaceRepository.getAverageRating(s1.getId());
            Double rating2 = spaceRepository.getAverageRating(s2.getId());
            if (rating1 == null) rating1 = 0.0;
            if (rating2 == null) rating2 = 0.0;
            return rating2.compareTo(rating1); // По убыванию
        });
        return spaces;
    }
}
