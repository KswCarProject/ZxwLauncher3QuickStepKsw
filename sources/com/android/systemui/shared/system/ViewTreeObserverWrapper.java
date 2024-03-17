package com.android.systemui.shared.system;

import android.graphics.Rect;
import android.graphics.Region;
import android.view.ViewTreeObserver;
import com.android.systemui.shared.system.ViewTreeObserverWrapper;
import java.util.HashMap;

public class ViewTreeObserverWrapper {
    private static final HashMap<OnComputeInsetsListener, ViewTreeObserver.OnComputeInternalInsetsListener> sListenerInternalListenerMap = new HashMap<>();
    private static final HashMap<OnComputeInsetsListener, ViewTreeObserver> sListenerObserverMap = new HashMap<>();

    public interface OnComputeInsetsListener {
        void onComputeInsets(InsetsInfo insetsInfo);
    }

    public static void addOnComputeInsetsListener(ViewTreeObserver viewTreeObserver, OnComputeInsetsListener onComputeInsetsListener) {
        $$Lambda$ViewTreeObserverWrapper$Fc87AITf2d5DHsK1sT4dqB8PMy4 r0 = new ViewTreeObserver.OnComputeInternalInsetsListener() {
            public final void onComputeInternalInsets(ViewTreeObserver.InternalInsetsInfo internalInsetsInfo) {
                ViewTreeObserverWrapper.lambda$addOnComputeInsetsListener$0(ViewTreeObserverWrapper.OnComputeInsetsListener.this, internalInsetsInfo);
            }
        };
        sListenerObserverMap.put(onComputeInsetsListener, viewTreeObserver);
        sListenerInternalListenerMap.put(onComputeInsetsListener, r0);
        viewTreeObserver.addOnComputeInternalInsetsListener(r0);
    }

    static /* synthetic */ void lambda$addOnComputeInsetsListener$0(OnComputeInsetsListener onComputeInsetsListener, ViewTreeObserver.InternalInsetsInfo internalInsetsInfo) {
        InsetsInfo insetsInfo = new InsetsInfo();
        insetsInfo.contentInsets.set(internalInsetsInfo.contentInsets);
        insetsInfo.visibleInsets.set(internalInsetsInfo.visibleInsets);
        insetsInfo.touchableRegion.set(internalInsetsInfo.touchableRegion);
        onComputeInsetsListener.onComputeInsets(insetsInfo);
        internalInsetsInfo.contentInsets.set(insetsInfo.contentInsets);
        internalInsetsInfo.visibleInsets.set(insetsInfo.visibleInsets);
        internalInsetsInfo.touchableRegion.set(insetsInfo.touchableRegion);
        internalInsetsInfo.setTouchableInsets(insetsInfo.mTouchableInsets);
    }

    public static void removeOnComputeInsetsListener(OnComputeInsetsListener onComputeInsetsListener) {
        HashMap<OnComputeInsetsListener, ViewTreeObserver> hashMap = sListenerObserverMap;
        ViewTreeObserver viewTreeObserver = hashMap.get(onComputeInsetsListener);
        HashMap<OnComputeInsetsListener, ViewTreeObserver.OnComputeInternalInsetsListener> hashMap2 = sListenerInternalListenerMap;
        ViewTreeObserver.OnComputeInternalInsetsListener onComputeInternalInsetsListener = hashMap2.get(onComputeInsetsListener);
        if (!(viewTreeObserver == null || onComputeInternalInsetsListener == null)) {
            viewTreeObserver.removeOnComputeInternalInsetsListener(onComputeInternalInsetsListener);
        }
        hashMap.remove(onComputeInsetsListener);
        hashMap2.remove(onComputeInsetsListener);
    }

    public static final class InsetsInfo {
        public static final int TOUCHABLE_INSETS_CONTENT = 1;
        public static final int TOUCHABLE_INSETS_FRAME = 0;
        public static final int TOUCHABLE_INSETS_REGION = 3;
        public static final int TOUCHABLE_INSETS_VISIBLE = 2;
        public final Rect contentInsets = new Rect();
        int mTouchableInsets;
        public final Region touchableRegion = new Region();
        public final Rect visibleInsets = new Rect();

        public void setTouchableInsets(int i) {
            this.mTouchableInsets = i;
        }

        public int hashCode() {
            return (((((this.contentInsets.hashCode() * 31) + this.visibleInsets.hashCode()) * 31) + this.touchableRegion.hashCode()) * 31) + this.mTouchableInsets;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            InsetsInfo insetsInfo = (InsetsInfo) obj;
            if (this.mTouchableInsets != insetsInfo.mTouchableInsets || !this.contentInsets.equals(insetsInfo.contentInsets) || !this.visibleInsets.equals(insetsInfo.visibleInsets) || !this.touchableRegion.equals(insetsInfo.touchableRegion)) {
                return false;
            }
            return true;
        }
    }
}
