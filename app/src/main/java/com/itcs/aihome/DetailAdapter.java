package com.itcs.aihome;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class DetailAdapter extends PagerAdapter {

    private List<DetailModel> detailModelList;
    private Context context;
    private LayoutInflater layoutInflater;

    public DetailAdapter(Context context, List<DetailModel> detailModels) {
        this.context = context;
        this.detailModelList = detailModels;
    }

    @Override
    public int getCount() {
        return detailModelList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view.equals(o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.detail_kwh_item, container, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.judul = view.findViewById(R.id.judul_detail_kwh);
        viewHolder.detail = view.findViewById(R.id.detail_kwh);
        viewHolder.judul.setText(detailModelList.get(position).getJudul());
        viewHolder.detail.setText(detailModelList.get(position).getDetail());
        container.addView(view, 0);
        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    public static class ViewHolder {
        TextView judul, detail;
    }
}
