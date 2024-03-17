package com.android.launcher3.taskbar;

import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.internal.view.SupportMenu;
import com.android.internal.logging.InstanceId;
import com.android.internal.logging.InstanceIdSequence;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.R;
import com.android.launcher3.accessibility.DragViewStateAnnouncer;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragDriver;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.dragndrop.DragView;
import com.android.launcher3.dragndrop.DraggableView;
import com.android.launcher3.graphics.DragPreviewProvider;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.popup.PopupContainerWithArrow;
import com.android.launcher3.shortcuts.DeepShortcutView;
import com.android.launcher3.shortcuts.ShortcutDragPreviewProvider;
import com.android.launcher3.taskbar.TaskbarControllers;
import com.android.launcher3.taskbar.TaskbarDragController;
import com.android.launcher3.testing.TestLogging;
import com.android.launcher3.testing.TestProtocol;
import com.android.systemui.shared.recents.model.Task;
import java.io.PrintWriter;
import java.util.Arrays;

public class TaskbarDragController extends DragController<BaseTaskbarContext> implements TaskbarControllers.LoggableTaskbarController {
    /* access modifiers changed from: private */
    public static boolean DEBUG_DRAG_SHADOW_SURFACE = false;
    TaskbarControllers mControllers;
    /* access modifiers changed from: private */
    public final int mDragIconSize = ((BaseTaskbarContext) this.mActivity).getResources().getDimensionPixelSize(R.dimen.taskbar_icon_drag_icon_size);
    private boolean mIsSystemDragInProgress;
    /* access modifiers changed from: private */
    public int mRegistrationX;
    /* access modifiers changed from: private */
    public int mRegistrationY;
    /* access modifiers changed from: private */
    public ValueAnimator mReturnAnimator;
    private final int[] mTempXY = new int[2];

    static /* synthetic */ void lambda$startDrag$2(MotionEvent motionEvent) {
    }

    static /* synthetic */ void lambda$startInternalDrag$1(View view, DropTarget.DragObject dragObject, boolean z) {
    }

    public void addDropTarget(DropTarget dropTarget) {
    }

    /* access modifiers changed from: protected */
    public DropTarget getDefaultDropTarget(int[] iArr) {
        return null;
    }

    public TaskbarDragController(BaseTaskbarContext baseTaskbarContext) {
        super(baseTaskbarContext);
    }

    public void init(TaskbarControllers taskbarControllers) {
        this.mControllers = taskbarControllers;
    }

    public boolean startDragOnLongClick(View view) {
        return startDragOnLongClick(view, (DragPreviewProvider) null, (Point) null);
    }

    /* access modifiers changed from: protected */
    public boolean startDragOnLongClick(DeepShortcutView deepShortcutView, Point point) {
        return startDragOnLongClick(deepShortcutView.getBubbleText(), new ShortcutDragPreviewProvider(deepShortcutView.getIconView(), point), point);
    }

    private boolean startDragOnLongClick(View view, DragPreviewProvider dragPreviewProvider, Point point) {
        if (!(view instanceof BubbleTextView)) {
            return false;
        }
        TestLogging.recordEvent(TestProtocol.SEQUENCE_MAIN, "onTaskbarItemLongClick");
        BubbleTextView bubbleTextView = (BubbleTextView) view;
        ((BaseTaskbarContext) this.mActivity).onDragStart();
        bubbleTextView.post(new Runnable(bubbleTextView, dragPreviewProvider, point) {
            public final /* synthetic */ BubbleTextView f$1;
            public final /* synthetic */ DragPreviewProvider f$2;
            public final /* synthetic */ Point f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                TaskbarDragController.this.lambda$startDragOnLongClick$0$TaskbarDragController(this.f$1, this.f$2, this.f$3);
            }
        });
        return true;
    }

    public /* synthetic */ void lambda$startDragOnLongClick$0$TaskbarDragController(BubbleTextView bubbleTextView, DragPreviewProvider dragPreviewProvider, Point point) {
        DragView startInternalDrag = startInternalDrag(bubbleTextView, dragPreviewProvider);
        if (point != null) {
            startInternalDrag.animateShift(-point.x, -point.y);
        }
        bubbleTextView.getIcon().setIsDisabled(true);
        this.mControllers.taskbarAutohideSuspendController.updateFlag(2, true);
    }

    private DragView startInternalDrag(BubbleTextView bubbleTextView, DragPreviewProvider dragPreviewProvider) {
        PopupContainerWithArrow<BaseTaskbarContext> showForIcon;
        BubbleTextView bubbleTextView2 = bubbleTextView;
        float animatedScale = bubbleTextView.getIcon().getAnimatedScale();
        bubbleTextView.clearFocus();
        bubbleTextView2.setPressed(false);
        bubbleTextView.clearPressedBackground();
        DragPreviewProvider dragPreviewProvider2 = dragPreviewProvider == null ? new DragPreviewProvider(bubbleTextView2) : dragPreviewProvider;
        Drawable createDrawable = dragPreviewProvider2.createDrawable();
        float scaleAndPosition = dragPreviewProvider2.getScaleAndPosition(createDrawable, this.mTempXY);
        int[] iArr = this.mTempXY;
        int i = iArr[0];
        int i2 = iArr[1];
        Rect rect = new Rect();
        bubbleTextView2.getSourceVisualDragBounds(rect);
        int i3 = rect.top + i2;
        DragOptions dragOptions = new DragOptions();
        dragOptions.preDragCondition = null;
        if (FeatureFlags.ENABLE_TASKBAR_POPUP_MENU.get() && (showForIcon = this.mControllers.taskbarPopupController.showForIcon(bubbleTextView2)) != null) {
            dragOptions.preDragCondition = showForIcon.createPreDragCondition(false);
        }
        if (dragOptions.preDragCondition == null) {
            dragOptions.preDragCondition = new DragOptions.PreDragCondition() {
                private DragView mDragView;

                public boolean shouldStartDrag(double d) {
                    DragView dragView = this.mDragView;
                    return dragView != null && dragView.isAnimationFinished();
                }

                public void onPreDragStart(DropTarget.DragObject dragObject) {
                    this.mDragView = dragObject.dragView;
                    if (FeatureFlags.ENABLE_TASKBAR_POPUP_MENU.get() && !shouldStartDrag(0.0d)) {
                        this.mDragView.setOnAnimationEndCallback(new Runnable() {
                            public final void run() {
                                TaskbarDragController.AnonymousClass1.this.lambda$onPreDragStart$0$TaskbarDragController$1();
                            }
                        });
                    }
                }

                public /* synthetic */ void lambda$onPreDragStart$0$TaskbarDragController$1() {
                    TaskbarDragController.this.callOnDragStart();
                }

                public void onPreDragEnd(DropTarget.DragObject dragObject, boolean z) {
                    this.mDragView = null;
                }
            };
        }
        return startDrag(createDrawable, (View) null, bubbleTextView, i, i3, $$Lambda$TaskbarDragController$GAZ5HKHQSsfvE2nE9EcXp5bRtSo.INSTANCE, (ItemInfo) bubbleTextView.getTag(), (Point) null, rect, scaleAndPosition * animatedScale, scaleAndPosition, dragOptions);
    }

    /* access modifiers changed from: protected */
    public DragView startDrag(Drawable drawable, View view, DraggableView draggableView, int i, int i2, DragSource dragSource, ItemInfo itemInfo, Point point, Rect rect, float f, float f2, DragOptions dragOptions) {
        int i3;
        int i4;
        ItemInfo itemInfo2 = itemInfo;
        Rect rect2 = rect;
        this.mOptions = dragOptions;
        this.mRegistrationX = this.mMotionDown.x - i;
        this.mRegistrationY = this.mMotionDown.y - i2;
        if (rect2 == null) {
            i3 = 0;
        } else {
            i3 = rect2.left;
        }
        if (rect2 == null) {
            i4 = 0;
        } else {
            i4 = rect2.top;
        }
        this.mLastDropTarget = null;
        this.mDragObject = new DropTarget.DragObject(((BaseTaskbarContext) this.mActivity).getApplicationContext());
        this.mDragObject.originalView = draggableView;
        this.mDragObject.deferDragViewCleanupPostAnimation = false;
        this.mIsInPreDrag = this.mOptions.preDragCondition != null && !this.mOptions.preDragCondition.shouldStartDrag(0.0d);
        float width = (float) (this.mDragIconSize - rect.width());
        DropTarget.DragObject dragObject = this.mDragObject;
        TaskbarDragView taskbarDragView = new TaskbarDragView((BaseTaskbarContext) this.mActivity, drawable, this.mRegistrationX, this.mRegistrationY, f, f2, width);
        dragObject.dragView = taskbarDragView;
        taskbarDragView.setItemInfo(itemInfo2);
        this.mDragObject.dragComplete = false;
        this.mDragObject.xOffset = this.mMotionDown.x - (i + i3);
        this.mDragObject.yOffset = this.mMotionDown.y - (i2 + i4);
        this.mDragDriver = DragDriver.create(this, this.mOptions, $$Lambda$TaskbarDragController$0jORGFlUrqLcqhjDn6B7xMYX96w.INSTANCE);
        if (!this.mOptions.isAccessibleDrag) {
            this.mDragObject.stateAnnouncer = DragViewStateAnnouncer.createFor(taskbarDragView);
        }
        this.mDragObject.dragSource = dragSource;
        this.mDragObject.dragInfo = itemInfo2;
        this.mDragObject.originalDragInfo = this.mDragObject.dragInfo.makeShallowCopy();
        if (rect2 != null) {
            taskbarDragView.setDragRegion(new Rect(rect2));
        }
        taskbarDragView.show(this.mLastTouch.x, this.mLastTouch.y);
        this.mDistanceSinceScroll = 0;
        if (!this.mIsInPreDrag) {
            callOnDragStart();
        } else if (this.mOptions.preDragCondition != null) {
            this.mOptions.preDragCondition.onPreDragStart(this.mDragObject);
        }
        handleMoveEvent(this.mLastTouch.x, this.mLastTouch.y);
        return taskbarDragView;
    }

    /* access modifiers changed from: protected */
    public void callOnDragStart() {
        super.callOnDragStart();
        AbstractFloatingView.closeAllOpenViews(this.mActivity);
        startSystemDrag((BubbleTextView) this.mDragObject.originalView);
    }

    private void startSystemDrag(final BubbleTextView bubbleTextView) {
        Intent intent;
        ClipDescription clipDescription;
        AnonymousClass2 r0 = new View.DragShadowBuilder(bubbleTextView) {
            public void onProvideShadowMetrics(Point point, Point point2) {
                int max = Math.max(TaskbarDragController.this.mDragIconSize, bubbleTextView.getWidth());
                point.set(max, max);
                point2.set(TaskbarDragController.this.mRegistrationX + ((TaskbarDragController.this.mDragIconSize - TaskbarDragController.this.mDragObject.dragView.getDragRegionWidth()) / 2), TaskbarDragController.this.mRegistrationY + ((TaskbarDragController.this.mDragIconSize - TaskbarDragController.this.mDragObject.dragView.getDragRegionHeight()) / 2));
            }

            public void onDrawShadow(Canvas canvas) {
                canvas.save();
                if (TaskbarDragController.DEBUG_DRAG_SHADOW_SURFACE) {
                    canvas.drawColor(SupportMenu.CATEGORY_MASK);
                }
                float scaleX = TaskbarDragController.this.mDragObject.dragView.getScaleX();
                canvas.scale(scaleX, scaleX);
                TaskbarDragController.this.mDragObject.dragView.draw(canvas);
                canvas.restore();
            }
        };
        Object tag = bubbleTextView.getTag();
        if (tag instanceof ItemInfo) {
            ItemInfo itemInfo = (ItemInfo) tag;
            LauncherApps launcherApps = (LauncherApps) ((BaseTaskbarContext) this.mActivity).getSystemService(LauncherApps.class);
            CharSequence charSequence = itemInfo.title;
            String[] strArr = new String[1];
            strArr[0] = itemInfo.itemType == 6 ? "application/vnd.android.shortcut" : "application/vnd.android.activity";
            clipDescription = new ClipDescription(charSequence, strArr);
            intent = new Intent();
            if (itemInfo.itemType == 6) {
                String deepShortcutId = ((WorkspaceItemInfo) itemInfo).getDeepShortcutId();
                intent.putExtra("android.intent.extra.PENDING_INTENT", launcherApps.getShortcutIntent(itemInfo.getIntent().getPackage(), deepShortcutId, (Bundle) null, itemInfo.user));
                intent.putExtra("android.intent.extra.PACKAGE_NAME", itemInfo.getIntent().getPackage());
                intent.putExtra(ShortcutManagerCompat.EXTRA_SHORTCUT_ID, deepShortcutId);
            } else {
                intent.putExtra("android.intent.extra.PENDING_INTENT", launcherApps.getMainActivityLaunchIntent(itemInfo.getIntent().getComponent(), (Bundle) null, itemInfo.user));
            }
            intent.putExtra("android.intent.extra.USER", itemInfo.user);
        } else if (tag instanceof Task) {
            Task task = (Task) tag;
            clipDescription = new ClipDescription(task.titleDescription, new String[]{"application/vnd.android.task"});
            intent = new Intent();
            intent.putExtra("android.intent.extra.TASK_ID", task.key.id);
            intent.putExtra("android.intent.extra.USER", UserHandle.of(task.key.userId));
        } else {
            clipDescription = null;
            intent = null;
        }
        if (clipDescription != null && intent != null) {
            InstanceId newInstanceId = new InstanceIdSequence(1048576).newInstanceId();
            com.android.launcher3.logging.InstanceId instanceId = new com.android.launcher3.logging.InstanceId(newInstanceId.getId());
            intent.putExtra("android.intent.extra.LOGGING_INSTANCE_ID", newInstanceId);
            if (bubbleTextView.startDragAndDrop(new ClipData(clipDescription, new ClipData.Item(intent)), r0, (Object) null, 2816)) {
                onSystemDragStarted(bubbleTextView);
                ((BaseTaskbarContext) this.mActivity).getStatsLogManager().logger().withItemInfo(this.mDragObject.dragInfo).withInstanceId(instanceId).log(StatsLogManager.LauncherEvent.LAUNCHER_ITEM_DRAG_STARTED);
            }
        }
    }

    private void onSystemDragStarted(BubbleTextView bubbleTextView) {
        this.mIsSystemDragInProgress = true;
        ((BaseTaskbarContext) this.mActivity).getDragLayer().setOnDragListener(new View.OnDragListener(bubbleTextView) {
            public final /* synthetic */ BubbleTextView f$1;

            {
                this.f$1 = r2;
            }

            public final boolean onDrag(View view, DragEvent dragEvent) {
                return TaskbarDragController.this.lambda$onSystemDragStarted$3$TaskbarDragController(this.f$1, view, dragEvent);
            }
        });
    }

    public /* synthetic */ boolean lambda$onSystemDragStarted$3$TaskbarDragController(BubbleTextView bubbleTextView, View view, DragEvent dragEvent) {
        int action = dragEvent.getAction();
        if (action != 1) {
            if (action != 4) {
                return false;
            }
            this.mIsSystemDragInProgress = false;
            if (dragEvent.getResult()) {
                maybeOnDragEnd();
            } else {
                animateGlobalDragViewToOriginalPosition(bubbleTextView, dragEvent);
            }
        }
        return true;
    }

    public boolean isDragging() {
        return super.isDragging() || this.mIsSystemDragInProgress;
    }

    public boolean isSystemDragInProgress() {
        return this.mIsSystemDragInProgress;
    }

    /* access modifiers changed from: private */
    public void maybeOnDragEnd() {
        if (!isDragging()) {
            ((BubbleTextView) this.mDragObject.originalView).getIcon().setIsDisabled(false);
            this.mControllers.taskbarAutohideSuspendController.updateFlag(2, false);
            ((BaseTaskbarContext) this.mActivity).onDragEnd();
        }
    }

    /* access modifiers changed from: protected */
    public void callOnDragEnd() {
        super.callOnDragEnd();
        maybeOnDragEnd();
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0057  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x007e  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0081  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void animateGlobalDragViewToOriginalPosition(com.android.launcher3.BubbleTextView r13, android.view.DragEvent r14) {
        /*
            r12 = this;
            android.view.SurfaceControl r9 = r14.getDragSurface()
            java.lang.Object r0 = r13.getTag()
            boolean r1 = r0 instanceof com.android.launcher3.model.data.ItemInfo
            if (r1 == 0) goto L_0x0052
            com.android.launcher3.model.data.ItemInfo r0 = (com.android.launcher3.model.data.ItemInfo) r0
            com.android.launcher3.taskbar.TaskbarControllers r1 = r12.mControllers
            com.android.launcher3.taskbar.TaskbarViewController r1 = r1.taskbarViewController
            int r2 = r0.container
            r3 = -104(0xffffffffffffff98, float:NaN)
            if (r2 != r3) goto L_0x001d
            android.view.View r0 = r1.getAllAppsButtonView()
            goto L_0x0053
        L_0x001d:
            int r2 = r0.container
            if (r2 < 0) goto L_0x003a
            r2 = 1
            int[] r2 = new int[r2]
            r3 = 0
            int r0 = r0.id
            r2[r3] = r0
            com.android.launcher3.util.IntSet r0 = com.android.launcher3.util.IntSet.wrap((int[]) r2)
            java.util.function.Predicate r0 = com.android.launcher3.util.ItemInfoMatcher.ofItemIds(r0)
            java.util.function.Predicate r0 = com.android.launcher3.util.ItemInfoMatcher.forFolderMatch(r0)
            android.view.View r0 = r1.getFirstIconMatch(r0)
            goto L_0x0053
        L_0x003a:
            int r2 = r0.itemType
            r3 = 6
            if (r2 != r3) goto L_0x0052
            java.lang.String r2 = r0.getTargetPackage()
            java.util.Set r2 = java.util.Collections.singleton(r2)
            android.os.UserHandle r0 = r0.user
            java.util.function.Predicate r0 = com.android.launcher3.util.ItemInfoMatcher.ofPackages(r2, r0)
            android.view.View r0 = r1.getFirstIconMatch(r0)
            goto L_0x0053
        L_0x0052:
            r0 = r13
        L_0x0053:
            android.animation.ValueAnimator r1 = r12.mReturnAnimator
            if (r1 == 0) goto L_0x005a
            r1.end()
        L_0x005a:
            float r1 = r14.getX()
            float r2 = r14.getOffsetX()
            float r6 = r1 - r2
            float r1 = r14.getY()
            float r14 = r14.getOffsetY()
            float r8 = r1 - r14
            int[] r7 = r0.getLocationOnScreen()
            int r14 = r0.getWidth()
            float r14 = (float) r14
            int r1 = r12.mDragIconSize
            float r1 = (float) r1
            float r2 = r14 / r1
            if (r0 != r13) goto L_0x0081
            r13 = 1065353216(0x3f800000, float:1.0)
            goto L_0x0082
        L_0x0081:
            r13 = 0
        L_0x0082:
            r3 = r13
            android.view.ViewRootImpl r13 = r0.getViewRootImpl()
            android.view.SurfaceControl$Transaction r14 = new android.view.SurfaceControl$Transaction
            r14.<init>()
            r0 = 2
            float[] r0 = new float[r0]
            r0 = {0, 1065353216} // fill-array
            android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r0)
            r12.mReturnAnimator = r0
            r4 = 300(0x12c, double:1.48E-321)
            r0.setDuration(r4)
            android.animation.ValueAnimator r0 = r12.mReturnAnimator
            android.view.animation.Interpolator r1 = com.android.launcher3.anim.Interpolators.FAST_OUT_SLOW_IN
            r0.setInterpolator(r1)
            android.animation.ValueAnimator r10 = r12.mReturnAnimator
            com.android.launcher3.taskbar.TaskbarDragController$3 r11 = new com.android.launcher3.taskbar.TaskbarDragController$3
            r0 = r11
            r1 = r12
            r4 = r14
            r5 = r9
            r0.<init>(r2, r3, r4, r5, r6, r7, r8)
            r10.addUpdateListener(r11)
            android.animation.ValueAnimator r0 = r12.mReturnAnimator
            com.android.launcher3.taskbar.TaskbarDragController$4 r1 = new com.android.launcher3.taskbar.TaskbarDragController$4
            r1.<init>(r14, r9, r13)
            r0.addListener(r1)
            android.animation.ValueAnimator r13 = r12.mReturnAnimator
            r13.start()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.taskbar.TaskbarDragController.animateGlobalDragViewToOriginalPosition(com.android.launcher3.BubbleTextView, android.view.DragEvent):void");
    }

    /* access modifiers changed from: protected */
    public float getX(MotionEvent motionEvent) {
        return motionEvent.getRawX();
    }

    /* access modifiers changed from: protected */
    public float getY(MotionEvent motionEvent) {
        return motionEvent.getRawY();
    }

    /* access modifiers changed from: protected */
    public Point getClampedDragLayerPos(float f, float f2) {
        this.mTmpPoint.set(Math.round(f), Math.round(f2));
        return this.mTmpPoint;
    }

    /* access modifiers changed from: protected */
    public void exitDrag() {
        if (this.mDragObject != null) {
            ((BaseTaskbarContext) this.mActivity).getDragLayer().removeView(this.mDragObject.dragView);
        }
    }

    public void dumpLogs(String str, PrintWriter printWriter) {
        printWriter.println(str + "TaskbarDragController:");
        printWriter.println(String.format("%s\tmDragIconSize=%dpx", new Object[]{str, Integer.valueOf(this.mDragIconSize)}));
        printWriter.println(String.format("%s\tmTempXY=%s", new Object[]{str, Arrays.toString(this.mTempXY)}));
        printWriter.println(String.format("%s\tmRegistrationX=%d", new Object[]{str, Integer.valueOf(this.mRegistrationX)}));
        printWriter.println(String.format("%s\tmRegistrationY=%d", new Object[]{str, Integer.valueOf(this.mRegistrationY)}));
        printWriter.println(String.format("%s\tmIsSystemDragInProgress=%b", new Object[]{str, Boolean.valueOf(this.mIsSystemDragInProgress)}));
        printWriter.println(String.format("%s\tisInternalDragInProgess=%b", new Object[]{str, Boolean.valueOf(super.isDragging())}));
    }
}
