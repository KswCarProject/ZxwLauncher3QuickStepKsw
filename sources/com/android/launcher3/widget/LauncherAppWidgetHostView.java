package com.android.launcher3.widget;

import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Trace;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.AdapterView;
import android.widget.Advanceable;
import android.widget.RemoteViews;
import com.android.launcher3.CheckLongPressHelper;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.util.Themes;
import com.android.launcher3.views.BaseDragLayer;
import com.android.launcher3.widget.LocalColorExtractor;

public class LauncherAppWidgetHostView extends BaseLauncherAppWidgetHostView implements BaseDragLayer.TouchCompleteListener, View.OnLongClickListener, LocalColorExtractor.Listener {
    private static final long ADVANCE_INTERVAL = 20000;
    private static final long ADVANCE_STAGGER = 250;
    private static final String TAG = "LauncherAppWidgetHostView";
    private static final String TRACE_METHOD_NAME = "appwidget load-widget ";
    private static final long UPDATE_LOCK_TIMEOUT_MILLIS = 1000;
    private static final SparseBooleanArray sAutoAdvanceWidgetIds = new SparseBooleanArray();
    private Runnable mAutoAdvanceRunnable;
    private final LocalColorExtractor mColorExtractor;
    private long mDeferUpdatesUntilMillis = 0;
    private SparseIntArray mDeferredColorChange = null;
    private RemoteViews mDeferredRemoteViews;
    private int mDragContentHeight = 0;
    private int mDragContentWidth = 0;
    private boolean mHasDeferredColorChange = false;
    private boolean mIsAttachedToWindow;
    private boolean mIsAutoAdvanceRegistered;
    private boolean mIsInDragMode = false;
    private boolean mIsScrollable;
    protected final Launcher mLauncher;
    private final CheckLongPressHelper mLongPressHelper;
    @ViewDebug.ExportedProperty(category = "launcher")
    private boolean mReinflateOnConfigChange;
    private final Rect mTempRect = new Rect();
    private boolean mTrackingWidgetUpdate = false;

    public LauncherAppWidgetHostView(Context context) {
        super(context);
        Launcher launcher = Launcher.getLauncher(context);
        this.mLauncher = launcher;
        this.mLongPressHelper = new CheckLongPressHelper(this, this);
        setAccessibilityDelegate(launcher.getAccessibilityDelegate());
        setBackgroundResource(R.drawable.widget_internal_focus_bg);
        if (Utilities.ATLEAST_Q && Themes.getAttrBoolean(launcher, R.attr.isWorkspaceDarkText)) {
            setOnLightBackground(true);
        }
        this.mColorExtractor = LocalColorExtractor.newInstance(getContext());
    }

    /* renamed from: setColorResources */
    public void lambda$onColorsChanged$0$LauncherAppWidgetHostView(SparseIntArray sparseIntArray) {
        if (sparseIntArray == null) {
            resetColorResources();
        } else {
            super.setColorResources(sparseIntArray);
        }
    }

    public boolean onLongClick(View view) {
        if (this.mIsScrollable) {
            this.mLauncher.getDragLayer().requestDisallowInterceptTouchEvent(false);
        }
        view.performLongClick();
        return true;
    }

    public void setAppWidget(int i, AppWidgetProviderInfo appWidgetProviderInfo) {
        super.setAppWidget(i, appWidgetProviderInfo);
        if (!this.mTrackingWidgetUpdate && Utilities.ATLEAST_Q) {
            this.mTrackingWidgetUpdate = true;
            Trace.beginAsyncSection(TRACE_METHOD_NAME + appWidgetProviderInfo.provider, i);
            Log.i(TAG, "App widget created with id: " + i);
        }
    }

    public void updateAppWidget(RemoteViews remoteViews) {
        if (this.mTrackingWidgetUpdate && remoteViews != null && Utilities.ATLEAST_Q) {
            Log.i(TAG, "App widget with id: " + getAppWidgetId() + " loaded");
            Trace.endAsyncSection(TRACE_METHOD_NAME + getAppWidgetInfo().provider, getAppWidgetId());
            this.mTrackingWidgetUpdate = false;
        }
        if (isDeferringUpdates()) {
            this.mDeferredRemoteViews = remoteViews;
            return;
        }
        this.mDeferredRemoteViews = null;
        super.updateAppWidget(remoteViews);
        checkIfAutoAdvance();
        this.mReinflateOnConfigChange = !isSameOrientation();
    }

    private boolean isSameOrientation() {
        return this.mLauncher.getResources().getConfiguration().orientation == this.mLauncher.getOrientation();
    }

    private boolean checkScrollableRecursively(ViewGroup viewGroup) {
        if (viewGroup instanceof AdapterView) {
            return true;
        }
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if ((childAt instanceof ViewGroup) && checkScrollableRecursively((ViewGroup) childAt)) {
                return true;
            }
        }
        return false;
    }

    private boolean isDeferringUpdates() {
        return SystemClock.uptimeMillis() < this.mDeferUpdatesUntilMillis;
    }

    public void beginDeferringUpdates() {
        this.mDeferUpdatesUntilMillis = SystemClock.uptimeMillis() + UPDATE_LOCK_TIMEOUT_MILLIS;
    }

    public void endDeferringUpdates() {
        this.mDeferUpdatesUntilMillis = 0;
        RemoteViews remoteViews = this.mDeferredRemoteViews;
        this.mDeferredRemoteViews = null;
        SparseIntArray sparseIntArray = this.mDeferredColorChange;
        boolean z = this.mHasDeferredColorChange;
        this.mDeferredColorChange = null;
        this.mHasDeferredColorChange = false;
        if (remoteViews != null) {
            updateAppWidget(remoteViews);
        }
        if (z) {
            onColorsChanged(sparseIntArray);
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            DragLayer dragLayer = this.mLauncher.getDragLayer();
            if (this.mIsScrollable) {
                dragLayer.requestDisallowInterceptTouchEvent(true);
            }
            dragLayer.setTouchCompleteListener(this);
        }
        this.mLongPressHelper.onTouchEvent(motionEvent);
        return this.mLongPressHelper.hasPerformedLongPress();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        this.mLongPressHelper.onTouchEvent(motionEvent);
        return true;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mIsAttachedToWindow = true;
        checkIfAutoAdvance();
        this.mColorExtractor.setListener(this);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mIsAttachedToWindow = false;
        checkIfAutoAdvance();
        this.mColorExtractor.setListener((LocalColorExtractor.Listener) null);
    }

    public void cancelLongPress() {
        super.cancelLongPress();
        this.mLongPressHelper.cancelLongPress();
    }

    public AppWidgetProviderInfo getAppWidgetInfo() {
        AppWidgetProviderInfo appWidgetInfo = super.getAppWidgetInfo();
        if (appWidgetInfo == null || (appWidgetInfo instanceof LauncherAppWidgetProviderInfo)) {
            return appWidgetInfo;
        }
        throw new IllegalStateException("Launcher widget must have LauncherAppWidgetProviderInfo");
    }

    public void onTouchComplete() {
        if (!this.mLongPressHelper.hasPerformedLongPress()) {
            this.mLongPressHelper.cancelLongPress();
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.mIsScrollable = checkScrollableRecursively(this);
        if (!this.mIsInDragMode && (getTag() instanceof LauncherAppWidgetInfo)) {
            this.mTempRect.set(i, i2, i3, i4);
            this.mColorExtractor.setWorkspaceLocation(this.mTempRect, (View) getParent(), ((LauncherAppWidgetInfo) getTag()).screenId);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.mIsInDragMode && this.mDragContentWidth > 0 && this.mDragContentHeight > 0 && getChildCount() == 1) {
            measureChild(getChildAt(0), View.MeasureSpec.getSize(this.mDragContentWidth), View.MeasureSpec.getSize(this.mDragContentHeight));
        }
    }

    public void startDrag() {
        this.mIsInDragMode = true;
        if (!(getScaleX() == 1.0f && getScaleY() == 1.0f) && getChildCount() == 1) {
            this.mDragContentWidth = getChildAt(0).getMeasuredWidth();
            this.mDragContentHeight = getChildAt(0).getMeasuredHeight();
        }
    }

    public void handleDrag(Rect rect, View view, int i) {
        if (this.mIsInDragMode) {
            this.mColorExtractor.setWorkspaceLocation(rect, view, i);
        }
    }

    public void endDrag() {
        this.mIsInDragMode = false;
        this.mDragContentWidth = 0;
        this.mDragContentHeight = 0;
        requestLayout();
    }

    public void onColorsChanged(SparseIntArray sparseIntArray) {
        if (isDeferringUpdates()) {
            this.mDeferredColorChange = sparseIntArray;
            this.mHasDeferredColorChange = true;
            return;
        }
        this.mDeferredColorChange = null;
        this.mHasDeferredColorChange = false;
        post(new Runnable(sparseIntArray) {
            public final /* synthetic */ SparseIntArray f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                LauncherAppWidgetHostView.this.lambda$onColorsChanged$0$LauncherAppWidgetHostView(this.f$1);
            }
        });
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(getClass().getName());
    }

    /* access modifiers changed from: protected */
    public void onWindowVisibilityChanged(int i) {
        super.onWindowVisibilityChanged(i);
        maybeRegisterAutoAdvance();
    }

    private void checkIfAutoAdvance() {
        boolean z;
        Advanceable advanceable = getAdvanceable();
        boolean z2 = false;
        if (advanceable != null) {
            advanceable.fyiWillBeAdvancedByHostKThx();
            z = true;
        } else {
            z = false;
        }
        SparseBooleanArray sparseBooleanArray = sAutoAdvanceWidgetIds;
        if (sparseBooleanArray.indexOfKey(getAppWidgetId()) >= 0) {
            z2 = true;
        }
        if (z != z2) {
            if (z) {
                sparseBooleanArray.put(getAppWidgetId(), true);
            } else {
                sparseBooleanArray.delete(getAppWidgetId());
            }
            maybeRegisterAutoAdvance();
        }
    }

    private Advanceable getAdvanceable() {
        AppWidgetProviderInfo appWidgetInfo = getAppWidgetInfo();
        if (appWidgetInfo == null || appWidgetInfo.autoAdvanceViewId == -1 || !this.mIsAttachedToWindow) {
            return null;
        }
        View findViewById = findViewById(appWidgetInfo.autoAdvanceViewId);
        if (findViewById instanceof Advanceable) {
            return (Advanceable) findViewById;
        }
        return null;
    }

    private void maybeRegisterAutoAdvance() {
        Handler handler = getHandler();
        boolean z = getWindowVisibility() == 0 && handler != null && sAutoAdvanceWidgetIds.indexOfKey(getAppWidgetId()) >= 0;
        if (z != this.mIsAutoAdvanceRegistered) {
            this.mIsAutoAdvanceRegistered = z;
            if (this.mAutoAdvanceRunnable == null) {
                this.mAutoAdvanceRunnable = new Runnable() {
                    public final void run() {
                        LauncherAppWidgetHostView.this.runAutoAdvance();
                    }
                };
            }
            handler.removeCallbacks(this.mAutoAdvanceRunnable);
            scheduleNextAdvance();
        }
    }

    private void scheduleNextAdvance() {
        if (this.mIsAutoAdvanceRegistered) {
            long uptimeMillis = SystemClock.uptimeMillis();
            long indexOfKey = uptimeMillis + (ADVANCE_INTERVAL - (uptimeMillis % ADVANCE_INTERVAL)) + (((long) sAutoAdvanceWidgetIds.indexOfKey(getAppWidgetId())) * 250);
            Handler handler = getHandler();
            if (handler != null) {
                handler.postAtTime(this.mAutoAdvanceRunnable, indexOfKey);
            }
        }
    }

    /* access modifiers changed from: private */
    public void runAutoAdvance() {
        Advanceable advanceable = getAdvanceable();
        if (advanceable != null) {
            advanceable.advance();
        }
        scheduleNextAdvance();
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.mReinflateOnConfigChange && isSameOrientation()) {
            this.mReinflateOnConfigChange = false;
            reInflate();
        }
    }

    public void reInflate() {
        LauncherAppWidgetInfo launcherAppWidgetInfo;
        if (isAttachedToWindow() && (launcherAppWidgetInfo = (LauncherAppWidgetInfo) getTag()) != null) {
            this.mLauncher.removeItem(this, launcherAppWidgetInfo, false, "widget removed because of configuration change");
            this.mLauncher.bindAppWidget(launcherAppWidgetInfo);
        }
    }

    /* access modifiers changed from: protected */
    public boolean shouldAllowDirectClick() {
        if (!(getTag() instanceof ItemInfo)) {
            return false;
        }
        ItemInfo itemInfo = (ItemInfo) getTag();
        if (itemInfo.spanX == 1 && itemInfo.spanY == 1) {
            return true;
        }
        return false;
    }
}
