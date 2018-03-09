package com.ogma.dealshaiapp.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.model.LocationModel;
import com.ogma.dealshaiapp.network.NetworkConnection;
import com.ogma.dealshaiapp.network.WebServiceHandler;
import com.ogma.dealshaiapp.network.WebServiceListener;
import com.ogma.dealshaiapp.utility.CheckLocationServiceEnabled;
import com.ogma.dealshaiapp.utility.LocationManagerHelper;
import com.ogma.dealshaiapp.utility.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocationActivity extends AppCompatActivity implements View.OnClickListener {

    private LocationManagerHelper locationManagerHelper;
    private AutoCompleteTextView actv_city;
    private AutoCompleteTextView actv_sub_location;

    private ArrayList<LocationModel> locationModelCity;
    private ArrayList<LocationModel> locationModelCityArea;

    private List<String> cityList;
    private List<String> subLocationList;
    private ArrayAdapter<String> cityAdapter;
    private ArrayAdapter<String> subLocationAdapter;
    private String cityLatitude;
    private String areaLatitude;
    private String cityLongitude;
    private String areaLongitude;
    private LinearLayout parentPanel;
    private String cityName;
    private String areaName;
    private TextView tv_city;
    private TextView tv_area;
    private RelativeLayout rl_area;
    private LinearLayout ll_city;
    private LinearLayout ll_area;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        locationManagerHelper = new LocationManagerHelper(LocationActivity.this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setTitle("Select Location");
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cityList = new ArrayList<>();
        subLocationList = new ArrayList<>();
        locationModelCity = new ArrayList<>();
        locationModelCityArea = new ArrayList<>();

        parentPanel = findViewById(R.id.parentPanel);

        rl_area = findViewById(R.id.rl_area);
        tv_city = findViewById(R.id.tv_city);
        tv_area = findViewById(R.id.tv_area);
        ll_city = findViewById(R.id.ll_city);
        ll_area = findViewById(R.id.ll_area);

        actv_city = findViewById(R.id.actv_city);
        actv_sub_location = findViewById(R.id.actv_sub_location);

        cityAdapter = new ArrayAdapter<>(LocationActivity.this, android.R.layout.select_dialog_item, cityList);
        actv_city.setThreshold(0);
        actv_city.setAdapter(cityAdapter);
        actv_city.setTextColor(Color.BLACK);

        subLocationAdapter = new ArrayAdapter<>(LocationActivity.this, android.R.layout.select_dialog_item, subLocationList);
        actv_sub_location.setThreshold(0);
        actv_sub_location.setAdapter(subLocationAdapter);
        actv_sub_location.setTextColor(Color.BLACK);

        final Session session = new Session(LocationActivity.this);
        HashMap<String, String> locationDetails = session.getLocationDetails();

        cityName = locationDetails.get(Session.KEY_CITY);
        areaName = locationDetails.get(Session.KEY_AREA);

        if (!cityName.equals("")) {
            ll_city.setVisibility(View.VISIBLE);
            tv_city.setText(cityName);
        }
        if (!areaName.equals("")) {
            ll_area.setVisibility(View.VISIBLE);
            tv_area.setText(areaName);
        }

        actv_city.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                actv_city.showDropDown();
            }
        });

        actv_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actv_city.showDropDown();
            }
        });

        actv_sub_location.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                actv_sub_location.showDropDown();
            }
        });

        actv_sub_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actv_sub_location.showDropDown();
            }
        });

        actv_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String cityId;
                if (locationModelCity.size() > 0) {
                    String city = actv_city.getText().toString();
                    for (int i = 0; i < locationModelCity.size(); i++) {
                        LocationModel locationModel = locationModelCity.get(i);
                        if (city.equals(locationModel.getName())) {
                            cityId = locationModel.getId();
                            getSubLocations(cityId);
                            cityLatitude = locationModel.getLat();
                            cityLongitude = locationModel.getLng();
                            cityName = locationModel.getName();
                            rl_area.setVisibility(View.VISIBLE);
                            ll_city.setVisibility(View.VISIBLE);
                            tv_city.setText(cityName);
                            ll_area.setVisibility(View.GONE);
                            areaName = "";
                            areaLatitude = "";
                            areaLongitude = "";
                        }
                    }
                }
            }
        });

        actv_sub_location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (locationModelCityArea.size() > 0) {
                    String cityArea = actv_sub_location.getText().toString();
                    for (int i = 0; i < locationModelCityArea.size(); i++) {
                        LocationModel locationModel = locationModelCityArea.get(i);
                        if (cityArea.equals(locationModel.getName())) {
                            areaLatitude = locationModel.getLat();
                            areaLongitude = locationModel.getLng();
                            areaName = locationModel.getName();
                            ll_area.setVisibility(View.VISIBLE);
                            tv_area.setText((areaName));
                        }
                    }
                }
            }
        });

        findViewById(R.id.tv_btn_save).setOnClickListener(this);
        findViewById(R.id.tv_btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_detect).setOnClickListener(this);

        NetworkConnection connection = new NetworkConnection(LocationActivity.this);
        if (connection.isNetworkConnected()) {
            getCity();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckLocationServiceEnabled serviceEnabled = new CheckLocationServiceEnabled(LocationActivity.this);
        serviceEnabled.checkLocationEnabled();
    }

    @Override
    public void onClick(View v) {
        session = new Session(LocationActivity.this);
        switch (v.getId()) {
            case R.id.tv_btn_save:
                if (isCityNotEmpty()) {
                    if (isAreaNotEmpty())
                        session.setLocationDetails(cityName, areaName, areaLatitude, areaLongitude);
                    else
                        session.setLocationDetails(cityName, "", cityLatitude, cityLongitude);
                    finish();
                }
                break;
            case R.id.tv_btn_cancel:
                finish();
                break;
            case R.id.btn_detect:

                startLocationManager();
                break;
        }
    }

    private boolean isCityNotEmpty() {
        if (actv_city.getText().toString().trim().equals("")) {
            actv_city.setError("Please select a City");
            return false;
        }
        return true;
    }

    private boolean isAreaNotEmpty() {
        return actv_sub_location.getVisibility() == View.VISIBLE && !areaName.equals("");
    }

    private void getCity() {
        WebServiceHandler webServiceHandler = new WebServiceHandler(LocationActivity.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                session = new Session(LocationActivity.this);
                JSONObject main = null;
                String is_error = null;
                try {
                    main = new JSONObject(response);
                    is_error = main.getString("err");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(parentPanel, "Response Error! ", Snackbar.LENGTH_SHORT).show();

                }
                if (is_error == null || Integer.parseInt(is_error) == 1) {
                    Snackbar.make(parentPanel, "No data found", Snackbar.LENGTH_SHORT).show();
                } else {
                    JSONArray cities = null;
                    try {
                        cities = main.getJSONArray("city");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    cityList.clear();
                    locationModelCity.clear();
                    if (cities != null && cities.length() > 0) {
                        for (int i = 0; i < cities.length(); i++) {
                            try {
                                JSONObject city = cities.getJSONObject(i);
                                if (city != null) {
                                    LocationModel locationModel = new LocationModel();
                                    locationModel.setId(city.getString("id"));
                                    locationModel.setName(city.getString("city_name"));
                                    locationModel.setLat(city.getString("lat"));
                                    locationModel.setLng(city.getString("lng"));
                                    locationModelCity.add(locationModel);
                                    cityList.add(city.getString("city_name"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Snackbar.make(parentPanel, "City's data missing.", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cityAdapter.notifyDataSetChanged();
                            int i;
                            i = cityAdapter.getPosition(cityName);
                            if (i != -1) {
                                cityLatitude = locationModelCity.get(i).getLat();
                                cityLongitude = locationModelCity.get(i).getLng();
                                ll_city.setVisibility(View.VISIBLE);
                                tv_city.setText(cityName);
                                areaName = "";
                                areaLatitude = "";
                                areaLongitude = "";
                            }
                        }
                    });
                }
            }
        };
        webServiceHandler.getCities();
    }

    private void getSubLocations(String cityId) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(LocationActivity.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                JSONObject main = null;
                String is_error = null;
                String msg = null;
                try {
                    main = new JSONObject(response);
                    is_error = main.getString("err");
                    msg = main.getString("msg");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(parentPanel, "Response Error!", Snackbar.LENGTH_SHORT).show();
                }
                if (is_error == null || Integer.parseInt(is_error) == 1) {
                    Snackbar.make(parentPanel, "No data found", Snackbar.LENGTH_SHORT).show();
                } else {
                    JSONArray cities = null;
                    try {
                        cities = main.getJSONArray("area");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Snackbar.make(parentPanel, "No area found", Snackbar.LENGTH_SHORT).show();
                    }
                    subLocationList.clear();
                    locationModelCityArea.clear();
                    if (cities != null && cities.length() > 0) {
                        for (int i = 0; i < cities.length(); i++) {
                            try {
                                JSONObject city = cities.getJSONObject(i);
                                if (city != null) {
                                    LocationModel locationModel = new LocationModel();
                                    locationModel.setId(city.getString("id"));
                                    locationModel.setName(city.getString("city_name"));
                                    locationModel.setLat(city.getString("lat"));
                                    locationModel.setLng(city.getString("lng"));
                                    locationModelCityArea.add(locationModel);
                                    subLocationList.add(city.getString("city_name"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Snackbar.make(parentPanel, "Area data missing.", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            subLocationAdapter.notifyDataSetChanged();
                            int i;
                            i = subLocationAdapter.getPosition(areaName);
                            if (i == -1) {
                                actv_sub_location.setText("");
                            }
                        }
                    });
                }
            }
        };
        webServiceHandler.getCityArea(cityId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("MissingPermission")
    private void startLocationManager() {
        Location location = locationManagerHelper.getLastKnownLocation();
        if (location == null) {
            locationManagerHelper.getLocation(locationListener);
        } else {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            getLocationInfo(String.valueOf(latitude), String.valueOf(longitude));
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(android.location.Location location) {
            locationManagerHelper.getLocationManager().removeUpdates(this);
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            getLocationInfo(String.valueOf(latitude), String.valueOf(longitude));
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

    private void getLocationInfo(String lat, String lng) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(LocationActivity.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                session = new Session(LocationActivity.this);
                JSONObject jsonObject = null;
                int is_error = 0;
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if (jsonObject != null) {
                        is_error = Integer.parseInt(jsonObject.getString("err"));
                        if (is_error != 0) {
                            Snackbar.make(parentPanel, jsonObject.getString("msg") + "", Snackbar.LENGTH_LONG).show();
                        } else {
                            try {
                                JSONObject location = jsonObject.getJSONObject("location");
                                if (location != null) {
                                    cityName = location.getString("city_name");
                                    cityLatitude = location.getString("lat");
                                    cityLongitude = location.getString("lng");
                                    areaName = "";
                                    areaLatitude = "";
                                    areaLongitude = "";

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            actv_city.setText(cityName);
                                            tv_city.setText(cityName);
                                        }
                                    });

                                    try {
                                        JSONArray cities = location.getJSONArray("area_list");
                                        subLocationList.clear();
                                        locationModelCityArea.clear();
                                        if (cities != null && cities.length() > 0) {
                                            for (int i = 0; i < cities.length(); i++) {
                                                try {
                                                    JSONObject city = cities.getJSONObject(i);
                                                    if (city != null) {
                                                        LocationModel locationModel = new LocationModel();
                                                        locationModel.setId(city.getString("id"));
                                                        locationModel.setName(city.getString("area_name"));
                                                        locationModel.setLat(city.getString("lat"));
                                                        locationModel.setLng(city.getString("lng"));
                                                        locationModelCityArea.add(locationModel);
                                                        subLocationList.add(city.getString("area_name"));
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    Snackbar.make(parentPanel, "Area data missing.", Snackbar.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (subLocationList.size() > 0) {
                                                    rl_area.setVisibility(View.VISIBLE);
                                                }
                                                subLocationAdapter.notifyDataSetChanged();
                                            }
                                        });

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
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
        webServiceHandler.getLocationInfo(lat, lng);
    }
}
