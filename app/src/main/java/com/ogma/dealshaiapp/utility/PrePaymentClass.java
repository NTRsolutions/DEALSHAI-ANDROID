package com.ogma.dealshaiapp.utility;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.ogma.dealshaiapp.activity.CheckOutActivity;
import com.ogma.dealshaiapp.activity.OrderStatusActivity;
import com.ogma.dealshaiapp.model.CouponsDetails;
import com.ogma.dealshaiapp.network.NetworkConnection;
import com.ogma.dealshaiapp.network.WebServiceHandler;
import com.ogma.dealshaiapp.network.WebServiceListener;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by AndroidDev on 08-02-2018.
 */

public class PrePaymentClass {
    private String mid = "";
    private String order_id = "";
    private String cust_id = "";
    private String industry_type_id = "";
    private String channel_id = "";
    private String txn_amount = "";
    private String website = "";
    private String callback_url = "";
    private String email = "";
    private String mobile_no = "";
    private String checksumhash = "";

    private String STATUS = "";
    private String CHECKSUMHASH = "";
    private String BANKNAME = "";
    private String ORDERID = "";
    private String TXNAMOUNT = "";
    private String TXNDATE = "";
    private String MID = "";
    private String TXNID = "";
    private String RESPCODE = "";
    private String PAYMENTMODE = "";
    private String BANKTXNID = "";
    private String CURRENCY = "";
    private String GATEWAYNAME = "";
    private String RESPMSG = "";

    private Context context;
    private AppCompatActivity activity;
    private ArrayList<CouponsDetails> couponsDetails;
    private String totalAmount;
    private String payableAmount;
    private JSONArray deals;
    private String userId;
    private String voucherId;
    private String voucherType;
    private String promoId;
    private String promoType;

    public PrePaymentClass(CheckOutActivity activity, CheckOutActivity context, ArrayList<CouponsDetails> couponsDetails, String payableAmount, String userId, String voucherId, String voucherType, String promoId, String promoType, String totalAmount) {
        this.context = context;
        this.couponsDetails = couponsDetails;
        this.payableAmount = payableAmount;
        this.userId = userId;
        this.activity = activity;
        this.voucherId = voucherId;
        this.voucherType = voucherType;
        this.promoId = promoId;
        this.promoType = promoType;
        this.totalAmount = totalAmount;
    }

    public void startTransaction() {
        deals = new JSONArray();
        for (int i = 0; i < couponsDetails.size(); i++) {
            CouponsDetails details = couponsDetails.get(i);
            String couponId = details.getCouponId();
            String merchantId = details.getMerchantId();
            String quantity = String.valueOf(details.getQuantity());
            Map<String, String> params = new HashMap<String, String>();
            params.put("merchant_id", merchantId);
            params.put("coupon_id", couponId);
            params.put("quantity", quantity);
            JSONObject jsonObject = new JSONObject(params);
            deals.put(jsonObject);
        }
        NetworkConnection connection = new NetworkConnection(context);
        if (connection.isNetworkConnected()) {
            getOrderDetails();
        }
    }

    private void getOrderDetails() {
        WebServiceHandler webServiceHandler = new WebServiceHandler(context);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    final String isError = jsonObject.getString("is_err");
                    final String message = jsonObject.getString("msg");

                    if (isError.equals("0")) {
                        order_id = jsonObject.getString("order_id");
                        checksumhash = jsonObject.getString("checkSum");
                        JSONObject paramList = jsonObject.getJSONObject("paramList");
                        if (paramList != null) {
                            txn_amount = paramList.getString("TXN_AMOUNT");
                            mobile_no = paramList.getString("MOBILE_NO");
                            email = paramList.getString("EMAIL");
                            mid = paramList.getString("MID");
                            cust_id = paramList.getString("CUST_ID");
                            industry_type_id = paramList.getString("INDUSTRY_TYPE_ID");
                            channel_id = paramList.getString("CHANNEL_ID");
                            website = paramList.getString("WEBSITE");
                            callback_url = paramList.getString("CALLBACK_URL");
                            if (Integer.parseInt(txn_amount) == 0) {
                                Intent intent = new Intent(context, OrderStatusActivity.class)
                                        .putExtra("ORDERID", order_id)
                                        .putExtra("TXNAMOUNT", txn_amount)
                                        .putExtra("VOUCHERID", voucherId)
                                        .putExtra("VOUCHERTYPE", voucherType)
                                        .putExtra("PROMOID", promoId)
                                        .putExtra("PROMOTYPE", promoType)
                                        .putExtra("TOTALAMOUNT", totalAmount);
                                context.startActivity(intent);
                            } else
                                onStartTransaction();
                        }
                    } else {
                        Toast.makeText(context, "" + message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        webServiceHandler.getOrderDetails(userId, payableAmount, deals);
    }

    public void onStartTransaction() {
        PaytmPGService Service = PaytmPGService.getProductionService();

        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("MID", mid);
        paramMap.put("ORDER_ID", order_id);
        paramMap.put("CUST_ID", cust_id);
        paramMap.put("INDUSTRY_TYPE_ID", industry_type_id);
        paramMap.put("CHANNEL_ID", channel_id);
        paramMap.put("TXN_AMOUNT", txn_amount);
        paramMap.put("WEBSITE", website);
        paramMap.put("CALLBACK_URL", callback_url);
        paramMap.put("EMAIL", email);
        paramMap.put("MOBILE_NO", mobile_no);
        paramMap.put("CHECKSUMHASH", checksumhash);

//        MERC_UNQ_REF: Value1_Value2_Value3)

        PaytmOrder Order = new PaytmOrder(paramMap);

        Service.initialize(Order, null);

        Service.startPaymentTransaction(activity, true, true, new PaytmPaymentTransactionCallback() {

            @Override
            public void onTransactionResponse(Bundle bundle) {
                STATUS = bundle.getString("STATUS");
                CHECKSUMHASH = bundle.getString("CHECKSUMHASH");
                BANKNAME = bundle.getString("BANKNAME");
                ORDERID = bundle.getString("ORDERID");
                TXNAMOUNT = bundle.getString("TXNAMOUNT");
                TXNDATE = bundle.getString("TXNDATE");
                MID = bundle.getString("MID");
                TXNID = bundle.getString("TXNID");
                RESPCODE = bundle.getString("RESPCODE");
                PAYMENTMODE = bundle.getString("PAYMENTMODE");
                BANKTXNID = bundle.getString("BANKTXNID");
                CURRENCY = bundle.getString("CURRENCY");
                GATEWAYNAME = bundle.getString("GATEWAYNAME");
                RESPMSG = bundle.getString("RESPMSG");

                Toast.makeText(context, "" + RESPMSG, Toast.LENGTH_LONG).show();

                if (STATUS.equals("TXN_SUCCESS")) {
                    Intent intent = new Intent(context, OrderStatusActivity.class)
                            .putExtra("STATUS", STATUS)
                            .putExtra("CHECKSUMHASH", CHECKSUMHASH)
                            .putExtra("BANKNAME", BANKNAME)
                            .putExtra("ORDERID", ORDERID)
                            .putExtra("TXNAMOUNT", TXNAMOUNT)
                            .putExtra("TXNDATE", TXNDATE)
                            .putExtra("MID", MID)
                            .putExtra("TXNID", TXNID)
                            .putExtra("RESPCODE", RESPCODE)
                            .putExtra("PAYMENTMODE", PAYMENTMODE)
                            .putExtra("BANKTXNID", BANKTXNID)
                            .putExtra("CURRENCY", CURRENCY)
                            .putExtra("GATEWAYNAME", GATEWAYNAME)
                            .putExtra("RESPMSG", RESPMSG)
                            .putExtra("VOUCHERID", voucherId)
                            .putExtra("VOUCHERTYPE", voucherType)
                            .putExtra("PROMOID", promoId)
                            .putExtra("PROMOTYPE", promoType)
                            .putExtra("TOTALAMOUNT", totalAmount);
                    context.startActivity(intent);
                }
            }

            @Override
            public void networkNotAvailable() {
                Toast.makeText(context, "Your payment is cancelled.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void clientAuthenticationFailed(String s) {
                Toast.makeText(context, "Client Authentication Failed", Toast.LENGTH_LONG).show();
            }

            @Override
            public void someUIErrorOccurred(String s) {
                Toast.makeText(context, "Some UI Error Occurred", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {
                Toast.makeText(context, "Error on Loading WebPage", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onBackPressedCancelTransaction() {
                Toast.makeText(context, "Transaction is Cancelled Manually", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                Toast.makeText(context, "Your Transaction is cancelled.", Toast.LENGTH_LONG).show();
            }
        });
    }

}
