package com.ogma.dealshaiapp.fragment.plankarle;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.adapter.PlankarleAddToPlanAdapter;
import com.ogma.dealshaiapp.model.MerchantDetails;
import com.ogma.dealshaiapp.model.SubCategory;
import com.ogma.dealshaiapp.network.NetworkConnection;
import com.ogma.dealshaiapp.network.WebServiceHandler;
import com.ogma.dealshaiapp.network.WebServiceListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by AndroidDev on 06-10-2017.
 */

public class FragmentSingleCategoryListViewItem extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private String catId;
    private String subCatId;
    private String sortParams;
    private ArrayList<MerchantDetails> merchantArrayList = new ArrayList<>();
    private ArrayList<SubCategory> filterList = new ArrayList<>();
    private List<String> sortingParameters = new ArrayList<>();
    private List<String> filterParameters = new ArrayList<>();
    private int[] sortIds;
    private int[] filterIds;
    private PlankarleAddToPlanAdapter recyclerViewAdapter;
    private ArrayAdapter<String> sortSpinnerAdapter;
    private ArrayAdapter<String> filterSpinnerAdapter;
    private SwipeRefreshLayout swipe_refresh_layout;
    private LinearLayout ll_filter;
    private EditText et_search_bar;
    private String searchText;
    private boolean hasLoadOnce = false;


    public static FragmentSingleCategoryListViewItem newInstance(String catId, String sortParams) {
        FragmentSingleCategoryListViewItem fragment = new FragmentSingleCategoryListViewItem();
        Bundle bundle = new Bundle();
        bundle.putString("catId", catId);
        bundle.putString("sortParams", "0");
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_plankarle_single_category_view, container, false);

        Bundle bundle = getArguments();
        catId = bundle.getString("catId");
        sortParams = bundle.getString("sortParams");

        et_search_bar = view.findViewById(R.id.et_search_bar);
        ImageView iv_search = view.findViewById(R.id.iv_search);
        ll_filter = view.findViewById(R.id.ll_filter);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        swipe_refresh_layout = view.findViewById(R.id.swipe_refresh_layout);
        Spinner spinner_sort = view.findViewById(R.id.spinner_sort);
        Spinner spinner_filter = view.findViewById(R.id.spinner_filter);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerViewAdapter = new PlankarleAddToPlanAdapter(getContext(), (AppCompatActivity) getActivity(), merchantArrayList);
        recyclerView.setAdapter(recyclerViewAdapter);

        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                NetworkConnection connection = new NetworkConnection(getContext());
                if (connection.isNetworkConnected()) {
                    searchText = et_search_bar.getText().toString().trim();
                    loadData(catId, subCatId, searchText);
                }
            }
        });

        sortSpinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, sortingParameters);
        sortSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_sort.setAdapter(sortSpinnerAdapter);

        filterSpinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, filterParameters);
        filterSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_filter.setAdapter(filterSpinnerAdapter);

        spinner_sort.setOnItemSelectedListener(this);
        spinner_filter.setOnItemSelectedListener(this);
        iv_search.setOnClickListener(this);

        searchText = et_search_bar.getText().toString().trim();
        loadData(catId, subCatId, searchText);

        return view;
    }

    private void loadData(String catId, String subCatId, String searchText) {
        NetworkConnection connection = new NetworkConnection(getContext());
        if (connection.isNetworkConnected()) {
            getData(catId, subCatId, searchText);
        }
    }

    private void getData(String catId, String subCatId, String searchText) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(getContext());
        webServiceHandler.serviceListener = new WebServiceListener() {

            @Override
            public void onResponse(String response) {
                JSONObject main = null;
                try {
                    main = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (main != null) {
                    try {
                        filterParameters.clear();
                        JSONArray filters = null;

                        filters = main.getJSONArray("sub_category");
                        if (filters != null && filters.length() != 0) {
                            filterIds = new int[filters.length() + 1];
                            filterIds[0] = 0;
                            filterParameters.add("Filters");
                            for (int i = 0; i < filters.length(); i++) {
                                JSONObject object = filters.getJSONObject(i);
                                filterIds[i + 1] = Integer.parseInt(object.getString("id"));
                                filterParameters.add(object.getString("name"));
                            }
                        } else

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_filter.setVisibility(View.GONE);
                                    filterSpinnerAdapter.notifyDataSetChanged();
                                }
                            });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    sortingParameters.clear();
                    try {
                        JSONArray sortParameters = main.getJSONArray("sort_parameters");
                        if (sortParameters.length() != 0) {
                            sortIds = new int[sortParameters.length()];
                            for (int i = 0; i < sortParameters.length(); i++) {
                                JSONObject object = sortParameters.getJSONObject(i);
                                sortIds[i] = Integer.parseInt(object.getString("id"));
                                sortingParameters.add(object.getString("name"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    merchantArrayList.clear();
                    try {
                        JSONArray merchant = main.getJSONArray("merchant");
                        if (merchant != null && merchant.length() > 0) {
                            for (int i = 0; i < merchant.length(); i++) {
                                MerchantDetails merchantDetails = new MerchantDetails();
                                JSONObject object = merchant.getJSONObject(i);
                                merchantDetails.setMerchantId(object.getString("merchant_id"));
                                merchantDetails.setTitle(object.getString("merchant_name"));
                                merchantDetails.setArea(object.getString("area_name"));
                                merchantDetails.setDiscount(object.getString("discount"));
                                merchantDetails.setShortDescription(object.getString("coupons_title"));
                                merchantDetails.setPrice(object.getString("price"));
                                merchantDetails.setOfferPrice(object.getString("our_price"));
                                merchantDetails.setImg(object.getString("img"));
                                merchantDetails.setLikes(object.getString("like"));
                                merchantArrayList.add(merchantDetails);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerViewAdapter.notifyDataSetChanged();
                        sortSpinnerAdapter.notifyDataSetChanged();
                        filterSpinnerAdapter.notifyDataSetChanged();
                        swipe_refresh_layout.setRefreshing(false);
                        et_search_bar.setText("");
                        et_search_bar.setCursorVisible(false);
                    }
                });
            }
        };
        webServiceHandler.getPlankarleLoadDeals(catId, subCatId, searchText);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_filter:
                if (filterList != null && filterList.size() > 0)
//                    new FilterOption((AppCompatActivity) getActivity(), filterList).show();
                    break;
            case R.id.iv_search:
                searchText = et_search_bar.getText().toString().trim();
                loadData(catId, subCatId, searchText);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner_sort:
                switch (sortIds[position]) {
                    case 0:
                        Collections.sort(merchantArrayList, MerchantDetails.sortLowToHigh);
                        break;
                    case 1:
                        Collections.sort(merchantArrayList, MerchantDetails.sortHighToLow);
                        break;
                    default:
                        Collections.sort(merchantArrayList, MerchantDetails.sortLowToHigh);
                        break;
                }
                recyclerViewAdapter.notifyDataSetChanged();
                break;
            case R.id.spinner_filter:
                if (filterIds[position] > 0) {
                    subCatId = String.valueOf(filterIds[position]);
                    searchText = et_search_bar.getText().toString().trim();
                    loadData(catId, subCatId, searchText);
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible()) {
            if (isVisibleToUser) {
                /*searchText = et_search_bar.getText().toString().trim();
                loadData(catId, subCatId, searchText);*/
                hasLoadOnce = true;
                Log.e("abcs", "ffffffff");
            }

        }
    }
}
