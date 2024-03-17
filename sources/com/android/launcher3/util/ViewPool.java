package com.android.launcher3.util;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.util.ViewPool.Reusable;

public class ViewPool<T extends View & Reusable> {
    private int mCurrentSize = 0;
    private final LayoutInflater mInflater;
    private final int mLayoutId;
    private final ViewGroup mParent;
    private final Object[] mPool;

    public interface Reusable {
        void onRecycle();
    }

    public ViewPool(Context context, ViewGroup viewGroup, int i, int i2, int i3) {
        this.mLayoutId = i;
        this.mParent = viewGroup;
        this.mInflater = LayoutInflater.from(context);
        this.mPool = new Object[i2];
        if (i3 > 0) {
            initPool(i3);
        }
    }

    private void initPool(int i) {
        Preconditions.assertUIThread();
        Handler handler = new Handler();
        LayoutInflater layoutInflater = this.mInflater;
        new Thread(new Runnable(i, layoutInflater.cloneInContext(layoutInflater.getContext()), handler) {
            public final /* synthetic */ int f$1;
            public final /* synthetic */ LayoutInflater f$2;
            public final /* synthetic */ Handler f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ViewPool.this.lambda$initPool$1$ViewPool(this.f$1, this.f$2, this.f$3);
            }
        }, "ViewPool-init").start();
    }

    public /* synthetic */ void lambda$initPool$1$ViewPool(int i, LayoutInflater layoutInflater, Handler handler) {
        for (int i2 = 0; i2 < i; i2++) {
            handler.post(new Runnable(inflateNewView(layoutInflater)) {
                public final /* synthetic */ View f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ViewPool.this.lambda$initPool$0$ViewPool(this.f$1);
                }
            });
        }
    }

    public void recycle(T t) {
        Preconditions.assertUIThread();
        ((Reusable) t).onRecycle();
        lambda$initPool$0$ViewPool(t);
    }

    /* access modifiers changed from: private */
    /* renamed from: addToPool */
    public void lambda$initPool$0$ViewPool(T t) {
        Preconditions.assertUIThread();
        int i = this.mCurrentSize;
        Object[] objArr = this.mPool;
        if (i < objArr.length) {
            objArr[i] = t;
            this.mCurrentSize = i + 1;
        }
    }

    public T getView() {
        Preconditions.assertUIThread();
        int i = this.mCurrentSize;
        if (i <= 0) {
            return inflateNewView(this.mInflater);
        }
        int i2 = i - 1;
        this.mCurrentSize = i2;
        return (View) this.mPool[i2];
    }

    private T inflateNewView(LayoutInflater layoutInflater) {
        return layoutInflater.inflate(this.mLayoutId, this.mParent, false);
    }
}
