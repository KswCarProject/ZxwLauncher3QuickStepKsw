package com.android.systemui.animation;

import android.view.View;
import android.window.SurfaceSyncer;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J$\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\b2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00060\u000bJ \u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\fH\u0007R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u000e¢\u0006\u0002\n\u0000¨\u0006\r"}, d2 = {"Lcom/android/systemui/animation/ViewRootSync;", "", "()V", "surfaceSyncer", "Landroid/window/SurfaceSyncer;", "synchronizeNextDraw", "", "view", "Landroid/view/View;", "otherView", "then", "Lkotlin/Function0;", "Ljava/lang/Runnable;", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: ViewRootSync.kt */
public final class ViewRootSync {
    public static final ViewRootSync INSTANCE = new ViewRootSync();
    private static SurfaceSyncer surfaceSyncer;

    private ViewRootSync() {
    }

    public final void synchronizeNextDraw(View view, View view2, Function0<Unit> function0) {
        Intrinsics.checkNotNullParameter(view, "view");
        Intrinsics.checkNotNullParameter(view2, "otherView");
        Intrinsics.checkNotNullParameter(function0, "then");
        if (!view.isAttachedToWindow() || view.getViewRootImpl() == null || !view2.isAttachedToWindow() || view2.getViewRootImpl() == null || Intrinsics.areEqual((Object) view.getViewRootImpl(), (Object) view2.getViewRootImpl())) {
            function0.invoke();
            return;
        }
        SurfaceSyncer surfaceSyncer2 = new SurfaceSyncer();
        int i = surfaceSyncer2.setupSync(new Runnable() {
            public final void run() {
                ViewRootSync.m117synchronizeNextDraw$lambda1$lambda0(Function0.this);
            }
        });
        surfaceSyncer2.addToSync(i, view);
        surfaceSyncer2.addToSync(i, view2);
        surfaceSyncer2.markSyncReady(i);
        surfaceSyncer = surfaceSyncer2;
    }

    /* access modifiers changed from: private */
    /* renamed from: synchronizeNextDraw$lambda-1$lambda-0  reason: not valid java name */
    public static final void m117synchronizeNextDraw$lambda1$lambda0(Function0 function0) {
        Intrinsics.checkNotNullParameter(function0, "$then");
        function0.invoke();
    }

    @JvmStatic
    public static final void synchronizeNextDraw(View view, View view2, Runnable runnable) {
        Intrinsics.checkNotNullParameter(view, "view");
        Intrinsics.checkNotNullParameter(view2, "otherView");
        Intrinsics.checkNotNullParameter(runnable, "then");
        INSTANCE.synchronizeNextDraw(view, view2, (Function0<Unit>) new ViewRootSync$synchronizeNextDraw$2(runnable));
    }
}
