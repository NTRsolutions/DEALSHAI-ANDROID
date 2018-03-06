package com.ogma.dealshaiapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.network.NetworkConnection;
import com.ogma.dealshaiapp.utility.Session;

import java.util.HashMap;

public class WelcomeActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    private String userId;
    private String name;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        LinearLayout parentPanel = findViewById(R.id.parentPanel);

        Session session = new Session(WelcomeActivity.this);
        HashMap<String, String> user = session.getUserDetails();
        userId = user.get(Session.KEY_ID);
        name = user.get(Session.KEY_NAME);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkConnection connection = new NetworkConnection(WelcomeActivity.this);
        if (connection.isNetworkConnected()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if ((userId != null && !userId.equals("")) && (name != null) && !name.equals("")) {
                        Intent intent = new Intent(WelcomeActivity.this, IndexActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        WelcomeActivity.this.finish();
                    } else {
                        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        WelcomeActivity.this.finish();
                    }
                }
            }, 1000);
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(R.string.no_internet_connection);
            alertDialogBuilder.setMessage("Please enable your internet connection.");
            alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                    onResume();
                }
            })
                    .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            WelcomeActivity.this.finish();
                        }
                    });
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            alertDialog.setCanceledOnTouchOutside(false);
        }
    }
}
