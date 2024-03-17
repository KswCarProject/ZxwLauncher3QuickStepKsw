package com.android.launcher3.taskbar;

import com.android.launcher3.DeviceProfile;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

@Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\n¢\u0006\u0002\b\u0004"}, d2 = {"<anonymous>", "", "<anonymous parameter 0>", "Lcom/android/launcher3/DeviceProfile;", "invoke"}, k = 3, mv = {1, 6, 0}, xi = 48)
/* compiled from: TaskbarInsetsController.kt */
final class TaskbarInsetsController$deviceProfileChangeListener$1 extends Lambda implements Function1<DeviceProfile, Unit> {
    final /* synthetic */ TaskbarInsetsController this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    TaskbarInsetsController$deviceProfileChangeListener$1(TaskbarInsetsController taskbarInsetsController) {
        super(1);
        this.this$0 = taskbarInsetsController;
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        invoke((DeviceProfile) obj);
        return Unit.INSTANCE;
    }

    public final void invoke(DeviceProfile deviceProfile) {
        Intrinsics.checkNotNullParameter(deviceProfile, "$noName_0");
        this.this$0.onTaskbarWindowHeightOrInsetsChanged();
    }
}
