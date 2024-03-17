package com.android.systemui.shared.system;

import android.graphics.Rect;
import com.android.systemui.shared.recents.model.ThumbnailData;
import java.util.HashMap;

public interface RecentsAnimationListener {
    void onAnimationCanceled(HashMap<Integer, ThumbnailData> hashMap);

    void onAnimationStart(RecentsAnimationControllerCompat recentsAnimationControllerCompat, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, Rect rect, Rect rect2);

    boolean onSwitchToScreenshot(Runnable runnable) {
        return false;
    }

    void onTasksAppeared(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr);
}
