package com.android.systemui.flags;

import kotlin.Metadata;

@Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\bf\u0018\u00002\u00020\u0001:\u0002\t\nJ\u001c\u0010\u0002\u001a\u00020\u00032\n\u0010\u0004\u001a\u0006\u0012\u0002\b\u00030\u00052\u0006\u0010\u0006\u001a\u00020\u0007H&J\u0010\u0010\b\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H&¨\u0006\u000b"}, d2 = {"Lcom/android/systemui/flags/FlagListenable;", "", "addListener", "", "flag", "Lcom/android/systemui/flags/Flag;", "listener", "Lcom/android/systemui/flags/FlagListenable$Listener;", "removeListener", "FlagEvent", "Listener", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: FlagListenable.kt */
public interface FlagListenable {

    @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\b\u0010\u0006\u001a\u00020\u0007H&R\u0012\u0010\u0002\u001a\u00020\u0003X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0004\u0010\u0005¨\u0006\b"}, d2 = {"Lcom/android/systemui/flags/FlagListenable$FlagEvent;", "", "flagId", "", "getFlagId", "()I", "requestNoRestart", "", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: FlagListenable.kt */
    public interface FlagEvent {
        int getFlagId();

        void requestNoRestart();
    }

    @Metadata(d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bæ\u0001\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&¨\u0006\u0006"}, d2 = {"Lcom/android/systemui/flags/FlagListenable$Listener;", "", "onFlagChanged", "", "event", "Lcom/android/systemui/flags/FlagListenable$FlagEvent;", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: FlagListenable.kt */
    public interface Listener {
        void onFlagChanged(FlagEvent flagEvent);
    }

    void addListener(Flag<?> flag, Listener listener);

    void removeListener(Listener listener);
}
