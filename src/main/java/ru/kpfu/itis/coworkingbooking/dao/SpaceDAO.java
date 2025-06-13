package ru.kpfu.itis.coworkingbooking.dao;

import ru.kpfu.itis.coworkingbooking.mappers.SpaceMapper;
import ru.kpfu.itis.coworkingbooking.models.Space;
import ru.kpfu.itis.coworkingbooking.ConnectionWrapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpaceDAO {

    private static final String SELECT_ALL_SPACES = "SELECT * FROM spaces";
    private static final String SELECT_SPACE_BY_ID = "SELECT * FROM spaces WHERE id = ?";
    private static final String INSERT_SPACE = "INSERT INTO spaces(name, description, price, location, image, availability) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SPACE = "UPDATE spaces SET name = ?, description = ?, price = ?, location = ?, image = ? WHERE id = ?";
    private static final String DELETE_SPACE = "DELETE FROM spaces WHERE id = ?";

    private SpaceMapper spaceMapper = new SpaceMapper();

    public List<Space> getAllSpaces() throws SQLException {
        List<Space> spaces = new ArrayList<>();
        try (Connection connection = ConnectionWrapper.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_ALL_SPACES)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                spaces.add(spaceMapper.toSpace(rs));
            }
        }
        System.out.println("Количество пространств: " + spaces.size());
        return spaces;
    }

    public Space getSpaceById(int id) throws SQLException {
        try (Connection connection = ConnectionWrapper.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_SPACE_BY_ID)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return spaceMapper.toSpace(rs);
            }
        }
        return null;
    }

    public void createSpace(Space space) throws SQLException {
        try (Connection connection = ConnectionWrapper.getConnection();
             PreparedStatement ps = connection.prepareStatement(INSERT_SPACE)) {
            Object[] params = spaceMapper.toParams(space);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            ps.executeUpdate();
        }
    }

    public void updateSpace(Space space) throws SQLException {
        try (Connection connection = ConnectionWrapper.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_SPACE)) {
            Object[] params = spaceMapper.toParams(space);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            ps.setInt(6, space.getId());
            ps.executeUpdate();
        }
    }

    public void deleteSpace(int id) throws SQLException {
        try (Connection connection = ConnectionWrapper.getConnection();
             PreparedStatement ps = connection.prepareStatement(DELETE_SPACE)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
