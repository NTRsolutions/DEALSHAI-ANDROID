package com.ogma.dealshaiapp.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.network.NetworkConnection;
import com.ogma.dealshaiapp.network.WebServiceHandler;
import com.ogma.dealshaiapp.network.WebServiceListener;
import com.ogma.dealshaiapp.utility.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ReferFriendDialog extends Dialog {

    private Activity activity;
    private String referAmountGet;
    private String referAmountSend;
    private TextView tv_you_earn;
    private TextView tv_friend_earn;
    private ImageView iv_cross;
    private ImageButton ib_refer_now;
    private String referMessage;
    private String shareUrl;
    private String userId;

    public ReferFriendDialog(@NonNull Activity activity, String userId) {
        super(activity);
        this.activity = activity;
        this.userId = userId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_refer_friend);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        Session session = new Session(getContext());
        HashMap<String, String> user = session.getKeyReferAmounts();
        referAmountGet = user.get(Session.KEY_REFER_AMOUNT_GET);
        referAmountSend = user.get(Session.KEY_REFER_AMOUNT_SEND);

        tv_you_earn = findViewById(R.id.tv_you_earn);
        tv_friend_earn = findViewById(R.id.tv_friend_earn);
        iv_cross = findViewById(R.id.iv_cross);
        ib_refer_now = findViewById(R.id.ib_refer_now);

        if (referAmountGet != null) {
            tv_you_earn.setText("₹" + referAmountGet);
            tv_friend_earn.setText("₹" + referAmountSend);
        }

        iv_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        ib_refer_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkConnection connection = new NetworkConnection(activity);
                if (connection.isNetworkConnected()) {
                    shareReferCode(userId);
                }
            }
        });

    }

    private void shareReferCode(String userId) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(activity);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                int is_error;
                try {
                    jsonObject = new JSONObject(response);
                    is_error = jsonObject.getInt("err");
                    if (is_error == 0) {
                        referMessage = jsonObject.getString("text");
                        shareUrl = jsonObject.getString("link");
                        if (shareUrl != null) {
                            referMessage = referMessage + "\n" + shareUrl;
                        }
                        share_via_app(referMessage);
                    } else
                        Toast.makeText(activity, jsonObject.getString("msg") + "", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        webServiceHandler.getReferralMessage(userId);
    }

    private void share_via_app(String referMessage) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        sendIntent.putExtra(Intent.EXTRA_TEXT, referMessage);
        sendIntent.setType("text/plain");
        activity.startActivity(sendIntent);
    }
}
