package ru.kpfu.itis.coworkingbooking.mappers;

import ru.kpfu.itis.coworkingbooking.models.WorkingHours;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WorkingHoursMapper {

    public WorkingHours toWorkingHours(ResultSet resultSet) throws SQLException {
        return new WorkingHours(
                resultSet.getInt("id"),
                resultSet.getInt("space_id"),
                resultSet.getString("day_of_week"),
                resultSet.getTimestamp("start_time"),
                resultSet.getTimestamp("end_time")
        );
    }

    public Object[] toParams(WorkingHours workingHours) {
        return new Object[]{
                workingHours.getSpaceId(),
                workingHours.getDayOfWeek(),
                workingHours.getStartTime(),
                workingHours.getEndTime()
        };
    }
}
