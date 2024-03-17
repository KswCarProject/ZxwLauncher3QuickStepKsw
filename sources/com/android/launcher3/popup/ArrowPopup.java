package com.android.launcher3.popup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import androidx.core.content.ContextCompat;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.Workspace;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.shortcuts.DeepShortcutView;
import com.android.launcher3.util.Themes;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.views.BaseDragLayer;
import com.android.launcher3.widget.LocalColorExtractor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.IntUnaryOperator;

public abstract class ArrowPopup<T extends Context & ActivityContext> extends AbstractFloatingView {
    private static final int DARK_COLOR_EXTRACTION_INDEX = 17170484;
    private static final int LIGHT_COLOR_EXTRACTION_INDEX = 17170502;
    protected int CLOSE_CHILD_FADE_DURATION;
    protected int CLOSE_CHILD_FADE_START_DELAY;
    protected int CLOSE_DURATION;
    protected int CLOSE_FADE_DURATION;
    protected int CLOSE_FADE_START_DELAY;
    protected int OPEN_CHILD_FADE_DURATION;
    protected int OPEN_CHILD_FADE_START_DELAY;
    protected int OPEN_DURATION;
    protected int OPEN_FADE_DURATION;
    protected int OPEN_FADE_START_DELAY;
    protected final T mActivityContext;
    protected final View mArrow;
    protected int mArrowColor;
    protected final int mArrowHeight;
    protected final int mArrowOffsetHorizontal;
    protected final int mArrowOffsetVertical;
    protected final int mArrowPointRadius;
    protected final int mArrowWidth;
    private final int mBackgroundColor;
    protected final List<LocalColorExtractor> mColorExtractors;
    private final int[] mColorIds;
    protected boolean mDeferContainerRemoval;
    protected final float mElevation;
    protected int mGravity;
    protected final LayoutInflater mInflater;
    protected boolean mIsAboveIcon;
    protected boolean mIsLeftAligned;
    protected final boolean mIsRtl;
    private final String mIterateChildrenTag;
    private final int mMargin;
    private Runnable mOnCloseCallback;
    protected AnimatorSet mOpenCloseAnimator;
    protected final float mOutlineRadius;
    private final GradientDrawable mRoundedBottom;
    private final GradientDrawable mRoundedTop;
    protected final Rect mTempRect;
    protected boolean shouldScaleArrow;

    /* access modifiers changed from: protected */
    public abstract void getTargetObjectLocation(Rect rect);

    /* access modifiers changed from: protected */
    public void onCreateCloseAnimation(AnimatorSet animatorSet) {
    }

    /* access modifiers changed from: protected */
    public void onCreateOpenAnimation(AnimatorSet animatorSet) {
    }

    /* access modifiers changed from: protected */
    public void onInflationComplete(boolean z) {
    }

    /* access modifiers changed from: protected */
    public boolean shouldAddArrow() {
        return true;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ArrowPopup(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        Context context2 = context;
        this.OPEN_DURATION = 276;
        this.OPEN_FADE_START_DELAY = 0;
        this.OPEN_FADE_DURATION = 38;
        this.OPEN_CHILD_FADE_START_DELAY = 38;
        this.OPEN_CHILD_FADE_DURATION = 76;
        this.CLOSE_DURATION = 200;
        this.CLOSE_FADE_START_DELAY = 140;
        this.CLOSE_FADE_DURATION = 50;
        this.CLOSE_CHILD_FADE_START_DELAY = 0;
        this.CLOSE_CHILD_FADE_DURATION = 140;
        this.mTempRect = new Rect();
        this.shouldScaleArrow = false;
        this.mOnCloseCallback = null;
        this.mInflater = LayoutInflater.from(context);
        float dialogCornerRadius = Themes.getDialogCornerRadius(context);
        this.mOutlineRadius = dialogCornerRadius;
        T lookupContext = ActivityContext.lookupContext(context);
        this.mActivityContext = lookupContext;
        this.mIsRtl = Utilities.isRtl(getResources());
        int attrColor = Themes.getAttrColor(context2, R.attr.popupColorPrimary);
        this.mBackgroundColor = attrColor;
        this.mArrowColor = attrColor;
        this.mElevation = getResources().getDimension(R.dimen.deep_shortcuts_elevation);
        Resources resources = getResources();
        this.mMargin = resources.getDimensionPixelSize(R.dimen.popup_margin);
        int dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.popup_arrow_width);
        this.mArrowWidth = dimensionPixelSize;
        int dimensionPixelSize2 = resources.getDimensionPixelSize(R.dimen.popup_arrow_height);
        this.mArrowHeight = dimensionPixelSize2;
        View view = new View(context2);
        this.mArrow = view;
        view.setLayoutParams(new BaseDragLayer.LayoutParams(dimensionPixelSize, dimensionPixelSize2));
        this.mArrowOffsetVertical = resources.getDimensionPixelSize(R.dimen.popup_arrow_vertical_offset);
        this.mArrowOffsetHorizontal = resources.getDimensionPixelSize(R.dimen.popup_arrow_horizontal_center_offset) - (dimensionPixelSize / 2);
        this.mArrowPointRadius = resources.getDimensionPixelSize(R.dimen.popup_arrow_corner_radius);
        int dimensionPixelSize3 = resources.getDimensionPixelSize(R.dimen.popup_smaller_radius);
        GradientDrawable gradientDrawable = new GradientDrawable();
        this.mRoundedTop = gradientDrawable;
        gradientDrawable.setColor(attrColor);
        float f = (float) dimensionPixelSize3;
        gradientDrawable.setCornerRadii(new float[]{dialogCornerRadius, dialogCornerRadius, dialogCornerRadius, dialogCornerRadius, f, f, f, f});
        GradientDrawable gradientDrawable2 = new GradientDrawable();
        this.mRoundedBottom = gradientDrawable2;
        gradientDrawable2.setColor(attrColor);
        gradientDrawable2.setCornerRadii(new float[]{f, f, f, f, dialogCornerRadius, dialogCornerRadius, dialogCornerRadius, dialogCornerRadius});
        this.mIterateChildrenTag = getContext().getString(R.string.popup_container_iterate_children);
        boolean shouldUseColorExtractionForPopup = ((ActivityContext) lookupContext).shouldUseColorExtractionForPopup();
        if (!shouldUseColorExtractionForPopup || !Utilities.ATLEAST_S || !FeatureFlags.ENABLE_LOCAL_COLOR_POPUPS.get()) {
            this.mColorExtractors = null;
        } else {
            this.mColorExtractors = new ArrayList();
        }
        if (shouldUseColorExtractionForPopup) {
            this.mColorIds = new int[]{R.color.popup_shade_first, R.color.popup_shade_second, R.color.popup_shade_third};
            return;
        }
        this.mColorIds = new int[]{R.color.popup_shade_first};
    }

    public ArrowPopup(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ArrowPopup(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    /* access modifiers changed from: protected */
    public void handleClose(boolean z) {
        if (z) {
            animateClose();
        } else {
            closeComplete();
        }
    }

    public <R extends View> R inflateAndAdd(int i, ViewGroup viewGroup) {
        R inflate = this.mInflater.inflate(i, viewGroup, false);
        viewGroup.addView(inflate);
        return inflate;
    }

    public <R extends View> R inflateAndAdd(int i, ViewGroup viewGroup, int i2) {
        R inflate = this.mInflater.inflate(i, viewGroup, false);
        viewGroup.addView(inflate, i2);
        return inflate;
    }

    public void assignMarginsAndBackgrounds(ViewGroup viewGroup) {
        assignMarginsAndBackgrounds(viewGroup, 0);
    }

    /* access modifiers changed from: protected */
    public void assignMarginsAndBackgrounds(ViewGroup viewGroup, int i) {
        View view = null;
        int[] array = i == 0 ? Arrays.stream(this.mColorIds).map(new IntUnaryOperator() {
            public final int applyAsInt(int i) {
                return ArrowPopup.this.lambda$assignMarginsAndBackgrounds$0$ArrowPopup(i);
            }
        }).toArray() : null;
        int childCount = viewGroup.getChildCount();
        int i2 = 0;
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = viewGroup.getChildAt(i3);
            if (childAt.getVisibility() == 0 && isShortcutOrWrapper(childAt)) {
                i2++;
            }
        }
        AnimatorSet animatorSet = new AnimatorSet();
        int i4 = 0;
        int i5 = 0;
        for (int i6 = 0; i6 < childCount; i6++) {
            View childAt2 = viewGroup.getChildAt(i6);
            if (childAt2.getVisibility() == 0) {
                if (view != null) {
                    ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).bottomMargin = this.mMargin;
                }
                ((ViewGroup.MarginLayoutParams) childAt2.getLayoutParams()).bottomMargin = 0;
                if (array != null) {
                    i = array[i4 % array.length];
                }
                if (!FeatureFlags.ENABLE_LOCAL_COLOR_POPUPS.get()) {
                    boolean z = this.mIsAboveIcon;
                    if (!z && i4 == 0 && viewGroup == this) {
                        this.mArrowColor = i;
                    } else if (z) {
                        this.mArrowColor = i;
                    }
                }
                if (!(childAt2 instanceof ViewGroup) || !this.mIterateChildrenTag.equals(childAt2.getTag())) {
                    if (isShortcutOrWrapper(childAt2)) {
                        if (i2 == 1) {
                            childAt2.setBackgroundResource(R.drawable.single_item_primary);
                        } else if (i2 > 1) {
                            if (i5 == 0) {
                                childAt2.setBackground(this.mRoundedTop.getConstantState().newDrawable());
                            } else if (i5 == i2 - 1) {
                                childAt2.setBackground(this.mRoundedBottom.getConstantState().newDrawable());
                            } else {
                                childAt2.setBackgroundResource(R.drawable.middle_item_primary);
                            }
                            i5++;
                        }
                    }
                    if (!FeatureFlags.ENABLE_LOCAL_COLOR_POPUPS.get()) {
                        setChildColor(childAt2, i, animatorSet);
                    }
                } else {
                    assignMarginsAndBackgrounds((ViewGroup) childAt2, i);
                }
                i4++;
                view = childAt2;
            }
        }
        animatorSet.setDuration(0).start();
        measure(0, 0);
    }

    public /* synthetic */ int lambda$assignMarginsAndBackgrounds$0$ArrowPopup(int i) {
        return ContextCompat.getColorStateList(getContext(), i).getDefaultColor();
    }

    /* access modifiers changed from: protected */
    public boolean isShortcutOrWrapper(View view) {
        return view instanceof DeepShortcutView;
    }

    private int getExtractedColor(SparseIntArray sparseIntArray) {
        return sparseIntArray.get(Utilities.isDarkTheme(getContext()) ? DARK_COLOR_EXTRACTION_INDEX : LIGHT_COLOR_EXTRACTION_INDEX, this.mBackgroundColor);
    }

    /* access modifiers changed from: protected */
    public void addPreDrawForColorExtraction(final Launcher launcher) {
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                ArrowPopup.this.getViewTreeObserver().removeOnPreDrawListener(this);
                ArrowPopup.this.initColorExtractionLocations(launcher);
                return true;
            }
        });
    }

    /* access modifiers changed from: protected */
    public List<View> getChildrenForColorExtraction() {
        return Collections.emptyList();
    }

    /* access modifiers changed from: private */
    public void initColorExtractionLocations(Launcher launcher) {
        Workspace<?> workspace;
        if (this.mColorExtractors != null && (workspace = launcher.getWorkspace()) != null) {
            int screenIdForPageIndex = workspace.getScreenIdForPageIndex(workspace.getCurrentPage());
            DragLayer dragLayer = launcher.getDragLayer();
            boolean z = true;
            View[] viewArr = new View[1];
            for (View next : getChildrenForColorExtraction()) {
                if (next != null && next.getVisibility() == 0) {
                    Rect rect = new Rect();
                    dragLayer.getDescendantRectRelativeToSelf(next, rect);
                    if (!rect.isEmpty()) {
                        LocalColorExtractor newInstance = LocalColorExtractor.newInstance(launcher);
                        newInstance.setWorkspaceLocation(rect, dragLayer, screenIdForPageIndex);
                        newInstance.setListener(new LocalColorExtractor.Listener(next, viewArr) {
                            public final /* synthetic */ View f$1;
                            public final /* synthetic */ View[] f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void onColorsChanged(SparseIntArray sparseIntArray) {
                                ArrowPopup.this.lambda$initColorExtractionLocations$1$ArrowPopup(this.f$1, this.f$2, sparseIntArray);
                            }
                        });
                        this.mColorExtractors.add(newInstance);
                        if (this.mIsAboveIcon || z) {
                            viewArr[0] = next;
                        }
                        z = false;
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$initColorExtractionLocations$1$ArrowPopup(View view, View[] viewArr, SparseIntArray sparseIntArray) {
        AnimatorSet animatorSet = new AnimatorSet();
        int extractedColor = getExtractedColor(sparseIntArray);
        setChildColor(view, extractedColor, animatorSet);
        int childCount = view instanceof ViewGroup ? ((ViewGroup) view).getChildCount() : 0;
        for (int i = 0; i < childCount; i++) {
            setChildColor(((ViewGroup) view).getChildAt(i), extractedColor, animatorSet);
        }
        if (viewArr[0] == view) {
            this.mArrowColor = extractedColor;
            updateArrowColor();
        }
        animatorSet.setDuration(150);
        Objects.requireNonNull(animatorSet);
        view.post(new Runnable(animatorSet) {
            public final /* synthetic */ AnimatorSet f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                this.f$0.start();
            }
        });
    }

    /* access modifiers changed from: protected */
    public void setChildColor(View view, int i, AnimatorSet animatorSet) {
        Drawable background = view.getBackground();
        if (background instanceof GradientDrawable) {
            animatorSet.play(ObjectAnimator.ofArgb((GradientDrawable) background.mutate(), "color", new int[]{((GradientDrawable) background).getColor().getDefaultColor(), i}));
        } else if (background instanceof ColorDrawable) {
            animatorSet.play(ObjectAnimator.ofArgb((ColorDrawable) background.mutate(), "color", new int[]{((ColorDrawable) background).getColor(), i}));
        }
    }

    /* access modifiers changed from: protected */
    public void reorderAndShow(int i) {
        setupForDisplay();
        boolean z = this.mIsAboveIcon;
        if (z) {
            reverseOrder(i);
        }
        onInflationComplete(z);
        assignMarginsAndBackgrounds(this);
        if (shouldAddArrow()) {
            addArrow();
        }
        animateOpen();
    }

    public void show() {
        setupForDisplay();
        onInflationComplete(false);
        assignMarginsAndBackgrounds(this);
        if (shouldAddArrow()) {
            addArrow();
        }
        animateOpen();
    }

    /* access modifiers changed from: protected */
    public void setupForDisplay() {
        setVisibility(4);
        this.mIsOpen = true;
        getPopupContainer().addView(this);
        orientAboutObject();
    }

    private void reverseOrder(int i) {
        int childCount = getChildCount();
        ArrayList arrayList = new ArrayList(childCount);
        for (int i2 = 0; i2 < childCount; i2++) {
            if (i2 == i) {
                Collections.reverse(arrayList);
            }
            arrayList.add(getChildAt(i2));
        }
        Collections.reverse(arrayList);
        removeAllViews();
        for (int i3 = 0; i3 < childCount; i3++) {
            addView((View) arrayList.get(i3));
        }
    }

    private int getArrowLeft() {
        if (this.mIsLeftAligned) {
            return this.mArrowOffsetHorizontal;
        }
        return (getMeasuredWidth() - this.mArrowOffsetHorizontal) - this.mArrowWidth;
    }

    public void showArrow(boolean z) {
        this.mArrow.setVisibility((!z || !shouldAddArrow()) ? 4 : 0);
    }

    /* access modifiers changed from: protected */
    public void addArrow() {
        getPopupContainer().addView(this.mArrow);
        this.mArrow.setX(getX() + ((float) getArrowLeft()));
        if (Gravity.isVertical(this.mGravity)) {
            this.mArrow.setVisibility(4);
        } else {
            updateArrowColor();
        }
        this.mArrow.setPivotX(((float) this.mArrowWidth) / 2.0f);
        this.mArrow.setPivotY(this.mIsAboveIcon ? (float) this.mArrowHeight : 0.0f);
    }

    /* access modifiers changed from: protected */
    public void updateArrowColor() {
        if (!Gravity.isVertical(this.mGravity)) {
            this.mArrow.setBackground(new RoundedArrowDrawable((float) this.mArrowWidth, (float) this.mArrowHeight, (float) this.mArrowPointRadius, this.mOutlineRadius, (float) getMeasuredWidth(), (float) getMeasuredHeight(), (float) this.mArrowOffsetHorizontal, (float) (-this.mArrowOffsetVertical), !this.mIsAboveIcon, this.mIsLeftAligned, this.mArrowColor));
            setElevation(this.mElevation);
            this.mArrow.setElevation(this.mElevation);
        }
    }

    /* access modifiers changed from: protected */
    public void orientAboutObject() {
        orientAboutObject(true, true);
    }

    private void orientAboutObject(boolean z, boolean z2) {
        boolean z3 = false;
        measure(0, 0);
        int dimensionPixelSize = this.mArrowHeight + this.mArrowOffsetVertical + getResources().getDimensionPixelSize(R.dimen.popup_vertical_padding);
        int i = 0;
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            if (getChildAt(childCount).getVisibility() == 0) {
                i++;
            }
        }
        int measuredHeight = getMeasuredHeight() + dimensionPixelSize + ((i - 1) * this.mMargin);
        int measuredWidth = getMeasuredWidth() + getPaddingLeft() + getPaddingRight();
        getTargetObjectLocation(this.mTempRect);
        BaseDragLayer popupContainer = getPopupContainer();
        Rect insets = popupContainer.getInsets();
        int i2 = this.mTempRect.left;
        int i3 = this.mTempRect.right - measuredWidth;
        boolean z4 = !this.mIsRtl ? z : !z2;
        this.mIsLeftAligned = z4;
        int i4 = z4 ? i2 : i3;
        int width = this.mTempRect.width();
        int i5 = ((width / 2) - this.mArrowOffsetHorizontal) - (this.mArrowWidth / 2);
        if (!this.mIsLeftAligned) {
            i5 = -i5;
        }
        int i6 = i4 + i5;
        if (z || z2) {
            boolean z5 = (i6 + measuredWidth) + insets.left < popupContainer.getWidth() - insets.right;
            boolean z6 = i6 > insets.left;
            boolean z7 = this.mIsLeftAligned;
            if (!((z7 && z5) || (!z7 && z6))) {
                boolean z8 = z && !z7;
                if (z2 && z7) {
                    z3 = true;
                }
                orientAboutObject(z8, z3);
                return;
            }
        }
        int height = this.mTempRect.height();
        int i7 = this.mTempRect.top - measuredHeight;
        boolean z9 = i7 > popupContainer.getTop() + insets.top;
        this.mIsAboveIcon = z9;
        if (!z9) {
            i7 = this.mTempRect.top + height + dimensionPixelSize;
            measuredHeight -= dimensionPixelSize;
        }
        int i8 = i6 - insets.left;
        int i9 = i7 - insets.top;
        this.mGravity = 0;
        if (insets.top + i9 + measuredHeight > popupContainer.getBottom() - insets.bottom) {
            this.mGravity = 16;
            int i10 = (i2 + width) - insets.left;
            int i11 = (i3 - width) - insets.left;
            if (!this.mIsRtl) {
                if (measuredWidth + i10 < popupContainer.getRight()) {
                    this.mIsLeftAligned = true;
                    i8 = i10;
                    this.mIsAboveIcon = true;
                } else {
                    this.mIsLeftAligned = false;
                }
            } else if (i11 > popupContainer.getLeft()) {
                this.mIsLeftAligned = false;
            } else {
                this.mIsLeftAligned = true;
                i8 = i10;
                this.mIsAboveIcon = true;
            }
            i8 = i11;
            this.mIsAboveIcon = true;
        }
        setX((float) i8);
        if (!Gravity.isVertical(this.mGravity)) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.mArrow.getLayoutParams();
            if (this.mIsAboveIcon) {
                layoutParams.gravity = 80;
                layoutParams2.gravity = 80;
                layoutParams.bottomMargin = ((getPopupContainer().getHeight() - i9) - getMeasuredHeight()) - insets.top;
                layoutParams2.bottomMargin = ((layoutParams.bottomMargin - layoutParams2.height) - this.mArrowOffsetVertical) - insets.bottom;
                return;
            }
            layoutParams.gravity = 48;
            layoutParams2.gravity = 48;
            layoutParams.topMargin = i9 + insets.top;
            layoutParams2.topMargin = ((layoutParams.topMargin - insets.top) - layoutParams2.height) - this.mArrowOffsetVertical;
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        BaseDragLayer popupContainer = getPopupContainer();
        Rect insets = popupContainer.getInsets();
        if (getTranslationX() + ((float) i) < ((float) insets.left) || getTranslationX() + ((float) i3) > ((float) (popupContainer.getWidth() - insets.right))) {
            this.mGravity |= 1;
        }
        if (Gravity.isHorizontal(this.mGravity)) {
            setX((float) ((popupContainer.getWidth() / 2) - (getMeasuredWidth() / 2)));
            this.mArrow.setVisibility(4);
        }
        if (Gravity.isVertical(this.mGravity)) {
            setY((float) ((popupContainer.getHeight() / 2) - (getMeasuredHeight() / 2)));
        }
    }

    /* access modifiers changed from: protected */
    public Pair<View, String> getAccessibilityTarget() {
        return Pair.create(this, "");
    }

    /* access modifiers changed from: protected */
    public View getAccessibilityInitialFocusView() {
        return getChildCount() > 0 ? getChildAt(0) : this;
    }

    /* access modifiers changed from: protected */
    public void animateOpen() {
        setVisibility(0);
        AnimatorSet openCloseAnimator = getOpenCloseAnimator(true, this.OPEN_DURATION, this.OPEN_FADE_START_DELAY, this.OPEN_FADE_DURATION, this.OPEN_CHILD_FADE_START_DELAY, this.OPEN_CHILD_FADE_DURATION, Interpolators.DECELERATED_EASE);
        this.mOpenCloseAnimator = openCloseAnimator;
        onCreateOpenAnimation(openCloseAnimator);
        this.mOpenCloseAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                ArrowPopup.this.setAlpha(1.0f);
                ArrowPopup.this.announceAccessibilityChanges();
                ArrowPopup.this.mOpenCloseAnimator = null;
            }
        });
        this.mOpenCloseAnimator.start();
    }

    private AnimatorSet getOpenCloseAnimator(boolean z, int i, int i2, int i3, int i4, int i5, Interpolator interpolator) {
        Interpolator interpolator2 = interpolator;
        AnimatorSet animatorSet = new AnimatorSet();
        float[] fArr = new float[2];
        if (z) {
            // fill-array-data instruction
            fArr[0] = 0;
            fArr[1] = 1065353216;
        } else {
            // fill-array-data instruction
            fArr[0] = 1065353216;
            fArr[1] = 0;
        }
        float[] fArr2 = new float[2];
        if (z) {
            // fill-array-data instruction
            fArr2[0] = 1056964608;
            fArr2[1] = 1065353216;
        } else {
            // fill-array-data instruction
            fArr2[0] = 1065353216;
            fArr2[1] = 1056964608;
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
        ofFloat.setStartDelay((long) i2);
        ofFloat.setDuration((long) i3);
        ofFloat.setInterpolator(Interpolators.LINEAR);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ArrowPopup.this.lambda$getOpenCloseAnimator$2$ArrowPopup(valueAnimator);
            }
        });
        animatorSet.play(ofFloat);
        float f = 0.0f;
        setPivotX(this.mIsLeftAligned ? 0.0f : (float) getMeasuredWidth());
        if (this.mIsAboveIcon) {
            f = (float) getMeasuredHeight();
        }
        setPivotY(f);
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this, View.SCALE_Y, fArr2);
        long j = (long) i;
        ofFloat2.setDuration(j);
        ofFloat2.setInterpolator(interpolator2);
        animatorSet.play(ofFloat2);
        if (this.shouldScaleArrow) {
            ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(this.mArrow, View.SCALE_Y, fArr2);
            ofFloat3.setDuration(j);
            ofFloat3.setInterpolator(interpolator2);
            animatorSet.play(ofFloat3);
        }
        fadeInChildViews(this, fArr, (long) i4, (long) i5, animatorSet);
        return animatorSet;
    }

    public /* synthetic */ void lambda$getOpenCloseAnimator$2$ArrowPopup(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.mArrow.setAlpha(floatValue);
        setAlpha(floatValue);
    }

    private void fadeInChildViews(ViewGroup viewGroup, float[] fArr, long j, long j2, AnimatorSet animatorSet) {
        float[] fArr2 = fArr;
        for (int childCount = viewGroup.getChildCount() - 1; childCount >= 0; childCount--) {
            ViewGroup viewGroup2 = viewGroup;
            View childAt = viewGroup.getChildAt(childCount);
            if (childAt.getVisibility() == 0 && (childAt instanceof ViewGroup)) {
                if (this.mIterateChildrenTag.equals(childAt.getTag())) {
                    fadeInChildViews((ViewGroup) childAt, fArr, j, j2, animatorSet);
                } else {
                    ViewGroup viewGroup3 = (ViewGroup) childAt;
                    for (int childCount2 = viewGroup3.getChildCount() - 1; childCount2 >= 0; childCount2--) {
                        View childAt2 = viewGroup3.getChildAt(childCount2);
                        childAt2.setAlpha(fArr2[0]);
                        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(childAt2, ALPHA, fArr);
                        ofFloat.setStartDelay(j);
                        ofFloat.setDuration(j2);
                        ofFloat.setInterpolator(Interpolators.LINEAR);
                        animatorSet.play(ofFloat);
                    }
                }
            }
            long j3 = j;
            long j4 = j2;
            AnimatorSet animatorSet2 = animatorSet;
        }
    }

    /* access modifiers changed from: protected */
    public void animateClose() {
        if (this.mIsOpen) {
            AnimatorSet animatorSet = this.mOpenCloseAnimator;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.mIsOpen = false;
            AnimatorSet openCloseAnimator = getOpenCloseAnimator(false, this.CLOSE_DURATION, this.CLOSE_FADE_START_DELAY, this.CLOSE_FADE_DURATION, this.CLOSE_CHILD_FADE_START_DELAY, this.CLOSE_CHILD_FADE_DURATION, Interpolators.ACCELERATED_EASE);
            this.mOpenCloseAnimator = openCloseAnimator;
            onCreateCloseAnimation(openCloseAnimator);
            this.mOpenCloseAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    ArrowPopup.this.mOpenCloseAnimator = null;
                    if (ArrowPopup.this.mDeferContainerRemoval) {
                        ArrowPopup.this.setVisibility(4);
                    } else {
                        ArrowPopup.this.closeComplete();
                    }
                }
            });
            this.mOpenCloseAnimator.start();
        }
    }

    /* access modifiers changed from: protected */
    public void closeComplete() {
        AnimatorSet animatorSet = this.mOpenCloseAnimator;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.mOpenCloseAnimator = null;
        }
        this.mIsOpen = false;
        this.mDeferContainerRemoval = false;
        getPopupContainer().removeView(this);
        getPopupContainer().removeView(this.mArrow);
        Runnable runnable = this.mOnCloseCallback;
        if (runnable != null) {
            runnable.run();
        }
        List<LocalColorExtractor> list = this.mColorExtractors;
        if (list != null) {
            list.forEach($$Lambda$ArrowPopup$SIWsLZvmhqEJ5OZy5kpvgEwyIQk.INSTANCE);
        }
    }

    public void setOnCloseCallback(Runnable runnable) {
        this.mOnCloseCallback = runnable;
    }

    /* access modifiers changed from: protected */
    public BaseDragLayer getPopupContainer() {
        return ((ActivityContext) this.mActivityContext).getDragLayer();
    }
}
