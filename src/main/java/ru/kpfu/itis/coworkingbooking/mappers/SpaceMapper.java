package ru.kpfu.itis.coworkingbooking.mappers;

import ru.kpfu.itis.coworkingbooking.models.Space;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SpaceMapper {

    public Space toSpace(ResultSet resultSet) throws SQLException {
        return new Space(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getDouble("price"),
                resultSet.getString("location"),
                resultSet.getString("image"),
                resultSet.getBoolean("availability")
        );
    }

    public Object[] toParams(Space space) {
        return new Object[]{
                space.getName(),
                space.getDescription(),
                space.getPrice(),
                space.getLocation(),
                space.getImage(),
                space.isAvailability()
        };
    }
}
