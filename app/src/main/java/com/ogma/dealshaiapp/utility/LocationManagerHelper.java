package com.ogma.dealshaiapp.utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

/**
 * Support class for {@link LocationManager} handles permission management for api 23 and above
 * <p>
 * Note: Call utility mthod {@link #getLocation(LocationListener)} to quickly get the location either last known or request location callback from the {@link LocationManager}
 * </p>
 *
 * @author Md Sajjad Mistri
 * @since version 1.0
 */
public class LocationManagerHelper {
    private static final String TAG = LocationManagerHelper.class.getSimpleName();

    public static final int PERMISSION_REQUEST_LOCATION = 200;

    private static final long LOCATION_UPDATE_MIN_INTERVAL = 0;
    private static final float LOCATION_UPDATE_MIN_DISTANCE = 0.0f;

    private Context context;
    private LocationManager locationManager;
    private Criteria criteria;
    private AlertDialog alertDialog;

    /**
     * Retrieve the location of the device
     * <p>
     * Note: @Override onRequestPermissionsResult in order to process permission results
     * Note: May return null or {@link #getLastKnownLocation()} or {@link #startRequestingLocationUpdates(LocationListener)}
     * See {@link #startRequestingLocationUpdates(int, float, LocationListener)}
     * </p>
     *
     * @return LocationModel or null
     * @since version 1.0
     */
    @Nullable
    public Location getLocation(LocationListener locationListener) {
        if (isPermissionGranted()) {
            if (isProviderEnabled()) {
                if (locationListener != null)
                    startRequestingLocationUpdates(locationListener);
                Location location = getLastKnownLocation();
                if (location != null) {
                    Log.e(TAG, "onBubbleClick: Last known latitude: " + location.getLatitude());
                    Log.e(TAG, "onBubbleClick: Last known longitude: " + location.getLongitude());
                    return location;
                }
            } else {
                promptForProvider(true);
            }
        } else {
            requestLocationPermission();
        }

        return null;
    }

    public LocationManagerHelper(Context context) {
        if (context instanceof Activity) {
            this.context = context;
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        } else {
            throw new IllegalArgumentException("context must an Activity Context!");
        }
    }

    /**
     * Retrieve the {@link LocationManager} associated
     * <p>
     * Note: Call this method to get the {@link LocationManager} to remove updates in activity's onDestroy()
     * </p>
     *
     * @return LocationModel or null
     * @since version 1.0
     */
    public LocationManager getLocationManager() {
        return locationManager;
    }

    /**
     * Retrieve the default {@link Criteria} when requesting location updates
     *
     * @return default criteria
     */
    private Criteria getDefaultCriteria() {
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        criteria.setAltitudeRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(false);
        criteria.setBearingRequired(false);
        return criteria;
    }

    /**
     * Set the {@link Criteria} for location updates
     */
    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    /**
     * Retrieve the last known location of the device
     * <p>
     * Note: @Override onRequestPermissionsResult in order to process permission results
     * Note: May return null. See {@link #startRequestingLocationUpdates(int, float, LocationListener)}
     * </p>
     *
     * @return LocationModel or null
     * @since version 1.0
     */
    @Nullable
    public Location getLastKnownLocation() {
        Location lastKnownLocation = null;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Criteria criteria = this.criteria == null ? getDefaultCriteria() : this.criteria;
            String provider = locationManager.getBestProvider(criteria, true);
            if (provider != null) {
                Log.e("LocationModel manager", "requesting last known location with provider " + provider);
                lastKnownLocation = locationManager.getLastKnownLocation(provider);
                if (lastKnownLocation != null) {
                    Log.e("getLastKnownLocation: ", "latitude: " + lastKnownLocation.getLatitude());
                    Log.e("getLastKnownLocation: ", "longitude: " + lastKnownLocation.getLongitude());
                }
            }
        } else {
            Log.e(TAG, "getLastKnownLocation: PERMISSION NOT GRANTED");
        }

        return lastKnownLocation;
    }

    /**
     * Start requesting location updates
     * <p>
     * Note: Uses Default interval 0 and default distance 0 while requesting location updates.
     * </p>
     *
     * @param locationListener description of the parameter
     * @since version
     */
    public void startRequestingLocationUpdates(LocationListener locationListener) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Criteria criteria = this.criteria == null ? getDefaultCriteria() : this.criteria;
            String provider = locationManager.getBestProvider(criteria, true);
            if (provider != null) {
                locationManager.requestLocationUpdates(provider, LOCATION_UPDATE_MIN_INTERVAL, LOCATION_UPDATE_MIN_DISTANCE, locationListener);
                Log.e("LocationModel manager", "requesting updates with provider " + provider);
            }
        } else {
            Log.e(TAG, "getLastKnownLocation: PERMISSION NOT GRANTED");
        }
    }

    /**
     * Start requesting location updates
     * <p>
     * {@link #startRequestingLocationUpdates}
     * </p>
     *
     * @param locationListener description of the parameter
     * @since version
     */
    public void startRequestingLocationUpdates(int minIntervalMills, float minDistance, LocationListener locationListener) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Criteria criteria = this.criteria == null ? getDefaultCriteria() : this.criteria;
            String provider = locationManager.getBestProvider(criteria, true);
            if (provider != null) {
                locationManager.requestLocationUpdates(provider, minIntervalMills, minDistance, locationListener);
                Log.e("LocationModel manager", "requesting updates with provider " + provider);
            }
        } else {
            Log.e(TAG, "getLastKnownLocation: PERMISSION NOT GRANTED");
        }
    }

    /**
     * Retrieve whether LocationModel Permission is granted
     *
     * @return boolean value
     * @since version 1.0
     */
    public boolean isPermissionGranted() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Request location permission
     *
     * @since version 1.0
     */
    public void requestLocationPermission() {
        if (!isPermissionGranted())
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_LOCATION);
    }

    /**
     * Retrieve whether the provider is enabled
     *
     * @return boolean value
     * @since version 1.0
     */
    public boolean isProviderEnabled() {
        Criteria criteria = this.criteria == null ? getDefaultCriteria() : this.criteria;
        String provider = locationManager.getBestProvider(criteria, true);
        return provider != null;
    }

    /**
     * Prompt to enable provider
     *
     * @since version 1.0
     */
    public void promptForProvider(boolean prompt) {
        if (prompt) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage("Turn on location in high accuracy mode");
            dialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    if (myIntent.resolveActivity(context.getPackageManager()) != null)
                        context.startActivity(myIntent);
                    else {
                        Toast.makeText(context, "Settings not found", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialog.setCancelable(false);
            alertDialog = dialog.create();
            alertDialog.show();
        } else {
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
        }
    }
}
