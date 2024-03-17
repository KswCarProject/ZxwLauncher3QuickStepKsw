package com.android.launcher3.folder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.widget.FrameLayout;
import com.android.launcher3.Alarm;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.CellLayout;
import com.android.launcher3.CheckLongPressHelper;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.DropTarget;
import com.android.launcher3.OnAlarmListener;
import com.android.launcher3.R;
import com.android.launcher3.Reorderable;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.dot.DotInfo;
import com.android.launcher3.dot.FolderDotInfo;
import com.android.launcher3.dragndrop.BaseItemDragListener;
import com.android.launcher3.dragndrop.DragView;
import com.android.launcher3.dragndrop.DraggableView;
import com.android.launcher3.icons.DotRenderer;
import com.android.launcher3.logger.LauncherAtom;
import com.android.launcher3.logging.InstanceId;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemFactory;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.touch.ItemClickHandler;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.views.IconLabelDotView;
import com.android.launcher3.widget.PendingAddShortcutInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class FolderIcon extends FrameLayout implements FolderInfo.FolderListener, IconLabelDotView, DraggableView, Reorderable {
    private static final Property<FolderIcon, Float> DOT_SCALE_PROPERTY = new Property<FolderIcon, Float>(Float.TYPE, "dotScale") {
        public Float get(FolderIcon folderIcon) {
            return Float.valueOf(folderIcon.mDotScale);
        }

        public void set(FolderIcon folderIcon, Float f) {
            float unused = folderIcon.mDotScale = f.floatValue();
            folderIcon.invalidate();
        }
    };
    static final int DROP_IN_ANIMATION_DURATION = 400;
    private static final int ON_OPEN_DELAY = 800;
    public static final boolean SPRING_LOADING_ENABLED = true;
    ActivityContext mActivity;
    boolean mAnimating = false;
    PreviewBackground mBackground = new PreviewBackground();
    private boolean mBackgroundIsVisible = true;
    private List<WorkspaceItemInfo> mCurrentPreviewItems = new ArrayList();
    @ViewDebug.ExportedProperty(category = "launcher", deepExport = true)
    private FolderDotInfo mDotInfo = new FolderDotInfo();
    @ViewDebug.ExportedProperty(category = "launcher", deepExport = true)
    private DotRenderer.DrawParams mDotParams;
    private DotRenderer mDotRenderer;
    /* access modifiers changed from: private */
    public float mDotScale;
    /* access modifiers changed from: private */
    public Animator mDotScaleAnim;
    Folder mFolder;
    BubbleTextView mFolderName;
    private boolean mForceHideDot;
    public FolderInfo mInfo;
    private CheckLongPressHelper mLongPressHelper;
    OnAlarmListener mOnOpenListener = new OnAlarmListener() {
        public void onAlarm(Alarm alarm) {
            FolderIcon.this.mFolder.beginExternalDrag();
        }
    };
    private Alarm mOpenAlarm = new Alarm();
    private PreviewItemManager mPreviewItemManager;
    ClippedFolderIconLayoutRule mPreviewLayoutRule;
    FolderGridOrganizer mPreviewVerifier;
    private float mScaleForReorderBounce = 1.0f;
    private PreviewItemDrawingParams mTmpParams = new PreviewItemDrawingParams(0.0f, 0.0f, 0.0f);
    private Rect mTouchArea = new Rect();
    private final PointF mTranslationForMoveFromCenterAnimation = new PointF(0.0f, 0.0f);
    private final PointF mTranslationForReorderBounce = new PointF(0.0f, 0.0f);
    private final PointF mTranslationForReorderPreview = new PointF(0.0f, 0.0f);
    private float mTranslationXForTaskbarAlignmentAnimation = 0.0f;

    public interface FolderIconParent {
        void clearFolderLeaveBehind(FolderIcon folderIcon);

        void drawFolderLeaveBehindForIcon(FolderIcon folderIcon);
    }

    public View getView() {
        return this;
    }

    public int getViewType() {
        return 0;
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

    public FolderIcon(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public FolderIcon(Context context) {
        super(context);
        init();
    }

    private void init() {
        this.mLongPressHelper = new CheckLongPressHelper(this);
        this.mPreviewLayoutRule = new ClippedFolderIconLayoutRule();
        this.mPreviewItemManager = new PreviewItemManager(this);
        this.mDotParams = new DotRenderer.DrawParams();
    }

    public static <T extends Context & ActivityContext> FolderIcon inflateFolderAndIcon(int i, T t, ViewGroup viewGroup, FolderInfo folderInfo) {
        Folder fromXml = Folder.fromXml(t);
        FolderIcon inflateIcon = inflateIcon(i, (ActivityContext) t, viewGroup, folderInfo);
        fromXml.setFolderIcon(inflateIcon);
        fromXml.bind(folderInfo);
        inflateIcon.setFolder(fromXml);
        return inflateIcon;
    }

    public static FolderIcon inflateIcon(int i, ActivityContext activityContext, ViewGroup viewGroup, FolderInfo folderInfo) {
        DeviceProfile deviceProfile = activityContext.getDeviceProfile();
        FolderIcon folderIcon = (FolderIcon) LayoutInflater.from(viewGroup.getContext()).inflate(i, viewGroup, false);
        folderIcon.setClipToPadding(false);
        BubbleTextView bubbleTextView = (BubbleTextView) folderIcon.findViewById(R.id.folder_icon_name);
        folderIcon.mFolderName = bubbleTextView;
        bubbleTextView.setText(folderInfo.title);
        folderIcon.mFolderName.setCompoundDrawablePadding(0);
        ((FrameLayout.LayoutParams) folderIcon.mFolderName.getLayoutParams()).topMargin = deviceProfile.iconSizePx + deviceProfile.iconDrawablePaddingPx;
        folderIcon.setTag(folderInfo);
        folderIcon.setOnClickListener(ItemClickHandler.INSTANCE);
        folderIcon.mInfo = folderInfo;
        folderIcon.mActivity = activityContext;
        folderIcon.mDotRenderer = deviceProfile.mDotRendererWorkSpace;
        folderIcon.setContentDescription(folderIcon.getAccessiblityTitle(folderInfo.title));
        FolderDotInfo folderDotInfo = new FolderDotInfo();
        Iterator<WorkspaceItemInfo> it = folderInfo.contents.iterator();
        while (it.hasNext()) {
            folderDotInfo.addDotInfo(activityContext.getDotInfoForItem(it.next()));
        }
        folderIcon.setDotInfo(folderDotInfo);
        folderIcon.setAccessibilityDelegate(activityContext.getAccessibilityDelegate());
        FolderGridOrganizer folderGridOrganizer = new FolderGridOrganizer(activityContext.getDeviceProfile().inv);
        folderIcon.mPreviewVerifier = folderGridOrganizer;
        folderGridOrganizer.setFolderInfo(folderInfo);
        folderIcon.updatePreviewItems(false);
        folderInfo.addListener(folderIcon);
        return folderIcon;
    }

    public void animateBgShadowAndStroke() {
        this.mBackground.fadeInBackgroundShadow();
        this.mBackground.animateBackgroundStroke();
    }

    public BubbleTextView getFolderName() {
        return this.mFolderName;
    }

    public void getPreviewBounds(Rect rect) {
        this.mPreviewItemManager.recomputePreviewDrawingParams();
        this.mBackground.getBounds(rect);
        Utilities.scaleRectAboutCenter(rect, 1.125f);
    }

    public float getBackgroundStrokeWidth() {
        return this.mBackground.getStrokeWidth();
    }

    public Folder getFolder() {
        return this.mFolder;
    }

    private void setFolder(Folder folder) {
        this.mFolder = folder;
    }

    private boolean willAcceptItem(ItemInfo itemInfo) {
        int i = itemInfo.itemType;
        if ((i == 0 || i == 1 || i == 6) && itemInfo != this.mInfo && !this.mFolder.isOpen()) {
            return true;
        }
        return false;
    }

    public boolean acceptDrop(ItemInfo itemInfo) {
        return !this.mFolder.isDestroyed() && willAcceptItem(itemInfo);
    }

    public void addItem(WorkspaceItemInfo workspaceItemInfo) {
        this.mInfo.add(workspaceItemInfo, true);
    }

    public void removeItem(WorkspaceItemInfo workspaceItemInfo, boolean z) {
        this.mInfo.remove(workspaceItemInfo, z);
    }

    public void onDragEnter(ItemInfo itemInfo) {
        if (!this.mFolder.isDestroyed() && willAcceptItem(itemInfo)) {
            CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) getLayoutParams();
            this.mBackground.animateToAccept((CellLayout) getParent().getParent(), layoutParams.cellX, layoutParams.cellY);
            this.mOpenAlarm.setOnAlarmListener(this.mOnOpenListener);
            if ((itemInfo instanceof WorkspaceItemFactory) || (itemInfo instanceof WorkspaceItemInfo) || (itemInfo instanceof PendingAddShortcutInfo)) {
                this.mOpenAlarm.setAlarm(800);
            }
        }
    }

    public Drawable prepareCreateAnimation(View view) {
        return this.mPreviewItemManager.prepareCreateAnimation(view);
    }

    public void performCreateAnimation(WorkspaceItemInfo workspaceItemInfo, View view, WorkspaceItemInfo workspaceItemInfo2, DropTarget.DragObject dragObject, Rect rect, float f) {
        DragView dragView = dragObject.dragView;
        prepareCreateAnimation(view);
        addItem(workspaceItemInfo);
        this.mPreviewItemManager.createFirstItemAnimation(false, (Runnable) null).start();
        onDrop(workspaceItemInfo2, dragObject, rect, f, 1, false);
    }

    public void performDestroyAnimation(Runnable runnable) {
        this.mPreviewItemManager.createFirstItemAnimation(true, runnable).start();
    }

    public void onDragExit() {
        this.mBackground.animateToRest();
        this.mOpenAlarm.cancelAlarm();
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x009e  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x00d3  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x00d5  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00de  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0108  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x011a  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x012e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void onDrop(com.android.launcher3.model.data.WorkspaceItemInfo r20, com.android.launcher3.DropTarget.DragObject r21, android.graphics.Rect r22, float r23, int r24, boolean r25) {
        /*
            r19 = this;
            r6 = r19
            r5 = r20
            r2 = r21
            r0 = r24
            r1 = -1
            r5.cellX = r1
            r5.cellY = r1
            com.android.launcher3.dragndrop.DragView r8 = r2.dragView
            if (r8 == 0) goto L_0x0135
            com.android.launcher3.views.ActivityContext r1 = r6.mActivity
            boolean r3 = r1 instanceof com.android.launcher3.Launcher
            if (r3 == 0) goto L_0x0135
            com.android.launcher3.Launcher r1 = (com.android.launcher3.Launcher) r1
            com.android.launcher3.dragndrop.DragLayer r7 = r1.getDragLayer()
            r3 = 1065353216(0x3f800000, float:1.0)
            if (r22 != 0) goto L_0x004a
            android.graphics.Rect r4 = new android.graphics.Rect
            r4.<init>()
            com.android.launcher3.Workspace r1 = r1.getWorkspace()
            r1.setFinalTransitionTransform()
            float r9 = r19.getScaleX()
            float r10 = r19.getScaleY()
            r6.setScaleX(r3)
            r6.setScaleY(r3)
            float r11 = r7.getDescendantRectRelativeToSelf(r6, r4)
            r6.setScaleX(r9)
            r6.setScaleY(r10)
            r1.resetTransitionTransform()
            r9 = r4
            goto L_0x004e
        L_0x004a:
            r9 = r22
            r11 = r23
        L_0x004e:
            int r1 = r0 + 1
            r4 = 4
            int r1 = java.lang.Math.min(r4, r1)
            r10 = 0
            r15 = 1
            if (r25 != 0) goto L_0x005b
            if (r0 < r4) goto L_0x009a
        L_0x005b:
            java.util.ArrayList r12 = new java.util.ArrayList
            java.util.List<com.android.launcher3.model.data.WorkspaceItemInfo> r13 = r6.mCurrentPreviewItems
            r12.<init>(r13)
            com.android.launcher3.model.data.FolderInfo r13 = r6.mInfo
            r13.add(r5, r0, r10)
            java.util.List<com.android.launcher3.model.data.WorkspaceItemInfo> r13 = r6.mCurrentPreviewItems
            r13.clear()
            java.util.List<com.android.launcher3.model.data.WorkspaceItemInfo> r13 = r6.mCurrentPreviewItems
            java.util.List r14 = r6.getPreviewItemsOnPage(r10)
            r13.addAll(r14)
            java.util.List<com.android.launcher3.model.data.WorkspaceItemInfo> r13 = r6.mCurrentPreviewItems
            boolean r13 = r12.equals(r13)
            if (r13 != 0) goto L_0x0097
            java.util.List<com.android.launcher3.model.data.WorkspaceItemInfo> r13 = r6.mCurrentPreviewItems
            int r13 = r13.indexOf(r5)
            if (r13 < 0) goto L_0x0086
            goto L_0x0087
        L_0x0086:
            r13 = r0
        L_0x0087:
            com.android.launcher3.folder.PreviewItemManager r0 = r6.mPreviewItemManager
            r0.hidePreviewItem(r13, r15)
            com.android.launcher3.folder.PreviewItemManager r0 = r6.mPreviewItemManager
            java.util.List<com.android.launcher3.model.data.WorkspaceItemInfo> r14 = r6.mCurrentPreviewItems
            r0.onDrop(r12, r14, r5)
            r0 = r13
            r18 = r15
            goto L_0x009c
        L_0x0097:
            r6.removeItem(r5, r10)
        L_0x009a:
            r18 = r10
        L_0x009c:
            if (r18 != 0) goto L_0x00a3
            com.android.launcher3.model.data.FolderInfo r12 = r6.mInfo
            r12.add(r5, r0, r15)
        L_0x00a3:
            r12 = 2
            int[] r13 = new int[r12]
            float r1 = r6.getLocalCenterForIndex(r0, r1, r13)
            r14 = r13[r10]
            float r14 = (float) r14
            float r14 = r14 * r11
            int r14 = java.lang.Math.round(r14)
            r13[r10] = r14
            r14 = r13[r15]
            float r14 = (float) r14
            float r14 = r14 * r11
            int r14 = java.lang.Math.round(r14)
            r13[r15] = r14
            r10 = r13[r10]
            int r14 = r8.getMeasuredWidth()
            int r14 = r14 / r12
            int r10 = r10 - r14
            r13 = r13[r15]
            int r14 = r8.getMeasuredHeight()
            int r14 = r14 / r12
            int r13 = r13 - r14
            r9.offset(r10, r13)
            if (r0 >= r4) goto L_0x00d5
            r10 = r3
            goto L_0x00d7
        L_0x00d5:
            r4 = 0
            r10 = r4
        L_0x00d7:
            float r1 = r1 * r11
            com.android.launcher3.DragSource r4 = r2.dragSource
            boolean r4 = r4 instanceof com.android.launcher3.allapps.ActivityAllAppsContainerView
            if (r4 == 0) goto L_0x00ed
            com.android.launcher3.views.ActivityContext r4 = r6.mActivity
            com.android.launcher3.DeviceProfile r4 = r4.getDeviceProfile()
            int r11 = r4.iconSizePx
            float r11 = (float) r11
            float r11 = r11 * r3
            int r3 = r4.allAppsIconSizePx
            float r3 = (float) r3
            float r11 = r11 / r3
            float r1 = r1 * r11
        L_0x00ed:
            r12 = r1
            r13 = 400(0x190, float:5.6E-43)
            android.view.animation.Interpolator r14 = com.android.launcher3.anim.Interpolators.DEACCEL_2
            com.android.launcher3.folder.-$$Lambda$FolderIcon$wNmKb9qzdlB7e6PXaM-w8WjmOIw r1 = new com.android.launcher3.folder.-$$Lambda$FolderIcon$wNmKb9qzdlB7e6PXaM-w8WjmOIw
            r1.<init>(r0, r5)
            r16 = 0
            r17 = 0
            r11 = r12
            r3 = r15
            r15 = r1
            r7.animateView(r8, r9, r10, r11, r12, r13, r14, r15, r16, r17)
            com.android.launcher3.folder.Folder r1 = r6.mFolder
            r1.hideItem(r5)
            if (r18 != 0) goto L_0x010d
            com.android.launcher3.folder.PreviewItemManager r1 = r6.mPreviewItemManager
            r1.hidePreviewItem(r0, r3)
        L_0x010d:
            com.android.launcher3.folder.FolderNameInfos r3 = new com.android.launcher3.folder.FolderNameInfos
            r3.<init>()
            com.android.launcher3.config.FeatureFlags$BooleanFlag r1 = com.android.launcher3.config.FeatureFlags.FOLDER_NAME_SUGGEST
            boolean r1 = r1.get()
            if (r1 == 0) goto L_0x012e
            com.android.launcher3.util.LooperExecutor r7 = com.android.launcher3.util.Executors.MODEL_EXECUTOR
            com.android.launcher3.folder.-$$Lambda$FolderIcon$8qYzRdS35-pYQHJZu6Uyps1ZCW0 r8 = new com.android.launcher3.folder.-$$Lambda$FolderIcon$8qYzRdS35-pYQHJZu6Uyps1ZCW0
            r13 = r0
            r0 = r8
            r1 = r19
            r2 = r21
            r4 = r13
            r5 = r20
            r0.<init>(r2, r3, r4, r5)
            r7.post(r8)
            goto L_0x0138
        L_0x012e:
            r13 = r0
            com.android.launcher3.logging.InstanceId r0 = r2.logInstanceId
            r6.showFinalView(r13, r5, r3, r0)
            goto L_0x0138
        L_0x0135:
            r19.addItem(r20)
        L_0x0138:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.folder.FolderIcon.onDrop(com.android.launcher3.model.data.WorkspaceItemInfo, com.android.launcher3.DropTarget$DragObject, android.graphics.Rect, float, int, boolean):void");
    }

    public /* synthetic */ void lambda$onDrop$0$FolderIcon(int i, WorkspaceItemInfo workspaceItemInfo) {
        this.mPreviewItemManager.hidePreviewItem(i, false);
        this.mFolder.showItem(workspaceItemInfo);
    }

    public /* synthetic */ void lambda$onDrop$1$FolderIcon(DropTarget.DragObject dragObject, FolderNameInfos folderNameInfos, int i, WorkspaceItemInfo workspaceItemInfo) {
        dragObject.folderNameProvider.getSuggestedFolderName(getContext(), this.mInfo.contents, folderNameInfos);
        showFinalView(i, workspaceItemInfo, folderNameInfos, dragObject.logInstanceId);
    }

    private void showFinalView(int i, WorkspaceItemInfo workspaceItemInfo, FolderNameInfos folderNameInfos, InstanceId instanceId) {
        postDelayed(new Runnable(folderNameInfos, instanceId) {
            public final /* synthetic */ FolderNameInfos f$1;
            public final /* synthetic */ InstanceId f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                FolderIcon.this.lambda$showFinalView$2$FolderIcon(this.f$1, this.f$2);
            }
        }, 400);
    }

    public /* synthetic */ void lambda$showFinalView$2$FolderIcon(FolderNameInfos folderNameInfos, InstanceId instanceId) {
        setLabelSuggestion(folderNameInfos, instanceId);
        invalidate();
    }

    public void setLabelSuggestion(FolderNameInfos folderNameInfos, InstanceId instanceId) {
        if (!FeatureFlags.FOLDER_NAME_SUGGEST.get() || !this.mInfo.getLabelState().equals(FolderInfo.LabelState.UNLABELED)) {
            return;
        }
        if (folderNameInfos == null || !folderNameInfos.hasSuggestions()) {
            StatsLogManager.newInstance(getContext()).logger().withInstanceId(instanceId).withItemInfo(this.mInfo).log(StatsLogManager.LauncherEvent.LAUNCHER_FOLDER_AUTO_LABELING_SKIPPED_EMPTY_SUGGESTIONS);
        } else if (!folderNameInfos.hasPrimary()) {
            StatsLogManager.newInstance(getContext()).logger().withInstanceId(instanceId).withItemInfo(this.mInfo).log(StatsLogManager.LauncherEvent.LAUNCHER_FOLDER_AUTO_LABELING_SKIPPED_EMPTY_PRIMARY);
        } else {
            CharSequence charSequence = folderNameInfos.getLabels()[0];
            LauncherAtom.FromState fromLabelState = this.mInfo.getFromLabelState();
            this.mInfo.setTitle(charSequence, this.mFolder.mLauncherDelegate.getModelWriter());
            onTitleChanged(this.mInfo.title);
            this.mFolder.mFolderName.setText(this.mInfo.title);
            StatsLogManager.newInstance(getContext()).logger().withInstanceId(instanceId).withItemInfo(this.mInfo).withFromState(fromLabelState).withToState(LauncherAtom.ToState.TO_SUGGESTION0).withEditText(charSequence.toString()).log(StatsLogManager.LauncherEvent.LAUNCHER_FOLDER_AUTO_LABELED);
        }
    }

    public void onDrop(DropTarget.DragObject dragObject, boolean z) {
        WorkspaceItemInfo workspaceItemInfo;
        if (dragObject.dragInfo instanceof WorkspaceItemFactory) {
            workspaceItemInfo = ((WorkspaceItemFactory) dragObject.dragInfo).makeWorkspaceItem(getContext());
        } else if (dragObject.dragSource instanceof BaseItemDragListener) {
            workspaceItemInfo = new WorkspaceItemInfo((WorkspaceItemInfo) dragObject.dragInfo);
        } else {
            workspaceItemInfo = (WorkspaceItemInfo) dragObject.dragInfo;
        }
        WorkspaceItemInfo workspaceItemInfo2 = workspaceItemInfo;
        this.mFolder.notifyDrop();
        onDrop(workspaceItemInfo2, dragObject, (Rect) null, 1.0f, z ? workspaceItemInfo2.rank : this.mInfo.contents.size(), z);
    }

    public void setDotInfo(FolderDotInfo folderDotInfo) {
        updateDotScale(this.mDotInfo.hasDot(), folderDotInfo.hasDot());
        this.mDotInfo = folderDotInfo;
    }

    public ClippedFolderIconLayoutRule getLayoutRule() {
        return this.mPreviewLayoutRule;
    }

    public void setForceHideDot(boolean z) {
        if (this.mForceHideDot != z) {
            this.mForceHideDot = z;
            if (z) {
                invalidate();
            } else if (hasDot()) {
                animateDotScale(0.0f, 1.0f);
            }
        }
    }

    private void updateDotScale(boolean z, boolean z2) {
        float f = z2 ? 1.0f : 0.0f;
        if (!(z ^ z2) || !isShown()) {
            cancelDotScaleAnim();
            this.mDotScale = f;
            invalidate();
            return;
        }
        animateDotScale(f);
    }

    private void cancelDotScaleAnim() {
        Animator animator = this.mDotScaleAnim;
        if (animator != null) {
            animator.cancel();
        }
    }

    public void animateDotScale(float... fArr) {
        cancelDotScaleAnim();
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, DOT_SCALE_PROPERTY, fArr);
        this.mDotScaleAnim = ofFloat;
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                Animator unused = FolderIcon.this.mDotScaleAnim = null;
            }
        });
        this.mDotScaleAnim.start();
    }

    public boolean hasDot() {
        FolderDotInfo folderDotInfo = this.mDotInfo;
        return folderDotInfo != null && folderDotInfo.hasDot();
    }

    private float getLocalCenterForIndex(int i, int i2, int[] iArr) {
        PreviewItemDrawingParams computePreviewItemDrawingParams = this.mPreviewItemManager.computePreviewItemDrawingParams(Math.min(4, i), i2, this.mTmpParams);
        this.mTmpParams = computePreviewItemDrawingParams;
        computePreviewItemDrawingParams.transX += (float) this.mBackground.basePreviewOffsetX;
        this.mTmpParams.transY += (float) this.mBackground.basePreviewOffsetY;
        float intrinsicIconSize = this.mPreviewItemManager.getIntrinsicIconSize();
        iArr[0] = Math.round(this.mTmpParams.transX + ((this.mTmpParams.scale * intrinsicIconSize) / 2.0f));
        iArr[1] = Math.round(this.mTmpParams.transY + ((this.mTmpParams.scale * intrinsicIconSize) / 2.0f));
        return this.mTmpParams.scale;
    }

    public void setFolderBackground(PreviewBackground previewBackground) {
        this.mBackground = previewBackground;
        previewBackground.setInvalidateDelegate(this);
    }

    public void setIconVisible(boolean z) {
        this.mBackgroundIsVisible = z;
        invalidate();
    }

    public boolean getIconVisible() {
        return this.mBackgroundIsVisible;
    }

    public PreviewBackground getFolderBackground() {
        return this.mBackground;
    }

    public PreviewItemManager getPreviewItemManager() {
        return this.mPreviewItemManager;
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.mBackgroundIsVisible) {
            this.mPreviewItemManager.recomputePreviewDrawingParams();
            if (!this.mBackground.drawingDelegated()) {
                this.mBackground.drawBackground(canvas);
            }
            if (!this.mCurrentPreviewItems.isEmpty() || this.mAnimating) {
                this.mPreviewItemManager.draw(canvas);
                if (!this.mBackground.drawingDelegated()) {
                    this.mBackground.drawBackgroundStroke(canvas);
                }
                drawDot(canvas);
            }
        }
    }

    public void drawDot(Canvas canvas) {
        if (!this.mForceHideDot) {
            FolderDotInfo folderDotInfo = this.mDotInfo;
            if ((folderDotInfo != null && folderDotInfo.hasDot()) || this.mDotScale > 0.0f) {
                Rect rect = this.mDotParams.iconBounds;
                Utilities.setRectToViewCenter(this, this.mActivity.getDeviceProfile().iconSizePx, rect);
                rect.offsetTo(rect.left, getPaddingTop());
                Utilities.scaleRectAboutCenter(rect, ((float) this.mBackground.previewSize) / ((float) rect.width()));
                this.mDotParams.scale = Math.max(0.0f, this.mDotScale - this.mBackground.getScaleProgress());
                this.mDotParams.dotColor = this.mBackground.getDotColor();
                this.mDotRenderer.draw(canvas, this.mDotParams);
            }
        }
    }

    public void setTextVisible(boolean z) {
        if (z) {
            this.mFolderName.setVisibility(0);
        } else {
            this.mFolderName.setVisibility(4);
        }
    }

    public boolean getTextVisible() {
        return this.mFolderName.getVisibility() == 0;
    }

    public List<WorkspaceItemInfo> getPreviewItemsOnPage(int i) {
        return this.mPreviewVerifier.setFolderInfo(this.mInfo).previewItemsForPage(i, this.mInfo.contents);
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable drawable) {
        return this.mPreviewItemManager.verifyDrawable(drawable) || super.verifyDrawable(drawable);
    }

    public void onItemsChanged(boolean z) {
        updatePreviewItems(z);
        invalidate();
        requestLayout();
    }

    private void updatePreviewItems(boolean z) {
        this.mPreviewItemManager.updatePreviewItems(z);
        this.mCurrentPreviewItems.clear();
        this.mCurrentPreviewItems.addAll(getPreviewItemsOnPage(0));
    }

    public void updatePreviewItems(Predicate<WorkspaceItemInfo> predicate) {
        this.mPreviewItemManager.updatePreviewItems(predicate);
    }

    public void onAdd(WorkspaceItemInfo workspaceItemInfo, int i) {
        updatePreviewItems(false);
        boolean hasDot = this.mDotInfo.hasDot();
        this.mDotInfo.addDotInfo(this.mActivity.getDotInfoForItem(workspaceItemInfo));
        updateDotScale(hasDot, this.mDotInfo.hasDot());
        setContentDescription(getAccessiblityTitle(this.mInfo.title));
        invalidate();
        requestLayout();
    }

    public void onRemove(List<WorkspaceItemInfo> list) {
        updatePreviewItems(false);
        boolean hasDot = this.mDotInfo.hasDot();
        Stream stream = list.stream();
        ActivityContext activityContext = this.mActivity;
        Objects.requireNonNull(activityContext);
        Stream map = stream.map(new Function() {
            public final Object apply(Object obj) {
                return ActivityContext.this.getDotInfoForItem((WorkspaceItemInfo) obj);
            }
        });
        FolderDotInfo folderDotInfo = this.mDotInfo;
        Objects.requireNonNull(folderDotInfo);
        map.forEach(new Consumer() {
            public final void accept(Object obj) {
                FolderDotInfo.this.subtractDotInfo((DotInfo) obj);
            }
        });
        updateDotScale(hasDot, this.mDotInfo.hasDot());
        setContentDescription(getAccessiblityTitle(this.mInfo.title));
        invalidate();
        requestLayout();
    }

    public void onTitleChanged(CharSequence charSequence) {
        this.mFolderName.setText(charSequence);
        setContentDescription(getAccessiblityTitle(charSequence));
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0 && shouldIgnoreTouchDown(motionEvent.getX(), motionEvent.getY())) {
            return false;
        }
        super.onTouchEvent(motionEvent);
        this.mLongPressHelper.onTouchEvent(motionEvent);
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean shouldIgnoreTouchDown(float f, float f2) {
        this.mTouchArea.set(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        return !this.mTouchArea.contains((int) f, (int) f2);
    }

    public void cancelLongPress() {
        super.cancelLongPress();
        this.mLongPressHelper.cancelLongPress();
    }

    public void removeListeners() {
        this.mInfo.removeListener(this);
        this.mInfo.removeListener(this.mFolder);
    }

    private boolean isInHotseat() {
        return this.mInfo.container == -101;
    }

    public void clearLeaveBehindIfExists() {
        if (getParent() instanceof FolderIconParent) {
            ((FolderIconParent) getParent()).clearFolderLeaveBehind(this);
        }
    }

    public void drawLeaveBehindIfExists() {
        if (getParent() instanceof FolderIconParent) {
            ((FolderIconParent) getParent()).drawFolderLeaveBehindForIcon(this);
        }
    }

    public void onFolderClose(int i) {
        this.mPreviewItemManager.onFolderClose(i);
    }

    private void updateTranslation() {
        super.setTranslationX(this.mTranslationForReorderBounce.x + this.mTranslationForReorderPreview.x + this.mTranslationForMoveFromCenterAnimation.x + this.mTranslationXForTaskbarAlignmentAnimation);
        super.setTranslationY(this.mTranslationForReorderBounce.y + this.mTranslationForReorderPreview.y + this.mTranslationForMoveFromCenterAnimation.y);
    }

    public void setReorderBounceOffset(float f, float f2) {
        this.mTranslationForReorderBounce.set(f, f2);
        updateTranslation();
    }

    public void getReorderBounceOffset(PointF pointF) {
        pointF.set(this.mTranslationForReorderBounce);
    }

    public void setTranslationForTaskbarAlignmentAnimation(float f) {
        this.mTranslationXForTaskbarAlignmentAnimation = f;
        updateTranslation();
    }

    public float getTranslationXForTaskbarAlignmentAnimation() {
        return this.mTranslationXForTaskbarAlignmentAnimation;
    }

    public void setTranslationForMoveFromCenterAnimation(float f, float f2) {
        this.mTranslationForMoveFromCenterAnimation.set(f, f2);
        updateTranslation();
    }

    public void setReorderPreviewOffset(float f, float f2) {
        this.mTranslationForReorderPreview.set(f, f2);
        updateTranslation();
    }

    public void getReorderPreviewOffset(PointF pointF) {
        pointF.set(this.mTranslationForReorderPreview);
    }

    public void setReorderBounceScale(float f) {
        this.mScaleForReorderBounce = f;
        super.setScaleX(f);
        super.setScaleY(f);
    }

    public float getReorderBounceScale() {
        return this.mScaleForReorderBounce;
    }

    public void getWorkspaceVisualDragBounds(Rect rect) {
        getPreviewBounds(rect);
    }

    public String getAccessiblityTitle(CharSequence charSequence) {
        int size = this.mInfo.contents.size();
        if (size < 4) {
            return getContext().getString(R.string.folder_name_format_exact, new Object[]{charSequence, Integer.valueOf(size)});
        }
        return getContext().getString(R.string.folder_name_format_overflow, new Object[]{charSequence, 4});
    }
}
