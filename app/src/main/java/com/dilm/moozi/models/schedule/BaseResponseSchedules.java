package com.dilm.moozi.models.schedule;

import com.dilm.moozi.models.Class;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BaseResponseSchedules {

    @SerializedName("status")
    private boolean status;

    @SerializedName("body")
    private ResponseSchedule responseSchedule;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public ResponseSchedule getResponseSchedule() {
        return responseSchedule;
    }

    public void setResponseSchedule(ResponseSchedule responseSchedule) {
        this.responseSchedule = responseSchedule;
    }
}
