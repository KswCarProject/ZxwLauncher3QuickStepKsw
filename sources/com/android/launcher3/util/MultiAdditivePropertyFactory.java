package com.android.launcher3.util;

import android.util.ArrayMap;
import android.util.FloatProperty;
import android.util.Property;
import android.view.View;
import com.android.launcher3.util.MultiAdditivePropertyFactory;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class MultiAdditivePropertyFactory<T extends View> {
    private static final boolean DEBUG = false;
    private static final String TAG = "MultiAdditivePropertyFactory";
    /* access modifiers changed from: private */
    public float mAggregationOfOthers = 0.0f;
    /* access modifiers changed from: private */
    public Integer mLastIndexSet = -1;
    private final String mName;
    /* access modifiers changed from: private */
    public final ArrayMap<Integer, MultiAdditivePropertyFactory<T>.MultiAdditiveProperty> mProperties = new ArrayMap<>();
    /* access modifiers changed from: private */
    public final Property<View, Float> mProperty;

    static /* synthetic */ float access$116(MultiAdditivePropertyFactory multiAdditivePropertyFactory, float f) {
        float f2 = multiAdditivePropertyFactory.mAggregationOfOthers + f;
        multiAdditivePropertyFactory.mAggregationOfOthers = f2;
        return f2;
    }

    public MultiAdditivePropertyFactory(String str, Property<View, Float> property) {
        this.mName = str;
        this.mProperty = property;
    }

    public MultiAdditivePropertyFactory<T>.MultiAdditiveProperty get(Integer num) {
        return (MultiAdditiveProperty) this.mProperties.computeIfAbsent(num, new Function(num) {
            public final /* synthetic */ Integer f$1;

            {
                this.f$1 = r2;
            }

            public final Object apply(Object obj) {
                return MultiAdditivePropertyFactory.this.lambda$get$0$MultiAdditivePropertyFactory(this.f$1, (Integer) obj);
            }
        });
    }

    public /* synthetic */ MultiAdditiveProperty lambda$get$0$MultiAdditivePropertyFactory(Integer num, Integer num2) {
        return new MultiAdditiveProperty(num.intValue(), this.mName + "_" + num);
    }

    class MultiAdditiveProperty extends FloatProperty<T> {
        private final int mInx;
        private float mValue = 0.0f;

        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        MultiAdditiveProperty(int i, String str) {
            super(str);
            this.mInx = i;
        }

        public void setValue(T t, float f) {
            if (MultiAdditivePropertyFactory.this.mLastIndexSet.intValue() != this.mInx) {
                float unused = MultiAdditivePropertyFactory.this.mAggregationOfOthers = 0.0f;
                MultiAdditivePropertyFactory.this.mProperties.forEach(new BiConsumer() {
                    public final void accept(Object obj, Object obj2) {
                        MultiAdditivePropertyFactory.MultiAdditiveProperty.this.lambda$setValue$0$MultiAdditivePropertyFactory$MultiAdditiveProperty((Integer) obj, (MultiAdditivePropertyFactory.MultiAdditiveProperty) obj2);
                    }
                });
                Integer unused2 = MultiAdditivePropertyFactory.this.mLastIndexSet = Integer.valueOf(this.mInx);
            }
            float access$100 = MultiAdditivePropertyFactory.this.mAggregationOfOthers + f;
            this.mValue = f;
            MultiAdditivePropertyFactory.this.apply(t, access$100);
        }

        public /* synthetic */ void lambda$setValue$0$MultiAdditivePropertyFactory$MultiAdditiveProperty(Integer num, MultiAdditiveProperty multiAdditiveProperty) {
            if (num.intValue() != this.mInx) {
                MultiAdditivePropertyFactory.access$116(MultiAdditivePropertyFactory.this, multiAdditiveProperty.mValue);
            }
        }

        public Float get(T t) {
            return (Float) MultiAdditivePropertyFactory.this.mProperty.get(t);
        }

        public String toString() {
            return String.valueOf(this.mValue);
        }
    }

    /* access modifiers changed from: protected */
    public void apply(View view, float f) {
        this.mProperty.set(view, Float.valueOf(f));
    }
}
