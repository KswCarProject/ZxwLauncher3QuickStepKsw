package com.android.systemui.animation;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Insets;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.view.GhostView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.widget.FrameLayout;
import com.android.internal.jank.InteractionJankMonitor;
import com.android.launcher3.testing.TestProtocol;
import com.android.systemui.animation.ActivityLaunchAnimator;
import com.android.systemui.animation.LaunchAnimator;
import java.util.LinkedList;
import java.util.Objects;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0015\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0014\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\n\b\u0016\u0018\u0000 A2\u00020\u0001:\u0002ABB\u0017\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006B%\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\n¢\u0006\u0002\u0010\u000bJ\b\u00100\u001a\u00020!H\u0016J\u000e\u00101\u001a\u0002022\u0006\u00103\u001a\u00020!J\b\u00104\u001a\u000205H\u0014J\b\u00106\u001a\u000205H\u0014J\u0010\u00107\u001a\u0002022\u0006\u00108\u001a\u000209H\u0016J \u0010:\u001a\u0002022\u0006\u00103\u001a\u00020!2\u0006\u0010;\u001a\u0002052\u0006\u0010<\u001a\u000205H\u0016J\u0010\u0010=\u001a\u0002022\u0006\u00108\u001a\u000209H\u0016J \u0010>\u001a\u0002022\u0006\u0010\f\u001a\u00020\r2\u0006\u0010?\u001a\u0002052\u0006\u0010@\u001a\u000205H\u0014R\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u000e¢\u0006\u0002\n\u0000R#\u0010\u0010\u001a\n \u0012*\u0004\u0018\u00010\u00110\u00118BX\u0002¢\u0006\f\n\u0004\b\u0015\u0010\u0016\u001a\u0004\b\u0013\u0010\u0014R\u0010\u0010\u0017\u001a\u0004\u0018\u00010\u0018X\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\b\u001a\u0004\u0018\u00010\u0005X\u0004¢\u0006\u0004\n\u0002\u0010\u0019R\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u001bX\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\u001dX\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u001fX\u0004¢\u0006\u0002\n\u0000R\u000e\u0010 \u001a\u00020!X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020#X\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u000e¢\u0006\u0002\n\u0000R\u001a\u0010$\u001a\u00020%X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b&\u0010'\"\u0004\b(\u0010)R\u000e\u0010*\u001a\u00020\u001fX\u0004¢\u0006\u0002\n\u0000R\u0014\u0010+\u001a\u00020,8BX\u0004¢\u0006\u0006\u001a\u0004\b-\u0010.R\u000e\u0010/\u001a\u00020\u0005X\u000e¢\u0006\u0002\n\u0000¨\u0006C"}, d2 = {"Lcom/android/systemui/animation/GhostedViewLaunchAnimatorController;", "Lcom/android/systemui/animation/ActivityLaunchAnimator$Controller;", "view", "Landroid/view/View;", "type", "", "(Landroid/view/View;I)V", "ghostedView", "cujType", "interactionJankMonitor", "Lcom/android/internal/jank/InteractionJankMonitor;", "(Landroid/view/View;Ljava/lang/Integer;Lcom/android/internal/jank/InteractionJankMonitor;)V", "background", "Landroid/graphics/drawable/Drawable;", "backgroundDrawable", "Lcom/android/systemui/animation/GhostedViewLaunchAnimatorController$WrappedDrawable;", "backgroundInsets", "Landroid/graphics/Insets;", "kotlin.jvm.PlatformType", "getBackgroundInsets", "()Landroid/graphics/Insets;", "backgroundInsets$delegate", "Lkotlin/Lazy;", "backgroundView", "Landroid/widget/FrameLayout;", "Ljava/lang/Integer;", "ghostView", "Landroid/view/GhostView;", "ghostViewMatrix", "Landroid/graphics/Matrix;", "ghostedViewLocation", "", "ghostedViewState", "Lcom/android/systemui/animation/LaunchAnimator$State;", "initialGhostViewMatrixValues", "", "launchContainer", "Landroid/view/ViewGroup;", "getLaunchContainer", "()Landroid/view/ViewGroup;", "setLaunchContainer", "(Landroid/view/ViewGroup;)V", "launchContainerLocation", "launchContainerOverlay", "Landroid/view/ViewGroupOverlay;", "getLaunchContainerOverlay", "()Landroid/view/ViewGroupOverlay;", "startBackgroundAlpha", "createAnimatorState", "fillGhostedViewState", "", "state", "getCurrentBottomCornerRadius", "", "getCurrentTopCornerRadius", "onLaunchAnimationEnd", "isExpandingFullyAbove", "", "onLaunchAnimationProgress", "progress", "linearProgress", "onLaunchAnimationStart", "setBackgroundCornerRadius", "topCornerRadius", "bottomCornerRadius", "Companion", "WrappedDrawable", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: GhostedViewLaunchAnimatorController.kt */
public class GhostedViewLaunchAnimatorController implements ActivityLaunchAnimator.Controller {
    private static final int CORNER_RADIUS_BOTTOM_INDEX = 4;
    private static final int CORNER_RADIUS_TOP_INDEX = 0;
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    /* access modifiers changed from: private */
    public final Drawable background;
    private WrappedDrawable backgroundDrawable;
    private final Lazy backgroundInsets$delegate;
    private FrameLayout backgroundView;
    private final Integer cujType;
    private GhostView ghostView;
    private final Matrix ghostViewMatrix;
    private final View ghostedView;
    private final int[] ghostedViewLocation;
    private final LaunchAnimator.State ghostedViewState;
    private final float[] initialGhostViewMatrixValues;
    private InteractionJankMonitor interactionJankMonitor;
    private ViewGroup launchContainer;
    private final int[] launchContainerLocation;
    private int startBackgroundAlpha;

    public GhostedViewLaunchAnimatorController(View view, Integer num, InteractionJankMonitor interactionJankMonitor2) {
        Intrinsics.checkNotNullParameter(view, "ghostedView");
        this.ghostedView = view;
        this.cujType = num;
        this.interactionJankMonitor = interactionJankMonitor2;
        View rootView = view.getRootView();
        Objects.requireNonNull(rootView, "null cannot be cast to non-null type android.view.ViewGroup");
        this.launchContainer = (ViewGroup) rootView;
        this.launchContainerLocation = new int[2];
        float[] fArr = new float[9];
        for (int i = 0; i < 9; i++) {
            fArr[i] = 0.0f;
        }
        this.initialGhostViewMatrixValues = fArr;
        this.ghostViewMatrix = new Matrix();
        this.backgroundInsets$delegate = LazyKt.lazy(new GhostedViewLaunchAnimatorController$backgroundInsets$2(this));
        this.startBackgroundAlpha = 255;
        this.ghostedViewLocation = new int[2];
        this.ghostedViewState = new LaunchAnimator.State(0, 0, 0, 0, 0.0f, 0.0f, 63, (DefaultConstructorMarker) null);
        this.background = _init_$findBackground(this.ghostedView);
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public /* synthetic */ GhostedViewLaunchAnimatorController(View view, Integer num, InteractionJankMonitor interactionJankMonitor2, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(view, (i & 2) != 0 ? null : num, (i & 4) != 0 ? null : interactionJankMonitor2);
    }

    public View getOpeningWindowSyncView() {
        return ActivityLaunchAnimator.Controller.DefaultImpls.getOpeningWindowSyncView(this);
    }

    public boolean isDialogLaunch() {
        return ActivityLaunchAnimator.Controller.DefaultImpls.isDialogLaunch(this);
    }

    public void onIntentStarted(boolean z) {
        ActivityLaunchAnimator.Controller.DefaultImpls.onIntentStarted(this, z);
    }

    public void onLaunchAnimationCancelled() {
        ActivityLaunchAnimator.Controller.DefaultImpls.onLaunchAnimationCancelled(this);
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public GhostedViewLaunchAnimatorController(View view, int i) {
        this(view, Integer.valueOf(i), (InteractionJankMonitor) null);
        Intrinsics.checkNotNullParameter(view, "view");
    }

    public ViewGroup getLaunchContainer() {
        return this.launchContainer;
    }

    public void setLaunchContainer(ViewGroup viewGroup) {
        Intrinsics.checkNotNullParameter(viewGroup, "<set-?>");
        this.launchContainer = viewGroup;
    }

    private final ViewGroupOverlay getLaunchContainerOverlay() {
        ViewGroupOverlay overlay = getLaunchContainer().getOverlay();
        Intrinsics.checkNotNullExpressionValue(overlay, "launchContainer.overlay");
        return overlay;
    }

    private final Insets getBackgroundInsets() {
        return (Insets) this.backgroundInsets$delegate.getValue();
    }

    private static final Drawable _init_$findBackground(View view) {
        if (view.getBackground() != null) {
            return view.getBackground();
        }
        LinkedList linkedList = new LinkedList();
        linkedList.add(view);
        while (!linkedList.isEmpty()) {
            View view2 = (View) linkedList.removeFirst();
            if (view2.getBackground() != null) {
                return view2.getBackground();
            }
            if (view2 instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view2;
                int childCount = viewGroup.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    linkedList.add(viewGroup.getChildAt(i));
                }
            }
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public void setBackgroundCornerRadius(Drawable drawable, float f, float f2) {
        Intrinsics.checkNotNullParameter(drawable, "background");
        WrappedDrawable wrappedDrawable = this.backgroundDrawable;
        if (wrappedDrawable != null) {
            wrappedDrawable.setBackgroundRadius(f, f2);
        }
    }

    /* access modifiers changed from: protected */
    public float getCurrentTopCornerRadius() {
        GradientDrawable findGradientDrawable;
        Drawable drawable = this.background;
        if (drawable == null || (findGradientDrawable = Companion.findGradientDrawable(drawable)) == null) {
            return 0.0f;
        }
        float[] cornerRadii = findGradientDrawable.getCornerRadii();
        Float valueOf = cornerRadii == null ? null : Float.valueOf(cornerRadii[0]);
        return valueOf == null ? findGradientDrawable.getCornerRadius() : valueOf.floatValue();
    }

    /* access modifiers changed from: protected */
    public float getCurrentBottomCornerRadius() {
        GradientDrawable findGradientDrawable;
        Drawable drawable = this.background;
        if (drawable == null || (findGradientDrawable = Companion.findGradientDrawable(drawable)) == null) {
            return 0.0f;
        }
        float[] cornerRadii = findGradientDrawable.getCornerRadii();
        Float valueOf = cornerRadii == null ? null : Float.valueOf(cornerRadii[4]);
        return valueOf == null ? findGradientDrawable.getCornerRadius() : valueOf.floatValue();
    }

    public LaunchAnimator.State createAnimatorState() {
        LaunchAnimator.State state = new LaunchAnimator.State(0, 0, 0, 0, getCurrentTopCornerRadius(), getCurrentBottomCornerRadius(), 15, (DefaultConstructorMarker) null);
        fillGhostedViewState(state);
        return state;
    }

    public final void fillGhostedViewState(LaunchAnimator.State state) {
        Intrinsics.checkNotNullParameter(state, TestProtocol.STATE_FIELD);
        this.ghostedView.getLocationOnScreen(this.ghostedViewLocation);
        Insets backgroundInsets = getBackgroundInsets();
        state.setTop(this.ghostedViewLocation[1] + backgroundInsets.top);
        state.setBottom((this.ghostedViewLocation[1] + this.ghostedView.getHeight()) - backgroundInsets.bottom);
        state.setLeft(this.ghostedViewLocation[0] + backgroundInsets.left);
        state.setRight((this.ghostedViewLocation[0] + this.ghostedView.getWidth()) - backgroundInsets.right);
    }

    public void onLaunchAnimationStart(boolean z) {
        Matrix matrix;
        if (!(this.ghostedView.getParent() instanceof ViewGroup)) {
            Log.w("GhostedViewLaunchAnimatorController", "Skipping animation as ghostedView is not attached to a ViewGroup");
            return;
        }
        this.backgroundView = new FrameLayout(getLaunchContainer().getContext());
        getLaunchContainerOverlay().add(this.backgroundView);
        Drawable drawable = this.background;
        this.startBackgroundAlpha = drawable == null ? 255 : drawable.getAlpha();
        WrappedDrawable wrappedDrawable = new WrappedDrawable(this.background);
        this.backgroundDrawable = wrappedDrawable;
        FrameLayout frameLayout = this.backgroundView;
        if (frameLayout != null) {
            frameLayout.setBackground(wrappedDrawable);
        }
        GhostView addGhost = GhostView.addGhost(this.ghostedView, getLaunchContainer());
        this.ghostView = addGhost;
        if (addGhost == null) {
            matrix = null;
        } else {
            matrix = addGhost.getAnimationMatrix();
        }
        if (matrix == null) {
            matrix = Matrix.IDENTITY_MATRIX;
        }
        matrix.getValues(this.initialGhostViewMatrixValues);
        Integer num = this.cujType;
        if (num != null) {
            int intValue = num.intValue();
            InteractionJankMonitor interactionJankMonitor2 = this.interactionJankMonitor;
            if (interactionJankMonitor2 != null) {
                interactionJankMonitor2.begin(this.ghostedView, intValue);
            }
        }
    }

    public void onLaunchAnimationProgress(LaunchAnimator.State state, float f, float f2) {
        Intrinsics.checkNotNullParameter(state, TestProtocol.STATE_FIELD);
        GhostView ghostView2 = this.ghostView;
        if (ghostView2 != null) {
            FrameLayout frameLayout = this.backgroundView;
            Intrinsics.checkNotNull(frameLayout);
            if (state.getVisible()) {
                if (ghostView2.getVisibility() == 4) {
                    ghostView2.setVisibility(0);
                    frameLayout.setVisibility(0);
                }
                fillGhostedViewState(this.ghostedViewState);
                int left = state.getLeft() - this.ghostedViewState.getLeft();
                int right = state.getRight() - this.ghostedViewState.getRight();
                int top = state.getTop() - this.ghostedViewState.getTop();
                int bottom = state.getBottom() - this.ghostedViewState.getBottom();
                float min = Math.min(((float) state.getWidth()) / ((float) this.ghostedViewState.getWidth()), ((float) state.getHeight()) / ((float) this.ghostedViewState.getHeight()));
                if (this.ghostedView.getParent() instanceof ViewGroup) {
                    GhostView.calculateMatrix(this.ghostedView, getLaunchContainer(), this.ghostViewMatrix);
                }
                getLaunchContainer().getLocationOnScreen(this.launchContainerLocation);
                this.ghostViewMatrix.postScale(min, min, this.ghostedViewState.getCenterX() - ((float) this.launchContainerLocation[0]), this.ghostedViewState.getCenterY() - ((float) this.launchContainerLocation[1]));
                this.ghostViewMatrix.postTranslate(((float) (left + right)) / 2.0f, ((float) (top + bottom)) / 2.0f);
                ghostView2.setAnimationMatrix(this.ghostViewMatrix);
                Insets backgroundInsets = getBackgroundInsets();
                int top2 = state.getTop() - backgroundInsets.top;
                int left2 = state.getLeft() - backgroundInsets.left;
                int right2 = state.getRight() + backgroundInsets.right;
                int bottom2 = state.getBottom() + backgroundInsets.bottom;
                frameLayout.setTop(top2 - this.launchContainerLocation[1]);
                frameLayout.setBottom(bottom2 - this.launchContainerLocation[1]);
                frameLayout.setLeft(left2 - this.launchContainerLocation[0]);
                frameLayout.setRight(right2 - this.launchContainerLocation[0]);
                WrappedDrawable wrappedDrawable = this.backgroundDrawable;
                Intrinsics.checkNotNull(wrappedDrawable);
                Drawable wrapped = wrappedDrawable.getWrapped();
                if (wrapped != null) {
                    setBackgroundCornerRadius(wrapped, state.getTopCornerRadius(), state.getBottomCornerRadius());
                }
            } else if (ghostView2.getVisibility() == 0) {
                ghostView2.setVisibility(4);
                this.ghostedView.setTransitionVisibility(4);
                frameLayout.setVisibility(4);
            }
        }
    }

    public void onLaunchAnimationEnd(boolean z) {
        if (this.ghostView != null) {
            Integer num = this.cujType;
            if (num != null) {
                int intValue = num.intValue();
                InteractionJankMonitor interactionJankMonitor2 = this.interactionJankMonitor;
                if (interactionJankMonitor2 != null) {
                    interactionJankMonitor2.end(intValue);
                }
            }
            WrappedDrawable wrappedDrawable = this.backgroundDrawable;
            Drawable wrapped = wrappedDrawable == null ? null : wrappedDrawable.getWrapped();
            if (wrapped != null) {
                wrapped.setAlpha(this.startBackgroundAlpha);
            }
            GhostView.removeGhost(this.ghostedView);
            getLaunchContainerOverlay().remove(this.backgroundView);
            this.ghostedView.setVisibility(4);
            this.ghostedView.setVisibility(0);
            this.ghostedView.invalidate();
        }
    }

    @Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u0006\u0010\b\u001a\u00020\tR\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000¨\u0006\n"}, d2 = {"Lcom/android/systemui/animation/GhostedViewLaunchAnimatorController$Companion;", "", "()V", "CORNER_RADIUS_BOTTOM_INDEX", "", "CORNER_RADIUS_TOP_INDEX", "findGradientDrawable", "Landroid/graphics/drawable/GradientDrawable;", "drawable", "Landroid/graphics/drawable/Drawable;", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: GhostedViewLaunchAnimatorController.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final GradientDrawable findGradientDrawable(Drawable drawable) {
            Intrinsics.checkNotNullParameter(drawable, "drawable");
            if (drawable instanceof GradientDrawable) {
                return (GradientDrawable) drawable;
            }
            if (drawable instanceof InsetDrawable) {
                Drawable drawable2 = ((InsetDrawable) drawable).getDrawable();
                if (drawable2 == null) {
                    return null;
                }
                return GhostedViewLaunchAnimatorController.Companion.findGradientDrawable(drawable2);
            }
            if (drawable instanceof LayerDrawable) {
                int i = 0;
                LayerDrawable layerDrawable = (LayerDrawable) drawable;
                int numberOfLayers = layerDrawable.getNumberOfLayers();
                while (i < numberOfLayers) {
                    int i2 = i + 1;
                    Drawable drawable3 = layerDrawable.getDrawable(i);
                    if (drawable3 instanceof GradientDrawable) {
                        return (GradientDrawable) drawable3;
                    }
                    i = i2;
                }
            }
            return null;
        }
    }

    @Metadata(d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0014\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0002\u0018\u00002\u00020\u0001B\u000f\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0001¢\u0006\u0002\u0010\u0003J\b\u0010\r\u001a\u00020\u000eH\u0002J\u0018\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u0005H\u0002J\u0010\u0010\u0011\u001a\u00020\u000e2\u0006\u0010\u0012\u001a\u00020\u0013H\u0016J\b\u0010\u0014\u001a\u00020\u0007H\u0016J\b\u0010\u0015\u001a\u00020\u0007H\u0016J\b\u0010\u0016\u001a\u00020\u000eH\u0002J\u0010\u0010\u0017\u001a\u00020\u000e2\u0006\u0010\u0018\u001a\u00020\u0001H\u0002J\u0010\u0010\u0019\u001a\u00020\u000e2\u0006\u0010\u001a\u001a\u00020\u0007H\u0016J\u0016\u0010\u001b\u001a\u00020\u000e2\u0006\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001dJ\u0012\u0010\u001f\u001a\u00020\u000e2\b\u0010 \u001a\u0004\u0018\u00010!H\u0016J \u0010\"\u001a\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\u00052\u0006\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001dH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0005X\u000e¢\u0006\u0002\n\u0000R\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0001¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f¨\u0006#"}, d2 = {"Lcom/android/systemui/animation/GhostedViewLaunchAnimatorController$WrappedDrawable;", "Landroid/graphics/drawable/Drawable;", "wrapped", "(Landroid/graphics/drawable/Drawable;)V", "cornerRadii", "", "currentAlpha", "", "previousBounds", "Landroid/graphics/Rect;", "previousCornerRadii", "getWrapped", "()Landroid/graphics/drawable/Drawable;", "applyBackgroundRadii", "", "drawable", "radii", "draw", "canvas", "Landroid/graphics/Canvas;", "getAlpha", "getOpacity", "restoreBackgroundRadii", "savePreviousBackgroundRadii", "background", "setAlpha", "alpha", "setBackgroundRadius", "topCornerRadius", "", "bottomCornerRadius", "setColorFilter", "filter", "Landroid/graphics/ColorFilter;", "updateRadii", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: GhostedViewLaunchAnimatorController.kt */
    private static final class WrappedDrawable extends Drawable {
        private float[] cornerRadii;
        private int currentAlpha = 255;
        private Rect previousBounds = new Rect();
        private float[] previousCornerRadii;
        private final Drawable wrapped;

        public WrappedDrawable(Drawable drawable) {
            this.wrapped = drawable;
            float[] fArr = new float[8];
            for (int i = 0; i < 8; i++) {
                fArr[i] = -1.0f;
            }
            this.cornerRadii = fArr;
            this.previousCornerRadii = new float[8];
        }

        public final Drawable getWrapped() {
            return this.wrapped;
        }

        public void draw(Canvas canvas) {
            Intrinsics.checkNotNullParameter(canvas, "canvas");
            Drawable drawable = this.wrapped;
            if (drawable != null) {
                drawable.copyBounds(this.previousBounds);
                drawable.setAlpha(this.currentAlpha);
                drawable.setBounds(getBounds());
                applyBackgroundRadii();
                drawable.draw(canvas);
                drawable.setAlpha(0);
                drawable.setBounds(this.previousBounds);
                restoreBackgroundRadii();
            }
        }

        public void setAlpha(int i) {
            if (i != this.currentAlpha) {
                this.currentAlpha = i;
                invalidateSelf();
            }
        }

        public int getAlpha() {
            return this.currentAlpha;
        }

        public int getOpacity() {
            Drawable drawable = this.wrapped;
            if (drawable == null) {
                return -2;
            }
            int alpha = drawable.getAlpha();
            drawable.setAlpha(this.currentAlpha);
            int opacity = drawable.getOpacity();
            drawable.setAlpha(alpha);
            return opacity;
        }

        public void setColorFilter(ColorFilter colorFilter) {
            Drawable drawable = this.wrapped;
            if (drawable != null) {
                drawable.setColorFilter(colorFilter);
            }
        }

        public final void setBackgroundRadius(float f, float f2) {
            updateRadii(this.cornerRadii, f, f2);
            invalidateSelf();
        }

        private final void updateRadii(float[] fArr, float f, float f2) {
            fArr[0] = f;
            fArr[1] = f;
            fArr[2] = f;
            fArr[3] = f;
            fArr[4] = f2;
            fArr[5] = f2;
            fArr[6] = f2;
            fArr[7] = f2;
        }

        private final void applyBackgroundRadii() {
            Drawable drawable;
            if (this.cornerRadii[0] >= 0.0f && (drawable = this.wrapped) != null) {
                savePreviousBackgroundRadii(drawable);
                applyBackgroundRadii(this.wrapped, this.cornerRadii);
            }
        }

        private final void savePreviousBackgroundRadii(Drawable drawable) {
            GradientDrawable findGradientDrawable = GhostedViewLaunchAnimatorController.Companion.findGradientDrawable(drawable);
            if (findGradientDrawable != null) {
                float[] cornerRadii2 = findGradientDrawable.getCornerRadii();
                if (cornerRadii2 != null) {
                    ArraysKt.copyInto$default(cornerRadii2, this.previousCornerRadii, 0, 0, 0, 14, (Object) null);
                    return;
                }
                float cornerRadius = findGradientDrawable.getCornerRadius();
                updateRadii(this.previousCornerRadii, cornerRadius, cornerRadius);
            }
        }

        private final void applyBackgroundRadii(Drawable drawable, float[] fArr) {
            if (drawable instanceof GradientDrawable) {
                ((GradientDrawable) drawable).setCornerRadii(fArr);
            } else if (drawable instanceof InsetDrawable) {
                Drawable drawable2 = ((InsetDrawable) drawable).getDrawable();
                if (drawable2 != null) {
                    applyBackgroundRadii(drawable2, fArr);
                }
            } else if (drawable instanceof LayerDrawable) {
                int i = 0;
                LayerDrawable layerDrawable = (LayerDrawable) drawable;
                int numberOfLayers = layerDrawable.getNumberOfLayers();
                while (i < numberOfLayers) {
                    int i2 = i + 1;
                    Drawable drawable3 = layerDrawable.getDrawable(i);
                    GradientDrawable gradientDrawable = drawable3 instanceof GradientDrawable ? (GradientDrawable) drawable3 : null;
                    if (gradientDrawable != null) {
                        gradientDrawable.setCornerRadii(fArr);
                    }
                    i = i2;
                }
            }
        }

        private final void restoreBackgroundRadii() {
            Drawable drawable;
            if (this.cornerRadii[0] >= 0.0f && (drawable = this.wrapped) != null) {
                applyBackgroundRadii(drawable, this.previousCornerRadii);
            }
        }
    }
}
