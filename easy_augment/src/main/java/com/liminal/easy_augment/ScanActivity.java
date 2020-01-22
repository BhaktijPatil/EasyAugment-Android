package com.liminal.easy_augment;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;

public class ScanActivity extends AppCompatActivity {

    private ArFragment arFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);

        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a reference to a file from a Google Cloud Storage URI
        StorageReference gsReference = storage.getReferenceFromUrl("gs://easy-augment.appspot.com");
        StorageReference islandRef = gsReference.child("Images/lambo.jpg");

        ImageView image = findViewById(R.id.image_view_fit_to_scan);

        Glide.with(this /* context */)
                .load(islandRef)
                .into(image);


//        final long ONE_MEGABYTE = 1024 * 1024;
//        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                // Data for "images/island.jpg" is returns, use this as needed
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle any errors
//            }
//        });
//        File localFile = null;
//        try {
//            localFile = File.createTempFile("images", "jpg");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                // Local temp file has been created
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle any errors
//            }
//        });
//        Toast.makeText(getApplicationContext(),localFile.toString(),Toast.LENGTH_SHORT).show();
//        if(localFile.exists()){
//
//            Bitmap myBitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
//
//            ImageView myImage = findViewById(R.id.image_view_fit_to_scan);
//
//            myImage.setImageBitmap(myBitmap);
//
//        }

/*        ImageView image = findViewById(R.id.image_view_fit_to_scan);
        image.setImageBitmap(localFile);*/





    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //This method is called at the start of each frame. @param frameTime = time since last frame.
    private void onUpdateFrame(FrameTime frameTime) {
        Frame frame = arFragment.getArSceneView().getArFrame();

        // If there is no frame, just return.
        if (frame == null) {
            return;
        }

        Collection<AugmentedImage> updatedAugmentedImages = frame.getUpdatedTrackables(AugmentedImage.class);
        for (AugmentedImage augmentedImage : updatedAugmentedImages) {
            switch (augmentedImage.getTrackingState()) {
                case PAUSED:
                    // When an image is in PAUSED state it has been detected.
                    ComponentName cn = new ComponentName(this,"com.liminal.easy_augment_example.MainActivity");
                    Intent intent = new Intent().setComponent(cn);
                    startActivity(intent);

                case TRACKING:
                case STOPPED:
                    break;
            }
        }
    }
}
