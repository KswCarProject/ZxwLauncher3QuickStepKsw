package com.android.launcher3.widget;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseArray;
import android.widget.Toast;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.testing.TestLogging;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.widget.custom.CustomWidgetManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.IntConsumer;

public class LauncherAppWidgetHost extends AppWidgetHost {
    public static final int APPWIDGET_HOST_ID = 1024;
    private static final int FLAGS_SHOULD_LISTEN = 14;
    private static final int FLAG_ACTIVITY_RESUMED = 8;
    private static final int FLAG_ACTIVITY_STARTED = 4;
    private static final int FLAG_LISTENING = 1;
    private static final int FLAG_STATE_IS_NORMAL = 2;
    private static final String KEY_SPLASH_SCREEN_STYLE = "android.activity.splashScreenStyle";
    private static final int SPLASH_SCREEN_STYLE_EMPTY = 0;
    private IntConsumer mAppWidgetRemovedCallback;
    private final Context mContext;
    private int mFlags;
    private final SparseArray<PendingAppWidgetHostView> mPendingViews;
    private final ArrayList<ProviderChangedListener> mProviderChangeListeners;
    private final SparseArray<LauncherAppWidgetHostView> mViews;

    public interface ProviderChangedListener {
        void notifyWidgetProvidersChanged();
    }

    public LauncherAppWidgetHost(Context context) {
        this(context, (IntConsumer) null);
    }

    public LauncherAppWidgetHost(Context context, IntConsumer intConsumer) {
        super(context, 1024);
        this.mProviderChangeListeners = new ArrayList<>();
        this.mViews = new SparseArray<>();
        this.mPendingViews = new SparseArray<>();
        this.mFlags = 2;
        this.mAppWidgetRemovedCallback = null;
        this.mContext = context;
        this.mAppWidgetRemovedCallback = intConsumer;
    }

    /* access modifiers changed from: protected */
    public LauncherAppWidgetHostView onCreateView(Context context, int i, AppWidgetProviderInfo appWidgetProviderInfo) {
        LauncherAppWidgetHostView launcherAppWidgetHostView;
        if (this.mPendingViews.get(i) != null) {
            launcherAppWidgetHostView = this.mPendingViews.get(i);
            this.mPendingViews.remove(i);
        } else {
            launcherAppWidgetHostView = new LauncherAppWidgetHostView(context);
        }
        this.mViews.put(i, launcherAppWidgetHostView);
        return launcherAppWidgetHostView;
    }

    public void startListening() {
        this.mFlags |= 1;
        try {
            super.startListening();
        } catch (Exception e) {
            if (!Utilities.isBinderSizeError(e)) {
                throw new RuntimeException(e);
            }
        }
        for (int size = this.mViews.size() - 1; size >= 0; size--) {
            LauncherAppWidgetHostView valueAt = this.mViews.valueAt(size);
            if (valueAt instanceof DeferredAppWidgetHostView) {
                valueAt.reInflate();
            }
        }
    }

    public void stopListening() {
        this.mFlags &= -2;
        super.stopListening();
    }

    public boolean isListening() {
        return (this.mFlags & 1) != 0;
    }

    private void setShouldListenFlag(int i, boolean z) {
        if (z) {
            this.mFlags = i | this.mFlags;
        } else {
            this.mFlags = (~i) & this.mFlags;
        }
        boolean isListening = isListening();
        if (!isListening && (this.mFlags & 14) == 14) {
            startListening();
        } else if (isListening && (this.mFlags & 4) == 0) {
            stopListening();
        }
    }

    public void setStateIsNormal(boolean z) {
        setShouldListenFlag(2, z);
    }

    public void setActivityStarted(boolean z) {
        setShouldListenFlag(4, z);
    }

    public void setActivityResumed(boolean z) {
        setShouldListenFlag(8, z);
    }

    public int allocateAppWidgetId() {
        return super.allocateAppWidgetId();
    }

    public void addProviderChangeListener(ProviderChangedListener providerChangedListener) {
        this.mProviderChangeListeners.add(providerChangedListener);
    }

    public void removeProviderChangeListener(ProviderChangedListener providerChangedListener) {
        this.mProviderChangeListeners.remove(providerChangedListener);
    }

    /* access modifiers changed from: protected */
    public void onProvidersChanged() {
        if (!this.mProviderChangeListeners.isEmpty()) {
            Iterator it = new ArrayList(this.mProviderChangeListeners).iterator();
            while (it.hasNext()) {
                ((ProviderChangedListener) it.next()).notifyWidgetProvidersChanged();
            }
        }
    }

    public void addPendingView(int i, PendingAppWidgetHostView pendingAppWidgetHostView) {
        this.mPendingViews.put(i, pendingAppWidgetHostView);
    }

    public AppWidgetHostView createView(Context context, int i, LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo) {
        if (launcherAppWidgetProviderInfo.isCustomWidget()) {
            LauncherAppWidgetHostView launcherAppWidgetHostView = new LauncherAppWidgetHostView(context);
            launcherAppWidgetHostView.setAppWidget(0, launcherAppWidgetProviderInfo);
            CustomWidgetManager.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).onViewCreated(launcherAppWidgetHostView);
            return launcherAppWidgetHostView;
        } else if ((this.mFlags & 1) == 0) {
            DeferredAppWidgetHostView deferredAppWidgetHostView = new DeferredAppWidgetHostView(context);
            deferredAppWidgetHostView.setAppWidget(i, launcherAppWidgetProviderInfo);
            this.mViews.put(i, deferredAppWidgetHostView);
            return deferredAppWidgetHostView;
        } else {
            try {
                return super.createView(context, i, launcherAppWidgetProviderInfo);
            } catch (Exception e) {
                if (Utilities.isBinderSizeError(e)) {
                    LauncherAppWidgetHostView launcherAppWidgetHostView2 = this.mViews.get(i);
                    if (launcherAppWidgetHostView2 == null) {
                        launcherAppWidgetHostView2 = onCreateView(this.mContext, i, (AppWidgetProviderInfo) launcherAppWidgetProviderInfo);
                    }
                    launcherAppWidgetHostView2.setAppWidget(i, launcherAppWidgetProviderInfo);
                    launcherAppWidgetHostView2.switchToErrorView();
                    return launcherAppWidgetHostView2;
                }
                throw new RuntimeException(e);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onProviderChanged(int i, AppWidgetProviderInfo appWidgetProviderInfo) {
        LauncherAppWidgetProviderInfo fromProviderInfo = LauncherAppWidgetProviderInfo.fromProviderInfo(this.mContext, appWidgetProviderInfo);
        super.onProviderChanged(i, fromProviderInfo);
        Context context = this.mContext;
        fromProviderInfo.initSpans(context, LauncherAppState.getIDP(context));
    }

    public void onAppWidgetRemoved(int i) {
        IntConsumer intConsumer = this.mAppWidgetRemovedCallback;
        if (intConsumer != null) {
            intConsumer.accept(i);
        }
    }

    public void deleteAppWidgetId(int i) {
        super.deleteAppWidgetId(i);
        this.mViews.remove(i);
    }

    public void clearViews() {
        super.clearViews();
        this.mViews.clear();
    }

    public void startBindFlow(BaseActivity baseActivity, int i, AppWidgetProviderInfo appWidgetProviderInfo, int i2) {
        baseActivity.startActivityForResult(new Intent("android.appwidget.action.APPWIDGET_BIND").putExtra(LauncherSettings.Favorites.APPWIDGET_ID, i).putExtra(LauncherSettings.Favorites.APPWIDGET_PROVIDER, appWidgetProviderInfo.provider).putExtra("appWidgetProviderProfile", appWidgetProviderInfo.getProfile()), i2);
    }

    public void startConfigActivity(BaseDraggingActivity baseDraggingActivity, int i, int i2) {
        try {
            TestLogging.recordEvent(TestProtocol.SEQUENCE_MAIN, "start: startConfigActivity");
            startAppWidgetConfigureActivityForResult(baseDraggingActivity, i, 0, i2, getConfigurationActivityOptions(baseDraggingActivity, i));
        } catch (ActivityNotFoundException | SecurityException unused) {
            Toast.makeText(baseDraggingActivity, R.string.activity_not_found, 0).show();
            sendActionCancelled(baseDraggingActivity, i2);
        }
    }

    private Bundle getConfigurationActivityOptions(BaseDraggingActivity baseDraggingActivity, int i) {
        LauncherAppWidgetHostView launcherAppWidgetHostView = this.mViews.get(i);
        if (launcherAppWidgetHostView == null) {
            return null;
        }
        Object tag = launcherAppWidgetHostView.getTag();
        if (!(tag instanceof ItemInfo)) {
            return null;
        }
        Bundle bundle = baseDraggingActivity.getActivityLaunchOptions(launcherAppWidgetHostView, (ItemInfo) tag).toBundle();
        bundle.putInt(KEY_SPLASH_SCREEN_STYLE, 0);
        return bundle;
    }

    private void sendActionCancelled(BaseActivity baseActivity, int i) {
        new Handler().post(new Runnable(i) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                BaseActivity.this.onActivityResult(this.f$1, 0, (Intent) null);
            }
        });
    }
}
