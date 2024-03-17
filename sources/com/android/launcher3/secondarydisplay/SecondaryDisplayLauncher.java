package com.android.launcher3.secondarydisplay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.inputmethod.InputMethodManager;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.R;
import com.android.launcher3.allapps.ActivityAllAppsContainerView;
import com.android.launcher3.allapps.AllAppsStore;
import com.android.launcher3.model.BgDataModel;
import com.android.launcher3.model.StringCache;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.popup.PopupContainerWithArrow;
import com.android.launcher3.popup.PopupDataProvider;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.Themes;
import com.android.launcher3.views.BaseDragLayer;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SecondaryDisplayLauncher extends BaseDraggingActivity implements BgDataModel.Callbacks {
    private boolean mAppDrawerShown = false;
    /* access modifiers changed from: private */
    public View mAppsButton;
    /* access modifiers changed from: private */
    public ActivityAllAppsContainerView<SecondaryDisplayLauncher> mAppsView;
    private BaseDragLayer mDragLayer;
    private LauncherModel mModel;
    private PopupDataProvider mPopupDataProvider;
    private StringCache mStringCache;

    public <T extends View> T getOverviewPanel() {
        return null;
    }

    /* access modifiers changed from: protected */
    public void reapplyUi() {
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mModel = LauncherAppState.getInstance(this).getModel();
        if (getWindow().getDecorView().isAttachedToWindow()) {
            initUi();
        }
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        initUi();
    }

    private void initUi() {
        if (this.mDragLayer == null) {
            this.mDeviceProfile = new InvariantDeviceProfile((Context) this, getWindow().getDecorView().getDisplay()).getDeviceProfile(this).toBuilder(this).setMultiWindowMode(true).setTransposeLayoutWithOrientation(false).build();
            this.mDeviceProfile.autoResizeAllAppsCells();
            setContentView(R.layout.secondary_launcher);
            this.mDragLayer = (BaseDragLayer) findViewById(R.id.drag_layer);
            this.mAppsView = (ActivityAllAppsContainerView) findViewById(R.id.apps_view);
            this.mAppsButton = findViewById(R.id.all_apps_button);
            AllAppsStore appsStore = this.mAppsView.getAppsStore();
            Objects.requireNonNull(appsStore);
            this.mPopupDataProvider = new PopupDataProvider(new Consumer() {
                public final void accept(Object obj) {
                    AllAppsStore.this.updateNotificationDots((Predicate) obj);
                }
            });
            this.mModel.addCallbacksAndLoad(this);
        }
    }

    public void onNewIntent(Intent intent) {
        View peekDecorView;
        super.onNewIntent(intent);
        if (!(!"android.intent.action.MAIN".equals(intent.getAction()) || (peekDecorView = getWindow().peekDecorView()) == null || peekDecorView.getWindowToken() == null)) {
            ((InputMethodManager) getSystemService(InputMethodManager.class)).hideSoftInputFromWindow(peekDecorView.getWindowToken(), 0);
        }
        showAppDrawer(false);
    }

    public void onBackPressed() {
        if (!finishAutoCancelActionMode()) {
            AbstractFloatingView topOpenView = AbstractFloatingView.getTopOpenView(this);
            if (topOpenView == null || !topOpenView.onBackPressed()) {
                showAppDrawer(false);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        this.mModel.removeCallbacks(this);
    }

    public boolean isAppDrawerShown() {
        return this.mAppDrawerShown;
    }

    public ActivityAllAppsContainerView<SecondaryDisplayLauncher> getAppsView() {
        return this.mAppsView;
    }

    public View getRootView() {
        return this.mDragLayer;
    }

    public BaseDragLayer getDragLayer() {
        return this.mDragLayer;
    }

    public void bindIncrementalDownloadProgressUpdated(AppInfo appInfo) {
        this.mAppsView.getAppsStore().updateProgressBar(appInfo);
    }

    public void onAppsButtonClicked(View view) {
        showAppDrawer(true);
    }

    public void showAppDrawer(boolean z) {
        if (z != this.mAppDrawerShown) {
            float hypot = (float) Math.hypot((double) this.mAppsView.getWidth(), (double) this.mAppsView.getHeight());
            float dialogCornerRadius = Themes.getDialogCornerRadius(this);
            float width = ((float) this.mAppsButton.getWidth()) / 2.0f;
            float[] fArr = {width, width};
            this.mDragLayer.getDescendantCoordRelativeToSelf(this.mAppsButton, fArr);
            this.mDragLayer.mapCoordInSelfToDescendant((View) this.mAppsView, fArr);
            ActivityAllAppsContainerView<SecondaryDisplayLauncher> activityAllAppsContainerView = this.mAppsView;
            int i = (int) fArr[0];
            int i2 = (int) fArr[1];
            float f = z ? dialogCornerRadius : hypot;
            if (!z) {
                hypot = dialogCornerRadius;
            }
            Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(activityAllAppsContainerView, i, i2, f, hypot);
            if (z) {
                this.mAppDrawerShown = true;
                this.mAppsView.setVisibility(0);
                this.mAppsButton.setVisibility(4);
            } else {
                this.mAppDrawerShown = false;
                createCircularReveal.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        SecondaryDisplayLauncher.this.mAppsView.setVisibility(4);
                        SecondaryDisplayLauncher.this.mAppsButton.setVisibility(0);
                        SecondaryDisplayLauncher.this.mAppsView.getSearchUiManager().resetSearch();
                    }
                });
            }
            createCircularReveal.start();
        }
    }

    public void bindDeepShortcutMap(HashMap<ComponentKey, Integer> hashMap) {
        this.mPopupDataProvider.setDeepShortcutMap(hashMap);
    }

    public void bindAllApplications(AppInfo[] appInfoArr, int i) {
        this.mAppsView.getAppsStore().setApps(appInfoArr, i);
        PopupContainerWithArrow.dismissInvalidPopup(this);
    }

    public StringCache getStringCache() {
        return this.mStringCache;
    }

    public void bindStringCache(StringCache stringCache) {
        this.mStringCache = stringCache;
    }

    public PopupDataProvider getPopupDataProvider() {
        return this.mPopupDataProvider;
    }

    public View.OnClickListener getItemOnClickListener() {
        return new View.OnClickListener() {
            public final void onClick(View view) {
                SecondaryDisplayLauncher.this.onIconClicked(view);
            }
        };
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0029  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x002d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onIconClicked(android.view.View r4) {
        /*
            r3 = this;
            android.os.IBinder r0 = r4.getWindowToken()
            if (r0 != 0) goto L_0x0007
            return
        L_0x0007:
            java.lang.Object r0 = r4.getTag()
            boolean r1 = r0 instanceof com.android.launcher3.model.data.ItemInfo
            if (r1 == 0) goto L_0x0035
            com.android.launcher3.model.data.ItemInfo r0 = (com.android.launcher3.model.data.ItemInfo) r0
            boolean r1 = r0 instanceof com.android.launcher3.model.data.ItemInfoWithIcon
            if (r1 == 0) goto L_0x0023
            r1 = r0
            com.android.launcher3.model.data.ItemInfoWithIcon r1 = (com.android.launcher3.model.data.ItemInfoWithIcon) r1
            int r2 = r1.runtimeStatusFlags
            r2 = r2 & 1024(0x400, float:1.435E-42)
            if (r2 == 0) goto L_0x0023
            android.content.Intent r1 = r1.getMarketIntent(r3)
            goto L_0x0027
        L_0x0023:
            android.content.Intent r1 = r0.getIntent()
        L_0x0027:
            if (r1 == 0) goto L_0x002d
            r3.startActivitySafely(r4, r1, r0)
            goto L_0x0035
        L_0x002d:
            java.lang.IllegalArgumentException r4 = new java.lang.IllegalArgumentException
            java.lang.String r0 = "Input must have a valid intent"
            r4.<init>(r0)
            throw r4
        L_0x0035:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.secondarydisplay.SecondaryDisplayLauncher.onIconClicked(android.view.View):void");
    }
}
