package ru.kpfu.itis.coworkingbooking.services;

import ru.kpfu.itis.coworkingbooking.dao.BookingDAO;
import ru.kpfu.itis.coworkingbooking.models.Booking;

import java.sql.SQLException;
import java.util.List;

public class BookingService {

    private BookingDAO bookingDAO = new BookingDAO();

    public List<Booking> getAllBookings() throws SQLException {
        return bookingDAO.getAllBookings();
    }

    public Booking getBookingById(int id) throws SQLException {
        return bookingDAO.getBookingById(id);
    }

    public void createBooking(Booking booking) throws SQLException {
        bookingDAO.createBooking(booking);
    }

    public void updateBooking(Booking booking) throws SQLException {
        bookingDAO.updateBooking(booking);
    }

    public List<Booking> getBookingsByUserId(int userId) throws SQLException {
        return bookingDAO.getBookingsByUserId(userId);
    }

    public void deleteBooking(int id) throws SQLException {
        bookingDAO.deleteBooking(id);
    }
}
