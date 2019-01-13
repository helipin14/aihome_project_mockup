package com.itcs.aihome;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Status extends AppCompatActivity {

    private ListView liststatus;
    private StatusAdapter statusAdapter;
    private List<StatusModel> relay;
    private String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status);
        liststatus = findViewById(R.id.liststatus);

        if(getDefaults("data", getApplicationContext()) != null) {
            data = getDefaults("data", getApplicationContext());
        }

        setData();
    }

    private void setData() {
        relay = new ArrayList<StatusModel>();
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray jsonArray = jsonObject.getJSONArray("controller");
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String blynk_key = jsonObject1.getString("blynk_key");
                String idcontroller = jsonObject1.getString("idcontroller");
                String name = jsonObject1.getString("name");
                relay.add(new StatusModel(name, idcontroller, blynk_key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        statusAdapter = new StatusAdapter(getApplicationContext(), relay);
        liststatus.setAdapter(statusAdapter);
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

}
