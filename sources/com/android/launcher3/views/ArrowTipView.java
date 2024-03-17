package com.android.launcher3.views;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.R;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.graphics.TriangleShape;
import com.android.launcher3.views.BaseDragLayer;

public class ArrowTipView extends AbstractFloatingView {
    private static final long AUTO_CLOSE_TIMEOUT_MILLIS = 10000;
    private static final long HIDE_DURATION_MS = 100;
    private static final long SHOW_DELAY_MS = 200;
    private static final long SHOW_DURATION_MS = 300;
    private static final String TAG = "ArrowTipView";
    protected final BaseDraggingActivity mActivity;
    private final int mArrowMinOffset;
    private View mArrowView;
    private final int mArrowWidth;
    private final Handler mHandler;
    private boolean mIsPointingUp;
    private Runnable mOnClosed;

    /* access modifiers changed from: protected */
    public boolean isOfType(int i) {
        return (i & 32) != 0;
    }

    public ArrowTipView(Context context) {
        this(context, false);
    }

    public ArrowTipView(Context context, boolean z) {
        super(context, (AttributeSet) null, 0);
        this.mHandler = new Handler();
        this.mActivity = (BaseDraggingActivity) BaseDraggingActivity.fromContext(context);
        this.mIsPointingUp = z;
        this.mArrowWidth = context.getResources().getDimensionPixelSize(R.dimen.arrow_toast_arrow_width);
        this.mArrowMinOffset = context.getResources().getDimensionPixelSize(R.dimen.dynamic_grid_cell_border_spacing);
        init(context);
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() != 0) {
            return false;
        }
        close(true);
        if (this.mActivity.getDragLayer().isEventOverView(this, motionEvent)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void handleClose(boolean z) {
        if (this.mIsOpen) {
            if (z) {
                animate().alpha(0.0f).withLayer().setStartDelay(0).setDuration(HIDE_DURATION_MS).setInterpolator(Interpolators.ACCEL).withEndAction(new Runnable() {
                    public final void run() {
                        ArrowTipView.this.lambda$handleClose$0$ArrowTipView();
                    }
                }).start();
            } else {
                animate().cancel();
                this.mActivity.getDragLayer().removeView(this);
            }
            Runnable runnable = this.mOnClosed;
            if (runnable != null) {
                runnable.run();
            }
            this.mIsOpen = false;
        }
    }

    public /* synthetic */ void lambda$handleClose$0$ArrowTipView() {
        this.mActivity.getDragLayer().removeView(this);
    }

    private void init(Context context) {
        inflate(context, R.layout.arrow_toast, this);
        setOrientation(1);
        this.mArrowView = findViewById(R.id.arrow);
        updateArrowTipInView();
    }

    public ArrowTipView show(String str, int i) {
        return show(str, 1, 0, i);
    }

    public ArrowTipView show(String str, int i, int i2, int i3) {
        return show(str, i, i2, i3, true);
    }

    public ArrowTipView show(String str, int i, int i2, int i3, boolean z) {
        ((TextView) findViewById(R.id.text)).setText(str);
        BaseDragLayer dragLayer = this.mActivity.getDragLayer();
        dragLayer.addView(this);
        DeviceProfile deviceProfile = this.mActivity.getDeviceProfile();
        BaseDragLayer.LayoutParams layoutParams = (BaseDragLayer.LayoutParams) getLayoutParams();
        layoutParams.gravity = i;
        layoutParams.leftMargin = this.mArrowMinOffset + deviceProfile.getInsets().left;
        layoutParams.rightMargin = this.mArrowMinOffset + deviceProfile.getInsets().right;
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mArrowView.getLayoutParams();
        layoutParams2.gravity = i;
        if (dragLayer.getLayoutDirection() == 1) {
            i2 = dragLayer.getMeasuredWidth() - i2;
        }
        if (i == 8388613) {
            layoutParams2.setMarginEnd(Math.max(this.mArrowMinOffset, ((dragLayer.getMeasuredWidth() - layoutParams.rightMargin) - i2) - (this.mArrowWidth / 2)));
        } else if (i == 8388611) {
            layoutParams2.setMarginStart(Math.max(this.mArrowMinOffset, (i2 - layoutParams.leftMargin) - (this.mArrowWidth / 2)));
        }
        requestLayout();
        post(new Runnable(i3) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ArrowTipView.this.lambda$show$1$ArrowTipView(this.f$1);
            }
        });
        this.mIsOpen = true;
        if (z) {
            this.mHandler.postDelayed(new Runnable() {
                public final void run() {
                    ArrowTipView.this.lambda$show$2$ArrowTipView();
                }
            }, AUTO_CLOSE_TIMEOUT_MILLIS);
        }
        setAlpha(0.0f);
        animate().alpha(1.0f).withLayer().setStartDelay(SHOW_DELAY_MS).setDuration(300).setInterpolator(Interpolators.DEACCEL).start();
        return this;
    }

    public /* synthetic */ void lambda$show$1$ArrowTipView(int i) {
        setY((float) (i - (this.mIsPointingUp ? 0 : getHeight())));
    }

    public /* synthetic */ void lambda$show$2$ArrowTipView() {
        handleClose(true);
    }

    public ArrowTipView showAtLocation(String str, int i, int i2) {
        return showAtLocation(str, i, i2, i2, true);
    }

    public ArrowTipView showAtLocation(String str, int i, int i2, boolean z) {
        return showAtLocation(str, i, i2, i2, z);
    }

    public ArrowTipView showAroundRect(String str, int i, Rect rect, int i2) {
        return showAtLocation(str, i, rect.top - i2, rect.bottom + i2, true);
    }

    private ArrowTipView showAtLocation(String str, int i, int i2, int i3, boolean z) {
        BaseDragLayer dragLayer = this.mActivity.getDragLayer();
        int width = dragLayer.getWidth();
        int height = dragLayer.getHeight();
        int dimensionPixelSize = getContext().getResources().getDimensionPixelSize(R.dimen.widget_picker_education_tip_max_width);
        int dimensionPixelSize2 = getContext().getResources().getDimensionPixelSize(R.dimen.widget_picker_education_tip_min_margin);
        if (width < (dimensionPixelSize2 * 2) + dimensionPixelSize) {
            Log.w(TAG, "Cannot display tip on a small screen of size: " + width);
            return null;
        }
        TextView textView = (TextView) findViewById(R.id.text);
        textView.setText(str);
        textView.setMaxWidth(dimensionPixelSize);
        dragLayer.addView(this);
        requestLayout();
        post(new Runnable(i, dimensionPixelSize2, width, i3, height, i2) {
            public final /* synthetic */ int f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ int f$3;
            public final /* synthetic */ int f$4;
            public final /* synthetic */ int f$5;
            public final /* synthetic */ int f$6;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
            }

            public final void run() {
                ArrowTipView.this.lambda$showAtLocation$3$ArrowTipView(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        });
        this.mIsOpen = true;
        if (z) {
            this.mHandler.postDelayed(new Runnable() {
                public final void run() {
                    ArrowTipView.this.lambda$showAtLocation$4$ArrowTipView();
                }
            }, AUTO_CLOSE_TIMEOUT_MILLIS);
        }
        setAlpha(0.0f);
        animate().alpha(1.0f).withLayer().setStartDelay(SHOW_DELAY_MS).setDuration(300).setInterpolator(Interpolators.DEACCEL).start();
        return this;
    }

    public /* synthetic */ void lambda$showAtLocation$3$ArrowTipView(int i, int i2, int i3, int i4, int i5, int i6) {
        float width = ((float) getWidth()) / 2.0f;
        float f = (float) i;
        float f2 = f - width;
        float f3 = (float) i2;
        if (f2 < f3) {
            f2 = f3;
        } else {
            int i7 = i3 - i2;
            if (width + f > ((float) i7)) {
                f2 = (float) (i7 - getWidth());
            }
        }
        setX(f2);
        int height = getHeight();
        boolean z = this.mIsPointingUp;
        if (!z ? i6 - height < 0 : i4 + height > i5) {
            this.mIsPointingUp = !z;
            updateArrowTipInView();
        }
        setY(this.mIsPointingUp ? (float) i4 : (float) (i6 - height));
        View view = this.mArrowView;
        view.setX((f - f2) - (((float) view.getWidth()) / 2.0f));
        requestLayout();
    }

    public /* synthetic */ void lambda$showAtLocation$4$ArrowTipView() {
        handleClose(true);
    }

    private void updateArrowTipInView() {
        ViewGroup.LayoutParams layoutParams = this.mArrowView.getLayoutParams();
        ShapeDrawable shapeDrawable = new ShapeDrawable(TriangleShape.create((float) layoutParams.width, (float) layoutParams.height, this.mIsPointingUp));
        Paint paint = shapeDrawable.getPaint();
        int dimensionPixelSize = getContext().getResources().getDimensionPixelSize(R.dimen.arrow_toast_corner_radius);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.arrow_tip_view_bg));
        paint.setPathEffect(new CornerPathEffect((float) dimensionPixelSize));
        this.mArrowView.setBackground(shapeDrawable);
        removeView(this.mArrowView);
        if (this.mIsPointingUp) {
            addView(this.mArrowView, 0);
            ((ViewGroup.MarginLayoutParams) layoutParams).setMargins(0, 0, 0, dimensionPixelSize * -1);
            return;
        }
        addView(this.mArrowView, 1);
        ((ViewGroup.MarginLayoutParams) layoutParams).setMargins(0, dimensionPixelSize * -1, 0, 0);
    }

    public ArrowTipView setOnClosedCallback(Runnable runnable) {
        this.mOnClosed = runnable;
        return this;
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        close(false);
    }
}
