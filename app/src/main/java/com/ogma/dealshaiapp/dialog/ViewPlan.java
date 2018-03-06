package com.ogma.dealshaiapp.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.activity.CheckOutActivity;
import com.ogma.dealshaiapp.adapter.CouponsViewAdapter;
import com.ogma.dealshaiapp.model.CouponsDetails;
import com.ogma.dealshaiapp.model.OrderedItems;
import com.ogma.dealshaiapp.network.NetworkConnection;
import com.ogma.dealshaiapp.network.WebServiceHandler;
import com.ogma.dealshaiapp.network.WebServiceListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewPlan extends Dialog implements View.OnClickListener {

    private AppCompatActivity activity;
    private ArrayList<CouponsDetails> mainArrayList;
    private ArrayList<OrderedItems> arrayList;
    private RecyclerView recycler_view;
    private CouponsViewAdapter couponsViewAdapter;
    private JSONArray jsonArray;
    private ImageView iv_esc;
    private TextView tv_buy_now;
    private int totalAmount;
    private RelativeLayout parentPanel;
    private String userId;
    private boolean flag;

    public ViewPlan(@NonNull AppCompatActivity activity, ArrayList<CouponsDetails> mainArrayList, int totalAmount, String userId) {
        super(activity);
        this.activity = activity;
        this.mainArrayList = mainArrayList;
        this.totalAmount = totalAmount;
        this.userId = userId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_view_plan);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        arrayList = new ArrayList<>();

        if (mainArrayList.size() > 0) {
            jsonArray = new JSONArray();
            for (int i = 0; i < mainArrayList.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                CouponsDetails couponsDetails = mainArrayList.get(i);
                String couponId = couponsDetails.getCouponId();
                String qty = String.valueOf(couponsDetails.getQuantity());
                try {
                    jsonObject.put("coupon_id", couponId);
                    jsonObject.put("qty", qty);
                    jsonArray.put(i, jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else
            Toast.makeText(getContext(), "There is no item in your plan.", Toast.LENGTH_SHORT).show();

        NetworkConnection connection = new NetworkConnection(getContext());
        if (connection.isNetworkConnected()) {
            getData(jsonArray);
        } else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "Network connection not available", Toast.LENGTH_SHORT).show();
//                    Snackbar.make(,"Your cart is empty. Please add items to buy.",Snackbar.LENGTH_LONG).show();
                }
            });
        }

        parentPanel = findViewById(R.id.parentPanel);
        recycler_view = findViewById(R.id.recycler_view);
        iv_esc = findViewById(R.id.iv_esc);
        tv_buy_now = findViewById(R.id.tv_buy_now);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.setScrollbarFadingEnabled(false);
        couponsViewAdapter = new CouponsViewAdapter(getContext(), arrayList);
        recycler_view.setAdapter(couponsViewAdapter);

        tv_buy_now.setOnClickListener(this);
        iv_esc.setOnClickListener(this);
    }

    private void getData(JSONArray jsonArray) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(getContext());
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                JSONObject main = null;
                try {
                    main = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONArray coupons = null;
                try {
                    if (main != null) {
                        coupons = main.getJSONArray("coupon");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (coupons != null) {
                    for (int i = 0; i < coupons.length(); i++) {
                        OrderedItems orderedItems = new OrderedItems();
                        try {
                            JSONObject deals = coupons.getJSONObject(i);
//                            orderedItems.setId(deals.getString("coupon_id"));
                            orderedItems.setMerchantName(deals.getString("merchant_name"));
//                            orderedItems.setCouponCode(deals.getString("cupon_code"));
                            orderedItems.setCouponTitle(deals.getString("coupon_title"));
                            orderedItems.setPrice(deals.getString("price"));
                            orderedItems.setQuantity(deals.getString("qty"));
                            orderedItems.setTotal_price(deals.getString("total_price"));
//                            orderedItems.setQrImg(deals.getString("qr_img"));
                            orderedItems.setImg(deals.getString("img"));
//                            orderedItems.setRedeemed(deals.getString("reedemed"));
                            arrayList.add(orderedItems);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            couponsViewAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        };
        webServiceHandler.getViewPlanData(jsonArray);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_esc:
                dismiss();
                break;
            case R.id.tv_buy_now:
                if (totalAmount > 0) {
                    Intent intent = new Intent(activity, CheckOutActivity.class)
                            .putExtra("couponList", mainArrayList)
                            .putExtra("totalAmount", String.valueOf(totalAmount));
                    activity.startActivity(intent);
//                    PrePaymentClass prePaymentClass = new PrePaymentClass(activity, getContext(), mainArrayList, String.valueOf(totalAmount), userId);
//                    prePaymentClass.startTransaction();
                    dismiss();
                } else
                    Snackbar.make(parentPanel, "There is no coupon in your plan.", Snackbar.LENGTH_LONG).show();
                break;
        }
    }
}
