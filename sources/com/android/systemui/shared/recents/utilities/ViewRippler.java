package com.android.systemui.shared.recents.utilities;

import android.view.View;

public class ViewRippler {
    private static final int RIPPLE_INTERVAL_MS = 2000;
    private static final int RIPPLE_OFFSET_MS = 50;
    private final Runnable mRipple = new Runnable() {
        public void run() {
            if (ViewRippler.this.mRoot.isAttachedToWindow()) {
                ViewRippler.this.mRoot.setPressed(true);
                ViewRippler.this.mRoot.setPressed(false);
            }
        }
    };
    /* access modifiers changed from: private */
    public View mRoot;

    public void start(View view) {
        stop();
        this.mRoot = view;
        view.postOnAnimationDelayed(this.mRipple, 50);
        this.mRoot.postOnAnimationDelayed(this.mRipple, 2000);
        this.mRoot.postOnAnimationDelayed(this.mRipple, 4000);
        this.mRoot.postOnAnimationDelayed(this.mRipple, 6000);
        this.mRoot.postOnAnimationDelayed(this.mRipple, 8000);
    }

    public void stop() {
        View view = this.mRoot;
        if (view != null) {
            view.removeCallbacks(this.mRipple);
        }
    }
}
