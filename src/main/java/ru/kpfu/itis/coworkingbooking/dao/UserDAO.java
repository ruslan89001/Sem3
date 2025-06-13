package ru.kpfu.itis.coworkingbooking.dao;

import ru.kpfu.itis.coworkingbooking.mappers.UserMapper;
import ru.kpfu.itis.coworkingbooking.models.User;
import ru.kpfu.itis.coworkingbooking.ConnectionWrapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserDAO {

    private static final String SELECT_USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String SELECT_USER_BY_TOKEN = "SELECT * FROM users WHERE token = ?";
    private static final String UPDATE_USER_TOKEN = "UPDATE users SET token = ? WHERE id = ?";
    private static final String SELECT_USER_BY_USERNAME = "SELECT * FROM users WHERE username = ?";
    private static final String SELECT_USER_BY_EMAIL = "SELECT * FROM users WHERE email = ?";
    private static final String INSERT_USER = "INSERT INTO users(username, password, email, role, token) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_ALL_USERS = "SELECT * FROM users";
    private static final String DELETE_USER_BY_ID = "DELETE FROM users WHERE id = ?";
    private static final String UPDATE_USER = "UPDATE users SET username = ?, password = ?, email = ?, role = ?, token = ? WHERE id = ?";
    private static final String UPDATE_USER_WITHOUT_PASSWORD = "UPDATE users SET username = ?, email = ?, role = ?, token = ? WHERE id = ?";
    private static final String CHECK_USERNAME_EXISTS = "SELECT COUNT(*) FROM users WHERE username = ? AND id != ?";
    private static final String CHECK_EMAIL_EXISTS = "SELECT COUNT(*) FROM users WHERE email = ? AND id != ?";

    private UserMapper userMapper = new UserMapper();

    public User getUserById(int id) throws SQLException {
        Connection connection = null;
        try {
            connection = ConnectionWrapper.getConnection();
            PreparedStatement ps = connection.prepareStatement(SELECT_USER_BY_ID);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return userMapper.toUser(rs);
            }
        } finally {
            ConnectionWrapper.closeConnection(connection);
        }
        return null;
    }

    public User getUserByToken(UUID token) throws SQLException {
        Connection connection = null;
        try {
            connection = ConnectionWrapper.getConnection();
            PreparedStatement ps = connection.prepareStatement(SELECT_USER_BY_TOKEN);
            ps.setObject(1, token, Types.OTHER);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return userMapper.toUser(rs);
            }
        } finally {
            ConnectionWrapper.closeConnection(connection);
        }
        return null;
    }

    public User getUserByUsername(String username) throws SQLException {
        Connection connection = null;
        try {
            connection = ConnectionWrapper.getConnection();
            PreparedStatement ps = connection.prepareStatement(SELECT_USER_BY_USERNAME);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return userMapper.toUser(rs);
            }
        } finally {
            ConnectionWrapper.closeConnection(connection);
        }
        return null;
    }

    public User getUserByEmail(String email) throws SQLException {
        Connection connection = null;
        try {
            connection = ConnectionWrapper.getConnection();
            PreparedStatement ps = connection.prepareStatement(SELECT_USER_BY_EMAIL);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return userMapper.toUser(rs);
            }
        } finally {
            ConnectionWrapper.closeConnection(connection);
        }
        return null;
    }

    public void createUser(User user) throws SQLException {
        Connection connection = null;
        try {
            connection = ConnectionWrapper.getConnection();
            PreparedStatement ps = connection.prepareStatement(INSERT_USER);
            Object[] params = userMapper.toParams(user);
            for (int i = 0; i < params.length; i++) {
                if (params[i] instanceof UUID) {
                    ps.setObject(i + 1, params[i], Types.OTHER);
                } else {
                    ps.setObject(i + 1, params[i]);
                }
            }
            ps.executeUpdate();
        } finally {
            ConnectionWrapper.closeConnection(connection);
        }
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        Connection connection = null;
        try {
            connection = ConnectionWrapper.getConnection();
            PreparedStatement ps = connection.prepareStatement(SELECT_ALL_USERS);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = userMapper.toUser(rs);
                System.out.println("[DEBUG] Загружен пользователь: "
                        + user.getUsername() + ", токен: " + user.getToken());
                users.add(user);
            }
        } finally {
            ConnectionWrapper.closeConnection(connection);
        }
        return users;
    }

    public void deleteUser(int id) throws SQLException {
        Connection connection = null;
        try {
            connection = ConnectionWrapper.getConnection();
            PreparedStatement ps = connection.prepareStatement(DELETE_USER_BY_ID);
            ps.setInt(1, id);
            ps.executeUpdate();
        } finally {
            ConnectionWrapper.closeConnection(connection);
        }
    }

    public void updateUserToken(User user) throws SQLException {
        try (Connection connection = ConnectionWrapper.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_USER_TOKEN)) {
            ps.setObject(1, user.getToken(), Types.OTHER);
            ps.setInt(2, user.getId());
            ps.executeUpdate();
        }
    }

    public void updateUser(User user) throws SQLException {
        Connection connection = null;
        try {
            connection = ConnectionWrapper.getConnection();
            PreparedStatement ps;

            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                ps = connection.prepareStatement(UPDATE_USER);
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getRole());
                ps.setObject(5, user.getToken(), Types.OTHER);
                ps.setInt(6, user.getId());
            } else {
                ps = connection.prepareStatement(UPDATE_USER_WITHOUT_PASSWORD);
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getEmail());
                ps.setString(3, user.getRole());
                ps.setObject(4, user.getToken(), Types.OTHER);
                ps.setInt(5, user.getId());
            }

            ps.executeUpdate();
        } finally {
            ConnectionWrapper.closeConnection(connection);
        }
    }

    public boolean isUsernameExists(String username, int excludeId) throws SQLException {
        try (Connection conn = ConnectionWrapper.getConnection();
             PreparedStatement ps = conn.prepareStatement(CHECK_USERNAME_EXISTS)) {
            ps.setString(1, username);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public boolean isEmailExists(String email, int excludeId) throws SQLException {
        try (Connection conn = ConnectionWrapper.getConnection();
             PreparedStatement ps = conn.prepareStatement(CHECK_EMAIL_EXISTS)) {
            ps.setString(1, email);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}
