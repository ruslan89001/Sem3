package ru.kpfu.itis.coworkingbooking.helpers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Optional;

public class CookieHelper {
    public static String getValueByName(String name, HttpServletRequest request){
        Optional<Cookie> cookie= getByName(name, request);
        return cookie.map(Cookie::getValue).orElse(null);
    }
    public static void add(String name, String value, HttpServletResponse response, int maxAge){
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }
    public static Optional<Cookie> getByName(String name, HttpServletRequest request){
        return Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> cookie.getName().equals(name))
                        .findAny());
    }
    public static void delete(String name, HttpServletResponse response,HttpServletRequest request){
        getByName(name,request).ifPresent(cookie -> {
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
        );
    }
}
