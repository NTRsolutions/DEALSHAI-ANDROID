package com.ogma.dealshaiapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.activity.IndexActivity;
import com.ogma.dealshaiapp.activity.MyPurchases;
import com.ogma.dealshaiapp.adapter.MerchantViewAdapter;
import com.ogma.dealshaiapp.fragment.dealshai.FragmentDealshaiPlusPackages;
import com.ogma.dealshaiapp.fragment.plankarle.FragmentPlankarleSelectCategories;
import com.ogma.dealshaiapp.model.CategoryDetails;
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

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

public class FragmentIndex extends Fragment implements View.OnClickListener, IndexActivity.OnBackPressedListener {

    private AutoScrollViewPager banner_view_pager;
    private JSONArray banner;
    //private JSONArray foodie;
    private ArrayList<MerchantDetails> arrayList;
    private ArrayList<CategoryDetails> categoryDetails;
    private JSONArray categories;
    private FragmentManager fragmentManager;
    private BannerAdapter bannerSlidingAdapter;
    private MerchantViewAdapter marchentViewAdapter;
    private LinearLayout dealshai_layout;
    /**private LinearLayout ll_cat1;
    private LinearLayout ll_cat2;
    private LinearLayout ll_cat3;
    private LinearLayout ll_cat4;
    private LinearLayout ll_cat5;*/
    private LinearLayout buffet_deals;
    private LinearLayout private_and_corporate;
    private LinearLayout alacarte;
    private LinearLayout comboDeals;
    private LinearLayout spa_and_salon;
    private LinearLayout events;
    private LinearLayout activities;
    private LinearLayout planKarle;
    private Session session;
    private String latitude;
    private String longitude;
    private String userId;
    private CoordinatorLayout parentPanel;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private AppCompatActivity activity;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_index2, container, false);

        fragmentManager = getChildFragmentManager();
        //foodie = new JSONArray();
        //banner = new JSONArray();
        arrayList = new ArrayList<>();
        Session session = new Session(getContext());

        HashMap<String, String> user = session.getUserDetails();

        userId = user.get(Session.KEY_ID);

        imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited()) {
            imageLoader.init(ImageLoaderConfiguration.createDefault(getContext()));
        }
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.account_icon)
                .cacheInMemory()
                .cacheOnDisc()
                .build();

        parentPanel = view.findViewById(R.id.parentPanel);
        RecyclerView merchantsView = view.findViewById(R.id.recycler_view);
        dealshai_layout=view.findViewById(R.id.dealshai_layout);
        /**ll_cat1 = view.findViewById(R.id.ll_cat1);
        ll_cat2 = view.findViewById(R.id.ll_cat2);
        ll_cat3 = view.findViewById(R.id.ll_cat3);
        ll_cat4 = view.findViewById(R.id.ll_cat4);
        ll_cat5 = view.findViewById(R.id.ll_cat5);*/
        banner_view_pager = view.findViewById(R.id.banner_view_pager);
        buffet_deals = view.findViewById(R.id.buffet_deals);
        private_and_corporate = view.findViewById(R.id.private_and_corporate);
        alacarte = view.findViewById(R.id.alacarte);
        comboDeals = view.findViewById(R.id.comboDeals);
        spa_and_salon = view.findViewById(R.id.spa_and_salon);
        events = view.findViewById(R.id.events);
        activities = view.findViewById(R.id.activities);
        planKarle = view.findViewById(R.id.planKarle);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        merchantsView.setLayoutManager(layoutManager);
        merchantsView.setItemAnimator(new DefaultItemAnimator());
        marchentViewAdapter = new MerchantViewAdapter(getContext(), (AppCompatActivity) getActivity(), arrayList);
        merchantsView.setAdapter(marchentViewAdapter);
        banner_view_pager.setInterval(5000);
        banner_view_pager.startAutoScroll();
        banner_view_pager.setStopScrollWhenTouch(true);
        dealshai_layout.setOnClickListener(this);
        /**ll_cat1.setOnClickListener(this);
        ll_cat2.setOnClickListener(this);
        ll_cat3.setOnClickListener(this);
        ll_cat4.setOnClickListener(this);
        ll_cat5.setOnClickListener(this);*/
        activities.setOnClickListener(this);
        events.setOnClickListener(this);
        buffet_deals.setOnClickListener(this);
        private_and_corporate.setOnClickListener(this);
        comboDeals.setOnClickListener(this);
        alacarte.setOnClickListener(this);
        spa_and_salon.setOnClickListener(this);
        planKarle.setOnClickListener(this);

        ((IndexActivity) getActivity()).setOnBackPressedListener(this);
        banner_view_pager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.equals(MotionEvent.ACTION_DOWN) || event.equals(MotionEvent.ACTION_MOVE)) {
                    banner_view_pager.stopAutoScroll();
                } else
                    banner_view_pager.startAutoScroll();
                return false;
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        session = new Session(getContext());
        HashMap<String, String> locationDetails = session.getLocationDetails();
        latitude = locationDetails.get(Session.KEY_LATITUDE);
        longitude = locationDetails.get(Session.KEY_LONGITUDE);

        NetworkConnection connection = new NetworkConnection(getContext());
        if (connection.isNetworkConnected())
            getData(latitude, longitude);
        else
            Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (AppCompatActivity) context;
    }

    public void getData(String latitude, String longitude) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(getContext());
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                JSONObject main;
                String is_error;
                try {
                    main = new JSONObject(response);
                    is_error = main.getString("is_error");
                    if (is_error == null || Integer.parseInt(is_error) == 1) {
                        Snackbar.make(parentPanel, "No data found", Snackbar.LENGTH_SHORT).show();
                    } else {
                        try {
                            categories = main.getJSONArray("Category");
                            if (categories.length() > 0) {
                                categoryDetails = new ArrayList<>();
                                for (int i = 0; i < categories.length(); i++) {
                                    CategoryDetails details = new CategoryDetails();
                                    JSONObject jsonObject = categories.getJSONObject(i);
                                    details.setCatId(jsonObject.getString("id"));
                                    details.setCatName(jsonObject.getString("name"));
                                    details.setImgUrl(jsonObject.getString("img"));
                                    categoryDetails.add(details);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Snackbar.make(parentPanel, "Categories not found", Snackbar.LENGTH_SHORT).show();
                        }
                        try {
                            banner = main.getJSONArray("Slider");
                            if (banner.length() > 0) {
                                if (activity instanceof IndexActivity) {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            bannerSlidingAdapter = new BannerAdapter(fragmentManager, banner);
                                            banner_view_pager.setAdapter(bannerSlidingAdapter);
                                            banner_view_pager.setStopScrollWhenTouch(true);
                                            banner_view_pager.setOnTouchListener(new View.OnTouchListener(){
                                                @Override
                                                public boolean onTouch(View v, MotionEvent event){
                                                    return false;
                                                }
                                            });
//                                            banner_indicator.setViewPager(banner_view_pager);
                                            //banner_view_pager.setStopScrollWhenTouch(true);
                                            banner_view_pager.setCurrentItem(Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % banner.length());
                                        }
                                    });
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Snackbar.make(parentPanel, "No banner image found", Snackbar.LENGTH_SHORT).show();
                        }
                        arrayList.clear();
                        try {
                            JSONArray merchant = main.getJSONArray("merchant");
                            if (merchant.length() > 0) {
                                for (int i = 0; i < merchant.length(); i++) {
                                    MerchantDetails merchantDetails = new MerchantDetails();
                                    JSONObject object = merchant.getJSONObject(i);
                                    merchantDetails.setMerchantId(object.getString("merchant_id"));
                                    merchantDetails.setTitle(object.getString("title"));
                                    merchantDetails.setArea(object.getString("area"));
                                    merchantDetails.setDiscount(object.getString("discount"));
                                    merchantDetails.setShortDescription(object.getString("short_description"));
                                    merchantDetails.setPrice(object.getString("price"));
                                    merchantDetails.setOfferPrice(object.getString("our_price"));
                                    merchantDetails.setImg(object.getString("img"));
                                    merchantDetails.setLikes(object.getString("like"));
                                    arrayList.add(merchantDetails);
                                }
                                if (activity instanceof IndexActivity) {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            marchentViewAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Snackbar.make(parentPanel, "No merchant data available for this location.", Snackbar.LENGTH_SHORT).show();
                            Snackbar.make(parentPanel, "Please select a city and location.", Snackbar.LENGTH_LONG).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(parentPanel, "Response error!", Snackbar.LENGTH_SHORT).show();
                }
            }
        };
        webServiceHandler.getIndexData(latitude, longitude, userId);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        FragmentManager manager;
        String catId = null;
        Bundle bundle;
        switch (id) {

            case R.id.dealshai_layout:
                FragmentDealshaiPlusPackages fdp = new FragmentDealshaiPlusPackages();
                manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.index_frame, fdp).addToBackStack(null).commitAllowingStateLoss();
                break;
            case R.id.buffet_deals:
                FragmentIndivisualCategories fca = new FragmentIndivisualCategories();
                bundle = new Bundle();
                manager = getFragmentManager();
                bundle.putString("subCategoryId", "60");
                bundle.putString("subCategoryName", "Buffet Special");
                fca.setArguments(bundle);
                manager.beginTransaction().replace(R.id.index_frame, fca).commitAllowingStateLoss();
                break;
            case R.id.private_and_corporate:
                fca = new FragmentIndivisualCategories();
                bundle = new Bundle();
                manager = getFragmentManager();
                bundle.putString("subCategoryId", "59");
                bundle.putString("subCategoryName", "Food Lovers");
                fca.setArguments(bundle);
                manager.beginTransaction().replace(R.id.index_frame, fca).commitAllowingStateLoss();
                break;
            case R.id.alacarte:
                fca = new FragmentIndivisualCategories();
                bundle = new Bundle();
                manager = getFragmentManager();
                bundle.putString("subCategoryId", "54");
                bundle.putString("subCategoryName", "Just Cakes");
                fca.setArguments(bundle);
                manager.beginTransaction().replace(R.id.index_frame, fca).commitAllowingStateLoss();
                break;
            case R.id.comboDeals:
                    bundle = new Bundle();
                    bundle.putString("categoryId", "7");
                    FragmentIndivisualCategories fct1 = new FragmentIndivisualCategories();
                    manager = getFragmentManager();
                    fct1.setArguments(bundle);
                    manager.beginTransaction().replace(R.id.index_frame, fct1).commitAllowingStateLoss();
                break;
            case R.id.spa_and_salon:
                    bundle = new Bundle();
                    bundle.putString("categoryId", "6");
                    fct1 = new FragmentIndivisualCategories();
                    manager = getFragmentManager();
                    fct1.setArguments(bundle);
                    manager.beginTransaction().replace(R.id.index_frame, fct1).commitAllowingStateLoss();
                break;
            case R.id.events:
                    bundle = new Bundle();
                    bundle.putString("categoryId", "8");
                    fct1 = new FragmentIndivisualCategories();
                    manager = getFragmentManager();
                    fct1.setArguments(bundle);
                    manager.beginTransaction().replace(R.id.index_frame, fct1).commitAllowingStateLoss();
                break;
            case R.id.activities:
                    bundle = new Bundle();
                    bundle.putString("categoryId", "3");
                    fct1 = new FragmentIndivisualCategories();
                    manager = getFragmentManager();
                    fct1.setArguments(bundle);
                    manager.beginTransaction().replace(R.id.index_frame, fct1).commitAllowingStateLoss();
                break;
            case R.id.planKarle:
                FragmentPlankarleSelectCategories fsc = new FragmentPlankarleSelectCategories();
                manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.index_frame, fsc).commitAllowingStateLoss();
                break;
        }
    }

    @Override
    public void doBack() {
        if (activity instanceof IndexActivity)
            activity.finish();
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
                return FragmentIndexBanner.newInstance(banner.getJSONObject(position));
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
}