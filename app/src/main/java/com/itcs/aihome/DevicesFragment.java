package com.itcs.aihome;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
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
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class DevicesFragment extends Fragment {

    private ExpandableGridView gridView1, gridView2;
    private GridViewAdapter adapter, adapter2;
    public String idupdate, data;
    private String TAG;
    private RadioButton onac, offac, onlight, offlight;
    private List<Model> models, models2;
    private Sinkron sinkron;
    private Timer timer;
    private TimerTask doAsyncTask;
    private int on, off = 0;
    private int on_ac, off_ac = 0;

    public static DevicesFragment newInstance() {
        DevicesFragment devicesFragment = new DevicesFragment();
        return devicesFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lights, container, false);
        gridView1 =  view.findViewById(R.id.detail_gridview2);
        gridView2 =  view.findViewById(R.id.detail_gridview3);
        onlight =  view.findViewById(R.id.on_light);
        offlight =  view.findViewById(R.id.off_light);
        onac =  view.findViewById(R.id.on_ac);
        offac =  view.findViewById(R.id.off_ac);

        TAG = getContext().getClass().getSimpleName();
        getData();

        setLights();
        setAC();
        lightControl();
        AcControl();
//        sinkronUpdate();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        timer.cancel();
    }

    @Override
    public void onStop() {
        super.onStop();
//        timer.cancel();
    }

    @Override
    public void onPause() {
        super.onPause();
//        timer.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        timer.cancel();
    }

    private void AcControl() {
        onac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                matikanSemuaAC( "on");
            }
        });
        offac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                matikanSemuaAC( "off");
            }
        });
    }

    private void checkData() {
        String data = getDefaults("data", getContext());
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
                            String status = jsonObject2.getString("status");
                            String type = jsonObject2.getString("type");
                            if(type.equals("light")) {
                                if(status.indexOf("on") > 0) {
                                    onlight.setChecked(true);
                                } else {
                                    onlight.setChecked(false);
                                }
                            } else {
                                if(status.indexOf("on") > 0) {
                                    onac.setChecked(true);
                                } else {
                                    onac.setChecked(false);
                                }
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkIsAllOnOrOff() {
        String data = getDefaults("data", getContext());
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
                            if(jsonObject2.getString("type").equals("light")) {
                                if(jsonObject2.getString("status").equals("on")) {
                                    on += 1;
                                } else {
                                    off += 1;
                                }
                                if(on == devices.length() / 2) {
                                    onlight.setChecked(true);
                                } else if(off == devices.length() / 2) {
                                    offlight.setChecked(true);
                                }
                            } else {
                                if(jsonObject2.getString("status").equals("on")) {
                                    on_ac += 1;
                                } else {
                                    off_ac += 1;
                                }
                                if(on_ac == devices.length() / 2) {
                                    onac.setChecked(true);
                                } else if(off_ac == devices.length() / 2) {
                                    offac.setChecked(true);
                                }
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void lightControl() {
        onlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                matikanSemuaLampu( "on");
            }
        });
        offlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                matikanSemuaLampu("off");
            }
        });
    }

    private void matikanSemuaLampu(String action) {
        String data = getDefaults("data", getContext());
        if(!TextUtils.isEmpty(data)) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray jsonArray = jsonObject.getJSONArray("controller");
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String blynk_key = jsonObject1.getString("blynk_key");
                    if(!jsonObject1.isNull("device")) {
                        JSONArray devices = jsonObject1.getJSONArray("device");
                        for(int x = 0; x < devices.length(); x++) {
                            JSONObject jsonObject2 = devices.getJSONObject(x);
                            if(jsonObject2.getString("type").equals("light")) {
                                String pin = jsonObject2.getString("pin");
                                String blynkurl = "http://188.166.206.43:8080/" + blynk_key + "/update/" + pin;
                                String iddevice = jsonObject2.getString("iddevice");
                                String baseUrl = "http://dataaihome.itcs.co.id/deviceTrigger.php";
                                if(action.equals("on")) {
                                    blynkurl += "?value=1";
                                } else {
                                    blynkurl += "?value=0";
                                }
                                makeRequestBlynk(blynkurl);
                                makeRequestTrigger(baseUrl, "device", action, iddevice);
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void matikanSemuaAC(String action) {
        String data = getDefaults("data", getContext());
        if(!TextUtils.isEmpty(data)) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray jsonArray = jsonObject.getJSONArray("controller");
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String blynk_key = jsonObject1.getString("blynk_key");
                    if(!jsonObject1.isNull("device")) {
                        JSONArray devices = jsonObject1.getJSONArray("device");
                        for(int x = 0; x < devices.length(); x++) {
                            JSONObject jsonObject2 = devices.getJSONObject(x);
                            if(jsonObject2.getString("type").equals("ac")) {
                                String pin = jsonObject2.getString("pin");
                                String blynkurl = "http://188.166.206.43:8080/" + blynk_key + "/update/" + pin;
                                String iddevice = jsonObject2.getString("iddevice");
                                String baseUrl = "http://dataaihome.itcs.co.id/deviceTrigger.php";
                                if(action.equals("on")) {
                                    blynkurl += "?value=1";
                                } else {
                                    blynkurl += "?value=0";
                                }
                                makeRequestBlynk(blynkurl);
                                makeRequestTrigger(baseUrl, "device", action, iddevice);
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setAC() {
        String data = "";
        if(getDefaults("data", getContext()) != null) {
            data = getDefaults("data", getContext());
        }
        models2 = new ArrayList<>();
        models2.clear();
        String status = "";
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
                        String type = jsonObject2.getString("type");
                        if(type.equals("ac")) {
                            String pin = jsonObject2.getString("pin");
                            String device_name = jsonObject2.getString("device_name");
                            String baseUrl = "http://dataaihome.itcs.co.id/deviceTrigger.php";
                            String iddevice = jsonObject2.getString("iddevice");
                            status = jsonObject2.getString("status");
                            String blynkurl = "http://188.166.206.43:8080/" + blynk_key + "/update/" + pin;
                            models2.add(new Model(R.drawable.ac, status, device_name, baseUrl, "ac", iddevice, blynkurl, pin));
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(models2.size() > 0) {
            adapter2 = new GridViewAdapter(getContext(), models2);
            gridView2.setAdapter(adapter2);
            gridView2.setExpanded(true);
        }
    }

    private void setLights() {
        String data = getDefaults("data", getContext());
        models = new ArrayList<>();
        models.clear();
        String status = "";
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
                        String type = jsonObject2.getString("type");
                        if(type.equals("light")) {
                            String pin = jsonObject2.getString("pin");
                            String device_name = jsonObject2.getString("device_name");
                            String baseUrl = "http://dataaihome.itcs.co.id/deviceTrigger.php";
                            String blynkurl = "http://188.166.206.43:8080/" + blynk_key + "/update/" + pin;
                            String iddevice = jsonObject2.getString("iddevice");
                            status = jsonObject2.getString("status");
                            models.add(new Model(R.drawable.bulb, status, device_name, baseUrl, "light", iddevice, blynkurl, pin));
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(models.size() > 0) {
            adapter = new GridViewAdapter(getContext(), models);
            gridView1.setAdapter(adapter);
            gridView1.setExpanded(true);
        }
    }

    private void makeRequestBlynk(String baseUrl) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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

    private String SinkronUpdate() throws IOException {
        String url = "http://dataaihome.itcs.co.id/syncThings.php";
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("idupdate", idupdate)
                .build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        okhttp3.Response response = client.newCall(request).execute();
        return response.body().string();
    }

    class Sinkron extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String response = "";
            try {
                response = SinkronUpdate();
            } catch(IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e(TAG, "Response dari update di devices fragment: " + s);
            updateUi(s);
            if(s == null || isCancelled()) {
                Log.e(TAG, "AsyncTask has been closed");
                sinkron.cancel(true);
            }
        }
    }

    private void sinkronUpdate() {
        final Handler handler = new Handler();
        timer = new Timer();
        doAsyncTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sinkron = new Sinkron();
                        sinkron.execute();
                    }
                }));
            }
        };
        timer.schedule(doAsyncTask, 0, 500);
    }

    private void getData() {
        data = getDefaults("data", getContext());
        try {
            JSONObject jsonObject = new JSONObject(data);
            idupdate = jsonObject.getString("idupdate");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void changeStatus(String iddevice, String status) {
        String data = getDefaults("data", getContext());
        if(!TextUtils.isEmpty(data)) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray controller = jsonObject.getJSONArray("controller");
                for (int i = 0; i < controller.length(); i++) {
                    JSONObject jsonObject1 = controller.getJSONObject(i);
                    if(!jsonObject1.isNull("device")) {
                        JSONArray devices = jsonObject1.getJSONArray("device");
                        for(int x = 0; x < devices.length();x++) {
                            JSONObject jsonObject2 = devices.getJSONObject(x);
                            if(jsonObject2.getString("iddevice").equals(iddevice)) {
                                jsonObject2.put("status", status);
                            }
                        }
                    }
                }
                setDefaults("data", jsonObject.toString(), getContext());
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void changeIdUpdate(String idupdate_update) {
        String data = getDefaults("data", getContext());
        if(!TextUtils.isEmpty(data)) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                String idupdate_latest = jsonObject.getString("idupdate");
                idupdate_latest = idupdate_update;
                jsonObject.put("idupdate", idupdate_latest);
                idupdate = idupdate_latest;
                setDefaults("data", jsonObject.toString(), getContext());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateUi(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String status = jsonObject.getString("status");
            if(status.equals("Y")) {
                JSONArray jsonArray = jsonObject.getJSONArray("data_update");
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String iddevice = jsonObject1.getString("iddevice");
                    String value_after = jsonObject1.getString("value_after");
                    changeStatus(iddevice, value_after);
                    updateData(iddevice, value_after);
                }
                String idupdate = jsonObject.getString("idupdate_latest");
                changeIdUpdate(idupdate);
                timer.cancel();
                Log.e(TAG, "Coba saja");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sinkronUpdate();
                    }
                }, 1000);
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateData(String iddevice, String status) {
        String data = getDefaults("data", getContext());
        int image = 0;
        if(!TextUtils.isEmpty(data)) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray controller = jsonObject.getJSONArray("controller");
                for (int i = 0; i < controller.length(); i++) {
                    JSONObject jsonObject1 = controller.getJSONObject(i);
                    String blynk_key = jsonObject1.getString("blynk_key");
                    if (!jsonObject1.isNull("device")) {
                        JSONArray devices = jsonObject1.getJSONArray("device");
                        for (int x = 0; x < devices.length(); x++) {
                            JSONObject jsonObject2 = devices.getJSONObject(x);
                            String id_device = jsonObject2.getString("iddevice");
                            String type = jsonObject2.getString("type");
                            if (id_device.equals(iddevice)) {
                                if (type.equals("light")) {
                                    if (status.equals("on")) {
                                        image = R.drawable.bulb;
                                    } else {
                                        image = R.drawable.bulb_white;
                                    }
                                    int position = updateLights(type, iddevice);
                                    models.remove(position);
                                    String pin = jsonObject2.getString("pin");
                                    String device_name = jsonObject2.getString("device_name");
                                    String baseUrl = "http://dataaihome.itcs.co.id/deviceTrigger.php";
                                    String blynkurl = "http://188.166.206.43:8080/" + blynk_key + "/update/" + pin;
                                    models.add(position, new Model(image, status, device_name, baseUrl, "light", iddevice, blynkurl, pin));
                                    adapter = new GridViewAdapter(getContext(), models);
                                    gridView1.setAdapter(adapter);
                                } else {
                                    int position = updateLights(type, iddevice);
                                    models2.remove(position);
                                    if (status.equals("on")) {
                                        image = R.drawable.ac;
                                    } else {
                                        image = R.drawable.ac_white;
                                    }
                                    String pin = jsonObject2.getString("pin");
                                    String device_name = jsonObject2.getString("device_name");
                                    String baseUrl = "http://dataaihome.itcs.co.id/deviceTrigger.php";
                                    String blynkurl = "http://188.166.206.43:8080/" + blynk_key + "/update/" + pin;
                                    models2.add(position, new Model(image, status, device_name, baseUrl, "AC", iddevice, blynkurl, pin));
                                    adapter2 = new GridViewAdapter(getContext(), models2);
                                    gridView2.setAdapter(adapter2);
                                }
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private int updateLights(String type, String iddevice) {
        int position = 0;
        if(type.equals("light")) {
            if(models.size() > 0) {
                for (int i = 0; i < models.size(); i++) {
                    if(models.get(i).getIddevice().equals(iddevice)) {
                        position = i;
                    }
                }
            }
        } else {
            if(models2.size() > 0) {
                for (int i = 0; i < models2.size(); i++) {
                    if(models2.get(i).getIddevice().equals(iddevice)) {
                        position = i;
                    }
                }
            }
        }
        return position;
    }

    public void makeRequestTrigger(final String baseUrl, final String tipe, final String action, final String iddevice) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //
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
}
