package com.ogma.dealshaiapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.activity.IndexActivity;
import com.ogma.dealshaiapp.model.CategoryDetails;

import java.util.ArrayList;

public class FragmentMoreCategories extends Fragment implements IndexActivity.OnBackPressedListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_categories, container, false);

        ((IndexActivity) getActivity()).setOnBackPressedListener(this);

        final ArrayList<CategoryDetails> categoryDetails;
        Bundle bundle = getArguments();
        categoryDetails = (ArrayList<CategoryDetails>) bundle.getSerializable("categoryList");

        GridView gv_categories = view.findViewById(R.id.gv_categories);

        if (categoryDetails != null) {
            CategoriesAdapter adapter = new CategoriesAdapter(getActivity(), R.layout.more_categories_item_view, categoryDetails);
            gv_categories.setAdapter(adapter);
        }

        gv_categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentIndivisualCategories fca = new FragmentIndivisualCategories();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                Bundle bundle = new Bundle();
                if (categoryDetails != null) {
                    CategoryDetails cat = categoryDetails.get(position);
                    String catId = cat.getCatId();
                    bundle.putString("categoryId", catId);
                }
                fca.setArguments(bundle);
                manager.beginTransaction().replace(R.id.index_frame, fca).commitAllowingStateLoss();
            }
        });
        return view;
    }

    @Override
    public void doBack() {
        FragmentIndex fca = new FragmentIndex();
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction().replace(R.id.index_frame, fca).commitAllowingStateLoss();
    }

    private class CategoriesAdapter extends ArrayAdapter {
        private Activity activity;
        private ArrayList<CategoryDetails> categoryList;
        private String catId;
        private DisplayImageOptions options;
        private ImageLoader imageLoader;

        CategoriesAdapter(Activity activity, int activity_more_categories_item, ArrayList<CategoryDetails> categoryList) {
            super(activity, activity_more_categories_item, categoryList);
            this.activity = activity;
            this.categoryList = categoryList;
        }

        @Override
        public int getCount() {
            return categoryList.size();
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View v = convertView;
            ViewHolder viewHolder;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.more_categories_item_view, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_icon = v.findViewById(R.id.iv_icon);
                viewHolder.tv_title = v.findViewById(R.id.tv_title);

                final CategoryDetails categoryDetails = categoryList.get(position);

                String catName = categoryDetails.getCatName();
                catId = categoryDetails.getCatId();
                String iconUrl = categoryDetails.getImgUrl();

                imageLoader = ImageLoader.getInstance();
                if (!imageLoader.isInited()) {
                    imageLoader.init(ImageLoaderConfiguration.createDefault(getContext()));
                }
                options = new DisplayImageOptions.Builder()
                        .showImageOnLoading(R.drawable.loading)
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .considerExifParams(true)
                        .build();
                imageLoader.displayImage(iconUrl, viewHolder.iv_icon, options);
                viewHolder.tv_title.setText("   " + catName);

            } else {
                viewHolder = (ViewHolder) v.getTag();
            }
            return v;
        }


        class ViewHolder {
            TextView tv_title;
            ImageView iv_icon;
        }
    }
}
