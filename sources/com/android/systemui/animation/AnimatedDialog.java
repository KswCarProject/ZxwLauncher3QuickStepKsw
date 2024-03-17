package com.android.systemui.animation;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Insets;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Looper;
import android.service.dreams.IDreamManager;
import android.util.Log;
import android.util.MathUtils;
import android.view.GhostView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import androidx.core.view.ViewCompat;
import com.android.internal.jank.InteractionJankMonitor;
import com.android.launcher3.systemui.R;
import com.android.systemui.animation.AnimatedDialog;
import com.android.systemui.animation.GhostedViewLaunchAnimatorController;
import com.android.systemui.animation.LaunchAnimator;
import java.util.Objects;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.math.MathKt;

@Metadata(d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0010\b\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0002\u0018\u00002\u00020\u0001:\u0001ABU\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0000\u0012\u0004\u0012\u00020\n0\t\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\r\u001a\u00020\u000e\u0012\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0000\u0012\u0006\u0010\u0010\u001a\u00020\u000e¢\u0006\u0002\u0010\u0011J\b\u00102\u001a\u00020\nH\u0002J\u0012\u00103\u001a\u0004\u0018\u00010\u00152\u0006\u00104\u001a\u00020\u0007H\u0002J\u001c\u00105\u001a\u00020\n2\u0012\u00106\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\n0\tH\u0002J\b\u00107\u001a\u00020\nH\u0002J\u0006\u0010\b\u001a\u00020\nJ\u0006\u00108\u001a\u00020\u0007J\b\u00109\u001a\u00020\u000eH\u0002J\u0006\u0010:\u001a\u00020\nJ0\u0010;\u001a\u00020\n2\u0006\u0010)\u001a\u00020\u000e2\u000e\b\u0002\u0010<\u001a\b\u0012\u0004\u0012\u00020\n0=2\u000e\b\u0002\u0010>\u001a\b\u0012\u0004\u0012\u00020\n0=H\u0002J\u0016\u0010?\u001a\u00020\n2\f\u0010@\u001a\b\u0012\u0004\u0012\u00020\n0=H\u0002R\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u0013X\u0004¢\u0006\u0002\n\u0000R\u001b\u0010\u0014\u001a\u00020\u00158BX\u0002¢\u0006\f\n\u0004\b\u0018\u0010\u0019\u001a\u0004\b\u0016\u0010\u0017R\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u001bX\u000e¢\u0006\u0002\n\u0000R\u0011\u0010\u000b\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u001c\u0010\u001e\u001a\u0004\u0018\u00010\u0015X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001f\u0010\u0017\"\u0004\b \u0010!R\u000e\u0010\"\u001a\u00020\u000eX\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0004¢\u0006\u0002\n\u0000R\u001a\u0010#\u001a\u00020\u000eX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b$\u0010%\"\u0004\b&\u0010'R\u000e\u0010\u0010\u001a\u00020\u000eX\u0004¢\u0006\u0002\n\u0000R\u000e\u0010(\u001a\u00020\u000eX\u000e¢\u0006\u0002\n\u0000R\u000e\u0010)\u001a\u00020\u000eX\u000e¢\u0006\u0002\n\u0000R\u000e\u0010*\u001a\u00020\u000eX\u000e¢\u0006\u0002\n\u0000R\u000e\u0010+\u001a\u00020\u000eX\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0000\u0012\u0004\u0012\u00020\n0\tX\u0004¢\u0006\u0002\n\u0000R\u000e\u0010,\u001a\u00020-X\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u0000X\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0006\u001a\u00020\u0007X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b.\u0010/\"\u0004\b0\u00101¨\u0006B"}, d2 = {"Lcom/android/systemui/animation/AnimatedDialog;", "", "launchAnimator", "Lcom/android/systemui/animation/LaunchAnimator;", "dreamManager", "Landroid/service/dreams/IDreamManager;", "touchSurface", "Landroid/view/View;", "onDialogDismissed", "Lkotlin/Function1;", "", "dialog", "Landroid/app/Dialog;", "animateBackgroundBoundsChange", "", "parentAnimatedDialog", "forceDisableSynchronization", "(Lcom/android/systemui/animation/LaunchAnimator;Landroid/service/dreams/IDreamManager;Landroid/view/View;Lkotlin/jvm/functions/Function1;Landroid/app/Dialog;ZLcom/android/systemui/animation/AnimatedDialog;Z)V", "backgroundLayoutListener", "Lcom/android/systemui/animation/AnimatedDialog$AnimatedBoundsLayoutListener;", "decorView", "Landroid/view/ViewGroup;", "getDecorView", "()Landroid/view/ViewGroup;", "decorView$delegate", "Lkotlin/Lazy;", "decorViewLayoutListener", "Landroid/view/View$OnLayoutChangeListener;", "getDialog", "()Landroid/app/Dialog;", "dialogContentWithBackground", "getDialogContentWithBackground", "setDialogContentWithBackground", "(Landroid/view/ViewGroup;)V", "dismissRequested", "exitAnimationDisabled", "getExitAnimationDisabled", "()Z", "setExitAnimationDisabled", "(Z)V", "isDismissing", "isLaunching", "isOriginalDialogViewLaidOut", "isTouchSurfaceGhostDrawn", "originalDialogBackgroundColor", "", "getTouchSurface", "()Landroid/view/View;", "setTouchSurface", "(Landroid/view/View;)V", "addTouchSurfaceGhost", "findFirstViewGroupWithBackground", "view", "hideDialogIntoView", "onAnimationFinished", "maybeStartLaunchAnimation", "prepareForStackDismiss", "shouldAnimateDialogIntoView", "start", "startAnimation", "onLaunchAnimationStart", "Lkotlin/Function0;", "onLaunchAnimationEnd", "synchronizeNextDraw", "then", "AnimatedBoundsLayoutListener", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: DialogLaunchAnimator.kt */
final class AnimatedDialog {
    /* access modifiers changed from: private */
    public final AnimatedBoundsLayoutListener backgroundLayoutListener;
    private final Lazy decorView$delegate;
    private View.OnLayoutChangeListener decorViewLayoutListener;
    private final Dialog dialog;
    private ViewGroup dialogContentWithBackground;
    /* access modifiers changed from: private */
    public boolean dismissRequested;
    private final IDreamManager dreamManager;
    private boolean exitAnimationDisabled;
    private final boolean forceDisableSynchronization;
    private boolean isDismissing;
    /* access modifiers changed from: private */
    public boolean isLaunching;
    /* access modifiers changed from: private */
    public boolean isOriginalDialogViewLaidOut;
    /* access modifiers changed from: private */
    public boolean isTouchSurfaceGhostDrawn;
    private final LaunchAnimator launchAnimator;
    /* access modifiers changed from: private */
    public final Function1<AnimatedDialog, Unit> onDialogDismissed;
    private int originalDialogBackgroundColor;
    private final AnimatedDialog parentAnimatedDialog;
    private View touchSurface;

    public AnimatedDialog(LaunchAnimator launchAnimator2, IDreamManager iDreamManager, View view, Function1<? super AnimatedDialog, Unit> function1, Dialog dialog2, boolean z, AnimatedDialog animatedDialog, boolean z2) {
        AnimatedBoundsLayoutListener animatedBoundsLayoutListener;
        Intrinsics.checkNotNullParameter(launchAnimator2, "launchAnimator");
        Intrinsics.checkNotNullParameter(iDreamManager, "dreamManager");
        Intrinsics.checkNotNullParameter(view, "touchSurface");
        Intrinsics.checkNotNullParameter(function1, "onDialogDismissed");
        Intrinsics.checkNotNullParameter(dialog2, "dialog");
        this.launchAnimator = launchAnimator2;
        this.dreamManager = iDreamManager;
        this.touchSurface = view;
        this.onDialogDismissed = function1;
        this.dialog = dialog2;
        this.parentAnimatedDialog = animatedDialog;
        this.forceDisableSynchronization = z2;
        this.decorView$delegate = LazyKt.lazy(new AnimatedDialog$decorView$2(this));
        this.originalDialogBackgroundColor = ViewCompat.MEASURED_STATE_MASK;
        this.isLaunching = true;
        if (z) {
            animatedBoundsLayoutListener = new AnimatedBoundsLayoutListener();
        } else {
            animatedBoundsLayoutListener = null;
        }
        this.backgroundLayoutListener = animatedBoundsLayoutListener;
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public /* synthetic */ AnimatedDialog(LaunchAnimator launchAnimator2, IDreamManager iDreamManager, View view, Function1 function1, Dialog dialog2, boolean z, AnimatedDialog animatedDialog, boolean z2, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(launchAnimator2, iDreamManager, view, function1, dialog2, z, (i & 64) != 0 ? null : animatedDialog, z2);
    }

    public final View getTouchSurface() {
        return this.touchSurface;
    }

    public final void setTouchSurface(View view) {
        Intrinsics.checkNotNullParameter(view, "<set-?>");
        this.touchSurface = view;
    }

    public final Dialog getDialog() {
        return this.dialog;
    }

    private final ViewGroup getDecorView() {
        return (ViewGroup) this.decorView$delegate.getValue();
    }

    public final ViewGroup getDialogContentWithBackground() {
        return this.dialogContentWithBackground;
    }

    public final void setDialogContentWithBackground(ViewGroup viewGroup) {
        this.dialogContentWithBackground = viewGroup;
    }

    public final boolean getExitAnimationDisabled() {
        return this.exitAnimationDisabled;
    }

    public final void setExitAnimationDisabled(boolean z) {
        this.exitAnimationDisabled = z;
    }

    public final void start() {
        ViewGroup viewGroup;
        ColorStateList color;
        this.dialog.create();
        Window window = this.dialog.getWindow();
        Intrinsics.checkNotNull(window);
        if (window.getAttributes().width == -1 && window.getAttributes().height == -1) {
            viewGroup = null;
            int childCount = getDecorView().getChildCount();
            int i = 0;
            while (true) {
                if (i >= childCount) {
                    break;
                }
                int i2 = i + 1;
                View childAt = getDecorView().getChildAt(i);
                Intrinsics.checkNotNullExpressionValue(childAt, "decorView.getChildAt(i)");
                ViewGroup findFirstViewGroupWithBackground = findFirstViewGroupWithBackground(childAt);
                if (findFirstViewGroupWithBackground != null) {
                    viewGroup = findFirstViewGroupWithBackground;
                    break;
                }
                ViewGroup viewGroup2 = findFirstViewGroupWithBackground;
                i = i2;
                viewGroup = viewGroup2;
            }
            if (viewGroup == null) {
                throw new IllegalStateException("Unable to find ViewGroup with background");
            }
        } else {
            FrameLayout frameLayout = new FrameLayout(this.dialog.getContext());
            getDecorView().addView(frameLayout, 0, new FrameLayout.LayoutParams(-1, -1));
            FrameLayout frameLayout2 = new FrameLayout(this.dialog.getContext());
            frameLayout2.setBackground(getDecorView().getBackground());
            window.setBackgroundDrawableResource(17170445);
            frameLayout.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    AnimatedDialog.m106start$lambda0(AnimatedDialog.this, view);
                }
            });
            frameLayout2.setClickable(true);
            frameLayout.setImportantForAccessibility(2);
            frameLayout2.setImportantForAccessibility(2);
            frameLayout.addView(frameLayout2, new FrameLayout.LayoutParams(window.getAttributes().width, window.getAttributes().height, window.getAttributes().gravity));
            int childCount2 = getDecorView().getChildCount();
            int i3 = 1;
            while (i3 < childCount2) {
                i3++;
                View childAt2 = getDecorView().getChildAt(1);
                getDecorView().removeViewAt(1);
                frameLayout2.addView(childAt2);
            }
            window.setLayout(-1, -1);
            this.decorViewLayoutListener = new View.OnLayoutChangeListener(window, frameLayout2) {
                public final /* synthetic */ Window f$0;
                public final /* synthetic */ FrameLayout f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                    AnimatedDialog.m107start$lambda1(this.f$0, this.f$1, view, i, i2, i3, i4, i5, i6, i7, i8);
                }
            };
            getDecorView().addOnLayoutChangeListener(this.decorViewLayoutListener);
            viewGroup = frameLayout2;
        }
        this.dialogContentWithBackground = viewGroup;
        viewGroup.setTag(R.id.tag_dialog_background, true);
        Drawable background = viewGroup.getBackground();
        GhostedViewLaunchAnimatorController.Companion companion = GhostedViewLaunchAnimatorController.Companion;
        Intrinsics.checkNotNullExpressionValue(background, "background");
        GradientDrawable findGradientDrawable = companion.findGradientDrawable(background);
        int i4 = ViewCompat.MEASURED_STATE_MASK;
        if (!(findGradientDrawable == null || (color = findGradientDrawable.getColor()) == null)) {
            i4 = color.getDefaultColor();
        }
        this.originalDialogBackgroundColor = i4;
        viewGroup.setTransitionVisibility(4);
        window.getAttributes().windowAnimations = R.style.Animation_LaunchAnimation;
        window.getAttributes().layoutInDisplayCutoutMode = 3;
        window.setAttributes(window.getAttributes());
        window.setDecorFitsSystemWindows(false);
        ViewParent parent = viewGroup.getParent();
        Objects.requireNonNull(parent, "null cannot be cast to non-null type android.view.ViewGroup");
        ((ViewGroup) parent).setOnApplyWindowInsetsListener($$Lambda$AnimatedDialog$NkWEg3BoPSm_W3W7fpQzk6AWEaM.INSTANCE);
        viewGroup.addOnLayoutChangeListener(new AnimatedDialog$start$2(viewGroup, this));
        window.clearFlags(2);
        this.dialog.setDismissOverride(new Runnable() {
            public final void run() {
                AnimatedDialog.this.onDialogDismissed();
            }
        });
        this.dialog.show();
        addTouchSurfaceGhost();
    }

    /* access modifiers changed from: private */
    /* renamed from: start$lambda-0  reason: not valid java name */
    public static final void m106start$lambda0(AnimatedDialog animatedDialog, View view) {
        Intrinsics.checkNotNullParameter(animatedDialog, "this$0");
        animatedDialog.dialog.dismiss();
    }

    /* access modifiers changed from: private */
    /* renamed from: start$lambda-1  reason: not valid java name */
    public static final void m107start$lambda1(Window window, FrameLayout frameLayout, View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        Intrinsics.checkNotNullParameter(window, "$window");
        Intrinsics.checkNotNullParameter(frameLayout, "$dialogContentWithBackground");
        if (window.getAttributes().width != -1 || window.getAttributes().height != -1) {
            ViewGroup.LayoutParams layoutParams = frameLayout.getLayoutParams();
            layoutParams.width = window.getAttributes().width;
            layoutParams.height = window.getAttributes().height;
            frameLayout.setLayoutParams(layoutParams);
            window.setLayout(-1, -1);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: start$lambda-2  reason: not valid java name */
    public static final WindowInsets m108start$lambda2(View view, WindowInsets windowInsets) {
        Insets insets = windowInsets.getInsets(WindowInsets.Type.displayCutout());
        view.setPadding(insets.left, insets.top, insets.right, insets.bottom);
        return WindowInsets.CONSUMED;
    }

    /* access modifiers changed from: private */
    public final void addTouchSurfaceGhost() {
        if (getDecorView().getViewRootImpl() == null) {
            getDecorView().post(new Runnable() {
                public final void run() {
                    AnimatedDialog.this.addTouchSurfaceGhost();
                }
            });
            return;
        }
        synchronizeNextDraw(new AnimatedDialog$addTouchSurfaceGhost$2(this));
        GhostView.addGhost(this.touchSurface, getDecorView());
        View view = this.touchSurface;
        LaunchableView launchableView = view instanceof LaunchableView ? (LaunchableView) view : null;
        if (launchableView != null) {
            launchableView.setShouldBlockVisibilityChanges(true);
        }
    }

    /* access modifiers changed from: private */
    public final void synchronizeNextDraw(Function0<Unit> function0) {
        if (this.forceDisableSynchronization) {
            function0.invoke();
        } else {
            ViewRootSync.INSTANCE.synchronizeNextDraw(this.touchSurface, (View) getDecorView(), function0);
        }
    }

    private final ViewGroup findFirstViewGroupWithBackground(View view) {
        if (!(view instanceof ViewGroup)) {
            return null;
        }
        ViewGroup viewGroup = (ViewGroup) view;
        if (viewGroup.getBackground() != null) {
            return viewGroup;
        }
        int i = 0;
        int childCount = viewGroup.getChildCount();
        while (i < childCount) {
            int i2 = i + 1;
            View childAt = viewGroup.getChildAt(i);
            Intrinsics.checkNotNullExpressionValue(childAt, "view.getChildAt(i)");
            ViewGroup findFirstViewGroupWithBackground = findFirstViewGroupWithBackground(childAt);
            if (findFirstViewGroupWithBackground != null) {
                return findFirstViewGroupWithBackground;
            }
            i = i2;
        }
        return null;
    }

    /* access modifiers changed from: private */
    public final void maybeStartLaunchAnimation() {
        if (this.isTouchSurfaceGhostDrawn && this.isOriginalDialogViewLaidOut) {
            this.dialog.getWindow().addFlags(2);
            startAnimation(true, new AnimatedDialog$maybeStartLaunchAnimation$1(this), new AnimatedDialog$maybeStartLaunchAnimation$2(this));
        }
    }

    public final void onDialogDismissed() {
        if (!Intrinsics.areEqual((Object) Looper.myLooper(), (Object) Looper.getMainLooper())) {
            this.dialog.getContext().getMainExecutor().execute(new Runnable() {
                public final void run() {
                    AnimatedDialog.m105onDialogDismissed$lambda3(AnimatedDialog.this);
                }
            });
        } else if (this.isLaunching) {
            this.dismissRequested = true;
        } else if (!this.isDismissing) {
            this.isDismissing = true;
            hideDialogIntoView(new AnimatedDialog$onDialogDismissed$2(this));
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onDialogDismissed$lambda-3  reason: not valid java name */
    public static final void m105onDialogDismissed$lambda3(AnimatedDialog animatedDialog) {
        Intrinsics.checkNotNullParameter(animatedDialog, "this$0");
        animatedDialog.onDialogDismissed();
    }

    private final void hideDialogIntoView(Function1<? super Boolean, Unit> function1) {
        if (this.decorViewLayoutListener != null) {
            getDecorView().removeOnLayoutChangeListener(this.decorViewLayoutListener);
        }
        if (!shouldAnimateDialogIntoView()) {
            Log.i("DialogLaunchAnimator", "Skipping animation of dialog into the touch surface");
            View view = this.touchSurface;
            LaunchableView launchableView = view instanceof LaunchableView ? (LaunchableView) view : null;
            if (launchableView != null) {
                launchableView.setShouldBlockVisibilityChanges(false);
            }
            if (this.touchSurface.getVisibility() == 4) {
                this.touchSurface.setVisibility(0);
            }
            function1.invoke(false);
            this.onDialogDismissed.invoke(this);
            return;
        }
        startAnimation(false, new AnimatedDialog$hideDialogIntoView$1(this), new AnimatedDialog$hideDialogIntoView$2(this, function1));
    }

    static /* synthetic */ void startAnimation$default(AnimatedDialog animatedDialog, boolean z, Function0 function0, Function0 function02, int i, Object obj) {
        if ((i & 2) != 0) {
            function0 = AnimatedDialog$startAnimation$1.INSTANCE;
        }
        if ((i & 4) != 0) {
            function02 = AnimatedDialog$startAnimation$2.INSTANCE;
        }
        animatedDialog.startAnimation(z, function0, function02);
    }

    private final void startAnimation(boolean z, Function0<Unit> function0, Function0<Unit> function02) {
        View view;
        View view2;
        if (z) {
            view = this.touchSurface;
        } else {
            ViewGroup viewGroup = this.dialogContentWithBackground;
            Intrinsics.checkNotNull(viewGroup);
            view = viewGroup;
        }
        View view3 = view;
        if (z) {
            ViewGroup viewGroup2 = this.dialogContentWithBackground;
            Intrinsics.checkNotNull(viewGroup2);
            view2 = viewGroup2;
        } else {
            view2 = this.touchSurface;
        }
        GhostedViewLaunchAnimatorController ghostedViewLaunchAnimatorController = new GhostedViewLaunchAnimatorController(view3, (Integer) null, (InteractionJankMonitor) null, 6, (DefaultConstructorMarker) null);
        GhostedViewLaunchAnimatorController ghostedViewLaunchAnimatorController2 = new GhostedViewLaunchAnimatorController(view2, (Integer) null, (InteractionJankMonitor) null, 6, (DefaultConstructorMarker) null);
        ghostedViewLaunchAnimatorController.setLaunchContainer(getDecorView());
        ghostedViewLaunchAnimatorController2.setLaunchContainer(getDecorView());
        LaunchAnimator.State createAnimatorState = ghostedViewLaunchAnimatorController2.createAnimatorState();
        AnimatedDialog$startAnimation$controller$1 animatedDialog$startAnimation$controller$1 = new AnimatedDialog$startAnimation$controller$1(ghostedViewLaunchAnimatorController, ghostedViewLaunchAnimatorController2, function0, function02, createAnimatorState);
        LaunchAnimator.startAnimation$default(this.launchAnimator, animatedDialog$startAnimation$controller$1, createAnimatorState, this.originalDialogBackgroundColor, false, 8, (Object) null);
    }

    private final boolean shouldAnimateDialogIntoView() {
        if (this.exitAnimationDisabled || !this.dialog.isShowing() || this.dreamManager.isDreaming() || this.touchSurface.getVisibility() != 4 || !this.touchSurface.isAttachedToWindow()) {
            return false;
        }
        ViewParent parent = this.touchSurface.getParent();
        View view = parent instanceof View ? (View) parent : null;
        if (view == null) {
            return true;
        }
        return view.isShown();
    }

    @Metadata(d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\t\u0018\u0000 \u00142\u00020\u0001:\u0001\u0014B\u0005¢\u0006\u0002\u0010\u0002JP\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\f2\u0006\u0010\u000e\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\f2\u0006\u0010\u0011\u001a\u00020\f2\u0006\u0010\u0012\u001a\u00020\f2\u0006\u0010\u0013\u001a\u00020\fH\u0016R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u000e¢\u0006\u0002\n\u0000¨\u0006\u0015"}, d2 = {"Lcom/android/systemui/animation/AnimatedDialog$AnimatedBoundsLayoutListener;", "Landroid/view/View$OnLayoutChangeListener;", "()V", "currentAnimator", "Landroid/animation/ValueAnimator;", "lastBounds", "Landroid/graphics/Rect;", "onLayoutChange", "", "view", "Landroid/view/View;", "left", "", "top", "right", "bottom", "oldLeft", "oldTop", "oldRight", "oldBottom", "Companion", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: DialogLaunchAnimator.kt */
    public static final class AnimatedBoundsLayoutListener implements View.OnLayoutChangeListener {
        private static final long ANIMATION_DURATION = 500;
        public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
        /* access modifiers changed from: private */
        public ValueAnimator currentAnimator;
        private Rect lastBounds;

        @Metadata(d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000¨\u0006\u0005"}, d2 = {"Lcom/android/systemui/animation/AnimatedDialog$AnimatedBoundsLayoutListener$Companion;", "", "()V", "ANIMATION_DURATION", "", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
        /* compiled from: DialogLaunchAnimator.kt */
        public static final class Companion {
            public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
                this();
            }

            private Companion() {
            }
        }

        public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            View view2 = view;
            int i9 = i5;
            int i10 = i6;
            int i11 = i7;
            int i12 = i8;
            Intrinsics.checkNotNullParameter(view2, "view");
            int i13 = i2;
            int i14 = i3;
            if (i == i9 && i13 == i10) {
                int i15 = i4;
                if (i14 == i11 && i15 == i12) {
                    Rect rect = this.lastBounds;
                    if (rect != null) {
                        view2.setLeft(rect.left);
                        view2.setTop(rect.top);
                        view2.setRight(rect.right);
                        view2.setBottom(rect.bottom);
                        return;
                    }
                    return;
                }
            } else {
                int i16 = i4;
            }
            if (this.lastBounds == null) {
                this.lastBounds = new Rect(i9, i10, i11, i12);
            }
            Rect rect2 = this.lastBounds;
            Intrinsics.checkNotNull(rect2);
            int i17 = rect2.left;
            int i18 = rect2.top;
            int i19 = rect2.right;
            int i20 = rect2.bottom;
            ValueAnimator valueAnimator = this.currentAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            this.currentAnimator = null;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            ofFloat.setDuration(ANIMATION_DURATION);
            ofFloat.setInterpolator(Interpolators.STANDARD);
            ofFloat.addListener(new AnimatedDialog$AnimatedBoundsLayoutListener$onLayoutChange$animator$1$1(this));
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(rect2, i17, i, i18, i2, i19, i3, i20, i4, view) {
                public final /* synthetic */ Rect f$0;
                public final /* synthetic */ int f$1;
                public final /* synthetic */ int f$2;
                public final /* synthetic */ int f$3;
                public final /* synthetic */ int f$4;
                public final /* synthetic */ int f$5;
                public final /* synthetic */ int f$6;
                public final /* synthetic */ int f$7;
                public final /* synthetic */ int f$8;
                public final /* synthetic */ View f$9;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                    this.f$7 = r8;
                    this.f$8 = r9;
                    this.f$9 = r10;
                }

                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    AnimatedDialog.AnimatedBoundsLayoutListener.m109onLayoutChange$lambda2$lambda1(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, valueAnimator);
                }
            });
            this.currentAnimator = ofFloat;
            ofFloat.start();
        }

        /* access modifiers changed from: private */
        /* renamed from: onLayoutChange$lambda-2$lambda-1  reason: not valid java name */
        public static final void m109onLayoutChange$lambda2$lambda1(Rect rect, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, View view, ValueAnimator valueAnimator) {
            Intrinsics.checkNotNullParameter(rect, "$bounds");
            Intrinsics.checkNotNullParameter(view, "$view");
            float animatedFraction = valueAnimator.getAnimatedFraction();
            rect.left = MathKt.roundToInt(MathUtils.lerp(i, i2, animatedFraction));
            rect.top = MathKt.roundToInt(MathUtils.lerp(i3, i4, animatedFraction));
            rect.right = MathKt.roundToInt(MathUtils.lerp(i5, i6, animatedFraction));
            rect.bottom = MathKt.roundToInt(MathUtils.lerp(i7, i8, animatedFraction));
            view.setLeft(rect.left);
            view.setTop(rect.top);
            view.setRight(rect.right);
            view.setBottom(rect.bottom);
        }
    }

    public final View prepareForStackDismiss() {
        AnimatedDialog animatedDialog = this.parentAnimatedDialog;
        if (animatedDialog == null) {
            return this.touchSurface;
        }
        animatedDialog.exitAnimationDisabled = true;
        animatedDialog.dialog.hide();
        View prepareForStackDismiss = this.parentAnimatedDialog.prepareForStackDismiss();
        this.parentAnimatedDialog.dialog.dismiss();
        prepareForStackDismiss.setVisibility(4);
        return prepareForStackDismiss;
    }
}
