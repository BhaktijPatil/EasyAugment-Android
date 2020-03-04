package com.liminal.easy_augment;

import android.content.Context;
import android.util.Log;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.rendering.FixedWidthViewSizer;
import com.google.ar.sceneform.rendering.ViewRenderable;

class AugmentView {

    boolean isTracking = false;

    void createViewRenderable(Anchor anchor, int viewID, Scene scene, Context context) {
        Log.d("SCAN_ACTIVITY_VIEW","Creating view");
        //Fix width of view augmented to 0.1 meters
        FixedWidthViewSizer viewSizer = new FixedWidthViewSizer(0.1f);
        ViewRenderable
                .builder()
                .setView(context, viewID)
                .build()
                .thenAccept(viewRenderable -> {
                    viewRenderable.setSizer(viewSizer);
                    addtoScene(viewRenderable,anchor,scene);
                });
    }

    //Add View Renderable to AR Scene
    private void addtoScene(ViewRenderable viewRenderable, Anchor anchor, Scene scene) {
        Log.d("SCAN_ACTIVITY_VIEW","Adding anchor");
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setRenderable(viewRenderable);
        scene.addChild(anchorNode);
    }
}
