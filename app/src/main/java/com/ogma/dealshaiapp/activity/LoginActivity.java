package com.ogma.dealshaiapp.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.network.NetworkConnection;
import com.ogma.dealshaiapp.network.WebServiceHandler;
import com.ogma.dealshaiapp.network.WebServiceListener;
import com.ogma.dealshaiapp.utility.LocationManagerHelper;
import com.ogma.dealshaiapp.utility.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class
LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_mobile_number;
    private EditText et_email;
    Session session;
    private EditText et_otp;
    private EditText et_name;
    private LinearLayout ll_mobile_email;
    private LinearLayout ll_otp;
    private LinearLayout ll_name_and_refer;
    private Button btn_signIn;
    private String userId;
    private String mobile;
    private String emailId;
    private String name = "";
    private String referral_code = "";
    private String otp;
    private LinearLayout ll_lower;
    private HashMap<String, String> user;
    private EditText et_rereferral_code;
    private LinearLayout rl_TnC;
    private TextView tv_TnC;
    private TextView tv_prv_plc;
    private LocationManagerHelper locationManagerHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        locationManagerHelper = new LocationManagerHelper(LoginActivity.this);
        locationManagerHelper.requestLocationPermission();
        getCurrentLocation();
        session = new Session(LoginActivity.this);

        et_mobile_number = findViewById(R.id.et_mobile_number);
        et_email = findViewById(R.id.et_email);
        et_otp = findViewById(R.id.et_otp);
        et_name = findViewById(R.id.et_name);
        et_rereferral_code = findViewById(R.id.et_refer_code);

        ll_mobile_email = findViewById(R.id.ll_mobile_email);
        ll_otp = findViewById(R.id.ll_otp);
        ll_name_and_refer = findViewById(R.id.ll_name_and_refer);
        ll_lower = findViewById(R.id.ll_lower);
        rl_TnC = findViewById(R.id.rl_TnC);
        tv_TnC = findViewById(R.id.tv_TnC);
        tv_prv_plc = findViewById(R.id.tv_prv_plc);

        ll_otp.setVisibility(View.GONE);
        ll_name_and_refer.setVisibility(View.GONE);
        ll_lower.setVisibility(View.GONE);
        btn_signIn = findViewById(R.id.btn_signIn);

        tv_TnC.setPaintFlags(tv_TnC.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tv_prv_plc.setPaintFlags(tv_prv_plc.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        findViewById(R.id.btn_signIn).setOnClickListener(this);
        findViewById(R.id.tv_btn_resend).setOnClickListener(this);
        findViewById(R.id.tv_btn_reset).setOnClickListener(this);
        findViewById(R.id.tv_TnC).setOnClickListener(this);
        findViewById(R.id.tv_prv_plc).setOnClickListener(this);
    }

    private void getCurrentLocation() {
        double longitude;
        double latitude;
        session = new Session(LoginActivity.this);
        Location location = locationManagerHelper.getLastKnownLocation();
        if (location == null)
            locationManagerHelper.getLocation(locationListener);
        else {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            session.setLocationDetails("", "", String.valueOf(latitude), String.valueOf(longitude));
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            session = new Session(LoginActivity.this);
//            locationManagerHelper.getLocationManager().removeUpdates(this);
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            session.setLocationDetails("", "", String.valueOf(latitude), String.valueOf(longitude));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    public void onClick(View v) {
        Intent browserIntent;
        int id = v.getId();
        switch (id) {
            case R.id.btn_signIn:
                NetworkConnection connection = new NetworkConnection(LoginActivity.this);
                if (connection.isNetworkConnected()) {
                    if (btn_signIn.getText().toString().equalsIgnoreCase("Get OTP")) {
                        if (isValidMobile()) {
                            userLogin();
                        }
                    } else if (btn_signIn.getText().toString().equalsIgnoreCase("Sign In")) {
                        checkOTP();
                    } else {
                        if (isValid()) {
                            addName();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.tv_btn_resend:
                userLogin();
                break;
            case R.id.tv_TnC:
                browserIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.dealshai.in/Home/terms_and_conditions?data=1526987232"));
                startActivity(browserIntent);
                break;
            case R.id.tv_prv_plc:
                browserIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.dealshai.in/Home/privacy_policy?data=1526987232"));
                startActivity(browserIntent);
                break;
            case R.id.tv_btn_reset:
                et_mobile_number.setText("");
                et_email.setText("");
                et_otp.setText("");
                btn_signIn.setText("Get OTP");
                ll_lower.setVisibility(View.GONE);
                ll_mobile_email.setVisibility(View.VISIBLE);
                ll_otp.setVisibility(View.GONE);
                rl_TnC.setVisibility(View.VISIBLE);
                break;
        }
    }

    private boolean isValidMobile() {
        if (et_mobile_number.getText().toString().equals("")) {
            et_mobile_number.setError("Please enter a your mobile number.");
            return false;
        } else if (et_mobile_number.getText().toString().trim().length() < 10) {
            et_mobile_number.setError("Please enter a valid mobile number.");
            return false;
        } else
            return true;
    }

    private boolean isValid() {
        if (et_name.getText().toString().trim().equals("")) {
            et_name.setError("Please enter your full name");
            return false;
        } else return isValidEmail();
    }

    public boolean isValidEmail() {
        CharSequence target = et_email.getText().toString().trim();
        if (target.equals("")) {
            et_email.setError("Please enter your email id.");
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private void userLogin() {
        mobile = et_mobile_number.getText().toString().trim();
        WebServiceHandler webServiceHandler = new WebServiceHandler(LoginActivity.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                try {
                    if (response != null) {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("err").equals("0")) {
                            session.saveUserLoginSession(null, null, mobile, null, null, null, null);
                            LoginActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_mobile_email.setVisibility(View.GONE);
                                    ll_otp.setVisibility(View.VISIBLE);
                                    btn_signIn.setText("Sign In");
                                    ll_lower.setVisibility(View.VISIBLE);
                                    rl_TnC.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        webServiceHandler.loginUser(mobile);
    }

    private void checkOTP() {
        otp = et_otp.getText().toString().trim();
        user = session.getUserDetails();
        mobile = user.get(Session.KEY_CONTACT_NUMBER);
        WebServiceHandler webServiceHandler = new WebServiceHandler(LoginActivity.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                try {
                    if (response != null) {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("err").equals("0")) {
                            String userType = String.valueOf(jsonObject.getInt("is_new_user"));
                            name = jsonObject.getString("name");
                            JSONObject userDetails = jsonObject.getJSONObject("user_details");
                            nextStep(userType, userDetails, name);
                        } else
                            LoginActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        webServiceHandler.checkOTP(otp, mobile);
    }

    private void addName() {
        user = session.getUserDetails();
        userId = user.get(Session.KEY_ID);
        name = et_name.getText().toString().trim();
        referral_code = et_rereferral_code.getText().toString().trim();
        emailId = et_email.getText().toString().trim();

        WebServiceHandler webServiceHandler = new WebServiceHandler(LoginActivity.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String err = jsonObject.getString("err");
                        if (err.equals("0")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    session.saveUserLoginSession(userId, name, mobile, emailId, "", "", "");
                                    startActivity(new Intent(LoginActivity.this, IndexActivity.class)
                                            .putExtra("Flag", true));
                                    LoginActivity.this.finish();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        webServiceHandler.add_name_and_referral_code(userId, name, referral_code, emailId);
    }

    private void nextStep(String userType, JSONObject userDetails, String name) {
        String userId = "";
        try {
            userId = userDetails.getString("id");
            emailId = userDetails.getString("email");
            session.saveUserLoginSession(userId, name, "", emailId, null, "", null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch (userType) {
            case "0":
                startActivity(new Intent(LoginActivity.this, IndexActivity.class)
                        .putExtra("Flag", true));
                LoginActivity.this.finish();
                break;
            case "1":
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btn_signIn.setText("Continue");
                        ll_otp.setVisibility(View.GONE);
                        ll_name_and_refer.setVisibility(View.VISIBLE);
                        ll_lower.setVisibility(View.GONE);
                    }
                });
                break;
        }
    }
}