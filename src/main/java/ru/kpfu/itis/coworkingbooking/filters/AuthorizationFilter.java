package ru.kpfu.itis.coworkingbooking.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ru.kpfu.itis.coworkingbooking.helpers.CookieHelper;
import ru.kpfu.itis.coworkingbooking.models.User;
import ru.kpfu.itis.coworkingbooking.services.UserService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

@WebFilter("/*")
public class AuthorizationFilter extends HttpFilter {
    private UserService userService;

    @Override
    public void init(FilterConfig config) {
        userService = (UserService) config.getServletContext().getAttribute("userService");
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpSession session = request.getSession();

        String servletPath = request.getServletPath();
        if (servletPath.contains("/resources/") || servletPath.endsWith(".jsp")) {
            chain.doFilter(request, response);
            return;
        }

        if (session.getAttribute("user") != null) {
            chain.doFilter(request, response);
            return;
        }

        String token = CookieHelper.getValueByName("token", request);
        if (token != null) {
            try {
                User user = userService.getUserByToken(UUID.fromString(token));
                session.setAttribute("user", user);
                chain.doFilter(request, response);
                return;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        if (servletPath.equals("/profile") ||
                servletPath.startsWith("/bookings") ||
                servletPath.startsWith("/spaces") ||
                servletPath.startsWith("/reviews") ) {
            response.sendRedirect(request.getContextPath() + "/user?action=register");
            return;
        }

        chain.doFilter(request, response);
    }
}
