package ru.kpfu.itis.coworkingbooking.dao;

import ru.kpfu.itis.coworkingbooking.mappers.WorkingHoursMapper;
import ru.kpfu.itis.coworkingbooking.models.WorkingHours;
import ru.kpfu.itis.coworkingbooking.ConnectionWrapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkingHoursDAO {

    private static final String SELECT_ALL_WORKING_HOURS = "SELECT * FROM working_hours";
    private static final String SELECT_WORKING_HOURS_BY_ID = "SELECT * FROM working_hours WHERE id = ?";
    private static final String INSERT_WORKING_HOURS = "INSERT INTO working_hours(space_id, day_of_week, start_time, end_time) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_WORKING_HOURS = "UPDATE working_hours SET space_id = ?, day_of_week = ?, start_time = ?, end_time = ? WHERE id = ?";
    private static final String DELETE_WORKING_HOURS = "DELETE FROM working_hours WHERE id = ?";

    private WorkingHoursMapper workingHoursMapper = new WorkingHoursMapper();

    public List<WorkingHours> getAllWorkingHours() throws SQLException {
        List<WorkingHours> workingHoursList = new ArrayList<>();
        try (Connection connection = ConnectionWrapper.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_ALL_WORKING_HOURS)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                workingHoursList.add(workingHoursMapper.toWorkingHours(rs));
            }
        }
        return workingHoursList;
    }

    public WorkingHours getWorkingHoursById(int id) throws SQLException {
        try (Connection connection = ConnectionWrapper.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_WORKING_HOURS_BY_ID)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return workingHoursMapper.toWorkingHours(rs);
            }
        }
        return null;
    }

    public void createWorkingHours(WorkingHours workingHours) throws SQLException {
        try (Connection connection = ConnectionWrapper.getConnection();
             PreparedStatement ps = connection.prepareStatement(INSERT_WORKING_HOURS)) {
            Object[] params = workingHoursMapper.toParams(workingHours);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            ps.executeUpdate();
        }
    }

    public void updateWorkingHours(WorkingHours workingHours) throws SQLException {
        try (Connection connection = ConnectionWrapper.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_WORKING_HOURS)) {
            ps.setInt(1, workingHours.getSpaceId());
            ps.setString(2, workingHours.getDayOfWeek());
            ps.setTimestamp(3, workingHours.getStartTime());
            ps.setTimestamp(4, workingHours.getEndTime());
            ps.setInt(5, workingHours.getId());
            ps.executeUpdate();
        }
    }

    public void deleteWorkingHours(int id) throws SQLException {
        try (Connection connection = ConnectionWrapper.getConnection();
             PreparedStatement ps = connection.prepareStatement(DELETE_WORKING_HOURS)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
