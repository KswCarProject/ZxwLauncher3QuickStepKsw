package com.android.launcher3.util;

import android.graphics.Point;
import android.graphics.Rect;

public class RotationUtils {
    public static int deltaRotation(int i, int i2) {
        int i3 = i2 - i;
        return i3 < 0 ? i3 + 4 : i3;
    }

    public static void rotateRect(Rect rect, int i) {
        if (i == 0) {
            return;
        }
        if (i == 1) {
            rect.set(rect.top, rect.right, rect.bottom, rect.left);
        } else if (i == 2) {
            rect.set(rect.right, rect.bottom, rect.left, rect.top);
        } else if (i == 3) {
            rect.set(rect.bottom, rect.left, rect.top, rect.right);
        } else {
            throw new IllegalArgumentException("unknown rotation: " + i);
        }
    }

    public static void rotateSize(Point point, int i) {
        if (i != 0) {
            if (i != 1) {
                if (i == 2) {
                    return;
                }
                if (i != 3) {
                    throw new IllegalArgumentException("unknown rotation: " + i);
                }
            }
            point.set(point.y, point.x);
        }
    }
}
