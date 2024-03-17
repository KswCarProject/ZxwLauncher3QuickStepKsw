package com.android.launcher3.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import com.android.launcher3.pm.UserCache;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.widget.custom.CustomWidgetManager;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WidgetManagerHelper {
    public static final String WIDGET_OPTION_RESTORE_COMPLETED = "appWidgetRestoreCompleted";
    final AppWidgetManager mAppWidgetManager;
    final Context mContext;

    public WidgetManagerHelper(Context context) {
        this.mContext = context;
        this.mAppWidgetManager = AppWidgetManager.getInstance(context);
    }

    public LauncherAppWidgetProviderInfo getLauncherAppWidgetInfo(int i) {
        if (i <= -100) {
            return CustomWidgetManager.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).getWidgetProvider(i);
        }
        AppWidgetProviderInfo appWidgetInfo = this.mAppWidgetManager.getAppWidgetInfo(i);
        if (appWidgetInfo == null) {
            return null;
        }
        return LauncherAppWidgetProviderInfo.fromProviderInfo(this.mContext, appWidgetInfo);
    }

    public List<AppWidgetProviderInfo> getAllProviders(PackageUserKey packageUserKey) {
        if (packageUserKey == null) {
            return (List) allWidgetsSteam(this.mContext).collect(Collectors.toList());
        }
        return this.mAppWidgetManager.getInstalledProvidersForPackage(packageUserKey.mPackageName, packageUserKey.mUser);
    }

    public boolean bindAppWidgetIdIfAllowed(int i, AppWidgetProviderInfo appWidgetProviderInfo, Bundle bundle) {
        if (i <= -100) {
            return true;
        }
        return this.mAppWidgetManager.bindAppWidgetIdIfAllowed(i, appWidgetProviderInfo.getProfile(), appWidgetProviderInfo.provider, bundle);
    }

    public LauncherAppWidgetProviderInfo findProvider(ComponentName componentName, UserHandle userHandle) {
        for (AppWidgetProviderInfo next : getAllProviders(new PackageUserKey(componentName.getPackageName(), userHandle))) {
            if (next.provider.equals(componentName)) {
                return LauncherAppWidgetProviderInfo.fromProviderInfo(this.mContext, next);
            }
        }
        return null;
    }

    public boolean isAppWidgetRestored(int i) {
        return this.mAppWidgetManager.getAppWidgetOptions(i).getBoolean(WIDGET_OPTION_RESTORE_COMPLETED);
    }

    private static Stream<AppWidgetProviderInfo> allWidgetsSteam(Context context) {
        return Stream.concat(UserCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).getUserProfiles().stream().flatMap(new Function((AppWidgetManager) context.getSystemService(AppWidgetManager.class)) {
            public final /* synthetic */ AppWidgetManager f$0;

            {
                this.f$0 = r1;
            }

            public final Object apply(Object obj) {
                return this.f$0.getInstalledProvidersForProfile((UserHandle) obj).stream();
            }
        }), CustomWidgetManager.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).stream());
    }
}
