package com.liminal.easy_augment_example;

// Import statements
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;

import com.liminal.easy_augment.ImageStore;
import com.liminal.easy_augment.ScanActivityHelper;

import java.util.ArrayList;

// Main activity
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // load the reference images that needs scanning
        ArrayList<Bitmap> refImageList = new ArrayList<>();
        refImageList.add(BitmapFactory.decodeResource(this.getResources(), R.drawable.ferrari_logo));
        refImageList.add(BitmapFactory.decodeResource(this.getResources(), R.drawable.lamborghini_logo));
        ImageStore.storeRefImage(this, refImageList);

//        // load the reference image that needs scanning
//        Bitmap refBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.modi_logo);
//        ImageStore.storeRefImage(this, refBitmap);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(view -> {
            Intent scan = ScanActivityHelper.getWebIntent(this, "https://www.oculus.com/");
//            Intent scan = ScanActivityHelper.getActivityIntent(this, "RedirectActivity");
//            Intent scan = ScanActivityHelper.getReturnIntent(this);
//            Intent scan = ScanActivityHelper.getVideoIntent(this, "https://www.youtube.com/watch?v=hAsZCTL__lo");

            startActivity(scan);
        });
    }
}
