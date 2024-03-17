package com.android.launcher3.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.Insettable;
import com.android.launcher3.util.SystemUiController;
import java.util.ArrayList;

public class ScrimView extends View implements Insettable {
    private static final float STATUS_BAR_COLOR_FORCE_UPDATE_THRESHOLD = 0.9f;
    private int mBackgroundColor;
    CheckInRecentLisener mCheckInRecentLisener;
    private ScrimDrawingController mDrawingController;
    private boolean mIsVisible = true;
    private boolean mLastDispatchedOpaqueness;
    private final ArrayList<Runnable> mOpaquenessListeners = new ArrayList<>(1);
    private SystemUiController mSystemUiController;

    public interface CheckInRecentLisener {
        void inRecent(boolean z);
    }

    public interface ScrimDrawingController {
        void drawOnScrim(Canvas canvas);
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public void setInsets(Rect rect) {
    }

    public ScrimView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setFocusable(false);
    }

    /* access modifiers changed from: protected */
    public boolean onSetAlpha(int i) {
        updateSysUiColors();
        dispatchVisibilityListenersIfNeeded();
        return super.onSetAlpha(i);
    }

    public void setCheckInRecentLisener(CheckInRecentLisener checkInRecentLisener) {
        this.mCheckInRecentLisener = checkInRecentLisener;
    }

    public void setBackgroundColor(int i) {
        if (i == 0) {
            System.setProperty("CheckInRecent", "0");
            CheckInRecentLisener checkInRecentLisener = this.mCheckInRecentLisener;
            if (checkInRecentLisener != null) {
                checkInRecentLisener.inRecent(false);
            }
        } else {
            System.setProperty("CheckInRecent", "1");
            CheckInRecentLisener checkInRecentLisener2 = this.mCheckInRecentLisener;
            if (checkInRecentLisener2 != null) {
                checkInRecentLisener2.inRecent(true);
            }
        }
        this.mBackgroundColor = i;
        updateSysUiColors();
        dispatchVisibilityListenersIfNeeded();
        super.setBackgroundColor(i);
    }

    public void onVisibilityAggregated(boolean z) {
        super.onVisibilityAggregated(z);
        this.mIsVisible = z;
        dispatchVisibilityListenersIfNeeded();
    }

    public boolean isFullyOpaque() {
        return this.mIsVisible && getAlpha() == 1.0f && Color.alpha(this.mBackgroundColor) == 255;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        ScrimDrawingController scrimDrawingController = this.mDrawingController;
        if (scrimDrawingController != null) {
            scrimDrawingController.drawOnScrim(canvas);
        }
    }

    /* access modifiers changed from: protected */
    public void onVisibilityChanged(View view, int i) {
        super.onVisibilityChanged(view, i);
        updateSysUiColors();
    }

    private void updateSysUiColors() {
        if (getVisibility() == 0 && getAlpha() > STATUS_BAR_COLOR_FORCE_UPDATE_THRESHOLD && ((float) Color.alpha(this.mBackgroundColor)) / 255.0f > STATUS_BAR_COLOR_FORCE_UPDATE_THRESHOLD) {
            getSystemUiController().updateUiState(1, !isScrimDark());
        } else {
            getSystemUiController().updateUiState(1, 0);
        }
    }

    private void dispatchVisibilityListenersIfNeeded() {
        boolean isFullyOpaque = isFullyOpaque();
        if (this.mLastDispatchedOpaqueness != isFullyOpaque) {
            this.mLastDispatchedOpaqueness = isFullyOpaque;
            for (int i = 0; i < this.mOpaquenessListeners.size(); i++) {
                this.mOpaquenessListeners.get(i).run();
            }
        }
    }

    private SystemUiController getSystemUiController() {
        if (this.mSystemUiController == null) {
            this.mSystemUiController = BaseActivity.fromContext(getContext()).getSystemUiController();
        }
        return this.mSystemUiController;
    }

    private boolean isScrimDark() {
        if (getBackground() instanceof ColorDrawable) {
            return ColorUtils.calculateLuminance(((ColorDrawable) getBackground()).getColor()) < 0.5d;
        }
        throw new IllegalStateException("ScrimView must have a ColorDrawable background, this one has: " + getBackground());
    }

    public void setDrawingController(ScrimDrawingController scrimDrawingController) {
        if (this.mDrawingController != scrimDrawingController) {
            this.mDrawingController = scrimDrawingController;
            invalidate();
        }
    }

    public void addOpaquenessListener(Runnable runnable) {
        this.mOpaquenessListeners.add(runnable);
    }

    public void removeOpaquenessListener(Runnable runnable) {
        this.mOpaquenessListeners.remove(runnable);
    }
}
