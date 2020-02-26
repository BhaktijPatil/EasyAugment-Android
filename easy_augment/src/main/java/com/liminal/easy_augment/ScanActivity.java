package com.liminal.easy_augment;

// Import statements

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ScanActivity extends AppCompatActivity {

    private ArFragment arFragment;
    private ImageView scannerView;
    private final Map<AugmentedImage, AugmentedImageNode> augmentedImageMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        scannerView = findViewById(R.id.image_view_fit_to_scan);
        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);
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
                    }
                    break;

                case TRACKING: // Image is being tracked
                    Log.d("SCAN_ACTIVITY_TRACKING_STATE", "tracking state : TRACKING");
                    scannerView.setVisibility(View.GONE);
                    // Create a new anchor for newly found images.
                    if (!augmentedImageMap.containsKey(augmentedImage))
                    {
                        AugmentedImageNode node = new AugmentedImageNode(this, DBManager.getDownloadedFromImageDetails("redirectURL").get(augmentedImage.getIndex()));
                        node.setImage(augmentedImage);
                        augmentedImageMap.put(augmentedImage, node);
                        arFragment.getArSceneView().getScene().addChild(node);
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
}