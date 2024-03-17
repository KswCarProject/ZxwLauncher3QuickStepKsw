package com.android.launcher3.util;

import android.content.Context;
import android.graphics.Canvas;
import com.android.launcher3.Utilities;
import com.android.systemui.plugins.shared.LauncherOverlayManager;

public class OverlayEdgeEffect extends EdgeEffectCompat {
    private float mDistance;
    private final boolean mIsRtl;
    private boolean mIsScrolling;
    private final LauncherOverlayManager.LauncherOverlay mOverlay;

    public boolean draw(Canvas canvas) {
        return false;
    }

    public void onAbsorb(int i) {
    }

    public OverlayEdgeEffect(Context context, LauncherOverlayManager.LauncherOverlay launcherOverlay) {
        super(context);
        this.mOverlay = launcherOverlay;
        this.mIsRtl = Utilities.isRtl(context.getResources());
    }

    public float getDistance() {
        return this.mDistance;
    }

    public float onPullDistance(float f, float f2) {
        this.mDistance = Math.max(0.0f, this.mDistance + f);
        if (!this.mIsScrolling) {
            this.mOverlay.onScrollInteractionBegin();
            this.mIsScrolling = true;
        }
        this.mOverlay.onScrollChange(this.mDistance, this.mIsRtl);
        if (this.mDistance > 0.0f) {
            return f;
        }
        return 0.0f;
    }

    public boolean isFinished() {
        return this.mDistance <= 0.0f;
    }

    public void onRelease() {
        if (this.mIsScrolling) {
            this.mDistance = 0.0f;
            this.mOverlay.onScrollInteractionEnd();
            this.mIsScrolling = false;
        }
    }

    public void finish() {
        this.mDistance = 0.0f;
    }
}
