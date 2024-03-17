package com.android.launcher3.widget;

import android.app.WallpaperColors;
import android.content.Context;
import android.graphics.Rect;
import android.util.SparseIntArray;
import android.view.View;
import com.android.launcher3.R;
import com.android.launcher3.util.ResourceBasedOverride;

public class LocalColorExtractor implements ResourceBasedOverride {

    public interface Listener {
        void onColorsChanged(SparseIntArray sparseIntArray);
    }

    public void applyColorsOverride(Context context, WallpaperColors wallpaperColors) {
    }

    public SparseIntArray generateColorsOverride(WallpaperColors wallpaperColors) {
        return null;
    }

    public void setListener(Listener listener) {
    }

    public void setWorkspaceLocation(Rect rect, View view, int i) {
    }

    public static LocalColorExtractor newInstance(Context context) {
        return (LocalColorExtractor) ResourceBasedOverride.Overrides.getObject(LocalColorExtractor.class, context.getApplicationContext(), R.string.local_colors_extraction_class);
    }
}
