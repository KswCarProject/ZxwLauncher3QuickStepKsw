package com.android.launcher3.anim;

public class SpringProperty {
    public static final SpringProperty DEFAULT = new SpringProperty();
    public static final int FLAG_CAN_SPRING_ON_END = 1;
    public static final int FLAG_CAN_SPRING_ON_START = 2;
    public final int flags;
    float mDampingRatio;
    float mStiffness;

    public SpringProperty() {
        this(0);
    }

    public SpringProperty(int i) {
        this.mDampingRatio = 0.5f;
        this.mStiffness = 1500.0f;
        this.flags = i;
    }

    public SpringProperty setDampingRatio(float f) {
        this.mDampingRatio = f;
        return this;
    }

    public SpringProperty setStiffness(float f) {
        this.mStiffness = f;
        return this;
    }
}
