package com.android.launcher3.widget;

import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;
import com.android.launcher3.R;

public class DeferredAppWidgetHostView extends LauncherAppWidgetHostView {
    private final TextPaint mPaint;
    private Layout mSetupTextLayout;

    public void addView(View view) {
    }

    public void updateAppWidget(RemoteViews remoteViews) {
    }

    public DeferredAppWidgetHostView(Context context) {
        super(context);
        setWillNotDraw(false);
        TextPaint textPaint = new TextPaint();
        this.mPaint = textPaint;
        textPaint.setColor(-1);
        textPaint.setTextSize(TypedValue.applyDimension(0, (float) this.mLauncher.getDeviceProfile().iconTextSizePx, getResources().getDisplayMetrics()));
        setBackgroundResource(R.drawable.bg_deferred_app_widget);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        AppWidgetProviderInfo appWidgetInfo = getAppWidgetInfo();
        if (appWidgetInfo != null && !TextUtils.isEmpty(appWidgetInfo.label)) {
            int measuredWidth = getMeasuredWidth() - ((getPaddingLeft() + getPaddingRight()) * 2);
            if (measuredWidth <= 0) {
                measuredWidth = getMeasuredWidth() - (getPaddingLeft() + getPaddingRight());
            }
            int i3 = measuredWidth;
            Layout layout = this.mSetupTextLayout;
            if (layout == null || !layout.getText().equals(appWidgetInfo.label) || this.mSetupTextLayout.getWidth() != i3) {
                this.mSetupTextLayout = new StaticLayout(appWidgetInfo.label, this.mPaint, i3, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, true);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.mSetupTextLayout != null) {
            canvas.translate((float) ((getWidth() - this.mSetupTextLayout.getWidth()) / 2), (float) ((getHeight() - this.mSetupTextLayout.getHeight()) / 2));
            this.mSetupTextLayout.draw(canvas);
        }
    }
}
