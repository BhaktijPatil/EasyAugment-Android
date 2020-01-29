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
import com.google.ar.sceneform.ux.ArFragment;


// Extend the ArFragment to customize the ARCore session configuration to include Augmented Images.
public class AugmentedImageFragment extends ArFragment {

    // Tag for creating logs
    private static final String TAG = "AugmentedImageFragment";

    // Do a runtime check for the OpenGL level available at runtime to avoid Sceneform crashing the application.
    private static final double MIN_OPENGL_VERSION = 3.0;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Check for Sceneform being supported on this device.  This check will be integrated into Sceneform eventually.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
        }

        // Check for openGL version
        String openGlVersionString = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getDeviceConfigurationInfo().getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 or later");
        }
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
        for(Bitmap augmentedImageBitmap : ImageStore.loadRefImage())
        {
            imageCount += 1;
            if (augmentedImageBitmap == null)
                return false;

            augmentedImageDatabase.addImage("Ref_Img_" + imageCount + ".jpg", augmentedImageBitmap);

        }
        config.setAugmentedImageDatabase(augmentedImageDatabase);
        return true;
    }
}