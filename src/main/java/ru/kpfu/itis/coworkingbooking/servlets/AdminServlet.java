package ru.kpfu.itis.coworkingbooking.servlets;

import ru.kpfu.itis.coworkingbooking.models.User;
import ru.kpfu.itis.coworkingbooking.models.Space;
import ru.kpfu.itis.coworkingbooking.models.Booking;
import ru.kpfu.itis.coworkingbooking.models.WorkingHours;
import ru.kpfu.itis.coworkingbooking.services.UserService;
import ru.kpfu.itis.coworkingbooking.services.SpaceService;
import ru.kpfu.itis.coworkingbooking.services.BookingService;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import ru.kpfu.itis.coworkingbooking.services.WorkingHoursService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
    private UserService userService;
    private SpaceService spaceService;
    private BookingService bookingService;

    @Override
    public void init() {
        userService = (UserService) getServletContext().getAttribute("userService");
        spaceService = (SpaceService) getServletContext().getAttribute("spaceService");
        bookingService = (BookingService) getServletContext().getAttribute("bookingService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if ("viewUsers".equals(action)) {
                List<User> users = userService.getAllUsers();
                request.setAttribute("users", users);
                request.getRequestDispatcher("/WEB-INF/jsp/admin/users.jsp").forward(request, response);
            } else if ("viewSpaces".equals(action)) {
                List<Space> spaces = spaceService.getAllSpaces();
                request.setAttribute("spaces", spaces);
                request.getRequestDispatcher("/WEB-INF/jsp/admin/spaces.jsp").forward(request, response);
            } else if ("viewBookings".equals(action)) {
                try {
                    List<Booking> bookings = bookingService.getAllBookings();
                    List<User> allUsers = userService.getAllUsers();
                    List<Space> allSpaces = spaceService.getAllSpaces();

                    List<Map<String, Object>> enrichedBookings = new ArrayList<>();
                    for (Booking booking : bookings) {
                        Map<String, Object> enriched = new HashMap<>();
                        enriched.put("booking", booking);
                        enriched.put("user", userService.getUserById(booking.getUserId()));
                        enriched.put("space", spaceService.getSpaceById(booking.getSpaceId()));
                        enrichedBookings.add(enriched);
                    }

                    request.setAttribute("enrichedBookings", enrichedBookings);
                    request.setAttribute("allUsers", allUsers);
                    request.setAttribute("allSpaces", allSpaces);

                    request.getRequestDispatcher("/WEB-INF/jsp/admin/bookings.jsp").forward(request, response);

                } catch (SQLException e) {
                    response.sendRedirect(request.getContextPath() + "/admin?error=database");
                }
            } else if ("editUser".equals(action)) {
                int userId = Integer.parseInt(request.getParameter("userId"));
                User user = userService.getUserById(userId);
                request.setAttribute("user", user);
                request.getRequestDispatcher("/WEB-INF/jsp/admin/editUser.jsp").forward(request, response);
            } else if ("editSpace".equals(action)) {
                int spaceId = Integer.parseInt(request.getParameter("spaceId"));
                Space space = spaceService.getSpaceById(spaceId);
                request.setAttribute("space", space);
                request.getRequestDispatcher("/WEB-INF/jsp/admin/editSpace.jsp").forward(request, response);
            } else if ("editBooking".equals(action)) {
                int bookingId = Integer.parseInt(request.getParameter("bookingId"));
                Booking booking = bookingService.getBookingById(bookingId);
                List<User> allUsers = userService.getAllUsers();
                List<Space> allSpaces = spaceService.getAllSpaces();

                request.setAttribute("booking", booking);
                request.setAttribute("allUsers", allUsers);
                request.setAttribute("allSpaces", allSpaces);
                request.getRequestDispatcher("/WEB-INF/jsp/admin/editBooking.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            log("Error occurred: " + e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching data.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if ("addUser".equals(action)) {
                String username = request.getParameter("username");
                String password = request.getParameter("password");
                String email = request.getParameter("email");

                UUID token = UUID.randomUUID();
                User newUser = new User(0, username, password, email, "USER", token);
                userService.registerUser(newUser);
                response.sendRedirect(request.getContextPath() + "/admin?action=viewUsers");
            } else if ("addBooking".equals(action)) {
                try {
                    int userId = Integer.parseInt(request.getParameter("user_id"));
                    int spaceId = Integer.parseInt(request.getParameter("space_id"));
                    String status = request.getParameter("status");

                    String startTimeStr = request.getParameter("start_time").replace("T", " ") + ":00";
                    String endTimeStr = request.getParameter("end_time").replace("T", " ") + ":00";
                    Timestamp startTime = Timestamp.valueOf(startTimeStr);
                    Timestamp endTime = Timestamp.valueOf(endTimeStr);

                    Booking newBooking = new Booking(0, userId, spaceId, startTime, endTime, status);
                    bookingService.createBooking(newBooking);
                    response.sendRedirect(request.getContextPath() + "/admin?action=viewBookings");
                } catch (Exception e) {
                    e.printStackTrace();
                    response.sendRedirect(request.getContextPath() + "/admin?action=viewBookings&error=invalidData");
                }
            } else if ("deleteUser".equals(action)) {
                int userId = Integer.parseInt(request.getParameter("userId"));
                userService.deleteUser(userId);
                response.sendRedirect(request.getContextPath() + "/admin?action=viewUsers");
            } else if ("deleteSpace".equals(action)) {
                int spaceId = Integer.parseInt(request.getParameter("spaceId"));
                spaceService.deleteSpace(spaceId);
                response.sendRedirect(request.getContextPath() + "/admin?action=viewSpaces");
            } else if ("deleteBooking".equals(action)) {
                int bookingId = Integer.parseInt(request.getParameter("bookingId"));
                bookingService.deleteBooking(bookingId);
                response.sendRedirect(request.getContextPath() + "/admin?action=viewBookings");
            } else if ("updateUser".equals(action)) {
                updateUser(request, response);
            } else if ("updateBooking".equals(action)) {
                updateBooking(request, response);
            }
        } catch (SQLException | NumberFormatException | NoSuchAlgorithmException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error performing action.");
        }
    }
    private void updateUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, NoSuchAlgorithmException {
        int userId = Integer.parseInt(request.getParameter("userId"));
        User existingUser = userService.getUserById(userId);

        User user = new User(
                userId,
                request.getParameter("username"),
                existingUser.getPassword(),
                request.getParameter("email"),
                request.getParameter("role"),
                existingUser.getToken()
        );

        String newPassword = request.getParameter("newPassword");
        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(userService.hashPassword(newPassword));
        }

        userService.updateUser(user);
        response.sendRedirect(request.getContextPath() + "/admin?action=viewUsers");
    }


    private void updateBooking(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        Booking booking = new Booking(
                Integer.parseInt(request.getParameter("bookingId")),
                Integer.parseInt(request.getParameter("userId")),
                Integer.parseInt(request.getParameter("spaceId")),
                Timestamp.valueOf(request.getParameter("startTime").replace("T", " ") + ":00"),
                Timestamp.valueOf(request.getParameter("endTime").replace("T", " ") + ":00"),
                request.getParameter("status")
        );

        bookingService.updateBooking(booking);
        response.sendRedirect(request.getContextPath() + "/admin?action=viewBookings");
    }

}
