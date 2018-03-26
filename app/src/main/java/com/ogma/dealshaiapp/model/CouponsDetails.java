package com.ogma.dealshaiapp.model;

import java.io.Serializable;

/**
 * Created by AndroidDev on 30-10-2017.
 */

public class CouponsDetails implements Serializable {
    private String merchantId;
    private String couponId;
    private String couponTitle;
    private String couponDescription;
    private String couponValidForPerson;
    private String couponValidOn;
    private int newPrice;
    private int originalPrice;
    private int quantity = 0;
    private int isSelected = 0;
    private int isSinglePurchase;

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCouponTitle() {
        return couponTitle;
    }

    public void setCouponTitle(String couponTitle) {
        this.couponTitle = couponTitle;
    }

    public String getCouponDescription() {
        return couponDescription;
    }

    public void setCouponDescription(String couponDescription) {
        this.couponDescription = couponDescription;
    }

    public String getCouponValidForPerson() {
        return couponValidForPerson;
    }

    public void setCouponValidForPerson(String couponValidForPerson) {
        this.couponValidForPerson = couponValidForPerson;
    }

    public String getCouponValidOn() {
        return couponValidOn;
    }

    public void setCouponValidOn(String couponValidOn) {
        this.couponValidOn = couponValidOn;
    }

    public int getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(int newPrice) {
        this.newPrice = newPrice;
    }

    public int getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(int originalPrice) {
        this.originalPrice = originalPrice;
    }

    public int getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(int isSelected) {
        this.isSelected = isSelected;
    }

    public int getIsSinglePurchase() {
        return isSinglePurchase;
    }

    public void setIsSinglePurchase(int isSinglePurchase) {
        this.isSinglePurchase = isSinglePurchase;
    }
}