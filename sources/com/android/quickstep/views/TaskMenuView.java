package com.android.quickstep.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Outline;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.R;
import com.android.launcher3.anim.AnimationSuccessListener;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.RoundedRectRevealOutlineProvider;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.popup.SystemShortcut;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.launcher3.views.BaseDragLayer;
import com.android.quickstep.TaskOverlayFactory;
import com.android.quickstep.TaskUtils;
import com.android.quickstep.util.TaskCornerRadius;
import com.android.quickstep.views.TaskView;
import com.android.systemui.shared.recents.model.ThumbnailData;
import java.util.HashMap;
import java.util.function.Consumer;

public class TaskMenuView extends AbstractFloatingView implements ViewTreeObserver.OnScrollChangedListener {
    private static final int REVEAL_CLOSE_DURATION = 100;
    private static final int REVEAL_OPEN_DURATION = 150;
    private static final Rect sTempRect = new Rect();
    private BaseDraggingActivity mActivity;
    private AnimatorSet mOpenCloseAnimator;
    private LinearLayout mOptionLayout;
    private TaskView.TaskIdAttributeContainer mTaskContainer;
    private final float mTaskInsetMargin;
    private TextView mTaskName;
    private TaskView mTaskView;

    /* access modifiers changed from: protected */
    public boolean isOfType(int i) {
        return (i & 2048) != 0;
    }

    public TaskMenuView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TaskMenuView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mActivity = (BaseDraggingActivity) BaseDraggingActivity.fromContext(context);
        setClipToOutline(true);
        this.mTaskInsetMargin = getResources().getDimension(R.dimen.task_card_margin);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mTaskName = (TextView) findViewById(R.id.task_name);
        this.mOptionLayout = (LinearLayout) findViewById(R.id.menu_option_layout);
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() != 0 || this.mActivity.getDragLayer().isEventOverView(this, motionEvent)) {
            return false;
        }
        close(true);
        return true;
    }

    /* access modifiers changed from: protected */
    public void handleClose(boolean z) {
        if (z) {
            animateClose();
        } else {
            closeComplete();
        }
    }

    public ViewOutlineProvider getOutlineProvider() {
        return new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), TaskCornerRadius.get(view.getContext()));
            }
        };
    }

    private void setPosition(float f, float f2, int i) {
        PagedOrientationHandler pagedOrientationHandler = this.mTaskView.getPagedOrientationHandler();
        PointF additionalInsetForTaskMenu = pagedOrientationHandler.getAdditionalInsetForTaskMenu(this.mTaskInsetMargin);
        DeviceProfile deviceProfile = this.mActivity.getDeviceProfile();
        int i2 = deviceProfile.overviewTaskThumbnailTopMarginPx;
        float f3 = (f2 + ((float) i2)) - additionalInsetForTaskMenu.y;
        float f4 = f - additionalInsetForTaskMenu.x;
        setPivotX(0.0f);
        if (deviceProfile.isTablet) {
            setPivotY((float) (-i2));
        } else {
            setPivotY(0.0f);
        }
        setRotation(pagedOrientationHandler.getDegreesRotated());
        setX(pagedOrientationHandler.getTaskMenuX(f4, this.mTaskContainer.getThumbnailView(), i, deviceProfile));
        setY(pagedOrientationHandler.getTaskMenuY(f3, this.mTaskContainer.getThumbnailView(), i));
        if (this.mTaskView.getTaskIdAttributeContainers()[0].getStagePosition() != -1 && this.mTaskContainer.getStagePosition() == 1) {
            Rect rect = new Rect();
            this.mTaskContainer.getThumbnailView().getBoundsOnScreen(rect);
            if (deviceProfile.isLandscape) {
                setX((float) rect.left);
            } else {
                setY((float) rect.top);
            }
        }
    }

    public void onRotationChanged() {
        AnimatorSet animatorSet = this.mOpenCloseAnimator;
        if (animatorSet != null && animatorSet.isRunning()) {
            this.mOpenCloseAnimator.end();
        }
        if (this.mIsOpen) {
            this.mOptionLayout.removeAllViews();
            if (!populateAndLayoutMenu()) {
                close(false);
            }
        }
    }

    public static boolean showForTask(TaskView.TaskIdAttributeContainer taskIdAttributeContainer) {
        BaseDraggingActivity baseDraggingActivity = (BaseDraggingActivity) BaseDraggingActivity.fromContext(taskIdAttributeContainer.getTaskView().getContext());
        return ((TaskMenuView) baseDraggingActivity.getLayoutInflater().inflate(R.layout.task_menu, baseDraggingActivity.getDragLayer(), false)).populateAndShowForTask(taskIdAttributeContainer);
    }

    private boolean populateAndShowForTask(TaskView.TaskIdAttributeContainer taskIdAttributeContainer) {
        if (isAttachedToWindow()) {
            return false;
        }
        this.mActivity.getDragLayer().addView(this);
        this.mTaskView = taskIdAttributeContainer.getTaskView();
        this.mTaskContainer = taskIdAttributeContainer;
        if (!populateAndLayoutMenu()) {
            return false;
        }
        post(new Runnable() {
            public final void run() {
                TaskMenuView.this.animateOpen();
            }
        });
        ((RecentsView) this.mActivity.getOverviewPanel()).addOnScrollChangedListener(this);
        return true;
    }

    public void onScrollChanged() {
        RecentsView recentsView = (RecentsView) this.mActivity.getOverviewPanel();
        setPosition(this.mTaskView.getX() - ((float) recentsView.getScrollX()), this.mTaskView.getY() - ((float) recentsView.getScrollY()), recentsView.getOverScrollShift());
    }

    private boolean populateAndLayoutMenu() {
        if (this.mTaskContainer.getTask().icon == null) {
            return false;
        }
        addMenuOptions(this.mTaskContainer);
        orientAroundTaskView(this.mTaskContainer);
        return true;
    }

    private void addMenuOptions(TaskView.TaskIdAttributeContainer taskIdAttributeContainer) {
        this.mTaskName.setText(TaskUtils.getTitle(getContext(), taskIdAttributeContainer.getTask()));
        this.mTaskName.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                TaskMenuView.this.lambda$addMenuOptions$0$TaskMenuView(view);
            }
        });
        TaskOverlayFactory.getEnabledShortcuts(this.mTaskView, this.mActivity.getDeviceProfile(), taskIdAttributeContainer).forEach(new Consumer() {
            public final void accept(Object obj) {
                TaskMenuView.this.addMenuOption((SystemShortcut) obj);
            }
        });
    }

    public /* synthetic */ void lambda$addMenuOptions$0$TaskMenuView(View view) {
        close(true);
    }

    /* access modifiers changed from: private */
    public void addMenuOption(SystemShortcut systemShortcut) {
        LinearLayout linearLayout = (LinearLayout) this.mActivity.getLayoutInflater().inflate(R.layout.task_view_menu_option, this, false);
        systemShortcut.setIconAndLabelFor(linearLayout.findViewById(R.id.icon), (TextView) linearLayout.findViewById(R.id.text));
        this.mTaskView.getPagedOrientationHandler().setLayoutParamsForTaskMenuOptionItem((LinearLayout.LayoutParams) linearLayout.getLayoutParams(), linearLayout, this.mActivity.getDeviceProfile());
        linearLayout.setOnClickListener(new View.OnClickListener(systemShortcut) {
            public final /* synthetic */ SystemShortcut f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                TaskMenuView.this.lambda$addMenuOption$3$TaskMenuView(this.f$1, view);
            }
        });
        this.mOptionLayout.addView(linearLayout);
    }

    public /* synthetic */ void lambda$addMenuOption$3$TaskMenuView(SystemShortcut systemShortcut, View view) {
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
            RecentsView recentsView = this.mTaskView.getRecentsView();
            recentsView.switchToScreenshot((HashMap<Integer, ThumbnailData>) null, new Runnable(systemShortcut, view) {
                public final /* synthetic */ SystemShortcut f$1;
                public final /* synthetic */ View f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    RecentsView.this.finishRecentsAnimation(true, false, new Runnable(this.f$2) {
                        public final /* synthetic */ View f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            SystemShortcut.this.onClick(this.f$1);
                        }
                    });
                }
            });
            return;
        }
        systemShortcut.onClick(view);
    }

    private void orientAroundTaskView(TaskView.TaskIdAttributeContainer taskIdAttributeContainer) {
        PagedOrientationHandler pagedOrientationHandler = ((RecentsView) this.mActivity.getOverviewPanel()).getPagedOrientationHandler();
        measure(0, 0);
        pagedOrientationHandler.setTaskMenuAroundTaskView(this, this.mTaskInsetMargin);
        DeviceProfile deviceProfile = this.mActivity.getDeviceProfile();
        BaseDragLayer dragLayer = this.mActivity.getDragLayer();
        TaskView taskView = this.mTaskView;
        Rect rect = sTempRect;
        dragLayer.getDescendantRectRelativeToSelf(taskView, rect);
        Rect insets = this.mActivity.getDragLayer().getInsets();
        BaseDragLayer.LayoutParams layoutParams = (BaseDragLayer.LayoutParams) getLayoutParams();
        layoutParams.width = pagedOrientationHandler.getTaskMenuWidth(taskIdAttributeContainer.getThumbnailView(), deviceProfile) - (getResources().getDimensionPixelSize(R.dimen.task_menu_vertical_padding) * 2);
        layoutParams.gravity = 3;
        setLayoutParams(layoutParams);
        setScaleX(this.mTaskView.getScaleX());
        setScaleY(this.mTaskView.getScaleY());
        ShapeDrawable shapeDrawable = new ShapeDrawable(new RectShape());
        shapeDrawable.getPaint().setColor(getResources().getColor(17170445));
        this.mOptionLayout.setShowDividers(2);
        pagedOrientationHandler.setTaskOptionsMenuLayoutOrientation(deviceProfile, this.mOptionLayout, (int) getResources().getDimension(R.dimen.task_menu_spacing), shapeDrawable);
        setPosition((float) (rect.left - insets.left), (float) (rect.top - insets.top), 0);
    }

    /* access modifiers changed from: private */
    public void animateOpen() {
        animateOpenOrClosed(false);
        this.mIsOpen = true;
    }

    private void animateClose() {
        animateOpenOrClosed(true);
    }

    private void animateOpenOrClosed(final boolean z) {
        AnimatorSet animatorSet = this.mOpenCloseAnimator;
        if (animatorSet != null && animatorSet.isRunning()) {
            this.mOpenCloseAnimator.end();
        }
        this.mOpenCloseAnimator = new AnimatorSet();
        ValueAnimator createRevealAnimator = createOpenCloseOutlineProvider().createRevealAnimator(this, z);
        createRevealAnimator.setInterpolator(Interpolators.DEACCEL);
        AnimatorSet animatorSet2 = this.mOpenCloseAnimator;
        Animator[] animatorArr = new Animator[3];
        animatorArr[0] = createRevealAnimator;
        TaskThumbnailView thumbnailView = this.mTaskContainer.getThumbnailView();
        Property<TaskThumbnailView, Float> property = TaskThumbnailView.DIM_ALPHA;
        float[] fArr = new float[1];
        float f = 0.0f;
        fArr[0] = z ? 0.0f : 0.4f;
        animatorArr[1] = ObjectAnimator.ofFloat(thumbnailView, property, fArr);
        Property property2 = ALPHA;
        float[] fArr2 = new float[1];
        if (!z) {
            f = 1.0f;
        }
        fArr2[0] = f;
        animatorArr[2] = ObjectAnimator.ofFloat(this, property2, fArr2);
        animatorSet2.playTogether(animatorArr);
        this.mOpenCloseAnimator.addListener(new AnimationSuccessListener() {
            public void onAnimationStart(Animator animator) {
                TaskMenuView.this.setVisibility(0);
            }

            public void onAnimationSuccess(Animator animator) {
                if (z) {
                    TaskMenuView.this.closeComplete();
                }
            }
        });
        this.mOpenCloseAnimator.setDuration(z ? 100 : 150);
        this.mOpenCloseAnimator.start();
    }

    /* access modifiers changed from: private */
    public void closeComplete() {
        this.mIsOpen = false;
        this.mActivity.getDragLayer().removeView(this);
        ((RecentsView) this.mActivity.getOverviewPanel()).removeOnScrollChangedListener(this);
    }

    private RoundedRectRevealOutlineProvider createOpenCloseOutlineProvider() {
        float f = TaskCornerRadius.get(this.mContext);
        return new RoundedRectRevealOutlineProvider(f, f, new Rect(0, 0, getWidth(), 0), new Rect(0, 0, getWidth(), getHeight()));
    }
}
