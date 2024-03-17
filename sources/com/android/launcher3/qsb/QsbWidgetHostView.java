package com.android.launcher3.qsb;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.widget.NavigableAppWidgetHostView;

public class QsbWidgetHostView extends NavigableAppWidgetHostView {
    @ViewDebug.ExportedProperty(category = "launcher")
    private int mPreviousOrientation;

    /* access modifiers changed from: protected */
    public boolean shouldAllowDirectClick() {
        return true;
    }

    public QsbWidgetHostView(Context context) {
        super(context);
        setFocusable(true);
        setBackgroundResource(R.drawable.qsb_host_view_focus_bg);
    }

    public void updateAppWidget(RemoteViews remoteViews) {
        this.mPreviousOrientation = getResources().getConfiguration().orientation;
        super.updateAppWidget(remoteViews);
    }

    public boolean isReinflateRequired(int i) {
        return this.mPreviousOrientation != i;
    }

    public void setPadding(int i, int i2, int i3, int i4) {
        super.setPadding(0, 0, 0, 0);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        try {
            super.onLayout(z, i, i2, i3, i4);
        } catch (RuntimeException unused) {
            post(new Runnable() {
                public final void run() {
                    QsbWidgetHostView.this.lambda$onLayout$0$QsbWidgetHostView();
                }
            });
        }
    }

    public /* synthetic */ void lambda$onLayout$0$QsbWidgetHostView() {
        updateAppWidget(new RemoteViews(getAppWidgetInfo().provider.getPackageName(), 0));
    }

    /* access modifiers changed from: protected */
    public View getErrorView() {
        return getDefaultView(this);
    }

    /* access modifiers changed from: protected */
    public View getDefaultView() {
        View defaultView = super.getDefaultView();
        defaultView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                QsbWidgetHostView.this.lambda$getDefaultView$1$QsbWidgetHostView(view);
            }
        });
        return defaultView;
    }

    public /* synthetic */ void lambda$getDefaultView$1$QsbWidgetHostView(View view) {
        Launcher.getLauncher(getContext()).startSearch("", false, (Bundle) null, true);
    }

    public static View getDefaultView(ViewGroup viewGroup) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.qsb_default_view, viewGroup, false);
        inflate.findViewById(R.id.btn_qsb_search).setOnClickListener($$Lambda$QsbWidgetHostView$UOz71DN3v7EvlljLFBdDT0She50.INSTANCE);
        return inflate;
    }
}
