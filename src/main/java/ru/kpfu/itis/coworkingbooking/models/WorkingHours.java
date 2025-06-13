package ru.kpfu.itis.coworkingbooking.models;

import java.sql.Timestamp;

public class WorkingHours {

    private int id;
    private int spaceId;
    private String dayOfWeek;
    private Timestamp startTime;
    private Timestamp endTime;

    public WorkingHours(int id, int spaceId, String dayOfWeek, Timestamp startTime, Timestamp endTime) {
        this.id = id;
        this.spaceId = spaceId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(int spaceId) {
        this.spaceId = spaceId;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
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
}
