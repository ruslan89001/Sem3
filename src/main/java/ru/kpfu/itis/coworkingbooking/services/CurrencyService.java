package ru.kpfu.itis.coworkingbooking.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
public class CurrencyService {
    
    private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);
    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/RUB";
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();
    
    private Map<String, BigDecimal> exchangeRates = new HashMap<>();
    private long lastUpdateTime = 0;
    private static final long UPDATE_INTERVAL = 3600000; // 1 час
    
    public BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency) {
        try {
            updateExchangeRates();
            
            if ("RUB".equals(fromCurrency) && "RUB".equals(toCurrency)) {
                return amount;
            }
            
            BigDecimal fromRate = exchangeRates.get(fromCurrency);
            BigDecimal toRate = exchangeRates.get(toCurrency);
            
            if (fromRate == null || toRate == null) {
                logger.warn("Курс валюты не найден: {} -> {}", fromCurrency, toCurrency);
                return amount;
            }

            if ("RUB".equals(fromCurrency)) {
                return amount.multiply(toRate).setScale(2, RoundingMode.HALF_UP);
            }

            if ("RUB".equals(toCurrency)) {
                return amount.divide(fromRate, 2, RoundingMode.HALF_UP);
            }

            BigDecimal amountInRub = amount.divide(fromRate, 2, RoundingMode.HALF_UP);
            BigDecimal convertedAmount = amountInRub.multiply(toRate).setScale(2, RoundingMode.HALF_UP);
            
            logger.info("Конвертация валюты: {} {} -> {} {} = {} {}", 
                       amount, fromCurrency, convertedAmount, toCurrency, convertedAmount, toCurrency);
            
            return convertedAmount;
        } catch (Exception e) {
            logger.error("Ошибка при конвертации валюты: {} {} -> {}", amount, fromCurrency, toCurrency, e);
            return amount;
        }
    }
    
    public Map<String, BigDecimal> getExchangeRates() {
        try {
            updateExchangeRates();
            return new HashMap<>(exchangeRates);
        } catch (Exception e) {
            logger.error("Ошибка при получении курсов валют", e);
            return new HashMap<>();
        }
    }
    
    public BigDecimal getExchangeRate(String currency) {
        try {
            updateExchangeRates();
            return exchangeRates.getOrDefault(currency, BigDecimal.ONE);
        } catch (Exception e) {
            logger.error("Ошибка при получении курса валюты: {}", currency, e);
            return BigDecimal.ONE;
        }
    }
    
    private void updateExchangeRates() throws IOException {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastUpdateTime < UPDATE_INTERVAL && !exchangeRates.isEmpty()) {
            return;
        }
        
        Request request = new Request.Builder()
                .url(API_URL)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Ошибка HTTP: " + response.code());
            }
            
            String responseBody = response.body().string();
            logger.info("Ответ API валют: {}", responseBody);
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            
            if (jsonResponse.has("rates")) {
                JsonObject rates = jsonResponse.getAsJsonObject("rates");
                exchangeRates.clear();

                exchangeRates.put("RUB", BigDecimal.ONE);

                for (String currency : rates.keySet()) {
                    BigDecimal rate = new BigDecimal(rates.get(currency).getAsString());
                    exchangeRates.put(currency, rate);
                    logger.info("Курс {}: {}", currency, rate);
                }
                
                lastUpdateTime = currentTime;
                logger.info("Курсы валют обновлены. Доступные валюты: {}", exchangeRates.keySet());
            } else {
                throw new IOException("Неверный формат ответа API");
            }
        } catch (Exception e) {
            logger.error("Ошибка при обновлении курсов валют", e);
            if (exchangeRates.isEmpty()) {
                exchangeRates.put("RUB", BigDecimal.ONE);
                exchangeRates.put("USD", new BigDecimal("0.011"));
                exchangeRates.put("EUR", new BigDecimal("0.010"));
            }
        }
    }
    
    public boolean isCurrencySupported(String currency) {
        try {
            updateExchangeRates();
            return exchangeRates.containsKey(currency);
        } catch (Exception e) {
            logger.error("Ошибка при проверке поддержки валюты: {}", currency, e);
            return false;
        }
    }
} 