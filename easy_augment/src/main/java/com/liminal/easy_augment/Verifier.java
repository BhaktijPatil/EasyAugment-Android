package com.liminal.easy_augment;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class Verifier extends AsyncTask<String, Void, String> {

    private String link;

    Verifier(String devKey) {
        link = "https://liminal.in/verifyUser.php?uid=" + devKey;
    }

    // Post execute returns if the user is verified
    @Override
    protected void onPostExecute(String text) {
        super.onPostExecute(text);
    }

    //Function to verify developer
    @Override
    protected String doInBackground(String... params) {
        try {
            java.net.URL url = new URL(link);

            // Set up connection via GET method
            Log.d("PHP_connect", "Trying to establish PHP connection for Developer verification");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Check if connection is established
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.d("PHP_connect", "Connection established for Developer verification");

                // Read data from the PHP connection
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String str = bufferedReader.readLine();

                // Log to let the developer know if incorrect key is entered
                if (str.equals("TRUE"))
                    Log.d("DEV_VERIFY", "Developer is verified");
                else
                    Log.d("DEV_VERIFY", "Illegal Developer key");

                return str;
            } else {
                Log.d("PHP_connect", "Failed to establish PHP connection for Developer verification");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
