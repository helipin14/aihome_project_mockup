package com.itcs.aihome;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.transform.OutputKeys;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class GridViewAdapter extends BaseAdapter {

    private final Context context;
    private List<Model> model;
    public int height = 0;
    private int startOffset = 100;
    private String TAG, idupdate, status;
    private Socket socket;
    private int flag = 0;

    private static class ViewHolder {
        CardView cardView;
        ImageButton cog;
        TextView status, devices;
        ImageView imageView;
    }

    public GridViewAdapter(Context context, List<Model> model, Socket socket) {
        this.context = context;
        this.model = model;
        if(socket != null) {
            this.socket = socket;
        }
    }

    @Override
    public int getCount() {
        return model.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        startOffset += 100;
        View view = convertView;
        final ViewHolder viewHolder;
        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.light_detail, null);
            TAG = context.getClass().getSimpleName();

            viewHolder.imageView = view.findViewById(R.id.image);
            viewHolder.status = view.findViewById(R.id.status);
            viewHolder.devices = view.findViewById(R.id.devices);
            viewHolder.cardView = view.findViewById(R.id.light_cards);
            viewHolder.cardView.setTag(model.get(i).getIddevice());
            viewHolder.cog = view.findViewById(R.id.btncog);
            viewHolder.imageView.setTag(model.get(i).getTag());

            if(model.get(i).getStatus() == 0) {
                viewHolder.cardView.setEnabled(false);
                status = "ON";
                viewHolder.cardView.setBackgroundResource(R.drawable.bg_gradient);
                viewHolder.status.setTextColor(Color.WHITE);
                viewHolder.devices.setTextColor(Color.WHITE);
                viewHolder.cog.setBackgroundResource(R.drawable.cog_white);
                if(viewHolder.imageView.getTag().toString().equals("light")) {
                    viewHolder.imageView.setImageResource(R.drawable.bulb_white);
                } else {
                    viewHolder.imageView.setImageResource(R.drawable.ac_white);
                }
                enableCardView(viewHolder);
            } else if(model.get(i).getStatus() == 1) {
                viewHolder.cardView.setEnabled(false);
                status = "OFF";
                viewHolder.cardView.setBackgroundResource(R.drawable.box);
                if(viewHolder.imageView.getTag().toString().equals("light")) {
                    viewHolder.imageView.setImageResource(R.drawable.bulb);
                } else {
                    viewHolder.imageView.setImageResource(R.drawable.ac);
                }
                viewHolder.cog.setBackgroundResource(R.drawable.cog);
                viewHolder.status.setTextColor(Color.BLACK);
                viewHolder.devices.setTextColor(Color.BLACK);
                enableCardView(viewHolder);
            }

            viewHolder.status.setText(status.toUpperCase());
            viewHolder.devices.setText(model.get(i).getDevices());

            viewHolder.cog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, Detail.class);
                    intent.putExtra("position", String.valueOf(i));
                    intent.putExtra("device",model.get(i).getDevices());
                    intent.putExtra("status", model.get(i).getStatus());
                    intent.putExtra("gambar", model.get(i).getImage());
                    intent.putExtra("iddevice", model.get(i).getIddevice());
                    intent.putExtra("type", viewHolder.imageView.getTag().toString());
                    context.startActivity(intent);
                }
            });

            viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int action;
                    if(model.get(i).getFlag() == 0) {
                        action = 0;
                        model.get(i).setFlag(1);
                    } else {
                        action = 1;
                        model.get(i).setFlag(0);
                    }
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("idaccess", model.get(i).getIdaccess());
                        jsonObject.put("iddevice", model.get(i).getIddevice());
                        jsonObject.put("action", action);
                        socket.emit("trigger", jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        return view;
    }

    private void setAnimation(View view, int position) {
        int lastPosition = 0;
        if(position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade);
            animation.setStartOffset(startOffset);
            Log.e("Start Offset", String.valueOf(startOffset));
            animation.setDuration(300);
            view.setAnimation(animation);
            lastPosition = position;
        }
    }

    public void makeRequest(final String baseUrl, final String tipe, final String action, final String iddevice) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
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

    private void changeStatus(String iddevice, String status) {
        String data = getDefaults("data", context);
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray jsonArray = jsonObject.getJSONArray("controller");
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                if(!jsonObject1.isNull("device")) {
                    JSONArray devices = jsonObject1.getJSONArray("device");
                    for(int x = 0; x < devices.length(); x++) {
                        JSONObject jsonObject2 = devices.getJSONObject(x);
                        String iddevice_data = jsonObject2.getString("iddevice");
                        if(iddevice_data.equals(iddevice)) {
                            jsonObject2.put("status", status);
                        }
                    }
                }
            }
            Log.e(TAG, "Hasil perubahan : " + jsonObject.toString());
            setDefaults("data", jsonObject.toString(), context);
        } catch(JSONException e) {
            e.printStackTrace();
        }
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

    private void makeRequestBlynk(String baseUrl) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, baseUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage());
            }
        });
        requestQueue.add(stringRequest);
    }

    private void sinkronUpdate() {
        socket.emit("connection", "true")
                .on("onOffApp", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        socket.emit("idupdate", idupdate);
                    }
                });
    }

    private JSONObject getData() {
        JSONObject jsonObject = new JSONObject();
        String data = getDefaults("data", context);
        if(!TextUtils.isEmpty(data)) {
            try {
                jsonObject = new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    private void getIdUpdate() {
        try {
            JSONObject jsonObject = getData();
            idupdate = jsonObject.getString("idupdate");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void changeIdUpdate() {
        try {
            JSONObject jsonObject = getData();
            jsonObject.put("idupdate", idupdate);
            Log.e(TAG, jsonObject.toString());
            setDefaults("data", jsonObject.toString(), context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void enableCardView(final ViewHolder viewHolder) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                viewHolder.cardView.setEnabled(true);
            }
        }, 500);
    }
}
