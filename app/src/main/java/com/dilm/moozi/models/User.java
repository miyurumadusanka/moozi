package com.dilm.moozi.models;

public class User {

    private String fullName;
    private String address;
    private String phoneNum;
    private String grade;

    public User(String fullName, String address, String phoneNum, String grade) {
        this.fullName = fullName;
        this.address = address;
        this.phoneNum = phoneNum;
        this.grade = grade;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
