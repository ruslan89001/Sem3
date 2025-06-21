package ru.kpfu.itis.coworkingbooking.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.kpfu.itis.coworkingbooking.models.Space;
import ru.kpfu.itis.coworkingbooking.services.SpaceService;
import ru.kpfu.itis.coworkingbooking.services.CurrencyService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {
    
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    
    @Autowired
    private SpaceService spaceService;
    
    @Autowired
    private CurrencyService currencyService;
    
    @GetMapping("/")
    public String home(Model model) {
        try {
            List<Space> popularSpaces = spaceService.findPopularSpaces();
            List<Space> highlyRatedSpaces = spaceService.findHighlyRatedSpaces();
            
            // Создаем карты для рейтингов
            Map<Long, Double> spaceRatings = new HashMap<>();
            Map<Long, Long> spaceReviewCounts = new HashMap<>();
            Map<Long, Map<String, BigDecimal>> spacePricesInCurrencies = new HashMap<>();
            
            // Получаем рейтинги и цены в разных валютах для популярных пространств
            for (Space space : popularSpaces) {
                Double rating = spaceService.getAverageRating(space.getId());
                Long reviewCount = spaceService.getReviewCount(space.getId());
                spaceRatings.put(space.getId(), rating);
                spaceReviewCounts.put(space.getId(), reviewCount);
                
                // Конвертируем цены в USD и EUR
                Map<String, BigDecimal> prices = new HashMap<>();
                prices.put("RUB", space.getPrice());
                prices.put("USD", currencyService.convertCurrency(space.getPrice(), "RUB", "USD"));
                prices.put("EUR", currencyService.convertCurrency(space.getPrice(), "RUB", "EUR"));
                spacePricesInCurrencies.put(space.getId(), prices);
            }
            
            // Получаем рейтинги и цены в разных валютах для высокооцененных пространств
            for (Space space : highlyRatedSpaces) {
                Double rating = spaceService.getAverageRating(space.getId());
                Long reviewCount = spaceService.getReviewCount(space.getId());
                spaceRatings.put(space.getId(), rating);
                spaceReviewCounts.put(space.getId(), reviewCount);
                
                // Конвертируем цены в USD и EUR
                Map<String, BigDecimal> prices = new HashMap<>();
                prices.put("RUB", space.getPrice());
                prices.put("USD", currencyService.convertCurrency(space.getPrice(), "RUB", "USD"));
                prices.put("EUR", currencyService.convertCurrency(space.getPrice(), "RUB", "EUR"));
                spacePricesInCurrencies.put(space.getId(), prices);
            }
            
            model.addAttribute("popularSpaces", popularSpaces);
            model.addAttribute("highlyRatedSpaces", highlyRatedSpaces);
            model.addAttribute("spaceRatings", spaceRatings);
            model.addAttribute("spaceReviewCounts", spaceReviewCounts);
            model.addAttribute("spacePricesInCurrencies", spacePricesInCurrencies);
            
            return "index";
        } catch (Exception e) {
            logger.error("Ошибка при загрузке главной страницы", e);
            return "error";
        }
    }
    
    @GetMapping("/home")
    public String homePage() {
        return "redirect:/";
    }
    
    @GetMapping("/about")
    public String about() {
        return "about";
    }
    
    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }
} 