package com.ogma.dealshaiapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.dialog.MoreInfoView;
import com.ogma.dealshaiapp.fragment.FragmentDetailsPageBanner;
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

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import me.relex.circleindicator.CircleIndicator;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private AutoScrollViewPager banner_view_pager;
    private BannerAdapter slidingViewAdapter;
    private CircleIndicator banner_indicator;
    private FragmentManager fragmentManager;
    private int currentPage;
    private JSONArray banner;
    private JSONArray deals;
    private ArrayList<CouponsDetails> couponsDetails;
    private CouponsListAdapter recyclerViewAdapter;
    private TextView title_header;
    private TextView tv_total_like;
    private TextView title;
    private TextView tv_rating;
    private TextView tv_location;
    private TextView tv_btn_menu;
    private TextView tv_btn_contact;
    private TextView tv_more_info;
    private TextView tv_terms_and_condition;
    private TextView tv_total_amount;
    private int totalAmount;
    private String merchant_id;
    private String merchantName = "";
    private String description;
    private String shareUrl = "https://www.dealshai.in/";
    private int totalLike = 0;
    private String likes;
    private String userId;
    private String termsCondition = "";
    private String msg;
    private RelativeLayout parentPanel;
    private String content;
    private String contact;
    private Dialog alertDialog;
    private FrameLayout error_screen;
    private static String[] PERMISSIONS_CALL = {Manifest.permission.CALL_PHONE};
    private static final int REQUEST_CALL = 1;
    private String menuStr;
    private boolean flagCanBuy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_modified);

        Session session = new Session(DetailsActivity.this);
        HashMap<String, String> user = session.getUserDetails();
        userId = user.get(Session.KEY_ID);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            merchant_id = String.valueOf(bundle.getInt("merchant_id"));
        }

        banner = new JSONArray();
        deals = new JSONArray();
        couponsDetails = new ArrayList<>();

        parentPanel = findViewById(R.id.parentPanel);
        ImageView iv_back = findViewById(R.id.iv_back);
        banner_view_pager = findViewById(R.id.banner_view_pager);
        error_screen = findViewById(R.id.error_screen);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        title_header = findViewById(R.id.title_header);
        tv_total_like = findViewById(R.id.tv_total_like);
        title = findViewById(R.id.title);
        tv_rating = findViewById(R.id.tv_rating);
        tv_location = findViewById(R.id.tv_location);
        tv_btn_menu = findViewById(R.id.tv_btn_menu);
        tv_btn_contact = findViewById(R.id.tv_btn_contact);
        tv_more_info = findViewById(R.id.tv_more_info);
        tv_total_amount = findViewById(R.id.tv_total_amount);
        tv_terms_and_condition = findViewById(R.id.tv_terms_and_condition);
        TextView tv_distance = findViewById(R.id.tv_distance);
        TextView tv_btn_buy_now = findViewById(R.id.tv_btn_buy_now);
        ImageView iv_share = findViewById(R.id.iv_share);
        ImageView iv_like = findViewById(R.id.iv_like);


        banner_indicator = findViewById(R.id.banner_indicator);
        banner_view_pager.setInterval(4000);
        banner_view_pager.startAutoScroll();
        fragmentManager = getSupportFragmentManager();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(DetailsActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerViewAdapter = new CouponsListAdapter(DetailsActivity.this, couponsDetails);
        recyclerView.setAdapter(recyclerViewAdapter);

        banner_view_pager.setPageTransformer(true, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                page.setTranslationX(page.getWidth() * -position);

                if (position <= -1.0F || position >= 1.0F) {
                    page.setAlpha(0.0F);
                } else if (position == 0.0F) {
                    page.setAlpha(1.0F);
                } else {
                    page.setAlpha(1.0F - Math.abs(position));
                }
            }
        });

        banner_view_pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        tv_btn_menu.setVisibility(View.GONE);
        error_screen.setVisibility(View.GONE);
        tv_terms_and_condition.setVisibility(View.VISIBLE);

        iv_back.setOnClickListener(this);
        iv_share.setOnClickListener(this);
        iv_like.setOnClickListener(this);
        tv_total_like.setOnClickListener(this);
        tv_btn_buy_now.setOnClickListener(this);
        tv_distance.setOnClickListener(this);
        tv_btn_menu.setOnClickListener(this);
        tv_more_info.setOnClickListener(this);
        tv_btn_contact.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // stop auto scroll when onPause
        banner_view_pager.stopAutoScroll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // start auto scroll when onResume
        banner_view_pager.startAutoScroll();
        NetworkConnection connection = new NetworkConnection(DetailsActivity.this);
        if (connection.isNetworkConnected()) {
            getData();
        } else
            Toast.makeText(DetailsActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        AlertDialog.Builder alertDialogBuilder;
        switch (id) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_btn_buy_now:
                for (int i = 0; i < couponsDetails.size(); i++) {
                    CouponsDetails couponsDetail = couponsDetails.get(i);
                    if (couponsDetail.getIsSelected() == 1) {
                        flagCanBuy = true;
                        break;
                    }
                }
                if (flagCanBuy) {
                    couponsDetails = recyclerViewAdapter.getCouponsDetails();
                    startActivity(new Intent(DetailsActivity.this, CheckOutActivity.class)
                            .putExtra("flag", "DetailsActivity")
                            .putExtra("couponList", couponsDetails)
                            .putExtra("totalAmount", String.valueOf(totalAmount)));
                } else {
                    Snackbar.make(parentPanel, "Please add a deals into cart please!", Snackbar.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_like:
            case R.id.tv_total_like:
                hitLike(merchant_id, userId);
                break;
            case R.id.iv_share:
                share_via_app();
                break;
            case R.id.tv_distance:
                startActivity(new Intent(DetailsActivity.this, MapsActivity.class).putExtra("merchantId", merchant_id));
                break;
            case R.id.tv_btn_menu:
                alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("MENU");
                alertDialogBuilder.setMessage(menuStr);
                alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                alertDialog.setCanceledOnTouchOutside(true);
                break;

            case R.id.tv_more_info:
                if (content != null) {
                    new MoreInfoView(DetailsActivity.this, content).show();
                }
                break;
            case R.id.tv_btn_contact:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    requestCallPermission();
                } else {

                    if (contact != null) {

                        alertDialogBuilder = new AlertDialog.Builder(this);
                        alertDialogBuilder.setTitle(R.string.contact);
                        alertDialogBuilder.setMessage(contact);
                        alertDialogBuilder.setPositiveButton(R.string.call, new DialogInterface.OnClickListener() {
                            @SuppressLint("MissingPermission")
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:" + contact));
                                startActivity(callIntent);
                            }
                        })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        alertDialog.dismiss();
                                    }
                                });
                        alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                        alertDialog.setCanceledOnTouchOutside(false);
                    } else
                        Snackbar.make(parentPanel, "No contact available", Snackbar.LENGTH_SHORT).show();

                }
                break;
        }
    }

    private void hitLike(String merchant_id, String userId) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(DetailsActivity.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int isErr = Integer.parseInt(jsonObject.getString("err"));
                    if (isErr == 0) {
                        msg = jsonObject.getString("msg");
                        DetailsActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (msg) {
                                    case "Like Successfully":
                                        totalLike = totalLike + 1;
                                        tv_total_like.setText(String.valueOf(totalLike));
                                        break;
                                    case "Dislike Successfully":
                                        totalLike = totalLike - 1;
                                        tv_total_like.setText(String.valueOf(totalLike));
                                        break;
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };
        webServiceHandler.hitLike(merchant_id, userId);
    }

    private void getData() {
        WebServiceHandler webServiceHandler = new WebServiceHandler(DetailsActivity.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject main = new JSONObject(response);
                    Log.e("Response", response);
                    String isErr = main.getString("err");
                    int is_menu;

                    if (isErr != null && Integer.parseInt(isErr) == 1) {
                        Snackbar.make(parentPanel, main.getString("err_msg"), Snackbar.LENGTH_SHORT).show();
                    } else {
                        merchantName = main.getString("merchant_name");
                        final String area = main.getString("area");
                        description = main.getString("description");
                        content = main.getString("more");
                        contact = main.getString("contact");
                        totalLike = Integer.parseInt(main.getString("like"));
                        termsCondition = main.getString("condition");
                        shareUrl = main.getString("share_link");
                        is_menu = main.getInt("is_menu");
                        if (is_menu == 1) {
                            menuStr = main.getString("menu");
                            setMenu(menuStr);
                        }
                        deals = main.getJSONArray("deals");
                        if (deals.length() > 0) {
                            couponsDetails.clear();
                            for (int i = 0; i < deals.length(); i++) {
                                CouponsDetails couponDetails = new CouponsDetails();
                                JSONObject object = deals.getJSONObject(i);
                                String couponTitle = object.getString("title");
                                String couponId = object.getString("id");
                                String couponDescription = object.getString("description");
                                String couponValidForPerson = object.getString("valid_for_person");
                                String couponValidOn = object.getString("valid_on");
                                int newPrice = object.getInt("our_price");
                                int originalPrice = object.getInt("price");
                                couponDetails.setCouponTitle(couponTitle);
                                couponDetails.setCouponDescription(couponDescription);
                                couponDetails.setCouponValidForPerson(couponValidForPerson);
                                couponDetails.setCouponValidOn(couponValidOn);
                                couponDetails.setNewPrice(newPrice);
                                couponDetails.setOriginalPrice(originalPrice);
                                couponDetails.setCouponId(couponId);
                                couponDetails.setMerchantId(merchant_id);
                                couponDetails.setIsSinglePurchase(object.getInt("is_single_purchase"));
                                couponsDetails.add(couponDetails);
                            }
                        } else {
                            Snackbar.make(parentPanel, "No Coupons found for this merchant.", Snackbar.LENGTH_SHORT).show();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    error_screen.setVisibility(View.VISIBLE);
                                    tv_terms_and_condition.setVisibility(View.GONE);
                                }
                            });
                        }


                        banner = main.getJSONArray("slider");

                        DetailsActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (banner.length() > 0) {
                                    slidingViewAdapter = new BannerAdapter(fragmentManager, banner);
                                    banner_view_pager.setAdapter(slidingViewAdapter);
                                    banner_indicator.setViewPager(banner_view_pager);
                                    banner_view_pager.setCurrentItem(Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % banner.length());
                                    banner_view_pager.setStopScrollWhenTouch(true);
                                    slidingViewAdapter.notifyDataSetChanged();
                                } else {
                                    Snackbar.make(parentPanel, "No image found for this merchant.", Snackbar.LENGTH_SHORT).show();
                                }
                                recyclerViewAdapter.notifyDataSetChanged();
                                title_header.setText(merchantName);
                                title.setText(merchantName);
                                tv_location.setText(" " + area);
                                tv_total_like.setText(String.valueOf(totalLike));
                                tv_terms_and_condition.setText("Terms & Condition:\n" + termsCondition);
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    DetailsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(parentPanel, "Server Error!!!", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        };
        webServiceHandler.getDetailsData(merchant_id, userId);
    }

    private void setMenu(String menuStr) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_btn_menu.setVisibility(View.VISIBLE);
            }
        });
    }

    public void share_via_app() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        sendIntent.putExtra(Intent.EXTRA_TEXT, merchantName);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareUrl);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void setTotalPayable(int amount) {

        totalAmount = Integer.parseInt(tv_total_amount.getText().toString().trim());
        if (totalAmount >= 0) {
            totalAmount = totalAmount + amount;
        }
        tv_total_amount.setText(String.valueOf(totalAmount));

    }

    private class BannerAdapter extends FragmentPagerAdapter {
        private JSONArray banner;

        BannerAdapter(FragmentManager fm, JSONArray banner) {
            super(fm);
            this.banner = banner;
        }

        @Override
        public Fragment getItem(int position) {
            try {
                return FragmentDetailsPageBanner.newInstance(banner.getString(position));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public int getCount() {
            return banner.length();
        }
    }

    private static class CouponsListAdapter extends RecyclerView.Adapter<CouponsListAdapter.ViewHolder> {
        private ArrayList<CouponsDetails> couponsDetails = new ArrayList<>();
        private Context context;

        CouponsListAdapter(Context context, ArrayList<CouponsDetails> couponsDetails) {
            this.couponsDetails = couponsDetails;
            this.context = context;
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
                        CouponsDetails couponDetails = couponsDetails.get(getAdapterPosition());
                        int quantity = couponDetails.getQuantity();
                        if (couponDetails.getIsSinglePurchase() == 1) {
                            if (couponDetails.getQuantity() < 1) {
                                quantity++;
                                if (context instanceof DetailsActivity) {
                                    int amount = couponDetails.getNewPrice();
                                    ((DetailsActivity) context).setTotalPayable(amount);
                                }
                                couponDetails.setQuantity(quantity);
                                if (couponDetails.getQuantity() > 0) {
                                    couponDetails.setIsSelected(1);
                                }
                                notifyDataSetChanged();
                            } else
                                Toast.makeText(context, R.string.not_more_than_one, Toast.LENGTH_SHORT).show();
                        } else {
                            quantity++;
                            if (context instanceof DetailsActivity) {
                                int amount = couponDetails.getNewPrice();
                                ((DetailsActivity) context).setTotalPayable(amount);
                            }
                            couponDetails.setQuantity(quantity);
                            if (couponDetails.getQuantity() > 0) {
                                couponDetails.setIsSelected(1);
                            }
                            notifyDataSetChanged();
                        }
                        //updateItemQuantity(quantity, getAdapterPosition());
                    }
                });

                iv_minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CouponsDetails couponDetails = couponsDetails.get(getAdapterPosition());
                        int quantity = couponDetails.getQuantity();
                        if (quantity > 0) {
                            quantity--;
                            int amount = couponDetails.getNewPrice();
                            if (context instanceof DetailsActivity) {
                                ((DetailsActivity) context).setTotalPayable(-amount);
                            }
                            couponDetails.setQuantity(quantity);
                            if (couponDetails.getQuantity() == 0) {
                                couponDetails.setIsSelected(0);
                            }
                            notifyDataSetChanged();
                            //updateItemQuantity(quantity, getAdapterPosition());
                        }
                    }
                });
            }
        }

        public CouponsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quick_view_item, parent, false);
            return new CouponsListAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final CouponsListAdapter.ViewHolder holder, int position) {

            String coupon_id = "";
            String deals_id = "";
            String title = "";
            String description = "";
            String valid_for = "";
            String valid_on = "";
            int original_price = 0;
            int new_price = 0;
            int quantity = 0;

            CouponsDetails couponDetails = couponsDetails.get(position);
            title = couponDetails.getCouponTitle();
            description = couponDetails.getCouponDescription();
            valid_for = couponDetails.getCouponValidForPerson();
            valid_on = couponDetails.getCouponValidOn();
            original_price = couponDetails.getOriginalPrice();
            new_price = couponDetails.getNewPrice();
            quantity = couponDetails.getQuantity();

            holder.coupon_no.setText(String.valueOf(position + 1));
            holder.coupon_title.setText(title);
            holder.coupon_details.setText(description);
            holder.coupon_old_price.setText("₹ " + String.valueOf(original_price));
            holder.coupon_new_preice.setText("₹ " + String.valueOf(new_price));
            holder.coupon_valid_for.setText(valid_for);
            holder.coupon_valid_on.setText(valid_on);
            holder.coupon_details.setText(description);
            holder.coupon_quantity.setText(String.valueOf(quantity));
            holder.coupon_old_price.setPaintFlags(holder.coupon_old_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        private void updateItemQuantity(int quantity, int adapterPosition) {
            couponsDetails.get(adapterPosition).setQuantity(quantity);
            notifyDataSetChanged();
        }

        ArrayList<CouponsDetails> getCouponsDetails() {
            ArrayList<CouponsDetails> arrayList = new ArrayList<>();
            for (int i = 0; i < couponsDetails.size(); i++) {
                CouponsDetails details = couponsDetails.get(i);
                if (details.getIsSelected() == 1) {
                    arrayList.add(details);
                }
            }
            return arrayList;
        }

        @Override
        public int getItemCount() {
            return couponsDetails.size();
        }
    }

    private void requestCallPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CALL_PHONE)) {
            Snackbar.make(parentPanel, "Call Permission Required.", Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(DetailsActivity.this, PERMISSIONS_CALL, REQUEST_CALL);
                }
            }).show();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS_CALL, REQUEST_CALL);
        }
    }
}