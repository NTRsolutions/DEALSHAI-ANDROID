package com.ogma.dealshaiapp.model;

/**
 * Created by AndroidDev on 23-01-2018.
 */

public class PaytmParameters {
    private String mid;
    private String order_id;
    private String cust_id;
    private String industry_type_id;
    private String channel_id;
    private String txn_amount;
    private String website;
    private String callback_url;
    private String email;
    private String mobile_no;
    private String checksumhash;

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getCust_id() {
        return cust_id;
    }

    public void setCust_id(String cust_id) {
        this.cust_id = cust_id;
    }

    public String getIndustry_type_id() {
        return industry_type_id;
    }

    public void setIndustry_type_id(String industry_type_id) {
        this.industry_type_id = industry_type_id;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getTxn_amount() {
        return txn_amount;
    }

    public void setTxn_amount(String txn_amount) {
        this.txn_amount = txn_amount;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getCallback_url() {
        return callback_url;
    }

    public void setCallback_url(String callback_url) {
        this.callback_url = callback_url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getChecksumhash() {
        return checksumhash;
    }

    public void setChecksumhash(String checksumhash) {
        this.checksumhash = checksumhash;
    }
}
