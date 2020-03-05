package com.liminal.easy_augment;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.FixedWidthViewSizer;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

class AugmentView {

    boolean isTracking = false;
    private ArFragment arFragment;
    private Context context;

    AugmentView(ArFragment arFragment, Context context)
    {
        this.arFragment = arFragment;
        this.context = context;
    }

    void createViewRenderable(Anchor anchor, int viewID) {
        Log.d("SCAN_ACTIVITY_VIEW","Creating view");
        // Fix width of view augmented to 0.1 meters
        FixedWidthViewSizer viewSizer = new FixedWidthViewSizer(0.1f);
        ViewRenderable
                .builder()
                .setView(context, viewID)
                .build()
                .thenAccept(viewRenderable -> {
                    viewRenderable.setSizer(viewSizer);
                    viewRenderable.setVerticalAlignment(ViewRenderable.VerticalAlignment.CENTER);
                    addtoScene(viewRenderable,anchor);
                    AugmentedViewManager augmentedViewManager = new AugmentedViewManager(viewRenderable.getView(), context);
                });
    }

    // Add View Renderable to AR Scene
    private void addtoScene(ViewRenderable viewRenderable, Anchor anchor) {
        Log.d("SCAN_ACTIVITY_VIEW","Adding anchor");
        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
        // Rotate node by -90 degrees on x axis
        node.setLocalRotation(Quaternion.axisAngle(new Vector3(1f, 0, 0), -90));
        node.setParent(anchorNode);
        node.setRenderable(viewRenderable);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
    }
}
