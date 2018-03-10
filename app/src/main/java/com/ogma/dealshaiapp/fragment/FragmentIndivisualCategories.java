package com.ogma.dealshaiapp.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.activity.IndexActivity;
import com.ogma.dealshaiapp.adapter.MerchantViewAdapter;
import com.ogma.dealshaiapp.dialog.FilterOption;
import com.ogma.dealshaiapp.model.FilterOptions;
import com.ogma.dealshaiapp.model.MerchantDetails;
import com.ogma.dealshaiapp.model.SubCategory;
import com.ogma.dealshaiapp.network.NetworkConnection;
import com.ogma.dealshaiapp.network.WebServiceHandler;
import com.ogma.dealshaiapp.network.WebServiceListener;
import com.ogma.dealshaiapp.utility.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FragmentIndivisualCategories extends Fragment implements IndexActivity.OnBackPressedListener, View.OnClickListener, AdapterView.OnItemSelectedListener {
    private MerchantViewAdapter recyclerViewAdapter;
    private String categoryId;
    private String subCategoryId;
    private int count;
    private ArrayList<FilterOptions> arrayList;
    private ArrayList<MerchantDetails> merchantArrayList;
    private int[] sortIds = null;
    private List<String> sortingParameters;

    private String latitude;
    private String longitude;
    private String sortPram;

    private ArrayAdapter<String> dataAdapter;
    private SwipeRefreshLayout swipe_refresh_layout;
    private CoordinatorLayout parentPanel;
    private FrameLayout error_screen;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_category_deals, container, false);

        if (getActivity() != null) {
            ((IndexActivity) getActivity()).setOnBackPressedListener(this);
        }
        sortPram = "1";
        arrayList = new ArrayList<>();
        merchantArrayList = new ArrayList<>();
        sortingParameters = new ArrayList<>();

        Session session = new Session(getContext());
        HashMap<String, String> locationDetails = session.getLocationDetails();
        latitude = locationDetails.get(Session.KEY_LATITUDE);
        longitude = locationDetails.get(Session.KEY_LONGITUDE);

        error_screen = view.findViewById(R.id.error_screen);

        parentPanel = view.findViewById(R.id.parentPanel);
        Spinner spinner_sort = view.findViewById(R.id.spinner_sort);
        TextView tv_filter = view.findViewById(R.id.tv_filter);
        ImageView iv_filter = view.findViewById(R.id.iv_filter);
        swipe_refresh_layout = view.findViewById(R.id.swipe_refresh_layout);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        Bundle bundle = getArguments();
        if (bundle != null) {
            count = bundle.size();
            if (count == 1)
                categoryId = bundle.getString("categoryId");
            else {
                subCategoryId = bundle.getString("subCategoryId");
            }
        }

        loadData();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerViewAdapter = new MerchantViewAdapter(getContext(), (AppCompatActivity) getActivity(), merchantArrayList);
        recyclerView.setAdapter(recyclerViewAdapter);


        dataAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item_display, sortingParameters);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_sort.setAdapter(dataAdapter);

        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        tv_filter.setOnClickListener(this);
        iv_filter.setOnClickListener(this);
        spinner_sort.setOnItemSelectedListener(this);
        return view;
    }

    private void loadData() {
        NetworkConnection connection = new NetworkConnection(getContext());
        if (connection.isNetworkConnected()) {
            if (count == 2)
                getFoodieData();
            else
                getData();
        }
    }

    private void getFoodieData() {
        WebServiceHandler webServiceHandler = new WebServiceHandler(getContext());
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipe_refresh_layout.setRefreshing(false);
                    }
                });

                String is_error = null;
                JSONObject main = null;
                try {
                    main = new JSONObject(response);
                    is_error = main.getString("is_error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONArray filters = null;
                try {
                    arrayList.clear();
                    if (main != null) {
                        filters = main.getJSONArray("filters");
                    }
                    if (filters != null && filters.length() > 0) {
                        for (int i = 0; i < filters.length(); i++) {
                            FilterOptions filterOptions = new FilterOptions();
                            JSONObject category = filters.getJSONObject(i);
                            filterOptions.setCatId(category.getString("category_id"));
                            filterOptions.setCatName(category.getString("category_name"));
                            JSONArray subcategory = category.getJSONArray("subcategory");
                            ArrayList<SubCategory> subCategoryArrayList = null;
                            if (subcategory.length() > 0) {
                                subCategoryArrayList = new ArrayList<>();
                                for (int j = 0; j < subcategory.length(); j++) {
                                    JSONObject sub = subcategory.getJSONObject(j);
                                    SubCategory subCat = new SubCategory();
                                    subCat.setSubCatId(sub.getString("category_id"));
                                    subCat.setSubCatName(sub.getString("category_name"));
                                    subCategoryArrayList.add(subCat);
                                }
                            }
                            if (subCategoryArrayList != null) {
                                filterOptions.setSubCatArrLst(subCategoryArrayList);
                            }
                            arrayList.add(filterOptions);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(parentPanel, "No filters found for this category.", Snackbar.LENGTH_SHORT).show();
                }
                sortingParameters.clear();
                JSONArray sortParameters = null;
                try {
                    if (main != null) {
                        sortParameters = main.getJSONArray("sort_parameters");
                    }
                    if (sortParameters != null && sortParameters.length() > 0) {
                        sortIds = new int[sortParameters.length()];
                        for (int i = 0; i < sortParameters.length(); i++) {
                            JSONObject object = sortParameters.getJSONObject(i);
                            sortIds[i] = Integer.parseInt(object.getString("id"));
                            sortingParameters.add(object.getString("name"));
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dataAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(parentPanel, "No sorting parameter found for this category.", Snackbar.LENGTH_SHORT).show();
                }
                if (is_error == null || Integer.parseInt(is_error) == 1) {
                    Snackbar.make(parentPanel, "No data found", Snackbar.LENGTH_SHORT).show();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            error_screen.setVisibility(View.VISIBLE);
                        }
                    });
                } else {

                    JSONArray merchant = null;
                    try {
                        merchant = main.getJSONArray("merchant");
                        if (merchant.length() > 0) {
                            merchantArrayList.clear();
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
                                merchantArrayList.add(merchantDetails);
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    error_screen.setVisibility(View.GONE);
                                    recyclerViewAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Snackbar.make(parentPanel, "No merchant found regarding this category.", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        };
        webServiceHandler.getSingleCategoryPackagesFoodie(subCategoryId, latitude, longitude, sortPram);

    }

    private void getData() {
        WebServiceHandler webServiceHandler = new WebServiceHandler(getContext());
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipe_refresh_layout.setRefreshing(false);
                    }
                });

                String is_error = null;
                JSONObject main = null;
                try {
                    main = new JSONObject(response);
                    is_error = main.getString("is_error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONArray filters = null;
                try {
                    arrayList.clear();
                    if (main != null) {
                        filters = main.getJSONArray("filters");
                    }
                    if (filters != null && filters.length() > 0) {
                        for (int i = 0; i < filters.length(); i++) {
                            FilterOptions filterOptions = new FilterOptions();
                            JSONObject category = filters.getJSONObject(i);
                            filterOptions.setCatId(category.getString("category_id"));
                            filterOptions.setCatName(category.getString("category_name"));
                            JSONArray subcategory = category.getJSONArray("subcategory");
                            ArrayList<SubCategory> subCategoryArrayList = null;
                            if (subcategory.length() > 0) {
                                subCategoryArrayList = new ArrayList<>();
                                for (int j = 0; j < subcategory.length(); j++) {
                                    JSONObject sub = subcategory.getJSONObject(j);
                                    SubCategory subCat = new SubCategory();
                                    subCat.setSubCatId(sub.getString("category_id"));
                                    subCat.setSubCatName(sub.getString("category_name"));
                                    subCategoryArrayList.add(subCat);
                                }
                            }
                            if (subCategoryArrayList != null) {
                                filterOptions.setSubCatArrLst(subCategoryArrayList);
                            }
                            arrayList.add(filterOptions);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(parentPanel, "No filters found for this category.", Snackbar.LENGTH_SHORT).show();
                }
                sortingParameters.clear();
                JSONArray sortParameters = null;
                try {
                    if (main != null) {
                        sortParameters = main.getJSONArray("sort_parameters");
                    }
                    if (sortParameters != null && sortParameters.length() > 0) {
                        sortIds = new int[sortParameters.length()];
                        for (int i = 0; i < sortParameters.length(); i++) {
                            JSONObject object = sortParameters.getJSONObject(i);
                            sortIds[i] = Integer.parseInt(object.getString("id"));
                            sortingParameters.add(object.getString("name"));
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dataAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(parentPanel, "No sorting parameter found for this category.", Snackbar.LENGTH_SHORT).show();
                }
                if (is_error == null || Integer.parseInt(is_error) == 1) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            error_screen.setVisibility(View.VISIBLE);
                        }
                    });
                    Snackbar.make(parentPanel, "No data found", Snackbar.LENGTH_SHORT).show();
                } else {
                    JSONArray merchant = null;

                    try {
                        merchant = main.getJSONArray("merchant");
                        if (merchant.length() > 0) {
                            merchantArrayList.clear();
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
                                merchantArrayList.add(merchantDetails);
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerViewAdapter.notifyDataSetChanged();
                                    error_screen.setVisibility(View.GONE);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Snackbar.make(parentPanel, "No merchant found regarding this category.", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        };
        webServiceHandler.getSingleCategoryPackages(categoryId, latitude, longitude, sortPram);
    }

    @Override
    public void doBack() {
        FragmentIndex fca = new FragmentIndex();
        FragmentManager manager = getActivity().getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.index_frame, fca).commitAllowingStateLoss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_filter:
            case R.id.iv_filter:
                if (arrayList != null && arrayList.size() > 0)
                    if (getActivity() != null) {
                        new FilterOption((AppCompatActivity) getActivity(), arrayList).show();
                    }
                break;

            case R.id.iv_sort:
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        sortPram = String.valueOf(sortIds[position]);
//        loadData();
        switch (sortIds[position]) {
            case 1:
                Collections.sort(merchantArrayList, MerchantDetails.sortLowToHigh);
                break;
            case 2:
                Collections.sort(merchantArrayList, MerchantDetails.sortHighToLow);
                break;
            default:
                Collections.sort(merchantArrayList, MerchantDetails.sortLowToHigh);
                break;
        }
        recyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}