package com.liminal.easy_augment;

import android.content.Context;
import android.content.Intent;

public class ScanActivityHelper {

    static Context context;

    static public void setContext(Context appContext) {
        context = appContext;
    }

    // Multiple getIntent functions for creating an intent to open ScanActivity
    static public Intent getWebIntent(String website)
    {
        Intent scan = new Intent(context, ScanActivity.class);
        scan.putExtra("REDIRECT", website);
        scan.putExtra("REDIRECT_TO", "website");
        scan.putExtra("PACKAGE_NAME", getPackageName());
        return scan;
    }

    static public Intent getActivityIntent(String activity)
    {
        Intent scan = new Intent(context, ScanActivity.class);
        scan.putExtra("REDIRECT", activity);
        scan.putExtra("REDIRECT_TO", "activity");
        scan.putExtra("PACKAGE_NAME", getPackageName());
        return scan;
    }

    static public Intent getVideoIntent(String video_url)
    {
        Intent scan = new Intent(context, ScanActivity.class);
        scan.putExtra("REDIRECT", video_url);
        scan.putExtra("REDIRECT_TO", "video");
        scan.putExtra("PACKAGE_NAME", getPackageName());
        return scan;
    }

    static public Intent getReturnIntent()
    {
        Intent scan = new Intent(context, ScanActivity.class);
        scan.putExtra("REDIRT", getPreviousClassName());
        scan.putExtra("REDIRECT_TO", "activity");
        scan.putExtra("PACKAGE_NAME", getPackageName());
        return scan;
    }

    // Get the name of the activity calling the Helper
    private static String getPreviousClassName()
    {
        return context.getClass().getSimpleName();
    }

    // Get the name of the application package
    private static String getPackageName()
    {
        return context.getApplicationContext().getPackageName();
    }

}
