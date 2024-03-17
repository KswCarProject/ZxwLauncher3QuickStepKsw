package com.android.launcher3;

import android.appwidget.AppWidgetHostView;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.util.Xml;
import android.view.Display;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.model.DeviceGridState;
import com.android.launcher3.provider.RestoreDbTask;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.IntArray;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.launcher3.util.Themes;
import com.android.launcher3.util.WindowBounds;
import com.android.launcher3.util.window.WindowManagerProxy;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.ToIntFunction;
import org.xmlpull.v1.XmlPullParserException;

public class InvariantDeviceProfile {
    static final int COUNT_SIZES = 4;
    private static final float ICON_SIZE_DEFINED_IN_APP_DP = 48.0f;
    static final int INDEX_DEFAULT = 0;
    static final int INDEX_LANDSCAPE = 1;
    static final int INDEX_TWO_PANEL_LANDSCAPE = 3;
    static final int INDEX_TWO_PANEL_PORTRAIT = 2;
    public static final MainThreadInitializedObject<InvariantDeviceProfile> INSTANCE = new MainThreadInitializedObject<>($$Lambda$InvariantDeviceProfile$DNcXzmawjoq65q3wgQi9M48DryY.INSTANCE);
    private static final String KEY_IDP_GRID_NAME = "idp_grid_name";
    private static final float KNEARESTNEIGHBOR = 3.0f;
    public static final String TAG = "IDP";
    public static final int TYPE_MULTI_DISPLAY = 1;
    public static final int TYPE_PHONE = 0;
    public static final int TYPE_TABLET = 2;
    private static final float WEIGHT_EFFICIENT = 100000.0f;
    private static final float WEIGHT_POWER = 5.0f;
    public PointF[] allAppsBorderSpaces;
    public PointF[] allAppsCellSize;
    public float[] allAppsIconSize;
    public float[] allAppsIconTextSize;
    public PointF[] borderSpaces;
    public String dbFile;
    public int defaultLayoutId;
    public Point defaultWallpaperSize;
    public Rect defaultWidgetPadding;
    int demoModeLayoutId;
    public int devicePaddingId;
    public DevicePaddings devicePaddings;
    public int deviceType;
    public int fillResIconDpi;
    public float folderBorderSpace;
    public float[] horizontalMargin;
    public float[] hotseatBorderSpaces;
    public int[] hotseatColumnSpan;
    public int iconBitmapSize;
    public float[] iconSize;
    public float[] iconTextSize;
    boolean[] inlineQsb = new boolean[4];
    protected boolean isScalable;
    private final ArrayList<OnIDPChangeListener> mChangeListeners = new ArrayList<>();
    private SparseArray<TypedValue> mExtraAttrs;
    public PointF[] minCellSize;
    public int numAllAppsColumns;
    public int numColumns;
    public int numDatabaseAllAppsColumns;
    public int numDatabaseHotseatIcons;
    public int numFolderColumns;
    public int numFolderRows;
    public int numRows;
    public int numSearchContainerColumns;
    public int numShownHotseatIcons;
    public int numShrunkenHotseatIcons;
    public List<DeviceProfile> supportedProfiles = Collections.EMPTY_LIST;

    @Retention(RetentionPolicy.SOURCE)
    public @interface DeviceType {
    }

    public interface OnIDPChangeListener {
        void onIdpChanged(boolean z);
    }

    public static /* synthetic */ InvariantDeviceProfile lambda$DNcXzmawjoq65q3wgQi9M48DryY(Context context) {
        return new InvariantDeviceProfile(context);
    }

    static /* synthetic */ int lambda$getDeviceType$2(int i, int i2) {
        return i | i2;
    }

    private static float wallpaperTravelToScreenWidthRatio(int i, int i2) {
        return ((((float) i) / ((float) i2)) * 0.30769226f) + 1.0076923f;
    }

    public InvariantDeviceProfile() {
    }

    private InvariantDeviceProfile(Context context) {
        String currentGridName = getCurrentGridName(context);
        String initGrid = initGrid(context, currentGridName);
        if (!initGrid.equals(currentGridName)) {
            Utilities.getPrefs(context).edit().putString(KEY_IDP_GRID_NAME, initGrid).apply();
        }
        new DeviceGridState(this).writeToPrefs(context);
        DisplayController.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).setPriorityListener(new DisplayController.DisplayInfoChangeListener() {
            public final void onDisplayInfoChanged(Context context, DisplayController.Info info, int i) {
                InvariantDeviceProfile.this.lambda$new$0$InvariantDeviceProfile(context, info, i);
            }
        });
    }

    public /* synthetic */ void lambda$new$0$InvariantDeviceProfile(Context context, DisplayController.Info info, int i) {
        if ((i & 28) != 0) {
            lambda$setCurrentGrid$3$InvariantDeviceProfile(context);
        }
    }

    public InvariantDeviceProfile(Context context, String str) {
        String initGrid = initGrid(context, str);
        if (initGrid == null || !initGrid.equals(str)) {
            throw new IllegalArgumentException("Unknown grid name");
        }
    }

    public InvariantDeviceProfile(Context context, Display display) {
        INSTANCE.lambda$get$1$MainThreadInitializedObject(context);
        String currentGridName = getCurrentGridName(context);
        DisplayController.Info info = DisplayController.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).getInfo();
        int deviceType2 = getDeviceType(info);
        DisplayOption invDistWeightedInterpolate = invDistWeightedInterpolate(info, getPredefinedDeviceProfiles(context, currentGridName, deviceType2, false), deviceType2);
        DisplayController.Info info2 = new DisplayController.Info(context, display);
        int deviceType3 = getDeviceType(info2);
        DisplayOption invDistWeightedInterpolate2 = invDistWeightedInterpolate(info2, getPredefinedDeviceProfiles(context, currentGridName, deviceType3, false), deviceType3);
        DisplayOption access$000 = new DisplayOption(invDistWeightedInterpolate.grid).add(invDistWeightedInterpolate2);
        access$000.iconSizes[0] = invDistWeightedInterpolate.iconSizes[0];
        for (int i = 1; i < 4; i++) {
            access$000.iconSizes[i] = Math.min(invDistWeightedInterpolate.iconSizes[i], invDistWeightedInterpolate2.iconSizes[i]);
        }
        System.arraycopy(invDistWeightedInterpolate.minCellSize, 0, access$000.minCellSize, 0, 4);
        System.arraycopy(invDistWeightedInterpolate.borderSpaces, 0, access$000.borderSpaces, 0, 4);
        System.arraycopy(invDistWeightedInterpolate.inlineQsb, 0, access$000.inlineQsb, 0, 4);
        initGrid(context, info2, access$000, deviceType3);
    }

    public void reinitializeAfterRestore(Context context) {
        String currentGridName = getCurrentGridName(context);
        String str = this.dbFile;
        String initGrid = initGrid(context, currentGridName);
        if (!this.dbFile.equals(str)) {
            Log.d(TAG, "Restored grid is disabled : " + currentGridName + ", migrating to: " + initGrid + ", removing all other grid db files");
            for (String next : LauncherFiles.GRID_DB_FILES) {
                if (!next.equals(str) && context.getDatabasePath(next).delete()) {
                    Log.d(TAG, "Removed old grid db file: " + next);
                }
            }
            setCurrentGrid(context, initGrid);
        }
    }

    private static int getDeviceType(DisplayController.Info info) {
        int reduce = info.supportedBounds.stream().mapToInt(new ToIntFunction(2, 1) {
            public final /* synthetic */ int f$1;
            public final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final int applyAsInt(Object obj) {
                return InvariantDeviceProfile.lambda$getDeviceType$1(DisplayController.Info.this, this.f$1, this.f$2, (WindowBounds) obj);
            }
        }).reduce(0, $$Lambda$InvariantDeviceProfile$5XGuvs29POe1AVmzGnVUg_0eDhE.INSTANCE);
        if (reduce == 3 && FeatureFlags.ENABLE_TWO_PANEL_HOME.get()) {
            return 1;
        }
        if (reduce == 2) {
            return 2;
        }
        return 0;
    }

    static /* synthetic */ int lambda$getDeviceType$1(DisplayController.Info info, int i, int i2, WindowBounds windowBounds) {
        return info.isTablet(windowBounds) ? i : i2;
    }

    public static String getCurrentGridName(Context context) {
        if (Utilities.isGridOptionsEnabled(context)) {
            return Utilities.getPrefs(context).getString(KEY_IDP_GRID_NAME, (String) null);
        }
        return null;
    }

    private String initGrid(Context context, String str) {
        DisplayController.Info info = DisplayController.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).getInfo();
        int deviceType2 = getDeviceType(info);
        DisplayOption invDistWeightedInterpolate = invDistWeightedInterpolate(info, getPredefinedDeviceProfiles(context, str, deviceType2, RestoreDbTask.isPending(context)), deviceType2);
        initGrid(context, info, invDistWeightedInterpolate, deviceType2);
        return invDistWeightedInterpolate.grid.name;
    }

    private void initGrid(Context context, DisplayController.Info info, DisplayOption displayOption, int i) {
        float f;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        GridOption gridOption = displayOption.grid;
        this.numRows = gridOption.numRows;
        this.numColumns = gridOption.numColumns;
        this.numSearchContainerColumns = gridOption.numSearchContainerColumns;
        this.dbFile = gridOption.dbFile;
        this.defaultLayoutId = gridOption.defaultLayoutId;
        this.demoModeLayoutId = gridOption.demoModeLayoutId;
        this.numFolderRows = gridOption.numFolderRows;
        this.numFolderColumns = gridOption.numFolderColumns;
        this.isScalable = gridOption.isScalable;
        this.devicePaddingId = gridOption.devicePaddingId;
        this.deviceType = i;
        this.mExtraAttrs = gridOption.extraAttrs;
        float[] access$100 = displayOption.iconSizes;
        this.iconSize = access$100;
        float f2 = access$100[0];
        int i2 = 1;
        while (true) {
            float[] fArr = this.iconSize;
            if (i2 >= fArr.length) {
                break;
            }
            f2 = Math.max(f2, fArr[i2]);
            i2++;
        }
        int pxFromDp = ResourceUtils.pxFromDp(f2, displayMetrics);
        this.iconBitmapSize = pxFromDp;
        this.fillResIconDpi = getLauncherIconDensity(pxFromDp);
        this.iconTextSize = displayOption.textSizes;
        this.minCellSize = displayOption.minCellSize;
        this.borderSpaces = displayOption.borderSpaces;
        this.folderBorderSpace = displayOption.folderBorderSpace;
        this.horizontalMargin = displayOption.horizontalMargin;
        this.numShownHotseatIcons = gridOption.numHotseatIcons;
        this.numShrunkenHotseatIcons = gridOption.numShrunkenHotseatIcons;
        this.numDatabaseHotseatIcons = i == 1 ? gridOption.numDatabaseHotseatIcons : gridOption.numHotseatIcons;
        this.hotseatColumnSpan = gridOption.hotseatColumnSpan;
        this.hotseatBorderSpaces = displayOption.hotseatBorderSpaces;
        this.numAllAppsColumns = gridOption.numAllAppsColumns;
        this.numDatabaseAllAppsColumns = i == 1 ? gridOption.numDatabaseAllAppsColumns : gridOption.numAllAppsColumns;
        this.allAppsCellSize = displayOption.allAppsCellSize;
        this.allAppsBorderSpaces = displayOption.allAppsBorderSpaces;
        this.allAppsIconSize = displayOption.allAppsIconSizes;
        this.allAppsIconTextSize = displayOption.allAppsIconTextSizes;
        if (!Utilities.isGridOptionsEnabled(context)) {
            this.allAppsIconSize = this.iconSize;
            this.allAppsIconTextSize = this.iconTextSize;
        }
        if (this.devicePaddingId != 0) {
            this.devicePaddings = new DevicePaddings(context, this.devicePaddingId);
        }
        this.inlineQsb = displayOption.inlineQsb;
        applyPartnerDeviceProfileOverrides(context, displayMetrics);
        ArrayList arrayList = new ArrayList();
        this.defaultWallpaperSize = new Point(info.currentSize);
        for (WindowBounds next : info.supportedBounds) {
            arrayList.add(new DeviceProfile.Builder(context, this, info).setUseTwoPanels(i == 1).setWindowBounds(next).build());
            int width = next.bounds.width();
            int height = next.bounds.height();
            Point point = this.defaultWallpaperSize;
            point.y = Math.max(point.y, height);
            if (Utilities.dpiFromPx((float) Math.min(width, height), info.getDensityDpi()) < 720.0f) {
                f = 2.0f;
            } else {
                f = wallpaperTravelToScreenWidthRatio(width, height);
            }
            Point point2 = this.defaultWallpaperSize;
            point2.x = Math.max(point2.x, Math.round(f * ((float) width)));
        }
        this.supportedProfiles = Collections.unmodifiableList(arrayList);
        this.defaultWidgetPadding = AppWidgetHostView.getDefaultPaddingForWidget(context, new ComponentName(context.getPackageName(), getClass().getName()), (Rect) null);
    }

    public void addOnChangeListener(OnIDPChangeListener onIDPChangeListener) {
        this.mChangeListeners.add(onIDPChangeListener);
    }

    public void removeOnChangeListener(OnIDPChangeListener onIDPChangeListener) {
        this.mChangeListeners.remove(onIDPChangeListener);
    }

    public void setCurrentGrid(Context context, String str) {
        Context applicationContext = context.getApplicationContext();
        Utilities.getPrefs(applicationContext).edit().putString(KEY_IDP_GRID_NAME, str).apply();
        Executors.MAIN_EXECUTOR.execute(new Runnable(applicationContext) {
            public final /* synthetic */ Context f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                InvariantDeviceProfile.this.lambda$setCurrentGrid$3$InvariantDeviceProfile(this.f$1);
            }
        });
    }

    private Object[] toModelState() {
        return new Object[]{Integer.valueOf(this.numColumns), Integer.valueOf(this.numRows), Integer.valueOf(this.numSearchContainerColumns), Integer.valueOf(this.numDatabaseHotseatIcons), Integer.valueOf(this.iconBitmapSize), Integer.valueOf(this.fillResIconDpi), Integer.valueOf(this.numDatabaseAllAppsColumns), this.dbFile};
    }

    /* access modifiers changed from: private */
    /* renamed from: onConfigChanged */
    public void lambda$setCurrentGrid$3$InvariantDeviceProfile(Context context) {
        Object[] modelState = toModelState();
        initGrid(context, getCurrentGridName(context));
        boolean z = !Arrays.equals(modelState, toModelState());
        Iterator<OnIDPChangeListener> it = this.mChangeListeners.iterator();
        while (it.hasNext()) {
            it.next().onIdpChanged(z);
        }
    }

    private static ArrayList<DisplayOption> getPredefinedDeviceProfiles(Context context, String str, int i, boolean z) {
        XmlResourceParser xml;
        ArrayList arrayList = new ArrayList();
        try {
            xml = context.getResources().getXml(R.xml.device_profiles);
            int depth = xml.getDepth();
            while (true) {
                int next = xml.next();
                if ((next != 3 || xml.getDepth() > depth) && next != 1) {
                    if (next == 2 && GridOption.TAG_NAME.equals(xml.getName())) {
                        GridOption gridOption = new GridOption(context, Xml.asAttributeSet(xml), i);
                        if (gridOption.isEnabled || z) {
                            int depth2 = xml.getDepth();
                            while (true) {
                                int next2 = xml.next();
                                if ((next2 == 3 && xml.getDepth() <= depth2) || next2 == 1) {
                                    break;
                                } else if (next2 == 2 && "display-option".equals(xml.getName())) {
                                    arrayList.add(new DisplayOption(gridOption, context, Xml.asAttributeSet(xml)));
                                }
                            }
                        }
                    }
                }
            }
            if (xml != null) {
                xml.close();
            }
            ArrayList<DisplayOption> arrayList2 = new ArrayList<>();
            if (!TextUtils.isEmpty(str)) {
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    DisplayOption displayOption = (DisplayOption) it.next();
                    if (str.equals(displayOption.grid.name) && (displayOption.grid.isEnabled || z)) {
                        arrayList2.add(displayOption);
                    }
                }
            }
            if (arrayList2.isEmpty()) {
                Iterator it2 = arrayList.iterator();
                while (it2.hasNext()) {
                    DisplayOption displayOption2 = (DisplayOption) it2.next();
                    if (displayOption2.canBeDefault) {
                        arrayList2.add(displayOption2);
                    }
                }
            }
            if (!arrayList2.isEmpty()) {
                return arrayList2;
            }
            throw new RuntimeException("No display option with canBeDefault=true");
        } catch (IOException | XmlPullParserException e) {
            throw new RuntimeException(e);
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    public List<GridOption> parseAllGridOptions(Context context) {
        XmlResourceParser xml;
        ArrayList arrayList = new ArrayList();
        try {
            xml = context.getResources().getXml(R.xml.device_profiles);
            int depth = xml.getDepth();
            while (true) {
                int next = xml.next();
                if ((next != 3 || xml.getDepth() > depth) && next != 1) {
                    if (next == 2 && GridOption.TAG_NAME.equals(xml.getName())) {
                        GridOption gridOption = new GridOption(context, Xml.asAttributeSet(xml), this.deviceType);
                        if (gridOption.isEnabled) {
                            arrayList.add(gridOption);
                        }
                    }
                }
            }
            if (xml != null) {
                xml.close();
            }
            return arrayList;
        } catch (IOException | XmlPullParserException e) {
            Log.e(TAG, "Error parsing device profile", e);
            return Collections.emptyList();
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    private int getLauncherIconDensity(int i) {
        int[] iArr = {120, 160, 213, 240, 320, 480, 640};
        int i2 = 640;
        for (int i3 = 6; i3 >= 0; i3--) {
            if ((((float) iArr[i3]) * ICON_SIZE_DEFINED_IN_APP_DP) / 160.0f >= ((float) i)) {
                i2 = iArr[i3];
            }
        }
        return i2;
    }

    private void applyPartnerDeviceProfileOverrides(Context context, DisplayMetrics displayMetrics) {
        Partner partner = Partner.get(context.getPackageManager());
        if (partner != null) {
            partner.applyInvariantDeviceProfileOverrides(this, displayMetrics);
        }
    }

    private static float dist(float f, float f2, float f3, float f4) {
        return (float) Math.hypot((double) (f3 - f), (double) (f4 - f2));
    }

    private static DisplayOption invDistWeightedInterpolate(DisplayController.Info info, ArrayList<DisplayOption> arrayList, int i) {
        int i2 = Integer.MAX_VALUE;
        int i3 = Integer.MAX_VALUE;
        for (WindowBounds next : info.supportedBounds) {
            boolean isTablet = info.isTablet(next);
            if (isTablet && i == 1) {
                i2 = Math.min(i2, next.availableSize.x / 2);
                i3 = Math.min(i3, next.availableSize.y);
            } else if (isTablet || !next.isLandscape()) {
                i2 = Math.min(i2, next.availableSize.x);
                i3 = Math.min(i3, next.availableSize.y);
            } else {
                i2 = Math.min(i2, next.availableSize.y);
                i3 = Math.min(i3, next.availableSize.x);
            }
        }
        float dpiFromPx = Utilities.dpiFromPx((float) i2, info.getDensityDpi());
        float dpiFromPx2 = Utilities.dpiFromPx((float) i3, info.getDensityDpi());
        Collections.sort(arrayList, new Comparator(dpiFromPx, dpiFromPx2) {
            public final /* synthetic */ float f$0;
            public final /* synthetic */ float f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final int compare(Object obj, Object obj2) {
                return Float.compare(InvariantDeviceProfile.dist(this.f$0, this.f$1, ((InvariantDeviceProfile.DisplayOption) obj).minWidthDps, ((InvariantDeviceProfile.DisplayOption) obj).minHeightDps), InvariantDeviceProfile.dist(this.f$0, this.f$1, ((InvariantDeviceProfile.DisplayOption) obj2).minWidthDps, ((InvariantDeviceProfile.DisplayOption) obj2).minHeightDps));
            }
        });
        DisplayOption displayOption = arrayList.get(0);
        GridOption gridOption = displayOption.grid;
        float f = 0.0f;
        if (dist(dpiFromPx, dpiFromPx2, displayOption.minWidthDps, displayOption.minHeightDps) == 0.0f) {
            return displayOption;
        }
        DisplayOption displayOption2 = new DisplayOption(gridOption);
        int i4 = 0;
        while (i4 < arrayList.size() && ((float) i4) < 3.0f) {
            DisplayOption displayOption3 = arrayList.get(i4);
            float weight = weight(dpiFromPx, dpiFromPx2, displayOption3.minWidthDps, displayOption3.minHeightDps, WEIGHT_POWER);
            f += weight;
            DisplayOption unused = displayOption2.add(new DisplayOption().add(displayOption3).multiply(weight));
            i4++;
        }
        DisplayOption unused2 = displayOption2.multiply(1.0f / f);
        for (int i5 = 0; i5 < 4; i5++) {
            displayOption2.iconSizes[i5] = Math.min(displayOption2.iconSizes[i5], displayOption.iconSizes[i5]);
        }
        return displayOption2;
    }

    public DeviceProfile getDeviceProfile(Context context) {
        Resources resources = context.getResources();
        Configuration configuration = context.getResources().getConfiguration();
        float f = ((float) configuration.screenWidthDp) * resources.getDisplayMetrics().density;
        float f2 = ((float) configuration.screenHeightDp) * resources.getDisplayMetrics().density;
        int rotation = WindowManagerProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).getRotation(context);
        if (Utilities.IS_DEBUG_DEVICE) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            DisplayController.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).dump(printWriter);
            printWriter.flush();
            Log.d("b/231312158", "getDeviceProfile -\nconfig: " + configuration + "\ndisplayMetrics: " + resources.getDisplayMetrics() + "\nrotation: " + rotation + "\n" + stringWriter.toString(), new Exception());
        }
        return getBestMatch(f, f2, rotation);
    }

    public DeviceProfile getBestMatch(float f, float f2, int i) {
        DeviceProfile deviceProfile = this.supportedProfiles.get(0);
        float f3 = Float.MAX_VALUE;
        for (DeviceProfile next : this.supportedProfiles) {
            float abs = Math.abs(((float) next.widthPx) - f) + Math.abs(((float) next.heightPx) - f2);
            if (abs < f3) {
                deviceProfile = next;
                f3 = abs;
            } else if (abs == f3 && next.rotationHint == i) {
                deviceProfile = next;
            }
        }
        return deviceProfile;
    }

    private static float weight(float f, float f2, float f3, float f4, float f5) {
        float dist = dist(f, f2, f3, f4);
        if (Float.compare(dist, 0.0f) == 0) {
            return Float.POSITIVE_INFINITY;
        }
        return (float) (100000.0d / Math.pow((double) dist, (double) f5));
    }

    public static final class GridOption {
        private static final int DEVICE_CATEGORY_ALL = 7;
        private static final int DEVICE_CATEGORY_MULTI_DISPLAY = 4;
        private static final int DEVICE_CATEGORY_PHONE = 1;
        private static final int DEVICE_CATEGORY_TABLET = 2;
        public static final String TAG_NAME = "grid-option";
        /* access modifiers changed from: private */
        public final String dbFile;
        /* access modifiers changed from: private */
        public final int defaultLayoutId;
        /* access modifiers changed from: private */
        public final int demoModeLayoutId;
        /* access modifiers changed from: private */
        public final int devicePaddingId;
        /* access modifiers changed from: private */
        public final SparseArray<TypedValue> extraAttrs;
        /* access modifiers changed from: private */
        public final int[] hotseatColumnSpan;
        public final boolean isEnabled;
        /* access modifiers changed from: private */
        public final boolean isScalable;
        public final String name;
        /* access modifiers changed from: private */
        public final int numAllAppsColumns;
        public final int numColumns;
        /* access modifiers changed from: private */
        public final int numDatabaseAllAppsColumns;
        /* access modifiers changed from: private */
        public final int numDatabaseHotseatIcons;
        /* access modifiers changed from: private */
        public final int numFolderColumns;
        /* access modifiers changed from: private */
        public final int numFolderRows;
        /* access modifiers changed from: private */
        public final int numHotseatIcons;
        public final int numRows;
        public final int numSearchContainerColumns;
        /* access modifiers changed from: private */
        public final int numShrunkenHotseatIcons;

        public GridOption(Context context, AttributeSet attributeSet, int i) {
            int[] iArr = new int[4];
            this.hotseatColumnSpan = iArr;
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.GridDisplayOption);
            this.name = obtainStyledAttributes.getString(11);
            boolean z = false;
            int i2 = obtainStyledAttributes.getInt(19, 0);
            this.numRows = i2;
            int i3 = obtainStyledAttributes.getInt(13, 0);
            this.numColumns = i3;
            this.numSearchContainerColumns = obtainStyledAttributes.getInt(20, i3);
            this.dbFile = obtainStyledAttributes.getString(0);
            int resourceId = obtainStyledAttributes.getResourceId((i != 1 || !obtainStyledAttributes.hasValue(2)) ? 1 : 2, 0);
            this.defaultLayoutId = resourceId;
            this.demoModeLayoutId = obtainStyledAttributes.getResourceId(3, resourceId);
            int i4 = obtainStyledAttributes.getInt(12, i3);
            this.numAllAppsColumns = i4;
            this.numDatabaseAllAppsColumns = obtainStyledAttributes.getInt(14, i4 * 2);
            int i5 = obtainStyledAttributes.getInt(18, i3);
            this.numHotseatIcons = i5;
            this.numShrunkenHotseatIcons = obtainStyledAttributes.getInt(21, i5 / 2);
            this.numDatabaseHotseatIcons = obtainStyledAttributes.getInt(15, i5 * 2);
            iArr[0] = obtainStyledAttributes.getInt(6, i3);
            iArr[1] = obtainStyledAttributes.getInt(7, i3);
            iArr[3] = obtainStyledAttributes.getInt(8, i3);
            iArr[2] = obtainStyledAttributes.getInt(9, i3);
            this.numFolderRows = obtainStyledAttributes.getInt(17, i2);
            this.numFolderColumns = obtainStyledAttributes.getInt(16, i3);
            this.isScalable = obtainStyledAttributes.getBoolean(10, false);
            this.devicePaddingId = obtainStyledAttributes.getResourceId(5, 0);
            int i6 = obtainStyledAttributes.getInt(4, 7);
            if ((i == 0 && (i6 & 1) == 1) || ((i == 2 && (i6 & 2) == 2) || (i == 1 && (i6 & 4) == 4))) {
                z = true;
            }
            this.isEnabled = z;
            obtainStyledAttributes.recycle();
            this.extraAttrs = Themes.createValueMap(context, attributeSet, IntArray.wrap(R.styleable.GridDisplayOption));
        }
    }

    static final class DisplayOption {
        private static final int DONT_INLINE_QSB = 0;
        private static final int INLINE_QSB_FOR_LANDSCAPE = 2;
        private static final int INLINE_QSB_FOR_PORTRAIT = 1;
        private static final int INLINE_QSB_FOR_TWO_PANEL_LANDSCAPE = 8;
        private static final int INLINE_QSB_FOR_TWO_PANEL_PORTRAIT = 4;
        /* access modifiers changed from: private */
        public final PointF[] allAppsBorderSpaces;
        /* access modifiers changed from: private */
        public final PointF[] allAppsCellSize;
        /* access modifiers changed from: private */
        public final float[] allAppsIconSizes;
        /* access modifiers changed from: private */
        public final float[] allAppsIconTextSizes;
        /* access modifiers changed from: private */
        public final PointF[] borderSpaces;
        /* access modifiers changed from: private */
        public final boolean canBeDefault;
        /* access modifiers changed from: private */
        public float folderBorderSpace;
        public final GridOption grid;
        /* access modifiers changed from: private */
        public final float[] horizontalMargin;
        /* access modifiers changed from: private */
        public final float[] hotseatBorderSpaces;
        /* access modifiers changed from: private */
        public final float[] iconSizes;
        /* access modifiers changed from: private */
        public final boolean[] inlineQsb;
        /* access modifiers changed from: private */
        public final PointF[] minCellSize;
        /* access modifiers changed from: private */
        public final float minHeightDps;
        /* access modifiers changed from: private */
        public final float minWidthDps;
        /* access modifiers changed from: private */
        public final float[] textSizes;

        DisplayOption(GridOption gridOption, Context context, AttributeSet attributeSet) {
            boolean[] zArr = new boolean[4];
            this.inlineQsb = zArr;
            PointF[] pointFArr = new PointF[4];
            this.minCellSize = pointFArr;
            PointF[] pointFArr2 = new PointF[4];
            this.borderSpaces = pointFArr2;
            float[] fArr = new float[4];
            this.horizontalMargin = fArr;
            float[] fArr2 = new float[4];
            this.hotseatBorderSpaces = fArr2;
            float[] fArr3 = new float[4];
            this.iconSizes = fArr3;
            float[] fArr4 = new float[4];
            this.textSizes = fArr4;
            PointF[] pointFArr3 = new PointF[4];
            this.allAppsCellSize = pointFArr3;
            float[] fArr5 = new float[4];
            this.allAppsIconSizes = fArr5;
            float[] fArr6 = new float[4];
            this.allAppsIconTextSizes = fArr6;
            PointF[] pointFArr4 = new PointF[4];
            this.allAppsBorderSpaces = pointFArr4;
            this.grid = gridOption;
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ProfileDisplayOption);
            this.minWidthDps = obtainStyledAttributes.getFloat(65, 0.0f);
            this.minHeightDps = obtainStyledAttributes.getFloat(64, 0.0f);
            this.canBeDefault = obtainStyledAttributes.getBoolean(38, false);
            int i = obtainStyledAttributes.getInt(55, 0);
            zArr[0] = (i & 1) == 1;
            zArr[1] = (i & 2) == 2;
            zArr[2] = (i & 4) == 4;
            zArr[3] = (i & 8) == 8;
            pointFArr[0] = new PointF(obtainStyledAttributes.getFloat(60, 0.0f), obtainStyledAttributes.getFloat(56, 0.0f));
            pointFArr[1] = new PointF(obtainStyledAttributes.getFloat(61, pointFArr[0].x), obtainStyledAttributes.getFloat(57, pointFArr[0].y));
            pointFArr[2] = new PointF(obtainStyledAttributes.getFloat(63, pointFArr[0].x), obtainStyledAttributes.getFloat(59, pointFArr[0].y));
            pointFArr[3] = new PointF(obtainStyledAttributes.getFloat(62, pointFArr[0].x), obtainStyledAttributes.getFloat(58, pointFArr[0].y));
            float f = obtainStyledAttributes.getFloat(26, 0.0f);
            float f2 = obtainStyledAttributes.getFloat(28, f);
            float f3 = obtainStyledAttributes.getFloat(34, f);
            float f4 = obtainStyledAttributes.getFloat(31, f);
            float[] fArr7 = fArr2;
            float[] fArr8 = fArr;
            float[] fArr9 = fArr6;
            pointFArr2[0] = new PointF(obtainStyledAttributes.getFloat(27, f), obtainStyledAttributes.getFloat(37, f));
            pointFArr2[1] = new PointF(obtainStyledAttributes.getFloat(29, f2), obtainStyledAttributes.getFloat(30, f2));
            pointFArr2[2] = new PointF(obtainStyledAttributes.getFloat(35, f3), obtainStyledAttributes.getFloat(36, f3));
            pointFArr2[3] = new PointF(obtainStyledAttributes.getFloat(32, f4), obtainStyledAttributes.getFloat(33, f4));
            this.folderBorderSpace = f;
            pointFArr3[0] = new PointF(obtainStyledAttributes.getFloat(16, pointFArr[0].x), obtainStyledAttributes.getFloat(12, pointFArr[0].y));
            pointFArr3[1] = new PointF(obtainStyledAttributes.getFloat(17, pointFArr3[0].x), obtainStyledAttributes.getFloat(13, pointFArr3[0].y));
            pointFArr3[2] = new PointF(obtainStyledAttributes.getFloat(19, pointFArr3[0].x), obtainStyledAttributes.getFloat(15, pointFArr3[0].y));
            pointFArr3[3] = new PointF(obtainStyledAttributes.getFloat(18, pointFArr3[0].x), obtainStyledAttributes.getFloat(14, pointFArr3[0].y));
            float f5 = obtainStyledAttributes.getFloat(0, f);
            float f6 = obtainStyledAttributes.getFloat(2, f5);
            float f7 = obtainStyledAttributes.getFloat(8, f5);
            float f8 = obtainStyledAttributes.getFloat(5, f5);
            pointFArr4[0] = new PointF(obtainStyledAttributes.getFloat(1, f5), obtainStyledAttributes.getFloat(11, f5));
            pointFArr4[1] = new PointF(obtainStyledAttributes.getFloat(3, f6), obtainStyledAttributes.getFloat(4, f6));
            pointFArr4[2] = new PointF(obtainStyledAttributes.getFloat(9, f7), obtainStyledAttributes.getFloat(10, f7));
            pointFArr4[3] = new PointF(obtainStyledAttributes.getFloat(6, f8), obtainStyledAttributes.getFloat(7, f8));
            fArr3[0] = obtainStyledAttributes.getFloat(47, 0.0f);
            fArr3[1] = obtainStyledAttributes.getFloat(48, fArr3[0]);
            fArr3[2] = obtainStyledAttributes.getFloat(50, fArr3[0]);
            fArr3[3] = obtainStyledAttributes.getFloat(49, fArr3[0]);
            fArr5[0] = obtainStyledAttributes.getFloat(20, fArr3[0]);
            fArr5[1] = fArr5[0];
            fArr5[2] = obtainStyledAttributes.getFloat(22, fArr5[0]);
            fArr5[3] = obtainStyledAttributes.getFloat(21, fArr5[0]);
            fArr4[0] = obtainStyledAttributes.getFloat(51, 0.0f);
            fArr4[1] = obtainStyledAttributes.getFloat(52, fArr4[0]);
            fArr4[2] = obtainStyledAttributes.getFloat(54, fArr4[0]);
            fArr4[3] = obtainStyledAttributes.getFloat(53, fArr4[0]);
            fArr9[0] = obtainStyledAttributes.getFloat(23, fArr4[0]);
            fArr9[1] = fArr9[0];
            fArr9[2] = obtainStyledAttributes.getFloat(25, fArr9[0]);
            fArr9[3] = obtainStyledAttributes.getFloat(24, fArr9[0]);
            fArr8[0] = obtainStyledAttributes.getFloat(39, 0.0f);
            fArr8[1] = obtainStyledAttributes.getFloat(40, fArr8[0]);
            fArr8[3] = obtainStyledAttributes.getFloat(41, fArr8[0]);
            fArr8[2] = obtainStyledAttributes.getFloat(42, fArr8[0]);
            fArr7[0] = obtainStyledAttributes.getFloat(43, f);
            fArr7[1] = obtainStyledAttributes.getFloat(44, fArr7[0]);
            fArr7[3] = obtainStyledAttributes.getFloat(45, fArr7[0]);
            fArr7[2] = obtainStyledAttributes.getFloat(46, fArr7[0]);
            obtainStyledAttributes.recycle();
        }

        DisplayOption() {
            this((GridOption) null);
        }

        DisplayOption(GridOption gridOption) {
            this.inlineQsb = new boolean[4];
            this.minCellSize = new PointF[4];
            this.borderSpaces = new PointF[4];
            this.horizontalMargin = new float[4];
            this.hotseatBorderSpaces = new float[4];
            this.iconSizes = new float[4];
            this.textSizes = new float[4];
            this.allAppsCellSize = new PointF[4];
            this.allAppsIconSizes = new float[4];
            this.allAppsIconTextSizes = new float[4];
            this.allAppsBorderSpaces = new PointF[4];
            this.grid = gridOption;
            this.minWidthDps = 0.0f;
            this.minHeightDps = 0.0f;
            this.canBeDefault = false;
            for (int i = 0; i < 4; i++) {
                this.iconSizes[i] = 0.0f;
                this.textSizes[i] = 0.0f;
                this.borderSpaces[i] = new PointF();
                this.minCellSize[i] = new PointF();
                this.allAppsCellSize[i] = new PointF();
                this.allAppsIconSizes[i] = 0.0f;
                this.allAppsIconTextSizes[i] = 0.0f;
                this.allAppsBorderSpaces[i] = new PointF();
                this.inlineQsb[i] = false;
            }
        }

        /* access modifiers changed from: private */
        public DisplayOption multiply(float f) {
            for (int i = 0; i < 4; i++) {
                float[] fArr = this.iconSizes;
                fArr[i] = fArr[i] * f;
                float[] fArr2 = this.textSizes;
                fArr2[i] = fArr2[i] * f;
                this.borderSpaces[i].x *= f;
                this.borderSpaces[i].y *= f;
                this.minCellSize[i].x *= f;
                this.minCellSize[i].y *= f;
                float[] fArr3 = this.horizontalMargin;
                fArr3[i] = fArr3[i] * f;
                float[] fArr4 = this.hotseatBorderSpaces;
                fArr4[i] = fArr4[i] * f;
                this.allAppsCellSize[i].x *= f;
                this.allAppsCellSize[i].y *= f;
                float[] fArr5 = this.allAppsIconSizes;
                fArr5[i] = fArr5[i] * f;
                float[] fArr6 = this.allAppsIconTextSizes;
                fArr6[i] = fArr6[i] * f;
                this.allAppsBorderSpaces[i].x *= f;
                this.allAppsBorderSpaces[i].y *= f;
            }
            this.folderBorderSpace *= f;
            return this;
        }

        /* access modifiers changed from: private */
        public DisplayOption add(DisplayOption displayOption) {
            for (int i = 0; i < 4; i++) {
                float[] fArr = this.iconSizes;
                fArr[i] = fArr[i] + displayOption.iconSizes[i];
                float[] fArr2 = this.textSizes;
                fArr2[i] = fArr2[i] + displayOption.textSizes[i];
                this.borderSpaces[i].x += displayOption.borderSpaces[i].x;
                this.borderSpaces[i].y += displayOption.borderSpaces[i].y;
                this.minCellSize[i].x += displayOption.minCellSize[i].x;
                this.minCellSize[i].y += displayOption.minCellSize[i].y;
                float[] fArr3 = this.horizontalMargin;
                fArr3[i] = fArr3[i] + displayOption.horizontalMargin[i];
                float[] fArr4 = this.hotseatBorderSpaces;
                fArr4[i] = fArr4[i] + displayOption.hotseatBorderSpaces[i];
                this.allAppsCellSize[i].x += displayOption.allAppsCellSize[i].x;
                this.allAppsCellSize[i].y += displayOption.allAppsCellSize[i].y;
                float[] fArr5 = this.allAppsIconSizes;
                fArr5[i] = fArr5[i] + displayOption.allAppsIconSizes[i];
                float[] fArr6 = this.allAppsIconTextSizes;
                fArr6[i] = fArr6[i] + displayOption.allAppsIconTextSizes[i];
                this.allAppsBorderSpaces[i].x += displayOption.allAppsBorderSpaces[i].x;
                this.allAppsBorderSpaces[i].y += displayOption.allAppsBorderSpaces[i].y;
                boolean[] zArr = this.inlineQsb;
                zArr[i] = zArr[i] | displayOption.inlineQsb[i];
            }
            this.folderBorderSpace += displayOption.folderBorderSpace;
            return this;
        }
    }
}
