package com.liminal.easy_augment;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class JSONFromURL extends AsyncTask<String, Void, String> {

    // Post execute returns text obtained from the script
    @Override
    protected void onPostExecute(String text) {
        super.onPostExecute(text);
    }

    //Function to read JSON from PHP link
    @Override
    protected String doInBackground(String... params) {
        try {
            String link = params[0];
            java.net.URL url = new URL(link);

            // Set up connection via GET method
            Log.d("PHP_connect", "Trying to establish PHP connection");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Check if connection is established
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.d("PHP_connect", "Connection established");

                // Read data from the PHP connection
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String str;
                StringBuilder sb = new StringBuilder();

                //reading until we don't find null
                while ((str = bufferedReader.readLine()) != null) {
                    Log.d("PHP_connect", "Read line : " + str);
                    sb.append(str + "\n");
                }
                return sb.toString().trim();
            } else {
                Log.d("PHP_connect", "Failed to establish PHP connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}