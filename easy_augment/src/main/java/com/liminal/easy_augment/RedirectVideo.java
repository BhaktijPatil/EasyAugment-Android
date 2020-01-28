package com.liminal.easy_augment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class RedirectVideo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redirect_video);

        // Get video URL from ScanActivity
        String video_link = getIntent().getStringExtra("VIDEO_URL");


        VideoView videoView=(VideoView)findViewById(R.id.videoView);
        videoView.setVideoPath(video_link);
        videoView.start();
    }
}
