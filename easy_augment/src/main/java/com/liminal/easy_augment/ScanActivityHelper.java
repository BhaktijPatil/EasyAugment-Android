package com.liminal.easy_augment;

import android.content.Context;
import android.content.Intent;

public class ScanActivityHelper {

    // Multiple getIntent functions for creating an intent to open ScanActivity
    static public Intent getIntent(Context context, String redirect)
    {
        Intent scan = new Intent(context, ScanActivity.class);
        scan.putExtra("REDIRECT", redirect);
        scan.putExtra("PACKAGE_NAME", getPackageName(context));
        return scan;
    }

    static public Intent getIntent(Context context)
    {
        Intent scan = new Intent(context, ScanActivity.class);
        scan.putExtra("REDIRECT", getPreviousClassName(context));
        scan.putExtra("PACKAGE_NAME", getPackageName(context));
        return scan;
    }

    // Get the name of the activity calling the Helper
    static  private String getPreviousClassName(Context context)
    {
        return context.getClass().getSimpleName();
    }

    // Get the name of the application package
    static private String getPackageName(Context context)
    {
        return context.getApplicationContext().getPackageName();
    }

    // Check if the given string is a website
    static public boolean isWebsite(String text)
    {
        return text.contains(".");
    }

}
