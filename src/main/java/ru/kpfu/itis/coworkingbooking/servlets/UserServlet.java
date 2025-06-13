package ru.kpfu.itis.coworkingbooking.servlets;

import ru.kpfu.itis.coworkingbooking.models.User;
import ru.kpfu.itis.coworkingbooking.services.UserService;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;

@WebServlet("/user")
public class UserServlet extends HttpServlet {
    private UserService userService;

    @Override
    public void init() {
        userService = (UserService) getServletContext().getAttribute("userService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("profile".equals(action)) {
            User user = (User) request.getSession().getAttribute("user");
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/user?action=login");
                return;
            }
            request.setAttribute("user", user);
            request.getRequestDispatcher("/WEB-INF/jsp/profile.jsp").forward(request, response);
        } else if ("logout".equals(action)) {
            request.getSession().invalidate();
            response.sendRedirect(request.getContextPath() + "/user?action=login");
        } else if ("login".equals(action)) {
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
        } else if ("register".equals(action)) {
            request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
        } else if ("check-username".equals(action)) {
            String username = request.getParameter("username");
            User existingUser = null;
            try {
                existingUser = userService.getUserByUsername(username);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            response.setContentType("application/json");
            response.getWriter().write("{\"exists\": " + (existingUser != null) + "}");
        } else {
            response.sendRedirect(request.getContextPath() + "/user?action=login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if ("register".equals(action)) {
                String username = request.getParameter("username");
                String password = request.getParameter("password");
                String email = request.getParameter("email");

                User existingUserByUsername = userService.getUserByUsername(username);
                User existingUserByEmail = userService.getUserByEmail(email);
                if (existingUserByUsername != null) {
                    response.sendRedirect(request.getContextPath() + "/user?action=register&error=userExists");
                    return;
                }
                if (existingUserByEmail != null) {
                    response.sendRedirect(request.getContextPath() + "/user?action=register&error=emailExists");
                    return;
                }

                String role = "USER";
                String hashedPassword = userService.hashPassword(password);
                UUID token = UUID.randomUUID();

                User newUser = new User(0, username, hashedPassword, email, role, token);
                userService.registerUser(newUser);
                request.getSession().setAttribute("user", newUser);
                response.sendRedirect(request.getContextPath() + "/user?action=profile");

            } else if ("login".equals(action)) {
                String username = request.getParameter("username");
                String password = request.getParameter("password");

                User user = userService.getUserByUsername(username);
                if (user != null && user.getPassword().equals(userService.hashPassword(password))) {
                    request.getSession().setAttribute("user", user);
                    response.sendRedirect(request.getContextPath() + "/user?action=profile");
                } else {
                    response.sendRedirect(request.getContextPath() + "/user?action=login&error=true");
                }

            } else if ("editProfile".equals(action)) {
                User sessionUser = (User) request.getSession().getAttribute("user");
                if (sessionUser == null) {
                    response.sendRedirect(request.getContextPath() + "/user?action=login");
                    return;
                }

                try {
                    User currentUser = userService.getUserById(sessionUser.getId());

                    String username = Optional.ofNullable(request.getParameter("username")).orElse("");
                    String email = Optional.ofNullable(request.getParameter("email")).orElse("");
                    String newPassword = Optional.ofNullable(request.getParameter("newPassword")).orElse("");

                    if (!username.isEmpty()) currentUser.setUsername(username);
                    if (!email.isEmpty()) currentUser.setEmail(email);
                    if (!newPassword.isEmpty()) {
                        currentUser.setPassword(userService.hashPassword(newPassword));
                    }

                    if (userService.isUsernameExists(currentUser.getUsername(), currentUser.getId())) {
                        response.sendRedirect(request.getContextPath() + "/user?action=profile&error=usernameExists");
                        return;
                    }

                    if (userService.isEmailExists(currentUser.getEmail(), currentUser.getId())) {
                        response.sendRedirect(request.getContextPath() + "/user?action=profile&error=emailExists");
                        return;
                    }

                    userService.updateUser(currentUser);
                    request.getSession().setAttribute("user", currentUser);
                    response.sendRedirect(request.getContextPath() + "/user?action=profile");

                } catch (SQLException | NoSuchAlgorithmException e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка обновления данных");
                }
            }
        } catch (SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка базы данных");
        }
    }
}
