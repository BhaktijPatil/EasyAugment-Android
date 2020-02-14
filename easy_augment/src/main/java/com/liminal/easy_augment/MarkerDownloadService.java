package com.liminal.easy_augment;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import org.json.JSONException;

import java.util.ArrayList;

public class MarkerDownloadService extends JobIntentService {

    private ImageManager imageManager;
    private DBManager dbManager;
    private String devKey;

    static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, MarkerDownloadService.class, 979, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        Log.d("MARKER_DOWNLOAD_SERVICE", "onHandleWork initiated");
        devKey = intent.getStringExtra("DEVELOPER_KEY");

        Verifier verifier = new Verifier(devKey);
        try {
            if (verifier.execute().get().equals("TRUE")) {
                dbManager = new DBManager(this);
                imageManager = new ImageManager(this);
                try {
                    dbManager.updateImageDetails(devKey);
                    deleteImages(dbManager.getRedundantImageNames());
                    storeImages(dbManager.getNewImageNames());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dbManager.viewImageDetails();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MARKER_DOWNLOAD_SERVICE", "Marker download service destroyed");
    }

    @Override
    public boolean onStopCurrentWork() {
        Log.d("MARKER_DOWNLOAD_SERVICE", "Marker download service paused");
        return super.onStopCurrentWork();
    }

    // Function to store new images in internal storage
    private void storeImages(ArrayList<String> new_img_list) {
        for (String imageName : new_img_list) {
            String URL = getImgURL(imageName);
            imageManager.storeImageFromURL(imageName, URL);
        }
    }

    // Function to delete old images from internal storage
    private void deleteImages(ArrayList<String> old_img_list) {
        for (String imageName : old_img_list) {
            imageManager.deleteImage(imageName);
        }
    }

    // Function to get image URL given the developer key and image name
    private String getImgURL(String imageName) {
        final String IMG_URL_PREFIX = "https://liminal.in/images/";
        return IMG_URL_PREFIX + devKey + "/" + imageName;
    }
}
