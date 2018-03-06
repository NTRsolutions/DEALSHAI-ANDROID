package com.ogma.dealshaiapp.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ogma.dealshaiapp.R;
import com.ogma.dealshaiapp.model.OutletDetails;
import com.ogma.dealshaiapp.network.WebServiceHandler;
import com.ogma.dealshaiapp.network.WebServiceListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String merchantId;
    private ArrayList<OutletDetails> arrayList;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        arrayList = new ArrayList<>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            merchantId = bundle.getString("merchantId");
            getOutlets(merchantId);
        }
        mapFragment.getMapAsync(this);
    }

    private void getOutlets(String merchantId) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                JSONObject main = null;
                String is_error = null;
                try {
                    main = new JSONObject(response);
                    is_error = main.getString("err");
                } catch (JSONException e) {
                    e.printStackTrace();
                    MapsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MapsActivity.this, "Response Error!", Toast.LENGTH_LONG).show();
                        }
                    });

                }
                if (Integer.parseInt(is_error) == 1 || is_error == null) {
                    MapsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MapsActivity.this, "No Data found", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    try {
                        JSONArray outlet = main.getJSONArray("Outlet");
                        if (outlet.length() > 0) {
                            arrayList.clear();
                            for (int i = 0; i < outlet.length(); i++) {
                                JSONObject jsonObject = outlet.getJSONObject(i);
                                OutletDetails outletDetails = new OutletDetails();
                                outletDetails.setCityName(jsonObject.getString("city_name"));
                                outletDetails.setAreaName(jsonObject.getString("area_name"));
                                outletDetails.setAddress(jsonObject.getString("address"));
                                outletDetails.setLat(jsonObject.getString("lat"));
                                outletDetails.setLng(jsonObject.getString("lng"));
                                arrayList.add(outletDetails);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        MapsActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MapsActivity.this, "No Outlet found", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    MapsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mapFragment.getMapAsync(MapsActivity.this);
                        }
                    });
                }
            }
        };
        webServiceHandler.getOutlets(merchantId);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map) {
//        mMap = map;

        // Add a marker in Sydney and move the camera
        for (int i = 0; i < arrayList.size(); i++) {
            OutletDetails outletDetails = arrayList.get(i);
            Double lat = Double.valueOf(outletDetails.getLat());
            Double lng = Double.valueOf(outletDetails.getLng());
            LatLng latLng = new LatLng(lat, lng);
            String areaName = outletDetails.getAreaName();
            String address = outletDetails.getAddress();
            map.setMyLocationEnabled(true);
            //map.addMarker(new MarkerOptions().position(latLng).title(areaName));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
            map.addMarker(new MarkerOptions()
                    .title(areaName)
                    .snippet(address)
                    .position(latLng));
//            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
//        LatLng stedium = new LatLng(22.5690538, 88.4090443);
//        LatLng ultadanga = new LatLng(22.0666742, 88.06981180000002);
//        mMap.setMyLocationEnabled(true);
//        mMap.addMarker(new MarkerOptions().position(ultadanga).title("Ultadanga"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(ultadanga));
    }
}
