package com.itcs.aihome;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.google.zxing.Result;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.List;
import android.net.wifi.ScanResult;
import android.widget.Toast;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class StepperAdapter extends FragmentPagerAdapter {

    private List<Fragment> layouts = new ArrayList<>();
    private Context context;

    public StepperAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int i) {
        return layouts.get(i);
    }

    @Override
    public int getCount() {
        return layouts.size();
    }

    public void AddFragment(Fragment fragment) {
        layouts.add(fragment);
    }
}
