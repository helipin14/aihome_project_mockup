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

public class GridViewAdapter extends BaseAdapter {

    private final Context context;
    private List<Model> model;
    private AlertDialog alertDialog;
    public int height = 0;
    private int flag = 0;
    private int startOffset = 100;
    private String TAG;

    private static class ViewHolder {
        CardView cardView;
        ImageButton cog, pencil;
        TextView status, devices;
        ImageView imageView;
    }

    public GridViewAdapter(Context context, List<Model> model) {
        this.context = context;
        this.model = model;
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
            viewHolder.pencil = view.findViewById(R.id.btnpencil);
            viewHolder.imageView.setTag(model.get(i).getTag());

            if(model.get(i).getStatus().equals("on")) {
                flag = 1;
                viewHolder.cardView.setBackgroundResource(R.drawable.bg_gradient);
                viewHolder.status.setTextColor(Color.WHITE);
                viewHolder.devices.setTextColor(Color.WHITE);
                viewHolder.cog.setBackgroundResource(R.drawable.cog_white);
                viewHolder.pencil.setBackgroundResource(R.drawable.pencil_white);
                if(viewHolder.imageView.getTag().toString() == "light") {
                    viewHolder.imageView.setImageResource(R.drawable.bulb_white);
                } else {
                    viewHolder.imageView.setImageResource(R.drawable.ac_white);
                }
            } else if(model.get(i).getStatus().equals("off")) {
                flag = 0;
                viewHolder.cardView.setBackgroundResource(R.drawable.box);
                if(viewHolder.imageView.getTag().toString().equals("light")) {
                    viewHolder.imageView.setImageResource(R.drawable.bulb);
                } else {
                    viewHolder.imageView.setImageResource(R.drawable.ac);
                }
                viewHolder.cog.setBackgroundResource(R.drawable.cog);
                viewHolder.pencil.setBackgroundResource(R.drawable.pencil);
                viewHolder.status.setTextColor(Color.BLACK);
                viewHolder.devices.setTextColor(Color.BLACK);
            }

                viewHolder.status.setText(model.get(i).getStatus().toUpperCase());
                viewHolder.devices.setText(model.get(i).getDevices());

                viewHolder.cog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, Detail.class);
                        intent.putExtra("device",model.get(i).getDevices());
                        intent.putExtra("status", model.get(i).getStatus());
                        intent.putExtra("gambar", model.get(i).getImage());
                        intent.putExtra("iddevice", model.get(i).getIddevice());
                        intent.putExtra("pin", model.get(i).getPin());
                        intent.putExtra("type", viewHolder.imageView.getTag().toString());
                        context.startActivity(intent);
                    }
                });

                viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String action = "";
                        if(flag == 0) {
                            flag = 1;
                            action = "on";
                            viewHolder.cardView.setBackgroundResource(R.drawable.bg_gradient);
                            viewHolder.status.setTextColor(Color.WHITE);
                            viewHolder.devices.setTextColor(Color.WHITE);
                            viewHolder.cog.setBackgroundResource(R.drawable.cog_white);
                            viewHolder.pencil.setBackgroundResource(R.drawable.pencil_white);
                            if(viewHolder.imageView.getTag().toString() == "light") {
                                viewHolder.imageView.setImageResource(R.drawable.bulb_white);
                            } else {
                                viewHolder.imageView.setImageResource(R.drawable.ac_white);
                            }
//                            makeRequest(model.get(i).getBaseUrl(), "device", action, model.get(i).getIddevice());
//                            makeRequestBlynk(model.get(i).getBlynkurl() + "?value=1");
                        } else {
                            flag = 0;
                            action = "off";
                            viewHolder.cardView.setBackgroundResource(R.drawable.box);
                            if(viewHolder.imageView.getTag().toString().equals("light")) {
                                viewHolder.imageView.setImageResource(R.drawable.bulb);
                            } else {
                                viewHolder.imageView.setImageResource(R.drawable.ac);
                            }
                            viewHolder.cog.setBackgroundResource(R.drawable.cog);
                            viewHolder.pencil.setBackgroundResource(R.drawable.pencil);
                            viewHolder.status.setTextColor(Color.BLACK);
                            viewHolder.devices.setTextColor(Color.BLACK);
//                            makeRequest(model.get(i).getBaseUrl(), "device", action, model.get(i).getIddevice());
//                            makeRequestBlynk(model.get(i).getBlynkurl() + "?value=0");
                        }
                    }
                });
                showDialogEdit(viewHolder, i);
//                setAnimation(viewHolder.cardView, i);
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

    private void showDialogEdit(final ViewHolder viewHolder, final int position) {
        viewHolder.pencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditDialog(position, viewHolder);
            }
        });
    }

    //buat update tampilan sekarang pas berubah
    private void Update(int position, ViewHolder viewHolder) {
        String update = getDefaults("update", context);
        if(TextUtils.isEmpty(update)) {
           Log.e(TAG, "No update");
        } else {
            try {
                JSONArray jsonArray = new JSONArray(update);
                for(int x = 0; x < jsonArray.length(); x++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(x);
                    String iddevice = jsonObject.getString("iddevice");
                    String statusp = jsonObject.getString("status");
                    if(model.get(position).getIddevice().equals(iddevice)) {
                        model.get(position).setStatus(statusp.toUpperCase());
                        if(statusp.equals("on")) {
                            flag = 1;
                            viewHolder.status.setTextColor(Color.WHITE);
                            viewHolder.devices.setTextColor(Color.WHITE);
                            viewHolder.cardView.setBackgroundResource(R.drawable.bg_gradient);
                            viewHolder.cog.setBackgroundResource(R.drawable.cog_white);
                            if(model.get(position).getTag() == "light") {
                                viewHolder.imageView.setImageResource(R.drawable.bulb_white);
                            } else {
                                viewHolder.imageView.setImageResource(R.drawable.ac_white);
                            }
                            viewHolder.status.setText(statusp.toUpperCase());
                        } else {
                            flag = 0;
                            viewHolder.cardView.setBackgroundResource(R.drawable.box);
                            if(viewHolder.imageView.getTag().toString() == "light") {
                                viewHolder.imageView.setImageResource(R.drawable.bulb);
                            } else {
                                viewHolder.imageView.setImageResource(R.drawable.ac);
                            }
                            viewHolder.cog.setBackgroundResource(R.drawable.cog);
                            viewHolder.status.setTextColor(Color.BLACK);
                            viewHolder.devices.setTextColor(Color.BLACK);
                            viewHolder.status.setText(statusp.toUpperCase());
                        }
                    }
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void changeStatus(String iddevice, String status) {
        String data = getDefaults("data", context);
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray jsonArray = jsonObject.getJSONArray("controller");
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
                        String iddevice_data = jsonObject2.getString("iddevice");
                        if(iddevice_data.equals(iddevice)) {
                            jsonObject2.put("status", status);
                        }
                    }
                }
            }
            setDefaults("data", jsonObject.toString(), context);
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    private void changeDeviceName(final String iddevice, final String device_name) {
        String url = "http://dataaihome.itcs.co.id/updateDevice_Name.php";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Response from url (Change Device Name) : " + response + "iddevice : " + iddevice);
                try {
                    String data = getDefaults("data", context);
                    if(!TextUtils.isEmpty(data)) {
                        JSONObject jsonObject = new JSONObject(data);
                        JSONArray jsonArray = jsonObject.getJSONArray("controller");
                        for(int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            if(!jsonObject1.isNull("device")) {
                                JSONArray devices = jsonObject1.getJSONArray("device");
                                for(int x = 0; x < devices.length(); x++) {
                                    JSONObject jsonObject2 = devices.getJSONObject(x);
                                    if(jsonObject2.getString("iddevice").equals(iddevice)) {
                                        jsonObject2.put("device_name", device_name);
                                        changeDeviceNameOnFavorite(iddevice, device_name);
                                    }
                                    Log.e(TAG, "Perubahan setelah ubah nama : " + jsonObject.toString());
                                    setDefaults("data", jsonObject.toString(), context);
                                }
                            }
                        }
                    }
                } catch(JSONException e) {
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
                params.put("iddevice", iddevice);
                params.put("name", device_name);
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

    private void changeDeviceNameOnFavorite(String iddevice, String device_name) {
        String data = getDefaults("data", context);
        if(!TextUtils.isEmpty(data)) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                if(!jsonObject.isNull("favorite")) {
                    JSONArray favorite = jsonObject.getJSONArray("favorite");
                    for (int i = 0; i < favorite.length(); i++) {
                        JSONObject jsonObject1 = favorite.getJSONObject(i);
                        String id_device = jsonObject1.getString("iddevice");
                        if(id_device.equals(iddevice)) {
                            jsonObject1.put("device_name", device_name);
                        }
                    }
                }
                Log.e(TAG, "Response dari change device name favorite : " + jsonObject.toString());
                setDefaults("data", jsonObject.toString(), context);
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void EditDialog(final int position, final ViewHolder viewHolder) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.CustomDialog));
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog, null);
        builder.setView(view);
        final EditText editText = view.findViewById(R.id.new_device_name);
        builder.setCancelable(false);
        builder.setTitle("Change Device Name");
        builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(!editText.getText().toString().trim().isEmpty()) {
                    changeDeviceName(model.get(position).getIddevice(), editText.getText().toString().trim());
                    model.get(position).setDevices(editText.getText().toString().trim());
                    viewHolder.devices.setText(editText.getText().toString());
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });
        alertDialog = builder.create();
        alertDialog.show();
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

            }
        });
        requestQueue.add(stringRequest);
    }
}
