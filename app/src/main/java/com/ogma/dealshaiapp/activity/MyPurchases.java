package com.ogma.dealshaiapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.dialog.QRCodeView;
import com.ogma.dealshaiapp.model.OrderedItems;
import com.ogma.dealshaiapp.network.NetworkConnection;
import com.ogma.dealshaiapp.network.WebServiceHandler;
import com.ogma.dealshaiapp.network.WebServiceListener;
import com.ogma.dealshaiapp.utility.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MyPurchases extends AppCompatActivity implements View.OnClickListener{

    private RecyclerView recycler_view;
    private Session session;
    private String userId;
    private PurchaseListAdapter adapter;
    private ArrayList<OrderedItems> arrayList;
    private CoordinatorLayout parentPanel;
    private Button click;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_purchases);

        parentPanel = findViewById(R.id.parentPanel);
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setTitle("My Purchases");
            // toolbar.setBackgroundColor();
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        session = new Session(MyPurchases.this);
        HashMap<String, String> user = session.getUserDetails();
        userId = user.get(Session.KEY_ID);
        back=findViewById(R.id.back_voucher);
        arrayList = new ArrayList<>();
        recycler_view = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MyPurchases.this);
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        adapter = new PurchaseListAdapter(MyPurchases.this, MyPurchases.this, arrayList);
        recycler_view.setAdapter(adapter);
        click=findViewById(R.id.more_purchase);
        back.setOnClickListener(this);
        click.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkConnection connection = new NetworkConnection(MyPurchases.this);
        if (connection.isNetworkConnected()) {
            getPurchesedItemList(userId);
        }
    }

    private void getPurchesedItemList(String userId) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(MyPurchases.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(parentPanel, "Response error!", Snackbar.LENGTH_LONG).show();
                }
                try {
                    JSONArray active = null;
                    if (jsonObject != null) {
                        active = jsonObject.getJSONArray("active");
                    }
                    if (active != null) {
                        arrayList.clear();
                        for (int i = 0; i < active.length(); i++) {
                            OrderedItems orderedItems = new OrderedItems();
                            JSONObject deals = active.getJSONObject(i);
                            orderedItems.setId(deals.getString("id"));
                            orderedItems.setCouponCode(deals.getString("cupon_code"));
                            orderedItems.setCouponTitle(deals.getString("cupon_title"));
                            orderedItems.setMerchantName(deals.getString("merchant_name"));
                            orderedItems.setPrice(deals.getString("price"));
                            orderedItems.setQuantity(deals.getString("quantity"));
                            orderedItems.setTotal_price(deals.getString("total_price"));
                            orderedItems.setQrImg(deals.getString("qr_img"));
                            orderedItems.setImg(deals.getString("img"));
                            orderedItems.setRedeemed(deals.getString("reedemed"));
                            orderedItems.setValidFor(deals.getString("valid_for"));
                            orderedItems.setValidOn(deals.getString("valid_on"));
                            orderedItems.setOrderId(deals.getString("order_id"));
                            orderedItems.setOrderDate(deals.getString("order_date"));
                            arrayList.add(orderedItems);
                        }
                    }
                } catch (JSONException e) {
                    MyPurchases.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(parentPanel, "You haven't purchased anything before", Snackbar.LENGTH_LONG).show();
                            recycler_view.setVisibility(View.INVISIBLE);
                        }
                    });
                    e.printStackTrace();
                }

                MyPurchases.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (arrayList.size() > 0) {
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

            }
        };
        webServiceHandler.getPurchaseList(userId);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id)
        {
            case R.id.back_voucher:
                startActivity(new Intent(MyPurchases.this, IndexActivity.class));
                break;
            case R.id.more_purchase:
                startActivity(new Intent(MyPurchases.this, IndexActivity.class));
                break;
        }
    }

    private class PurchaseListAdapter extends RecyclerView.Adapter<PurchaseListAdapter.ViewHolder> {
        private Activity activity;
        private Context context;
        private DisplayImageOptions options;
        private ImageLoader imageLoader;
        private ArrayList<OrderedItems> purchaseItems;
        private String orderId;
        private String uniquCode;

        PurchaseListAdapter(Activity activity, Context context, ArrayList<OrderedItems> purchaseItems) {
            this.activity = activity;
            this.context = context;
            this.purchaseItems = purchaseItems;

            imageLoader = ImageLoader.getInstance();
            if (!imageLoader.isInited()) {
                imageLoader.init(ImageLoaderConfiguration.createDefault(context));
            }
            options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .build();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView index_banner;
            ImageView iv_redeemed;
            TextView tv_title;
            TextView tv_merchant_name;
            TextView tv_quantity;
            TextView tv_price;
            TextView tv_valid_for;
            TextView tv_valid_on;
            TextView tv_total_amount;
            TextView tv_order_id;
            TextView tv_purchased_date;
            ImageView iv_qr_code;

            ViewHolder(View itemView) {
                super(itemView);
                tv_title = itemView.findViewById(R.id.tv_title);
                tv_merchant_name = itemView.findViewById(R.id.tv_merchant_name);
                tv_quantity = itemView.findViewById(R.id.tv_quantity);
                tv_price = itemView.findViewById(R.id.tv_price);
                tv_valid_for = itemView.findViewById(R.id.tv_valid_for);
                tv_valid_on = itemView.findViewById(R.id.tv_valid_on);
                tv_total_amount = itemView.findViewById(R.id.tv_total_amount);
                tv_purchased_date = itemView.findViewById(R.id.tv_purchased_date);
                tv_order_id = itemView.findViewById(R.id.tv_order_id);
                index_banner = itemView.findViewById(R.id.index_banner);
                iv_redeemed = itemView.findViewById(R.id.iv_redeemed);
                iv_qr_code = itemView.findViewById(R.id.iv_qr_code);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OrderedItems orderedItems = arrayList.get(getAdapterPosition());
                        orderId = orderedItems.getId();
                        uniquCode = orderedItems.getCouponCode();
                        new QRCodeView(context, activity, orderId, uniquCode).show();
                    }
                });

                iv_qr_code.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OrderedItems orderedItems = arrayList.get(getAdapterPosition());
                        orderId = orderedItems.getId();
                        uniquCode = orderedItems.getCouponCode();
                        new QRCodeView(context, activity, orderId, uniquCode).show();
                    }
                });

            }
        }

        public PurchaseListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchased_single_item_view, parent, false);
            return new PurchaseListAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(PurchaseListAdapter.ViewHolder holder, int position) {

            OrderedItems orderedItems = arrayList.get(position);
            holder.tv_merchant_name.setText(orderedItems.getMerchantName());
            holder.tv_title.setText(orderedItems.getCouponTitle());
            holder.tv_quantity.setText(orderedItems.getQuantity());
            holder.tv_price.setText("₹" + orderedItems.getPrice());
            holder.tv_total_amount.setText("₹" + orderedItems.getTotal_price());
            holder.tv_valid_for.setText(orderedItems.getValidFor());
            holder.tv_valid_on.setText(orderedItems.getValidOn());
            holder.tv_purchased_date.setText(orderedItems.getOrderDate());
            holder.tv_order_id.setText(orderedItems.getOrderId());

            imageLoader.displayImage(orderedItems.getImg(), holder.index_banner, options);
            if (orderedItems.getRedeemed().equals("1"))
                holder.iv_redeemed.setVisibility(View.VISIBLE);
        }

        @Override
        public int getItemCount() {
            return purchaseItems.size();
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
        Intent i = new Intent(MyPurchases.this, IndexActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
}
