package com.android.launcher3.views;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.os.CancellationSignal;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.InsettableFrameLayout;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.icons.FastBitmapDrawable;
import com.android.launcher3.icons.LauncherIcons;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.ItemInfoWithIcon;
import com.android.launcher3.util.Executors;
import com.android.launcher3.views.FloatingIconView;
import java.util.Objects;
import java.util.function.Supplier;

public class FloatingIconView extends FrameLayout implements Animator.AnimatorListener, ViewTreeObserver.OnGlobalLayoutListener, FloatingView {
    public static final float SHAPE_PROGRESS_DURATION = 0.1f;
    private static final String TAG = "FloatingIconView";
    private static long sFetchIconId;
    private static IconLoadResult sIconLoadResult;
    private static long sRecycledFetchIconId;
    private static final Object[] sTmpObjArray = new Object[1];
    private static final RectF sTmpRectF = new RectF();
    private Drawable mBadge;
    private View mBtvDrawable;
    private ClipIconView mClipIconView;
    private Runnable mEndRunnable;
    private Runnable mFastFinishRunnable;
    private final Rect mFinalDrawableBounds;
    private IconLoadResult mIconLoadResult;
    private float mIconOffsetY;
    private boolean mIsOpening;
    private final boolean mIsRtl;
    private final Launcher mLauncher;
    private ListenerView mListenerView;
    private CancellationSignal mLoadIconSignal;
    private Runnable mOnTargetChangeRunnable;
    private View mOriginalIcon;
    private RectF mPositionOut;

    static /* synthetic */ Drawable lambda$fetchIcon$2(FastBitmapDrawable fastBitmapDrawable) {
        return fastBitmapDrawable;
    }

    static /* synthetic */ Drawable lambda$getIconResult$0(Drawable drawable) {
        return drawable;
    }

    public void onAnimationCancel(Animator animator) {
    }

    public void onAnimationRepeat(Animator animator) {
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

    public FloatingIconView(Context context) {
        this(context, (AttributeSet) null);
    }

    public FloatingIconView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FloatingIconView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mFinalDrawableBounds = new Rect();
        this.mLauncher = Launcher.getLauncher(context);
        this.mIsRtl = Utilities.isRtl(getResources());
        this.mListenerView = new ListenerView(context, attributeSet);
        this.mClipIconView = new ClipIconView(context, attributeSet);
        ImageView imageView = new ImageView(context, attributeSet);
        this.mBtvDrawable = imageView;
        addView(imageView);
        addView(this.mClipIconView);
        setWillNotDraw(false);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!this.mIsOpening) {
            getViewTreeObserver().addOnGlobalLayoutListener(this);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
        super.onDetachedFromWindow();
    }

    public void update(float f, int i, RectF rectF, float f2, float f3, float f4, boolean z) {
        setAlpha(f);
        this.mClipIconView.update(rectF, f2, f3, f4, i, z, this, this.mLauncher.getDeviceProfile());
    }

    public void onAnimationEnd(Animator animator) {
        CancellationSignal cancellationSignal = this.mLoadIconSignal;
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
        }
        Runnable runnable = this.mEndRunnable;
        if (runnable != null) {
            runnable.run();
        } else {
            this.mClipIconView.endReveal();
        }
    }

    private void matchPositionOf(Launcher launcher, View view, boolean z, RectF rectF) {
        getLocationBoundsForView(launcher, view, z, rectF);
        InsettableFrameLayout.LayoutParams layoutParams = new InsettableFrameLayout.LayoutParams(Math.round(rectF.width()), Math.round(rectF.height()));
        updatePosition(rectF, layoutParams);
        setLayoutParams(layoutParams);
        this.mClipIconView.setLayoutParams(new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height));
        this.mBtvDrawable.setLayoutParams(new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height));
    }

    private void updatePosition(RectF rectF, InsettableFrameLayout.LayoutParams layoutParams) {
        int i;
        this.mPositionOut.set(rectF);
        layoutParams.ignoreInsets = true;
        layoutParams.topMargin = Math.round(rectF.top);
        if (this.mIsRtl) {
            layoutParams.setMarginStart(Math.round(((float) this.mLauncher.getDeviceProfile().widthPx) - rectF.right));
        } else {
            layoutParams.setMarginStart(Math.round(rectF.left));
        }
        if (this.mIsRtl) {
            i = (this.mLauncher.getDeviceProfile().widthPx - layoutParams.getMarginStart()) - layoutParams.width;
        } else {
            i = layoutParams.leftMargin;
        }
        layout(i, layoutParams.topMargin, layoutParams.width + i, layoutParams.topMargin + layoutParams.height);
    }

    private static void getLocationBoundsForView(Launcher launcher, View view, boolean z, RectF rectF) {
        getLocationBoundsForView(launcher, view, z, rectF, new Rect());
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x0028  */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x0027 A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void getLocationBoundsForView(com.android.launcher3.Launcher r8, android.view.View r9, boolean r10, android.graphics.RectF r11, android.graphics.Rect r12) {
        /*
            r10 = r10 ^ 1
            boolean r0 = r9 instanceof com.android.launcher3.views.BubbleTextHolder
            r1 = 0
            if (r0 == 0) goto L_0x0010
            com.android.launcher3.views.BubbleTextHolder r9 = (com.android.launcher3.views.BubbleTextHolder) r9
            com.android.launcher3.BubbleTextView r9 = r9.getBubbleText()
        L_0x000d:
            r3 = r9
            r5 = r1
            goto L_0x0025
        L_0x0010:
            android.view.ViewParent r0 = r9.getParent()
            boolean r0 = r0 instanceof com.android.launcher3.shortcuts.DeepShortcutView
            if (r0 == 0) goto L_0x0023
            android.view.ViewParent r9 = r9.getParent()
            com.android.launcher3.shortcuts.DeepShortcutView r9 = (com.android.launcher3.shortcuts.DeepShortcutView) r9
            android.view.View r9 = r9.getIconView()
            goto L_0x000d
        L_0x0023:
            r3 = r9
            r5 = r10
        L_0x0025:
            if (r3 != 0) goto L_0x0028
            return
        L_0x0028:
            boolean r9 = r3 instanceof com.android.launcher3.BubbleTextView
            if (r9 == 0) goto L_0x0033
            r9 = r3
            com.android.launcher3.BubbleTextView r9 = (com.android.launcher3.BubbleTextView) r9
            r9.getIconBounds(r12)
            goto L_0x0049
        L_0x0033:
            boolean r9 = r3 instanceof com.android.launcher3.folder.FolderIcon
            if (r9 == 0) goto L_0x003e
            r9 = r3
            com.android.launcher3.folder.FolderIcon r9 = (com.android.launcher3.folder.FolderIcon) r9
            r9.getPreviewBounds(r12)
            goto L_0x0049
        L_0x003e:
            int r9 = r3.getWidth()
            int r10 = r3.getHeight()
            r12.set(r1, r1, r9, r10)
        L_0x0049:
            com.android.launcher3.dragndrop.DragLayer r2 = r8.getDragLayer()
            r6 = 0
            r4 = r12
            r7 = r11
            com.android.launcher3.Utilities.getBoundsForViewInDragLayer(r2, r3, r4, r5, r6, r7)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.views.FloatingIconView.getLocationBoundsForView(com.android.launcher3.Launcher, android.view.View, boolean, android.graphics.RectF, android.graphics.Rect):void");
    }

    /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:659)
        	at java.util.ArrayList.get(ArrayList.java:435)
        	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
        */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0075  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0077  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0085  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0087  */
    private static void getIconResult(com.android.launcher3.Launcher r10, android.view.View r11, com.android.launcher3.model.data.ItemInfo r12, android.graphics.RectF r13, android.graphics.drawable.Drawable r14, com.android.launcher3.views.FloatingIconView.IconLoadResult r15) {
        /*
            boolean r0 = r12.isDisabled()
            r1 = 1
            r0 = r0 ^ r1
            boolean r2 = r12 instanceof com.android.launcher3.popup.SystemShortcut
            r3 = 0
            if (r2 == 0) goto L_0x002a
            boolean r12 = r11 instanceof android.widget.ImageView
            if (r12 == 0) goto L_0x0016
            android.widget.ImageView r11 = (android.widget.ImageView) r11
            android.graphics.drawable.Drawable r11 = r11.getDrawable()
            goto L_0x0067
        L_0x0016:
            boolean r12 = r11 instanceof com.android.launcher3.shortcuts.DeepShortcutView
            if (r12 == 0) goto L_0x0025
            com.android.launcher3.shortcuts.DeepShortcutView r11 = (com.android.launcher3.shortcuts.DeepShortcutView) r11
            android.view.View r11 = r11.getIconView()
            android.graphics.drawable.Drawable r11 = r11.getBackground()
            goto L_0x0067
        L_0x0025:
            android.graphics.drawable.Drawable r11 = r11.getBackground()
            goto L_0x0067
        L_0x002a:
            boolean r2 = r14 instanceof com.android.launcher3.graphics.PreloadIconDrawable
            if (r2 == 0) goto L_0x002f
            goto L_0x0066
        L_0x002f:
            float r2 = r13.width()
            int r6 = (int) r2
            float r2 = r13.height()
            int r7 = (int) r2
            if (r0 == 0) goto L_0x0062
            boolean r11 = r14 instanceof com.android.launcher3.icons.FastBitmapDrawable
            r0 = 0
            if (r11 == 0) goto L_0x004b
            r11 = r14
            com.android.launcher3.icons.FastBitmapDrawable r11 = (com.android.launcher3.icons.FastBitmapDrawable) r11
            boolean r11 = r11.isThemed()
            if (r11 == 0) goto L_0x004b
            r8 = r1
            goto L_0x004c
        L_0x004b:
            r8 = r0
        L_0x004c:
            java.lang.Object[] r11 = sTmpObjArray
            r4 = r10
            r5 = r12
            r9 = r11
            android.graphics.drawable.Drawable r2 = com.android.launcher3.Utilities.getFullDrawable(r4, r5, r6, r7, r8, r9)
            boolean r4 = r2 instanceof android.graphics.drawable.AdaptiveIconDrawable
            if (r4 == 0) goto L_0x0066
            r11 = r11[r0]
            android.graphics.drawable.Drawable r11 = com.android.launcher3.Utilities.getBadge(r10, r12, r11)
            r12 = r11
            r11 = r2
            goto L_0x0073
        L_0x0062:
            boolean r11 = r11 instanceof com.android.launcher3.BubbleTextView
            if (r11 == 0) goto L_0x0069
        L_0x0066:
            r11 = r14
        L_0x0067:
            r12 = r3
            goto L_0x0073
        L_0x0069:
            r8 = 1
            java.lang.Object[] r9 = sTmpObjArray
            r4 = r10
            r5 = r12
            android.graphics.drawable.Drawable r11 = com.android.launcher3.Utilities.getFullDrawable(r4, r5, r6, r7, r8, r9)
            goto L_0x0067
        L_0x0073:
            if (r11 != 0) goto L_0x0077
            r11 = r3
            goto L_0x007f
        L_0x0077:
            android.graphics.drawable.Drawable$ConstantState r11 = r11.getConstantState()
            android.graphics.drawable.Drawable r11 = r11.newDrawable()
        L_0x007f:
            int r13 = getOffsetForIconBounds(r10, r11, r13)
            if (r14 != 0) goto L_0x0087
            r14 = r3
            goto L_0x008f
        L_0x0087:
            android.graphics.drawable.Drawable$ConstantState r14 = r14.getConstantState()
            android.graphics.drawable.Drawable r14 = r14.newDrawable()
        L_0x008f:
            monitor-enter(r15)
            com.android.launcher3.views.-$$Lambda$FloatingIconView$AVnVUUnLtKrRqnl_NSVvpf1kLaw r0 = new com.android.launcher3.views.-$$Lambda$FloatingIconView$AVnVUUnLtKrRqnl_NSVvpf1kLaw     // Catch:{ all -> 0x00b0 }
            r0.<init>(r14)     // Catch:{ all -> 0x00b0 }
            r15.btvDrawable = r0     // Catch:{ all -> 0x00b0 }
            r15.drawable = r11     // Catch:{ all -> 0x00b0 }
            r15.badge = r12     // Catch:{ all -> 0x00b0 }
            r15.iconOffset = r13     // Catch:{ all -> 0x00b0 }
            java.lang.Runnable r11 = r15.onIconLoaded     // Catch:{ all -> 0x00b0 }
            if (r11 == 0) goto L_0x00ac
            java.util.concurrent.Executor r10 = r10.getMainExecutor()     // Catch:{ all -> 0x00b0 }
            java.lang.Runnable r11 = r15.onIconLoaded     // Catch:{ all -> 0x00b0 }
            r10.execute(r11)     // Catch:{ all -> 0x00b0 }
            r15.onIconLoaded = r3     // Catch:{ all -> 0x00b0 }
        L_0x00ac:
            r15.isIconLoaded = r1     // Catch:{ all -> 0x00b0 }
            monitor-exit(r15)     // Catch:{ all -> 0x00b0 }
            return
        L_0x00b0:
            r10 = move-exception
            monitor-exit(r15)     // Catch:{ all -> 0x00b0 }
            throw r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.views.FloatingIconView.getIconResult(com.android.launcher3.Launcher, android.view.View, com.android.launcher3.model.data.ItemInfo, android.graphics.RectF, android.graphics.drawable.Drawable, com.android.launcher3.views.FloatingIconView$IconLoadResult):void");
    }

    private void setIcon(Drawable drawable, Drawable drawable2, Supplier<Drawable> supplier, int i) {
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        InsettableFrameLayout.LayoutParams layoutParams = (InsettableFrameLayout.LayoutParams) getLayoutParams();
        this.mBadge = drawable2;
        this.mClipIconView.setIcon(drawable, i, layoutParams, this.mIsOpening, deviceProfile);
        if (drawable instanceof AdaptiveIconDrawable) {
            int i2 = layoutParams.height;
            this.mFinalDrawableBounds.set(0, 0, layoutParams.width, i2);
            float f = this.mLauncher.getDeviceProfile().aspectRatio;
            if (deviceProfile.isLandscape) {
                layoutParams.width = (int) Math.max((float) layoutParams.width, ((float) layoutParams.height) * f);
            } else {
                layoutParams.height = (int) Math.max((float) layoutParams.height, ((float) layoutParams.width) * f);
            }
            setLayoutParams(layoutParams);
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.mClipIconView.getLayoutParams();
            if (this.mBadge != null) {
                FastBitmapDrawable.setBadgeBounds(this.mBadge, new Rect(0, 0, layoutParams2.width, layoutParams2.height));
            }
            layoutParams2.width = layoutParams.width;
            layoutParams2.height = layoutParams.height;
            this.mClipIconView.setLayoutParams(layoutParams2);
        }
        setOriginalDrawableBackground(supplier);
        invalidate();
    }

    private void setOriginalDrawableBackground(Supplier<Drawable> supplier) {
        if (!this.mIsOpening) {
            this.mBtvDrawable.setBackground(supplier == null ? null : supplier.get());
        }
    }

    public boolean isDifferentFromAppIcon() {
        IconLoadResult iconLoadResult = this.mIconLoadResult;
        if (iconLoadResult == null) {
            return false;
        }
        return iconLoadResult.isThemed;
    }

    private void checkIconResult(View view) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        IconLoadResult iconLoadResult = this.mIconLoadResult;
        if (iconLoadResult == null) {
            Log.w(TAG, "No icon load result found in checkIconResult");
            return;
        }
        synchronized (iconLoadResult) {
            if (this.mIconLoadResult.isIconLoaded) {
                setIcon(this.mIconLoadResult.drawable, this.mIconLoadResult.badge, this.mIconLoadResult.btvDrawable, this.mIconLoadResult.iconOffset);
                setVisibility(0);
                IconLabelDotView.setIconAndDotVisible(view, false);
            } else {
                this.mIconLoadResult.onIconLoaded = new Runnable(cancellationSignal, view) {
                    public final /* synthetic */ CancellationSignal f$1;
                    public final /* synthetic */ View f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        FloatingIconView.this.lambda$checkIconResult$1$FloatingIconView(this.f$1, this.f$2);
                    }
                };
                this.mLoadIconSignal = cancellationSignal;
            }
        }
    }

    public /* synthetic */ void lambda$checkIconResult$1$FloatingIconView(CancellationSignal cancellationSignal, View view) {
        if (!cancellationSignal.isCanceled()) {
            setIcon(this.mIconLoadResult.drawable, this.mIconLoadResult.badge, this.mIconLoadResult.btvDrawable, this.mIconLoadResult.iconOffset);
            setVisibility(0);
            IconLabelDotView.setIconAndDotVisible(view, false);
        }
    }

    private static int getOffsetForIconBounds(Launcher launcher, Drawable drawable, RectF rectF) {
        if (!(drawable instanceof AdaptiveIconDrawable)) {
            return 0;
        }
        int dimensionPixelSize = launcher.getResources().getDimensionPixelSize(R.dimen.blur_size_medium_outline);
        Rect rect = new Rect(0, 0, ((int) rectF.width()) + dimensionPixelSize, ((int) rectF.height()) + dimensionPixelSize);
        int i = dimensionPixelSize / 2;
        rect.inset(i, i);
        LauncherIcons obtain = LauncherIcons.obtain(launcher);
        try {
            Utilities.scaleRectAboutCenter(rect, obtain.getNormalizer().getScale(drawable, (RectF) null, (Path) null, (boolean[]) null));
            if (obtain != null) {
                obtain.close();
            }
            rect.inset((int) (((float) (-rect.width())) * AdaptiveIconDrawable.getExtraInsetFraction()), (int) (((float) (-rect.height())) * AdaptiveIconDrawable.getExtraInsetFraction()));
            return rect.left;
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        Drawable drawable = this.mBadge;
        if (drawable != null) {
            drawable.draw(canvas);
        }
    }

    public void setFastFinishRunnable(Runnable runnable) {
        this.mFastFinishRunnable = runnable;
    }

    public void fastFinish() {
        Runnable runnable = this.mFastFinishRunnable;
        if (runnable != null) {
            runnable.run();
            this.mFastFinishRunnable = null;
        }
        CancellationSignal cancellationSignal = this.mLoadIconSignal;
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
            this.mLoadIconSignal = null;
        }
        Runnable runnable2 = this.mEndRunnable;
        if (runnable2 != null) {
            runnable2.run();
            this.mEndRunnable = null;
        }
    }

    public void onAnimationStart(Animator animator) {
        View view;
        IconLoadResult iconLoadResult = this.mIconLoadResult;
        if ((iconLoadResult != null && iconLoadResult.isIconLoaded) || (!this.mIsOpening && this.mBtvDrawable.getBackground() != null)) {
            setVisibility(0);
        }
        if (!this.mIsOpening && (view = this.mOriginalIcon) != null) {
            IconLabelDotView.setIconAndDotVisible(view, false);
        }
    }

    public void setPositionOffsetY(float f) {
        this.mIconOffsetY = f;
        onGlobalLayout();
    }

    public void onGlobalLayout() {
        View view = this.mOriginalIcon;
        if (view != null && view.isAttachedToWindow() && this.mPositionOut != null) {
            Launcher launcher = this.mLauncher;
            View view2 = this.mOriginalIcon;
            boolean z = this.mIsOpening;
            RectF rectF = sTmpRectF;
            getLocationBoundsForView(launcher, view2, z, rectF);
            rectF.offset(0.0f, this.mIconOffsetY);
            if (!rectF.equals(this.mPositionOut)) {
                updatePosition(rectF, (InsettableFrameLayout.LayoutParams) getLayoutParams());
                Runnable runnable = this.mOnTargetChangeRunnable;
                if (runnable != null) {
                    runnable.run();
                }
            }
        }
    }

    public void setOnTargetChangeListener(Runnable runnable) {
        this.mOnTargetChangeRunnable = runnable;
    }

    public static IconLoadResult fetchIcon(Launcher launcher, View view, ItemInfo itemInfo, boolean z) {
        FastBitmapDrawable fastBitmapDrawable;
        FastBitmapDrawable fastBitmapDrawable2;
        RectF rectF = new RectF();
        getLocationBoundsForView(launcher, view, z, rectF);
        Supplier<Drawable> supplier = null;
        if (view instanceof BubbleTextView) {
            BubbleTextView bubbleTextView = (BubbleTextView) view;
            if (!(itemInfo instanceof ItemInfoWithIcon) || (((ItemInfoWithIcon) itemInfo).runtimeStatusFlags & ItemInfoWithIcon.FLAG_SHOW_DOWNLOAD_PROGRESS_MASK) == 0) {
                fastBitmapDrawable2 = bubbleTextView.getIcon();
                supplier = new Supplier() {
                    public final Object get() {
                        return FastBitmapDrawable.this.getConstantState().newDrawable();
                    }
                };
            } else {
                fastBitmapDrawable2 = bubbleTextView.makePreloadIcon();
                supplier = new Supplier() {
                    public final Object get() {
                        return FloatingIconView.lambda$fetchIcon$2(FastBitmapDrawable.this);
                    }
                };
            }
            fastBitmapDrawable = fastBitmapDrawable2;
        } else {
            fastBitmapDrawable = null;
        }
        IconLoadResult iconLoadResult = new IconLoadResult(itemInfo, fastBitmapDrawable != null && fastBitmapDrawable.isThemed());
        iconLoadResult.btvDrawable = supplier;
        long j = sFetchIconId;
        sFetchIconId = 1 + j;
        Executors.MODEL_EXECUTOR.getHandler().postAtFrontOfQueue(new Runnable(j, launcher, view, itemInfo, rectF, fastBitmapDrawable, iconLoadResult) {
            public final /* synthetic */ long f$0;
            public final /* synthetic */ Launcher f$1;
            public final /* synthetic */ View f$2;
            public final /* synthetic */ ItemInfo f$3;
            public final /* synthetic */ RectF f$4;
            public final /* synthetic */ FastBitmapDrawable f$5;
            public final /* synthetic */ FloatingIconView.IconLoadResult f$6;

            {
                this.f$0 = r1;
                this.f$1 = r3;
                this.f$2 = r4;
                this.f$3 = r5;
                this.f$4 = r6;
                this.f$5 = r7;
                this.f$6 = r8;
            }

            public final void run() {
                FloatingIconView.lambda$fetchIcon$4(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        });
        sIconLoadResult = iconLoadResult;
        return iconLoadResult;
    }

    static /* synthetic */ void lambda$fetchIcon$4(long j, Launcher launcher, View view, ItemInfo itemInfo, RectF rectF, FastBitmapDrawable fastBitmapDrawable, IconLoadResult iconLoadResult) {
        if (j >= sRecycledFetchIconId) {
            getIconResult(launcher, view, itemInfo, rectF, fastBitmapDrawable, iconLoadResult);
        }
    }

    public static FloatingIconView getFloatingIconView(Launcher launcher, View view, boolean z, RectF rectF, boolean z2) {
        DragLayer dragLayer = launcher.getDragLayer();
        ViewGroup viewGroup = (ViewGroup) dragLayer.getParent();
        FloatingIconView floatingIconView = (FloatingIconView) launcher.getViewCache().getView(R.layout.floating_icon_view, launcher, viewGroup);
        floatingIconView.recycle();
        floatingIconView.mIsOpening = z2;
        floatingIconView.mOriginalIcon = view;
        floatingIconView.mPositionOut = rectF;
        boolean z3 = (view.getTag() instanceof ItemInfo) && z;
        if (z3) {
            IconLoadResult iconLoadResult = sIconLoadResult;
            if (iconLoadResult == null || iconLoadResult.itemInfo != view.getTag()) {
                floatingIconView.mIconLoadResult = fetchIcon(launcher, view, (ItemInfo) view.getTag(), z2);
            } else {
                floatingIconView.mIconLoadResult = sIconLoadResult;
            }
            floatingIconView.setOriginalDrawableBackground(floatingIconView.mIconLoadResult.btvDrawable);
        }
        sIconLoadResult = null;
        floatingIconView.matchPositionOf(launcher, view, z2, rectF);
        IconLabelDotView.setIconAndDotVisible(floatingIconView, false);
        viewGroup.addView(floatingIconView);
        dragLayer.addView(floatingIconView.mListenerView);
        ListenerView listenerView = floatingIconView.mListenerView;
        Objects.requireNonNull(floatingIconView);
        listenerView.setListener(new Runnable() {
            public final void run() {
                FloatingIconView.this.fastFinish();
            }
        });
        floatingIconView.mEndRunnable = new Runnable(z, view, dragLayer) {
            public final /* synthetic */ boolean f$1;
            public final /* synthetic */ View f$2;
            public final /* synthetic */ DragLayer f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                FloatingIconView.lambda$getFloatingIconView$5(FloatingIconView.this, this.f$1, this.f$2, this.f$3);
            }
        };
        if (z3) {
            floatingIconView.checkIconResult(view);
        }
        return floatingIconView;
    }

    static /* synthetic */ void lambda$getFloatingIconView$5(FloatingIconView floatingIconView, boolean z, View view, DragLayer dragLayer) {
        floatingIconView.mEndRunnable = null;
        if (z) {
            IconLabelDotView.setIconAndDotVisible(view, true);
            floatingIconView.finish(dragLayer);
            return;
        }
        floatingIconView.finish(dragLayer);
    }

    private void finish(DragLayer dragLayer) {
        ((ViewGroup) dragLayer.getParent()).removeView(this);
        dragLayer.removeView(this.mListenerView);
        recycle();
        this.mLauncher.getViewCache().recycleView(R.layout.floating_icon_view, this);
    }

    private void recycle() {
        setTranslationX(0.0f);
        setTranslationY(0.0f);
        setScaleX(1.0f);
        setScaleY(1.0f);
        setAlpha(1.0f);
        CancellationSignal cancellationSignal = this.mLoadIconSignal;
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
        }
        this.mLoadIconSignal = null;
        this.mEndRunnable = null;
        this.mFinalDrawableBounds.setEmpty();
        this.mIsOpening = false;
        this.mPositionOut = null;
        this.mListenerView.setListener((Runnable) null);
        this.mOriginalIcon = null;
        this.mOnTargetChangeRunnable = null;
        this.mBadge = null;
        sTmpObjArray[0] = null;
        sRecycledFetchIconId = sFetchIconId;
        this.mIconLoadResult = null;
        this.mClipIconView.recycle();
        this.mBtvDrawable.setBackground((Drawable) null);
        this.mFastFinishRunnable = null;
        this.mIconOffsetY = 0.0f;
    }

    private static class IconLoadResult {
        Drawable badge;
        Supplier<Drawable> btvDrawable;
        Drawable drawable;
        int iconOffset;
        boolean isIconLoaded;
        final boolean isThemed;
        final ItemInfo itemInfo;
        Runnable onIconLoaded;

        IconLoadResult(ItemInfo itemInfo2, boolean z) {
            this.itemInfo = itemInfo2;
            this.isThemed = z;
        }
    }
}
