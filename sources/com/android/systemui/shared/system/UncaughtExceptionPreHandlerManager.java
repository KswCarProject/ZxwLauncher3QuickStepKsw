package com.android.systemui.shared.system;

import android.util.Log;
import java.lang.Thread;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.inject.Singleton;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Singleton
@Metadata(d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0003\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001:\u0001\u0013B\u0007\b\u0007¢\u0006\u0002\u0010\u0002J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0007H\u0002J\b\u0010\u000b\u001a\u00020\tH\u0002J\u001a\u0010\f\u001a\u00020\t2\b\u0010\r\u001a\u0004\u0018\u00010\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010J\u000e\u0010\u0011\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u0007R\u0012\u0010\u0003\u001a\u00060\u0004R\u00020\u0000X\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0004¢\u0006\u0002\n\u0000¨\u0006\u0014"}, d2 = {"Lcom/android/systemui/shared/system/UncaughtExceptionPreHandlerManager;", "", "()V", "globalUncaughtExceptionPreHandler", "Lcom/android/systemui/shared/system/UncaughtExceptionPreHandlerManager$GlobalUncaughtExceptionHandler;", "handlers", "", "Ljava/lang/Thread$UncaughtExceptionHandler;", "addHandler", "", "it", "checkGlobalHandlerSetup", "handleUncaughtException", "thread", "Ljava/lang/Thread;", "throwable", "", "registerHandler", "handler", "GlobalUncaughtExceptionHandler", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: UncaughtExceptionPreHandlerManager.kt */
public final class UncaughtExceptionPreHandlerManager {
    private final GlobalUncaughtExceptionHandler globalUncaughtExceptionPreHandler = new GlobalUncaughtExceptionHandler(this);
    private final List<Thread.UncaughtExceptionHandler> handlers = new CopyOnWriteArrayList();

    public final void registerHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        Intrinsics.checkNotNullParameter(uncaughtExceptionHandler, "handler");
        checkGlobalHandlerSetup();
        addHandler(uncaughtExceptionHandler);
    }

    private final void checkGlobalHandlerSetup() {
        Thread.UncaughtExceptionHandler uncaughtExceptionPreHandler = Thread.getUncaughtExceptionPreHandler();
        if (Intrinsics.areEqual((Object) uncaughtExceptionPreHandler, (Object) this.globalUncaughtExceptionPreHandler)) {
            return;
        }
        if (!(uncaughtExceptionPreHandler instanceof GlobalUncaughtExceptionHandler)) {
            if (uncaughtExceptionPreHandler != null) {
                addHandler(uncaughtExceptionPreHandler);
            }
            Thread.setUncaughtExceptionPreHandler(this.globalUncaughtExceptionPreHandler);
            return;
        }
        throw new IllegalStateException("Two UncaughtExceptionPreHandlerManagers created");
    }

    private final void addHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        if (!this.handlers.contains(uncaughtExceptionHandler)) {
            this.handlers.add(uncaughtExceptionHandler);
        }
    }

    public final void handleUncaughtException(Thread thread, Throwable th) {
        for (Thread.UncaughtExceptionHandler uncaughtException : this.handlers) {
            try {
                uncaughtException.uncaughtException(thread, th);
            } catch (Exception e) {
                Log.wtf("Uncaught exception pre-handler error", e);
            }
        }
    }

    @Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0003\n\u0000\b\u0004\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u001c\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\bH\u0016¨\u0006\t"}, d2 = {"Lcom/android/systemui/shared/system/UncaughtExceptionPreHandlerManager$GlobalUncaughtExceptionHandler;", "Ljava/lang/Thread$UncaughtExceptionHandler;", "(Lcom/android/systemui/shared/system/UncaughtExceptionPreHandlerManager;)V", "uncaughtException", "", "thread", "Ljava/lang/Thread;", "throwable", "", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: UncaughtExceptionPreHandlerManager.kt */
    public final class GlobalUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        final /* synthetic */ UncaughtExceptionPreHandlerManager this$0;

        public GlobalUncaughtExceptionHandler(UncaughtExceptionPreHandlerManager uncaughtExceptionPreHandlerManager) {
            Intrinsics.checkNotNullParameter(uncaughtExceptionPreHandlerManager, "this$0");
            this.this$0 = uncaughtExceptionPreHandlerManager;
        }

        public void uncaughtException(Thread thread, Throwable th) {
            this.this$0.handleUncaughtException(thread, th);
        }
    }
}
