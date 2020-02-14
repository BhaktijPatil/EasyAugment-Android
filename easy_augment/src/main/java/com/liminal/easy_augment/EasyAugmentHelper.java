package com.liminal.easy_augment;

import android.content.Context;
import android.content.Intent;

public class EasyAugmentHelper {

    private static boolean isVerified = false;
    private String devKey;
    private Context appContext;

    // Constructor takes in developer key and application context as necessary arguments
    public EasyAugmentHelper(String devKey, Context appContext) {
        isVerified = verifyDevKey(devKey);
        this.devKey = devKey;
        this.appContext = appContext;
    }

    // Function to validate is developer key is correct
    private boolean verifyDevKey(String devkey) {
        return true;
    }

    // Function to load marker images from Liminal database
    public void loadMarkerImages() {
        Intent serviceIntent = new Intent(appContext, MarkerDownloadService.class);
        serviceIntent.putExtra("DEVELOPER_KEY", devKey);
        MarkerDownloadService.enqueueWork(appContext, serviceIntent);
    }

    // Function to start the Scanner
    public void activateScanner() {
        Intent startScanner = new Intent(appContext, ScanActivity.class);
        appContext.startActivity(startScanner);
    }
}
