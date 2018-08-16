package com.ogma.dealshaiapp.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.adapter.MerchantViewAdapter;
import com.ogma.dealshaiapp.model.MerchantDetails;
import com.ogma.dealshaiapp.network.NetworkConnection;
import com.ogma.dealshaiapp.network.WebServiceHandler;
import com.ogma.dealshaiapp.network.WebServiceListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchResultActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<MerchantDetails> arrayList;
    private MerchantViewAdapter merchentViewAdapter;
    private String searchText;
    private LinearLayout parentPanel;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        arrayList = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.toolbar);
        /**if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setTitle("Search Result");
            // toolbar.setBackgroundColor();
        }*/
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            searchText = bundle.getString("searchText");
        }

        parentPanel = findViewById(R.id.parentPanel);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SearchResultActivity.this);
        RecyclerView merchantsView = findViewById(R.id.recycler_view);
        merchantsView.setLayoutManager(layoutManager);
        merchantsView.setItemAnimator(new DefaultItemAnimator());
        merchentViewAdapter = new MerchantViewAdapter(SearchResultActivity.this, SearchResultActivity.this, arrayList);
        merchantsView.setAdapter(merchentViewAdapter);
        back=findViewById(R.id.back_voucher);
        back.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkConnection connection = new NetworkConnection(SearchResultActivity.this);
        if (connection.isNetworkConnected()) {
            getData();
        }
    }

    private void getData() {
        WebServiceHandler webServiceHandler = new WebServiceHandler(SearchResultActivity.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {

                JSONObject main = null;
                String is_error = null;
                try {
                    main = new JSONObject(response);
                    is_error = main.getString("err");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(parentPanel, "Response error!", Snackbar.LENGTH_LONG).show();
                }
                if (is_error == null || Integer.parseInt(is_error) == 1) {
                    Snackbar.make(parentPanel, "No data found", Snackbar.LENGTH_SHORT).show();
                } else {
                    arrayList.clear();
                    JSONArray merchant = null;
                    try {
                        merchant = main.getJSONArray("merchant");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Snackbar.make(parentPanel, "No merchant found", Snackbar.LENGTH_SHORT).show();
                    }
                    try {
                        if (merchant != null && merchant.length() > 0) {
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Snackbar.make(parentPanel, "Missing merchant data", Snackbar.LENGTH_SHORT).show();
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        merchentViewAdapter.notifyDataSetChanged();
                    }
                });

            }
        };
        webServiceHandler.getSearchResult(searchText);
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
