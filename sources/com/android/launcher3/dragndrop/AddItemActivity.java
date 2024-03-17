package com.android.launcher3.dragndrop;

import android.app.ActivityOptions;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.content.pm.ShortcutInfo;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.R;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.ItemInstallQueue;
import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.pm.PinRequestHelper;
import com.android.launcher3.pm.ShortcutConfigActivityInfo;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.SystemUiController;
import com.android.launcher3.views.AbstractSlideInView;
import com.android.launcher3.views.BaseDragLayer;
import com.android.launcher3.widget.AddItemWidgetsBottomSheet;
import com.android.launcher3.widget.LauncherAppWidgetHost;
import com.android.launcher3.widget.LauncherAppWidgetProviderInfo;
import com.android.launcher3.widget.NavigableAppWidgetHostView;
import com.android.launcher3.widget.PendingAddShortcutInfo;
import com.android.launcher3.widget.PendingAddWidgetInfo;
import com.android.launcher3.widget.WidgetCell;
import com.android.launcher3.widget.WidgetCellPreview;
import com.android.launcher3.widget.WidgetImageView;
import com.android.launcher3.widget.WidgetManagerHelper;
import java.util.function.Supplier;

public class AddItemActivity extends BaseActivity implements View.OnLongClickListener, View.OnTouchListener, AbstractSlideInView.OnCloseListener {
    private static final int REQUEST_BIND_APPWIDGET = 1;
    private static final int SHADOW_SIZE = 10;
    private static final String STATE_EXTRA_WIDGET_ID = "state.widget.id";
    private AccessibilityManager mAccessibilityManager;
    private LauncherAppState mApp;
    private LauncherAppWidgetHost mAppWidgetHost;
    private WidgetManagerHelper mAppWidgetManager;
    private BaseDragLayer<AddItemActivity> mDragLayer;
    private boolean mFinishOnPause = false;
    private InvariantDeviceProfile mIdp;
    private final PointF mLastTouchPos = new PointF();
    private int mPendingBindWidgetId;
    private LauncherApps.PinItemRequest mRequest;
    private AddItemWidgetsBottomSheet mSlideInView;
    /* access modifiers changed from: private */
    public WidgetCell mWidgetCell;
    private Bundle mWidgetOptions;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LauncherApps.PinItemRequest pinItemRequest = PinRequestHelper.getPinItemRequest(getIntent());
        this.mRequest = pinItemRequest;
        if (pinItemRequest == null) {
            finish();
            return;
        }
        LauncherAppState instance = LauncherAppState.getInstance(this);
        this.mApp = instance;
        InvariantDeviceProfile invariantDeviceProfile = instance.getInvariantDeviceProfile();
        this.mIdp = invariantDeviceProfile;
        this.mDeviceProfile = invariantDeviceProfile.getDeviceProfile(getApplicationContext());
        setContentView(R.layout.add_item_confirmation_activity);
        getWindow().setFlags(512, 512);
        BaseDragLayer<AddItemActivity> baseDragLayer = (BaseDragLayer) findViewById(R.id.add_item_drag_layer);
        this.mDragLayer = baseDragLayer;
        baseDragLayer.recreateControllers();
        this.mWidgetCell = (WidgetCell) findViewById(R.id.widget_cell);
        this.mAccessibilityManager = (AccessibilityManager) getApplicationContext().getSystemService(AccessibilityManager.class);
        if (this.mRequest.getRequestType() == 1) {
            setupShortcut();
        } else if (!setupWidget()) {
            finish();
        }
        WidgetCellPreview widgetCellPreview = (WidgetCellPreview) this.mWidgetCell.findViewById(R.id.widget_preview_container);
        widgetCellPreview.setOnTouchListener(this);
        widgetCellPreview.setOnLongClickListener(this);
        if (bundle == null) {
            logCommand(StatsLogManager.LauncherEvent.LAUNCHER_ADD_EXTERNAL_ITEM_START);
        }
        ((TextView) findViewById(R.id.widget_appName)).setText(getApplicationInfo().labelRes);
        AddItemWidgetsBottomSheet addItemWidgetsBottomSheet = (AddItemWidgetsBottomSheet) findViewById(R.id.add_item_bottom_sheet);
        this.mSlideInView = addItemWidgetsBottomSheet;
        addItemWidgetsBottomSheet.addOnCloseListener(this);
        this.mSlideInView.show();
        setupNavBarColor();
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        this.mLastTouchPos.set(motionEvent.getX(), motionEvent.getY());
        return false;
    }

    public boolean onLongClick(View view) {
        PinItemDragListener pinItemDragListener;
        WidgetImageView widgetView = this.mWidgetCell.getWidgetView();
        NavigableAppWidgetHostView appWidgetHostViewPreview = this.mWidgetCell.getAppWidgetHostViewPreview();
        if (widgetView.getDrawable() == null && appWidgetHostViewPreview == null) {
            return false;
        }
        if (appWidgetHostViewPreview != null) {
            Rect rect = new Rect();
            appWidgetHostViewPreview.getSourceVisualDragBounds(rect);
            float appWidgetHostViewScale = this.mWidgetCell.getAppWidgetHostViewScale();
            rect.offset(appWidgetHostViewPreview.getLeft() - ((int) (this.mLastTouchPos.x * appWidgetHostViewScale)), appWidgetHostViewPreview.getTop() - ((int) (this.mLastTouchPos.y * appWidgetHostViewScale)));
            pinItemDragListener = new PinItemDragListener(this.mRequest, rect, appWidgetHostViewPreview.getMeasuredWidth(), appWidgetHostViewPreview.getMeasuredWidth(), appWidgetHostViewScale);
        } else {
            Rect bitmapBounds = widgetView.getBitmapBounds();
            bitmapBounds.offset(widgetView.getLeft() - ((int) this.mLastTouchPos.x), widgetView.getTop() - ((int) this.mLastTouchPos.y));
            pinItemDragListener = new PinItemDragListener(this.mRequest, bitmapBounds, widgetView.getDrawable().getIntrinsicWidth(), widgetView.getWidth());
        }
        view.startDragAndDrop(new ClipData(new ClipDescription("", new String[]{pinItemDragListener.getMimeType()}), new ClipData.Item("")), new View.DragShadowBuilder(view) {
            public void onDrawShadow(Canvas canvas) {
            }

            public void onProvideShadowMetrics(Point point, Point point2) {
                point.set(10, 10);
                point2.set(5, 5);
            }
        }, (Object) null, 256);
        Intent flags = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.HOME").setPackage(getPackageName()).setFlags(268435456);
        Launcher.ACTIVITY_TRACKER.registerCallback(pinItemDragListener);
        startActivity(flags, ActivityOptions.makeCustomAnimation(this, 0, 17432577).toBundle());
        logCommand(StatsLogManager.LauncherEvent.LAUNCHER_ADD_EXTERNAL_ITEM_DRAGGED);
        this.mFinishOnPause = true;
        return false;
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        if (this.mFinishOnPause) {
            finish();
        }
    }

    private void setupShortcut() {
        PinShortcutRequestActivityInfo pinShortcutRequestActivityInfo = new PinShortcutRequestActivityInfo(this.mRequest, this);
        this.mWidgetCell.getWidgetView().setTag(new PendingAddShortcutInfo(pinShortcutRequestActivityInfo));
        applyWidgetItemAsync(new Supplier(pinShortcutRequestActivityInfo) {
            public final /* synthetic */ PinShortcutRequestActivityInfo f$1;

            {
                this.f$1 = r2;
            }

            public final Object get() {
                return AddItemActivity.this.lambda$setupShortcut$0$AddItemActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ WidgetItem lambda$setupShortcut$0$AddItemActivity(PinShortcutRequestActivityInfo pinShortcutRequestActivityInfo) {
        return new WidgetItem((ShortcutConfigActivityInfo) pinShortcutRequestActivityInfo, this.mApp.getIconCache(), getPackageManager());
    }

    private boolean setupWidget() {
        LauncherAppWidgetProviderInfo fromProviderInfo = LauncherAppWidgetProviderInfo.fromProviderInfo(this, this.mRequest.getAppWidgetProviderInfo(this));
        if (fromProviderInfo.minSpanX > this.mIdp.numColumns || fromProviderInfo.minSpanY > this.mIdp.numRows) {
            return false;
        }
        this.mWidgetCell.setRemoteViewsPreview(PinItemDragListener.getPreview(this.mRequest));
        this.mAppWidgetManager = new WidgetManagerHelper(this);
        this.mAppWidgetHost = new LauncherAppWidgetHost(this);
        PendingAddWidgetInfo pendingAddWidgetInfo = new PendingAddWidgetInfo(fromProviderInfo, LauncherSettings.Favorites.CONTAINER_PIN_WIDGETS);
        pendingAddWidgetInfo.spanX = Math.min(this.mIdp.numColumns, fromProviderInfo.spanX);
        pendingAddWidgetInfo.spanY = Math.min(this.mIdp.numRows, fromProviderInfo.spanY);
        this.mWidgetOptions = pendingAddWidgetInfo.getDefaultSizeOptions(this);
        this.mWidgetCell.getWidgetView().setTag(pendingAddWidgetInfo);
        applyWidgetItemAsync(new Supplier(fromProviderInfo) {
            public final /* synthetic */ LauncherAppWidgetProviderInfo f$1;

            {
                this.f$1 = r2;
            }

            public final Object get() {
                return AddItemActivity.this.lambda$setupWidget$1$AddItemActivity(this.f$1);
            }
        });
        return true;
    }

    public /* synthetic */ WidgetItem lambda$setupWidget$1$AddItemActivity(LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo) {
        return new WidgetItem(launcherAppWidgetProviderInfo, this.mIdp, this.mApp.getIconCache());
    }

    private void applyWidgetItemAsync(final Supplier<WidgetItem> supplier) {
        new AsyncTask<Void, Void, WidgetItem>() {
            /* access modifiers changed from: protected */
            public WidgetItem doInBackground(Void... voidArr) {
                return (WidgetItem) supplier.get();
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(WidgetItem widgetItem) {
                AddItemActivity.this.mWidgetCell.applyFromCellItem(widgetItem);
            }
        }.executeOnExecutor(Executors.MODEL_EXECUTOR, new Void[0]);
    }

    public void onCancelClick(View view) {
        logCommand(StatsLogManager.LauncherEvent.LAUNCHER_ADD_EXTERNAL_ITEM_CANCELLED);
        this.mSlideInView.close(true);
    }

    public void onPlaceAutomaticallyClick(View view) {
        if (this.mRequest.getRequestType() == 1) {
            ShortcutInfo shortcutInfo = this.mRequest.getShortcutInfo();
            ItemInstallQueue.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).queueItem(shortcutInfo);
            logCommand(StatsLogManager.LauncherEvent.LAUNCHER_ADD_EXTERNAL_ITEM_PLACED_AUTOMATICALLY);
            this.mRequest.accept();
            CharSequence longLabel = shortcutInfo.getLongLabel();
            if (TextUtils.isEmpty(longLabel)) {
                longLabel = shortcutInfo.getShortLabel();
            }
            sendWidgetAddedToScreenAccessibilityEvent(longLabel.toString());
            this.mSlideInView.close(true);
            return;
        }
        this.mPendingBindWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
        AppWidgetProviderInfo appWidgetProviderInfo = this.mRequest.getAppWidgetProviderInfo(this);
        if (this.mAppWidgetManager.bindAppWidgetIdIfAllowed(this.mPendingBindWidgetId, appWidgetProviderInfo, this.mWidgetOptions)) {
            sendWidgetAddedToScreenAccessibilityEvent(appWidgetProviderInfo.label);
            acceptWidget(this.mPendingBindWidgetId);
            return;
        }
        this.mAppWidgetHost.startBindFlow(this, this.mPendingBindWidgetId, this.mRequest.getAppWidgetProviderInfo(this), 1);
    }

    private void acceptWidget(int i) {
        ItemInstallQueue.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).queueItem(this.mRequest.getAppWidgetProviderInfo(this), i);
        this.mWidgetOptions.putInt(LauncherSettings.Favorites.APPWIDGET_ID, i);
        this.mRequest.accept(this.mWidgetOptions);
        logCommand(StatsLogManager.LauncherEvent.LAUNCHER_ADD_EXTERNAL_ITEM_PLACED_AUTOMATICALLY);
        this.mSlideInView.close(true);
    }

    public void onBackPressed() {
        logCommand(StatsLogManager.LauncherEvent.LAUNCHER_ADD_EXTERNAL_ITEM_BACK);
        this.mSlideInView.close(true);
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        int i3;
        if (i == 1) {
            if (intent != null) {
                i3 = intent.getIntExtra(LauncherSettings.Favorites.APPWIDGET_ID, this.mPendingBindWidgetId);
            } else {
                i3 = this.mPendingBindWidgetId;
            }
            if (i2 == -1) {
                acceptWidget(i3);
                return;
            }
            this.mAppWidgetHost.deleteAppWidgetId(i3);
            this.mPendingBindWidgetId = -1;
            return;
        }
        super.onActivityResult(i, i2, intent);
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(STATE_EXTRA_WIDGET_ID, this.mPendingBindWidgetId);
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        this.mPendingBindWidgetId = bundle.getInt(STATE_EXTRA_WIDGET_ID, this.mPendingBindWidgetId);
    }

    public BaseDragLayer getDragLayer() {
        return this.mDragLayer;
    }

    public void onSlideInViewClosed() {
        finish();
    }

    /* access modifiers changed from: protected */
    public void setupNavBarColor() {
        int i = 1;
        boolean z = (getApplicationContext().getResources().getConfiguration().uiMode & 48) == 32;
        SystemUiController systemUiController = getSystemUiController();
        if (z) {
            i = 2;
        }
        systemUiController.updateUiState(0, i);
    }

    private void sendWidgetAddedToScreenAccessibilityEvent(String str) {
        if (this.mAccessibilityManager.isEnabled()) {
            AccessibilityEvent obtain = AccessibilityEvent.obtain(16384);
            obtain.setContentDescription(getApplicationContext().getResources().getString(R.string.added_to_home_screen_accessibility_text, new Object[]{str}));
            this.mAccessibilityManager.sendAccessibilityEvent(obtain);
        }
    }

    private void logCommand(StatsLogManager.EventEnum eventEnum) {
        getStatsLogManager().logger().withItemInfo((ItemInfo) this.mWidgetCell.getWidgetView().getTag()).log(eventEnum);
    }
}
