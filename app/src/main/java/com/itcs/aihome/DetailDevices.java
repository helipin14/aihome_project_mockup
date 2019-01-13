package com.itcs.aihome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailDevices extends AppCompatActivity {

    private ExpandableGridView gridView;
    private ImageButton imageButton;
    private GridViewAdapter adapter;
    private TextView textView;
    private String data;
    private List<Model> models;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_devices);

        imageButton = findViewById(R.id.arrow_back);

        gridView = (ExpandableGridView) findViewById(R.id.detail_gridview);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if(getDefaults("data", getApplicationContext()) != null) {
            data = getDefaults("data", getApplicationContext());
        }

        getData();
    }

    private void getData() {
        Intent intent = getIntent();
        String tipe = intent.getStringExtra("tipe");
        setData(tipe);
    }

    private void setData(String tipe) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray jsonArray = jsonObject.getJSONArray("controller");
            models = new ArrayList<>();
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String blynk_key = jsonObject1.getString("blynk_key");
                if(jsonObject1.isNull("device")) {
                } else {
                    JSONArray devices = jsonObject1.getJSONArray("device");
                    for(int x = 0; x < devices.length(); x++) {
                        String status = "";
                        String device_name = "";
                        String baseUrl = "";
                        String iddevice = "";
                        String blynkurl = "";
                        String tag = "";
                        String pin = "";
                        int image = 0;
                        final JSONObject jsonObject2 = devices.getJSONObject(x);
                        String type = jsonObject2.getString("type");
                        if(tipe.equals("light") && type.equals("light")) {
                            pin = jsonObject2.getString("pin");
                            device_name = jsonObject2.getString("device_name");
                            baseUrl = "http://dataaihome.itcs.co.id/deviceTrigger.php";
                            iddevice = jsonObject2.getString("iddevice");
                            status = jsonObject2.getString("status");
                            blynkurl = "http://188.166.206.43:8080/" + blynk_key + "/update/" + pin;
                            tag = "light";
                            image = R.drawable.bulb;
                            models.add(new Model(image, status, device_name, baseUrl, tag, iddevice, blynkurl, pin));
                        } else if(tipe.equals("ac") && type.equals("ac")){
                            pin = jsonObject2.getString("pin");
                            device_name = jsonObject2.getString("device_name");
                            baseUrl = "http://dataaihome.itcs.co.id/deviceTrigger.php";
                            iddevice = jsonObject2.getString("iddevice");
                            status = jsonObject2.getString("status");
                            blynkurl = "http://188.166.206.43:8080/" + blynk_key + "/update/" + pin;
                            tag = "AC";
                            image = R.drawable.ac;
                            models.add(new Model(image, status, device_name, baseUrl, tag, iddevice, blynkurl, pin));
                        }
                    }
                }
            }
            adapter = new GridViewAdapter(getApplicationContext(), models);
            gridView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }
}
