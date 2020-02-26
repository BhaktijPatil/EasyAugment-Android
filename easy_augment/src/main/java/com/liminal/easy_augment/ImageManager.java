package com.liminal.easy_augment;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


class ImageManager {

    private static final String STORAGE_DIRECTORY = "Marker_Img_Dir";
    private static File imageDirectory;
    private Context appContext;

    ImageManager(Context appContext) {
        this.appContext = appContext;
        ContextWrapper cw = new ContextWrapper(appContext.getApplicationContext());
        // path to /data/data/app_name/app_data/imageDir
        imageDirectory = cw.getDir(STORAGE_DIRECTORY, Context.MODE_PRIVATE);
    }

    static ArrayList<Bitmap> loadMarkerImages() {
        // List to store all marker images stored in storage
        ArrayList<Bitmap> markerImages = new ArrayList<>();

        Log.d("IMG_MANAGER","shafihsaijf");
        for (String imageName : DBManager.getDownloadedFromImageDetails("imageHash")) {
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

    // (Glide) Function to load Image from an url and store it in internal storage
    void storeImageFromURL(String imageName, String URL)
    {
        Log.d("IMG_MANAGER_URL_LOAD", "Finding image at URL : " + URL);

        Glide.with(appContext)
                .asBitmap()
                .load(URL)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        storeImage(resource, imageName);
                        DBManager.setDownloadStatus(imageName, "TRUE");
                        Log.d("IMG_MANAGER_URL_LOAD", "Image " + imageName + " loaded from URL");
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    // Function to store a Bitmap in internal storage
    private void storeImage(Bitmap image, String imageName) {
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