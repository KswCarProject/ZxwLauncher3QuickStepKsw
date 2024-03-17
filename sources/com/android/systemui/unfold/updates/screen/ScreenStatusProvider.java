package com.android.systemui.unfold.updates.screen;

import com.android.systemui.statusbar.policy.CallbackController;
import kotlin.Metadata;

@Metadata(d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u0003¨\u0006\u0004"}, d2 = {"Lcom/android/systemui/unfold/updates/screen/ScreenStatusProvider;", "Lcom/android/systemui/statusbar/policy/CallbackController;", "Lcom/android/systemui/unfold/updates/screen/ScreenStatusProvider$ScreenListener;", "ScreenListener", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: ScreenStatusProvider.kt */
public interface ScreenStatusProvider extends CallbackController<ScreenListener> {

    @Metadata(d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&¨\u0006\u0004"}, d2 = {"Lcom/android/systemui/unfold/updates/screen/ScreenStatusProvider$ScreenListener;", "", "onScreenTurnedOn", "", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: ScreenStatusProvider.kt */
    public interface ScreenListener {
        void onScreenTurnedOn();
    }
}
