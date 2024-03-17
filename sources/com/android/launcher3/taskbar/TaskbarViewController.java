package com.android.launcher3.taskbar;

import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.util.FloatProperty;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.OneShotPreDrawListener;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.icons.ThemedIconDrawable;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.taskbar.TaskbarControllers;
import com.android.launcher3.taskbar.TaskbarViewController;
import com.android.launcher3.util.ItemInfoMatcher;
import com.android.launcher3.util.LauncherBindableItemsContainer;
import com.android.launcher3.util.MultiValueAlpha;
import com.android.quickstep.AnimatedFloat;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.function.Predicate;

public class TaskbarViewController implements TaskbarControllers.LoggableTaskbarController {
    public static final int ALPHA_INDEX_HOME = 0;
    public static final int ALPHA_INDEX_KEYGUARD = 1;
    public static final int ALPHA_INDEX_NOTIFICATION_EXPANDED = 4;
    public static final int ALPHA_INDEX_RECENTS_DISABLED = 3;
    public static final int ALPHA_INDEX_STASH = 2;
    public static final FloatProperty<View> ICON_TRANSLATE_X = new FloatProperty<View>("taskbarAligmentTranslateX") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(View view, float f) {
            if (view instanceof BubbleTextView) {
                ((BubbleTextView) view).setTranslationXForTaskbarAlignmentAnimation(f);
            } else if (view instanceof FolderIcon) {
                ((FolderIcon) view).setTranslationForTaskbarAlignmentAnimation(f);
            } else {
                view.setTranslationX(f);
            }
        }

        public Float get(View view) {
            if (view instanceof BubbleTextView) {
                return Float.valueOf(((BubbleTextView) view).getTranslationXForTaskbarAlignmentAnimation());
            }
            if (view instanceof FolderIcon) {
                return Float.valueOf(((FolderIcon) view).getTranslationXForTaskbarAlignmentAnimation());
            }
            return Float.valueOf(view.getTranslationX());
        }
    };
    private static final Runnable NO_OP = $$Lambda$TaskbarViewController$auaO9RrEzXr1HQP4931NyQPkA.INSTANCE;
    private static final int NUM_ALPHA_CHANNELS = 5;
    private static final String TAG = "TaskbarViewController";
    /* access modifiers changed from: private */
    public final TaskbarActivityContext mActivity;
    /* access modifiers changed from: private */
    public TaskbarControllers mControllers;
    private AnimatorPlaybackController mIconAlignControllerLazy = null;
    private final TaskbarModelCallbacks mModelCallbacks;
    private Runnable mOnControllerPreCreateCallback = NO_OP;
    private final MultiValueAlpha mTaskbarIconAlpha;
    private final AnimatedFloat mTaskbarIconScaleForStash = new AnimatedFloat(new Runnable() {
        public final void run() {
            TaskbarViewController.this.updateScale();
        }
    });
    private final AnimatedFloat mTaskbarIconTranslationYForHome = new AnimatedFloat(new Runnable() {
        public final void run() {
            TaskbarViewController.this.updateTranslationY();
        }
    });
    private final AnimatedFloat mTaskbarIconTranslationYForStash = new AnimatedFloat(new Runnable() {
        public final void run() {
            TaskbarViewController.this.updateTranslationY();
        }
    });
    private AnimatedFloat mTaskbarNavButtonTranslationY;
    private AnimatedFloat mTaskbarNavButtonTranslationYForInAppDisplay;
    private final TaskbarView mTaskbarView;
    private final AnimatedFloat mThemeIconsBackground = new AnimatedFloat(new Runnable() {
        public final void run() {
            TaskbarViewController.this.updateIconsBackground();
        }
    });
    private int mThemeIconsColor;

    static /* synthetic */ void lambda$static$0() {
    }

    public TaskbarViewController(TaskbarActivityContext taskbarActivityContext, TaskbarView taskbarView) {
        this.mActivity = taskbarActivityContext;
        this.mTaskbarView = taskbarView;
        MultiValueAlpha multiValueAlpha = new MultiValueAlpha(taskbarView, 5);
        this.mTaskbarIconAlpha = multiValueAlpha;
        multiValueAlpha.setUpdateVisibility(true);
        this.mModelCallbacks = new TaskbarModelCallbacks(taskbarActivityContext, taskbarView);
    }

    public void init(TaskbarControllers taskbarControllers) {
        this.mControllers = taskbarControllers;
        this.mTaskbarView.init(new TaskbarViewCallbacks());
        this.mTaskbarView.getLayoutParams().height = this.mActivity.getDeviceProfile().taskbarSize;
        this.mThemeIconsColor = ThemedIconDrawable.getColors(this.mTaskbarView.getContext())[0];
        this.mTaskbarIconScaleForStash.updateValue(1.0f);
        this.mModelCallbacks.init(taskbarControllers);
        if (this.mActivity.isUserSetupComplete()) {
            LauncherAppState.getInstance(this.mActivity).getModel().addCallbacksAndLoad(this.mModelCallbacks);
        }
        this.mTaskbarNavButtonTranslationY = taskbarControllers.navbarButtonsViewController.getTaskbarNavButtonTranslationY();
        this.mTaskbarNavButtonTranslationYForInAppDisplay = taskbarControllers.navbarButtonsViewController.getTaskbarNavButtonTranslationYForInAppDisplay();
    }

    public void onDestroy() {
        LauncherAppState.getInstance(this.mActivity).getModel().removeCallbacks(this.mModelCallbacks);
    }

    public boolean areIconsVisible() {
        return this.mTaskbarView.areIconsVisible();
    }

    public MultiValueAlpha getTaskbarIconAlpha() {
        return this.mTaskbarIconAlpha;
    }

    public void setImeIsVisible(boolean z) {
        this.mTaskbarView.setTouchesEnabled(!z);
    }

    public void setRecentsButtonDisabled(boolean z) {
        this.mTaskbarIconAlpha.getProperty(3).setValue(z ? 0.0f : 1.0f);
    }

    public void setClickAndLongClickListenersForIcon(View view) {
        this.mTaskbarView.setClickAndLongClickListenersForIcon(view);
    }

    public void addOneTimePreDrawListener(Runnable runnable) {
        OneShotPreDrawListener.add(this.mTaskbarView, runnable);
    }

    public Rect getIconLayoutBounds() {
        return this.mTaskbarView.getIconLayoutBounds();
    }

    public View[] getIconViews() {
        return this.mTaskbarView.getIconViews();
    }

    public View getAllAppsButtonView() {
        return this.mTaskbarView.getAllAppsButtonView();
    }

    public AnimatedFloat getTaskbarIconScaleForStash() {
        return this.mTaskbarIconScaleForStash;
    }

    public AnimatedFloat getTaskbarIconTranslationYForStash() {
        return this.mTaskbarIconTranslationYForStash;
    }

    /* access modifiers changed from: private */
    public void updateScale() {
        float f = this.mTaskbarIconScaleForStash.value;
        this.mTaskbarView.setScaleX(f);
        this.mTaskbarView.setScaleY(f);
    }

    /* access modifiers changed from: private */
    public void updateTranslationY() {
        this.mTaskbarView.setTranslationY(this.mTaskbarIconTranslationYForHome.value + this.mTaskbarIconTranslationYForStash.value);
    }

    /* access modifiers changed from: private */
    public void updateIconsBackground() {
        TaskbarView taskbarView = this.mTaskbarView;
        taskbarView.setThemedIconsBackgroundColor(ColorUtils.blendARGB(this.mThemeIconsColor, taskbarView.mThemeIconsBackground, this.mThemeIconsBackground.value));
    }

    public void setLauncherIconAlignment(float f, DeviceProfile deviceProfile) {
        if (this.mIconAlignControllerLazy == null) {
            this.mIconAlignControllerLazy = createIconAlignmentController(deviceProfile);
        }
        this.mIconAlignControllerLazy.setPlayFraction(f);
        if (f <= 0.0f || f >= 1.0f) {
            this.mIconAlignControllerLazy = null;
        }
    }

    private AnimatorPlaybackController createIconAlignmentController(DeviceProfile deviceProfile) {
        int i;
        this.mOnControllerPreCreateCallback.run();
        PendingAnimation pendingAnimation = new PendingAnimation(100);
        Rect hotseatLayoutPadding = deviceProfile.getHotseatLayoutPadding(this.mActivity);
        float f = ((float) deviceProfile.iconSizePx) / ((float) this.mActivity.getDeviceProfile().iconSizePx);
        int i2 = deviceProfile.hotseatBorderSpace;
        int calculateCellWidth = DeviceProfile.calculateCellWidth((deviceProfile.availableWidthPx - hotseatLayoutPadding.left) - hotseatLayoutPadding.right, i2, deviceProfile.numShownHotseatIcons);
        int taskbarOffsetY = deviceProfile.getTaskbarOffsetY();
        float f2 = (float) (-taskbarOffsetY);
        pendingAnimation.setFloat(this.mTaskbarIconTranslationYForHome, AnimatedFloat.VALUE, f2, Interpolators.LINEAR);
        pendingAnimation.setFloat(this.mTaskbarNavButtonTranslationY, AnimatedFloat.VALUE, f2, Interpolators.LINEAR);
        pendingAnimation.setFloat(this.mTaskbarNavButtonTranslationYForInAppDisplay, AnimatedFloat.VALUE, (float) taskbarOffsetY, Interpolators.LINEAR);
        if (Utilities.isDarkTheme(this.mTaskbarView.getContext())) {
            pendingAnimation.addFloat(this.mThemeIconsBackground, AnimatedFloat.VALUE, 0.0f, 1.0f, Interpolators.LINEAR);
        }
        int defaultTaskbarWindowHeight = this.mActivity.getDefaultTaskbarWindowHeight();
        pendingAnimation.addOnFrameListener(new ValueAnimator.AnimatorUpdateListener(Math.max(defaultTaskbarWindowHeight, this.mActivity.getDeviceProfile().taskbarSize + taskbarOffsetY), defaultTaskbarWindowHeight) {
            public final /* synthetic */ int f$1;
            public final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                TaskbarViewController.this.lambda$createIconAlignmentController$1$TaskbarViewController(this.f$1, this.f$2, valueAnimator);
            }
        });
        for (int i3 = 0; i3 < this.mTaskbarView.getChildCount(); i3++) {
            View childAt = this.mTaskbarView.getChildAt(i3);
            if (FeatureFlags.ENABLE_ALL_APPS_IN_TASKBAR.get() && childAt == this.mTaskbarView.getAllAppsButtonView()) {
                if (Utilities.isRtl(childAt.getResources())) {
                    i = -1;
                } else {
                    i = this.mActivity.getDeviceProfile().numShownHotseatIcons;
                }
                if (!FeatureFlags.ENABLE_ALL_APPS_BUTTON_IN_HOTSEAT.get()) {
                    pendingAnimation.setViewAlpha(childAt, 0.0f, Interpolators.LINEAR);
                }
            } else if (childAt.getTag() instanceof ItemInfo) {
                i = ((ItemInfo) childAt.getTag()).screenId;
            } else {
                Log.w(TAG, "Unsupported view found in createIconAlignmentController, v=" + childAt);
            }
            pendingAnimation.setFloat(childAt, ICON_TRANSLATE_X, (((float) (hotseatLayoutPadding.left + ((calculateCellWidth + i2) * i))) + (((float) calculateCellWidth) / 2.0f)) - (((float) (childAt.getLeft() + childAt.getRight())) / 2.0f), Interpolators.LINEAR);
            pendingAnimation.setFloat(childAt, LauncherAnimUtils.SCALE_PROPERTY, f, Interpolators.LINEAR);
        }
        AnimatorPlaybackController createPlaybackController = pendingAnimation.createPlaybackController();
        this.mOnControllerPreCreateCallback = new Runnable() {
            public final void run() {
                AnimatorPlaybackController.this.setPlayFraction(0.0f);
            }
        };
        return createPlaybackController;
    }

    public /* synthetic */ void lambda$createIconAlignmentController$1$TaskbarViewController(int i, int i2, ValueAnimator valueAnimator) {
        TaskbarActivityContext taskbarActivityContext = this.mActivity;
        if (valueAnimator.getAnimatedFraction() <= 0.0f) {
            i = i2;
        }
        taskbarActivityContext.setTaskbarWindowHeight(i);
    }

    public void onRotationChanged(DeviceProfile deviceProfile) {
        if (!this.mControllers.taskbarStashController.isInApp()) {
            this.mActivity.setTaskbarWindowHeight(deviceProfile.taskbarSize + deviceProfile.getTaskbarOffsetY());
            this.mTaskbarNavButtonTranslationY.updateValue((float) (-deviceProfile.getTaskbarOffsetY()));
        }
    }

    public void mapOverItems(LauncherBindableItemsContainer.ItemOperator itemOperator) {
        this.mTaskbarView.mapOverItems(itemOperator);
    }

    public View getFirstIconMatch(Predicate<ItemInfo> predicate) {
        Predicate<ItemInfo> forFolderMatch = ItemInfoMatcher.forFolderMatch(predicate);
        return this.mTaskbarView.getFirstMatch(predicate, forFolderMatch);
    }

    public boolean isEventOverAnyItem(MotionEvent motionEvent) {
        return this.mTaskbarView.isEventOverAnyItem(motionEvent);
    }

    public void dumpLogs(String str, PrintWriter printWriter) {
        printWriter.println(str + "TaskbarViewController:");
        this.mModelCallbacks.dumpLogs(str + "\t", printWriter);
    }

    public class TaskbarViewCallbacks {
        private boolean mCanceledStashHint;
        private float mDownX;
        private float mDownY;
        private final float mSquaredTouchSlop;

        public TaskbarViewCallbacks() {
            this.mSquaredTouchSlop = Utilities.squaredTouchSlop(TaskbarViewController.this.mActivity);
        }

        public View.OnClickListener getIconOnClickListener() {
            return TaskbarViewController.this.mActivity.getItemOnClickListener();
        }

        public View.OnClickListener getAllAppsButtonClickListener() {
            return new View.OnClickListener() {
                public final void onClick(View view) {
                    TaskbarViewController.TaskbarViewCallbacks.this.lambda$getAllAppsButtonClickListener$0$TaskbarViewController$TaskbarViewCallbacks(view);
                }
            };
        }

        public /* synthetic */ void lambda$getAllAppsButtonClickListener$0$TaskbarViewController$TaskbarViewCallbacks(View view) {
            TaskbarViewController.this.mActivity.getStatsLogManager().logger().log(StatsLogManager.LauncherEvent.LAUNCHER_TASKBAR_ALLAPPS_BUTTON_TAP);
            TaskbarViewController.this.mControllers.taskbarAllAppsController.show();
        }

        public View.OnLongClickListener getIconOnLongClickListener() {
            TaskbarDragController taskbarDragController = TaskbarViewController.this.mControllers.taskbarDragController;
            Objects.requireNonNull(taskbarDragController);
            return new View.OnLongClickListener() {
                public final boolean onLongClick(View view) {
                    return TaskbarDragController.this.startDragOnLongClick(view);
                }
            };
        }

        public View.OnLongClickListener getBackgroundOnLongClickListener() {
            return new View.OnLongClickListener() {
                public final boolean onLongClick(View view) {
                    return TaskbarViewController.TaskbarViewCallbacks.this.lambda$getBackgroundOnLongClickListener$1$TaskbarViewController$TaskbarViewCallbacks(view);
                }
            };
        }

        public /* synthetic */ boolean lambda$getBackgroundOnLongClickListener$1$TaskbarViewController$TaskbarViewCallbacks(View view) {
            return TaskbarViewController.this.mControllers.taskbarStashController.updateAndAnimateIsManuallyStashedInApp(true);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:6:0x0016, code lost:
            if (r6 != 3) goto L_0x005c;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTouchEvent(android.view.MotionEvent r6) {
            /*
                r5 = this;
                float r0 = r6.getRawX()
                float r1 = r6.getRawY()
                int r6 = r6.getAction()
                r2 = 0
                r3 = 1
                if (r6 == 0) goto L_0x004b
                if (r6 == r3) goto L_0x003b
                r4 = 2
                if (r6 == r4) goto L_0x0019
                r0 = 3
                if (r6 == r0) goto L_0x003b
                goto L_0x005c
            L_0x0019:
                boolean r6 = r5.mCanceledStashHint
                if (r6 != 0) goto L_0x005c
                float r6 = r5.mDownX
                float r6 = r6 - r0
                float r0 = r5.mDownY
                float r0 = r0 - r1
                float r6 = com.android.launcher3.Utilities.squaredHypot(r6, r0)
                float r0 = r5.mSquaredTouchSlop
                int r6 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
                if (r6 <= 0) goto L_0x005c
                com.android.launcher3.taskbar.TaskbarViewController r6 = com.android.launcher3.taskbar.TaskbarViewController.this
                com.android.launcher3.taskbar.TaskbarControllers r6 = r6.mControllers
                com.android.launcher3.taskbar.TaskbarStashController r6 = r6.taskbarStashController
                r6.startStashHint(r2)
                r5.mCanceledStashHint = r3
                return r3
            L_0x003b:
                boolean r6 = r5.mCanceledStashHint
                if (r6 != 0) goto L_0x005c
                com.android.launcher3.taskbar.TaskbarViewController r6 = com.android.launcher3.taskbar.TaskbarViewController.this
                com.android.launcher3.taskbar.TaskbarControllers r6 = r6.mControllers
                com.android.launcher3.taskbar.TaskbarStashController r6 = r6.taskbarStashController
                r6.startStashHint(r2)
                goto L_0x005c
            L_0x004b:
                r5.mDownX = r0
                r5.mDownY = r1
                com.android.launcher3.taskbar.TaskbarViewController r6 = com.android.launcher3.taskbar.TaskbarViewController.this
                com.android.launcher3.taskbar.TaskbarControllers r6 = r6.mControllers
                com.android.launcher3.taskbar.TaskbarStashController r6 = r6.taskbarStashController
                r6.startStashHint(r3)
                r5.mCanceledStashHint = r2
            L_0x005c:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.taskbar.TaskbarViewController.TaskbarViewCallbacks.onTouchEvent(android.view.MotionEvent):boolean");
        }
    }
}
