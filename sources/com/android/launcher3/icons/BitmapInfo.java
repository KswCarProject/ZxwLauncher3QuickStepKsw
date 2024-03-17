package com.android.launcher3.icons;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import com.android.launcher3.util.FlagOp;

public class BitmapInfo {
    static final int FLAG_INSTANT = 2;
    public static final int FLAG_NO_BADGE = 2;
    public static final int FLAG_THEMED = 1;
    static final int FLAG_WORK = 1;
    public static final Bitmap LOW_RES_ICON;
    public static final BitmapInfo LOW_RES_INFO;
    public static final String TAG = "BitmapInfo";
    private BitmapInfo badgeInfo;
    public final int color;
    public int flags;
    public final Bitmap icon;
    protected Bitmap mMono;
    protected Bitmap mWhiteShadowLayer;

    @interface BitmapInfoFlags {
    }

    public @interface DrawableCreationFlags {
    }

    public interface Extender {
        void drawForPersistence(Canvas canvas);

        BitmapInfo getExtendedInfo(Bitmap bitmap, int i, BaseIconFactory baseIconFactory, float f);
    }

    static {
        Bitmap createBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8);
        LOW_RES_ICON = createBitmap;
        LOW_RES_INFO = fromBitmap(createBitmap);
    }

    public BitmapInfo(Bitmap bitmap, int i) {
        this.icon = bitmap;
        this.color = i;
    }

    public BitmapInfo withBadgeInfo(BitmapInfo bitmapInfo) {
        BitmapInfo clone = clone();
        clone.badgeInfo = bitmapInfo;
        return clone;
    }

    public BitmapInfo withFlags(FlagOp flagOp) {
        if (flagOp == FlagOp.NO_OP) {
            return this;
        }
        BitmapInfo clone = clone();
        clone.flags = flagOp.apply(clone.flags);
        return clone;
    }

    /* access modifiers changed from: protected */
    public BitmapInfo copyInternalsTo(BitmapInfo bitmapInfo) {
        bitmapInfo.mMono = this.mMono;
        bitmapInfo.mWhiteShadowLayer = this.mWhiteShadowLayer;
        bitmapInfo.flags = this.flags;
        bitmapInfo.badgeInfo = this.badgeInfo;
        return bitmapInfo;
    }

    public BitmapInfo clone() {
        return copyInternalsTo(new BitmapInfo(this.icon, this.color));
    }

    public void setMonoIcon(Bitmap bitmap, BaseIconFactory baseIconFactory) {
        this.mMono = bitmap;
        this.mWhiteShadowLayer = baseIconFactory.getWhiteShadowLayer();
    }

    public final boolean isNullOrLowRes() {
        Bitmap bitmap = this.icon;
        return bitmap == null || bitmap == LOW_RES_ICON;
    }

    public final boolean isLowRes() {
        return LOW_RES_ICON == this.icon;
    }

    public boolean canPersist() {
        return !isNullOrLowRes();
    }

    public Bitmap getMono() {
        return this.mMono;
    }

    public FastBitmapDrawable newIcon(Context context) {
        return newIcon(context, 0);
    }

    public FastBitmapDrawable newIcon(Context context, int i) {
        FastBitmapDrawable fastBitmapDrawable;
        if (isLowRes()) {
            fastBitmapDrawable = new PlaceHolderIconDrawable(this, context);
        } else if ((i & 1) == 0 || this.mMono == null) {
            fastBitmapDrawable = new FastBitmapDrawable(this);
        } else {
            fastBitmapDrawable = ThemedIconDrawable.newDrawable(this, context);
        }
        applyFlags(context, fastBitmapDrawable, i);
        return fastBitmapDrawable;
    }

    /* access modifiers changed from: protected */
    public void applyFlags(Context context, FastBitmapDrawable fastBitmapDrawable, int i) {
        fastBitmapDrawable.mDisabledAlpha = GraphicsUtils.getFloat(context, R.attr.disabledIconAlpha, 1.0f);
        if ((i & 2) == 0) {
            BitmapInfo bitmapInfo = this.badgeInfo;
            if (bitmapInfo != null) {
                fastBitmapDrawable.setBadge(bitmapInfo.newIcon(context, i));
                return;
            }
            int i2 = this.flags;
            if ((i2 & 2) != 0) {
                fastBitmapDrawable.setBadge(context.getDrawable(R.drawable.ic_instant_app_badge));
            } else if ((i2 & 1) != 0) {
                fastBitmapDrawable.setBadge(context.getDrawable(R.drawable.ic_work_app_badge));
            }
        }
    }

    public static BitmapInfo fromBitmap(Bitmap bitmap) {
        return of(bitmap, 0);
    }

    public static BitmapInfo of(Bitmap bitmap, int i) {
        return new BitmapInfo(bitmap, i);
    }
}
