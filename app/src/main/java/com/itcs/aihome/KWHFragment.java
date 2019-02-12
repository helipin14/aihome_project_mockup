package com.itcs.aihome;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class KWHFragment extends Fragment {

    private DetailAdapter detailAdapter;
    private List<DetailModel> detailModels;
    private WrapContentViewPager detail, grafik;
    private DotsIndicator detail_dots, grafik_dots;
    private List<BarEntry> entries;
    private List<BarEntry> entries2;
    private Calendar calendar;
    private GrafikAdapter adapter;
    private List<GrafikItem> items;

    public static KWHFragment newInstance() {
        KWHFragment kwhFragment = new KWHFragment();
        return kwhFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.kwh, container, false);
        detail = view.findViewById(R.id.kwh_hari_ini);
        grafik = view.findViewById(R.id.grafik_viewpager);
        detail_dots = view.findViewById(R.id.dots_indicator);
        grafik = view.findViewById(R.id.grafik_viewpager);
        grafik_dots = view.findViewById(R.id.grafik_dots);
        calendar = Calendar.getInstance();
        setupChart();
        setupViewPagerDetail();
        return view;
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    private String getKWH() throws IOException {
        String url = "http://dataaihome.itcs.co.id/getKwh.php";
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("", "")
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }

    class Sinkron extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String response = "";
            try {
                response = getKWH();
            }catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    private void setupChart() {
        items = new ArrayList<>();
        loadChartData();
        loadChartData2();
        adapter = new GrafikAdapter(getContext(), items);
        grafik.setAdapter(adapter);
        grafik.setClipToPadding(false);
        grafik.setPadding(10, 0, 20, 0);
        grafik.setPageMargin(10);
        grafik_dots.setViewPager(grafik);
    }

    private void setupViewPagerDetail() {
        detailModels = new ArrayList<>();
        detailModels.add(new DetailModel("Total Biaya", "Rp. 17.000"));
        detailModels.add(new DetailModel("Total Biaya", "Rp. 17.000"));
        detailModels.add(new DetailModel("Total Biaya", "Rp. 17.000"));
        detailAdapter = new DetailAdapter(getContext(), detailModels);
        detail.setAdapter(detailAdapter);
        detail.setClipToPadding(false);
        detail.setPadding(10, 0, 20, 0);
        detail.setPageMargin(10);
        detail_dots.setViewPager(detail);
    }

    private void loadChartData() {
      entries = new ArrayList<>();
      Date c = calendar.getTime();
      calendar.add(Calendar.DATE, -10);
      int date = calendar.get(Calendar.DATE);
      int count = 0;
      for(int i = date; i <= c.getDate(); i++) {
          count += 10;
          entries.add(new BarEntry(i, count));
      }
      items.add(new GrafikItem("KWH Value Usage", entries));
    }

    private void loadChartData2() {
        entries2 = new ArrayList<>();
        Date c = calendar.getTime();
        calendar.add(Calendar.DATE, -10);
        int date = calendar.get(Calendar.DATE);
        int count = 0;
        for(int i = date; i <= c.getDate(); i++) {
            count += 10;
            entries2.add(new BarEntry(i, count));
        }
        items.add(new GrafikItem("KWH Total Usage", entries));
    }
}
