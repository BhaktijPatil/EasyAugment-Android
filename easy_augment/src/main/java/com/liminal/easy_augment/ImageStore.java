package com.liminal.easy_augment;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageStore {

    static private List<String> storage_loc_list = new ArrayList<>();

    // load Image from an url into a Bitmap
    static public void storeImageFromURL(String URL)
    {
        Log.d("IMG_URL_LOAD","Finding image at URL");
        Picasso.get().load(URL).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                // bitmap stores the image obtained from the mentioned URL
                storeRefImage(bitmap);
                Log.d("IMG_URL_LOAD", "Image loaded from URL");

            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Log.d("IMG_URL_LOAD","Failed to load image from URL");
                Toast.makeText(ScanActivityHelper.context, "Error 404: Bad Connection", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {}
        });
    }

    // storeRefImage function for storing a single image
    public static void storeRefImage(Bitmap refImage){
        ArrayList<Bitmap> refImageList = new ArrayList<>();
        refImageList.add(refImage);
        storeRefImage(refImageList);
    }

    // storeRefImage function for storing multiple images
    public static void storeRefImage(ArrayList<Bitmap> refImageList){
        int imageCount = 0;
        for(Bitmap refImage : refImageList)
        {
            if(refImage == null)
                continue;

            // increase the number of images everytime the function is called
            imageCount += 1;

            ContextWrapper cw = new ContextWrapper(ScanActivityHelper.context.getApplicationContext());

            // path to /data/data/app_name/app_data/imageDir
            File directory = cw.getDir("refImgDir", Context.MODE_PRIVATE);

            // Create image directory
            File img_path = new File(directory,"Ref_Img_" + imageCount + ".jpg");

            FileOutputStream fos = null;

            try {
                fos = new FileOutputStream(img_path);
                // Use the compress method on the BitMap object to write image to the OutputStream
                refImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                try {
                    fos.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

            storage_loc_list.add(directory.getAbsolutePath());
            Log.d("IMG_LOC", storage_loc_list.get(imageCount-1));
        }

    }

//    // storeRefImage function for storing a single image
//    static public void storeRefImage(Context context, Bitmap refImage){
//        ArrayList<Bitmap> refImageList = new ArrayList<>();
//        refImageList.add(refImage);
//        storeRefImage(context, refImageList);
//    }

//    // storeRefImage function for storing multiple images
//    static public void storeRefImage(Context context, ArrayList<Bitmap> refImageList){
//        int imageCount = 0;
//        for(Bitmap refImage : refImageList)
//        {
//            if(refImage == null)
//                continue;
//
//            // increase the number of images everytime the function is called
//            imageCount += 1;
//
//            ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
//
//            // path to /data/data/app_name/app_data/imageDir
//            File directory = cw.getDir("refImgDir", Context.MODE_PRIVATE);
//
//            // Create image directory
//            File img_path = new File(directory,"Ref_Img_" + imageCount + ".jpg");
//
//            FileOutputStream fos = null;
//
//            try {
//                fos = new FileOutputStream(img_path);
//                // Use the compress method on the BitMap object to write image to the OutputStream
//                refImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
//            }
//            catch (Exception e) {
//                e.printStackTrace();
//            }
//            finally {
//                try {
//                    fos.close();
//                }
//                catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            storage_loc_list.add(directory.getAbsolutePath());
//            Log.d("IMG_LOC", Integer.toString(storage_loc_list.indexOf(imageCount-1)));
//        }
//
//    }


    static ArrayList<Bitmap> loadRefImage() {

        // List to store all reference images stored in storage
        ArrayList<Bitmap> refImages = new ArrayList<>();
        int count = 0;
        for(String storage_loc : storage_loc_list)
        {
            count += 1;
            try {
                File file = new File(storage_loc, "Ref_Img_" + count + ".jpg");
                Bitmap refBitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                Log.d("LOAD_IMG", "Reference Image file found");
                refImages.add(refBitmap);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.d("LOAD_IMG", "Reference Image file not found");
                return null;
            }
        }
        return refImages;
    }
}
