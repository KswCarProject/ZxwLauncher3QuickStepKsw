package com.android.launcher3.util;

import android.graphics.Insets;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.WindowInsets;
import android.view.WindowMetrics;
import java.util.Objects;

public class WindowBounds {
    public final Point availableSize;
    public final Rect bounds;
    public final Rect insets;
    public final int rotationHint;

    public WindowBounds(Rect rect, Rect rect2) {
        this(rect, rect2, -1);
    }

    public WindowBounds(Rect rect, Rect rect2, int i) {
        this.bounds = rect;
        this.insets = rect2;
        this.rotationHint = i;
        this.availableSize = new Point((rect.width() - rect2.left) - rect2.right, (rect.height() - rect2.top) - rect2.bottom);
    }

    public WindowBounds(int i, int i2, int i3, int i4, int i5) {
        this.bounds = new Rect(0, 0, i, i2);
        this.availableSize = new Point(i3, i4);
        this.insets = new Rect(0, 0, i - i3, i2 - i4);
        this.rotationHint = i5;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.bounds, this.insets});
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof WindowBounds)) {
            return false;
        }
        WindowBounds windowBounds = (WindowBounds) obj;
        if (!windowBounds.bounds.equals(this.bounds) || !windowBounds.insets.equals(this.insets)) {
            return false;
        }
        return true;
    }

    public String toString() {
        return "WindowBounds{bounds=" + this.bounds + ", insets=" + this.insets + ", availableSize=" + this.availableSize + '}';
    }

    public final boolean isLandscape() {
        return this.availableSize.x > this.availableSize.y;
    }

    public static WindowBounds fromWindowMetrics(WindowMetrics windowMetrics) {
        Insets insets2 = windowMetrics.getWindowInsets().getInsets(WindowInsets.Type.systemBars());
        return new WindowBounds(windowMetrics.getBounds(), new Rect(insets2.left, insets2.top, insets2.right, insets2.bottom));
    }
}
