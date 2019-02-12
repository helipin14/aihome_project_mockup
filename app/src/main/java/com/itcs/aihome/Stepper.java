package com.itcs.aihome;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.itcs.aihome.Fragment.HostConfigurationFragment;
import com.itcs.aihome.Fragment.QRCodeScanning;
import com.itcs.aihome.Fragment.ScanningDeviceFragment;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.List;

public class Stepper extends AppCompatActivity {

    private StepperAdapter adapter;
    private HeightWrappingViewPager viewPager;
    private DotsIndicator dotsIndicator;
    private Button nextbtn, backbtn;
    private String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stepper);
        init();
        main();
    }

    private void init() {
        viewPager = findViewById(R.id.stepper_viewpager);
        dotsIndicator = findViewById(R.id.stepper_dots);
        nextbtn = findViewById(R.id.nextbtn);
        backbtn = findViewById(R.id.backbtn);
    }

    private void main() {
        setupViewPager();
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToNextSlide();
            }
        });
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToPrevSlide();
            }
        });
        handlingElement(viewPager.getCurrentItem());
    }

    private void setupViewPager() {
        adapter = new StepperAdapter(getApplicationContext(), getSupportFragmentManager());
        loadLayouts();
        viewPager.setAdapter(adapter);
        viewPager.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                Log.e(TAG, "Selected page : " + String.valueOf(i));
                handlingElement(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        dotsIndicator.setViewPager(viewPager);
    }

    private void loadLayouts() {
        adapter.AddFragment(new QRCodeScanning());
        adapter.AddFragment(new HostConfigurationFragment());
        adapter.AddFragment(new ScanningDeviceFragment());
    }

    private void handlingElement(int position) {
        switch (position) {
            case 0:
                backbtn.setVisibility(View.GONE);
                nextbtn.setVisibility(View.VISIBLE);
                break;
            case 1:
                backbtn.setVisibility(View.VISIBLE);
                nextbtn.setVisibility(View.VISIBLE);
                break;
            case 2:
                backbtn.setVisibility(View.VISIBLE);
                nextbtn.setVisibility(View.GONE);
                break;
        }
    }

    private void goToNextSlide() {
        int nextslide = viewPager.getCurrentItem() + 1;
        if(nextslide < adapter.getCount()) {
            viewPager.setCurrentItem(nextslide);
        } else {
            viewPager.setCurrentItem(adapter.getCount() - 1);
        }
    }

    private void backToPrevSlide() {
        int prev = viewPager.getCurrentItem() - 1;
        if(prev < adapter.getCount()) {
            viewPager.setCurrentItem(prev);
        } else {
            viewPager.setCurrentItem(0);
        }
    }

    private void requestPermission() {
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }
}
