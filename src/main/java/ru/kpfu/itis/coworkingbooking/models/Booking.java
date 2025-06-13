package ru.kpfu.itis.coworkingbooking.models;

import java.sql.Timestamp;

public class Booking {
    private int id;
    private int userId;
    private int spaceId;
    private Timestamp startTime;
    private Timestamp endTime;
    private String status;

    public Booking(int id, int userId, int spaceId, Timestamp startTime, Timestamp endTime, String status) {
        this.id = id;
        this.userId = userId;
        this.spaceId = spaceId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
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

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
