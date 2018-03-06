package com.ogma.dealshaiapp.model;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by AndroidDev on 15-11-2017.
 */

public class MerchantDetails implements Serializable {
    private String merchantId;
    private String title;
    private String area;
    private String discount;
    private String shortDescription;
    private String price;
    private String offerPrice;
    private String img;
    private String likes;

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOfferPrice() {
        return offerPrice;
    }

    public void setOfferPrice(String offerPrice) {
        this.offerPrice = offerPrice;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public static Comparator<MerchantDetails> sortLowToHigh = new Comparator<MerchantDetails>() {
        @Override
        public int compare(MerchantDetails o1, MerchantDetails o2) {
            int q1 = Integer.parseInt(o1.getOfferPrice());
            int q2 = Integer.parseInt(o2.getOfferPrice());
            return q1 - q2;
        }
    };

    public static Comparator<MerchantDetails> sortHighToLow = new Comparator<MerchantDetails>() {
        @Override
        public int compare(MerchantDetails o1, MerchantDetails o2) {
            int q2 = Integer.parseInt(o2.getOfferPrice());
            int q1 = Integer.parseInt(o1.getOfferPrice());
            return q2 - q1;
        }
    };
}