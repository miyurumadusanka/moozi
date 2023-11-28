package com.dilm.moozi.utils;

import android.content.Context;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;

import java.net.MalformedURLException;
import java.net.URL;

public class MeetConference {

    private final Context context;
    private JitsiMeetConferenceOptions options;

    public MeetConference(Context context, String username, String meetingId, String className) {
        this.context = context;

        JitsiMeetUserInfo userInfo = new JitsiMeetUserInfo();
        userInfo.setDisplayName(username);
        try {
            options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL("https://meet.jit.si"))
                    .setRoom(meetingId)
                    .setSubject(className)
                    .setUserInfo(userInfo)
                    .setAudioMuted(false)
                    .setVideoMuted(true)
                    .setConfigOverride("requireDisplayName", false)
                    .setFeatureFlag("welcomepage.enabled", false)
                    .setFeatureFlag("prejoinpage.enabled", false)
                    .setFeatureFlag("invite.enabled", false)
                    .build();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void initProcess() {
        JitsiMeetActivity.launch(context, options);
    }
}
