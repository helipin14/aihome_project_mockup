package com.itcs.aihome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.TextView;

import java.util.List;

public class GroupSettings extends AppCompatActivity {

    private WrapContentViewPager viewPager;
    private TabLayout tabLayout;
    private GroupSettingsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_settings);
        init();
        main();
    }

    private void init() {
        viewPager = findViewById(R.id.tab_viewpager);
        tabLayout = findViewById(R.id.tab_layout);
    }

    private void main() {
        setupViewPager();
    }

    private void setupViewPager() {
        adapter = new GroupSettingsAdapter(getSupportFragmentManager());
        adapter.AddFragment(new GroupSettingFragmentUsers(), "Users");
        adapter.AddFragment(new GroupSettingFragmentDevices(), "Devices");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }
}
