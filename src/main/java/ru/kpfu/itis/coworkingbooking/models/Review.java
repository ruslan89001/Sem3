package ru.kpfu.itis.coworkingbooking.models;

import java.sql.Timestamp;

public class Review {
    private int id;
    private int userId;
    private int spaceId;
    private int rating;
    private String comment;
    private Timestamp createdAt;

    public Review(int id, int userId, int spaceId, int rating, String comment, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.spaceId = spaceId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(int spaceId) {
        this.spaceId = spaceId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
