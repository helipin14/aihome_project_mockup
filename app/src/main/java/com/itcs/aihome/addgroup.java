package com.itcs.aihome;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.sysdata.widget.accordion.FancyAccordionView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class addgroup extends AppCompatActivity {

    RecyclerView recyclerView;
    private DeviceAdapter adapter;
    private List<DataDevice> dataDeviceList;
    private CheckBox showsettings;
    private LinearLayout advancedsettings;
//    private TextView devices;
//    private LinearLayout container;
//    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_group);
        init();
        main();
    }

    private void init() {
        recyclerView = findViewById(R.id.device_list);
        showsettings = findViewById(R.id.show_advanced_settings);
        advancedsettings = findViewById(R.id.advanced_settings);
//        devices = findViewById(R.id.selected_device);
//        container = findViewById(R.id.container_device_selected);
    }

    private void main() {
        loadData();
        adapter = new DeviceAdapter(dataDeviceList, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
        showAdvancedSettings();
    }

    private void loadData() {
        dataDeviceList = new ArrayList<>();
        String data =getDefaults("data", getApplicationContext());
        if(!TextUtils.isEmpty(data)) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray controller = jsonObject.getJSONArray("controller");
                for (int i = 0; i < controller.length(); i++) {
                    JSONObject jsonObject1 = controller.getJSONObject(i);
                    if(!jsonObject1.isNull("device")) {
                        JSONArray devices = jsonObject1.getJSONArray("device");
                        for(int x = 0; x < devices.length(); x++) {
                            JSONObject jsonObject2 = devices.getJSONObject(x);
                            dataDeviceList.add(new DataDevice(jsonObject2.getString("device_name"), jsonObject2.getString("iddevice"), 0));
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    private void showAdvancedSettings() {
        showsettings.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    advancedsettings.setVisibility(View.VISIBLE);
                } else {
                    advancedsettings.setVisibility(View.GONE);
                }
            }
        });
    }

//    private void selectedDevices() {
//        count = adapter.getItemCount();
//        devices.setText(String.valueOf(count) + " devices selected");
//    }

}
