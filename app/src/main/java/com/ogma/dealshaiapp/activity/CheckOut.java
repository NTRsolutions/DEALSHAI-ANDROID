package com.ogma.dealshaiapp.activity;

import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.model.CouponsDetails;
import com.ogma.dealshaiapp.utility.Session;

import java.util.ArrayList;
import java.util.HashMap;

public class CheckOut extends AppCompatActivity implements View.OnClickListener {

    private String merchant_id;
    private String totalAmount;
    private String merchantName;
    private String description;
    private TextView tv_total_amount;
    private ArrayList<CouponsDetails> couponsDetails;
    private Session session;
    private String userId;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setTitle("CHECKOUT");
            // toolbar.setBackgroundColor();
        }
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        session = new Session(CheckOut.this);

        HashMap<String, String> user = session.getUserDetails();
        userId = user.get(Session.KEY_ID);

        couponsDetails = new ArrayList<>();
        couponsDetails = (ArrayList<CouponsDetails>) getIntent().getSerializableExtra("couponList");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            merchant_id = bundle.getString("merchant_id");
            totalAmount = bundle.getString("totalAmount");
            merchantName = bundle.getString("merchantName");
            description = bundle.getString("description");
        }

        for (int i = 0; i < couponsDetails.size(); i++) {
            CouponsDetails couponDetails = couponsDetails.get(i);
            couponDetails.setMerchantId(merchant_id);
        }

        tv_total_amount = findViewById(R.id.tv_total_amount);
        TextView merchant_title = findViewById(R.id.merchant_title);
        TextView tv_details = findViewById(R.id.tv_details);
        TextView tv_btn_apply = findViewById(R.id.tv_btn_apply);
        TextView tv_btn_proceed = findViewById(R.id.tv_btn_proceed);

        tv_btn_apply.setOnClickListener(this);
        tv_btn_proceed.setOnClickListener(this);

        LinearLayout ll_purchase_items = findViewById(R.id.ll_purchase_items);
        LayoutInflater inflater = LayoutInflater.from(this);

        tv_total_amount.setText("₹ " + totalAmount);
        merchant_title.setText(merchantName);
        tv_details.setText(description);

        for (int i = 0; i < couponsDetails.size(); i++) {
            View view = inflater.inflate(R.layout.purchase_items, ll_purchase_items, false);

            TextView tv_item_title = view.findViewById(R.id.tv_item_title);
            TextView tv_new_price = view.findViewById(R.id.tv_new_price);
            TextView tv_old_price = view.findViewById(R.id.tv_old_price);
            TextView tv_quantity = view.findViewById(R.id.tv_quantity);

            String coupon_id = "";
            String title = "";
            int original_price = 0;
            int new_price = 0;
            int quantity = 0;

            CouponsDetails couponDetails = couponsDetails.get(i);
            couponDetails.setMerchantId(merchant_id);
            title = couponDetails.getCouponTitle();
            original_price = couponDetails.getOriginalPrice();
            new_price = couponDetails.getNewPrice();
            quantity = couponDetails.getQuantity();

            tv_item_title.setText(title);
            tv_new_price.setText("₹ " + String.valueOf(new_price));
            tv_old_price.setText("₹ " + String.valueOf(original_price));
            tv_old_price.setPaintFlags(tv_old_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tv_quantity.setText(String.valueOf(quantity));
            ll_purchase_items.addView(view);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_btn_apply:
                break;
            case R.id.tv_btn_proceed:
//                PrePaymentClass prePaymentClass = new PrePaymentClass(CheckOut.this, CheckOut.this, couponsDetails, totalAmount, userId);
//                prePaymentClass.startTransaction();
//                CheckOut.this.finish();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}