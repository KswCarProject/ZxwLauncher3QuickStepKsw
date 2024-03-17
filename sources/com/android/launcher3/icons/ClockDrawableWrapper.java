package com.android.launcher3.icons;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.TypedValue;
import com.android.launcher3.icons.BitmapInfo;
import com.android.launcher3.icons.FastBitmapDrawable;
import com.android.launcher3.icons.IconProvider;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.function.IntFunction;

public class ClockDrawableWrapper extends AdaptiveIconDrawable implements BitmapInfo.Extender {
    private static final String DEFAULT_HOUR_METADATA_KEY = "com.android.launcher3.DEFAULT_HOUR";
    private static final String DEFAULT_MINUTE_METADATA_KEY = "com.android.launcher3.DEFAULT_MINUTE";
    private static final String DEFAULT_SECOND_METADATA_KEY = "com.android.launcher3.DEFAULT_SECOND";
    private static final boolean DISABLE_SECONDS = true;
    private static final String HOUR_INDEX_METADATA_KEY = "com.android.launcher3.HOUR_LAYER_INDEX";
    public static final int INVALID_VALUE = -1;
    private static final String LAUNCHER_PACKAGE = "com.android.launcher3";
    private static final int LEVELS_PER_SECOND = 10;
    private static final String MINUTE_INDEX_METADATA_KEY = "com.android.launcher3.MINUTE_LAYER_INDEX";
    private static final int NO_COLOR = -1;
    private static final String ROUND_ICON_METADATA_KEY = "com.android.launcher3.LEVEL_PER_TICK_ICON_ROUND";
    private static final String SECOND_INDEX_METADATA_KEY = "com.android.launcher3.SECOND_LAYER_INDEX";
    private static final String TAG = "ClockDrawableWrapper";
    public static final long TICK_MS = TimeUnit.MINUTES.toMillis(1);
    private final AnimationInfo mAnimationInfo = new AnimationInfo();
    private AnimationInfo mThemeInfo = null;

    private ClockDrawableWrapper(AdaptiveIconDrawable adaptiveIconDrawable) {
        super(adaptiveIconDrawable.getBackground(), adaptiveIconDrawable.getForeground());
    }

    private void applyThemeData(IconProvider.ThemeData themeData) {
        if (IconProvider.ATLEAST_T && this.mThemeInfo == null) {
            try {
                TypedArray obtainTypedArray = themeData.mResources.obtainTypedArray(themeData.mResID);
                int length = obtainTypedArray.length();
                Bundle bundle = new Bundle();
                for (int i = 0; i < length; i += 2) {
                    TypedValue peekValue = obtainTypedArray.peekValue(i + 1);
                    bundle.putInt(obtainTypedArray.getString(i), (peekValue.type < 16 || peekValue.type > 31) ? peekValue.resourceId : peekValue.data);
                }
                obtainTypedArray.recycle();
                ClockDrawableWrapper forExtras = forExtras(bundle, new IntFunction() {
                    public final Object apply(int i) {
                        return ClockDrawableWrapper.lambda$applyThemeData$0(IconProvider.ThemeData.this, i);
                    }
                });
                if (forExtras != null) {
                    this.mThemeInfo = forExtras.mAnimationInfo;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading themed clock", e);
            }
        }
    }

    static /* synthetic */ Drawable lambda$applyThemeData$0(IconProvider.ThemeData themeData, int i) {
        return new AdaptiveIconDrawable(new ColorDrawable(-1), themeData.mResources.getDrawable(i).mutate());
    }

    public Drawable getMonochrome() {
        AnimationInfo animationInfo = this.mThemeInfo;
        if (animationInfo == null) {
            return null;
        }
        Drawable mutate = animationInfo.baseDrawableState.newDrawable().mutate();
        if (!(mutate instanceof AdaptiveIconDrawable)) {
            return null;
        }
        Drawable foreground = ((AdaptiveIconDrawable) mutate).getForeground();
        this.mThemeInfo.applyTime(Calendar.getInstance(), (LayerDrawable) foreground);
        return foreground;
    }

    public static ClockDrawableWrapper forPackage(Context context, String str, int i, IconProvider.ThemeData themeData) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str, 8320);
            ClockDrawableWrapper forExtras = forExtras(applicationInfo.metaData, new IntFunction(packageManager.getResourcesForApplication(applicationInfo), i) {
                public final /* synthetic */ Resources f$0;
                public final /* synthetic */ int f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final Object apply(int i) {
                    return this.f$0.getDrawableForDensity(i, this.f$1);
                }
            });
            if (!(forExtras == null || themeData == null)) {
                forExtras.applyThemeData(themeData);
            }
            return forExtras;
        } catch (Exception e) {
            Log.d(TAG, "Unable to load clock drawable info", e);
            return null;
        }
    }

    private static ClockDrawableWrapper forExtras(Bundle bundle, IntFunction<Drawable> intFunction) {
        int i;
        if (bundle == null || (i = bundle.getInt(ROUND_ICON_METADATA_KEY, 0)) == 0) {
            return null;
        }
        Drawable mutate = intFunction.apply(i).mutate();
        if (!(mutate instanceof AdaptiveIconDrawable)) {
            return null;
        }
        AdaptiveIconDrawable adaptiveIconDrawable = (AdaptiveIconDrawable) mutate;
        ClockDrawableWrapper clockDrawableWrapper = new ClockDrawableWrapper(adaptiveIconDrawable);
        AnimationInfo animationInfo = clockDrawableWrapper.mAnimationInfo;
        animationInfo.baseDrawableState = mutate.getConstantState();
        animationInfo.hourLayerIndex = bundle.getInt(HOUR_INDEX_METADATA_KEY, -1);
        animationInfo.minuteLayerIndex = bundle.getInt(MINUTE_INDEX_METADATA_KEY, -1);
        animationInfo.secondLayerIndex = bundle.getInt(SECOND_INDEX_METADATA_KEY, -1);
        animationInfo.defaultHour = bundle.getInt(DEFAULT_HOUR_METADATA_KEY, 0);
        animationInfo.defaultMinute = bundle.getInt(DEFAULT_MINUTE_METADATA_KEY, 0);
        animationInfo.defaultSecond = bundle.getInt(DEFAULT_SECOND_METADATA_KEY, 0);
        LayerDrawable layerDrawable = (LayerDrawable) clockDrawableWrapper.getForeground();
        int numberOfLayers = layerDrawable.getNumberOfLayers();
        if (animationInfo.hourLayerIndex < 0 || animationInfo.hourLayerIndex >= numberOfLayers) {
            animationInfo.hourLayerIndex = -1;
        }
        if (animationInfo.minuteLayerIndex < 0 || animationInfo.minuteLayerIndex >= numberOfLayers) {
            animationInfo.minuteLayerIndex = -1;
        }
        if (animationInfo.secondLayerIndex < 0 || animationInfo.secondLayerIndex >= numberOfLayers) {
            animationInfo.secondLayerIndex = -1;
        } else {
            layerDrawable.setDrawable(animationInfo.secondLayerIndex, (Drawable) null);
            animationInfo.secondLayerIndex = -1;
        }
        if (IconProvider.ATLEAST_T && (adaptiveIconDrawable.getMonochrome() instanceof LayerDrawable)) {
            clockDrawableWrapper.mThemeInfo = animationInfo.copyForIcon(new AdaptiveIconDrawable(new ColorDrawable(-1), adaptiveIconDrawable.getMonochrome().mutate()));
        }
        animationInfo.applyTime(Calendar.getInstance(), layerDrawable);
        return clockDrawableWrapper;
    }

    public ClockBitmapInfo getExtendedInfo(Bitmap bitmap, int i, BaseIconFactory baseIconFactory, float f) {
        Bitmap bitmap2 = null;
        Bitmap createScaledBitmapWithShadow = baseIconFactory.createScaledBitmapWithShadow(new AdaptiveIconDrawable(getBackground().getConstantState().newDrawable(), (Drawable) null));
        AnimationInfo animationInfo = baseIconFactory.mMonoIconEnabled ? this.mThemeInfo : null;
        if (animationInfo != null) {
            bitmap2 = baseIconFactory.getWhiteShadowLayer();
        }
        return new ClockBitmapInfo(bitmap, i, f, this.mAnimationInfo, createScaledBitmapWithShadow, animationInfo, bitmap2);
    }

    public void drawForPersistence(Canvas canvas) {
        LayerDrawable layerDrawable = (LayerDrawable) getForeground();
        resetLevel(layerDrawable, this.mAnimationInfo.hourLayerIndex);
        resetLevel(layerDrawable, this.mAnimationInfo.minuteLayerIndex);
        resetLevel(layerDrawable, this.mAnimationInfo.secondLayerIndex);
        draw(canvas);
        this.mAnimationInfo.applyTime(Calendar.getInstance(), (LayerDrawable) getForeground());
    }

    private void resetLevel(LayerDrawable layerDrawable, int i) {
        if (i != -1) {
            layerDrawable.getDrawable(i).setLevel(0);
        }
    }

    private static class AnimationInfo {
        public Drawable.ConstantState baseDrawableState;
        public int defaultHour;
        public int defaultMinute;
        public int defaultSecond;
        public int hourLayerIndex;
        public int minuteLayerIndex;
        public int secondLayerIndex;

        private AnimationInfo() {
        }

        public AnimationInfo copyForIcon(Drawable drawable) {
            AnimationInfo animationInfo = new AnimationInfo();
            animationInfo.baseDrawableState = drawable.getConstantState();
            animationInfo.defaultHour = this.defaultHour;
            animationInfo.defaultMinute = this.defaultMinute;
            animationInfo.defaultSecond = this.defaultSecond;
            animationInfo.hourLayerIndex = this.hourLayerIndex;
            animationInfo.minuteLayerIndex = this.minuteLayerIndex;
            animationInfo.secondLayerIndex = this.secondLayerIndex;
            return animationInfo;
        }

        /* access modifiers changed from: package-private */
        public boolean applyTime(Calendar calendar, LayerDrawable layerDrawable) {
            calendar.setTimeInMillis(System.currentTimeMillis());
            int i = (calendar.get(10) + (12 - this.defaultHour)) % 12;
            int i2 = (calendar.get(12) + (60 - this.defaultMinute)) % 60;
            int i3 = (calendar.get(13) + (60 - this.defaultSecond)) % 60;
            int i4 = this.hourLayerIndex;
            boolean z = i4 != -1 && layerDrawable.getDrawable(i4).setLevel((i * 60) + calendar.get(12));
            int i5 = this.minuteLayerIndex;
            if (i5 != -1 && layerDrawable.getDrawable(i5).setLevel((calendar.get(10) * 60) + i2)) {
                z = true;
            }
            int i6 = this.secondLayerIndex;
            if (i6 == -1 || !layerDrawable.getDrawable(i6).setLevel(i3 * 10)) {
                return z;
            }
            return true;
        }
    }

    static class ClockBitmapInfo extends BitmapInfo {
        public final AnimationInfo animInfo;
        public final float boundsOffset;
        public final Bitmap mFlattenedBackground;
        public final Bitmap themeBackground;
        public final AnimationInfo themeData;

        public boolean canPersist() {
            return false;
        }

        ClockBitmapInfo(Bitmap bitmap, int i, float f, AnimationInfo animationInfo, Bitmap bitmap2, AnimationInfo animationInfo2, Bitmap bitmap3) {
            super(bitmap, i);
            this.boundsOffset = Math.max(0.035f, (1.0f - f) / 2.0f);
            this.animInfo = animationInfo;
            this.mFlattenedBackground = bitmap2;
            this.themeData = animationInfo2;
            this.themeBackground = bitmap3;
        }

        public FastBitmapDrawable newIcon(Context context, int i) {
            BlendModeColorFilter blendModeColorFilter;
            int i2;
            Bitmap bitmap;
            AnimationInfo animationInfo;
            if ((i & 1) == 0 || this.themeData == null) {
                animationInfo = this.animInfo;
                i2 = -1;
                bitmap = this.mFlattenedBackground;
                blendModeColorFilter = null;
            } else {
                int[] colors = ThemedIconDrawable.getColors(context);
                Drawable mutate = this.themeData.baseDrawableState.newDrawable().mutate();
                i2 = colors[1];
                mutate.setTint(colors[1]);
                animationInfo = this.themeData.copyForIcon(mutate);
                bitmap = this.themeBackground;
                blendModeColorFilter = new BlendModeColorFilter(colors[0], BlendMode.SRC_IN);
            }
            AnimationInfo animationInfo2 = animationInfo;
            Bitmap bitmap2 = bitmap;
            int i3 = i2;
            BlendModeColorFilter blendModeColorFilter2 = blendModeColorFilter;
            if (animationInfo2 == null) {
                return super.newIcon(context, i);
            }
            FastBitmapDrawable newDrawable = new ClockIconDrawable.ClockConstantState(this.icon, this.color, i3, this.boundsOffset, animationInfo2, bitmap2, blendModeColorFilter2).newDrawable();
            applyFlags(context, newDrawable, i);
            return newDrawable;
        }

        public BitmapInfo clone() {
            return copyInternalsTo(new ClockBitmapInfo(this.icon, this.color, 1.0f - (this.boundsOffset * 2.0f), this.animInfo, this.mFlattenedBackground, this.themeData, this.themeBackground));
        }
    }

    private static class ClockIconDrawable extends FastBitmapDrawable implements Runnable {
        private final AnimationInfo mAnimInfo;
        private final Bitmap mBG;
        private final ColorFilter mBgFilter;
        private final Paint mBgPaint;
        private final float mBoundsOffset;
        private final float mCanvasScale;
        private final LayerDrawable mFG;
        private final AdaptiveIconDrawable mFullDrawable;
        private final int mThemedFgColor;
        private final Calendar mTime;

        ClockIconDrawable(ClockConstantState clockConstantState) {
            super(clockConstantState.mBitmap, clockConstantState.mIconColor);
            Calendar instance = Calendar.getInstance();
            this.mTime = instance;
            Paint paint = new Paint(3);
            this.mBgPaint = paint;
            float access$100 = clockConstantState.mBoundsOffset;
            this.mBoundsOffset = access$100;
            AnimationInfo access$200 = clockConstantState.mAnimInfo;
            this.mAnimInfo = access$200;
            this.mBG = clockConstantState.mBG;
            this.mBgFilter = clockConstantState.mBgFilter;
            paint.setColorFilter(clockConstantState.mBgFilter);
            this.mThemedFgColor = clockConstantState.mThemedFgColor;
            AdaptiveIconDrawable adaptiveIconDrawable = (AdaptiveIconDrawable) access$200.baseDrawableState.newDrawable();
            this.mFullDrawable = adaptiveIconDrawable;
            LayerDrawable layerDrawable = (LayerDrawable) adaptiveIconDrawable.getForeground();
            this.mFG = layerDrawable;
            access$200.applyTime(instance, layerDrawable);
            this.mCanvasScale = 1.0f - (access$100 * 2.0f);
        }

        /* access modifiers changed from: protected */
        public void onBoundsChange(Rect rect) {
            super.onBoundsChange(rect);
            this.mFullDrawable.setBounds(0, 0, rect.width(), rect.height());
        }

        public void drawInternal(Canvas canvas, Rect rect) {
            if (this.mAnimInfo == null) {
                super.drawInternal(canvas, rect);
                return;
            }
            canvas.drawBitmap(this.mBG, (Rect) null, rect, this.mBgPaint);
            this.mAnimInfo.applyTime(this.mTime, this.mFG);
            int save = canvas.save();
            canvas.translate((float) rect.left, (float) rect.top);
            float f = this.mCanvasScale;
            canvas.scale(f, f, (float) (rect.width() / 2), (float) (rect.height() / 2));
            canvas.clipPath(this.mFullDrawable.getIconMask());
            this.mFG.draw(canvas);
            canvas.restoreToCount(save);
            reschedule();
        }

        public boolean isThemed() {
            return this.mBgPaint.getColorFilter() != null;
        }

        /* access modifiers changed from: protected */
        public void updateFilter() {
            super.updateFilter();
            int i = this.mIsDisabled ? (int) (this.mDisabledAlpha * 255.0f) : 255;
            this.mBgPaint.setAlpha(i);
            this.mFG.setAlpha(i);
            this.mBgPaint.setColorFilter(this.mIsDisabled ? getDisabledColorFilter() : this.mBgFilter);
            this.mFG.setColorFilter(this.mIsDisabled ? getDisabledColorFilter() : null);
        }

        public int getIconColor() {
            return isThemed() ? this.mThemedFgColor : super.getIconColor();
        }

        public void run() {
            if (this.mAnimInfo.applyTime(this.mTime, this.mFG)) {
                invalidateSelf();
            } else {
                reschedule();
            }
        }

        public boolean setVisible(boolean z, boolean z2) {
            boolean visible = super.setVisible(z, z2);
            if (z) {
                reschedule();
            } else {
                unscheduleSelf(this);
            }
            return visible;
        }

        private void reschedule() {
            if (isVisible()) {
                unscheduleSelf(this);
                long uptimeMillis = SystemClock.uptimeMillis();
                long j = ClockDrawableWrapper.TICK_MS;
                scheduleSelf(this, (uptimeMillis - (uptimeMillis % j)) + j);
            }
        }

        public FastBitmapDrawable.FastBitmapConstantState newConstantState() {
            return new ClockConstantState(this.mBitmap, this.mIconColor, this.mThemedFgColor, this.mBoundsOffset, this.mAnimInfo, this.mBG, this.mBgPaint.getColorFilter());
        }

        private static class ClockConstantState extends FastBitmapDrawable.FastBitmapConstantState {
            /* access modifiers changed from: private */
            public final AnimationInfo mAnimInfo;
            /* access modifiers changed from: private */
            public final Bitmap mBG;
            /* access modifiers changed from: private */
            public final ColorFilter mBgFilter;
            /* access modifiers changed from: private */
            public final float mBoundsOffset;
            /* access modifiers changed from: private */
            public final int mThemedFgColor;

            ClockConstantState(Bitmap bitmap, int i, int i2, float f, AnimationInfo animationInfo, Bitmap bitmap2, ColorFilter colorFilter) {
                super(bitmap, i);
                this.mBoundsOffset = f;
                this.mAnimInfo = animationInfo;
                this.mBG = bitmap2;
                this.mBgFilter = colorFilter;
                this.mThemedFgColor = i2;
            }

            public FastBitmapDrawable createDrawable() {
                return new ClockIconDrawable(this);
            }
        }
    }
}
