package com.android.launcher3;

import android.app.WallpaperColors;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Display;
import android.view.View;
import com.android.launcher3.allapps.ActivityAllAppsContainerView;
import com.android.launcher3.allapps.search.DefaultSearchAdapterProvider;
import com.android.launcher3.allapps.search.SearchAdapterProvider;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.touch.ItemClickHandler;
import com.android.launcher3.util.ActivityOptionsWrapper;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.RunnableList;
import com.android.launcher3.util.Themes;
import com.android.launcher3.util.TraceHelper;
import com.android.launcher3.util.WindowBounds;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class BaseDraggingActivity extends BaseActivity implements WallpaperManager.OnColorsChangedListener, DisplayController.DisplayInfoChangeListener {
    public static final Object AUTO_CANCEL_ACTION_MODE = new Object();
    private static final String TAG = "BaseDraggingActivity";
    private ActionMode mCurrentActionMode;
    protected boolean mIsSafeModeEnabled;
    private RunnableList mOnResumeCallbacks = new RunnableList();
    private Runnable mOnStartCallback;
    private int mThemeRes = R.style.AppTheme;

    public abstract <T extends View> T getOverviewPanel();

    public abstract View getRootView();

    /* access modifiers changed from: protected */
    public abstract void reapplyUi();

    public void returnToHomescreen() {
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mIsSafeModeEnabled = ((Boolean) TraceHelper.allowIpcs("isSafeMode", new Supplier() {
            public final Object get() {
                return BaseDraggingActivity.this.lambda$onCreate$0$BaseDraggingActivity();
            }
        })).booleanValue();
        DisplayController.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).addChangeListener(this);
        if (Utilities.ATLEAST_P) {
            ((WallpaperManager) getSystemService(WallpaperManager.class)).addOnColorsChangedListener(this, Executors.MAIN_EXECUTOR.getHandler());
        }
        int activityThemeRes = Themes.getActivityThemeRes(this);
        if (activityThemeRes != this.mThemeRes) {
            this.mThemeRes = activityThemeRes;
            setTheme(activityThemeRes);
        }
    }

    public /* synthetic */ Boolean lambda$onCreate$0$BaseDraggingActivity() {
        return Boolean.valueOf(getPackageManager().isSafeMode());
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        this.mOnResumeCallbacks.executeAllAndClear();
    }

    public void addOnResumeCallback(Runnable runnable) {
        this.mOnResumeCallbacks.add(runnable);
    }

    public void onColorsChanged(WallpaperColors wallpaperColors, int i) {
        updateTheme();
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        updateTheme();
    }

    private void updateTheme() {
        if (this.mThemeRes != Themes.getActivityThemeRes(this)) {
            recreate();
        }
    }

    public void onActionModeStarted(ActionMode actionMode) {
        super.onActionModeStarted(actionMode);
        this.mCurrentActionMode = actionMode;
    }

    public void onActionModeFinished(ActionMode actionMode) {
        super.onActionModeFinished(actionMode);
        this.mCurrentActionMode = null;
    }

    public boolean finishAutoCancelActionMode() {
        ActionMode actionMode = this.mCurrentActionMode;
        if (actionMode == null || AUTO_CANCEL_ACTION_MODE != actionMode.getTag()) {
            return false;
        }
        this.mCurrentActionMode.finish();
        return true;
    }

    public ActivityOptionsWrapper getActivityLaunchOptions(View view, ItemInfo itemInfo) {
        ActivityOptionsWrapper activityLaunchOptions = super.getActivityLaunchOptions(view, itemInfo);
        RunnableList runnableList = activityLaunchOptions.onEndCallback;
        Objects.requireNonNull(runnableList);
        addOnResumeCallback(new Runnable() {
            public final void run() {
                RunnableList.this.executeAllAndDestroy();
            }
        });
        return activityLaunchOptions;
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        Runnable runnable = this.mOnStartCallback;
        if (runnable != null) {
            runnable.run();
            this.mOnStartCallback = null;
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        if (Utilities.ATLEAST_P) {
            ((WallpaperManager) getSystemService(WallpaperManager.class)).removeOnColorsChangedListener(this);
        }
        DisplayController.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).removeChangeListener(this);
    }

    public void runOnceOnStart(Runnable runnable) {
        this.mOnStartCallback = runnable;
    }

    public void clearRunOnceOnStartCallback() {
        this.mOnStartCallback = null;
    }

    /* access modifiers changed from: protected */
    public void onDeviceProfileInitiated() {
        if (this.mDeviceProfile.isVerticalBarLayout()) {
            this.mDeviceProfile.updateIsSeascape(this);
        }
    }

    public void onDisplayInfoChanged(Context context, DisplayController.Info info, int i) {
        if ((i & 2) != 0 && this.mDeviceProfile.updateIsSeascape(this)) {
            reapplyUi();
        }
    }

    public View.OnClickListener getItemOnClickListener() {
        return ItemClickHandler.INSTANCE;
    }

    /* access modifiers changed from: protected */
    public WindowBounds getMultiWindowDisplaySize() {
        if (Utilities.ATLEAST_R) {
            return WindowBounds.fromWindowMetrics(getWindowManager().getCurrentWindowMetrics());
        }
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        return new WindowBounds(new Rect(0, 0, point.x, point.y), new Rect());
    }

    public SearchAdapterProvider<?> createSearchAdapterProvider(ActivityAllAppsContainerView<?> activityAllAppsContainerView) {
        return new DefaultSearchAdapterProvider(this);
    }

    public boolean isAppBlockedForSafeMode() {
        return this.mIsSafeModeEnabled;
    }
}
