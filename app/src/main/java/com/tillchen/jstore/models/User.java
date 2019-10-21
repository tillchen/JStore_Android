package com.tillchen.jstore.models;

import androidx.annotation.Nullable;

import java.util.Date;


public class User {
    private String fullName;
    private boolean whatsApp;
    private String phoneNumber; // null when whatsApp is false
    private String email;
    private Date creationDate;

    public static final String CREATIONDATE = "creationDate";
    public static final String FULL_NAME = "fullName";
    public static final String EMAIL = "email";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String WHATSAPP = "whatsApp";

    public User() {
    }

    public User(String fullName, boolean whatsApp, @Nullable String phoneNumber, String email) {
        this.fullName = fullName;
        this.whatsApp = whatsApp;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isWhatsApp() {
        return whatsApp;
    }

    public void setWhatsApp(boolean whatsApp) {
        this.whatsApp = whatsApp;
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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
