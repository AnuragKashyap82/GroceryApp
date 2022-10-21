package com.anurag.emart.models;

public class ModelPromotion {

    String id, timestamp, description, promoCode, promoPrice, minimumOrderPrice, expiryDate;

    public ModelPromotion() {
    }

    public ModelPromotion(String id, String timestamp, String description, String promoCode, String promoPrice, String minimumOrderPrice, String expiryDate) {
        this.id = id;
        this.timestamp = timestamp;
        this.description = description;
        this.promoCode = promoCode;
        this.promoPrice = promoPrice;
        this.minimumOrderPrice = minimumOrderPrice;
        this.expiryDate = expiryDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getPromoPrice() {
        return promoPrice;
    }

    public void setPromoPrice(String promoPrice) {
        this.promoPrice = promoPrice;
    }

    public String getMinimumOrderPrice() {
        return minimumOrderPrice;
    }

    public void setMinimumOrderPrice(String minimumOrderPrice) {
        this.minimumOrderPrice = minimumOrderPrice;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}