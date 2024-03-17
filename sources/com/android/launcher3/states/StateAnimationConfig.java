package com.android.launcher3.states;

import android.view.animation.Interpolator;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class StateAnimationConfig {
    public static final int ANIM_ALL_APPS_FADE = 10;
    public static final int ANIM_DEPTH = 13;
    public static final int ANIM_HOTSEAT_FADE = 16;
    public static final int ANIM_HOTSEAT_SCALE = 4;
    public static final int ANIM_HOTSEAT_TRANSLATE = 5;
    public static final int ANIM_OVERVIEW_ACTIONS_FADE = 14;
    public static final int ANIM_OVERVIEW_FADE = 9;
    public static final int ANIM_OVERVIEW_MODAL = 12;
    public static final int ANIM_OVERVIEW_SCALE = 6;
    public static final int ANIM_OVERVIEW_TRANSLATE_X = 7;
    public static final int ANIM_OVERVIEW_TRANSLATE_Y = 8;
    public static final int ANIM_SCRIM_FADE = 11;
    private static final int ANIM_TYPES_COUNT = 17;
    public static final int ANIM_VERTICAL_PROGRESS = 0;
    public static final int ANIM_WORKSPACE_FADE = 3;
    public static final int ANIM_WORKSPACE_PAGE_TRANSLATE_X = 15;
    public static final int ANIM_WORKSPACE_SCALE = 1;
    public static final int ANIM_WORKSPACE_TRANSLATE = 2;
    public static final int SKIP_ALL_ANIMATIONS = 1;
    public static final int SKIP_DEPTH_CONTROLLER = 4;
    public static final int SKIP_OVERVIEW = 2;
    public static final int SKIP_SCRIM = 8;
    public int animFlags = 0;
    public long duration;
    protected final Interpolator[] mInterpolators = new Interpolator[17];
    public boolean userControlled;

    @Retention(RetentionPolicy.SOURCE)
    public @interface AnimType {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface AnimationFlags {
    }

    public void copyTo(StateAnimationConfig stateAnimationConfig) {
        stateAnimationConfig.duration = this.duration;
        stateAnimationConfig.animFlags = this.animFlags;
        stateAnimationConfig.userControlled = this.userControlled;
        for (int i = 0; i < 17; i++) {
            stateAnimationConfig.mInterpolators[i] = this.mInterpolators[i];
        }
    }

    public Interpolator getInterpolator(int i, Interpolator interpolator) {
        Interpolator[] interpolatorArr = this.mInterpolators;
        return interpolatorArr[i] == null ? interpolator : interpolatorArr[i];
    }

    public void setInterpolator(int i, Interpolator interpolator) {
        this.mInterpolators[i] = interpolator;
    }

    public boolean hasAnimationFlag(int i) {
        return (i & this.animFlags) != 0;
    }
}
