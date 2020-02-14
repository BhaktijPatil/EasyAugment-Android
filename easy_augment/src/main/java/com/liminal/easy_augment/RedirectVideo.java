package com.liminal.easy_augment;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import androidx.appcompat.app.AppCompatDelegate;

public class RedirectVideo extends AppCompatActivity implements Player.EventListener{


    private PlayerView playerView;
    private SimpleExoPlayer player;
    String vid_url;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow()
                .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_redirect_video);
        playerView = findViewById(R.id.video_view);
        vid_url = getIntent().getStringExtra("VIDEO_URL");
    }

    private void initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(player);
        Uri uri = Uri.parse(vid_url);
        MediaSource mediaSource = buildMediaSource(uri);
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);
        player.prepare(mediaSource, false, false);
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, "exoplayer-codelab");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }

    @Override
    public void onStart() {
        super.onStart();
            initializePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if (player == null) {
            initializePlayer();
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT < 24) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.release();
            player = null;
        }
    }



//    private void setUp() {
//        initializePlayer();
//        if (videoUri == null) {
//            return;
//        }
//        buildMediaSource(Uri.parse(videoUri));
//    }
//    @OnClick(R.id.imageViewExit)
//    public void onViewClicked() {
//        finish();
//    }
//    private void initializePlayer() {
//        if (player == null) {
//            // 1. Create a default TrackSelector
//            LoadControl loadControl = new DefaultLoadControl(
//                    new DefaultAllocator(true, 16),
//                    VideoPlayerConfig.MIN_BUFFER_DURATION,
//                    VideoPlayerConfig.MAX_BUFFER_DURATION,
//                    VideoPlayerConfig.MIN_PLAYBACK_START_BUFFER,
//                    VideoPlayerConfig.MIN_PLAYBACK_RESUME_BUFFER, -1, true);
//            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
//            TrackSelection.Factory videoTrackSelectionFactory =
//                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
//            TrackSelector trackSelector =
//                    new DefaultTrackSelector(videoTrackSelectionFactory);
//            // 2. Create the player
//            player =
//                    ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this), trackSelector,
//                            loadControl);
//            videoFullScreenPlayer.setPlayer(player);
//        }
//    }
//    private void buildMediaSource(Uri mUri) {
//        // Measures bandwidth during playback. Can be null if not required.
//        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
//        // Produces DataSource instances through which media data is loaded.
//        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
//                Util.getUserAgent(this, getString(R.string.app_name)), bandwidthMeter);
//        // This is the MediaSource representing the media to be played.
//        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
//                .createMediaSource(mUri);
//        // Prepare the player with the source.
//        player.prepare(videoSource);
//        player.setPlayWhenReady(true);
//        player.addListener(this);
//    }
//    private void releasePlayer() {
//        if (player != null) {
//            player.release();
//            player = null;
//        }
//    }
//    private void pausePlayer() {
//        if (player != null) {
//            player.setPlayWhenReady(false);
//            player.getPlaybackState();
//        }
//    }
//    private void resumePlayer() {
//        if (player != null) {
//            player.setPlayWhenReady(true);
//            player.getPlaybackState();
//        }
//    }
//    @Override
//    protected void onPause() {
//        super.onPause();
//        pausePlayer();
//        if (mRunnable != null) {
//            mHandler.removeCallbacks(mRunnable);
//        }
//    }
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        resumePlayer();
//    }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        releasePlayer();
//    }
//    @Override
//    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
//    }
//    @Override
//    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
//    }
//    @Override
//    public void onLoadingChanged(boolean isLoading) {
//    }
//    @Override
//    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//        switch (playbackState) {
//            case Player.STATE_BUFFERING:
//                spinnerVideoDetails.setVisibility(View.VISIBLE);
//                break;
//            case Player.STATE_ENDED:
//                // Activate the force enable
//                break;
//            case Player.STATE_IDLE:
//                break;
//            case Player.STATE_READY:
//                spinnerVideoDetails.setVisibility(View.GONE);
//                break;
//            default:
//                // status = PlaybackStatus.IDLE;
//                break;
//        }
//    }
//    @Override
//    public void onRepeatModeChanged(int repeatMode) {
//    }
//    @Override
//    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
//    }
//    @Override
//    public void onPlayerError(ExoPlaybackException error) {
//    }
//    @Override
//    public void onPositionDiscontinuity(int reason) {
//    }
//    @Override
//    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
//    }
//    @Override
//    public void onSeekProcessed() {
//    }

}