package com.android.launcher3.pageindicators;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import androidx.core.view.ViewCompat;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Insettable;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.Themes;

public class WorkspacePageIndicator extends View implements Insettable, PageIndicator {
    private static final int ANIMATOR_COUNT = 3;
    public static final int BLACK_ALPHA = 165;
    private static final int LINE_ALPHA_ANIMATOR_INDEX = 0;
    private static final int LINE_ANIMATE_DURATION = ViewConfiguration.getScrollBarFadeDuration();
    private static final int LINE_FADE_DELAY = ViewConfiguration.getScrollDefaultDelay();
    private static final Property<WorkspacePageIndicator, Float> NUM_PAGES = new Property<WorkspacePageIndicator, Float>(Float.class, "num_pages") {
        public Float get(WorkspacePageIndicator workspacePageIndicator) {
            return Float.valueOf(workspacePageIndicator.mNumPagesFloat);
        }

        public void set(WorkspacePageIndicator workspacePageIndicator, Float f) {
            float unused = workspacePageIndicator.mNumPagesFloat = f.floatValue();
            workspacePageIndicator.invalidate();
        }
    };
    private static final int NUM_PAGES_ANIMATOR_INDEX = 1;
    private static final Property<WorkspacePageIndicator, Integer> PAINT_ALPHA = new Property<WorkspacePageIndicator, Integer>(Integer.class, "paint_alpha") {
        public Integer get(WorkspacePageIndicator workspacePageIndicator) {
            return Integer.valueOf(workspacePageIndicator.mLinePaint.getAlpha());
        }

        public void set(WorkspacePageIndicator workspacePageIndicator, Integer num) {
            workspacePageIndicator.mLinePaint.setAlpha(num.intValue());
            workspacePageIndicator.invalidate();
        }
    };
    private static final Property<WorkspacePageIndicator, Integer> TOTAL_SCROLL = new Property<WorkspacePageIndicator, Integer>(Integer.class, "total_scroll") {
        public Integer get(WorkspacePageIndicator workspacePageIndicator) {
            return Integer.valueOf(workspacePageIndicator.mTotalScroll);
        }

        public void set(WorkspacePageIndicator workspacePageIndicator, Integer num) {
            int unused = workspacePageIndicator.mTotalScroll = num.intValue();
            workspacePageIndicator.invalidate();
        }
    };
    private static final int TOTAL_SCROLL_ANIMATOR_INDEX = 2;
    public static final int WHITE_ALPHA = 178;
    private int mActiveAlpha;
    /* access modifiers changed from: private */
    public ValueAnimator[] mAnimators;
    private int mCurrentScroll;
    private final Handler mDelayedLineFadeHandler;
    private Runnable mHideLineRunnable;
    private final Launcher mLauncher;
    private final int mLineHeight;
    /* access modifiers changed from: private */
    public Paint mLinePaint;
    /* access modifiers changed from: private */
    public float mNumPagesFloat;
    private boolean mShouldAutoHide;
    private int mToAlpha;
    /* access modifiers changed from: private */
    public int mTotalScroll;

    public void setActiveMarker(int i) {
    }

    public /* synthetic */ void lambda$new$0$WorkspacePageIndicator() {
        animateLineToAlpha(0);
    }

    public WorkspacePageIndicator(Context context) {
        this(context, (AttributeSet) null);
    }

    public WorkspacePageIndicator(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public WorkspacePageIndicator(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mAnimators = new ValueAnimator[3];
        this.mDelayedLineFadeHandler = new Handler(Looper.getMainLooper());
        this.mShouldAutoHide = true;
        this.mActiveAlpha = 0;
        this.mHideLineRunnable = new Runnable() {
            public final void run() {
                WorkspacePageIndicator.this.lambda$new$0$WorkspacePageIndicator();
            }
        };
        Resources resources = context.getResources();
        Paint paint = new Paint();
        this.mLinePaint = paint;
        paint.setAlpha(0);
        Launcher launcher = Launcher.getLauncher(context);
        this.mLauncher = launcher;
        this.mLineHeight = resources.getDimensionPixelSize(R.dimen.workspace_page_indicator_line_height);
        boolean attrBoolean = Themes.getAttrBoolean(launcher, R.attr.isWorkspaceDarkText);
        this.mActiveAlpha = attrBoolean ? BLACK_ALPHA : WHITE_ALPHA;
        this.mLinePaint.setColor(attrBoolean ? ViewCompat.MEASURED_STATE_MASK : -1);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i = this.mTotalScroll;
        if (i != 0 && this.mNumPagesFloat != 0.0f) {
            float boundToRange = Utilities.boundToRange(((float) this.mCurrentScroll) / ((float) i), 0.0f, 1.0f);
            int width = getWidth();
            int i2 = (int) (((float) width) / this.mNumPagesFloat);
            int i3 = (int) (boundToRange * ((float) (width - i2)));
            int i4 = this.mLineHeight;
            Canvas canvas2 = canvas;
            canvas2.drawRoundRect((float) i3, (float) ((getHeight() / 2) - (this.mLineHeight / 2)), (float) (i2 + i3), (float) ((getHeight() / 2) + (i4 / 2)), (float) i4, (float) i4, this.mLinePaint);
        }
    }

    public void setScroll(int i, int i2) {
        if (getAlpha() != 0.0f) {
            animateLineToAlpha(this.mActiveAlpha);
            this.mCurrentScroll = i;
            int i3 = this.mTotalScroll;
            if (i3 == 0) {
                this.mTotalScroll = i2;
            } else if (i3 != i2) {
                animateToTotalScroll(i2);
            } else {
                invalidate();
            }
            if (this.mShouldAutoHide) {
                hideAfterDelay();
            }
        }
    }

    private void hideAfterDelay() {
        this.mDelayedLineFadeHandler.removeCallbacksAndMessages((Object) null);
        this.mDelayedLineFadeHandler.postDelayed(this.mHideLineRunnable, (long) LINE_FADE_DELAY);
    }

    public void setMarkersCount(int i) {
        float f = (float) i;
        if (Float.compare(f, this.mNumPagesFloat) != 0) {
            setupAndRunAnimation(ObjectAnimator.ofFloat(this, NUM_PAGES, new float[]{f}), 1);
            return;
        }
        ValueAnimator[] valueAnimatorArr = this.mAnimators;
        if (valueAnimatorArr[1] != null) {
            valueAnimatorArr[1].cancel();
            this.mAnimators[1] = null;
        }
    }

    public void setShouldAutoHide(boolean z) {
        this.mShouldAutoHide = z;
        if (z && this.mLinePaint.getAlpha() > 0) {
            hideAfterDelay();
        } else if (!z) {
            this.mDelayedLineFadeHandler.removeCallbacksAndMessages((Object) null);
        }
    }

    private void animateLineToAlpha(int i) {
        if (i != this.mToAlpha) {
            this.mToAlpha = i;
            setupAndRunAnimation(ObjectAnimator.ofInt(this, PAINT_ALPHA, new int[]{i}), 0);
        }
    }

    private void animateToTotalScroll(int i) {
        setupAndRunAnimation(ObjectAnimator.ofInt(this, TOTAL_SCROLL, new int[]{i}), 2);
    }

    private void setupAndRunAnimation(ValueAnimator valueAnimator, final int i) {
        ValueAnimator[] valueAnimatorArr = this.mAnimators;
        if (valueAnimatorArr[i] != null) {
            valueAnimatorArr[i].cancel();
        }
        ValueAnimator[] valueAnimatorArr2 = this.mAnimators;
        valueAnimatorArr2[i] = valueAnimator;
        valueAnimatorArr2[i].addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                WorkspacePageIndicator.this.mAnimators[i] = null;
            }
        });
        this.mAnimators[i].setDuration((long) LINE_ANIMATE_DURATION);
        this.mAnimators[i].start();
    }

    public void pauseAnimations() {
        for (int i = 0; i < 3; i++) {
            ValueAnimator[] valueAnimatorArr = this.mAnimators;
            if (valueAnimatorArr[i] != null) {
                valueAnimatorArr[i].pause();
            }
        }
    }

    public void skipAnimationsToEnd() {
        for (int i = 0; i < 3; i++) {
            ValueAnimator[] valueAnimatorArr = this.mAnimators;
            if (valueAnimatorArr[i] != null) {
                valueAnimatorArr[i].end();
            }
        }
    }

    public void setInsets(Rect rect) {
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        if (deviceProfile.isVerticalBarLayout()) {
            Rect rect2 = deviceProfile.workspacePadding;
            layoutParams.leftMargin = rect2.left + deviceProfile.workspaceCellPaddingXPx;
            layoutParams.rightMargin = rect2.right + deviceProfile.workspaceCellPaddingXPx;
            layoutParams.bottomMargin = rect2.bottom;
        } else {
            layoutParams.rightMargin = 0;
            layoutParams.leftMargin = 0;
            layoutParams.gravity = 81;
            layoutParams.bottomMargin = deviceProfile.hotseatBarSizePx + rect.bottom;
        }
        setLayoutParams(layoutParams);
    }
}
