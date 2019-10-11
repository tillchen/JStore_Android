package com.tillchen.jstore.models;

import androidx.annotation.Nullable;

public class User {
    private String fullName;
    private boolean isWhatsApp;
    private String phoneNumber; // null when isWhatsApp is false
    private String email;

    public User(String fullName, boolean isWhatsApp, @Nullable String phoneNumber, String email) {
        this.fullName = fullName;
        this.isWhatsApp = isWhatsApp;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public boolean isWhatsApp() {
        return isWhatsApp;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setWhatsApp(boolean whatsApp) {
        isWhatsApp = whatsApp;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
