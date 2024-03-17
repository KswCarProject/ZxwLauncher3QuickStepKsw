package com.android.launcher3.util;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.animation.Interpolator;
import com.android.launcher3.Utilities;
import com.android.launcher3.Workspace;
import com.android.launcher3.anim.Interpolators;

public class WallpaperOffsetInterpolator extends BroadcastReceiver {
    private static final int ANIMATION_DURATION = 250;
    private static final int MIN_PARALLAX_PAGE_SPAN = 4;
    private static final int MSG_APPLY_OFFSET = 3;
    private static final int MSG_JUMP_TO_FINAL = 5;
    private static final int MSG_SET_NUM_PARALLAX = 4;
    private static final int MSG_START_ANIMATION = 1;
    private static final int MSG_UPDATE_OFFSET = 2;
    private static final String TAG = "WPOffsetInterpolator";
    private static final int[] sTempInt = new int[2];
    private final Handler mHandler;
    private final boolean mIsRtl;
    private boolean mLockedToDefaultPage;
    private int mNumScreens;
    private boolean mRegistered = false;
    private boolean mWallpaperIsLiveWallpaper;
    private IBinder mWindowToken;
    private final Workspace<?> mWorkspace;

    public WallpaperOffsetInterpolator(Workspace<?> workspace) {
        this.mWorkspace = workspace;
        this.mIsRtl = Utilities.isRtl(workspace.getResources());
        this.mHandler = new OffsetHandler(workspace.getContext());
    }

    public void setLockToDefaultPage(boolean z) {
        this.mLockedToDefaultPage = z;
    }

    public boolean isLockedToDefaultPage() {
        return this.mLockedToDefaultPage;
    }

    private void wallpaperOffsetForScroll(int i, int i2, int[] iArr) {
        int i3;
        iArr[1] = 1;
        if (this.mLockedToDefaultPage || i2 <= 1) {
            iArr[0] = this.mIsRtl;
            return;
        }
        if (this.mWallpaperIsLiveWallpaper) {
            i3 = i2;
        } else {
            i3 = Math.max(4, i2);
        }
        int numPagesExcludingEmpty = getNumPagesExcludingEmpty() - 1;
        boolean z = this.mIsRtl;
        int i4 = z ? numPagesExcludingEmpty : 0;
        if (z) {
            numPagesExcludingEmpty = 0;
        }
        int scrollForPage = this.mWorkspace.getScrollForPage(i4);
        int scrollForPage2 = this.mWorkspace.getScrollForPage(numPagesExcludingEmpty) - scrollForPage;
        if (scrollForPage2 <= 0) {
            iArr[0] = 0;
            return;
        }
        int boundToRange = Utilities.boundToRange((i - scrollForPage) - this.mWorkspace.getLayoutTransitionOffsetForPage(0), 0, scrollForPage2);
        iArr[1] = (i3 - 1) * scrollForPage2;
        iArr[0] = (this.mIsRtl ? iArr[1] - ((i2 - 1) * scrollForPage2) : 0) + (boundToRange * (i2 - 1));
    }

    public float wallpaperOffsetForScroll(int i) {
        int numScrollableScreensExcludingEmpty = getNumScrollableScreensExcludingEmpty();
        int[] iArr = sTempInt;
        wallpaperOffsetForScroll(i, numScrollableScreensExcludingEmpty, iArr);
        return ((float) iArr[0]) / ((float) iArr[1]);
    }

    private int getNumScrollableScreensExcludingEmpty() {
        return (int) Math.ceil((double) (((float) getNumPagesExcludingEmpty()) / ((float) this.mWorkspace.getPanelCount())));
    }

    private int getNumPagesExcludingEmpty() {
        int childCount = this.mWorkspace.getChildCount();
        return (childCount < 4 || !this.mWorkspace.hasExtraEmptyScreens()) ? childCount : childCount - this.mWorkspace.getPanelCount();
    }

    public void syncWithScroll() {
        int numScrollableScreensExcludingEmpty = getNumScrollableScreensExcludingEmpty();
        int scrollX = this.mWorkspace.getScrollX();
        int[] iArr = sTempInt;
        wallpaperOffsetForScroll(scrollX, numScrollableScreensExcludingEmpty, iArr);
        Message obtain = Message.obtain(this.mHandler, 2, iArr[0], iArr[1], this.mWindowToken);
        int i = this.mNumScreens;
        if (numScrollableScreensExcludingEmpty != i) {
            if (i > 0) {
                obtain.what = 1;
            }
            this.mNumScreens = numScrollableScreensExcludingEmpty;
            updateOffset();
        }
        obtain.sendToTarget();
    }

    public int getNumPagesForWallpaperParallax() {
        if (this.mWallpaperIsLiveWallpaper) {
            return this.mNumScreens;
        }
        return Math.max(4, this.mNumScreens);
    }

    private void updateOffset() {
        Message.obtain(this.mHandler, 4, getNumPagesForWallpaperParallax(), 0, this.mWindowToken).sendToTarget();
    }

    public void jumpToFinal() {
        Message.obtain(this.mHandler, 5, this.mWindowToken).sendToTarget();
    }

    public void setWindowToken(IBinder iBinder) {
        this.mWindowToken = iBinder;
        if (iBinder == null && this.mRegistered) {
            this.mWorkspace.getContext().unregisterReceiver(this);
            this.mRegistered = false;
        } else if (iBinder != null && !this.mRegistered) {
            this.mWorkspace.getContext().registerReceiver(this, new IntentFilter("android.intent.action.WALLPAPER_CHANGED"));
            onReceive(this.mWorkspace.getContext(), (Intent) null);
            this.mRegistered = true;
        }
    }

    public void onReceive(Context context, Intent intent) {
        Executors.UI_HELPER_EXECUTOR.execute(new Runnable(context) {
            public final /* synthetic */ Context f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                WallpaperOffsetInterpolator.this.lambda$onReceive$0$WallpaperOffsetInterpolator(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$onReceive$0$WallpaperOffsetInterpolator(Context context) {
        this.mWallpaperIsLiveWallpaper = WallpaperManager.getInstance(context).getWallpaperInfo() != null;
        updateOffset();
    }

    private static class OffsetHandler extends Handler {
        private boolean mAnimating;
        private float mAnimationStartOffset;
        private long mAnimationStartTime;
        private float mCurrentOffset = 0.5f;
        private float mFinalOffset;
        private final Interpolator mInterpolator = Interpolators.DEACCEL_1_5;
        private float mOffsetX;
        private final WallpaperManager mWM;

        public OffsetHandler(Context context) {
            super(Executors.UI_HELPER_EXECUTOR.getLooper());
            this.mWM = WallpaperManager.getInstance(context);
        }

        /* JADX WARNING: Removed duplicated region for block: B:24:0x0059  */
        /* JADX WARNING: Removed duplicated region for block: B:28:0x007d  */
        /* JADX WARNING: Removed duplicated region for block: B:31:0x0089  */
        /* JADX WARNING: Removed duplicated region for block: B:34:0x0097  */
        /* JADX WARNING: Removed duplicated region for block: B:36:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void handleMessage(android.os.Message r11) {
            /*
                r10 = this;
                java.lang.Object r0 = r11.obj
                android.os.IBinder r0 = (android.os.IBinder) r0
                if (r0 != 0) goto L_0x0007
                return
            L_0x0007:
                int r1 = r11.what
                r2 = 0
                r3 = 3
                r4 = 1065353216(0x3f800000, float:1.0)
                r5 = 1
                if (r1 == r5) goto L_0x003e
                r6 = 2
                if (r1 == r6) goto L_0x004a
                if (r1 == r3) goto L_0x0053
                r3 = 4
                if (r1 == r3) goto L_0x0030
                r11 = 5
                if (r1 == r11) goto L_0x001c
                return
            L_0x001c:
                float r11 = r10.mCurrentOffset
                float r1 = r10.mFinalOffset
                int r11 = java.lang.Float.compare(r11, r1)
                if (r11 == 0) goto L_0x002d
                float r11 = r10.mFinalOffset
                r10.mCurrentOffset = r11
                r10.setOffsetSafely(r0)
            L_0x002d:
                r10.mAnimating = r2
                return
            L_0x0030:
                int r11 = r11.arg1
                int r11 = r11 - r5
                float r11 = (float) r11
                float r11 = r4 / r11
                r10.mOffsetX = r11
                android.app.WallpaperManager r0 = r10.mWM
                r0.setWallpaperOffsetSteps(r11, r4)
                return
            L_0x003e:
                r10.mAnimating = r5
                float r1 = r10.mCurrentOffset
                r10.mAnimationStartOffset = r1
                long r6 = r11.getWhen()
                r10.mAnimationStartTime = r6
            L_0x004a:
                int r1 = r11.arg1
                float r1 = (float) r1
                int r11 = r11.arg2
                float r11 = (float) r11
                float r1 = r1 / r11
                r10.mFinalOffset = r1
            L_0x0053:
                float r11 = r10.mCurrentOffset
                boolean r1 = r10.mAnimating
                if (r1 == 0) goto L_0x007d
                long r6 = android.os.SystemClock.uptimeMillis()
                long r8 = r10.mAnimationStartTime
                long r6 = r6 - r8
                float r1 = (float) r6
                r8 = 1132068864(0x437a0000, float:250.0)
                float r1 = r1 / r8
                android.view.animation.Interpolator r8 = r10.mInterpolator
                float r1 = r8.getInterpolation(r1)
                float r8 = r10.mAnimationStartOffset
                float r9 = r10.mFinalOffset
                float r9 = r9 - r8
                float r9 = r9 * r1
                float r8 = r8 + r9
                r10.mCurrentOffset = r8
                r8 = 250(0xfa, double:1.235E-321)
                int r1 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
                if (r1 >= 0) goto L_0x007a
                r2 = r5
            L_0x007a:
                r10.mAnimating = r2
                goto L_0x0081
            L_0x007d:
                float r1 = r10.mFinalOffset
                r10.mCurrentOffset = r1
            L_0x0081:
                float r1 = r10.mCurrentOffset
                int r11 = java.lang.Float.compare(r1, r11)
                if (r11 == 0) goto L_0x0093
                r10.setOffsetSafely(r0)
                android.app.WallpaperManager r11 = r10.mWM
                float r1 = r10.mOffsetX
                r11.setWallpaperOffsetSteps(r1, r4)
            L_0x0093:
                boolean r11 = r10.mAnimating
                if (r11 == 0) goto L_0x009e
                android.os.Message r11 = android.os.Message.obtain(r10, r3, r0)
                r11.sendToTarget()
            L_0x009e:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.util.WallpaperOffsetInterpolator.OffsetHandler.handleMessage(android.os.Message):void");
        }

        private void setOffsetSafely(IBinder iBinder) {
            try {
                this.mWM.setWallpaperOffsets(iBinder, this.mCurrentOffset, 0.5f);
            } catch (IllegalArgumentException e) {
                Log.e(WallpaperOffsetInterpolator.TAG, "Error updating wallpaper offset: " + e);
            }
        }
    }
}
