package ru.kpfu.itis.coworkingbooking.mappers;

import ru.kpfu.itis.coworkingbooking.models.Booking;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BookingMapper {

    public Booking toBooking(ResultSet resultSet) throws SQLException {
        return new Booking(
                resultSet.getInt("id"),
                resultSet.getInt("user_id"),
                resultSet.getInt("space_id"),
                resultSet.getTimestamp("start_time"),
                resultSet.getTimestamp("end_time"),
                resultSet.getString("status")
        );
    }

    public Object[] toParams(Booking booking) {
        return new Object[]{
                booking.getUserId(),
                booking.getSpaceId(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getStatus()
        };
    }
}
