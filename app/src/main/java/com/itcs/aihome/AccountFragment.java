package com.itcs.aihome;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AccountFragment extends Fragment {

    private ListView listview;
    private AccountAdapter adapter;
    private List<String> items;
    private List<Integer> images;
    private String iduser, username;

    public static AccountFragment newInstance() {
        AccountFragment accountFragment = new AccountFragment();
        return accountFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account, container, false);
        listview =  view.findViewById(R.id.list_account);
        items = new ArrayList<>();
        images = new ArrayList<>();
        items.add("Account");
        items.add("Host settings");
        images.add(R.string.user);
        images.add(R.string.tools);
        adapter = new AccountAdapter(getContext(), items, images);
        listview.setAdapter(adapter);

        getDataUser();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(), AccountResponse.class);
                switch(i) {
                    case 0:
                        intent.putExtra("tipe", "account");
                        break;
                    case 1:
                        intent.putExtra("tipe", "host");
                        break;
                }
                startActivity(intent);
            }
        });

        return view;
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    public void getDataUser() {
        String data = getDefaults("data", getContext());
        String data_user = getDefaults("data_user", getContext());
        if(!TextUtils.isEmpty(data_user) && !TextUtils.isEmpty(data)) {
            try {
                // iduser
                JSONObject jsonObject = new JSONObject(data_user);
                iduser = jsonObject.getString("iduser");
                // username
                JSONObject jsonObject1 = new JSONObject(data);
                JSONObject user = jsonObject1.getJSONObject("user");
                username = user.getString("name");
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
