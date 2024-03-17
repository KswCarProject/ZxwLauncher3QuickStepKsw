package com.android.launcher3;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Process;
import android.os.SystemClock;
import android.os.Trace;
import android.os.UserHandle;
import android.text.TextUtils;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.KeyboardShortcutGroup;
import android.view.KeyboardShortcutInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import com.android.launcher3.DropTarget;
import com.android.launcher3.InsettableFrameLayout;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.accessibility.BaseAccessibilityDelegate;
import com.android.launcher3.accessibility.LauncherAccessibilityDelegate;
import com.android.launcher3.allapps.ActivityAllAppsContainerView;
import com.android.launcher3.allapps.AllAppsRecyclerView;
import com.android.launcher3.allapps.AllAppsTransitionController;
import com.android.launcher3.allapps.DiscoveryBounce;
import com.android.launcher3.anim.PropertyListBuilder;
import com.android.launcher3.compat.AccessibilityManagerCompat;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.dot.DotInfo;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.dragndrop.DragView;
import com.android.launcher3.dragndrop.LauncherDragController;
import com.android.launcher3.folder.FolderGridOrganizer;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.icons.BitmapRenderer;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.keyboard.ViewGroupFocusHelper;
import com.android.launcher3.logger.LauncherAtom;
import com.android.launcher3.logging.FileLog;
import com.android.launcher3.logging.InstanceId;
import com.android.launcher3.logging.InstanceIdSequence;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.BgDataModel;
import com.android.launcher3.model.ItemInstallQueue;
import com.android.launcher3.model.ModelUtils;
import com.android.launcher3.model.ModelWriter;
import com.android.launcher3.model.StringCache;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.notification.NotificationListener;
import com.android.launcher3.pageindicators.PageIndicator;
import com.android.launcher3.pm.PinRequestHelper;
import com.android.launcher3.pm.UserCache;
import com.android.launcher3.popup.ArrowPopup;
import com.android.launcher3.popup.PopupContainerWithArrow;
import com.android.launcher3.popup.PopupDataProvider;
import com.android.launcher3.popup.SystemShortcut;
import com.android.launcher3.qsb.QsbContainerView;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.launcher3.states.RotationHelper;
import com.android.launcher3.testing.TestLogging;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.touch.AllAppsSwipeController;
import com.android.launcher3.touch.ItemClickHandler;
import com.android.launcher3.uioverrides.plugins.PluginManagerWrapper;
import com.android.launcher3.util.ActivityResultInfo;
import com.android.launcher3.util.ActivityTracker;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.IntArray;
import com.android.launcher3.util.IntSet;
import com.android.launcher3.util.ItemInfoMatcher;
import com.android.launcher3.util.OnboardingPrefs;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.util.PendingRequestArgs;
import com.android.launcher3.util.RunnableList;
import com.android.launcher3.util.SafeCloseable;
import com.android.launcher3.util.Themes;
import com.android.launcher3.util.TouchController;
import com.android.launcher3.util.TraceHelper;
import com.android.launcher3.util.UiThreadHelper;
import com.android.launcher3.util.ViewOnDrawExecutor;
import com.android.launcher3.views.AccessibilityActionsView;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.views.FloatingIconView;
import com.android.launcher3.views.FloatingSurfaceView;
import com.android.launcher3.views.OptionsPopupView;
import com.android.launcher3.views.ScrimView;
import com.android.launcher3.widget.LauncherAppWidgetHost;
import com.android.launcher3.widget.LauncherAppWidgetHostView;
import com.android.launcher3.widget.LauncherAppWidgetProviderInfo;
import com.android.launcher3.widget.PendingAddShortcutInfo;
import com.android.launcher3.widget.PendingAddWidgetInfo;
import com.android.launcher3.widget.PendingAppWidgetHostView;
import com.android.launcher3.widget.WidgetAddFlowHandler;
import com.android.launcher3.widget.WidgetManagerHelper;
import com.android.launcher3.widget.custom.CustomWidgetManager;
import com.android.launcher3.widget.model.WidgetsListBaseEntry;
import com.android.launcher3.widget.picker.WidgetsFullSheet;
import com.android.quickstep.views.RecentsView;
import com.android.systemui.plugins.LauncherOverlayPlugin;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.plugins.shared.LauncherExterns;
import com.android.systemui.plugins.shared.LauncherOverlayManager;
import com.szchoiceway.SysProviderOpt;
import com.szchoiceway.view.CustomerView;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Launcher extends StatefulActivity<LauncherState> implements LauncherExterns, BgDataModel.Callbacks, InvariantDeviceProfile.OnIDPChangeListener, PluginListener<LauncherOverlayPlugin>, LauncherOverlayManager.LauncherOverlayCallbacks, ViewTreeObserver.OnGlobalLayoutListener {
    public static final String ACTION_LAUNCHER_ALLAPPS_START_EVT = "ZXW_ACTION_LAUNCHER_ALLAPPS_START_EVT";
    public static final String ACTION_LAUNCHER_CHANGE_STATUS_BAR_COLOR = "ZXW_ACTION_LAUNCHER_CHANGE_STATUS_BAR_COLOR";
    public static final ActivityTracker<Launcher> ACTIVITY_TRACKER = new ActivityTracker<>();
    private static final float BOUNCE_ANIMATION_TENSION = 1.3f;
    static final boolean DEBUG_STRICT_MODE = false;
    public static final int DISPLAY_ALL_APPS_TRACE_COOKIE = 1;
    private static final String DISPLAY_ALL_APPS_TRACE_METHOD_NAME = "DisplayAllApps";
    public static final int DISPLAY_WORKSPACE_TRACE_COOKIE = 0;
    private static final String DISPLAY_WORKSPACE_TRACE_METHOD_NAME = "DisplayWorkspaceFirstFrame";
    private static final int EVT_START_ALL_APPS = 0;
    private static final int EVT_START_TASK = 1;
    private static final String LAUNCHER_IS_IN_MULTIWINDOWMODE = "com.szchoiceway.action.isInMultiWindowMode";
    private static final String LAUNCHER_ON_BACK_PRESSED = "com.szchoiceway.action.LAUNCHER_ON_BACK_PRESSED";
    private static final String LAUNCHER_STATUS_CHANGE = "com.szchoiceway.action.LAUNCHER_STATUS";
    private static final String LAUNCHER_STATUS_DESTROY = "com.szchoiceway.action.LAUNCHER_STATUS_DESTROY";
    static final boolean LOGD = false;
    public static final int NEW_APPS_ANIMATION_DELAY = 500;
    private static final int NEW_APPS_ANIMATION_INACTIVE_TIMEOUT_SECONDS = 5;
    public static final int NEW_APPS_PAGE_MOVE_DELAY = 500;
    private static final int ON_ACTIVITY_RESULT_ANIMATION_DELAY = 500;
    public static final String ON_CREATE_EVT = "Launcher.onCreate";
    public static final String ON_NEW_INTENT_EVT = "Launcher.onNewIntent";
    public static final String ON_RESUME_EVT = "Launcher.onResume";
    public static final String ON_START_EVT = "Launcher.onStart";
    private static final int REQUEST_BIND_APPWIDGET = 11;
    public static final int REQUEST_BIND_PENDING_APPWIDGET = 12;
    private static final int REQUEST_CREATE_APPWIDGET = 5;
    private static final int REQUEST_CREATE_SHORTCUT = 1;
    protected static final int REQUEST_LAST = 100;
    private static final int REQUEST_PERMISSION_CALL_PHONE = 14;
    private static final int REQUEST_PICK_APPWIDGET = 9;
    public static final int REQUEST_RECONFIGURE_APPWIDGET = 13;
    private static final String RUNTIME_STATE = "launcher.state";
    private static final String RUNTIME_STATE_CURRENT_SCREEN_IDS = "launcher.current_screen_ids";
    private static final String RUNTIME_STATE_PENDING_ACTIVITY_RESULT = "launcher.activity_result";
    private static final String RUNTIME_STATE_PENDING_REQUEST_ARGS = "launcher.request_args";
    private static final String RUNTIME_STATE_PENDING_REQUEST_CODE = "launcher.request_code";
    private static final String RUNTIME_STATE_WIDGET_PANEL = "launcher.widget_panel";
    public static final String TAG = "Launcher";
    private static final int THEME_CROSS_FADE_ANIMATION_DURATION = 375;
    private AccessibilityActionsView acccl;
    private LauncherAccessibilityDelegate mAccessibilityDelegate;
    AllAppsTransitionController mAllAppsController;
    protected InstanceId mAllAppsSessionLogId;
    private LauncherAppWidgetHost mAppWidgetHost;
    private WidgetManagerHelper mAppWidgetManager;
    ActivityAllAppsContainerView<Launcher> mAppsView;
    private boolean mDeferOverlayCallbacks;
    private final Runnable mDeferredOverlayCallbacks = new Runnable() {
        public final void run() {
            Launcher.this.checkIfOverlayStillDeferred();
        }
    };
    private DragController mDragController;
    DragLayer mDragLayer;
    private DropTargetBar mDropTargetBar;
    private ViewGroupFocusHelper mFocusHandler;
    Hotseat mHotseat;
    private IconCache mIconCache;
    protected long mLastTouchUpTime = -1;
    private LauncherCallbacks mLauncherCallbacks;
    private LauncherModel mModel;
    private ModelWriter mModelWriter;
    private Configuration mOldConfig;
    private int mOldTop = 0;
    private Runnable mOnDeferredActivityLaunchCallback;
    private ViewTreeObserver.OnPreDrawListener mOnInitialBindListener;
    private OnboardingPrefs<? extends Launcher> mOnboardingPrefs;
    protected LauncherOverlayManager mOverlayManager;
    private View mOverviewPanel;
    private IntSet mPagesToBindSynchronously = new IntSet();
    protected int mPendingActivityRequestCode = -1;
    private ActivityResultInfo mPendingActivityResult;
    private ViewOnDrawExecutor mPendingExecutor;
    private PendingRequestArgs mPendingRequestArgs;
    private PopupDataProvider mPopupDataProvider;
    /* access modifiers changed from: private */
    public LauncherState mPrevLauncherState;
    private RotationHelper mRotationHelper;
    private final BroadcastReceiver mScreenOffReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Launcher.this.onScreenOff();
        }
    };
    ScrimView mScrimView;
    private SharedPreferences mSharedPrefs;
    /* access modifiers changed from: private */
    public StateManager<LauncherState> mStateManager;
    private StringCache mStringCache;
    private IntSet mSynchronouslyBoundPages = new IntSet();
    private SysProviderOpt mSysProviderOpt;
    private final int[] mTmpAddItemCellCoordinates = new int[2];
    private boolean mTouchInProgress;
    private SafeCloseable mUserChangedCallbackCloseable;
    Workspace<?> mWorkspace;
    boolean mWorkspaceLoading = true;
    private int m_iModeSet = 16;
    private final BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("ZXW_ACTION_LONGCLICK_ENTER_SPLIT_SCREEN".equals(action)) {
                Log.d(Launcher.TAG, "onReceive ZXW_ACTION_LONGCLICK_ENTER_SPLIT_SCREEN");
                ((RecentsView) Launcher.this.getOverviewPanel()).enterSplitScreen();
            } else if (Launcher.ACTION_LAUNCHER_CHANGE_STATUS_BAR_COLOR.equals(action)) {
                int intExtra = intent.getIntExtra("blackFont", 0);
                Log.d(Launcher.TAG, "onReceive ACTION_LAUNCHER_CHANGE_STATUS_BAR_COLOR color = " + intExtra);
                if (intExtra == 0) {
                    Launcher.this.getSystemUiController().updateUiState(0, 10);
                } else {
                    Launcher.this.getSystemUiController().updateUiState(0, 5);
                }
            }
        }
    };
    private CustomerView zxw_launcher;

    public void onAllAppsTransition(float f) {
    }

    public void onDragLayerHierarchyChanged() {
    }

    public void onOverlayVisibilityChanged(boolean z) {
    }

    public void onPageEndTransition() {
    }

    public void onWidgetsTransition(float f) {
    }

    public boolean supportsAdaptiveIconAnimation(View view) {
        return false;
    }

    public void useFadeOutAnimationForLauncherStart(CancellationSignal cancellationSignal) {
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        int[] intArray;
        this.mSysProviderOpt = SysProviderOpt.getInstance(this);
        if (Utilities.ATLEAST_S) {
            Trace.beginAsyncSection(DISPLAY_WORKSPACE_TRACE_METHOD_NAME, 0);
            Trace.beginAsyncSection(DISPLAY_ALL_APPS_TRACE_METHOD_NAME, 1);
        }
        Object beginSection = TraceHelper.INSTANCE.beginSection(ON_CREATE_EVT, 5);
        if (Utilities.IS_DEBUG_DEVICE && FeatureFlags.NOTIFY_CRASHES.get()) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel("com.android.launcher3.Debug", "Debug", 4));
            Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(notificationManager) {
                public final /* synthetic */ NotificationManager f$1;

                {
                    this.f$1 = r2;
                }

                public final void uncaughtException(Thread thread, Throwable th) {
                    Launcher.this.lambda$onCreate$0$Launcher(this.f$1, thread, th);
                }
            });
        }
        super.onCreate(bundle);
        this.m_iModeSet = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KESAIWEI_SYS_MODE_SELECTION, 16);
        LauncherAppState instance = LauncherAppState.getInstance(this);
        this.mOldConfig = new Configuration(getResources().getConfiguration());
        this.mModel = instance.getModel();
        this.mRotationHelper = new RotationHelper(this);
        InvariantDeviceProfile invariantDeviceProfile = instance.getInvariantDeviceProfile();
        initDeviceProfile(invariantDeviceProfile);
        invariantDeviceProfile.addOnChangeListener(this);
        this.mSharedPrefs = Utilities.getPrefs(this);
        this.mIconCache = instance.getIconCache();
        this.mAccessibilityDelegate = createAccessibilityDelegate();
        this.mDragController = new LauncherDragController(this);
        this.mAllAppsController = new AllAppsTransitionController(this);
        this.mStateManager = new StateManager<>(this, LauncherState.NORMAL);
        this.mOnboardingPrefs = createOnboardingPrefs(this.mSharedPrefs);
        this.mAppWidgetManager = new WidgetManagerHelper(this);
        LauncherAppWidgetHost createAppWidgetHost = createAppWidgetHost();
        this.mAppWidgetHost = createAppWidgetHost;
        createAppWidgetHost.startListening();
        inflateRootView(R.layout.launcher_zxw);
        setupViews();
        crossFadeWithPreviousAppearance();
        this.mPopupDataProvider = new PopupDataProvider(new Consumer() {
            public final void accept(Object obj) {
                Launcher.this.updateNotificationDots((Predicate) obj);
            }
        });
        boolean handleCreate = ACTIVITY_TRACKER.handleCreate(this);
        if (handleCreate && bundle != null) {
            bundle.remove(RUNTIME_STATE);
        }
        restoreState(bundle);
        this.mStateManager.reapplyState();
        if (!(bundle == null || (intArray = bundle.getIntArray(RUNTIME_STATE_CURRENT_SCREEN_IDS)) == null)) {
            this.mPagesToBindSynchronously = IntSet.wrap(intArray);
        }
        if (!this.mModel.addCallbacksAndLoad(this) && !handleCreate) {
            Log.d(TestProtocol.BAD_STATE, "Launcher onCreate not binding sync, prevent drawing");
            Boolean bool = Boolean.FALSE;
            Objects.requireNonNull(bool);
            this.mOnInitialBindListener = new ViewTreeObserver.OnPreDrawListener(bool) {
                public final /* synthetic */ Boolean f$0;

                {
                    this.f$0 = r1;
                }

                public final boolean onPreDraw() {
                    return this.f$0.booleanValue();
                }
            };
        }
        setDefaultKeyMode(3);
        setContentView(getRootView());
        if (this.mOnInitialBindListener != null) {
            getRootView().getViewTreeObserver().addOnPreDrawListener(this.mOnInitialBindListener);
        }
        getRootView().dispatchInsets();
        registerReceiver(this.mScreenOffReceiver, new IntentFilter("android.intent.action.SCREEN_OFF"));
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("ZXW_ACTION_LONGCLICK_ENTER_SPLIT_SCREEN");
        intentFilter.addAction(ACTION_LAUNCHER_CHANGE_STATUS_BAR_COLOR);
        registerReceiver(this.myBroadcastReceiver, intentFilter);
        getSystemUiController().updateUiState(0, Themes.getAttrBoolean(this, R.attr.isWorkspaceDarkText));
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            launcherCallbacks.onCreate(bundle);
        }
        this.mOverlayManager = getDefaultOverlay();
        PluginManagerWrapper.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).addPluginListener(this, LauncherOverlayPlugin.class, false);
        this.mRotationHelper.initialize();
        TraceHelper.INSTANCE.endSection(beginSection);
        this.mUserChangedCallbackCloseable = UserCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).addUserChangeListener(new Runnable() {
            public final void run() {
                Launcher.this.lambda$onCreate$1$Launcher();
            }
        });
        if (Utilities.ATLEAST_R) {
            getWindow().setSoftInputMode(48);
        }
        setTitle(R.string.home_screen);
        Log.i(TAG, "zxw app starting...");
        WallpaperManager instance2 = WallpaperManager.getInstance(this);
        if (instance2 != null) {
            try {
                Log.d(TAG, "set wallpaper");
                instance2.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ksw_background_1920x720));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public /* synthetic */ void lambda$onCreate$0$Launcher(NotificationManager notificationManager, Thread thread, Throwable th) {
        String stackTraceString = Log.getStackTraceString(th);
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.TEXT", stackTraceString);
        notificationManager.notify("Debug", 0, new Notification.Builder(this, "com.android.launcher3.Debug").setSmallIcon(17301560).setContentTitle("Launcher crash detected!").setStyle(new Notification.BigTextStyle().bigText(stackTraceString)).addAction(17301586, "Share", PendingIntent.getActivity(this, 0, Intent.createChooser(intent, (CharSequence) null), 201326592)).build());
        Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (defaultUncaughtExceptionHandler != null) {
            defaultUncaughtExceptionHandler.uncaughtException(thread, th);
        }
    }

    public /* synthetic */ void lambda$onCreate$1$Launcher() {
        getStateManager().goToState(LauncherState.NORMAL);
    }

    /* access modifiers changed from: protected */
    public LauncherOverlayManager getDefaultOverlay() {
        return new LauncherOverlayManager() {
        };
    }

    /* access modifiers changed from: protected */
    public OnboardingPrefs<? extends Launcher> createOnboardingPrefs(SharedPreferences sharedPreferences) {
        return new OnboardingPrefs<>(this, sharedPreferences);
    }

    public OnboardingPrefs<? extends Launcher> getOnboardingPrefs() {
        return this.mOnboardingPrefs;
    }

    public /* synthetic */ LauncherOverlayManager lambda$onPluginConnected$2$Launcher(LauncherOverlayPlugin launcherOverlayPlugin) {
        return launcherOverlayPlugin.createOverlayManager(this, this);
    }

    public void onPluginConnected(LauncherOverlayPlugin launcherOverlayPlugin, Context context) {
        switchOverlay(new Supplier(launcherOverlayPlugin) {
            public final /* synthetic */ LauncherOverlayPlugin f$1;

            {
                this.f$1 = r2;
            }

            public final Object get() {
                return Launcher.this.lambda$onPluginConnected$2$Launcher(this.f$1);
            }
        });
    }

    public void onPluginDisconnected(LauncherOverlayPlugin launcherOverlayPlugin) {
        switchOverlay(new Supplier() {
            public final Object get() {
                return Launcher.this.getDefaultOverlay();
            }
        });
    }

    private void switchOverlay(Supplier<LauncherOverlayManager> supplier) {
        LauncherOverlayManager launcherOverlayManager = this.mOverlayManager;
        if (launcherOverlayManager != null) {
            launcherOverlayManager.onActivityDestroyed(this);
        }
        this.mOverlayManager = supplier.get();
        if (getRootView().isAttachedToWindow()) {
            this.mOverlayManager.onAttachedToWindow();
        }
        this.mDeferOverlayCallbacks = true;
        checkIfOverlayStillDeferred();
    }

    public void dispatchDeviceProfileChanged() {
        super.dispatchDeviceProfileChanged();
        this.mOverlayManager.onDeviceProvideChanged();
    }

    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        this.mRotationHelper.setCurrentTransitionRequest(0);
        if (!Utilities.ATLEAST_S) {
            AbstractFloatingView.closeOpenViews(this, false, 8192);
        }
    }

    public void onMultiWindowModeChanged(boolean z, Configuration configuration) {
        super.onMultiWindowModeChanged(z, configuration);
        initDeviceProfile(this.mDeviceProfile.inv);
        dispatchDeviceProfileChanged();
        Intent intent = new Intent(LAUNCHER_IS_IN_MULTIWINDOWMODE);
        intent.putExtra("multi", z);
        sendBroadcast(intent);
    }

    public void onGlobalLayout() {
        AccessibilityActionsView accessibilityActionsView = this.acccl;
        if (accessibilityActionsView != null && this.mOldTop != accessibilityActionsView.getTop()) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.zxw_launcher.getLayoutParams();
            Log.i(TAG, "onChange: " + this.acccl.getTop());
            Log.i(TAG, "onChange: -------------------------------");
            this.mOldTop = this.acccl.getTop();
            layoutParams.topMargin = -this.acccl.getTop();
            Log.i(TAG, "onMultiWindowModeChanged:      |   " + layoutParams.topMargin);
            this.zxw_launcher.setLayoutParams(layoutParams);
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        if ((configuration.diff(this.mOldConfig) & 1152) != 0) {
            onIdpChanged(false);
        }
        this.mOldConfig.setTo(configuration);
        super.onConfigurationChanged(configuration);
        Log.d(TAG, "onConfigurationChanged");
    }

    public void onIdpChanged(boolean z) {
        initDeviceProfile(this.mDeviceProfile.inv);
        dispatchDeviceProfileChanged();
        reapplyUi();
        this.mDragLayer.recreateControllers();
        onSaveInstanceState(new Bundle());
        this.mModel.rebindCallbacks();
    }

    public void onAssistantVisibilityChanged(float f) {
        this.mHotseat.getQsb().setAlpha(1.0f - f);
    }

    public void onOneHandedStateChanged(boolean z) {
        this.mDragLayer.onOneHandedModeStateChanged(z);
    }

    /* access modifiers changed from: protected */
    public void initDeviceProfile(InvariantDeviceProfile invariantDeviceProfile) {
        this.mDeviceProfile = invariantDeviceProfile.getDeviceProfile(this);
        if (isInMultiWindowMode()) {
            this.mDeviceProfile = this.mDeviceProfile.getMultiWindowProfile(this, getMultiWindowDisplaySize());
        }
        onDeviceProfileInitiated();
        this.mModelWriter = this.mModel.getWriter(getDeviceProfile().isVerticalBarLayout(), true, this);
    }

    public RotationHelper getRotationHelper() {
        return this.mRotationHelper;
    }

    public ViewGroupFocusHelper getFocusHandler() {
        return this.mFocusHandler;
    }

    public StateManager<LauncherState> getStateManager() {
        return this.mStateManager;
    }

    public void setLauncherOverlay(LauncherOverlayManager.LauncherOverlay launcherOverlay) {
        if (launcherOverlay != null) {
            launcherOverlay.setOverlayCallbacks(this);
        }
        this.mWorkspace.setLauncherOverlay(launcherOverlay);
    }

    public void runOnOverlayHidden(Runnable runnable) {
        getWorkspace().runOnOverlayHidden(runnable);
    }

    public boolean setLauncherCallbacks(LauncherCallbacks launcherCallbacks) {
        this.mLauncherCallbacks = launcherCallbacks;
        return true;
    }

    public boolean isDraggingEnabled() {
        return !isWorkspaceLoading();
    }

    public PopupDataProvider getPopupDataProvider() {
        return this.mPopupDataProvider;
    }

    public DotInfo getDotInfoForItem(ItemInfo itemInfo) {
        return this.mPopupDataProvider.getDotInfoForItem(itemInfo);
    }

    public void invalidateParent(ItemInfo itemInfo) {
        if (itemInfo.container >= 0) {
            View homescreenIconByItemId = getWorkspace().getHomescreenIconByItemId(itemInfo.container);
            if ((homescreenIconByItemId instanceof FolderIcon) && (homescreenIconByItemId.getTag() instanceof FolderInfo) && new FolderGridOrganizer(getDeviceProfile().inv).setFolderInfo((FolderInfo) homescreenIconByItemId.getTag()).isItemInPreview(itemInfo.rank)) {
                homescreenIconByItemId.invalidate();
            }
        }
    }

    private int completeAdd(int i, Intent intent, int i2, PendingRequestArgs pendingRequestArgs) {
        LauncherAppWidgetProviderInfo launcherAppWidgetInfo;
        int i3 = pendingRequestArgs.screenId;
        if (pendingRequestArgs.container == -100) {
            i3 = ensurePendingDropLayoutExists(pendingRequestArgs.screenId);
        }
        if (i == 1) {
            completeAddShortcut(intent, pendingRequestArgs.container, i3, pendingRequestArgs.cellX, pendingRequestArgs.cellY, pendingRequestArgs);
            announceForAccessibility(R.string.item_added_to_workspace);
        } else if (i == 5) {
            completeAddAppWidget(i2, pendingRequestArgs, (AppWidgetHostView) null, (LauncherAppWidgetProviderInfo) null);
        } else if (i == 12) {
            LauncherAppWidgetInfo completeRestoreAppWidget = completeRestoreAppWidget(i2, 4);
            if (!(completeRestoreAppWidget == null || (launcherAppWidgetInfo = this.mAppWidgetManager.getLauncherAppWidgetInfo(i2)) == null)) {
                new WidgetAddFlowHandler((AppWidgetProviderInfo) launcherAppWidgetInfo).startConfigActivity(this, completeRestoreAppWidget, 13);
            }
        } else if (i == 13) {
            this.mStatsLogManager.logger().withItemInfo(pendingRequestArgs).log(StatsLogManager.LauncherEvent.LAUNCHER_WIDGET_RECONFIGURED);
            completeRestoreAppWidget(i2, 0);
        }
        return i3;
    }

    private void handleActivityResult(int i, int i2, Intent intent) {
        if (isWorkspaceLoading()) {
            this.mPendingActivityResult = new ActivityResultInfo(i, i2, intent);
            return;
        }
        this.mPendingActivityResult = null;
        final PendingRequestArgs pendingRequestArgs = this.mPendingRequestArgs;
        setWaitingForResult((PendingRequestArgs) null);
        if (pendingRequestArgs != null) {
            int widgetId = pendingRequestArgs.getWidgetId();
            AnonymousClass2 r1 = new Runnable() {
                public void run() {
                    Launcher.this.mStateManager.goToState(LauncherState.NORMAL, 500);
                }
            };
            final int i3 = -1;
            if (i == 11) {
                int intExtra = intent != null ? intent.getIntExtra(LauncherSettings.Favorites.APPWIDGET_ID, -1) : -1;
                if (i2 == 0) {
                    completeTwoStageWidgetDrop(0, intExtra, pendingRequestArgs);
                    this.mWorkspace.removeExtraEmptyScreenDelayed(500, false, r1);
                } else if (i2 == -1) {
                    addAppWidgetImpl(intExtra, pendingRequestArgs, (AppWidgetHostView) null, pendingRequestArgs.getWidgetHandler(), 500);
                }
            } else {
                if (i == 9 || i == 5) {
                    if (intent != null) {
                        i3 = intent.getIntExtra(LauncherSettings.Favorites.APPWIDGET_ID, -1);
                    }
                    if (i3 < 0) {
                        i3 = widgetId;
                    }
                    if (i3 < 0 || i2 == 0) {
                        Log.e(TAG, "Error: appWidgetId (EXTRA_APPWIDGET_ID) was not returned from the widget configuration activity.");
                        completeTwoStageWidgetDrop(0, i3, pendingRequestArgs);
                        this.mWorkspace.removeExtraEmptyScreenDelayed(500, false, new Runnable() {
                            public final void run() {
                                Launcher.this.lambda$handleActivityResult$3$Launcher();
                            }
                        });
                        return;
                    }
                    if (pendingRequestArgs.container == -100) {
                        pendingRequestArgs.screenId = ensurePendingDropLayoutExists(pendingRequestArgs.screenId);
                    }
                    CellLayout screenWithId = this.mWorkspace.getScreenWithId(pendingRequestArgs.screenId);
                    screenWithId.setDropPending(true);
                    final int i4 = i2;
                    final CellLayout cellLayout = screenWithId;
                    this.mWorkspace.removeExtraEmptyScreenDelayed(500, false, new Runnable() {
                        public void run() {
                            Launcher.this.completeTwoStageWidgetDrop(i4, i3, pendingRequestArgs);
                            cellLayout.setDropPending(false);
                        }
                    });
                } else if (i != 13 && i != 12) {
                    if (i == 1) {
                        if (i2 == -1 && pendingRequestArgs.container != -1) {
                            completeAdd(i, intent, -1, pendingRequestArgs);
                            this.mWorkspace.removeExtraEmptyScreenDelayed(500, false, r1);
                        } else if (i2 == 0) {
                            this.mWorkspace.removeExtraEmptyScreenDelayed(500, false, r1);
                        }
                    }
                    this.mDragLayer.clearAnimatedView();
                } else if (i2 == -1) {
                    completeAdd(i, intent, widgetId, pendingRequestArgs);
                }
            }
        }
    }

    public /* synthetic */ void lambda$handleActivityResult$3$Launcher() {
        getStateManager().goToState(LauncherState.NORMAL);
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        this.mPendingActivityRequestCode = -1;
        handleActivityResult(i, i2, intent);
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        PendingRequestArgs pendingRequestArgs = this.mPendingRequestArgs;
        if (i == 14 && pendingRequestArgs != null && pendingRequestArgs.getRequestCode() == 14) {
            setWaitingForResult((PendingRequestArgs) null);
            CellLayout cellLayout = getCellLayout(pendingRequestArgs.container, pendingRequestArgs.screenId);
            View childAt = cellLayout != null ? cellLayout.getChildAt(pendingRequestArgs.cellX, pendingRequestArgs.cellY) : null;
            Intent pendingIntent = pendingRequestArgs.getPendingIntent();
            if (iArr.length <= 0 || iArr[0] != 0) {
                Toast.makeText(this, getString(R.string.msg_no_phone_permission, new Object[]{getString(R.string.derived_app_name)}), 0).show();
            } else {
                lambda$startActivitySafely$7$Launcher(childAt, pendingIntent, (ItemInfo) null);
            }
        }
    }

    private int ensurePendingDropLayoutExists(int i) {
        if (this.mWorkspace.getScreenWithId(i) != null) {
            return i;
        }
        this.mWorkspace.addExtraEmptyScreens();
        IntSet commitExtraEmptyScreens = this.mWorkspace.commitExtraEmptyScreens();
        if (commitExtraEmptyScreens.isEmpty()) {
            return -1;
        }
        return commitExtraEmptyScreens.getArray().get(0);
    }

    /* access modifiers changed from: package-private */
    public void completeTwoStageWidgetDrop(int i, final int i2, final PendingRequestArgs pendingRequestArgs) {
        AppWidgetHostView appWidgetHostView;
        int i3;
        AnonymousClass4 r6;
        int i4;
        CellLayout screenWithId = this.mWorkspace.getScreenWithId(pendingRequestArgs.screenId);
        if (i == -1) {
            final AppWidgetHostView createView = this.mAppWidgetHost.createView(this, i2, pendingRequestArgs.getWidgetHandler().getProviderInfo(this));
            i3 = 3;
            appWidgetHostView = createView;
            r6 = new Runnable() {
                public void run() {
                    Launcher.this.completeAddAppWidget(i2, pendingRequestArgs, createView, (LauncherAppWidgetProviderInfo) null);
                    Launcher.this.mStateManager.goToState(LauncherState.NORMAL, 500);
                }
            };
        } else {
            if (i == 0) {
                this.mAppWidgetHost.deleteAppWidgetId(i2);
                i4 = 4;
            } else {
                i4 = 0;
            }
            i3 = i4;
            r6 = null;
            appWidgetHostView = null;
        }
        if (this.mDragLayer.getAnimatedView() != null) {
            this.mWorkspace.animateWidgetDrop(pendingRequestArgs, screenWithId, (DragView) this.mDragLayer.getAnimatedView(), r6, i3, appWidgetHostView, true);
        } else if (r6 != null) {
            r6.run();
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        if (this.mDeferOverlayCallbacks) {
            checkIfOverlayStillDeferred();
        } else {
            this.mOverlayManager.onActivityStopped(this);
        }
        hideKeyboard();
        logStopAndResume(false);
        this.mAppWidgetHost.setActivityStarted(false);
        NotificationListener.removeNotificationsChangedListener(getPopupDataProvider());
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        Object beginSection = TraceHelper.INSTANCE.beginSection(ON_START_EVT, 5);
        super.onStart();
        if (!this.mDeferOverlayCallbacks) {
            this.mOverlayManager.onActivityStarted(this);
        }
        this.mAppWidgetHost.setActivityStarted(true);
        TraceHelper.INSTANCE.endSection(beginSection);
    }

    /* access modifiers changed from: protected */
    public void onDeferredResumed() {
        logStopAndResume(true);
        ItemInstallQueue.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).resumeModelPush(1);
        this.mModel.validateModelDataOnResume();
        NotificationListener.addNotificationsChangedListener(this.mPopupDataProvider);
        DiscoveryBounce.showForHomeIfNeeded(this);
        this.mAppWidgetHost.setActivityResumed(true);
    }

    private void logStopAndResume(boolean z) {
        StatsLogManager.LauncherEvent launcherEvent;
        if (this.mPendingExecutor == null) {
            int currentPage = this.mWorkspace.isOverlayShown() ? -1 : this.mWorkspace.getCurrentPage();
            int i = this.mStateManager.getState().statsLogOrdinal;
            StatsLogManager.StatsLogger logger = getStatsLogManager().logger();
            if (z) {
                logger.withSrcState(1).withDstState(this.mStateManager.getState().statsLogOrdinal);
                launcherEvent = StatsLogManager.LauncherEvent.LAUNCHER_ONRESUME;
            } else {
                logger.withSrcState(this.mStateManager.getState().statsLogOrdinal).withDstState(1);
                launcherEvent = StatsLogManager.LauncherEvent.LAUNCHER_ONSTOP;
            }
            if (i == 2 && this.mWorkspace != null) {
                logger.withContainerInfo((LauncherAtom.ContainerInfo) LauncherAtom.ContainerInfo.newBuilder().setWorkspace(LauncherAtom.WorkspaceContainer.newBuilder().setPageIndex(currentPage)).build());
            }
            logger.log(launcherEvent);
        }
    }

    private void scheduleDeferredCheck() {
        this.mHandler.removeCallbacks(this.mDeferredOverlayCallbacks);
        Utilities.postAsyncCallback(this.mHandler, this.mDeferredOverlayCallbacks);
    }

    /* access modifiers changed from: private */
    public void checkIfOverlayStillDeferred() {
        if (this.mDeferOverlayCallbacks) {
            if (!isStarted() || (hasBeenResumed() && !this.mStateManager.getState().hasFlag(1))) {
                this.mDeferOverlayCallbacks = false;
                if (isStarted()) {
                    this.mOverlayManager.onActivityStarted(this);
                }
                if (hasBeenResumed()) {
                    this.mOverlayManager.onActivityResumed(this);
                } else {
                    this.mOverlayManager.onActivityPaused(this);
                }
                if (!isStarted()) {
                    this.mOverlayManager.onActivityStopped(this);
                }
            }
        }
    }

    public void deferOverlayCallbacksUntilNextResumeOrStop() {
        this.mDeferOverlayCallbacks = true;
    }

    public LauncherOverlayManager getOverlayManager() {
        return this.mOverlayManager;
    }

    public void onStateSetStart(LauncherState launcherState) {
        super.onStateSetStart(launcherState);
        if (this.mDeferOverlayCallbacks) {
            scheduleDeferredCheck();
        }
        addActivityFlags(64);
        if (launcherState.hasFlag(LauncherState.FLAG_CLOSE_POPUPS)) {
            AbstractFloatingView.closeAllOpenViews(this, !launcherState.hasFlag(1));
        }
        if (launcherState == LauncherState.SPRING_LOADED) {
            ItemInstallQueue.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).pauseModelPush(4);
            getRotationHelper().setCurrentStateRequest(2);
            this.mWorkspace.showPageIndicatorAtCurrentScroll();
            this.mWorkspace.setClipChildren(false);
        }
        ((PageIndicator) this.mWorkspace.getPageIndicator()).setShouldAutoHide(true ^ launcherState.hasFlag(LauncherState.FLAG_MULTI_PAGE));
        LauncherState currentStableState = this.mStateManager.getCurrentStableState();
        this.mPrevLauncherState = currentStableState;
        if (currentStableState != launcherState && LauncherState.ALL_APPS.equals(launcherState) && this.mAllAppsSessionLogId == null) {
            this.mAllAppsSessionLogId = new InstanceIdSequence().newInstanceId();
            if (getAllAppsEntryEvent().isPresent()) {
                getStatsLogManager().logger().withContainerInfo((LauncherAtom.ContainerInfo) LauncherAtom.ContainerInfo.newBuilder().setWorkspace(LauncherAtom.WorkspaceContainer.newBuilder().setPageIndex(getWorkspace().getCurrentPage())).build()).log(getAllAppsEntryEvent().get());
            }
        }
    }

    /* access modifiers changed from: protected */
    public Optional<StatsLogManager.EventEnum> getAllAppsEntryEvent() {
        StatsLogManager.LauncherEvent launcherEvent;
        if (FeatureFlags.ENABLE_DEVICE_SEARCH.get()) {
            launcherEvent = StatsLogManager.LauncherEvent.LAUNCHER_ALLAPPS_ENTRY_WITH_DEVICE_SEARCH;
        } else {
            launcherEvent = StatsLogManager.LauncherEvent.LAUNCHER_ALLAPPS_ENTRY;
        }
        return Optional.of(launcherEvent);
    }

    public void onStateSetEnd(LauncherState launcherState) {
        super.onStateSetEnd(launcherState);
        getAppWidgetHost().setStateIsNormal(launcherState == LauncherState.NORMAL);
        getWorkspace().setClipChildren(!launcherState.hasFlag(LauncherState.FLAG_MULTI_PAGE));
        finishAutoCancelActionMode();
        removeActivityFlags(64);
        getWindow().getDecorView().sendAccessibilityEvent(32);
        AccessibilityManagerCompat.sendStateEventToTest(this, launcherState.ordinal);
        if (launcherState == LauncherState.NORMAL) {
            ItemInstallQueue.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).resumeModelPush(4);
            getRotationHelper().setCurrentStateRequest(0);
        }
        if (LauncherState.ALL_APPS.equals(this.mPrevLauncherState) && !LauncherState.ALL_APPS.equals(launcherState) && this.mAllAppsSessionLogId != null) {
            getAppsView().reset(false);
            Optional<StatsLogManager.EventEnum> allAppsExitEvent = getAllAppsExitEvent();
            StatsLogManager.StatsLogger logger = getStatsLogManager().logger();
            Objects.requireNonNull(logger);
            allAppsExitEvent.ifPresent(new Consumer() {
                public final void accept(Object obj) {
                    StatsLogManager.StatsLogger.this.log((StatsLogManager.EventEnum) obj);
                }
            });
            this.mAllAppsSessionLogId = null;
        }
    }

    /* access modifiers changed from: protected */
    public Optional<StatsLogManager.EventEnum> getAllAppsExitEvent() {
        return Optional.of(StatsLogManager.LauncherEvent.LAUNCHER_ALLAPPS_EXIT);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        Object beginSection = TraceHelper.INSTANCE.beginSection(ON_RESUME_EVT, 5);
        super.onResume();
        if (this.mDeferOverlayCallbacks) {
            scheduleDeferredCheck();
        } else {
            this.mOverlayManager.onActivityResumed(this);
        }
        AbstractFloatingView.closeAllOpenViewsExcept(this, false, AbstractFloatingView.TYPE_REBIND_SAFE);
        DragView.removeAllViews(this);
        TraceHelper.INSTANCE.endSection(beginSection);
        Intent intent = new Intent(LAUNCHER_STATUS_CHANGE);
        intent.putExtra(TestProtocol.STATE_FIELD, 1);
        sendBroadcast(intent);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        ItemInstallQueue.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).pauseModelPush(1);
        super.onPause();
        this.mDragController.cancelDrag();
        this.mLastTouchUpTime = -1;
        this.mDropTargetBar.animateToVisibility(false);
        if (!this.mDeferOverlayCallbacks) {
            this.mOverlayManager.onActivityPaused(this);
        }
        this.mAppWidgetHost.setActivityResumed(false);
        Intent intent = new Intent(LAUNCHER_STATUS_CHANGE);
        intent.putExtra(TestProtocol.STATE_FIELD, 0);
        sendBroadcast(intent);
    }

    public void onScrollChanged(float f) {
        Workspace<?> workspace = this.mWorkspace;
        if (workspace != null) {
            workspace.onOverlayScrollChanged(f);
        }
    }

    private void restoreState(Bundle bundle) {
        if (bundle != null) {
            LauncherState launcherState = LauncherState.values()[bundle.getInt(RUNTIME_STATE, LauncherState.NORMAL.ordinal)];
            NonConfigInstance nonConfigInstance = (NonConfigInstance) getLastNonConfigurationInstance();
            if (((nonConfigInstance == null || (nonConfigInstance.config.diff(this.mOldConfig) & 512) == 0) ? false : true) || !launcherState.shouldDisableRestore()) {
                this.mStateManager.goToState(launcherState, false);
            }
            PendingRequestArgs pendingRequestArgs = (PendingRequestArgs) bundle.getParcelable(RUNTIME_STATE_PENDING_REQUEST_ARGS);
            if (pendingRequestArgs != null) {
                setWaitingForResult(pendingRequestArgs);
            }
            this.mPendingActivityRequestCode = bundle.getInt(RUNTIME_STATE_PENDING_REQUEST_CODE);
            this.mPendingActivityResult = (ActivityResultInfo) bundle.getParcelable(RUNTIME_STATE_PENDING_ACTIVITY_RESULT);
            SparseArray sparseParcelableArray = bundle.getSparseParcelableArray(RUNTIME_STATE_WIDGET_PANEL);
            if (sparseParcelableArray != null) {
                WidgetsFullSheet.show(this, false).restoreHierarchyState(sparseParcelableArray);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void setupViews() {
        DragLayer dragLayer = (DragLayer) findViewById(R.id.drag_layer);
        this.mDragLayer = dragLayer;
        this.mFocusHandler = dragLayer.getFocusIndicatorHelper();
        Workspace<?> workspace = (Workspace) this.mDragLayer.findViewById(R.id.workspace);
        this.mWorkspace = workspace;
        workspace.initParentViews(this.mDragLayer);
        this.mOverviewPanel = findViewById(R.id.overview_panel);
        Hotseat hotseat = (Hotseat) findViewById(R.id.hotseat);
        this.mHotseat = hotseat;
        hotseat.setWorkspace(this.mWorkspace);
        this.mDragLayer.setup(this.mDragController, this.mWorkspace);
        this.mWorkspace.setup(this.mDragController);
        this.mWorkspace.lockWallpaperToDefaultPage();
        this.mWorkspace.bindAndInitFirstWorkspaceScreen();
        this.mDragController.addDragListener(this.mWorkspace);
        this.mDropTargetBar = (DropTargetBar) this.mDragLayer.findViewById(R.id.drop_target_bar);
        this.mAppsView = (ActivityAllAppsContainerView) findViewById(R.id.apps_view);
        this.mScrimView = (ScrimView) findViewById(R.id.scrim_view);
        this.mDropTargetBar.setup(this.mDragController);
        this.mAllAppsController.setupViews(this.mScrimView, this.mAppsView);
        this.zxw_launcher = (CustomerView) findViewById(R.id.zxw_launcher);
        this.acccl = (AccessibilityActionsView) findViewById(R.id.accc);
        this.mDragLayer.setmIsBMWID8(true);
        this.mDragLayer.getViewTreeObserver().addOnGlobalLayoutListener(this);
        View findViewById = findViewById(R.id.cover);
        if (findViewById != null) {
            findViewById.setVisibility(0);
        }
        this.mScrimView.setCheckInRecentLisener(new ScrimView.CheckInRecentLisener(findViewById) {
            public final /* synthetic */ View f$1;

            {
                this.f$1 = r2;
            }

            public final void inRecent(boolean z) {
                Launcher.this.lambda$setupViews$4$Launcher(this.f$1, z);
            }
        });
    }

    public /* synthetic */ void lambda$setupViews$4$Launcher(View view, boolean z) {
        Intent intent = new Intent("com.android.launcher3.IN_RECENT");
        intent.putExtra(NotificationCompat.CATEGORY_STATUS, z ? 1 : 0);
        sendBroadcast(intent);
        if (z) {
            getWindow().clearFlags(512);
        } else {
            getWindow().addFlags(512);
        }
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                }
            });
            view.setVisibility(z ? 0 : 8);
        }
    }

    /* access modifiers changed from: package-private */
    public View createShortcut(WorkspaceItemInfo workspaceItemInfo) {
        return createShortcut((ViewGroup) this.mWorkspace.getChildAt(0), workspaceItemInfo);
    }

    public View createShortcut(ViewGroup viewGroup, WorkspaceItemInfo workspaceItemInfo) {
        BubbleTextView bubbleTextView = (BubbleTextView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.app_icon, viewGroup, false);
        bubbleTextView.applyFromWorkspaceItem(workspaceItemInfo);
        bubbleTextView.setOnClickListener(ItemClickHandler.INSTANCE);
        bubbleTextView.setOnFocusChangeListener(this.mFocusHandler);
        return bubbleTextView;
    }

    /* access modifiers changed from: protected */
    public void completeAddShortcut(Intent intent, int i, int i2, int i3, int i4, PendingRequestArgs pendingRequestArgs) {
        View view;
        WorkspaceItemInfo workspaceItemInfo;
        int[] iArr;
        char c;
        CellLayout cellLayout;
        boolean z;
        InstanceId instanceId;
        int i5 = i;
        PendingRequestArgs pendingRequestArgs2 = pendingRequestArgs;
        if (pendingRequestArgs.getRequestCode() == 1 && pendingRequestArgs.getPendingIntent().getComponent() != null) {
            int[] iArr2 = this.mTmpAddItemCellCoordinates;
            CellLayout cellLayout2 = getCellLayout(i5, i2);
            WorkspaceItemInfo createWorkspaceItemFromPinItemRequest = PinRequestHelper.createWorkspaceItemFromPinItemRequest(this, PinRequestHelper.getPinItemRequest(intent), 0);
            if (createWorkspaceItemFromPinItemRequest == null) {
                createWorkspaceItemFromPinItemRequest = Process.myUserHandle().equals(pendingRequestArgs2.user) ? ModelUtils.fromLegacyShortcutIntent(this, intent) : null;
                if (createWorkspaceItemFromPinItemRequest == null) {
                    Log.e(TAG, "Unable to parse a valid custom shortcut result");
                    return;
                } else if (!new PackageManagerHelper(this).hasPermissionForActivity(createWorkspaceItemFromPinItemRequest.intent, pendingRequestArgs.getPendingIntent().getComponent().getPackageName())) {
                    Log.e(TAG, "Ignoring malicious intent " + createWorkspaceItemFromPinItemRequest.intent.toUri(0));
                    return;
                }
            }
            WorkspaceItemInfo workspaceItemInfo2 = createWorkspaceItemFromPinItemRequest;
            if (i5 < 0) {
                View createShortcut = createShortcut(workspaceItemInfo2);
                if (i3 < 0 || i4 < 0) {
                    view = createShortcut;
                    workspaceItemInfo = workspaceItemInfo2;
                    c = 0;
                    instanceId = null;
                    cellLayout = cellLayout2;
                    iArr = iArr2;
                    z = cellLayout.findCellForSpan(iArr, 1, 1);
                } else {
                    iArr2[0] = i3;
                    iArr2[1] = i4;
                    DropTarget.DragObject dragObject = new DropTarget.DragObject(getApplicationContext());
                    dragObject.dragInfo = workspaceItemInfo2;
                    DropTarget.DragObject dragObject2 = dragObject;
                    view = createShortcut;
                    workspaceItemInfo = workspaceItemInfo2;
                    if (!this.mWorkspace.createUserFolderIfNecessary(createShortcut, i, cellLayout2, iArr2, 0.0f, true, dragObject2)) {
                        c = 0;
                        instanceId = null;
                        cellLayout = cellLayout2;
                        iArr = iArr2;
                        if (!this.mWorkspace.addToExistingFolderIfNecessary(view, cellLayout2, iArr2, 0.0f, dragObject2, true)) {
                            z = true;
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                }
                if (!z) {
                    this.mWorkspace.onNoCellFound(cellLayout, workspaceItemInfo, instanceId);
                    return;
                }
                WorkspaceItemInfo workspaceItemInfo3 = workspaceItemInfo;
                getModelWriter().addItemToDatabase(workspaceItemInfo3, i, i2, iArr[c], iArr[1]);
                this.mWorkspace.addInScreen(view, workspaceItemInfo3);
                return;
            }
            WorkspaceItemInfo workspaceItemInfo4 = workspaceItemInfo2;
            FolderIcon findFolderIcon = findFolderIcon(i5);
            if (findFolderIcon != null) {
                ((FolderInfo) findFolderIcon.getTag()).add(workspaceItemInfo4, pendingRequestArgs2.rank, false);
            } else {
                Log.e(TAG, "Could not find folder with id " + i5 + " to add shortcut.");
            }
        }
    }

    public FolderIcon findFolderIcon(int i) {
        return (FolderIcon) this.mWorkspace.getHomescreenIconByItemId(i);
    }

    /* access modifiers changed from: package-private */
    public void completeAddAppWidget(int i, ItemInfo itemInfo, AppWidgetHostView appWidgetHostView, LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo) {
        if (launcherAppWidgetProviderInfo == null) {
            launcherAppWidgetProviderInfo = this.mAppWidgetManager.getLauncherAppWidgetInfo(i);
        }
        if (appWidgetHostView == null) {
            appWidgetHostView = this.mAppWidgetHost.createView(this, i, launcherAppWidgetProviderInfo);
        }
        LauncherAppWidgetInfo launcherAppWidgetInfo = new LauncherAppWidgetInfo(i, launcherAppWidgetProviderInfo.provider, launcherAppWidgetProviderInfo, appWidgetHostView);
        launcherAppWidgetInfo.spanX = itemInfo.spanX;
        launcherAppWidgetInfo.spanY = itemInfo.spanY;
        launcherAppWidgetInfo.minSpanX = itemInfo.minSpanX;
        launcherAppWidgetInfo.minSpanY = itemInfo.minSpanY;
        launcherAppWidgetInfo.user = launcherAppWidgetProviderInfo.getProfile();
        if (itemInfo instanceof PendingAddWidgetInfo) {
            launcherAppWidgetInfo.sourceContainer = ((PendingAddWidgetInfo) itemInfo).sourceContainer;
        } else if (itemInfo instanceof PendingRequestArgs) {
            launcherAppWidgetInfo.sourceContainer = ((PendingRequestArgs) itemInfo).getWidgetSourceContainer();
        }
        getModelWriter().addItemToDatabase(launcherAppWidgetInfo, itemInfo.container, itemInfo.screenId, itemInfo.cellX, itemInfo.cellY);
        appWidgetHostView.setVisibility(0);
        prepareAppWidget(appWidgetHostView, launcherAppWidgetInfo);
        this.mWorkspace.addInScreen(appWidgetHostView, launcherAppWidgetInfo);
        announceForAccessibility(R.string.item_added_to_workspace);
        if (appWidgetHostView instanceof LauncherAppWidgetHostView) {
            final LauncherAppWidgetHostView launcherAppWidgetHostView = (LauncherAppWidgetHostView) appWidgetHostView;
            final CellLayout cellLayout = getCellLayout(launcherAppWidgetInfo.container, launcherAppWidgetInfo.screenId);
            if (this.mStateManager.getState() == LauncherState.NORMAL) {
                AppWidgetResizeFrame.showForWidget(launcherAppWidgetHostView, cellLayout);
            } else {
                this.mStateManager.addStateListener(new StateManager.StateListener<LauncherState>() {
                    public void onStateTransitionComplete(LauncherState launcherState) {
                        if (Launcher.this.mPrevLauncherState == LauncherState.SPRING_LOADED && launcherState == LauncherState.NORMAL) {
                            AppWidgetResizeFrame.showForWidget(launcherAppWidgetHostView, cellLayout);
                            Launcher.this.mStateManager.removeStateListener(this);
                        }
                    }
                });
            }
        }
    }

    private void prepareAppWidget(AppWidgetHostView appWidgetHostView, LauncherAppWidgetInfo launcherAppWidgetInfo) {
        appWidgetHostView.setTag(launcherAppWidgetInfo);
        launcherAppWidgetInfo.onBindAppWidget(this, appWidgetHostView);
        appWidgetHostView.setFocusable(true);
        appWidgetHostView.setOnFocusChangeListener(this.mFocusHandler);
    }

    /* access modifiers changed from: private */
    public void updateNotificationDots(Predicate<PackageUserKey> predicate) {
        this.mWorkspace.updateNotificationDots(predicate);
        this.mAppsView.getAppsStore().updateNotificationDots(predicate);
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mOverlayManager.onAttachedToWindow();
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mOverlayManager.onDetachedFromWindow();
        closeContextMenu();
    }

    public Object onRetainNonConfigurationInstance() {
        NonConfigInstance nonConfigInstance = new NonConfigInstance();
        nonConfigInstance.config = new Configuration(this.mOldConfig);
        int width = this.mDragLayer.getWidth();
        int height = this.mDragLayer.getHeight();
        if (FeatureFlags.ENABLE_LAUNCHER_ACTIVITY_THEME_CROSSFADE.get() && width > 0 && height > 0) {
            DragLayer dragLayer = this.mDragLayer;
            Objects.requireNonNull(dragLayer);
            nonConfigInstance.snapshot = BitmapRenderer.createHardwareBitmap(width, height, new BitmapRenderer() {
                public final void draw(Canvas canvas) {
                    DragLayer.this.draw(canvas);
                }
            });
        }
        return nonConfigInstance;
    }

    public AllAppsTransitionController getAllAppsController() {
        return this.mAllAppsController;
    }

    public DragLayer getDragLayer() {
        return this.mDragLayer;
    }

    public ActivityAllAppsContainerView<Launcher> getAppsView() {
        return this.mAppsView;
    }

    public Workspace<?> getWorkspace() {
        return this.mWorkspace;
    }

    public Hotseat getHotseat() {
        return this.mHotseat;
    }

    public <T extends View> T getOverviewPanel() {
        return this.mOverviewPanel;
    }

    public DropTargetBar getDropTargetBar() {
        return this.mDropTargetBar;
    }

    public ScrimView getScrimView() {
        return this.mScrimView;
    }

    public LauncherAppWidgetHost getAppWidgetHost() {
        return this.mAppWidgetHost;
    }

    /* access modifiers changed from: protected */
    public LauncherAppWidgetHost createAppWidgetHost() {
        return new LauncherAppWidgetHost(this, new IntConsumer() {
            public final void accept(int i) {
                Launcher.this.lambda$createAppWidgetHost$5$Launcher(i);
            }
        });
    }

    public /* synthetic */ void lambda$createAppWidgetHost$5$Launcher(int i) {
        getWorkspace().removeWidget(i);
    }

    public LauncherModel getModel() {
        return this.mModel;
    }

    public ModelWriter getModelWriter() {
        return this.mModelWriter;
    }

    public SharedPreferences getSharedPrefs() {
        return this.mSharedPrefs;
    }

    public SharedPreferences getDevicePrefs() {
        return Utilities.getDevicePrefs(this);
    }

    public int getOrientation() {
        return this.mOldConfig.orientation;
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        if (Utilities.IS_RUNNING_IN_TEST_HARNESS) {
            Log.d(TestProtocol.PERMANENT_DIAG_TAG, "Launcher.onNewIntent: " + intent);
        }
        Object beginSection = TraceHelper.INSTANCE.beginSection(ON_NEW_INTENT_EVT);
        super.onNewIntent(intent);
        boolean z = true;
        boolean z2 = hasWindowFocus() && (intent.getFlags() & 4194304) != 4194304;
        boolean z3 = z2 && isInState(LauncherState.NORMAL) && AbstractFloatingView.getTopOpenView(this) == null;
        boolean equals = "android.intent.action.MAIN".equals(intent.getAction());
        boolean handleNewIntent = ACTIVITY_TRACKER.handleNewIntent(this);
        hideKeyboard();
        if (equals) {
            if (!handleNewIntent) {
                closeOpenViews(isStarted());
                if (!isInState(LauncherState.NORMAL)) {
                    this.mStateManager.goToState(LauncherState.NORMAL, this.mStateManager.shouldAnimateStateChange());
                }
                if (!z2) {
                    this.mAppsView.reset(isStarted());
                }
                if (z3 && !this.mWorkspace.isHandlingTouch()) {
                    Workspace<?> workspace = this.mWorkspace;
                    Objects.requireNonNull(workspace);
                    workspace.post(new Runnable() {
                        public final void run() {
                            Workspace.this.moveToDefaultScreen();
                        }
                    });
                }
            }
            LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
            if (launcherCallbacks != null) {
                launcherCallbacks.onHomeIntent(handleNewIntent);
            }
            LauncherOverlayManager launcherOverlayManager = this.mOverlayManager;
            if (!isStarted() || isForceInvisible()) {
                z = false;
            }
            launcherOverlayManager.hideOverlay(z);
            handleGestureContract(intent);
        } else if ("android.intent.action.ALL_APPS".equals(intent.getAction())) {
            showAllAppsFromIntent(z2);
        } else if ("android.intent.action.SHOW_WORK_APPS".equals(intent.getAction())) {
            showAllAppsWorkTabFromIntent(z2);
        }
        TraceHelper.INSTANCE.endSection(beginSection);
    }

    /* access modifiers changed from: protected */
    public void showAllAppsFromIntent(boolean z) {
        AbstractFloatingView.closeAllOpenViews(this);
        getStateManager().goToState(LauncherState.ALL_APPS, z);
    }

    private void showAllAppsWorkTabFromIntent(boolean z) {
        showAllAppsFromIntent(z);
        this.mAppsView.switchToTab(1);
    }

    /* access modifiers changed from: protected */
    public void handleGestureContract(Intent intent) {
        GestureNavContract fromIntent = GestureNavContract.fromIntent(intent);
        if (fromIntent != null) {
            AbstractFloatingView.closeOpenViews(this, false, 8192);
            FloatingSurfaceView.show(this, fromIntent);
        }
    }

    public void hideKeyboard() {
        View peekDecorView = getWindow().peekDecorView();
        if (peekDecorView != null && peekDecorView.getWindowToken() != null) {
            UiThreadHelper.hideKeyboardAsync(this, peekDecorView.getWindowToken());
        }
    }

    public void onRestoreInstanceState(Bundle bundle) {
        try {
            super.onRestoreInstanceState(bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        IntSet intSet = this.mSynchronouslyBoundPages;
        if (intSet != null) {
            intSet.forEach(new Consumer() {
                public final void accept(Object obj) {
                    Launcher.this.lambda$onRestoreInstanceState$6$Launcher((Integer) obj);
                }
            });
        }
    }

    public /* synthetic */ void lambda$onRestoreInstanceState$6$Launcher(Integer num) {
        int pageIndexForScreenId = this.mWorkspace.getPageIndexForScreenId(num.intValue());
        if (pageIndexForScreenId != -1) {
            this.mWorkspace.restoreInstanceStateForChild(pageIndexForScreenId);
        }
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putIntArray(RUNTIME_STATE_CURRENT_SCREEN_IDS, this.mWorkspace.getCurrentPageScreenIds().getArray().toArray());
        bundle.putInt(RUNTIME_STATE, this.mStateManager.getState().ordinal);
        AbstractFloatingView openView = AbstractFloatingView.getOpenView(this, 16);
        if (openView != null) {
            SparseArray sparseArray = new SparseArray();
            openView.saveHierarchyState(sparseArray);
            bundle.putSparseParcelableArray(RUNTIME_STATE_WIDGET_PANEL, sparseArray);
        } else {
            bundle.remove(RUNTIME_STATE_WIDGET_PANEL);
        }
        finishAutoCancelActionMode();
        PendingRequestArgs pendingRequestArgs = this.mPendingRequestArgs;
        if (pendingRequestArgs != null) {
            bundle.putParcelable(RUNTIME_STATE_PENDING_REQUEST_ARGS, pendingRequestArgs);
        }
        bundle.putInt(RUNTIME_STATE_PENDING_REQUEST_CODE, this.mPendingActivityRequestCode);
        ActivityResultInfo activityResultInfo = this.mPendingActivityResult;
        if (activityResultInfo != null) {
            bundle.putParcelable(RUNTIME_STATE_PENDING_ACTIVITY_RESULT, activityResultInfo);
        }
        super.onSaveInstanceState(bundle);
        this.mOverlayManager.onActivitySaveInstanceState(this, bundle);
    }

    public void onDestroy() {
        sendBroadcast(new Intent(LAUNCHER_STATUS_DESTROY));
        super.onDestroy();
        ACTIVITY_TRACKER.onActivityDestroyed(this);
        unregisterReceiver(this.mScreenOffReceiver);
        unregisterReceiver(this.myBroadcastReceiver);
        this.mWorkspace.removeFolderListeners();
        PluginManagerWrapper.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).removePluginListener(this);
        this.mModel.removeCallbacks(this);
        this.mRotationHelper.destroy();
        try {
            this.mAppWidgetHost.stopListening();
        } catch (NullPointerException e) {
            Log.w(TAG, "problem while stopping AppWidgetHost during Launcher destruction", e);
        }
        TextKeyListener.getInstance().release();
        clearPendingBinds();
        LauncherAppState.getIDP(this).removeOnChangeListener(this);
        this.mOverlayManager.onActivityDestroyed(this);
        this.mUserChangedCallbackCloseable.close();
    }

    public LauncherAccessibilityDelegate getAccessibilityDelegate() {
        return this.mAccessibilityDelegate;
    }

    public DragController getDragController() {
        return this.mDragController;
    }

    public void startActivityForResult(Intent intent, int i, Bundle bundle) {
        if (i != -1) {
            this.mPendingActivityRequestCode = i;
        }
        super.startActivityForResult(intent, i, bundle);
    }

    public void startIntentSenderForResult(IntentSender intentSender, int i, Intent intent, int i2, int i3, int i4, Bundle bundle) {
        if (i != -1) {
            this.mPendingActivityRequestCode = i;
        }
        try {
            super.startIntentSenderForResult(intentSender, i, intent, i2, i3, i4, bundle);
        } catch (IntentSender.SendIntentException unused) {
            throw new ActivityNotFoundException();
        }
    }

    public void startSearch(String str, boolean z, Bundle bundle, boolean z2) {
        if (bundle == null) {
            bundle = new Bundle();
            bundle.putString("source", "launcher-search");
        }
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks == null || !launcherCallbacks.startSearch(str, z, bundle)) {
            super.startSearch(str, z, bundle, true);
        }
        this.mStateManager.goToState(LauncherState.NORMAL);
    }

    public boolean isWorkspaceLocked() {
        return this.mWorkspaceLoading || this.mPendingRequestArgs != null;
    }

    public boolean isWorkspaceLoading() {
        return this.mWorkspaceLoading;
    }

    public boolean isBindingItems() {
        return this.mWorkspaceLoading;
    }

    private void setWorkspaceLoading(boolean z) {
        this.mWorkspaceLoading = z;
    }

    public void setWaitingForResult(PendingRequestArgs pendingRequestArgs) {
        this.mPendingRequestArgs = pendingRequestArgs;
    }

    /* access modifiers changed from: package-private */
    public void addAppWidgetFromDropImpl(int i, ItemInfo itemInfo, AppWidgetHostView appWidgetHostView, WidgetAddFlowHandler widgetAddFlowHandler) {
        addAppWidgetImpl(i, itemInfo, appWidgetHostView, widgetAddFlowHandler, 0);
    }

    /* access modifiers changed from: package-private */
    public void addAppWidgetImpl(int i, ItemInfo itemInfo, AppWidgetHostView appWidgetHostView, WidgetAddFlowHandler widgetAddFlowHandler, int i2) {
        if (!widgetAddFlowHandler.startConfigActivity(this, i, itemInfo, 5)) {
            AnonymousClass9 r0 = new Runnable() {
                public void run() {
                    Launcher.this.mStateManager.goToState(LauncherState.NORMAL, 500);
                }
            };
            completeAddAppWidget(i, itemInfo, appWidgetHostView, widgetAddFlowHandler.getProviderInfo(this));
            this.mWorkspace.removeExtraEmptyScreenDelayed(i2, false, r0);
        }
    }

    public void addPendingItem(PendingAddItemInfo pendingAddItemInfo, int i, int i2, int[] iArr, int i3, int i4) {
        pendingAddItemInfo.container = i;
        pendingAddItemInfo.screenId = i2;
        if (iArr != null) {
            pendingAddItemInfo.cellX = iArr[0];
            pendingAddItemInfo.cellY = iArr[1];
        }
        pendingAddItemInfo.spanX = i3;
        pendingAddItemInfo.spanY = i4;
        int i5 = pendingAddItemInfo.itemType;
        if (i5 == 1) {
            processShortcutFromDrop((PendingAddShortcutInfo) pendingAddItemInfo);
        } else if (i5 == 4 || i5 == 5) {
            addAppWidgetFromDrop((PendingAddWidgetInfo) pendingAddItemInfo);
        } else {
            throw new IllegalStateException("Unknown item type: " + pendingAddItemInfo.itemType);
        }
    }

    private void processShortcutFromDrop(PendingAddShortcutInfo pendingAddShortcutInfo) {
        setWaitingForResult(PendingRequestArgs.forIntent(1, new Intent("android.intent.action.CREATE_SHORTCUT").setComponent(pendingAddShortcutInfo.componentName), pendingAddShortcutInfo));
        TestLogging.recordEvent(TestProtocol.SEQUENCE_MAIN, "start: processShortcutFromDrop");
        if (!pendingAddShortcutInfo.activityInfo.startConfigActivity(this, 1)) {
            handleActivityResult(1, 0, (Intent) null);
        }
    }

    private void addAppWidgetFromDrop(PendingAddWidgetInfo pendingAddWidgetInfo) {
        int i;
        AppWidgetHostView appWidgetHostView = pendingAddWidgetInfo.boundWidget;
        WidgetAddFlowHandler handler = pendingAddWidgetInfo.getHandler();
        if (appWidgetHostView != null) {
            getDragLayer().removeView(appWidgetHostView);
            addAppWidgetFromDropImpl(appWidgetHostView.getAppWidgetId(), pendingAddWidgetInfo, appWidgetHostView, handler);
            pendingAddWidgetInfo.boundWidget = null;
            return;
        }
        if (pendingAddWidgetInfo.itemType == 5) {
            i = CustomWidgetManager.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).getWidgetIdForCustomProvider(pendingAddWidgetInfo.componentName);
        } else {
            i = getAppWidgetHost().allocateAppWidgetId();
        }
        if (this.mAppWidgetManager.bindAppWidgetIdIfAllowed(i, pendingAddWidgetInfo.info, pendingAddWidgetInfo.bindOptions)) {
            addAppWidgetFromDropImpl(i, pendingAddWidgetInfo, (AppWidgetHostView) null, handler);
        } else {
            handler.startBindFlow(this, i, pendingAddWidgetInfo, 11);
        }
    }

    public FolderIcon addFolder(CellLayout cellLayout, int i, int i2, int i3, int i4) {
        FolderInfo folderInfo = new FolderInfo();
        getModelWriter().addItemToDatabase(folderInfo, i, i2, i3, i4);
        FolderIcon inflateFolderAndIcon = FolderIcon.inflateFolderAndIcon(R.layout.folder_icon, this, cellLayout, folderInfo);
        this.mWorkspace.addInScreen(inflateFolderAndIcon, folderInfo);
        this.mWorkspace.getParentCellLayoutForView(inflateFolderAndIcon).getShortcutsAndWidgets().measureChild(inflateFolderAndIcon);
        return inflateFolderAndIcon;
    }

    public Rect getFolderBoundingBox() {
        return getWorkspace().getPageAreaRelativeToDragLayer();
    }

    public void updateOpenFolderPosition(int[] iArr, Rect rect, int i, int i2) {
        int i3;
        int i4 = iArr[0];
        int i5 = iArr[1];
        DeviceProfile deviceProfile = getDeviceProfile();
        int paddingLeft = getWorkspace().getPaddingLeft();
        if (deviceProfile.isPhone && deviceProfile.availableWidthPx - i < paddingLeft * 4) {
            i4 = (deviceProfile.availableWidthPx - i) / 2;
        } else if (i >= rect.width()) {
            i4 = rect.left + ((rect.width() - i) / 2);
        }
        if (i2 >= rect.height()) {
            i3 = rect.top + ((rect.height() - i2) / 2);
        } else {
            Rect absoluteOpenFolderBounds = deviceProfile.getAbsoluteOpenFolderBounds();
            i4 = Math.max(absoluteOpenFolderBounds.left, Math.min(i4, absoluteOpenFolderBounds.right - i));
            i3 = Math.max(absoluteOpenFolderBounds.top, Math.min(i5, absoluteOpenFolderBounds.bottom - i2));
        }
        iArr[0] = i4;
        iArr[1] = i3;
    }

    public boolean removeItem(View view, ItemInfo itemInfo, boolean z) {
        return removeItem(view, itemInfo, z, (String) null);
    }

    public boolean removeItem(View view, ItemInfo itemInfo, boolean z, String str) {
        if (itemInfo instanceof WorkspaceItemInfo) {
            View homescreenIconByItemId = this.mWorkspace.getHomescreenIconByItemId(itemInfo.container);
            if (homescreenIconByItemId instanceof FolderIcon) {
                ((FolderInfo) homescreenIconByItemId.getTag()).remove((WorkspaceItemInfo) itemInfo, true);
            } else {
                this.mWorkspace.removeWorkspaceItem(view);
            }
            if (z) {
                getModelWriter().deleteItemFromDatabase(itemInfo, str);
            }
        } else if (itemInfo instanceof FolderInfo) {
            FolderInfo folderInfo = (FolderInfo) itemInfo;
            if (view instanceof FolderIcon) {
                ((FolderIcon) view).removeListeners();
            }
            this.mWorkspace.removeWorkspaceItem(view);
            if (z) {
                getModelWriter().deleteFolderAndContentsFromDatabase(folderInfo);
            }
        } else if (!(itemInfo instanceof LauncherAppWidgetInfo)) {
            return false;
        } else {
            LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) itemInfo;
            this.mWorkspace.removeWorkspaceItem(view);
            if (z) {
                getModelWriter().deleteWidgetInfo(launcherAppWidgetInfo, getAppWidgetHost(), str);
            }
        }
        return true;
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        TestLogging.recordKeyEvent(TestProtocol.SEQUENCE_MAIN, "Key event", keyEvent);
        return keyEvent.getKeyCode() == 3 || super.dispatchKeyEvent(keyEvent);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x000a, code lost:
        if (r0 != 3) goto L_0x0019;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean dispatchTouchEvent(android.view.MotionEvent r3) {
        /*
            r2 = this;
            int r0 = r3.getAction()
            r1 = 1
            if (r0 == 0) goto L_0x0017
            if (r0 == r1) goto L_0x000d
            r1 = 3
            if (r0 == r1) goto L_0x0013
            goto L_0x0019
        L_0x000d:
            long r0 = android.os.SystemClock.uptimeMillis()
            r2.mLastTouchUpTime = r0
        L_0x0013:
            r0 = 0
            r2.mTouchInProgress = r0
            goto L_0x0019
        L_0x0017:
            r2.mTouchInProgress = r1
        L_0x0019:
            java.lang.String r0 = "Main"
            java.lang.String r1 = "Touch event"
            com.android.launcher3.testing.TestLogging.recordMotionEvent(r0, r1, r3)
            boolean r3 = super.dispatchTouchEvent(r3)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.Launcher.dispatchTouchEvent(android.view.MotionEvent):boolean");
    }

    public boolean isTouchInProgress() {
        return this.mTouchInProgress;
    }

    public void onBackPressed() {
        sendBroadcast(new Intent(LAUNCHER_ON_BACK_PRESSED));
        if (!finishAutoCancelActionMode()) {
            if (this.mDragController.isDragging()) {
                this.mDragController.cancelDrag();
                return;
            }
            AbstractFloatingView topOpenView = AbstractFloatingView.getTopOpenView(this);
            if (topOpenView == null || !topOpenView.onBackPressed()) {
                onStateBack();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onStateBack() {
        this.mStateManager.getState().onBackPressed(this);
    }

    /* access modifiers changed from: protected */
    public void onScreenOff() {
        if (this.mPendingRequestArgs == null) {
            if (!isInState(LauncherState.NORMAL)) {
                onUiChangedWhileSleeping();
            }
            this.mStateManager.goToState(LauncherState.NORMAL);
        }
    }

    public boolean onErrorStartingShortcut(Intent intent, ItemInfo itemInfo) {
        if (intent.getComponent() != null || !"android.intent.action.CALL".equals(intent.getAction()) || checkSelfPermission("android.permission.CALL_PHONE") == 0) {
            return false;
        }
        setWaitingForResult(PendingRequestArgs.forIntent(14, intent, itemInfo));
        requestPermissions(new String[]{"android.permission.CALL_PHONE"}, 14);
        return true;
    }

    /* renamed from: startActivitySafely */
    public boolean lambda$startActivitySafely$7$Launcher(View view, Intent intent, ItemInfo itemInfo) {
        if (!hasBeenResumed()) {
            addOnResumeCallback(new Runnable(view, intent, itemInfo) {
                public final /* synthetic */ View f$1;
                public final /* synthetic */ Intent f$2;
                public final /* synthetic */ ItemInfo f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    Launcher.this.lambda$startActivitySafely$7$Launcher(this.f$1, this.f$2, this.f$3);
                }
            });
            Runnable runnable = this.mOnDeferredActivityLaunchCallback;
            if (runnable != null) {
                runnable.run();
                this.mOnDeferredActivityLaunchCallback = null;
            }
            return true;
        }
        boolean startActivitySafely = super.startActivitySafely(view, intent, itemInfo);
        if (startActivitySafely && (view instanceof BubbleTextView)) {
            BubbleTextView bubbleTextView = (BubbleTextView) view;
            bubbleTextView.setStayPressed(true);
            addOnResumeCallback(new Runnable() {
                public final void run() {
                    BubbleTextView.this.setStayPressed(false);
                }
            });
        }
        return startActivitySafely;
    }

    /* access modifiers changed from: package-private */
    public boolean isHotseatLayout(View view) {
        Hotseat hotseat = this.mHotseat;
        return hotseat != null && view == hotseat;
    }

    public CellLayout getCellLayout(int i, int i2) {
        return i == -101 ? this.mHotseat : this.mWorkspace.getScreenWithId(i2);
    }

    public void onTrimMemory(int i) {
        super.onTrimMemory(i);
        if (i >= 20) {
            SQLiteDatabase.releaseMemory();
        }
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        String str;
        boolean dispatchPopulateAccessibilityEvent = super.dispatchPopulateAccessibilityEvent(accessibilityEvent);
        List text = accessibilityEvent.getText();
        text.clear();
        if (this.mWorkspace == null) {
            str = getString(R.string.home_screen);
        } else {
            str = this.mStateManager.getState().getDescription(this);
        }
        text.add(str);
        return dispatchPopulateAccessibilityEvent;
    }

    public void setOnDeferredActivityLaunchCallback(Runnable runnable) {
        this.mOnDeferredActivityLaunchCallback = runnable;
    }

    public void setPagesToBindSynchronously(IntSet intSet) {
        this.mPagesToBindSynchronously = intSet;
    }

    public IntSet getPagesToBindSynchronously(IntArray intArray) {
        IntSet intSet;
        if (!this.mPagesToBindSynchronously.isEmpty()) {
            intSet = this.mPagesToBindSynchronously;
        } else if (!this.mWorkspaceLoading) {
            intSet = this.mWorkspace.getCurrentPageScreenIds();
        } else {
            intSet = this.mSynchronouslyBoundPages;
        }
        IntArray intArray2 = new IntArray();
        IntSet intSet2 = new IntSet();
        if (intSet.isEmpty()) {
            if (TestProtocol.sDebugTracing) {
                Log.d(TestProtocol.NULL_INT_SET, "getPagesToBindSynchronously (1): " + intSet2);
            }
            return intSet2;
        }
        for (int add : intArray.toArray()) {
            intArray2.add(add);
        }
        int i = intSet.getArray().get(0);
        int screenPair = this.mWorkspace.getScreenPair(i);
        if (intArray2.contains(i)) {
            intSet2.add(i);
            if (this.mDeviceProfile.isTwoPanels && intArray2.contains(screenPair)) {
                intSet2.add(screenPair);
            }
        } else if (LauncherAppState.getIDP(this).supportedProfiles.stream().anyMatch($$Lambda$Launcher$OqFBLfTCgmrMpZZ2bDUI3uU5eQ.INSTANCE) && intArray2.contains(screenPair)) {
            intSet2.add(screenPair);
        }
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.NULL_INT_SET, "getPagesToBindSynchronously (2): " + intSet2);
        }
        return intSet2;
    }

    public void clearPendingBinds() {
        ViewOnDrawExecutor viewOnDrawExecutor = this.mPendingExecutor;
        if (viewOnDrawExecutor != null) {
            viewOnDrawExecutor.cancel();
            this.mPendingExecutor = null;
            this.mAppsView.getAppsStore().disableDeferUpdatesSilently(1);
        }
    }

    public void startBinding() {
        Object beginSection = TraceHelper.INSTANCE.beginSection("startBinding");
        AbstractFloatingView.closeOpenViews(this, true, 23947);
        setWorkspaceLoading(true);
        this.mDragController.cancelDrag();
        this.mWorkspace.clearDropTargets();
        this.mWorkspace.removeAllWorkspaceScreens();
        this.mAppWidgetHost.clearViews();
        Hotseat hotseat = this.mHotseat;
        if (hotseat != null) {
            hotseat.resetLayout(getDeviceProfile().isVerticalBarLayout());
        }
        TraceHelper.INSTANCE.endSection(beginSection);
    }

    public void bindScreens(IntArray intArray) {
        if (intArray.isEmpty()) {
            this.mWorkspace.addExtraEmptyScreens();
        }
        bindAddScreens(intArray);
        this.mWorkspace.unlockWallpaperFromDefaultPageOnNextLayout();
    }

    private void bindAddScreens(IntArray intArray) {
        if (this.mDeviceProfile.isTwoPanels) {
            IntSet wrap = IntSet.wrap(intArray);
            intArray.forEach(new Consumer(wrap) {
                public final /* synthetic */ IntSet f$1;

                {
                    this.f$1 = r2;
                }

                public final void accept(Object obj) {
                    Launcher.this.lambda$bindAddScreens$10$Launcher(this.f$1, (Integer) obj);
                }
            });
            intArray = wrap.getArray();
        }
        int size = intArray.size();
        for (int i = 0; i < size; i++) {
            this.mWorkspace.insertNewWorkspaceScreenBeforeEmptyScreen(intArray.get(i));
        }
    }

    public /* synthetic */ void lambda$bindAddScreens$10$Launcher(IntSet intSet, Integer num) {
        intSet.add(this.mWorkspace.getScreenPair(num.intValue()));
    }

    public void preAddApps() {
        this.mModelWriter.commitDelete();
        AbstractFloatingView openView = AbstractFloatingView.getOpenView(this, 128);
        if (openView != null) {
            openView.post(new Runnable() {
                public final void run() {
                    AbstractFloatingView.this.close(true);
                }
            });
        }
    }

    public void bindAppsAdded(IntArray intArray, ArrayList<ItemInfo> arrayList, ArrayList<ItemInfo> arrayList2) {
        if (intArray != null) {
            intArray.removeAllValues(this.mWorkspace.mScreenOrder);
            bindAddScreens(intArray);
        }
        if (arrayList != null && !arrayList.isEmpty()) {
            bindItems(arrayList, false);
        }
        if (arrayList2 != null && !arrayList2.isEmpty()) {
            bindItems(arrayList2, true);
        }
        this.mWorkspace.removeExtraEmptyScreen(false);
    }

    public void bindItems(List<ItemInfo> list, boolean z) {
        bindItems(list, z, false);
    }

    /* JADX WARNING: Removed duplicated region for block: B:30:0x0087  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00ee  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void bindItems(java.util.List<com.android.launcher3.model.data.ItemInfo> r16, boolean r17, boolean r18) {
        /*
            r15 = this;
            r0 = r15
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            boolean r2 = r15.canAnimatePageChange()
            com.android.launcher3.Workspace<?> r3 = r0.mWorkspace
            int r4 = r16.size()
            r5 = -1
            r6 = 0
            r7 = 0
            r8 = r5
        L_0x0014:
            if (r7 >= r4) goto L_0x010d
            r9 = r16
            java.lang.Object r10 = r9.get(r7)
            com.android.launcher3.model.data.ItemInfo r10 = (com.android.launcher3.model.data.ItemInfo) r10
            int r11 = r10.container
            r12 = -101(0xffffffffffffff9b, float:NaN)
            if (r11 != r12) goto L_0x002a
            com.android.launcher3.Hotseat r11 = r0.mHotseat
            if (r11 != 0) goto L_0x002a
            goto L_0x0109
        L_0x002a:
            int r11 = r10.itemType
            if (r11 == 0) goto L_0x0066
            r12 = 1
            if (r11 == r12) goto L_0x0066
            r12 = 2
            if (r11 == r12) goto L_0x0051
            r12 = 4
            if (r11 == r12) goto L_0x0046
            r12 = 5
            if (r11 == r12) goto L_0x0046
            r12 = 6
            if (r11 != r12) goto L_0x003e
            goto L_0x0066
        L_0x003e:
            java.lang.RuntimeException r1 = new java.lang.RuntimeException
            java.lang.String r2 = "Invalid Item Type"
            r1.<init>(r2)
            throw r1
        L_0x0046:
            r11 = r10
            com.android.launcher3.model.data.LauncherAppWidgetInfo r11 = (com.android.launcher3.model.data.LauncherAppWidgetInfo) r11
            android.view.View r11 = r15.inflateAppWidget(r11)
            if (r11 != 0) goto L_0x006d
            goto L_0x0109
        L_0x0051:
            r11 = 2131492940(0x7f0c004c, float:1.8609346E38)
            int r12 = r3.getCurrentPage()
            android.view.View r12 = r3.getChildAt(r12)
            android.view.ViewGroup r12 = (android.view.ViewGroup) r12
            r13 = r10
            com.android.launcher3.model.data.FolderInfo r13 = (com.android.launcher3.model.data.FolderInfo) r13
            com.android.launcher3.folder.FolderIcon r11 = com.android.launcher3.folder.FolderIcon.inflateFolderAndIcon(r11, r15, r12, r13)
            goto L_0x006d
        L_0x0066:
            r11 = r10
            com.android.launcher3.model.data.WorkspaceItemInfo r11 = (com.android.launcher3.model.data.WorkspaceItemInfo) r11
            android.view.View r11 = r15.createShortcut(r11)
        L_0x006d:
            int r12 = r10.container
            r13 = -100
            if (r12 != r13) goto L_0x00ee
            com.android.launcher3.Workspace<?> r12 = r0.mWorkspace
            int r13 = r10.screenId
            com.android.launcher3.CellLayout r12 = r12.getScreenWithId(r13)
            if (r12 == 0) goto L_0x00ee
            int r13 = r10.cellX
            int r14 = r10.cellY
            boolean r13 = r12.isOccupied(r13, r14)
            if (r13 == 0) goto L_0x00ee
            int r11 = r10.cellX
            int r13 = r10.cellY
            android.view.View r11 = r12.getChildAt(r11, r13)
            if (r11 != 0) goto L_0x00a9
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = "bindItems failed when removing colliding item="
            java.lang.StringBuilder r12 = r12.append(r13)
            java.lang.StringBuilder r12 = r12.append(r10)
            java.lang.String r12 = r12.toString()
            java.lang.String r13 = "Launcher"
            android.util.Log.e(r13, r12)
        L_0x00a9:
            java.lang.Object r11 = r11.getTag()
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = "Collision while binding workspace item: "
            java.lang.StringBuilder r12 = r12.append(r13)
            java.lang.StringBuilder r12 = r12.append(r10)
            java.lang.String r13 = ". Collides with "
            java.lang.StringBuilder r12 = r12.append(r13)
            java.lang.StringBuilder r11 = r12.append(r11)
            java.lang.String r11 = r11.toString()
            com.android.launcher3.model.ModelWriter r12 = r15.getModelWriter()
            r12.deleteItemFromDatabase(r10, r11)
            boolean r11 = com.android.launcher3.testing.TestProtocol.sDebugTracing
            if (r11 == 0) goto L_0x0109
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r12 = "LauncherbindItems failed for item="
            java.lang.StringBuilder r11 = r11.append(r12)
            java.lang.StringBuilder r10 = r11.append(r10)
            java.lang.String r10 = r10.toString()
            java.lang.String r11 = "b/202985412"
            android.util.Log.d(r11, r10)
            goto L_0x0109
        L_0x00ee:
            r3.addInScreenFromBind(r11, r10)
            if (r17 == 0) goto L_0x0106
            r8 = 0
            r11.setAlpha(r8)
            r11.setScaleX(r8)
            r11.setScaleY(r8)
            android.animation.ValueAnimator r8 = r15.createNewAppBounceAnimation(r11, r7)
            r1.add(r8)
            int r8 = r10.screenId
        L_0x0106:
            if (r6 != 0) goto L_0x0109
            r6 = r11
        L_0x0109:
            int r7 = r7 + 1
            goto L_0x0014
        L_0x010d:
            if (r17 == 0) goto L_0x0154
            if (r8 <= r5) goto L_0x0154
            android.animation.AnimatorSet r4 = new android.animation.AnimatorSet
            r4.<init>()
            r4.playTogether(r1)
            if (r18 == 0) goto L_0x0125
            if (r6 == 0) goto L_0x0125
            com.android.launcher3.Launcher$10 r1 = new com.android.launcher3.Launcher$10
            r1.<init>(r6)
            r4.addListener(r1)
        L_0x0125:
            com.android.launcher3.Workspace<?> r1 = r0.mWorkspace
            int r5 = r1.getNextPage()
            int r1 = r1.getScreenIdForPageIndex(r5)
            com.android.launcher3.Workspace<?> r5 = r0.mWorkspace
            int r5 = r5.getPageIndexForScreenId(r8)
            java.util.Objects.requireNonNull(r4)
            com.android.launcher3.-$$Lambda$Launcher$Zf57QPJypVY-GBQtLFFT_795rFY r6 = new com.android.launcher3.-$$Lambda$Launcher$Zf57QPJypVY-GBQtLFFT_795rFY
            r6.<init>(r4)
            r9 = 500(0x1f4, double:2.47E-321)
            if (r2 == 0) goto L_0x014e
            if (r8 == r1) goto L_0x014e
            com.android.launcher3.Workspace<?> r1 = r0.mWorkspace
            com.android.launcher3.Launcher$11 r2 = new com.android.launcher3.Launcher$11
            r2.<init>(r5, r6)
            r1.postDelayed(r2, r9)
            goto L_0x015d
        L_0x014e:
            com.android.launcher3.Workspace<?> r1 = r0.mWorkspace
            r1.postDelayed(r6, r9)
            goto L_0x015d
        L_0x0154:
            if (r18 == 0) goto L_0x015d
            if (r6 == 0) goto L_0x015d
            r1 = 8
            r6.sendAccessibilityEvent(r1)
        L_0x015d:
            r3.requestLayout()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.Launcher.bindItems(java.util.List, boolean, boolean):void");
    }

    public void bindAppWidget(LauncherAppWidgetInfo launcherAppWidgetInfo) {
        View inflateAppWidget = inflateAppWidget(launcherAppWidgetInfo);
        if (inflateAppWidget != null) {
            this.mWorkspace.addInScreen(inflateAppWidget, launcherAppWidgetInfo);
            this.mWorkspace.requestLayout();
        }
    }

    private View inflateAppWidget(LauncherAppWidgetInfo launcherAppWidgetInfo) {
        LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo;
        AppWidgetHostView appWidgetHostView;
        if (launcherAppWidgetInfo.hasOptionFlag(1)) {
            launcherAppWidgetInfo.providerName = QsbContainerView.getSearchComponentName(this);
            if (launcherAppWidgetInfo.providerName == null) {
                getModelWriter().deleteItemFromDatabase(launcherAppWidgetInfo, "search widget removed because search component cannot be found");
                return null;
            }
        }
        if (this.mIsSafeModeEnabled) {
            PendingAppWidgetHostView pendingAppWidgetHostView = new PendingAppWidgetHostView(this, launcherAppWidgetInfo, this.mIconCache, true);
            prepareAppWidget(pendingAppWidgetHostView, launcherAppWidgetInfo);
            return pendingAppWidgetHostView;
        }
        Object beginSection = TraceHelper.INSTANCE.beginSection("BIND_WIDGET_id=" + launcherAppWidgetInfo.appWidgetId);
        String str = "";
        try {
            if (launcherAppWidgetInfo.hasRestoreFlag(2)) {
                str = "the provider isn't ready.";
                launcherAppWidgetProviderInfo = null;
            } else if (launcherAppWidgetInfo.hasRestoreFlag(1)) {
                launcherAppWidgetProviderInfo = this.mAppWidgetManager.findProvider(launcherAppWidgetInfo.providerName, launcherAppWidgetInfo.user);
                if (launcherAppWidgetProviderInfo == null) {
                    str = "WidgetManagerHelper cannot find a provider from provider info.";
                }
            } else {
                launcherAppWidgetProviderInfo = this.mAppWidgetManager.getLauncherAppWidgetInfo(launcherAppWidgetInfo.appWidgetId);
                if (launcherAppWidgetProviderInfo == null) {
                    str = launcherAppWidgetInfo.appWidgetId <= -100 ? "CustomWidgetManager cannot find provider from that widget id." : "AppWidgetManager cannot find provider for that widget id. It could be because AppWidgetService is not available, or the appWidgetId has not been bound to a the provider yet, or you don't have access to that appWidgetId.";
                }
            }
            if (!launcherAppWidgetInfo.hasRestoreFlag(2) && launcherAppWidgetInfo.restoreStatus != 0) {
                if (launcherAppWidgetProviderInfo == null) {
                    getModelWriter().deleteItemFromDatabase(launcherAppWidgetInfo, "Removing restored widget: id=" + launcherAppWidgetInfo.appWidgetId + " belongs to component " + launcherAppWidgetInfo.providerName + " user " + launcherAppWidgetInfo.user + ", as the provider is null and " + str);
                    return null;
                }
                int i = 4;
                if (launcherAppWidgetInfo.hasRestoreFlag(1)) {
                    if (!launcherAppWidgetInfo.hasRestoreFlag(16)) {
                        launcherAppWidgetInfo.appWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
                        launcherAppWidgetInfo.restoreStatus = 16 | launcherAppWidgetInfo.restoreStatus;
                        PendingAddWidgetInfo pendingAddWidgetInfo = new PendingAddWidgetInfo(launcherAppWidgetProviderInfo, launcherAppWidgetInfo.sourceContainer);
                        pendingAddWidgetInfo.spanX = launcherAppWidgetInfo.spanX;
                        pendingAddWidgetInfo.spanY = launcherAppWidgetInfo.spanY;
                        pendingAddWidgetInfo.minSpanX = launcherAppWidgetInfo.minSpanX;
                        pendingAddWidgetInfo.minSpanY = launcherAppWidgetInfo.minSpanY;
                        Bundle defaultSizeOptions = pendingAddWidgetInfo.getDefaultSizeOptions(this);
                        boolean hasRestoreFlag = launcherAppWidgetInfo.hasRestoreFlag(32);
                        if (hasRestoreFlag && launcherAppWidgetInfo.bindOptions != null) {
                            Bundle extras = launcherAppWidgetInfo.bindOptions.getExtras();
                            if (defaultSizeOptions != null) {
                                extras.putAll(defaultSizeOptions);
                            }
                            defaultSizeOptions = extras;
                        }
                        boolean bindAppWidgetIdIfAllowed = this.mAppWidgetManager.bindAppWidgetIdIfAllowed(launcherAppWidgetInfo.appWidgetId, launcherAppWidgetProviderInfo, defaultSizeOptions);
                        launcherAppWidgetInfo.bindOptions = null;
                        launcherAppWidgetInfo.restoreStatus &= -33;
                        if (bindAppWidgetIdIfAllowed) {
                            if (launcherAppWidgetProviderInfo.configure == null || hasRestoreFlag) {
                                i = 0;
                            }
                            launcherAppWidgetInfo.restoreStatus = i;
                        }
                        getModelWriter().updateItemInDatabase(launcherAppWidgetInfo);
                    }
                } else if (launcherAppWidgetInfo.hasRestoreFlag(4) && launcherAppWidgetProviderInfo.configure == null) {
                    launcherAppWidgetInfo.restoreStatus = 0;
                    getModelWriter().updateItemInDatabase(launcherAppWidgetInfo);
                } else if (launcherAppWidgetInfo.hasRestoreFlag(4) && launcherAppWidgetProviderInfo.configure != null && this.mAppWidgetManager.isAppWidgetRestored(launcherAppWidgetInfo.appWidgetId)) {
                    launcherAppWidgetInfo.restoreStatus = 0;
                    getModelWriter().updateItemInDatabase(launcherAppWidgetInfo);
                }
            }
            if (launcherAppWidgetInfo.restoreStatus == 0) {
                if (launcherAppWidgetProviderInfo == null) {
                    FileLog.e(TAG, "Removing invalid widget: id=" + launcherAppWidgetInfo.appWidgetId);
                    getModelWriter().deleteWidgetInfo(launcherAppWidgetInfo, getAppWidgetHost(), str);
                    TraceHelper.INSTANCE.endSection(beginSection);
                    return null;
                }
                launcherAppWidgetInfo.minSpanX = launcherAppWidgetProviderInfo.minSpanX;
                launcherAppWidgetInfo.minSpanY = launcherAppWidgetProviderInfo.minSpanY;
                appWidgetHostView = this.mAppWidgetHost.createView(this, launcherAppWidgetInfo.appWidgetId, launcherAppWidgetProviderInfo);
            } else if (launcherAppWidgetInfo.hasRestoreFlag(1) || launcherAppWidgetProviderInfo == null) {
                appWidgetHostView = new PendingAppWidgetHostView(this, launcherAppWidgetInfo, this.mIconCache, false);
            } else {
                this.mAppWidgetHost.addPendingView(launcherAppWidgetInfo.appWidgetId, new PendingAppWidgetHostView(this, launcherAppWidgetInfo, this.mIconCache, false));
                appWidgetHostView = this.mAppWidgetHost.createView(this, launcherAppWidgetInfo.appWidgetId, launcherAppWidgetProviderInfo);
            }
            prepareAppWidget(appWidgetHostView, launcherAppWidgetInfo);
            TraceHelper.INSTANCE.endSection(beginSection);
            return appWidgetHostView;
        } finally {
            TraceHelper.INSTANCE.endSection(beginSection);
        }
    }

    private LauncherAppWidgetInfo completeRestoreAppWidget(int i, int i2) {
        LauncherAppWidgetHostView widgetForAppWidgetId = this.mWorkspace.getWidgetForAppWidgetId(i);
        if (widgetForAppWidgetId == null || !(widgetForAppWidgetId instanceof PendingAppWidgetHostView)) {
            Log.e(TAG, "Widget update called, when the widget no longer exists.");
            return null;
        }
        LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) widgetForAppWidgetId.getTag();
        launcherAppWidgetInfo.restoreStatus = i2;
        if (launcherAppWidgetInfo.restoreStatus == 0) {
            launcherAppWidgetInfo.pendingItemInfo = null;
        }
        if (((PendingAppWidgetHostView) widgetForAppWidgetId).isReinflateIfNeeded()) {
            widgetForAppWidgetId.reInflate();
        }
        getModelWriter().updateItemInDatabase(launcherAppWidgetInfo);
        return launcherAppWidgetInfo;
    }

    public void clearPendingExecutor(ViewOnDrawExecutor viewOnDrawExecutor) {
        if (this.mPendingExecutor == viewOnDrawExecutor) {
            this.mPendingExecutor = null;
        }
    }

    public void onInitialBindComplete(IntSet intSet, RunnableList runnableList) {
        this.mSynchronouslyBoundPages = intSet;
        this.mPagesToBindSynchronously = new IntSet();
        clearPendingBinds();
        ViewOnDrawExecutor viewOnDrawExecutor = new ViewOnDrawExecutor(runnableList);
        this.mPendingExecutor = viewOnDrawExecutor;
        if (!isInState(LauncherState.ALL_APPS)) {
            this.mAppsView.getAppsStore().enableDeferUpdates(1);
            runnableList.add(new Runnable() {
                public final void run() {
                    Launcher.this.lambda$onInitialBindComplete$12$Launcher();
                }
            });
        }
        if (this.mOnInitialBindListener != null) {
            getRootView().getViewTreeObserver().removeOnPreDrawListener(this.mOnInitialBindListener);
            this.mOnInitialBindListener = null;
        }
        viewOnDrawExecutor.onLoadAnimationCompleted();
        viewOnDrawExecutor.attachTo(this);
        if (Utilities.ATLEAST_S) {
            Trace.endAsyncSection(DISPLAY_WORKSPACE_TRACE_METHOD_NAME, 0);
        }
    }

    public /* synthetic */ void lambda$onInitialBindComplete$12$Launcher() {
        this.mAppsView.getAppsStore().disableDeferUpdates(1);
    }

    public void finishBindingItems(IntSet intSet) {
        Object beginSection = TraceHelper.INSTANCE.beginSection("finishBindingItems");
        this.mWorkspace.restoreInstanceStateForRemainingPages();
        setWorkspaceLoading(false);
        ActivityResultInfo activityResultInfo = this.mPendingActivityResult;
        if (activityResultInfo != null) {
            handleActivityResult(activityResultInfo.requestCode, this.mPendingActivityResult.resultCode, this.mPendingActivityResult.data);
            this.mPendingActivityResult = null;
        }
        int pageIndexForScreenId = (intSet == null || intSet.isEmpty()) ? -1 : this.mWorkspace.getPageIndexForScreenId(intSet.getArray().get(0));
        this.mWorkspace.setCurrentPage(pageIndexForScreenId, pageIndexForScreenId);
        this.mPagesToBindSynchronously = new IntSet();
        getViewCache().setCacheSize(R.layout.folder_application, this.mDeviceProfile.inv.numFolderColumns * this.mDeviceProfile.inv.numFolderRows);
        getViewCache().setCacheSize(R.layout.folder_page, 2);
        TraceHelper.INSTANCE.endSection(beginSection);
    }

    private boolean canAnimatePageChange() {
        if (!this.mDragController.isDragging() && SystemClock.uptimeMillis() - this.mLastTouchUpTime > 5000) {
            return true;
        }
        return false;
    }

    public View getFirstMatchForAppClose(int i, String str, UserHandle userHandle, boolean z) {
        $$Lambda$Launcher$IGZKc2QBxkMoR4WwF7J8NpBhC0I r0 = new Predicate(i) {
            public final /* synthetic */ int f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return Launcher.lambda$getFirstMatchForAppClose$13(this.f$0, (ItemInfo) obj);
            }
        };
        $$Lambda$Launcher$Ofq_x_Xg5CdcF0f1PYpkDtvo_wA r5 = new Predicate(userHandle, str) {
            public final /* synthetic */ UserHandle f$0;
            public final /* synthetic */ String f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final boolean test(Object obj) {
                return Launcher.lambda$getFirstMatchForAppClose$14(this.f$0, this.f$1, (ItemInfo) obj);
            }
        };
        if (!z || !isInState(LauncherState.ALL_APPS)) {
            ArrayList arrayList = new ArrayList(this.mWorkspace.getPanelCount() + 1);
            arrayList.add(this.mWorkspace.getHotseat().getShortcutsAndWidgets());
            this.mWorkspace.forEachVisiblePage(new Consumer(arrayList) {
                public final /* synthetic */ List f$0;

                {
                    this.f$0 = r1;
                }

                public final void accept(Object obj) {
                    this.f$0.add(((CellLayout) ((View) obj)).getShortcutsAndWidgets());
                }
            });
            return getFirstMatch(arrayList, r0, ItemInfoMatcher.forFolderMatch(r0), r5, ItemInfoMatcher.forFolderMatch(r5));
        }
        AllAppsRecyclerView activeRecyclerView = this.mAppsView.getActiveRecyclerView();
        View firstMatch = getFirstMatch(Collections.singletonList(activeRecyclerView), r0, r5);
        if (firstMatch == null || activeRecyclerView.getCurrentScrollY() <= 0) {
            return firstMatch;
        }
        RectF rectF = new RectF();
        FloatingIconView.getLocationBoundsForView(this, firstMatch, false, rectF, new Rect());
        if (rectF.top < ((float) this.mAppsView.getHeaderBottom())) {
            return null;
        }
        return firstMatch;
    }

    static /* synthetic */ boolean lambda$getFirstMatchForAppClose$13(int i, ItemInfo itemInfo) {
        return itemInfo != null && itemInfo.id == i;
    }

    static /* synthetic */ boolean lambda$getFirstMatchForAppClose$14(UserHandle userHandle, String str, ItemInfo itemInfo) {
        return itemInfo != null && itemInfo.itemType == 0 && itemInfo.user.equals(userHandle) && itemInfo.getTargetComponent() != null && TextUtils.equals(itemInfo.getTargetComponent().getPackageName(), str);
    }

    private static View getFirstMatch(Iterable<ViewGroup> iterable, Predicate<ItemInfo>... predicateArr) {
        for (Predicate<ItemInfo> predicate : predicateArr) {
            for (ViewGroup mapOverViewGroup : iterable) {
                View mapOverViewGroup2 = mapOverViewGroup(mapOverViewGroup, predicate);
                if (mapOverViewGroup2 != null) {
                    return mapOverViewGroup2;
                }
            }
        }
        return null;
    }

    private static View mapOverViewGroup(ViewGroup viewGroup, Predicate<ItemInfo> predicate) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (predicate.test((ItemInfo) childAt.getTag())) {
                return childAt;
            }
        }
        return null;
    }

    private ValueAnimator createNewAppBounceAnimation(View view, int i) {
        ObjectAnimator duration = new PropertyListBuilder().alpha(1.0f).scale(1.0f).build(view).setDuration(450);
        duration.setStartDelay((long) (i * 85));
        duration.setInterpolator(new OvershootInterpolator(BOUNCE_ANIMATION_TENSION));
        return duration;
    }

    private void announceForAccessibility(int i) {
        getDragLayer().announceForAccessibility(getString(i));
    }

    public void bindAllApplications(AppInfo[] appInfoArr, int i) {
        this.mAppsView.getAppsStore().setApps(appInfoArr, i);
        PopupContainerWithArrow.dismissInvalidPopup(this);
        if (Utilities.ATLEAST_S) {
            Trace.endAsyncSection(DISPLAY_ALL_APPS_TRACE_METHOD_NAME, 1);
        }
    }

    public void bindDeepShortcutMap(HashMap<ComponentKey, Integer> hashMap) {
        this.mPopupDataProvider.setDeepShortcutMap(hashMap);
    }

    public void bindIncrementalDownloadProgressUpdated(AppInfo appInfo) {
        this.mAppsView.getAppsStore().updateProgressBar(appInfo);
    }

    public void bindWidgetsRestored(ArrayList<LauncherAppWidgetInfo> arrayList) {
        this.mWorkspace.widgetsRestored(arrayList);
    }

    public void bindWorkspaceItemsChanged(List<WorkspaceItemInfo> list) {
        if (!list.isEmpty()) {
            this.mWorkspace.updateWorkspaceItems(list, this);
            PopupContainerWithArrow.dismissInvalidPopup(this);
        }
    }

    public void bindRestoreItemsChange(HashSet<ItemInfo> hashSet) {
        this.mWorkspace.updateRestoreItems(hashSet, this);
    }

    public void bindWorkspaceComponentsRemoved(Predicate<ItemInfo> predicate) {
        this.mWorkspace.removeItemsByMatcher(predicate);
        this.mDragController.onAppsRemoved(predicate);
        PopupContainerWithArrow.dismissInvalidPopup(this);
    }

    public void bindAllWidgets(List<WidgetsListBaseEntry> list) {
        this.mPopupDataProvider.setAllWidgets(list);
    }

    public void bindStringCache(StringCache stringCache) {
        this.mStringCache = stringCache;
    }

    public StringCache getStringCache() {
        return this.mStringCache;
    }

    public void refreshAndBindWidgetsForPackageUser(PackageUserKey packageUserKey) {
        this.mModel.refreshAndBindWidgetsAndShortcuts(packageUserKey);
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        super.dump(str, fileDescriptor, printWriter, strArr);
        if (strArr.length > 0) {
            if (TextUtils.equals(strArr[0], "--all")) {
                printWriter.println(str + "Workspace Items");
                for (int i = 0; i < this.mWorkspace.getPageCount(); i++) {
                    printWriter.println(str + "  Homescreen " + i);
                    ShortcutAndWidgetContainer shortcutsAndWidgets = ((CellLayout) this.mWorkspace.getPageAt(i)).getShortcutsAndWidgets();
                    for (int i2 = 0; i2 < shortcutsAndWidgets.getChildCount(); i2++) {
                        Object tag = shortcutsAndWidgets.getChildAt(i2).getTag();
                        if (tag != null) {
                            printWriter.println(str + "    " + tag.toString());
                        }
                    }
                }
                printWriter.println(str + "  Hotseat");
                ShortcutAndWidgetContainer shortcutsAndWidgets2 = this.mHotseat.getShortcutsAndWidgets();
                for (int i3 = 0; i3 < shortcutsAndWidgets2.getChildCount(); i3++) {
                    Object tag2 = shortcutsAndWidgets2.getChildAt(i3).getTag();
                    if (tag2 != null) {
                        printWriter.println(str + "    " + tag2.toString());
                    }
                }
            }
        }
        printWriter.println(str + "Misc:");
        dumpMisc(str + "\t", printWriter);
        printWriter.println(str + "\tmWorkspaceLoading=" + this.mWorkspaceLoading);
        printWriter.println(str + "\tmPendingRequestArgs=" + this.mPendingRequestArgs + " mPendingActivityResult=" + this.mPendingActivityResult);
        printWriter.println(str + "\tmRotationHelper: " + this.mRotationHelper);
        printWriter.println(str + "\tmAppWidgetHost.isListening: " + this.mAppWidgetHost.isListening());
        this.mDragLayer.dump(str, printWriter);
        this.mStateManager.dump(str, printWriter);
        this.mPopupDataProvider.dump(str, printWriter);
        this.mDeviceProfile.dump(str, printWriter);
        try {
            FileLog.flushAll(printWriter);
        } catch (Exception unused) {
        }
        this.mModel.dumpState(str, fileDescriptor, printWriter, strArr);
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            launcherCallbacks.dump(str, fileDescriptor, printWriter, strArr);
        }
        this.mOverlayManager.dump(str, printWriter);
    }

    public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> list, Menu menu, int i) {
        ArrayList arrayList = new ArrayList();
        if (isInState(LauncherState.NORMAL)) {
            arrayList.add(new KeyboardShortcutInfo(getString(R.string.all_apps_button_label), 29, 4096));
            arrayList.add(new KeyboardShortcutInfo(getString(R.string.widget_button_text), 51, 4096));
        }
        LauncherAccessibilityDelegate.getSupportedActions(this, getCurrentFocus()).forEach(new Consumer(arrayList) {
            public final /* synthetic */ ArrayList f$0;

            {
                this.f$0 = r1;
            }

            public final void accept(Object obj) {
                this.f$0.add(new KeyboardShortcutInfo(((BaseAccessibilityDelegate.LauncherAction) obj).accessibilityAction.getLabel(), ((BaseAccessibilityDelegate.LauncherAction) obj).keyCode, 4096));
            }
        });
        if (!arrayList.isEmpty()) {
            list.add(new KeyboardShortcutGroup(getString(R.string.home_screen), arrayList));
        }
        super.onProvideKeyboardShortcuts(list, menu, i);
    }

    public boolean onKeyShortcut(int i, KeyEvent keyEvent) {
        if (keyEvent.hasModifiers(4096)) {
            if (i != 29) {
                if (i != 51) {
                    for (BaseAccessibilityDelegate.LauncherAction next : LauncherAccessibilityDelegate.getSupportedActions(this, getCurrentFocus())) {
                        if (next.keyCode == i) {
                            return next.invokeFromKeyboard(getCurrentFocus());
                        }
                    }
                } else if (isInState(LauncherState.NORMAL)) {
                    OptionsPopupView.openWidgets(this);
                    return true;
                }
            } else if (isInState(LauncherState.NORMAL)) {
                getStateManager().goToState(LauncherState.ALL_APPS);
                return true;
            }
        }
        return super.onKeyShortcut(i, keyEvent);
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (i != 82) {
            return super.onKeyUp(i, keyEvent);
        }
        if (this.mDragController.isDragging() || this.mWorkspace.isSwitchingState() || !isInState(LauncherState.NORMAL)) {
            return true;
        }
        closeOpenViews();
        if (Utilities.IS_RUNNING_IN_TEST_HARNESS) {
            Log.d(TestProtocol.PERMANENT_DIAG_TAG, "Opening options popup on key up");
        }
        showDefaultOptions(-1.0f, -1.0f);
        return true;
    }

    public void showDefaultOptions(float f, float f2) {
        OptionsPopupView.show(this, getPopupTarget(f, f2), OptionsPopupView.getOptions(this), false);
    }

    /* access modifiers changed from: protected */
    public RectF getPopupTarget(float f, float f2) {
        float dimension = getResources().getDimension(R.dimen.options_menu_thumb_size) / 2.0f;
        if (f < 0.0f || f2 < 0.0f) {
            f = (float) (this.mDragLayer.getWidth() / 2);
            f2 = (float) (this.mDragLayer.getHeight() / 2);
        }
        return new RectF(f - dimension, f2 - dimension, f + dimension, f2 + dimension);
    }

    public boolean shouldUseColorExtractionForPopup() {
        if (AbstractFloatingView.getTopOpenViewWithType(this, 1) != null || getStateManager().getState() == LauncherState.ALL_APPS) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public void collectStateHandlers(List<StateManager.StateHandler> list) {
        list.add(getAllAppsController());
        list.add(getWorkspace());
    }

    public TouchController[] createTouchControllers() {
        return new TouchController[]{getDragController(), new AllAppsSwipeController(this)};
    }

    public void returnToHomescreen() {
        super.returnToHomescreen();
        getStateManager().goToState(LauncherState.NORMAL);
    }

    private void closeOpenViews() {
        closeOpenViews(true);
    }

    /* access modifiers changed from: protected */
    public void closeOpenViews(boolean z) {
        AbstractFloatingView.closeAllOpenViews(this, z);
    }

    public Stream<SystemShortcut.Factory> getSupportedShortcuts() {
        return Stream.of(new SystemShortcut.Factory[]{SystemShortcut.APP_INFO, SystemShortcut.WIDGETS, SystemShortcut.INSTALL});
    }

    /* access modifiers changed from: protected */
    public LauncherAccessibilityDelegate createAccessibilityDelegate() {
        return new LauncherAccessibilityDelegate(this);
    }

    public float[] getNormalOverviewScaleAndOffset() {
        return new float[]{1.0f, 0.0f};
    }

    public static Launcher getLauncher(Context context) {
        return (Launcher) fromContext(context);
    }

    public static <T extends Launcher> T cast(ActivityContext activityContext) {
        return (Launcher) activityContext;
    }

    private void crossFadeWithPreviousAppearance() {
        NonConfigInstance nonConfigInstance = (NonConfigInstance) getLastNonConfigurationInstance();
        if (nonConfigInstance != null && nonConfigInstance.snapshot != null) {
            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(nonConfigInstance.snapshot);
            imageView.setImportantForAccessibility(2);
            InsettableFrameLayout.LayoutParams layoutParams = new InsettableFrameLayout.LayoutParams(-1, -1);
            layoutParams.ignoreInsets = true;
            imageView.setLayoutParams(layoutParams);
            getRootView().addView(imageView);
            imageView.animate().setDuration(375).alpha(0.0f).withEndAction(new Runnable(imageView) {
                public final /* synthetic */ ImageView f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    Launcher.this.lambda$crossFadeWithPreviousAppearance$17$Launcher(this.f$1);
                }
            }).start();
        }
    }

    public /* synthetic */ void lambda$crossFadeWithPreviousAppearance$17$Launcher(ImageView imageView) {
        getRootView().removeView(imageView);
    }

    public DragOptions getDefaultWorkspaceDragOptions() {
        return new DragOptions();
    }

    private static class NonConfigInstance {
        public Configuration config;
        public Bitmap snapshot;

        private NonConfigInstance() {
        }
    }

    public StatsLogManager getStatsLogManager() {
        return super.getStatsLogManager().withDefaultInstanceId(this.mAllAppsSessionLogId);
    }

    public ArrowPopup<?> getOptionsPopup() {
        return (ArrowPopup) findViewById(R.id.popup_container);
    }

    public void pauseExpensiveViewUpdates() {
        ((PageIndicator) getWorkspace().getPageIndicator()).pauseAnimations();
        getWorkspace().mapOverItems($$Lambda$Launcher$8Z9yfJq2_WMWRrEQkNeeYRkwe1w.INSTANCE);
    }

    static /* synthetic */ boolean lambda$pauseExpensiveViewUpdates$18(ItemInfo itemInfo, View view) {
        if (!(view instanceof LauncherAppWidgetHostView)) {
            return false;
        }
        ((LauncherAppWidgetHostView) view).beginDeferringUpdates();
        return false;
    }

    public void resumeExpensiveViewUpdates() {
        ((PageIndicator) getWorkspace().getPageIndicator()).skipAnimationsToEnd();
        getWorkspace().mapOverItems($$Lambda$Launcher$ISERr2VYXsMM7IiB_TX9nym0pY4.INSTANCE);
    }

    static /* synthetic */ boolean lambda$resumeExpensiveViewUpdates$19(ItemInfo itemInfo, View view) {
        if (!(view instanceof LauncherAppWidgetHostView)) {
            return false;
        }
        ((LauncherAppWidgetHostView) view).endDeferringUpdates();
        return false;
    }
}
