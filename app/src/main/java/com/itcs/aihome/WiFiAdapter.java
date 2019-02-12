package com.itcs.aihome;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class WiFiAdapter extends RecyclerView.Adapter<WiFiAdapter.ViewHolder> {

    private Context context;
    private List<String> wifiList;

    public WiFiAdapter(Context context, List<String> wifiList) {
        this.context = context;
        this.wifiList = wifiList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.wifi_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String ssid = wifiList.get(i);
        viewHolder.wifi_ssid.setText(ssid);
    }

    @Override
    public int getItemCount() {
        return wifiList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView wifi_ssid;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            wifi_ssid = itemView.findViewById(R.id.wifi_name);
        }
    }
}
