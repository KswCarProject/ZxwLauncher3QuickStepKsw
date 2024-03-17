package com.android.launcher3.util;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.util.FloatProperty;
import android.view.View;
import com.android.launcher3.anim.AlphaUpdateListener;
import java.util.Arrays;
import java.util.function.Consumer;

public class MultiValueAlpha {
    public static final FloatProperty<AlphaProperty> VALUE = new FloatProperty<AlphaProperty>("value") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public Float get(AlphaProperty alphaProperty) {
            return Float.valueOf(alphaProperty.mValue);
        }

        public void setValue(AlphaProperty alphaProperty, float f) {
            alphaProperty.setValue(f);
        }
    };
    /* access modifiers changed from: private */
    public final AlphaProperty[] mMyProperties;
    /* access modifiers changed from: private */
    public boolean mUpdateVisibility;
    /* access modifiers changed from: private */
    public int mValidMask = 0;
    /* access modifiers changed from: private */
    public final View mView;

    public MultiValueAlpha(View view, int i) {
        this.mView = view;
        this.mMyProperties = new AlphaProperty[i];
        for (int i2 = 0; i2 < i; i2++) {
            int i3 = 1 << i2;
            this.mValidMask |= i3;
            this.mMyProperties[i2] = new AlphaProperty(i3);
        }
    }

    public String toString() {
        return Arrays.toString(this.mMyProperties);
    }

    public AlphaProperty getProperty(int i) {
        return this.mMyProperties[i];
    }

    public void setUpdateVisibility(boolean z) {
        this.mUpdateVisibility = z;
    }

    public class AlphaProperty {
        private Consumer<Float> mConsumer;
        private final int mMyMask;
        private float mOthers = 1.0f;
        /* access modifiers changed from: private */
        public float mValue = 1.0f;

        AlphaProperty(int i) {
            this.mMyMask = i;
        }

        public void setValue(float f) {
            if (this.mValue != f) {
                if ((MultiValueAlpha.this.mValidMask & this.mMyMask) == 0) {
                    this.mOthers = 1.0f;
                    for (AlphaProperty alphaProperty : MultiValueAlpha.this.mMyProperties) {
                        if (alphaProperty != this) {
                            this.mOthers *= alphaProperty.mValue;
                        }
                    }
                }
                int unused = MultiValueAlpha.this.mValidMask = this.mMyMask;
                this.mValue = f;
                MultiValueAlpha.this.mView.setAlpha(this.mOthers * f);
                if (MultiValueAlpha.this.mUpdateVisibility) {
                    AlphaUpdateListener.updateVisibility(MultiValueAlpha.this.mView);
                }
                Consumer<Float> consumer = this.mConsumer;
                if (consumer != null) {
                    consumer.accept(Float.valueOf(this.mValue));
                }
            }
        }

        public float getValue() {
            return this.mValue;
        }

        public void setConsumer(Consumer<Float> consumer) {
            this.mConsumer = consumer;
            if (consumer != null) {
                consumer.accept(Float.valueOf(this.mValue));
            }
        }

        public String toString() {
            return Float.toString(this.mValue);
        }

        public Animator animateToValue(float f) {
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, MultiValueAlpha.VALUE, new float[]{f});
            ofFloat.setAutoCancel(true);
            return ofFloat;
        }
    }
}
