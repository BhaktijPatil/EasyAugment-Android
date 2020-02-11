package com.liminal.easy_augment;

// Import statements

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
                    switch (DBManager.getFromImageDetails("redirectTo").get(augmentedImage.getIndex())) {

                        case "0": // Open Video
                            String videoURL = DBManager.getFromImageDetails("redirectURL").get(augmentedImage.getIndex());
                            Intent newVideoActivity = new Intent(this, RedirectWeb.class);
                            newVideoActivity.putExtra("VIDEO_URL", videoURL);
                            startActivity(newVideoActivity);
                            Log.d("REDIRECT_TO", "Redirecting to Video : " + videoURL);
                            break;

                        case "1": // Open website
                            String website = DBManager.getFromImageDetails("redirectURL").get(augmentedImage.getIndex());
                            Intent newWebActivity = new Intent(this, RedirectWeb.class);
                            newWebActivity.putExtra("WEBSITE", website);
                            startActivity(newWebActivity);
                            Log.d("SCAN_ACTIVITY_REDIRECT_TO", "Redirecting to Website : " + website);
                            break;

                        case "2": // Open activity
//                            ComponentName cn = new ComponentName(this,packageName + "." + redirect);
//                            Intent newActivity = new Intent().setComponent(cn);
//                            startActivity(newActivity);
//                            Log.d("REDIRECT_TO","Redirecting to Activity : " + redirect);
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