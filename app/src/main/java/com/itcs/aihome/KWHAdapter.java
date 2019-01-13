package com.itcs.aihome;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.List;

import pl.pawelkleczkowski.customgauge.CustomGauge;

public class KWHAdapter extends BaseAdapter {

    private Context context;
    private List<KWHModel> kwhModelList;
    private float kwh_val;
    private Double harga;
    private DecimalFormat format;

    public KWHAdapter(Context context, List<KWHModel> kwhModelList) {
        this.context = context;
        this.kwhModelList = kwhModelList;
    }

    private static class ViewHolder {
        TextView name, value, total_biaya;
        CustomGauge gauge;
    }

    @Override
    public int getCount() {
        return kwhModelList.size();
    }

    @Override
    public Object getItem(int i) {
        return kwhModelList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        ViewHolder viewHolder;
        format = new DecimalFormat("#,###");
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.tampilan_kwh, viewGroup, false);
            viewHolder.name = view.findViewById(R.id.kwh_name);
            viewHolder.value = view.findViewById(R.id.kwh_value);
            viewHolder.total_biaya = view.findViewById(R.id.total_biaya);
            viewHolder.gauge = view.findViewById(R.id.gauge);

            viewHolder.name.setText(kwhModelList.get(position).getName());
            viewHolder.value.setText(kwhModelList.get(position).getValue());
            kwh_val = Float.parseFloat(kwhModelList.get(position).getValue());
            int val = Math.round(kwh_val);
            harga = Double.parseDouble(kwhModelList.get(position).getBiaya());
            viewHolder.total_biaya.setText("Total biaya : Rp. "  + format.format(harga));
            viewHolder.gauge.setValue(val);
        }

        return view;
    }
}



