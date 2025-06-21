package ru.kpfu.itis.coworkingbooking.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGlobalException(Exception ex, WebRequest request) {
        logger.error("Необработанное исключение: {}", ex.getMessage(), ex);
        
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", "Произошла внутренняя ошибка сервера");
        modelAndView.addObject("errorDetails", ex.getMessage());
        modelAndView.addObject("statusCode", 500);
        
        return modelAndView;
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleRuntimeException(RuntimeException ex, WebRequest request) {
        logger.error("RuntimeException: {}", ex.getMessage(), ex);
        
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", ex.getMessage());
        modelAndView.addObject("errorDetails", "Ошибка выполнения");
        modelAndView.addObject("statusCode", 400);
        
        return modelAndView;
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        logger.warn("Отказано в доступе: {}", ex.getMessage());
        
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", "Доступ запрещен");
        modelAndView.addObject("errorDetails", "У вас нет прав для выполнения этого действия");
        modelAndView.addObject("statusCode", 403);
        
        return modelAndView;
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ModelAndView handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        logger.warn("Ошибка аутентификации: {}", ex.getMessage());
        
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", "Ошибка аутентификации");
        modelAndView.addObject("errorDetails", "Неверные учетные данные");
        modelAndView.addObject("statusCode", 401);
        
        return modelAndView;
    }
    
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ModelAndView handleValidationException(Exception ex, WebRequest request) {
        logger.warn("Ошибка валидации: {}", ex.getMessage());
        
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", "Ошибка валидации данных");
        modelAndView.addObject("errorDetails", "Проверьте правильность введенных данных");
        modelAndView.addObject("statusCode", 400);
        
        return modelAndView;
    }
} 