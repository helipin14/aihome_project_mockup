package com.itcs.aihome;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class HomeRoomAdapter extends PagerAdapter {

    private ArrayList<String> namaruangan;
    private ArrayList<Integer> gambar;
    private Context context;
    private TextView ruanngan;
    private ImageView gambar_ruangan;

    public HomeRoomAdapter(Context context, ArrayList<String> namaruangan, ArrayList<Integer> gambar) {
        this.namaruangan = namaruangan;
        this.gambar = gambar;
        this.context =context;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.room_home, container, false);

        ruanngan = view.findViewById(R.id.room_textview);
        gambar_ruangan = view.findViewById(R.id.room_image);

        ruanngan.setText(namaruangan.get(position));
        gambar_ruangan.setImageResource(gambar.get(position));

        container.addView(view, 0);
        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view.equals(o);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
