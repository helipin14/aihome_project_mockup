package com.itcs.aihome;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

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
    private ImageButton imageButton, connection;
    private TextView tempTextView, humidityTextView, dateTextView, user_name;
    private String temp, humidity, blynk_key, checkupdate;
    private String username, data, idupdate, iduser;
    private LinearLayout  nodata_light, nodata_ac;
    private Sinkron sinkron;
    private SinkronFavorite sinkronFavorite;
    private Timer timer, timer2;
    private AssetManager assetManager;
    private Typeface typeface;
    private Snackbar snackbar;
    private LinearLayout parent;
    private LocationManager locationManager;

    public static HomeFragment newInstance() {
        HomeFragment homeFragment = new HomeFragment();
        return homeFragment;
    }

    public HomeFragment() {}

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
        assetManager = getContext().getAssets();
        typeface = Typeface.createFromAsset(assetManager, "fonts/valeraround.ttf");
        parent = view.findViewById(R.id.parent_home);
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        getData();
        getDataUser();

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

        if (getDefaults("data", getContext()) != null) {
            data = getDefaults("data", getContext());
        }
        lights();
        ac();
        noData();
        checkToRunSinkron();
        if(checkupdate.equals("Yes")) {
//            sinkronUpdate();
        }
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        if(checkupdate.equals("Yes")) {
//            timer.cancel();
        }
//        timer2.cancel();
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
        if(checkupdate.equals("Yes")) {
//            timer.cancel();
        }
//        timer2.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        timer.cancel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        timer.cancel();
    }

    private void checkData() {
        String data = getDefaults("data", getContext());
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
                    updateList();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateList() {
        String data = getDefaults("data", getContext());
        models2 = new ArrayList<>();
        models = new ArrayList<>();
        models.clear();
        models2.clear();
        if(!TextUtils.isEmpty(data)) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                if(!jsonObject.isNull("favorite")) {
                    JSONArray favorites = jsonObject.getJSONArray("favorite");
                    if(favorites.length() > 0) {
                        for(int i = 0; i < favorites.length(); i++) {
                            JSONObject jsonObject1 = favorites.getJSONObject(i);
                            String type = jsonObject1.getString("type");
                            String iddevice = jsonObject1.getString("iddevice");
                            String status = getStatus(iddevice);
                            String device_name = jsonObject1.getString("device_name");
                            String baseUrl = "http://dataaihome.itcs.co.id/deviceTrigger.php";
                            String pin = jsonObject1.getString("pin");
                            String blynkurl = "http://188.166.206.43:8080/" + blynk_key + "/update/" + pin;
                            int image = 0;
                            if(type.equals("light")) {
                                image = R.drawable.bulb;
                                models.add(new Model(image, status, device_name, baseUrl, "light", iddevice, blynkurl, pin));
                                if(models.size() > 0) {
                                    adapter = new Adapter(models, getContext());
                                    viewPager.setAdapter(adapter);
                                }
                            } else {
                                image = R.drawable.ac;
                                models2.add(new Model(image, status, device_name, baseUrl, "ac", iddevice, blynkurl, pin));
                                if(models2.size() > 0) {
                                    adapter = new Adapter(models2, getContext());
                                    viewPager.setAdapter(adapter);
                                }
                            }
                        }
                    } else {
                        noData();
                    }
                } else {
                    noData();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

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
                    }
                    for(int i = 0; i < favorites.length(); i++) {
                        JSONObject jsonObject1 = favorites.getJSONObject(i);
                        if(!jsonObject1.getString("type").equals("light")) {
                            nodata_light.setVisibility(View.VISIBLE);
                        } else if(!jsonObject1.getString("type").contains("ac")) {
                            nodata_ac.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    nodata_light.setVisibility(View.VISIBLE);
                    nodata_ac.setVisibility(View.VISIBLE);
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void lights() {
        int image = 0;
        String data = getDefaults("data", getContext());
        String baseUrl = "";
        String pin = "";
        String blynkurl ="";
        String iddevice = "";
        String device_name = "";
        String status = "";
        models = new ArrayList<>();
        if(!TextUtils.isEmpty(data)) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                if(!jsonObject.isNull("favorite")) {
                    if(jsonObject.getJSONArray("favorite").length() > 0) {
                        JSONArray favorites = jsonObject.getJSONArray("favorite");
                        for(int y = 0; y < favorites.length(); y++) {
                            JSONObject jsonObject3 = favorites.getJSONObject(y);
                            if (jsonObject3.getString("type").contains("light")) {
                                device_name = jsonObject3.getString("device_name");
                                baseUrl = "http://dataaihome.itcs.co.id/deviceTrigger.php";
                                pin = jsonObject3.getString("pin");
                                blynkurl = "http://188.166.206.43:8080/" + blynk_key + "/update/" + pin;
                                iddevice = jsonObject3.getString("iddevice");
                                image = R.drawable.bulb;
                                status = getStatus(iddevice);
                                models.add(new Model(image, status, device_name, baseUrl, "light", iddevice, blynkurl, pin));
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(models.size() > 0) {
                adapter = new Adapter(models, getContext());
                viewPager.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                viewPager.setClipToPadding(false);
                viewPager.setPadding(10, 0, 20, 0);
                viewPager.setPageMargin(10);
            }
        }
    }

    private String getStatus(String iddevice) {
        String status = "";
       try {
           JSONObject jsonObject = new JSONObject(data);
           JSONArray jsonArray = jsonObject.getJSONArray("controller");
           for(int i = 0; i < jsonArray.length(); i++) {
               JSONObject jsonObject1 = jsonArray.getJSONObject(i);
               if(jsonObject1.isNull("device")) {
               } else {
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

    private void ac() {
        models2 = new ArrayList<>();
        int image = 0;
        String data = getDefaults("data", getContext());

        if(!TextUtils.isEmpty(data)) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray jsonArray = jsonObject.getJSONArray("controller");
                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                blynk_key = jsonObject1.getString("blynk_key");
                if(!jsonObject.isNull("favorite")) {
                    JSONArray devices = jsonObject.getJSONArray("favorite");
                    for(int i = 0; i < devices.length(); i++) {
                        JSONObject jsonObject2 = devices.getJSONObject(i);
                        String device_name = jsonObject2.getString("device_name");
                        if(jsonObject2.getString("type").contains("ac")) {
                            String baseUrl = "http://dataaihome.itcs.co.id/deviceTrigger.php";
                            String iddevice = jsonObject2.getString("iddevice");
                            String pin = jsonObject2.getString("pin");
                            String blynkurl = "http://188.166.206.43:8080/" + blynk_key + "/update/" + pin;
                            image = R.drawable.ac;
                            String status = getStatus(iddevice);
                            models2.add(new Model(image, status, device_name, baseUrl, "AC", iddevice, blynkurl, pin));
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        if(models2.size() > 0) {
            adapter2 = new Adapter(models2, getContext());
            viewPager2.setAdapter(adapter2);
            viewPager2.setClipToPadding(false);
            viewPager2.setPadding(10, 0, 20, 0);
            viewPager2.setPageMargin(10);
        }
    }

    private void logoutUser() {
        setDefaults("data", "", getContext());
        setDefaults("update", "", getContext());
        setDefaults("update_adapter", "", getContext());
        setDefaults("data_user", "", getContext());
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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
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
                    updateData(iddevice, value_after);
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
        String data = getDefaults("data", getContext());
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
                                        image = R.drawable.bulb_white;
                                    } else {
                                        image = R.drawable.bulb;
                                    }
                                    int position = updateLights(type, iddevice);
                                    models.remove(position);
                                    String pin = jsonObject2.getString("pin");
                                    String device_name = jsonObject2.getString("device_name");
                                    String baseUrl = "http://dataaihome.itcs.co.id/deviceTrigger.php";
                                    String blynkurl = "http://188.166.206.43:8080/" + blynk_key + "/update/" + pin;
                                    models.add(position, new Model(image, status, device_name, baseUrl, "light", iddevice, blynkurl, pin));
                                    adapter = new Adapter(models, getContext());
                                    viewPager.setAdapter(adapter);
                                } else {
                                    if (status.equals("on")) {
                                        image = R.drawable.ac_white;
                                    } else {
                                        image = R.drawable.ac;
                                    }
                                    int position = updateLights(type, iddevice);
                                    models2.remove(position);
                                    String pin = jsonObject2.getString("pin");
                                    String device_name = jsonObject2.getString("device_name");
                                    String baseUrl = "http://dataaihome.itcs.co.id/deviceTrigger.php";
                                    String blynkurl = "http://188.166.206.43:8080/" + blynk_key + "/update/" + pin;
                                    models2.add(position, new Model(image, status, device_name, baseUrl, "AC", iddevice, blynkurl, pin));
                                    adapter2 = new Adapter(models2, getContext());
                                    viewPager2.setAdapter(adapter2);
                                    delayViewPager(viewPager2);
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

    private void changeStatus(String iddevice, String status) {
        String data = getDefaults("data", getContext());
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
        String url = "http://dataaihome.itcs.co.id/syncFavorite.php";
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

    private void delayViewPager(final ViewPager viewPager) {
        Toast.makeText(getContext(), "has been disabled for a while please wait!", Toast.LENGTH_LONG).show();
        viewPager.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                viewPager.setEnabled(true);
            }
        }, 1000);
    }
}