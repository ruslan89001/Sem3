package ru.kpfu.itis.coworkingbooking.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ru.kpfu.itis.coworkingbooking.models.User;

import java.io.IOException;

@WebFilter("/admin/*")
public class AdminFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(false);

        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null && "admin".equalsIgnoreCase(user.getRole())) {
                chain.doFilter(request, response);
                return;
            }
        }

        ((HttpServletResponse) response).sendRedirect("/login?error=accessDenied");
    }
}

