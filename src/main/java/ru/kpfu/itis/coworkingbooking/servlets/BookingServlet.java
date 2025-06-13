package ru.kpfu.itis.coworkingbooking.servlets;

import ru.kpfu.itis.coworkingbooking.models.User;
import ru.kpfu.itis.coworkingbooking.services.BookingService;
import ru.kpfu.itis.coworkingbooking.models.Booking;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.sql.Timestamp;


@WebServlet("/bookings")
public class BookingServlet extends HttpServlet {

    private BookingService bookingService = new BookingService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            List<Booking> bookings = bookingService.getBookingsByUserId(user.getId());
            request.setAttribute("bookings", bookings);
            request.getRequestDispatcher("/WEB-INF/jsp/bookings.jsp").forward(request, response);
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Проверяем, что пользователь авторизован
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int spaceId = Integer.parseInt(request.getParameter("spaceId"));
            String startTimeStr = request.getParameter("startTime").replace("T", " ") + ":00";
            String endTimeStr = request.getParameter("endTime").replace("T", " ") + ":00";

            Timestamp startTime = Timestamp.valueOf(startTimeStr);
            Timestamp endTime = Timestamp.valueOf(endTimeStr);

            // Проверяем, что время начала раньше времени окончания
            if (startTime.after(endTime)) {
                response.sendRedirect(request.getContextPath() + "/spaces?error=invalidTime");
                return;
            }

            Booking booking = new Booking(
                    0,
                    user.getId(),
                    spaceId,
                    startTime,
                    endTime,
                    "pending"
            );

            bookingService.createBooking(booking);
            response.sendRedirect(request.getContextPath() + "/bookings");

        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/spaces?error=bookingError");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int bookingId = Integer.parseInt(request.getParameter("id"));
            bookingService.deleteBooking(bookingId);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (SQLException | NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error deleting booking.");
        }
    }
}
