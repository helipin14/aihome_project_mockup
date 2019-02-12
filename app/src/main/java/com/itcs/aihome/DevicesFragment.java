package com.itcs.aihome;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.NetworkOnMainThreadException;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class DevicesFragment extends Fragment {

    private RecyclerView light_container, ac_container;
    private GridAdapter light_adapter, ac_adapter;
    public String idupdate, data;
    private String TAG;
    private RadioButton onac, offac, onlight, offlight;
    private List<Model> models, models2;
    private Sinkron sinkron;
    private Timer timer;
    private TimerTask doAsyncTask;
    private int on_light, off_light, on_ac, off_ac = 0;
    private OkHttpClient okHttpClient;
    private okhttp3.Response response;
    private boolean isEnabled = false;
    private Socket socket;
    private SocketListener listener;
    private SolidIconTextView refresh;
    private String iduser;
    private AlertDialog alertDialog;
    private int spacing = 5;

    public static DevicesFragment newInstance() {
        DevicesFragment devicesFragment = new DevicesFragment();
        return devicesFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lights, container, false);
        onlight =  view.findViewById(R.id.on_light);
        offlight =  view.findViewById(R.id.off_light);
        onac =  view.findViewById(R.id.on_ac);
        offac =  view.findViewById(R.id.off_ac);
        refresh = view.findViewById(R.id.refresh_data);
        light_container = view.findViewById(R.id.data_light);
        ac_container = view.findViewById(R.id.data_ac);

        TAG = getContext().getClass().getSimpleName();
        getDataUser();

        startSocket();
        setLights();
        setAC();
        lightControl();
        AcControl();
        refreshData();
        sinkronDevices();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkIsAllOnOrOff();
        refreshGridView();
    }

    @Override
    public void onDestroyView() throws NullPointerException {
        super.onDestroyView();
        Log.e(TAG, "Destroy view : Sinkron telah berhenti");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void AcControl() {
        onac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                matikanSemuaAC( "0");
            }
        });
        offac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                matikanSemuaAC( "1");
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
                    JSONObject jsonObject1 = controller .getJSONObject(i);
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
        if(getDefaults("data", getContext()) != null) {
            String data = getDefaults("data", getContext());
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray house = jsonObject.getJSONArray("house");
                for (int i = 0; i < house.length(); i++) {
                    JSONObject jsonObject1 = house.getJSONObject(i);
                    if(!jsonObject1.isNull("controller")) {
                        JSONArray controller = jsonObject1.getJSONArray("controller");
                        for(int x = 0; x < controller.length(); x++) {
                            JSONObject jsonObject2 = controller.getJSONObject(x);
                            if(!jsonObject2.isNull("appliances")) {
                                JSONArray appliances = jsonObject2.getJSONArray("appliances");
                                for(int j = 0; j < appliances.length(); j++) {
                                    int count_on = 0;
                                    int count_off = 0;
                                    JSONObject jsonObject3 = appliances.getJSONObject(j);
                                    if(!jsonObject3.isNull("device")) {
                                        String type = jsonObject3.getString("type");
                                        int length = jsonObject3.getJSONArray("device").length();
                                        if(type.equals("light")) {
                                            count_off = getCount("light", 1);
                                            count_on = getCount("light", 0);
                                            if(length == count_off) {
                                                offlight.setChecked(true);
                                            } else if(length == count_on) {
                                                onlight.setChecked(true);
                                            }
                                        } else {
                                            count_off = getCount("ac", 1);
                                            count_on = getCount("ac", 0);
                                            if(length == count_off) {
                                                offac.setChecked(true);
                                            } else if(length == count_on) {
                                                onac.setChecked(true);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "checkIsAllOnOrOff: " + e.getMessage());
            }
        }
    }

    private int getCount(String type, int status) {
        int count = 0;
        if(getDefaults("data", getContext()) != null) {
            try {
                String data = getDefaults("data", getContext());
                JSONObject jsonObject = new JSONObject(data);
                JSONArray house = jsonObject.getJSONArray("house");
                for (int i = 0; i < house.length(); i++) {
                    JSONObject jsonObject1 = house.getJSONObject(i);
                    if(!jsonObject1.isNull("controller")) {
                        JSONArray controller = jsonObject1.getJSONArray("controller");
                        for(int x = 0; x < controller.length(); x++) {
                            JSONObject jsonObject2 = controller.getJSONObject(x);
                            if(!jsonObject2.isNull("appliances")) {
                                JSONArray appliances = jsonObject2.getJSONArray("appliances");
                                for(int j = 0; j < appliances.length(); j++) {
                                    JSONObject jsonObject3 = appliances.getJSONObject(j);
                                    if(!jsonObject3.isNull("device")) {
                                        if(jsonObject3.getString("type").equals(type)) {
                                            JSONArray devices = jsonObject3.getJSONArray("device");
                                            for(int a = 0; a < devices.length(); a++) {
                                                JSONObject jsonObject4 = devices.getJSONObject(a);
                                                if(jsonObject4.getInt("status") == status) {
                                                    count += 1;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "checkIsAllOnOrOff: " + e.getMessage());
            }
        }
        return count;
    }


    private void lightControl() {
        onlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                matikanSemuaLampu( "0");
            }
        });
        offlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                matikanSemuaLampu("1");
            }
        });
    }

    private void matikanSemuaLampu(String action) {
        if(getDefaults("data", getContext()) != null) {
            try {
                String data = getDefaults("data", getContext());
                JSONObject jsonObject = new JSONObject(data);
                JSONArray house = jsonObject.getJSONArray("house");
                for (int i = 0; i < house.length(); i++) {
                    JSONObject jsonObject1 = house.getJSONObject(i);
                    String idaccess = jsonObject1.getString("idaccess");
                    if(!jsonObject1.isNull("controller")) {
                        JSONArray controller = jsonObject1.getJSONArray("controller");
                        for(int x = 0; x < controller.length(); x++) {
                            JSONObject jsonObject2 = controller.getJSONObject(x);
                            String idcontroller = jsonObject2.getString("idcontroller");
                            if(!jsonObject2.isNull("appliances")) {
                                JSONArray appliances = jsonObject2.getJSONArray("appliances");
                                for(int j = 0; j < appliances.length(); j++) {
                                    JSONObject jsonObject3 = appliances.getJSONObject(j);
                                    String type = jsonObject3.getString("type");
                                    if(type.equals("light")) {
                                        if(!jsonObject3.isNull("device")) {
                                            JSONArray devices = jsonObject3.getJSONArray("device");
                                            for(int a = 0; a < devices.length(); a++) {
                                                JSONObject jsonObject4 = devices.getJSONObject(a);
                                                String iddevice = jsonObject4.getString("iddevice");
                                                JSONObject jsonObject5 = new JSONObject();
                                                jsonObject5.put("idaccess", idaccess);
                                                jsonObject5.put("iddevice",iddevice);
                                                jsonObject5.put("action", action);
                                                jsonObject5.put("idcontroller", idcontroller);
                                                socket.emit("trigger", jsonObject5);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                setDefaults("data", jsonObject.toString(), getContext());
                Log.e(TAG, "matikanSemuaLampu: " + jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void matikanSemuaAC(String action) {
        if(getDefaults("data", getContext()) != null) {
            try {
                String data = getDefaults("data", getContext());
                JSONObject jsonObject = new JSONObject(data);
                JSONArray house = jsonObject.getJSONArray("house");
                for (int i = 0; i < house.length(); i++) {
                    JSONObject jsonObject1 = house.getJSONObject(i);
                    String idaccess = jsonObject1.getString("idaccess");
                    if(!jsonObject1.isNull("controller")) {
                        JSONArray controller = jsonObject1.getJSONArray("controller");
                        for(int x = 0; x < controller.length(); x++) {
                            JSONObject jsonObject2 = controller.getJSONObject(x);
                            if(!jsonObject2.isNull("appliances")) {
                                JSONArray appliances = jsonObject2.getJSONArray("appliances");
                                for(int j = 0; j < appliances.length(); j++) {
                                    JSONObject jsonObject3 = appliances.getJSONObject(j);
                                    String type = jsonObject3.getString("type");
                                    if(type.equals("ac")) {
                                        if(!jsonObject3.isNull("device")) {
                                            JSONArray devices = jsonObject3.getJSONArray("device");
                                            for(int a = 0; a < devices.length(); a++) {
                                                JSONObject jsonObject4 = devices.getJSONObject(a);
                                                String iddevice = jsonObject4.getString("iddevice");
                                                JSONObject jsonObject5 = new JSONObject();
                                                jsonObject5.put("idaccess", idaccess);
                                                jsonObject5.put("iddevice",iddevice);
                                                jsonObject5.put("action", action);
                                                socket.emit("trigger", jsonObject5);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                setDefaults("data", jsonObject.toString(), getContext());
                Log.e(TAG, "matikanSemuaAC: " + jsonObject.toString());
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
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray house = jsonObject.getJSONArray("house");
            for (int i = 0; i < house.length(); i++) {
                JSONObject jsonObject1 = house.getJSONObject(i);
                String iddaccess = jsonObject1.getString("idaccess");
                if(!jsonObject1.isNull("controller")) {
                    JSONArray controller = jsonObject1.getJSONArray("controller");
                    for (int x = 0; x < controller.length(); x++) {
                        JSONObject jsonObject2 = controller.getJSONObject(x);
                        String idcontroller = jsonObject2.getString("idcontroller");
                        if(!jsonObject2.isNull("appliances")) {
                            JSONArray appliances = jsonObject2.getJSONArray("appliances");
                            for(int a = 0; a < appliances.length(); a++) {
                                JSONObject jsonObject3 = appliances.getJSONObject(a);
                                String type = jsonObject3.getString("type");
                                if(type.equals("ac")) {
                                    if(!jsonObject3.isNull("device")) {
                                        JSONArray devices = jsonObject3.getJSONArray("device");
                                        for(int j = 0; j < devices.length(); j++) {
                                            int flag;
                                            JSONObject jsonObject4 = devices.getJSONObject(j);
                                            String iddevice = jsonObject4.getString("iddevice");
                                            int status = jsonObject4.getInt("status");
                                            String device_name = jsonObject4.getString("name");
                                            if(status == 1) {
                                                flag = 0;
                                            } else {
                                                flag = 1;
                                            }
                                            models2.add(new Model(R.drawable.ac, status, device_name, "AC", iddevice, flag, iddaccess, idcontroller));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(models2.size() > 0) {
            ac_adapter = new GridAdapter(getContext(), models2, socket);
            ac_container.setLayoutManager(new GridLayoutManager(getContext(), 2));
            ac_container.addItemDecoration(new SpaceItemDecoration(spacing));
            ac_container.setAdapter(ac_adapter);
        }
    }

    private void setLights() {
        models = new ArrayList<>();
        if(getDefaults("data", getContext()) != null) {
            String data = getDefaults("data", getContext());
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray house = jsonObject.getJSONArray("house");
                for (int i = 0; i < house.length(); i++) {
                    JSONObject jsonObject1 = house.getJSONObject(i);
                    String idaccess = jsonObject1.getString("idaccess");
                    if(!jsonObject1.isNull("controller")) {
                        JSONArray controller = jsonObject1.getJSONArray("controller");
                        for (int x = 0; x < controller.length(); x++) {
                            JSONObject jsonObject2 = controller.getJSONObject(x);
                            String idcontroller = jsonObject2.getString("idcontroller");
                            if(!jsonObject2.isNull("appliances")) {
                                JSONArray appliances = jsonObject2.getJSONArray("appliances");
                                for(int a = 0; a < appliances.length(); a++) {
                                    JSONObject jsonObject3 = appliances.getJSONObject(a);
                                    String type = jsonObject3.getString("type");
                                    if(type.equals("light")) {
                                        if(!jsonObject3.isNull("device")) {
                                            JSONArray devices = jsonObject3.getJSONArray("device");
                                            for(int j = 0; j < devices.length(); j++) {
                                                int flag;
                                                JSONObject jsonObject4 = devices.getJSONObject(j);
                                                String iddevice = jsonObject4.getString("iddevice");
                                                int status = jsonObject4.getInt("status");
                                                String device_name = jsonObject4.getString("name");
                                                if(status == 1) {
                                                    flag = 0;
                                                } else {
                                                    flag = 1;
                                                }
                                                models.add(new Model(R.drawable.bulb, status, device_name, "light", iddevice, flag, idaccess, idcontroller));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(models.size() > 0) {
            light_adapter = new GridAdapter(getContext(), models, socket);
            light_container.setLayoutManager(new GridLayoutManager(getContext(), 2));
            light_container.addItemDecoration(new SpaceItemDecoration(spacing));
            light_container.setAdapter(light_adapter);
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

    private String SinkronUpdate() throws IOException {
        String result = "";
        String url = config.server_temp + "syncThings.php";
        okHttpClient = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("idupdate", idupdate)
                .build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        response = okHttpClient.newCall(request).execute();
        if(response.isSuccessful()) {
            result = response.body().string();
        }
        return result;
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
            if(s == null || isCancelled()) {
                Log.e(TAG, "AsyncTask has been closed");
                sinkron.cancel(true);
            } else if(!TextUtils.isEmpty(s)){
                updateUi(s);
            }
        }
    }

    private void sinkronUpdate() {
        isEnabled = true;
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


    private void changeStatus(String iddevice, int status, String type) {
        if(getDefaults("data", getContext()) != null) {
            String data = getDefaults("data", getContext());
            if(!TextUtils.isEmpty(data)) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray house = jsonObject.getJSONArray("house");
                    for (int i = 0; i < house.length(); i++) {
                        JSONObject jsonObject1 = house.getJSONObject(i);
                        if(!jsonObject1.isNull("controller")) {
                            JSONArray controller = jsonObject1.getJSONArray("controller");
                            for(int x = 0; x < controller.length(); x++) {
                                JSONObject jsonObject2 = controller.getJSONObject(x);
                                if(!jsonObject2.isNull("appliances")) {
                                    JSONArray appliances = jsonObject2.getJSONArray("appliances");
                                    for(int j = 0; j < appliances.length(); j++) {
                                        JSONObject jsonObject3 = appliances.getJSONObject(j);
                                        if(jsonObject3.getString("type").equals(type)) {
                                            if(!jsonObject3.isNull("device")) {
                                                JSONArray devices = jsonObject3.getJSONArray("device");
                                                for(int a = 0; a < devices.length(); a++) {
                                                    JSONObject jsonObject4 = devices.getJSONObject(a);
                                                    if(jsonObject4.getString("iddevice").equals(iddevice)) {
                                                        jsonObject4.put("status", status);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    setDefaults("data", jsonObject.toString(), getContext());
                    Log.e(TAG, "changeStatus: " + jsonObject.toString());
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void changeIdUpdate(String idupdate_update) {
        if(getDefaults("data", getContext()) != null) {
            try {
                String data = getDefaults("data", getContext());
                idupdate = idupdate_update;
                JSONObject jsonObject = new JSONObject(data);
                String idupdate_latest = jsonObject.getString("idupdate");
                idupdate_latest = idupdate_update;
                jsonObject.put("idupdate", idupdate_latest);
                setDefaults("data", jsonObject.toString(), getContext());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateUi(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if (status.equals("Y")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("data_update");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String iddevice = jsonObject1.getString("iddevice");
                        String value_after = jsonObject1.getString("value_after");
//                        changeStatus(iddevice, value_after);
//                        updateData(iddevice, value_after);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

//    private void updateData(String iddevice, String status) {
//        String type = getType(iddevice);
//        int image = 0;
//        if(getDefaults("data", getContext()) != null) {
//            try {
//                String data = getDefaults("data", getContext());
//                JSONObject jsonObject = new JSONObject(data);
//                JSONArray controller = jsonObject.getJSONArray("controller");
//                for (int i = 0; i < controller.length(); i++) {
//                    JSONObject jsonObject1 = controller.getJSONObject(i);
//                    String blynk_key = jsonObject1.getString("blynk_key");
//                    if (!jsonObject1.isNull("device")) {
//                        JSONArray devices = jsonObject1.getJSONArray("device");
//                        for (int x = 0; x < devices.length(); x++) {
//                            JSONObject jsonObject2 = devices.getJSONObject(x);
//                            String id_device = jsonObject2.getString("iddevice");
//                            if (id_device.equals(iddevice)) {
//                                if (type.equals("light")) {
//                                    int position = getPosition(type, iddevice);
//                                    int flag = 0;
//                                    if (status.equals("on")) {
//                                        image = R.drawable.bulb;
//                                        flag = 1;
//                                    } else {
//                                        flag = 0;
//                                        image = R.drawable.bulb_white;
//                                    }
//                                    models.remove(position);
//                                    String pin = jsonObject2.getString("pin");
//                                    String device_name = jsonObject2.getString("device_name");
//                                    String baseUrl = config.server_temp + "deviceTrigger.php";
//                                    String blynkurl = "http://188.166.206.43:8080/" + blynk_key + "/update/" + pin;
//                                    models.add(position, new Model(image, status, device_name, baseUrl, "light", iddevice, blynkurl, pin, flag));
//                                    adapter = new GridViewAdapter(getContext(), models);
//                                    gridView1.setAdapter(adapter);
//                                } else {
//                                    int position = getPosition(type, iddevice);
//                                    int flag = 0;
//                                    models2.remove(position);
//                                    if (status.equals("on")) {
//                                        image = R.drawable.ac;
//                                        flag = 1;
//                                    } else {
//                                        flag = 0;
//                                        image = R.drawable.ac_white;
//                                    }
//                                    String pin = jsonObject2.getString("pin");
//                                    String device_name = jsonObject2.getString("device_name");
//                                    String baseUrl = config.server_temp + "deviceTrigger.php";
//                                    String blynkurl = "http://188.166.206.43:8080/" + blynk_key + "/update/" + pin;
//                                    models2.add(position, new Model(image, status, device_name, baseUrl, "AC", iddevice, blynkurl, pin, flag));
//                                    adapter2 = new GridViewAdapter(getContext(), models2);
//                                    gridView2.setAdapter(adapter2);
//                                }
//                            }
//                        }
//                    }
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    private int getPosition(String type, String iddevice) {
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

    private void refreshGridView() {
        String update = getDefaults("update_data", getContext());
        if (!TextUtils.isEmpty(update)) {
            if (update.equals("yes")) {
                setLights();
                setAC();
                setDefaults("update_data", "no", getContext());
            }
        }
    }

    private void refreshData() {
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogRefresh();
            }
        });
    }

    private void showDialogRefresh() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.progress_dialog, null);
        builder.setView(view);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                alertDialog.dismiss();
            }
        }, 1500);
    }

    private void getAllData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray controller = jsonObject.getJSONArray("controller");
            for (int i = 0; i < controller.length(); i++) {
                JSONObject jsonObject1 = controller.getJSONObject(i);
                String blynk_key = jsonObject1.getString("blynk_key");
                if(!jsonObject1.isNull("device")) {
                    JSONArray devices = jsonObject1.getJSONArray("device");
                    for(int x = 0; x < devices.length(); x++) {
                        JSONObject jsonObject2 = devices.getJSONObject(x);
                        String pin = jsonObject2.getString("pin");
                        String baseUrl = "http://188.166.206.43:8080/" + blynk_key + "/get/" + pin;
                        String status = fetchStatus(baseUrl);
                        Log.e(TAG, "Status : " + status);
                        jsonObject2.put("status", status);
                        Log.e(TAG, "Hasil dari refresh : " + jsonObject.toString());
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NetworkOnMainThreadException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String fetchStatus(String baseUrl) throws IOException {
        okHttpClient = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(baseUrl)
                .method("GET", null)
                .build();
        response = okHttpClient.newCall(request).execute();
        String result = "";
        if(response.isSuccessful()) {
            result = response.body().string();
        }
        return result;
    }

    private String fetchData() throws IOException {
        String url = config.server_temp + "getAll_Things.php";
        okHttpClient = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("iduser", iduser)
                .build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        response = okHttpClient.newCall(request).execute();
        String result = "";
        if(response.isSuccessful()) {
            result = response.body().string();
        }
        return result;
    }

    private void getDataUser() {
        String data = getDefaults("data_user", getContext());
        if(!TextUtils.isEmpty(data)) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                iduser = jsonObject.getString("iduser");
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void startSocket() {
        listener = new SocketListener();
        socket = listener.getSocket();
        socket.connect();
        if(socket.connected()) {
            Log.e(TAG, "startSocket: Connect");
        }
    }

    private void sinkronDevices() {
        socket.on("feedback", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject jsonObject = (JSONObject)args[0];
                    Log.e(TAG, "call: " + jsonObject.toString());
                    String section = jsonObject.getString("section");
                    if(section.equals("trigger")) {
                        String status = jsonObject.getString("status");
                        if(status.equals("accepted")) {
                            checkIsAllOnOrOff();
                            JSONObject jsonObject1 = jsonObject.getJSONObject("device");
                            String iddevice = jsonObject1.getString("iddevice");
                            final int action = jsonObject1.getInt("action");
                            final String type = getType(iddevice);
                            changeStatus(iddevice, action, type);
                            final int position = getPosition(type, iddevice);
                            Log.e(TAG, "call: " + type);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(type.equals("light")) {
                                        models.get(position).setStatus(action);
                                        light_adapter.notifyDataSetChanged();
                                    } else {
                                        models2.get(position).setStatus(action);
                                        ac_adapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String getType(String iddevice) {
        String type = "";
        if(getDefaults("data", getContext()) != null) {
            String data = getDefaults("data", getContext());
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray house = jsonObject.getJSONArray("house");
                for (int i = 0; i < house.length(); i++) {
                    JSONObject jsonObject1 = house.getJSONObject(i);
                    if(!jsonObject1.isNull("controller")) {
                        JSONArray controller = jsonObject1.getJSONArray("controller");
                        for(int x = 0; x < controller.length(); x++) {
                            JSONObject jsonObject2 = controller.getJSONObject(x);
                            if(!jsonObject2.isNull("appliances")) {
                                JSONArray appliances = jsonObject2.getJSONArray("appliances");
                                for(int j = 0; j < appliances.length(); j++) {
                                    JSONObject jsonObject3 = appliances.getJSONObject(j);
                                    if(!jsonObject3.isNull("device")) {
                                        JSONArray devices = jsonObject3.getJSONArray("device");
                                        for(int a = 0; a < devices.length(); a++) {
                                            JSONObject jsonObject4 = devices.getJSONObject(a);
                                            if(jsonObject4.getString("iddevice").equals(iddevice)) {
                                                type = jsonObject3.getString("type");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return type;
    }

}
