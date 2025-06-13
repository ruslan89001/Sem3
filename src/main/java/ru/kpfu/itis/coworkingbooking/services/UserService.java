package ru.kpfu.itis.coworkingbooking.services;

import ru.kpfu.itis.coworkingbooking.dao.UserDAO;
import ru.kpfu.itis.coworkingbooking.models.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class UserService {
    private UserDAO userDAO = new UserDAO();

    public User getUserById(int id) throws SQLException {
        return userDAO.getUserById(id);
    }

    public User getUserByToken(UUID token) throws SQLException {
        return userDAO.getUserByToken(token);
    }

    public User getUserByUsername(String username) throws SQLException {
        return userDAO.getUserByUsername(username);
    }

    public User getUserByEmail(String email) throws SQLException {
        return userDAO.getUserByEmail(email);
    }

    public void registerUser(User user) throws SQLException {
        if (user.getToken() == null) {
            user.setToken(UUID.randomUUID());
        }
        userDAO.createUser(user);
    }

    public List<User> getAllUsers() throws SQLException {
        return userDAO.getAllUsers();
    }

    public void updateUser(User user) throws SQLException {
        userDAO.updateUser(user);
    }

    public boolean isUsernameExists(String username, int excludeId) throws SQLException {
        return userDAO.isUsernameExists(username, excludeId);
    }

    public boolean isEmailExists(String email, int excludeId) throws SQLException {
        return userDAO.isEmailExists(email, excludeId);
    }

    public void deleteUser(int id) throws SQLException {
        userDAO.deleteUser(id);
    }
    public String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
