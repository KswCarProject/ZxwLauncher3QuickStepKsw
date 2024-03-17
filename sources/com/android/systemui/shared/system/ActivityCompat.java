package com.android.systemui.shared.system;

import android.app.Activity;

public class ActivityCompat {
    private final Activity mWrapped;

    public ActivityCompat(Activity activity) {
        this.mWrapped = activity;
    }

    public void registerRemoteAnimations(RemoteAnimationDefinitionCompat remoteAnimationDefinitionCompat) {
        this.mWrapped.registerRemoteAnimations(remoteAnimationDefinitionCompat.getWrapped());
    }

    public void unregisterRemoteAnimations() {
        this.mWrapped.unregisterRemoteAnimations();
    }
}
