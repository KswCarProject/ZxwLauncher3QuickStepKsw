package com.android.systemui.animation;

import android.animation.ValueAnimator;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.MathUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.ViewOverlay;
import android.view.animation.Interpolator;
import com.android.launcher3.testing.TestProtocol;
import com.android.systemui.animation.LaunchAnimator;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.math.MathKt;

@Metadata(d1 = {"\u0000^\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0014\n\u0000\n\u0002\u0010\u0015\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\u0018\u0000 !2\u00020\u0001:\u0006 !\"#$%B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J0\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0016H\u0002J\u001d\u0010\u0017\u001a\u00020\u00162\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0018\u001a\u00020\u0010H\u0000¢\u0006\u0002\b\u0019J(\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u0018\u001a\u00020\u00102\u0006\u0010\u001e\u001a\u00020\u001f2\b\b\u0002\u0010\u0015\u001a\u00020\u0016R\u000e\u0010\u0007\u001a\u00020\bX\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000¨\u0006&"}, d2 = {"Lcom/android/systemui/animation/LaunchAnimator;", "", "timings", "Lcom/android/systemui/animation/LaunchAnimator$Timings;", "interpolators", "Lcom/android/systemui/animation/LaunchAnimator$Interpolators;", "(Lcom/android/systemui/animation/LaunchAnimator$Timings;Lcom/android/systemui/animation/LaunchAnimator$Interpolators;)V", "cornerRadii", "", "launchContainerLocation", "", "applyStateToWindowBackgroundLayer", "", "drawable", "Landroid/graphics/drawable/GradientDrawable;", "state", "Lcom/android/systemui/animation/LaunchAnimator$State;", "linearProgress", "", "launchContainer", "Landroid/view/View;", "drawHole", "", "isExpandingFullyAbove", "endState", "isExpandingFullyAbove$SharedLibWrapper_release", "startAnimation", "Lcom/android/systemui/animation/LaunchAnimator$Animation;", "controller", "Lcom/android/systemui/animation/LaunchAnimator$Controller;", "windowBackgroundColor", "", "Animation", "Companion", "Controller", "Interpolators", "State", "Timings", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: LaunchAnimator.kt */
public final class LaunchAnimator {
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    public static final boolean DEBUG = false;
    private static final PorterDuffXfermode SRC_MODE = new PorterDuffXfermode(PorterDuff.Mode.SRC);
    private final float[] cornerRadii = new float[8];
    private final Interpolators interpolators;
    private final int[] launchContainerLocation = new int[2];
    private final Timings timings;

    @Metadata(d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&¨\u0006\u0004"}, d2 = {"Lcom/android/systemui/animation/LaunchAnimator$Animation;", "", "cancel", "", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: LaunchAnimator.kt */
    public interface Animation {
        void cancel();
    }

    @Metadata(d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J\b\u0010\f\u001a\u00020\rH&J\u0010\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0016J \u0010\u0012\u001a\u00020\u000f2\u0006\u0010\u0013\u001a\u00020\r2\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0015H\u0016J\u0010\u0010\u0017\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0016R\u0018\u0010\u0002\u001a\u00020\u0003X¦\u000e¢\u0006\f\u001a\u0004\b\u0004\u0010\u0005\"\u0004\b\u0006\u0010\u0007R\u0016\u0010\b\u001a\u0004\u0018\u00010\t8VX\u0004¢\u0006\u0006\u001a\u0004\b\n\u0010\u000b¨\u0006\u0018"}, d2 = {"Lcom/android/systemui/animation/LaunchAnimator$Controller;", "", "launchContainer", "Landroid/view/ViewGroup;", "getLaunchContainer", "()Landroid/view/ViewGroup;", "setLaunchContainer", "(Landroid/view/ViewGroup;)V", "openingWindowSyncView", "Landroid/view/View;", "getOpeningWindowSyncView", "()Landroid/view/View;", "createAnimatorState", "Lcom/android/systemui/animation/LaunchAnimator$State;", "onLaunchAnimationEnd", "", "isExpandingFullyAbove", "", "onLaunchAnimationProgress", "state", "progress", "", "linearProgress", "onLaunchAnimationStart", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: LaunchAnimator.kt */
    public interface Controller {

        @Metadata(k = 3, mv = {1, 6, 0}, xi = 48)
        /* compiled from: LaunchAnimator.kt */
        public static final class DefaultImpls {
            public static View getOpeningWindowSyncView(Controller controller) {
                Intrinsics.checkNotNullParameter(controller, "this");
                return null;
            }

            public static void onLaunchAnimationEnd(Controller controller, boolean z) {
                Intrinsics.checkNotNullParameter(controller, "this");
            }

            public static void onLaunchAnimationProgress(Controller controller, State state, float f, float f2) {
                Intrinsics.checkNotNullParameter(controller, "this");
                Intrinsics.checkNotNullParameter(state, TestProtocol.STATE_FIELD);
            }

            public static void onLaunchAnimationStart(Controller controller, boolean z) {
                Intrinsics.checkNotNullParameter(controller, "this");
            }
        }

        State createAnimatorState();

        ViewGroup getLaunchContainer();

        View getOpeningWindowSyncView();

        void onLaunchAnimationEnd(boolean z);

        void onLaunchAnimationProgress(State state, float f, float f2);

        void onLaunchAnimationStart(boolean z);

        void setLaunchContainer(ViewGroup viewGroup);
    }

    @JvmStatic
    public static final float getProgress(Timings timings2, float f, long j, long j2) {
        return Companion.getProgress(timings2, f, j, j2);
    }

    public LaunchAnimator(Timings timings2, Interpolators interpolators2) {
        Intrinsics.checkNotNullParameter(timings2, "timings");
        Intrinsics.checkNotNullParameter(interpolators2, "interpolators");
        this.timings = timings2;
        this.interpolators = interpolators2;
    }

    @Metadata(d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J(\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\rH\u0007R\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0004¢\u0006\u0002\n\u0000¨\u0006\u000f"}, d2 = {"Lcom/android/systemui/animation/LaunchAnimator$Companion;", "", "()V", "DEBUG", "", "SRC_MODE", "Landroid/graphics/PorterDuffXfermode;", "getProgress", "", "timings", "Lcom/android/systemui/animation/LaunchAnimator$Timings;", "linearProgress", "delay", "", "duration", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: LaunchAnimator.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @JvmStatic
        public final float getProgress(Timings timings, float f, long j, long j2) {
            Intrinsics.checkNotNullParameter(timings, "timings");
            return MathUtils.constrain(((f * ((float) timings.getTotalDuration())) - ((float) j)) / ((float) j2), 0.0f, 1.0f);
        }
    }

    @Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u0007\n\u0002\b\u001c\n\u0002\u0010\u000b\n\u0002\b\u0007\b\u0016\u0018\u00002\u00020\u0001BA\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\b¢\u0006\u0002\u0010\nR\u001a\u0010\u0004\u001a\u00020\u0003X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u001a\u0010\t\u001a\u00020\bX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0013\u001a\u00020\b8F¢\u0006\u0006\u001a\u0004\b\u0014\u0010\u0010R\u0011\u0010\u0015\u001a\u00020\b8F¢\u0006\u0006\u001a\u0004\b\u0016\u0010\u0010R\u0011\u0010\u0017\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b\u0018\u0010\fR\u001a\u0010\u0005\u001a\u00020\u0003X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\f\"\u0004\b\u001a\u0010\u000eR\u001a\u0010\u0006\u001a\u00020\u0003X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\f\"\u0004\b\u001c\u0010\u000eR\u000e\u0010\u001d\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0002\u001a\u00020\u0003X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001e\u0010\f\"\u0004\b\u001f\u0010\u000eR\u0014\u0010 \u001a\u00020\u00038VX\u0004¢\u0006\u0006\u001a\u0004\b!\u0010\fR\u001a\u0010\u0007\u001a\u00020\bX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\"\u0010\u0010\"\u0004\b#\u0010\u0012R\u001a\u0010$\u001a\u00020%X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b&\u0010'\"\u0004\b(\u0010)R\u0011\u0010*\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b+\u0010\f¨\u0006,"}, d2 = {"Lcom/android/systemui/animation/LaunchAnimator$State;", "", "top", "", "bottom", "left", "right", "topCornerRadius", "", "bottomCornerRadius", "(IIIIFF)V", "getBottom", "()I", "setBottom", "(I)V", "getBottomCornerRadius", "()F", "setBottomCornerRadius", "(F)V", "centerX", "getCenterX", "centerY", "getCenterY", "height", "getHeight", "getLeft", "setLeft", "getRight", "setRight", "startTop", "getTop", "setTop", "topChange", "getTopChange", "getTopCornerRadius", "setTopCornerRadius", "visible", "", "getVisible", "()Z", "setVisible", "(Z)V", "width", "getWidth", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: LaunchAnimator.kt */
    public static class State {
        private int bottom;
        private float bottomCornerRadius;
        private int left;
        private int right;
        private final int startTop;
        private int top;
        private float topCornerRadius;
        private boolean visible;

        public State() {
            this(0, 0, 0, 0, 0.0f, 0.0f, 63, (DefaultConstructorMarker) null);
        }

        public State(int i, int i2, int i3, int i4, float f, float f2) {
            this.top = i;
            this.bottom = i2;
            this.left = i3;
            this.right = i4;
            this.topCornerRadius = f;
            this.bottomCornerRadius = f2;
            this.startTop = i;
            this.visible = true;
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ State(int r5, int r6, int r7, int r8, float r9, float r10, int r11, kotlin.jvm.internal.DefaultConstructorMarker r12) {
            /*
                r4 = this;
                r12 = r11 & 1
                r0 = 0
                if (r12 == 0) goto L_0x0007
                r12 = r0
                goto L_0x0008
            L_0x0007:
                r12 = r5
            L_0x0008:
                r5 = r11 & 2
                if (r5 == 0) goto L_0x000e
                r1 = r0
                goto L_0x000f
            L_0x000e:
                r1 = r6
            L_0x000f:
                r5 = r11 & 4
                if (r5 == 0) goto L_0x0015
                r2 = r0
                goto L_0x0016
            L_0x0015:
                r2 = r7
            L_0x0016:
                r5 = r11 & 8
                if (r5 == 0) goto L_0x001b
                goto L_0x001c
            L_0x001b:
                r0 = r8
            L_0x001c:
                r5 = r11 & 16
                r6 = 0
                if (r5 == 0) goto L_0x0023
                r3 = r6
                goto L_0x0024
            L_0x0023:
                r3 = r9
            L_0x0024:
                r5 = r11 & 32
                if (r5 == 0) goto L_0x002a
                r11 = r6
                goto L_0x002b
            L_0x002a:
                r11 = r10
            L_0x002b:
                r5 = r4
                r6 = r12
                r7 = r1
                r8 = r2
                r9 = r0
                r10 = r3
                r5.<init>(r6, r7, r8, r9, r10, r11)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.animation.LaunchAnimator.State.<init>(int, int, int, int, float, float, int, kotlin.jvm.internal.DefaultConstructorMarker):void");
        }

        public final int getTop() {
            return this.top;
        }

        public final void setTop(int i) {
            this.top = i;
        }

        public final int getBottom() {
            return this.bottom;
        }

        public final void setBottom(int i) {
            this.bottom = i;
        }

        public final int getLeft() {
            return this.left;
        }

        public final void setLeft(int i) {
            this.left = i;
        }

        public final int getRight() {
            return this.right;
        }

        public final void setRight(int i) {
            this.right = i;
        }

        public final float getTopCornerRadius() {
            return this.topCornerRadius;
        }

        public final void setTopCornerRadius(float f) {
            this.topCornerRadius = f;
        }

        public final float getBottomCornerRadius() {
            return this.bottomCornerRadius;
        }

        public final void setBottomCornerRadius(float f) {
            this.bottomCornerRadius = f;
        }

        public final int getWidth() {
            return this.right - this.left;
        }

        public final int getHeight() {
            return this.bottom - this.top;
        }

        public int getTopChange() {
            return this.top - this.startTop;
        }

        public final float getCenterX() {
            return ((float) this.left) + (((float) getWidth()) / 2.0f);
        }

        public final float getCenterY() {
            return ((float) this.top) + (((float) getHeight()) / 2.0f);
        }

        public final boolean getVisible() {
            return this.visible;
        }

        public final void setVisible(boolean z) {
            this.visible = z;
        }
    }

    @Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\b\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003¢\u0006\u0002\u0010\bJ\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0003HÆ\u0003J;\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0018\u001a\u00020\u0019HÖ\u0001J\t\u0010\u001a\u001a\u00020\u001bHÖ\u0001R\u0011\u0010\u0006\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0007\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\nR\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\nR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\n¨\u0006\u001c"}, d2 = {"Lcom/android/systemui/animation/LaunchAnimator$Timings;", "", "totalDuration", "", "contentBeforeFadeOutDelay", "contentBeforeFadeOutDuration", "contentAfterFadeInDelay", "contentAfterFadeInDuration", "(JJJJJ)V", "getContentAfterFadeInDelay", "()J", "getContentAfterFadeInDuration", "getContentBeforeFadeOutDelay", "getContentBeforeFadeOutDuration", "getTotalDuration", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "", "toString", "", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: LaunchAnimator.kt */
    public static final class Timings {
        private final long contentAfterFadeInDelay;
        private final long contentAfterFadeInDuration;
        private final long contentBeforeFadeOutDelay;
        private final long contentBeforeFadeOutDuration;
        private final long totalDuration;

        public static /* synthetic */ Timings copy$default(Timings timings, long j, long j2, long j3, long j4, long j5, int i, Object obj) {
            Timings timings2 = timings;
            return timings.copy((i & 1) != 0 ? timings2.totalDuration : j, (i & 2) != 0 ? timings2.contentBeforeFadeOutDelay : j2, (i & 4) != 0 ? timings2.contentBeforeFadeOutDuration : j3, (i & 8) != 0 ? timings2.contentAfterFadeInDelay : j4, (i & 16) != 0 ? timings2.contentAfterFadeInDuration : j5);
        }

        public final long component1() {
            return this.totalDuration;
        }

        public final long component2() {
            return this.contentBeforeFadeOutDelay;
        }

        public final long component3() {
            return this.contentBeforeFadeOutDuration;
        }

        public final long component4() {
            return this.contentAfterFadeInDelay;
        }

        public final long component5() {
            return this.contentAfterFadeInDuration;
        }

        public final Timings copy(long j, long j2, long j3, long j4, long j5) {
            return new Timings(j, j2, j3, j4, j5);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Timings)) {
                return false;
            }
            Timings timings = (Timings) obj;
            return this.totalDuration == timings.totalDuration && this.contentBeforeFadeOutDelay == timings.contentBeforeFadeOutDelay && this.contentBeforeFadeOutDuration == timings.contentBeforeFadeOutDuration && this.contentAfterFadeInDelay == timings.contentAfterFadeInDelay && this.contentAfterFadeInDuration == timings.contentAfterFadeInDuration;
        }

        public int hashCode() {
            return (((((((Long.hashCode(this.totalDuration) * 31) + Long.hashCode(this.contentBeforeFadeOutDelay)) * 31) + Long.hashCode(this.contentBeforeFadeOutDuration)) * 31) + Long.hashCode(this.contentAfterFadeInDelay)) * 31) + Long.hashCode(this.contentAfterFadeInDuration);
        }

        public String toString() {
            return "Timings(totalDuration=" + this.totalDuration + ", contentBeforeFadeOutDelay=" + this.contentBeforeFadeOutDelay + ", contentBeforeFadeOutDuration=" + this.contentBeforeFadeOutDuration + ", contentAfterFadeInDelay=" + this.contentAfterFadeInDelay + ", contentAfterFadeInDuration=" + this.contentAfterFadeInDuration + ')';
        }

        public Timings(long j, long j2, long j3, long j4, long j5) {
            this.totalDuration = j;
            this.contentBeforeFadeOutDelay = j2;
            this.contentBeforeFadeOutDuration = j3;
            this.contentAfterFadeInDelay = j4;
            this.contentAfterFadeInDuration = j5;
        }

        public final long getTotalDuration() {
            return this.totalDuration;
        }

        public final long getContentBeforeFadeOutDelay() {
            return this.contentBeforeFadeOutDelay;
        }

        public final long getContentBeforeFadeOutDuration() {
            return this.contentBeforeFadeOutDuration;
        }

        public final long getContentAfterFadeInDelay() {
            return this.contentAfterFadeInDelay;
        }

        public final long getContentAfterFadeInDuration() {
            return this.contentAfterFadeInDuration;
        }
    }

    @Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\b\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003¢\u0006\u0002\u0010\u0007J\t\u0010\r\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J1\u0010\u0011\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0015\u001a\u00020\u0016HÖ\u0001J\t\u0010\u0017\u001a\u00020\u0018HÖ\u0001R\u0011\u0010\u0006\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\t¨\u0006\u0019"}, d2 = {"Lcom/android/systemui/animation/LaunchAnimator$Interpolators;", "", "positionInterpolator", "Landroid/view/animation/Interpolator;", "positionXInterpolator", "contentBeforeFadeOutInterpolator", "contentAfterFadeInInterpolator", "(Landroid/view/animation/Interpolator;Landroid/view/animation/Interpolator;Landroid/view/animation/Interpolator;Landroid/view/animation/Interpolator;)V", "getContentAfterFadeInInterpolator", "()Landroid/view/animation/Interpolator;", "getContentBeforeFadeOutInterpolator", "getPositionInterpolator", "getPositionXInterpolator", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "", "toString", "", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: LaunchAnimator.kt */
    public static final class Interpolators {
        private final Interpolator contentAfterFadeInInterpolator;
        private final Interpolator contentBeforeFadeOutInterpolator;
        private final Interpolator positionInterpolator;
        private final Interpolator positionXInterpolator;

        public static /* synthetic */ Interpolators copy$default(Interpolators interpolators, Interpolator interpolator, Interpolator interpolator2, Interpolator interpolator3, Interpolator interpolator4, int i, Object obj) {
            if ((i & 1) != 0) {
                interpolator = interpolators.positionInterpolator;
            }
            if ((i & 2) != 0) {
                interpolator2 = interpolators.positionXInterpolator;
            }
            if ((i & 4) != 0) {
                interpolator3 = interpolators.contentBeforeFadeOutInterpolator;
            }
            if ((i & 8) != 0) {
                interpolator4 = interpolators.contentAfterFadeInInterpolator;
            }
            return interpolators.copy(interpolator, interpolator2, interpolator3, interpolator4);
        }

        public final Interpolator component1() {
            return this.positionInterpolator;
        }

        public final Interpolator component2() {
            return this.positionXInterpolator;
        }

        public final Interpolator component3() {
            return this.contentBeforeFadeOutInterpolator;
        }

        public final Interpolator component4() {
            return this.contentAfterFadeInInterpolator;
        }

        public final Interpolators copy(Interpolator interpolator, Interpolator interpolator2, Interpolator interpolator3, Interpolator interpolator4) {
            Intrinsics.checkNotNullParameter(interpolator, "positionInterpolator");
            Intrinsics.checkNotNullParameter(interpolator2, "positionXInterpolator");
            Intrinsics.checkNotNullParameter(interpolator3, "contentBeforeFadeOutInterpolator");
            Intrinsics.checkNotNullParameter(interpolator4, "contentAfterFadeInInterpolator");
            return new Interpolators(interpolator, interpolator2, interpolator3, interpolator4);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Interpolators)) {
                return false;
            }
            Interpolators interpolators = (Interpolators) obj;
            return Intrinsics.areEqual((Object) this.positionInterpolator, (Object) interpolators.positionInterpolator) && Intrinsics.areEqual((Object) this.positionXInterpolator, (Object) interpolators.positionXInterpolator) && Intrinsics.areEqual((Object) this.contentBeforeFadeOutInterpolator, (Object) interpolators.contentBeforeFadeOutInterpolator) && Intrinsics.areEqual((Object) this.contentAfterFadeInInterpolator, (Object) interpolators.contentAfterFadeInInterpolator);
        }

        public int hashCode() {
            return (((((this.positionInterpolator.hashCode() * 31) + this.positionXInterpolator.hashCode()) * 31) + this.contentBeforeFadeOutInterpolator.hashCode()) * 31) + this.contentAfterFadeInInterpolator.hashCode();
        }

        public String toString() {
            return "Interpolators(positionInterpolator=" + this.positionInterpolator + ", positionXInterpolator=" + this.positionXInterpolator + ", contentBeforeFadeOutInterpolator=" + this.contentBeforeFadeOutInterpolator + ", contentAfterFadeInInterpolator=" + this.contentAfterFadeInInterpolator + ')';
        }

        public Interpolators(Interpolator interpolator, Interpolator interpolator2, Interpolator interpolator3, Interpolator interpolator4) {
            Intrinsics.checkNotNullParameter(interpolator, "positionInterpolator");
            Intrinsics.checkNotNullParameter(interpolator2, "positionXInterpolator");
            Intrinsics.checkNotNullParameter(interpolator3, "contentBeforeFadeOutInterpolator");
            Intrinsics.checkNotNullParameter(interpolator4, "contentAfterFadeInInterpolator");
            this.positionInterpolator = interpolator;
            this.positionXInterpolator = interpolator2;
            this.contentBeforeFadeOutInterpolator = interpolator3;
            this.contentAfterFadeInInterpolator = interpolator4;
        }

        /* JADX INFO: this call moved to the top of the method (can break code semantics) */
        public /* synthetic */ Interpolators(Interpolator interpolator, Interpolator interpolator2, Interpolator interpolator3, Interpolator interpolator4, int i, DefaultConstructorMarker defaultConstructorMarker) {
            this(interpolator, (i & 2) != 0 ? interpolator : interpolator2, interpolator3, interpolator4);
        }

        public final Interpolator getPositionInterpolator() {
            return this.positionInterpolator;
        }

        public final Interpolator getPositionXInterpolator() {
            return this.positionXInterpolator;
        }

        public final Interpolator getContentBeforeFadeOutInterpolator() {
            return this.contentBeforeFadeOutInterpolator;
        }

        public final Interpolator getContentAfterFadeInInterpolator() {
            return this.contentAfterFadeInInterpolator;
        }
    }

    public static /* synthetic */ Animation startAnimation$default(LaunchAnimator launchAnimator, Controller controller, State state, int i, boolean z, int i2, Object obj) {
        if ((i2 & 8) != 0) {
            z = false;
        }
        return launchAnimator.startAnimation(controller, state, i, z);
    }

    public final Animation startAnimation(Controller controller, State state, int i, boolean z) {
        ViewOverlay viewOverlay;
        State state2 = state;
        Intrinsics.checkNotNullParameter(controller, "controller");
        Intrinsics.checkNotNullParameter(state2, "endState");
        State createAnimatorState = controller.createAnimatorState();
        int top = createAnimatorState.getTop();
        int bottom = createAnimatorState.getBottom();
        int left = createAnimatorState.getLeft();
        int right = createAnimatorState.getRight();
        float f = ((float) (left + right)) / 2.0f;
        int i2 = right - left;
        float topCornerRadius = createAnimatorState.getTopCornerRadius();
        float bottomCornerRadius = createAnimatorState.getBottomCornerRadius();
        Ref.IntRef intRef = new Ref.IntRef();
        intRef.element = state.getTop();
        Ref.IntRef intRef2 = new Ref.IntRef();
        intRef2.element = state.getBottom();
        Ref.IntRef intRef3 = new Ref.IntRef();
        intRef3.element = state.getLeft();
        Ref.IntRef intRef4 = new Ref.IntRef();
        intRef4.element = state.getRight();
        Ref.FloatRef floatRef = new Ref.FloatRef();
        floatRef.element = ((float) (intRef3.element + intRef4.element)) / 2.0f;
        Ref.IntRef intRef5 = new Ref.IntRef();
        intRef5.element = intRef4.element - intRef3.element;
        float topCornerRadius2 = state.getTopCornerRadius();
        float bottomCornerRadius2 = state.getBottomCornerRadius();
        ViewGroup launchContainer = controller.getLaunchContainer();
        boolean isExpandingFullyAbove$SharedLibWrapper_release = isExpandingFullyAbove$SharedLibWrapper_release(launchContainer, state2);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(i);
        gradientDrawable.setAlpha(0);
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        Ref.IntRef intRef6 = intRef;
        ofFloat.setDuration(this.timings.getTotalDuration());
        ofFloat.setInterpolator(Interpolators.LINEAR);
        View openingWindowSyncView = controller.getOpeningWindowSyncView();
        if (openingWindowSyncView == null) {
            viewOverlay = null;
        } else {
            viewOverlay = openingWindowSyncView.getOverlay();
        }
        ViewOverlay viewOverlay2 = viewOverlay;
        boolean z2 = openingWindowSyncView != null && !Intrinsics.areEqual((Object) openingWindowSyncView.getViewRootImpl(), (Object) controller.getLaunchContainer().getViewRootImpl());
        ViewGroupOverlay overlay = launchContainer.getOverlay();
        Ref.BooleanRef booleanRef = new Ref.BooleanRef();
        Ref.BooleanRef booleanRef2 = r29;
        Ref.BooleanRef booleanRef3 = new Ref.BooleanRef();
        ValueAnimator valueAnimator = ofFloat;
        GradientDrawable gradientDrawable2 = gradientDrawable;
        Ref.IntRef intRef7 = intRef5;
        boolean z3 = isExpandingFullyAbove$SharedLibWrapper_release;
        Ref.IntRef intRef8 = intRef2;
        valueAnimator.addListener(new LaunchAnimator$startAnimation$1(controller, z3, overlay, gradientDrawable2, z2, viewOverlay2));
        $$Lambda$LaunchAnimator$ZompmiSstNH405tYAJfcpoXQCSU r36 = r0;
        ValueAnimator valueAnimator2 = valueAnimator;
        Ref.BooleanRef booleanRef4 = booleanRef;
        $$Lambda$LaunchAnimator$ZompmiSstNH405tYAJfcpoXQCSU r0 = new ValueAnimator.AnimatorUpdateListener(booleanRef4, this, f, floatRef, i2, intRef7, createAnimatorState, top, intRef6, bottom, intRef8, topCornerRadius, topCornerRadius2, bottomCornerRadius, bottomCornerRadius2, z2, booleanRef2, overlay, gradientDrawable2, viewOverlay2, launchContainer, openingWindowSyncView, controller, z, state, intRef3, intRef4) {
            public final /* synthetic */ Ref.BooleanRef f$0;
            public final /* synthetic */ LaunchAnimator f$1;
            public final /* synthetic */ Ref.IntRef f$10;
            public final /* synthetic */ float f$11;
            public final /* synthetic */ float f$12;
            public final /* synthetic */ float f$13;
            public final /* synthetic */ float f$14;
            public final /* synthetic */ boolean f$15;
            public final /* synthetic */ Ref.BooleanRef f$16;
            public final /* synthetic */ ViewGroupOverlay f$17;
            public final /* synthetic */ GradientDrawable f$18;
            public final /* synthetic */ ViewOverlay f$19;
            public final /* synthetic */ float f$2;
            public final /* synthetic */ ViewGroup f$20;
            public final /* synthetic */ View f$21;
            public final /* synthetic */ LaunchAnimator.Controller f$22;
            public final /* synthetic */ boolean f$23;
            public final /* synthetic */ LaunchAnimator.State f$24;
            public final /* synthetic */ Ref.IntRef f$25;
            public final /* synthetic */ Ref.IntRef f$26;
            public final /* synthetic */ Ref.FloatRef f$3;
            public final /* synthetic */ int f$4;
            public final /* synthetic */ Ref.IntRef f$5;
            public final /* synthetic */ LaunchAnimator.State f$6;
            public final /* synthetic */ int f$7;
            public final /* synthetic */ Ref.IntRef f$8;
            public final /* synthetic */ int f$9;

            {
                this.f$0 = r3;
                this.f$1 = r4;
                this.f$2 = r5;
                this.f$3 = r6;
                this.f$4 = r7;
                this.f$5 = r8;
                this.f$6 = r9;
                this.f$7 = r10;
                this.f$8 = r11;
                this.f$9 = r12;
                this.f$10 = r13;
                this.f$11 = r14;
                this.f$12 = r15;
                this.f$13 = r16;
                this.f$14 = r17;
                this.f$15 = r18;
                this.f$16 = r19;
                this.f$17 = r20;
                this.f$18 = r21;
                this.f$19 = r22;
                this.f$20 = r23;
                this.f$21 = r24;
                this.f$22 = r25;
                this.f$23 = r26;
                this.f$24 = r27;
                this.f$25 = r28;
                this.f$26 = r29;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ValueAnimator valueAnimator2 = valueAnimator;
                Ref.BooleanRef booleanRef = this.f$0;
                Ref.BooleanRef booleanRef2 = booleanRef;
                LaunchAnimator.m111startAnimation$lambda1(booleanRef2, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14, this.f$15, this.f$16, this.f$17, this.f$18, this.f$19, this.f$20, this.f$21, this.f$22, this.f$23, this.f$24, this.f$25, this.f$26, valueAnimator2);
            }
        };
        ValueAnimator valueAnimator3 = valueAnimator2;
        valueAnimator3.addUpdateListener(r36);
        valueAnimator3.start();
        return new LaunchAnimator$startAnimation$3(booleanRef, valueAnimator3);
    }

    private static final void startAnimation$maybeUpdateEndState(Ref.IntRef intRef, State state, Ref.IntRef intRef2, Ref.IntRef intRef3, Ref.IntRef intRef4, Ref.FloatRef floatRef, Ref.IntRef intRef5) {
        if (intRef.element != state.getTop() || intRef2.element != state.getBottom() || intRef3.element != state.getLeft() || intRef4.element != state.getRight()) {
            intRef.element = state.getTop();
            intRef2.element = state.getBottom();
            intRef3.element = state.getLeft();
            intRef4.element = state.getRight();
            floatRef.element = ((float) (intRef3.element + intRef4.element)) / 2.0f;
            intRef5.element = intRef4.element - intRef3.element;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: startAnimation$lambda-1  reason: not valid java name */
    public static final void m111startAnimation$lambda1(Ref.BooleanRef booleanRef, LaunchAnimator launchAnimator, float f, Ref.FloatRef floatRef, int i, Ref.IntRef intRef, State state, int i2, Ref.IntRef intRef2, int i3, Ref.IntRef intRef3, float f2, float f3, float f4, float f5, boolean z, Ref.BooleanRef booleanRef2, ViewGroupOverlay viewGroupOverlay, GradientDrawable gradientDrawable, ViewOverlay viewOverlay, ViewGroup viewGroup, View view, Controller controller, boolean z2, State state2, Ref.IntRef intRef4, Ref.IntRef intRef5, ValueAnimator valueAnimator) {
        View view2;
        View view3;
        Ref.BooleanRef booleanRef3 = booleanRef;
        LaunchAnimator launchAnimator2 = launchAnimator;
        Ref.FloatRef floatRef2 = floatRef;
        Ref.IntRef intRef6 = intRef;
        State state3 = state;
        Ref.IntRef intRef7 = intRef2;
        Ref.IntRef intRef8 = intRef3;
        Ref.BooleanRef booleanRef4 = booleanRef2;
        GradientDrawable gradientDrawable2 = gradientDrawable;
        Controller controller2 = controller;
        Intrinsics.checkNotNullParameter(booleanRef3, "$cancelled");
        Intrinsics.checkNotNullParameter(launchAnimator2, "this$0");
        Intrinsics.checkNotNullParameter(floatRef2, "$endCenterX");
        Intrinsics.checkNotNullParameter(intRef6, "$endWidth");
        Intrinsics.checkNotNullParameter(state3, "$state");
        Intrinsics.checkNotNullParameter(intRef7, "$endTop");
        Intrinsics.checkNotNullParameter(intRef8, "$endBottom");
        Intrinsics.checkNotNullParameter(booleanRef4, "$movedBackgroundLayer");
        Intrinsics.checkNotNullParameter(gradientDrawable2, "$windowBackgroundLayer");
        Intrinsics.checkNotNullParameter(viewGroup, "$launchContainer");
        Intrinsics.checkNotNullParameter(controller2, "$controller");
        Intrinsics.checkNotNullParameter(state2, "$endState");
        Intrinsics.checkNotNullParameter(intRef4, "$endLeft");
        Intrinsics.checkNotNullParameter(intRef5, "$endRight");
        if (!booleanRef3.element) {
            Controller controller3 = controller2;
            startAnimation$maybeUpdateEndState(intRef2, state2, intRef3, intRef4, intRef5, floatRef, intRef);
            float animatedFraction = valueAnimator.getAnimatedFraction();
            float interpolation = launchAnimator2.interpolators.getPositionInterpolator().getInterpolation(animatedFraction);
            float lerp = MathUtils.lerp(f, floatRef2.element, launchAnimator2.interpolators.getPositionXInterpolator().getInterpolation(animatedFraction));
            float lerp2 = MathUtils.lerp(i, intRef6.element, interpolation) / 2.0f;
            state3.setTop(MathKt.roundToInt(MathUtils.lerp(i2, intRef7.element, interpolation)));
            state3.setBottom(MathKt.roundToInt(MathUtils.lerp(i3, intRef8.element, interpolation)));
            state3.setLeft(MathKt.roundToInt(lerp - lerp2));
            state3.setRight(MathKt.roundToInt(lerp + lerp2));
            state3.setTopCornerRadius(MathUtils.lerp(f2, f3, interpolation));
            state3.setBottomCornerRadius(MathUtils.lerp(f4, f5, interpolation));
            Companion companion = Companion;
            Timings timings2 = launchAnimator2.timings;
            state3.setVisible(companion.getProgress(timings2, animatedFraction, timings2.getContentBeforeFadeOutDelay(), launchAnimator2.timings.getContentBeforeFadeOutDuration()) < 1.0f);
            if (!z || state.getVisible() || booleanRef4.element) {
                view2 = view;
            } else {
                booleanRef4.element = true;
                Drawable drawable = gradientDrawable2;
                viewGroupOverlay.remove(drawable);
                Intrinsics.checkNotNull(viewOverlay);
                viewOverlay.add(drawable);
                view2 = view;
                ViewRootSync.INSTANCE.synchronizeNextDraw((View) viewGroup, view2, (Function0<Unit>) LaunchAnimator$startAnimation$2$1.INSTANCE);
            }
            if (booleanRef4.element) {
                Intrinsics.checkNotNull(view);
                view3 = view2;
            } else {
                view3 = controller.getLaunchContainer();
            }
            launchAnimator.applyStateToWindowBackgroundLayer(gradientDrawable, state, animatedFraction, view3, z2);
            controller3.onLaunchAnimationProgress(state3, interpolation, animatedFraction);
        }
    }

    public final boolean isExpandingFullyAbove$SharedLibWrapper_release(View view, State state) {
        Intrinsics.checkNotNullParameter(view, "launchContainer");
        Intrinsics.checkNotNullParameter(state, "endState");
        view.getLocationOnScreen(this.launchContainerLocation);
        if (state.getTop() > this.launchContainerLocation[1] || state.getBottom() < this.launchContainerLocation[1] + view.getHeight() || state.getLeft() > this.launchContainerLocation[0] || state.getRight() < this.launchContainerLocation[0] + view.getWidth()) {
            return false;
        }
        return true;
    }

    private final void applyStateToWindowBackgroundLayer(GradientDrawable gradientDrawable, State state, float f, View view, boolean z) {
        GradientDrawable gradientDrawable2 = gradientDrawable;
        view.getLocationOnScreen(this.launchContainerLocation);
        gradientDrawable.setBounds(state.getLeft() - this.launchContainerLocation[0], state.getTop() - this.launchContainerLocation[1], state.getRight() - this.launchContainerLocation[0], state.getBottom() - this.launchContainerLocation[1]);
        this.cornerRadii[0] = state.getTopCornerRadius();
        this.cornerRadii[1] = state.getTopCornerRadius();
        this.cornerRadii[2] = state.getTopCornerRadius();
        this.cornerRadii[3] = state.getTopCornerRadius();
        this.cornerRadii[4] = state.getBottomCornerRadius();
        this.cornerRadii[5] = state.getBottomCornerRadius();
        this.cornerRadii[6] = state.getBottomCornerRadius();
        this.cornerRadii[7] = state.getBottomCornerRadius();
        gradientDrawable.setCornerRadii(this.cornerRadii);
        Companion companion = Companion;
        Timings timings2 = this.timings;
        float progress = companion.getProgress(timings2, f, timings2.getContentBeforeFadeOutDelay(), this.timings.getContentBeforeFadeOutDuration());
        if (progress < 1.0f) {
            gradientDrawable.setAlpha(MathKt.roundToInt(this.interpolators.getContentBeforeFadeOutInterpolator().getInterpolation(progress) * ((float) 255)));
            return;
        }
        Timings timings3 = this.timings;
        gradientDrawable.setAlpha(MathKt.roundToInt((((float) 1) - this.interpolators.getContentAfterFadeInInterpolator().getInterpolation(companion.getProgress(timings3, f, timings3.getContentAfterFadeInDelay(), this.timings.getContentAfterFadeInDuration()))) * ((float) 255)));
        if (z) {
            gradientDrawable.setXfermode(SRC_MODE);
        }
    }
}
