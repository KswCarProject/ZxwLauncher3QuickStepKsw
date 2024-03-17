package com.android.launcher3.util;

import android.app.WallpaperColors;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.icons.GraphicsUtils;

public class Themes {
    public static final String KEY_THEMED_ICONS = "themed_icons";

    public static int getActivityThemeRes(Context context) {
        WallpaperColors wallpaperColors;
        int i = 0;
        if (Utilities.ATLEAST_P && (wallpaperColors = ((WallpaperManager) context.getSystemService(WallpaperManager.class)).getWallpaperColors(1)) != null) {
            i = wallpaperColors.getColorHints();
        }
        return getActivityThemeRes(context, i);
    }

    public static int getActivityThemeRes(Context context, int i) {
        boolean z = false;
        boolean z2 = Utilities.ATLEAST_S && (i & 1) != 0;
        if (Utilities.ATLEAST_S && (i & 2) != 0) {
            z = true;
        }
        if (Utilities.isDarkTheme(context)) {
            if (z2) {
                return R.style.AppTheme_Dark_DarkText;
            }
            return z ? R.style.AppTheme_Dark_DarkMainColor : R.style.AppTheme_Dark;
        } else if (z2) {
            return R.style.AppTheme_DarkText;
        } else {
            return z ? R.style.AppTheme_DarkMainColor : R.style.AppTheme;
        }
    }

    public static boolean isThemedIconEnabled(Context context) {
        if (!FeatureFlags.ENABLE_THEMED_ICONS.get() || !Utilities.getPrefs(context).getBoolean(KEY_THEMED_ICONS, false)) {
            return false;
        }
        return true;
    }

    public static String getDefaultBodyFont(Context context) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(16974253, new int[]{16843692});
        String string = obtainStyledAttributes.getString(0);
        obtainStyledAttributes.recycle();
        return string;
    }

    public static float getDialogCornerRadius(Context context) {
        return getDimension(context, 16844145, context.getResources().getDimension(R.dimen.default_dialog_corner_radius));
    }

    public static float getDimension(Context context, int i, float f) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{i});
        float dimension = obtainStyledAttributes.getDimension(0, f);
        obtainStyledAttributes.recycle();
        return dimension;
    }

    public static int getColorAccent(Context context) {
        return getAttrColor(context, 16843829);
    }

    public static int getColorBackground(Context context) {
        return getAttrColor(context, 16842801);
    }

    public static int getColorBackgroundFloating(Context context) {
        return getAttrColor(context, 16844002);
    }

    public static int getAttrColor(Context context, int i) {
        return GraphicsUtils.getAttrColor(context, i);
    }

    public static boolean getAttrBoolean(Context context, int i) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{i});
        boolean z = obtainStyledAttributes.getBoolean(0, false);
        obtainStyledAttributes.recycle();
        return z;
    }

    public static Drawable getAttrDrawable(Context context, int i) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{i});
        Drawable drawable = obtainStyledAttributes.getDrawable(0);
        obtainStyledAttributes.recycle();
        return drawable;
    }

    public static int getAttrInteger(Context context, int i) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{i});
        int integer = obtainStyledAttributes.getInteger(0, 0);
        obtainStyledAttributes.recycle();
        return integer;
    }

    public static void setColorScaleOnMatrix(int i, ColorMatrix colorMatrix) {
        colorMatrix.setScale(((float) Color.red(i)) / 255.0f, ((float) Color.green(i)) / 255.0f, ((float) Color.blue(i)) / 255.0f, ((float) Color.alpha(i)) / 255.0f);
    }

    public static void setColorChangeOnMatrix(int i, int i2, ColorMatrix colorMatrix) {
        colorMatrix.reset();
        colorMatrix.getArray()[4] = (float) (Color.red(i2) - Color.red(i));
        colorMatrix.getArray()[9] = (float) (Color.green(i2) - Color.green(i));
        colorMatrix.getArray()[14] = (float) (Color.blue(i2) - Color.blue(i));
        colorMatrix.getArray()[19] = (float) (Color.alpha(i2) - Color.alpha(i));
    }

    public static SparseArray<TypedValue> createValueMap(Context context, AttributeSet attributeSet, IntArray intArray) {
        int attributeCount = attributeSet.getAttributeCount();
        IntArray intArray2 = new IntArray(attributeCount);
        for (int i = 0; i < attributeCount; i++) {
            intArray2.add(attributeSet.getAttributeNameResource(i));
        }
        intArray2.removeAllValues(intArray);
        int[] array = intArray2.toArray();
        SparseArray<TypedValue> sparseArray = new SparseArray<>(array.length);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, array);
        for (int i2 = 0; i2 < array.length; i2++) {
            TypedValue typedValue = new TypedValue();
            obtainStyledAttributes.getValue(i2, typedValue);
            sparseArray.put(array[i2], typedValue);
        }
        return sparseArray;
    }
}
