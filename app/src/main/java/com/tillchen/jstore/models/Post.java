package com.tillchen.jstore.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;

public class Post {
    private boolean sold = false;
    private String ownerId; // Email address
    private String ownerName;
    private boolean whatsApp = false;
    private String phoneNumber;
    private String title;
    private String category;
    private String condition;
    private String description;
    private String imageUrl;
    private String price; // TODO: 1 Limit the digits
    private ArrayList<String> paymentOptions;
    private @ServerTimestamp Date creationDate;
    private Date soldDate;

    public Post() {
    }

    public Post(String ownerId, String ownerName, boolean whatsApp, String phoneNumber, String title, String category, String condition,
                String description, String imageUrl, String price, ArrayList<String> paymentOptions) {
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.whatsApp = whatsApp;
        this.phoneNumber = phoneNumber;
        this.title = title;
        this.category = category;
        this.condition = condition;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.paymentOptions = paymentOptions;
    }

    public boolean isSold() {
        return sold;
    }

    public void setSold(boolean sold) {
        this.sold = sold;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public ArrayList<String> getPaymentOptions() {
        return paymentOptions;
    }

    public void setPaymentOptions(ArrayList<String> paymentOptions) {
        this.paymentOptions = paymentOptions;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getSoldDate() {
        return soldDate;
    }

    public void setSoldDate(Date soldDate) {
        this.soldDate = soldDate;
    }
}
