package com.liminal.easy_augment;

import android.content.Context;
import android.content.Intent;

public class ScanActivityHelper {

    // Multiple getIntent functions for creating an intent to open ScanActivity
    static public Intent getWebIntent(Context context, String website)
    {
        Intent scan = new Intent(context, ScanActivity.class);
        scan.putExtra("REDIRECT", website);
        scan.putExtra("REDIRECT_TO", "website");
        scan.putExtra("PACKAGE_NAME", getPackageName(context));
        return scan;
    }

    static public Intent getActivityIntent(Context context, String activity)
    {
        Intent scan = new Intent(context, ScanActivity.class);
        scan.putExtra("REDIRECT", activity);
        scan.putExtra("REDIRECT_TO", "activity");
        scan.putExtra("PACKAGE_NAME", getPackageName(context));
        return scan;
    }

    static public Intent getVideoIntent(Context context, String video_url)
    {
        Intent scan = new Intent(context, ScanActivity.class);
        scan.putExtra("REDIRECT", video_url);
        scan.putExtra("REDIRECT_TO", "video");
        scan.putExtra("PACKAGE_NAME", getPackageName(context));
        return scan;
    }

    static public Intent getReturnIntent(Context context)
    {
        Intent scan = new Intent(context, ScanActivity.class);
        scan.putExtra("REDIRT", getPreviousClassName(context));
        scan.putExtra("REDIRECT_TO", "activity");
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

}
