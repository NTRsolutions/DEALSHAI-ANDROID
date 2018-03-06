package com.ogma.dealshaiapp.model;

import java.io.Serializable;

/**
 * Created by AndroidDev on 09-11-2017.
 */

public class SubCategory implements Serializable {

    private String subCatId;
    private String subCatName;

    public String getSubCatId() {
        return subCatId;
    }

    public void setSubCatId(String subCatId) {
        this.subCatId = subCatId;
    }

    public String getSubCatName() {
        return subCatName;
    }

    public void setSubCatName(String subCatName) {
        this.subCatName = subCatName;
    }
}
