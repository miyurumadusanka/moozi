package com.dilm.moozi.models.schedule;

import com.dilm.moozi.models.profile.Profile;
import com.google.gson.annotations.SerializedName;

public class ResponseAddStudent {

    @SerializedName("status")
    private boolean status;

    @SerializedName("body")
    private Profile profile;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
