package ru.kpfu.itis.coworkingbooking.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kpfu.itis.coworkingbooking.models.Space;
import ru.kpfu.itis.coworkingbooking.services.SpaceService;
import ru.kpfu.itis.coworkingbooking.services.ReviewService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SpaceController {
    
    private static final Logger logger = LoggerFactory.getLogger(SpaceController.class);
    
    @Autowired
    private SpaceService spaceService;
    
    @Autowired
    private ReviewService reviewService;
    
    @GetMapping("/spaces/{id}")
    public String spaceDetails(@PathVariable Long id, Model model) {
        try {
            var space = spaceService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Пространство не найдено"));
            
            model.addAttribute("space", space);
            model.addAttribute("reviews", reviewService.findBySpaceId(id));
            model.addAttribute("averageRating", reviewService.getAverageRatingForSpace(id));
            
            return "space-details";
        } catch (Exception e) {
            logger.error("Ошибка при загрузке деталей пространства: {}", id, e);
            return "error";
        }
    }
    
    @GetMapping("/spaces")
    public String spaces(@RequestParam(required = false) String location,
                        @RequestParam(required = false) String name,
                        @RequestParam(required = false, defaultValue = "default") String sort,
                        Model model) {
        try {
            List<Space> spaces;
            if (location != null && !location.trim().isEmpty()) {
                spaces = spaceService.findByLocation(location);
                model.addAttribute("searchLocation", location);
            } else if (name != null && !name.trim().isEmpty()) {
                spaces = spaceService.findByName(name);
                model.addAttribute("searchName", name);
            } else {
                spaces = spaceService.findAvailable();
            }

            if ("popular".equals(sort)) {
                spaces = spaceService.sortByPopularity(spaces);
            } else if ("rating".equals(sort)) {
                spaces = spaceService.sortByRating(spaces);
            }

            Map<Long, Double> spaceRatings = new HashMap<>();
            Map<Long, Long> spaceReviewCounts = new HashMap<>();

            for (Space space : spaces) {
                Double rating = spaceService.getAverageRating(space.getId());
                Long reviewCount = spaceService.getReviewCount(space.getId());
                spaceRatings.put(space.getId(), rating);
                spaceReviewCounts.put(space.getId(), reviewCount);
            }
            
            model.addAttribute("spaces", spaces);
            model.addAttribute("spaceRatings", spaceRatings);
            model.addAttribute("spaceReviewCounts", spaceReviewCounts);
            model.addAttribute("currentSort", sort);
            
            return "spaces";
        } catch (Exception e) {
            logger.error("Ошибка при загрузке страницы пространств", e);
            return "error";
        }
    }
} 