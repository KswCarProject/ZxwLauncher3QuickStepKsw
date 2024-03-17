package com.android.launcher3;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import com.android.launcher3.icons.DotRenderer;
import com.android.launcher3.icons.IconNormalizer;
import com.android.launcher3.uioverrides.ApiWrapper;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.WindowBounds;
import java.io.PrintWriter;
import java.util.List;

public class DeviceProfile {
    private static final int DEFAULT_DOT_SIZE = 100;
    private static final float MAX_HORIZONTAL_PADDING_PERCENT = 0.14f;
    private static final float TALLER_DEVICE_ASPECT_RATIO_THRESHOLD = 2.15f;
    private static final float TALL_DEVICE_ASPECT_RATIO_THRESHOLD = 2.0f;
    private static final float TALL_DEVICE_EXTRA_SPACE_THRESHOLD_DP = 252.0f;
    private static final float TALL_DEVICE_MORE_EXTRA_SPACE_THRESHOLD_DP = 268.0f;
    public Point allAppsBorderSpacePx;
    public int allAppsCellHeightPx;
    public int allAppsCellWidthPx;
    public int allAppsIconDrawablePaddingPx;
    public int allAppsIconSizePx;
    public float allAppsIconTextSizePx;
    public int allAppsLeftRightMargin;
    public int allAppsLeftRightPadding;
    public int allAppsShiftRange;
    public int allAppsTopPadding;
    public final PointF appWidgetScale = new PointF(1.0f, 1.0f);
    private final boolean areNavButtonsInline;
    public final float aspectRatio;
    public final int availableHeightPx;
    public final int availableWidthPx;
    public int bottomSheetTopPadding;
    public int cellHeightPx;
    public Point cellLayoutBorderSpaceOriginalPx;
    public Point cellLayoutBorderSpacePx;
    public Rect cellLayoutPaddingPx = new Rect();
    public float cellScaleToFit;
    public int cellWidthPx;
    public int cellYPaddingPx;
    public final int desiredWorkspaceHorizontalMarginOriginalPx;
    public int desiredWorkspaceHorizontalMarginPx;
    public int dropTargetBarBottomMarginPx;
    public int dropTargetBarSizePx;
    public int dropTargetBarTopMarginPx;
    public int dropTargetButtonWorkspaceEdgeGapPx;
    public int dropTargetDragPaddingPx;
    public int dropTargetGapPx;
    public int dropTargetHorizontalPaddingPx;
    public int dropTargetTextSizePx;
    public int dropTargetVerticalPaddingPx;
    public final int edgeMarginPx;
    public int extraHotseatBottomPadding;
    private final int extraSpace;
    public int flingToDeleteThresholdVelocity;
    public int folderCellHeightPx;
    public int folderCellLayoutBorderSpaceOriginalPx;
    public Point folderCellLayoutBorderSpacePx;
    public int folderCellWidthPx;
    public int folderChildDrawablePaddingPx;
    public int folderChildIconSizePx;
    public int folderChildTextSizePx;
    public int folderContentPaddingLeftRight;
    public int folderContentPaddingTop;
    public int folderIconOffsetYPx;
    public int folderIconSizePx;
    public float folderLabelTextScale;
    public int folderLabelTextSizePx;
    public int gridVisualizationPaddingX;
    public int gridVisualizationPaddingY;
    public final int heightPx;
    public final int hotseatBarBottomPaddingPx;
    public final int hotseatBarSidePaddingEndPx;
    public final int hotseatBarSidePaddingStartPx;
    public int hotseatBarSizeExtraSpacePx;
    public int hotseatBarSizePx;
    public int hotseatBarTopPaddingPx;
    public int hotseatBorderSpace;
    public int hotseatCellHeightPx;
    private final int hotseatExtraVerticalSize;
    public final int hotseatQsbHeight;
    public int iconDrawablePaddingOriginalPx;
    public int iconDrawablePaddingPx;
    public float iconScale;
    public int iconSizePx;
    public int iconTextSizePx;
    public final InvariantDeviceProfile inv;
    public final boolean isGestureMode;
    public final boolean isLandscape;
    public final boolean isMultiWindowMode;
    public final boolean isPhone;
    public final boolean isQsbInline;
    public final boolean isScalableGrid;
    public final boolean isTablet;
    public boolean isTaskbarPresent;
    public boolean isTaskbarPresentInApps;
    public final boolean isTwoPanels;
    public DotRenderer mDotRendererAllApps;
    public DotRenderer mDotRendererWorkSpace;
    private final Rect mHotseatPadding;
    private final DisplayController.Info mInfo;
    private final Rect mInsets;
    private boolean mIsSeascape;
    private final DisplayMetrics mMetrics;
    private final float mQsbCenterFactor;
    private final int mTypeIndex;
    private final int mWorkspacePageIndicatorOverlapWorkspace;
    public final int numShownAllAppsColumns;
    public final int numShownHotseatIcons;
    public final int overviewActionsButtonSpacing;
    public final int overviewActionsHeight;
    public final int overviewActionsTopMarginPx;
    public int overviewGridSideMargin;
    public int overviewPageSpacing;
    public int overviewRowSpacing;
    public int overviewTaskIconDrawableSizeGridPx;
    public int overviewTaskIconDrawableSizePx;
    public int overviewTaskIconSizePx;
    public int overviewTaskMarginGridPx;
    public int overviewTaskMarginPx;
    public int overviewTaskThumbnailTopMarginPx;
    public final float qsbBottomMarginOriginalPx;
    public int qsbBottomMarginPx;
    public int qsbWidth;
    public final int rotationHint;
    public int springLoadedHotseatBarTopMarginPx;
    public int stashedTaskbarSize;
    public int taskbarSize;
    public final boolean transposeLayoutWithOrientation;
    public final int widthPx;
    public final int windowX;
    public final int windowY;
    public int workspaceBottomPadding;
    public int workspaceCellPaddingXPx;
    public final Rect workspacePadding;
    public final int workspacePageIndicatorHeight;
    public float workspaceSpringLoadShrunkBottom;
    public float workspaceSpringLoadShrunkTop;
    public final int workspaceSpringLoadedBottomSpace;
    public final int workspaceSpringLoadedMinNextPageVisiblePx;
    public int workspaceTopPadding;

    public interface OnDeviceProfileChangeListener {
        void onDeviceProfileChanged(DeviceProfile deviceProfile);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:53:0x0255, code lost:
        if (r1.inlineQsb[1] != false) goto L_0x0259;
     */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x038e  */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x03d2  */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x0431  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x0437  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x0473  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0266 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x0279 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x0282  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x0285  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x028b  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x028e  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x02a0  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x02aa  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x02dd  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x02df  */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x02f9  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0302  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x0340  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x0343  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    DeviceProfile(android.content.Context r17, com.android.launcher3.InvariantDeviceProfile r18, com.android.launcher3.util.DisplayController.Info r19, com.android.launcher3.util.WindowBounds r20, boolean r21, boolean r22, boolean r23, boolean r24) {
        /*
            r16 = this;
            r0 = r16
            r1 = r18
            r2 = r19
            r3 = r20
            r4 = r21
            r5 = r24
            r16.<init>()
            android.graphics.Rect r6 = new android.graphics.Rect
            r6.<init>()
            r0.cellLayoutPaddingPx = r6
            android.graphics.PointF r6 = new android.graphics.PointF
            r7 = 1065353216(0x3f800000, float:1.0)
            r6.<init>(r7, r7)
            r0.appWidgetScale = r6
            android.graphics.Rect r6 = new android.graphics.Rect
            r6.<init>()
            r0.mInsets = r6
            android.graphics.Rect r7 = new android.graphics.Rect
            r7.<init>()
            r0.workspacePadding = r7
            android.graphics.Rect r7 = new android.graphics.Rect
            r7.<init>()
            r0.mHotseatPadding = r7
            r0.inv = r1
            boolean r7 = r20.isLandscape()
            r0.isLandscape = r7
            r0.isMultiWindowMode = r4
            r8 = r22
            r0.transposeLayoutWithOrientation = r8
            r0.isGestureMode = r5
            android.graphics.Rect r8 = r3.bounds
            int r8 = r8.left
            r0.windowX = r8
            android.graphics.Rect r8 = r3.bounds
            int r8 = r8.top
            r0.windowY = r8
            int r8 = r3.rotationHint
            r0.rotationHint = r8
            android.graphics.Rect r8 = r3.insets
            r6.set(r8)
            boolean r8 = r1.isScalable
            if (r8 == 0) goto L_0x0067
            boolean r8 = r16.isVerticalBarLayout()
            if (r8 != 0) goto L_0x0067
            if (r4 != 0) goto L_0x0067
            r4 = 1
            goto L_0x0068
        L_0x0067:
            r4 = 0
        L_0x0068:
            r0.isScalableGrid = r4
            r0.mInfo = r2
            boolean r8 = r19.isTablet(r20)
            r0.isTablet = r8
            r11 = r8 ^ 1
            r0.isPhone = r11
            if (r8 == 0) goto L_0x007c
            if (r23 == 0) goto L_0x007c
            r12 = 1
            goto L_0x007d
        L_0x007c:
            r12 = 0
        L_0x007d:
            r0.isTwoPanels = r12
            r0.isTaskbarPresent = r8
            boolean r13 = r16.isVerticalBarLayout()
            r14 = 2
            if (r13 != 0) goto L_0x0091
            if (r8 == 0) goto L_0x008d
            if (r7 == 0) goto L_0x008d
            goto L_0x0091
        L_0x008d:
            r13 = r17
            r15 = 1
            goto L_0x0094
        L_0x0091:
            r13 = r17
            r15 = r14
        L_0x0094:
            android.content.Context r2 = getContext(r13, r2, r15, r3)
            android.content.res.Resources r2 = r2.getResources()
            android.util.DisplayMetrics r13 = r2.getDisplayMetrics()
            r0.mMetrics = r13
            android.graphics.Rect r15 = r3.bounds
            int r15 = r15.width()
            r0.widthPx = r15
            android.graphics.Rect r10 = r3.bounds
            int r10 = r10.height()
            r0.heightPx = r10
            android.graphics.Point r9 = r3.availableSize
            int r9 = r9.x
            r0.availableWidthPx = r9
            android.graphics.Point r3 = r3.availableSize
            int r3 = r3.y
            r0.availableHeightPx = r3
            int r3 = java.lang.Math.max(r15, r10)
            float r3 = (float) r3
            int r9 = java.lang.Math.min(r15, r10)
            float r9 = (float) r9
            float r3 = r3 / r9
            r0.aspectRatio = r3
            r9 = 1073741824(0x40000000, float:2.0)
            int r9 = java.lang.Float.compare(r3, r9)
            if (r9 < 0) goto L_0x00d5
            r9 = 1
            goto L_0x00d6
        L_0x00d5:
            r9 = 0
        L_0x00d6:
            r15 = 2131165902(0x7f0702ce, float:1.7946034E38)
            float r15 = r2.getFloat(r15)
            r0.mQsbCenterFactor = r15
            r15 = 3
            if (r12 == 0) goto L_0x00ea
            if (r7 == 0) goto L_0x00e7
            r0.mTypeIndex = r15
            goto L_0x00f3
        L_0x00e7:
            r0.mTypeIndex = r14
            goto L_0x00f3
        L_0x00ea:
            if (r7 == 0) goto L_0x00f0
            r7 = 1
            r0.mTypeIndex = r7
            goto L_0x00f3
        L_0x00f0:
            r7 = 0
            r0.mTypeIndex = r7
        L_0x00f3:
            boolean r7 = r0.isTaskbarPresent
            if (r7 == 0) goto L_0x0109
            r7 = 2131166000(0x7f070330, float:1.7946233E38)
            int r7 = r2.getDimensionPixelSize(r7)
            r0.taskbarSize = r7
            r7 = 2131166003(0x7f070333, float:1.794624E38)
            int r7 = r2.getDimensionPixelSize(r7)
            r0.stashedTaskbarSize = r7
        L_0x0109:
            r7 = 2131165446(0x7f070106, float:1.794511E38)
            int r7 = r2.getDimensionPixelSize(r7)
            r0.edgeMarginPx = r7
            int r15 = r0.getHorizontalMarginPx(r1, r2)
            r0.desiredWorkspaceHorizontalMarginPx = r15
            r0.desiredWorkspaceHorizontalMarginOriginalPx = r15
            r15 = 2131165564(0x7f07017c, float:1.7945349E38)
            int r15 = r2.getDimensionPixelSize(r15)
            r0.gridVisualizationPaddingX = r15
            r15 = 2131165566(0x7f07017e, float:1.7945353E38)
            int r15 = r2.getDimensionPixelSize(r15)
            r0.gridVisualizationPaddingY = r15
            int r6 = r6.top
            r15 = 2131165324(0x7f07008c, float:1.7944862E38)
            int r15 = r2.getDimensionPixelSize(r15)
            int r6 = r6 + r15
            if (r8 == 0) goto L_0x0139
            r7 = 0
        L_0x0139:
            int r6 = r6 + r7
            r0.bottomSheetTopPadding = r6
            if (r8 == 0) goto L_0x013f
            goto L_0x0140
        L_0x013f:
            r6 = 0
        L_0x0140:
            r0.allAppsTopPadding = r6
            if (r8 == 0) goto L_0x0146
            int r10 = r10 - r6
            goto L_0x014d
        L_0x0146:
            r6 = 2131165297(0x7f070071, float:1.7944807E38)
            int r10 = r2.getDimensionPixelSize(r6)
        L_0x014d:
            r0.allAppsShiftRange = r10
            r6 = 2131165484(0x7f07012c, float:1.7945186E38)
            float r6 = r2.getFloat(r6)
            r0.folderLabelTextScale = r6
            r6 = 2131165481(0x7f070129, float:1.794518E38)
            int r6 = r2.getDimensionPixelSize(r6)
            r0.folderContentPaddingLeftRight = r6
            r6 = 2131165482(0x7f07012a, float:1.7945182E38)
            int r6 = r2.getDimensionPixelSize(r6)
            r0.folderContentPaddingTop = r6
            android.graphics.Point r6 = r0.getCellLayoutBorderSpace(r1)
            r0.cellLayoutBorderSpacePx = r6
            android.graphics.Point r6 = new android.graphics.Point
            android.graphics.PointF[] r7 = r1.allAppsBorderSpaces
            int r8 = r0.mTypeIndex
            r7 = r7[r8]
            float r7 = r7.x
            int r7 = com.android.launcher3.ResourceUtils.pxFromDp(r7, r13)
            android.graphics.PointF[] r8 = r1.allAppsBorderSpaces
            int r10 = r0.mTypeIndex
            r8 = r8[r10]
            float r8 = r8.y
            int r8 = com.android.launcher3.ResourceUtils.pxFromDp(r8, r13)
            r6.<init>(r7, r8)
            r0.allAppsBorderSpacePx = r6
            android.graphics.Point r6 = new android.graphics.Point
            android.graphics.Point r7 = r0.cellLayoutBorderSpacePx
            r6.<init>(r7)
            r0.cellLayoutBorderSpaceOriginalPx = r6
            float r6 = r1.folderBorderSpace
            int r6 = com.android.launcher3.ResourceUtils.pxFromDp(r6, r13)
            r0.folderCellLayoutBorderSpaceOriginalPx = r6
            android.graphics.Point r6 = new android.graphics.Point
            int r7 = r0.folderCellLayoutBorderSpaceOriginalPx
            r6.<init>(r7, r7)
            r0.folderCellLayoutBorderSpacePx = r6
            r6 = 2131166068(0x7f070374, float:1.794637E38)
            int r7 = r2.getDimensionPixelSize(r6)
            r0.workspacePageIndicatorHeight = r7
            r6 = 2131166070(0x7f070376, float:1.7946375E38)
            int r6 = r2.getDimensionPixelSize(r6)
            r0.mWorkspacePageIndicatorOverlapWorkspace = r6
            r6 = 2131165453(0x7f07010d, float:1.7945124E38)
            int r6 = r2.getDimensionPixelSize(r6)
            r0.iconDrawablePaddingOriginalPx = r6
            r6 = 2131165445(0x7f070105, float:1.7945107E38)
            int r6 = r2.getDimensionPixelSize(r6)
            r0.dropTargetBarSizePx = r6
            r6 = 2131165441(0x7f070101, float:1.79451E38)
            int r6 = r2.getDimensionPixelSize(r6)
            r0.dropTargetBarTopMarginPx = r6
            r6 = 2131165432(0x7f0700f8, float:1.794508E38)
            int r6 = r2.getDimensionPixelSize(r6)
            r0.dropTargetBarBottomMarginPx = r6
            r6 = 2131165438(0x7f0700fe, float:1.7945093E38)
            int r6 = r2.getDimensionPixelSize(r6)
            r0.dropTargetDragPaddingPx = r6
            r6 = 2131165440(0x7f070100, float:1.7945097E38)
            int r6 = r2.getDimensionPixelSize(r6)
            r0.dropTargetTextSizePx = r6
            r6 = 2131165433(0x7f0700f9, float:1.7945083E38)
            int r6 = r2.getDimensionPixelSize(r6)
            r0.dropTargetHorizontalPaddingPx = r6
            r6 = 2131165435(0x7f0700fb, float:1.7945087E38)
            int r6 = r2.getDimensionPixelSize(r6)
            r0.dropTargetVerticalPaddingPx = r6
            r6 = 2131165436(0x7f0700fc, float:1.794509E38)
            int r6 = r2.getDimensionPixelSize(r6)
            r0.dropTargetGapPx = r6
            r6 = 2131165437(0x7f0700fd, float:1.7945091E38)
            int r6 = r2.getDimensionPixelSize(r6)
            r0.dropTargetButtonWorkspaceEdgeGapPx = r6
            r6 = 2131165455(0x7f07010f, float:1.7945128E38)
            int r6 = r2.getDimensionPixelSize(r6)
            r0.workspaceSpringLoadedBottomSpace = r6
            r6 = 2131165456(0x7f070110, float:1.794513E38)
            int r6 = r2.getDimensionPixelSize(r6)
            r0.workspaceSpringLoadedMinNextPageVisiblePx = r6
            r6 = 2131165444(0x7f070104, float:1.7945105E38)
            int r6 = r2.getDimensionPixelSize(r6)
            r0.workspaceCellPaddingXPx = r6
            r6 = 2131165903(0x7f0702cf, float:1.7946036E38)
            int r6 = r2.getDimensionPixelSize(r6)
            r0.hotseatQsbHeight = r6
            boolean[] r8 = r1.inlineQsb
            if (r12 == 0) goto L_0x024b
            boolean r8 = r8[r14]
            if (r8 != 0) goto L_0x0258
            boolean[] r8 = r1.inlineQsb
            r10 = 3
            boolean r8 = r8[r10]
            if (r8 == 0) goto L_0x0249
            goto L_0x0258
        L_0x0249:
            r10 = 1
            goto L_0x025d
        L_0x024b:
            r10 = 0
            boolean r8 = r8[r10]
            if (r8 != 0) goto L_0x0258
            boolean[] r8 = r1.inlineQsb
            r10 = 1
            boolean r8 = r8[r10]
            if (r8 == 0) goto L_0x025d
            goto L_0x0259
        L_0x0258:
            r10 = 1
        L_0x0259:
            if (r6 <= 0) goto L_0x025d
            r6 = r10
            goto L_0x025e
        L_0x025d:
            r6 = 0
        L_0x025e:
            boolean[] r8 = r1.inlineQsb
            int r15 = r0.mTypeIndex
            boolean r8 = r8[r15]
            if (r8 == 0) goto L_0x026a
            if (r6 == 0) goto L_0x026a
            r8 = r10
            goto L_0x026b
        L_0x026a:
            r8 = 0
        L_0x026b:
            r0.isQsbInline = r8
            boolean r15 = r0.isTaskbarPresent
            if (r15 == 0) goto L_0x0274
            if (r5 != 0) goto L_0x0274
            goto L_0x0275
        L_0x0274:
            r10 = 0
        L_0x0275:
            r0.areNavButtonsInline = r10
            if (r10 == 0) goto L_0x0280
            if (r6 == 0) goto L_0x0280
            int r5 = r1.numShrunkenHotseatIcons
            r0.numShownHotseatIcons = r5
            goto L_0x0289
        L_0x0280:
            if (r12 == 0) goto L_0x0285
            int r5 = r1.numDatabaseHotseatIcons
            goto L_0x0287
        L_0x0285:
            int r5 = r1.numShownHotseatIcons
        L_0x0287:
            r0.numShownHotseatIcons = r5
        L_0x0289:
            if (r12 == 0) goto L_0x028e
            int r5 = r1.numDatabaseAllAppsColumns
            goto L_0x0290
        L_0x028e:
            int r5 = r1.numAllAppsColumns
        L_0x0290:
            r0.numShownAllAppsColumns = r5
            r5 = 0
            r0.hotseatBarSizeExtraSpacePx = r5
            r5 = 2131165452(0x7f07010c, float:1.7945122E38)
            int r5 = r2.getDimensionPixelSize(r5)
            r0.hotseatBarTopPaddingPx = r5
            if (r8 == 0) goto L_0x02aa
            r5 = 2131165581(0x7f07018d, float:1.7945383E38)
            int r5 = r2.getDimensionPixelSize(r5)
            r0.hotseatBarBottomPaddingPx = r5
            goto L_0x02c5
        L_0x02aa:
            if (r9 == 0) goto L_0x02b4
            r5 = 2131165449(0x7f070109, float:1.7945115E38)
            int r5 = r2.getDimensionPixelSize(r5)
            goto L_0x02bb
        L_0x02b4:
            r5 = 2131165447(0x7f070107, float:1.7945111E38)
            int r5 = r2.getDimensionPixelSize(r5)
        L_0x02bb:
            r6 = 2131165448(0x7f070108, float:1.7945113E38)
            int r6 = r2.getDimensionPixelSize(r6)
            int r5 = r5 + r6
            r0.hotseatBarBottomPaddingPx = r5
        L_0x02c5:
            r5 = 2131165940(0x7f0702f4, float:1.7946111E38)
            int r5 = r2.getDimensionPixelSize(r5)
            r0.springLoadedHotseatBarTopMarginPx = r5
            r5 = 2131165451(0x7f07010b, float:1.794512E38)
            int r5 = r2.getDimensionPixelSize(r5)
            r0.hotseatBarSidePaddingEndPx = r5
            boolean r5 = r16.isVerticalBarLayout()
            if (r5 == 0) goto L_0x02df
            r5 = r7
            goto L_0x02e0
        L_0x02df:
            r5 = 0
        L_0x02e0:
            r0.hotseatBarSidePaddingStartPx = r5
            r5 = 2131165450(0x7f07010a, float:1.7945117E38)
            int r5 = r2.getDimensionPixelSize(r5)
            r0.hotseatExtraVerticalSize = r5
            float[] r5 = r1.iconSize
            r6 = 0
            r5 = r5[r6]
            int r5 = com.android.launcher3.ResourceUtils.pxFromDp(r5, r13)
            r0.updateHotseatIconSize(r5)
            if (r4 == 0) goto L_0x0302
            r5 = 2131165919(0x7f0702df, float:1.7946069E38)
            int r5 = r2.getDimensionPixelSize(r5)
            float r5 = (float) r5
            goto L_0x0303
        L_0x0302:
            r5 = 0
        L_0x0303:
            r0.qsbBottomMarginOriginalPx = r5
            r6 = 2131165869(0x7f0702ad, float:1.7945967E38)
            int r6 = r2.getDimensionPixelSize(r6)
            r0.overviewTaskMarginPx = r6
            r6 = 2131165870(0x7f0702ae, float:1.794597E38)
            int r6 = r2.getDimensionPixelSize(r6)
            r0.overviewTaskMarginGridPx = r6
            r6 = 2131165979(0x7f07031b, float:1.794619E38)
            int r6 = r2.getDimensionPixelSize(r6)
            r0.overviewTaskIconSizePx = r6
            r6 = 2131165977(0x7f070319, float:1.7946186E38)
            int r6 = r2.getDimensionPixelSize(r6)
            r0.overviewTaskIconDrawableSizePx = r6
            r6 = 2131165978(0x7f07031a, float:1.7946188E38)
            int r6 = r2.getDimensionPixelSize(r6)
            r0.overviewTaskIconDrawableSizeGridPx = r6
            int r6 = r0.overviewTaskIconSizePx
            int r8 = r0.overviewTaskMarginPx
            int r8 = r8 * r14
            int r6 = r6 + r8
            r0.overviewTaskThumbnailTopMarginPx = r6
            boolean r6 = r16.isVerticalBarLayout()
            if (r6 == 0) goto L_0x0343
            int r6 = r0.overviewTaskMarginPx
            goto L_0x034a
        L_0x0343:
            r6 = 2131165860(0x7f0702a4, float:1.794595E38)
            int r6 = r2.getDimensionPixelSize(r6)
        L_0x034a:
            r0.overviewActionsTopMarginPx = r6
            r6 = 2131165866(0x7f0702aa, float:1.7945961E38)
            int r6 = r2.getDimensionPixelSize(r6)
            r0.overviewPageSpacing = r6
            r6 = 2131165857(0x7f0702a1, float:1.7945943E38)
            int r6 = r2.getDimensionPixelSize(r6)
            r0.overviewActionsButtonSpacing = r6
            r6 = 2131165858(0x7f0702a2, float:1.7945945E38)
            int r6 = r2.getDimensionPixelSize(r6)
            r0.overviewActionsHeight = r6
            int r6 = r0.overviewTaskThumbnailTopMarginPx
            int r8 = r0.overviewTaskIconSizePx
            int r6 = r6 - r8
            int r8 = r0.overviewTaskMarginGridPx
            int r6 = r6 - r8
            r8 = 2131165861(0x7f0702a5, float:1.7945951E38)
            int r8 = r2.getDimensionPixelSize(r8)
            int r8 = r8 - r6
            r0.overviewRowSpacing = r8
            r6 = 2131165862(0x7f0702a6, float:1.7945953E38)
            int r6 = r2.getDimensionPixelSize(r6)
            r0.overviewGridSideMargin = r6
            int r6 = r0.updateAvailableDimensions(r2)
            r0.extraSpace = r6
            if (r4 == 0) goto L_0x03d2
            com.android.launcher3.DevicePaddings r4 = r1.devicePaddings
            if (r4 == 0) goto L_0x03d2
            float r3 = (float) r6
            float r4 = r0.cellScaleToFit
            float r3 = r3 / r4
            int r3 = (int) r3
            com.android.launcher3.DevicePaddings r1 = r1.devicePaddings
            com.android.launcher3.DevicePaddings$DevicePadding r1 = r1.getDevicePadding(r3)
            int r4 = r1.getWorkspaceTopPadding(r3)
            int r6 = r1.getWorkspaceBottomPadding(r3)
            int r1 = r1.getHotseatBottomPadding(r3)
            float r3 = (float) r4
            float r4 = r0.cellScaleToFit
            float r3 = r3 * r4
            int r3 = java.lang.Math.round(r3)
            r0.workspaceTopPadding = r3
            float r3 = (float) r6
            float r4 = r0.cellScaleToFit
            float r3 = r3 * r4
            int r3 = java.lang.Math.round(r3)
            r0.workspaceBottomPadding = r3
            float r1 = (float) r1
            float r3 = r0.cellScaleToFit
            float r1 = r1 * r3
            int r1 = java.lang.Math.round(r1)
            r0.extraHotseatBottomPadding = r1
            int r3 = r0.hotseatBarSizePx
            int r3 = r3 + r1
            r0.hotseatBarSizePx = r3
            float r1 = r0.cellScaleToFit
            float r5 = r5 * r1
            int r1 = java.lang.Math.round(r5)
            r0.qsbBottomMarginPx = r1
            goto L_0x042f
        L_0x03d2:
            boolean r4 = r16.isVerticalBarLayout()
            if (r4 != 0) goto L_0x042f
            if (r11 == 0) goto L_0x042f
            if (r9 == 0) goto L_0x042f
            r4 = 1074370970(0x4009999a, float:2.15)
            int r3 = java.lang.Float.compare(r3, r4)
            if (r3 < 0) goto L_0x0417
            r3 = 1132199936(0x437c0000, float:252.0)
            int r3 = com.android.launcher3.Utilities.dpToPx(r3)
            if (r6 < r3) goto L_0x0417
            r3 = 1132855296(0x43860000, float:268.0)
            int r3 = com.android.launcher3.Utilities.dpToPx(r3)
            if (r6 >= r3) goto L_0x03f7
            r3 = 7
            goto L_0x03f8
        L_0x03f7:
            r3 = 5
        L_0x03f8:
            android.graphics.Point r4 = r16.getCellSize()
            int r4 = r4.y
            int r5 = r0.iconSizePx
            int r4 = r4 - r5
            int r5 = r0.iconDrawablePaddingPx
            int r5 = r5 * r14
            int r4 = r4 - r5
            int r1 = r1.numRows
            int r4 = r4 * r1
            int r4 = r4 / r3
            int r1 = r4 / 8
            r0.workspaceTopPadding = r1
            int r4 = r4 - r1
            int r4 = r4 / r14
            int r1 = r0.hotseatBarTopPaddingPx
            int r1 = r1 + r4
            r0.hotseatBarTopPaddingPx = r1
            r0.hotseatBarSizeExtraSpacePx = r4
            goto L_0x0427
        L_0x0417:
            android.graphics.Point r1 = r16.getCellSize()
            int r1 = r1.y
            int r3 = r0.iconSizePx
            int r1 = r1 - r3
            int r3 = r0.iconDrawablePaddingPx
            int r3 = r3 * r14
            int r1 = r1 - r3
            int r1 = r1 - r7
            r0.hotseatBarSizeExtraSpacePx = r1
        L_0x0427:
            int r1 = r0.iconSizePx
            r0.updateHotseatIconSize(r1)
            r0.updateAvailableDimensions(r2)
        L_0x042f:
            if (r12 == 0) goto L_0x0437
            android.graphics.Point r1 = r0.cellLayoutBorderSpacePx
            int r1 = r1.x
            int r1 = r1 / r14
            goto L_0x043e
        L_0x0437:
            r1 = 2131165334(0x7f070096, float:1.7944882E38)
            int r1 = r2.getDimensionPixelSize(r1)
        L_0x043e:
            android.graphics.Rect r3 = new android.graphics.Rect
            r3.<init>(r1, r1, r1, r1)
            r0.cellLayoutPaddingPx = r3
            r16.updateWorkspacePadding()
            int r1 = r16.calculateHotseatBorderSpace()
            r0.hotseatBorderSpace = r1
            int r1 = r16.calculateQsbWidth()
            r0.qsbWidth = r1
            r1 = 2131165430(0x7f0700f6, float:1.7945077E38)
            int r1 = r2.getDimensionPixelSize(r1)
            r0.flingToDeleteThresholdVelocity = r1
            r1 = 100
            android.graphics.Path r2 = com.android.launcher3.icons.GraphicsUtils.getShapePath(r1)
            com.android.launcher3.icons.DotRenderer r3 = new com.android.launcher3.icons.DotRenderer
            int r4 = r0.iconSizePx
            r3.<init>(r4, r2, r1)
            r0.mDotRendererWorkSpace = r3
            int r4 = r0.iconSizePx
            int r5 = r0.allAppsIconSizePx
            if (r4 != r5) goto L_0x0473
            goto L_0x047a
        L_0x0473:
            com.android.launcher3.icons.DotRenderer r3 = new com.android.launcher3.icons.DotRenderer
            int r4 = r0.allAppsIconSizePx
            r3.<init>(r4, r2, r1)
        L_0x047a:
            r0.mDotRendererAllApps = r3
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.DeviceProfile.<init>(android.content.Context, com.android.launcher3.InvariantDeviceProfile, com.android.launcher3.util.DisplayController$Info, com.android.launcher3.util.WindowBounds, boolean, boolean, boolean, boolean):void");
    }

    private int calculateQsbWidth() {
        if (!this.isQsbInline) {
            return getIconToIconWidthForColumns(this.inv.hotseatColumnSpan[this.mTypeIndex]);
        }
        int iconToIconWidthForColumns = getIconToIconWidthForColumns(getPanelCount() * this.inv.numColumns);
        int i = this.iconSizePx;
        int i2 = this.numShownHotseatIcons;
        return (iconToIconWidthForColumns - (i * i2)) - (this.hotseatBorderSpace * i2);
    }

    private int getIconToIconWidthForColumns(int i) {
        return ((getCellSize().x * i) + ((i - 1) * this.cellLayoutBorderSpacePx.x)) - (getCellSize().x - this.iconSizePx);
    }

    private int getHorizontalMarginPx(InvariantDeviceProfile invariantDeviceProfile, Resources resources) {
        if (isVerticalBarLayout()) {
            return 0;
        }
        if (this.isScalableGrid) {
            return ResourceUtils.pxFromDp(invariantDeviceProfile.horizontalMargin[this.mTypeIndex], this.mMetrics);
        }
        return resources.getDimensionPixelSize(R.dimen.dynamic_grid_left_right_margin);
    }

    private void updateHotseatIconSize(int i) {
        this.hotseatCellHeightPx = (int) Math.ceil((double) (((float) i) * 1.125f));
        if (isVerticalBarLayout()) {
            this.hotseatBarSizePx = i + this.hotseatBarSidePaddingStartPx + this.hotseatBarSidePaddingEndPx;
        } else {
            this.hotseatBarSizePx = i + this.hotseatBarTopPaddingPx + this.hotseatBarBottomPaddingPx + (this.isScalableGrid ? 0 : this.hotseatExtraVerticalSize) + this.hotseatBarSizeExtraSpacePx;
        }
    }

    private Point getCellLayoutBorderSpace(InvariantDeviceProfile invariantDeviceProfile) {
        return getCellLayoutBorderSpace(invariantDeviceProfile, 1.0f);
    }

    private Point getCellLayoutBorderSpace(InvariantDeviceProfile invariantDeviceProfile, float f) {
        if (!this.isScalableGrid) {
            return new Point(0, 0);
        }
        return new Point(ResourceUtils.pxFromDp(invariantDeviceProfile.borderSpaces[this.mTypeIndex].x, this.mMetrics, f), ResourceUtils.pxFromDp(invariantDeviceProfile.borderSpaces[this.mTypeIndex].y, this.mMetrics, f));
    }

    public DisplayController.Info getDisplayInfo() {
        return this.mInfo;
    }

    public boolean shouldInsetWidgets() {
        Rect rect = this.inv.defaultWidgetPadding;
        return this.workspaceTopPadding > rect.top && this.cellLayoutBorderSpacePx.x > rect.left && this.cellLayoutBorderSpacePx.y > rect.top && this.cellLayoutBorderSpacePx.x > rect.right && this.cellLayoutBorderSpacePx.y > rect.bottom;
    }

    public Builder toBuilder(Context context) {
        WindowBounds windowBounds = new WindowBounds(this.widthPx, this.heightPx, this.availableWidthPx, this.availableHeightPx, this.rotationHint);
        windowBounds.bounds.offsetTo(this.windowX, this.windowY);
        windowBounds.insets.set(this.mInsets);
        return new Builder(context, this.inv, this.mInfo).setWindowBounds(windowBounds).setUseTwoPanels(this.isTwoPanels).setMultiWindowMode(this.isMultiWindowMode).setGestureMode(this.isGestureMode);
    }

    public DeviceProfile copy(Context context) {
        return toBuilder(context).build();
    }

    public DeviceProfile getMultiWindowProfile(Context context, WindowBounds windowBounds) {
        DeviceProfile build = toBuilder(context).setWindowBounds(windowBounds).setMultiWindowMode(true).build();
        build.hideWorkspaceLabelsIfNotEnoughSpace();
        build.appWidgetScale.set(((float) build.getCellSize().x) / ((float) getCellSize().x), ((float) build.getCellSize().y) / ((float) getCellSize().y));
        return build;
    }

    private void hideWorkspaceLabelsIfNotEnoughSpace() {
        float calculateTextHeight = (float) Utilities.calculateTextHeight((float) this.iconTextSizePx);
        int i = getCellSize().y;
        int i2 = this.iconSizePx;
        if (((float) ((i - i2) - this.iconDrawablePaddingPx)) - calculateTextHeight < calculateTextHeight) {
            this.iconTextSizePx = 0;
            this.iconDrawablePaddingPx = 0;
            this.cellHeightPx = (int) Math.ceil((double) (((float) i2) * 1.125f));
            autoResizeAllAppsCells();
        }
    }

    public void autoResizeAllAppsCells() {
        int calculateTextHeight = Utilities.calculateTextHeight(this.allAppsIconTextSizePx);
        this.allAppsCellHeightPx = this.allAppsIconSizePx + this.allAppsIconDrawablePaddingPx + calculateTextHeight + (calculateTextHeight * 2);
    }

    private void updateAllAppsContainerWidth(Resources resources) {
        int i = (this.cellLayoutPaddingPx.left + this.cellLayoutPaddingPx.right) / 2;
        if (this.isTablet) {
            this.allAppsLeftRightPadding = resources.getDimensionPixelSize(R.dimen.all_apps_bottom_sheet_horizontal_padding);
            this.allAppsLeftRightMargin = Math.max(1, (this.availableWidthPx - (((this.allAppsCellWidthPx * this.numShownAllAppsColumns) + (this.allAppsBorderSpacePx.x * (this.numShownAllAppsColumns - 1))) + (this.allAppsLeftRightPadding * 2))) / 2);
            return;
        }
        this.allAppsLeftRightPadding = this.desiredWorkspaceHorizontalMarginPx + i;
    }

    private int updateAvailableDimensions(Resources resources) {
        float f = 1.0f;
        updateIconSize(1.0f, resources);
        updateWorkspacePadding();
        float cellLayoutHeightSpecification = (float) getCellLayoutHeightSpecification();
        int cellLayoutHeight = getCellLayoutHeight();
        float f2 = (float) cellLayoutHeight;
        float max = Math.max(0.0f, f2 - cellLayoutHeightSpecification);
        float f3 = f2 / cellLayoutHeightSpecification;
        boolean z = true;
        boolean z2 = f3 < 1.0f;
        if (this.isScalableGrid) {
            f = ((float) this.availableWidthPx) / ((float) (getCellLayoutWidthSpecification() + (this.desiredWorkspaceHorizontalMarginPx * 2)));
        } else {
            z = z2;
        }
        if (z) {
            updateIconSize(Math.min(f, f3), resources);
            max = (float) Math.max(0, cellLayoutHeight - getCellLayoutHeightSpecification());
        }
        updateAvailableFolderCellDimensions(resources);
        return Math.round(max);
    }

    private int getCellLayoutHeightSpecification() {
        return (this.cellHeightPx * this.inv.numRows) + (this.cellLayoutBorderSpacePx.y * (this.inv.numRows - 1)) + this.cellLayoutPaddingPx.top + this.cellLayoutPaddingPx.bottom;
    }

    private int getCellLayoutWidthSpecification() {
        int panelCount = getPanelCount() * this.inv.numColumns;
        return (this.cellWidthPx * panelCount) + (this.cellLayoutBorderSpacePx.x * (panelCount - 1)) + this.cellLayoutPaddingPx.left + this.cellLayoutPaddingPx.right;
    }

    public void updateIconSize(float f, Resources resources) {
        this.iconScale = Math.min(1.0f, f);
        this.cellScaleToFit = f;
        boolean isVerticalBarLayout = isVerticalBarLayout();
        float f2 = this.inv.iconSize[this.mTypeIndex];
        float f3 = this.inv.iconTextSize[this.mTypeIndex];
        this.iconSizePx = Math.max(1, ResourceUtils.pxFromDp(f2, this.mMetrics, this.iconScale));
        float f4 = this.iconScale;
        this.iconTextSizePx = (int) (((float) Utilities.pxFromSp(f3, this.mMetrics)) * f4);
        this.iconDrawablePaddingPx = (int) (((float) this.iconDrawablePaddingOriginalPx) * f4);
        this.cellLayoutBorderSpacePx = getCellLayoutBorderSpace(this.inv, f);
        if (this.isScalableGrid) {
            this.cellWidthPx = ResourceUtils.pxFromDp(this.inv.minCellSize[this.mTypeIndex].x, this.mMetrics, f);
            this.cellHeightPx = ResourceUtils.pxFromDp(this.inv.minCellSize[this.mTypeIndex].y, this.mMetrics, f);
            this.cellYPaddingPx = Math.max(0, this.cellHeightPx - ((this.iconSizePx + this.iconDrawablePaddingPx) + Utilities.calculateTextHeight((float) this.iconTextSizePx))) / 2;
            this.desiredWorkspaceHorizontalMarginPx = (int) (((float) this.desiredWorkspaceHorizontalMarginOriginalPx) * f);
        } else {
            int i = this.iconSizePx;
            this.cellWidthPx = this.iconDrawablePaddingPx + i;
            this.cellHeightPx = ((int) Math.ceil((double) (((float) i) * 1.125f))) + this.iconDrawablePaddingPx + Utilities.calculateTextHeight((float) this.iconTextSizePx);
            int i2 = getCellSize().y;
            int i3 = this.cellHeightPx;
            int i4 = (i2 - i3) / 2;
            int i5 = this.iconDrawablePaddingPx;
            if (i5 > i4 && !isVerticalBarLayout && !this.isMultiWindowMode) {
                this.cellHeightPx = i3 - (i5 - i4);
                this.iconDrawablePaddingPx = i4;
            }
        }
        updateAllAppsIconSize(f, resources);
        updateHotseatIconSize(this.iconSizePx);
        int normalizedCircleSize = IconNormalizer.getNormalizedCircleSize(this.iconSizePx);
        this.folderIconSizePx = normalizedCircleSize;
        this.folderIconOffsetYPx = (this.iconSizePx - normalizedCircleSize) / 2;
    }

    private int calculateHotseatBorderSpace() {
        if (!this.isScalableGrid) {
            return 0;
        }
        if (this.areNavButtonsInline) {
            return ResourceUtils.pxFromDp(this.inv.hotseatBorderSpaces[this.mTypeIndex], this.mMetrics);
        }
        int i = this.iconSizePx;
        int i2 = this.numShownHotseatIcons;
        return ((int) (((float) getIconToIconWidthForColumns(this.inv.hotseatColumnSpan[this.mTypeIndex])) - ((float) (i * i2)))) / (i2 - 1);
    }

    private void updateAllAppsIconSize(float f, Resources resources) {
        this.allAppsBorderSpacePx = new Point(ResourceUtils.pxFromDp(this.inv.allAppsBorderSpaces[this.mTypeIndex].x, this.mMetrics, f), ResourceUtils.pxFromDp(this.inv.allAppsBorderSpaces[this.mTypeIndex].y, this.mMetrics, f));
        this.allAppsCellHeightPx = ResourceUtils.pxFromDp(this.inv.allAppsCellSize[this.mTypeIndex].y, this.mMetrics, f) + this.allAppsBorderSpacePx.y;
        this.allAppsCellWidthPx = ResourceUtils.pxFromDp(this.inv.allAppsCellSize[this.mTypeIndex].x, this.mMetrics, f);
        if (this.isScalableGrid) {
            this.allAppsIconSizePx = ResourceUtils.pxFromDp(this.inv.allAppsIconSize[this.mTypeIndex], this.mMetrics, f);
            this.allAppsIconTextSizePx = (float) Utilities.pxFromSp(this.inv.allAppsIconTextSize[this.mTypeIndex], this.mMetrics, f);
            this.allAppsIconDrawablePaddingPx = this.iconDrawablePaddingOriginalPx;
        } else {
            float f2 = this.inv.allAppsIconSize[this.mTypeIndex];
            float f3 = this.inv.allAppsIconTextSize[this.mTypeIndex];
            this.allAppsIconSizePx = Math.max(1, ResourceUtils.pxFromDp(f2, this.mMetrics, f));
            this.allAppsIconTextSizePx = (float) ((int) (((float) Utilities.pxFromSp(f3, this.mMetrics)) * f));
            this.allAppsIconDrawablePaddingPx = resources.getDimensionPixelSize(R.dimen.all_apps_icon_drawable_padding);
        }
        updateAllAppsContainerWidth(resources);
        if (isVerticalBarLayout()) {
            hideWorkspaceLabelsIfNotEnoughSpace();
        }
    }

    private void updateAvailableFolderCellDimensions(Resources resources) {
        updateFolderCellSize(1.0f, resources);
        int dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.folder_label_height);
        int i = this.edgeMarginPx * 2;
        Point totalWorkspacePadding = getTotalWorkspacePadding();
        float min = Math.min(((float) (((this.availableWidthPx - totalWorkspacePadding.x) - i) - (this.folderContentPaddingLeftRight * 2))) / ((float) ((this.folderCellWidthPx * this.inv.numFolderColumns) + ((this.inv.numFolderColumns - 1) * this.folderCellLayoutBorderSpacePx.x))), ((float) ((((this.availableHeightPx - totalWorkspacePadding.y) - dimensionPixelSize) - i) - this.folderContentPaddingTop)) / ((float) ((this.folderCellHeightPx * this.inv.numFolderRows) + ((this.inv.numFolderRows - 1) * this.folderCellLayoutBorderSpacePx.y))));
        if (min < 1.0f) {
            updateFolderCellSize(min, resources);
        }
    }

    private void updateFolderCellSize(float f, Resources resources) {
        float f2;
        if (isVerticalBarLayout()) {
            f2 = this.inv.iconSize[1];
        } else {
            f2 = this.inv.iconSize[0];
        }
        this.folderChildIconSizePx = Math.max(1, ResourceUtils.pxFromDp(f2, this.mMetrics, f));
        int pxFromSp = Utilities.pxFromSp(this.inv.iconTextSize[0], this.mMetrics, f);
        this.folderChildTextSizePx = pxFromSp;
        this.folderLabelTextSizePx = (int) (((float) pxFromSp) * this.folderLabelTextScale);
        int calculateTextHeight = Utilities.calculateTextHeight((float) pxFromSp);
        if (this.isScalableGrid) {
            int i = this.folderChildIconSizePx;
            int i2 = this.iconDrawablePaddingPx;
            this.folderCellWidthPx = (int) Math.max((float) ((i2 * 2) + i), ((float) this.cellWidthPx) * f);
            this.folderCellHeightPx = (int) Math.max((float) (i + (i2 * 2) + calculateTextHeight), ((float) this.cellHeightPx) * f);
            int i3 = (int) (((float) this.folderCellLayoutBorderSpaceOriginalPx) * f);
            this.folderCellLayoutBorderSpacePx = new Point(i3, i3);
            this.folderContentPaddingLeftRight = i3;
            this.folderContentPaddingTop = i3;
        } else {
            int dimensionPixelSize = (int) (((float) resources.getDimensionPixelSize(R.dimen.folder_cell_y_padding)) * f);
            int i4 = this.folderChildIconSizePx;
            this.folderCellWidthPx = (((int) (((float) resources.getDimensionPixelSize(R.dimen.folder_cell_x_padding)) * f)) * 2) + i4;
            this.folderCellHeightPx = i4 + (dimensionPixelSize * 2) + calculateTextHeight;
        }
        this.folderChildDrawablePaddingPx = Math.max(0, ((this.folderCellHeightPx - this.folderChildIconSizePx) - calculateTextHeight) / 3);
    }

    public void updateInsets(Rect rect) {
        this.mInsets.set(rect);
    }

    public Rect getInsets() {
        return this.mInsets;
    }

    public Point getCellSize() {
        return getCellSize((Point) null);
    }

    public Point getCellSize(Point point) {
        if (point == null) {
            point = new Point();
        }
        point.x = calculateCellWidth(getCellLayoutWidth() - (this.cellLayoutPaddingPx.left + this.cellLayoutPaddingPx.right), this.cellLayoutBorderSpacePx.x, this.inv.numColumns);
        point.y = calculateCellHeight(getCellLayoutHeight() - (this.cellLayoutPaddingPx.top + this.cellLayoutPaddingPx.bottom), this.cellLayoutBorderSpacePx.y, this.inv.numRows);
        return point;
    }

    public int getPanelCount() {
        return this.isTwoPanels ? 2 : 1;
    }

    public int getVerticalHotseatLastItemBottomOffset() {
        int calculateCellHeight = calculateCellHeight((this.heightPx - this.mHotseatPadding.top) - this.mHotseatPadding.bottom, this.hotseatBorderSpace, this.numShownHotseatIcons);
        int i = this.numShownHotseatIcons;
        return ((this.heightPx - ((calculateCellHeight * i) + (this.hotseatBorderSpace * (i - 1)))) / 2) + ((calculateCellHeight - this.iconSizePx) / 2) + this.mHotseatPadding.bottom;
    }

    public float getCellLayoutSpringLoadShrunkTop() {
        float f = (float) (this.mInsets.top + this.dropTargetBarTopMarginPx + this.dropTargetBarSizePx + this.dropTargetBarBottomMarginPx);
        this.workspaceSpringLoadShrunkTop = f;
        return f;
    }

    private float getCellLayoutSpringLoadShrunkBottom() {
        int i = this.hotseatBarSizePx + this.springLoadedHotseatBarTopMarginPx;
        int i2 = this.heightPx;
        if (isVerticalBarLayout()) {
            i = getVerticalHotseatLastItemBottomOffset();
        }
        float f = (float) (i2 - i);
        this.workspaceSpringLoadShrunkBottom = f;
        return f;
    }

    public float getWorkspaceSpringLoadScale() {
        float min = Math.min((getCellLayoutSpringLoadShrunkBottom() - getCellLayoutSpringLoadShrunkTop()) / ((float) getCellLayoutHeight()), 1.0f);
        int i = this.availableWidthPx;
        float f = ((float) i) * min;
        float f2 = (float) (i - (this.workspaceSpringLoadedMinNextPageVisiblePx * 2));
        return f > f2 ? min * (f2 / f) : min;
    }

    public int getCellLayoutWidth() {
        return (this.availableWidthPx - getTotalWorkspacePadding().x) / getPanelCount();
    }

    public int getCellLayoutHeight() {
        return this.availableHeightPx - getTotalWorkspacePadding().y;
    }

    public Point getTotalWorkspacePadding() {
        return new Point(this.workspacePadding.left + this.workspacePadding.right, this.workspacePadding.top + this.workspacePadding.bottom);
    }

    private void updateWorkspacePadding() {
        Rect rect = this.workspacePadding;
        int i = 0;
        if (isVerticalBarLayout()) {
            rect.top = 0;
            rect.bottom = this.edgeMarginPx;
            if (isSeascape()) {
                rect.left = this.hotseatBarSizePx;
                rect.right = this.hotseatBarSidePaddingStartPx;
            } else {
                rect.left = this.hotseatBarSidePaddingStartPx;
                rect.right = this.hotseatBarSizePx;
            }
        } else {
            int i2 = ((this.hotseatBarSizePx + this.workspacePageIndicatorHeight) + this.workspaceBottomPadding) - this.mWorkspacePageIndicatorOverlapWorkspace;
            int i3 = this.workspaceTopPadding;
            if (!this.isScalableGrid) {
                i = this.edgeMarginPx;
            }
            int i4 = i3 + i;
            int i5 = this.desiredWorkspaceHorizontalMarginPx;
            rect.set(i5, i4, i5, i2);
        }
        insetPadding(this.workspacePadding, this.cellLayoutPaddingPx);
    }

    private void insetPadding(Rect rect, Rect rect2) {
        rect2.left = Math.min(rect2.left, rect.left);
        rect.left -= rect2.left;
        rect2.top = Math.min(rect2.top, rect.top);
        rect.top -= rect2.top;
        rect2.right = Math.min(rect2.right, rect.right);
        rect.right -= rect2.right;
        rect2.bottom = Math.min(rect2.bottom, rect.bottom);
        rect.bottom -= rect2.bottom;
    }

    public Rect getHotseatLayoutPadding(Context context) {
        int i = 0;
        if (isVerticalBarLayout()) {
            float f = (((float) this.iconSizePx) * 0.125f) / 2.0f;
            int max = Math.max((int) (((float) (this.mInsets.top + this.cellLayoutPaddingPx.top)) - f), 0);
            int max2 = Math.max((int) (((float) (this.mInsets.bottom + this.cellLayoutPaddingPx.bottom)) + f), 0);
            if (isSeascape()) {
                this.mHotseatPadding.set(this.mInsets.left + this.hotseatBarSidePaddingStartPx, max, this.hotseatBarSidePaddingEndPx, max2);
            } else {
                this.mHotseatPadding.set(this.hotseatBarSidePaddingEndPx, max, this.mInsets.right + this.hotseatBarSidePaddingStartPx, max2);
            }
        } else if (this.isTaskbarPresent) {
            int hotseatBottomPadding = getHotseatBottomPadding();
            int i2 = (this.workspacePadding.bottom - hotseatBottomPadding) - this.hotseatCellHeightPx;
            if (this.isQsbInline) {
                i = this.qsbWidth + this.hotseatBorderSpace;
            }
            int i3 = this.iconSizePx;
            int i4 = this.numShownHotseatIcons;
            int hotseatEndOffset = ApiWrapper.getHotseatEndOffset(context);
            int min = (this.availableWidthPx - Math.min(((i3 * i4) + (this.hotseatBorderSpace * (i4 - 1))) + i, this.availableWidthPx - hotseatEndOffset)) / 2;
            this.mHotseatPadding.set(min, i2, min, hotseatBottomPadding);
            boolean isRtl = Utilities.isRtl(context.getResources());
            if (isRtl) {
                this.mHotseatPadding.right += i;
            } else {
                this.mHotseatPadding.left += i;
            }
            if (hotseatEndOffset > min) {
                int i5 = isRtl ? min - hotseatEndOffset : hotseatEndOffset - min;
                this.mHotseatPadding.left -= i5;
                this.mHotseatPadding.right += i5;
            }
        } else if (this.isScalableGrid) {
            int i6 = (this.availableWidthPx - this.qsbWidth) / 2;
            Rect rect = this.mHotseatPadding;
            int i7 = this.hotseatBarTopPaddingPx;
            rect.set(i6, i7, i6, ((this.hotseatBarSizePx - this.hotseatCellHeightPx) - i7) + this.mInsets.bottom);
        } else {
            int round = Math.round(((((float) this.widthPx) / ((float) this.inv.numColumns)) - (((float) this.widthPx) / ((float) this.numShownHotseatIcons))) / 2.0f);
            this.mHotseatPadding.set(this.workspacePadding.left + round + this.cellLayoutPaddingPx.left + this.mInsets.left, this.hotseatBarTopPaddingPx, round + this.workspacePadding.right + this.cellLayoutPaddingPx.right + this.mInsets.right, ((this.hotseatBarSizePx - this.hotseatCellHeightPx) - this.hotseatBarTopPaddingPx) + this.mInsets.bottom);
        }
        return this.mHotseatPadding;
    }

    public int getQsbOffsetY() {
        int i;
        if (this.isQsbInline) {
            return this.hotseatBarBottomPaddingPx;
        }
        if (this.isTaskbarPresent) {
            i = this.workspacePadding.bottom;
        } else {
            i = (this.hotseatBarSizePx - this.hotseatCellHeightPx) - this.hotseatQsbHeight;
        }
        if (this.isScalableGrid && this.qsbBottomMarginPx > this.mInsets.bottom) {
            return Math.min(this.qsbBottomMarginPx + this.taskbarSize, i);
        }
        return ((int) (((float) i) * this.mQsbCenterFactor)) + (this.isTaskbarPresent ? this.taskbarSize : this.mInsets.bottom);
    }

    private int getHotseatBottomPadding() {
        if (this.isQsbInline) {
            return getQsbOffsetY() - (Math.abs(this.hotseatQsbHeight - this.hotseatCellHeightPx) / 2);
        }
        return (getQsbOffsetY() - this.taskbarSize) / 2;
    }

    public int getTaskbarOffsetY() {
        int i = this.taskbarSize;
        int i2 = this.iconSizePx;
        return (getHotseatBottomPadding() + Math.min((this.hotseatCellHeightPx - i2) / 2, this.gridVisualizationPaddingY)) - ((i - i2) / 2);
    }

    public int getOverviewActionsClaimedSpaceBelow() {
        boolean z = this.isTaskbarPresent;
        if (!z || this.isGestureMode) {
            return z ? this.stashedTaskbarSize : this.mInsets.bottom;
        }
        return ((this.taskbarSize - this.overviewActionsHeight) / 2) + getTaskbarOffsetY();
    }

    public int getOverviewActionsClaimedSpace() {
        return this.overviewActionsTopMarginPx + this.overviewActionsHeight + getOverviewActionsClaimedSpaceBelow();
    }

    public Rect getAbsoluteOpenFolderBounds() {
        if (isVerticalBarLayout()) {
            return new Rect(this.mInsets.left + this.dropTargetBarSizePx + this.edgeMarginPx, this.mInsets.top, ((this.mInsets.left + this.availableWidthPx) - this.hotseatBarSizePx) - this.edgeMarginPx, this.mInsets.top + this.availableHeightPx);
        }
        return new Rect(this.mInsets.left + this.edgeMarginPx, this.mInsets.top + this.dropTargetBarSizePx + this.edgeMarginPx, (this.mInsets.left + this.availableWidthPx) - this.edgeMarginPx, (((this.mInsets.top + this.availableHeightPx) - (this.isTaskbarPresent ? this.taskbarSize : this.hotseatBarSizePx)) - this.workspacePageIndicatorHeight) - this.edgeMarginPx);
    }

    public static int calculateCellWidth(int i, int i2, int i3) {
        return (i - ((i3 - 1) * i2)) / i3;
    }

    public static int calculateCellHeight(int i, int i2, int i3) {
        return (i - ((i3 - 1) * i2)) / i3;
    }

    public boolean isVerticalBarLayout() {
        return this.isLandscape && this.transposeLayoutWithOrientation;
    }

    public boolean updateIsSeascape(Context context) {
        if (isVerticalBarLayout()) {
            boolean z = DisplayController.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).getInfo().rotation == 3;
            if (this.mIsSeascape != z) {
                this.mIsSeascape = z;
                updateWorkspacePadding();
                return true;
            }
        }
        return false;
    }

    public boolean isSeascape() {
        return isVerticalBarLayout() && this.mIsSeascape;
    }

    public boolean shouldFadeAdjacentWorkspaceScreens() {
        return isVerticalBarLayout();
    }

    public int getCellContentHeight(int i) {
        if (i == 0) {
            return this.cellHeightPx;
        }
        if (i == 1) {
            return this.iconSizePx;
        }
        if (i != 2) {
            return 0;
        }
        return this.folderCellHeightPx;
    }

    private String pxToDpStr(String str, float f) {
        return "\t" + str + ": " + f + "px (" + Utilities.dpiFromPx(f, this.mMetrics.densityDpi) + "dp)";
    }

    public void dump(String str, PrintWriter printWriter) {
        printWriter.println(str + "DeviceProfile:");
        printWriter.println(str + "\t1 dp = " + this.mMetrics.density + " px");
        printWriter.println(str + "\tisTablet:" + this.isTablet);
        printWriter.println(str + "\tisPhone:" + this.isPhone);
        printWriter.println(str + "\ttransposeLayoutWithOrientation:" + this.transposeLayoutWithOrientation);
        printWriter.println(str + "\tisGestureMode:" + this.isGestureMode);
        printWriter.println(str + "\tisLandscape:" + this.isLandscape);
        printWriter.println(str + "\tisMultiWindowMode:" + this.isMultiWindowMode);
        printWriter.println(str + "\tisTwoPanels:" + this.isTwoPanels);
        printWriter.println(str + pxToDpStr("windowX", (float) this.windowX));
        printWriter.println(str + pxToDpStr("windowY", (float) this.windowY));
        printWriter.println(str + pxToDpStr("widthPx", (float) this.widthPx));
        printWriter.println(str + pxToDpStr("heightPx", (float) this.heightPx));
        printWriter.println(str + pxToDpStr("availableWidthPx", (float) this.availableWidthPx));
        printWriter.println(str + pxToDpStr("availableHeightPx", (float) this.availableHeightPx));
        printWriter.println(str + pxToDpStr("mInsets.left", (float) this.mInsets.left));
        printWriter.println(str + pxToDpStr("mInsets.top", (float) this.mInsets.top));
        printWriter.println(str + pxToDpStr("mInsets.right", (float) this.mInsets.right));
        printWriter.println(str + pxToDpStr("mInsets.bottom", (float) this.mInsets.bottom));
        printWriter.println(str + "\taspectRatio:" + this.aspectRatio);
        printWriter.println(str + "\tisScalableGrid:" + this.isScalableGrid);
        printWriter.println(str + "\tinv.numRows: " + this.inv.numRows);
        printWriter.println(str + "\tinv.numColumns: " + this.inv.numColumns);
        printWriter.println(str + "\tinv.numSearchContainerColumns: " + this.inv.numSearchContainerColumns);
        printWriter.println(str + "\tminCellSize: " + this.inv.minCellSize[this.mTypeIndex] + "dp");
        printWriter.println(str + pxToDpStr("cellWidthPx", (float) this.cellWidthPx));
        printWriter.println(str + pxToDpStr("cellHeightPx", (float) this.cellHeightPx));
        printWriter.println(str + pxToDpStr("getCellSize().x", (float) getCellSize().x));
        printWriter.println(str + pxToDpStr("getCellSize().y", (float) getCellSize().y));
        printWriter.println(str + pxToDpStr("cellLayoutBorderSpacePx Horizontal", (float) this.cellLayoutBorderSpacePx.x));
        printWriter.println(str + pxToDpStr("cellLayoutBorderSpacePx Vertical", (float) this.cellLayoutBorderSpacePx.y));
        printWriter.println(str + pxToDpStr("cellLayoutPaddingPx.left", (float) this.cellLayoutPaddingPx.left));
        printWriter.println(str + pxToDpStr("cellLayoutPaddingPx.top", (float) this.cellLayoutPaddingPx.top));
        printWriter.println(str + pxToDpStr("cellLayoutPaddingPx.right", (float) this.cellLayoutPaddingPx.right));
        printWriter.println(str + pxToDpStr("cellLayoutPaddingPx.bottom", (float) this.cellLayoutPaddingPx.bottom));
        printWriter.println(str + pxToDpStr("iconSizePx", (float) this.iconSizePx));
        printWriter.println(str + pxToDpStr("iconTextSizePx", (float) this.iconTextSizePx));
        printWriter.println(str + pxToDpStr("iconDrawablePaddingPx", (float) this.iconDrawablePaddingPx));
        printWriter.println(str + pxToDpStr("folderCellWidthPx", (float) this.folderCellWidthPx));
        printWriter.println(str + pxToDpStr("folderCellHeightPx", (float) this.folderCellHeightPx));
        printWriter.println(str + pxToDpStr("folderChildIconSizePx", (float) this.folderChildIconSizePx));
        printWriter.println(str + pxToDpStr("folderChildTextSizePx", (float) this.folderChildTextSizePx));
        printWriter.println(str + pxToDpStr("folderChildDrawablePaddingPx", (float) this.folderChildDrawablePaddingPx));
        printWriter.println(str + pxToDpStr("folderCellLayoutBorderSpaceOriginalPx", (float) this.folderCellLayoutBorderSpaceOriginalPx));
        printWriter.println(str + pxToDpStr("folderCellLayoutBorderSpacePx Horizontal", (float) this.folderCellLayoutBorderSpacePx.x));
        printWriter.println(str + pxToDpStr("folderCellLayoutBorderSpacePx Vertical", (float) this.folderCellLayoutBorderSpacePx.y));
        printWriter.println(str + pxToDpStr("bottomSheetTopPadding", (float) this.bottomSheetTopPadding));
        printWriter.println(str + pxToDpStr("allAppsShiftRange", (float) this.allAppsShiftRange));
        printWriter.println(str + pxToDpStr("allAppsTopPadding", (float) this.allAppsTopPadding));
        printWriter.println(str + pxToDpStr("allAppsIconSizePx", (float) this.allAppsIconSizePx));
        printWriter.println(str + pxToDpStr("allAppsIconTextSizePx", this.allAppsIconTextSizePx));
        printWriter.println(str + pxToDpStr("allAppsIconDrawablePaddingPx", (float) this.allAppsIconDrawablePaddingPx));
        printWriter.println(str + pxToDpStr("allAppsCellHeightPx", (float) this.allAppsCellHeightPx));
        printWriter.println(str + pxToDpStr("allAppsCellWidthPx", (float) this.allAppsCellWidthPx));
        printWriter.println(str + pxToDpStr("allAppsBorderSpacePx", (float) this.allAppsBorderSpacePx.x));
        printWriter.println(str + "\tnumShownAllAppsColumns: " + this.numShownAllAppsColumns);
        printWriter.println(str + pxToDpStr("allAppsLeftRightPadding", (float) this.allAppsLeftRightPadding));
        printWriter.println(str + pxToDpStr("allAppsLeftRightMargin", (float) this.allAppsLeftRightMargin));
        printWriter.println(str + pxToDpStr("hotseatBarSizePx", (float) this.hotseatBarSizePx));
        printWriter.println(str + "\tinv.hotseatColumnSpan: " + this.inv.hotseatColumnSpan[this.mTypeIndex]);
        printWriter.println(str + pxToDpStr("hotseatCellHeightPx", (float) this.hotseatCellHeightPx));
        printWriter.println(str + pxToDpStr("hotseatBarTopPaddingPx", (float) this.hotseatBarTopPaddingPx));
        printWriter.println(str + pxToDpStr("hotseatBarBottomPaddingPx", (float) this.hotseatBarBottomPaddingPx));
        printWriter.println(str + pxToDpStr("hotseatBarSidePaddingStartPx", (float) this.hotseatBarSidePaddingStartPx));
        printWriter.println(str + pxToDpStr("hotseatBarSidePaddingEndPx", (float) this.hotseatBarSidePaddingEndPx));
        printWriter.println(str + pxToDpStr("springLoadedHotseatBarTopMarginPx", (float) this.springLoadedHotseatBarTopMarginPx));
        printWriter.println(str + pxToDpStr("mHotseatPadding.top", (float) this.mHotseatPadding.top));
        printWriter.println(str + pxToDpStr("mHotseatPadding.bottom", (float) this.mHotseatPadding.bottom));
        printWriter.println(str + pxToDpStr("mHotseatPadding.left", (float) this.mHotseatPadding.left));
        printWriter.println(str + pxToDpStr("mHotseatPadding.right", (float) this.mHotseatPadding.right));
        printWriter.println(str + "\tnumShownHotseatIcons: " + this.numShownHotseatIcons);
        printWriter.println(str + pxToDpStr("hotseatBorderSpace", (float) this.hotseatBorderSpace));
        printWriter.println(str + "\tisQsbInline: " + this.isQsbInline);
        printWriter.println(str + pxToDpStr("qsbWidth", (float) this.qsbWidth));
        printWriter.println(str + "\tisTaskbarPresent:" + this.isTaskbarPresent);
        printWriter.println(str + "\tisTaskbarPresentInApps:" + this.isTaskbarPresentInApps);
        printWriter.println(str + pxToDpStr("taskbarSize", (float) this.taskbarSize));
        printWriter.println(str + pxToDpStr("desiredWorkspaceHorizontalMarginPx", (float) this.desiredWorkspaceHorizontalMarginPx));
        printWriter.println(str + pxToDpStr("workspacePadding.left", (float) this.workspacePadding.left));
        printWriter.println(str + pxToDpStr("workspacePadding.top", (float) this.workspacePadding.top));
        printWriter.println(str + pxToDpStr("workspacePadding.right", (float) this.workspacePadding.right));
        printWriter.println(str + pxToDpStr("workspacePadding.bottom", (float) this.workspacePadding.bottom));
        printWriter.println(str + pxToDpStr("iconScale", this.iconScale));
        printWriter.println(str + pxToDpStr("cellScaleToFit ", this.cellScaleToFit));
        printWriter.println(str + pxToDpStr("extraSpace", (float) this.extraSpace));
        printWriter.println(str + pxToDpStr("unscaled extraSpace", ((float) this.extraSpace) / this.iconScale));
        if (this.inv.devicePaddings != null) {
            printWriter.println(str + pxToDpStr("maxEmptySpace", (float) this.inv.devicePaddings.getDevicePadding((int) (((float) this.extraSpace) / this.iconScale)).getMaxEmptySpacePx()));
        }
        printWriter.println(str + pxToDpStr("workspaceTopPadding", (float) this.workspaceTopPadding));
        printWriter.println(str + pxToDpStr("workspaceBottomPadding", (float) this.workspaceBottomPadding));
        printWriter.println(str + pxToDpStr("extraHotseatBottomPadding", (float) this.extraHotseatBottomPadding));
        printWriter.println(str + pxToDpStr("overviewTaskMarginPx", (float) this.overviewTaskMarginPx));
        printWriter.println(str + pxToDpStr("overviewTaskMarginGridPx", (float) this.overviewTaskMarginGridPx));
        printWriter.println(str + pxToDpStr("overviewTaskIconSizePx", (float) this.overviewTaskIconSizePx));
        printWriter.println(str + pxToDpStr("overviewTaskIconDrawableSizePx", (float) this.overviewTaskIconDrawableSizePx));
        printWriter.println(str + pxToDpStr("overviewTaskIconDrawableSizeGridPx", (float) this.overviewTaskIconDrawableSizeGridPx));
        printWriter.println(str + pxToDpStr("overviewTaskThumbnailTopMarginPx", (float) this.overviewTaskThumbnailTopMarginPx));
        printWriter.println(str + pxToDpStr("overviewActionsTopMarginPx", (float) this.overviewActionsTopMarginPx));
        printWriter.println(str + pxToDpStr("overviewActionsHeight", (float) this.overviewActionsHeight));
        printWriter.println(str + pxToDpStr("overviewActionsButtonSpacing", (float) this.overviewActionsButtonSpacing));
        printWriter.println(str + pxToDpStr("overviewPageSpacing", (float) this.overviewPageSpacing));
        printWriter.println(str + pxToDpStr("overviewRowSpacing", (float) this.overviewRowSpacing));
        printWriter.println(str + pxToDpStr("overviewGridSideMargin", (float) this.overviewGridSideMargin));
        printWriter.println(str + pxToDpStr("dropTargetBarTopMarginPx", (float) this.dropTargetBarTopMarginPx));
        printWriter.println(str + pxToDpStr("dropTargetBarSizePx", (float) this.dropTargetBarSizePx));
        printWriter.println(str + pxToDpStr("dropTargetBarBottomMarginPx", (float) this.dropTargetBarBottomMarginPx));
        printWriter.println(str + pxToDpStr("workspaceSpringLoadShrunkTop", this.workspaceSpringLoadShrunkTop));
        printWriter.println(str + pxToDpStr("workspaceSpringLoadShrunkBottom", this.workspaceSpringLoadShrunkBottom));
        printWriter.println(str + pxToDpStr("workspaceSpringLoadedBottomSpace", (float) this.workspaceSpringLoadedBottomSpace));
        printWriter.println(str + pxToDpStr("workspaceSpringLoadedMinNextPageVisiblePx", (float) this.workspaceSpringLoadedMinNextPageVisiblePx));
        printWriter.println(str + pxToDpStr("getWorkspaceSpringLoadScale()", getWorkspaceSpringLoadScale()));
    }

    private static Context getContext(Context context, DisplayController.Info info, int i, WindowBounds windowBounds) {
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.orientation = i;
        configuration.densityDpi = info.getDensityDpi();
        configuration.smallestScreenWidthDp = (int) info.smallestSizeDp(windowBounds);
        return context.createConfigurationContext(configuration);
    }

    public interface DeviceProfileListenable {
        DeviceProfile getDeviceProfile();

        List<OnDeviceProfileChangeListener> getOnDeviceProfileChangeListeners();

        void dispatchDeviceProfileChanged() {
            DeviceProfile deviceProfile = getDeviceProfile();
            List<OnDeviceProfileChangeListener> onDeviceProfileChangeListeners = getOnDeviceProfileChangeListeners();
            for (int size = onDeviceProfileChangeListeners.size() - 1; size >= 0; size--) {
                onDeviceProfileChangeListeners.get(size).onDeviceProfileChanged(deviceProfile);
            }
        }

        void addOnDeviceProfileChangeListener(OnDeviceProfileChangeListener onDeviceProfileChangeListener) {
            getOnDeviceProfileChangeListeners().add(onDeviceProfileChangeListener);
        }

        void removeOnDeviceProfileChangeListener(OnDeviceProfileChangeListener onDeviceProfileChangeListener) {
            getOnDeviceProfileChangeListeners().remove(onDeviceProfileChangeListener);
        }
    }

    public static class Builder {
        private Context mContext;
        private DisplayController.Info mInfo;
        private InvariantDeviceProfile mInv;
        private Boolean mIsGestureMode;
        private boolean mIsMultiWindowMode = false;
        private Boolean mTransposeLayoutWithOrientation;
        private boolean mUseTwoPanels;
        private WindowBounds mWindowBounds;

        public Builder(Context context, InvariantDeviceProfile invariantDeviceProfile, DisplayController.Info info) {
            this.mContext = context;
            this.mInv = invariantDeviceProfile;
            this.mInfo = info;
        }

        public Builder setMultiWindowMode(boolean z) {
            this.mIsMultiWindowMode = z;
            return this;
        }

        public Builder setUseTwoPanels(boolean z) {
            this.mUseTwoPanels = z;
            return this;
        }

        public Builder setWindowBounds(WindowBounds windowBounds) {
            this.mWindowBounds = windowBounds;
            return this;
        }

        public Builder setTransposeLayoutWithOrientation(boolean z) {
            this.mTransposeLayoutWithOrientation = Boolean.valueOf(z);
            return this;
        }

        public Builder setGestureMode(boolean z) {
            this.mIsGestureMode = Boolean.valueOf(z);
            return this;
        }

        public DeviceProfile build() {
            WindowBounds windowBounds = this.mWindowBounds;
            if (windowBounds != null) {
                if (this.mTransposeLayoutWithOrientation == null) {
                    this.mTransposeLayoutWithOrientation = Boolean.valueOf(!this.mInfo.isTablet(windowBounds));
                }
                if (this.mIsGestureMode == null) {
                    this.mIsGestureMode = Boolean.valueOf(DisplayController.getNavigationMode(this.mContext).hasGestures);
                }
                return new DeviceProfile(this.mContext, this.mInv, this.mInfo, this.mWindowBounds, this.mIsMultiWindowMode, this.mTransposeLayoutWithOrientation.booleanValue(), this.mUseTwoPanels, this.mIsGestureMode.booleanValue());
            }
            throw new IllegalArgumentException("Window bounds not set");
        }
    }
}
