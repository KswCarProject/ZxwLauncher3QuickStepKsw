package com.android.launcher3.widget;

import android.content.Context;
import android.graphics.Outline;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.RemoteViews;
import com.android.launcher3.R;
import com.android.launcher3.util.Executors;

public abstract class BaseLauncherAppWidgetHostView extends NavigableAppWidgetHostView {
    private final ViewOutlineProvider mCornerRadiusEnforcementOutline = new ViewOutlineProvider() {
        public void getOutline(View view, Outline outline) {
            if (BaseLauncherAppWidgetHostView.this.mEnforcedRectangle.isEmpty() || BaseLauncherAppWidgetHostView.this.mEnforcedCornerRadius <= 0.0f) {
                outline.setEmpty();
            } else {
                outline.setRoundRect(BaseLauncherAppWidgetHostView.this.mEnforcedRectangle, BaseLauncherAppWidgetHostView.this.mEnforcedCornerRadius);
            }
        }
    };
    /* access modifiers changed from: private */
    public final float mEnforcedCornerRadius;
    /* access modifiers changed from: private */
    public final Rect mEnforcedRectangle = new Rect();
    protected final LayoutInflater mInflater;

    public BaseLauncherAppWidgetHostView(Context context) {
        super(context);
        setExecutor(Executors.THREAD_POOL_EXECUTOR);
        this.mInflater = LayoutInflater.from(context);
        this.mEnforcedCornerRadius = RoundedCornerEnforcement.computeEnforcedRadius(getContext());
    }

    /* access modifiers changed from: protected */
    public View getErrorView() {
        return this.mInflater.inflate(R.layout.appwidget_error, this, false);
    }

    public void switchToErrorView() {
        updateAppWidget(new RemoteViews(getAppWidgetInfo().provider.getPackageName(), 0));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        try {
            super.onLayout(z, i, i2, i3, i4);
        } catch (RuntimeException unused) {
            post(new Runnable() {
                public final void run() {
                    BaseLauncherAppWidgetHostView.this.switchToErrorView();
                }
            });
        }
        enforceRoundedCorners();
    }

    private void resetRoundedCorners() {
        setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        setClipToOutline(false);
    }

    private void enforceRoundedCorners() {
        if (this.mEnforcedCornerRadius <= 0.0f || !RoundedCornerEnforcement.isRoundedCornerEnabled()) {
            resetRoundedCorners();
            return;
        }
        View findBackground = RoundedCornerEnforcement.findBackground(this);
        if (findBackground == null || RoundedCornerEnforcement.hasAppWidgetOptedOut(this, findBackground)) {
            resetRoundedCorners();
            return;
        }
        RoundedCornerEnforcement.computeRoundedRectangle(this, findBackground, this.mEnforcedRectangle);
        setOutlineProvider(this.mCornerRadiusEnforcementOutline);
        setClipToOutline(true);
    }

    public float getEnforcedCornerRadius() {
        return this.mEnforcedCornerRadius;
    }

    public boolean hasEnforcedCornerRadius() {
        return getClipToOutline();
    }
}
