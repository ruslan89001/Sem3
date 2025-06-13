package ru.kpfu.itis.coworkingbooking.services;

import ru.kpfu.itis.coworkingbooking.dao.ReviewDAO;
import ru.kpfu.itis.coworkingbooking.models.Review;

import java.sql.SQLException;
import java.util.List;

public class ReviewService {

    private ReviewDAO reviewDAO = new ReviewDAO();

    public List<Review> getAllReviews() throws SQLException {
        return reviewDAO.getAllReviews();
    }

    public Review getReviewById(int id) throws SQLException {
        return reviewDAO.getReviewById(id);
    }

    public List<Review> getReviewsBySpaceId(int spaceId) throws SQLException {
        return reviewDAO.getReviewsBySpaceId(spaceId);
    }

    public void createReview(Review review) throws SQLException {
        reviewDAO.createReview(review);
    }

    public void updateReview(Review review) throws SQLException {
        reviewDAO.updateReview(review);
    }

    public void deleteReview(int id) throws SQLException {
        reviewDAO.deleteReview(id);
    }
}
