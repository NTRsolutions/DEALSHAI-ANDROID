package com.ogma.dealshaiapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.activity.DetailsActivity;
import com.ogma.dealshaiapp.dialog.QuickView;
import com.ogma.dealshaiapp.fragment.FragmentIndex;
import com.ogma.dealshaiapp.model.MerchantDetails;
import com.ogma.dealshaiapp.network.WebServiceHandler;
import com.ogma.dealshaiapp.network.WebServiceListener;
import com.ogma.dealshaiapp.utility.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.support.constraint.R.id.parent;

/**
 * Created by AndroidDev on 16-11-2017.
 */

public class MerchantViewAdapter extends RecyclerView.Adapter<MerchantViewAdapter.ViewHolder> {
    private ArrayList<MerchantDetails> arrayList;
    private Context context;
    private int merchantId;
    private AppCompatActivity activity;
    private ImageLoader imageLoader;
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showStubImage(R.drawable.loading)
            .showImageOnLoading(R.drawable.loading)
            .cacheInMemory()
            .cacheOnDisc()
            .build();
    private String merchant_id;
    private String userId;
    private int totalLike;

    public MerchantViewAdapter(Context context, AppCompatActivity activity, ArrayList<MerchantDetails> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
        this.activity = activity;
        imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited()) {
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        }

        Session session = new Session(context);
        HashMap<String, String> user = session.getUserDetails();
        userId = user.get(Session.KEY_ID);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView index_banner;
        private TextView tv_likes;
        private TextView tv_title;
        private TextView tv_address;
        private TextView tv_lowest_deal_tittle;
        private TextView tv_discount;
        private TextView tv_quick_view;
        private TextView tv_old_price;
        private TextView tv_new_price;

        ViewHolder(final View itemView) {
            super(itemView);
            index_banner = itemView.findViewById(R.id.index_banner);
            tv_likes = itemView.findViewById(R.id.tv_likes);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_address = itemView.findViewById(R.id.tv_address);
            tv_lowest_deal_tittle = itemView.findViewById(R.id.tv_lowest_deal_tittle);
            tv_discount = itemView.findViewById(R.id.tv_discount);
            tv_quick_view = itemView.findViewById(R.id.tv_quick_view);
            tv_old_price = itemView.findViewById(R.id.tv_old_price);
            tv_new_price = itemView.findViewById(R.id.tv_new_price);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MerchantDetails merchantDetails = arrayList.get(getAdapterPosition());
                    merchantId = Integer.parseInt(merchantDetails.getMerchantId());
                    activity.startActivity(new Intent(activity, DetailsActivity.class).putExtra("merchant_id", merchantId));
                }
            });

            tv_quick_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MerchantDetails merchantDetails = arrayList.get(getAdapterPosition());
                    merchantId = Integer.parseInt(merchantDetails.getMerchantId());
                    new QuickView(activity, merchantId).show();

                }
            });

            tv_likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MerchantDetails merchantDetails = arrayList.get(getAdapterPosition());
                    merchant_id = merchantDetails.getMerchantId();
                    totalLike = Integer.parseInt(merchantDetails.getLikes());
                    hitlLike(userId, merchant_id, getAdapterPosition(), totalLike);
                    int t=Integer.parseInt(merchantDetails.getLikes());
                    Log.d("value",String.valueOf(t));
                    /**if(t-totalLike>0)
                        Toast.makeText(itemView.getContext(),"You Liked "+tv_title.getText(),Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(itemView.getContext(),"You Disliked "+tv_title.getText(),Toast.LENGTH_SHORT).show();*/

                    //Snackbar.make(itemView,"You Liked "+tv_title, Snackbar.LENGTH_SHORT).show();
                    //Toast.makeText(itemView.getContext(),"You Liked "+tv_title.getText(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void hitlLike(String userId, String merchant_id, final int adapterPosition, int totalLike) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(context);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                String msg = null;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int isErr = Integer.parseInt(jsonObject.getString("err"));
                    if (isErr == 0) {
                        msg = jsonObject.getString("msg");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (msg != null) {
                    MerchantDetails merchantDetails = arrayList.get(adapterPosition);
                    int like = Integer.parseInt(merchantDetails.getLikes());
                    switch (msg) {
                        case "Like Successfully":
                            like += 1;
                            //Toast.makeText(itemView.getContext(),"You Liked "+tv_title.getText(),Toast.LENGTH_SHORT).show();
                            break;
                        case "Dislike Successfully":
                            like -= 1;
                            //Toast.makeText(itemView.getContext(),"You Disliked "+tv_title.getText(),Toast.LENGTH_SHORT).show();
                            break;
                    }
                    merchantDetails.setLikes(String.valueOf(like));
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });

            }
        };
        webServiceHandler.hitLike(merchant_id, userId);
    }

    public MerchantViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.index_page_merchant_view3, parent, false);
        return new MerchantViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MerchantViewAdapter.ViewHolder holder, int position) {

        MerchantDetails merchantDetails = arrayList.get(position);

        String imgUri = merchantDetails.getImg();
        String title = merchantDetails.getTitle();
        String address = merchantDetails.getArea();
        String discount = merchantDetails.getDiscount();
        String dealTitle = merchantDetails.getShortDescription();
        String price = merchantDetails.getPrice();
        String offerPrice = merchantDetails.getOfferPrice();
        String likes = merchantDetails.getLikes();

        imageLoader.displayImage(imgUri, holder.index_banner, options);
        holder.tv_title.setText(title);
        holder.tv_address.setText(" " + address);
        holder.tv_discount.setText(discount);
        holder.tv_lowest_deal_tittle.setText(dealTitle);
        holder.tv_old_price.setText("₹" + price);
        holder.tv_new_price.setText("₹" + offerPrice);
       // holder.tv_likes.setText(" " + likes);
        holder.tv_old_price.setPaintFlags(holder.tv_old_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}