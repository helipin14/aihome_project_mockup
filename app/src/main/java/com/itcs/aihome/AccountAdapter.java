package com.itcs.aihome;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AccountAdapter extends BaseAdapter {

    private Context context;
    private List<String> items;
    private List<Integer> image;

    public AccountAdapter(Context context, List<String> items, List<Integer> image) {
        this.items = items;
        this.context = context;
        this.image = image;
    }

    private static class ViewHolder {
        TextView textView;
        SolidIconTextView imageView;
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
        ViewHolder viewHolder = new ViewHolder();
        if(view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.accountitem, viewGroup, false);
            viewHolder.textView = view.findViewById(R.id.text_account);
            viewHolder.imageView = view.findViewById(R.id.image_account);
            viewHolder.textView.setText(items.get(i));
            viewHolder.imageView.setText(image.get(i));
        }
        return view;
    }
}
