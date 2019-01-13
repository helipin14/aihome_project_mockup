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

public class CCTVFragment extends Fragment {

    private Button button;
    private VideoView videoView;
    private Switch aSwitch;
    private ProgressBar progressBar;
    private Uri videoUri;
    private Snackbar snackbar;
    private ImageView imageView;

    public static CCTVFragment newInstance() {
        CCTVFragment cctvFragment = new CCTVFragment();
        return cctvFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cctv, container, false);
        init(view);
        switchVideoStart(videoView, view);
        return view;
    }

    private void switchVideoStart(final VideoView videoView, final View view) {
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    videoHandler(videoView, view);
                    videoView.setVisibility(View.VISIBLE);
                    videoView.setBackgroundResource(0);
                    imageView.setVisibility(View.GONE);
                } else {
                    videoView.stopPlayback();
                    progressBar.setVisibility(View.GONE);
                    videoView.setBackgroundResource(R.color.colorGrey);
                    imageView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void videoHandler(final VideoView videoView, final View view) {
        videoUri = Uri.parse("rtsp://184.72.239.149/vod/mp4:BigBuckBunny_175k.mov");
        videoView.setVideoURI(videoUri);
        videoView.start();
        videoView.requestFocus();
        videoView.setKeepScreenOn(true);
        progressBar.setVisibility(View.VISIBLE);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
              mediaPlayer.start();
              mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                  @Override
                  public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {
                      progressBar.setVisibility(View.GONE);
                      mediaPlayer.start();
                  }
              });
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                imageView.setVisibility(View.VISIBLE);
            }
        });

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Log.d("API123", "What " + i + " extra " + i1);
                snackbar = Snackbar.make(view.findViewById(R.id.container_cctv), "There was an error while playing video", Snackbar.LENGTH_LONG);
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundResource(R.color.colorRed);
                snackbar.show();
                return false;
            }
        });
    }

    private void init(View view) {
        imageView = view.findViewById(R.id.image_thumbnail);
        progressBar = view.findViewById(R.id.progress_video);
        videoView = view.findViewById(R.id.videoview);
        aSwitch = view.findViewById(R.id.playcontroller);
    }
}
