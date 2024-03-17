package androidx.core.graphics;

import android.graphics.BlendMode;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import androidx.core.util.Pair;

public final class PaintCompat {
    private static final String EM_STRING = "m";
    private static final String TOFU_STRING = "󟿽";
    private static final ThreadLocal<Pair<Rect, Rect>> sRectThreadLocal = new ThreadLocal<>();

    public static boolean hasGlyph(Paint paint, String str) {
        if (Build.VERSION.SDK_INT >= 23) {
            return Api23Impl.hasGlyph(paint, str);
        }
        int length = str.length();
        if (length == 1 && Character.isWhitespace(str.charAt(0))) {
            return true;
        }
        float measureText = paint.measureText(TOFU_STRING);
        float measureText2 = paint.measureText(EM_STRING);
        float measureText3 = paint.measureText(str);
        float f = 0.0f;
        if (measureText3 == 0.0f) {
            return false;
        }
        if (str.codePointCount(0, str.length()) > 1) {
            if (measureText3 > measureText2 * 2.0f) {
                return false;
            }
            int i = 0;
            while (i < length) {
                int charCount = Character.charCount(str.codePointAt(i)) + i;
                f += paint.measureText(str, i, charCount);
                i = charCount;
            }
            if (measureText3 >= f) {
                return false;
            }
        }
        if (measureText3 != measureText) {
            return true;
        }
        Pair<Rect, Rect> obtainEmptyRects = obtainEmptyRects();
        paint.getTextBounds(TOFU_STRING, 0, 2, (Rect) obtainEmptyRects.first);
        paint.getTextBounds(str, 0, length, (Rect) obtainEmptyRects.second);
        return !((Rect) obtainEmptyRects.first).equals(obtainEmptyRects.second);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v0, resolved type: android.graphics.PorterDuffXfermode} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v1, resolved type: android.graphics.PorterDuffXfermode} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v3, resolved type: android.graphics.PorterDuffXfermode} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v4, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v5, resolved type: android.graphics.PorterDuffXfermode} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v6, resolved type: android.graphics.PorterDuffXfermode} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean setBlendMode(android.graphics.Paint r4, androidx.core.graphics.BlendModeCompat r5) {
        /*
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 1
            r2 = 0
            r3 = 29
            if (r0 < r3) goto L_0x0012
            if (r5 == 0) goto L_0x000e
            java.lang.Object r2 = androidx.core.graphics.BlendModeUtils.Api29Impl.obtainBlendModeFromCompat(r5)
        L_0x000e:
            androidx.core.graphics.PaintCompat.Api29Impl.setBlendMode(r4, r2)
            return r1
        L_0x0012:
            if (r5 == 0) goto L_0x0027
            android.graphics.PorterDuff$Mode r5 = androidx.core.graphics.BlendModeUtils.obtainPorterDuffFromCompat(r5)
            if (r5 == 0) goto L_0x001f
            android.graphics.PorterDuffXfermode r2 = new android.graphics.PorterDuffXfermode
            r2.<init>(r5)
        L_0x001f:
            r4.setXfermode(r2)
            if (r5 == 0) goto L_0x0025
            goto L_0x0026
        L_0x0025:
            r1 = 0
        L_0x0026:
            return r1
        L_0x0027:
            r4.setXfermode(r2)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.core.graphics.PaintCompat.setBlendMode(android.graphics.Paint, androidx.core.graphics.BlendModeCompat):boolean");
    }

    private static Pair<Rect, Rect> obtainEmptyRects() {
        ThreadLocal<Pair<Rect, Rect>> threadLocal = sRectThreadLocal;
        Pair<Rect, Rect> pair = threadLocal.get();
        if (pair == null) {
            Pair<Rect, Rect> pair2 = new Pair<>(new Rect(), new Rect());
            threadLocal.set(pair2);
            return pair2;
        }
        ((Rect) pair.first).setEmpty();
        ((Rect) pair.second).setEmpty();
        return pair;
    }

    private PaintCompat() {
    }

    static class Api29Impl {
        private Api29Impl() {
        }

        static void setBlendMode(Paint paint, Object obj) {
            paint.setBlendMode((BlendMode) obj);
        }
    }

    static class Api23Impl {
        private Api23Impl() {
        }

        static boolean hasGlyph(Paint paint, String str) {
            return paint.hasGlyph(str);
        }
    }
}
