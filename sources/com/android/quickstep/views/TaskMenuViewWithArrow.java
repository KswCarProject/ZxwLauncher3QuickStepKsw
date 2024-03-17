package com.android.quickstep.views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.InsettableFrameLayout;
import com.android.launcher3.R;
import com.android.launcher3.popup.ArrowPopup;
import com.android.launcher3.popup.RoundedArrowDrawable;
import com.android.launcher3.popup.SystemShortcut;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.launcher3.util.Themes;
import com.android.launcher3.views.BaseDragLayer;
import com.android.quickstep.KtR;
import com.android.quickstep.TaskOverlayFactory;
import com.android.quickstep.views.TaskView;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000\u0001\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\u0018\u0000 A*\b\b\u0000\u0010\u0001*\u00020\u00022\b\u0012\u0004\u0012\u0002H\u00010\u0003:\u0001AB\u000f\b\u0016\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006B\u0017\b\u0016\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0002\u0010\tB\u001f\b\u0016\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\n\u001a\u00020\u000b¢\u0006\u0002\u0010\fJ\b\u0010\"\u001a\u00020#H\u0014J\u0014\u0010$\u001a\u00020#2\n\u0010%\u001a\u0006\u0012\u0002\b\u00030&H\u0002J\b\u0010'\u001a\u00020#H\u0002J\b\u0010(\u001a\u00020#H\u0002J\u0010\u0010)\u001a\u00020#2\u0006\u0010*\u001a\u00020+H\u0016J\b\u0010,\u001a\u00020#H\u0014J\u0010\u0010-\u001a\u00020#2\u0006\u0010.\u001a\u00020/H\u0002J\b\u00100\u001a\u00020\u001dH\u0002J\u0012\u00101\u001a\u00020#2\b\u00102\u001a\u0004\u0018\u00010/H\u0014J\u0010\u00103\u001a\u00020\u000e2\u0006\u00104\u001a\u00020\u000bH\u0014J\u0012\u00105\u001a\u00020\u000e2\b\u00106\u001a\u0004\u0018\u000107H\u0016J\u0010\u00108\u001a\u00020#2\u0006\u00109\u001a\u00020:H\u0014J\u0010\u0010;\u001a\u00020#2\u0006\u00109\u001a\u00020:H\u0014J\b\u0010<\u001a\u00020#H\u0014J\b\u0010=\u001a\u00020#H\u0014J\u001c\u0010>\u001a\u00020\u000e2\n\u0010\u001e\u001a\u00060\u001fR\u00020 2\u0006\u0010\r\u001a\u00020\u000eH\u0002J\b\u0010?\u001a\u00020\u000eH\u0002J\b\u0010@\u001a\u00020#H\u0014R\u000e\u0010\r\u001a\u00020\u000eX\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u000f\u001a\u00020\u000b8BX\u0004¢\u0006\u0006\u001a\u0004\b\u0010\u0010\u0011R\u0014\u0010\u0012\u001a\u00020\u000b8BX\u0004¢\u0006\u0006\u001a\u0004\b\u0013\u0010\u0011R\u0010\u0010\u0014\u001a\u0004\u0018\u00010\u0015X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u000bX\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0018X.¢\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u000bX\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u001bX\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\u001dXD¢\u0006\u0002\n\u0000R\u0012\u0010\u001e\u001a\u00060\u001fR\u00020 X.¢\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020 X.¢\u0006\u0002\n\u0000¨\u0006B"}, d2 = {"Lcom/android/quickstep/views/TaskMenuViewWithArrow;", "T", "Lcom/android/launcher3/BaseDraggingActivity;", "Lcom/android/launcher3/popup/ArrowPopup;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "attrs", "Landroid/util/AttributeSet;", "(Landroid/content/Context;Landroid/util/AttributeSet;)V", "defStyleAttr", "", "(Landroid/content/Context;Landroid/util/AttributeSet;I)V", "alignSecondRow", "", "arrowHorizontalPadding", "getArrowHorizontalPadding", "()I", "extraSpaceForSecondRowAlignment", "getExtraSpaceForSecondRowAlignment", "iconView", "Lcom/android/quickstep/views/IconView;", "menuWidth", "optionLayout", "Landroid/widget/LinearLayout;", "optionMeasuredHeight", "scrim", "Landroid/view/View;", "scrimAlpha", "", "taskContainer", "Lcom/android/quickstep/views/TaskView$TaskIdAttributeContainer;", "Lcom/android/quickstep/views/TaskView;", "taskView", "addArrow", "", "addMenuOption", "menuOption", "Lcom/android/launcher3/popup/SystemShortcut;", "addMenuOptions", "addScrim", "assignMarginsAndBackgrounds", "viewGroup", "Landroid/view/ViewGroup;", "closeComplete", "copyIconToDragLayer", "insets", "Landroid/graphics/Rect;", "getArrowX", "getTargetObjectLocation", "outPos", "isOfType", "type", "onControllerInterceptTouchEvent", "ev", "Landroid/view/MotionEvent;", "onCreateCloseAnimation", "anim", "Landroid/animation/AnimatorSet;", "onCreateOpenAnimation", "onFinishInflate", "orientAboutObject", "populateAndShowForTask", "populateMenu", "updateArrowColor", "Companion", "Launcher3_Android13_aospWithQuickstepRelease"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: TaskMenuViewWithArrow.kt */
public final class TaskMenuViewWithArrow<T extends BaseDraggingActivity> extends ArrowPopup<T> {
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    public static final String TAG = "TaskMenuViewWithArrow";
    public Map<Integer, View> _$_findViewCache = new LinkedHashMap();
    private boolean alignSecondRow;
    private IconView iconView;
    private final int menuWidth = getContext().getResources().getDimensionPixelSize(R.dimen.task_menu_width_grid);
    private LinearLayout optionLayout;
    private int optionMeasuredHeight;
    private View scrim;
    private final float scrimAlpha = 0.8f;
    private TaskView.TaskIdAttributeContainer taskContainer;
    private TaskView taskView;

    public void _$_clearFindViewByIdCache() {
        this._$_findViewCache.clear();
    }

    public View _$_findCachedViewById(int i) {
        Map<Integer, View> map = this._$_findViewCache;
        View view = map.get(Integer.valueOf(i));
        if (view != null) {
            return view;
        }
        View findViewById = findViewById(i);
        if (findViewById == null) {
            return null;
        }
        map.put(Integer.valueOf(i), findViewById);
        return findViewById;
    }

    /* access modifiers changed from: protected */
    public boolean isOfType(int i) {
        return (i & 2048) != 0;
    }

    @Metadata(d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u001c\u0010\u0005\u001a\u00020\u00062\n\u0010\u0007\u001a\u00060\bR\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u0006R\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000¨\u0006\u000b"}, d2 = {"Lcom/android/quickstep/views/TaskMenuViewWithArrow$Companion;", "", "()V", "TAG", "", "showForTask", "", "taskContainer", "Lcom/android/quickstep/views/TaskView$TaskIdAttributeContainer;", "Lcom/android/quickstep/views/TaskView;", "alignSecondRow", "Launcher3_Android13_aospWithQuickstepRelease"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: TaskMenuViewWithArrow.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public static /* synthetic */ boolean showForTask$default(Companion companion, TaskView.TaskIdAttributeContainer taskIdAttributeContainer, boolean z, int i, Object obj) {
            if ((i & 2) != 0) {
                z = false;
            }
            return companion.showForTask(taskIdAttributeContainer, z);
        }

        public final boolean showForTask(TaskView.TaskIdAttributeContainer taskIdAttributeContainer, boolean z) {
            Intrinsics.checkNotNullParameter(taskIdAttributeContainer, "taskContainer");
            BaseDraggingActivity baseDraggingActivity = (BaseDraggingActivity) BaseDraggingActivity.fromContext(taskIdAttributeContainer.getTaskView().getContext());
            View inflate = baseDraggingActivity.getLayoutInflater().inflate(KtR.layout.task_menu_with_arrow, baseDraggingActivity.getDragLayer(), false);
            Objects.requireNonNull(inflate, "null cannot be cast to non-null type com.android.quickstep.views.TaskMenuViewWithArrow<*>");
            return ((TaskMenuViewWithArrow) inflate).populateAndShowForTask(taskIdAttributeContainer, z);
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public TaskMenuViewWithArrow(Context context) {
        super(context);
        Intrinsics.checkNotNullParameter(context, "context");
        setClipToOutline(true);
        this.shouldScaleArrow = true;
        this.OPEN_CHILD_FADE_START_DELAY = this.OPEN_FADE_START_DELAY;
        this.OPEN_CHILD_FADE_DURATION = this.OPEN_FADE_DURATION;
        this.CLOSE_FADE_START_DELAY = this.CLOSE_CHILD_FADE_START_DELAY;
        this.CLOSE_FADE_DURATION = this.CLOSE_CHILD_FADE_DURATION;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public TaskMenuViewWithArrow(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(attributeSet, "attrs");
        setClipToOutline(true);
        this.shouldScaleArrow = true;
        this.OPEN_CHILD_FADE_START_DELAY = this.OPEN_FADE_START_DELAY;
        this.OPEN_CHILD_FADE_DURATION = this.OPEN_FADE_DURATION;
        this.CLOSE_FADE_START_DELAY = this.CLOSE_CHILD_FADE_START_DELAY;
        this.CLOSE_FADE_DURATION = this.CLOSE_CHILD_FADE_DURATION;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public TaskMenuViewWithArrow(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(attributeSet, "attrs");
        setClipToOutline(true);
        this.shouldScaleArrow = true;
        this.OPEN_CHILD_FADE_START_DELAY = this.OPEN_FADE_START_DELAY;
        this.OPEN_CHILD_FADE_DURATION = this.OPEN_FADE_DURATION;
        this.CLOSE_FADE_START_DELAY = this.CLOSE_CHILD_FADE_START_DELAY;
        this.CLOSE_FADE_DURATION = this.CLOSE_CHILD_FADE_DURATION;
    }

    private final int getExtraSpaceForSecondRowAlignment() {
        if (this.alignSecondRow) {
            return this.optionMeasuredHeight;
        }
        return 0;
    }

    private final int getArrowHorizontalPadding() {
        TaskView taskView2 = this.taskView;
        if (taskView2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("taskView");
            taskView2 = null;
        }
        if (taskView2.isFocusedTask()) {
            return getResources().getDimensionPixelSize(KtR.dimen.task_menu_horizontal_padding);
        }
        return 0;
    }

    /* access modifiers changed from: protected */
    public void getTargetObjectLocation(Rect rect) {
        BaseDragLayer popupContainer = getPopupContainer();
        TaskView.TaskIdAttributeContainer taskIdAttributeContainer = this.taskContainer;
        if (taskIdAttributeContainer == null) {
            Intrinsics.throwUninitializedPropertyAccessException("taskContainer");
            taskIdAttributeContainer = null;
        }
        popupContainer.getDescendantRectRelativeToSelf(taskIdAttributeContainer.getIconView(), rect);
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        if (!(motionEvent != null && motionEvent.getAction() == 0) || getPopupContainer().isEventOverView(this, motionEvent)) {
            return false;
        }
        close(true);
        return true;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        View findViewById = findViewById(KtR.id.menu_option_layout);
        Intrinsics.checkNotNullExpressionValue(findViewById, "findViewById(KtR.id.menu_option_layout)");
        this.optionLayout = (LinearLayout) findViewById;
    }

    /* access modifiers changed from: private */
    public final boolean populateAndShowForTask(TaskView.TaskIdAttributeContainer taskIdAttributeContainer, boolean z) {
        if (isAttachedToWindow()) {
            return false;
        }
        TaskView taskView2 = taskIdAttributeContainer.getTaskView();
        Intrinsics.checkNotNullExpressionValue(taskView2, "taskContainer.taskView");
        this.taskView = taskView2;
        this.taskContainer = taskIdAttributeContainer;
        this.alignSecondRow = z;
        if (!populateMenu()) {
            return false;
        }
        addScrim();
        show();
        return true;
    }

    private final void addScrim() {
        View view = new View(getContext());
        view.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        view.setBackgroundColor(Themes.getAttrColor(view.getContext(), R.attr.overviewScrimColor));
        view.setAlpha(0.0f);
        this.scrim = view;
        getPopupContainer().addView(this.scrim);
    }

    private final boolean populateMenu() {
        TaskView.TaskIdAttributeContainer taskIdAttributeContainer = this.taskContainer;
        if (taskIdAttributeContainer == null) {
            Intrinsics.throwUninitializedPropertyAccessException("taskContainer");
            taskIdAttributeContainer = null;
        }
        if (taskIdAttributeContainer.getTask().icon == null) {
            return false;
        }
        addMenuOptions();
        return true;
    }

    private final void addMenuOptions() {
        TaskView taskView2 = this.taskView;
        LinearLayout linearLayout = null;
        if (taskView2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("taskView");
            taskView2 = null;
        }
        DeviceProfile deviceProfile = ((BaseDraggingActivity) this.mActivityContext).getDeviceProfile();
        TaskView.TaskIdAttributeContainer taskIdAttributeContainer = this.taskContainer;
        if (taskIdAttributeContainer == null) {
            Intrinsics.throwUninitializedPropertyAccessException("taskContainer");
            taskIdAttributeContainer = null;
        }
        List<SystemShortcut> enabledShortcuts = TaskOverlayFactory.getEnabledShortcuts(taskView2, deviceProfile, taskIdAttributeContainer);
        Intrinsics.checkNotNullExpressionValue(enabledShortcuts, "getEnabledShortcuts(task…ceProfile, taskContainer)");
        for (SystemShortcut systemShortcut : enabledShortcuts) {
            Intrinsics.checkNotNullExpressionValue(systemShortcut, "it");
            addMenuOption(systemShortcut);
        }
        ShapeDrawable shapeDrawable = new ShapeDrawable(new RectShape());
        shapeDrawable.getPaint().setColor(getResources().getColor(17170445));
        int dimension = (int) getResources().getDimension(KtR.dimen.task_menu_spacing);
        LinearLayout linearLayout2 = this.optionLayout;
        if (linearLayout2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("optionLayout");
            linearLayout2 = null;
        }
        linearLayout2.setShowDividers(2);
        View overviewPanel = ((BaseDraggingActivity) this.mActivityContext).getOverviewPanel();
        Intrinsics.checkNotNullExpressionValue(overviewPanel, "mActivityContext.getOverviewPanel()");
        PagedOrientationHandler pagedOrientationHandler = ((RecentsView) overviewPanel).getPagedOrientationHandler();
        DeviceProfile deviceProfile2 = ((BaseDraggingActivity) this.mActivityContext).getDeviceProfile();
        Intrinsics.checkNotNullExpressionValue(deviceProfile2, "mActivityContext.deviceProfile");
        LinearLayout linearLayout3 = this.optionLayout;
        if (linearLayout3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("optionLayout");
        } else {
            linearLayout = linearLayout3;
        }
        pagedOrientationHandler.setTaskOptionsMenuLayoutOrientation(deviceProfile2, linearLayout, dimension, shapeDrawable);
    }

    private final void addMenuOption(SystemShortcut<?> systemShortcut) {
        View inflate = ((BaseDraggingActivity) this.mActivityContext).getLayoutInflater().inflate(KtR.layout.task_view_menu_option, this, false);
        Objects.requireNonNull(inflate, "null cannot be cast to non-null type android.widget.LinearLayout");
        LinearLayout linearLayout = (LinearLayout) inflate;
        systemShortcut.setIconAndLabelFor(linearLayout.findViewById(R.id.icon), (TextView) linearLayout.findViewById(R.id.text));
        ViewGroup.LayoutParams layoutParams = linearLayout.getLayoutParams();
        Objects.requireNonNull(layoutParams, "null cannot be cast to non-null type android.widget.LinearLayout.LayoutParams");
        ((LinearLayout.LayoutParams) layoutParams).width = this.menuWidth;
        linearLayout.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                TaskMenuViewWithArrow.m98addMenuOption$lambda2(SystemShortcut.this, view);
            }
        });
        LinearLayout linearLayout2 = this.optionLayout;
        if (linearLayout2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("optionLayout");
            linearLayout2 = null;
        }
        linearLayout2.addView(linearLayout);
    }

    /* access modifiers changed from: private */
    /* renamed from: addMenuOption$lambda-2  reason: not valid java name */
    public static final void m98addMenuOption$lambda2(SystemShortcut systemShortcut, View view) {
        Intrinsics.checkNotNullParameter(systemShortcut, "$menuOption");
        systemShortcut.onClick(view);
    }

    public void assignMarginsAndBackgrounds(ViewGroup viewGroup) {
        Intrinsics.checkNotNullParameter(viewGroup, "viewGroup");
        assignMarginsAndBackgrounds(this, Themes.getAttrColor(getContext(), 17956909));
    }

    /* access modifiers changed from: protected */
    public void onCreateOpenAnimation(AnimatorSet animatorSet) {
        Intrinsics.checkNotNullParameter(animatorSet, "anim");
        View view = this.scrim;
        if (view != null) {
            animatorSet.play(ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{0.0f, this.scrimAlpha}).setDuration((long) this.OPEN_DURATION));
        }
    }

    /* access modifiers changed from: protected */
    public void onCreateCloseAnimation(AnimatorSet animatorSet) {
        Intrinsics.checkNotNullParameter(animatorSet, "anim");
        View view = this.scrim;
        if (view != null) {
            animatorSet.play(ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{this.scrimAlpha, 0.0f}).setDuration((long) this.CLOSE_DURATION));
        }
    }

    /* access modifiers changed from: protected */
    public void closeComplete() {
        super.closeComplete();
        getPopupContainer().removeView(this.scrim);
        getPopupContainer().removeView(this.iconView);
    }

    private final void copyIconToDragLayer(Rect rect) {
        IconView iconView2 = new IconView(getContext());
        TaskView.TaskIdAttributeContainer taskIdAttributeContainer = this.taskContainer;
        TaskView.TaskIdAttributeContainer taskIdAttributeContainer2 = null;
        if (taskIdAttributeContainer == null) {
            Intrinsics.throwUninitializedPropertyAccessException("taskContainer");
            taskIdAttributeContainer = null;
        }
        int width = taskIdAttributeContainer.getIconView().getWidth();
        TaskView.TaskIdAttributeContainer taskIdAttributeContainer3 = this.taskContainer;
        if (taskIdAttributeContainer3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("taskContainer");
            taskIdAttributeContainer3 = null;
        }
        iconView2.setLayoutParams(new FrameLayout.LayoutParams(width, taskIdAttributeContainer3.getIconView().getHeight()));
        iconView2.setX(((float) this.mTempRect.left) - ((float) rect.left));
        iconView2.setY(((float) this.mTempRect.top) - ((float) rect.top));
        TaskView.TaskIdAttributeContainer taskIdAttributeContainer4 = this.taskContainer;
        if (taskIdAttributeContainer4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("taskContainer");
            taskIdAttributeContainer4 = null;
        }
        iconView2.setDrawable(taskIdAttributeContainer4.getIconView().getDrawable());
        TaskView.TaskIdAttributeContainer taskIdAttributeContainer5 = this.taskContainer;
        if (taskIdAttributeContainer5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("taskContainer");
            taskIdAttributeContainer5 = null;
        }
        int drawableWidth = taskIdAttributeContainer5.getIconView().getDrawableWidth();
        TaskView.TaskIdAttributeContainer taskIdAttributeContainer6 = this.taskContainer;
        if (taskIdAttributeContainer6 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("taskContainer");
        } else {
            taskIdAttributeContainer2 = taskIdAttributeContainer6;
        }
        iconView2.setDrawableSize(drawableWidth, taskIdAttributeContainer2.getIconView().getDrawableHeight());
        this.iconView = iconView2;
        getPopupContainer().addView(this.iconView);
    }

    /* access modifiers changed from: protected */
    public void orientAboutObject() {
        boolean z = false;
        measure(0, 0);
        LinearLayout linearLayout = this.optionLayout;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("optionLayout");
            linearLayout = null;
        }
        this.optionMeasuredHeight = linearLayout.getChildAt(0).getMeasuredHeight();
        int arrowHorizontalPadding = this.mArrowHeight + this.mArrowOffsetVertical + getArrowHorizontalPadding();
        int measuredWidth = getMeasuredWidth() + getPaddingLeft() + getPaddingRight() + arrowHorizontalPadding;
        getTargetObjectLocation(this.mTempRect);
        BaseDragLayer popupContainer = getPopupContainer();
        Intrinsics.checkNotNullExpressionValue(popupContainer, "popupContainer");
        InsettableFrameLayout insettableFrameLayout = popupContainer;
        Rect insets = insettableFrameLayout.getInsets();
        Intrinsics.checkNotNullExpressionValue(insets, "insets");
        copyIconToDragLayer(insets);
        int i = this.mTempRect.left - measuredWidth;
        int i2 = this.mTempRect.right + arrowHorizontalPadding;
        if (!this.mIsRtl ? (measuredWidth - arrowHorizontalPadding) + i2 + insets.left < insettableFrameLayout.getWidth() - insets.right : insets.left + i < 0) {
            z = true;
        }
        this.mIsLeftAligned = z;
        if (this.mIsLeftAligned) {
            i = i2;
        }
        int height = (this.mTempRect.top - ((this.optionMeasuredHeight - this.mTempRect.height()) / 2)) - getExtraSpaceForSecondRowAlignment();
        int i3 = i - insets.left;
        int i4 = height - insets.top;
        setX((float) i3);
        setY((float) i4);
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        Objects.requireNonNull(layoutParams, "null cannot be cast to non-null type android.widget.FrameLayout.LayoutParams");
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) layoutParams;
        ViewGroup.LayoutParams layoutParams3 = this.mArrow.getLayoutParams();
        Objects.requireNonNull(layoutParams3, "null cannot be cast to non-null type android.widget.FrameLayout.LayoutParams");
        layoutParams2.gravity = 48;
        ((FrameLayout.LayoutParams) layoutParams3).gravity = layoutParams2.gravity;
    }

    /* access modifiers changed from: protected */
    public void addArrow() {
        getPopupContainer().addView(this.mArrow);
        this.mArrow.setX(getArrowX());
        this.mArrow.setY(((getY() + ((float) (this.optionMeasuredHeight / 2))) - ((float) (this.mArrowHeight / 2))) + ((float) getExtraSpaceForSecondRowAlignment()));
        updateArrowColor();
        this.mArrow.setPivotX(this.mIsLeftAligned ? 0.0f : (float) this.mArrowHeight);
        this.mArrow.setPivotY(0.0f);
    }

    private final float getArrowX() {
        if (this.mIsLeftAligned) {
            return getX() - ((float) this.mArrowHeight);
        }
        return getX() + ((float) getMeasuredWidth()) + ((float) this.mArrowOffsetVertical);
    }

    /* access modifiers changed from: protected */
    public void updateArrowColor() {
        this.mArrow.setBackground(new RoundedArrowDrawable((float) this.mArrowWidth, (float) this.mArrowHeight, (float) this.mArrowPointRadius, this.mIsLeftAligned, this.mArrowColor));
        setElevation(this.mElevation);
        this.mArrow.setElevation(this.mElevation);
    }
}
