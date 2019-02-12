package com.itcs.aihome;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CCTVFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<CCTVData> data;
    private CCTVAdapter adapter;
    private Button button;

    public static CCTVFragment newInstance() {
        CCTVFragment cctvFragment = new CCTVFragment();
        return cctvFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cctv, container, false);
        init(view);
        setupCCTV();
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        super.onSaveInstanceState(outState);
    }

    private void init(View view) {
        recyclerView = view.findViewById(R.id.cctv_list);
        button = view.findViewById(R.id.coba);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), VideoPlayerActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupCCTV() {
        loadData();
        adapter = new CCTVAdapter(getContext(), data);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadData() {
        data = new ArrayList<>();
        data.add(new CCTVData("rtsp://184.72.239.149/vod/mp4:BigBuckBunny_115k.mov", "1", "CCTV 1"));
        data.add(new CCTVData("rtsp://184.72.239.149/vod/mp4:BigBuckBunny_115k.mov", "2", "CCTV 2"));
    }

}
