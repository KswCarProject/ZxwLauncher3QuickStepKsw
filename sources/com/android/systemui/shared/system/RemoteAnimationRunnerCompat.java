package com.android.systemui.shared.system;

public interface RemoteAnimationRunnerCompat {
    void onAnimationCancelled();

    void onAnimationStart(int i, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3, Runnable runnable);
}
