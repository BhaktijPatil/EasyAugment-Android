package com.liminal.easy_augment_example;

// Import statements

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.liminal.easy_augment.EasyAugmentHelper;

// Main activity
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String devKey = "101";

        EasyAugmentHelper easyAugmentHelper = new EasyAugmentHelper(devKey, this, RedirectActivity.class.getName());
        easyAugmentHelper.loadMarkerImages();

        Button button = findViewById(R.id.button);
        button.setOnClickListener(view -> easyAugmentHelper.activateScanner());

    }
}