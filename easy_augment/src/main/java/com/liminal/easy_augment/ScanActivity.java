package com.liminal.easy_augment;

// Import statements
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.bumptech.glide.Glide;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ScanActivity extends AppCompatActivity {

    private ArFragment arFragment;
    private ImageView scannerView;

    private Scene scene;

    // Required for video augmentation
    private boolean isTracking = false;
    private boolean augmentVideoFlag = false;
    private AugmentVideo augmentVideo;

    // Variable that stores Marker detected in previous frame
    private AugmentedImage currentMarker = null;

    // Arraylist to store image details
    private ArrayList<ImageDetails> imageDetailsArrayList;

    // Layout for loading bar
    private RelativeLayout loadingBar;
    private TextView loadingText;

    private final Map<AugmentedImage, AugmentedImageNode> augmentedImageMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        // Initializes the loading bar
        setupLoadingBar();

        // Load the imageDetails table
        imageDetailsArrayList = DBManager.getDownloadedFromImageDetails();

        // Load Augmented Image Fragment
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        scannerView = findViewById(R.id.scanner_view);

        //Initialize Augment Video player
        augmentVideo = new AugmentVideo();

        if (arFragment != null) {
            scene = arFragment.getArSceneView().getScene();
            scene.addOnUpdateListener(this::onUpdateFrame);
        }
    }

    //This method is called at the start of each frame. @param frameTime = time since last frame.
    private void onUpdateFrame(FrameTime frameTime) {
        Frame frame = arFragment.getArSceneView().getArFrame();
        if (frame == null) return;

        // Detect marker present in the frame
        AugmentedImage newMarker = detectMarker(frame);

        // If a New marker is detected update the current marker
        if(newMarker !=  null && newMarker != currentMarker)
        {
            currentMarker = newMarker;

            //prepare for change in video playing on marker
            augmentVideo.setChangeIndexTrue();
            isTracking = false;
        }
        if(currentMarker == null) return;

        // Work with the marker that is currently in frame
        switch (currentMarker.getTrackingState()) {
            case PAUSED: //image has been detected.
                Log.d("SCAN_ACTIVITY_TRACKING_STATE", "tracking state : PAUSED");

                if(loadingBar.getVisibility() == View.GONE)
                    setLoadingBarVisible("Image Found. Processing Environment");
                switch (imageDetailsArrayList.get(currentMarker.getIndex()).redirectTo)
                {
                    case "0": // Open Activity
                        Intent intent = getIntent();
                        String redirectActivityName = intent.getStringExtra("REDIRECT_ACTIVITY_NAME");
                        if (redirectActivityName != null) {
                            ComponentName cn = new ComponentName(this, redirectActivityName);
                            Intent newActivity = new Intent().setComponent(cn);
                            newActivity.putExtra("IMAGE_NAME", imageDetailsArrayList.get(currentMarker.getIndex()).imageName);
                            startActivity(newActivity);
                            Log.d("SCAN_ACTIVITY_REDIRECT_TO", "Redirecting to Activity : " + redirectActivityName);
                        } else
                            Log.d("SCAN_ACTIVITY_REDIRECT_TO", "Redirect Activity name is not specified");
                        break;

                    case "1": // Open website
                        String website = imageDetailsArrayList.get(currentMarker.getIndex()).redirectURL;
                        Intent newWebActivity = new Intent(this, RedirectWeb.class);
                        newWebActivity.putExtra("WEBSITE", website);
                        startActivity(newWebActivity);
                        Log.d("SCAN_ACTIVITY_REDIRECT_TO", "Redirecting to Website : " + website);
                        break;

                    case "2": // Open Video
                        String videoURL = imageDetailsArrayList.get(currentMarker.getIndex()).redirectURL;
                        Intent newVideoActivity = new Intent(this, RedirectVideo.class);
                        newVideoActivity.putExtra("VIDEO_URL", videoURL);
                        startActivity(newVideoActivity);
                        Log.d("SCAN_ACTIVITY_REDIRECT_TO", "Redirecting to Video : " + videoURL);
                        break;

                    case "3": // Augment Model
                        break;

                    case "4": // Augment Video
                        augmentVideo.videoAugment(imageDetailsArrayList.get(currentMarker.getIndex()).redirectURL,this);
                        Log.d("SCAN_ACTIVITY_VIDEO","Video Player Initialized");
                        augmentVideoFlag = true;
                        break;
                }
                break;

            case TRACKING: // Image is being tracked
                Log.d("SCAN_ACTIVITY_TRACKING_STATE", "tracking state : " + currentMarker.getTrackingMethod());

                // Set visibility of scanner depending on tracking status
                if(currentMarker.getTrackingMethod() == AugmentedImage.TrackingMethod.FULL_TRACKING)
                {
                    if(loadingBar.getVisibility() == View.VISIBLE)
                    {
                        loadingBar.setVisibility(View.GONE);
                        scannerView.setVisibility(View.GONE);
                    }
                }
                else if(currentMarker.getTrackingMethod() == AugmentedImage.TrackingMethod.LAST_KNOWN_POSE)
                {
                    if(loadingBar.getVisibility() == View.GONE)
                    {
                        scannerView.setVisibility(View.VISIBLE);
                        setLoadingBarVisible("Lost Marker. Searching...");
                    }
                }

                // Augment 3D model
                if (!augmentedImageMap.containsKey(currentMarker) && !augmentVideoFlag)
                {
                    Log.d("SCAN_ACTIVITY_REDIRECT_TO", "Augmenting model : " + imageDetailsArrayList.get(currentMarker.getIndex()).redirectURL);
                    AugmentedImageNode node = new AugmentedImageNode(this, imageDetailsArrayList.get(currentMarker.getIndex()).redirectURL);
                    node.setImage(currentMarker);
                    augmentedImageMap.put(currentMarker, node);
                    scene.addChild(node);
                }

                // Augment video over 2D plane
                else if(!isTracking && augmentVideoFlag)
                {
                    Log.d("SCAN_ACTIVITY_REDIRECT_TO", "Augmenting video : " + imageDetailsArrayList.get(currentMarker.getIndex()).redirectURL);
                    augmentVideo.playVideo(currentMarker.createAnchor(currentMarker.getCenterPose()), currentMarker.getExtentX(), currentMarker.getExtentZ(), scene);
                    Log.d("SCAN_ACTIVITY_VIDEO","Video is playing");
                    isTracking = true;
                }
                break;

            case STOPPED: // Image Marker is not present in camera frame
                Log.d("SCAN_ACTIVITY_TRACKING_STATE", "tracking state : STOPPED");
                augmentedImageMap.remove(currentMarker);
                break;
        }
    }

    // Function to detect markers
    private AugmentedImage detectMarker(Frame frame) {
        for (AugmentedImage augmentedImage : frame.getUpdatedTrackables(AugmentedImage.class)) {
            if (augmentedImage.getTrackingState() == TrackingState.PAUSED)
            {
                Log.d("SCAN_ACTIVITY_DETECT", String.valueOf(augmentedImage.getIndex()));
                return augmentedImage;
            }
        }
        return null;
    }

    // Function to setup loading bar
    private void setupLoadingBar()
    {
        ImageView loadingImgView = findViewById(R.id.loadingImgView);
        loadingText = findViewById(R.id.loadingTextView);
        loadingBar = findViewById(R.id.loadingBar);

        Glide.with(this)
                .load(R.drawable.loading_spinner)
                .centerCrop()
                .transition(withCrossFade())
                .into(loadingImgView);
    }

    // Function to set Text in the loading bar and make it visible
    private void setLoadingBarVisible(String text)
    {
        loadingBar.setVisibility(View.VISIBLE);
        loadingText.setText(text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (augmentedImageMap.isEmpty())
            scannerView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Destroy the scanner activity once an image is found
        finish();
    }
}