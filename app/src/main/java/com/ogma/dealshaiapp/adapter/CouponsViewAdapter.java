package com.ogma.dealshaiapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.model.OrderedItems;

import java.util.ArrayList;

/**
 * Created by AndroidDev on 21-11-2017.
 */

public class CouponsViewAdapter extends RecyclerView.Adapter<CouponsViewAdapter.ViewHolder> {
    private ArrayList<OrderedItems> arrayList = new ArrayList<>();
    private Context context;

    public CouponsViewAdapter(Context context, ArrayList<OrderedItems> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView index_banner;
        private TextView tv_merchant_name;
        private TextView tv_title;
        private TextView tv_quantity;
        private TextView tv_price;
        private TextView tv_total_amount;


        ViewHolder(View itemView) {
            super(itemView);
            index_banner = itemView.findViewById(R.id.index_banner);
            tv_merchant_name = itemView.findViewById(R.id.tv_merchant_name);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_quantity = itemView.findViewById(R.id.tv_quantity);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_total_amount = itemView.findViewById(R.id.tv_total_amount);
        }
    }

    public CouponsViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_view_plan_single_item, parent, false);
        return new CouponsViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CouponsViewAdapter.ViewHolder holder, int position) {

        ImageLoader imageLoader;
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.loading)
                .showImageOnLoading(R.drawable.loading)
                .cacheInMemory()
                .cacheOnDisc()
                .build();

        imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited()) {
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        }

        OrderedItems orderedItems = arrayList.get(position);
        holder.tv_price.setText("₹ " + orderedItems.getPrice());
        holder.tv_merchant_name.setText(orderedItems.getMerchantName());
        holder.tv_title.setText(orderedItems.getCouponTitle());
        holder.tv_quantity.setText(orderedItems.getQuantity());
        holder.tv_total_amount.setText("₹ " + orderedItems.getTotal_price());

        imageLoader.displayImage(orderedItems.getImg(), holder.index_banner
                , options);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}