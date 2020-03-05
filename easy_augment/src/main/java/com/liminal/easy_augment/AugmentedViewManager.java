package com.liminal.easy_augment;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

class AugmentedViewManager {
    private View view;
    private Context context;

    AugmentedViewManager(View view, Context scannerContext)
    {
        context = scannerContext;
        this.view = view;
        onViewInflated();
    }

    private void onViewInflated()
    {
        Button button = view.findViewById(R.id.button);
        Intent intent = new Intent(context, RedirectWeb.class);
        intent.putExtra("WEBSITE", "https://github.com/google-ar/sceneform-android-sdk/issues/989");
        button.setOnClickListener(view -> context.startActivity(intent));
    }
}
