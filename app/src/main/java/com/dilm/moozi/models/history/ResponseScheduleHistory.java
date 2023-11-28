package com.dilm.moozi.models.history;

import com.dilm.moozi.models.Class;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseScheduleHistory {

    @SerializedName("status")
    private boolean status;

    @SerializedName("body")
    private List<Class> classList;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<Class> getClassList() {
        return classList;
    }

    public void setClassList(List<Class> classList) {
        this.classList = classList;
    }
}
