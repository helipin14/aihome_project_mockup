package com.itcs.aihome.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.itcs.aihome.ColorItem;
import com.itcs.aihome.ColorItemAdapter;
import com.itcs.aihome.R;

import java.util.ArrayList;
import java.util.List;

public class HostConfigurationFragment extends Fragment {

    private RecyclerView recyclerView;
    private ColorItemAdapter adapter;
    private List<ColorItem> colors;
    private CheckBox showadvancedsettings;
    private LinearLayout advanced_settings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.home_config, container, false);
        init(view);
        setupColors();
        showAdvancedSettings();
        return view;
    }

    private void init(View view) {
        recyclerView = view.findViewById(R.id.color_list_config);
        showadvancedsettings = view.findViewById(R.id.show_advanced_settings_homeconfig);
        advanced_settings = view.findViewById(R.id.advanced_settings_home_config);
    }

    private void setupColors() {
        loadDataColor();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true));
        adapter = new ColorItemAdapter(colors, getContext());
        recyclerView.setAdapter(adapter);
    }

    private void loadDataColor() {
        colors = new ArrayList<>();
        colors.add(new ColorItem(0, Color.parseColor("#f5af19")));
        colors.add(new ColorItem(0, Color.parseColor("#e74c3c")));
        colors.add(new ColorItem(0, Color.parseColor("#3498db")));
        colors.add(new ColorItem(0, Color.parseColor("#2ecc71")));
        colors.add(new ColorItem(0, Color.parseColor("#f1c40f")));
        colors.add(new ColorItem(0, Color.parseColor("#9b59b6")));
        colors.add(new ColorItem(0, Color.parseColor("#e67e22")));
        colors.add(new ColorItem(0, Color.parseColor("#e67e22")));
        colors.add(new ColorItem(0, Color.parseColor("#2c3e50")));
    }

    private void showAdvancedSettings() {
        showadvancedsettings.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    advanced_settings.setVisibility(View.VISIBLE);
                } else {
                    advanced_settings.setVisibility(View.GONE);
                }
            }
        });
    }
}
