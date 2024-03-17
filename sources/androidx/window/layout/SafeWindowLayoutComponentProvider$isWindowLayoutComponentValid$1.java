package androidx.window.layout;

import android.app.Activity;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

@Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u0001H\n¢\u0006\u0004\b\u0002\u0010\u0003"}, d2 = {"<anonymous>", "", "invoke", "()Ljava/lang/Boolean;"}, k = 3, mv = {1, 6, 0}, xi = 48)
/* compiled from: SafeWindowLayoutComponentProvider.kt */
final class SafeWindowLayoutComponentProvider$isWindowLayoutComponentValid$1 extends Lambda implements Function0<Boolean> {
    final /* synthetic */ ClassLoader $classLoader;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    SafeWindowLayoutComponentProvider$isWindowLayoutComponentValid$1(ClassLoader classLoader) {
        super(0);
        this.$classLoader = classLoader;
    }

    public final Boolean invoke() {
        Class access$windowLayoutComponentClass = SafeWindowLayoutComponentProvider.INSTANCE.windowLayoutComponentClass(this.$classLoader);
        boolean z = false;
        Method method = access$windowLayoutComponentClass.getMethod("addWindowLayoutInfoListener", new Class[]{Activity.class, Consumer.class});
        Method method2 = access$windowLayoutComponentClass.getMethod("removeWindowLayoutInfoListener", new Class[]{Consumer.class});
        SafeWindowLayoutComponentProvider safeWindowLayoutComponentProvider = SafeWindowLayoutComponentProvider.INSTANCE;
        Intrinsics.checkNotNullExpressionValue(method, "addListenerMethod");
        if (safeWindowLayoutComponentProvider.isPublic(method)) {
            SafeWindowLayoutComponentProvider safeWindowLayoutComponentProvider2 = SafeWindowLayoutComponentProvider.INSTANCE;
            Intrinsics.checkNotNullExpressionValue(method2, "removeListenerMethod");
            if (safeWindowLayoutComponentProvider2.isPublic(method2)) {
                z = true;
            }
        }
        return Boolean.valueOf(z);
    }
}
