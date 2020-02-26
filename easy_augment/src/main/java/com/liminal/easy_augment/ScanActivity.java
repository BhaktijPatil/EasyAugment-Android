package com.liminal.easy_augment;

// Import statements

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ScanActivity extends AppCompatActivity {

    private ArFragment arFragment;
    private ImageView scannerView;

    private ExternalTexture texture;
    private ModelRenderable renderable;
    private SimpleExoPlayer player;
    private Scene scene;

    private boolean isTracking = false;
    private boolean augmentVideo = false;

    private final Map<AugmentedImage, AugmentedImageNode> augmentedImageMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        scannerView = findViewById(R.id.image_view_fit_to_scan);

        if (arFragment != null) {
            scene = arFragment.getArSceneView().getScene();
            scene.addOnUpdateListener(this::onUpdateFrame);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (augmentedImageMap.isEmpty())
        {
            scannerView.setVisibility(View.VISIBLE);
        }
    }

    //This method is called at the start of each frame. @param frameTime = time since last frame.
    private void onUpdateFrame(FrameTime frameTime) {
        Frame frame = arFragment.getArSceneView().getArFrame();
        // If there is no frame, just return.
        if (frame == null) {
            return;
        }

        Collection<AugmentedImage> updatedAugmentedImages = frame.getUpdatedTrackables(AugmentedImage.class);

        for (AugmentedImage augmentedImage : updatedAugmentedImages) {
            switch (augmentedImage.getTrackingState()) {

                case PAUSED: //image has been detected.
                    Log.d("SCAN_ACTIVITY_TRACKING_STATE", "tracking state : PAUSED");
                    switch (DBManager.getDownloadedFromImageDetails("redirectTo").get(augmentedImage.getIndex())) {
                        case "0": // Open Activity
                            Intent intent = getIntent();
                            String redirectActivityName = intent.getStringExtra("REDIRECT_ACTIVITY_NAME");
                            if (redirectActivityName != null) {
                                ComponentName cn = new ComponentName(this, redirectActivityName);
                                Intent newActivity = new Intent().setComponent(cn);
                                startActivity(newActivity);
                                Log.d("SCAN_ACTIVITY_REDIRECT_TO", "Redirecting to Activity : " + redirectActivityName);
                            } else
                                Log.d("SCAN_ACTIVITY_REDIRECT_TO", "Redirect Activity name is not specified");
                            break;

                        case "1": // Open website
                            String website = DBManager.getDownloadedFromImageDetails("redirectURL").get(augmentedImage.getIndex());
                            Intent newWebActivity = new Intent(this, RedirectWeb.class);
                            newWebActivity.putExtra("WEBSITE", website);
                            startActivity(newWebActivity);
                            Log.d("SCAN_ACTIVITY_REDIRECT_TO", "Redirecting to Website : " + website);
                            break;

                        case "2": // Open Video
                            String videoURL = DBManager.getDownloadedFromImageDetails("redirectURL").get(augmentedImage.getIndex());
                            Intent newVideoActivity = new Intent(this, RedirectVideo.class);
                            newVideoActivity.putExtra("VIDEO_URL", videoURL);
                            startActivity(newVideoActivity);
                            Log.d("SCAN_ACTIVITY_REDIRECT_TO", "Redirecting to Video : " + videoURL);
                            break;

                        case "3": // Augment Model
                            Log.d("SCAN_ACTIVITY_REDIRECT_TO", "Augmenting model : " + DBManager.getDownloadedFromImageDetails("redirectURL").get(augmentedImage.getIndex()));
                            break;

                        case "4": // Augment Video
                            Log.d("SCAN_ACTIVITY_REDIRECT_TO", "Augmenting video : " + DBManager.getDownloadedFromImageDetails("redirectURL").get(augmentedImage.getIndex()));
                            videoAugment(DBManager.getDownloadedFromImageDetails("redirectURL").get(augmentedImage.getIndex()));
                            break;
                    }
                    break;

                case TRACKING: // Image is being tracked
                    Log.d("SCAN_ACTIVITY_TRACKING_STATE", "tracking state : TRACKING");
                    scannerView.setVisibility(View.GONE);
                    // Create a new anchor for newly found images.
                    if (!augmentedImageMap.containsKey(augmentedImage))
                    {
                        if(!augmentVideo)
                        {
                            AugmentedImageNode node = new AugmentedImageNode(this, DBManager.getDownloadedFromImageDetails("redirectURL").get(augmentedImage.getIndex()));
                            node.setImage(augmentedImage);
                            augmentedImageMap.put(augmentedImage, node);
                            arFragment.getArSceneView().getScene().addChild(node);
                        }
                        else
                            if(!isTracking)
                            {
                                playVideo(augmentedImage.createAnchor(augmentedImage.getCenterPose()), augmentedImage.getExtentX(),
                                        augmentedImage.getExtentZ());
                                isTracking = true;
                            }
                    }
                    break;

                case STOPPED: // Image Marker is not present in camera frame
                    Log.d("SCAN_ACTIVITY_TRACKING_STATE", "tracking state : STOPPED");
                    augmentedImageMap.remove(augmentedImage);
                    break;
            }
        }
    }

    // On stop method is called when the activity is paused (Image is detected)
    @Override
    protected void onStop() {
        super.onStop();
        // Destroy the scanner activity once an image is found
        finish();
    }

    protected void videoAugment(String url){
        if(player == null)
        {
            texture = new ExternalTexture();
            Uri uri = Uri.parse(url);
            MediaSource mediaSource = buildMediaSource(uri);
            player = ExoPlayerFactory.newSimpleInstance(this);
            player.setVideoSurface(texture.getSurface());
            player.prepare(mediaSource, false, false);
            player.setRepeatMode(Player.REPEAT_MODE_ALL);
            player.setPlayWhenReady(false);
            augmentVideo = true;

            ModelRenderable
                    .builder()
                    .setSource(this, R.raw.augmented_video_model)
                    .build()
                    .thenAccept(modelRenderable -> {
                        modelRenderable.getMaterial().setExternalTexture("videoTexture",
                                texture);

                        renderable = modelRenderable;
                    });
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, "exoplayer-codelab");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }

    private void playVideo(Anchor anchor, float extentX, float extentZ) {

        player.setPlayWhenReady(true);

        AnchorNode anchorNode = new AnchorNode(anchor);

        texture.getSurfaceTexture().setOnFrameAvailableListener(surfaceTexture -> {
            anchorNode.setRenderable(renderable);
            texture.getSurfaceTexture().setOnFrameAvailableListener(null);
        });

        anchorNode.setWorldScale(new Vector3(extentX, 1f, extentZ));

        scene.addChild(anchorNode);

    }
}