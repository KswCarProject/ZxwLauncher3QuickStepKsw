package com.android.launcher3.folder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.util.Property;
import android.view.View;
import android.view.animation.AnimationUtils;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.R;
import com.android.launcher3.ShortcutAndWidgetContainer;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.PropertyResetListener;
import com.android.launcher3.graphics.IconShape;
import com.android.launcher3.util.Themes;
import com.android.launcher3.views.BaseDragLayer;
import java.util.Iterator;
import java.util.List;

public class FolderAnimationManager {
    private static final int FOLDER_NAME_ALPHA_DURATION = 32;
    private static final int LARGE_FOLDER_FOOTER_DURATION = 128;
    private ObjectAnimator mBgColorAnimator;
    /* access modifiers changed from: private */
    public FolderPagedView mContent;
    private Context mContext;
    private final int mDelay;
    private DeviceProfile mDeviceProfile;
    private final int mDuration;
    /* access modifiers changed from: private */
    public Folder mFolder;
    private GradientDrawable mFolderBackground;
    private FolderIcon mFolderIcon;
    private final TimeInterpolator mFolderInterpolator;
    /* access modifiers changed from: private */
    public final boolean mIsOpening;
    private final TimeInterpolator mLargeFolderPreviewItemCloseInterpolator;
    private final TimeInterpolator mLargeFolderPreviewItemOpenInterpolator;
    private PreviewBackground mPreviewBackground;
    private final FolderGridOrganizer mPreviewVerifier;
    private final PreviewItemDrawingParams mTmpParams = new PreviewItemDrawingParams(0.0f, 0.0f, 0.0f);

    public FolderAnimationManager(Folder folder, boolean z) {
        this.mFolder = folder;
        this.mContent = folder.mContent;
        this.mFolderBackground = (GradientDrawable) this.mFolder.getBackground();
        FolderIcon folderIcon = folder.mFolderIcon;
        this.mFolderIcon = folderIcon;
        this.mPreviewBackground = folderIcon.mBackground;
        this.mContext = folder.getContext();
        this.mDeviceProfile = folder.mActivityContext.getDeviceProfile();
        this.mPreviewVerifier = new FolderGridOrganizer(this.mDeviceProfile.inv);
        this.mIsOpening = z;
        Resources resources = this.mContent.getResources();
        this.mDuration = resources.getInteger(R.integer.config_materialFolderExpandDuration);
        this.mDelay = resources.getInteger(R.integer.config_folderDelay);
        this.mFolderInterpolator = AnimationUtils.loadInterpolator(this.mContext, R.interpolator.folder_interpolator);
        this.mLargeFolderPreviewItemOpenInterpolator = AnimationUtils.loadInterpolator(this.mContext, R.interpolator.large_folder_preview_item_open_interpolator);
        this.mLargeFolderPreviewItemCloseInterpolator = AnimationUtils.loadInterpolator(this.mContext, R.interpolator.large_folder_preview_item_close_interpolator);
    }

    public ObjectAnimator getBgColorAnimator() {
        return this.mBgColorAnimator;
    }

    public AnimatorSet getAnimator() {
        int i;
        int i2;
        BaseDragLayer.LayoutParams layoutParams = (BaseDragLayer.LayoutParams) this.mFolder.getLayoutParams();
        this.mFolderIcon.getPreviewItemManager().recomputePreviewDrawingParams();
        ClippedFolderIconLayoutRule layoutRule = this.mFolderIcon.getLayoutRule();
        List<BubbleTextView> previewIconsOnPage = getPreviewIconsOnPage(0);
        Rect rect = new Rect();
        float descendantRectRelativeToSelf = this.mFolder.mActivityContext.getDragLayer().getDescendantRectRelativeToSelf(this.mFolderIcon, rect);
        int scaledRadius = this.mPreviewBackground.getScaledRadius();
        float f = ((float) (scaledRadius * 2)) * descendantRectRelativeToSelf;
        float iconSize = layoutRule.getIconSize() * layoutRule.scaleForItem(previewIconsOnPage.size());
        float iconSize2 = (iconSize / ((float) previewIconsOnPage.get(0).getIconSize())) * descendantRectRelativeToSelf;
        float f2 = this.mIsOpening ? iconSize2 : 1.0f;
        this.mFolder.setPivotX(0.0f);
        this.mFolder.setPivotY(0.0f);
        this.mFolder.mContent.setScaleX(f2);
        this.mFolder.mContent.setScaleY(f2);
        this.mFolder.mContent.setPivotX(0.0f);
        this.mFolder.mContent.setPivotY(0.0f);
        this.mFolder.mFooter.setScaleX(f2);
        this.mFolder.mFooter.setScaleY(f2);
        this.mFolder.mFooter.setPivotX(0.0f);
        this.mFolder.mFooter.setPivotY(0.0f);
        int i3 = (int) (iconSize / 2.0f);
        if (Utilities.isRtl(this.mContext.getResources())) {
            i3 = (int) (((((float) layoutParams.width) * iconSize2) - f) - ((float) i3));
        }
        int i4 = i3;
        int paddingLeft = (int) (((float) this.mContent.getPaddingLeft()) * iconSize2);
        int paddingTop = (int) (((float) this.mContent.getPaddingTop()) * iconSize2);
        float paddingLeft2 = (float) (((((rect.left + this.mFolder.getPaddingLeft()) + this.mPreviewBackground.getOffsetX()) - paddingLeft) - i4) - layoutParams.x);
        float paddingTop2 = (float) ((((rect.top + this.mFolder.getPaddingTop()) + this.mPreviewBackground.getOffsetY()) - paddingTop) - layoutParams.y);
        int attrColor = Themes.getAttrColor(this.mContext, R.attr.folderPreviewColor);
        int attrColor2 = Themes.getAttrColor(this.mContext, R.attr.folderBackgroundColor);
        this.mFolderBackground.mutate();
        this.mFolderBackground.setColor(this.mIsOpening ? attrColor : attrColor2);
        int i5 = paddingLeft + i4;
        Rect rect2 = new Rect(i5, paddingTop, Math.round(((float) i5) + f), Math.round(((float) paddingTop) + f));
        Rect rect3 = new Rect(0, 0, layoutParams.width, layoutParams.height);
        float cornerRadius = this.mFolderBackground.getCornerRadius();
        AnimatorSet animatorSet = new AnimatorSet();
        int i6 = i4;
        PropertyResetListener propertyResetListener = new PropertyResetListener(BubbleTextView.TEXT_ALPHA_PROPERTY, Float.valueOf(1.0f));
        Folder folder = this.mFolder;
        Iterator<BubbleTextView> it = folder.getItemsOnPage(folder.mContent.getCurrentPage()).iterator();
        while (it.hasNext()) {
            BubbleTextView next = it.next();
            Iterator<BubbleTextView> it2 = it;
            if (this.mIsOpening) {
                next.setTextVisibility(false);
            }
            ObjectAnimator createTextAlphaAnimator = next.createTextAlphaAnimator(this.mIsOpening);
            createTextAlphaAnimator.addListener(propertyResetListener);
            play(animatorSet, createTextAlphaAnimator);
            it = it2;
        }
        ObjectAnimator animator = getAnimator(this.mFolderBackground, "color", attrColor, attrColor2);
        this.mBgColorAnimator = animator;
        play(animatorSet, animator);
        play(animatorSet, getAnimator((View) this.mFolder, View.TRANSLATION_X, paddingLeft2, 0.0f));
        play(animatorSet, getAnimator((View) this.mFolder, View.TRANSLATION_Y, paddingTop2, 0.0f));
        play(animatorSet, getAnimator((View) this.mFolder.mContent, (Property) LauncherAnimUtils.SCALE_PROPERTY, iconSize2, 1.0f));
        play(animatorSet, getAnimator(this.mFolder.mFooter, (Property) LauncherAnimUtils.SCALE_PROPERTY, iconSize2, 1.0f));
        if (!isLargeFolder()) {
            i = this.mDuration;
            i2 = 0;
        } else if (this.mIsOpening) {
            i2 = this.mDuration - 128;
            i = 128;
        } else {
            i2 = 0;
            i = 0;
        }
        float f3 = descendantRectRelativeToSelf;
        AnimatorSet animatorSet2 = animatorSet;
        play(animatorSet, getAnimator(this.mFolder.mFooter, View.ALPHA, 0.0f, 1.0f), (long) i2, i);
        play(animatorSet2, IconShape.getShape().createRevealAnimator(this.mFolder, rect2, rect3, cornerRadius, !this.mIsOpening));
        int i7 = this.mDeviceProfile.folderCellLayoutBorderSpacePx.x + (this.mDeviceProfile.folderCellWidthPx * 2);
        int i8 = this.mDeviceProfile.folderCellLayoutBorderSpacePx.y + (this.mDeviceProfile.folderCellHeightPx * 2);
        int paddingLeft3 = this.mContent.getPaddingLeft() + ((this.mIsOpening ? this.mContent.getCurrentPage() : this.mContent.getDestinationPage()) * layoutParams.width);
        play(animatorSet2, IconShape.getShape().createRevealAnimator(this.mFolder.getContent(), new Rect(paddingLeft3, 0, i7 + paddingLeft3, i8), new Rect(paddingLeft3, 0, layoutParams.width + paddingLeft3, layoutParams.height), cornerRadius, !this.mIsOpening));
        this.mFolder.mFolderName.setAlpha(this.mIsOpening ? 0.0f : 1.0f);
        Animator animator2 = getAnimator((View) this.mFolder.mFolderName, View.ALPHA, 0.0f, 1.0f);
        boolean z = this.mIsOpening;
        play(animatorSet2, animator2, z ? 32 : 0, z ? this.mDuration - 32 : 32);
        float contentAreaHeight = (float) this.mFolder.getContentAreaHeight();
        play(animatorSet2, getAnimator(this.mFolder.mFooter, View.TRANSLATION_Y, -(contentAreaHeight - (contentAreaHeight * iconSize2)), 0.0f));
        int i9 = this.mDuration / 2;
        play(animatorSet2, getAnimator((View) this.mFolder, View.TRANSLATION_Z, -this.mFolder.getElevation(), 0.0f), this.mIsOpening ? (long) i9 : 0, i9);
        final CellLayout currentCellLayout = this.mContent.getCurrentCellLayout();
        final boolean clipChildren = this.mFolder.getClipChildren();
        final boolean clipToPadding = this.mFolder.getClipToPadding();
        final boolean clipChildren2 = this.mContent.getClipChildren();
        final boolean clipToPadding2 = this.mContent.getClipToPadding();
        final boolean clipChildren3 = currentCellLayout.getClipChildren();
        final boolean clipToPadding3 = currentCellLayout.getClipToPadding();
        this.mFolder.setClipChildren(false);
        this.mFolder.setClipToPadding(false);
        this.mContent.setClipChildren(false);
        this.mContent.setClipToPadding(false);
        currentCellLayout.setClipChildren(false);
        currentCellLayout.setClipToPadding(false);
        animatorSet2.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                FolderAnimationManager.this.mFolder.setTranslationX(0.0f);
                FolderAnimationManager.this.mFolder.setTranslationY(0.0f);
                FolderAnimationManager.this.mFolder.setTranslationZ(0.0f);
                FolderAnimationManager.this.mFolder.mContent.setScaleX(1.0f);
                FolderAnimationManager.this.mFolder.mContent.setScaleY(1.0f);
                FolderAnimationManager.this.mFolder.mFooter.setScaleX(1.0f);
                FolderAnimationManager.this.mFolder.mFooter.setScaleY(1.0f);
                FolderAnimationManager.this.mFolder.mFooter.setTranslationX(0.0f);
                FolderAnimationManager.this.mFolder.mFolderName.setAlpha(1.0f);
                FolderAnimationManager.this.mFolder.setClipChildren(clipChildren);
                FolderAnimationManager.this.mFolder.setClipToPadding(clipToPadding);
                FolderAnimationManager.this.mContent.setClipChildren(clipChildren2);
                FolderAnimationManager.this.mContent.setClipToPadding(clipToPadding2);
                currentCellLayout.setClipChildren(clipChildren3);
                currentCellLayout.setClipToPadding(clipToPadding3);
            }
        });
        Iterator<Animator> it3 = animatorSet2.getChildAnimations().iterator();
        while (it3.hasNext()) {
            it3.next().setInterpolator(this.mFolderInterpolator);
        }
        int radius = scaledRadius - this.mPreviewBackground.getRadius();
        addPreviewItemAnimators(animatorSet2, iconSize2 / f3, i6 + radius, radius);
        return animatorSet2;
    }

    private List<BubbleTextView> getPreviewIconsOnPage(int i) {
        return this.mPreviewVerifier.setFolderInfo(this.mFolder.mInfo).previewItemsForPage(i, this.mFolder.getIconsInReadingOrder());
    }

    private void addPreviewItemAnimators(AnimatorSet animatorSet, float f, int i, int i2) {
        int i3;
        List<BubbleTextView> list;
        int i4;
        int i5;
        List<BubbleTextView> list2;
        AnimatorSet animatorSet2 = animatorSet;
        ClippedFolderIconLayoutRule layoutRule = this.mFolderIcon.getLayoutRule();
        boolean z = true;
        boolean z2 = this.mFolder.mContent.getCurrentPage() == 0;
        if (z2) {
            i3 = 0;
        } else {
            i3 = this.mFolder.mContent.getCurrentPage();
        }
        List<BubbleTextView> previewIconsOnPage = getPreviewIconsOnPage(i3);
        int size = previewIconsOnPage.size();
        int i6 = z2 ? size : 4;
        TimeInterpolator previewItemInterpolator = getPreviewItemInterpolator();
        ShortcutAndWidgetContainer shortcutsAndWidgets = this.mContent.getPageAt(0).getShortcutsAndWidgets();
        int i7 = 0;
        while (i7 < size) {
            final BubbleTextView bubbleTextView = previewIconsOnPage.get(i7);
            CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) bubbleTextView.getLayoutParams();
            layoutParams.isLockedToGrid = z;
            shortcutsAndWidgets.setupLp(bubbleTextView);
            float iconSize = (layoutRule.getIconSize() * layoutRule.scaleForItem(i6)) / ((float) previewIconsOnPage.get(i7).getIconSize());
            float f2 = iconSize / f;
            float f3 = this.mIsOpening ? f2 : 1.0f;
            bubbleTextView.setScaleX(f3);
            bubbleTextView.setScaleY(f3);
            layoutRule.computePreviewItemDrawingParams(i7, i6, this.mTmpParams);
            int iconSize2 = (int) (((this.mTmpParams.transX - ((float) (((int) (((float) (layoutParams.width - bubbleTextView.getIconSize())) * iconSize)) / 2))) + ((float) i)) / f);
            ClippedFolderIconLayoutRule clippedFolderIconLayoutRule = layoutRule;
            int paddingTop = (int) (((this.mTmpParams.transY + ((float) i2)) - (((float) bubbleTextView.getPaddingTop()) * iconSize)) / f);
            final float f4 = (float) (iconSize2 - layoutParams.x);
            float f5 = (float) (paddingTop - layoutParams.y);
            Animator animator = getAnimator((View) bubbleTextView, View.TRANSLATION_X, f4, 0.0f);
            animator.setInterpolator(previewItemInterpolator);
            play(animatorSet2, animator);
            Animator animator2 = getAnimator((View) bubbleTextView, View.TRANSLATION_Y, f5, 0.0f);
            animator2.setInterpolator(previewItemInterpolator);
            play(animatorSet2, animator2);
            int i8 = i7;
            Animator animator3 = getAnimator((View) bubbleTextView, (Property) LauncherAnimUtils.SCALE_PROPERTY, f2, 1.0f);
            animator3.setInterpolator(previewItemInterpolator);
            play(animatorSet2, animator3);
            if (this.mFolder.getItemCount() > 4) {
                boolean z3 = this.mIsOpening;
                int i9 = this.mDelay;
                if (!z3) {
                    i9 *= 2;
                }
                if (z3) {
                    list2 = previewIconsOnPage;
                    i5 = size;
                    long j = (long) i9;
                    animator.setStartDelay(j);
                    animator2.setStartDelay(j);
                    animator3.setStartDelay(j);
                } else {
                    list2 = previewIconsOnPage;
                    i5 = size;
                }
                list = list2;
                i4 = i6;
                long j2 = (long) i9;
                animator.setDuration(animator.getDuration() - j2);
                animator2.setDuration(animator2.getDuration() - j2);
                animator3.setDuration(animator3.getDuration() - j2);
            } else {
                list = previewIconsOnPage;
                i5 = size;
                i4 = i6;
            }
            final float f6 = f2;
            final float f7 = f5;
            animatorSet2.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animator) {
                    super.onAnimationStart(animator);
                    if (FolderAnimationManager.this.mIsOpening) {
                        bubbleTextView.setTranslationX(f4);
                        bubbleTextView.setTranslationY(f7);
                        bubbleTextView.setScaleX(f6);
                        bubbleTextView.setScaleY(f6);
                    }
                }

                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    bubbleTextView.setTranslationX(0.0f);
                    bubbleTextView.setTranslationY(0.0f);
                    bubbleTextView.setScaleX(1.0f);
                    bubbleTextView.setScaleY(1.0f);
                }
            });
            i7 = i8 + 1;
            size = i5;
            layoutRule = clippedFolderIconLayoutRule;
            i6 = i4;
            previewIconsOnPage = list;
            z = true;
        }
    }

    private void play(AnimatorSet animatorSet, Animator animator) {
        play(animatorSet, animator, animator.getStartDelay(), this.mDuration);
    }

    private void play(AnimatorSet animatorSet, Animator animator, long j, int i) {
        animator.setStartDelay(j);
        animator.setDuration((long) i);
        animatorSet.play(animator);
    }

    private boolean isLargeFolder() {
        return this.mFolder.getItemCount() > 4;
    }

    private TimeInterpolator getPreviewItemInterpolator() {
        if (!isLargeFolder()) {
            return this.mFolderInterpolator;
        }
        if (this.mIsOpening) {
            return this.mLargeFolderPreviewItemOpenInterpolator;
        }
        return this.mLargeFolderPreviewItemCloseInterpolator;
    }

    private Animator getAnimator(View view, Property property, float f, float f2) {
        if (this.mIsOpening) {
            return ObjectAnimator.ofFloat(view, property, new float[]{f, f2});
        }
        return ObjectAnimator.ofFloat(view, property, new float[]{f2, f});
    }

    private ObjectAnimator getAnimator(GradientDrawable gradientDrawable, String str, int i, int i2) {
        if (this.mIsOpening) {
            return ObjectAnimator.ofArgb(gradientDrawable, str, new int[]{i, i2});
        }
        return ObjectAnimator.ofArgb(gradientDrawable, str, new int[]{i2, i});
    }
}
