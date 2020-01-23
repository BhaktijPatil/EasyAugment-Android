package com.liminal.easy_augment_example;

// Import statements
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.liminal.easy_augment.ScanActivityHelper;

// Main activity
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(view -> {
            Intent scan = ScanActivityHelper.getIntent(this, "https://www.oculus.com/");
            startActivity(scan);
        });
    }
}
