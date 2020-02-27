package com.liminal.easy_augment_example;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RedirectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redirect);

        Intent intent = getIntent();
        String imageName = intent.getStringExtra("IMAGE_NAME");

        TextView textView = findViewById(R.id.textViewJebaited);
        textView.setText(imageName);
    }
}
