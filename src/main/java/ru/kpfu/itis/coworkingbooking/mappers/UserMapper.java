package ru.kpfu.itis.coworkingbooking.mappers;

import ru.kpfu.itis.coworkingbooking.models.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserMapper {

    public User toUser(ResultSet resultSet) throws SQLException {
        String tokenStr = resultSet.getString("token");
        UUID token = null;

        if (tokenStr != null && !tokenStr.trim().isEmpty()) {
            try {
                token = UUID.fromString(tokenStr.trim());
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid UUID format: " + tokenStr);
            }
        }

        return new User(
                resultSet.getInt("id"),
                resultSet.getString("username"),
                resultSet.getString("password"),
                resultSet.getString("email"),
                resultSet.getString("role"),
                token
        );
    }

    public Object[] toParams(User user) {
        return new Object[]{
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getRole(),
                user.getToken() != null ? user.getToken().toString() : null
        };
    }
}