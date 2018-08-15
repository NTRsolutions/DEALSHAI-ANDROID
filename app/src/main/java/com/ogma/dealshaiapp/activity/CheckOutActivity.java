package com.ogma.dealshaiapp.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import java.util.List;

public class CheckOutActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private String userId;
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
    private String discountAmount = "0";
    private String promoId = "";
    private String promoType = "";
    private String promoCode = "";
    private String promoAmount = "";
    private String voucherId = "";
    private String voucherType = "";
    private String flag = "";
    private RelativeLayout rl_package_qty;
    private Spinner spinner_qty;
    private int quantity = 1;
    private TextView tv_total_amount;
    private int actualPrice = 0;
    private TextView tv_remove_vouchers;
    private TextView tv_reset_promo_code;
    private RelativeLayout rl_vouchers;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out_activity);
        Typeface font = Typeface.createFromAsset(getAssets(), "gothic.ttf");

        Toolbar toolbar = findViewById(R.id.toolbar);
        /**if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setTitle("Checkout");
        }*/
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Session session = new Session(CheckOutActivity.this);
        HashMap<String, String> user = session.getUserDetails();
        userId = user.get(Session.KEY_ID);

        List<String> quantity = new ArrayList<>();
        quantity.add("1");
        quantity.add("2");
        quantity.add("3");
        quantity.add("4");
        quantity.add("5");
        quantity.add("6");
        quantity.add("7");
        quantity.add("8");
        quantity.add("9");
        quantity.add("10");

        couponsDetails = new ArrayList<>();
        couponsDetails = (ArrayList<CouponsDetails>) getIntent().getSerializableExtra("couponList");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            totalAmount = bundle.getString("totalAmount");
            actualPrice = Integer.parseInt(totalAmount);
            payableAmount = totalAmount;
            flag = bundle.getString("flag");
        }

        parentPanel = findViewById(R.id.parentPanel);
        rl_discount = findViewById(R.id.rl_discount);
        tv_total_amount = findViewById(R.id.tv_total_amount);
        tv_discount_amount = findViewById(R.id.tv_discount_amount);
        tv_payable_amount = findViewById(R.id.tv_payable_amount);
        tv_voucher_message = findViewById(R.id.tv_voucher_message);
        et_promo_code = findViewById(R.id.et_promo_code);
        tv_vouchers = findViewById(R.id.tv_vouchers);
        rl_vouchers = findViewById(R.id.rl_vouchers);
        tv_remove_vouchers = findViewById(R.id.tv_remove_vouchers);
        tv_reset_promo_code = findViewById(R.id.tv_reset_promo_code);
        back=findViewById(R.id.back_voucher);

        rl_package_qty = findViewById(R.id.rl_package_qty);
        spinner_qty = findViewById(R.id.spinner_qty);


        findViewById(R.id.tv_btn_apply).setOnClickListener(this);
        findViewById(R.id.tv_btn_proceed).setOnClickListener(this);
        findViewById(R.id.tv_remove_vouchers).setOnClickListener(this);
        findViewById(R.id.tv_reset_promo_code).setOnClickListener(this);
        back.setOnClickListener(this);


        tv_total_amount.setText("₹ " + totalAmount);
        tv_payable_amount.setText("₹ " + payableAmount);

        if (flag.equals("DealshaiPlus")) {
            rl_package_qty.setVisibility(View.VISIBLE);
        } else {
            rl_package_qty.setVisibility(View.GONE);
        }

        RecyclerView recycler_view = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CheckOutActivity.this);
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        adapter = new VouchersListAdapter(CheckOutActivity.this, CheckOutActivity.this, vouchersArrayList);
        recycler_view.setAdapter(adapter);
        rl_discount.setVisibility(View.GONE);
        tv_voucher_message.setVisibility(View.GONE);
        tv_reset_promo_code.setVisibility(View.GONE);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, quantity);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_qty.setAdapter(dataAdapter);

        spinner_qty.setOnItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
            case R.id.back_voucher:
                onBackPressed();
                break;
            case R.id.tv_btn_apply:
                NetworkConnection connection = new NetworkConnection(CheckOutActivity.this);
                promoCode = et_promo_code.getText().toString().trim();
                if (connection.isNetworkConnected()) {
                    if (!promoCode.equals(""))
                        applyCouponCode(userId, promoCode, totalAmount);
                    else
                        Snackbar.make(parentPanel, "Please enter a valid promo code.", Snackbar.LENGTH_LONG).show();
                }
                break;
            case R.id.tv_btn_proceed:
                PrePaymentClass prePaymentClass = new PrePaymentClass(CheckOutActivity.this, CheckOutActivity.this, couponsDetails, payableAmount, userId, voucherId, voucherType, promoId, promoType, totalAmount, quantity);
                prePaymentClass.startTransaction();
                break;
            case R.id.tv_remove_vouchers:
                voucherType = "";
                voucherId = "";
                discountAmount = "0";
                rl_discount.setVisibility(View.GONE);
                payableAmount = String.valueOf(Integer.parseInt(totalAmount) - Integer.parseInt(discountAmount));
                tv_payable_amount.setText("₹ " + payableAmount);
                tv_remove_vouchers.setVisibility(View.GONE);
                break;
            case R.id.tv_reset_promo_code:
                promoType = "";
                payableAmount = "";
                promoId = "";
                promoCode = "";
                et_promo_code.setText("");
                tv_voucher_message.setVisibility(View.GONE);
                tv_reset_promo_code.setVisibility(View.GONE);
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
                    Log.e("Response", response);
                    String err = String.valueOf(jsonObject.getInt("err"));
                    final String msg = jsonObject.getString("msg");
                    if (err.equals("0")) {
                        promoId = jsonObject.getString("id");
                        promoAmount = jsonObject.getString("amount");
                        promoType = jsonObject.getString("type");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_reset_promo_code.setVisibility(View.VISIBLE);
                                tv_voucher_message.setVisibility(View.VISIBLE);
                                tv_voucher_message.setText(msg);
                                tv_voucher_message.setTextColor(getResources().getColor(R.color.colorDarkGreen));
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_reset_promo_code.setVisibility(View.VISIBLE);
                                tv_voucher_message.setVisibility(View.VISIBLE);
                                tv_voucher_message.setText(msg);
                                tv_voucher_message.setTextColor(getResources().getColor(R.color.colorDarkRed));
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(parentPanel, "Response error!", Snackbar.LENGTH_LONG).show();
                }
            }
        };
        webServiceHandler.applyPromoCode(userId, promoCode, totalAmount, promoId, promoType, voucherId, voucherType);
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
                            obj.setMinPurchase(object.getString("min_purchase"));
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
                                rl_vouchers.setVisibility(View.GONE);
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

    private void applyVoucher(final String voucherId, String voucherType, final String totalAmount) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(CheckOutActivity.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    Log.e("Response", response);
                    String err = String.valueOf(jsonObject.getInt("err"));
                    String msg = jsonObject.getString("msg");
                    if (err.equals("0")) {
                        discountAmount = jsonObject.getString("amount");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rl_discount.setVisibility(View.VISIBLE);
                                tv_remove_vouchers.setVisibility(View.VISIBLE);
                                tv_discount_amount.setText("₹ " + discountAmount);
                                payableAmount = String.valueOf(Integer.parseInt(totalAmount) - Integer.parseInt(discountAmount));
                                tv_payable_amount.setText("₹ " + payableAmount);
                            }
                        });
                    } else {
                        Snackbar.make(parentPanel, msg, Snackbar.LENGTH_LONG).show();
                        CheckOutActivity.this.voucherId = "";
                        CheckOutActivity.this.voucherType = "";
                        tv_remove_vouchers.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(parentPanel, "Response error!", Snackbar.LENGTH_LONG).show();
                }
            }
        };
        webServiceHandler.applyVoucher(userId, voucherId, voucherType, totalAmount, promoId, promoType);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
//        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        quantity = Integer.parseInt(item);
        totalAmount = String.valueOf(actualPrice * quantity);
        payableAmount = String.valueOf(Integer.parseInt(totalAmount) - Integer.parseInt(discountAmount));
        tv_payable_amount.setText("₹ " + payableAmount);
        tv_total_amount.setText("₹ " + totalAmount);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
            private LinearLayout linearLayout;
            //private ImageView iv_voucher_type;
            //private TextView tv_voucher_status;

            ViewHolder(View itemView) {
                super(itemView);
                tv_referral_code = itemView.findViewById(R.id.tv_referral_code);
                tv_voucher_amount = itemView.findViewById(R.id.tv_voucher_amount);
                tv_voucher_date = itemView.findViewById(R.id.tv_voucher_date);
                linearLayout=itemView.findViewById(R.id.my_vouchers);
                //iv_voucher_type = itemView.findViewById(R.id.iv_voucher_type);
              //  tv_voucher_status = itemView.findViewById(R.id.tv_voucher_status);

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
            holder.tv_referral_code.setText("Minimum Order: ₹" + vouchers.getMinPurchase());
            holder.tv_voucher_amount.setText("₹" + vouchers.getVoucherAmount());
            holder.tv_voucher_date.setText("Valid Till: "+vouchers.getVoucherDate());
            /**switch (vouchers.getVoucherType()) {
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
            }*/
            //holder.tv_voucher_status.setText(" " + vouchers.getVoucherStaus());
             String v=vouchers.getVoucherStaus();
             if(v.equals("inactive"))
             {
                 holder.tv_voucher_date.setText(" expired");
                 holder.tv_voucher_date.setTextColor(getResources().getColor(R.color.red_dot_color));
                 holder.linearLayout.setBackgroundResource(R.drawable.shape_voucher_background_expired);
             //myVoucher.setBackground(getResources().getDrawable(R.drawable.shape_voucher_background_expired));
             }
           /** switch (vouchers.getVoucherStaus()) {
                case "active":
                    holder.tv_voucher_status.setTextColor(getResources().getColor(R.color.green_dot_color));
                    holder.tv_voucher_status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.green_dot, 0, 0, 0);
                    break;
                case "Expired":
                    holder.tv_voucher_status.setTextColor(getResources().getColor(R.color.red_dot_color));
                    holder.tv_voucher_status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.red_dot, 0, 0, 0);
                    break;
            }*/
        }

        @Override
        public int getItemCount() {
            return vouchersArrayList.size();
        }
    }
}