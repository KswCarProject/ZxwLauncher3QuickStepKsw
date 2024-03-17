package com.android.launcher3.taskbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Outline;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewOutlineProvider;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.RoundedRectRevealOutlineProvider;
import com.android.launcher3.taskbar.TaskbarControllers;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.MultiValueAlpha;
import com.android.quickstep.AnimatedFloat;
import com.android.systemui.shared.navigationbar.RegionSamplingHelper;
import java.io.PrintWriter;

public class StashedHandleViewController implements TaskbarControllers.LoggableTaskbarController {
    public static final int ALPHA_INDEX_HOME_DISABLED = 1;
    public static final int ALPHA_INDEX_STASHED = 0;
    private static final int NUM_ALPHA_CHANNELS = 2;
    private static final String SHARED_PREFS_STASHED_HANDLE_REGION_DARK_KEY = "stashed_handle_region_is_dark";
    private final TaskbarActivityContext mActivity;
    private TaskbarControllers mControllers;
    /* access modifiers changed from: private */
    public final SharedPreferences mPrefs;
    private final RegionSamplingHelper mRegionSamplingHelper;
    /* access modifiers changed from: private */
    public float mStartProgressForNextRevealAnim;
    /* access modifiers changed from: private */
    public final Rect mStashedHandleBounds = new Rect();
    /* access modifiers changed from: private */
    public final int mStashedHandleHeight;
    /* access modifiers changed from: private */
    public float mStashedHandleRadius;
    /* access modifiers changed from: private */
    public final StashedHandleView mStashedHandleView;
    /* access modifiers changed from: private */
    public final int mStashedHandleWidth;
    private final MultiValueAlpha mTaskbarStashedHandleAlpha;
    private final AnimatedFloat mTaskbarStashedHandleHintScale = new AnimatedFloat(new Runnable() {
        public final void run() {
            StashedHandleViewController.this.updateStashedHandleHintScale();
        }
    });
    private boolean mWasLastRevealAnimReversed;

    public StashedHandleViewController(TaskbarActivityContext taskbarActivityContext, StashedHandleView stashedHandleView) {
        this.mActivity = taskbarActivityContext;
        SharedPreferences prefs = Utilities.getPrefs(taskbarActivityContext);
        this.mPrefs = prefs;
        this.mStashedHandleView = stashedHandleView;
        MultiValueAlpha multiValueAlpha = new MultiValueAlpha(stashedHandleView, 2);
        this.mTaskbarStashedHandleAlpha = multiValueAlpha;
        multiValueAlpha.setUpdateVisibility(true);
        stashedHandleView.updateHandleColor(prefs.getBoolean(SHARED_PREFS_STASHED_HANDLE_REGION_DARK_KEY, false), false);
        Resources resources = taskbarActivityContext.getResources();
        this.mStashedHandleWidth = resources.getDimensionPixelSize(R.dimen.taskbar_stashed_handle_width);
        this.mStashedHandleHeight = resources.getDimensionPixelSize(R.dimen.taskbar_stashed_handle_height);
        this.mRegionSamplingHelper = new RegionSamplingHelper(stashedHandleView, new RegionSamplingHelper.SamplingCallback() {
            public void onRegionDarknessChanged(boolean z) {
                StashedHandleViewController.this.mStashedHandleView.updateHandleColor(z, true);
                StashedHandleViewController.this.mPrefs.edit().putBoolean(StashedHandleViewController.SHARED_PREFS_STASHED_HANDLE_REGION_DARK_KEY, z).apply();
            }

            public Rect getSampledRegion(View view) {
                return StashedHandleViewController.this.mStashedHandleView.getSampledRegion();
            }
        }, Executors.UI_HELPER_EXECUTOR);
    }

    public void init(TaskbarControllers taskbarControllers) {
        this.mControllers = taskbarControllers;
        this.mStashedHandleView.getLayoutParams().height = this.mActivity.getDeviceProfile().taskbarSize;
        this.mTaskbarStashedHandleAlpha.getProperty(0).setValue(0.0f);
        this.mTaskbarStashedHandleHintScale.updateValue(1.0f);
        final int stashedHeight = this.mControllers.taskbarStashController.getStashedHeight();
        this.mStashedHandleView.setOutlineProvider(new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                int width = view.getWidth() / 2;
                int height = view.getHeight() - (stashedHeight / 2);
                StashedHandleViewController.this.mStashedHandleBounds.set(width - (StashedHandleViewController.this.mStashedHandleWidth / 2), height - (StashedHandleViewController.this.mStashedHandleHeight / 2), width + (StashedHandleViewController.this.mStashedHandleWidth / 2), height + (StashedHandleViewController.this.mStashedHandleHeight / 2));
                StashedHandleViewController.this.mStashedHandleView.updateSampledRegion(StashedHandleViewController.this.mStashedHandleBounds);
                float unused = StashedHandleViewController.this.mStashedHandleRadius = ((float) view.getHeight()) / 2.0f;
                outline.setRoundRect(StashedHandleViewController.this.mStashedHandleBounds, StashedHandleViewController.this.mStashedHandleRadius);
            }
        });
        this.mStashedHandleView.addOnLayoutChangeListener(new View.OnLayoutChangeListener(stashedHeight) {
            public final /* synthetic */ int f$0;

            {
                this.f$0 = r1;
            }

            public final void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                StashedHandleViewController.lambda$init$0(this.f$0, view, i, i2, i3, i4, i5, i6, i7, i8);
            }
        });
    }

    static /* synthetic */ void lambda$init$0(int i, View view, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
        int height = view.getHeight() - (i / 2);
        view.setPivotX((float) (view.getWidth() / 2));
        view.setPivotY((float) height);
    }

    public void onDestroy() {
        this.mRegionSamplingHelper.stopAndDestroy();
    }

    public MultiValueAlpha getStashedHandleAlpha() {
        return this.mTaskbarStashedHandleAlpha;
    }

    public AnimatedFloat getStashedHandleHintScale() {
        return this.mTaskbarStashedHandleHintScale;
    }

    public Animator createRevealAnimToIsStashed(boolean z) {
        float f = this.mStashedHandleRadius;
        RoundedRectRevealOutlineProvider roundedRectRevealOutlineProvider = new RoundedRectRevealOutlineProvider(f, f, this.mControllers.taskbarViewController.getIconLayoutBounds(), this.mStashedHandleBounds);
        boolean z2 = true;
        boolean z3 = !z;
        if (this.mWasLastRevealAnimReversed == z3) {
            z2 = false;
        }
        this.mWasLastRevealAnimReversed = z3;
        if (z2) {
            this.mStartProgressForNextRevealAnim = 1.0f - this.mStartProgressForNextRevealAnim;
        }
        ValueAnimator createRevealAnimator = roundedRectRevealOutlineProvider.createRevealAnimator(this.mStashedHandleView, z3, this.mStartProgressForNextRevealAnim);
        createRevealAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                float unused = StashedHandleViewController.this.mStartProgressForNextRevealAnim = ((ValueAnimator) animator).getAnimatedFraction();
            }
        });
        return createRevealAnimator;
    }

    public void onIsStashedChanged(boolean z) {
        this.mRegionSamplingHelper.setWindowVisible(z);
        if (z) {
            this.mStashedHandleView.updateSampledRegion(this.mStashedHandleBounds);
            this.mRegionSamplingHelper.start(this.mStashedHandleView.getSampledRegion());
            return;
        }
        this.mRegionSamplingHelper.stop();
    }

    /* access modifiers changed from: protected */
    public void updateStashedHandleHintScale() {
        this.mStashedHandleView.setScaleX(this.mTaskbarStashedHandleHintScale.value);
        this.mStashedHandleView.setScaleY(this.mTaskbarStashedHandleHintScale.value);
    }

    public void setIsHomeButtonDisabled(boolean z) {
        this.mTaskbarStashedHandleAlpha.getProperty(1).setValue(z ? 0.0f : 1.0f);
    }

    public boolean isStashedHandleVisible() {
        return this.mStashedHandleView.getVisibility() == 0;
    }

    public void dumpLogs(String str, PrintWriter printWriter) {
        printWriter.println(str + "StashedHandleViewController:");
        printWriter.println(String.format("%s\tisStashedHandleVisible=%b", new Object[]{str, Boolean.valueOf(isStashedHandleVisible())}));
        printWriter.println(String.format("%s\tmStashedHandleWidth=%dpx", new Object[]{str, Integer.valueOf(this.mStashedHandleWidth)}));
        printWriter.println(String.format("%s\tmStashedHandleHeight=%dpx", new Object[]{str, Integer.valueOf(this.mStashedHandleHeight)}));
        this.mRegionSamplingHelper.dump(str, printWriter);
    }
}
