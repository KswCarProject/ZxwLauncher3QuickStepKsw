package com.android.launcher3.icons;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import androidx.core.os.BuildCompat;
import com.android.launcher3.util.SafeCloseable;
import java.util.Calendar;
import java.util.function.Supplier;

public class IconProvider {
    public static final boolean ATLEAST_T = BuildCompat.isAtLeastT();
    private static final int CONFIG_ICON_MASK_RES_ID = Resources.getSystem().getIdentifier("config_icon_mask", "string", "android");
    private static final boolean DEBUG = false;
    private static final String ICON_METADATA_KEY_PREFIX = ".dynamic_icons";
    private static final String SYSTEM_STATE_SEPARATOR = " ";
    private static final String TAG = "IconProvider";
    private final String ACTION_OVERLAY_CHANGED = "android.intent.action.OVERLAY_CHANGED";
    /* access modifiers changed from: private */
    public final ComponentName mCalendar;
    /* access modifiers changed from: private */
    public final ComponentName mClock;
    protected final Context mContext;

    public interface IconChangeListener {
        void onAppIconChanged(String str, UserHandle userHandle);

        void onSystemIconStateChanged(String str);
    }

    /* access modifiers changed from: protected */
    public ThemeData getThemeDataForPackage(String str) {
        return null;
    }

    public IconProvider(Context context) {
        this.mContext = context;
        this.mCalendar = parseComponentOrNull(context, R.string.calendar_component_name);
        this.mClock = parseComponentOrNull(context, R.string.clock_component_name);
    }

    public String getSystemStateForPackage(String str, String str2) {
        ComponentName componentName = this.mCalendar;
        return (componentName == null || !componentName.getPackageName().equals(str2)) ? str : str + SYSTEM_STATE_SEPARATOR + getDay();
    }

    public Drawable getIcon(LauncherActivityInfo launcherActivityInfo, int i) {
        return getIconWithOverrides(launcherActivityInfo.getApplicationInfo().packageName, i, new Supplier(launcherActivityInfo, i) {
            public final /* synthetic */ LauncherActivityInfo f$0;
            public final /* synthetic */ int f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final Object get() {
                return this.f$0.getIcon(this.f$1);
            }
        });
    }

    public Drawable getIcon(ActivityInfo activityInfo) {
        return getIcon(activityInfo, this.mContext.getResources().getConfiguration().densityDpi);
    }

    public Drawable getIcon(ActivityInfo activityInfo, int i) {
        return getIconWithOverrides(activityInfo.applicationInfo.packageName, i, new Supplier(activityInfo, i) {
            public final /* synthetic */ ActivityInfo f$1;
            public final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final Object get() {
                return IconProvider.this.lambda$getIcon$1$IconProvider(this.f$1, this.f$2);
            }
        });
    }

    private Drawable getIconWithOverrides(String str, int i, Supplier<Drawable> supplier) {
        Drawable drawable;
        ThemeData themeDataForPackage = getThemeDataForPackage(str);
        ComponentName componentName = this.mCalendar;
        if (componentName == null || !componentName.getPackageName().equals(str)) {
            ComponentName componentName2 = this.mClock;
            drawable = (componentName2 == null || !componentName2.getPackageName().equals(str)) ? null : ClockDrawableWrapper.forPackage(this.mContext, this.mClock.getPackageName(), i, themeDataForPackage);
        } else {
            drawable = loadCalendarDrawable(i, themeDataForPackage);
        }
        if (drawable != null) {
            return drawable;
        }
        Drawable drawable2 = supplier.get();
        if (!ATLEAST_T || !(drawable2 instanceof AdaptiveIconDrawable) || themeDataForPackage == null) {
            return drawable2;
        }
        AdaptiveIconDrawable adaptiveIconDrawable = (AdaptiveIconDrawable) drawable2;
        return adaptiveIconDrawable.getMonochrome() == null ? new AdaptiveIconDrawable(adaptiveIconDrawable.getBackground(), adaptiveIconDrawable.getForeground(), themeDataForPackage.loadPaddedDrawable()) : drawable2;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:10:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x001c  */
    /* renamed from: loadActivityInfoIcon */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.graphics.drawable.Drawable lambda$getIcon$1$IconProvider(android.content.pm.ActivityInfo r4, int r5) {
        /*
            r3 = this;
            int r0 = r4.getIconResource()
            if (r5 == 0) goto L_0x0019
            if (r0 == 0) goto L_0x0019
            android.content.Context r1 = r3.mContext     // Catch:{ NameNotFoundException | NotFoundException -> 0x0019 }
            android.content.pm.PackageManager r1 = r1.getPackageManager()     // Catch:{ NameNotFoundException | NotFoundException -> 0x0019 }
            android.content.pm.ApplicationInfo r2 = r4.applicationInfo     // Catch:{ NameNotFoundException | NotFoundException -> 0x0019 }
            android.content.res.Resources r1 = r1.getResourcesForApplication(r2)     // Catch:{ NameNotFoundException | NotFoundException -> 0x0019 }
            android.graphics.drawable.Drawable r5 = r1.getDrawableForDensity(r0, r5)     // Catch:{ NameNotFoundException | NotFoundException -> 0x0019 }
            goto L_0x001a
        L_0x0019:
            r5 = 0
        L_0x001a:
            if (r5 != 0) goto L_0x0026
            android.content.Context r5 = r3.mContext
            android.content.pm.PackageManager r5 = r5.getPackageManager()
            android.graphics.drawable.Drawable r5 = r4.loadIcon(r5)
        L_0x0026:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.icons.IconProvider.lambda$getIcon$1$IconProvider(android.content.pm.ActivityInfo, int):android.graphics.drawable.Drawable");
    }

    private Drawable loadCalendarDrawable(int i, ThemeData themeData) {
        PackageManager packageManager = this.mContext.getPackageManager();
        try {
            Bundle bundle = packageManager.getActivityInfo(this.mCalendar, 8320).metaData;
            Resources resourcesForApplication = packageManager.getResourcesForApplication(this.mCalendar.getPackageName());
            int dynamicIconId = getDynamicIconId(bundle, resourcesForApplication);
            if (dynamicIconId != 0) {
                Drawable drawableForDensity = resourcesForApplication.getDrawableForDensity(dynamicIconId, i, (Resources.Theme) null);
                if (!ATLEAST_T || !(drawableForDensity instanceof AdaptiveIconDrawable) || themeData == null) {
                    return drawableForDensity;
                }
                AdaptiveIconDrawable adaptiveIconDrawable = (AdaptiveIconDrawable) drawableForDensity;
                if (adaptiveIconDrawable.getMonochrome() != null || !"array".equals(themeData.mResources.getResourceTypeName(themeData.mResID))) {
                    return drawableForDensity;
                }
                TypedArray obtainTypedArray = themeData.mResources.obtainTypedArray(themeData.mResID);
                int resourceId = obtainTypedArray.getResourceId(getDay(), 0);
                obtainTypedArray.recycle();
                return resourceId == 0 ? drawableForDensity : new AdaptiveIconDrawable(adaptiveIconDrawable.getBackground(), adaptiveIconDrawable.getForeground(), new ThemeData(themeData.mResources, resourceId).loadPaddedDrawable());
            }
        } catch (PackageManager.NameNotFoundException unused) {
        }
        return null;
    }

    private int getDynamicIconId(Bundle bundle, Resources resources) {
        int i;
        if (bundle == null || (i = bundle.getInt(this.mCalendar.getPackageName() + ICON_METADATA_KEY_PREFIX, 0)) == 0) {
            return 0;
        }
        try {
            return resources.obtainTypedArray(i).getResourceId(getDay(), 0);
        } catch (Resources.NotFoundException unused) {
            return 0;
        }
    }

    private static int getDay() {
        return Calendar.getInstance().get(5) - 1;
    }

    private static ComponentName parseComponentOrNull(Context context, int i) {
        String string = context.getString(i);
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        return ComponentName.unflattenFromString(string);
    }

    public String getSystemIconState() {
        int i = CONFIG_ICON_MASK_RES_ID;
        if (i == 0) {
            return "";
        }
        return this.mContext.getResources().getString(i);
    }

    public SafeCloseable registerIconChangeListener(IconChangeListener iconChangeListener, Handler handler) {
        return new IconChangeReceiver(iconChangeListener, handler);
    }

    public static class ThemeData {
        final int mResID;
        final Resources mResources;

        public ThemeData(Resources resources, int i) {
            this.mResources = resources;
            this.mResID = i;
        }

        /* access modifiers changed from: package-private */
        public Drawable loadPaddedDrawable() {
            if (!"drawable".equals(this.mResources.getResourceTypeName(this.mResID))) {
                return null;
            }
            return new InsetDrawable(new InsetDrawable(this.mResources.getDrawable(this.mResID).mutate(), 0.2f), AdaptiveIconDrawable.getExtraInsetFraction() / ((AdaptiveIconDrawable.getExtraInsetFraction() * 2.0f) + 1.0f));
        }
    }

    private class IconChangeReceiver extends BroadcastReceiver implements SafeCloseable {
        private final IconChangeListener mCallback;
        private String mIconState;

        IconChangeReceiver(IconChangeListener iconChangeListener, Handler handler) {
            this.mCallback = iconChangeListener;
            this.mIconState = IconProvider.this.getSystemIconState();
            IntentFilter intentFilter = new IntentFilter("android.intent.action.OVERLAY_CHANGED");
            intentFilter.addDataScheme("package");
            intentFilter.addDataSchemeSpecificPart("android", 0);
            IconProvider.this.mContext.registerReceiver(this, intentFilter, (String) null, handler);
            if (IconProvider.this.mCalendar != null || IconProvider.this.mClock != null) {
                IntentFilter intentFilter2 = new IntentFilter("android.intent.action.TIMEZONE_CHANGED");
                if (IconProvider.this.mCalendar != null) {
                    intentFilter2.addAction("android.intent.action.TIME_SET");
                    intentFilter2.addAction("android.intent.action.DATE_CHANGED");
                }
                IconProvider.this.mContext.registerReceiver(this, intentFilter2, (String) null, handler);
            }
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            action.hashCode();
            char c = 65535;
            switch (action.hashCode()) {
                case -1946981856:
                    if (action.equals("android.intent.action.OVERLAY_CHANGED")) {
                        c = 0;
                        break;
                    }
                    break;
                case 502473491:
                    if (action.equals("android.intent.action.TIMEZONE_CHANGED")) {
                        c = 1;
                        break;
                    }
                    break;
                case 505380757:
                    if (action.equals("android.intent.action.TIME_SET")) {
                        c = 2;
                        break;
                    }
                    break;
                case 1041332296:
                    if (action.equals("android.intent.action.DATE_CHANGED")) {
                        c = 3;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    String systemIconState = IconProvider.this.getSystemIconState();
                    if (!this.mIconState.equals(systemIconState)) {
                        this.mIconState = systemIconState;
                        this.mCallback.onSystemIconStateChanged(systemIconState);
                        return;
                    }
                    return;
                case 1:
                    if (IconProvider.this.mClock != null) {
                        this.mCallback.onAppIconChanged(IconProvider.this.mClock.getPackageName(), Process.myUserHandle());
                        break;
                    }
                    break;
                case 2:
                case 3:
                    break;
                default:
                    return;
            }
            if (IconProvider.this.mCalendar != null) {
                for (UserHandle onAppIconChanged : ((UserManager) context.getSystemService(UserManager.class)).getUserProfiles()) {
                    this.mCallback.onAppIconChanged(IconProvider.this.mCalendar.getPackageName(), onAppIconChanged);
                }
            }
        }

        public void close() {
            IconProvider.this.mContext.unregisterReceiver(this);
        }
    }
}
