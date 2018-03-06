package com.ogma.dealshaiapp.model;

import java.util.ArrayList;

/**
 * Created by AndroidDev on 31-10-2017.
 */

public class PackageDetails {

    private String packageId;
    private String price;
    private String offerPrice;
    private ArrayList<SingleDealsDetails> dealsDetails;

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
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

    public ArrayList<SingleDealsDetails> getDealsDetails() {
        return dealsDetails;
    }

    public void setDealsDetails(ArrayList<SingleDealsDetails> dealsDetails) {
        this.dealsDetails = dealsDetails;
    }
}
