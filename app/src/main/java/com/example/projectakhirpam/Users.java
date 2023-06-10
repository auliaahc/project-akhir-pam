package com.example.projectakhirpam;

public class Users {
    String userId, profile, name, email, phoneNumber;

    public Users(String userId, String profile, String name, String phoneNumber, String email) {
        this.userId = userId;
        this.profile = profile;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public Users() {}
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getProfile() {
        return profile;
    }
    public void setProfile(String profile) {
        this.profile = profile;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

}
