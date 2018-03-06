package com.ogma.dealshaiapp.model;

import java.io.Serializable;

/**
 * Created by AndroidDev on 01-11-2017.
 */

public class CategoryDetails implements Serializable {

    private String catId;
    private String catName;
    private String imgUrl;
    private int isActive = 0;

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catid) {
        this.catId = catid;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }
}
