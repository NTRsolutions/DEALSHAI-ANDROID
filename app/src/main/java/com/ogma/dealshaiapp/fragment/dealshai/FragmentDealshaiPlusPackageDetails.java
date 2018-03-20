package com.ogma.dealshaiapp.fragment.dealshai;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.activity.CheckOutActivity;
import com.ogma.dealshaiapp.activity.IndexActivity;
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

/**
 * Created by AndroidDev on 31-10-2017.
 */

public class FragmentDealshaiPlusPackageDetails extends Fragment implements IndexActivity.OnBackPressedListener, View.OnClickListener {

    private String packageId;
    private RecyclerView recycler_view;
    private JSONArray packageItems;
    private PackageDetailsAdapter adapter;
    private TextView tv_total_amount;
    private TextView tv_title;
    private TextView tv_btn_buy_now;
    private ArrayList<CouponsDetails> arrayList;
    private String totalPrice;
    private String pageTitle;
    private RelativeLayout parentPanel;
    private boolean flag;
    private String userId;
    private AlertDialog alertDialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_dealshai_plus_details, container, false);

        Session session = new Session(getContext());
        HashMap<String, String> user = session.getUserDetails();
        userId = user.get(Session.KEY_ID);

        packageItems = new JSONArray();
        arrayList = new ArrayList<CouponsDetails>();

        ((IndexActivity) getActivity()).setOnBackPressedListener(this);

        Bundle bundle = getArguments();
        if (bundle != null)
            packageId = bundle.getString("packageId");

        tv_total_amount = view.findViewById(R.id.tv_total_amount);
        tv_btn_buy_now = view.findViewById(R.id.tv_btn_buy_now);
        tv_title = view.findViewById(R.id.tv_title);

        parentPanel = view.findViewById(R.id.parentPanel);
        recycler_view = view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler_view.setLayoutManager(layoutManager);

        DividerItemDecoration itemDecorator = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.divider));

//        recycler_view.addItemDecoration(itemDecorator);

        tv_btn_buy_now.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        NetworkConnection connection = new NetworkConnection(getActivity());
        if (connection.isNetworkConnected()) {
            getData(packageId);
        } else
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
    }

    private void getData(final String packageId) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(getActivity());
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                JSONObject main = null;
                String is_error = null;
                try {
                    main = new JSONObject(response);
                    is_error = main.getString("err");
                    totalPrice = main.getString("price");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(parentPanel, "Response error!", Snackbar.LENGTH_LONG).show();
                }

                if (is_error == null || Integer.parseInt(is_error) == 1) {
                    Snackbar.make(parentPanel, "No data found", Snackbar.LENGTH_SHORT).show();
                } else {

                    try {
                        packageItems = main.getJSONArray("package_item");
                        if (packageItems.length() > 0) {
                            arrayList.clear();
                            for (int i = 0; i < packageItems.length(); i++) {
                                CouponsDetails couponsDetails = new CouponsDetails();
                                JSONObject object = packageItems.getJSONObject(i);
                                couponsDetails.setMerchantId(object.getString("merchant_id"));
                                couponsDetails.setCouponId(object.getString("cupon_id"));
                                couponsDetails.setQuantity(object.getInt("qty"));
                                arrayList.add(couponsDetails);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_total_amount.setText("₹  " + totalPrice);
                            if (packageItems.length() != 0) {
                                adapter = new PackageDetailsAdapter(getActivity(), getContext(), packageItems);
                                recycler_view.setAdapter(adapter);
                            }
                        }
                    });
                }
            }
        };
        webServiceHandler.getDealshaiPlusDetails(packageId);
    }

    @Override
    public void doBack() {
        FragmentDealshaiPlusPackages fca = new FragmentDealshaiPlusPackages();
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction().replace(R.id.index_frame, fca).commitAllowingStateLoss();
    }

    @Override
    public void onClick(View v) {

        startActivity(new Intent(getContext(), CheckOutActivity.class)
                .putExtra("flag", "DealshaiPlus")
                .putExtra("couponList", arrayList)
                .putExtra("totalAmount", String.valueOf(totalPrice)));
    }

    private class PackageDetailsAdapter extends RecyclerView.Adapter<PackageDetailsAdapter.ViewHolder> {
        private Activity activity;
        private Context context;
        private JSONArray packageItems;
        private DisplayImageOptions options;
        private ImageLoader imageLoader;

        PackageDetailsAdapter(Activity activity, Context context, JSONArray packageItems) {
            this.activity = activity;
            this.context = context;
            this.packageItems = packageItems;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView index_banner;
            TextView tv_title;
            TextView tv_merchant_name;
            TextView tv_category_name;
            TextView tv_valid_for;
            TextView tv_valid_on;
            TextView tv_t_and_c;
            TextView qty;
            TextView tv_view_more;

            ViewHolder(View itemView) {
                super(itemView);
                tv_title = itemView.findViewById(R.id.tv_title);
                tv_merchant_name = itemView.findViewById(R.id.tv_merchant_name);
                tv_category_name = itemView.findViewById(R.id.tv_category_name);
                tv_valid_for = itemView.findViewById(R.id.tv_valid_for);
                tv_valid_on = itemView.findViewById(R.id.tv_valid_on);
                tv_t_and_c = itemView.findViewById(R.id.tv_t_and_c);
                index_banner = itemView.findViewById(R.id.index_banner);
                qty = itemView.findViewById(R.id.qty);
                tv_view_more = itemView.findViewById(R.id.tv_view_more);

                tv_view_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String merchantName = "";
                        String condition = "";

                        try {
                            JSONObject object = packageItems.getJSONObject(getAdapterPosition());
                            merchantName = object.getString("merchant_name");
                            condition = object.getString("condition");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        View view = getLayoutInflater().inflate(R.layout.view_t_and_c, null, false);
                        alertDialogBuilder.setView(view);
                        TextView tv_ok = view.findViewById(R.id.tv_ok);
                        TextView tv_t_and_c = view.findViewById(R.id.tv_t_and_c);
                        TextView tv_merchant_name = view.findViewById(R.id.tv_merchant_name);

                        tv_merchant_name.setText(merchantName);
                        tv_t_and_c.setText(condition);

                        tv_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });

                        alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                });

            }
        }

        public PackageDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dealshai_single_item_view, parent, false);
            return new PackageDetailsAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(PackageDetailsAdapter.ViewHolder holder, int position) {
            String title = "";
            String categoryName = "";
            String merchantName = "";
            String condition = "";
            String imgUrl = "";
            String validFor = "";
            String validOn = "";
            String quantity = "";

            imageLoader = ImageLoader.getInstance();
            if (!imageLoader.isInited()) {
                imageLoader.init(ImageLoaderConfiguration.createDefault(getContext()));
            }
            options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .build();

            try {
                JSONObject object = packageItems.getJSONObject(position);
                title = object.getString("cupon_title").trim();
                categoryName = object.getString("category").trim();
                merchantName = object.getString("merchant_name").trim();
                condition = object.getString("condition").trim();
                validFor = object.getString("valid_for").trim();
                validOn = object.getString("valid_on").trim();
                imgUrl = object.getString("img");
                quantity = object.getString("qty");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            holder.tv_title.setText(title);
            holder.tv_merchant_name.setText(merchantName);
            holder.tv_category_name.setText(categoryName);
            holder.tv_valid_for.setText(validFor);
            holder.tv_valid_on.setText(validOn);
            holder.tv_t_and_c.setText(condition);
            holder.qty.setText(quantity);
            imageLoader.displayImage(imgUrl, holder.index_banner, options);
        }

        @Override
        public int getItemCount() {
            return packageItems.length();
        }
    }
}
