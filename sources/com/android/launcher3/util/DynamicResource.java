package com.android.launcher3.util;

import android.content.Context;
import android.content.res.Resources;
import com.android.launcher3.uioverrides.plugins.PluginManagerWrapper;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.plugins.ResourceProvider;

public class DynamicResource implements ResourceProvider, PluginListener<ResourceProvider> {
    private static final MainThreadInitializedObject<DynamicResource> INSTANCE = new MainThreadInitializedObject<>($$Lambda$DynamicResource$H76pgZzgL_y1hqAVfGzB3i_vAOw.INSTANCE);
    private final Context mContext;
    private ResourceProvider mPlugin;

    public static /* synthetic */ DynamicResource lambda$H76pgZzgL_y1hqAVfGzB3i_vAOw(Context context) {
        return new DynamicResource(context);
    }

    private DynamicResource(Context context) {
        this.mContext = context;
        PluginManagerWrapper.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).addPluginListener(this, ResourceProvider.class, false);
    }

    public int getInt(int i) {
        return this.mContext.getResources().getInteger(i);
    }

    public float getFraction(int i) {
        return this.mContext.getResources().getFraction(i, 1, 1);
    }

    public float getDimension(int i) {
        return this.mContext.getResources().getDimension(i);
    }

    public int getColor(int i) {
        return this.mContext.getResources().getColor(i, (Resources.Theme) null);
    }

    public float getFloat(int i) {
        return this.mContext.getResources().getFloat(i);
    }

    public void onPluginConnected(ResourceProvider resourceProvider, Context context) {
        this.mPlugin = resourceProvider;
    }

    public void onPluginDisconnected(ResourceProvider resourceProvider) {
        this.mPlugin = null;
    }

    public static ResourceProvider provider(Context context) {
        DynamicResource dynamicResource = INSTANCE.lambda$get$1$MainThreadInitializedObject(context);
        ResourceProvider resourceProvider = dynamicResource.mPlugin;
        return resourceProvider == null ? dynamicResource : resourceProvider;
    }
}
