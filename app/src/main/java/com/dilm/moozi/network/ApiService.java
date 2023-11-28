package com.dilm.moozi.network;

import com.dilm.moozi.models.login.LoginRequest;
import com.dilm.moozi.models.profile.ProfileRequest;
import com.dilm.moozi.models.registration.SignupRequest;
import com.dilm.moozi.models.schedule.Schedule;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiService {

    @POST("auth/signin")
    Call<JsonObject> postLogin(@Body LoginRequest authRequest);

    @POST("auth/signup")
    Call<JsonObject> postRegistration(@Body SignupRequest signupRequest);

    @GET("schedule/get-all-students")
    Call<JsonObject> getScheduleStudentList(@Query("schedule_id") String scheduleId);

    @POST("schedule/add_schedule_student")
    Call<JsonObject> addScheduleStudent(@Query("schedule_id") String scheduleId, @Query("student_code") String studentId);

    @DELETE("schedule/remove_schedule_student")
    Call<JsonObject> removeScheduleStudent(@Query("schedule_id") String scheduleId, @Query("student_code") String studentId);

    @POST("schedule/create")
    Call<JsonObject> createSchedule(@Header("Authorization") String token, @Body Schedule schedule);

    @GET("schedule/get-all-teacher-schedules")
    Call<JsonObject> getSchedules(@Header("Authorization") String token);

    @GET("schedule/get-schedule-history")
    Call<JsonObject> getScheduleHistory(@Header("Authorization") String token);

    @POST("profile/create")
    Call<JsonObject> createProfile(@Header("Authorization") String token, @Body ProfileRequest profileRequest);

    @GET("profile/get-profile")
    Call<JsonObject> getProfile(@Header("Authorization") String token);

    @GET("schedule/get-student-schedules-history")
    Call<JsonObject> getStudentScheduleHistory(@Header("Authorization") String token);

    @GET("schedule/get-all-student-schedules")
    Call<JsonObject> getStudentSchedules(@Header("Authorization") String token);

    @DELETE("schedule/remove_schedule")
    Call<JsonObject> deleteSchedule(@Header("Authorization") String token, @Query("schedule_id") String scheduleId);

    @GET("schedule/create-attendance")
    Call<JsonObject> createStudentScheduleAttendance(@Header("Authorization") String token, @Query("schedule_id") String scheduleId);

    @PUT("schedule/update")
    Call<JsonObject> updateSchedule(@Header("Authorization") String token, @Body Schedule schedule);
}
