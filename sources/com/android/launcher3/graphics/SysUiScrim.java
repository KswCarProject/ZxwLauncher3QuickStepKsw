package com.android.launcher3.graphics;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.FloatProperty;
import android.view.View;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.R;
import com.android.launcher3.ResourceUtils;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.icons.GraphicsUtils;
import com.android.launcher3.util.DynamicResource;
import com.android.launcher3.util.Themes;

public class SysUiScrim implements View.OnAttachStateChangeListener {
    private static final int ALPHA_MASK_BITMAP_DP = 200;
    private static final int ALPHA_MASK_HEIGHT_DP = 500;
    private static final int ALPHA_MASK_WIDTH_DP = 2;
    private static final int MAX_HOTSEAT_SCRIM_ALPHA = 100;
    private static final FloatProperty<SysUiScrim> SYSUI_ANIM_MULTIPLIER = new FloatProperty<SysUiScrim>("sysUiAnimMultiplier") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public Float get(SysUiScrim sysUiScrim) {
            return Float.valueOf(sysUiScrim.mSysUiAnimMultiplier);
        }

        public void setValue(SysUiScrim sysUiScrim, float f) {
            float unused = sysUiScrim.mSysUiAnimMultiplier = f;
            sysUiScrim.reapplySysUiAlpha();
        }
    };
    public static final FloatProperty<SysUiScrim> SYSUI_PROGRESS = new FloatProperty<SysUiScrim>("sysUiProgress") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public Float get(SysUiScrim sysUiScrim) {
            return Float.valueOf(sysUiScrim.mSysUiProgress);
        }

        public void setValue(SysUiScrim sysUiScrim, float f) {
            sysUiScrim.setSysUiProgress(f);
        }
    };
    private final BaseDraggingActivity mActivity;
    /* access modifiers changed from: private */
    public boolean mAnimateScrimOnNextDraw;
    private final Bitmap mBottomMask;
    private final Paint mBottomMaskPaint;
    private boolean mDrawBottomScrim;
    private boolean mDrawTopScrim;
    private boolean mDrawWallpaperScrim;
    private final RectF mFinalMaskRect;
    private boolean mHideSysUiScrim;
    private final int mMaskHeight;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.SCREEN_OFF".equals(action)) {
                boolean unused = SysUiScrim.this.mAnimateScrimOnNextDraw = true;
            } else if ("android.intent.action.USER_PRESENT".equals(action)) {
                boolean unused2 = SysUiScrim.this.mAnimateScrimOnNextDraw = false;
            }
        }
    };
    private final View mRoot;
    /* access modifiers changed from: private */
    public float mSysUiAnimMultiplier;
    /* access modifiers changed from: private */
    public float mSysUiProgress;
    private final Drawable mTopScrim;
    private int mWallpaperScrimMaxAlpha;
    private final Paint mWallpaperScrimPaint;
    private final RectF mWallpaperScrimRect = new RectF();

    public SysUiScrim(View view) {
        Bitmap bitmap;
        Paint paint = new Paint();
        this.mWallpaperScrimPaint = paint;
        this.mFinalMaskRect = new RectF();
        this.mBottomMaskPaint = new Paint(2);
        this.mSysUiProgress = 1.0f;
        boolean z = false;
        this.mAnimateScrimOnNextDraw = false;
        this.mSysUiAnimMultiplier = 1.0f;
        this.mRoot = view;
        this.mActivity = (BaseDraggingActivity) BaseDraggingActivity.fromContext(view.getContext());
        this.mMaskHeight = ResourceUtils.pxFromDp(200.0f, view.getResources().getDisplayMetrics());
        Drawable attrDrawable = Themes.getAttrDrawable(view.getContext(), R.attr.workspaceStatusBarScrim);
        this.mTopScrim = attrDrawable;
        if (attrDrawable == null) {
            bitmap = null;
        } else {
            bitmap = createDitheredAlphaMask();
        }
        this.mBottomMask = bitmap;
        this.mHideSysUiScrim = attrDrawable == null;
        if (FeatureFlags.ENABLE_WALLPAPER_SCRIM.get() && !Themes.getAttrBoolean(view.getContext(), R.attr.isMainColorDark) && !Themes.getAttrBoolean(view.getContext(), R.attr.isWorkspaceDarkText)) {
            z = true;
        }
        this.mDrawWallpaperScrim = z;
        int color = DynamicResource.provider(view.getContext()).getColor(R.color.wallpaper_scrim_color);
        this.mWallpaperScrimMaxAlpha = Color.alpha(color);
        paint.setColor(color);
        view.addOnAttachStateChangeListener(this);
    }

    public void draw(Canvas canvas) {
        if (this.mHideSysUiScrim) {
            return;
        }
        if (this.mSysUiProgress <= 0.0f) {
            this.mAnimateScrimOnNextDraw = false;
            return;
        }
        if (this.mAnimateScrimOnNextDraw) {
            this.mSysUiAnimMultiplier = 0.0f;
            reapplySysUiAlphaNoInvalidate();
            ObjectAnimator createSysuiMultiplierAnim = createSysuiMultiplierAnim(1.0f);
            createSysuiMultiplierAnim.setDuration(600);
            createSysuiMultiplierAnim.setStartDelay(this.mActivity.getWindow().getTransitionBackgroundFadeDuration());
            createSysuiMultiplierAnim.start();
            this.mAnimateScrimOnNextDraw = false;
        }
        if (this.mDrawWallpaperScrim) {
            canvas.drawRect(this.mWallpaperScrimRect, this.mWallpaperScrimPaint);
        }
        if (this.mDrawTopScrim) {
            this.mTopScrim.draw(canvas);
        }
        if (this.mDrawBottomScrim) {
            canvas.drawBitmap(this.mBottomMask, (Rect) null, this.mFinalMaskRect, this.mBottomMaskPaint);
        }
    }

    public ObjectAnimator createSysuiMultiplierAnim(float... fArr) {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, SYSUI_ANIM_MULTIPLIER, fArr);
        ofFloat.setAutoCancel(true);
        return ofFloat;
    }

    public void onInsetsChanged(Rect rect) {
        boolean z = true;
        this.mDrawTopScrim = this.mTopScrim != null && rect.top > 0;
        if (this.mBottomMask == null || this.mActivity.getDeviceProfile().isVerticalBarLayout() || !hasBottomNavButtons()) {
            z = false;
        }
        this.mDrawBottomScrim = z;
    }

    private boolean hasBottomNavButtons() {
        if (!Utilities.ATLEAST_Q || this.mActivity.getRootView() == null || this.mActivity.getRootView().getRootWindowInsets() == null || this.mActivity.getRootView().getRootWindowInsets().getTappableElementInsets().bottom > 0) {
            return true;
        }
        return false;
    }

    public void onViewAttachedToWindow(View view) {
        if (!FeatureFlags.KEYGUARD_ANIMATION.get() && this.mTopScrim != null) {
            IntentFilter intentFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
            intentFilter.addAction("android.intent.action.USER_PRESENT");
            this.mRoot.getContext().registerReceiver(this.mReceiver, intentFilter);
        }
    }

    public void onViewDetachedFromWindow(View view) {
        if (!FeatureFlags.KEYGUARD_ANIMATION.get() && this.mTopScrim != null) {
            this.mRoot.getContext().unregisterReceiver(this.mReceiver);
        }
    }

    public void setSize(int i, int i2) {
        Drawable drawable = this.mTopScrim;
        if (drawable != null) {
            drawable.setBounds(0, 0, i, i2);
            this.mFinalMaskRect.set(0.0f, (float) (i2 - this.mMaskHeight), (float) i, (float) i2);
        }
        this.mWallpaperScrimRect.set(0.0f, 0.0f, (float) i, (float) i2);
    }

    /* access modifiers changed from: private */
    public void setSysUiProgress(float f) {
        if (f != this.mSysUiProgress) {
            this.mSysUiProgress = f;
            reapplySysUiAlpha();
        }
    }

    /* access modifiers changed from: private */
    public void reapplySysUiAlpha() {
        reapplySysUiAlphaNoInvalidate();
        if (!this.mHideSysUiScrim) {
            this.mRoot.invalidate();
        }
    }

    private void reapplySysUiAlphaNoInvalidate() {
        float f = this.mSysUiProgress * this.mSysUiAnimMultiplier;
        this.mBottomMaskPaint.setAlpha(Math.round(100.0f * f));
        Drawable drawable = this.mTopScrim;
        if (drawable != null) {
            drawable.setAlpha(Math.round(255.0f * f));
        }
        this.mWallpaperScrimPaint.setAlpha(Math.round(((float) this.mWallpaperScrimMaxAlpha) * f));
    }

    private Bitmap createDitheredAlphaMask() {
        DisplayMetrics displayMetrics = this.mActivity.getResources().getDisplayMetrics();
        int pxFromDp = ResourceUtils.pxFromDp(2.0f, displayMetrics);
        int pxFromDp2 = ResourceUtils.pxFromDp(500.0f, displayMetrics);
        Bitmap createBitmap = Bitmap.createBitmap(pxFromDp, this.mMaskHeight, Bitmap.Config.ALPHA_8);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint(4);
        float f = (float) pxFromDp2;
        float f2 = f;
        paint.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, f2, new int[]{16777215, GraphicsUtils.setColorAlphaBound(-1, 242), -1}, new float[]{0.0f, 0.8f, 1.0f}, Shader.TileMode.CLAMP));
        canvas.drawRect(0.0f, 0.0f, (float) pxFromDp, f, paint);
        return createBitmap;
    }
}
