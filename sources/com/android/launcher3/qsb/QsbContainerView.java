package com.android.launcher3.qsb;

import android.app.SearchManager;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.widget.FrameLayout;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.graphics.FragmentWithPreview;
import com.android.launcher3.qsb.QsbContainerView;
import com.android.launcher3.widget.util.WidgetSizes;

public class QsbContainerView extends FrameLayout {
    public static final String SEARCH_PROVIDER_SETTINGS_KEY = "SEARCH_PROVIDER_PACKAGE_NAME";

    @FunctionalInterface
    public interface WidgetProvidersUpdateCallback {
        void onProvidersUpdated();
    }

    public interface WidgetViewFactory {
        QsbWidgetHostView newView(Context context);
    }

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return super.generateDefaultLayoutParams();
    }

    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return super.generateLayoutParams(attributeSet);
    }

    public /* bridge */ /* synthetic */ ViewOverlay getOverlay() {
        return super.getOverlay();
    }

    public static String getSearchWidgetPackageName(Context context) {
        String string = Settings.Global.getString(context.getContentResolver(), SEARCH_PROVIDER_SETTINGS_KEY);
        if (string != null) {
            return string;
        }
        SearchManager searchManager = (SearchManager) context.getSystemService(SearchManager.class);
        return searchManager.getGlobalSearchActivity() != null ? searchManager.getGlobalSearchActivity().getPackageName() : string;
    }

    public static AppWidgetProviderInfo getSearchWidgetProviderInfo(Context context) {
        String searchWidgetPackageName = getSearchWidgetPackageName(context);
        AppWidgetProviderInfo appWidgetProviderInfo = null;
        if (searchWidgetPackageName == null) {
            return null;
        }
        for (AppWidgetProviderInfo next : AppWidgetManager.getInstance(context).getInstalledProvidersForPackage(searchWidgetPackageName, (UserHandle) null)) {
            if (next.provider.getPackageName().equals(searchWidgetPackageName) && next.configure == null) {
                if ((next.widgetCategory & 4) != 0) {
                    return next;
                }
                if (appWidgetProviderInfo == null) {
                    appWidgetProviderInfo = next;
                }
            }
        }
        return appWidgetProviderInfo;
    }

    public static ComponentName getSearchComponentName(Context context) {
        AppWidgetProviderInfo searchWidgetProviderInfo = getSearchWidgetProviderInfo(context);
        if (searchWidgetProviderInfo != null) {
            return searchWidgetProviderInfo.provider;
        }
        String searchWidgetPackageName = getSearchWidgetPackageName(context);
        if (searchWidgetPackageName != null) {
            return new ComponentName(searchWidgetPackageName, searchWidgetPackageName);
        }
        return null;
    }

    public QsbContainerView(Context context) {
        super(context);
    }

    public QsbContainerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public QsbContainerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void setPadding(int i, int i2, int i3, int i4) {
        super.setPadding(0, 0, 0, 0);
    }

    /* access modifiers changed from: protected */
    public void setPaddingUnchecked(int i, int i2, int i3, int i4) {
        super.setPadding(i, i2, i3, i4);
    }

    public static class QsbFragment extends FragmentWithPreview {
        public static final int QSB_WIDGET_HOST_ID = 1026;
        private static final int REQUEST_BIND_QSB = 1;
        protected String mKeyWidgetId = "qsb_widget_id";
        private int mOrientation;
        private QsbWidgetHostView mQsb;
        private QsbWidgetHost mQsbWidgetHost;
        protected AppWidgetProviderInfo mWidgetInfo;
        private FrameLayout mWrapper;

        public boolean isQsbEnabled() {
            return false;
        }

        public void onInit(Bundle bundle) {
            this.mQsbWidgetHost = createHost();
            this.mOrientation = getContext().getResources().getConfiguration().orientation;
        }

        /* access modifiers changed from: protected */
        public QsbWidgetHost createHost() {
            return new QsbWidgetHost(getContext(), QSB_WIDGET_HOST_ID, $$Lambda$QsbContainerView$QsbFragment$NntLzMG_V9JH7e31aiJhm30VSM.INSTANCE, new WidgetProvidersUpdateCallback() {
                public final void onProvidersUpdated() {
                    QsbContainerView.QsbFragment.this.rebindFragment();
                }
            });
        }

        static /* synthetic */ QsbWidgetHostView lambda$createHost$0(Context context) {
            return new QsbWidgetHostView(context);
        }

        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            this.mWrapper = new FrameLayout(getContext());
            if (isQsbEnabled()) {
                this.mQsbWidgetHost.startListening();
                FrameLayout frameLayout = this.mWrapper;
                frameLayout.addView(createQsb(frameLayout));
            }
            return this.mWrapper;
        }

        private View createQsb(ViewGroup viewGroup) {
            AppWidgetProviderInfo searchWidgetProvider = getSearchWidgetProvider();
            this.mWidgetInfo = searchWidgetProvider;
            boolean z = false;
            if (searchWidgetProvider == null) {
                return getDefaultView(viewGroup, false);
            }
            Bundle createBindOptions = createBindOptions();
            Context context = getContext();
            AppWidgetManager instance = AppWidgetManager.getInstance(context);
            int i = -1;
            int i2 = Utilities.getPrefs(context).getInt(this.mKeyWidgetId, -1);
            AppWidgetProviderInfo appWidgetInfo = instance.getAppWidgetInfo(i2);
            if (appWidgetInfo != null && appWidgetInfo.provider.equals(this.mWidgetInfo.provider)) {
                z = true;
            }
            if (!z && !isInPreviewMode()) {
                if (i2 > -1) {
                    this.mQsbWidgetHost.deleteHost();
                }
                int allocateAppWidgetId = this.mQsbWidgetHost.allocateAppWidgetId();
                boolean bindAppWidgetIdIfAllowed = instance.bindAppWidgetIdIfAllowed(allocateAppWidgetId, this.mWidgetInfo.getProfile(), this.mWidgetInfo.provider, createBindOptions);
                if (!bindAppWidgetIdIfAllowed) {
                    this.mQsbWidgetHost.deleteAppWidgetId(allocateAppWidgetId);
                } else {
                    i = allocateAppWidgetId;
                }
                if (i2 != i) {
                    saveWidgetId(i);
                }
                z = bindAppWidgetIdIfAllowed;
                i2 = i;
            }
            if (!z) {
                return getDefaultView(viewGroup, true);
            }
            QsbWidgetHostView qsbWidgetHostView = (QsbWidgetHostView) this.mQsbWidgetHost.createView(context, i2, this.mWidgetInfo);
            this.mQsb = qsbWidgetHostView;
            qsbWidgetHostView.setId(R.id.qsb_widget);
            if (!isInPreviewMode() && !QsbContainerView.containsAll(AppWidgetManager.getInstance(context).getAppWidgetOptions(i2), createBindOptions)) {
                this.mQsb.updateAppWidgetOptions(createBindOptions);
            }
            return this.mQsb;
        }

        private void saveWidgetId(int i) {
            Utilities.getPrefs(getContext()).edit().putInt(this.mKeyWidgetId, i).apply();
        }

        public void onActivityResult(int i, int i2, Intent intent) {
            if (i != 1) {
                return;
            }
            if (i2 == -1) {
                saveWidgetId(intent.getIntExtra(LauncherSettings.Favorites.APPWIDGET_ID, -1));
                rebindFragment();
                return;
            }
            this.mQsbWidgetHost.deleteHost();
        }

        public void onResume() {
            super.onResume();
            QsbWidgetHostView qsbWidgetHostView = this.mQsb;
            if (qsbWidgetHostView != null && qsbWidgetHostView.isReinflateRequired(this.mOrientation)) {
                rebindFragment();
            }
        }

        public void onDestroy() {
            this.mQsbWidgetHost.stopListening();
            super.onDestroy();
        }

        /* access modifiers changed from: private */
        public void rebindFragment() {
            if (isQsbEnabled() && this.mWrapper != null && getContext() != null) {
                this.mWrapper.removeAllViews();
                FrameLayout frameLayout = this.mWrapper;
                frameLayout.addView(createQsb(frameLayout));
            }
        }

        /* access modifiers changed from: protected */
        public Bundle createBindOptions() {
            return WidgetSizes.getWidgetSizeOptions(getContext(), this.mWidgetInfo.provider, LauncherAppState.getIDP(getContext()).numColumns, 1);
        }

        /* access modifiers changed from: protected */
        public View getDefaultView(ViewGroup viewGroup, boolean z) {
            View defaultView = QsbWidgetHostView.getDefaultView(viewGroup);
            if (z) {
                View findViewById = defaultView.findViewById(R.id.btn_qsb_setup);
                findViewById.setVisibility(0);
                findViewById.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        QsbContainerView.QsbFragment.this.lambda$getDefaultView$1$QsbContainerView$QsbFragment(view);
                    }
                });
            }
            return defaultView;
        }

        public /* synthetic */ void lambda$getDefaultView$1$QsbContainerView$QsbFragment(View view) {
            startActivityForResult(new Intent("android.appwidget.action.APPWIDGET_BIND").putExtra(LauncherSettings.Favorites.APPWIDGET_ID, this.mQsbWidgetHost.allocateAppWidgetId()).putExtra(LauncherSettings.Favorites.APPWIDGET_PROVIDER, this.mWidgetInfo.provider), 1);
        }

        /* access modifiers changed from: protected */
        public AppWidgetProviderInfo getSearchWidgetProvider() {
            return QsbContainerView.getSearchWidgetProviderInfo(getContext());
        }
    }

    public static class QsbWidgetHost extends AppWidgetHost {
        private final WidgetViewFactory mViewFactory;
        private final WidgetProvidersUpdateCallback mWidgetsUpdateCallback;

        public QsbWidgetHost(Context context, int i, WidgetViewFactory widgetViewFactory, WidgetProvidersUpdateCallback widgetProvidersUpdateCallback) {
            super(context, i);
            this.mViewFactory = widgetViewFactory;
            this.mWidgetsUpdateCallback = widgetProvidersUpdateCallback;
        }

        public QsbWidgetHost(Context context, int i, WidgetViewFactory widgetViewFactory) {
            this(context, i, widgetViewFactory, (WidgetProvidersUpdateCallback) null);
        }

        /* access modifiers changed from: protected */
        public AppWidgetHostView onCreateView(Context context, int i, AppWidgetProviderInfo appWidgetProviderInfo) {
            return this.mViewFactory.newView(context);
        }

        /* access modifiers changed from: protected */
        public void onProvidersChanged() {
            super.onProvidersChanged();
            WidgetProvidersUpdateCallback widgetProvidersUpdateCallback = this.mWidgetsUpdateCallback;
            if (widgetProvidersUpdateCallback != null) {
                widgetProvidersUpdateCallback.onProvidersUpdated();
            }
        }
    }

    /* access modifiers changed from: private */
    public static boolean containsAll(Bundle bundle, Bundle bundle2) {
        for (String str : bundle2.keySet()) {
            Object obj = bundle2.get(str);
            Object obj2 = bundle.get(str);
            if (obj == null) {
                if (obj2 != null) {
                    return false;
                }
            } else if (!obj.equals(obj2)) {
                return false;
            }
        }
        return true;
    }
}
