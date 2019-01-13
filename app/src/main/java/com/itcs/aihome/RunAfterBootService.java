package com.itcs.aihome;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunAfterBootService extends Service {

    private Calendar calendar;
    private Date date;
    private int hour, minute, day;

    private static final String TAG_BOOT_BROADCAST_RECEIVER = "BOOT_BROADCAST_RECEIVER";
    public RunAfterBootService() {}

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        Log.d(TAG_BOOT_BROADCAST_RECEIVER, "RunAfterBootService onCreate() method");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String message = "RunAfterBootService onStartCommand() method.";
//        setOnTimer();
        Log.d(TAG_BOOT_BROADCAST_RECEIVER, message);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setOnTimer() {
        String data = getDefaults("data", getApplicationContext());
        if(!TextUtils.isEmpty(data)) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray controller = jsonObject.getJSONArray("controller");
                for (int i = 0; i < controller.length(); i++) {
                    JSONObject jsonObject1 = controller.getJSONObject(i);
                    final String blynk_key = jsonObject1.getString("blynk_key");
                    if(!jsonObject1.isNull("device")) {
                        JSONArray devices = jsonObject1.getJSONArray("device");
                        for(int x = 0; x < devices.length(); x++) {
                            JSONObject jsonObject2 = devices.getJSONObject(x);
                            String pin = jsonObject2.getString("pin");
                            String type = jsonObject2.getString("type");
                            String iddevice = jsonObject2.getString("iddevice");
                            String baseUrl = "http://dataaihome.itcs.co.id/deviceTrigger.php";
                            String blynkurl = "http://188.166.206.43:8080/" + blynk_key + "/update/" + pin;
                            Toast.makeText(this, jsonObject2.toString(), Toast.LENGTH_LONG).show();
//                            if(!jsonObject2.isNull("timer")) {
//                                JSONObject timer = jsonObject2.getJSONObject("timer");
//                                List<Integer> days = convertStringToList(timer.getString("hari"));
//                                List<Integer> start_time = convertStringToList(timer.getString("waktu_mulai"));
//                                List<Integer> end_time = convertStringToList(timer.getString("waktu_selesai"));
//                                for (int j = day - 1; j < days.size(); j++) {
//                                    if(start_time.get(0) == hour && start_time.get(1) == minute) {
//                                        makeRequest(baseUrl, "device", "on", iddevice);
//                                        makeRequestBlynk(blynkurl + "?value=1");
//                                    }
//                                    if(end_time.get(0) == hour && end_time.get(1) == minute) {
//                                        makeRequest(baseUrl, "device", "off", iddevice);
//                                        makeRequestBlynk(blynkurl + "?value=0");
//                                    }
//                                }
//                            }
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

    private void init() {
        calendar = Calendar.getInstance();
        date = calendar.getTime();
        hour = date.getHours();
        minute = date.getMinutes();
        day = date.getDay();
    }

    private List<Integer> convertStringToList(String str) {
        List<Integer> data = new ArrayList<>();
        str = str.replace("[", "");
        str = str.replace("]", "");
        str = str.replace(" ", "");
        String[] times = str.split(",");
        for(int i = 0; i < times.length; i++) {
            data.add(Integer.parseInt(times[i]));
        }
        return data;
    }

    private void makeRequestBlynk(String baseUrl) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, baseUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }

    public void makeRequest(final String baseUrl, final String tipe, final String action, final String iddevice) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("idtrigger", iddevice);
                params.put("type", tipe);
                params.put("action", action);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}
