package com.ogma.dealshaiapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.ogma.dealshaiapp.dialog.ReferFriendDialog;
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

import me.anwarshahriar.calligrapher.Calligrapher;

public class IndexActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_LOCATION = 1;
    private LocationManagerHelper locationManagerHelper;

    private String cityName = "";
    private String areaName = "";
    private String latitude;
    private String longitude;
    Typeface myfont;
    private TextView tv_location;
    //private TextView tv_area;
    private TextView tv_notification;
    private DrawerLayout drawer_layout;
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
    private String userId;
    private String total_unread;
    private FrameLayout frameLayout;
    private ImageView pf_image;
    private AlertDialog alertDialog;
    private boolean flag = false;
    private String referAmountGet;
    private String referAmountSend;

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

        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this, "gothic.ttf",true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            flag = bundle.getBoolean("Flag");
        }
        locationManagerHelper = new LocationManagerHelper(IndexActivity.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationManagerHelper.requestLocationPermission();
        } else {
            if (flag)
                startLocationManager();
        }

        drawer_layout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        frameLayout = findViewById(R.id.frameLayout);
        tv_notification = findViewById(R.id.tv_notification);
        Typeface tx=Typeface.createFromAsset(getAssets(),"gothic.ttf");
        //et_search.setTypeface(tx);
        et_search = findViewById(R.id.et_search);
        tv_location = findViewById(R.id.tv_location);
        //tv_area = findViewById(R.id.tv_area);
        pf_image = header.findViewById(R.id.pf_imge);

       // TextView tv_liked = findViewById(R.id.tv_liked);
        ImageView tv_search = findViewById(R.id.tv_search);
        ImageView iv_notification = findViewById(R.id.iv_notification);
        LinearLayout ll_area = findViewById(R.id.ll_area);

        ImageView tv_home = findViewById(R.id.tv_home);
        //TextView tv_my_purchase = findViewById(R.id.tv_my_purchase);

        HashMap<String, String> user = session.getUserDetails();
        userId = user.get(Session.KEY_ID);


        ImageView drawer = findViewById(R.id.iv_drawer);
        drawer.setOnClickListener(this);
        ll_area.setOnClickListener(this);
        iv_notification.setOnClickListener(this);
        tv_notification.setOnClickListener(this);
        tv_search.setOnClickListener(this);
       // tv_liked.setOnClickListener(this);
       // Typeface tx=Typeface.createFromAsset(getAssets(),"gothic.ttf");

        onNavigationItemSelected(navigationView.getMenu().getItem(0));
        navigationView.setCheckedItem(R.id.nav_home);

        tv_home.setOnClickListener(this);
        //tv_my_purchase.setOnClickListener(this);
        //findViewById(R.id.tv_help).setOnClickListener(this);
        //findViewById(R.id.tv_refer).setOnClickListener(this);
        findViewById(R.id.frameLayout).setOnClickListener(this);

        NetworkConnection connection = new NetworkConnection(IndexActivity.this);
        if (connection.isNetworkConnected()) {
            getCountOfUnreadNotification(userId);
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
        if (cityName.equals("")) tv_location.setText("Kolkata");
        else tv_location.setText(cityName);

        //if (areaName.equals("")) tv_area.setText("Select Area  ");
       // else tv_area.setText(areaName + " ");

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START);
        }

        HashMap<String, String> user = session.getUserDetails();
        String imgUri = user.get(Session.KEY_IMAGE);

        ImageLoader imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited())
            imageLoader.init(ImageLoaderConfiguration.createDefault(IndexActivity.this));
        if (imgUri != null)
            imageLoader.displayImage(imgUri, pf_image, options);
    }

    @Override
    public void onBackPressed() {
//        Fragment fragmentIndex = getSupportFragmentManager().findFragmentByTag(FragmentIndex.class.getSimpleName());
//        Fragment fragmentPlankarleSelectCategories = getSupportFragmentManager().findFragmentByTag(FragmentPlankarleSelectCategories.class.getSimpleName());
//
//        if (fragmentIndex != null && fragmentIndex instanceof FragmentIndex) {
//            super.onBackPressed();
//        } else if (fragmentPlankarleSelectCategories != null && fragmentPlankarleSelectCategories instanceof FragmentPlankarleSelectCategories) {
//
//        }
        if (onBackPressedListener != null) {
            if (drawer_layout.isDrawerOpen(GravityCompat.START))
                drawer_layout.closeDrawer(GravityCompat.START);
            else onBackPressedListener.doBack();
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
                } else
                    et_search.setError("Please type something");
                break;
            case R.id.tv_home:
                FragmentIndex fragmentIndex = new FragmentIndex();
                manager = getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.index_frame, fragmentIndex).addToBackStack(null).commit();
                break;
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
                startActivity(new Intent(IndexActivity.this, MyPurchases.class));
                break;
            case R.id.vouchers:
                startActivity(new Intent(IndexActivity.this, MyVouchers.class));
                break;
            case R.id.account:
                startActivity(new Intent(IndexActivity.this, MyAccount.class));
                break;
            case R.id.liked:
                startActivity(new Intent(IndexActivity.this, LikedProductActivity.class));
                break;
            case R.id.logout:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Logout");
                alertDialogBuilder.setMessage("Are you sure you want to logout?");
                alertDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        session = new Session(IndexActivity.this);
                        session.saveUserLoginSession(null, null, null, null, null, null, null);
                        session.setLocationDetails(null, null, null, null);
                        startActivity(new Intent(IndexActivity.this, WelcomeActivity.class));
                        IndexActivity.this.finish();
                    }
                })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }
                        });
                alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                alertDialog.setCanceledOnTouchOutside(false);
                break;
            case R.id.help:
                addNewContact();
                break;
            case R.id.refer_a_friend:
                new ReferFriendDialog(IndexActivity.this, userId).show();
                break;
            case R.id.rate_the_app:
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                break;
        }
        return true;
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
        double longitude;
        double latitude;

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

    private void addNewContact() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.help_popup, null, false);
        dialog.setView(view);

        ImageView btn_wtsp_us = view.findViewById(R.id.btn_wtsp_us);
        btn_wtsp_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creates a new Intent to insert a contact
                Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                intent.putExtra(ContactsContract.Intents.Insert.NAME, "Dealshai Help");
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, "8335083753");
                intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
                intent.putExtra(ContactsContract.Intents.Insert.EMAIL, "help@dealshai.in");
                intent.putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);
                startActivity(intent);
                alertDialog.dismiss();
            }
        });
        alertDialog = dialog.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
    }

    private void getCountOfUnreadNotification(String userId) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(IndexActivity.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                final Session session = new Session(IndexActivity.this);
                try {
                    JSONObject main = new JSONObject(response);
                    String err = main.getString("err");
                    referAmountGet = main.getString("referrer_amount");
                    referAmountSend = main.getString("ref_amount");
                    if (err != null && err.equals("0")) {
                        total_unread = main.getString("total_unread");
                        JSONObject location = main.getJSONObject("location");
                        cityName = location.getString("city_name");
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            session.setKeyReferAmount(referAmountGet, referAmountSend);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(drawer_layout, "No new notifications.", Snackbar.LENGTH_SHORT).show();
                }

                if (total_unread != null) {
                    IndexActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (Integer.parseInt(total_unread) > 0) {
                                frameLayout.setVisibility(View.VISIBLE);
                                tv_notification.setText(total_unread);
                            }
                            if (cityName.equals("")) tv_location.setText("Kolkata");
                            else tv_location.setText(cityName);
                        }
                    });
                }
            }
        };
        webServiceHandler.getUnreadNotification(userId);
    }

    private void getCurrentLocationInfo(String lat, String lng) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(IndexActivity.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject;
                final Session session = new Session(IndexActivity.this);
                int is_error;
                try {
                    jsonObject = new JSONObject(response);
                    if (jsonObject != null) {
                        is_error = Integer.parseInt(jsonObject.getString("err"));
                        if (is_error != 0) {
                            Snackbar.make(drawer_layout, jsonObject.getString("msg") + "", Snackbar.LENGTH_LONG).show();
                        } else {
                            JSONObject location = jsonObject.getJSONObject("location");
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
                                        /**if (areaName.equals(""))
                                            tv_area.setText("Select Area  ");
                                        else
                                            tv_area.setText(areaName + " ");*/

                                        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FragmentIndex.class.getSimpleName());
                                        if (fragment != null && fragment instanceof FragmentIndex) {
                                            FragmentIndex fragmentIndex = (FragmentIndex) fragment;
                                            fragmentIndex.getData(latitude, longitude);
                                        }
                                    }
                                });
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
}