package com.itcs.aihome.Fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itcs.aihome.R;
import com.itcs.aihome.WiFiAdapter;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

public class ScanningDeviceFragment extends Fragment {

    private RecyclerView wifiList;
    private WiFiAdapter adapter;
    private List<String> networks;
    private List<ScanResult> scanResults;
    private WifiManager manager;
    private String TAG = this.getClass().getSimpleName();
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.scanning_device, container, false);
        init(view);
        setupWiFiList();
        return view;
    }

    private void init(View view) {
        manager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiList = view.findViewById(R.id.listwifi);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
    }

    private void setupWiFiList() throws NullPointerException {
        networks = new ArrayList<>();
        if(!manager.isWifiEnabled()) {
            requestPermission();
        }
        manager.startScan();
        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                if(success) {
                    scanResults = manager.getScanResults();
                    if(scanResults.size() > 0) {
                        for (int i = 0; i < scanResults.size(); i++) {
                            networks.add(scanResults.get(i).SSID);
                        }
                    }
                }
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        if(networks.size() > 0) {
            adapter = new WiFiAdapter(getContext(), networks);
            wifiList.setLayoutManager(new LinearLayoutManager(getContext()));
            wifiList.setAdapter(adapter);
        }
    }

    private void requestPermission() {
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),  new String[] {Manifest.permission.ACCESS_WIFI_STATE}, 1);
        }
    }

}