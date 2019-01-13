package com.itcs.aihome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.OkHttpClient;
import okhttp3.WebSocket;

public class Adapter extends PagerAdapter {
    private List<Model> models;
    private LayoutInflater inflater;
    private Context context;
    public int flag = 0;
    private String baseUrl;
    private String TAG;
    private AssetManager assetManager;
    private Typeface typeface;
    private Socket socket;
    private SocketListener listener;

    public Adapter(List<Model> models, Context context) {
        this.models = models;
        this.context = context;
    }

    private static class ViewHolder {
        ImageView imageView, ex;
        CardView cardView;
        ImageButton cog;
        TextView status, devices, detail;

    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view.equals(o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.light_card, container, false);
        final ViewHolder viewHolder = new ViewHolder();

        assetManager = context.getAssets();
        typeface = Typeface.createFromAsset(assetManager, "fonts/valeraround.ttf");
        startSocket();

        viewHolder.imageView = view.findViewById(R.id.image);
        viewHolder.status = view.findViewById(R.id.status);
        viewHolder.devices = view.findViewById(R.id.devices);
        viewHolder.detail = view.findViewById(R.id.detail);
        viewHolder.cardView = view.findViewById(R.id.card);
        viewHolder.ex = view.findViewById(R.id.ex);
        viewHolder.cog = view.findViewById(R.id.cog);
        viewHolder.imageView.setTag(models.get(position).getTag());

        viewHolder.status.setTypeface(typeface);
        viewHolder.devices.setTypeface(typeface);
        viewHolder.detail.setTypeface(typeface);

        TAG = context.getClass().getSimpleName();

        if (models.get(position).getStatus().equals("on")) {
            flag = 1;
            viewHolder.cardView.setBackgroundResource(R.drawable.bg_gradient);
            viewHolder.status.setTextColor(Color.WHITE);
            viewHolder.devices.setTextColor(Color.WHITE);
            viewHolder.cog.setBackgroundResource(R.drawable.cog_white);
            if (viewHolder.imageView.getTag().equals("light")) {
                viewHolder.imageView.setImageResource(R.drawable.bulb_white);
            } else {
                viewHolder.imageView.setImageResource(R.drawable.ac_white);
            }
            viewHolder.detail.setTextColor(Color.WHITE);
            viewHolder.ex.setImageResource(R.drawable.exclamation_white);
        } else if (models.get(position).getStatus().equals("off")) {
            flag = 1;
            viewHolder.cardView.setBackgroundResource(R.drawable.box);
            viewHolder.status.setTextColor(Color.parseColor("#414141"));
            viewHolder.devices.setTextColor(Color.parseColor("#414141"));
            viewHolder.cog.setBackgroundResource(R.drawable.cog);
            if (viewHolder.imageView.getTag().equals("light")) {
                viewHolder.imageView.setImageResource(models.get(position).getImage());
            } else {
                viewHolder.imageView.setImageResource(models.get(position).getImage());
            }
            viewHolder.detail.setTextColor(Color.parseColor("#414141"));
            viewHolder.ex.setImageResource(R.drawable.exclamation);
        }

        viewHolder.status.setText(models.get(position).getStatus().toUpperCase());
        viewHolder.devices.setText(models.get(position).getDevices());

        viewHolder.cog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Detail.class);
                intent.putExtra("device", models.get(position).getDevices());
                intent.putExtra("status", models.get(position).getStatus());
                intent.putExtra("gambar", models.get(position).getImage());
                intent.putExtra("iddevice", models.get(position).getIddevice());
                intent.putExtra("pin", models.get(position).getPin());
                intent.putExtra("type", viewHolder.imageView.getTag().toString());
                context.startActivity(intent);
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                baseUrl = models.get(position).getBlynkurl();
                String action = "";
                if (flag == 0) {
                    flag = 1;
                    action = "on";
                    String url = baseUrl + "?value=1";
                    viewHolder.cardView.setBackgroundResource(R.drawable.bg_gradient);
                    viewHolder.status.setTextColor(Color.WHITE);
                    viewHolder.devices.setTextColor(Color.WHITE);
                    viewHolder.cog.setBackgroundResource(R.drawable.cog_white);
                    if (viewHolder.imageView.getTag().equals("light")) {
                        viewHolder.imageView.setImageResource(R.drawable.bulb_white);
                    } else {
                        viewHolder.imageView.setImageResource(R.drawable.ac_white);
                    }
                    viewHolder.detail.setTextColor(Color.WHITE);
                    viewHolder.ex.setImageResource(R.drawable.exclamation_white);
//                    makeRequest(url);
//                    makeRequest2(models.get(position).getBaseUrl(),  "device", action,  models.get(position).getIddevice());
                } else {
                    flag = 0;
                    action = "off";
                    String url = baseUrl + "?value=0";
                    viewHolder.cardView.setBackgroundResource(R.drawable.box);
                    viewHolder.status.setTextColor(Color.parseColor("#414141"));
                    viewHolder.devices.setTextColor(Color.parseColor("#414141"));
                    viewHolder.cog.setBackgroundResource(R.drawable.cog);
                    if (viewHolder.imageView.getTag().equals("light")) {
                        viewHolder.imageView.setImageResource(models.get(position).getImage());
                    } else {
                        viewHolder.imageView.setImageResource(models.get(position).getImage());
                    }
                    viewHolder.detail.setTextColor(Color.parseColor("#414141"));
                    viewHolder.ex.setImageResource(R.drawable.exclamation);
//                    makeRequest(url);
//                    makeRequest2(models.get(position).getBaseUrl(),  "device", action,  models.get(position).getIddevice());
                }
            }
        });
        container.addView(view, 0);
        return view;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager) container).removeView((View) object);
        if(socket.connected()) {
            socket.disconnect();
        }
    }

    @Override
    public float getPageWidth(int position) {
        return (0.5f);
    }

    private void changeStatus(String iddevice, String status) {
        String data = getDefaults("data", context);
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray jsonArray = jsonObject.getJSONArray("controller");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String blynk_key = jsonObject1.getString("blynk_key");
                if (jsonObject1.isNull("device")) {
                } else {
                    JSONArray devices = jsonObject1.getJSONArray("device");
                    for (int x = 0; x < devices.length(); x++) {
                        final JSONObject jsonObject2 = devices.getJSONObject(x);
                        String pin = jsonObject2.getString("pin");
                        String baseUrl = "http://188.166.206.43:8080/" + blynk_key + "/get/" + pin;
                        String iddevice_data = jsonObject2.getString("iddevice");
                        if (iddevice_data.equals(iddevice)) {
                            jsonObject2.put("status", status);
                        }
                    }
                }
            }
            setDefaults("data", jsonObject.toString(), context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void Update(int position, ViewHolder viewHolder) {
        String update = getDefaults("update_adapter", context);
        if (TextUtils.isEmpty(update)) {
            Log.e(TAG, "No update");
        } else {
            try {
                JSONArray jsonArray = new JSONArray(update);
                for (int x = 0; x < jsonArray.length(); x++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(x);
                    String iddevice = jsonObject.getString("iddevice");
                    String statusp = jsonObject.getString("status");
                    if (models.get(position).getIddevice().equals(iddevice)) {
                        if (statusp.equals("on")) {
                            flag = 1;
                            viewHolder.status.setTextColor(Color.WHITE);
                            viewHolder.devices.setTextColor(Color.WHITE);
                            viewHolder.cardView.setBackgroundResource(R.drawable.bg_gradient);
                            viewHolder.cog.setBackgroundResource(R.drawable.cog_white);
                            viewHolder.detail.setTextColor(Color.WHITE);
                            viewHolder.ex.setImageResource(R.drawable.exclamation_white);
                            if (models.get(position).getTag().equals("lampu")) {
                                viewHolder.imageView.setImageResource(R.drawable.bulb_white);
                            } else {
                                viewHolder.imageView.setImageResource(R.drawable.ac_white);
                            }
                            viewHolder.status.setTextColor(Color.WHITE);
                            viewHolder.devices.setTextColor(Color.WHITE);
                            viewHolder.status.setText("ON");
                        } else {
                            flag = 0;
                            viewHolder.cardView.setBackgroundResource(R.drawable.box);
                            if (viewHolder.imageView.getTag().toString().equals("lampu")) {
                                viewHolder.imageView.setImageResource(R.drawable.bulb);
                            } else {
                                viewHolder.imageView.setImageResource(R.drawable.ac);
                            }
                            viewHolder.cog.setBackgroundResource(R.drawable.cog);
                            viewHolder.status.setTextColor(Color.BLACK);
                            viewHolder.devices.setTextColor(Color.BLACK);
                            viewHolder.status.setText("OFF");
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void makeRequest(final String baseUrl) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, baseUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Response dari blynk : " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
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


    public void makeRequest2(final String baseUrl, final String tipe, final String action, final String iddevice) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
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
                params.put("idtrigger", iddevice);
                params.put("type", tipe);
                params.put("action", action);
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

    public void toggleButton(View view, int position) {
        baseUrl = models.get(position).getBlynkurl();
        String action = "";
        if (flag == 0) {
            flag = 1;
            action = "on";
            String url = baseUrl + "?value=1";

//            makeRequest(url);
//            makeRequest2(models.get(position).getBaseUrl(),  "device", action,  models.get(position).getIddevice());
        } else {
            flag = 0;
            action = "off";
            String url = baseUrl + "?value=0";
//            makeRequest(url);
//            makeRequest2(models.get(position).getBaseUrl(),  "device", action,  models.get(position).getIddevice());
        }
    }
    
    private void disconnectSocket() {
        if(socket.connected()) {
            socket.disconnect();
        } else {
            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        }
    }

    private void startSocket() {
        listener = new SocketListener();
        socket = listener.getSocket();
        socket.connect();
        Log.e(TAG, "Socket IO running");
    }
}
