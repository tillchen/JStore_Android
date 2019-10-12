package com.tillchen.jstore.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Post {
    private boolean sold = false;
    private String ownerId;
    private String title;
    private String category;
    private String condition;
    private String description;
    private String imageUrl;
    private double price; // TODO: 1 Limit the digits
    private String[] paymentOptions;
    private @ServerTimestamp Date creationTime;
    private @ServerTimestamp Date soldTime;

    public Post(String ownerId, String title, String category, String condition, String description, String imageUrl, double price, String[] paymentOptions) {
        this.ownerId = ownerId;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String[] getPaymentOptions() {
        return paymentOptions;
    }

    public void setPaymentOptions(String[] paymentOptions) {
        this.paymentOptions = paymentOptions;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        if (!sold) {
            this.creationTime = creationTime;
        }
    }

    public Date getSoldTime() {
        return soldTime;
    }

    public void setSoldTime(Date soldTime) {
        if (sold) {
            this.soldTime = soldTime;
        }

    }
}
