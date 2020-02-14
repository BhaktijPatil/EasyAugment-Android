package com.liminal.easy_augment;

// Import statements

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.Collection;

public class ScanActivity extends AppCompatActivity {

    private ArFragment arFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        if (arFragment != null) {
            arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                case PAUSED:
                    // When an image is in PAUSED state it has been detected.
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
                            Intent newVideoActivity = new Intent(this, RedirectWeb.class);
                            newVideoActivity.putExtra("VIDEO_URL", videoURL);
                            startActivity(newVideoActivity);
                            Log.d("SCAN_ACTIVITY_REDIRECT_TO", "Redirecting to Video : " + videoURL);
                            break;

                    }
                case TRACKING:
                case STOPPED:
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