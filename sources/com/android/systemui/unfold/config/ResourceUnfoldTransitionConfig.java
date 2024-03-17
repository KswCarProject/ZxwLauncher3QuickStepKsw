package com.android.systemui.unfold.config;

import android.content.Context;
import android.os.SystemProperties;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0006\b\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\b\u0010\n\u001a\u00020\u0006H\u0002J\b\u0010\u000b\u001a\u00020\u0006H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0005\u001a\u00020\u00068VX\u0004¢\u0006\u0006\u001a\u0004\b\u0005\u0010\u0007R\u0014\u0010\b\u001a\u00020\u00068VX\u0004¢\u0006\u0006\u001a\u0004\b\b\u0010\u0007R\u0014\u0010\t\u001a\u00020\u00068BX\u0004¢\u0006\u0006\u001a\u0004\b\t\u0010\u0007¨\u0006\f"}, d2 = {"Lcom/android/systemui/unfold/config/ResourceUnfoldTransitionConfig;", "Lcom/android/systemui/unfold/config/UnfoldTransitionConfig;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "isEnabled", "", "()Z", "isHingeAngleEnabled", "isPropertyEnabled", "readIsEnabledResource", "readIsHingeAngleEnabled", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: ResourceUnfoldTransitionConfig.kt */
public final class ResourceUnfoldTransitionConfig implements UnfoldTransitionConfig {
    private final Context context;

    public ResourceUnfoldTransitionConfig(Context context2) {
        Intrinsics.checkNotNullParameter(context2, "context");
        this.context = context2;
    }

    public boolean isEnabled() {
        return readIsEnabledResource() && isPropertyEnabled();
    }

    public boolean isHingeAngleEnabled() {
        return readIsHingeAngleEnabled();
    }

    private final boolean isPropertyEnabled() {
        return SystemProperties.getInt("persist.unfold.transition_enabled", 1) == 1;
    }

    private final boolean readIsEnabledResource() {
        return this.context.getResources().getBoolean(17891804);
    }

    private final boolean readIsHingeAngleEnabled() {
        return this.context.getResources().getBoolean(17891805);
    }
}
