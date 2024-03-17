package com.android.systemui.unfold.updates.hinge;

import androidx.core.util.Consumer;
import com.android.systemui.statusbar.policy.CallbackController;
import kotlin.Metadata;

@Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\bf\u0018\u00002\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00030\u00020\u0001J\b\u0010\u0004\u001a\u00020\u0005H&J\b\u0010\u0006\u001a\u00020\u0005H&¨\u0006\u0007"}, d2 = {"Lcom/android/systemui/unfold/updates/hinge/HingeAngleProvider;", "Lcom/android/systemui/statusbar/policy/CallbackController;", "Landroidx/core/util/Consumer;", "", "start", "", "stop", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: HingeAngleProvider.kt */
public interface HingeAngleProvider extends CallbackController<Consumer<Float>> {
    void start();

    void stop();
}
