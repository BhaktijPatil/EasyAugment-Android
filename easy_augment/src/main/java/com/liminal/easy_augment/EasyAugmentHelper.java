package com.liminal.easy_augment;

import android.content.Context;
import android.content.Intent;

public class EasyAugmentHelper {

    private String devKey;
    private Context appContext;
    private String redirectActivityName = null;

    // Constructor takes in developer key and application context as necessary arguments
    public EasyAugmentHelper(String devKey, Context appContext) {
        this.devKey = devKey;
        this.appContext = appContext;
    }

    // Constructor that takes extra RedirectActivityName parameter if needed
    public EasyAugmentHelper(String devKey, Context appContext, String redirectActivityName) {
        this.devKey = devKey;
        this.appContext = appContext;
        this.redirectActivityName = redirectActivityName;
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
        startScanner.putExtra("REDIRECT_ACTIVITY_NAME", redirectActivityName);
        appContext.startActivity(startScanner);
    }
}
