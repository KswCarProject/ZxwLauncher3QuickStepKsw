package com.android.systemui.flags;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import com.android.systemui.flags.FlagListenable;
import com.android.systemui.flags.FlagManager;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;

@Metadata(d1 = {"\u0000x\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010#\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0010\u001e\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\b\u0018\u0000 52\u00020\u0001:\u0003567B\u0017\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\tJ\u001c\u0010\u001a\u001a\u00020\u001b2\n\u0010\u001c\u001a\u0006\u0012\u0002\b\u00030\u001d2\u0006\u0010\u001e\u001a\u00020\u001fH\u0016J\u0010\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020\fH\u0002J\u001e\u0010#\u001a\u00020\u001b2\u0006\u0010\"\u001a\u00020\f2\u000e\u0010$\u001a\n\u0012\u0004\u0012\u00020\u0015\u0018\u00010\u000bJ\u000e\u0010%\u001a\u00020\u001b2\u0006\u0010\"\u001a\u00020\fJ\u0016\u0010&\u001a\u0012\u0012\u000e\u0012\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u001d0(0'J\u000e\u0010)\u001a\u00020*2\u0006\u0010\"\u001a\u00020\fJ\u0015\u0010+\u001a\u0004\u0018\u00010\u00152\u0006\u0010\"\u001a\u00020\f¢\u0006\u0002\u0010,J)\u0010-\u001a\u0004\u0018\u0001H.\"\u0004\b\u0000\u0010.2\u0006\u0010\"\u001a\u00020\f2\f\u0010/\u001a\b\u0012\u0004\u0012\u0002H.00¢\u0006\u0002\u00101J\u0010\u00102\u001a\u00020\u001b2\u0006\u0010\u001e\u001a\u00020\u001fH\u0016J\u0016\u00103\u001a\u00020\u001b2\u0006\u0010\"\u001a\u00020\f2\u0006\u00104\u001a\u00020\u0015R\"\u0010\n\u001a\n\u0012\u0004\u0012\u00020\f\u0018\u00010\u000bX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00130\u0012X\u0004¢\u0006\u0002\n\u0000R\"\u0010\u0014\u001a\n\u0012\u0004\u0012\u00020\u0015\u0018\u00010\u000bX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u000e\"\u0004\b\u0017\u0010\u0010R\u000e\u0010\u0007\u001a\u00020\bX\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0019X\u0004¢\u0006\u0002\n\u0000¨\u00068"}, d2 = {"Lcom/android/systemui/flags/FlagManager;", "Lcom/android/systemui/flags/FlagListenable;", "context", "Landroid/content/Context;", "handler", "Landroid/os/Handler;", "(Landroid/content/Context;Landroid/os/Handler;)V", "settings", "Lcom/android/systemui/flags/FlagSettingsHelper;", "(Landroid/content/Context;Lcom/android/systemui/flags/FlagSettingsHelper;Landroid/os/Handler;)V", "clearCacheAction", "Ljava/util/function/Consumer;", "", "getClearCacheAction", "()Ljava/util/function/Consumer;", "setClearCacheAction", "(Ljava/util/function/Consumer;)V", "listeners", "", "Lcom/android/systemui/flags/FlagManager$PerFlagListener;", "onSettingsChangedAction", "", "getOnSettingsChangedAction", "setOnSettingsChangedAction", "settingsObserver", "Landroid/database/ContentObserver;", "addListener", "", "flag", "Lcom/android/systemui/flags/Flag;", "listener", "Lcom/android/systemui/flags/FlagListenable$Listener;", "createIntent", "Landroid/content/Intent;", "id", "dispatchListenersAndMaybeRestart", "restartAction", "eraseFlag", "getFlagsFuture", "Lcom/google/common/util/concurrent/ListenableFuture;", "", "idToSettingsKey", "", "isEnabled", "(I)Ljava/lang/Boolean;", "readFlagValue", "T", "serializer", "Lcom/android/systemui/flags/FlagSerializer;", "(ILcom/android/systemui/flags/FlagSerializer;)Ljava/lang/Object;", "removeListener", "setFlagValue", "enabled", "Companion", "PerFlagListener", "SettingsObserver", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: FlagManager.kt */
public final class FlagManager implements FlagListenable {
    public static final String ACTION_GET_FLAGS = "com.android.systemui.action.GET_FLAGS";
    public static final String ACTION_SET_FLAG = "com.android.systemui.action.SET_FLAG";
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    public static final String EXTRA_FLAGS = "flags";
    public static final String EXTRA_ID = "id";
    public static final String EXTRA_VALUE = "value";
    public static final String FLAGS_PERMISSION = "com.android.systemui.permission.FLAGS";
    public static final String RECEIVING_PACKAGE = "com.android.systemui";
    private static final String SETTINGS_PREFIX = "systemui/flags";
    private Consumer<Integer> clearCacheAction;
    private final Context context;
    /* access modifiers changed from: private */
    public final Handler handler;
    private final Set<PerFlagListener> listeners;
    private Consumer<Boolean> onSettingsChangedAction;
    private final FlagSettingsHelper settings;
    private final ContentObserver settingsObserver;

    public FlagManager(Context context2, FlagSettingsHelper flagSettingsHelper, Handler handler2) {
        Intrinsics.checkNotNullParameter(context2, "context");
        Intrinsics.checkNotNullParameter(flagSettingsHelper, "settings");
        Intrinsics.checkNotNullParameter(handler2, "handler");
        this.context = context2;
        this.settings = flagSettingsHelper;
        this.handler = handler2;
        this.listeners = new LinkedHashSet();
        this.settingsObserver = new SettingsObserver(this);
    }

    @Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\b\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000¨\u0006\f"}, d2 = {"Lcom/android/systemui/flags/FlagManager$Companion;", "", "()V", "ACTION_GET_FLAGS", "", "ACTION_SET_FLAG", "EXTRA_FLAGS", "EXTRA_ID", "EXTRA_VALUE", "FLAGS_PERMISSION", "RECEIVING_PACKAGE", "SETTINGS_PREFIX", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: FlagManager.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public FlagManager(android.content.Context r4, android.os.Handler r5) {
        /*
            r3 = this;
            java.lang.String r0 = "context"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r4, r0)
            java.lang.String r0 = "handler"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r5, r0)
            com.android.systemui.flags.FlagSettingsHelper r0 = new com.android.systemui.flags.FlagSettingsHelper
            android.content.ContentResolver r1 = r4.getContentResolver()
            java.lang.String r2 = "context.contentResolver"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r1, r2)
            r0.<init>(r1)
            r3.<init>(r4, r0, r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.flags.FlagManager.<init>(android.content.Context, android.os.Handler):void");
    }

    public final Consumer<Boolean> getOnSettingsChangedAction() {
        return this.onSettingsChangedAction;
    }

    public final void setOnSettingsChangedAction(Consumer<Boolean> consumer) {
        this.onSettingsChangedAction = consumer;
    }

    public final Consumer<Integer> getClearCacheAction() {
        return this.clearCacheAction;
    }

    public final void setClearCacheAction(Consumer<Integer> consumer) {
        this.clearCacheAction = consumer;
    }

    public final ListenableFuture<Collection<Flag<?>>> getFlagsFuture() {
        Intent intent = new Intent(ACTION_GET_FLAGS);
        intent.setPackage(RECEIVING_PACKAGE);
        ListenableFuture<Collection<Flag<?>>> future = CallbackToFutureAdapter.getFuture(new CallbackToFutureAdapter.Resolver(intent) {
            public final /* synthetic */ Intent f$1;

            {
                this.f$1 = r2;
            }

            public final Object attachCompleter(CallbackToFutureAdapter.Completer completer) {
                return FlagManager.m118getFlagsFuture$lambda0(FlagManager.this, this.f$1, completer);
            }
        });
        Intrinsics.checkNotNullExpressionValue(future, "getFuture {\n            …\"QueryingFlags\"\n        }");
        return future;
    }

    /* access modifiers changed from: private */
    /* renamed from: getFlagsFuture$lambda-0  reason: not valid java name */
    public static final Object m118getFlagsFuture$lambda0(FlagManager flagManager, Intent intent, CallbackToFutureAdapter.Completer completer) {
        Intrinsics.checkNotNullParameter(flagManager, "this$0");
        Intrinsics.checkNotNullParameter(intent, "$intent");
        Intrinsics.checkNotNullParameter(completer, "completer");
        flagManager.context.sendOrderedBroadcast(intent, (String) null, new FlagManager$getFlagsFuture$1$1(completer), (Handler) null, -1, "extra data", (Bundle) null);
        return "QueryingFlags";
    }

    public final Boolean isEnabled(int i) {
        return (Boolean) readFlagValue(i, BooleanFlagSerializer.INSTANCE);
    }

    public final void setFlagValue(int i, boolean z) {
        Intent createIntent = createIntent(i);
        createIntent.putExtra("value", z);
        this.context.sendBroadcast(createIntent);
    }

    public final void eraseFlag(int i) {
        this.context.sendBroadcast(createIntent(i));
    }

    public final <T> T readFlagValue(int i, FlagSerializer<T> flagSerializer) {
        Intrinsics.checkNotNullParameter(flagSerializer, "serializer");
        return flagSerializer.fromSettingsData(this.settings.getString(idToSettingsKey(i)));
    }

    public void addListener(Flag<?> flag, FlagListenable.Listener listener) {
        Intrinsics.checkNotNullParameter(flag, "flag");
        Intrinsics.checkNotNullParameter(listener, "listener");
        synchronized (this.listeners) {
            boolean isEmpty = this.listeners.isEmpty();
            this.listeners.add(new PerFlagListener(flag.getId(), listener));
            if (isEmpty) {
                this.settings.registerContentObserver(SETTINGS_PREFIX, true, this.settingsObserver);
            }
            Unit unit = Unit.INSTANCE;
        }
    }

    public void removeListener(FlagListenable.Listener listener) {
        Intrinsics.checkNotNullParameter(listener, "listener");
        synchronized (this.listeners) {
            if (!this.listeners.isEmpty()) {
                this.listeners.removeIf(new Predicate() {
                    public final boolean test(Object obj) {
                        return FlagManager.m120removeListener$lambda3$lambda2(FlagListenable.Listener.this, (FlagManager.PerFlagListener) obj);
                    }
                });
                if (this.listeners.isEmpty()) {
                    this.settings.unregisterContentObserver(this.settingsObserver);
                }
                Unit unit = Unit.INSTANCE;
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: removeListener$lambda-3$lambda-2  reason: not valid java name */
    public static final boolean m120removeListener$lambda3$lambda2(FlagListenable.Listener listener, PerFlagListener perFlagListener) {
        Intrinsics.checkNotNullParameter(listener, "$listener");
        Intrinsics.checkNotNullParameter(perFlagListener, "it");
        return Intrinsics.areEqual((Object) perFlagListener.getListener(), (Object) listener);
    }

    private final Intent createIntent(int i) {
        Intent intent = new Intent(ACTION_SET_FLAG);
        intent.setPackage(RECEIVING_PACKAGE);
        intent.putExtra(EXTRA_ID, i);
        return intent;
    }

    public final String idToSettingsKey(int i) {
        return Intrinsics.stringPlus("systemui/flags/", Integer.valueOf(i));
    }

    @Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0004\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u001a\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\bH\u0016¨\u0006\t"}, d2 = {"Lcom/android/systemui/flags/FlagManager$SettingsObserver;", "Landroid/database/ContentObserver;", "(Lcom/android/systemui/flags/FlagManager;)V", "onChange", "", "selfChange", "", "uri", "Landroid/net/Uri;", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: FlagManager.kt */
    public final class SettingsObserver extends ContentObserver {
        final /* synthetic */ FlagManager this$0;

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public SettingsObserver(FlagManager flagManager) {
            super(flagManager.handler);
            Intrinsics.checkNotNullParameter(flagManager, "this$0");
            this.this$0 = flagManager;
        }

        public void onChange(boolean z, Uri uri) {
            if (uri != null) {
                List<String> pathSegments = uri.getPathSegments();
                String str = pathSegments.get(pathSegments.size() - 1);
                try {
                    Intrinsics.checkNotNullExpressionValue(str, "idStr");
                    int parseInt = Integer.parseInt(str);
                    Consumer<Integer> clearCacheAction = this.this$0.getClearCacheAction();
                    if (clearCacheAction != null) {
                        clearCacheAction.accept(Integer.valueOf(parseInt));
                    }
                    FlagManager flagManager = this.this$0;
                    flagManager.dispatchListenersAndMaybeRestart(parseInt, flagManager.getOnSettingsChangedAction());
                } catch (NumberFormatException unused) {
                }
            }
        }
    }

    public final void dispatchListenersAndMaybeRestart(int i, Consumer<Boolean> consumer) {
        List list;
        synchronized (this.listeners) {
            Collection arrayList = new ArrayList();
            for (PerFlagListener perFlagListener : this.listeners) {
                FlagListenable.Listener listener = perFlagListener.getId() == i ? perFlagListener.getListener() : null;
                if (listener != null) {
                    arrayList.add(listener);
                }
            }
            list = (List) arrayList;
        }
        boolean z = false;
        if (!list.isEmpty()) {
            Iterable<FlagListenable.Listener> iterable = list;
            Collection arrayList2 = new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10));
            for (FlagListenable.Listener onFlagChanged : iterable) {
                Ref.BooleanRef booleanRef = new Ref.BooleanRef();
                onFlagChanged.onFlagChanged(new FlagManager$dispatchListenersAndMaybeRestart$suppressRestartList$1$event$1(i, booleanRef));
                arrayList2.add(Boolean.valueOf(booleanRef.element));
            }
            Iterable iterable2 = (List) arrayList2;
            if (!(iterable2 instanceof Collection) || !((Collection) iterable2).isEmpty()) {
                Iterator it = iterable2.iterator();
                while (true) {
                    if (it.hasNext()) {
                        if (!((Boolean) it.next()).booleanValue()) {
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
            z = true;
            if (consumer != null) {
                consumer.accept(Boolean.valueOf(z));
            }
        } else if (consumer != null) {
            consumer.accept(false);
        }
    }

    @Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003HÆ\u0003J\t\u0010\f\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÆ\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0011\u001a\u00020\u0003HÖ\u0001J\t\u0010\u0012\u001a\u00020\u0013HÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n¨\u0006\u0014"}, d2 = {"Lcom/android/systemui/flags/FlagManager$PerFlagListener;", "", "id", "", "listener", "Lcom/android/systemui/flags/FlagListenable$Listener;", "(ILcom/android/systemui/flags/FlagListenable$Listener;)V", "getId", "()I", "getListener", "()Lcom/android/systemui/flags/FlagListenable$Listener;", "component1", "component2", "copy", "equals", "", "other", "hashCode", "toString", "", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: FlagManager.kt */
    private static final class PerFlagListener {
        private final int id;
        private final FlagListenable.Listener listener;

        public static /* synthetic */ PerFlagListener copy$default(PerFlagListener perFlagListener, int i, FlagListenable.Listener listener2, int i2, Object obj) {
            if ((i2 & 1) != 0) {
                i = perFlagListener.id;
            }
            if ((i2 & 2) != 0) {
                listener2 = perFlagListener.listener;
            }
            return perFlagListener.copy(i, listener2);
        }

        public final int component1() {
            return this.id;
        }

        public final FlagListenable.Listener component2() {
            return this.listener;
        }

        public final PerFlagListener copy(int i, FlagListenable.Listener listener2) {
            Intrinsics.checkNotNullParameter(listener2, "listener");
            return new PerFlagListener(i, listener2);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof PerFlagListener)) {
                return false;
            }
            PerFlagListener perFlagListener = (PerFlagListener) obj;
            return this.id == perFlagListener.id && Intrinsics.areEqual((Object) this.listener, (Object) perFlagListener.listener);
        }

        public int hashCode() {
            return (Integer.hashCode(this.id) * 31) + this.listener.hashCode();
        }

        public String toString() {
            return "PerFlagListener(id=" + this.id + ", listener=" + this.listener + ')';
        }

        public PerFlagListener(int i, FlagListenable.Listener listener2) {
            Intrinsics.checkNotNullParameter(listener2, "listener");
            this.id = i;
            this.listener = listener2;
        }

        public final int getId() {
            return this.id;
        }

        public final FlagListenable.Listener getListener() {
            return this.listener;
        }
    }
}
