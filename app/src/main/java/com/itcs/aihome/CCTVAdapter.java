package com.itcs.aihome;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.List;

public class CCTVAdapter extends RecyclerView.Adapter<CCTVAdapter.ViewHolder> {

    private List<CCTVData> data;
    private Context context;
    private Uri uri;

    public CCTVAdapter(Context context, List<CCTVData> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.cctv_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        CCTVData cctvData = data.get(i);
        viewHolder.progressBar.setVisibility(View.GONE);
        viewHolder.cctv_name.setText(cctvData.getCctvname());
        uri = cctvData.getVideoUri();
        switchVideoStart(viewHolder);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void videoHandler(final ViewHolder viewHolder) {
        viewHolder.videoView.setVideoURI(uri);
        viewHolder.videoView.setVideoURI(uri);
        viewHolder.videoView.start();
        viewHolder.videoView.requestFocus();
        viewHolder.videoView.setKeepScreenOn(true);
        viewHolder.progressBar.setVisibility(View.VISIBLE);

        viewHolder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {
                        viewHolder.progressBar.setVisibility(View.GONE);
                        mediaPlayer.start();
                    }
                });
            }
        });

        viewHolder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                viewHolder.imageView.setVisibility(View.VISIBLE);
            }
        });

        viewHolder.videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Log.d("API123", "What " + i + " extra " + i1);
                Toast.makeText(context, "There was an error while playing video", Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }

    private void switchVideoStart(final ViewHolder viewHolder) {
        viewHolder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    videoHandler(viewHolder);
                    viewHolder.videoView.setVisibility(View.VISIBLE);
                    viewHolder.videoView.setBackgroundResource(0);
                    viewHolder.imageView.setVisibility(View.GONE);
                } else {
                    viewHolder.videoView.stopPlayback();
                    viewHolder.progressBar.setVisibility(View.GONE);
                    viewHolder.videoView.setBackgroundResource(R.color.colorGrey);
                    viewHolder.imageView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public Switch aSwitch;
        public VideoView videoView;
        public ProgressBar progressBar;
        public ImageView imageView;
        public TextView cctv_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            aSwitch = itemView.findViewById(R.id.playcontroller);
            videoView = itemView.findViewById(R.id.videoview);
            progressBar = itemView.findViewById(R.id.progress_video);
            cctv_name = itemView.findViewById(R.id.cctv_name);
            imageView = itemView.findViewById(R.id.image_thumbnail);
        }
    }
}
