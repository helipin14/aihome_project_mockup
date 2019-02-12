package com.itcs.aihome;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class GroupSettingsAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> fragmentTitles = new ArrayList<>();

    public GroupSettingsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
       return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentTitles.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitles.get(position);
    }

    public void AddFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        fragmentTitles.add(title);
    }
}
