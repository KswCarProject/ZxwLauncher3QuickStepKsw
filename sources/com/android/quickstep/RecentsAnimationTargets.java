package com.android.quickstep;

import android.graphics.Rect;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;

public class RecentsAnimationTargets extends RemoteAnimationTargets {
    public final Rect homeContentInsets;
    public final Rect minimizedHomeBounds;

    public RecentsAnimationTargets(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3, Rect rect, Rect rect2) {
        super(remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, remoteAnimationTargetCompatArr3, 1);
        this.homeContentInsets = rect;
        this.minimizedHomeBounds = rect2;
    }

    public boolean hasTargets() {
        return this.unfilteredApps.length != 0;
    }
}
