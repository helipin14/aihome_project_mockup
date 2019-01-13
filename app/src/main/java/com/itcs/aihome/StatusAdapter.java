package com.itcs.aihome;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class StatusAdapter extends BaseAdapter {

    private Context context;
    private List<StatusModel> relay;

    public StatusAdapter(Context context, List<StatusModel> relay) {
        this.context = context;
        this.relay = relay;
    }

    private static class ViewHolder {
        TextView status;
        ImageView imageView;
    }

    @Override
    public int getCount() {
        return relay.size();
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
        ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.status_item, viewGroup, false);
            viewHolder.status = view.findViewById(R.id.relay);
            viewHolder.status.setText(relay.get(i).getRelay());
        }

        return view;
    }
}
