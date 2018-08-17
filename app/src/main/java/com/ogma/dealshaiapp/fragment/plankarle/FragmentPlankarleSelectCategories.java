package com.ogma.dealshaiapp.fragment.plankarle;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
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
import com.ogma.dealshaiapp.activity.IndexActivity;
import com.ogma.dealshaiapp.fragment.FragmentIndex;
import com.ogma.dealshaiapp.model.CategoryDetails;
import com.ogma.dealshaiapp.model.MerchantDetails;
import com.ogma.dealshaiapp.network.NetworkConnection;
import com.ogma.dealshaiapp.network.WebServiceHandler;
import com.ogma.dealshaiapp.network.WebServiceListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by AndroidDev on 06-10-2017.
 */

public class FragmentPlankarleSelectCategories extends Fragment implements IndexActivity.OnBackPressedListener, View.OnClickListener {

    private CategoryAdapter categoryAdapter;
    private ArrayList<CategoryDetails> categoryList;
    private RelativeLayout parentPanel;
    //private ImageView like;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_plankarle_select_categories, container, false);

        ((IndexActivity) getActivity()).setOnBackPressedListener(this);
        FloatingActionButton fab_go = view.findViewById(R.id.fab_go);

        categoryList = new ArrayList<>();
        parentPanel = view.findViewById(R.id.parentPanel);
        RecyclerView recycler_view = view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());

        categoryAdapter = new CategoryAdapter(getContext(), categoryList);
        recycler_view.setAdapter(categoryAdapter);

        NetworkConnection connection = new NetworkConnection(getContext());
        if (connection.isNetworkConnected()) {
            getCategories();
        }
        fab_go.setOnClickListener(this);
        return view;
    }

    private void getCategories() {
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
                    JSONArray categories = null;
                    try {
                        categories = main.getJSONArray("category");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Snackbar.make(parentPanel, "No category found", Snackbar.LENGTH_SHORT).show();
                    }
                    try {
                        if (categories != null) {
                            categoryList.clear();
                            for (int i = 0; i < categories.length(); i++) {
                                CategoryDetails details = new CategoryDetails();
                                JSONObject jsonObject = categories.getJSONObject(i);
                                details.setCatId(jsonObject.getString("id"));
                                details.setCatName(jsonObject.getString("name"));
                                details.setImgUrl(jsonObject.getString("img"));
                                categoryList.add(details);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Snackbar.make(parentPanel, "Category data missing.", Snackbar.LENGTH_SHORT).show();

                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            categoryAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        };
        webServiceHandler.getPlankarleCategories();
    }

    @Override
    public void doBack() {
        FragmentIndex fca = new FragmentIndex();
        FragmentManager manager = getFragmentManager();
        if (manager != null) {
            manager.beginTransaction().replace(R.id.index_frame, fca).commitAllowingStateLoss();
        }
    }

    @Override
    public void onClick(View v) {
        categoryList = categoryAdapter.getArrayList();
        Bundle bundle = new Bundle();
        bundle.putSerializable("categoryList", categoryList);
        FragmentCategoryList fca = new FragmentCategoryList();
        FragmentManager manager = getFragmentManager();
        fca.setArguments(bundle);
        if (manager != null) {
            manager.beginTransaction().replace(R.id.index_frame, fca).commitAllowingStateLoss();
        }
    }

    public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
        private ArrayList<CategoryDetails> arrayList;
        private Context context;
        private ImageLoader imageLoader;
        private DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.loading)
                .showImageOnLoading(R.drawable.loading)
                .cacheInMemory()
                .cacheOnDisc()
                .build();

        CategoryAdapter(Context context, ArrayList<CategoryDetails> arrayList) {
            this.arrayList = arrayList;
            this.context = context;
            imageLoader = ImageLoader.getInstance();
            if (!imageLoader.isInited()) {
                imageLoader.init(ImageLoaderConfiguration.createDefault(context));
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView index_banner;
            private TextView cat_title;
            private ImageView tick_icon;
            //private TextView tv_likes;

            ViewHolder(final View itemView) {
                super(itemView);
                index_banner = itemView.findViewById(R.id.index_banner);
                cat_title = itemView.findViewById(R.id.cat_title);
                tick_icon = itemView.findViewById(R.id.tick_icon);
                //tv_likes = itemView.findViewById(R.id.tv_likes);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CategoryDetails categoryDetails = arrayList.get(getAdapterPosition());
                        if (tick_icon.getVisibility() == View.VISIBLE) {
                            categoryDetails.setIsActive(0);
                        } else {
                            categoryDetails.setIsActive(1);
                        }
                        notifyDataSetChanged();
                    }
                });

            }
        }

        public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plankarle_select_categories, parent, false);
            return new CategoryAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CategoryAdapter.ViewHolder holder, int position) {
            CategoryDetails categoryDetails = arrayList.get(position);
            String title = categoryDetails.getCatName();
            String imgUri = categoryDetails.getImgUrl();

            imageLoader.displayImage(imgUri, holder.index_banner, options);
            holder.cat_title.setText(title);
            if (categoryDetails.getIsActive() == 1) {
                holder.tick_icon.setVisibility(View.VISIBLE);
            } else
                holder.tick_icon.setVisibility(View.GONE);
        }

        public ArrayList<CategoryDetails> getArrayList() {
            ArrayList<CategoryDetails> categoryDetailsArrayList = new ArrayList<>();
            for (int i = 0; i < arrayList.size(); i++) {
                CategoryDetails categoryDetails = arrayList.get(i);
                if (categoryDetails.getIsActive() == 1) {
                    categoryDetailsArrayList.add(categoryDetails);
                }
            }
            return categoryDetailsArrayList;
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }
}
