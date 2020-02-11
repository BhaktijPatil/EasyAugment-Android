package com.liminal.easy_augment;

import android.content.Context;
import android.content.Intent;

import org.json.JSONException;

import java.util.ArrayList;


public class EasyAugmentHelper {

    private static boolean isVerified = false;
    private String devKey;
    private Context appContext;
    private ImageManager imageManager;

    // Constructor takes in developer key and application context as necessary arguments
    public EasyAugmentHelper(String devKey, Context appContext) {
        isVerified = verifyDevKey(devKey);

        this.devKey = devKey;
        this.appContext = appContext;

        imageManager = new ImageManager(appContext);
    }

    // Function to validate is developer key is correct
    private boolean verifyDevKey(String devkey) {
        return true;
    }

    // Function to load marker images from Liminal database
    public void loadMarkerImages() {
        DBManager dbManager = new DBManager(appContext);
        try {
            dbManager.updateImageDetails(devKey);
            deleteImages(dbManager.getRedundantImageNames());
            storeImages(dbManager.getNewImageNames());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dbManager.viewImageDetails();
    }

    // Function to start the Scanner
    public void activateScanner() {
        Intent startScanner = new Intent(appContext, ScanActivity.class);
        appContext.startActivity(startScanner);
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
