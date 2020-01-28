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

// Scan Activity
public class ScanActivity extends AppCompatActivity {

    private ArFragment arFragment;

    // These variables are obtained from the application
    private String redirect;
    private String packageName;
    private String redirect_to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        redirect_to = getIntent().getStringExtra("REDIRECT_TO");
        redirect = getIntent().getStringExtra("REDIRECT");
        packageName = getIntent().getStringExtra("PACKAGE_NAME");

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);
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
                    if(redirect_to.equals("website"))
                    {
                        // Open website specified by user
                        Intent newWebActivity = new Intent(this, RedirectWeb.class);
                        newWebActivity.putExtra("WEBSITE", redirect);
                        startActivity(newWebActivity);
                        Log.d("REDIRECT_TO","Redirecting to Website : " + redirect);
                    }
                    else if(redirect_to.equals("activity"))
                    {
                        // Open activity specified by user
                        ComponentName cn = new ComponentName(this,packageName + "." + redirect);
                        Intent newActivity = new Intent().setComponent(cn);
                        startActivity(newActivity);
                        Log.d("REDIRECT_TO","Redirecting to Activity : " + redirect);
                    }
                    else
                    {
                        // Open website specified by user
                        Intent newVideoActivity = new Intent(this, RedirectWeb.class);
                        newVideoActivity.putExtra("VIDEO_URL", redirect);
                        startActivity(newVideoActivity);
                        Log.d("REDIRECT_TO","Redirecting to Video : " + redirect);
                    }
                case TRACKING:
                case STOPPED:
                    break;
            }
        }
    }
}
