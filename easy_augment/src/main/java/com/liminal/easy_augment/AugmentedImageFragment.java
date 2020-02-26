package com.liminal.easy_augment;

// Import statements
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.ImageInsufficientQualityException;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.ArrayList;


// Extend the ArFragment to customize the ARCore session configuration to include Augmented Images.
public class AugmentedImageFragment extends ArFragment {

    // Tag for creating logs
    private static final String TAG = "AUGMENTED_IMAGE_FRAGMENT";

    // Do a runtime check for the OpenGL level available at runtime to avoid Sceneform crashing the application.
    private static final double MIN_OPENGL_VERSION = 3.0;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Sceneform support check
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
            Log.e(TAG, "Sceneform requires Android N or later");
        // OpenGL version check
        if (Double.parseDouble(((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getDeviceConfigurationInfo().getGlEsVersion()) < MIN_OPENGL_VERSION)
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 or later");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        // Turn off the plane discovery since we're only looking for images
        getPlaneDiscoveryController().hide();
        getPlaneDiscoveryController().setInstructionView(null);
        getArSceneView().getPlaneRenderer().setEnabled(false);
        return view;
    }

    @Override
    protected Config getSessionConfiguration(Session session) {
        Config config = super.getSessionConfiguration(session);
        config.setFocusMode(Config.FocusMode.AUTO);
        if (!setupAugmentedImageDatabase(config, session)) {
            Log.e(TAG, "Could not setup augmented image database");
        }
        return config;
    }

    private boolean setupAugmentedImageDatabase(Config config, Session session) {
        // Create Augmented image database and add the reference image to it
        AugmentedImageDatabase augmentedImageDatabase = new AugmentedImageDatabase(session);

        // Load the reference images into the database
        int imageCount = 0;
        Log.d(TAG, "Setting up Augmented Image Database");

        ArrayList<Bitmap> markerImages = ImageManager.loadMarkerImages();

        if (markerImages != null)
            for (Bitmap augmentedImageBitmap : markerImages) {
                Log.d(TAG, "Image loaded in DB");
                imageCount += 1;
                if (augmentedImageBitmap == null)
                    return false;

                try
                {
                    augmentedImageDatabase.addImage("Marker_Img_" + imageCount + ".jpg", augmentedImageBitmap);

                }
                catch (ImageInsufficientQualityException e)
                {
                    Log.d(TAG,"Image Marker quality poor");
                }
            }
        config.setAugmentedImageDatabase(augmentedImageDatabase);

        Log.d(TAG, "Augmented Image Database has been setup");
        return true;
    }
}