package com.ogma.dealshaiapp.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by AndroidDev on 09-11-2017.
 */

public class FilterOptions implements Serializable {

    private String catId;
    private String catName;
    private ArrayList<SubCategory> subCatArrLst;

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public ArrayList<SubCategory> getSubCatArrLst() {
        return subCatArrLst;
    }

    public void setSubCatArrLst(ArrayList<SubCategory> subCatArrLst) {
        this.subCatArrLst = subCatArrLst;
    }
}
