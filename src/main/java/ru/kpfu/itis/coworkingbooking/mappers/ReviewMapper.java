package ru.kpfu.itis.coworkingbooking.mappers;

import ru.kpfu.itis.coworkingbooking.models.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReviewMapper {

    public Review toReview(ResultSet resultSet) throws SQLException {
        return new Review(
                resultSet.getInt("id"),
                resultSet.getInt("user_id"),
                resultSet.getInt("space_id"),
                resultSet.getInt("rating"),
                resultSet.getString("comment"),
                resultSet.getTimestamp("created_at")
        );
    }

    public Object[] toParams(Review review) {
        return new Object[]{
                review.getUserId(),
                review.getSpaceId(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        };
    }
}
