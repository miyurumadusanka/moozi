package com.dilm.moozi.models.schedule;

import com.dilm.moozi.models.Class;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseSchedule {

    @SerializedName("today_schedules")
    private List<Class> todaySchedules;

    @SerializedName("upcoming_schedule")
    private List<Class> upComingSchedules;

    public List<Class> getTodaySchedules() {
        return todaySchedules;
    }

    public void setTodaySchedules(List<Class> todaySchedules) {
        this.todaySchedules = todaySchedules;
    }

    public List<Class> getUpComingSchedules() {
        return upComingSchedules;
    }

    public void setUpComingSchedules(List<Class> upComingSchedules) {
        this.upComingSchedules = upComingSchedules;
    }
}
