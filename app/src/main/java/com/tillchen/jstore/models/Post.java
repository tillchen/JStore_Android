package com.tillchen.jstore.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;

public class Post {
    private boolean sold = false;
    private String ownerId; // Email address
    private String title;
    private String category;
    private String condition;
    private String description;
    private String imageUrl;
    private String price; // TODO: 1 Limit the digits
    private ArrayList<String> paymentOptions;
    private @ServerTimestamp Date creationDate;
    private @ServerTimestamp Date soldDate;

    public Post(String ownerId, String title, String category, String condition, String description, String imageUrl, String price, ArrayList<String> paymentOptions) {
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
        if (!sold) {
            this.creationDate = creationDate;
        }
    }

    public Date getSoldDate() {
        return soldDate;
    }

    public void setSoldDate(Date soldDate) {
        if (sold) {
            this.soldDate = soldDate;
        }

    }
}
