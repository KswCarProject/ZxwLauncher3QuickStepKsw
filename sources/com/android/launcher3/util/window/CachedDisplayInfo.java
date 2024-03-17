package com.android.launcher3.util.window;

import android.graphics.Point;
import android.graphics.Rect;
import com.android.launcher3.util.RotationUtils;
import java.util.Objects;

public class CachedDisplayInfo {
    public final Rect cutout;
    public final String id;
    public final int rotation;
    public final Point size;

    public CachedDisplayInfo() {
        this(new Point(0, 0), 0);
    }

    public CachedDisplayInfo(Point point, int i) {
        this("", point, i, new Rect());
    }

    public CachedDisplayInfo(String str, Point point, int i, Rect rect) {
        this.id = str;
        this.size = point;
        this.rotation = i;
        this.cutout = rect;
    }

    public CachedDisplayInfo normalize() {
        if (this.rotation == 0) {
            return this;
        }
        Point point = new Point(this.size);
        RotationUtils.rotateSize(point, RotationUtils.deltaRotation(this.rotation, 0));
        Rect rect = new Rect(this.cutout);
        RotationUtils.rotateRect(rect, RotationUtils.deltaRotation(this.rotation, 0));
        return new CachedDisplayInfo(this.id, point, 0, rect);
    }

    public String toString() {
        return "CachedDisplayInfo{id='" + this.id + '\'' + ", size=" + this.size + ", rotation=" + this.rotation + ", cutout=" + this.cutout + '}';
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CachedDisplayInfo)) {
            return false;
        }
        CachedDisplayInfo cachedDisplayInfo = (CachedDisplayInfo) obj;
        if (this.rotation != cachedDisplayInfo.rotation || !Objects.equals(this.id, cachedDisplayInfo.id) || !Objects.equals(this.size, cachedDisplayInfo.size) || !Objects.equals(this.cutout, cachedDisplayInfo.cutout)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.id, this.size, Integer.valueOf(this.rotation), this.cutout});
    }
}
