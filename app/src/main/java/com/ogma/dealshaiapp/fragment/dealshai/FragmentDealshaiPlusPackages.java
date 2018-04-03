package com.ogma.dealshaiapp.fragment.dealshai;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.activity.IndexActivity;
import com.ogma.dealshaiapp.fragment.FragmentIndex;
import com.ogma.dealshaiapp.model.PackageDetails;
import com.ogma.dealshaiapp.model.SingleDealsDetails;
import com.ogma.dealshaiapp.network.NetworkConnection;
import com.ogma.dealshaiapp.network.WebServiceHandler;
import com.ogma.dealshaiapp.network.WebServiceListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by AndroidDev on 31-10-2017.
 */

public class FragmentDealshaiPlusPackages extends Fragment implements IndexActivity.OnBackPressedListener {

    private String packageId;
    private String price;
    private String offerPrice;
    private ArrayList<PackageDetails> packagesDetails;
    private RecyclerView recycler_view;
    private PackagesListAdapter adapter;
    private ExpandableListView dealshai_packages;
    private PackagesListAdapter packagesListAdapter;
    private SwipeRefreshLayout swipe_refresh_layout;
    private CoordinatorLayout parentPanel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_dealshai_plus, container, false);

        ((IndexActivity) getActivity()).setOnBackPressedListener(this);

        packagesDetails = new ArrayList<>();

        parentPanel = view.findViewById(R.id.parentPanel);

        dealshai_packages = view.findViewById(R.id.dealshai_packages);
        dealshai_packages.setGroupIndicator(null);
        dealshai_packages.setChildIndicator(null);

        swipe_refresh_layout = view.findViewById(R.id.swipe_refresh_layout);
        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onResume();
            }
        });

        packagesListAdapter = new PackagesListAdapter(getContext(), packagesDetails);
        dealshai_packages.setAdapter(packagesListAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        NetworkConnection connection = new NetworkConnection(getActivity());
        if (connection.isNetworkConnected()) {
            getData();
        } else
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
    }

    private void getData() {
        WebServiceHandler webServiceHandler = new WebServiceHandler(getActivity());
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                JSONObject main = null;
                String is_error = null;
                try {
                    main = new JSONObject(response);
                    is_error = main.getString("is_error");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(parentPanel, "Response Error!", Snackbar.LENGTH_SHORT).show();
                }
                if (is_error == null || Integer.parseInt(is_error) == 1) {
                    Snackbar.make(parentPanel, "No data found", Snackbar.LENGTH_SHORT).show();
                } else {
                    JSONArray packages = null;
                    try {
                        packages = main.getJSONArray("package");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Snackbar.make(parentPanel, "No packages found", Snackbar.LENGTH_SHORT).show();
                    }

                    if (packages != null && packages.length() > 0) {
                        try {
                            packagesDetails.clear();
                            for (int i = 0; i < packages.length(); i++) {
                                PackageDetails packageDetails = new PackageDetails();
                                JSONObject jsonObject = packages.getJSONObject(i);
                                packageId = jsonObject.getString("id");
                                price = jsonObject.getString("price");
                                offerPrice = jsonObject.getString("our_price");
                                JSONArray deals = jsonObject.getJSONArray("deals");
                                ArrayList<SingleDealsDetails> dealsDetails = new ArrayList<>();
                                for (int j = 0; j < deals.length(); j++) {
                                    JSONObject deal = deals.getJSONObject(j);
                                    SingleDealsDetails singleDealsDetails = new SingleDealsDetails();
                                    String img = deal.getString("img");
                                    String title = deal.getString("title");
                                    String city = deal.getString("area");
                                    String description = deal.getString("description");
                                    String qty = deal.getString("qty");

                                    singleDealsDetails.setTitle(title);
                                    singleDealsDetails.setCity(city);
                                    singleDealsDetails.setDescription(description);
                                    singleDealsDetails.setImg(img);
                                    singleDealsDetails.setQuantty(qty);
                                    dealsDetails.add(singleDealsDetails);
                                }
                                packageDetails.setPackageId(packageId);
                                packageDetails.setPrice(price);
                                packageDetails.setOfferPrice(offerPrice);
                                packageDetails.setDealsDetails(dealsDetails);
                                packagesDetails.add(packageDetails);
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    packagesListAdapter.notifyDataSetChanged();
                                    swipe_refresh_layout.setRefreshing(false);
                                }
                            });


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Snackbar.make(parentPanel, "Package's data missing.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        };
        webServiceHandler.getDealshaiData();
    }

    @Override
    public void doBack() {
        FragmentIndex fca = new FragmentIndex();
        FragmentManager manager = getActivity().getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.index_frame, fca).commitAllowingStateLoss();
    }

    public class PackagesListAdapter extends BaseExpandableListAdapter {

        private ArrayList<PackageDetails> packagesDetails;
        private Context context;
        private ImageLoader imageLoader;
        private DisplayImageOptions options;
        private ExpandableListView expandableListView;


        PackagesListAdapter(Context context, ArrayList<PackageDetails> scheduleClassGroupsArrayList) {
            this.packagesDetails = scheduleClassGroupsArrayList;
            this.context = context;
        }

        @Override
        public int getGroupCount() {
            return packagesDetails.size();
        }


        @Override
        public int getChildrenCount(int groupPosition) {
            return packagesDetails.get(groupPosition).getDealsDetails().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return packagesDetails.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return packagesDetails.get(groupPosition).getDealsDetails().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            View v = convertView;
            ViewHolderGroup mHolder;
            expandableListView = (ExpandableListView) parent;
            expandableListView.expandGroup(groupPosition);

            if (v == null) {
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if (layoutInflater != null) {
                    v = layoutInflater.inflate(R.layout.package_group_view, parent, false);
                }
                expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                        expandableListView.expandGroup(groupPosition);
                        return true;
                    }
                });

                mHolder = new ViewHolderGroup();
                mHolder.tv_tittle = v.findViewById(R.id.tv_title);
                mHolder.tv_old_price = v.findViewById(R.id.tv_old_price);
                mHolder.tv_new_price = v.findViewById(R.id.tv_new_price);
                mHolder.view1 = v.findViewById(R.id.view1);
                mHolder.view2 = v.findViewById(R.id.view2);
                v.setTag(mHolder);
            } else
                mHolder = (ViewHolderGroup) v.getTag();

            PackageDetails packageDetails = (PackageDetails) getGroup(groupPosition);

            String groupTittle = "DealsHai+";
            String price = packageDetails.getPrice();
            String offerPrice = packageDetails.getOfferPrice();

            mHolder.tv_tittle.setText(groupTittle);
            mHolder.tv_new_price.setText("₹" + offerPrice);
            mHolder.tv_old_price.setText("₹" + price);
            mHolder.tv_old_price.setPaintFlags(mHolder.tv_old_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            if (groupPosition == 0) {
                mHolder.view1.setVisibility(View.GONE);
                mHolder.view2.setVisibility(View.GONE);
            } else {
                mHolder.view1.setVisibility(View.VISIBLE);
                mHolder.view2.setVisibility(View.VISIBLE);
            }

            return v;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            View v = convertView;
            ViewHolderChild mHolder;

            if (v == null) {
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = layoutInflater.inflate(R.layout.activity_packages_deal_item, parent, false);
                mHolder = new ViewHolderChild();
                mHolder.index_banner = v.findViewById(R.id.index_banner);
                mHolder.tv_title = v.findViewById(R.id.tv_title);
                mHolder.tv_address = v.findViewById(R.id.tv_address);
                mHolder.tv_description = v.findViewById(R.id.tv_description);
                mHolder.qty = v.findViewById(R.id.qty);
                v.setTag(mHolder);
            } else
                mHolder = (ViewHolderChild) v.getTag();

            SingleDealsDetails child = (SingleDealsDetails) getChild(groupPosition, childPosition);

            imageLoader = ImageLoader.getInstance();
            if (!imageLoader.isInited()) {
                imageLoader.init(ImageLoaderConfiguration.createDefault(getContext()));
            }
            options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .build();
            imageLoader.displayImage(child.getImg(), mHolder.index_banner, options);

            mHolder.tv_title.setText(child.getTitle());
            mHolder.tv_address.setText(" " + child.getCity());
            mHolder.tv_description.setText(child.getDescription());
            mHolder.qty.setText(child.getQuantty());

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String packageId = null;
                    if (packagesDetails.size() != 0) {
                        PackageDetails packageDetails = packagesDetails.get(groupPosition);
                        packageId = packageDetails.getPackageId();
                    }

                    if (packageId != null) {
                        FragmentDealshaiPlusPackageDetails fpd = new FragmentDealshaiPlusPackageDetails();
                        FragmentManager manager = getActivity().getSupportFragmentManager();
                        Bundle bundle = new Bundle();
                        bundle.putString("packageId", packageId);
                        fpd.setArguments(bundle);
                        manager.beginTransaction().replace(R.id.index_frame, fpd).commitAllowingStateLoss();
                    }
                }
            });
            return v;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        private class ViewHolderGroup {
            TextView tv_tittle;
            TextView tv_old_price;
            TextView tv_new_price;
            View view1;
            View view2;

        }

        private class ViewHolderChild {
            ImageView index_banner;
            TextView tv_title;
            TextView tv_address;
            TextView tv_description;
            TextView qty;
        }
    }
}
