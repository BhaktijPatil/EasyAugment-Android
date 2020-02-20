package com.liminal.easy_augment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;


class DBManager {

    private static SQLiteDatabase imgDB;
    private final String DATABASE_NAME = "ImageDetailsDB";
    private Context appContext;
    private ArrayList<String> redundantImageNames = new ArrayList<>();
    private ArrayList<String> newImageNames = new ArrayList<>();

    DBManager(Context appContext) {
        // Get application context
        this.appContext = appContext;
        // Get recommended database path
        File db_path = appContext.getDatabasePath(DATABASE_NAME);
        // Create the database directory if it doesn't exist
        if (!db_path.getParentFile().exists()) db_path.getParentFile().mkdirs();
        // Open or create database at db_path
        imgDB = openOrCreateDatabase(db_path, null);
    }

    // Function to get any column from ImageDetails table
    static ArrayList<String> getDownloadedFromImageDetails(String fieldName) {
        // Cursor that stores image names
        Cursor ptr = imgDB.rawQuery("SELECT " + fieldName + " FROM ImageDetails WHERE isDownloaded = 'TRUE'", null);

        // Arraylist to store field details
        ArrayList<String> list = new ArrayList<>();

        ptr.moveToFirst();
        while (!ptr.isAfterLast()) {
            list.add(ptr.getString(0));
            ptr.moveToNext();
        }

        ptr.close();

        return list;
    }

    // Function to update Download status of an image
    static void setDownloadStatus(String imageHash, String downloadStatus) {
        ContentValues cv = new ContentValues();
        cv.put("isDownloaded", downloadStatus);
        imgDB.update("ImageDetails", cv, "imageHash = '" + imageHash + "'", null);
    }

    // Function to update the image details table from the remote database
    void updateImageDetails(String devKey) throws JSONException {
        // Create table if it doesn't exist
        imgDB.execSQL("CREATE TABLE IF NOT EXISTS ImageDetails(imageID INTEGER PRIMARY KEY, redirectTo INTEGER, redirectURL VARCHAR, imageHash VARCHAR, isDownloaded VARCHAR);");

        ArrayList<Integer> old_img_id_list = getImageIdList();
        ArrayList<Integer> old_img_id_list_copy = getImageIdList();
        ArrayList<Integer> new_img_id_list = new ArrayList<>();

        // Get JSON array from PHP script
        JSONArray arrJSON = getJSONarr(devKey);

        for (int i = 0; i < arrJSON.length(); i++) {
            JSONObject obj = arrJSON.getJSONObject(i);

            // Get fields from JSON object
            int imageID = obj.getInt("ImageID");
            int redirectTo = obj.getInt("RedirectTo");
            String redirectURL = obj.getString("RedirectURL");
            String imageHash = obj.getString("ImageHash");

            // Add new image ids to the list
            new_img_id_list.add(imageID);
            // Insert values into Image details database
            insertImageDetails(imageID, redirectTo, redirectURL, imageHash, "FALSE");
        }

        // Create lists that store image ids that need to be deleted or updated
        old_img_id_list.removeAll(new_img_id_list);
        new_img_id_list.removeAll(old_img_id_list_copy);

        // Store names of images that need to be deleted
        storeRedundantImageNames(old_img_id_list);
        storeUpdatedImageNames(new_img_id_list);

        // Delete old image details from database
        delImageDetails(old_img_id_list);
        for (int img_id : old_img_id_list) {
            Log.d("DB_MANAGER_DEL", "IMG_ID : " + img_id);
        }
    }


    // Function to get imageIDs from ImageDetails table
    private ArrayList<Integer> getImageIdList() {
        // list to Store image IDs
        ArrayList<Integer> img_id_list = new ArrayList<>();
        // Cursor that stores image IDs
        Cursor img_id_list_ptr = imgDB.rawQuery("SELECT imageID FROM ImageDetails", null);

        img_id_list_ptr.moveToFirst();
        while (!img_id_list_ptr.isAfterLast()) {
            img_id_list.add(img_id_list_ptr.getInt(0));
            img_id_list_ptr.moveToNext();
        }

        img_id_list_ptr.close();

        return img_id_list;
    }

    // Function to get redundant image names
    private void storeRedundantImageNames(ArrayList<Integer> old_img_id_list) {
        Cursor ptr = null;
        for (int img_id : old_img_id_list) {
            ptr = imgDB.rawQuery("SELECT imageHash FROM ImageDetails WHERE imageID = " + img_id, null);
            ptr.moveToFirst();
            redundantImageNames.add(ptr.getString(0));
        }
        if (ptr != null)
            ptr.close();
    }

    // Function to get redundant image names
    private void storeUpdatedImageNames(ArrayList<Integer> new_img_id_list) {
        Cursor ptr = null;
        for (int img_id : new_img_id_list) {
            ptr = imgDB.rawQuery("SELECT imageHash FROM ImageDetails WHERE imageID = " + img_id, null);
            ptr.moveToFirst();
            newImageNames.add(ptr.getString(0));
        }
        if (ptr != null)
            ptr.close();
    }

    // Function to retrieve redundant image names
    ArrayList<String> getRedundantImageNames() {
        for (String img_name : redundantImageNames) {
            Log.d("DB_MANAGER_DEL", "IMG_NAME : " + img_name);
        }
        return redundantImageNames;
    }

    // Function to retrieve redundant image names
    ArrayList<String> getNewImageNames() {
        for (String img_name : newImageNames) {
            Log.d("DB_MANAGER_ADD", "IMG_NAME : " + img_name);
        }
        return newImageNames;
    }

    // Function to delete image details of specified ImageIDs from the database
    private void delImageDetails(ArrayList<Integer> image_id_list) {
        // Delete old image details if the image has been removed online
        for (int image_id : image_id_list)
            imgDB.execSQL("DELETE FROM ImageDetails WHERE imageID = " + image_id);
    }

    // Function to update the image details table from the remote database
    void viewImageDetails() {
        // Get the entire imageDetails table
        Cursor ptr = imgDB.rawQuery("SELECT * FROM ImageDetails", null);

        // Go to first row
        ptr.moveToFirst();

        // Print each row in the imageDetails table
        for (int i = 0; i < ptr.getCount(); i++) {
            String imageID = ptr.getString(0);
            String redirectTo = ptr.getString(1);
            String redirectURL = ptr.getString(2);
            String imageHash = ptr.getString(3);
            String isDownloaded = ptr.getString(4);

            Log.d("DB_MANAGER_VIEW", "Row " + i + " : " + imageID + " " + redirectTo + " " + redirectURL + " " + imageHash + " " + isDownloaded);

            // Go to next row
            ptr.moveToNext();
        }

        ptr.close();
    }

    // Function to Insert into ImageDetails
    private void insertImageDetails(int imageID, int redirectTo, String redirectURL, String imageHash, String isDownloaded) {
        ContentValues values = new ContentValues();

        values.put("imageID", imageID);
        values.put("redirectTo", redirectTo);
        values.put("redirectURL", redirectURL);
        values.put("imageHash", imageHash);
        values.put("isDownloaded", isDownloaded);

        imgDB.insert("ImageDetails", null, values);
    }

    // Function to get JSON array from PHP script
    private JSONArray getJSONarr(String devKey) {
        JSONFromURL jsonFromURL = new JSONFromURL();
        String URL = "https://liminal.in/getData.php?uid=" + devKey;

        try {
            return new JSONArray(jsonFromURL.execute(URL).get());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}