package com.ogma.dealshaiapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.model.CouponsDetails;
import com.ogma.dealshaiapp.network.NetworkConnection;
import com.ogma.dealshaiapp.network.WebServiceHandler;
import com.ogma.dealshaiapp.network.WebServiceListener;
import com.ogma.dealshaiapp.utility.Session;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {
    private String mid = "";
    private String order_id = "";
    private String cust_id = "";
    private String industry_type_id = "";
    private String channel_id = "";
    private String txn_amount = "";
    private String website = "";
    //    private String callback_url = "http://www.ogmaconceptions.com/demo/dealshai/api/complete_payment";
    private String callback_url = "";
    private String email = "";
    private String mobile_no = "";
    private String checksumhash = "";

    private String totalAmount;
    private JSONArray deals;
    private String userId;
    private TextView tv_order_id;
    private TextView tv_transaction_amount;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        tv_order_id = findViewById(R.id.tv_order_id);
        tv_transaction_amount = findViewById(R.id.tv_transaction_amount);

        Session session = new Session(PaymentActivity.this);
        HashMap<String, String> user = session.getUserDetails();
        userId = user.get(Session.KEY_ID);

        ArrayList<CouponsDetails> couponsDetails = (ArrayList<CouponsDetails>) getIntent().getSerializableExtra("couponsDetails");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            totalAmount = bundle.getString("totalAmount");
        }

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

        NetworkConnection connection = new NetworkConnection(PaymentActivity.this);
        if (connection.isNetworkConnected()) {
            getOrderDetails();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public void onStartTransaction(View view) {
        PaytmPGService Service = PaytmPGService.getStagingService();

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

        Service.startPaymentTransaction(this, true, true, new PaytmPaymentTransactionCallback() {
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

                if (STATUS.equals("TXN_SUCCESS")) {
                    Intent intent = new Intent(PaymentActivity.this, OrderStatusActivity.class)
//                            .putExtra("bundle", bundle);
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
                            .putExtra("RESPMSG", RESPMSG);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void networkNotAvailable() {
            }

            @Override
            public void clientAuthenticationFailed(String s) {
            }

            @Override
            public void someUIErrorOccurred(String s) {

            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {

            }

            @Override
            public void onBackPressedCancelTransaction() {
                Toast.makeText(PaymentActivity.this, "Your payment is canceled.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                Toast.makeText(PaymentActivity.this, "Your payment is canceled.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getOrderDetails() {
        WebServiceHandler webServiceHandler = new WebServiceHandler(PaymentActivity.this);
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
                            mobile_no = paramList.getString("MOBILE_NO");
                            email = paramList.getString("EMAIL");
                            mid = paramList.getString("MID");
                            cust_id = paramList.getString("CUST_ID");
                            industry_type_id = paramList.getString("INDUSTRY_TYPE_ID");
                            channel_id = paramList.getString("CHANNEL_ID");
                            txn_amount = paramList.getString("TXN_AMOUNT");
                            website = paramList.getString("WEBSITE");
                            callback_url = paramList.getString("CALLBACK_URL");
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_order_id.setText("" + order_id);
                                tv_transaction_amount.setText("" + txn_amount);
                            }
                        });
                    } else {
                        Toast.makeText(PaymentActivity.this, "" + message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        webServiceHandler.getOrderDetails(userId, totalAmount, deals);
    }
}
