package com.itcs.aihome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    private String temps, hum, TAG, username, iduser;
    private JSONObject jsonObject;

    public interface VolleyCallback {
        void onSuccess(String result);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.setIndeterminate(true);

        getDataUser();
        getData();
        Log.e(TAG, "Response dari yang aku tambahkan disini : " + getDefaults("data", getApplicationContext()));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String data = getDefaults("data", getApplicationContext());
                String weather = getDefaults("weather", getApplicationContext());
                if((!TextUtils.isEmpty(data) && !TextUtils.isEmpty(weather)) || (!TextUtils.isEmpty(data) || !TextUtils.isEmpty(weather))) {
                    Intent intent = new Intent(SplashActivity.this, Homepage.class);
                    startActivity(intent);
                }
            }
        }, 3000);
    }

    public void getData() {
        class GetWeather extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                getAllData();
                getWeather();
                return null;
            }
        }
        new GetWeather().execute();
    }

    public void getWeather() {
        HttpHandler sh = new HttpHandler();
        String jsonstr = sh.makeRequestCall(config.weather_url);
        Log.e(TAG, "Weather response from url : " + jsonstr);
        if(jsonstr != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonstr);
                JSONObject jsonObject1 = jsonObject.getJSONObject("main");
                String temp = jsonObject1.getString("temp");
                String humidity = jsonObject1.getString("humidity");
                float temperature = Float.parseFloat(temp);
                temperature -= 273.15;
                temps = String.valueOf(new DecimalFormat("#").format(temperature)) + "°";
                hum = humidity + "%";
                JSONObject weather = new JSONObject();
                weather.put("temp", temps);
                weather.put("hum", hum);
                setDefaults("weather", weather.toString(), getApplicationContext());
            } catch (final JSONException e) {
                Log.e(TAG, "JSON parsing error : " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "JSON parsing error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    private void getDataUser() {
        String data = getDefaults("data_user", getApplicationContext());
        if(!TextUtils.isEmpty(data)) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                iduser = jsonObject.getString("iduser");
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getAllData() {
        String url = "http://dataaihome.itcs.co.id/getAll_Things.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    JSONObject user = jsonObject.getJSONObject("user");
                    username = user.getString("name");
                    Log.e(TAG, "Response from url : " + response);
                    final JSONArray jsonArray = jsonObject.getJSONArray("controller");
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String blynk_key = jsonObject1.getString("blynk_key");
                        if(jsonObject1.isNull("device")) {
                        } else {
                            JSONArray devices = jsonObject1.getJSONArray("device");
                            for(int x = 0; x < devices.length(); x++) {
                                final JSONObject jsonObject2 = devices.getJSONObject(x);
                                String pin = jsonObject2.getString("pin");
                                String baseUrl = "http://188.166.206.43:8080/" + blynk_key + "/get/" + pin;
                                getStatus(baseUrl, new VolleyCallback() {
                                    String status = "";
                                    @Override
                                    public void onSuccess(String result) {
                                        status = result;
                                        try {
                                            jsonObject2.put("status", status);
                                            setDefaults("data", jsonObject.toString(), getApplicationContext());
                                        } catch(JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("iduser", iduser);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void getStatus(String url, final VolleyCallback volleyCallback) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Home page Volley Callback Response from url : " + response);
                String status = "";
                try {
                    JSONArray jsonArray1 = new JSONArray(response);
                    if(jsonArray1.getString(0).equals("0")) {
                        status = "off";
                    } else {
                        status = "on";
                    }
                    volleyCallback.onSuccess(status);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "There was an error : " + error.getMessage());
            }
        });
        queue.add(stringRequest);
    }

    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    private void UpdateStatus(final String status, final String iddevice) {
        String url = "http://dataaihome.itcs.co.id/setDevice_Stage.php";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Response from url : " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("iddevice", iddevice);
                params.put("status", status);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}
