package com.tillchen.jstore.models;

import androidx.annotation.Nullable;

public class User {
    private String fullName;
    private boolean whatsApp;
    private String phoneNumber; // null when whatsApp is false
    private String email;

    public User(String fullName, boolean whatsApp, @Nullable String phoneNumber, String email) {
        this.fullName = fullName;
        this.whatsApp = whatsApp;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public boolean getWhatsApp() {
        return whatsApp;
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
        this.whatsApp = whatsApp;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
