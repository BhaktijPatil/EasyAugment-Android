package com.liminal.easy_augment_example;

// Import statements

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.liminal.easy_augment.ImageStore;
import com.liminal.easy_augment.ScanActivityHelper;

// Main activity
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // image Store class needs context of Main Activity
        ScanActivityHelper.setContext(this);

//        // load the reference images that needs scanning
//        ArrayList<Bitmap> refImageList = new ArrayList<>();
//        refImageList.add(BitmapFactory.decodeResource(this.getResources(), R.drawable.ferrari_logo));
//        refImageList.add(BitmapFactory.decodeResource(this.getResources(), R.drawable.lamborghini_logo));
//
//        ImageStore.storeRefImage(refImageList);

        // load the image from given URL
        ImageStore.storeImageFromURL("https://liminal.in/images/test.jpg");

//        // load the reference image that needs scanning
//        Bitmap refBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.modi_logo);
//        ImageStore.storeRefImage(this, refBitmap);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(view -> {
            Intent scan = ScanActivityHelper.getWebIntent("https://www.oculus.com/");
//            Intent scan = ScanActivityHelper.getActivityIntent("RedirectActivity");
//            Intent scan = ScanActivityHelper.getReturnIntent();
//            Intent scan = ScanActivityHelper.getVideoIntent("https://www.youtube.com/watch?v=hAsZCTL__lo");

            startActivity(scan);
        });

    }
}
