package com.android.launcher3.testing;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.R;
import com.android.launcher3.Workspace;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.testing.TestInformationHandler;
import com.android.launcher3.util.ActivityTracker;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.ResourceBasedOverride;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Supplier;

public class TestInformationHandler implements ResourceBasedOverride {
    protected Context mContext;
    protected DeviceProfile mDeviceProfile;
    protected LauncherAppState mLauncherAppState;

    public interface BundleSetter<T> {
        void set(Bundle bundle, String str, T t);
    }

    public static TestInformationHandler newInstance(Context context) {
        return (TestInformationHandler) ResourceBasedOverride.Overrides.getObject(TestInformationHandler.class, context, R.string.test_information_handler_class);
    }

    public void init(Context context) {
        this.mContext = context;
        this.mDeviceProfile = InvariantDeviceProfile.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).getDeviceProfile(context);
        this.mLauncherAppState = LauncherAppState.getInstanceNoCreate();
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        Bundle bundle2 = new Bundle();
        if (bundle != null && bundle.getClassLoader() == null) {
            bundle.setClassLoader(getClass().getClassLoader());
        }
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1842849687:
                if (str.equals(TestProtocol.REQUEST_IS_TABLET)) {
                    c = 0;
                    break;
                }
                break;
            case -1222247840:
                if (str.equals(TestProtocol.REQUEST_IS_TWO_PANELS)) {
                    c = 1;
                    break;
                }
                break;
            case -919388399:
                if (str.equals(TestProtocol.REQUEST_WORKSPACE_CELL_LAYOUT_SIZE)) {
                    c = 2;
                    break;
                }
                break;
            case -805999811:
                if (str.equals(TestProtocol.REQUEST_SET_FORCE_PAUSE_TIMEOUT)) {
                    c = 3;
                    break;
                }
                break;
            case -739296992:
                if (str.equals(TestProtocol.REQUEST_GET_HAD_NONTEST_EVENTS)) {
                    c = 4;
                    break;
                }
                break;
            case -634643027:
                if (str.equals(TestProtocol.REQUEST_APPS_LIST_SCROLL_Y)) {
                    c = 5;
                    break;
                }
                break;
            case -335925014:
                if (str.equals(TestProtocol.REQUEST_IS_LAUNCHER_INITIALIZED)) {
                    c = 6;
                    break;
                }
                break;
            case -132279417:
                if (str.equals(TestProtocol.REQUEST_UNFREEZE_APP_LIST)) {
                    c = 7;
                    break;
                }
                break;
            case -8342034:
                if (str.equals(TestProtocol.REQUEST_MOCK_SENSOR_ROTATION)) {
                    c = 8;
                    break;
                }
                break;
            case 116891322:
                if (str.equals(TestProtocol.REQUEST_ENABLE_ROTATION)) {
                    c = 9;
                    break;
                }
                break;
            case 117113715:
                if (str.equals(TestProtocol.REQUEST_WINDOW_INSETS)) {
                    c = 10;
                    break;
                }
                break;
            case 299522245:
                if (str.equals(TestProtocol.REQUEST_HOME_TO_ALL_APPS_SWIPE_HEIGHT)) {
                    c = 11;
                    break;
                }
                break;
            case 485168855:
                if (str.equals(TestProtocol.REQUEST_WIDGETS_SCROLL_Y)) {
                    c = 12;
                    break;
                }
                break;
            case 512131560:
                if (str.equals(TestProtocol.REQUEST_WORKSPACE_CELL_CENTER)) {
                    c = 13;
                    break;
                }
                break;
            case 599032057:
                if (str.equals(TestProtocol.REQUEST_HAS_TIS)) {
                    c = 14;
                    break;
                }
                break;
            case 738461362:
                if (str.equals(TestProtocol.REQUEST_TARGET_INSETS)) {
                    c = 15;
                    break;
                }
                break;
            case 768460608:
                if (str.equals(TestProtocol.REQUEST_FREEZE_APP_LIST)) {
                    c = 16;
                    break;
                }
                break;
            case 2052224251:
                if (str.equals(TestProtocol.REQUEST_ICON_HEIGHT)) {
                    c = 17;
                    break;
                }
                break;
            case 2140625885:
                if (str.equals(TestProtocol.REQUEST_START_DRAG_THRESHOLD)) {
                    c = 18;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                bundle2.putBoolean(TestProtocol.TEST_INFO_RESPONSE_FIELD, this.mDeviceProfile.isTablet);
                return bundle2;
            case 1:
                bundle2.putBoolean(TestProtocol.TEST_INFO_RESPONSE_FIELD, this.mDeviceProfile.isTwoPanels);
                return bundle2;
            case 2:
                return getLauncherUIProperty($$Lambda$TestInformationHandler$XCCkMxEVmzChrpbsyAxnELl7Ew.INSTANCE, $$Lambda$TestInformationHandler$FLkJdS2XTTH_p9qFSmoZ3wMB5W0.INSTANCE);
            case 3:
                TestProtocol.sForcePauseTimeout = Long.valueOf(Long.parseLong(str2));
                return bundle2;
            case 4:
                bundle2.putBoolean(TestProtocol.TEST_INFO_RESPONSE_FIELD, TestLogging.sHadEventsNotFromTest);
                return bundle2;
            case 5:
                return getLauncherUIProperty($$Lambda$TestInformationHandler$D6S0dcEO_uKDVVM8mvy9GY8ogPw.INSTANCE, $$Lambda$TestInformationHandler$bjJJ0B4jEhECXqH7yF6GWSH4.INSTANCE);
            case 6:
                return getUIProperty($$Lambda$TestInformationHandler$nwhDbJt04LGamgYjMmUlmPpp31s.INSTANCE, new Function() {
                    public final Object apply(Object obj) {
                        return TestInformationHandler.this.lambda$call$1$TestInformationHandler((Boolean) obj);
                    }
                }, $$Lambda$TestInformationHandler$YlRzmzqRn87rsJUTjdHKX0DKHtk.INSTANCE);
            case 7:
                return getLauncherUIProperty($$Lambda$TestInformationHandler$nwhDbJt04LGamgYjMmUlmPpp31s.INSTANCE, $$Lambda$TestInformationHandler$tp0rZUvbWITqkAs4FytlFb5AJoU.INSTANCE);
            case 8:
                TestProtocol.sDisableSensorRotation = true;
                return bundle2;
            case 9:
                Executors.MAIN_EXECUTOR.submit(new Runnable(str2) {
                    public final /* synthetic */ String f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void run() {
                        ((Launcher) Launcher.ACTIVITY_TRACKER.getCreatedActivity()).getRotationHelper().forceAllowRotationForTesting(Boolean.parseBoolean(this.f$0));
                    }
                });
                return null;
            case 10:
                return getUIProperty($$Lambda$TestInformationHandler$b6Mpv6uD_i6T941ym6siMKoeC0A.INSTANCE, $$Lambda$TestInformationHandler$MedHHcZ7L2adMYy65cJFiELHmPM.INSTANCE, new Supplier() {
                    public final Object get() {
                        return TestInformationHandler.this.getCurrentActivity();
                    }
                });
            case 11:
                return getLauncherUIProperty($$Lambda$TestInformationHandler$D6S0dcEO_uKDVVM8mvy9GY8ogPw.INSTANCE, $$Lambda$TestInformationHandler$ykHx1fS8Ino8ygoFttBzzt0vYAk.INSTANCE);
            case 12:
                return getLauncherUIProperty($$Lambda$TestInformationHandler$D6S0dcEO_uKDVVM8mvy9GY8ogPw.INSTANCE, $$Lambda$TestInformationHandler$7HszJHUR_yImtj17H6DnPCeP0.INSTANCE);
            case 13:
                return getLauncherUIProperty($$Lambda$TestInformationHandler$IemUce8XIRkt6hZ8spoFTyo3lo.INSTANCE, new Function() {
                    public final Object apply(Object obj) {
                        return TestInformationHandler.lambda$call$11(WorkspaceCellCenterRequest.this, (Launcher) obj);
                    }
                });
            case 14:
                bundle2.putBoolean(TestProtocol.REQUEST_HAS_TIS, false);
                return bundle2;
            case 15:
                return getUIProperty($$Lambda$TestInformationHandler$b6Mpv6uD_i6T941ym6siMKoeC0A.INSTANCE, $$Lambda$TestInformationHandler$buItxVIj62hfqspJQ13br2Mrlg.INSTANCE, new Supplier() {
                    public final Object get() {
                        return TestInformationHandler.this.getCurrentActivity();
                    }
                });
            case 16:
                return getLauncherUIProperty($$Lambda$TestInformationHandler$nwhDbJt04LGamgYjMmUlmPpp31s.INSTANCE, $$Lambda$TestInformationHandler$gLM38ZSbsCRrsQu3lokLKJtlzUc.INSTANCE);
            case 17:
                bundle2.putInt(TestProtocol.TEST_INFO_RESPONSE_FIELD, this.mDeviceProfile.allAppsCellHeightPx);
                return bundle2;
            case 18:
                Resources resources = this.mContext.getResources();
                bundle2.putInt(TestProtocol.TEST_INFO_RESPONSE_FIELD, resources.getDimensionPixelSize(R.dimen.deep_shortcuts_start_drag_threshold) + resources.getDimensionPixelSize(R.dimen.pre_drag_view_scale));
                return bundle2;
            default:
                return null;
        }
    }

    static /* synthetic */ Boolean lambda$call$2() {
        return true;
    }

    public /* synthetic */ Boolean lambda$call$1$TestInformationHandler(Boolean bool) {
        return Boolean.valueOf(isLauncherInitialized());
    }

    static /* synthetic */ int[] lambda$call$10(Launcher launcher) {
        Workspace<?> workspace = launcher.getWorkspace();
        CellLayout screenWithId = workspace.getScreenWithId(workspace.getScreenIdForPageIndex(workspace.getCurrentPage()));
        return new int[]{screenWithId.getCountX(), screenWithId.getCountY()};
    }

    static /* synthetic */ Point lambda$call$11(WorkspaceCellCenterRequest workspaceCellCenterRequest, Launcher launcher) {
        Workspace<?> workspace = launcher.getWorkspace();
        Rect descendantRectRelativeToDragLayerForCell = getDescendantRectRelativeToDragLayerForCell(launcher, (CellLayout) workspace.getPageAt(workspace.getCurrentPage()), workspaceCellCenterRequest.cellX, workspaceCellCenterRequest.cellY, workspaceCellCenterRequest.spanX, workspaceCellCenterRequest.spanY);
        return new Point(descendantRectRelativeToDragLayerForCell.centerX(), descendantRectRelativeToDragLayerForCell.centerY());
    }

    private static Rect getDescendantRectRelativeToDragLayerForCell(Launcher launcher, CellLayout cellLayout, int i, int i2, int i3, int i4) {
        DragLayer dragLayer = launcher.getDragLayer();
        Rect rect = new Rect();
        cellLayout.cellToRect(i, i2, i3, i4, rect);
        int[] iArr = {rect.left, rect.top};
        int[] iArr2 = {rect.right, rect.bottom};
        dragLayer.getDescendantCoordRelativeToSelf((View) cellLayout, iArr);
        dragLayer.getDescendantCoordRelativeToSelf((View) cellLayout, iArr2);
        rect.set(iArr[0], iArr[1], iArr2[0], iArr2[1]);
        return rect;
    }

    /* access modifiers changed from: protected */
    public boolean isLauncherInitialized() {
        return Launcher.ACTIVITY_TRACKER.getCreatedActivity() == null || LauncherAppState.getInstance(this.mContext).getModel().isModelLoaded();
    }

    /* access modifiers changed from: protected */
    public Activity getCurrentActivity() {
        return Launcher.ACTIVITY_TRACKER.getCreatedActivity();
    }

    public static <T> Bundle getLauncherUIProperty(BundleSetter<T> bundleSetter, Function<Launcher, T> function) {
        ActivityTracker<Launcher> activityTracker = Launcher.ACTIVITY_TRACKER;
        Objects.requireNonNull(activityTracker);
        return getUIProperty(bundleSetter, function, new Supplier() {
            public final Object get() {
                return (Launcher) ActivityTracker.this.getCreatedActivity();
            }
        });
    }

    private static <S, T> Bundle getUIProperty(BundleSetter<T> bundleSetter, Function<S, T> function, Supplier<S> supplier) {
        try {
            return (Bundle) Executors.MAIN_EXECUTOR.submit(new Callable(supplier, function, bundleSetter) {
                public final /* synthetic */ Supplier f$0;
                public final /* synthetic */ Function f$1;
                public final /* synthetic */ TestInformationHandler.BundleSetter f$2;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final Object call() {
                    return TestInformationHandler.lambda$getUIProperty$12(this.f$0, this.f$1, this.f$2);
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    static /* synthetic */ Bundle lambda$getUIProperty$12(Supplier supplier, Function function, BundleSetter bundleSetter) throws Exception {
        Object obj = supplier.get();
        if (obj == null) {
            return null;
        }
        Object apply = function.apply(obj);
        Bundle bundle = new Bundle();
        bundleSetter.set(bundle, TestProtocol.TEST_INFO_RESPONSE_FIELD, apply);
        return bundle;
    }
}
