package com.android.launcher3.graphics;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.FloatArrayEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.Xml;
import android.view.View;
import android.view.ViewOutlineProvider;
import androidx.core.view.ViewCompat;
import com.android.launcher3.R;
import com.android.launcher3.anim.RoundedRectRevealOutlineProvider;
import com.android.launcher3.graphics.IconShape;
import com.android.launcher3.icons.GraphicsUtils;
import com.android.launcher3.icons.IconNormalizer;
import com.android.launcher3.views.ClipPathView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

public abstract class IconShape {
    private static IconShape sInstance = new Circle();
    private static float sNormalizationScale = 0.92f;

    public abstract void addToPath(Path path, float f, float f2, float f3);

    public abstract <T extends View & ClipPathView> Animator createRevealAnimator(T t, Rect rect, Rect rect2, float f, boolean z);

    public abstract void drawShape(Canvas canvas, float f, float f2, float f3, Paint paint);

    public boolean enableShapeDetection() {
        return false;
    }

    public static IconShape getShape() {
        return sInstance;
    }

    public static float getNormalizationScale() {
        return sNormalizationScale;
    }

    private static abstract class SimpleRectShape extends IconShape {
        /* access modifiers changed from: protected */
        public abstract float getStartRadius(Rect rect);

        private SimpleRectShape() {
        }

        public final <T extends View & ClipPathView> Animator createRevealAnimator(T t, Rect rect, Rect rect2, float f, boolean z) {
            return new RoundedRectRevealOutlineProvider(getStartRadius(rect), f, rect, rect2) {
                public boolean shouldRemoveElevationDuringAnimation() {
                    return true;
                }
            }.createRevealAnimator(t, z);
        }
    }

    private static abstract class PathShape extends IconShape {
        private final Path mTmpPath;

        /* access modifiers changed from: protected */
        public abstract ValueAnimator.AnimatorUpdateListener newUpdateListener(Rect rect, Rect rect2, float f, Path path);

        private PathShape() {
            this.mTmpPath = new Path();
        }

        public final void drawShape(Canvas canvas, float f, float f2, float f3, Paint paint) {
            this.mTmpPath.reset();
            addToPath(this.mTmpPath, f, f2, f3);
            canvas.drawPath(this.mTmpPath, paint);
        }

        public final <T extends View & ClipPathView> Animator createRevealAnimator(final T t, Rect rect, Rect rect2, float f, boolean z) {
            ValueAnimator valueAnimator;
            Path path = new Path();
            ValueAnimator.AnimatorUpdateListener newUpdateListener = newUpdateListener(rect, rect2, f, path);
            float[] fArr = {0.0f, 1.0f};
            if (z) {
                // fill-array-data instruction
                fArr[0] = 1065353216;
                fArr[1] = 0;
                valueAnimator = ValueAnimator.ofFloat(fArr);
            } else {
                valueAnimator = ValueAnimator.ofFloat(fArr);
            }
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                private ViewOutlineProvider mOldOutlineProvider;

                public void onAnimationStart(Animator animator) {
                    this.mOldOutlineProvider = t.getOutlineProvider();
                    t.setOutlineProvider((ViewOutlineProvider) null);
                    View view = t;
                    view.setTranslationZ(-view.getElevation());
                }

                public void onAnimationEnd(Animator animator) {
                    t.setTranslationZ(0.0f);
                    ((ClipPathView) t).setClipPath((Path) null);
                    t.setOutlineProvider(this.mOldOutlineProvider);
                }
            });
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(path, newUpdateListener, t) {
                public final /* synthetic */ Path f$0;
                public final /* synthetic */ ValueAnimator.AnimatorUpdateListener f$1;
                public final /* synthetic */ View f$2;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    IconShape.PathShape.lambda$createRevealAnimator$0(this.f$0, this.f$1, this.f$2, valueAnimator);
                }
            });
            return valueAnimator;
        }

        static /* synthetic */ void lambda$createRevealAnimator$0(Path path, ValueAnimator.AnimatorUpdateListener animatorUpdateListener, View view, ValueAnimator valueAnimator) {
            path.reset();
            animatorUpdateListener.onAnimationUpdate(valueAnimator);
            ((ClipPathView) view).setClipPath(path);
        }
    }

    public static final class Circle extends PathShape {
        private final float[] mTempRadii = new float[8];

        public boolean enableShapeDetection() {
            return true;
        }

        public Circle() {
            super();
        }

        /* access modifiers changed from: protected */
        public ValueAnimator.AnimatorUpdateListener newUpdateListener(Rect rect, Rect rect2, float f, Path path) {
            float startRadius = getStartRadius(rect);
            return new ValueAnimator.AnimatorUpdateListener(new FloatArrayEvaluator(new float[6]), new float[]{(float) rect.left, (float) rect.top, (float) rect.right, (float) rect.bottom, startRadius, startRadius}, new float[]{(float) rect2.left, (float) rect2.top, (float) rect2.right, (float) rect2.bottom, f, f}, path) {
                public final /* synthetic */ FloatArrayEvaluator f$1;
                public final /* synthetic */ float[] f$2;
                public final /* synthetic */ float[] f$3;
                public final /* synthetic */ Path f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    IconShape.Circle.this.lambda$newUpdateListener$0$IconShape$Circle(this.f$1, this.f$2, this.f$3, this.f$4, valueAnimator);
                }
            };
        }

        public /* synthetic */ void lambda$newUpdateListener$0$IconShape$Circle(FloatArrayEvaluator floatArrayEvaluator, float[] fArr, float[] fArr2, Path path, ValueAnimator valueAnimator) {
            float[] evaluate = floatArrayEvaluator.evaluate(((Float) valueAnimator.getAnimatedValue()).floatValue(), fArr, fArr2);
            path.addRoundRect(evaluate[0], evaluate[1], evaluate[2], evaluate[3], getRadiiArray(evaluate[4], evaluate[5]), Path.Direction.CW);
        }

        private float[] getRadiiArray(float f, float f2) {
            float[] fArr = this.mTempRadii;
            fArr[7] = f;
            fArr[6] = f;
            fArr[3] = f;
            fArr[2] = f;
            fArr[1] = f;
            fArr[0] = f;
            fArr[5] = f2;
            fArr[4] = f2;
            return fArr;
        }

        public void addToPath(Path path, float f, float f2, float f3) {
            path.addCircle(f + f3, f2 + f3, f3, Path.Direction.CW);
        }

        /* access modifiers changed from: protected */
        public float getStartRadius(Rect rect) {
            return ((float) rect.width()) / 2.0f;
        }
    }

    public static class RoundedSquare extends SimpleRectShape {
        private final float mRadiusRatio;

        public RoundedSquare(float f) {
            super();
            this.mRadiusRatio = f;
        }

        public void drawShape(Canvas canvas, float f, float f2, float f3, Paint paint) {
            float f4 = f + f3;
            float f5 = f2 + f3;
            float f6 = f3 * this.mRadiusRatio;
            canvas.drawRoundRect(f4 - f3, f5 - f3, f4 + f3, f5 + f3, f6, f6, paint);
        }

        public void addToPath(Path path, float f, float f2, float f3) {
            float f4 = f + f3;
            float f5 = f2 + f3;
            float f6 = f3 * this.mRadiusRatio;
            path.addRoundRect(f4 - f3, f5 - f3, f4 + f3, f5 + f3, f6, f6, Path.Direction.CW);
        }

        /* access modifiers changed from: protected */
        public float getStartRadius(Rect rect) {
            return (((float) rect.width()) / 2.0f) * this.mRadiusRatio;
        }
    }

    public static class TearDrop extends PathShape {
        private final float mRadiusRatio;
        private final float[] mTempRadii = new float[8];

        public TearDrop(float f) {
            super();
            this.mRadiusRatio = f;
        }

        public void addToPath(Path path, float f, float f2, float f3) {
            float f4 = f + f3;
            float f5 = f2 + f3;
            path.addRoundRect(f4 - f3, f5 - f3, f4 + f3, f5 + f3, getRadiiArray(f3, this.mRadiusRatio * f3), Path.Direction.CW);
        }

        private float[] getRadiiArray(float f, float f2) {
            float[] fArr = this.mTempRadii;
            fArr[7] = f;
            fArr[6] = f;
            fArr[3] = f;
            fArr[2] = f;
            fArr[1] = f;
            fArr[0] = f;
            fArr[5] = f2;
            fArr[4] = f2;
            return fArr;
        }

        /* access modifiers changed from: protected */
        public ValueAnimator.AnimatorUpdateListener newUpdateListener(Rect rect, Rect rect2, float f, Path path) {
            float width = ((float) rect.width()) / 2.0f;
            return new ValueAnimator.AnimatorUpdateListener(new FloatArrayEvaluator(new float[6]), new float[]{(float) rect.left, (float) rect.top, (float) rect.right, (float) rect.bottom, width, this.mRadiusRatio * width}, new float[]{(float) rect2.left, (float) rect2.top, (float) rect2.right, (float) rect2.bottom, f, f}, path) {
                public final /* synthetic */ FloatArrayEvaluator f$1;
                public final /* synthetic */ float[] f$2;
                public final /* synthetic */ float[] f$3;
                public final /* synthetic */ Path f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    IconShape.TearDrop.this.lambda$newUpdateListener$0$IconShape$TearDrop(this.f$1, this.f$2, this.f$3, this.f$4, valueAnimator);
                }
            };
        }

        public /* synthetic */ void lambda$newUpdateListener$0$IconShape$TearDrop(FloatArrayEvaluator floatArrayEvaluator, float[] fArr, float[] fArr2, Path path, ValueAnimator valueAnimator) {
            float[] evaluate = floatArrayEvaluator.evaluate(((Float) valueAnimator.getAnimatedValue()).floatValue(), fArr, fArr2);
            path.addRoundRect(evaluate[0], evaluate[1], evaluate[2], evaluate[3], getRadiiArray(evaluate[4], evaluate[5]), Path.Direction.CW);
        }
    }

    public static class Squircle extends PathShape {
        private final float mRadiusRatio;

        public Squircle(float f) {
            super();
            this.mRadiusRatio = f;
        }

        public void addToPath(Path path, float f, float f2, float f3) {
            float f4 = f + f3;
            float f5 = f2 + f3;
            float f6 = f3 - (this.mRadiusRatio * f3);
            path.moveTo(f4, f5 - f3);
            float f7 = f4;
            float f8 = f5;
            float f9 = f3;
            float f10 = f6;
            Path path2 = path;
            addLeftCurve(f7, f8, f9, f10, path2);
            addRightCurve(f7, f8, f9, f10, path2);
            float f11 = -f3;
            float f12 = -f6;
            addLeftCurve(f7, f8, f11, f12, path2);
            addRightCurve(f7, f8, f11, f12, path2);
            path.close();
        }

        private void addLeftCurve(float f, float f2, float f3, float f4, Path path) {
            float f5 = f - f3;
            path.cubicTo(f - f4, f2 - f3, f5, f2 - f4, f5, f2);
        }

        private void addRightCurve(float f, float f2, float f3, float f4, Path path) {
            float f5 = f2 + f3;
            path.cubicTo(f - f3, f2 + f4, f - f4, f5, f, f5);
        }

        /* access modifiers changed from: protected */
        public ValueAnimator.AnimatorUpdateListener newUpdateListener(Rect rect, Rect rect2, float f, Path path) {
            float exactCenterX = rect.exactCenterX();
            float exactCenterY = rect.exactCenterY();
            float width = ((float) rect.width()) / 2.0f;
            return new ValueAnimator.AnimatorUpdateListener(exactCenterX, rect2.exactCenterX(), exactCenterY, rect2.exactCenterY(), width, f, width - (this.mRadiusRatio * width), f * 0.55191505f, 0.0f, (((float) rect2.width()) / 2.0f) - f, 0.0f, (((float) rect2.height()) / 2.0f) - f, path) {
                public final /* synthetic */ float f$1;
                public final /* synthetic */ float f$10;
                public final /* synthetic */ float f$11;
                public final /* synthetic */ float f$12;
                public final /* synthetic */ Path f$13;
                public final /* synthetic */ float f$2;
                public final /* synthetic */ float f$3;
                public final /* synthetic */ float f$4;
                public final /* synthetic */ float f$5;
                public final /* synthetic */ float f$6;
                public final /* synthetic */ float f$7;
                public final /* synthetic */ float f$8;
                public final /* synthetic */ float f$9;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                    this.f$7 = r8;
                    this.f$8 = r9;
                    this.f$9 = r10;
                    this.f$10 = r11;
                    this.f$11 = r12;
                    this.f$12 = r13;
                    this.f$13 = r14;
                }

                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    IconShape.Squircle.this.lambda$newUpdateListener$0$IconShape$Squircle(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, valueAnimator);
                }
            };
        }

        public /* synthetic */ void lambda$newUpdateListener$0$IconShape$Squircle(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10, float f11, float f12, Path path, ValueAnimator valueAnimator) {
            Path path2 = path;
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            float f13 = 1.0f - floatValue;
            float f14 = (f13 * f) + (floatValue * f2);
            float f15 = (f13 * f3) + (floatValue * f4);
            float f16 = (f13 * f5) + (floatValue * f6);
            float f17 = (f13 * f7) + (floatValue * f8);
            float f18 = (f13 * f9) + (floatValue * f10);
            float f19 = (f13 * f11) + (floatValue * f12);
            float f20 = f15 - f19;
            path2.moveTo(f14, f20 - f16);
            path2.rLineTo(-f18, 0.0f);
            float f21 = f14 - f18;
            float f22 = f16;
            float f23 = f17;
            Path path3 = path;
            addLeftCurve(f21, f20, f22, f23, path3);
            path2.rLineTo(0.0f, f19 + f19);
            float f24 = f15 + f19;
            addRightCurve(f21, f24, f22, f23, path3);
            path2.rLineTo(f18 + f18, 0.0f);
            float f25 = f14 + f18;
            float f26 = -f16;
            float f27 = -f17;
            addLeftCurve(f25, f24, f26, f27, path3);
            path2.rLineTo(0.0f, (-f19) - f19);
            addRightCurve(f25, f20, f26, f27, path3);
            path.close();
        }
    }

    public static void init(Context context) {
        pickBestShape(context);
    }

    private static IconShape getShapeDefinition(String str, float f) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1599780719:
                if (str.equals("TearDrop")) {
                    c = 0;
                    break;
                }
                break;
            case -716854276:
                if (str.equals("Squircle")) {
                    c = 1;
                    break;
                }
                break;
            case -458911222:
                if (str.equals("RoundedSquare")) {
                    c = 2;
                    break;
                }
                break;
            case 2018617584:
                if (str.equals("Circle")) {
                    c = 3;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return new TearDrop(f);
            case 1:
                return new Squircle(f);
            case 2:
                return new RoundedSquare(f);
            case 3:
                return new Circle();
            default:
                throw new IllegalArgumentException("Invalid shape type: " + str);
        }
    }

    private static List<IconShape> getAllShapes(Context context) {
        XmlResourceParser xml;
        ArrayList arrayList = new ArrayList();
        try {
            xml = context.getResources().getXml(R.xml.folder_shapes);
            while (true) {
                int next = xml.next();
                if (next == 3 || next == 1 || "shapes".equals(xml.getName())) {
                    int depth = xml.getDepth();
                    int[] iArr = {R.attr.folderIconRadius};
                }
            }
            int depth2 = xml.getDepth();
            int[] iArr2 = {R.attr.folderIconRadius};
            while (true) {
                int next2 = xml.next();
                if ((next2 != 3 || xml.getDepth() > depth2) && next2 != 1) {
                    if (next2 == 2) {
                        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(Xml.asAttributeSet(xml), iArr2);
                        IconShape shapeDefinition = getShapeDefinition(xml.getName(), obtainStyledAttributes.getFloat(0, 1.0f));
                        obtainStyledAttributes.recycle();
                        arrayList.add(shapeDefinition);
                    }
                }
            }
            if (xml != null) {
                xml.close();
            }
            return arrayList;
        } catch (IOException | XmlPullParserException e) {
            throw new RuntimeException(e);
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    protected static void pickBestShape(Context context) {
        Region region = new Region(0, 0, 200, 200);
        Region region2 = new Region();
        AdaptiveIconDrawable adaptiveIconDrawable = new AdaptiveIconDrawable(new ColorDrawable(ViewCompat.MEASURED_STATE_MASK), new ColorDrawable(ViewCompat.MEASURED_STATE_MASK));
        adaptiveIconDrawable.setBounds(0, 0, 200, 200);
        region2.setPath(adaptiveIconDrawable.getIconMask(), region);
        Path path = new Path();
        Region region3 = new Region();
        int i = Integer.MAX_VALUE;
        IconShape iconShape = null;
        for (IconShape next : getAllShapes(context)) {
            path.reset();
            next.addToPath(path, 0.0f, 0.0f, 100.0f);
            region3.setPath(path, region);
            region3.op(region2, Region.Op.XOR);
            int area = GraphicsUtils.getArea(region3);
            if (area < i) {
                iconShape = next;
                i = area;
            }
        }
        if (iconShape != null) {
            sInstance = iconShape;
        }
        sNormalizationScale = IconNormalizer.normalizeAdaptiveIcon(adaptiveIconDrawable, 200, (RectF) null);
    }
}
