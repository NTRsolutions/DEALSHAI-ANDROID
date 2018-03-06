package com.ogma.dealshaiapp.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.network.WebServiceHandler;
import com.ogma.dealshaiapp.network.WebServiceListener;

import org.json.JSONException;
import org.json.JSONObject;

public class QRCodeView extends Dialog {

    private String oderId;
    private Context context;
    private String qrCodeUrl;
    private String unique_code;
    private String title;
    private String qty;
    private String price;
    private String total_price;
    private Activity activity;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ImageView index_banner;
    private TextView tv_title;
    private TextView tv_quantity;
    private TextView tv_price;
    private TextView tv_total_amount;
    private TextView tv_unique_code;
    private CardView parentPanel;
    private ImageView iv_esc;

    public QRCodeView(Context context, @NonNull Activity activity, String oderId, String unique_code) {
        super(activity);
        this.context = context;
        this.activity = activity;
        this.oderId = oderId;
        this.unique_code = unique_code;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_view);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        parentPanel = findViewById(R.id.parentPanel);
        index_banner = findViewById(R.id.index_banner);
        tv_title = findViewById(R.id.tv_title);
        tv_quantity = findViewById(R.id.tv_quantity);
        tv_price = findViewById(R.id.tv_price);
        tv_unique_code = findViewById(R.id.tv_unique_code);
        tv_total_amount = findViewById(R.id.tv_total_amount);
        iv_esc = findViewById(R.id.iv_esc);
        getQRCode(oderId);
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.account_icon)
                .cacheInMemory()
                .cacheOnDisc()
                .build();

        imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited()) {
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        }

        iv_esc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void getQRCode(String couponCode) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(context);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                String is_error = null;
                try {
                    jsonObject = new JSONObject(response);
                    is_error = jsonObject.getString("err");

                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(parentPanel, "Response Error!", Snackbar.LENGTH_LONG).show();
                }

                if (is_error == null || Integer.parseInt(is_error) == 1) {
                    Snackbar.make(parentPanel, "No data found", Snackbar.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject object = jsonObject.getJSONObject("deal_details");
                        if (object.length() > 0) {
                            qrCodeUrl = object.getString("qr_code");
                            title = object.getString("title");
                            qty = object.getString("qty");
                            price = object.getString("price");
                            total_price = object.getString("total_price");

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageLoader.displayImage(qrCodeUrl, index_banner, options);
                                    tv_price.setText("₹ " + price);
                                    tv_title.setText(title);
                                    tv_quantity.setText(qty);
                                    tv_total_amount.setText("₹ " + total_price);
                                    tv_unique_code.setText(unique_code);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Snackbar.make(parentPanel, "No details found for this purchase", Snackbar.LENGTH_SHORT).show();
                    }

                }
            }
        };
        webServiceHandler.getQrcode(couponCode);

    }
}
