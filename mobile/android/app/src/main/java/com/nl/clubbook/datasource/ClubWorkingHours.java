package com.nl.clubbook.datasource;

/**
 * Created by Volodymyr on 15.08.2014.
 */
public class ClubWorkingHours {

    public static final String STATUS_OPENED = "opened";
    public static final String STATUS_CLOSED = "closed";

    private String id;
    private String status;
    private String startTime;
    private String endTime;
    private int day;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
