package com.itcs.aihome;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GrafikAdapter extends PagerAdapter {

    private LayoutInflater layoutInflater;
    private Context context;
    private List<GrafikItem> items;

    public GrafikAdapter(Context context, List<GrafikItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view.equals(o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.grafik_item, container, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.barChart = view.findViewById(R.id.bar_chart);
        viewHolder.judul = view.findViewById(R.id.judul_10hari);
        viewHolder.judul.setText(items.get(position).getJudul());
        loadData(position, viewHolder.barChart);
        configBar(viewHolder.barChart);
        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    private void loadData(int position, BarChart chart) {
        BarDataSet dataSet = new BarDataSet(items.get(position).getEntries(), "KWH Usage");
        dataSetConfig(dataSet);
        BarData data = new BarData(dataSet);
        chart.setData(data);
    }

    private void configBar(BarChart barChart) {
        barChart.invalidate();
        barChart.setScaleEnabled(true);
        barChart.setDrawGridBackground(false);
        barChart.setBorderColor(R.color.colorRed);
        barChart.setBorderWidth(2);
        barChart.animateX(2000, Easing.EasingOption.EaseOutBack);
        barChart.animateY(2000, Easing.EasingOption.EaseOutBack);
        barChart.fitScreen();
    }

    private void dataSetConfig(BarDataSet dataSet) {
        dataSet.setColors(new int[] {
                R.color.colorGreen,
                R.color.colorRed,
                R.color.colorBlue,
                R.color.colorPrimary,
                R.color.colorAccent
        }, context);
        dataSet.setValueTextColor(R.color.colorRed);
        dataSet.setHighlightEnabled(true);
        dataSet.setHighLightColor(R.color.colorGreen);
    }

    public static class ViewHolder {
        TextView judul;
        BarChart barChart;
    }
}
