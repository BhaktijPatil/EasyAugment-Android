package com.liminal.easy_augment;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.google.ar.core.AugmentedImage;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ModelRenderable;
import java.util.concurrent.CompletableFuture;

public class AugmentedImageNode extends AnchorNode {

    private static final String TAG = "AUGMENTED_IMAGE_NODE";

    private AugmentedImage image;
    private Node objNode;
    private CompletableFuture<ModelRenderable> objRenderable;

    // Constructor for AInode
    public AugmentedImageNode(Context context, String modelName) {
        objRenderable =
                ModelRenderable.builder()
                        .setSource(context, Uri.parse(modelName))
                        .build();
    }

    // Function to set anchor AR model to the image
    public void setImage(AugmentedImage image) {
        this.image = image;

        // Initialize mazeNode and set its parents and the Renderable. If any of the models are not loaded, process this function until they all are loaded.
        if (!objRenderable.isDone()) {
            CompletableFuture.allOf(objRenderable)
                    .thenAccept((Void aVoid) -> setImage(image))
                    .exceptionally(
                            throwable -> {
                                Log.e(TAG, "3D Model is rendering", throwable);
                                return null;
                            });
            return;
        }

        // Set the anchor based on the center of the image.
        setAnchor(image.createAnchor(image.getCenterPose()));

        // Assign renderable to ObjNode
        objNode = new Node();
        objNode.setParent(this);
        objNode.setRenderable(objRenderable.getNow(null));
    }
}
