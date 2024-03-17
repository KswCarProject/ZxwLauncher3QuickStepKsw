package com.android.launcher3.model;

import android.os.UserHandle;
import com.android.launcher3.icons.cache.IconCacheUpdateHandler;
import java.util.HashSet;

/* renamed from: com.android.launcher3.model.-$$Lambda$LoaderTask$cAJXD07GePZf3zBbuQLMQDZqXvI  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$LoaderTask$cAJXD07GePZf3zBbuQLMQDZqXvI implements IconCacheUpdateHandler.OnUpdateCallback {
    public static final /* synthetic */ $$Lambda$LoaderTask$cAJXD07GePZf3zBbuQLMQDZqXvI INSTANCE = new $$Lambda$LoaderTask$cAJXD07GePZf3zBbuQLMQDZqXvI();

    private /* synthetic */ $$Lambda$LoaderTask$cAJXD07GePZf3zBbuQLMQDZqXvI() {
    }

    public final void onPackageIconsUpdated(HashSet hashSet, UserHandle userHandle) {
        LoaderTask.lambda$run$0(hashSet, userHandle);
    }
}
