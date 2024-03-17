package com.android.launcher3;

import android.app.ActivityManager;
import android.app.Person;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ShortcutInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.Process;
import android.os.TransactionTooLargeException;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TtsSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import androidx.core.graphics.ColorUtils;
import com.android.launcher3.dragndrop.FolderAdaptiveIcon;
import com.android.launcher3.graphics.GridCustomizationsProvider;
import com.android.launcher3.graphics.TintedDrawableSpan;
import com.android.launcher3.icons.ThemedIconDrawable;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.ItemInfoWithIcon;
import com.android.launcher3.util.IntArray;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.launcher3.util.SplitConfigurationOptions;
import com.android.launcher3.util.Themes;
import com.android.launcher3.views.BaseDragLayer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class Utilities {
    public static final boolean ATLEAST_P = (Build.VERSION.SDK_INT >= 28);
    public static final boolean ATLEAST_Q = (Build.VERSION.SDK_INT >= 29);
    public static final boolean ATLEAST_R = (Build.VERSION.SDK_INT >= 30);
    public static final boolean ATLEAST_S = (Build.VERSION.SDK_INT >= 31);
    public static final boolean ATLEAST_T = (Build.VERSION.SDK_INT >= 33);
    public static final int EDGE_NAV_BAR = 256;
    public static final Person[] EMPTY_PERSON_ARRAY = new Person[0];
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    public static final String EXTRA_WALLPAPER_FLAVOR = "com.android.launcher3.WALLPAPER_FLAVOR";
    public static final String EXTRA_WALLPAPER_LAUNCH_SOURCE = "com.android.wallpaper.LAUNCH_SOURCE";
    public static final String EXTRA_WALLPAPER_OFFSET = "com.android.launcher3.WALLPAPER_OFFSET";
    public static final boolean IS_DEBUG_DEVICE;
    public static boolean IS_RUNNING_IN_TEST_HARNESS = ActivityManager.isRunningInTestHarness();
    private static final String TAG = "Launcher.Utilities";
    private static final Matrix sInverseMatrix = new Matrix();
    private static final int[] sLoc0 = new int[2];
    private static final int[] sLoc1 = new int[2];
    private static final Matrix sMatrix = new Matrix();
    private static final Pattern sTrimPattern = Pattern.compile("^[\\s|\\p{javaSpaceChar}]*(.*)[\\s|\\p{javaSpaceChar}]*$");

    public static float comp(float f) {
        return 1.0f - f;
    }

    public static float dpiFromPx(float f, int i) {
        return f / (((float) i) / 160.0f);
    }

    public static float mapRange(float f, float f2, float f3) {
        return f2 + (f * (f3 - f2));
    }

    public static float squaredHypot(float f, float f2) {
        return (f * f) + (f2 * f2);
    }

    static {
        boolean z = false;
        if (Build.TYPE.toLowerCase(Locale.ROOT).contains("debug") || Build.TYPE.toLowerCase(Locale.ROOT).equals("eng")) {
            z = true;
        }
        IS_DEBUG_DEVICE = z;
    }

    public static boolean isDarkTheme(Context context) {
        return (context.getResources().getConfiguration().uiMode & 48) == 32;
    }

    public static boolean isDevelopersOptionsEnabled(Context context) {
        return Settings.Global.getInt(context.getApplicationContext().getContentResolver(), "development_settings_enabled", 0) != 0;
    }

    public static void enableRunningInTestHarnessForTests() {
        IS_RUNNING_IN_TEST_HARNESS = true;
    }

    public static boolean isPropertyEnabled(String str) {
        return Log.isLoggable(str, 2);
    }

    public static boolean existsStyleWallpapers(Context context) {
        if (context.getPackageManager().resolveActivity(PackageManagerHelper.getStyleWallpapersIntent(context), 0) != null) {
            return true;
        }
        return false;
    }

    public static float getDescendantCoordRelativeToAncestor(View view, View view2, float[] fArr, boolean z) {
        return getDescendantCoordRelativeToAncestor(view, view2, fArr, z, false);
    }

    public static float getDescendantCoordRelativeToAncestor(View view, View view2, float[] fArr, boolean z, boolean z2) {
        float f = 1.0f;
        View view3 = view;
        while (view3 != view2 && view3 != null) {
            if (view3 != view || z) {
                offsetPoints(fArr, (float) (-view3.getScrollX()), (float) (-view3.getScrollY()));
            }
            if (!z2) {
                view3.getMatrix().mapPoints(fArr);
            }
            offsetPoints(fArr, (float) view3.getLeft(), (float) view3.getTop());
            f *= view3.getScaleX();
            view3 = view3.getParent() instanceof View ? (View) view3.getParent() : null;
        }
        return f;
    }

    public static void getBoundsForViewInDragLayer(BaseDragLayer baseDragLayer, View view, Rect rect, boolean z, float[] fArr, RectF rectF) {
        if (fArr == null) {
            fArr = new float[4];
        }
        fArr[0] = (float) rect.left;
        fArr[1] = (float) rect.top;
        fArr[2] = (float) rect.right;
        fArr[3] = (float) rect.bottom;
        getDescendantCoordRelativeToAncestor(view, baseDragLayer, fArr, false, z);
        rectF.set(Math.min(fArr[0], fArr[2]), Math.min(fArr[1], fArr[3]), Math.max(fArr[0], fArr[2]), Math.max(fArr[1], fArr[3]));
    }

    public static void mapRectInSelfToDescendant(View view, View view2, Rect rect) {
        float[] fArr = {(float) rect.left, (float) rect.top, (float) rect.right, (float) rect.bottom};
        mapCoordInSelfToDescendant(view, view2, fArr);
        rect.set((int) fArr[0], (int) fArr[1], (int) fArr[2], (int) fArr[3]);
    }

    public static void mapCoordInSelfToDescendant(View view, View view2, float[] fArr) {
        sMatrix.reset();
        while (view != view2) {
            Matrix matrix = sMatrix;
            matrix.postTranslate((float) (-view.getScrollX()), (float) (-view.getScrollY()));
            matrix.postConcat(view.getMatrix());
            matrix.postTranslate((float) view.getLeft(), (float) view.getTop());
            view = (View) view.getParent();
        }
        Matrix matrix2 = sMatrix;
        matrix2.postTranslate((float) (-view.getScrollX()), (float) (-view.getScrollY()));
        Matrix matrix3 = sInverseMatrix;
        matrix2.invert(matrix3);
        matrix3.mapPoints(fArr);
    }

    public static void roundArray(float[] fArr, int[] iArr) {
        for (int i = 0; i < fArr.length; i++) {
            iArr[i] = Math.round(fArr[i]);
        }
    }

    public static void offsetPoints(float[] fArr, float f, float f2) {
        for (int i = 0; i < fArr.length; i += 2) {
            fArr[i] = fArr[i] + f;
            int i2 = i + 1;
            fArr[i2] = fArr[i2] + f2;
        }
    }

    public static boolean pointInView(View view, float f, float f2, float f3) {
        float f4 = -f3;
        return f >= f4 && f2 >= f4 && f < ((float) view.getWidth()) + f3 && f2 < ((float) view.getHeight()) + f3;
    }

    public static int[] getCenterDeltaInScreenSpace(View view, View view2) {
        int[] iArr = sLoc0;
        view.getLocationInWindow(iArr);
        int[] iArr2 = sLoc1;
        view2.getLocationInWindow(iArr2);
        iArr[0] = (int) (((float) iArr[0]) + ((((float) view.getMeasuredWidth()) * view.getScaleX()) / 2.0f));
        iArr[1] = (int) (((float) iArr[1]) + ((((float) view.getMeasuredHeight()) * view.getScaleY()) / 2.0f));
        iArr2[0] = (int) (((float) iArr2[0]) + ((((float) view2.getMeasuredWidth()) * view2.getScaleX()) / 2.0f));
        iArr2[1] = (int) (((float) iArr2[1]) + ((((float) view2.getMeasuredHeight()) * view2.getScaleY()) / 2.0f));
        return new int[]{iArr2[0] - iArr[0], iArr2[1] - iArr[1]};
    }

    public static void setRect(RectF rectF, Rect rect) {
        rect.left = (int) rectF.left;
        rect.top = (int) rectF.top;
        rect.right = (int) rectF.right;
        rect.bottom = (int) rectF.bottom;
    }

    public static void scaleRectFAboutCenter(RectF rectF, float f) {
        scaleRectFAboutPivot(rectF, f, rectF.centerX(), rectF.centerY());
    }

    public static void scaleRectFAboutPivot(RectF rectF, float f, float f2, float f3) {
        if (f != 1.0f) {
            rectF.offset(-f2, -f3);
            rectF.left *= f;
            rectF.top *= f;
            rectF.right *= f;
            rectF.bottom *= f;
            rectF.offset(f2, f3);
        }
    }

    public static void scaleRectAboutCenter(Rect rect, float f) {
        if (f != 1.0f) {
            int centerX = rect.centerX();
            int centerY = rect.centerY();
            rect.offset(-centerX, -centerY);
            scaleRect(rect, f);
            rect.offset(centerX, centerY);
        }
    }

    public static void scaleRect(Rect rect, float f) {
        if (f != 1.0f) {
            rect.left = (int) ((((float) rect.left) * f) + 0.5f);
            rect.top = (int) ((((float) rect.top) * f) + 0.5f);
            rect.right = (int) ((((float) rect.right) * f) + 0.5f);
            rect.bottom = (int) ((((float) rect.bottom) * f) + 0.5f);
        }
    }

    public static void insetRect(Rect rect, Rect rect2) {
        rect.left = Math.min(rect.right, rect.left + rect2.left);
        rect.top = Math.min(rect.bottom, rect.top + rect2.top);
        rect.right = Math.max(rect.left, rect.right - rect2.right);
        rect.bottom = Math.max(rect.top, rect.bottom - rect2.bottom);
    }

    public static float shrinkRect(Rect rect, float f, float f2) {
        float min = Math.min(Math.min(f, f2), 1.0f);
        if (min < 1.0f) {
            int width = (int) (((float) rect.width()) * (f - min) * 0.5f);
            rect.left += width;
            rect.right -= width;
            int height = (int) (((float) rect.height()) * (f2 - min) * 0.5f);
            rect.top += height;
            rect.bottom -= height;
        }
        return min;
    }

    public static void scaleRectFAboutCenter(RectF rectF, float f, float f2) {
        float centerX = rectF.centerX();
        float centerY = rectF.centerY();
        rectF.offset(-centerX, -centerY);
        rectF.left *= f;
        rectF.top *= f2;
        rectF.right *= f;
        rectF.bottom *= f2;
        rectF.offset(centerX, centerY);
    }

    public static float mapToRange(float f, float f2, float f3, float f4, float f5, Interpolator interpolator) {
        if (f2 != f3 && f4 != f5) {
            return mapRange(interpolator.getInterpolation(getProgress(f, f2, f3)), f4, f5);
        }
        Log.e(TAG, "mapToRange: range has 0 length");
        return f4;
    }

    public static float mapBoundToRange(float f, float f2, float f3, float f4, float f5, Interpolator interpolator) {
        return mapToRange(boundToRange(f, f2, f3), f2, f3, f4, f5, interpolator);
    }

    public static float getProgress(float f, float f2, float f3) {
        return Math.abs(f - f2) / Math.abs(f3 - f2);
    }

    public static float saturate(float f) {
        return boundToRange(f, 0.0f, 1.0f);
    }

    public static float or(float f, float f2) {
        float saturate = saturate(f);
        float saturate2 = saturate(f2);
        return (saturate + saturate2) - (saturate * saturate2);
    }

    public static String trim(CharSequence charSequence) {
        if (charSequence == null) {
            return "";
        }
        return sTrimPattern.matcher(charSequence).replaceAll("$1");
    }

    public static int calculateTextHeight(float f) {
        Paint paint = new Paint();
        paint.setTextSize(f);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return (int) Math.ceil((double) (fontMetrics.bottom - fontMetrics.top));
    }

    public static boolean isRtl(Resources resources) {
        return resources.getConfiguration().getLayoutDirection() == 1;
    }

    public static float pxToSp(float f) {
        return f / Resources.getSystem().getDisplayMetrics().scaledDensity;
    }

    public static int dpToPx(float f) {
        return (int) (f * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxFromSp(float f, DisplayMetrics displayMetrics) {
        return pxFromSp(f, displayMetrics, 1.0f);
    }

    public static int pxFromSp(float f, DisplayMetrics displayMetrics, float f2) {
        return Math.round(TypedValue.applyDimension(2, f, displayMetrics) * f2);
    }

    public static String createDbSelectionQuery(String str, IntArray intArray) {
        return String.format(Locale.ENGLISH, "%s IN (%s)", new Object[]{str, intArray.toConcatString()});
    }

    public static boolean isBootCompleted() {
        return "1".equals(getSystemProperty("sys.boot_completed", "1"));
    }

    public static String getSystemProperty(String str, String str2) {
        try {
            String str3 = (String) Class.forName("android.os.SystemProperties").getDeclaredMethod("get", new Class[]{String.class}).invoke((Object) null, new Object[]{str});
            if (!TextUtils.isEmpty(str3)) {
                return str3;
            }
            return str2;
        } catch (Exception unused) {
            Log.d(TAG, "Unable to read system properties");
        }
    }

    public static void setRectToViewCenter(View view, int i, Rect rect) {
        int height = (view.getHeight() - i) / 2;
        int width = (view.getWidth() - i) / 2;
        rect.set(width, height, width + i, i + height);
    }

    public static int boundToRange(int i, int i2, int i3) {
        return Math.max(i2, Math.min(i, i3));
    }

    public static float boundToRange(float f, float f2, float f3) {
        return Math.max(f2, Math.min(f, f3));
    }

    public static long boundToRange(long j, long j2, long j3) {
        return Math.max(j2, Math.min(j, j3));
    }

    public static Intent createHomeIntent() {
        return new Intent("android.intent.action.MAIN").addCategory("android.intent.category.HOME").setFlags(268435456);
    }

    public static CharSequence wrapForTts(CharSequence charSequence, String str) {
        SpannableString spannableString = new SpannableString(charSequence);
        spannableString.setSpan(new TtsSpan.TextBuilder(str).build(), 0, spannableString.length(), 18);
        return spannableString;
    }

    public static CharSequence prefixTextWithIcon(Context context, int i, CharSequence charSequence) {
        SpannableString spannableString = new SpannableString("  " + charSequence);
        spannableString.setSpan(new TintedDrawableSpan(context, i), 0, 1, 34);
        return spannableString;
    }

    public static SharedPreferences getPrefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(LauncherFiles.SHARED_PREFERENCES_KEY, 0);
    }

    public static SharedPreferences getDevicePrefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(LauncherFiles.DEVICE_PREFERENCES_KEY, 0);
    }

    public static boolean isWallpaperSupported(Context context) {
        return ((WallpaperManager) context.getSystemService(WallpaperManager.class)).isWallpaperSupported();
    }

    public static boolean isWallpaperAllowed(Context context) {
        return ((WallpaperManager) context.getSystemService(WallpaperManager.class)).isSetWallpaperAllowed();
    }

    public static boolean isBinderSizeError(Exception exc) {
        return (exc.getCause() instanceof TransactionTooLargeException) || (exc.getCause() instanceof DeadObjectException);
    }

    public static boolean isGridOptionsEnabled(Context context) {
        return isComponentEnabled(context.getPackageManager(), context.getPackageName(), GridCustomizationsProvider.class.getName());
    }

    private static boolean isComponentEnabled(PackageManager packageManager, String str, String str2) {
        int componentEnabledSetting = packageManager.getComponentEnabledSetting(new ComponentName(str, str2));
        if (componentEnabledSetting == 1) {
            return true;
        }
        if (componentEnabledSetting != 2) {
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(str, 520);
                if (packageInfo.providers != null) {
                    return Arrays.stream(packageInfo.providers).anyMatch(new Predicate(str2) {
                        public final /* synthetic */ String f$0;

                        {
                            this.f$0 = r1;
                        }

                        public final boolean test(Object obj) {
                            return Utilities.lambda$isComponentEnabled$0(this.f$0, (ProviderInfo) obj);
                        }
                    });
                }
            } catch (PackageManager.NameNotFoundException unused) {
            }
        }
        return false;
    }

    static /* synthetic */ boolean lambda$isComponentEnabled$0(String str, ProviderInfo providerInfo) {
        return providerInfo.name.equals(str) && providerInfo.isEnabled();
    }

    public static void postAsyncCallback(Handler handler, Runnable runnable) {
        Message obtain = Message.obtain(handler, runnable);
        obtain.setAsynchronous(true);
        handler.sendMessage(obtain);
    }

    public static void unregisterReceiverSafely(Context context, BroadcastReceiver broadcastReceiver) {
        try {
            context.unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException unused) {
        }
    }

    public static Drawable getFullDrawable(Context context, ItemInfo itemInfo, int i, int i2, boolean z, Object[] objArr) {
        Drawable monochrome;
        Drawable loadFullDrawableWithoutTheme = loadFullDrawableWithoutTheme(context, itemInfo, i, i2, objArr);
        if (!ATLEAST_T || !(loadFullDrawableWithoutTheme instanceof AdaptiveIconDrawable) || !z || (monochrome = ((AdaptiveIconDrawable) loadFullDrawableWithoutTheme.mutate()).getMonochrome()) == null || !Themes.isThemedIconEnabled(context)) {
            return loadFullDrawableWithoutTheme;
        }
        int[] colors = ThemedIconDrawable.getColors(context);
        Drawable mutate = monochrome.mutate();
        mutate.setTint(colors[1]);
        return new AdaptiveIconDrawable(new ColorDrawable(colors[0]), mutate);
    }

    /* JADX WARNING: type inference failed for: r10v0, types: [java.lang.Object[]] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static android.graphics.drawable.Drawable loadFullDrawableWithoutTheme(android.content.Context r6, com.android.launcher3.model.data.ItemInfo r7, int r8, int r9, java.lang.Object[] r10) {
        /*
            android.content.Context r0 = com.android.launcher3.views.ActivityContext.lookupContext(r6)
            com.android.launcher3.views.ActivityContext r0 = (com.android.launcher3.views.ActivityContext) r0
            com.android.launcher3.LauncherAppState r1 = com.android.launcher3.LauncherAppState.getInstance(r6)
            int r2 = r7.itemType
            r3 = 0
            r4 = 0
            if (r2 != 0) goto L_0x003c
            java.lang.Class<android.content.pm.LauncherApps> r8 = android.content.pm.LauncherApps.class
            java.lang.Object r8 = r6.getSystemService(r8)
            android.content.pm.LauncherApps r8 = (android.content.pm.LauncherApps) r8
            android.content.Intent r9 = r7.getIntent()
            android.os.UserHandle r7 = r7.user
            android.content.pm.LauncherActivityInfo r7 = r8.resolveActivity(r9, r7)
            r10[r4] = r7
            if (r7 != 0) goto L_0x0027
            goto L_0x003b
        L_0x0027:
            com.android.launcher3.LauncherAppState r6 = com.android.launcher3.LauncherAppState.getInstance(r6)
            com.android.launcher3.icons.IconProvider r6 = r6.getIconProvider()
            com.android.launcher3.DeviceProfile r8 = r0.getDeviceProfile()
            com.android.launcher3.InvariantDeviceProfile r8 = r8.inv
            int r8 = r8.fillResIconDpi
            android.graphics.drawable.Drawable r3 = r6.getIcon((android.content.pm.LauncherActivityInfo) r7, (int) r8)
        L_0x003b:
            return r3
        L_0x003c:
            int r2 = r7.itemType
            r5 = 6
            if (r2 != r5) goto L_0x0080
            boolean r8 = r7 instanceof com.android.launcher3.widget.PendingAddShortcutInfo
            if (r8 == 0) goto L_0x0054
            com.android.launcher3.widget.PendingAddShortcutInfo r7 = (com.android.launcher3.widget.PendingAddShortcutInfo) r7
            com.android.launcher3.pm.ShortcutConfigActivityInfo r6 = r7.activityInfo
            r10[r4] = r6
            com.android.launcher3.icons.IconCache r7 = r1.getIconCache()
            android.graphics.drawable.Drawable r6 = r6.getFullResIcon(r7)
            return r6
        L_0x0054:
            com.android.launcher3.shortcuts.ShortcutKey r7 = com.android.launcher3.shortcuts.ShortcutKey.fromItemInfo(r7)
            com.android.launcher3.shortcuts.ShortcutRequest r7 = r7.buildRequest(r6)
            r8 = 11
            com.android.launcher3.shortcuts.ShortcutRequest$QueryResult r7 = r7.query(r8)
            boolean r8 = r7.isEmpty()
            if (r8 == 0) goto L_0x0069
            return r3
        L_0x0069:
            java.lang.Object r8 = r7.get(r4)
            r10[r4] = r8
            java.lang.Object r7 = r7.get(r4)
            android.content.pm.ShortcutInfo r7 = (android.content.pm.ShortcutInfo) r7
            com.android.launcher3.InvariantDeviceProfile r8 = r1.getInvariantDeviceProfile()
            int r8 = r8.fillResIconDpi
            android.graphics.drawable.Drawable r6 = com.android.launcher3.icons.ShortcutCachingLogic.getIcon(r6, r7, r8)
            return r6
        L_0x0080:
            int r1 = r7.itemType
            r2 = 2
            if (r1 != r2) goto L_0x0096
            int r6 = r7.id
            android.graphics.Point r7 = new android.graphics.Point
            r7.<init>(r8, r9)
            com.android.launcher3.dragndrop.FolderAdaptiveIcon r6 = com.android.launcher3.dragndrop.FolderAdaptiveIcon.createFolderAdaptiveIcon(r0, r6, r7)
            if (r6 != 0) goto L_0x0093
            return r3
        L_0x0093:
            r10[r4] = r6
            return r6
        L_0x0096:
            int r8 = r7.itemType
            r9 = 7
            if (r8 != r9) goto L_0x00a8
            boolean r8 = r7 instanceof com.android.launcher3.model.data.SearchActionItemInfo
            if (r8 == 0) goto L_0x00a8
            com.android.launcher3.model.data.SearchActionItemInfo r7 = (com.android.launcher3.model.data.SearchActionItemInfo) r7
            com.android.launcher3.icons.BitmapInfo r7 = r7.bitmap
            com.android.launcher3.icons.FastBitmapDrawable r6 = r7.newIcon(r6)
            return r6
        L_0x00a8:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.Utilities.loadFullDrawableWithoutTheme(android.content.Context, com.android.launcher3.model.data.ItemInfo, int, int, java.lang.Object[]):android.graphics.drawable.Drawable");
    }

    public static Drawable getBadge(Context context, ItemInfo itemInfo, Object obj) {
        LauncherAppState instance = LauncherAppState.getInstance(context);
        if (itemInfo.itemType == 6) {
            boolean z = (itemInfo instanceof ItemInfoWithIcon) && (((ItemInfoWithIcon) itemInfo).runtimeStatusFlags & 512) > 0;
            if ((itemInfo.id == -1 && !z) || !(obj instanceof ShortcutInfo)) {
                return new ColorDrawable(0);
            }
            return LauncherAppState.getInstance(instance.getContext()).getIconCache().getShortcutInfoBadge((ShortcutInfo) obj).newIcon(context, 1);
        } else if (itemInfo.itemType == 2) {
            return ((FolderAdaptiveIcon) obj).getBadge();
        } else {
            if (Process.myUserHandle().equals(itemInfo.user)) {
                return new ColorDrawable(0);
            }
            return context.getDrawable(R.drawable.ic_work_app_badge);
        }
    }

    public static boolean isValidExtraType(Intent intent, String str, Class cls) {
        Parcelable parcelableExtra = intent.getParcelableExtra(str);
        return parcelableExtra == null || cls.isInstance(parcelableExtra);
    }

    public static float squaredTouchSlop(Context context) {
        float scaledTouchSlop = (float) ViewConfiguration.get(context).getScaledTouchSlop();
        return scaledTouchSlop * scaledTouchSlop;
    }

    public static ContentObserver newContentObserver(Handler handler, final Consumer<Uri> consumer) {
        return new ContentObserver(handler) {
            public void onChange(boolean z, Uri uri) {
                consumer.accept(uri);
            }
        };
    }

    public static boolean isRelativePercentDifferenceGreaterThan(float f, float f2, float f3) {
        return Math.abs(f - f2) / Math.abs((f + f2) / 2.0f) > f3;
    }

    public static void rotateBounds(Rect rect, int i, int i2, int i3) {
        int i4 = ((i3 % 4) + 4) % 4;
        int i5 = rect.left;
        if (i4 == 1) {
            rect.left = rect.top;
            rect.top = i - rect.right;
            rect.right = rect.bottom;
            rect.bottom = i - i5;
        } else if (i4 == 2) {
            rect.left = i - rect.right;
            rect.right = i - i5;
        } else if (i4 == 3) {
            rect.left = i2 - rect.bottom;
            rect.bottom = rect.right;
            rect.right = i2 - rect.top;
            rect.top = i5;
        }
    }

    public static ColorFilter makeColorTintingColorFilter(int i, float f) {
        if (f == 0.0f) {
            return null;
        }
        return new LightingColorFilter(ColorUtils.blendARGB(-1, 0, f), ColorUtils.blendARGB(0, i, f));
    }

    public static void setStartMarginForView(View view, int i) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        layoutParams.setMarginStart(i);
        view.setLayoutParams(layoutParams);
    }

    public static Rect getViewBounds(View view) {
        int[] iArr = new int[2];
        view.getLocationOnScreen(iArr);
        return new Rect(iArr[0], iArr[1], iArr[0] + view.getWidth(), iArr[1] + view.getHeight());
    }

    public static List<SplitConfigurationOptions.SplitPositionOption> getSplitPositionOptions(DeviceProfile deviceProfile) {
        ArrayList arrayList = new ArrayList();
        Log.d("Utilties", "isTablet = " + deviceProfile.isTablet + ", isLandscape = " + deviceProfile.isLandscape);
        arrayList.add(new SplitConfigurationOptions.SplitPositionOption(R.drawable.ic_split_left, R.string.split_screen_position_left, 0, 0));
        arrayList.add(new SplitConfigurationOptions.SplitPositionOption(R.drawable.ic_split_right, R.string.split_screen_position_right, 1, 0));
        return arrayList;
    }
}
