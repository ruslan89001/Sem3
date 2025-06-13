package ru.kpfu.itis.coworkingbooking.servlets;

import ru.kpfu.itis.coworkingbooking.models.Review;
import ru.kpfu.itis.coworkingbooking.models.Space;
import ru.kpfu.itis.coworkingbooking.models.User;
import ru.kpfu.itis.coworkingbooking.services.SpaceService;
import ru.kpfu.itis.coworkingbooking.services.ReviewService;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.sql.Timestamp;
import java.util.Map;

@WebServlet("/reviews")
public class ReviewServlet extends HttpServlet {

    private ReviewService reviewService = new ReviewService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            SpaceService spaceService = (SpaceService) getServletContext().getAttribute("spaceService");
            ReviewService reviewService = (ReviewService) getServletContext().getAttribute("reviewService");

            List<Space> spaces = spaceService.getAllSpaces();

            List<Review> reviews = reviewService.getAllReviews();

            List<Map<String, Object>> enrichedReviews = new ArrayList<>();

            for (Review review : reviews) {
                Map<String, Object> reviewData = new HashMap<>();

                Space space = spaceService.getSpaceById(review.getSpaceId());
                String spaceName = space != null ? space.getName() : "Неизвестно";

                reviewData.put("review", review);
                reviewData.put("spaceName", spaceName);

                enrichedReviews.add(reviewData);
            }

            request.setAttribute("spaces", spaces);
            request.setAttribute("enrichedReviews", enrichedReviews);

            request.getRequestDispatcher("/WEB-INF/jsp/reviews.jsp").forward(request, response);

        } catch (SQLException e) {
            response.sendRedirect(request.getContextPath() + "/error?code=500");
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");

            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            int spaceId = Integer.parseInt(request.getParameter("spaceId"));
            int rating = Integer.parseInt(request.getParameter("rating"));
            String comment = request.getParameter("comment");

            if (rating < 1 || rating > 5) {
                throw new IllegalArgumentException("Invalid rating");
            }

            Review review = new Review(
                    0,
                    user.getId(),
                    spaceId,
                    rating,
                    comment,
                    new Timestamp(System.currentTimeMillis())
            );

            reviewService.createReview(review);
            response.sendRedirect(request.getContextPath() + "/reviews");

        } catch (SQLException | IllegalArgumentException e) {
            response.sendRedirect(request.getContextPath() + "/reviews?error=invalidInput");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int reviewId = Integer.parseInt(request.getParameter("id"));
            reviewService.deleteReview(reviewId);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT); // Возвращаем статус успешного удаления
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error deleting review.");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid input.");
        }
    }
}


