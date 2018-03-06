package com.ogma.dealshaiapp.model;

/**
 * Created by AndroidDev on 22-02-2018.
 */

public class Vouchers {
    private String voucherId;
    private String voucherType;
    private String voucherAmount;
    private String voucherDate;
    private String promoCode;
    private String voucherStaus;

    public String getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(String voucherId) {
        this.voucherId = voucherId;
    }

    public String getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(String voucherType) {
        this.voucherType = voucherType;
    }

    public String getVoucherAmount() {
        return voucherAmount;
    }

    public void setVoucherAmount(String voucherAmount) {
        this.voucherAmount = voucherAmount;
    }

    public String getVoucherDate() {
        return voucherDate;
    }

    public void setVoucherDate(String voucherDate) {
        this.voucherDate = voucherDate;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getVoucherStaus() {
        return voucherStaus;
    }

    public void setVoucherStaus(String voucherStaus) {
        this.voucherStaus = voucherStaus;
    }
}
