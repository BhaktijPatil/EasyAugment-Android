package com.liminal.easy_augment;

import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class RedirectVideo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redirect_video);

        // Get video URL from ScanActivity
        String video_link = getIntent().getStringExtra("VIDEO_URL");

        Uri videoURL = Uri.parse(video_link);
        VideoView videoView = findViewById(R.id.videoView);
        videoView.setVideoURI(videoURL);
        videoView.start();
    }
}
