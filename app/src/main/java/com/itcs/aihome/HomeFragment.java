package com.itcs.aihome;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import info.hoang8f.android.segmented.SegmentedGroup;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class HomeFragment extends Fragment {

    private WrapContentViewPager viewPager, viewPager2;
    private Adapter adapter, adapter2;
    List<Model> models;
    List<Model> models2;
    private String TAG = MainActivity.class.getSimpleName();
    private SessionManager session;
    private AlertDialog logout;
    private ImageButton connection;
    private SolidIconTextView imageButton;
    private TextView tempTextView, humidityTextView, dateTextView, user_name, addgroup, viewallgroup, viewalluser, city;
    private String temp, humidity, blynk_key, checkupdate;
    private String username, data, idupdate, iduser;
    private LinearLayout  nodata_light, nodata_ac;
    private Sinkron sinkron;
    private SinkronFavorite sinkronFavorite;
    private Timer timer, timer2;
    private AssetManager assetManager;
    private Typeface typeface;
    private Snackbar snackbar;
    private LinearLayout parent, parent_group, parent_nodatagroup;
    private LocationManager locationManager;
    private GroupAdapter groupAdapter;
    private List<GroupItem> groupItems;
    private RecyclerView groups;
    private boolean enabled = false;
    private SegmentedGroup light, ac;
    private Toolbar toolbar;

    public static HomeFragment newInstance() {
        HomeFragment homeFragment = new HomeFragment();
        return homeFragment;
    }

    public HomeFragment() {}

    @Override
    public void onStart() {
        super.onStart();
        checkToRunSinkron();
//        if(checkupdate.equals("Yes")) {
//            sinkronUpdate();
//        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, container, false);

        tempTextView = view.findViewById(R.id.temperature);
        humidityTextView = view.findViewById(R.id.humidity);
        viewPager = view.findViewById(R.id.viewpager);
        viewPager2 = view.findViewById(R.id.viewpager2);
        dateTextView = view.findViewById(R.id.date);
        user_name = view.findViewById(R.id.user_name);
        nodata_light = view.findViewById(R.id.nodata_light);
        nodata_ac = view.findViewById(R.id.nodata_ac);
        connection = view.findViewById(R.id.status_relay);
        addgroup = view.findViewById(R.id.add_group_nodata);
        assetManager = getContext().getAssets();
        typeface = Typeface.createFromAsset(assetManager, "fonts/valeraround.ttf");
        parent = view.findViewById(R.id.parent_home);
        groups = view.findViewById(R.id.group_home_list);
        parent_group = view.findViewById(R.id.data_group_container);
        parent_nodatagroup = view.findViewById(R.id.nodata_group);
        viewallgroup = view.findViewById(R.id.view_all_group);
        city = view.findViewById(R.id.location);
        light = view.findViewById(R.id.layout_onofflight);
        ac = view.findViewById(R.id.layout_onoffac);
        toolbar = view.findViewById(R.id.my_toolbar);
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        getData();
        getDataUser();
        getCityName();

        getDate(dateTextView);

        dapatWeather();
        user_name.setTypeface(typeface);
        tempTextView.setTypeface(typeface);
        humidityTextView.setTypeface(typeface);
        tempTextView.setText(temp);
        humidityTextView.setText(humidity);
        user_name.setText("Hello, " + username);

        session = new SessionManager(getContext());
        imageButton = view.findViewById(R.id.logoutbutton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOut();
            }
        });
        connection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Status.class);
                startActivity(intent);
            }
        });

        addgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), addgroup.class);
                startActivity(intent);
            }
        });

        if (getDefaults("data", getContext()) != null) {
            data = getDefaults("data", getContext());
        }
//        lights();
//        ac();
        noData();
        setupGroup();
//        showElemOnOff();
        themeSettings();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!TextUtils.isEmpty(getDefaults("update_favorite", getContext()))) {
            String update_favorite = getDefaults("update_favorite", getContext());
            if(update_favorite.equals("true")) {
                checkData();
            }
        }
//        syncFavorite();
    }

    @Override
    public void onPause() {
        super.onPause();
//        if(checkupdate.equals("Yes")) {
//            timer.cancel();
//            Log.e(TAG, "Sinkron telah berhenti");
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if(checkupdate.equals("Yes")) {
//            timer.cancel();
//            Log.e(TAG, "Sinkron telah berhenti");
//        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        if(checkupdate.equals("Yes")) {
//            timer.cancel();
//            Log.e(TAG, "Sinkron telah berhenti");
//        }
    }

    private void checkData() {
        data = getDefaults("data", getContext());
        if(!TextUtils.isEmpty(data)) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                if(jsonObject.isNull("favorite")) {
                    noData();
                } else {
                    if(jsonObject.getJSONArray("favorite").length() == 0) {
                        noData();
                        viewPager.invalidate();
                    }
//                    updateList();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

//    private void updateList() {
//        data = getDefaults("data", getContext());
//        models2 = new ArrayList<>();
//        models = new ArrayList<>();
//        models.clear();
//        models2.clear();
//        if(!TextUtils.isEmpty(data)) {
//            try {
//                JSONObject jsonObject = new JSONObject(data);
//                if(!jsonObject.isNull("favorite")) {
//                    JSONArray favorites = jsonObject.getJSONArray("favorite");
//                    if(favorites.length() > 0) {
//                        for(int i = 0; i < favorites.length(); i++) {
//                            JSONObject jsonObject1 = favorites.getJSONObject(i);
//                            String type = jsonObject1.getString("type");
//                            String iddevice = jsonObject1.getString("iddevice");
//                            String status = getStatus(iddevice);
//                            String device_name = jsonObject1.getString("device_name");
//                            String baseUrl = "http://dataaihome2.itcs.co.id/deviceTrigger.php";
//                            String pin = jsonObject1.getString("pin");
//                            String blynkurl = "http://188.166.206.43:8080/" + blynk_key + "/update/" + pin;
//                            int image = 0;
//                            if(type.equals("light")) {
//                                int flag = 0;
//                                if(status.equals("on")) {
//                                    flag = 1;
//                                } else {
//                                    flag = 0;
//                                }
//                                image = R.drawable.bulb;
//                                models.add(new Model(image, status, device_name, baseUrl, "light", iddevice, blynkurl, pin, flag));
//                                if(models.size() > 0) {
//                                    adapter = new Adapter(models, getContext());
//                                    viewPager.setAdapter(adapter);
//                                }
//                            } else {
//                                int flag = 0;
//                                if(status.equals("on")) {
//                                    flag = 1;
//                                } else {
//                                    flag = 0;
//                                }
//                                image = R.drawable.ac;
//                                models2.add(new Model(image, status, device_name, baseUrl, "ac", iddevice, blynkurl, pin, flag));
//                                if(models2.size() > 0) {
//                                    adapter = new Adapter(models2, getContext());
//                                    viewPager.setAdapter(adapter);
//                                }
//                            }
//                        }
//                    } else {
//                        noData();
//                    }
//                } else {
//                    noData();
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    private void noData() {
        String data = getDefaults("data", getContext());
        if(!TextUtils.isEmpty(data)){
            try {
                JSONObject jsonObject = new JSONObject(data);
                if(!jsonObject.isNull("favorite")) {
                    JSONArray favorites = jsonObject.getJSONArray("favorite");
                    if(favorites.length() < 1) {
                        nodata_light.setVisibility(View.VISIBLE);
                        nodata_ac.setVisibility(View.VISIBLE);
                        light.setVisibility(View.GONE);
                        ac.setVisibility(View.GONE);
                    }
                    for(int i = 0; i < favorites.length(); i++) {
                        JSONObject jsonObject1 = favorites.getJSONObject(i);
                        if(!jsonObject1.getString("type").equals("light")) {
                            nodata_light.setVisibility(View.VISIBLE);
                            light.setVisibility(View.GONE);
                        } else if(!jsonObject1.getString("type").contains("ac")) {
                            nodata_ac.setVisibility(View.VISIBLE);
                            ac.setVisibility(View.GONE);
                        }
                    }
                } else {
                    nodata_light.setVisibility(View.VISIBLE);
                    nodata_ac.setVisibility(View.VISIBLE);
                    light.setVisibility(View.GONE);
                    ac.setVisibility(View.GONE);
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

//    private void lights() {
//        int image = 0;
//        String data = getDefaults("data", getContext());
//        String baseUrl = "";
//        String pin = "";
//        String blynkurl ="";
//        String iddevice = "";
//        String device_name = "";
//        String status = "";
//        models = new ArrayList<>();
//        if(!TextUtils.isEmpty(data)) {
//            try {
//                JSONObject jsonObject = new JSONObject(data);
//                if(!jsonObject.isNull("favorite")) {
//                    if(jsonObject.getJSONArray("favorite").length() > 0) {
//                        JSONArray favorites = jsonObject.getJSONArray("favorite");
//                        for(int y = 0; y < favorites.length(); y++) {
//                            JSONObject jsonObject3 = favorites.getJSONObject(y);
//                            if (jsonObject3.getString("type").contains("light")) {
//                                int flag = 0;
//                                device_name = jsonObject3.getString("device_name");
//                                baseUrl = config.server_temp + "deviceTrigger.php";
//                                pin = jsonObject3.getString("pin");
//                                blynkurl = "http://188.166.206.43:8080/" + blynk_key + "/update/" + pin;
//                                iddevice = jsonObject3.getString("iddevice");
//                                image = R.drawable.bulb;
//                                status = getStatus(iddevice);
//                                if(status.equals("on")) {
//                                    flag = 1;
//                                } else {
//                                    flag = 0;
//                                }
//                                models.add(new Model(image, status, device_name, baseUrl, "light", iddevice, blynkurl, pin, flag));
//                            }
//                        }
//                    }
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            if(models.size() > 0) {
//                adapter = new Adapter(models, getContext());
//                viewPager.setAdapter(adapter);
//                adapter.notifyDataSetChanged();
//                viewPager.setClipToPadding(false);
//                viewPager.setPadding(10, 0, 20, 0);
//                viewPager.setPageMargin(10);
//            }
//        }
//    }

//    private void showElemOnOff() {
//        if(models.size() > 1) {
//            light.setVisibility(View.VISIBLE);
//        } else if(models2.size() > 1) {
//            ac.setVisibility(View.VISIBLE);
//        } else {
//            light.setVisibility(View.GONE);
//            ac.setVisibility(View.GONE);
//        }
//    }

    private String getStatus(String iddevice) {
        String status = "";
       try {
           JSONObject jsonObject = new JSONObject(data);
           JSONArray jsonArray = jsonObject.getJSONArray("controller");
           for(int i = 0; i < jsonArray.length(); i++) {
               JSONObject jsonObject1 = jsonArray.getJSONObject(i);
               if(!jsonObject1.isNull("device")) {
                   JSONArray devices = jsonObject1.getJSONArray("device");
                   for(int x = 0; x < devices.length(); x++) {
                       final JSONObject jsonObject2 = devices.getJSONObject(x);
                       String id_device = jsonObject2.getString("iddevice");
                       if(id_device.equals(iddevice)) {
                           status = jsonObject2.getString("status");
                       }
                   }
               }
           }
       } catch(JSONException e) {
           e.printStackTrace();
       }
        return status;
    }

//    private void ac() {
//        models2 = new ArrayList<>();
//        int image = 0;
//        String data = getDefaults("data", getContext());
//
//        if(!TextUtils.isEmpty(data)) {
//            try {
//                JSONObject jsonObject = new JSONObject(data);
//                JSONArray jsonArray = jsonObject.getJSONArray("controller");
//                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
//                blynk_key = jsonObject1.getString("blynk_key");
//                if(!jsonObject.isNull("favorite")) {
//                    JSONArray devices = jsonObject.getJSONArray("favorite");
//                    for(int i = 0; i < devices.length(); i++) {
//                        JSONObject jsonObject2 = devices.getJSONObject(i);
//                        String device_name = jsonObject2.getString("device_name");
//                        if(jsonObject2.getString("type").contains("ac")) {
//                            String baseUrl = config.server_temp + "deviceTrigger.php";
//                            String iddevice = jsonObject2.getString("iddevice");
//                            String pin = jsonObject2.getString("pin");
//                            String blynkurl = "http://188.166.206.43:8080/" + blynk_key + "/update/" + pin;
//                            image = R.drawable.ac;
//                            int flag = 0;
//                            String status = getStatus(iddevice);
//                            if(status.equals("on")) {
//                                flag = 1;
//                            } else {
//                                flag = 0;
//                            }
//                            models2.add(new Model(image, status, device_name, baseUrl, "AC", iddevice, blynkurl, pin, flag));
//                        }
//                    }
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
//        if(models2.size() > 0) {
//            adapter2 = new Adapter(models2, getContext());
//            viewPager2.setAdapter(adapter2);
//            viewPager2.setClipToPadding(false);
//            viewPager2.setPadding(10, 0, 20, 0);
//            viewPager2.setPageMargin(10);
//        }
//    }

    private void logoutUser() {
        setDefaults("data", "", getContext());
        setDefaults("update", "", getContext());
        setDefaults("update_adapter", "", getContext());
        setDefaults("data_user", "", getContext());
        if(enabled) {
            timer.cancel();
        }
        session.setLogin(false);
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }

    private void LogOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure want to logout?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                logoutUser();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        logout = builder.create();
        logout.show();
    }


    void getDate(TextView dateTxtView) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("EEE, MMM d yyyy", Locale.US);
        String date = df.format(c);
        dateTxtView.setText(date);
    }

    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getDefaults(String key, Context context) {
        String result = "";
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        result = preferences.getString(key, null);
        if(result == null) {
            return null;
        }
        return result;
    }

    private void dapatWeather() {
        String weather = getDefaults("weather", getContext());
        try {
            JSONObject jsonObject = new JSONObject(weather);
            temp = jsonObject.getString("temp");
            humidity = jsonObject.getString("hum");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getDataUser() {
        if(!TextUtils.isEmpty(getDefaults("data_user", getContext()))) {
            String data = getDefaults("data_user", getContext());
            try {
                JSONObject user = new JSONObject(data);
                iduser = user.getString("iduser");
            } catch(JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getContext(), "There is no data user available! Please try again!", Toast.LENGTH_LONG).show();
        }
    }

    private void getData() {
        data = getDefaults("data", getContext());
       if(!TextUtils.isEmpty(data)) {
           try {
               JSONObject jsonObject = new JSONObject(data);
               JSONObject user = jsonObject.getJSONObject("user");
               idupdate = jsonObject.getString("idupdate");
               username = user.getString("name");
           } catch (JSONException e) {
               e.printStackTrace();
           }
       } else {
           Toast.makeText(getContext(), "There is no data available please try again!", Toast.LENGTH_LONG).show();
           new Handler().postDelayed(new Runnable() {
               @Override
               public void run() {
                   Intent intent = new Intent(getContext(), SplashActivity.class);
                   startActivity(intent);
               }
           }, 2000);
       }
    }

    private String SinkronUpdate() throws IOException {
        String url = "http://dataaihome2.itcs.co.id/syncThings.php";
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e(TAG, "Response dari sinkron home fragment : " + s);
            if(s == null || isCancelled()) {
                Log.e(TAG, "AsyncTask has been closed");
                Toast.makeText(getContext(), "Failed!", Toast.LENGTH_LONG).show();
                sinkron.cancel(true);
            } else {
                updateUi(s);
            }
        }
    }

    class SinkronFavorite extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String response = "";
            try {
                response = sinkronFavorite();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e(TAG, "Response dari sinkron favorite : " + s);
            if(s == null || isCancelled()) {
                Log.e("AsyncTask", "AsyncTask Favorite has been cancelled");
            }
        }
    }

    private void sinkronUpdate() {
        final Handler handler = new Handler();
        timer = new Timer();
        TimerTask doAsyncTask = new TimerTask() {
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
        enabled = true;
        timer.schedule(doAsyncTask, 0, 500);
    }

    private void syncFavorite() {
        final Handler handler = new Handler();
        timer2 = new Timer();
        TimerTask doAsyncTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sinkronFavorite = new SinkronFavorite();
                        sinkronFavorite.execute();
                    }
                }));
            }
        };
        timer2.schedule(doAsyncTask, 0, 500);
    }

    private void updateUi(String response) {
        if(!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equals("Y")) {
                    String idupdate = jsonObject.getString("idupdate_latest");
                    JSONArray jsonArray = jsonObject.getJSONArray("data_update");
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String iddevice = jsonObject1.getString("iddevice");
                        String value_after = jsonObject1.getString("value_after");
                        changeStatus(iddevice, value_after);
//                        updateData(iddevice, value_after);
                    }
                    timer.cancel();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sinkronUpdate();
                        }
                    }, 500);
                    changeIdUpdate(idupdate);
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
        } else {
            timer.cancel();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    sinkronUpdate();
                }
            }, 500);
        }
    }


    private void showSnack(String message, int color) {
        snackbar = Snackbar.make(parent, message, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundResource(color);
        snackbar.show();
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

    private void changeIdUpdate(String idupdate_update) {
        data = getDefaults("data", getContext());
        if(!TextUtils.isEmpty(data)) {
            try {
                idupdate = idupdate_update;
                JSONObject jsonObject = new JSONObject(data);
                String idupdate_latest = jsonObject.getString("idupdate");
                idupdate_latest = idupdate_update;
                jsonObject.put("idupdate", idupdate_latest);
                setDefaults("data", jsonObject.toString(), getContext());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getContext(), "Data is null", Toast.LENGTH_LONG).show();
        }
    }

//    private void updateData(String iddevice, String status) {
//        String data = "";
//        if(!TextUtils.isEmpty(getDefaults("data", getContext()))) {
//            data = getDefaults("data", getContext());
//        }
//        int image = 0;
//        if(!TextUtils.isEmpty(data)) {
//            try {
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
//                            String type = jsonObject2.getString("type");
//                            if (id_device.equals(iddevice)) {
//                                if (type.equals("light")) {
//                                    int flag = 0;
//                                    if (status.equals("on")) {
//                                        flag = 1;
//                                        image = R.drawable.bulb_white;
//                                    } else {
//                                        flag = 0;
//                                        image = R.drawable.bulb;
//                                    }
//                                    int position = updateLights(type, iddevice);
//                                    models.remove(position);
//                                    String pin = jsonObject2.getString("pin");
//                                    String device_name = jsonObject2.getString("device_name");
//                                    String baseUrl = "http://dataaihome2.itcs.co.id/deviceTrigger.php";
//                                    String blynkurl = "http://188.166.206.43:8080/" + blynk_key + "/update/" + pin;
//                                    models.add(position, new Model(image, status, device_name, baseUrl, "light", iddevice, blynkurl, pin, flag));
//                                    adapter = new Adapter(models, getContext());
//                                    viewPager.setAdapter(adapter);
//                                } else {
//                                    int flag = 0;
//                                    if (status.equals("on")) {
//                                        flag = 1;
//                                        image = R.drawable.ac_white;
//                                    } else {
//                                        flag = 0;
//                                        image = R.drawable.ac;
//                                    }
//                                    int position = updateLights(type, iddevice);
//                                    models2.remove(position);
//                                    String pin = jsonObject2.getString("pin");
//                                    String device_name = jsonObject2.getString("device_name");
//                                    String baseUrl = "http://dataaihome2.itcs.co.id/deviceTrigger.php";
//                                    String blynkurl = "http://188.166.206.43:8080/" + blynk_key + "/update/" + pin;
//                                    models2.add(position, new Model(image, status, device_name, baseUrl, "AC", iddevice, blynkurl, pin, flag));
//                                    adapter2 = new Adapter(models2, getContext());
//                                    viewPager2.setAdapter(adapter2);
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

    private void changeStatus(String iddevice, String status) {
        data = getDefaults("data", getContext());
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
        } else {
            Toast.makeText(getContext(), "Data is null", Toast.LENGTH_LONG).show();
        }
    }

    private String checkToRunSinkron() {
        String data = getDefaults("data", getContext());
        if(!TextUtils.isEmpty(data)) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                if(!jsonObject.isNull("favorite")) {
                    JSONArray favorite = jsonObject.getJSONArray("favorite");
                    if(favorite.length() > 0) {
                        checkupdate = "Yes";
                    } else {
                        checkupdate = "No";
                    }
                } else {
                    checkupdate = "No";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return checkupdate;
    }

    private String sinkronFavorite() throws IOException {
        String url = "http://dataaihome2.itcs.co.id/syncFavorite.php";
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

    private void checkUpdateFavorite(String response) {
        String data = getDefaults("data", getContext());
        try {
            JSONObject jsonObject = new JSONObject(response);
            String status = jsonObject.getString("status");
            if(status.equals("Y")) {
                JSONArray update = jsonObject.getJSONArray("data_update");
                for (int i = 0; i < update.length(); i++) {
                    JSONObject jsonObject1 = update.getJSONObject(i);

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupGroup() {
        hideElem(parent_nodatagroup);
        showElem(parent_group);
        loadDataGroup();
        groupAdapter = new GroupAdapter(groupItems, R.layout.group_recently_item, getContext());
        groups.setLayoutManager(new LinearLayoutManager(getContext()));
        groups.setAdapter(groupAdapter);
        Log.e(TAG, "Group Item count : " + String.valueOf(groupAdapter.getItemCount()));
        viewallgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GroupList.class);
                startActivity(intent);
            }
        });
    }

    private void loadDataGroup() {
        groupItems = new ArrayList<>();
        groupItems.add(new GroupItem("Group 1", "Helipin, Ricky, Willy, Welly, Joshua, Erik, Thomas", "1"));
        groupItems.add(new GroupItem("Group 1", "Helipin, Ricky, Willy, Welly, Joshua, Erik, Thomas", "1"));
        groupItems.add(new GroupItem("Group 1", "Helipin, Ricky, Willy, Welly, Joshua, Erik, Thomas", "1"));
    }

    private void hideElem(LinearLayout linearLayout) {
        linearLayout.setVisibility(View.GONE);
    }

    private void showElem(LinearLayout linearLayout) {
        linearLayout.setVisibility(View.VISIBLE);
    }


    @Override
    public void onStop() {
        super.onStop();
//        if(checkupdate.equals("Yes")) {
//            timer.cancel();
//            Log.e(TAG, "Sinkron telah berhenti");
//        }
    }

    private void getCityName() {
        String cityname = getDefaults("city", getContext());
        if(!TextUtils.isEmpty(cityname)) {
            city.setText(cityname);
        }
    }

    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format(  "yyyy-mm-dd_hh:mm:ss", now);
        try {
            String mPath = Environment.getExternalStorageState() + "/" + now + ".jpg";
            View view = getActivity().getWindow().getDecorView().getRootView();
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();
            int width =  getActivity().getWindowManager().getDefaultDisplay().getWidth();
            int height = getActivity().getWindowManager().getDefaultDisplay().getHeight();

            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, width, height);
            view.setDrawingCacheEnabled(false);

            File image = new File(mPath);
            FileOutputStream outputStream = new FileOutputStream(image);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            openScreenshot(image);
            Toast.makeText(getContext(), "Berhasil", Toast.LENGTH_LONG).show();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private Bitmap getBitmap(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    private void openScreenshot(File image) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(image);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    private void themeSettings() {
        String colorStr = getDefaults("host1_themecolor", getContext());
        if(!TextUtils.isEmpty(colorStr)) {
            Toast.makeText(getContext(), colorStr, Toast.LENGTH_LONG).show();
//            int color = Color.parseColor(colorStr);
//            toolbar.setBackgroundColor(color);
//            light.setTintColor(color);
//            ac.setTintColor(color);
        }
    }

    private void showDialogSwitch() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.progress_dialog, null);
        builder.setView(view);
        builder.setCancelable(false);
        builder.show();
    }
}