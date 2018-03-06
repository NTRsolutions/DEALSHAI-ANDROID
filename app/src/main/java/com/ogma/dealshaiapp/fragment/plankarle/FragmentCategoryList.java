package com.ogma.dealshaiapp.fragment.plankarle;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.activity.IndexActivity;
import com.ogma.dealshaiapp.dialog.ViewPlan;
import com.ogma.dealshaiapp.model.CategoryDetails;
import com.ogma.dealshaiapp.model.CouponsDetails;
import com.ogma.dealshaiapp.network.NetworkConnection;
import com.ogma.dealshaiapp.network.WebServiceHandler;
import com.ogma.dealshaiapp.network.WebServiceListener;
import com.ogma.dealshaiapp.utility.Session;
import com.ruslankishai.unmaterialtab.tabs.RoundTabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by AndroidDev on 06-10-2017.
 */

public class FragmentCategoryList extends Fragment implements IndexActivity.OnBackPressedListener, View.OnClickListener {

    private CategoriesAdapter adapter;
    private ArrayList<CategoryDetails> arrayList = new ArrayList<>();
    private ViewPager viewPager;
    private RoundTabLayout tabLayout;
    public static ArrayList<CouponsDetails> mainArrayList = new ArrayList<>();
    public static int totalAmount;
    private RelativeLayout parentPanel;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_plankarle_category_items, container, false);

        Session session = new Session(getContext());
        HashMap<String, String> user = session.getUserDetails();
        userId = user.get(Session.KEY_ID);

        FragmentManager fragmentManager = getChildFragmentManager();
        Bundle bundle = getArguments();
        ArrayList<CategoryDetails> categoryList = (ArrayList<CategoryDetails>) bundle.getSerializable("categoryList");

        ((IndexActivity) getActivity()).setOnBackPressedListener(this);

        JSONObject category = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        if (categoryList != null) {
            for (int i = 0; i < categoryList.size(); i++) {
                CategoryDetails categoryDetails = categoryList.get(i);
                String id = categoryDetails.getCatId();
                try {
                    JSONObject main = new JSONObject();
                    main.put("category_id", id);
                    jsonArray.put(i, main);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            category.put("category", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("json", String.valueOf(jsonArray));

        NetworkConnection connection = new NetworkConnection(getContext());
        if (connection.isNetworkConnected()) {
            getData(category);
        }

        parentPanel = view.findViewById(R.id.parentPanel);
        view.findViewById(R.id.rl_view_plan).setOnClickListener(this);

        viewPager = view.findViewById(R.id.view_pager);
        tabLayout = view.findViewById(R.id.round_tabs);
        adapter = new CategoriesAdapter(fragmentManager, arrayList);
        viewPager.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        totalAmount = 0;
        mainArrayList = new ArrayList<>();
    }

    private void getData(JSONObject category) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(getContext());
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
                    try {
                        JSONArray jsonArray = main.getJSONArray("Category");
                        if (jsonArray != null) {
                            arrayList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                CategoryDetails categoryDetails = new CategoryDetails();
                                JSONObject object = jsonArray.getJSONObject(i);
                                categoryDetails.setCatId(object.getString("id"));
                                categoryDetails.setCatName(object.getString("name"));
                                arrayList.add(categoryDetails);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Snackbar.make(parentPanel, "Category details not found.", Snackbar.LENGTH_SHORT).show();
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            tabLayout.setupWithViewPager(viewPager);
                        }
                    });
                }
            }
        }
        ;
        webServiceHandler.getPlankarleLoadTabs(category);
    }

    @Override
    public void doBack() {
        FragmentPlankarleSelectCategories fsc = new FragmentPlankarleSelectCategories();
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction().replace(R.id.index_frame, fsc).commitAllowingStateLoss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_view_plan:
                if (mainArrayList.size() > 0)
                    new ViewPlan((AppCompatActivity) getActivity(), mainArrayList, totalAmount, userId).show();
                else
                    Snackbar.make(parentPanel, "There is no item in your plan.", Snackbar.LENGTH_LONG).show();

                break;
        }
    }

    private class CategoriesAdapter extends FragmentPagerAdapter {
        private FragmentManager fragmentManager;
        private ArrayList<CategoryDetails> arrayList;

        CategoriesAdapter(FragmentManager fragmentManager, ArrayList<CategoryDetails> arrayList) {
            super(fragmentManager);
            this.fragmentManager = fragmentManager;
            this.arrayList = arrayList;
        }

        public void setArrayList(ArrayList<CategoryDetails> arrayList) {
            this.arrayList = arrayList;
        }

        @Override
        public String getPageTitle(int position) {
            CategoryDetails categoryDetails = arrayList.get(position);
            return categoryDetails.getCatName();
        }

        @Override
        public Fragment getItem(int position) {
            CategoryDetails categoryDetails = arrayList.get(position);
            return FragmentSingleCategoryListViewItem.newInstance(categoryDetails.getCatId(), "0");
            /*FragmentSingleCategoryListViewItem fragment = new FragmentSingleCategoryListViewItem();
            Bundle bundle = new Bundle();
            bundle.putString("catId", categoryDetails.getCatId());
            bundle.putString("sortParams", "0");
            fragment.setArguments(bundle);
            return fragment;*/
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }
    }
}
