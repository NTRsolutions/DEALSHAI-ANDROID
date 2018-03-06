package com.ogma.dealshaiapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.activity.OfferActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by AndroidDev on 26-09-2017.
 */

public class FragmentIndexBanner extends Fragment implements View.OnClickListener {

    private String discount;
    private String oldPrice;

    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private String photo;
    private static String itemId;
    private String coupon_Id;

    public static FragmentIndexBanner newInstance(JSONObject jsonObject) {
        String photo = "";
        try {
            itemId = jsonObject.getString("cupon_id");
            photo = jsonObject.getString("img");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Bundle bundle = new Bundle();
        bundle.putString("id", itemId);
        bundle.putString("photo", photo);
        FragmentIndexBanner fragment = new FragmentIndexBanner();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.sliding_banner_page, container, false);

        ImageView index_banner = view.findViewById(R.id.index_banner);

        Bundle bundle = getArguments();

        coupon_Id = bundle.getString("id");
        photo = bundle.getString("photo");

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
        imageLoader.displayImage(photo, index_banner, options);
        index_banner.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.index_banner:
                startActivity(new Intent(getActivity(), OfferActivity.class).putExtra("coupon_id", coupon_Id));
                break;

        }
    }
}
