package com.itcs.aihome;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dpro.widgets.OnWeekdaysChangeListener;
import com.dpro.widgets.WeekdaysPicker;
import com.nex3z.togglebuttongroup.MultiSelectToggleGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.FRIDAY;
import static java.util.Calendar.THURSDAY;
import static java.util.Calendar.WEDNESDAY;
import static java.util.Calendar.TUESDAY;
import static java.util.Calendar.MONDAY;

public class Detail extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ButtonAdapter adapter;
    List<Integer> items, time_start_list, time_end_list, hari;
    List<String> days;
    private TextView devices, d_status, starttime, endtime, starttime_text, endtime_text;
    private Calendar calendar;
    private int hour, minute;
    private TimePickerDialog timePickerDialog, timePickerDialogEnd;
    private Button start, end, save, cancel, update, change;
    private ImageButton love;
    private View parent;
    private Snackbar snackbar;
    private ImageView imageView;
    private ArrayList<Integer> time_start, time_end;
    private CheckBox everyday;
    private String id_device, iduser, pin, device, status, type, idtimer, idtimer_save, idhouse;
    private LinearLayout container_time;
    private Switch time_control;
    private LinearLayout container1, container2, container3, container_configuration;
    private WeekdaysPicker weekdaysPicker;
    private AlertDialog alertDialog;

    int flag = 0;
    private String TAG = Detail.class.getSimpleName();
    private int id = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_light);

        devices =  findViewById(R.id.detail_perangkat);
        d_status =  findViewById(R.id.detail_status);
        start =  findViewById(R.id.starttimepicker);
        end =  findViewById(R.id.endtimepicker);
        love =  findViewById(R.id.lovebutton);
        imageView =  findViewById(R.id.detail_image);
        everyday = findViewById(R.id.everyday);
        container_time =  findViewById(R.id.container_detail_time);
        container_configuration = findViewById(R.id.container_configuration);
        starttime =  findViewById(R.id.starttime);
        endtime =  findViewById(R.id.endtime);
        save =  findViewById(R.id.simpan);
        cancel  =  findViewById(R.id.cancel);
        time_control = findViewById(R.id.timer_control);
        container1 =  findViewById(R.id.container_timer1);
        container2 =  findViewById(R.id.container_timer2);
        container3 =  findViewById(R.id.container_timeroff);
        weekdaysPicker = findViewById(R.id.weekdays);
        starttime_text = findViewById(R.id.start_time_text);
        endtime_text = findViewById(R.id.end_time_text);
        update = findViewById(R.id.update_conf);
        change = findViewById(R.id.change_timer);
        parent = findViewById(R.id.container_detail);
        devices.setText("");
        idhouse = getIdHouse();

        getDataUser();

        // calendar buat timer
        calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        items = new ArrayList<Integer>();
        items.add(SATURDAY);
        items.add(FRIDAY);
        items.add(THURSDAY);
        items.add(WEDNESDAY);
        items.add(TUESDAY);
        items.add(MONDAY);
        items.add(SUNDAY);

        getData();
        ToggleButton();
        HandleData();
        checkData();
        showDialog();
    }
    
    private void checkData() {
        String data = getDefaults("data", getApplicationContext());
        if(!TextUtils.isEmpty(data)) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray controller = jsonObject.getJSONArray("controller");
                for(int i = 0; i < controller.length(); i++) {
                    JSONObject jsonObject1 = controller.getJSONObject(i);
                    if(!jsonObject1.isNull("device")) {
                        JSONArray devices = jsonObject1.getJSONArray("device");
                        for(int x = 0; x < devices.length(); x++) {
                            JSONObject jsonObject2 = devices.getJSONObject(x);
                            if(jsonObject2.getString("iddevice").equals(id_device)) {
                                if(!jsonObject2.isNull("timer")) {
                                    sinkronTimer();
                                } else {
                                    disableAllItem();
                                    controlTime();
                                    // button start
                                    showTimePicker();
                                    // button end
                                    showTimePickerEnd();
                                    end.setEnabled(false);
                                    sendTime();
                                    weekDays();
                                    // set all visibility gone
                                    container1.setVisibility(View.GONE);
                                    container2.setVisibility(View.GONE);
                                    everyday.setVisibility(View.GONE);
                                    findViewById(R.id.container_configuration).setVisibility(View.GONE);
                                    // cancel timer
                                    cancelTimer();
                                    save.setEnabled(false);
                                    customDay();
                                }
                            }
                        }
                    }
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void customDay(){
        LinkedHashMap<Integer, Boolean> map = new LinkedHashMap<>();
        map.put(SUNDAY, false);
        map.put(MONDAY, false);
        map.put(TUESDAY, false);
        map.put(WEDNESDAY, false);
        map.put(THURSDAY, false);
        map.put(FRIDAY, false);
        map.put(SATURDAY, false);
        weekdaysPicker.setCustomDays(map);
    }

    private void disableAllItem() {
      everyday.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              if(((CheckBox) view).isChecked()) {
                  weekdaysPicker.setVisibility(View.GONE);
                  weekdaysPicker.setSelectedDays(items);
              } else {
                  weekdaysPicker.setVisibility(View.VISIBLE);
                  customDay();
              }
          }
      });
    }

    private void weekDays() {
        weekdaysPicker.setOnWeekdaysChangeListener(new OnWeekdaysChangeListener() {
            @Override
            public void onChange(View view, int i, List<Integer> list) {
                Toast.makeText(getApplicationContext(), "Ditambahkan", Toast.LENGTH_SHORT).show();
            }
        });
        if(weekdaysPicker.allDaysSelected()) {
            everyday.setChecked(true);
            weekdaysPicker.setVisibility(View.GONE);
        }
    }

    private void cancelTimer() {
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogCancel();
                end.setEnabled(false);
            }
        });
    }

    private void dialogCancel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Detail.this);
        builder.setMessage("Are you sure want to cancel?");
        builder.setTitle("Confirmation cancel");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                container_time.setVisibility(View.GONE);
                container2.setVisibility(View.GONE);
                findViewById(R.id.container_button).setVisibility(View.GONE);
                time_control.setChecked(false);
                everyday.setChecked(false);
                everyday.setVisibility(View.GONE);
                if((time_start.size() > 0 && time_end.size() > 0) || (time_start.size() > 0 || time_end.size() > 0))  {
                    time_start.clear();
                    time_end.clear();
                }
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setIcon(R.drawable.exclamation).show();
    }

    private void controlTime() {
        time_control.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    container1.setVisibility(View.VISIBLE);
                    container2.setVisibility(View.VISIBLE);
                    container3.setVisibility(View.GONE);
                    everyday.setVisibility(View.VISIBLE);
                } else {
                    hapusTimerDialog();
                    container3.setVisibility(View.VISIBLE);
                    container1.setVisibility(View.GONE);
                    container2.setVisibility(View.GONE);
                    everyday.setVisibility(View.GONE);
                    container_time.setVisibility(View.GONE);
                }
            }
        });
    }

    private void sendTime() {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(time_end.size() > 0 && time_start.size() > 0 && !weekdaysPicker.noDaySelected() && weekdaysPicker.getSelectedDaysText().size() > 0) {
                    kirimWaktu(weekdaysPicker.getSelectedDays().toString(), time_start.toString(), time_end.toString());
                    container_time.setVisibility(View.GONE);
                    container2.setVisibility(View.GONE);
                    findViewById(R.id.container_button).setVisibility(View.GONE);
                } else {
                    Toast.makeText(Detail.this, "Please Fill the empty field!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateTime() {
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(time_start_list.size() > 0 && time_end_list.size() > 0 && !weekdaysPicker.noDaySelected()) {
                    updateTimer(weekdaysPicker.getSelectedDays().toString(), time_start_list.toString(), time_end_list.toString(), idtimer);
                    updateData(weekdaysPicker.getSelectedDays().toString(), time_start_list.toString(), time_end_list.toString());
                    onFinishUpdate();
                }
            }
        });
    }

    private void onFinishUpdate() {
        container_configuration.setVisibility(View.VISIBLE);
        findViewById(R.id.container_detail_time_conf).setVisibility(View.VISIBLE);
        container2.setVisibility(View.GONE);
        container_time.setVisibility(View.GONE);
        starttime_text.setText("Start time : " + String.valueOf(time_start_list.get(0) + ":" + time_start_list.get(1)));
        endtime_text.setText("End time : " + String.valueOf(time_end_list.get(0) + ":" + time_end_list.get(1)));
        findViewById(R.id.cancel_time_conf).setVisibility(View.GONE);
    }

    private void updateData(String hari, String time_start, String time_end) {
        String data = getDefaults("data", getApplicationContext());
        if(!TextUtils.isEmpty(data)) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray controller = jsonObject.getJSONArray("controller");
                for(int i = 0; i < controller.length(); i++) {
                    JSONObject jsonObject1 = controller.getJSONObject(i);
                    if(!jsonObject1.isNull("device")) {
                        JSONArray devices = jsonObject1.getJSONArray("device");
                        for(int x = 0; x < devices.length(); x++) {
                            JSONObject jsonObject2 = devices.getJSONObject(x);
                            if(jsonObject2.getString("iddevice").equals(id_device)) {
                               if(!jsonObject2.isNull("timer")) {
                                   JSONObject jsonObject3 = new JSONObject();
                                   jsonObject3.put("idtimer", idtimer);
                                   jsonObject3.put("waktu_mulai", time_start);
                                   jsonObject3.put("waktu_selesai", time_end);
                                   jsonObject3.put("hari", hari);
                                   jsonObject2.put("timer", jsonObject3);
                                   Log.e(TAG, "Response dari timer yang aku update timer : " + jsonObject.toString());
                                   setDefaults("data", jsonObject.toString(), getApplicationContext());
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

    private void insertData(List<Integer> start_time, List<Integer> end_time, List<Integer> days, String idtimer) {
        String data = getDefaults("data", getApplicationContext());
        if(!TextUtils.isEmpty(data)) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray controller = jsonObject.getJSONArray("controller");
                for(int i = 0; i < controller.length(); i++) {
                    JSONObject jsonObject1 = controller.getJSONObject(i);
                    if(!jsonObject1.isNull("device")) {
                        JSONArray devices = jsonObject1.getJSONArray("device");
                        for(int x = 0; x < devices.length(); x++) {
                            JSONObject jsonObject2 = devices.getJSONObject(x);
                            if(jsonObject2.getString("iddevice").equals(id_device)) {
                                JSONObject jsonObject3 = new JSONObject();
                                jsonObject3.put("waktu_mulai", start_time.toString());
                                jsonObject3.put("waktu_selesai", end_time.toString());
                                JSONArray jsonArray1 = new JSONArray();
                                for(int y = 0; y < days.size(); y++) {
                                    jsonArray1.put(days.get(y));
                                }
                                jsonObject3.put("hari", jsonArray1.toString());
                                jsonObject3.put("idtimer", idtimer);
                                jsonObject2.put("timer", jsonObject3);
                                Log.e(TAG, "Response dari timer yang aku tambahkan : " + jsonObject.toString());
                                setDefaults("data", jsonObject.toString(), getApplicationContext());
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void convertStringToList(String type, String times) {
        times = times.replace("[", "");
        times = times.replace(" ", "");
        times = times.replace("]", "");
        if(type.equals("time_start")) {
            time_start_list = new ArrayList<>();
            time_start_list.clear();
            String[] time = times.split(",");
            for(int i = 0; i < time.length; i++) {
                time_start_list.add(Integer.parseInt(time[i]));
            }
        } else if(type.equals("time_end")) {
            time_end_list = new ArrayList<>();
            time_end_list.clear();
            String[] time = times.split(",");
            for(int i = 0; i < time.length; i++) {
                time_end_list.add(Integer.parseInt(time[i]));
            }
        } else if(type.equals("hari")) {
            hari = new ArrayList<>();
            hari.clear();
            String[] time = times.split(",");
            for(int i = 0; i < time.length; i++) {
                hari.add(Integer.parseInt(time[i]));
            }
        }
    }

    private void kirimWaktu(final String hari, final String start_time, final String end_time) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = config.server_temp + "setTimer.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if(status.equals("success")) {
                            idtimer_save = jsonObject.getString("idtimer");
                            insertData(time_start, time_end, weekdaysPicker.getSelectedDays(), idtimer_save);
                            sinkronTimer();
                            showSnack("Successfull to save timer configuration!", R.color.colorGreen);
                        } else {
                            showSnack("There was an error! Try again!", R.color.colorRed);
                        }
                        Log.e(TAG, "Response dari save timer : " + response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    showSnack("There was an error!", R.color.colorRed);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "There was an error!", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("waktu_mulai", start_time);
                params.put("waktu_selesai", end_time);
                params.put("hari", hari);
                params.put("iddevice", id_device);
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

    private void updateTimer(final String hari, final String time_start, final String time_end, final String idtimer) {
        String url = config.server_temp + "updateTimer.php";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
              if(response != null) {
                  try {
                      JSONObject jsonObject = new JSONObject(response);
                      String status = jsonObject.getString("status");
                      if(status.equals("success")) {
                          showSnack("Successfull to update time configuration!", R.color.colorGreen);
                      } else {
                          showSnack("There was an error to update time configuration!", R.color.colorRed);
                      }
                  } catch (JSONException e) {
                      e.printStackTrace();
                  }
              } else {
                  showSnack("There was an error!", R.color.colorRed);
              }
                Log.e("updatetimer", "Ini dia response update timer" + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("hari", hari);
                params.put("waktu_mulai", time_start);
                params.put("waktu_selesai", time_end);
                params.put("idtimer", idtimer);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void getData() {
        Intent intent = getIntent();
        device = intent.getStringExtra("device");
        status = intent.getStringExtra("status");
        id_device = intent.getStringExtra("iddevice");
        devices.setText(device);
        d_status.setText(status);
        pin = intent.getStringExtra("pin");
        type = intent.getStringExtra("type");
        imageView.setImageResource(intent.getIntExtra("gambar", R.drawable.bulb));
    }

    private void showTimePicker() {
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                time_start = new ArrayList<>();
                time_start.add(i);
                time_start.add(i1);
                timePickerDialog.dismiss();
                String hour = "";
                String minute = "";
                if(i < 10 || i1 < 10) {
                    hour = String.format("%02d", i);
                    minute = String.format("%02d", i1);
                } else if(i < 10 && i1 < 10){
                    hour = String.format("%02d", i);
                    minute = String.format("%02d", i1);
                } else {
                    hour = String.valueOf(i);
                    minute = String.valueOf(i1);
                }
                starttime.setText("Start time : " + hour + ":" + minute);
                end.setEnabled(true);
                container_time.setVisibility(View.VISIBLE);
                findViewById(R.id.container_button).setVisibility(View.VISIBLE);
            }
        }, hour, minute, true);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              timePickerDialog.show();
            }
        });
    }

    private void updateTimePickerStart() {
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                time_start_list.clear();
                time_start_list.add(i);
                time_start_list.add(i1);
                timePickerDialog.dismiss();
                String hour = "";
                String minute = "";
                if(i < 10 || i1 < 10) {
                    hour = String.format("%02d", i);
                    minute = String.format("%02d", i1);
                } else if(i < 10 && i1 < 10){
                    hour = String.format("%02d", i);
                    minute = String.format("%02d", i1);
                } else {
                    hour = String.valueOf(i);
                    minute = String.valueOf(i1);
                }
                starttime.setText("Start time : " + hour + ":" + minute);
                end.setEnabled(true);
                container_time.setVisibility(View.VISIBLE);
            }
        }, time_start_list.get(0), time_start_list.get(1), true);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog.show();
            }
        });
    }

    private void updateTimePickerEnd() {
        timePickerDialogEnd = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                time_end_list.clear();
                time_end_list.add(i);
                time_end_list.add(i1);
                timePickerDialogEnd.dismiss();
                String hour = "";
                String minute = "";
                if(i < 10 || i1 < 10) {
                    hour = String.format("%02d", i);
                    minute = String.format("%02d", i1);
                } else if(i < 10 && i1 < 10){
                    hour = String.format("%02d", i);
                    minute = String.format("%02d", i1);
                } else {
                    hour = String.valueOf(i);
                    minute = String.valueOf(i1);
                }
                endtime.setText("End time : " + hour + ":" + minute);
            }
        }, time_end_list.get(0), time_end_list.get(1), true);
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialogEnd.show();
            }
        });
    }

    private void showTimePickerEnd() {
        timePickerDialogEnd = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                time_end = new ArrayList<>();
                time_end.add(i);
                time_end.add(i1);
                timePickerDialogEnd.dismiss();
                String hour = "";
                String minute = "";
                if(i < 10 || i1 < 10) {
                    hour = String.format("%02d", i);
                    minute = String.format("%02d", i1);
                } else if(i < 10 && i1 < 10){
                    hour = String.format("%02d", i);
                    minute = String.format("%02d", i1);
                } else {
                    hour = String.valueOf(i);
                    minute = String.valueOf(i1);
                }
                endtime.setText("End time : " + hour + ":" + minute);
                save.setEnabled(true);
            }
        }, hour, minute, true);
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialogEnd.show();
            }
        });
    }

    private void sinkronTimer() {
        String data = getDefaults("data", getApplicationContext());
        if(!TextUtils.isEmpty(data)) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray jsonArray = jsonObject.getJSONArray("controller");
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    if(!jsonObject1.isNull("device")) {
                        JSONArray devices = jsonObject1.getJSONArray("device");
                        for(int x = 0; x < devices.length(); x++) {
                            JSONObject jsonObject2 = devices.getJSONObject(x);
                            if(jsonObject2.getString("iddevice").equals(id_device)) {
                                if(!jsonObject2.isNull("timer")) {
                                    time_control.setChecked(true);
                                    JSONObject jsonObject3 = jsonObject2.getJSONObject("timer");
                                    idtimer = jsonObject3.getString("idtimer");
                                    String days = jsonObject3.getString("hari");
                                    String start_time = jsonObject3.getString("waktu_mulai");
                                    String end_time = jsonObject3.getString("waktu_selesai");
                                    convertStringToList("time_start", start_time);
                                    convertStringToList("time_end", end_time);
                                    convertStringToList("hari", days);
                                    container2.setVisibility(View.GONE);
                                    container3.setVisibility(View.GONE);
                                    container_time.setVisibility(View.GONE);
                                    container_configuration.setVisibility(View.VISIBLE);
                                    findViewById(R.id.container_save_cancel).setVisibility(View.VISIBLE);
                                    findViewById(R.id.cancel_time_conf).setVisibility(View.GONE);
                                    everyday.setVisibility(View.VISIBLE);
                                    disableAllItemOnUpdate();
                                    controlTimeOnUpdate();
                                    updateTime();
                                    if(hari.size() == 7) {
                                        everyday.setChecked(true);
                                        container1.setVisibility(View.GONE);
                                    } else {
                                        everyday.setChecked(false);
                                        container1.setVisibility(View.VISIBLE);
                                        setDays(hari);
                                    }
                                    starttime_text.setText("Start time : " + String.valueOf(time_start_list.get(0) + ":" + time_start_list.get(1)));
                                    endtime_text.setText("End time : " + String.valueOf(time_end_list.get(0) + ":" + time_end_list.get(1)));
                                    changeTime();
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

    private void controlTimeOnUpdate() {
        time_control.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    weekdaysPicker.setVisibility(View.VISIBLE);
                    everyday.setVisibility(View.VISIBLE);
                    findViewById(R.id.container_save_cancel).setVisibility(View.VISIBLE);
                } else {
                    hapusTimerDialog();
                }
            }
        });
    }

    private void changeTime() {
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                container2.setVisibility(View.VISIBLE);
                findViewById(R.id.container_detail_time_conf).setVisibility(View.GONE);
                findViewById(R.id.cancel_time_conf).setVisibility(View.VISIBLE);
                cancelUpdateTime();
            }
        });
        updateTimePickerStart();
        updateTimePickerEnd();
    }

    private void cancelUpdateTime() {
        findViewById(R.id.cancel_time_conf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                container2.setVisibility(View.GONE);
                findViewById(R.id.container_detail_time_conf).setVisibility(View.VISIBLE);
                findViewById(R.id.cancel_time_conf).setVisibility(View.GONE);
            }
        });
    }

    private void disableAllItemOnUpdate() {
        everyday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    container1.setVisibility(View.GONE);
                    weekdaysPicker.setSelectedDays(items);
                } else {
                    weekdaysPicker.setSelectedDays(hari);
                    container1.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setDays(List<Integer> days) {
        weekdaysPicker.setSelectedDays(days);
    }

    private void setViewIfDeviceExist(String time_start, String time_end, String day) {
        starttime_text.setText(time_start);
        endtime_text.setText(time_end);
    }

    private void ToggleButton() {
        parent = findViewById(R.id.container_detail);
        love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag == 0) {
                    snackbar = Snackbar.make(parent, "Success add to favorite!", Snackbar.LENGTH_SHORT);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundResource(R.color.colorGreen);
                    snackbar.show();
                    flag = 1;
                    addToFavorite();
                    love.setBackgroundResource(R.drawable.love_fill);
                } else {
                    flag = 0;
                    deleteFavorite();
                    love.setBackgroundResource(R.drawable.love);
                    snackbar = Snackbar.make(parent, "Removed from favorite!", Snackbar.LENGTH_SHORT);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundResource(R.color.colorRed);
                    snackbar.show();
                }
            }
        });
    }

    private void HandleData() {
        class handleData extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                sinkronFavorite();
                return null;
            }
        }
        new handleData().execute();
    }

    private void addToFavorite() {
        String url = config.server_temp + "saveFavorite.php";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Response from url : " + response);
                try {
                    String data = getDefaults("data", getApplicationContext());
                    if(!TextUtils.isEmpty(data)) {
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

                                        }
                                    }
                                }
                            }
                        }
                        setDefaults("data", jsonObject.toString(), getApplicationContext());
                        Log.e(TAG, "Setelah aku tambah favorite : " + jsonObject.toString());
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
                params.put("iddevice", id_device);
                params.put("idhouse", idhouse);
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

    private void deleteFavorite() {
        String url = config.server_temp + "deleteFavorite.php";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Response from url : " + response);
                String data = getDefaults("data", getApplicationContext());
                if(!TextUtils.isEmpty(data)) {
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        JSONArray jsonArray = jsonObject.getJSONArray("favorite");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String iddevice = jsonObject1.getString("iddevice");
                            if (iddevice.equals(id_device)) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    jsonArray.remove(i);
                                }
                            }
                        }
                        setDefaults("data", jsonObject.toString(), getApplicationContext());
                        setDefaults("update_favorite", "true", getApplicationContext());
                        Log.e(TAG, "Setelah aku hapus favorite : " + jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
                params.put("iddevice", id_device);
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

    private void sinkronFavorite() {
        String data = getDefaults("data", getApplicationContext());
        if(!TextUtils.isEmpty(data)) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                if(!jsonObject.isNull("favorite")) {
                    JSONArray favorite = jsonObject.getJSONArray("favorite");
                    if(favorite.length() > 0) {
                        for(int i = 0; i < favorite.length(); i++) {
                            JSONObject jsonObject1 = favorite.getJSONObject(i);
                            String iddevice = jsonObject1.getString("iddevice");
                            if(iddevice.equals(id_device)) {
                                flag = 1;
                                love.setEnabled(true);
                                love.setBackgroundResource(0);
                                love.setBackgroundResource(R.drawable.love_fill);
                            } else {
                                if(favorite.length() == 5) {
                                    love.setEnabled(false);
                                }
                            }
                        }
                    }
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }


    public void getDataUser() {
        if(getDefaults("data_user", getApplicationContext()) != null) {
            String data = getDefaults("data_user", getApplicationContext());
            try {
                JSONObject user = new JSONObject(data);
                iduser = user.getString("iduser");
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void hapusTimerDialog() {
       String data = getDefaults("data", getApplicationContext());
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
                           if(jsonObject2.getString("iddevice").equals(id_device)) {
                               if(!jsonObject2.isNull("timer")) {
                                   AlertDialog.Builder builder = new AlertDialog.Builder(Detail.this);
                                   builder.setMessage("Are you sure want to delete this device's timer configuration?");
                                   builder.setTitle("Confirmation");
                                   builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialogInterface, int i) {
                                           hapusTimer();
                                           deleteTimer(idtimer);
                                           time_start_list.clear();
                                           time_end_list.clear();
                                           findViewById(R.id.container_detail_time_conf).setVisibility(View.GONE);
                                           container3.setVisibility(View.VISIBLE);
                                           time_control.setChecked(false);
                                           everyday.setVisibility(View.GONE);
                                           findViewById(R.id.container_save_cancel).setVisibility(View.GONE);
                                           weekdaysPicker.setVisibility(View.GONE);
                                       }
                                   });
                                   builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialogInterface, int i) {
                                           dialogInterface.dismiss();
                                           time_control.setChecked(true);
                                           findViewById(R.id.container_timeroff).setVisibility(View.GONE);
                                           container2.setVisibility(View.GONE);
                                           Log.e(TAG, "Length of start_time : " + String.valueOf(time_start_list.size()) + " Length of end_time : " + String.valueOf(time_end_list.size()) + " Hari : " + String.valueOf(hari.size()));
                                       }
                                   }).setIcon(R.drawable.exclamation).show();
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

    private void hapusTimer() {
        String data = getDefaults("data", getApplicationContext());
        if(!TextUtils.isEmpty(data)) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray controller = jsonObject.getJSONArray("controller");
                for(int i = 0; i < controller.length(); i++) {
                    JSONObject jsonObject1 = controller.getJSONObject(i);
                    if(!jsonObject1.isNull("device")) {
                        JSONArray devices = jsonObject1.getJSONArray("device");
                        for(int x = 0; x < devices.length(); x++) {
                            JSONObject jsonObject2 = devices.getJSONObject(x);
                            String iddevice = jsonObject2.getString("iddevice");
                            if(iddevice.equals(id_device)) {
                                if(!jsonObject2.isNull("timer")) {
                                    jsonObject2.put("timer", null);
                                }
                            }
                        }
                    }
                }
                setDefaults("data", jsonObject.toString(), getApplicationContext());
                Log.e(TAG, "Setelah hapus timer : " + jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static JSONArray remove(final int idx, final JSONArray from) {
        final List<JSONObject> objs = asList(from);
        objs.remove(idx);

        final JSONArray ja = new JSONArray();
        for (final JSONObject obj : objs) {
            ja.put(obj);
        }

        return ja;
    }

    public static List<JSONObject> asList(final JSONArray ja) {
        final int len = ja.length();
        final ArrayList<JSONObject> result = new ArrayList<JSONObject>(len);
        for (int i = 0; i < len; i++) {
            final JSONObject obj = ja.optJSONObject(i);
            if (obj != null) {
                result.add(obj);
            }
        }
        return result;
    }

    private void showSnack(String message, int color) {
        snackbar = Snackbar.make(parent, message, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundResource(color);
        snackbar.show();
    }

    private void deleteTimer(final String idtimer) {
        String url = config.server_temp + "deleteTimer.php";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if(status.equals("success")) {
                        showSnack("Success to delete timer configuration!", R.color.colorGreen);
                    } else {
                        showSnack("Failed to delete timer configuration!", R.color.colorRed);
                    }
                    Log.e(TAG, "Response dari delete timer : " + response);
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
                Map<String, String> params = new HashMap<>();
                params.put("idtimer", idtimer);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void showDialog() {
        devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditDialog();
            }
        });
    }

    private void EditDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View view = inflater.inflate(R.layout.dialog, null);
        builder.setView(view);
        final EditText editText = view.findViewById(R.id.new_device_name);
        builder.setCancelable(false);
        builder.setTitle("Change Device Name");
        builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(!editText.getText().toString().trim().isEmpty()) {
                    changeDeviceName(id_device, editText.getText().toString().trim());
                    devices.setText(editText.getText().toString());
                    setDefaults("update_data", "yes", getApplicationContext());
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

    private void changeDeviceName(final String iddevice, final String device_name) {
        String url = config.server_temp + "updateDevice_Name.php";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Response from url (Change Device Name) : " + response + "iddevice : " + iddevice);
                try {
                    String data = getDefaults("data", getApplicationContext());
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
                                    setDefaults("data", jsonObject.toString(), getApplicationContext());
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
        String data = getDefaults("data", getApplicationContext());
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
                setDefaults("data", jsonObject.toString(), getApplicationContext());
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String getIdHouse() {
        String idhouse = "";
        if(getDefaults("data", getApplicationContext()) != null) {
            String data = getDefaults("data", getApplicationContext());
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray house = jsonObject.getJSONArray("house");
                for (int i = 0; i < house.length(); i++) {
                    JSONObject jsonObject1 = house.getJSONObject(i);
                    idhouse = jsonObject1.getString("idhouse");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
         }
         return idhouse;
    }
}
