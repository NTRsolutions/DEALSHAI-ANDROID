package com.ogma.dealshaiapp.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.model.CouponsDetails;
import com.ogma.dealshaiapp.model.Vouchers;
import com.ogma.dealshaiapp.network.NetworkConnection;
import com.ogma.dealshaiapp.network.WebServiceHandler;
import com.ogma.dealshaiapp.network.WebServiceListener;
import com.ogma.dealshaiapp.utility.PrePaymentClass;
import com.ogma.dealshaiapp.utility.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CheckOutActivity extends AppCompatActivity implements View.OnClickListener {

    private Session session;
    private String userId;
    private boolean flag;
    private ArrayList<CouponsDetails> couponsDetails;
    private String payableAmount;
    private String totalAmount;
    private TextView tv_discount_amount;
    private TextView tv_payable_amount;
    private TextView tv_voucher_message;
    private EditText et_promo_code;
    private RelativeLayout parentPanel;
    private RelativeLayout rl_discount;
    private TextView tv_vouchers;
    private VouchersListAdapter adapter;
    private ArrayList<Vouchers> vouchersArrayList = new ArrayList<>();
    private String discountAmount;
    private String promoId;
    private String promoAmount;
    private String voucherId;
    private String promoType;
    private String voucherType;
    private String promoCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setTitle("CHECKOUT");
            // toolbar.setBackgroundColor();
        }
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        session = new Session(CheckOutActivity.this);
        HashMap<String, String> user = session.getUserDetails();
        userId = user.get(Session.KEY_ID);

        couponsDetails = new ArrayList<>();
        couponsDetails = (ArrayList<CouponsDetails>) getIntent().getSerializableExtra("couponList");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            totalAmount = bundle.getString("totalAmount");
            payableAmount = totalAmount;
        }

        parentPanel = findViewById(R.id.parentPanel);
        rl_discount = findViewById(R.id.rl_discount);
        TextView tv_total_amount = findViewById(R.id.tv_total_amount);
        tv_discount_amount = findViewById(R.id.tv_discount_amount);
        tv_payable_amount = findViewById(R.id.tv_payable_amount);
        tv_voucher_message = findViewById(R.id.tv_voucher_message);
        et_promo_code = findViewById(R.id.et_promo_code);
        tv_vouchers = findViewById(R.id.tv_vouchers);

        findViewById(R.id.tv_btn_apply).setOnClickListener(this);
        findViewById(R.id.tv_btn_proceed).setOnClickListener(this);

        tv_total_amount.setText("₹ " + totalAmount);
        tv_payable_amount.setText("₹ " + payableAmount);

        RecyclerView recycler_view = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CheckOutActivity.this);
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        adapter = new VouchersListAdapter(CheckOutActivity.this, CheckOutActivity.this, vouchersArrayList);
        recycler_view.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        rl_discount.setVisibility(View.GONE);
        tv_voucher_message.setVisibility(View.GONE);

        NetworkConnection connection = new NetworkConnection(CheckOutActivity.this);
        if (connection.isNetworkConnected()) {
            getVoucherItemList(userId);
        } else
            Snackbar.make(parentPanel, "No network connection.", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_btn_apply:
                NetworkConnection connection = new NetworkConnection(CheckOutActivity.this);
                promoCode = et_promo_code.getText().toString().trim();
                if (connection.isNetworkConnected()) {
                    if (!promoCode.equals(""))
                        applyCouponCode(userId, promoCode, totalAmount);
                }
                break;
            case R.id.tv_btn_proceed:
                PrePaymentClass prePaymentClass = new PrePaymentClass(CheckOutActivity.this, CheckOutActivity.this, couponsDetails, payableAmount, userId, voucherId, voucherType, promoId, promoType, totalAmount);
                prePaymentClass.startTransaction();
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

    private void applyCouponCode(String userId, final String promoCode, String totalAmount) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(CheckOutActivity.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    String err = String.valueOf(jsonObject.getInt("err"));
                    final String msg = jsonObject.getString("msg");
                    if (err.equals("0")) {
                        promoId = jsonObject.getString("id");
                        promoAmount = jsonObject.getString("amount");
                        promoType = jsonObject.getString("type");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_voucher_message.setText(msg);
                                tv_voucher_message.setVisibility(View.VISIBLE);
                            }
                        });
                    } else {
                        Snackbar.make(parentPanel, msg, Snackbar.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(parentPanel, "Response error!", Snackbar.LENGTH_LONG).show();
                }
            }
        };
        webServiceHandler.applyPromoCode(userId, promoCode, totalAmount);
    }

    private void getVoucherItemList(String userId) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(CheckOutActivity.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    String err = String.valueOf(jsonObject.getInt("err"));
                    if (err.equals("0")) {
                        JSONArray vouchers = jsonObject.getJSONArray("vouchers");
                        vouchersArrayList.clear();
                        for (int i = 0; i < vouchers.length(); i++) {
                            Vouchers obj = new Vouchers();
                            JSONObject object = vouchers.getJSONObject(i);
                            obj.setVoucherId(object.getString("id"));
                            obj.setVoucherAmount(object.getString("amount"));
                            obj.setVoucherType(object.getString("type"));
                            obj.setVoucherDate(object.getString("expire_date"));
                            obj.setVoucherStaus(object.getString("status"));
                            vouchersArrayList.add(obj);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    } else {
//                        String msg = jsonObject.getString("msg");
//                        Snackbar.make(parentPanel, msg, Snackbar.LENGTH_LONG).show();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_vouchers.setVisibility(View.GONE);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(parentPanel, "Response error!", Snackbar.LENGTH_LONG).show();
                }
            }
        };
        webServiceHandler.getVoucherList(userId);
    }

    private void applyVoucher(String voucherId, String voucherType, final String totalAmount) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(CheckOutActivity.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    String err = String.valueOf(jsonObject.getInt("err"));
                    String msg = jsonObject.getString("msg");
                    if (err.equals("0")) {
                        discountAmount = jsonObject.getString("amount");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rl_discount.setVisibility(View.VISIBLE);
                                tv_discount_amount.setText("₹ " + discountAmount);
                                payableAmount = String.valueOf(Integer.parseInt(totalAmount) - Integer.parseInt(discountAmount));
                                tv_payable_amount.setText("₹ " + payableAmount);
                            }
                        });
                    } else {
//                        Snackbar.make(parentPanel, msg, Snackbar.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(parentPanel, "Response error!", Snackbar.LENGTH_LONG).show();
                }
            }
        };
        webServiceHandler.applyVoucher(userId, voucherId, voucherType, totalAmount);
    }

    private class VouchersListAdapter extends RecyclerView.Adapter<VouchersListAdapter.ViewHolder> {
        private Activity activity;
        private Context context;
        private ArrayList<Vouchers> vouchersArrayList;

        VouchersListAdapter(Activity activity, Context context, ArrayList<Vouchers> vouchersArrayList) {
            this.activity = activity;
            this.context = context;
            this.vouchersArrayList = vouchersArrayList;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView tv_referral_code;
            private TextView tv_voucher_amount;
            private TextView tv_voucher_date;
            private ImageView iv_voucher_type;
            private TextView tv_voucher_status;

            ViewHolder(View itemView) {
                super(itemView);
                tv_referral_code = itemView.findViewById(R.id.tv_referral_code);
                tv_voucher_amount = itemView.findViewById(R.id.tv_voucher_amount);
                tv_voucher_date = itemView.findViewById(R.id.tv_voucher_date);
                iv_voucher_type = itemView.findViewById(R.id.iv_voucher_type);
                tv_voucher_status = itemView.findViewById(R.id.tv_voucher_status);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Vouchers vouchers = new Vouchers();
                        vouchers = vouchersArrayList.get(getAdapterPosition());
                        voucherId = vouchers.getVoucherId();
                        voucherType = vouchers.getVoucherType();
                        applyVoucher(voucherId, voucherType, totalAmount);
                    }
                });
            }
        }

        public VouchersListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.voucher_single_item_view, parent, false);
            return new VouchersListAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(VouchersListAdapter.ViewHolder holder, int position) {

            Vouchers vouchers = vouchersArrayList.get(position);
            holder.tv_referral_code.setText(vouchers.getVoucherType());
            holder.tv_voucher_amount.setText("₹ " + vouchers.getVoucherAmount());
            holder.tv_voucher_date.setText("Expire on: " + vouchers.getVoucherDate());
            switch (vouchers.getVoucherType()) {
                case "U":
                    holder.iv_voucher_type.setImageResource(R.drawable.coupon_blue);
                    break;
                case "P":
                    holder.iv_voucher_type.setImageResource(R.drawable.coupon_brown);
                    break;
                case "RE":
                    holder.iv_voucher_type.setImageResource(R.drawable.coupon_green);
                    break;
                case "RT":
                    holder.iv_voucher_type.setImageResource(R.drawable.coupon_redviolet);
                    break;
                default:
                    break;
            }
            holder.tv_voucher_status.setText(" " + vouchers.getVoucherStaus());
            switch (vouchers.getVoucherStaus()) {
                case "active":
                    holder.tv_voucher_status.setTextColor(getResources().getColor(R.color.green_dot_color));
                    holder.tv_voucher_status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.green_dot, 0, 0, 0);
                    break;
                case "Expired":
                    holder.tv_voucher_status.setTextColor(getResources().getColor(R.color.red_dot_color));
                    holder.tv_voucher_status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.red_dot, 0, 0, 0);
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return vouchersArrayList.size();
        }
    }

}