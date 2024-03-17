package androidx.window.layout;

import java.lang.reflect.Method;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

@Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u0001H\n¢\u0006\u0004\b\u0002\u0010\u0003"}, d2 = {"<anonymous>", "", "invoke", "()Ljava/lang/Boolean;"}, k = 3, mv = {1, 6, 0}, xi = 48)
/* compiled from: SafeWindowLayoutComponentProvider.kt */
final class SafeWindowLayoutComponentProvider$isWindowLayoutProviderValid$1 extends Lambda implements Function0<Boolean> {
    final /* synthetic */ ClassLoader $classLoader;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    SafeWindowLayoutComponentProvider$isWindowLayoutProviderValid$1(ClassLoader classLoader) {
        super(0);
        this.$classLoader = classLoader;
    }

    public final Boolean invoke() {
        boolean z = false;
        Method declaredMethod = SafeWindowLayoutComponentProvider.INSTANCE.windowExtensionsProviderClass(this.$classLoader).getDeclaredMethod("getWindowExtensions", new Class[0]);
        Class access$windowExtensionsClass = SafeWindowLayoutComponentProvider.INSTANCE.windowExtensionsClass(this.$classLoader);
        SafeWindowLayoutComponentProvider safeWindowLayoutComponentProvider = SafeWindowLayoutComponentProvider.INSTANCE;
        Intrinsics.checkNotNullExpressionValue(declaredMethod, "getWindowExtensionsMethod");
        Intrinsics.checkNotNullExpressionValue(access$windowExtensionsClass, "windowExtensionsClass");
        if (safeWindowLayoutComponentProvider.doesReturn(declaredMethod, (Class<?>) access$windowExtensionsClass) && SafeWindowLayoutComponentProvider.INSTANCE.isPublic(declaredMethod)) {
            z = true;
        }
        return Boolean.valueOf(z);
    }
}
