package com.liminal.easy_augment;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

class ImageManager {

    private static final String STORAGE_DIRECTORY = "Marker_Img_Dir";
    private static File imageDirectory;

    ImageManager(Context appContext) {
        ContextWrapper cw = new ContextWrapper(appContext.getApplicationContext());
        // path to /data/data/app_name/app_data/imageDir
        imageDirectory = cw.getDir(STORAGE_DIRECTORY, Context.MODE_PRIVATE);
    }

    static ArrayList<Bitmap> loadMarkerImages() {
        // List to store all marker images stored in storage
        ArrayList<Bitmap> markerImages = new ArrayList<>();

        for (String imageName : DBManager.getFromImageDetails("imageHash")) {
            try {
                File file = new File(imageDirectory, imageName);
                Bitmap refBitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                Log.d("IMG_MANAGER_LOAD_IMG", "Reference Image file " + imageName + " found");
                markerImages.add(refBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.d("IMG_MANAGER_LOAD_IMG", "Reference Image file " + imageName + " not found");
                return null;
            }
        }
        return markerImages;
    }

    // Function to load Image from an url and store it in internal storage
    void storeImageFromURL(String imageName, String URL)
    {
        Log.d("IMG_MANAGER_URL_LOAD", "Finding image at URL : " + URL);
        Picasso.get().load(URL).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap image, Picasso.LoadedFrom from) {
                new Thread(() -> {
                    storeImage(image, imageName);
                    Log.d("IMG_MANAGER_URL_LOAD", "Image " + imageName + " loaded from URL");
                }).start();
            }
            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Log.d("IMG_MANAGER_URL_LOAD", "Failed to load image from URL");
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {}
        });
    }

    // Function to store a Bitmap in internal storage
    void storeImage(Bitmap image, String imageName) {
        // Create image directory
        File img_path = new File(imageDirectory, imageName);
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(img_path);
            // Use the compress method on the BitMap object to write image to the OutputStream
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Function to delete an image from internal storage
    void deleteImage(String imageName) {
        File img_path = new File(imageDirectory, imageName);
        boolean isDeleted = img_path.delete();
        if (isDeleted)
            Log.d("IMAGE_MANAGER_DELETED", imageName);
    }

    // Function to get storage location of images
    private String getStorageLoc() {
        Log.d("IMAGE_MANAGER_STORAGE_LOC", imageDirectory.getAbsolutePath());
        return imageDirectory.getAbsolutePath();
    }

}