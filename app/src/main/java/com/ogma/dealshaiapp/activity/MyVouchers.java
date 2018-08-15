package com.ogma.dealshaiapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.model.Vouchers;
import com.ogma.dealshaiapp.network.NetworkConnection;
import com.ogma.dealshaiapp.network.WebServiceHandler;
import com.ogma.dealshaiapp.network.WebServiceListener;
import com.ogma.dealshaiapp.utility.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import me.anwarshahriar.calligrapher.Calligrapher;

public class MyVouchers extends AppCompatActivity implements View.OnClickListener{
    TextView mytv;
    Typeface myfont;

    private Session session;
    private ImageView back_button;
    private String userId;
    private ArrayList<Vouchers> vouchersArrayList = new ArrayList<>();
    private VouchersListAdapter adapter;
    private CoordinatorLayout parentPanel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_vouchers);
        Toolbar toolbar = findViewById(R.id.toolbar);
        /**if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setTitle("My Vouchers");
            toolbar.setTitleTextColor(Color.BLACK);
        }*/
        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this, "GOTHICB.ttf",true);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        parentPanel = findViewById(R.id.parentPanel);
        back_button=findViewById(R.id.back_voucher);
        session = new Session(MyVouchers.this);
        HashMap<String, String> user = session.getUserDetails();
        userId = user.get(Session.KEY_ID);

        RecyclerView recycler_view = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MyVouchers.this);
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        adapter = new VouchersListAdapter(MyVouchers.this, MyVouchers.this, vouchersArrayList);
        recycler_view.setAdapter(adapter);
        back_button.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkConnection connection = new NetworkConnection(MyVouchers.this);
        if (connection.isNetworkConnected()) {
            getVoucherItemList(userId);
        }
    }

    private void getVoucherItemList(String userId) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(MyVouchers.this);
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
                        String msg = jsonObject.getString("msg");
                        Snackbar.make(parentPanel, msg, Snackbar.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(parentPanel, "Response error!", Snackbar.LENGTH_LONG).show();
                }
            }
        };
        webServiceHandler.getVoucherList(userId);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id)
        {
            case R.id.back_voucher:
                startActivity(new Intent(MyVouchers.this, IndexActivity.class));
                break;
        }
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
            Typeface tx=Typeface.createFromAsset(getAssets(),"gothic.ttf");
            //private ImageView iv_voucher_type;
           // private TextView tv_voucher_status;

            ViewHolder(View itemView) {
                super(itemView);
                tv_referral_code = itemView.findViewById(R.id.tv_referral_code);
                tv_voucher_amount = itemView.findViewById(R.id.tv_voucher_amount);
                tv_voucher_date = itemView.findViewById(R.id.tv_voucher_date);
                linearLayout=itemView.findViewById(R.id.my_vouchers);
                //iv_voucher_type = itemView.findViewById(R.id.iv_voucher_type);
                //tv_voucher_status = itemView.findViewById(R.id.tv_voucher_status);
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
            //holder.tv_voucher_date.setText("Expire on: " + vouchers.getVoucherDate());
            //holder.tv_referral_code.setText(vouchers.getMinPurchase());
            //holder.tv_voucher_amount.setText(vouchers.getVoucherAmount());
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
            }
            /**switch (vouchers.getVoucherStaus()) {
                case "active":
                    holder.tv_voucher_status.setTextColor(getResources().getColor(R.color.green_dot_color));
                    holder.tv_voucher_status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.green_dot, 0, 0, 0);
                    break;
                case "inactive":
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(MyVouchers.this, IndexActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
}
