package com.android.launcher3.folder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.FloatProperty;
import android.view.View;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.Utilities;
import com.android.launcher3.graphics.PreloadIconDrawable;
import com.android.launcher3.icons.FastBitmapDrawable;
import com.android.launcher3.model.data.ItemInfoWithIcon;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.views.ActivityContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class PreviewItemManager {
    private static final FloatProperty<PreviewItemManager> CURRENT_PAGE_ITEMS_TRANS_X = new FloatProperty<PreviewItemManager>("currentPageItemsTransX") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(PreviewItemManager previewItemManager, float f) {
            float unused = previewItemManager.mCurrentPageItemsTransX = f;
            previewItemManager.onParamsChanged();
        }

        public Float get(PreviewItemManager previewItemManager) {
            return Float.valueOf(previewItemManager.mCurrentPageItemsTransX);
        }
    };
    private static final int FINAL_ITEM_ANIMATION_DURATION = 200;
    static final int INITIAL_ITEM_ANIMATION_DURATION = 350;
    private static final int ITEM_SLIDE_IN_OUT_DISTANCE_PX = 200;
    private static final int SLIDE_IN_FIRST_PAGE_ANIMATION_DURATION = 300;
    private static final int SLIDE_IN_FIRST_PAGE_ANIMATION_DURATION_DELAY = 100;
    private final float mClipThreshold;
    private final Context mContext;
    /* access modifiers changed from: private */
    public float mCurrentPageItemsTransX = 0.0f;
    /* access modifiers changed from: private */
    public ArrayList<PreviewItemDrawingParams> mCurrentPageParams = new ArrayList<>();
    private ArrayList<PreviewItemDrawingParams> mFirstPageParams = new ArrayList<>();
    private final FolderIcon mIcon;
    private final int mIconSize;
    private float mIntrinsicIconSize = -1.0f;
    private int mPrevTopPadding = -1;
    private Drawable mReferenceDrawable = null;
    private boolean mShouldSlideInFirstPage;
    private int mTotalWidth = -1;

    public PreviewItemManager(FolderIcon folderIcon) {
        Context context = folderIcon.getContext();
        this.mContext = context;
        this.mIcon = folderIcon;
        this.mIconSize = ((ActivityContext) ActivityContext.lookupContext(context)).getDeviceProfile().folderChildIconSizePx;
        this.mClipThreshold = (float) Utilities.dpToPx(1.0f);
    }

    public FolderPreviewItemAnim createFirstItemAnimation(boolean z, Runnable runnable) {
        if (z) {
            return new FolderPreviewItemAnim(this, this.mFirstPageParams.get(0), 0, 2, -1, -1, 200, runnable);
        }
        return new FolderPreviewItemAnim(this, this.mFirstPageParams.get(0), -1, -1, 0, 2, 350, runnable);
    }

    /* access modifiers changed from: package-private */
    public Drawable prepareCreateAnimation(View view) {
        FastBitmapDrawable icon = ((BubbleTextView) view).getIcon();
        computePreviewDrawingParams(icon.getIntrinsicWidth(), view.getMeasuredWidth());
        this.mReferenceDrawable = icon;
        return icon;
    }

    public void recomputePreviewDrawingParams() {
        Drawable drawable = this.mReferenceDrawable;
        if (drawable != null) {
            computePreviewDrawingParams(drawable.getIntrinsicWidth(), this.mIcon.getMeasuredWidth());
        }
    }

    private void computePreviewDrawingParams(int i, int i2) {
        float f = (float) i;
        if (this.mIntrinsicIconSize != f || this.mTotalWidth != i2 || this.mPrevTopPadding != this.mIcon.getPaddingTop()) {
            this.mIntrinsicIconSize = f;
            this.mTotalWidth = i2;
            this.mPrevTopPadding = this.mIcon.getPaddingTop();
            PreviewBackground previewBackground = this.mIcon.mBackground;
            Context context = this.mIcon.getContext();
            ActivityContext activityContext = this.mIcon.mActivity;
            FolderIcon folderIcon = this.mIcon;
            previewBackground.setup(context, activityContext, folderIcon, this.mTotalWidth, folderIcon.getPaddingTop());
            this.mIcon.mPreviewLayoutRule.init(this.mIcon.mBackground.previewSize, this.mIntrinsicIconSize, Utilities.isRtl(this.mIcon.getResources()));
            updatePreviewItems(false);
        }
    }

    /* access modifiers changed from: package-private */
    public PreviewItemDrawingParams computePreviewItemDrawingParams(int i, int i2, PreviewItemDrawingParams previewItemDrawingParams) {
        if (i == -1) {
            return getFinalIconParams(previewItemDrawingParams);
        }
        return this.mIcon.mPreviewLayoutRule.computePreviewItemDrawingParams(i, i2, previewItemDrawingParams);
    }

    private PreviewItemDrawingParams getFinalIconParams(PreviewItemDrawingParams previewItemDrawingParams) {
        float f = (float) this.mIcon.mActivity.getDeviceProfile().iconSizePx;
        float f2 = (((float) this.mIcon.mBackground.previewSize) - f) / 2.0f;
        previewItemDrawingParams.update(f2, f2, f / ((float) this.mReferenceDrawable.getIntrinsicWidth()));
        return previewItemDrawingParams;
    }

    public void drawParams(Canvas canvas, ArrayList<PreviewItemDrawingParams> arrayList, PointF pointF, boolean z, Path path) {
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            PreviewItemDrawingParams previewItemDrawingParams = arrayList.get(size);
            if (!previewItemDrawingParams.hidden) {
                drawPreviewItem(canvas, previewItemDrawingParams, pointF, (previewItemDrawingParams.index == -2.0f) | z, path);
            }
        }
    }

    public void draw(Canvas canvas) {
        float f;
        int saveCount = canvas.getSaveCount();
        PreviewBackground folderBackground = this.mIcon.getFolderBackground();
        Path clipPath = folderBackground.getClipPath();
        if (this.mShouldSlideInFirstPage) {
            drawParams(canvas, this.mCurrentPageParams, new PointF(((float) folderBackground.basePreviewOffsetX) + this.mCurrentPageItemsTransX, (float) folderBackground.basePreviewOffsetY), this.mCurrentPageItemsTransX > this.mClipThreshold, clipPath);
            f = this.mCurrentPageItemsTransX - 0.022460938f;
        } else {
            f = 0.0f;
        }
        drawParams(canvas, this.mFirstPageParams, new PointF(((float) folderBackground.basePreviewOffsetX) + f, (float) folderBackground.basePreviewOffsetY), f < (-this.mClipThreshold), clipPath);
        canvas.restoreToCount(saveCount);
    }

    public void onParamsChanged() {
        this.mIcon.invalidate();
    }

    private void drawPreviewItem(Canvas canvas, PreviewItemDrawingParams previewItemDrawingParams, PointF pointF, boolean z, Path path) {
        canvas.save();
        if (z) {
            canvas.clipPath(path);
        }
        canvas.translate(pointF.x + previewItemDrawingParams.transX, pointF.y + previewItemDrawingParams.transY);
        canvas.scale(previewItemDrawingParams.scale, previewItemDrawingParams.scale);
        Drawable drawable = previewItemDrawingParams.drawable;
        if (drawable != null) {
            Rect bounds = drawable.getBounds();
            canvas.save();
            canvas.translate((float) (-bounds.left), (float) (-bounds.top));
            canvas.scale(this.mIntrinsicIconSize / ((float) bounds.width()), this.mIntrinsicIconSize / ((float) bounds.height()));
            drawable.draw(canvas);
            canvas.restore();
        }
        canvas.restore();
    }

    public void hidePreviewItem(int i, boolean z) {
        int max = i + Math.max(this.mFirstPageParams.size() - 4, 0);
        PreviewItemDrawingParams previewItemDrawingParams = max < this.mFirstPageParams.size() ? this.mFirstPageParams.get(max) : null;
        if (previewItemDrawingParams != null) {
            previewItemDrawingParams.hidden = z;
        }
    }

    /* access modifiers changed from: package-private */
    public void buildParamsForPage(int i, ArrayList<PreviewItemDrawingParams> arrayList, boolean z) {
        PreviewItemManager previewItemManager = this;
        int i2 = i;
        ArrayList<PreviewItemDrawingParams> arrayList2 = arrayList;
        List<WorkspaceItemInfo> previewItemsOnPage = previewItemManager.mIcon.getPreviewItemsOnPage(i2);
        int size = arrayList.size();
        while (previewItemsOnPage.size() < arrayList.size()) {
            arrayList2.remove(arrayList.size() - 1);
        }
        while (previewItemsOnPage.size() > arrayList.size()) {
            arrayList2.add(new PreviewItemDrawingParams(0.0f, 0.0f, 0.0f));
        }
        int size2 = i2 == 0 ? previewItemsOnPage.size() : 4;
        int i3 = 0;
        while (i3 < arrayList.size()) {
            PreviewItemDrawingParams previewItemDrawingParams = arrayList2.get(i3);
            previewItemManager.setDrawable(previewItemDrawingParams, previewItemsOnPage.get(i3));
            if (!z) {
                if (previewItemDrawingParams.anim != null) {
                    previewItemDrawingParams.anim.cancel();
                }
                previewItemManager.computePreviewItemDrawingParams(i3, size2, previewItemDrawingParams);
                if (previewItemManager.mReferenceDrawable == null) {
                    previewItemManager.mReferenceDrawable = previewItemDrawingParams.drawable;
                }
            } else {
                FolderPreviewItemAnim folderPreviewItemAnim = r0;
                FolderPreviewItemAnim folderPreviewItemAnim2 = new FolderPreviewItemAnim(this, previewItemDrawingParams, i3, size, i3, size2, 400, (Runnable) null);
                if (previewItemDrawingParams.anim != null) {
                    if (!previewItemDrawingParams.anim.hasEqualFinalState(folderPreviewItemAnim)) {
                        previewItemDrawingParams.anim.cancel();
                    }
                }
                previewItemDrawingParams.anim = folderPreviewItemAnim;
                previewItemDrawingParams.anim.start();
            }
            i3++;
            previewItemManager = this;
        }
    }

    /* access modifiers changed from: package-private */
    public void onFolderClose(int i) {
        boolean z = i != 0;
        this.mShouldSlideInFirstPage = z;
        if (z) {
            this.mCurrentPageItemsTransX = 0.0f;
            buildParamsForPage(i, this.mCurrentPageParams, false);
            onParamsChanged();
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, CURRENT_PAGE_ITEMS_TRANS_X, new float[]{0.0f, 200.0f});
            ofFloat.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    PreviewItemManager.this.mCurrentPageParams.clear();
                }
            });
            ofFloat.setStartDelay(100);
            ofFloat.setDuration(300);
            ofFloat.start();
        }
    }

    /* access modifiers changed from: package-private */
    public void updatePreviewItems(boolean z) {
        buildParamsForPage(0, this.mFirstPageParams, z);
    }

    /* access modifiers changed from: package-private */
    public void updatePreviewItems(Predicate<WorkspaceItemInfo> predicate) {
        Iterator<PreviewItemDrawingParams> it = this.mFirstPageParams.iterator();
        boolean z = false;
        while (it.hasNext()) {
            PreviewItemDrawingParams next = it.next();
            if (predicate.test(next.item)) {
                setDrawable(next, next.item);
                z = true;
            }
        }
        Iterator<PreviewItemDrawingParams> it2 = this.mCurrentPageParams.iterator();
        while (it2.hasNext()) {
            PreviewItemDrawingParams next2 = it2.next();
            if (predicate.test(next2.item)) {
                setDrawable(next2, next2.item);
                z = true;
            }
        }
        if (z) {
            this.mIcon.invalidate();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean verifyDrawable(Drawable drawable) {
        for (int i = 0; i < this.mFirstPageParams.size(); i++) {
            if (this.mFirstPageParams.get(i).drawable == drawable) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public float getIntrinsicIconSize() {
        return this.mIntrinsicIconSize;
    }

    public void onDrop(List<WorkspaceItemInfo> list, List<WorkspaceItemInfo> list2, WorkspaceItemInfo workspaceItemInfo) {
        int size = list2.size();
        ArrayList<PreviewItemDrawingParams> arrayList = this.mFirstPageParams;
        buildParamsForPage(0, arrayList, false);
        ArrayList arrayList2 = new ArrayList();
        for (WorkspaceItemInfo next : list2) {
            if (!list.contains(next) && !next.equals(workspaceItemInfo)) {
                arrayList2.add(next);
            }
        }
        for (int i = 0; i < arrayList2.size(); i++) {
            int indexOf = list2.indexOf(arrayList2.get(i));
            PreviewItemDrawingParams previewItemDrawingParams = arrayList.get(indexOf);
            computePreviewItemDrawingParams(indexOf, size, previewItemDrawingParams);
            updateTransitionParam(previewItemDrawingParams, (WorkspaceItemInfo) arrayList2.get(i), -3, list2.indexOf(arrayList2.get(i)), size);
        }
        for (int i2 = 0; i2 < list2.size(); i2++) {
            int indexOf2 = list.indexOf(list2.get(i2));
            if (indexOf2 >= 0 && i2 != indexOf2) {
                updateTransitionParam(arrayList.get(i2), list2.get(i2), indexOf2, i2, size);
            }
        }
        ArrayList arrayList3 = new ArrayList(list);
        arrayList3.removeAll(list2);
        for (int i3 = 0; i3 < arrayList3.size(); i3++) {
            WorkspaceItemInfo workspaceItemInfo2 = (WorkspaceItemInfo) arrayList3.get(i3);
            int indexOf3 = list.indexOf(workspaceItemInfo2);
            PreviewItemDrawingParams computePreviewItemDrawingParams = computePreviewItemDrawingParams(indexOf3, size, (PreviewItemDrawingParams) null);
            updateTransitionParam(computePreviewItemDrawingParams, workspaceItemInfo2, indexOf3, -2, size);
            arrayList.add(0, computePreviewItemDrawingParams);
        }
        for (int i4 = 0; i4 < arrayList.size(); i4++) {
            if (arrayList.get(i4).anim != null) {
                arrayList.get(i4).anim.start();
            }
        }
    }

    private void updateTransitionParam(PreviewItemDrawingParams previewItemDrawingParams, WorkspaceItemInfo workspaceItemInfo, int i, int i2, int i3) {
        setDrawable(previewItemDrawingParams, workspaceItemInfo);
        FolderPreviewItemAnim folderPreviewItemAnim = new FolderPreviewItemAnim(this, previewItemDrawingParams, i, i3, i2, i3, 400, (Runnable) null);
        if (previewItemDrawingParams.anim != null && !previewItemDrawingParams.anim.hasEqualFinalState(folderPreviewItemAnim)) {
            previewItemDrawingParams.anim.cancel();
        }
        previewItemDrawingParams.anim = folderPreviewItemAnim;
    }

    private void setDrawable(PreviewItemDrawingParams previewItemDrawingParams, WorkspaceItemInfo workspaceItemInfo) {
        if (workspaceItemInfo.hasPromiseIconUi() || (workspaceItemInfo.runtimeStatusFlags & ItemInfoWithIcon.FLAG_SHOW_DOWNLOAD_PROGRESS_MASK) != 0) {
            PreloadIconDrawable newPendingIcon = PreloadIconDrawable.newPendingIcon(this.mContext, workspaceItemInfo);
            newPendingIcon.setLevel(workspaceItemInfo.getProgressLevel());
            previewItemDrawingParams.drawable = newPendingIcon;
        } else {
            previewItemDrawingParams.drawable = workspaceItemInfo.newIcon(this.mContext, 1);
        }
        Drawable drawable = previewItemDrawingParams.drawable;
        int i = this.mIconSize;
        drawable.setBounds(0, 0, i, i);
        previewItemDrawingParams.item = workspaceItemInfo;
        previewItemDrawingParams.drawable.setCallback(this.mIcon);
    }
}
