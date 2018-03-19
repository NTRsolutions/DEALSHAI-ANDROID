package com.ogma.dealshaiapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import me.relex.circleindicator.CircleIndicator;

public class FragmentIndex extends Fragment implements View.OnClickListener, IndexActivity.OnBackPressedListener {

    private AutoScrollViewPager banner_view_pager;
    private ViewPager funtime_view_pager;
    private ViewPager shopping_view_pager;
    private JSONArray banner;
    private JSONArray foodie;
    private ArrayList<MerchantDetails> arrayList;
    private JSONArray shopping;
    private FragmentManager fragmentManager;

    private CircleIndicator fun_time_indicator;
    private CircleIndicator shopping_indicator;
    private CircleIndicator banner_indicator;
    private FoodieAdapter recyclerViewAdapter;
    private MerchantViewAdapter marchentViewAdapter;
    private BannerAdapter bannerSlidingAdapter;
    private int currentPage;
    private SwipeRefreshLayout swipe_refresh_layout;
    private ImageView iv_cat1;
    private ImageView iv_cat2;
    private ImageView iv_cat3;
    private ImageView iv_cat4;
    private TextView tv_cat1;
    private TextView tv_cat2;
    private TextView tv_cat3;
    private TextView tv_cat4;
    private RelativeLayout ll_cat1;
    private RelativeLayout ll_cat2;
    private RelativeLayout ll_cat3;
    private RelativeLayout ll_cat4;
    private String categoryId = "3";
    private JSONArray categories;
    private ArrayList<CategoryDetails> categoryDetails;
    private Session session;
    private String latitude;
    private String longitude;
    private String userId;
    private CoordinatorLayout parentPanel;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_index, container, false);

        fragmentManager = getChildFragmentManager();
        foodie = new JSONArray();
        banner = new JSONArray();
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
        LinearLayout ll_plankarle = view.findViewById(R.id.ll_plankarle);
        LinearLayout ll_dealshai = view.findViewById(R.id.ll_dealshai);
        LinearLayout ll_more = view.findViewById(R.id.ll_more);
        banner_view_pager = view.findViewById(R.id.banner_view_pager);
        RecyclerView foodieSubView = view.findViewById(R.id.cat_foodie);
        RecyclerView merchantsView = view.findViewById(R.id.recycler_view);
        banner_indicator = view.findViewById(R.id.banner_indicator);
        iv_cat1 = view.findViewById(R.id.iv_cat1);
        iv_cat2 = view.findViewById(R.id.iv_cat2);
        iv_cat3 = view.findViewById(R.id.iv_cat3);
        iv_cat4 = view.findViewById(R.id.iv_cat4);

        tv_cat1 = view.findViewById(R.id.tv_cat1);
        tv_cat2 = view.findViewById(R.id.tv_cat2);
        tv_cat3 = view.findViewById(R.id.tv_cat3);
        tv_cat4 = view.findViewById(R.id.tv_cat4);

        ll_cat1 = view.findViewById(R.id.ll_cat1);
        ll_cat2 = view.findViewById(R.id.ll_cat2);
        ll_cat3 = view.findViewById(R.id.ll_cat3);
        ll_cat4 = view.findViewById(R.id.ll_cat4);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        foodieSubView.setLayoutManager(mLayoutManager);
        foodieSubView.setItemAnimator(new DefaultItemAnimator());
        recyclerViewAdapter = new FoodieAdapter(getContext(), foodie);
        foodieSubView.setAdapter(recyclerViewAdapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        merchantsView.setLayoutManager(layoutManager);
        merchantsView.setItemAnimator(new DefaultItemAnimator());
        marchentViewAdapter = new MerchantViewAdapter(getContext(), (AppCompatActivity) getActivity(), arrayList);
        merchantsView.setAdapter(marchentViewAdapter);

        banner_view_pager.setInterval(3000);
        banner_view_pager.startAutoScroll();

        ll_plankarle.setOnClickListener(this);
        ll_dealshai.setOnClickListener(this);
        ll_cat1.setOnClickListener(this);
        ll_cat2.setOnClickListener(this);
        ll_cat3.setOnClickListener(this);
        ll_cat4.setOnClickListener(this);
        ll_more.setOnClickListener(this);

        ((IndexActivity) getActivity()).setOnBackPressedListener(this);

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

    public void getData(String latitude, String longitude) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(getContext());
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                JSONObject main = null;
                String is_error = null;
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
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (categoryDetails.size() > 0) {
                                            CategoryDetails cat1 = categoryDetails.get(0);
                                            CategoryDetails cat2 = categoryDetails.get(1);
                                            CategoryDetails cat3 = categoryDetails.get(2);
                                            CategoryDetails cat4 = categoryDetails.get(3);

                                            tv_cat1.setText(cat1.getCatName());
                                            tv_cat2.setText(cat2.getCatName());
                                            tv_cat3.setText(cat3.getCatName());
                                            tv_cat4.setText(cat4.getCatName());

                                            imageLoader.displayImage(cat1.getImgUrl(), iv_cat1, options);
                                            imageLoader.displayImage(cat2.getImgUrl(), iv_cat2, options);
                                            imageLoader.displayImage(cat3.getImgUrl(), iv_cat3, options);
                                            imageLoader.displayImage(cat4.getImgUrl(), iv_cat4, options);


                                        }
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Snackbar.make(parentPanel, "Categories not found", Snackbar.LENGTH_SHORT).show();
                        }
                        try {
                            foodie = main.getJSONArray("Foodie");
                            if (foodie.length() > 0) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        recyclerViewAdapter.setArray(foodie);
                                        recyclerViewAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Snackbar.make(parentPanel, "Foodie subcategories not found", Snackbar.LENGTH_SHORT).show();
                        }
                        try {
                            banner = main.getJSONArray("Slider");
                            if (banner.length() > 0) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        bannerSlidingAdapter = new BannerAdapter(fragmentManager, banner);
                                        banner_view_pager.setAdapter(bannerSlidingAdapter);
                                        banner_indicator.setViewPager(banner_view_pager);
                                        banner_view_pager.setCurrentItem(Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % banner.length());
                                        banner_view_pager.setStopScrollWhenTouch(true);
                                    }
                                });
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
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        marchentViewAdapter.notifyDataSetChanged();
                                    }
                                });
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
            case R.id.ll_plankarle:
                FragmentPlankarleSelectCategories fsc = new FragmentPlankarleSelectCategories();
                manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.index_frame, fsc).commitAllowingStateLoss();
                break;
            case R.id.ll_dealshai:
                FragmentDealshaiPlusPackages fdp = new FragmentDealshaiPlusPackages();
                manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.index_frame, fdp).addToBackStack(null).commitAllowingStateLoss();
                break;

            case R.id.ll_cat1:
                if (categoryDetails.size() > 0) {
                    CategoryDetails category = categoryDetails.get(0);
                    catId = category.getCatId();
                }
                if (catId != null) {
                    bundle = new Bundle();
                    bundle.putString("categoryId", catId);
                    FragmentIndivisualCategories fct1 = new FragmentIndivisualCategories();
                    manager = getFragmentManager();
                    fct1.setArguments(bundle);
                    manager.beginTransaction().replace(R.id.index_frame, fct1).commitAllowingStateLoss();
                }
                break;

            case R.id.ll_cat2:
                if (categoryDetails.size() > 0) {
                    CategoryDetails category = categoryDetails.get(1);
                    catId = category.getCatId();
                }
                if (catId != null) {
                    bundle = new Bundle();
                    bundle.putString("categoryId", catId);
                    FragmentIndivisualCategories fct1 = new FragmentIndivisualCategories();
                    manager = getFragmentManager();
                    fct1.setArguments(bundle);
                    manager.beginTransaction().replace(R.id.index_frame, fct1).commitAllowingStateLoss();
                }
                break;
            case R.id.ll_cat3:
                if (categoryDetails.size() > 0) {
                    CategoryDetails category = categoryDetails.get(2);
                    catId = category.getCatId();
                }
                if (catId != null) {
                    bundle = new Bundle();
                    bundle.putString("categoryId", catId);
                    FragmentIndivisualCategories fct1 = new FragmentIndivisualCategories();
                    manager = getFragmentManager();
                    fct1.setArguments(bundle);
                    manager.beginTransaction().replace(R.id.index_frame, fct1).commitAllowingStateLoss();
                }
                break;
            case R.id.ll_cat4:
                if (categoryDetails.size() > 0) {
                    CategoryDetails category = categoryDetails.get(3);
                    catId = category.getCatId();
                }
                if (catId != null) {
                    bundle = new Bundle();
                    bundle.putString("categoryId", catId);
                    FragmentIndivisualCategories fct1 = new FragmentIndivisualCategories();
                    manager = getFragmentManager();
                    fct1.setArguments(bundle);
                    manager.beginTransaction().replace(R.id.index_frame, fct1).commitAllowingStateLoss();
                }
                break;
            case R.id.ll_more:
                FragmentMoreCategories fmc = new FragmentMoreCategories();
                manager = getFragmentManager();
                bundle = new Bundle();
                bundle.putSerializable("categoryList", categoryDetails);
                fmc.setArguments(bundle);
                manager.beginTransaction().replace(R.id.index_frame, fmc).commitAllowingStateLoss();
                break;
        }
    }

    @Override
    public void doBack() {
        getActivity().finish();
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

    private class FoodieAdapter extends RecyclerView.Adapter<FoodieAdapter.ViewHolder> {
        private JSONArray foodie;
        private Context context;
        private String id;
        private String title;
        private ImageLoader imageLoader;
        private DisplayImageOptions options;

        FoodieAdapter(Context context, JSONArray foodie) {
            this.foodie = foodie;
            this.context = context;

            imageLoader = ImageLoader.getInstance();
            if (!imageLoader.isInited()) {
                imageLoader.init(ImageLoaderConfiguration.createDefault(context));
            }
            options = new DisplayImageOptions.Builder()
                    .showStubImage(R.drawable.account_icon)
                    .cacheInMemory()
                    .cacheOnDisc()
                    .build();
        }

        private void setArray(JSONArray foodie) {
            this.foodie = foodie;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView cat_img;
            private TextView cat_title;
            private TextView cat_range;

            ViewHolder(View itemView) {
                super(itemView);
                cat_img = itemView.findViewById(R.id.cat_img);
                cat_title = itemView.findViewById(R.id.cat_title);
                cat_range = itemView.findViewById(R.id.cat_range);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentIndivisualCategories fca = new FragmentIndivisualCategories();
                        FragmentManager manager = getFragmentManager();
                        Bundle bundle = new Bundle();
                        JSONObject object = null;
                        try {
                            object = foodie.getJSONObject(getAdapterPosition());
                            id = object.getString("id");
                            title = object.getString("name");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        bundle.putString("subCategoryId", id);
                        bundle.putString("subCategoryName", title);
                        fca.setArguments(bundle);
                        manager.beginTransaction().replace(R.id.index_frame, fca).commitAllowingStateLoss();
                    }
                });
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.index_page_foodie, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            String imgUri = "";
            title = "";
            String range = "";

            try {
                JSONObject object = foodie.getJSONObject(position);
                id = object.getString("id");
                imgUri = object.getString("img");
                title = object.getString("name");
                range = object.getString("range");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            imageLoader.displayImage(imgUri, holder.cat_img, options);

            holder.cat_title.setText(title);
            holder.cat_range.setText(range);
        }

        @Override
        public int getItemCount() {
            return foodie.length();
        }
    }
}