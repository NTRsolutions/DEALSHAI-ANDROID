package com.ogma.dealshaiapp.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.network.WebServiceHandler;
import com.ogma.dealshaiapp.network.WebServiceListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificationView extends Dialog {

    private NotificationItemAdapter notificationItemAdapter;
    private String userId;
    private Activity activity;
    private JSONArray notifications;
    private RecyclerView recycler_view;


    public NotificationView(@NonNull Activity context, String userId) {
        super(context);
        this.activity = context;
        this.userId = userId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.notification_view);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setGravity(Gravity.TOP);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        notifications = new JSONArray();
        getNotification(userId);

        TextView tv_close = findViewById(R.id.tv_close);
        ImageView iv_esc = findViewById(R.id.iv_esc);
        recycler_view = findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());

        tv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        iv_esc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    private void getNotification(String userId) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(getContext());
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    notifications = jsonObject.getJSONArray("notification");
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notificationItemAdapter = new NotificationItemAdapter(getContext(), notifications);
                            recycler_view.setAdapter(notificationItemAdapter);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        };
        webServiceHandler.getNotification(userId);
    }


    private class NotificationItemAdapter extends RecyclerView.Adapter<NotificationItemAdapter.ViewHolder> {
        private JSONArray notifications;
        private Context context;

        NotificationItemAdapter(Context context, JSONArray notifications) {
            this.notifications = notifications;
            this.context = context;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private TextView tv_description;

            ViewHolder(View itemView) {
                super(itemView);
                tv_description = itemView.findViewById(R.id.tv_description);
            }
        }

        public NotificationItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_view_item, parent, false);
            return new NotificationItemAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final NotificationItemAdapter.ViewHolder holder, int position) {

            String description = "";

            try {
                JSONObject object = notifications.getJSONObject(position);
                description = object.getString("notification");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            holder.tv_description.setText(description);
        }

        @Override
        public int getItemCount() {
            return notifications.length();
        }
    }
}
