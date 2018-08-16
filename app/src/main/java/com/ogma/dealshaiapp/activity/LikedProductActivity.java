package com.ogma.dealshaiapp.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.adapter.MerchantViewAdapter;
import com.ogma.dealshaiapp.model.MerchantDetails;
import com.ogma.dealshaiapp.network.NetworkConnection;
import com.ogma.dealshaiapp.network.WebServiceHandler;
import com.ogma.dealshaiapp.network.WebServiceListener;
import com.ogma.dealshaiapp.utility.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class LikedProductActivity extends AppCompatActivity implements View.OnClickListener{

    private ArrayList<MerchantDetails> arrayList;
    private MerchantViewAdapter marchentViewAdapter;
    private String userId;
    private LinearLayout parentPanel;
    private SwipeRefreshLayout swipe_refresh_layout;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        arrayList = new ArrayList<>();

        parentPanel = findViewById(R.id.parentPanel);
        TextView text_toolbar=findViewById(R.id.text_toolbar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        text_toolbar.setText("Favorites");
            // toolbar.setBackgroundColor();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Session session = new Session(LikedProductActivity.this);
        HashMap<String, String> user = session.getUserDetails();
        back=findViewById(R.id.back_voucher);
        userId = user.get(Session.KEY_ID);

        swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout);
        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                NetworkConnection connection = new NetworkConnection(LikedProductActivity.this);
                if (connection.isNetworkConnected()) {
                    getData();
                }
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(LikedProductActivity.this);
        RecyclerView merchantsView = findViewById(R.id.recycler_view);
        merchantsView.setLayoutManager(layoutManager);
        merchantsView.setItemAnimator(new DefaultItemAnimator());
        marchentViewAdapter = new MerchantViewAdapter(LikedProductActivity.this, LikedProductActivity.this, arrayList);
        merchantsView.setAdapter(marchentViewAdapter);
        back.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkConnection connection = new NetworkConnection(LikedProductActivity.this);
        if (connection.isNetworkConnected()) {
            getData();
        }
    }

    private void getData() {
        WebServiceHandler webServiceHandler = new WebServiceHandler(LikedProductActivity.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipe_refresh_layout.setRefreshing(false);
                    }
                });
                JSONObject main = null;
                String is_error = null;
                try {
                    main = new JSONObject(response);
                    is_error = main.getString("err");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(parentPanel, "Response error!", Snackbar.LENGTH_SHORT).show();
                }

                if (is_error == null || Integer.parseInt(is_error) == 1) {
                    Snackbar.make(parentPanel, "No data found", Snackbar.LENGTH_SHORT).show();
                } else {
                    JSONArray merchant = null;
                    try {
                        merchant = main.getJSONArray("merchant");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Snackbar.make(parentPanel, "No merchants found.", Snackbar.LENGTH_SHORT).show();
                    }
                    try {
                        if (Integer.parseInt(main.getString("err")) == 1) {
                            Snackbar.make(parentPanel, main.getString("err_msg"), Snackbar.LENGTH_SHORT).show();
                        } else {
                            if (merchant != null && merchant.length() > 0) {
                                arrayList.clear();
                                for (int i = 0; i < merchant.length(); i++) {
                                    MerchantDetails merchantDetails = new MerchantDetails();
                                    JSONObject object = merchant.getJSONObject(i);
                                    merchantDetails.setMerchantId(object.getString("id"));
                                    merchantDetails.setTitle(object.getString("merchant_name"));
                                    merchantDetails.setArea(object.getString("area"));
                                    merchantDetails.setDiscount(object.getString("discount"));
                                    merchantDetails.setShortDescription(object.getString("cupon_title"));
                                    merchantDetails.setPrice(object.getString("price"));
                                    merchantDetails.setOfferPrice(object.getString("our_price"));
                                    merchantDetails.setImg(object.getString("img"));
                                    merchantDetails.setLikes(object.getString("like"));
                                    arrayList.add(merchantDetails);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            marchentViewAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        };
        webServiceHandler.getLikedProduct(userId);
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
    public void onClick(View view) {
        onBackPressed();
    }
}
