package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.util.Size;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewRootImpl;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import androidx.core.graphics.ColorUtils;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.LauncherAnimationRunner;
import com.android.launcher3.QuickstepTransitionManager;
import com.android.launcher3.allapps.ActivityAllAppsContainerView;
import com.android.launcher3.anim.AnimationSuccessListener;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.icons.FastBitmapDrawable;
import com.android.launcher3.shortcuts.DeepShortcutView;
import com.android.launcher3.statehandlers.DepthController;
import com.android.launcher3.taskbar.LauncherTaskbarUIController;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.launcher3.util.ActivityOptionsWrapper;
import com.android.launcher3.util.ObjectWrapper;
import com.android.launcher3.util.RunnableList;
import com.android.launcher3.util.Themes;
import com.android.launcher3.util.window.RefreshRateTracker;
import com.android.launcher3.views.FloatingIconView;
import com.android.launcher3.views.ScrimView;
import com.android.launcher3.widget.LauncherAppWidgetHostView;
import com.android.quickstep.LauncherBackAnimationController;
import com.android.quickstep.RemoteAnimationTargets;
import com.android.quickstep.SystemUiProxy;
import com.android.quickstep.TaskViewUtils;
import com.android.quickstep.util.MultiValueUpdateListener;
import com.android.quickstep.util.RectFSpringAnim;
import com.android.quickstep.util.RemoteAnimationProvider;
import com.android.quickstep.util.SurfaceTransactionApplier;
import com.android.quickstep.views.FloatingWidgetView;
import com.android.quickstep.views.RecentsView;
import com.android.systemui.shared.system.ActivityCompat;
import com.android.systemui.shared.system.ActivityOptionsCompat;
import com.android.systemui.shared.system.BlurUtils;
import com.android.systemui.shared.system.InteractionJankMonitorWrapper;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.shared.system.RemoteAnimationAdapterCompat;
import com.android.systemui.shared.system.RemoteAnimationDefinitionCompat;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.android.systemui.shared.system.RemoteTransitionCompat;
import com.android.systemui.shared.system.SyncRtSurfaceTransactionApplierCompat;
import com.android.wm.shell.startingsurface.IStartingWindowListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class QuickstepTransitionManager implements DeviceProfile.OnDeviceProfileChangeListener {
    public static final long ANIMATION_DELAY_NAV_FADE_IN = 234;
    public static final int ANIMATION_NAV_FADE_IN_DURATION = 266;
    public static final int ANIMATION_NAV_FADE_OUT_DURATION = 133;
    private static final long APP_LAUNCH_ALPHA_DURATION = 50;
    private static final long APP_LAUNCH_ALPHA_START_DELAY = 25;
    private static final long APP_LAUNCH_DURATION = 500;
    private static final int CLOSING_TRANSITION_DURATION_MS = 250;
    public static final int CONTENT_ALPHA_DURATION = 217;
    protected static final int CONTENT_SCALE_DURATION = 350;
    protected static final int CONTENT_SCRIM_DURATION = 350;
    private static final String CONTROL_REMOTE_APP_TRANSITION_PERMISSION = "android.permission.CONTROL_REMOTE_APP_TRANSITION_ANIMATIONS";
    private static final boolean ENABLE_SHELL_STARTING_SURFACE = SystemProperties.getBoolean("persist.debug.shell_starting_surface", true);
    private static final int LAUNCHER_RESUME_START_DELAY = 100;
    private static final int MAX_NUM_TASKS = 5;
    public static final Interpolator NAV_FADE_IN_INTERPOLATOR = new PathInterpolator(0.0f, 0.0f, 0.0f, 1.0f);
    public static final Interpolator NAV_FADE_OUT_INTERPOLATOR = new PathInterpolator(0.2f, 0.0f, 1.0f, 1.0f);
    public static final int RECENTS_LAUNCH_DURATION = 336;
    public static final int SPLIT_DIVIDER_ANIM_DURATION = 100;
    public static final int SPLIT_LAUNCH_DURATION = 370;
    public static final int STATUS_BAR_TRANSITION_DURATION = 120;
    public static final int STATUS_BAR_TRANSITION_PRE_DELAY = 96;
    private static final String TAG = "QuickstepTransition";
    private static final int WIDGET_CROSSFADE_DURATION_MILLIS = 125;
    private LauncherAnimationRunner.RemoteAnimationFactory mAppLaunchRunner;
    private LauncherBackAnimationController mBackAnimationController;
    /* access modifiers changed from: private */
    public final float mClosingWindowTransY;
    private final float mContentScale;
    /* access modifiers changed from: private */
    public DeviceProfile mDeviceProfile;
    /* access modifiers changed from: private */
    public final DragLayer mDragLayer;
    /* access modifiers changed from: private */
    public final AnimatorListenerAdapter mForceInvisibleListener = new AnimatorListenerAdapter() {
        public void onAnimationStart(Animator animator) {
            QuickstepTransitionManager.this.mLauncher.addForceInvisibleFlag(2);
        }

        public void onAnimationEnd(Animator animator) {
            QuickstepTransitionManager.this.mLauncher.clearForceInvisibleFlag(2);
        }
    };
    final Handler mHandler;
    private LauncherAnimationRunner.RemoteAnimationFactory mKeyguardGoingAwayRunner;
    protected final BaseQuickstepLauncher mLauncher;
    private RemoteTransitionCompat mLauncherOpenTransition;
    private final float mMaxShadowRadius;
    /* access modifiers changed from: private */
    public final Interpolator mOpeningInterpolator;
    /* access modifiers changed from: private */
    public final Interpolator mOpeningXInterpolator;
    private RemoteAnimationProvider mRemoteAnimationProvider;
    private final StartingWindowListener mStartingWindowListener;
    /* access modifiers changed from: private */
    public LinkedHashMap<Integer, Pair<Integer, Integer>> mTaskStartParams;
    private LauncherAnimationRunner.RemoteAnimationFactory mWallpaperOpenRunner;
    private LauncherAnimationRunner.RemoteAnimationFactory mWallpaperOpenTransitionRunner;

    public QuickstepTransitionManager(Context context) {
        StartingWindowListener startingWindowListener = new StartingWindowListener();
        this.mStartingWindowListener = startingWindowListener;
        BaseQuickstepLauncher baseQuickstepLauncher = (BaseQuickstepLauncher) Launcher.cast(Launcher.getLauncher(context));
        this.mLauncher = baseQuickstepLauncher;
        this.mDragLayer = baseQuickstepLauncher.getDragLayer();
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mDeviceProfile = baseQuickstepLauncher.getDeviceProfile();
        this.mBackAnimationController = new LauncherBackAnimationController(baseQuickstepLauncher, this);
        Resources resources = baseQuickstepLauncher.getResources();
        this.mContentScale = resources.getFloat(R.dimen.content_scale);
        this.mClosingWindowTransY = (float) resources.getDimensionPixelSize(R.dimen.closing_window_trans_y);
        this.mMaxShadowRadius = (float) resources.getDimensionPixelSize(R.dimen.max_shadow_radius);
        baseQuickstepLauncher.addOnDeviceProfileChangeListener(this);
        if (supportsSSplashScreen()) {
            this.mTaskStartParams = new LinkedHashMap<Integer, Pair<Integer, Integer>>(5) {
                /* access modifiers changed from: protected */
                public boolean removeEldestEntry(Map.Entry<Integer, Pair<Integer, Integer>> entry) {
                    return size() > 5;
                }
            };
            startingWindowListener.setTransitionManager(this);
            SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(baseQuickstepLauncher).setStartingWindowListener(startingWindowListener);
        }
        this.mOpeningXInterpolator = AnimationUtils.loadInterpolator(context, R.interpolator.app_open_x);
        this.mOpeningInterpolator = AnimationUtils.loadInterpolator(context, R.interpolator.three_point_fast_out_extra_slow_in);
    }

    public void onDeviceProfileChanged(DeviceProfile deviceProfile) {
        this.mDeviceProfile = deviceProfile;
    }

    public ActivityOptionsWrapper getActivityLaunchOptions(View view) {
        boolean isLaunchingFromRecents = isLaunchingFromRecents(view, (RemoteAnimationTargetCompat[]) null);
        RunnableList runnableList = new RunnableList();
        this.mAppLaunchRunner = new AppLaunchAnimationRunner(view, runnableList);
        LauncherAnimationRunner launcherAnimationRunner = new LauncherAnimationRunner(this.mHandler, this.mAppLaunchRunner, true);
        long j = isLaunchingFromRecents ? 336 : APP_LAUNCH_DURATION;
        return new ActivityOptionsWrapper(ActivityOptionsCompat.makeRemoteAnimation(new RemoteAnimationAdapterCompat(launcherAnimationRunner, j, (j - 120) - 96, this.mLauncher.getIApplicationThread())), runnableList);
    }

    /* access modifiers changed from: protected */
    public boolean isLaunchingFromRecents(View view, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr) {
        return this.mLauncher.getStateManager().getState().overviewUi && TaskViewUtils.findTaskViewToLaunch((RecentsView) this.mLauncher.getOverviewPanel(), view, remoteAnimationTargetCompatArr) != null;
    }

    /* access modifiers changed from: protected */
    public void composeRecentsLaunchAnimator(AnimatorSet animatorSet, View view, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3, boolean z) {
        TaskViewUtils.composeRecentsLaunchAnimator(animatorSet, view, remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, remoteAnimationTargetCompatArr3, z, this.mLauncher.getStateManager(), (RecentsView) this.mLauncher.getOverviewPanel(), this.mLauncher.getDepthController());
    }

    private boolean areAllTargetsTranslucent(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr) {
        boolean z = true;
        for (RemoteAnimationTargetCompat remoteAnimationTargetCompat : remoteAnimationTargetCompatArr) {
            if (remoteAnimationTargetCompat.mode == 0) {
                z &= remoteAnimationTargetCompat.isTranslucent;
            }
            if (!z) {
                break;
            }
        }
        return z;
    }

    /* access modifiers changed from: private */
    public void composeIconLaunchAnimator(AnimatorSet animatorSet, View view, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3, boolean z) {
        AnimatorSet animatorSet2 = animatorSet;
        this.mLauncher.getStateManager().setCurrentAnimation(animatorSet, new Animator[0]);
        int rotationChange = getRotationChange(remoteAnimationTargetCompatArr);
        int singleFrameMs = RefreshRateTracker.getSingleFrameMs(this.mLauncher);
        Animator openingWindowAnimators = getOpeningWindowAnimators(view, remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, remoteAnimationTargetCompatArr3, getWindowTargetBounds(remoteAnimationTargetCompatArr, rotationChange), areAllTargetsTranslucent(remoteAnimationTargetCompatArr), rotationChange);
        openingWindowAnimators.setStartDelay((long) singleFrameMs);
        animatorSet.play(openingWindowAnimators);
        if (z) {
            final Pair<AnimatorSet, Runnable> launcherContentAnimator = getLauncherContentAnimator(true, singleFrameMs, false);
            animatorSet.play((Animator) launcherContentAnimator.first);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    ((Runnable) launcherContentAnimator.second).run();
                }
            });
            return;
        }
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                QuickstepTransitionManager.this.mLauncher.addOnResumeCallback(new Runnable() {
                    public final void run() {
                        QuickstepTransitionManager.AnonymousClass4.this.lambda$onAnimationStart$0$QuickstepTransitionManager$4();
                    }
                });
            }

            public /* synthetic */ void lambda$onAnimationStart$0$QuickstepTransitionManager$4() {
                ObjectAnimator.ofFloat(QuickstepTransitionManager.this.mLauncher.getDepthController(), DepthController.DEPTH, new float[]{QuickstepTransitionManager.this.mLauncher.getStateManager().getState().getDepth(QuickstepTransitionManager.this.mLauncher)}).start();
            }
        });
    }

    /* access modifiers changed from: private */
    public void composeWidgetLaunchAnimator(AnimatorSet animatorSet, LauncherAppWidgetHostView launcherAppWidgetHostView, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3) {
        this.mLauncher.getStateManager().setCurrentAnimation(animatorSet, new Animator[0]);
        animatorSet.play(getOpeningWindowAnimatorsForWidget(launcherAppWidgetHostView, remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, remoteAnimationTargetCompatArr3, getWindowTargetBounds(remoteAnimationTargetCompatArr, getRotationChange(remoteAnimationTargetCompatArr)), areAllTargetsTranslucent(remoteAnimationTargetCompatArr)));
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                QuickstepTransitionManager.this.mLauncher.addOnResumeCallback(new Runnable() {
                    public final void run() {
                        QuickstepTransitionManager.AnonymousClass5.this.lambda$onAnimationStart$0$QuickstepTransitionManager$5();
                    }
                });
            }

            public /* synthetic */ void lambda$onAnimationStart$0$QuickstepTransitionManager$5() {
                ObjectAnimator.ofFloat(QuickstepTransitionManager.this.mLauncher.getDepthController(), DepthController.DEPTH, new float[]{QuickstepTransitionManager.this.mLauncher.getStateManager().getState().getDepth(QuickstepTransitionManager.this.mLauncher)}).start();
            }
        });
    }

    private Rect getWindowTargetBounds(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, int i) {
        RemoteAnimationTargetCompat remoteAnimationTargetCompat;
        int length = remoteAnimationTargetCompatArr.length;
        int i2 = 0;
        while (true) {
            if (i2 >= length) {
                remoteAnimationTargetCompat = null;
                break;
            }
            remoteAnimationTargetCompat = remoteAnimationTargetCompatArr[i2];
            if (remoteAnimationTargetCompat.mode == 0) {
                break;
            }
            i2++;
        }
        if (remoteAnimationTargetCompat == null) {
            return new Rect(0, 0, this.mDeviceProfile.widthPx, this.mDeviceProfile.heightPx);
        }
        Rect rect = new Rect(remoteAnimationTargetCompat.screenSpaceBounds);
        if (remoteAnimationTargetCompat.localBounds != null) {
            rect.set(remoteAnimationTargetCompat.localBounds);
        } else {
            rect.offsetTo(remoteAnimationTargetCompat.position.x, remoteAnimationTargetCompat.position.y);
        }
        if (i != 0) {
            if (i % 2 == 1) {
                Utilities.rotateBounds(rect, this.mDeviceProfile.heightPx, this.mDeviceProfile.widthPx, 4 - i);
            } else {
                Utilities.rotateBounds(rect, this.mDeviceProfile.widthPx, this.mDeviceProfile.heightPx, 4 - i);
            }
        }
        if (this.mDeviceProfile.isTaskbarPresentInApps) {
            rect.bottom -= remoteAnimationTargetCompat.contentInsets.bottom;
        }
        return rect;
    }

    public void setRemoteAnimationProvider(RemoteAnimationProvider remoteAnimationProvider, CancellationSignal cancellationSignal) {
        this.mRemoteAnimationProvider = remoteAnimationProvider;
        cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener(remoteAnimationProvider) {
            public final /* synthetic */ RemoteAnimationProvider f$1;

            {
                this.f$1 = r2;
            }

            public final void onCancel() {
                QuickstepTransitionManager.this.lambda$setRemoteAnimationProvider$0$QuickstepTransitionManager(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$setRemoteAnimationProvider$0$QuickstepTransitionManager(RemoteAnimationProvider remoteAnimationProvider) {
        if (remoteAnimationProvider == this.mRemoteAnimationProvider) {
            this.mRemoteAnimationProvider = null;
        }
    }

    private Pair<AnimatorSet, Runnable> getLauncherContentAnimator(boolean z, int i, boolean z2) {
        float[] fArr;
        Object obj;
        int i2;
        AnimatorSet animatorSet = new AnimatorSet();
        if (z) {
            fArr = new float[]{1.0f, 0.0f};
        } else {
            fArr = new float[]{0.0f, 1.0f};
        }
        float[] fArr2 = z ? new float[]{1.0f, this.mContentScale} : new float[]{this.mContentScale, 1.0f};
        this.mLauncher.pauseExpensiveViewUpdates();
        if (this.mLauncher.isInState(LauncherState.ALL_APPS)) {
            final ActivityAllAppsContainerView<Launcher> appsView = this.mLauncher.getAppsView();
            float alpha = appsView.getAlpha();
            float floatValue = ((Float) LauncherAnimUtils.SCALE_PROPERTY.get(appsView)).floatValue();
            appsView.setAlpha(fArr[0]);
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(appsView, View.ALPHA, fArr);
            ofFloat.setDuration(217);
            ofFloat.setInterpolator(Interpolators.LINEAR);
            appsView.setLayerType(2, (Paint) null);
            ofFloat.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    appsView.setLayerType(0, (Paint) null);
                }
            });
            if (!z2) {
                LauncherAnimUtils.SCALE_PROPERTY.set(appsView, Float.valueOf(fArr2[0]));
                ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(appsView, LauncherAnimUtils.SCALE_PROPERTY, fArr2);
                ofFloat2.setInterpolator(Interpolators.AGGRESSIVE_EASE);
                ofFloat2.setDuration(350);
                animatorSet.play(ofFloat2);
            }
            animatorSet.play(ofFloat);
            obj = new Runnable(appsView, alpha, floatValue) {
                public final /* synthetic */ View f$0;
                public final /* synthetic */ float f$1;
                public final /* synthetic */ float f$2;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    QuickstepTransitionManager.lambda$getLauncherContentAnimator$1(this.f$0, this.f$1, this.f$2);
                }
            };
        } else if (this.mLauncher.isInState(LauncherState.OVERVIEW)) {
            obj = composeViewContentAnimator(animatorSet, fArr, fArr2);
        } else {
            ArrayList arrayList = new ArrayList();
            this.mLauncher.getWorkspace().forEachVisiblePage(new Consumer(arrayList) {
                public final /* synthetic */ List f$0;

                {
                    this.f$0 = r1;
                }

                public final void accept(Object obj) {
                    this.f$0.add(((CellLayout) ((View) obj)).getShortcutsAndWidgets());
                }
            });
            arrayList.add(this.mLauncher.getHotseat());
            arrayList.forEach(new Consumer(fArr2, animatorSet) {
                public final /* synthetic */ float[] f$0;
                public final /* synthetic */ AnimatorSet f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void accept(Object obj) {
                    QuickstepTransitionManager.lambda$getLauncherContentAnimator$3(this.f$0, this.f$1, (View) obj);
                }
            });
            boolean z3 = FeatureFlags.ENABLE_SCRIM_FOR_APP_LAUNCH.get();
            if (z3) {
                boolean z4 = this.mDeviceProfile.isTaskbarPresentInApps;
                if (z4) {
                    i2 = this.mLauncher.getResources().getColor(R.color.taskbar_background);
                } else {
                    i2 = Themes.getAttrColor(this.mLauncher, R.attr.overviewScrimColor);
                }
                int alphaComponent = ColorUtils.setAlphaComponent(i2, 0);
                int[] iArr = z ? new int[]{alphaComponent, i2} : new int[]{i2, alphaComponent};
                ScrimView scrimView = this.mLauncher.getScrimView();
                if (scrimView.getBackground() instanceof ColorDrawable) {
                    scrimView.setBackgroundColor(iArr[0]);
                    ObjectAnimator ofArgb = ObjectAnimator.ofArgb(scrimView, LauncherAnimUtils.VIEW_BACKGROUND_COLOR, iArr);
                    ofArgb.setDuration(350);
                    ofArgb.setInterpolator(Interpolators.DEACCEL_1_5);
                    if (z4) {
                        ofArgb.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationStart(Animator animator) {
                                LauncherTaskbarUIController taskbarUIController = QuickstepTransitionManager.this.mLauncher.getTaskbarUIController();
                                if (taskbarUIController != null) {
                                    taskbarUIController.forceHideBackground(true);
                                }
                            }

                            public void onAnimationEnd(Animator animator) {
                                LauncherTaskbarUIController taskbarUIController = QuickstepTransitionManager.this.mLauncher.getTaskbarUIController();
                                if (taskbarUIController != null) {
                                    taskbarUIController.forceHideBackground(false);
                                }
                            }
                        });
                    }
                    animatorSet.play(ofArgb);
                }
            }
            obj = new Runnable(arrayList, z3) {
                public final /* synthetic */ List f$1;
                public final /* synthetic */ boolean f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    QuickstepTransitionManager.this.lambda$getLauncherContentAnimator$5$QuickstepTransitionManager(this.f$1, this.f$2);
                }
            };
        }
        animatorSet.setStartDelay((long) i);
        return new Pair<>(animatorSet, obj);
    }

    static /* synthetic */ void lambda$getLauncherContentAnimator$1(View view, float f, float f2) {
        view.setAlpha(f);
        LauncherAnimUtils.SCALE_PROPERTY.set(view, Float.valueOf(f2));
        view.setLayerType(0, (Paint) null);
    }

    static /* synthetic */ void lambda$getLauncherContentAnimator$3(float[] fArr, AnimatorSet animatorSet, View view) {
        view.setLayerType(2, (Paint) null);
        ObjectAnimator duration = ObjectAnimator.ofFloat(view, LauncherAnimUtils.SCALE_PROPERTY, fArr).setDuration(350);
        duration.setInterpolator(Interpolators.DEACCEL_1_5);
        animatorSet.play(duration);
    }

    public /* synthetic */ void lambda$getLauncherContentAnimator$5$QuickstepTransitionManager(List list, boolean z) {
        list.forEach($$Lambda$QuickstepTransitionManager$SKSli0wdmdn7PzQ1oUt1m8btT9I.INSTANCE);
        if (z) {
            this.mLauncher.getScrimView().setBackgroundColor(0);
        }
        this.mLauncher.resumeExpensiveViewUpdates();
    }

    static /* synthetic */ void lambda$getLauncherContentAnimator$4(View view) {
        LauncherAnimUtils.SCALE_PROPERTY.set(view, Float.valueOf(1.0f));
        view.setLayerType(0, (Paint) null);
    }

    /* access modifiers changed from: protected */
    public Runnable composeViewContentAnimator(AnimatorSet animatorSet, float[] fArr, float[] fArr2) {
        final RecentsView recentsView = (RecentsView) this.mLauncher.getOverviewPanel();
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(recentsView, RecentsView.CONTENT_ALPHA, fArr);
        Log.d(TestProtocol.BAD_STATE, "QTM composeViewContentAnimator alphas=" + Arrays.toString(fArr));
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                Log.d(TestProtocol.BAD_STATE, "QTM composeViewContentAnimator onStart");
            }

            public void onAnimationCancel(Animator animator) {
                Log.d(TestProtocol.BAD_STATE, "QTM composeViewContentAnimator onCancel, alpha=" + (recentsView == null ? -1.0f : ((Float) RecentsView.CONTENT_ALPHA.get(recentsView)).floatValue()));
            }

            public void onAnimationEnd(Animator animator) {
                Log.d(TestProtocol.BAD_STATE, "QTM composeViewContentAnimator onEnd");
            }
        });
        ofFloat.setDuration(217);
        ofFloat.setInterpolator(Interpolators.LINEAR);
        animatorSet.play(ofFloat);
        Log.d(TestProtocol.BAD_STATE, "QTM composeViewContentAnimator setFreezeVisibility=true");
        recentsView.setFreezeViewVisibility(true);
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(recentsView, LauncherAnimUtils.SCALE_PROPERTY, fArr2);
        ofFloat2.setInterpolator(Interpolators.AGGRESSIVE_EASE);
        ofFloat2.setDuration(350);
        animatorSet.play(ofFloat2);
        return new Runnable(recentsView) {
            public final /* synthetic */ RecentsView f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                QuickstepTransitionManager.this.lambda$composeViewContentAnimator$6$QuickstepTransitionManager(this.f$1);
            }
        };
    }

    public /* synthetic */ void lambda$composeViewContentAnimator$6$QuickstepTransitionManager(RecentsView recentsView) {
        Log.d(TestProtocol.BAD_STATE, "QTM composeViewContentAnimator onEnd setFreezeVisibility=false");
        recentsView.setFreezeViewVisibility(false);
        LauncherAnimUtils.SCALE_PROPERTY.set(recentsView, Float.valueOf(1.0f));
        this.mLauncher.getStateManager().reapplyState();
    }

    private Animator getOpeningWindowAnimators(View view, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3, Rect rect, boolean z, int i) {
        boolean z2;
        float f;
        float f2;
        final View view2 = view;
        RectF rectF = new RectF();
        FloatingIconView floatingIconView = FloatingIconView.getFloatingIconView(this.mLauncher, view2, !z, rectF, true);
        Rect rect2 = new Rect();
        Matrix matrix = new Matrix();
        RemoteAnimationTargets remoteAnimationTargets = new RemoteAnimationTargets(remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, remoteAnimationTargetCompatArr3, 0);
        SurfaceTransactionApplier surfaceTransactionApplier = new SurfaceTransactionApplier(floatingIconView);
        remoteAnimationTargets.addReleaseCheck(surfaceTransactionApplier);
        RemoteAnimationTargetCompat navBarRemoteAnimationTarget = remoteAnimationTargets.getNavBarRemoteAnimationTarget();
        int[] iArr = new int[2];
        this.mDragLayer.getLocationOnScreen(iArr);
        if (supportsSSplashScreen()) {
            int firstAppTargetTaskId = remoteAnimationTargets.getFirstAppTargetTaskId();
            Pair create = Pair.create(0, 0);
            this.mTaskStartParams.remove(Integer.valueOf(firstAppTargetTaskId));
            z2 = ((Integer) this.mTaskStartParams.getOrDefault(Integer.valueOf(firstAppTargetTaskId), create).first).intValue() == 1;
        } else {
            z2 = false;
        }
        AnimOpenProperties animOpenProperties = r0;
        int[] iArr2 = iArr;
        RectF rectF2 = rectF;
        SurfaceTransactionApplier surfaceTransactionApplier2 = surfaceTransactionApplier;
        RemoteAnimationTargets remoteAnimationTargets2 = remoteAnimationTargets;
        AnimOpenProperties animOpenProperties2 = new AnimOpenProperties(this.mLauncher.getResources(), this.mDeviceProfile, rect, rectF, view, iArr[0], iArr[1], z2, floatingIconView.isDifferentFromAppIcon());
        int i2 = animOpenProperties.cropCenterXStart - (animOpenProperties.cropWidthStart / 2);
        int i3 = animOpenProperties.cropCenterYStart - (animOpenProperties.cropHeightStart / 2);
        rect2.set(i2, i3, animOpenProperties.cropWidthStart + i2, animOpenProperties.cropHeightStart + i3);
        RectF rectF3 = new RectF();
        RectF rectF4 = new RectF();
        Point point = new Point();
        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.setDuration(APP_LAUNCH_DURATION);
        ofFloat.setInterpolator(Interpolators.LINEAR);
        ofFloat.addListener(floatingIconView);
        final RemoteAnimationTargets remoteAnimationTargets3 = remoteAnimationTargets2;
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                LauncherTaskbarUIController taskbarUIController = QuickstepTransitionManager.this.mLauncher.getTaskbarUIController();
                if (taskbarUIController != null && taskbarUIController.shouldShowEdu()) {
                    Settings.Secure.putInt(QuickstepTransitionManager.this.mLauncher.getContentResolver(), "launcher_taskbar_education_showing", 1);
                }
            }

            public void onAnimationEnd(Animator animator) {
                View view = view2;
                if (view instanceof BubbleTextView) {
                    ((BubbleTextView) view).setStayPressed(false);
                }
                LauncherTaskbarUIController taskbarUIController = QuickstepTransitionManager.this.mLauncher.getTaskbarUIController();
                if (taskbarUIController != null) {
                    taskbarUIController.showEdu();
                }
                remoteAnimationTargets3.release();
            }
        });
        float max = QuickStepContract.supportsRoundedCornersOnWindows(this.mLauncher.getResources()) ? ((float) Math.max(rect2.width(), rect2.height())) / 2.0f : 0.0f;
        if (this.mDeviceProfile.isMultiWindowMode) {
            f = 0.0f;
        } else {
            f = QuickStepContract.getWindowCornerRadius(this.mLauncher);
        }
        if (z) {
            f2 = 0.0f;
        } else {
            f2 = this.mMaxShadowRadius;
        }
        AnimatorSet animatorSet2 = animatorSet;
        AnonymousClass10 r28 = r0;
        AnonymousClass10 r0 = new MultiValueUpdateListener(this, animOpenProperties, max, f, f2, rectF2, rect2, i, rectF4, iArr2, rectF3, floatingIconView, remoteAnimationTargetCompatArr, matrix, point, navBarRemoteAnimationTarget, surfaceTransactionApplier2) {
            MultiValueUpdateListener.FloatProp mCropRectCenterX;
            MultiValueUpdateListener.FloatProp mCropRectCenterY;
            MultiValueUpdateListener.FloatProp mCropRectHeight;
            MultiValueUpdateListener.FloatProp mCropRectWidth;
            MultiValueUpdateListener.FloatProp mDx;
            MultiValueUpdateListener.FloatProp mDy;
            MultiValueUpdateListener.FloatProp mIconAlpha;
            MultiValueUpdateListener.FloatProp mIconScaleToFitScreen;
            MultiValueUpdateListener.FloatProp mNavFadeIn = new MultiValueUpdateListener.FloatProp(0.0f, 1.0f, 234.0f, 266.0f, QuickstepTransitionManager.NAV_FADE_IN_INTERPOLATOR);
            MultiValueUpdateListener.FloatProp mNavFadeOut = new MultiValueUpdateListener.FloatProp(1.0f, 0.0f, 0.0f, 133.0f, QuickstepTransitionManager.NAV_FADE_OUT_INTERPOLATOR);
            MultiValueUpdateListener.FloatProp mShadowRadius;
            MultiValueUpdateListener.FloatProp mWindowRadius;
            final /* synthetic */ QuickstepTransitionManager this$0;
            final /* synthetic */ RemoteAnimationTargetCompat[] val$appTargets;
            final /* synthetic */ Rect val$crop;
            final /* synthetic */ int[] val$dragLayerBounds;
            final /* synthetic */ float val$finalShadowRadius;
            final /* synthetic */ float val$finalWindowRadius;
            final /* synthetic */ RectF val$floatingIconBounds;
            final /* synthetic */ FloatingIconView val$floatingView;
            final /* synthetic */ float val$initialWindowRadius;
            final /* synthetic */ RectF val$launcherIconBounds;
            final /* synthetic */ Matrix val$matrix;
            final /* synthetic */ RemoteAnimationTargetCompat val$navBarTarget;
            final /* synthetic */ AnimOpenProperties val$prop;
            final /* synthetic */ int val$rotationChange;
            final /* synthetic */ SurfaceTransactionApplier val$surfaceApplier;
            final /* synthetic */ Point val$tmpPos;
            final /* synthetic */ RectF val$tmpRectF;

            {
                AnimOpenProperties animOpenProperties = r14;
                this.this$0 = r13;
                this.val$prop = animOpenProperties;
                this.val$initialWindowRadius = r15;
                this.val$finalWindowRadius = r16;
                this.val$finalShadowRadius = r17;
                this.val$launcherIconBounds = r18;
                this.val$crop = r19;
                this.val$rotationChange = r20;
                this.val$tmpRectF = r21;
                this.val$dragLayerBounds = r22;
                this.val$floatingIconBounds = r23;
                this.val$floatingView = r24;
                this.val$appTargets = r25;
                this.val$matrix = r26;
                this.val$tmpPos = r27;
                this.val$navBarTarget = r28;
                this.val$surfaceApplier = r29;
                this.mDx = new MultiValueUpdateListener.FloatProp(0.0f, animOpenProperties.dX, 0.0f, 500.0f, r13.mOpeningXInterpolator);
                this.mDy = new MultiValueUpdateListener.FloatProp(0.0f, animOpenProperties.dY, 0.0f, 500.0f, r13.mOpeningInterpolator);
                this.mIconScaleToFitScreen = new MultiValueUpdateListener.FloatProp(animOpenProperties.initialAppIconScale, animOpenProperties.finalAppIconScale, 0.0f, 500.0f, r13.mOpeningInterpolator);
                this.mIconAlpha = new MultiValueUpdateListener.FloatProp(animOpenProperties.iconAlphaStart, 0.0f, 25.0f, 50.0f, Interpolators.LINEAR);
                this.mWindowRadius = new MultiValueUpdateListener.FloatProp(r15, r16, 0.0f, 500.0f, r13.mOpeningInterpolator);
                this.mShadowRadius = new MultiValueUpdateListener.FloatProp(0.0f, r17, 0.0f, 500.0f, r13.mOpeningInterpolator);
                this.mCropRectCenterX = new MultiValueUpdateListener.FloatProp((float) animOpenProperties.cropCenterXStart, (float) animOpenProperties.cropCenterXEnd, 0.0f, 500.0f, r13.mOpeningInterpolator);
                this.mCropRectCenterY = new MultiValueUpdateListener.FloatProp((float) animOpenProperties.cropCenterYStart, (float) animOpenProperties.cropCenterYEnd, 0.0f, 500.0f, r13.mOpeningInterpolator);
                this.mCropRectWidth = new MultiValueUpdateListener.FloatProp((float) animOpenProperties.cropWidthStart, (float) animOpenProperties.cropWidthEnd, 0.0f, 500.0f, r13.mOpeningInterpolator);
                this.mCropRectHeight = new MultiValueUpdateListener.FloatProp((float) animOpenProperties.cropHeightStart, (float) animOpenProperties.cropHeightEnd, 0.0f, 500.0f, r13.mOpeningInterpolator);
            }

            public void onUpdate(float f, boolean z) {
                boolean z2;
                float width = this.val$launcherIconBounds.width() * this.mIconScaleToFitScreen.value;
                float height = this.val$launcherIconBounds.height() * this.mIconScaleToFitScreen.value;
                int i = (int) (this.mCropRectCenterX.value - (this.mCropRectWidth.value / 2.0f));
                int i2 = (int) (this.mCropRectCenterY.value - (this.mCropRectHeight.value / 2.0f));
                this.val$crop.set(i, i2, (int) (((float) i) + this.mCropRectWidth.value), (int) (((float) i2) + this.mCropRectHeight.value));
                int width2 = this.val$crop.width();
                int height2 = this.val$crop.height();
                if (this.val$rotationChange != 0) {
                    Utilities.rotateBounds(this.val$crop, this.this$0.mDeviceProfile.widthPx, this.this$0.mDeviceProfile.heightPx, this.val$rotationChange);
                }
                float f2 = (float) width2;
                float f3 = (float) height2;
                float min = Math.min(1.0f, Math.max(width / f2, height / f3));
                float f4 = f2 * min;
                float f5 = f3 * min;
                float f6 = (f4 - width) / 2.0f;
                float f7 = (f5 - height) / 2.0f;
                this.val$tmpRectF.set(this.val$launcherIconBounds);
                RectF rectF = this.val$tmpRectF;
                int[] iArr = this.val$dragLayerBounds;
                rectF.offset((float) iArr[0], (float) iArr[1]);
                this.val$tmpRectF.offset(this.mDx.value, this.mDy.value);
                Utilities.scaleRectFAboutCenter(this.val$tmpRectF, this.mIconScaleToFitScreen.value);
                float f8 = (this.val$tmpRectF.left - f6) - (((float) this.val$crop.left) * min);
                float f9 = (this.val$tmpRectF.top - f7) - (((float) this.val$crop.top) * min);
                this.val$floatingIconBounds.set(this.val$launcherIconBounds);
                this.val$floatingIconBounds.offset(this.mDx.value, this.mDy.value);
                Utilities.scaleRectFAboutCenter(this.val$floatingIconBounds, this.mIconScaleToFitScreen.value);
                this.val$floatingIconBounds.left -= f6;
                this.val$floatingIconBounds.top -= f7;
                this.val$floatingIconBounds.right += f6;
                this.val$floatingIconBounds.bottom += f7;
                if (z) {
                    this.val$floatingView.update(1.0f, 255, this.val$floatingIconBounds, f, 0.0f, this.mWindowRadius.value * min, true);
                    return;
                }
                ArrayList arrayList = new ArrayList();
                int length = this.val$appTargets.length - 1;
                while (length >= 0) {
                    RemoteAnimationTargetCompat remoteAnimationTargetCompat = this.val$appTargets[length];
                    SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder = new SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder(remoteAnimationTargetCompat.leash);
                    if (remoteAnimationTargetCompat.mode == 0) {
                        this.val$matrix.setScale(min, min);
                        int i3 = this.val$rotationChange;
                        if (i3 == 1) {
                            this.val$matrix.postTranslate(f9, ((float) this.this$0.mDeviceProfile.widthPx) - (f8 + f4));
                        } else if (i3 == 2) {
                            this.val$matrix.postTranslate(((float) this.this$0.mDeviceProfile.widthPx) - (f8 + f4), ((float) this.this$0.mDeviceProfile.heightPx) - (f9 + f5));
                        } else if (i3 == 3) {
                            this.val$matrix.postTranslate(((float) this.this$0.mDeviceProfile.heightPx) - (f9 + f5), f8);
                        } else {
                            this.val$matrix.postTranslate(f8, f9);
                        }
                        this.val$floatingView.update(this.mIconAlpha.value, 255, this.val$floatingIconBounds, f, 0.0f, this.mWindowRadius.value * min, true);
                        builder.withMatrix(this.val$matrix).withWindowCrop(this.val$crop).withAlpha(1.0f - this.mIconAlpha.value).withCornerRadius(this.mWindowRadius.value).withShadowRadius(this.mShadowRadius.value);
                    } else if (remoteAnimationTargetCompat.mode == 1) {
                        if (remoteAnimationTargetCompat.localBounds != null) {
                            Rect rect = remoteAnimationTargetCompat.localBounds;
                            this.val$tmpPos.set(remoteAnimationTargetCompat.localBounds.left, remoteAnimationTargetCompat.localBounds.top);
                        } else {
                            this.val$tmpPos.set(remoteAnimationTargetCompat.position.x, remoteAnimationTargetCompat.position.y);
                        }
                        Rect rect2 = new Rect(remoteAnimationTargetCompat.screenSpaceBounds);
                        z2 = false;
                        rect2.offsetTo(0, 0);
                        if (this.val$rotationChange % 2 == 1) {
                            int i4 = rect2.right;
                            rect2.right = rect2.bottom;
                            rect2.bottom = i4;
                            int i5 = this.val$tmpPos.x;
                            Point point = this.val$tmpPos;
                            point.x = point.y;
                            this.val$tmpPos.y = i5;
                        }
                        this.val$matrix.setTranslate((float) this.val$tmpPos.x, (float) this.val$tmpPos.y);
                        builder.withMatrix(this.val$matrix).withWindowCrop(rect2).withAlpha(1.0f);
                        arrayList.add(builder.build());
                        length--;
                        boolean z3 = z2;
                    }
                    z2 = false;
                    arrayList.add(builder.build());
                    length--;
                    boolean z32 = z2;
                }
                if (this.val$navBarTarget != null) {
                    SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder2 = new SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder(this.val$navBarTarget.leash);
                    if (this.mNavFadeIn.value > this.mNavFadeIn.getStartValue()) {
                        this.val$matrix.setScale(min, min);
                        this.val$matrix.postTranslate(f8, f9);
                        builder2.withMatrix(this.val$matrix).withWindowCrop(this.val$crop).withAlpha(this.mNavFadeIn.value);
                    } else {
                        builder2.withAlpha(this.mNavFadeOut.value);
                    }
                    arrayList.add(builder2.build());
                }
                this.val$surfaceApplier.scheduleApply((SyncRtSurfaceTransactionApplierCompat.SurfaceParams[]) arrayList.toArray(new SyncRtSurfaceTransactionApplierCompat.SurfaceParams[arrayList.size()]));
            }
        };
        ValueAnimator valueAnimator = ofFloat;
        AnonymousClass10 r1 = r28;
        valueAnimator.addUpdateListener(r1);
        r1.onUpdate(0.0f, true);
        if (z) {
            AnimatorSet animatorSet3 = animatorSet2;
            animatorSet3.play(valueAnimator);
            return animatorSet3;
        }
        AnimatorSet animatorSet4 = animatorSet2;
        animatorSet4.playTogether(new Animator[]{valueAnimator, getBackgroundAnimator()});
        return animatorSet4;
    }

    private Animator getOpeningWindowAnimatorsForWidget(LauncherAppWidgetHostView launcherAppWidgetHostView, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3, Rect rect, boolean z) {
        int i;
        float f;
        RectF rectF = new RectF();
        Rect rect2 = new Rect();
        Matrix matrix = new Matrix();
        final RemoteAnimationTargets remoteAnimationTargets = new RemoteAnimationTargets(remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, remoteAnimationTargetCompatArr3, 0);
        RemoteAnimationTargetCompat firstAppTarget = remoteAnimationTargets.getFirstAppTarget();
        if (firstAppTarget == null || !supportsSSplashScreen()) {
            i = 0;
        } else {
            i = this.mTaskStartParams.containsKey(Integer.valueOf(firstAppTarget.taskId)) ? ((Integer) this.mTaskStartParams.get(Integer.valueOf(firstAppTarget.taskId)).second).intValue() : 0;
            this.mTaskStartParams.remove(Integer.valueOf(firstAppTarget.taskId));
        }
        int defaultBackgroundColor = i == 0 ? FloatingWidgetView.getDefaultBackgroundColor(this.mLauncher, firstAppTarget) : i;
        if (this.mDeviceProfile.isMultiWindowMode) {
            f = 0.0f;
        } else {
            f = QuickStepContract.getWindowCornerRadius(this.mLauncher);
        }
        FloatingWidgetView floatingWidgetView = FloatingWidgetView.getFloatingWidgetView(this.mLauncher, launcherAppWidgetHostView, rectF, new Size(rect.width(), rect.height()), f, z, defaultBackgroundColor);
        float initialCornerRadius = QuickStepContract.supportsRoundedCornersOnWindows(this.mLauncher.getResources()) ? floatingWidgetView.getInitialCornerRadius() : 0.0f;
        SurfaceTransactionApplier surfaceTransactionApplier = new SurfaceTransactionApplier(floatingWidgetView);
        remoteAnimationTargets.addReleaseCheck(surfaceTransactionApplier);
        RemoteAnimationTargetCompat navBarRemoteAnimationTarget = remoteAnimationTargets.getNavBarRemoteAnimationTarget();
        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.setDuration(APP_LAUNCH_DURATION);
        ofFloat.setInterpolator(Interpolators.LINEAR);
        ofFloat.addListener(floatingWidgetView);
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                remoteAnimationTargets.release();
            }
        });
        Objects.requireNonNull(animatorSet);
        floatingWidgetView.setFastFinishRunnable(new Runnable(animatorSet) {
            public final /* synthetic */ AnimatorSet f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                this.f$0.end();
            }
        });
        ValueAnimator valueAnimator = ofFloat;
        AnimatorSet animatorSet2 = animatorSet;
        AnonymousClass12 r13 = r0;
        AnonymousClass12 r0 = new MultiValueUpdateListener(this, initialCornerRadius, f, rectF, rect, rect2, matrix, z, remoteAnimationTargetCompatArr, floatingWidgetView, navBarRemoteAnimationTarget, surfaceTransactionApplier) {
            float mAppWindowScale = 1.0f;
            final MultiValueUpdateListener.FloatProp mCornerRadiusProgress;
            final MultiValueUpdateListener.FloatProp mDx;
            final MultiValueUpdateListener.FloatProp mDy;
            final MultiValueUpdateListener.FloatProp mHeight;
            final MultiValueUpdateListener.FloatProp mNavFadeIn;
            final MultiValueUpdateListener.FloatProp mNavFadeOut;
            final MultiValueUpdateListener.FloatProp mPreviewAlpha;
            final MultiValueUpdateListener.FloatProp mWidgetFallbackBackgroundAlpha;
            final MultiValueUpdateListener.FloatProp mWidgetForegroundAlpha;
            final MultiValueUpdateListener.FloatProp mWidth;
            final MultiValueUpdateListener.FloatProp mWindowRadius;
            final /* synthetic */ QuickstepTransitionManager this$0;
            final /* synthetic */ RemoteAnimationTargetCompat[] val$appTargets;
            final /* synthetic */ boolean val$appTargetsAreTranslucent;
            final /* synthetic */ Rect val$appWindowCrop;
            final /* synthetic */ float val$finalWindowRadius;
            final /* synthetic */ FloatingWidgetView val$floatingView;
            final /* synthetic */ float val$initialWindowRadius;
            final /* synthetic */ Matrix val$matrix;
            final /* synthetic */ RemoteAnimationTargetCompat val$navBarTarget;
            final /* synthetic */ SurfaceTransactionApplier val$surfaceApplier;
            final /* synthetic */ RectF val$widgetBackgroundBounds;
            final /* synthetic */ Rect val$windowTargetBounds;

            {
                this.this$0 = r13;
                this.val$initialWindowRadius = r14;
                this.val$finalWindowRadius = r15;
                this.val$widgetBackgroundBounds = r16;
                this.val$windowTargetBounds = r17;
                this.val$appWindowCrop = r18;
                this.val$matrix = r19;
                this.val$appTargetsAreTranslucent = r20;
                this.val$appTargets = r21;
                this.val$floatingView = r22;
                this.val$navBarTarget = r23;
                this.val$surfaceApplier = r24;
                this.mWidgetForegroundAlpha = new MultiValueUpdateListener.FloatProp(1.0f, 0.0f, 0.0f, 62.0f, Interpolators.LINEAR);
                this.mWidgetFallbackBackgroundAlpha = new MultiValueUpdateListener.FloatProp(0.0f, 1.0f, 0.0f, 75.0f, Interpolators.LINEAR);
                this.mPreviewAlpha = new MultiValueUpdateListener.FloatProp(0.0f, 1.0f, 62.0f, 62.0f, Interpolators.LINEAR);
                this.mWindowRadius = new MultiValueUpdateListener.FloatProp(r14, r15, 0.0f, 500.0f, r13.mOpeningInterpolator);
                this.mCornerRadiusProgress = new MultiValueUpdateListener.FloatProp(0.0f, 1.0f, 0.0f, 500.0f, r13.mOpeningInterpolator);
                this.mDx = new MultiValueUpdateListener.FloatProp(r16.centerX(), (float) r17.centerX(), 0.0f, 500.0f, r13.mOpeningXInterpolator);
                this.mDy = new MultiValueUpdateListener.FloatProp(r16.centerY(), (float) r17.centerY(), 0.0f, 500.0f, r13.mOpeningInterpolator);
                this.mWidth = new MultiValueUpdateListener.FloatProp(r16.width(), (float) r17.width(), 0.0f, 500.0f, r13.mOpeningInterpolator);
                this.mHeight = new MultiValueUpdateListener.FloatProp(r16.height(), (float) r17.height(), 0.0f, 500.0f, r13.mOpeningInterpolator);
                this.mNavFadeOut = new MultiValueUpdateListener.FloatProp(1.0f, 0.0f, 0.0f, 133.0f, QuickstepTransitionManager.NAV_FADE_OUT_INTERPOLATOR);
                this.mNavFadeIn = new MultiValueUpdateListener.FloatProp(0.0f, 1.0f, 234.0f, 266.0f, QuickstepTransitionManager.NAV_FADE_IN_INTERPOLATOR);
            }

            public void onUpdate(float f, boolean z) {
                this.val$widgetBackgroundBounds.set(this.mDx.value - (this.mWidth.value / 2.0f), this.mDy.value - (this.mHeight.value / 2.0f), this.mDx.value + (this.mWidth.value / 2.0f), this.mDy.value + (this.mHeight.value / 2.0f));
                this.mAppWindowScale = this.val$widgetBackgroundBounds.width() / ((float) this.val$windowTargetBounds.width());
                this.val$appWindowCrop.set(0, 0, Math.round((float) this.val$windowTargetBounds.width()), Math.round(this.val$widgetBackgroundBounds.height() / this.mAppWindowScale));
                this.val$matrix.setTranslate(this.val$widgetBackgroundBounds.left, this.val$widgetBackgroundBounds.top);
                Matrix matrix = this.val$matrix;
                float f2 = this.mAppWindowScale;
                matrix.postScale(f2, f2, this.val$widgetBackgroundBounds.left, this.val$widgetBackgroundBounds.top);
                ArrayList arrayList = new ArrayList();
                float f3 = 1.0f;
                if (this.val$appTargetsAreTranslucent) {
                    f3 = 1.0f - this.mPreviewAlpha.value;
                }
                for (int length = this.val$appTargets.length - 1; length >= 0; length--) {
                    RemoteAnimationTargetCompat remoteAnimationTargetCompat = this.val$appTargets[length];
                    SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder = new SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder(remoteAnimationTargetCompat.leash);
                    if (remoteAnimationTargetCompat.mode == 0) {
                        this.val$floatingView.update(this.val$widgetBackgroundBounds, f3, this.mWidgetForegroundAlpha.value, this.mWidgetFallbackBackgroundAlpha.value, this.mCornerRadiusProgress.value);
                        builder.withMatrix(this.val$matrix).withWindowCrop(this.val$appWindowCrop).withAlpha(this.mPreviewAlpha.value).withCornerRadius(this.mWindowRadius.value / this.mAppWindowScale);
                    }
                    arrayList.add(builder.build());
                }
                if (this.val$navBarTarget != null) {
                    SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder2 = new SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder(this.val$navBarTarget.leash);
                    if (this.mNavFadeIn.value > this.mNavFadeIn.getStartValue()) {
                        builder2.withMatrix(this.val$matrix).withWindowCrop(this.val$appWindowCrop).withAlpha(this.mNavFadeIn.value);
                    } else {
                        builder2.withAlpha(this.mNavFadeOut.value);
                    }
                    arrayList.add(builder2.build());
                }
                this.val$surfaceApplier.scheduleApply((SyncRtSurfaceTransactionApplierCompat.SurfaceParams[]) arrayList.toArray(new SyncRtSurfaceTransactionApplierCompat.SurfaceParams[arrayList.size()]));
            }
        };
        valueAnimator.addUpdateListener(r13);
        if (z) {
            animatorSet2.play(valueAnimator);
        } else {
            animatorSet2.playTogether(new Animator[]{valueAnimator, getBackgroundAnimator()});
        }
        return animatorSet2;
    }

    private ObjectAnimator getBackgroundAnimator() {
        boolean z = this.mLauncher.getStateManager().getState() != LauncherState.OVERVIEW;
        final DepthController depthController = this.mLauncher.getDepthController();
        ObjectAnimator duration = ObjectAnimator.ofFloat(depthController, DepthController.DEPTH, new float[]{LauncherState.BACKGROUND_APP.getDepth(this.mLauncher)}).setDuration(APP_LAUNCH_DURATION);
        if (z) {
            final SurfaceControl surfaceControl = null;
            if (BlurUtils.supportsBlursOnWindows()) {
                ViewRootImpl viewRootImpl = this.mLauncher.getDragLayer().getViewRootImpl();
                if (viewRootImpl != null) {
                    surfaceControl = viewRootImpl.getSurfaceControl();
                }
                surfaceControl = new SurfaceControl.Builder().setName("Blur layer").setParent(surfaceControl).setOpaque(false).setHidden(false).setEffectLayer().build();
            }
            depthController.setSurface(surfaceControl);
            duration.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animator) {
                    depthController.setIsInLaunchTransition(true);
                }

                public void onAnimationEnd(Animator animator) {
                    depthController.setIsInLaunchTransition(false);
                    depthController.setSurface((SurfaceControl) null);
                    if (surfaceControl != null) {
                        new SurfaceControl.Transaction().remove(surfaceControl).apply();
                    }
                }
            });
        }
        return duration;
    }

    public void registerRemoteAnimations() {
        if (!FeatureFlags.SEPARATE_RECENTS_ACTIVITY.get() && hasControlRemoteAppTransitionPermission()) {
            this.mWallpaperOpenRunner = createWallpaperOpenRunner(false);
            RemoteAnimationDefinitionCompat remoteAnimationDefinitionCompat = new RemoteAnimationDefinitionCompat();
            remoteAnimationDefinitionCompat.addRemoteAnimation(13, 1, new RemoteAnimationAdapterCompat(new LauncherAnimationRunner(this.mHandler, this.mWallpaperOpenRunner, false), 250, 0, this.mLauncher.getIApplicationThread()));
            if (FeatureFlags.KEYGUARD_ANIMATION.get()) {
                this.mKeyguardGoingAwayRunner = createWallpaperOpenRunner(true);
                remoteAnimationDefinitionCompat.addRemoteAnimation(21, new RemoteAnimationAdapterCompat(new LauncherAnimationRunner(this.mHandler, this.mKeyguardGoingAwayRunner, true), 250, 0, this.mLauncher.getIApplicationThread()));
            }
            new ActivityCompat(this.mLauncher).registerRemoteAnimations(remoteAnimationDefinitionCompat);
        }
    }

    public void registerRemoteTransitions() {
        if (!FeatureFlags.SEPARATE_RECENTS_ACTIVITY.get()) {
            if (hasControlRemoteAppTransitionPermission()) {
                this.mWallpaperOpenTransitionRunner = createWallpaperOpenRunner(false);
                RemoteTransitionCompat buildRemoteTransition = RemoteAnimationAdapterCompat.buildRemoteTransition(new LauncherAnimationRunner(this.mHandler, this.mWallpaperOpenTransitionRunner, false), this.mLauncher.getIApplicationThread());
                this.mLauncherOpenTransition = buildRemoteTransition;
                buildRemoteTransition.addHomeOpenCheck(this.mLauncher.getComponentName());
                SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mLauncher).registerRemoteTransition(this.mLauncherOpenTransition);
            }
            LauncherBackAnimationController launcherBackAnimationController = this.mBackAnimationController;
            if (launcherBackAnimationController != null) {
                launcherBackAnimationController.registerBackCallbacks(this.mHandler);
            }
        }
    }

    public void onActivityDestroyed() {
        unregisterRemoteAnimations();
        unregisterRemoteTransitions();
        this.mStartingWindowListener.setTransitionManager((QuickstepTransitionManager) null);
        SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mLauncher).setStartingWindowListener((IStartingWindowListener) null);
    }

    private void unregisterRemoteAnimations() {
        if (!FeatureFlags.SEPARATE_RECENTS_ACTIVITY.get() && hasControlRemoteAppTransitionPermission()) {
            new ActivityCompat(this.mLauncher).unregisterRemoteAnimations();
            this.mWallpaperOpenRunner = null;
            this.mAppLaunchRunner = null;
            this.mKeyguardGoingAwayRunner = null;
        }
    }

    private void unregisterRemoteTransitions() {
        if (!FeatureFlags.SEPARATE_RECENTS_ACTIVITY.get()) {
            if (hasControlRemoteAppTransitionPermission()) {
                if (this.mLauncherOpenTransition != null) {
                    SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mLauncher).unregisterRemoteTransition(this.mLauncherOpenTransition);
                    this.mLauncherOpenTransition = null;
                    this.mWallpaperOpenTransitionRunner = null;
                } else {
                    return;
                }
            }
            LauncherBackAnimationController launcherBackAnimationController = this.mBackAnimationController;
            if (launcherBackAnimationController != null) {
                launcherBackAnimationController.unregisterBackCallbacks();
                this.mBackAnimationController = null;
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean launcherIsATargetWithMode(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, int i) {
        for (RemoteAnimationTargetCompat remoteAnimationTargetCompat : remoteAnimationTargetCompatArr) {
            if (remoteAnimationTargetCompat.mode == i && remoteAnimationTargetCompat.taskInfo != null && remoteAnimationTargetCompat.taskInfo.topActivity != null && remoteAnimationTargetCompat.taskInfo.topActivity.equals(this.mLauncher.getComponentName())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasMultipleTargetsWithMode(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, int i) {
        int i2 = 0;
        for (RemoteAnimationTargetCompat remoteAnimationTargetCompat : remoteAnimationTargetCompatArr) {
            if (remoteAnimationTargetCompat.mode == i) {
                i2++;
            }
            if (i2 > 1) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public LauncherAnimationRunner.RemoteAnimationFactory createWallpaperOpenRunner(boolean z) {
        return new WallpaperOpenLauncherAnimationRunner(this.mHandler, z);
    }

    private Animator getUnlockWindowAnimator(final RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2) {
        final float f;
        final SurfaceTransactionApplier surfaceTransactionApplier = new SurfaceTransactionApplier(this.mDragLayer);
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.setDuration(250);
        if (this.mDeviceProfile.isMultiWindowMode) {
            f = 0.0f;
        } else {
            f = QuickStepContract.getWindowCornerRadius(this.mLauncher);
        }
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr = remoteAnimationTargetCompatArr;
                SyncRtSurfaceTransactionApplierCompat.SurfaceParams[] surfaceParamsArr = new SyncRtSurfaceTransactionApplierCompat.SurfaceParams[remoteAnimationTargetCompatArr.length];
                for (int length = remoteAnimationTargetCompatArr.length - 1; length >= 0; length--) {
                    RemoteAnimationTargetCompat remoteAnimationTargetCompat = remoteAnimationTargetCompatArr[length];
                    surfaceParamsArr[length] = new SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder(remoteAnimationTargetCompat.leash).withAlpha(1.0f).withWindowCrop(remoteAnimationTargetCompat.screenSpaceBounds).withCornerRadius(f).build();
                }
                surfaceTransactionApplier.scheduleApply(surfaceParamsArr);
            }
        });
        return ofFloat;
    }

    private static int getRotationChange(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr) {
        int i = 0;
        for (RemoteAnimationTargetCompat remoteAnimationTargetCompat : remoteAnimationTargetCompatArr) {
            if (Math.abs(remoteAnimationTargetCompat.rotationChange) > Math.abs(i)) {
                i = remoteAnimationTargetCompat.rotationChange;
            }
        }
        return i;
    }

    private View findLauncherView(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr) {
        View findLauncherView;
        for (RemoteAnimationTargetCompat remoteAnimationTargetCompat : remoteAnimationTargetCompatArr) {
            if (remoteAnimationTargetCompat.mode == 1 && (findLauncherView = findLauncherView(remoteAnimationTargetCompat)) != null) {
                return findLauncherView;
            }
        }
        return null;
    }

    private View findLauncherView(RemoteAnimationTargetCompat remoteAnimationTargetCompat) {
        String str;
        ArrayList arrayList;
        if (remoteAnimationTargetCompat == null || remoteAnimationTargetCompat.taskInfo == null) {
            return null;
        }
        int i = 0;
        ComponentName[] componentNameArr = {remoteAnimationTargetCompat.taskInfo.baseActivity, remoteAnimationTargetCompat.taskInfo.origActivity, remoteAnimationTargetCompat.taskInfo.realActivity, remoteAnimationTargetCompat.taskInfo.topActivity};
        while (true) {
            if (i < 4) {
                ComponentName componentName = componentNameArr[i];
                if (componentName != null && componentName.getPackageName() != null) {
                    str = componentName.getPackageName();
                    break;
                }
                i++;
            } else {
                str = null;
                break;
            }
        }
        if (str == null) {
            return null;
        }
        if (remoteAnimationTargetCompat.taskInfo.launchCookies == null) {
            arrayList = new ArrayList();
        } else {
            arrayList = remoteAnimationTargetCompat.taskInfo.launchCookies;
        }
        int i2 = Integer.MIN_VALUE;
        Iterator it = arrayList.iterator();
        while (true) {
            if (it.hasNext()) {
                Integer num = (Integer) ObjectWrapper.unwrap((IBinder) it.next());
                if (num != null) {
                    i2 = num.intValue();
                    break;
                }
            } else {
                break;
            }
        }
        return this.mLauncher.getFirstMatchForAppClose(i2, str, UserHandle.of(remoteAnimationTargetCompat.taskInfo.userId), true);
    }

    private RectF getDefaultWindowTargetRect() {
        PagedOrientationHandler pagedOrientationHandler = ((RecentsView) this.mLauncher.getOverviewPanel()).getPagedOrientationHandler();
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        float primaryValue = ((float) pagedOrientationHandler.getPrimaryValue(deviceProfile.availableWidthPx, deviceProfile.availableHeightPx)) / 2.0f;
        float secondaryValue = ((float) pagedOrientationHandler.getSecondaryValue(deviceProfile.availableWidthPx, deviceProfile.availableHeightPx)) - ((float) deviceProfile.hotseatBarSizePx);
        float f = (float) (deviceProfile.iconSizePx / 2);
        return new RectF(primaryValue - f, secondaryValue - f, primaryValue + f, secondaryValue + f);
    }

    private RectFSpringAnim getClosingWindowAnimators(AnimatorSet animatorSet, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, View view, PointF pointF, RectF rectF, float f) {
        FloatingWidgetView floatingWidgetView;
        boolean z;
        RemoteAnimationTargetCompat remoteAnimationTargetCompat;
        final FloatingIconView floatingIconView;
        RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2 = remoteAnimationTargetCompatArr;
        View view2 = view;
        RectF rectF2 = new RectF();
        int length = remoteAnimationTargetCompatArr2.length;
        int i = 0;
        while (true) {
            floatingWidgetView = null;
            if (i >= length) {
                z = false;
                remoteAnimationTargetCompat = null;
                break;
            }
            remoteAnimationTargetCompat = remoteAnimationTargetCompatArr2[i];
            if (remoteAnimationTargetCompat.mode == 1) {
                z = remoteAnimationTargetCompat.isTranslucent;
                break;
            }
            i++;
        }
        float f2 = 0.0f;
        if (view2 instanceof LauncherAppWidgetHostView) {
            floatingIconView = null;
            floatingWidgetView = FloatingWidgetView.getFloatingWidgetView(this.mLauncher, (LauncherAppWidgetHostView) view2, rectF2, new Size(this.mDeviceProfile.availableWidthPx, this.mDeviceProfile.availableHeightPx), this.mDeviceProfile.isMultiWindowMode ? 0.0f : QuickStepContract.getWindowCornerRadius(this.mLauncher), z, FloatingWidgetView.getDefaultBackgroundColor(this.mLauncher, remoteAnimationTargetCompat));
        } else if (view2 != null) {
            floatingIconView = FloatingIconView.getFloatingIconView(this.mLauncher, view2, true, rectF2, false);
        } else {
            rectF2.set(getDefaultWindowTargetRect());
            floatingIconView = null;
        }
        final RectFSpringAnim rectFSpringAnim = new RectFSpringAnim(rectF, rectF2, this.mLauncher, this.mDeviceProfile);
        Rect windowTargetBounds = getWindowTargetBounds(remoteAnimationTargetCompatArr2, getRotationChange(remoteAnimationTargetCompatArr));
        if (floatingIconView != null) {
            rectFSpringAnim.addAnimatorListener(floatingIconView);
            Objects.requireNonNull(rectFSpringAnim);
            floatingIconView.setOnTargetChangeListener(new Runnable() {
                public final void run() {
                    RectFSpringAnim.this.onTargetPositionChanged();
                }
            });
            Objects.requireNonNull(rectFSpringAnim);
            floatingIconView.setFastFinishRunnable(new Runnable() {
                public final void run() {
                    RectFSpringAnim.this.end();
                }
            });
            rectFSpringAnim.addOnUpdateListener(new SpringAnimRunner(remoteAnimationTargetCompatArr, rectF2, windowTargetBounds, f) {
                public void onUpdate(RectF rectF, float f) {
                    floatingIconView.update(1.0f, 255, rectF, f, 0.9f, getCornerRadius(f), false);
                    super.onUpdate(rectF, f);
                }
            });
        } else if (floatingWidgetView != null) {
            rectFSpringAnim.addAnimatorListener(floatingWidgetView);
            Objects.requireNonNull(rectFSpringAnim);
            floatingWidgetView.setOnTargetChangeListener(new Runnable() {
                public final void run() {
                    RectFSpringAnim.this.onTargetPositionChanged();
                }
            });
            Objects.requireNonNull(rectFSpringAnim);
            floatingWidgetView.setFastFinishRunnable(new Runnable() {
                public final void run() {
                    RectFSpringAnim.this.end();
                }
            });
            if (!z) {
                f2 = 1.0f;
            }
            final FloatingWidgetView floatingWidgetView2 = floatingWidgetView;
            final float f3 = f2;
            rectFSpringAnim.addOnUpdateListener(new SpringAnimRunner(remoteAnimationTargetCompatArr, rectF2, windowTargetBounds, f) {
                public void onUpdate(RectF rectF, float f) {
                    float mapBoundToRange = Utilities.mapBoundToRange(f, 0.5f, 1.0f, 0.0f, 1.0f, Interpolators.EXAGGERATED_EASE);
                    FloatingWidgetView floatingWidgetView = floatingWidgetView2;
                    float f2 = f3;
                    RectF rectF2 = rectF;
                    floatingWidgetView.update(rectF2, f2, mapBoundToRange, 1.0f - Utilities.mapBoundToRange(f, 0.8f, 1.0f, 0.0f, 1.0f, Interpolators.EXAGGERATED_EASE), 1.0f - f);
                    super.onUpdate(rectF, f);
                }
            });
        } else {
            rectFSpringAnim.addOnUpdateListener(new SpringAnimRunner(remoteAnimationTargetCompatArr, rectF2, windowTargetBounds, f));
        }
        final PointF pointF2 = pointF;
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                rectFSpringAnim.start(QuickstepTransitionManager.this.mLauncher, pointF2);
            }
        });
        return rectFSpringAnim;
    }

    /* access modifiers changed from: private */
    public Animator getFallbackClosingWindowAnimators(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr) {
        float f;
        int rotationChange = getRotationChange(remoteAnimationTargetCompatArr);
        SurfaceTransactionApplier surfaceTransactionApplier = new SurfaceTransactionApplier(this.mDragLayer);
        Matrix matrix = new Matrix();
        Point point = new Point();
        Rect rect = new Rect();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        if (this.mDeviceProfile.isMultiWindowMode) {
            f = 0.0f;
        } else {
            f = QuickStepContract.getWindowCornerRadius(this.mLauncher);
        }
        float f2 = areAllTargetsTranslucent(remoteAnimationTargetCompatArr) ? 0.0f : this.mMaxShadowRadius;
        ofFloat.setDuration((long) 250);
        ofFloat.addUpdateListener(new MultiValueUpdateListener(this, 250, f2, remoteAnimationTargetCompatArr, point, rect, rotationChange, matrix, f, surfaceTransactionApplier) {
            MultiValueUpdateListener.FloatProp mAlpha = new MultiValueUpdateListener.FloatProp(1.0f, 0.0f, 25.0f, 125.0f, Interpolators.LINEAR);
            MultiValueUpdateListener.FloatProp mDy;
            MultiValueUpdateListener.FloatProp mScale;
            MultiValueUpdateListener.FloatProp mShadowRadius;
            final /* synthetic */ QuickstepTransitionManager this$0;
            final /* synthetic */ RemoteAnimationTargetCompat[] val$appTargets;
            final /* synthetic */ int val$duration;
            final /* synthetic */ Matrix val$matrix;
            final /* synthetic */ int val$rotationChange;
            final /* synthetic */ float val$startShadowRadius;
            final /* synthetic */ SurfaceTransactionApplier val$surfaceApplier;
            final /* synthetic */ Point val$tmpPos;
            final /* synthetic */ Rect val$tmpRect;
            final /* synthetic */ float val$windowCornerRadius;

            {
                int i = r11;
                this.this$0 = r10;
                this.val$duration = i;
                this.val$startShadowRadius = r12;
                this.val$appTargets = r13;
                this.val$tmpPos = r14;
                this.val$tmpRect = r15;
                this.val$rotationChange = r16;
                this.val$matrix = r17;
                this.val$windowCornerRadius = r18;
                this.val$surfaceApplier = r19;
                this.mDy = new MultiValueUpdateListener.FloatProp(0.0f, r10.mClosingWindowTransY, 0.0f, (float) i, Interpolators.DEACCEL_1_7);
                this.mScale = new MultiValueUpdateListener.FloatProp(1.0f, 1.0f, 0.0f, (float) i, Interpolators.DEACCEL_1_7);
                this.mShadowRadius = new MultiValueUpdateListener.FloatProp(r12, 0.0f, 0.0f, (float) i, Interpolators.DEACCEL_1_7);
            }

            public void onUpdate(float f, boolean z) {
                RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr = this.val$appTargets;
                SyncRtSurfaceTransactionApplierCompat.SurfaceParams[] surfaceParamsArr = new SyncRtSurfaceTransactionApplierCompat.SurfaceParams[remoteAnimationTargetCompatArr.length];
                for (int length = remoteAnimationTargetCompatArr.length - 1; length >= 0; length--) {
                    RemoteAnimationTargetCompat remoteAnimationTargetCompat = this.val$appTargets[length];
                    SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder = new SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder(remoteAnimationTargetCompat.leash);
                    if (remoteAnimationTargetCompat.localBounds != null) {
                        this.val$tmpPos.set(remoteAnimationTargetCompat.localBounds.left, remoteAnimationTargetCompat.localBounds.top);
                    } else {
                        this.val$tmpPos.set(remoteAnimationTargetCompat.position.x, remoteAnimationTargetCompat.position.y);
                    }
                    Rect rect = new Rect(remoteAnimationTargetCompat.screenSpaceBounds);
                    rect.offsetTo(0, 0);
                    if (remoteAnimationTargetCompat.mode == 1) {
                        this.val$tmpRect.set(remoteAnimationTargetCompat.screenSpaceBounds);
                        if (this.val$rotationChange % 2 != 0) {
                            int i = rect.right;
                            rect.right = rect.bottom;
                            rect.bottom = i;
                        }
                        this.val$matrix.setScale(this.mScale.value, this.mScale.value, (float) this.val$tmpRect.centerX(), (float) this.val$tmpRect.centerY());
                        this.val$matrix.postTranslate(0.0f, this.mDy.value);
                        this.val$matrix.postTranslate((float) this.val$tmpPos.x, (float) this.val$tmpPos.y);
                        builder.withMatrix(this.val$matrix).withWindowCrop(rect).withAlpha(this.mAlpha.value).withCornerRadius(this.val$windowCornerRadius).withShadowRadius(this.mShadowRadius.value);
                    } else if (remoteAnimationTargetCompat.mode == 0) {
                        this.val$matrix.setTranslate((float) this.val$tmpPos.x, (float) this.val$tmpPos.y);
                        builder.withMatrix(this.val$matrix).withWindowCrop(rect).withAlpha(1.0f);
                    }
                    surfaceParamsArr[length] = builder.build();
                }
                this.val$surfaceApplier.scheduleApply(surfaceParamsArr);
            }
        });
        return ofFloat;
    }

    private boolean supportsSSplashScreen() {
        return hasControlRemoteAppTransitionPermission() && Utilities.ATLEAST_S && ENABLE_SHELL_STARTING_SURFACE;
    }

    public boolean hasControlRemoteAppTransitionPermission() {
        return this.mLauncher.checkSelfPermission(CONTROL_REMOTE_APP_TRANSITION_PERMISSION) == 0;
    }

    /* access modifiers changed from: private */
    public void addCujInstrumentation(Animator animator, final int i) {
        animator.addListener(new AnimationSuccessListener() {
            public void onAnimationStart(Animator animator) {
                QuickstepTransitionManager.this.mDragLayer.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
                    boolean mHandled = false;

                    public void onDraw() {
                        if (!this.mHandled) {
                            this.mHandled = true;
                            InteractionJankMonitorWrapper.begin(QuickstepTransitionManager.this.mDragLayer, i);
                            QuickstepTransitionManager.this.mDragLayer.post(
                            /*  JADX ERROR: Method code generation error
                                jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0024: INVOKE  
                                  (wrap: com.android.launcher3.dragndrop.DragLayer : 0x001b: INVOKE  (r0v7 com.android.launcher3.dragndrop.DragLayer) = 
                                  (wrap: com.android.launcher3.QuickstepTransitionManager : 0x0019: IGET  (r0v6 com.android.launcher3.QuickstepTransitionManager) = 
                                  (wrap: com.android.launcher3.QuickstepTransitionManager$19 : 0x0017: IGET  (r0v5 com.android.launcher3.QuickstepTransitionManager$19) = 
                                  (r2v0 'this' com.android.launcher3.QuickstepTransitionManager$19$1 A[THIS])
                                 com.android.launcher3.QuickstepTransitionManager.19.1.this$1 com.android.launcher3.QuickstepTransitionManager$19)
                                 com.android.launcher3.QuickstepTransitionManager.19.this$0 com.android.launcher3.QuickstepTransitionManager)
                                 com.android.launcher3.QuickstepTransitionManager.access$500(com.android.launcher3.QuickstepTransitionManager):com.android.launcher3.dragndrop.DragLayer type: STATIC)
                                  (wrap: com.android.launcher3.-$$Lambda$QuickstepTransitionManager$19$1$ts7kpbGVuU4qP49Hm8eTXZ7UIyU : 0x0021: CONSTRUCTOR  (r1v2 com.android.launcher3.-$$Lambda$QuickstepTransitionManager$19$1$ts7kpbGVuU4qP49Hm8eTXZ7UIyU) = 
                                  (r2v0 'this' com.android.launcher3.QuickstepTransitionManager$19$1 A[THIS])
                                 call: com.android.launcher3.-$$Lambda$QuickstepTransitionManager$19$1$ts7kpbGVuU4qP49Hm8eTXZ7UIyU.<init>(com.android.launcher3.QuickstepTransitionManager$19$1):void type: CONSTRUCTOR)
                                 com.android.launcher3.dragndrop.DragLayer.post(java.lang.Runnable):boolean type: VIRTUAL in method: com.android.launcher3.QuickstepTransitionManager.19.1.onDraw():void, dex: classes.dex
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                                	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                                	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                                	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                                	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                                	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                                Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0021: CONSTRUCTOR  (r1v2 com.android.launcher3.-$$Lambda$QuickstepTransitionManager$19$1$ts7kpbGVuU4qP49Hm8eTXZ7UIyU) = 
                                  (r2v0 'this' com.android.launcher3.QuickstepTransitionManager$19$1 A[THIS])
                                 call: com.android.launcher3.-$$Lambda$QuickstepTransitionManager$19$1$ts7kpbGVuU4qP49Hm8eTXZ7UIyU.<init>(com.android.launcher3.QuickstepTransitionManager$19$1):void type: CONSTRUCTOR in method: com.android.launcher3.QuickstepTransitionManager.19.1.onDraw():void, dex: classes.dex
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                	... 100 more
                                Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: com.android.launcher3.-$$Lambda$QuickstepTransitionManager$19$1$ts7kpbGVuU4qP49Hm8eTXZ7UIyU, state: NOT_LOADED
                                	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                	... 106 more
                                */
                            /*
                                this = this;
                                boolean r0 = r2.mHandled
                                if (r0 == 0) goto L_0x0005
                                return
                            L_0x0005:
                                r0 = 1
                                r2.mHandled = r0
                                com.android.launcher3.QuickstepTransitionManager$19 r0 = com.android.launcher3.QuickstepTransitionManager.AnonymousClass19.this
                                com.android.launcher3.QuickstepTransitionManager r0 = com.android.launcher3.QuickstepTransitionManager.this
                                com.android.launcher3.dragndrop.DragLayer r0 = r0.mDragLayer
                                com.android.launcher3.QuickstepTransitionManager$19 r1 = com.android.launcher3.QuickstepTransitionManager.AnonymousClass19.this
                                int r1 = r3
                                com.android.systemui.shared.system.InteractionJankMonitorWrapper.begin(r0, r1)
                                com.android.launcher3.QuickstepTransitionManager$19 r0 = com.android.launcher3.QuickstepTransitionManager.AnonymousClass19.this
                                com.android.launcher3.QuickstepTransitionManager r0 = com.android.launcher3.QuickstepTransitionManager.this
                                com.android.launcher3.dragndrop.DragLayer r0 = r0.mDragLayer
                                com.android.launcher3.-$$Lambda$QuickstepTransitionManager$19$1$ts7kpbGVuU4qP49Hm8eTXZ7UIyU r1 = new com.android.launcher3.-$$Lambda$QuickstepTransitionManager$19$1$ts7kpbGVuU4qP49Hm8eTXZ7UIyU
                                r1.<init>(r2)
                                r0.post(r1)
                                return
                            */
                            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.QuickstepTransitionManager.AnonymousClass19.AnonymousClass1.onDraw():void");
                        }

                        public /* synthetic */ void lambda$onDraw$0$QuickstepTransitionManager$19$1() {
                            QuickstepTransitionManager.this.mDragLayer.getViewTreeObserver().removeOnDrawListener(this);
                        }
                    });
                    super.onAnimationStart(animator);
                }

                public void onAnimationCancel(Animator animator) {
                    super.onAnimationCancel(animator);
                    InteractionJankMonitorWrapper.cancel(i);
                }

                public void onAnimationSuccess(Animator animator) {
                    InteractionJankMonitorWrapper.end(i);
                }
            });
        }

        /* JADX WARNING: Removed duplicated region for block: B:35:0x00d5  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public android.util.Pair<com.android.quickstep.util.RectFSpringAnim, android.animation.AnimatorSet> createWallpaperOpenAnimations(com.android.systemui.shared.system.RemoteAnimationTargetCompat[] r16, com.android.systemui.shared.system.RemoteAnimationTargetCompat[] r17, boolean r18, android.graphics.RectF r19, float r20) {
            /*
                r15 = this;
                r7 = r15
                r8 = r16
                com.android.quickstep.util.RemoteAnimationProvider r0 = r7.mRemoteAnimationProvider
                r1 = 0
                r2 = r17
                if (r0 == 0) goto L_0x000f
                android.animation.AnimatorSet r0 = r0.createWindowAnimation(r8, r2)
                goto L_0x0010
            L_0x000f:
                r0 = r1
            L_0x0010:
                if (r0 != 0) goto L_0x0116
                android.animation.AnimatorSet r9 = new android.animation.AnimatorSet
                r9.<init>()
                com.android.launcher3.BaseQuickstepLauncher r0 = r7.mLauncher
                boolean r0 = r0.isForceInvisible()
                r10 = 1
                r11 = 0
                if (r0 != 0) goto L_0x002a
                boolean r0 = r15.launcherIsATargetWithMode(r8, r11)
                if (r0 == 0) goto L_0x0028
                goto L_0x002a
            L_0x0028:
                r12 = r11
                goto L_0x002b
            L_0x002a:
                r12 = r10
            L_0x002b:
                android.view.View r13 = r15.findLauncherView((com.android.systemui.shared.system.RemoteAnimationTargetCompat[]) r16)
                if (r13 != 0) goto L_0x0033
                if (r12 != 0) goto L_0x0048
            L_0x0033:
                com.android.launcher3.BaseQuickstepLauncher r0 = r7.mLauncher
                com.android.launcher3.Workspace r0 = r0.getWorkspace()
                boolean r0 = r0.isOverlayShown()
                if (r0 != 0) goto L_0x0048
                boolean r0 = r15.hasMultipleTargetsWithMode(r8, r10)
                if (r0 == 0) goto L_0x0046
                goto L_0x0048
            L_0x0046:
                r0 = r11
                goto L_0x0049
            L_0x0048:
                r0 = r10
            L_0x0049:
                if (r18 == 0) goto L_0x0054
                android.animation.Animator r0 = r15.getUnlockWindowAnimator(r16, r17)
                r9.play(r0)
                goto L_0x00d2
            L_0x0054:
                com.android.launcher3.config.FeatureFlags$BooleanFlag r2 = com.android.launcher3.config.FeatureFlags.ENABLE_BACK_SWIPE_HOME_ANIMATION
                boolean r2 = r2.get()
                if (r2 == 0) goto L_0x00cb
                if (r0 != 0) goto L_0x00cb
                com.android.launcher3.BaseQuickstepLauncher r0 = r7.mLauncher
                com.android.systemui.plugins.ResourceProvider r0 = com.android.launcher3.util.DynamicResource.provider(r0)
                r1 = 2131166025(0x7f070349, float:1.7946284E38)
                float r0 = r0.getDimension(r1)
                android.graphics.PointF r14 = new android.graphics.PointF
                r1 = 0
                float r0 = -r0
                r14.<init>(r1, r0)
                r0 = r15
                r1 = r9
                r2 = r16
                r3 = r13
                r4 = r14
                r5 = r19
                r6 = r20
                com.android.quickstep.util.RectFSpringAnim r1 = r0.getClosingWindowAnimators(r1, r2, r3, r4, r5, r6)
                com.android.launcher3.BaseQuickstepLauncher r0 = r7.mLauncher
                com.android.launcher3.LauncherState r2 = com.android.launcher3.LauncherState.ALL_APPS
                boolean r0 = r0.isInState(r2)
                if (r0 != 0) goto L_0x00c9
                com.android.quickstep.util.StaggeredWorkspaceAnim r0 = new com.android.quickstep.util.StaggeredWorkspaceAnim
                com.android.launcher3.BaseQuickstepLauncher r2 = r7.mLauncher
                float r3 = r14.y
                r0.<init>(r2, r3, r10, r13)
                android.animation.AnimatorSet r0 = r0.getAnimators()
                r9.play(r0)
                boolean r0 = r15.areAllTargetsTranslucent(r16)
                if (r0 != 0) goto L_0x00c6
                com.android.launcher3.BaseQuickstepLauncher r0 = r7.mLauncher
                com.android.launcher3.statehandlers.DepthController r0 = r0.getDepthController()
                android.util.FloatProperty<com.android.launcher3.statehandlers.DepthController> r2 = com.android.launcher3.statehandlers.DepthController.DEPTH
                r3 = 2
                float[] r3 = new float[r3]
                com.android.launcher3.LauncherState r4 = com.android.launcher3.LauncherState.BACKGROUND_APP
                com.android.launcher3.BaseQuickstepLauncher r5 = r7.mLauncher
                float r4 = r4.getDepth(r5)
                r3[r11] = r4
                com.android.launcher3.LauncherState r4 = com.android.launcher3.LauncherState.NORMAL
                com.android.launcher3.BaseQuickstepLauncher r5 = r7.mLauncher
                float r4 = r4.getDepth(r5)
                r3[r10] = r4
                android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r2, r3)
                r9.play(r0)
            L_0x00c6:
                r0 = r11
                r10 = r0
                goto L_0x00d3
            L_0x00c9:
                r0 = r10
                goto L_0x00d3
            L_0x00cb:
                android.animation.Animator r0 = r15.getFallbackClosingWindowAnimators(r16)
                r9.play(r0)
            L_0x00d2:
                r0 = r11
            L_0x00d3:
                if (r12 == 0) goto L_0x0115
                r2 = 9
                r15.addCujInstrumentation(r9, r2)
                com.android.launcher3.BaseQuickstepLauncher r2 = r7.mLauncher
                com.android.launcher3.statemanager.StateManager r2 = r2.getStateManager()
                android.animation.Animator[] r3 = new android.animation.Animator[r11]
                r2.setCurrentAnimation((android.animation.AnimatorSet) r9, (android.animation.Animator[]) r3)
                com.android.launcher3.BaseQuickstepLauncher r2 = r7.mLauncher
                com.android.launcher3.LauncherState r3 = com.android.launcher3.LauncherState.ALL_APPS
                boolean r2 = r2.isInState(r3)
                if (r2 == 0) goto L_0x0105
                r2 = 100
                android.util.Pair r0 = r15.getLauncherContentAnimator(r11, r2, r0)
                java.lang.Object r2 = r0.first
                android.animation.Animator r2 = (android.animation.Animator) r2
                r9.play(r2)
                com.android.launcher3.QuickstepTransitionManager$20 r2 = new com.android.launcher3.QuickstepTransitionManager$20
                r2.<init>(r0)
                r9.addListener(r2)
                goto L_0x0115
            L_0x0105:
                if (r10 == 0) goto L_0x0115
                com.android.quickstep.util.WorkspaceRevealAnim r0 = new com.android.quickstep.util.WorkspaceRevealAnim
                com.android.launcher3.BaseQuickstepLauncher r2 = r7.mLauncher
                r0.<init>(r2, r11)
                android.animation.AnimatorSet r0 = r0.getAnimators()
                r9.play(r0)
            L_0x0115:
                r0 = r9
            L_0x0116:
                android.util.Pair r2 = new android.util.Pair
                r2.<init>(r1, r0)
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.QuickstepTransitionManager.createWallpaperOpenAnimations(com.android.systemui.shared.system.RemoteAnimationTargetCompat[], com.android.systemui.shared.system.RemoteAnimationTargetCompat[], boolean, android.graphics.RectF, float):android.util.Pair");
        }

        protected class WallpaperOpenLauncherAnimationRunner implements LauncherAnimationRunner.RemoteAnimationFactory {
            private final boolean mFromUnlock;
            private final Handler mHandler;

            public WallpaperOpenLauncherAnimationRunner(Handler handler, boolean z) {
                this.mHandler = handler;
                this.mFromUnlock = z;
            }

            public void onCreateAnimation(int i, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3, LauncherAnimationRunner.AnimationResult animationResult) {
                if (QuickstepTransitionManager.this.mLauncher.isDestroyed()) {
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.play(QuickstepTransitionManager.this.getFallbackClosingWindowAnimators(remoteAnimationTargetCompatArr));
                    animationResult.setAnimation(animatorSet, QuickstepTransitionManager.this.mLauncher.getApplicationContext());
                    return;
                }
                if (QuickstepTransitionManager.this.mLauncher.hasSomeInvisibleFlag(8)) {
                    QuickstepTransitionManager.this.mLauncher.addForceInvisibleFlag(4);
                    QuickstepTransitionManager.this.mLauncher.getStateManager().moveToRestState();
                }
                Pair<RectFSpringAnim, AnimatorSet> createWallpaperOpenAnimations = QuickstepTransitionManager.this.createWallpaperOpenAnimations(remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, this.mFromUnlock, new RectF(0.0f, 0.0f, (float) QuickstepTransitionManager.this.mDeviceProfile.widthPx, (float) QuickstepTransitionManager.this.mDeviceProfile.heightPx), QuickStepContract.getWindowCornerRadius(QuickstepTransitionManager.this.mLauncher));
                QuickstepTransitionManager.this.mLauncher.clearForceInvisibleFlag(15);
                animationResult.setAnimation((AnimatorSet) createWallpaperOpenAnimations.second, QuickstepTransitionManager.this.mLauncher);
            }
        }

        private class AppLaunchAnimationRunner implements LauncherAnimationRunner.RemoteAnimationFactory {
            private final RunnableList mOnEndCallback;
            private final View mV;

            AppLaunchAnimationRunner(View view, RunnableList runnableList) {
                this.mV = view;
                this.mOnEndCallback = runnableList;
            }

            public void onCreateAnimation(int i, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3, LauncherAnimationRunner.AnimationResult animationResult) {
                AnimatorSet animatorSet = new AnimatorSet();
                boolean z = true;
                boolean access$700 = QuickstepTransitionManager.this.launcherIsATargetWithMode(remoteAnimationTargetCompatArr, 1);
                View view = this.mV;
                boolean z2 = view instanceof LauncherAppWidgetHostView;
                boolean isLaunchingFromRecents = QuickstepTransitionManager.this.isLaunchingFromRecents(view, remoteAnimationTargetCompatArr);
                if (z2) {
                    QuickstepTransitionManager.this.composeWidgetLaunchAnimator(animatorSet, (LauncherAppWidgetHostView) this.mV, remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, remoteAnimationTargetCompatArr3);
                    QuickstepTransitionManager.this.addCujInstrumentation(animatorSet, 27);
                } else if (isLaunchingFromRecents) {
                    QuickstepTransitionManager.this.composeRecentsLaunchAnimator(animatorSet, this.mV, remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, remoteAnimationTargetCompatArr3, access$700);
                    QuickstepTransitionManager.this.addCujInstrumentation(animatorSet, 7);
                } else {
                    QuickstepTransitionManager.this.composeIconLaunchAnimator(animatorSet, this.mV, remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, remoteAnimationTargetCompatArr3, access$700);
                    QuickstepTransitionManager.this.addCujInstrumentation(animatorSet, 8);
                    z = false;
                }
                if (access$700) {
                    animatorSet.addListener(QuickstepTransitionManager.this.mForceInvisibleListener);
                }
                BaseQuickstepLauncher baseQuickstepLauncher = QuickstepTransitionManager.this.mLauncher;
                RunnableList runnableList = this.mOnEndCallback;
                Objects.requireNonNull(runnableList);
                animationResult.setAnimation(animatorSet, baseQuickstepLauncher, new Runnable() {
                    public final void run() {
                        RunnableList.this.executeAllAndDestroy();
                    }
                }, z);
            }

            public void onAnimationCancelled() {
                this.mOnEndCallback.executeAllAndDestroy();
            }
        }

        static class AnimOpenProperties {
            public final int cropCenterXEnd;
            public final int cropCenterXStart;
            public final int cropCenterYEnd;
            public final int cropCenterYStart;
            public final int cropHeightEnd;
            public final int cropHeightStart;
            public final int cropWidthEnd;
            public final int cropWidthStart;
            public final float dX;
            public final float dY;
            public final float finalAppIconScale;
            public final float iconAlphaStart;
            public final float initialAppIconScale;

            AnimOpenProperties(Resources resources, DeviceProfile deviceProfile, Rect rect, RectF rectF, View view, int i, int i2, boolean z, boolean z2) {
                float f;
                float min = (float) Math.min(rect.height(), rect.width());
                float width = min / rectF.width();
                float height = min / rectF.height();
                float f2 = 1.0f;
                if ((view instanceof BubbleTextView) && !(view.getParent() instanceof DeepShortcutView)) {
                    FastBitmapDrawable icon = ((BubbleTextView) view).getIcon();
                    if (icon instanceof FastBitmapDrawable) {
                        f = icon.getAnimatedScale();
                        this.initialAppIconScale = f;
                        this.finalAppIconScale = Math.max(width, height);
                        this.dX = ((float) (rect.centerX() - i)) - rectF.centerX();
                        this.dY = ((float) (rect.centerY() - i2)) - rectF.centerY();
                        if (z && !z2) {
                            f2 = 0.0f;
                        }
                        this.iconAlphaStart = f2;
                        int dimenByName = ResourceUtils.getDimenByName("starting_surface_icon_size", resources, 108);
                        this.cropCenterXStart = rect.centerX();
                        this.cropCenterYStart = rect.centerY();
                        this.cropWidthStart = dimenByName;
                        this.cropHeightStart = dimenByName;
                        this.cropWidthEnd = rect.width();
                        this.cropHeightEnd = rect.height();
                        this.cropCenterXEnd = rect.centerX();
                        this.cropCenterYEnd = rect.centerY();
                    }
                }
                f = 1.0f;
                this.initialAppIconScale = f;
                this.finalAppIconScale = Math.max(width, height);
                this.dX = ((float) (rect.centerX() - i)) - rectF.centerX();
                this.dY = ((float) (rect.centerY() - i2)) - rectF.centerY();
                f2 = 0.0f;
                this.iconAlphaStart = f2;
                int dimenByName2 = ResourceUtils.getDimenByName("starting_surface_icon_size", resources, 108);
                this.cropCenterXStart = rect.centerX();
                this.cropCenterYStart = rect.centerY();
                this.cropWidthStart = dimenByName2;
                this.cropHeightStart = dimenByName2;
                this.cropWidthEnd = rect.width();
                this.cropHeightEnd = rect.height();
                this.cropCenterXEnd = rect.centerX();
                this.cropCenterYEnd = rect.centerY();
            }
        }

        private static class StartingWindowListener extends IStartingWindowListener.Stub {
            private QuickstepTransitionManager mTransitionManager;

            private StartingWindowListener() {
            }

            public void setTransitionManager(QuickstepTransitionManager quickstepTransitionManager) {
                this.mTransitionManager = quickstepTransitionManager;
            }

            public void onTaskLaunching(int i, int i2, int i3) {
                this.mTransitionManager.mTaskStartParams.put(Integer.valueOf(i), Pair.create(Integer.valueOf(i2), Integer.valueOf(i3)));
            }
        }

        private class SpringAnimRunner implements RectFSpringAnim.OnUpdateListener {
            private final RemoteAnimationTargetCompat[] mAppTargets;
            private final Rect mCurrentRect = new Rect();
            private final float mEndRadius;
            private final Matrix mMatrix = new Matrix();
            private final float mStartRadius;
            private final SurfaceTransactionApplier mSurfaceApplier;
            private final Point mTmpPos = new Point();
            private final Rect mTmpRect;
            private final Rect mWindowTargetBounds;

            SpringAnimRunner(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RectF rectF, Rect rect, float f) {
                Rect rect2 = new Rect();
                this.mWindowTargetBounds = rect2;
                this.mTmpRect = new Rect();
                this.mAppTargets = remoteAnimationTargetCompatArr;
                this.mStartRadius = f;
                this.mEndRadius = Math.max(1.0f, rectF.width()) / 2.0f;
                this.mSurfaceApplier = new SurfaceTransactionApplier(QuickstepTransitionManager.this.mDragLayer);
                rect2.set(rect);
            }

            public float getCornerRadius(float f) {
                return Utilities.mapRange(f, this.mStartRadius, this.mEndRadius);
            }

            public void onUpdate(RectF rectF, float f) {
                float f2;
                RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr = this.mAppTargets;
                SyncRtSurfaceTransactionApplierCompat.SurfaceParams[] surfaceParamsArr = new SyncRtSurfaceTransactionApplierCompat.SurfaceParams[remoteAnimationTargetCompatArr.length];
                for (int length = remoteAnimationTargetCompatArr.length - 1; length >= 0; length--) {
                    RemoteAnimationTargetCompat remoteAnimationTargetCompat = this.mAppTargets[length];
                    SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder = new SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder(remoteAnimationTargetCompat.leash);
                    if (remoteAnimationTargetCompat.localBounds != null) {
                        this.mTmpPos.set(remoteAnimationTargetCompat.localBounds.left, remoteAnimationTargetCompat.localBounds.top);
                    } else {
                        this.mTmpPos.set(remoteAnimationTargetCompat.position.x, remoteAnimationTargetCompat.position.y);
                    }
                    if (remoteAnimationTargetCompat.mode == 1) {
                        rectF.round(this.mCurrentRect);
                        if (this.mWindowTargetBounds.height() > this.mWindowTargetBounds.width()) {
                            f2 = Math.min(1.0f, rectF.width() / ((float) this.mWindowTargetBounds.width()));
                            this.mTmpRect.set(0, 0, this.mWindowTargetBounds.width(), this.mWindowTargetBounds.height() - (this.mWindowTargetBounds.height() - ((int) (((float) this.mCurrentRect.height()) * (1.0f / f2)))));
                        } else {
                            f2 = Math.min(1.0f, rectF.height() / ((float) this.mWindowTargetBounds.height()));
                            this.mTmpRect.set(0, 0, this.mWindowTargetBounds.width() - (this.mWindowTargetBounds.width() - ((int) (((float) this.mCurrentRect.width()) * (1.0f / f2)))), this.mWindowTargetBounds.height());
                        }
                        this.mMatrix.setScale(f2, f2);
                        this.mMatrix.postTranslate((float) this.mCurrentRect.left, (float) this.mCurrentRect.top);
                        builder.withMatrix(this.mMatrix).withWindowCrop(this.mTmpRect).withAlpha(getWindowAlpha(f)).withCornerRadius(getCornerRadius(f) / f2);
                    } else if (remoteAnimationTargetCompat.mode == 0) {
                        this.mMatrix.setTranslate((float) this.mTmpPos.x, (float) this.mTmpPos.y);
                        builder.withMatrix(this.mMatrix).withAlpha(1.0f);
                    }
                    surfaceParamsArr[length] = builder.build();
                }
                this.mSurfaceApplier.scheduleApply(surfaceParamsArr);
            }

            /* access modifiers changed from: protected */
            public float getWindowAlpha(float f) {
                if (f <= 0.0f) {
                    return 1.0f;
                }
                if (f >= 0.85f) {
                    return 0.0f;
                }
                return Utilities.mapToRange(f, 0.0f, 0.85f, 1.0f, 0.0f, Interpolators.ACCEL_1_5);
            }
        }
    }
