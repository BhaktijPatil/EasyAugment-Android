package com.liminal.easy_augment;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageStore {

    static private String storage_loc;

    static public void storeRefImage(Context context, Bitmap RefImage){
        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());

        // path to /data/data/app_name/app_data/imageDir
        File directory = cw.getDir("refImgDir", Context.MODE_PRIVATE);

        // Create image directory
        File img_path = new File(directory,"Ref_Img.jpg");

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(img_path);
            // Use the compress method on the BitMap object to write image to the OutputStream
            RefImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
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

        storage_loc = directory.getAbsolutePath();
        Log.d("IMG_LOC", storage_loc);
    }

    static public Bitmap loadRefImage() {
        try {
            File file = new File(storage_loc, "Ref_Img.jpg");
            Bitmap refBitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            Log.d("LOAD_IMG", "Reference Image file found");
            return refBitmap;
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("LOAD_IMG", "Reference Image file not found");
            return null;
        }
    }
}
