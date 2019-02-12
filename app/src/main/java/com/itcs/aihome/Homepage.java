package com.itcs.aihome;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;


public class Homepage extends AppCompatActivity implements LocationListener{

    public static final String TAG = Homepage.class.getSimpleName();
    private static Homepage mInstance;
    private BottomNavigationView bottomNavigationView;
    private int mSelectedItem;
    private static final String SELECTED_ITEM = "arg_selected_item";
    private Double latitude, longtitude;
    private Location location;
    private final static int PLAY_SERVICES_REQUEST = 1000;
    private OkHttpClient client;
    private LocationManager locationManager;
    private LocationListener listener;
    private String cityname = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        mInstance = this;
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        client = new OkHttpClient();
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        checkConnection();
        loadFragment(HomeFragment.newInstance());
        startService(new Intent(Homepage.this, CheckConnection.class));
        startService(new Intent(Homepage.this, RunAfterBootService.class));
        getLocation();
    }

    public static synchronized Homepage getInstance() {
        return mInstance;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(SELECTED_ITEM, mSelectedItem);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;
                    switch (menuItem.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = HomeFragment.newInstance();
                            loadFragment(selectedFragment);
                            break;
                        case R.id.nav_devices:
                            selectedFragment = DevicesFragment.newInstance();
                            loadFragment(selectedFragment);
                            break;
                        case R.id.nav_kwh:
                            selectedFragment = KWHFragment.newInstance();
                            loadFragment(selectedFragment);
                            break;
                        case R.id.nav_cctv:
                            selectedFragment = CCTVFragment.newInstance();
                            loadFragment(selectedFragment);
                            break;
                        case R.id.nav_account:
                            selectedFragment = AccountFragment.newInstance();
                            loadFragment(selectedFragment);
                            break;
                    }
                    return true;
                }
            };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    private void checkConnection() {
        boolean isConnected = CheckConnection.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Connected to Internet";
            color = R.color.colorGreen;
        } else {
            message = "Not connected to Internet";
            color = R.color.colorRed;
        }
        Snackbar snackbar = Snackbar.make(findViewById(R.id.parent_layout_container), message, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundResource(color);
        snackbar.show();
    }

    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private boolean isPermitted() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void getLocation(){
        if(isPermitted()) {
           requestPermission();
        } else {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 ,1000, this);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longtitude = location.getLongitude();
        Log.e(TAG, "Latitude : " + String.valueOf(location.getLatitude()));
        Log.e(TAG, "Longitude : " + String.valueOf(location.getLongitude()));
        setDefaults("city", getCityName(latitude, longtitude), getApplicationContext());
    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.e(TAG, "Status changed : " + s);
    }

    private String getCityName(Double latitude, Double longtitude) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longtitude, 1);
            if(addresses.size() > 0) {
                return addresses.get(0).getLocality();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

}
