package com.dilm.moozi.models.schedule;

import com.dilm.moozi.models.profile.Profile;
import com.dilm.moozi.models.profile.StudentProfile;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ScheduleStudents {

    @SerializedName("status")
    private boolean status;

    @SerializedName("body")
    private List<StudentProfile> studentProfileList;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<StudentProfile> getStudentProfileList() {
        return studentProfileList;
    }

    public void setStudentProfileList(List<StudentProfile> studentProfileList) {
        this.studentProfileList = studentProfileList;
    }
}
