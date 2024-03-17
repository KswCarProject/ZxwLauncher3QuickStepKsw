package com.android.launcher3.util;

import android.util.ArrayMap;
import android.util.FloatProperty;
import android.view.View;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.MultiScalePropertyFactory;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class MultiScalePropertyFactory<T extends View> {
    private static final boolean DEBUG = false;
    private static final String TAG = "MultiScaleProperty";
    /* access modifiers changed from: private */
    public float mLastAggregatedValue = 1.0f;
    /* access modifiers changed from: private */
    public Integer mLastIndexSet = -1;
    /* access modifiers changed from: private */
    public float mMaxOfOthers = 0.0f;
    /* access modifiers changed from: private */
    public float mMinOfOthers = 0.0f;
    /* access modifiers changed from: private */
    public float mMultiplicationOfOthers = 0.0f;
    private final String mName;
    /* access modifiers changed from: private */
    public final ArrayMap<Integer, MultiScalePropertyFactory<T>.MultiScaleProperty> mProperties = new ArrayMap<>();

    static /* synthetic */ float access$332(MultiScalePropertyFactory multiScalePropertyFactory, float f) {
        float f2 = multiScalePropertyFactory.mMultiplicationOfOthers * f;
        multiScalePropertyFactory.mMultiplicationOfOthers = f2;
        return f2;
    }

    public MultiScalePropertyFactory(String str) {
        this.mName = str;
    }

    public MultiScalePropertyFactory<T>.MultiScaleProperty get(Integer num) {
        return (MultiScaleProperty) this.mProperties.computeIfAbsent(num, new Function(num) {
            public final /* synthetic */ Integer f$1;

            {
                this.f$1 = r2;
            }

            public final Object apply(Object obj) {
                return MultiScalePropertyFactory.this.lambda$get$0$MultiScalePropertyFactory(this.f$1, (Integer) obj);
            }
        });
    }

    public /* synthetic */ MultiScaleProperty lambda$get$0$MultiScalePropertyFactory(Integer num, Integer num2) {
        return new MultiScaleProperty(num.intValue(), this.mName + "_" + num);
    }

    class MultiScaleProperty extends FloatProperty<T> {
        private final int mInx;
        private float mValue = 1.0f;

        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        MultiScaleProperty(int i, String str) {
            super(str);
            this.mInx = i;
        }

        public void setValue(T t, float f) {
            if (MultiScalePropertyFactory.this.mLastIndexSet.intValue() != this.mInx) {
                float unused = MultiScalePropertyFactory.this.mMinOfOthers = Float.MAX_VALUE;
                float unused2 = MultiScalePropertyFactory.this.mMaxOfOthers = Float.MIN_VALUE;
                float unused3 = MultiScalePropertyFactory.this.mMultiplicationOfOthers = 1.0f;
                MultiScalePropertyFactory.this.mProperties.forEach(new BiConsumer() {
                    public final void accept(Object obj, Object obj2) {
                        MultiScalePropertyFactory.MultiScaleProperty.this.lambda$setValue$0$MultiScalePropertyFactory$MultiScaleProperty((Integer) obj, (MultiScalePropertyFactory.MultiScaleProperty) obj2);
                    }
                });
                Integer unused4 = MultiScalePropertyFactory.this.mLastIndexSet = Integer.valueOf(this.mInx);
            }
            float unused5 = MultiScalePropertyFactory.this.mLastAggregatedValue = Utilities.boundToRange(MultiScalePropertyFactory.this.mMultiplicationOfOthers * f, Math.min(MultiScalePropertyFactory.this.mMinOfOthers, f), Math.max(MultiScalePropertyFactory.this.mMaxOfOthers, f));
            this.mValue = f;
            MultiScalePropertyFactory multiScalePropertyFactory = MultiScalePropertyFactory.this;
            multiScalePropertyFactory.apply(t, multiScalePropertyFactory.mLastAggregatedValue);
        }

        public /* synthetic */ void lambda$setValue$0$MultiScalePropertyFactory$MultiScaleProperty(Integer num, MultiScaleProperty multiScaleProperty) {
            if (num.intValue() != this.mInx) {
                MultiScalePropertyFactory multiScalePropertyFactory = MultiScalePropertyFactory.this;
                float unused = multiScalePropertyFactory.mMinOfOthers = Math.min(multiScalePropertyFactory.mMinOfOthers, multiScaleProperty.mValue);
                MultiScalePropertyFactory multiScalePropertyFactory2 = MultiScalePropertyFactory.this;
                float unused2 = multiScalePropertyFactory2.mMaxOfOthers = Math.max(multiScalePropertyFactory2.mMaxOfOthers, multiScaleProperty.mValue);
                MultiScalePropertyFactory.access$332(MultiScalePropertyFactory.this, multiScaleProperty.mValue);
            }
        }

        public Float get(T t) {
            return Float.valueOf(t.getScaleX());
        }

        public String toString() {
            return String.valueOf(this.mValue);
        }
    }

    /* access modifiers changed from: protected */
    public void apply(View view, float f) {
        view.setScaleX(f);
        view.setScaleY(f);
    }
}
