package com.android.launcher3.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.R;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.compat.AccessibilityManagerCompat;
import com.android.launcher3.views.BaseDragLayer;

public class Snackbar extends AbstractFloatingView {
    private static final long HIDE_DURATION_MS = 180;
    private static final long SHOW_DURATION_MS = 180;
    private static final int TIMEOUT_DURATION_MS = 4000;
    private final ActivityContext mActivity;
    private Runnable mOnDismissed;

    /* access modifiers changed from: protected */
    public boolean isOfType(int i) {
        return (i & 128) != 0;
    }

    public Snackbar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public Snackbar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mActivity = (ActivityContext) ActivityContext.lookupContext(context);
        inflate(context, R.layout.snackbar, this);
    }

    public static <T extends Context & ActivityContext> void show(T t, int i, Runnable runnable) {
        show(t, i, -1, runnable, (Runnable) null);
    }

    public static <T extends Context & ActivityContext> void show(T t, int i, int i2, Runnable runnable, Runnable runnable2) {
        float f;
        int i3 = i2;
        ActivityContext activityContext = (ActivityContext) t;
        closeOpenViews(activityContext, true, 128);
        Snackbar snackbar = new Snackbar(t, (AttributeSet) null);
        snackbar.setOrientation(0);
        snackbar.setGravity(16);
        Resources resources = t.getResources();
        snackbar.setElevation(resources.getDimension(R.dimen.snackbar_elevation));
        int dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.snackbar_padding);
        snackbar.setPadding(dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize);
        snackbar.setBackgroundResource(R.drawable.round_rect_primary);
        snackbar.mIsOpen = true;
        BaseDragLayer dragLayer = activityContext.getDragLayer();
        dragLayer.addView(snackbar);
        BaseDragLayer.LayoutParams layoutParams = (BaseDragLayer.LayoutParams) snackbar.getLayoutParams();
        layoutParams.gravity = 81;
        layoutParams.height = resources.getDimensionPixelSize(R.dimen.snackbar_height);
        int dimensionPixelSize2 = resources.getDimensionPixelSize(R.dimen.snackbar_max_margin_left_right);
        int dimensionPixelSize3 = resources.getDimensionPixelSize(R.dimen.snackbar_min_margin_left_right);
        int dimensionPixelSize4 = resources.getDimensionPixelSize(R.dimen.snackbar_margin_bottom);
        int dimensionPixelSize5 = resources.getDimensionPixelSize(R.dimen.snackbar_max_width);
        Rect insets = activityContext.getDeviceProfile().getInsets();
        int min = Math.min(((dragLayer.getWidth() - (dimensionPixelSize3 * 2)) - insets.left) - insets.right, dimensionPixelSize5);
        layoutParams.width = Math.min(((dragLayer.getWidth() - (dimensionPixelSize2 * 2)) - insets.left) - insets.right, dimensionPixelSize5);
        layoutParams.setMargins(0, 0, 0, dimensionPixelSize4 + insets.bottom);
        TextView textView = (TextView) snackbar.findViewById(R.id.label);
        String string = resources.getString(i);
        textView.setText(string);
        TextView textView2 = (TextView) snackbar.findViewById(R.id.action);
        if (i3 != -1) {
            String string2 = resources.getString(i3);
            f = textView2.getPaint().measureText(string2) + ((float) textView2.getPaddingRight()) + ((float) textView2.getPaddingLeft());
            textView2.setText(string2);
            textView2.setOnClickListener(new View.OnClickListener(runnable2, snackbar) {
                public final /* synthetic */ Runnable f$0;
                public final /* synthetic */ Snackbar f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    Snackbar.lambda$show$0(this.f$0, this.f$1, view);
                }
            });
        } else {
            textView2.setVisibility(8);
            f = 0.0f;
        }
        int measureText = ((int) (textView.getPaint().measureText(string) + f)) + textView.getPaddingRight() + textView.getPaddingLeft() + (dimensionPixelSize * 2);
        if (measureText > layoutParams.width) {
            if (measureText <= min) {
                layoutParams.width = measureText;
            } else {
                int dimensionPixelSize6 = resources.getDimensionPixelSize(R.dimen.snackbar_content_height);
                float dimension = resources.getDimension(R.dimen.snackbar_min_text_size);
                textView.setLines(2);
                int i4 = dimensionPixelSize6 * 2;
                textView.getLayoutParams().height = i4;
                textView2.getLayoutParams().height = i4;
                textView.setTextSize(0, dimension);
                textView2.setTextSize(0, dimension);
                layoutParams.height += dimensionPixelSize6;
                layoutParams.width = min;
            }
        }
        snackbar.mOnDismissed = runnable;
        snackbar.setAlpha(0.0f);
        snackbar.setScaleX(0.8f);
        snackbar.setScaleY(0.8f);
        snackbar.animate().alpha(1.0f).withLayer().scaleX(1.0f).scaleY(1.0f).setDuration(180).setInterpolator(Interpolators.ACCEL_DEACCEL).start();
        snackbar.postDelayed(new Runnable() {
            public final void run() {
                Snackbar.this.close(true);
            }
        }, (long) AccessibilityManagerCompat.getRecommendedTimeoutMillis(t, TIMEOUT_DURATION_MS, 6));
    }

    static /* synthetic */ void lambda$show$0(Runnable runnable, Snackbar snackbar, View view) {
        if (runnable != null) {
            runnable.run();
        }
        snackbar.mOnDismissed = null;
        snackbar.close(true);
    }

    /* access modifiers changed from: protected */
    public void handleClose(boolean z) {
        if (this.mIsOpen) {
            if (z) {
                animate().alpha(0.0f).withLayer().setStartDelay(0).setDuration(180).setInterpolator(Interpolators.ACCEL).withEndAction(new Runnable() {
                    public final void run() {
                        Snackbar.this.onClosed();
                    }
                }).start();
            } else {
                animate().cancel();
                onClosed();
            }
            this.mIsOpen = false;
        }
    }

    /* access modifiers changed from: private */
    public void onClosed() {
        this.mActivity.getDragLayer().removeView(this);
        Runnable runnable = this.mOnDismissed;
        if (runnable != null) {
            runnable.run();
        }
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() != 0 || this.mActivity.getDragLayer().isEventOverView(this, motionEvent)) {
            return false;
        }
        close(true);
        return false;
    }
}
