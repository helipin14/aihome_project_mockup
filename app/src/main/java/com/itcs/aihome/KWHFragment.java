package com.itcs.aihome;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class KWHFragment extends Fragment {

    private ListView listView;
    private List<KWHModel> kwhModelList;
    private KWHAdapter adapter;

    public static KWHFragment newInstance() {
        KWHFragment kwhFragment = new KWHFragment();
        return kwhFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.kwh, container, false);
        listView = view.findViewById(R.id.kwh_listview);
        setKWH();
        return view;
    }

    private void setKWH() {
        kwhModelList = new ArrayList<>();
        String data = getDefaults("data", getContext());
        try {
            JSONObject jsonObject = new JSONObject(data);
            String harga = jsonObject.getString("harga_kwh");
            JSONArray jsonArray = jsonObject.getJSONArray("kwh");
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String name = jsonObject1.getString("name");
                String value = jsonObject1.getString("value");
                float final_value = Float.parseFloat(value) / 1000;
                float final_total = final_value * Integer.parseInt(harga);
                kwhModelList.add(new KWHModel(name, String.valueOf(final_value), String.valueOf(final_total)));
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }

        adapter = new KWHAdapter(getContext(), kwhModelList);
        listView.setAdapter(adapter);
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
}
