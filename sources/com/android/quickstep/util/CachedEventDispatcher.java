package com.android.quickstep.util;

import android.view.MotionEvent;
import com.android.systemui.shared.system.InputChannelCompat;
import java.util.ArrayList;
import java.util.function.Consumer;

public class CachedEventDispatcher {
    private ArrayList<MotionEvent> mCache;
    private Consumer<MotionEvent> mConsumer;
    private MotionEvent mLastEvent;

    public void dispatchEvent(MotionEvent motionEvent) {
        Consumer<MotionEvent> consumer = this.mConsumer;
        if (consumer != null) {
            consumer.accept(motionEvent);
            return;
        }
        MotionEvent motionEvent2 = this.mLastEvent;
        if (motionEvent2 == null || !InputChannelCompat.mergeMotionEvent(motionEvent, motionEvent2)) {
            if (this.mCache == null) {
                this.mCache = new ArrayList<>();
            }
            MotionEvent obtain = MotionEvent.obtain(motionEvent);
            this.mLastEvent = obtain;
            this.mCache.add(obtain);
        }
    }

    public void setConsumer(Consumer<MotionEvent> consumer) {
        if (consumer != null) {
            this.mConsumer = consumer;
            ArrayList<MotionEvent> arrayList = this.mCache;
            int size = arrayList == null ? 0 : arrayList.size();
            for (int i = 0; i < size; i++) {
                MotionEvent motionEvent = this.mCache.get(i);
                this.mConsumer.accept(motionEvent);
                motionEvent.recycle();
            }
            this.mCache = null;
            this.mLastEvent = null;
        }
    }

    public boolean hasConsumer() {
        return this.mConsumer != null;
    }
}
