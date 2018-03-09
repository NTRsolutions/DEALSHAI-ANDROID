package com.ogma.dealshaiapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.dialog.NotificationView;
import com.ogma.dealshaiapp.fragment.FragmentIndex;
import com.ogma.dealshaiapp.network.NetworkConnection;
import com.ogma.dealshaiapp.network.WebServiceHandler;
import com.ogma.dealshaiapp.network.WebServiceListener;
import com.ogma.dealshaiapp.utility.LocationManagerHelper;
import com.ogma.dealshaiapp.utility.PermissionUtil;
import com.ogma.dealshaiapp.utility.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class IndexActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_LOCATION = 1;
    private static String[] PERMISSIONS_LOCATION = {android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION};
    private LocationManagerHelper locationManagerHelper;
    private Location location;

    private String cityName;
    private String areaName;
    private String latitude;
    private String longitude;

    private TextView tv_location;
    private TextView tv_area;
    private TextView tv_notification;
    private DrawerLayout drawer_layout;
    private ImageLoader imageLoader;
    private AppCompatEditText et_search;
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showStubImage(R.drawable.pf_male)
            .showImageOnFail(R.drawable.pf_male)
            .showImageForEmptyUri(R.drawable.pf_male)
            .cacheInMemory()
            .cacheOnDisc()
            .build();

    private Session session;

    protected OnBackPressedListener onBackPressedListener;
    private String TAG = IndexActivity.class.getName();
    private String userId;
    private String total_unread;
    private FrameLayout frameLayout;
    private String imgUri;
    private String gender;
    private ImageView pf_imge;
    private String referMessage = "";
    private String shareUrl = "";

    public interface OnBackPressedListener {
        void doBack();
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        session = new Session(IndexActivity.this);

        locationManagerHelper = new LocationManagerHelper(IndexActivity.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissions();
            locationManagerHelper.requestLocationPermission();
        } else {
            startLocationManager();
        }

        drawer_layout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        frameLayout = findViewById(R.id.frameLayout);
        tv_notification = findViewById(R.id.tv_notification);
        et_search = findViewById(R.id.et_search);
        tv_location = findViewById(R.id.tv_location);
        tv_area = findViewById(R.id.tv_area);
        pf_imge = header.findViewById(R.id.pf_imge);
        TextView tv_liked = findViewById(R.id.tv_liked);
        TextView tv_search = findViewById(R.id.tv_search);
        ImageView iv_notification = findViewById(R.id.iv_notification);
        LinearLayout ll_area = findViewById(R.id.ll_area);

        TextView tv_home = findViewById(R.id.tv_home);
        TextView tv_my_purchase = findViewById(R.id.tv_my_purchase);

        HashMap<String, String> user = session.getUserDetails();
        userId = user.get(Session.KEY_ID);


        ImageView drawer = findViewById(R.id.iv_drawer);
        drawer.setOnClickListener(this);
        ll_area.setOnClickListener(this);
        iv_notification.setOnClickListener(this);
        tv_notification.setOnClickListener(this);
        tv_search.setOnClickListener(this);
        tv_liked.setOnClickListener(this);

        onNavigationItemSelected(navigationView.getMenu().getItem(0));
        navigationView.setCheckedItem(R.id.nav_home);

        tv_home.setOnClickListener(this);
        tv_my_purchase.setOnClickListener(this);
        findViewById(R.id.tv_refer).setOnClickListener(this);
        findViewById(R.id.frameLayout).setOnClickListener(this);
    }

    private void requestLocationPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Snackbar.make(drawer_layout, "", Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(IndexActivity.this, PERMISSIONS_LOCATION, REQUEST_LOCATION);
                }
            }).show();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS_LOCATION, REQUEST_LOCATION);
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            session = new Session(IndexActivity.this);
            locationManagerHelper.getLocationManager().removeUpdates(this);
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            session.setLocationDetails(cityName, areaName, String.valueOf(latitude), String.valueOf(longitude));
            getCurrentLocationInfo(String.valueOf(latitude), String.valueOf(longitude));
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
    public void onResume() {
        super.onResume();
        session = new Session(IndexActivity.this);
        HashMap<String, String> locationDetails = session.getLocationDetails();
        cityName = locationDetails.get(Session.KEY_CITY);
        areaName = locationDetails.get(Session.KEY_AREA);
        if (cityName.equals(""))
            tv_location.setText("Select City");
        else
            tv_location.setText(cityName);

        if (areaName.equals(""))
            tv_area.setText("Select Area  ");
        else
            tv_area.setText(areaName + " ");

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START);
        }

        NetworkConnection connection = new NetworkConnection(IndexActivity.this);
        if (connection.isNetworkConnected()) {
            getCountOfUnreadNotification(userId);
        }

        HashMap<String, String> user = session.getUserDetails();
        gender = user.get(Session.KEY_GENDER);
        imgUri = user.get(Session.KEY_IMAGE);

        switch (gender) {
            case "Male":
                options = new DisplayImageOptions.Builder()
                        .showStubImage(R.drawable.pf_male)
                        .showImageForEmptyUri(R.drawable.pf_male)
                        .showImageOnFail(R.drawable.pf_male)
                        .cacheInMemory()
                        .cacheOnDisc()
                        .build();
                break;
            case "Female":
                options = new DisplayImageOptions.Builder()
                        .showStubImage(R.drawable.pf_female)
                        .showImageForEmptyUri(R.drawable.pf_female)
                        .showImageOnFail(R.drawable.pf_female)
                        .cacheInMemory()
                        .cacheOnDisc()
                        .build();
                break;
            default:
                options = new DisplayImageOptions.Builder()
                        .showStubImage(R.drawable.pf_male)
                        .showImageForEmptyUri(R.drawable.pf_male)
                        .showImageOnFail(R.drawable.pf_male)
                        .cacheInMemory()
                        .cacheOnDisc()
                        .build();
        }

        imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited()) {
            imageLoader.init(ImageLoaderConfiguration.createDefault(IndexActivity.this));
        }
        if (imgUri != null)
            imageLoader.displayImage(imgUri, pf_imge, options);

    }

    private void getCountOfUnreadNotification(String userId) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(IndexActivity.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject main = new JSONObject(response);
                    String err = main.getString("err");
                    if (err != null && err.equals("0")) {
                        total_unread = main.getString("total_unread");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(drawer_layout, "No data found", Snackbar.LENGTH_SHORT).show();
                }

                if (total_unread != null) {
                    IndexActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (Integer.parseInt(total_unread) > 0) {
                                frameLayout.setVisibility(View.VISIBLE);
                                tv_notification.setText(total_unread);
                            }
                        }
                    });
                }

            }
        };
        webServiceHandler.getUnreadNotification(userId);
    }

    @Override
    public void onBackPressed() {
        if (onBackPressedListener != null) {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawer(GravityCompat.START);
            } else {
                onBackPressedListener.doBack();
            }
        } else
            super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        onBackPressedListener = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        FragmentManager manager;
        switch (id) {
            case R.id.iv_drawer:
                drawer_layout.openDrawer(Gravity.START);
                break;
            case R.id.ll_area:
                startActivity(new Intent(IndexActivity.this, LocationActivity.class));
                break;
            case R.id.iv_notification:
            case R.id.frameLayout:
                new NotificationView(IndexActivity.this, userId).show();
                if (frameLayout.getVisibility() == View.VISIBLE)
                    frameLayout.setVisibility(View.INVISIBLE);
                break;
            case R.id.tv_search:
                if (!et_search.getText().toString().trim().equals("") || !et_search.getText().toString().isEmpty()) {
                    String searchText = et_search.getText().toString().trim();
                    startActivity(new Intent(IndexActivity.this, SearchResultActivity.class)
                            .putExtra("searchText", searchText));
                    et_search.setText("");
                    et_search.setCursorVisible(false);
                } else
                    et_search.setError("Please type something");
                break;
            case R.id.tv_home:
                FragmentIndex fragmentIndex = new FragmentIndex();
                manager = getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.index_frame, fragmentIndex).addToBackStack(null).commit();
                break;
            case R.id.tv_my_purchase:
                startActivity(new Intent(getApplicationContext(), MyPurchases.class));
                break;
            case R.id.tv_liked:
                startActivity(new Intent(getApplicationContext(), LikedProductActivity.class));
                break;
            case R.id.tv_refer:
                shareReferCode(userId);
                break;
        }
    }

    private void shareReferCode(String userId) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(IndexActivity.this);
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
                        Snackbar.make(drawer_layout, jsonObject.getString("msg") + "", Snackbar.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        webServiceHandler.getReferralMessage(userId);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(drawer_layout, "", Snackbar.LENGTH_SHORT).show();
                startLocationManager();
            } else {
                Snackbar.make(drawer_layout, "", Snackbar.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationManager() {
        session = new Session(IndexActivity.this);
        HashMap<String, String> locationDetails = session.getLocationDetails();
        String lat = locationDetails.get(Session.KEY_LATITUDE);
        double longitude;
        double latitude;

        if (lat.equals("")) {
            Location location = locationManagerHelper.getLastKnownLocation();
            if (location == null)
                locationManagerHelper.getLocation(locationListener);
            else {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                session.setLocationDetails(cityName, areaName, String.valueOf(latitude), String.valueOf(longitude));
                getCurrentLocationInfo(String.valueOf(latitude), String.valueOf(longitude));
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        FragmentManager manager;
        switch (id) {
            case R.id.nav_home:
                FragmentIndex fragmentIndex = new FragmentIndex();
                manager = getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.index_frame, fragmentIndex, FragmentIndex.class.getSimpleName()).addToBackStack(null).commit();
                break;
            case R.id.my_purchases:
                startActivity(new Intent(getApplicationContext(), MyPurchases.class));
                break;
            case R.id.vouchers:
                startActivity(new Intent(getApplicationContext(), MyVouchers.class));
                break;
            case R.id.account:
                startActivity(new Intent(getApplicationContext(), MyAccount.class));
                break;
            case R.id.liked:
                startActivity(new Intent(getApplicationContext(), LikedProductActivity.class));
                break;
            case R.id.logout:
                session = new Session(getApplicationContext());
                session.saveUserLoginSession(null, null, null, null, null, null, null);
                /*session.revokeSession();
                session.clearPref();*/
                session.setLocationDetails(null, null, null, null);
//                LoginManager.getInstance().logOut();
                startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
                break;
            case R.id.refer_a_friend:
                shareReferCode(userId);
                break;
        }
        return true;
    }

    private void getCurrentLocationInfo(String lat, String lng) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(IndexActivity.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                final Session session = new Session(IndexActivity.this);
                int is_error;
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if (jsonObject != null) {
                        is_error = Integer.parseInt(jsonObject.getString("err"));
                        if (is_error != 0) {
                            Snackbar.make(drawer_layout, jsonObject.getString("msg") + "", Snackbar.LENGTH_LONG).show();
                        } else {
                            try {
                                final JSONObject location = jsonObject.getJSONObject("location");
                                if (location != null) {
                                    String lat;
                                    String lng;
                                    String area;
                                    cityName = location.getString("city_name");
                                    lat = location.getString("lat");
                                    lng = location.getString("lng");
                                    if (lat != null && !lat.equals("")) {
                                        latitude = lat;
                                    }
                                    if (lng != null && !lng.equals("")) {
                                        longitude = lng;
                                    }

                                    try {
                                        area = location.getString("area_name");
                                        lat = location.getString("area_lat");
                                        lng = location.getString("area_lng");
                                        if (lat != null && !lat.equals("")) {
                                            latitude = lat;
                                        }
                                        if (lng != null && !lng.equals("")) {
                                            longitude = lng;
                                        }
                                        if (area != null && !area.equals(""))
                                            areaName = area;
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    session.setLocationDetails(cityName, areaName, latitude, longitude);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            HashMap<String, String> locationDetails = session.getLocationDetails();
                                            cityName = locationDetails.get(Session.KEY_CITY);
                                            areaName = locationDetails.get(Session.KEY_AREA);
                                            latitude = locationDetails.get(Session.KEY_LATITUDE);
                                            longitude = locationDetails.get(Session.KEY_LONGITUDE);

                                            tv_location.setText(cityName);
                                            if (areaName.equals(""))
                                                tv_area.setText("Select Area  ");
                                            else
                                                tv_area.setText(areaName + " ");

//                                            HashMap<String, String> lat = session.getKeyLatitude();
//                                            String latitude = lat.get(Session.KEY_LATITUDE);
//                                            HashMap<String, String> lng = session.getKeyLongitude();
//                                            String longitude = lng.get(Session.KEY_LONGITUDE);
                                            Fragment fragment = getSupportFragmentManager().findFragmentByTag(FragmentIndex.class.getSimpleName());
                                            if (fragment != null && fragment instanceof FragmentIndex) {
                                                FragmentIndex fragmentIndex = (FragmentIndex) fragment;
                                                fragmentIndex.getData(latitude, longitude);
                                            }
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        webServiceHandler.getCurrentLocationInfo(lat, lng);
    }

    public void share_via_app(String referMessage) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        sendIntent.putExtra(Intent.EXTRA_TEXT, referMessage);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void checkLocationEnabled() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(this.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(this.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(this.getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        } else {

        }
    }
}