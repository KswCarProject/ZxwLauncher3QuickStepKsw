package androidx.window.layout;

import android.graphics.Rect;
import java.lang.reflect.Method;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KClass;

@Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u0001H\n¢\u0006\u0004\b\u0002\u0010\u0003"}, d2 = {"<anonymous>", "", "invoke", "()Ljava/lang/Boolean;"}, k = 3, mv = {1, 6, 0}, xi = 48)
/* compiled from: SafeWindowLayoutComponentProvider.kt */
final class SafeWindowLayoutComponentProvider$isFoldingFeatureValid$1 extends Lambda implements Function0<Boolean> {
    final /* synthetic */ ClassLoader $classLoader;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    SafeWindowLayoutComponentProvider$isFoldingFeatureValid$1(ClassLoader classLoader) {
        super(0);
        this.$classLoader = classLoader;
    }

    public final Boolean invoke() {
        Class access$foldingFeatureClass = SafeWindowLayoutComponentProvider.INSTANCE.foldingFeatureClass(this.$classLoader);
        boolean z = false;
        Method method = access$foldingFeatureClass.getMethod("getBounds", new Class[0]);
        Method method2 = access$foldingFeatureClass.getMethod("getType", new Class[0]);
        Method method3 = access$foldingFeatureClass.getMethod("getState", new Class[0]);
        SafeWindowLayoutComponentProvider safeWindowLayoutComponentProvider = SafeWindowLayoutComponentProvider.INSTANCE;
        Intrinsics.checkNotNullExpressionValue(method, "getBoundsMethod");
        if (safeWindowLayoutComponentProvider.doesReturn(method, (KClass<?>) Reflection.getOrCreateKotlinClass(Rect.class)) && SafeWindowLayoutComponentProvider.INSTANCE.isPublic(method)) {
            SafeWindowLayoutComponentProvider safeWindowLayoutComponentProvider2 = SafeWindowLayoutComponentProvider.INSTANCE;
            Intrinsics.checkNotNullExpressionValue(method2, "getTypeMethod");
            if (safeWindowLayoutComponentProvider2.doesReturn(method2, (KClass<?>) Reflection.getOrCreateKotlinClass(Integer.TYPE)) && SafeWindowLayoutComponentProvider.INSTANCE.isPublic(method2)) {
                SafeWindowLayoutComponentProvider safeWindowLayoutComponentProvider3 = SafeWindowLayoutComponentProvider.INSTANCE;
                Intrinsics.checkNotNullExpressionValue(method3, "getStateMethod");
                if (safeWindowLayoutComponentProvider3.doesReturn(method3, (KClass<?>) Reflection.getOrCreateKotlinClass(Integer.TYPE)) && SafeWindowLayoutComponentProvider.INSTANCE.isPublic(method3)) {
                    z = true;
                }
            }
        }
        return Boolean.valueOf(z);
    }
}
