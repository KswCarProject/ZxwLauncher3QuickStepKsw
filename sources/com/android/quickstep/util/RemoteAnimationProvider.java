package com.android.quickstep.util;

import android.animation.AnimatorSet;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;

public abstract class RemoteAnimationProvider {
    public abstract AnimatorSet createWindowAnimation(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2);

    public static RemoteAnimationTargetCompat findLowestOpaqueLayerTarget(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, int i) {
        int i2;
        int i3 = Integer.MAX_VALUE;
        int i4 = -1;
        for (int length = remoteAnimationTargetCompatArr.length - 1; length >= 0; length--) {
            RemoteAnimationTargetCompat remoteAnimationTargetCompat = remoteAnimationTargetCompatArr[length];
            if (remoteAnimationTargetCompat.mode == i && !remoteAnimationTargetCompat.isTranslucent && (i2 = remoteAnimationTargetCompat.prefixOrderIndex) < i3) {
                i4 = length;
                i3 = i2;
            }
        }
        if (i4 != -1) {
            return remoteAnimationTargetCompatArr[i4];
        }
        return null;
    }
}
