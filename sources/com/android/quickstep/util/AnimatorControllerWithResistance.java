package com.android.quickstep.util;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.FloatProperty;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.quickstep.LauncherActivityInterface;
import com.android.quickstep.views.RecentsView;

public class AnimatorControllerWithResistance {
    private static final TimeInterpolator RECENTS_SCALE_RESIST_INTERPOLATOR = Interpolators.DEACCEL;
    private static final TimeInterpolator RECENTS_TRANSLATE_RESIST_INTERPOLATOR = Interpolators.LINEAR;
    private float mLastNormalProgress = -1.0f;
    private float mLastResistProgress;
    private final AnimatorPlaybackController mNormalController;
    private final AnimatorPlaybackController mResistanceController;

    private enum RecentsResistanceParams {
        FROM_APP(0.75f, 0.5f, 1.0f, false),
        FROM_APP_TABLET(1.0f, 0.7f, 1.0f, true),
        FROM_OVERVIEW(1.0f, 0.75f, 0.5f, false);
        
        public final float scaleMaxResist;
        public final float scaleStartResist;
        public final boolean stopScalingAtTop;
        public final float translationFactor;

        private RecentsResistanceParams(float f, float f2, float f3, boolean z) {
            this.scaleStartResist = f;
            this.scaleMaxResist = f2;
            this.translationFactor = f3;
            this.stopScalingAtTop = z;
        }
    }

    public AnimatorControllerWithResistance(AnimatorPlaybackController animatorPlaybackController, AnimatorPlaybackController animatorPlaybackController2) {
        this.mNormalController = animatorPlaybackController;
        this.mResistanceController = animatorPlaybackController2;
    }

    public AnimatorPlaybackController getNormalController() {
        return this.mNormalController;
    }

    public void setProgress(float f, float f2) {
        float f3 = 0.0f;
        float boundToRange = Utilities.boundToRange(f, 0.0f, 1.0f);
        if (boundToRange != this.mLastNormalProgress) {
            this.mLastNormalProgress = boundToRange;
            this.mNormalController.setPlayFraction(boundToRange);
        }
        if (f2 > 1.0f) {
            if (f > 1.0f) {
                f3 = Utilities.getProgress(f, 1.0f, f2);
            }
            if (f3 != this.mLastResistProgress) {
                this.mLastResistProgress = f3;
                this.mResistanceController.setPlayFraction(f3);
            }
        }
    }

    public static <SCALE, TRANSLATION> AnimatorControllerWithResistance createForRecents(AnimatorPlaybackController animatorPlaybackController, Context context, RecentsOrientedState recentsOrientedState, DeviceProfile deviceProfile, SCALE scale, FloatProperty<SCALE> floatProperty, TRANSLATION translation, FloatProperty<TRANSLATION> floatProperty2) {
        AnimatorPlaybackController animatorPlaybackController2 = animatorPlaybackController;
        return new AnimatorControllerWithResistance(animatorPlaybackController, createRecentsResistanceAnim(new RecentsParams(context, recentsOrientedState, deviceProfile, scale, floatProperty, translation, floatProperty2)).createPlaybackController());
    }

    public static <SCALE, TRANSLATION> PendingAnimation createRecentsResistanceAnim(RecentsParams<SCALE, TRANSLATION> recentsParams) {
        PendingAnimation pendingAnimation;
        Rect rect = new Rect();
        PagedOrientationHandler orientationHandler = recentsParams.recentsOrientedState.getOrientationHandler();
        LauncherActivityInterface.INSTANCE.calculateTaskSize(recentsParams.context, recentsParams.dp, rect);
        long j = (long) rect.bottom;
        if (recentsParams.resistAnim != null) {
            pendingAnimation = recentsParams.resistAnim;
        } else {
            pendingAnimation = new PendingAnimation(2 * j);
        }
        PointF pointF = new PointF();
        float fullScreenScaleAndPivot = recentsParams.recentsOrientedState.getFullScreenScaleAndPivot(rect, recentsParams.dp, pointF);
        RectF rectF = new RectF(rect);
        Matrix matrix = new Matrix();
        matrix.setScale(recentsParams.resistanceParams.scaleMaxResist, recentsParams.resistanceParams.scaleMaxResist, pointF.x, pointF.y);
        matrix.mapRect(rectF);
        PendingAnimation pendingAnimation2 = pendingAnimation;
        pendingAnimation2.addFloat(recentsParams.translationTarget, recentsParams.translationProperty, recentsParams.startTranslation, rectF.top * ((float) orientationHandler.getSecondaryTranslationDirectionFactor()) * recentsParams.resistanceParams.translationFactor, RECENTS_TRANSLATE_RESIST_INTERPOLATOR);
        float f = recentsParams.startScale - (((fullScreenScaleAndPivot - recentsParams.startScale) / ((float) (recentsParams.dp.heightPx - rect.bottom))) * ((float) j));
        float progress = Utilities.getProgress(recentsParams.resistanceParams.scaleStartResist, recentsParams.startScale, f);
        float progress2 = Utilities.getProgress(recentsParams.resistanceParams.scaleMaxResist, recentsParams.startScale, f);
        float f2 = 1.0f;
        if (recentsParams.resistanceParams.stopScalingAtTop) {
            f2 = 1.0f - (((float) rect.top) / rectF.top);
        }
        PendingAnimation pendingAnimation3 = pendingAnimation;
        pendingAnimation3.addFloat(recentsParams.scaleTarget, recentsParams.scaleProperty, recentsParams.startScale, f, new TimeInterpolator(progress, f2, progress2) {
            public final /* synthetic */ float f$0;
            public final /* synthetic */ float f$1;
            public final /* synthetic */ float f$2;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final float getInterpolation(float f) {
                return AnimatorControllerWithResistance.lambda$createRecentsResistanceAnim$0(this.f$0, this.f$1, this.f$2, f);
            }
        });
        return pendingAnimation;
    }

    static /* synthetic */ float lambda$createRecentsResistanceAnim$0(float f, float f2, float f3, float f4) {
        if (f4 < f) {
            return f4;
        }
        if (f4 > f2) {
            return f3;
        }
        return f + (RECENTS_SCALE_RESIST_INTERPOLATOR.getInterpolation(Utilities.getProgress(f4, f, f2)) * (f3 - f));
    }

    public static PendingAnimation createRecentsResistanceFromOverviewAnim(Launcher launcher, PendingAnimation pendingAnimation) {
        RecentsView recentsView = (RecentsView) launcher.getOverviewPanel();
        return createRecentsResistanceAnim(new RecentsParams(launcher, recentsView.getPagedViewOrientedState(), launcher.getDeviceProfile(), recentsView, RecentsView.RECENTS_SCALE_PROPERTY, recentsView, RecentsView.TASK_SECONDARY_TRANSLATION).setResistAnim(pendingAnimation).setResistanceParams(RecentsResistanceParams.FROM_OVERVIEW).setStartScale(recentsView.getScaleX()));
    }

    private static class RecentsParams<SCALE, TRANSLATION> {
        public final Context context;
        public final DeviceProfile dp;
        public final RecentsOrientedState recentsOrientedState;
        public PendingAnimation resistAnim;
        public RecentsResistanceParams resistanceParams;
        public final FloatProperty<SCALE> scaleProperty;
        public final SCALE scaleTarget;
        public float startScale;
        public float startTranslation;
        public final FloatProperty<TRANSLATION> translationProperty;
        public final TRANSLATION translationTarget;

        private RecentsParams(Context context2, RecentsOrientedState recentsOrientedState2, DeviceProfile deviceProfile, SCALE scale, FloatProperty<SCALE> floatProperty, TRANSLATION translation, FloatProperty<TRANSLATION> floatProperty2) {
            this.resistAnim = null;
            this.startScale = 1.0f;
            this.startTranslation = 0.0f;
            this.context = context2;
            this.recentsOrientedState = recentsOrientedState2;
            this.dp = deviceProfile;
            this.scaleTarget = scale;
            this.scaleProperty = floatProperty;
            this.translationTarget = translation;
            this.translationProperty = floatProperty2;
            if (deviceProfile.isTablet) {
                this.resistanceParams = RecentsResistanceParams.FROM_APP_TABLET;
            } else {
                this.resistanceParams = RecentsResistanceParams.FROM_APP;
            }
        }

        /* access modifiers changed from: private */
        public RecentsParams setResistAnim(PendingAnimation pendingAnimation) {
            this.resistAnim = pendingAnimation;
            return this;
        }

        /* access modifiers changed from: private */
        public RecentsParams setResistanceParams(RecentsResistanceParams recentsResistanceParams) {
            this.resistanceParams = recentsResistanceParams;
            return this;
        }

        /* access modifiers changed from: private */
        public RecentsParams setStartScale(float f) {
            this.startScale = f;
            return this;
        }

        private RecentsParams setStartTranslation(float f) {
            this.startTranslation = f;
            return this;
        }
    }
}
