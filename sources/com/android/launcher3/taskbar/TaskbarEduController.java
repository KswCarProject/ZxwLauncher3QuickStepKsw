package com.android.launcher3.taskbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.R;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.icons.BitmapInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.ItemInfoWithIcon;
import com.android.launcher3.taskbar.TaskbarControllers;
import com.android.launcher3.taskbar.TaskbarEduController;
import com.android.launcher3.uioverrides.PredictedAppIcon;
import com.android.launcher3.views.AbstractSlideInView;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TaskbarEduController implements TaskbarControllers.LoggableTaskbarController {
    private static final long WAVE_ANIM_DELAY = 250;
    private static final long WAVE_ANIM_EACH_ICON_DURATION = 633;
    private static final float WAVE_ANIM_FRACTION_BOTTOM = 0.9f;
    private static final float WAVE_ANIM_FRACTION_TOP = 0.4f;
    private static final float WAVE_ANIM_ICON_SCALE = 1.2f;
    private static final TimeInterpolator WAVE_ANIM_OVERSHOOT_INTERPOLATOR = Interpolators.DEACCEL;
    private static final TimeInterpolator WAVE_ANIM_OVERSHOOT_RETURN_INTERPOLATOR = Interpolators.ACCEL_DEACCEL;
    private static final long WAVE_ANIM_SLOT_MACHINE_DURATION = 1085;
    private static final int WAVE_ANIM_SLOT_MACHINE_NUM_ICONS = 3;
    private static final long WAVE_ANIM_STAGGER = 50;
    private static final TimeInterpolator WAVE_ANIM_TO_BOTTOM_INTERPOLATOR = Interpolators.ACCEL_2;
    private static final TimeInterpolator WAVE_ANIM_TO_TOP_INTERPOLATOR = Interpolators.FAST_OUT_SLOW_IN;
    private final TaskbarActivityContext mActivity;
    /* access modifiers changed from: private */
    public Animator mAnim;
    TaskbarControllers mControllers;
    /* access modifiers changed from: private */
    public TaskbarEduView mTaskbarEduView;
    private final float mWaveAnimTranslationY;
    private final float mWaveAnimTranslationYReturnOvershoot;

    public TaskbarEduController(TaskbarActivityContext taskbarActivityContext) {
        this.mActivity = taskbarActivityContext;
        Resources resources = taskbarActivityContext.getResources();
        this.mWaveAnimTranslationY = resources.getDimension(R.dimen.taskbar_edu_wave_anim_trans_y);
        this.mWaveAnimTranslationYReturnOvershoot = resources.getDimension(R.dimen.taskbar_edu_wave_anim_trans_y_return_overshoot);
    }

    public void init(TaskbarControllers taskbarControllers) {
        this.mControllers = taskbarControllers;
    }

    /* access modifiers changed from: package-private */
    public void showEdu() {
        this.mActivity.setTaskbarWindowFullscreen(true);
        this.mActivity.getDragLayer().post(new Runnable() {
            public final void run() {
                TaskbarEduController.this.lambda$showEdu$1$TaskbarEduController();
            }
        });
    }

    public /* synthetic */ void lambda$showEdu$1$TaskbarEduController() {
        TaskbarEduView taskbarEduView = (TaskbarEduView) this.mActivity.getLayoutInflater().inflate(R.layout.taskbar_edu, this.mActivity.getDragLayer(), false);
        this.mTaskbarEduView = taskbarEduView;
        taskbarEduView.init(new TaskbarEduCallbacks());
        this.mTaskbarEduView.addOnCloseListener(new AbstractSlideInView.OnCloseListener() {
            public final void onSlideInViewClosed() {
                TaskbarEduController.this.lambda$showEdu$0$TaskbarEduController();
            }
        });
        this.mTaskbarEduView.show();
        startAnim(createWaveAnim());
    }

    public /* synthetic */ void lambda$showEdu$0$TaskbarEduController() {
        this.mTaskbarEduView = null;
    }

    /* access modifiers changed from: package-private */
    public void hideEdu() {
        TaskbarEduView taskbarEduView = this.mTaskbarEduView;
        if (taskbarEduView != null) {
            taskbarEduView.close(true);
        }
    }

    private void startAnim(Animator animator) {
        Animator animator2 = this.mAnim;
        if (animator2 != null) {
            animator2.end();
        }
        this.mAnim = animator;
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                Animator unused = TaskbarEduController.this.mAnim = null;
            }
        });
        this.mAnim.start();
    }

    private Animator createWaveAnim() {
        char c;
        AnimatorSet animatorSet = new AnimatorSet();
        View[] iconViews = this.mControllers.taskbarViewController.getIconViews();
        char c2 = 0;
        int i = 0;
        while (i < iconViews.length) {
            View view = iconViews[i];
            AnimatorSet animatorSet2 = new AnimatorSet();
            Keyframe[] keyframeArr = new Keyframe[4];
            keyframeArr[c2] = Keyframe.ofFloat(0.0f, 1.0f);
            keyframeArr[1] = Keyframe.ofFloat(0.4f, WAVE_ANIM_ICON_SCALE);
            keyframeArr[2] = Keyframe.ofFloat(WAVE_ANIM_FRACTION_BOTTOM, 1.0f);
            keyframeArr[3] = Keyframe.ofFloat(1.0f, 1.0f);
            Keyframe keyframe = keyframeArr[1];
            TimeInterpolator timeInterpolator = WAVE_ANIM_TO_TOP_INTERPOLATOR;
            keyframe.setInterpolator(timeInterpolator);
            Keyframe keyframe2 = keyframeArr[2];
            TimeInterpolator timeInterpolator2 = WAVE_ANIM_TO_BOTTOM_INTERPOLATOR;
            keyframe2.setInterpolator(timeInterpolator2);
            Keyframe[] keyframeArr2 = new Keyframe[5];
            keyframeArr2[c2] = Keyframe.ofFloat(0.0f, 0.0f);
            keyframeArr2[1] = Keyframe.ofFloat(0.4f, -this.mWaveAnimTranslationY);
            keyframeArr2[2] = Keyframe.ofFloat(WAVE_ANIM_FRACTION_BOTTOM, 0.0f);
            keyframeArr2[3] = Keyframe.ofFloat(0.95f, this.mWaveAnimTranslationYReturnOvershoot);
            keyframeArr2[4] = Keyframe.ofFloat(1.0f, 0.0f);
            keyframeArr2[1].setInterpolator(timeInterpolator);
            keyframeArr2[2].setInterpolator(timeInterpolator2);
            keyframeArr2[3].setInterpolator(WAVE_ANIM_OVERSHOOT_INTERPOLATOR);
            keyframeArr2[4].setInterpolator(WAVE_ANIM_OVERSHOOT_RETURN_INTERPOLATOR);
            animatorSet2.play(ObjectAnimator.ofPropertyValuesHolder(view, new PropertyValuesHolder[]{PropertyValuesHolder.ofKeyframe(LauncherAnimUtils.SCALE_PROPERTY, keyframeArr)}).setDuration(WAVE_ANIM_EACH_ICON_DURATION));
            animatorSet2.play(ObjectAnimator.ofPropertyValuesHolder(view, new PropertyValuesHolder[]{PropertyValuesHolder.ofKeyframe(View.TRANSLATION_Y, keyframeArr2)}).setDuration(WAVE_ANIM_EACH_ICON_DURATION));
            if (view instanceof PredictedAppIcon) {
                PredictedAppIcon predictedAppIcon = (PredictedAppIcon) view;
                List list = (List) this.mControllers.uiController.getAppIconsForEdu().filter(new Predicate() {
                    public final boolean test(Object obj) {
                        return TaskbarEduController.lambda$createWaveAnim$2(ItemInfo.this, (ItemInfoWithIcon) obj);
                    }
                }).map($$Lambda$TaskbarEduController$bx1Ij0hXuEdfGD8tlZV1FuoU.INSTANCE).filter($$Lambda$TaskbarEduController$aHoRaQ3I12KFN4KYw5MmgOKmAW4.INSTANCE).collect(Collectors.toList());
                Collections.shuffle(list);
                if (list.size() > 3) {
                    c = 0;
                    list = list.subList(0, 3);
                } else {
                    c = 0;
                }
                Animator createSlotMachineAnim = predictedAppIcon.createSlotMachineAnim(list);
                if (createSlotMachineAnim != null) {
                    animatorSet2.play(createSlotMachineAnim.setDuration(WAVE_ANIM_SLOT_MACHINE_DURATION));
                }
            } else {
                c = 0;
            }
            animatorSet2.setStartDelay(((long) i) * WAVE_ANIM_STAGGER);
            animatorSet.play(animatorSet2);
            i++;
            c2 = c;
        }
        animatorSet.setStartDelay(250);
        return animatorSet;
    }

    static /* synthetic */ boolean lambda$createWaveAnim$2(ItemInfo itemInfo, ItemInfoWithIcon itemInfoWithIcon) {
        return !TextUtils.equals(itemInfoWithIcon.title, itemInfo.title);
    }

    static /* synthetic */ boolean lambda$createWaveAnim$4(BitmapInfo bitmapInfo) {
        return !bitmapInfo.isNullOrLowRes();
    }

    public void dumpLogs(String str, PrintWriter printWriter) {
        printWriter.println(str + "TaskbarEduController:");
        Object[] objArr = new Object[2];
        objArr[0] = str;
        objArr[1] = Boolean.valueOf(this.mTaskbarEduView != null);
        printWriter.println(String.format("%s\tisShowingEdu=%b", objArr));
        printWriter.println(String.format("%s\tmWaveAnimTranslationY=%.2f", new Object[]{str, Float.valueOf(this.mWaveAnimTranslationY)}));
        printWriter.println(String.format("%s\tmWaveAnimTranslationYReturnOvershoot=%.2f", new Object[]{str, Float.valueOf(this.mWaveAnimTranslationYReturnOvershoot)}));
    }

    class TaskbarEduCallbacks {
        TaskbarEduCallbacks() {
        }

        /* access modifiers changed from: package-private */
        public void onPageChanged(int i, int i2) {
            if (i == 0) {
                TaskbarEduController.this.mTaskbarEduView.updateStartButton(R.string.taskbar_edu_close, new View.OnClickListener() {
                    public final void onClick(View view) {
                        TaskbarEduController.TaskbarEduCallbacks.this.lambda$onPageChanged$0$TaskbarEduController$TaskbarEduCallbacks(view);
                    }
                });
            } else {
                TaskbarEduController.this.mTaskbarEduView.updateStartButton(R.string.taskbar_edu_previous, new View.OnClickListener(i) {
                    public final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(View view) {
                        TaskbarEduController.TaskbarEduCallbacks.this.lambda$onPageChanged$1$TaskbarEduController$TaskbarEduCallbacks(this.f$1, view);
                    }
                });
            }
            if (i == i2 - 1) {
                TaskbarEduController.this.mTaskbarEduView.updateEndButton(R.string.taskbar_edu_done, new View.OnClickListener() {
                    public final void onClick(View view) {
                        TaskbarEduController.TaskbarEduCallbacks.this.lambda$onPageChanged$2$TaskbarEduController$TaskbarEduCallbacks(view);
                    }
                });
            } else {
                TaskbarEduController.this.mTaskbarEduView.updateEndButton(R.string.taskbar_edu_next, new View.OnClickListener(i) {
                    public final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(View view) {
                        TaskbarEduController.TaskbarEduCallbacks.this.lambda$onPageChanged$3$TaskbarEduController$TaskbarEduCallbacks(this.f$1, view);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onPageChanged$0$TaskbarEduController$TaskbarEduCallbacks(View view) {
            TaskbarEduController.this.mTaskbarEduView.close(true);
        }

        public /* synthetic */ void lambda$onPageChanged$1$TaskbarEduController$TaskbarEduCallbacks(int i, View view) {
            TaskbarEduController.this.mTaskbarEduView.snapToPage(i - 1);
        }

        public /* synthetic */ void lambda$onPageChanged$2$TaskbarEduController$TaskbarEduCallbacks(View view) {
            TaskbarEduController.this.mTaskbarEduView.close(true);
        }

        public /* synthetic */ void lambda$onPageChanged$3$TaskbarEduController$TaskbarEduCallbacks(int i, View view) {
            TaskbarEduController.this.mTaskbarEduView.snapToPage(i + 1);
        }
    }
}
