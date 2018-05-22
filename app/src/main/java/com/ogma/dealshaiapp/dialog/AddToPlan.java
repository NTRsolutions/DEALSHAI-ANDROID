package com.ogma.dealshaiapp.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.fragment.plankarle.FragmentCategoryList;
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

public class AddToPlan extends Dialog implements View.OnClickListener {

    private QuickViewItemsAdapter quickViewItemsAdapter;
    private JSONArray coupons;
    private int merchantId;
    private String merchantName;
    private String merchantDescription;
    private Activity activity;
    private ArrayList<CouponsDetails> arrayList;
    private int totalAmount;
    private RecyclerView recycler_view;
    private FrameLayout error_screen;
    private String userId;
    private boolean flagCanBuy;
    private RelativeLayout parentPanel;


    public AddToPlan(@NonNull Activity activity, int merchantId) {
        super(activity);
        this.merchantId = merchantId;
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_to_plan);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Session session = new Session(getContext());
        HashMap<String, String> user = session.getUserDetails();
        userId = user.get(Session.KEY_ID);

        NetworkConnection connection = new NetworkConnection(getContext());
        if (connection.isNetworkConnected()) {
            getCoupons();
        } else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "Network connection not available", Toast.LENGTH_SHORT).show();
                }
            });
        }

        parentPanel = findViewById(R.id.parentPanel);
        recycler_view = findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.setScrollbarFadingEnabled(false);
        coupons = new JSONArray();

        error_screen = findViewById(R.id.error_screen);
        TextView tv_buy_now = findViewById(R.id.tv_add_to_plan);
        ImageView iv_esc = findViewById(R.id.iv_esc);
        tv_buy_now.setOnClickListener(this);
        iv_esc.setOnClickListener(this);
    }

    private void getCoupons() {
        WebServiceHandler webServiceHandler = new WebServiceHandler(getContext());
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject;
                int is_err;
                try {
                    jsonObject = new JSONObject(response);
                    is_err = jsonObject.getInt("err");
                    if (is_err == 0) {
                        try {
                            merchantId = Integer.parseInt(jsonObject.getString("merchant_id"));
                            merchantName = jsonObject.getString("place_name");
                            merchantDescription = jsonObject.getString("description");
                            coupons = jsonObject.getJSONArray("cupons");

                            arrayList = new ArrayList<>();
                            for (int i = 0; i < coupons.length(); i++) {
                                CouponsDetails couponsDetails = new CouponsDetails();
                                JSONObject object = coupons.getJSONObject(i);
                                couponsDetails.setCouponId(object.getString("id"));
                                couponsDetails.setMerchantId(String.valueOf(merchantId));
                                couponsDetails.setCouponTitle(object.getString("title"));
                                couponsDetails.setCouponDescription(object.getString("description"));
                                couponsDetails.setCouponValidOn(object.getString("valid_on"));
                                couponsDetails.setCouponValidForPerson(object.getString("valid_for_person"));
                                couponsDetails.setNewPrice(Integer.parseInt(object.getString("our_price")));
                                couponsDetails.setOriginalPrice(Integer.parseInt(object.getString("price")));
                                couponsDetails.setIsSinglePurchase(object.getInt("is_single_purchase"));
                                arrayList.add(couponsDetails);
                            }

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    error_screen.setVisibility(View.GONE);
                                    quickViewItemsAdapter = new QuickViewItemsAdapter(getContext(), coupons, arrayList);
                                    recycler_view.setAdapter(quickViewItemsAdapter);
                                    quickViewItemsAdapter.notifyDataSetChanged();
                                }
                            });
                        } catch (final JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                error_screen.setVisibility(View.VISIBLE);
                                Toast.makeText(getContext(), "No coupons are available for this merchant", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Server Response Error", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        };
        webServiceHandler.getCoupons(merchantId, userId);
    }

    @Override
    public void onClick(final View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_add_to_plan:
                quickViewItemsAdapter.getCouponsDetails();
                break;
            case R.id.iv_esc:
                dismiss();
                break;
        }
    }

    private class QuickViewItemsAdapter extends RecyclerView.Adapter<QuickViewItemsAdapter.ViewHolder> {
        private JSONArray cupons;
        private Context context;
        private ArrayList<CouponsDetails> arrayList;

        QuickViewItemsAdapter(Context context, JSONArray cupons, ArrayList<CouponsDetails> arrayList) {
            this.cupons = cupons;
            this.context = context;
            this.arrayList = arrayList;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private TextView coupon_no;
            private TextView coupon_title;
            private TextView coupon_details;
            private TextView coupon_old_price;
            private TextView coupon_new_preice;
            private TextView coupon_valid_for;
            private TextView coupon_valid_on;
            private TextView coupon_quantity;
            private ImageView iv_plus;
            private ImageView iv_minus;

            ViewHolder(View itemView) {
                super(itemView);

                coupon_no = itemView.findViewById(R.id.tv_item_num);
                coupon_title = itemView.findViewById(R.id.tv_item_title);
                coupon_details = itemView.findViewById(R.id.tv_item_details);
                coupon_old_price = itemView.findViewById(R.id.tv_old_price);
                coupon_new_preice = itemView.findViewById(R.id.tv_new_price);
                coupon_valid_for = itemView.findViewById(R.id.tv_valid_for);
                coupon_valid_on = itemView.findViewById(R.id.tv_valid_on);
                coupon_quantity = itemView.findViewById(R.id.tv_quantity);
                iv_plus = itemView.findViewById(R.id.iv_plus);
                iv_minus = itemView.findViewById(R.id.iv_minus);

                iv_plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CouponsDetails couponDetails = arrayList.get(getAdapterPosition());
                        int quantity = couponDetails.getQuantity();
                        if (couponDetails.getIsSinglePurchase() == 1) {
                            if (couponDetails.getQuantity() < 1) {
                                quantity++;
                                int amount = couponDetails.getNewPrice();
                                setTotalAmount(amount);
                                couponDetails.setQuantity(quantity);
                                if (couponDetails.getQuantity() > 0) {
                                    couponDetails.setIsSelected(1);
                                }
                                FragmentCategoryList.mainArrayList = arrayList;
                                notifyDataSetChanged();
                            } else
                                Toast.makeText(context, R.string.not_more_than_one, Toast.LENGTH_SHORT).show();
                        } else {
                            quantity++;
                            int amount = couponDetails.getNewPrice();
                            setTotalAmount(amount);
                            couponDetails.setQuantity(quantity);
                            if (couponDetails.getQuantity() > 0) {
                                couponDetails.setIsSelected(1);
                            }
                            notifyDataSetChanged();
                        }
                    }
                });

                iv_minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CouponsDetails couponDetails = arrayList.get(getAdapterPosition());
                        int quantity = couponDetails.getQuantity();
                        if (quantity > 0) {
                            quantity--;
                            int amount = couponDetails.getNewPrice();
                            setTotalAmount(-amount);
                            couponDetails.setQuantity(quantity);
                            if (couponDetails.getQuantity() == 0) {
                                couponDetails.setIsSelected(0);
                            }
                            notifyDataSetChanged();
                        }
                    }
                });

            }
        }

        public QuickViewItemsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quick_view_item, parent, false);
            return new QuickViewItemsAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final QuickViewItemsAdapter.ViewHolder holder, int position) {

            String coupon_id = "";
            String title = "";
            String description = "";
            String valid_for = "";
            String valid_on = "";
            String price = "";
            String offerPrice = "";
            int quantity = 0;

            CouponsDetails couponsDetails = arrayList.get(position);

            title = couponsDetails.getCouponTitle();
            description = couponsDetails.getCouponDescription();
            valid_for = couponsDetails.getCouponValidForPerson();
            valid_on = couponsDetails.getCouponValidOn();
            price = String.valueOf(couponsDetails.getOriginalPrice());
            offerPrice = String.valueOf(couponsDetails.getNewPrice());
            quantity = Integer.parseInt(String.valueOf(couponsDetails.getQuantity()));

            holder.coupon_no.setText(String.valueOf(position + 1));
            holder.coupon_title.setText(title);
            holder.coupon_details.setText(description);
            holder.coupon_old_price.setText("₹ " + price);
            holder.coupon_new_preice.setText("₹ " + offerPrice);
            holder.coupon_valid_for.setText(valid_for);
            holder.coupon_valid_on.setText(valid_on);
            holder.coupon_quantity.setText(String.valueOf(quantity));
            holder.coupon_old_price.setPaintFlags(holder.coupon_old_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        @Override
        public int getItemCount() {
            return cupons.length();
        }

        void getCouponsDetails() {
            for (int i = 0; i < arrayList.size(); i++) {
                CouponsDetails couponsDetails = arrayList.get(i);
                if (couponsDetails.getIsSelected() == 1) {
                    FragmentCategoryList.mainArrayList.add(couponsDetails);
                    flagCanBuy = true;
                }
            }
            if (!flagCanBuy) {
                Toast.makeText(activity, "Your cart is empty. Please add items to buy.", Toast.LENGTH_LONG).show();
            } else {
                dismiss();
            }
        }
    }

    private void setTotalAmount(int amount) {
        if (FragmentCategoryList.totalAmount >= 0) {
            FragmentCategoryList.totalAmount = FragmentCategoryList.totalAmount + amount;
        }
    }
}
