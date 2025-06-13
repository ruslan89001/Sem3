package ru.kpfu.itis.coworkingbooking.dao;

import ru.kpfu.itis.coworkingbooking.mappers.ReviewMapper;
import ru.kpfu.itis.coworkingbooking.models.Review;
import ru.kpfu.itis.coworkingbooking.ConnectionWrapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

    private static final String SELECT_ALL_REVIEWS = "SELECT * FROM reviews";
    private static final String SELECT_REVIEW_BY_ID = "SELECT * FROM reviews WHERE id = ?";
    private static final String SELECT_REVIEWS_BY_SPACE_ID = "SELECT * FROM reviews WHERE space_id = ?";
    private static final String INSERT_REVIEW = "INSERT INTO reviews(user_id, space_id, rating, comment, created_at) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_REVIEW = "UPDATE reviews SET rating = ?, comment = ? WHERE id = ?";
    private static final String DELETE_REVIEW = "DELETE FROM reviews WHERE id = ?";

    private ReviewMapper reviewMapper = new ReviewMapper();

    public List<Review> getAllReviews() throws SQLException {
        List<Review> reviews = new ArrayList<>();
        try (Connection connection = ConnectionWrapper.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_ALL_REVIEWS)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reviews.add(reviewMapper.toReview(rs));
            }
        }
        return reviews;
    }

    public Review getReviewById(int id) throws SQLException {
        try (Connection connection = ConnectionWrapper.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_REVIEW_BY_ID)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return reviewMapper.toReview(rs);
            }
        }
        return null;
    }

    public List<Review> getReviewsBySpaceId(int spaceId) throws SQLException {
        List<Review> reviews = new ArrayList<>();
        try (Connection connection = ConnectionWrapper.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_REVIEWS_BY_SPACE_ID)) {
            ps.setInt(1, spaceId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reviews.add(reviewMapper.toReview(rs));
            }
        }
        return reviews;
    }

    public void createReview(Review review) throws SQLException {
        try (Connection connection = ConnectionWrapper.getConnection();
             PreparedStatement ps = connection.prepareStatement(INSERT_REVIEW)) {
            Object[] params = reviewMapper.toParams(review);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            ps.executeUpdate();
        }
    }

    public void updateReview(Review review) throws SQLException {
        try (Connection connection = ConnectionWrapper.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_REVIEW)) {
            ps.setInt(1, review.getRating());
            ps.setString(2, review.getComment());
            ps.setInt(3, review.getId());
            ps.executeUpdate();
        }
    }

    public void deleteReview(int id) throws SQLException {
        try (Connection connection = ConnectionWrapper.getConnection();
             PreparedStatement ps = connection.prepareStatement(DELETE_REVIEW)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
