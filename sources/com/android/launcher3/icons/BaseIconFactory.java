package com.android.launcher3.icons;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.UserHandle;
import android.util.SparseBooleanArray;
import androidx.core.view.ViewCompat;
import com.android.launcher3.icons.BitmapInfo;
import com.android.launcher3.util.FlagOp;
import com.android.systemui.shared.system.SysUiStatsLog;

public class BaseIconFactory implements AutoCloseable {
    private static final int DEFAULT_WRAPPER_BACKGROUND = -1;
    private static final float ICON_BADGE_SCALE = 0.444f;
    private static int PLACEHOLDER_BACKGROUND_COLOR = Color.rgb(SysUiStatsLog.NOTIFICATION_PANEL_REPORTED, SysUiStatsLog.NOTIFICATION_PANEL_REPORTED, SysUiStatsLog.NOTIFICATION_PANEL_REPORTED);
    private static final float PLACEHOLDER_TEXT_SIZE = 20.0f;
    private final Canvas mCanvas;
    private final ColorExtractor mColorExtractor;
    protected final Context mContext;
    private boolean mDisableColorExtractor;
    protected final int mFillResIconDpi;
    protected final int mIconBitmapSize;
    private final SparseBooleanArray mIsUserBadged;
    protected boolean mMonoIconEnabled;
    private IconNormalizer mNormalizer;
    private final Rect mOldBounds;
    private final PackageManager mPm;
    private ShadowGenerator mShadowGenerator;
    private final boolean mShapeDetection;
    private final Paint mTextPaint;
    private Bitmap mWhiteShadowLayer;
    private int mWrapperBackgroundColor;
    private Drawable mWrapperIcon;

    public static int getBadgeSizeForIconSize(int i) {
        return (int) (((float) i) * ICON_BADGE_SCALE);
    }

    protected BaseIconFactory(Context context, int i, int i2, boolean z) {
        this.mOldBounds = new Rect();
        this.mIsUserBadged = new SparseBooleanArray();
        this.mWrapperBackgroundColor = -1;
        Paint paint = new Paint(3);
        this.mTextPaint = paint;
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext;
        this.mShapeDetection = z;
        this.mFillResIconDpi = i;
        this.mIconBitmapSize = i2;
        this.mPm = applicationContext.getPackageManager();
        this.mColorExtractor = new ColorExtractor();
        Canvas canvas = new Canvas();
        this.mCanvas = canvas;
        canvas.setDrawFilter(new PaintFlagsDrawFilter(4, 2));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(PLACEHOLDER_BACKGROUND_COLOR);
        paint.setTextSize(context.getResources().getDisplayMetrics().density * PLACEHOLDER_TEXT_SIZE);
        clear();
    }

    public BaseIconFactory(Context context, int i, int i2) {
        this(context, i, i2, false);
    }

    /* access modifiers changed from: protected */
    public void clear() {
        this.mWrapperBackgroundColor = -1;
        this.mDisableColorExtractor = false;
    }

    public ShadowGenerator getShadowGenerator() {
        if (this.mShadowGenerator == null) {
            this.mShadowGenerator = new ShadowGenerator(this.mIconBitmapSize);
        }
        return this.mShadowGenerator;
    }

    public IconNormalizer getNormalizer() {
        if (this.mNormalizer == null) {
            this.mNormalizer = new IconNormalizer(this.mContext, this.mIconBitmapSize, this.mShapeDetection);
        }
        return this.mNormalizer;
    }

    public BitmapInfo createIconBitmap(Intent.ShortcutIconResource shortcutIconResource) {
        try {
            Resources resourcesForApplication = this.mPm.getResourcesForApplication(shortcutIconResource.packageName);
            if (resourcesForApplication != null) {
                return createBadgedIconBitmap(resourcesForApplication.getDrawableForDensity(resourcesForApplication.getIdentifier(shortcutIconResource.resourceName, (String) null, (String) null), this.mFillResIconDpi));
            }
        } catch (Exception unused) {
        }
        return null;
    }

    public BitmapInfo createIconBitmap(String str, int i) {
        int i2 = this.mIconBitmapSize;
        Bitmap createBitmap = Bitmap.createBitmap(i2, i2, Bitmap.Config.ARGB_8888);
        this.mTextPaint.setColor(i);
        Canvas canvas = new Canvas(createBitmap);
        int i3 = this.mIconBitmapSize;
        canvas.drawText(str, (float) (i3 / 2), (float) ((i3 * 5) / 8), this.mTextPaint);
        Bitmap createIconBitmap = createIconBitmap((Drawable) new AdaptiveIconDrawable(new ColorDrawable(PLACEHOLDER_BACKGROUND_COLOR), new BitmapDrawable(this.mContext.getResources(), createBitmap)), 0.92f);
        return BitmapInfo.of(createIconBitmap, extractColor(createIconBitmap));
    }

    public BitmapInfo createIconBitmap(Bitmap bitmap) {
        if (!(this.mIconBitmapSize == bitmap.getWidth() && this.mIconBitmapSize == bitmap.getHeight())) {
            bitmap = createIconBitmap((Drawable) new BitmapDrawable(this.mContext.getResources(), bitmap), 1.0f);
        }
        return BitmapInfo.of(bitmap, extractColor(bitmap));
    }

    public BitmapInfo createShapedIconBitmap(Bitmap bitmap, IconOptions iconOptions) {
        FixedSizeBitmapDrawable fixedSizeBitmapDrawable = new FixedSizeBitmapDrawable(bitmap);
        float extraInsetFraction = AdaptiveIconDrawable.getExtraInsetFraction();
        float f = extraInsetFraction / ((2.0f * extraInsetFraction) + 1.0f);
        return createBadgedIconBitmap(new AdaptiveIconDrawable(new ColorDrawable(ViewCompat.MEASURED_STATE_MASK), new InsetDrawable(fixedSizeBitmapDrawable, f, f, f, f)), iconOptions);
    }

    public BitmapInfo createBadgedIconBitmap(Drawable drawable) {
        return createBadgedIconBitmap(drawable, (IconOptions) null);
    }

    public BitmapInfo createBadgedIconBitmap(Drawable drawable, IconOptions iconOptions) {
        Drawable monochrome;
        float[] fArr = new float[1];
        Drawable normalizeAndWrapToAdaptiveIcon = normalizeAndWrapToAdaptiveIcon(drawable, iconOptions == null || iconOptions.mShrinkNonAdaptiveIcons, (RectF) null, fArr);
        Bitmap createIconBitmap = createIconBitmap(normalizeAndWrapToAdaptiveIcon, fArr[0]);
        boolean z = normalizeAndWrapToAdaptiveIcon instanceof AdaptiveIconDrawable;
        if (z) {
            this.mCanvas.setBitmap(createIconBitmap);
            getShadowGenerator().recreateIcon(Bitmap.createBitmap(createIconBitmap), this.mCanvas);
            this.mCanvas.setBitmap((Bitmap) null);
        }
        int extractColor = extractColor(createIconBitmap);
        BitmapInfo of = BitmapInfo.of(createIconBitmap, extractColor);
        if (normalizeAndWrapToAdaptiveIcon instanceof BitmapInfo.Extender) {
            of = ((BitmapInfo.Extender) normalizeAndWrapToAdaptiveIcon).getExtendedInfo(createIconBitmap, extractColor, this, fArr[0]);
        } else if (this.mMonoIconEnabled && IconProvider.ATLEAST_T && z && (monochrome = ((AdaptiveIconDrawable) normalizeAndWrapToAdaptiveIcon).getMonochrome()) != null) {
            of.setMonoIcon(createIconBitmap(new ClippedMonoDrawable(monochrome), fArr[0], this.mIconBitmapSize, Bitmap.Config.ALPHA_8), this);
        }
        return of.withFlags(getBitmapFlagOp(iconOptions));
    }

    public FlagOp getBitmapFlagOp(IconOptions iconOptions) {
        boolean z;
        FlagOp flagOp = FlagOp.NO_OP;
        if (iconOptions == null) {
            return flagOp;
        }
        if (iconOptions.mIsInstantApp) {
            flagOp = flagOp.addFlag(2);
        }
        if (iconOptions.mUserHandle == null) {
            return flagOp;
        }
        int hashCode = iconOptions.mUserHandle.hashCode();
        int indexOfKey = this.mIsUserBadged.indexOfKey(hashCode);
        if (indexOfKey >= 0) {
            z = this.mIsUserBadged.valueAt(indexOfKey);
        } else {
            NoopDrawable noopDrawable = new NoopDrawable();
            z = noopDrawable != this.mPm.getUserBadgedIcon(noopDrawable, iconOptions.mUserHandle);
            this.mIsUserBadged.put(hashCode, z);
        }
        return flagOp.setFlag(1, z);
    }

    /* access modifiers changed from: package-private */
    public Bitmap getWhiteShadowLayer() {
        if (this.mWhiteShadowLayer == null) {
            this.mWhiteShadowLayer = createScaledBitmapWithShadow(new AdaptiveIconDrawable(new ColorDrawable(-1), (Drawable) null));
        }
        return this.mWhiteShadowLayer;
    }

    public Bitmap createScaledBitmapWithShadow(Drawable drawable) {
        Bitmap createIconBitmap = createIconBitmap(drawable, getNormalizer().getScale(drawable, (RectF) null, (Path) null, (boolean[]) null));
        this.mCanvas.setBitmap(createIconBitmap);
        getShadowGenerator().recreateIcon(Bitmap.createBitmap(createIconBitmap), this.mCanvas);
        this.mCanvas.setBitmap((Bitmap) null);
        return createIconBitmap;
    }

    public Bitmap createScaledBitmapWithoutShadow(Drawable drawable) {
        RectF rectF = new RectF();
        float[] fArr = new float[1];
        return createIconBitmap(normalizeAndWrapToAdaptiveIcon(drawable, true, rectF, fArr), Math.min(fArr[0], ShadowGenerator.getScaleForBounds(rectF)));
    }

    public void setWrapperBackgroundColor(int i) {
        if (Color.alpha(i) < 255) {
            i = -1;
        }
        this.mWrapperBackgroundColor = i;
    }

    public void disableColorExtraction() {
        this.mDisableColorExtractor = true;
    }

    private Drawable normalizeAndWrapToAdaptiveIcon(Drawable drawable, boolean z, RectF rectF, float[] fArr) {
        float f;
        if (drawable == null) {
            return null;
        }
        if (!z || (drawable instanceof AdaptiveIconDrawable)) {
            f = getNormalizer().getScale(drawable, rectF, (Path) null, (boolean[]) null);
        } else {
            if (this.mWrapperIcon == null) {
                this.mWrapperIcon = this.mContext.getDrawable(R.drawable.adaptive_icon_drawable_wrapper).mutate();
            }
            AdaptiveIconDrawable adaptiveIconDrawable = (AdaptiveIconDrawable) this.mWrapperIcon;
            adaptiveIconDrawable.setBounds(0, 0, 1, 1);
            boolean[] zArr = new boolean[1];
            f = getNormalizer().getScale(drawable, rectF, adaptiveIconDrawable.getIconMask(), zArr);
            if (!zArr[0]) {
                FixedScaleDrawable fixedScaleDrawable = (FixedScaleDrawable) adaptiveIconDrawable.getForeground();
                fixedScaleDrawable.setDrawable(drawable);
                fixedScaleDrawable.setScale(f);
                f = getNormalizer().getScale(adaptiveIconDrawable, rectF, (Path) null, (boolean[]) null);
                ((ColorDrawable) adaptiveIconDrawable.getBackground()).setColor(this.mWrapperBackgroundColor);
                drawable = adaptiveIconDrawable;
            }
        }
        fArr[0] = f;
        return drawable;
    }

    private Bitmap createIconBitmap(Drawable drawable, float f) {
        return createIconBitmap(drawable, f, this.mIconBitmapSize);
    }

    public Bitmap createIconBitmap(Drawable drawable, float f, int i) {
        return createIconBitmap(drawable, f, i, Bitmap.Config.ARGB_8888);
    }

    private Bitmap createIconBitmap(Drawable drawable, float f, int i, Bitmap.Config config) {
        int i2;
        int i3;
        Bitmap createBitmap = Bitmap.createBitmap(i, i, config);
        if (drawable == null) {
            return createBitmap;
        }
        this.mCanvas.setBitmap(createBitmap);
        this.mOldBounds.set(drawable.getBounds());
        if (drawable instanceof AdaptiveIconDrawable) {
            float f2 = (float) i;
            int max = Math.max((int) Math.ceil((double) (0.035f * f2)), Math.round((f2 * (1.0f - f)) / 2.0f));
            int i4 = (i - max) - max;
            drawable.setBounds(0, 0, i4, i4);
            int save = this.mCanvas.save();
            float f3 = (float) max;
            this.mCanvas.translate(f3, f3);
            if (drawable instanceof BitmapInfo.Extender) {
                ((BitmapInfo.Extender) drawable).drawForPersistence(this.mCanvas);
            } else {
                drawable.draw(this.mCanvas);
            }
            this.mCanvas.restoreToCount(save);
        } else {
            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                if (createBitmap != null && bitmap.getDensity() == 0) {
                    bitmapDrawable.setTargetDensity(this.mContext.getResources().getDisplayMetrics());
                }
            }
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();
            if (intrinsicWidth > 0 && intrinsicHeight > 0) {
                float f4 = ((float) intrinsicWidth) / ((float) intrinsicHeight);
                if (intrinsicWidth > intrinsicHeight) {
                    i2 = (int) (((float) i) / f4);
                    i3 = i;
                } else if (intrinsicHeight > intrinsicWidth) {
                    i3 = (int) (((float) i) * f4);
                    i2 = i;
                }
                int i5 = (i - i3) / 2;
                int i6 = (i - i2) / 2;
                drawable.setBounds(i5, i6, i3 + i5, i2 + i6);
                this.mCanvas.save();
                float f5 = (float) (i / 2);
                this.mCanvas.scale(f, f, f5, f5);
                drawable.draw(this.mCanvas);
                this.mCanvas.restore();
            }
            i3 = i;
            i2 = i3;
            int i52 = (i - i3) / 2;
            int i62 = (i - i2) / 2;
            drawable.setBounds(i52, i62, i3 + i52, i2 + i62);
            this.mCanvas.save();
            float f52 = (float) (i / 2);
            this.mCanvas.scale(f, f, f52, f52);
            drawable.draw(this.mCanvas);
            this.mCanvas.restore();
        }
        drawable.setBounds(this.mOldBounds);
        this.mCanvas.setBitmap((Bitmap) null);
        return createBitmap;
    }

    public void close() {
        clear();
    }

    public BitmapInfo makeDefaultIcon() {
        return createBadgedIconBitmap(getFullResDefaultActivityIcon(this.mFillResIconDpi));
    }

    public static Drawable getFullResDefaultActivityIcon(int i) {
        return Resources.getSystem().getDrawableForDensity(17301651, i);
    }

    private int extractColor(Bitmap bitmap) {
        if (this.mDisableColorExtractor) {
            return 0;
        }
        return this.mColorExtractor.findDominantColorByHue(bitmap);
    }

    public static class IconOptions {
        boolean mIsInstantApp;
        boolean mShrinkNonAdaptiveIcons = true;
        UserHandle mUserHandle;

        public IconOptions setShrinkNonAdaptiveIcons(boolean z) {
            this.mShrinkNonAdaptiveIcons = z;
            return this;
        }

        public IconOptions setUser(UserHandle userHandle) {
            this.mUserHandle = userHandle;
            return this;
        }

        public IconOptions setInstantApp(boolean z) {
            this.mIsInstantApp = z;
            return this;
        }
    }

    private static class FixedSizeBitmapDrawable extends BitmapDrawable {
        public FixedSizeBitmapDrawable(Bitmap bitmap) {
            super((Resources) null, bitmap);
        }

        public int getIntrinsicHeight() {
            return getBitmap().getWidth();
        }

        public int getIntrinsicWidth() {
            return getBitmap().getWidth();
        }
    }

    private static class NoopDrawable extends ColorDrawable {
        public int getIntrinsicHeight() {
            return 1;
        }

        public int getIntrinsicWidth() {
            return 1;
        }

        private NoopDrawable() {
        }
    }

    private static class ClippedMonoDrawable extends InsetDrawable {
        private final AdaptiveIconDrawable mCrop = new AdaptiveIconDrawable(new ColorDrawable(ViewCompat.MEASURED_STATE_MASK), (Drawable) null);

        public ClippedMonoDrawable(Drawable drawable) {
            super(drawable, -AdaptiveIconDrawable.getExtraInsetFraction());
        }

        public void draw(Canvas canvas) {
            this.mCrop.setBounds(getBounds());
            int save = canvas.save();
            canvas.clipPath(this.mCrop.getIconMask());
            super.draw(canvas);
            canvas.restoreToCount(save);
        }
    }
}
