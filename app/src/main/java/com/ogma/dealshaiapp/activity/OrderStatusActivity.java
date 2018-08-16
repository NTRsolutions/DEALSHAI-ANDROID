package com.ogma.dealshaiapp.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.model.CouponsDetails;
import com.ogma.dealshaiapp.network.NetworkConnection;
import com.ogma.dealshaiapp.network.WebServiceHandler;
import com.ogma.dealshaiapp.network.WebServiceListener;
import com.ogma.dealshaiapp.utility.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class OrderStatusActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_view_purchase;
    private ArrayList<CouponsDetails> couponsDetails;
    private String userId;
    private String totalAmount;
    private Bundle paymentResponse;
    private String merchantId;
    private JSONArray deals;
    private TextView tv_msg;
    private TextView tv_msg_desc;
    private ImageView imageView;

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
    private ImageView back;

    private String voucherId;
    private String voucherType;
    private String promoId;
    private String promoType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // toolbar.setBackgroundColor();
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Session session = new Session(OrderStatusActivity.this);

        HashMap<String, String> user = session.getUserDetails();
        userId = user.get(Session.KEY_ID);

        btn_view_purchase = findViewById(R.id.btn_view_purchase);
        tv_msg = findViewById(R.id.tv_msg);
        tv_msg_desc = findViewById(R.id.tv_msg_desc);
        imageView = findViewById(R.id.imageView);
        back=findViewById(R.id.back_voucher);

//        couponsDetails = (ArrayList<CouponsDetails>) getIntent().getSerializableExtra("couponsDetails");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            ORDERID = bundle.getString("ORDERID");
            voucherId = bundle.getString("VOUCHERID");
            voucherType = bundle.getString("VOUCHERTYPE");
            promoId = bundle.getString("PROMOID");
            promoType = bundle.getString("PROMOTYPE");
            totalAmount = bundle.getString("TOTALAMOUNT");
            TXNAMOUNT = bundle.getString("TXNAMOUNT");

            if (Float.parseFloat(TXNAMOUNT) > 0.0) {
                STATUS = bundle.getString("STATUS");
                CHECKSUMHASH = bundle.getString("CHECKSUMHASH");
                BANKNAME = bundle.getString("BANKNAME");
                TXNDATE = bundle.getString("TXNDATE");
                MID = bundle.getString("MID");
                TXNID = bundle.getString("TXNID");
                RESPCODE = bundle.getString("RESPCODE");
                PAYMENTMODE = bundle.getString("PAYMENTMODE");
                BANKTXNID = bundle.getString("BANKTXNID");
                CURRENCY = bundle.getString("CURRENCY");
                GATEWAYNAME = bundle.getString("GATEWAYNAME");
                RESPMSG = bundle.getString("RESPMSG");
            }
        }

        NetworkConnection connection = new NetworkConnection(OrderStatusActivity.this);
        if (connection.isNetworkConnected()) {
            if (Float.parseFloat(TXNAMOUNT) > 0.0) {
                completePaytmPayment();
            } else {
                completePayment();
            }
        }

        btn_view_purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderStatusActivity.this, MyPurchases.class));
                OrderStatusActivity.this.finish();
            }
        });

        back.setOnClickListener(this);
    }

    private void completePayment() {
        WebServiceHandler webServiceHandler = new WebServiceHandler(OrderStatusActivity.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    final String isError = jsonObject.getString("is_err");
                    final String message = jsonObject.getString("msg");
                    final String desc = jsonObject.getString("desc");
                    OrderStatusActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_msg.setText(message);
                            tv_msg_desc.setText(desc);
                            if (isError.equals("0")) {
                                imageView.setImageResource(R.drawable.ic_order_successful);
                            } else {
                                imageView.setImageResource(R.drawable.ic_order_failed);
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        webServiceHandler.completePayment(userId, ORDERID, TXNAMOUNT, voucherId, voucherType, promoId, promoType, totalAmount);
    }

    private void completePaytmPayment() {
        WebServiceHandler webServiceHandler = new WebServiceHandler(OrderStatusActivity.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    final String isError = jsonObject.getString("is_err");
                    final String message = jsonObject.getString("msg");
                    final String desc = jsonObject.getString("desc");
                    OrderStatusActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_msg.setText(message);
                            tv_msg_desc.setText(desc);
                            if (isError.equals("0")) {
                                imageView.setImageResource(R.drawable.ic_order_successful);
                            } else {
                                imageView.setImageResource(R.drawable.ic_order_failed);
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        webServiceHandler.completePaytmPayment(userId, STATUS, CHECKSUMHASH, BANKNAME, ORDERID, TXNAMOUNT, TXNDATE, MID, TXNID, RESPCODE, PAYMENTMODE, BANKTXNID, CURRENCY, GATEWAYNAME, RESPMSG, voucherId, voucherType, promoId, promoType, totalAmount);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(OrderStatusActivity.this, IndexActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(OrderStatusActivity.this, IndexActivity.class));
                OrderStatusActivity.this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        onBackPressed();
    }
}
