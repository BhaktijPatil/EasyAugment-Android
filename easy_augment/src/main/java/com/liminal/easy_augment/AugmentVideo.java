package com.liminal.easy_augment;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.ModelRenderable;

class AugmentVideo {

    private static ExternalTexture texture;
    private static ModelRenderable renderable;
    private static SimpleExoPlayer player;

    static void videoAugment(String url, Context context){
        if(player == null)
        {
            texture = new ExternalTexture();
            Uri uri = Uri.parse(url);
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, "EasyAugment");
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            player = new SimpleExoPlayer.Builder(context).build();
            player.setVideoSurface(texture.getSurface());
            player.prepare(mediaSource, false, false);
            player.setRepeatMode(Player.REPEAT_MODE_ALL);
            player.setPlayWhenReady(false);

            ModelRenderable
                    .builder()
                    .setSource(context, R.raw.augmented_video_model)
                    .build()
                    .thenAccept(modelRenderable -> {
                        modelRenderable.getMaterial().setExternalTexture("videoTexture",
                                texture);

                        renderable = modelRenderable;
                        renderable.setShadowCaster(false);
                        renderable.setShadowReceiver(false);
                    });
        }
    }

    static void playVideo(Anchor anchor, float extentX, float extentZ, Scene scene) {
        player.setPlayWhenReady(true);

        AnchorNode anchorNode = new AnchorNode(anchor);

        texture.getSurfaceTexture().setOnFrameAvailableListener(surfaceTexture -> {
            anchorNode.setRenderable(renderable);
            texture.getSurfaceTexture().setOnFrameAvailableListener(null);
        });

        anchorNode.setWorldScale(new Vector3(extentX, 1f, extentZ));
        scene.addChild(anchorNode);
    }

}
