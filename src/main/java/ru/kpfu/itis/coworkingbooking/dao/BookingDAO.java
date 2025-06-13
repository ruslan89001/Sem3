package ru.kpfu.itis.coworkingbooking.dao;

import ru.kpfu.itis.coworkingbooking.mappers.BookingMapper;
import ru.kpfu.itis.coworkingbooking.models.Booking;
import ru.kpfu.itis.coworkingbooking.ConnectionWrapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    private static final String SELECT_ALL_BOOKINGS = "SELECT * FROM bookings";
    private static final String SELECT_BOOKING_BY_ID = "SELECT * FROM bookings WHERE id = ?";
    private static final String INSERT_BOOKING = "INSERT INTO bookings(user_id, space_id, start_time, end_time, status) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_BOOKING = "UPDATE bookings SET user_id = ?, space_id = ?, start_time = ?, end_time = ?, status = ? WHERE id = ?";
    private static final String DELETE_BOOKING = "DELETE FROM bookings WHERE id = ?";

    private BookingMapper bookingMapper = new BookingMapper();

    public List<Booking> getAllBookings() throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        try (Connection connection = ConnectionWrapper.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_ALL_BOOKINGS)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                bookings.add(bookingMapper.toBooking(rs));
            }
        }
        return bookings;
    }

    public Booking getBookingById(int id) throws SQLException {
        try (Connection connection = ConnectionWrapper.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BOOKING_BY_ID)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return bookingMapper.toBooking(rs);
            }
        }
        return null;
    }

    public void createBooking(Booking booking) throws SQLException {
        try (Connection connection = ConnectionWrapper.getConnection();
             PreparedStatement ps = connection.prepareStatement(INSERT_BOOKING)) {
            Object[] params = bookingMapper.toParams(booking);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            ps.executeUpdate();
        }
    }

    public void updateBooking(Booking booking) throws SQLException {
        try (Connection connection = ConnectionWrapper.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_BOOKING)) {
            ps.setInt(1, booking.getUserId());
            ps.setInt(2, booking.getSpaceId());
            ps.setTimestamp(3, booking.getStartTime());
            ps.setTimestamp(4, booking.getEndTime());
            ps.setString(5, booking.getStatus());
            ps.setInt(6, booking.getId());
            ps.executeUpdate();
        }
    }

    public List<Booking> getBookingsByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM bookings WHERE user_id = ?";
        List<Booking> bookings = new ArrayList<>();

        try (Connection conn = ConnectionWrapper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                bookings.add(bookingMapper.toBooking(rs));
            }
        }
        return bookings;
    }

    public void deleteBooking(int id) throws SQLException {
        try (Connection connection = ConnectionWrapper.getConnection();
             PreparedStatement ps = connection.prepareStatement(DELETE_BOOKING)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
