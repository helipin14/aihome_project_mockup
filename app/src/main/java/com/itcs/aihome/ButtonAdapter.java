package com.itcs.aihome;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ButtonAdapter extends BaseAdapter {

    private ArrayList<String> items;
    private static int flag = 0;
    public String active = "yes";
    private Context context;

    public ButtonAdapter(Context context, ArrayList<String> items) {
        this.items = items;
        this.context = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        final ViewHolder viewHolder = new ViewHolder();
        if(view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.button, viewGroup, false);
            viewHolder.button = view.findViewById(R.id.btndays);
            viewHolder.button.setText(items.get(i));
            viewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(flag == 0) {
                        flag = 1;
                        viewHolder.button.setBackgroundResource(R.drawable.circle_active);
                    } else {
                        flag = 0;
                        viewHolder.button.setBackgroundResource(R.drawable.circle);
                    }
                }
            });
        }
        return view;
    }

    public static class ViewHolder {
        Button button;
    }

    public void setActive(String active) {
        this.active = active;
    }
}
