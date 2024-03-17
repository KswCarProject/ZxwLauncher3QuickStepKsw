package com.android.launcher3.logging;

import android.content.Context;
import android.view.View;
import androidx.core.view.PointerIconCompat;
import androidx.slice.SliceItem;
import com.android.launcher3.R;
import com.android.launcher3.Workspace;
import com.android.launcher3.logger.LauncherAtom;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.util.ResourceBasedOverride;
import com.android.launcher3.views.ActivityContext;
import com.android.systemui.shared.system.SysUiStatsLog;

public class StatsLogManager implements ResourceBasedOverride {
    public static final int LAUNCHER_STATE_ALLAPPS = 4;
    public static final int LAUNCHER_STATE_BACKGROUND = 1;
    public static final int LAUNCHER_STATE_HOME = 2;
    public static final int LAUNCHER_STATE_OVERVIEW = 3;
    public static final int LAUNCHER_STATE_UNCHANGED = 5;
    public static final int LAUNCHER_STATE_UNSPECIFIED = 0;
    protected ActivityContext mActivityContext = null;
    private InstanceId mInstanceId;

    public interface EventEnum {
        public static final int RESERVE_NEW_UI_EVENT_ID = Integer.MIN_VALUE;

        int getId();
    }

    public interface StatsLogger {
        void log(EventEnum eventEnum) {
        }

        void sendToInteractionJankMonitor(EventEnum eventEnum, View view) {
        }

        StatsLogger withContainerInfo(LauncherAtom.ContainerInfo containerInfo) {
            return this;
        }

        StatsLogger withDstState(int i) {
            return this;
        }

        StatsLogger withEditText(String str) {
            return this;
        }

        StatsLogger withFromState(LauncherAtom.FromState fromState) {
            return this;
        }

        StatsLogger withInstanceId(InstanceId instanceId) {
            return this;
        }

        StatsLogger withItemInfo(ItemInfo itemInfo) {
            return this;
        }

        StatsLogger withRank(int i) {
            return this;
        }

        StatsLogger withSlice(LauncherAtom.Slice slice) {
            return this;
        }

        StatsLogger withSliceItem(SliceItem sliceItem) {
            return this;
        }

        StatsLogger withSrcState(int i) {
            return this;
        }

        StatsLogger withToState(LauncherAtom.ToState toState) {
            return this;
        }
    }

    public static EventEnum getLauncherAtomEvent(int i, int i2, EventEnum eventEnum) {
        if (i == 2 && i2 == 2) {
            return LauncherEvent.LAUNCHER_HOME_GESTURE;
        }
        if (i != 3 && i2 == 3) {
            return LauncherEvent.LAUNCHER_OVERVIEW_GESTURE;
        }
        if (i == 4 || i2 != 4) {
            return (i != 4 || i2 == 4) ? eventEnum : LauncherEvent.LAUNCHER_ALLAPPS_CLOSE_DOWN;
        }
        return LauncherEvent.LAUNCHER_ALLAPPS_OPEN_UP;
    }

    public enum LauncherEvent implements EventEnum {
        IGNORE(-1),
        LAUNCHER_APP_LAUNCH_TAP(338),
        LAUNCHER_TASK_LAUNCH_TAP(339),
        LAUNCHER_NOTIFICATION_LAUNCH_TAP(516),
        LAUNCHER_TASK_LAUNCH_SWIPE_DOWN(340),
        LAUNCHER_TASK_DISMISS_SWIPE_UP(341),
        LAUNCHER_ITEM_DRAG_STARTED(383),
        LAUNCHER_ITEM_DROP_COMPLETED(385),
        LAUNCHER_ITEM_DROP_COMPLETED_ON_FOLDER_ICON(697),
        LAUNCHER_ITEM_DROP_FOLDER_CREATED(386),
        LAUNCHER_FOLDER_AUTO_LABELED(591),
        LAUNCHER_FOLDER_AUTO_LABELING_SKIPPED_EMPTY_PRIMARY(592),
        LAUNCHER_FOLDER_AUTO_LABELING_SKIPPED_EMPTY_SUGGESTIONS(593),
        LAUNCHER_FOLDER_LABEL_UPDATED(460),
        LAUNCHER_WORKSPACE_LONGPRESS(461),
        LAUNCHER_WALLPAPER_BUTTON_TAP_OR_LONGPRESS(462),
        LAUNCHER_SETTINGS_BUTTON_TAP_OR_LONGPRESS(463),
        LAUNCHER_WIDGETSTRAY_BUTTON_TAP_OR_LONGPRESS(464),
        LAUNCHER_WIDGETSTRAY_APP_EXPANDED(818),
        LAUNCHER_WIDGETSTRAY_SEARCHED(819),
        LAUNCHER_ITEM_DROPPED_ON_REMOVE(465),
        LAUNCHER_ITEM_DROPPED_ON_CANCEL(SysUiStatsLog.MEDIAOUTPUT_OP_INTERACTION_REPORT),
        LAUNCHER_ITEM_DROPPED_ON_DONT_SUGGEST(467),
        LAUNCHER_ITEM_DROPPED_ON_UNINSTALL(468),
        LAUNCHER_ITEM_UNINSTALL_COMPLETED(469),
        LAUNCHER_ITEM_UNINSTALL_CANCELLED(470),
        LAUNCHER_TASK_ICON_TAP_OR_LONGPRESS(517),
        LAUNCHER_SYSTEM_SHORTCUT_WIDGETS_TAP(514),
        LAUNCHER_SYSTEM_SHORTCUT_APP_INFO_TAP(515),
        LAUNCHER_SYSTEM_SHORTCUT_SPLIT_SCREEN_TAP(518),
        LAUNCHER_SYSTEM_SHORTCUT_FREE_FORM_TAP(519),
        LAUNCHER_SYSTEM_SHORTCUT_PAUSE_TAP(521),
        LAUNCHER_SYSTEM_SHORTCUT_PIN_TAP(522),
        LAUNCHER_ALL_APPS_EDU_SHOWN(523),
        LAUNCHER_FOLDER_OPEN(551),
        LAUNCHER_HOTSEAT_EDU_SEEN(479),
        LAUNCHER_HOTSEAT_EDU_ACCEPT(480),
        LAUNCHER_HOTSEAT_EDU_DENY(481),
        LAUNCHER_HOTSEAT_EDU_ONLY_TIP(482),
        LAUNCHER_ALL_APPS_RANKED(552),
        LAUNCHER_HOTSEAT_RANKED(553),
        LAUNCHER_ONSTOP(562),
        LAUNCHER_ONRESUME(563),
        LAUNCHER_SWIPELEFT(564),
        LAUNCHER_SWIPERIGHT(565),
        LAUNCHER_UNKNOWN_SWIPEUP(566),
        LAUNCHER_UNKNOWN_SWIPEDOWN(567),
        LAUNCHER_ALLAPPS_OPEN_UP(568),
        LAUNCHER_ALLAPPS_CLOSE_DOWN(569),
        LAUNCHER_ALLAPPS_CLOSE_TAP_OUTSIDE(941),
        LAUNCHER_OVERVIEW_GESTURE(570),
        LAUNCHER_QUICKSWITCH_LEFT(571),
        LAUNCHER_QUICKSWITCH_RIGHT(572),
        LAUNCHER_SWIPEDOWN_NAVBAR(573),
        LAUNCHER_HOME_GESTURE(574),
        LAUNCHER_WORKSPACE_SNAPSHOT(579),
        LAUNCHER_OVERVIEW_ACTIONS_SCREENSHOT(580),
        LAUNCHER_OVERVIEW_ACTIONS_SELECT(581),
        LAUNCHER_OVERVIEW_ACTIONS_SHARE(582),
        LAUNCHER_OVERVIEW_ACTIONS_SPLIT(895),
        LAUNCHER_SELECT_MODE_CLOSE(583),
        LAUNCHER_SELECT_MODE_ITEM(584),
        LAUNCHER_NOTIFICATION_DOT_ENABLED(611),
        LAUNCHER_NOTIFICATION_DOT_DISABLED(612),
        LAUNCHER_ADD_NEW_APPS_TO_HOME_SCREEN_ENABLED(613),
        LAUNCHER_ADD_NEW_APPS_TO_HOME_SCREEN_DISABLED(614),
        LAUNCHER_HOME_SCREEN_ROTATION_ENABLED(615),
        LAUNCHER_HOME_SCREEN_ROTATION_DISABLED(616),
        LAUNCHER_ALL_APPS_SUGGESTIONS_ENABLED(619),
        LAUNCHER_ALL_APPS_SUGGESTIONS_DISABLED(620),
        LAUNCHER_HOME_SCREEN_SUGGESTIONS_ENABLED(621),
        LAUNCHER_HOME_SCREEN_SUGGESTIONS_DISABLED(622),
        LAUNCHER_NAVIGATION_MODE_3_BUTTON(623),
        LAUNCHER_NAVIGATION_MODE_2_BUTTON(624),
        LAUNCHER_NAVIGATION_MODE_GESTURE_BUTTON(625),
        LAUNCHER_SELECT_MODE_IMAGE(627),
        LAUNCHER_ADD_EXTERNAL_ITEM_START(641),
        LAUNCHER_ADD_EXTERNAL_ITEM_CANCELLED(642),
        LAUNCHER_ADD_EXTERNAL_ITEM_BACK(643),
        LAUNCHER_ADD_EXTERNAL_ITEM_PLACED_AUTOMATICALLY(644),
        LAUNCHER_ADD_EXTERNAL_ITEM_DRAGGED(645),
        LAUNCHER_FOLDER_CONVERTED_TO_ICON(646),
        LAUNCHER_HOTSEAT_PREDICTION_PINNED(647),
        LAUNCHER_UNDO(648),
        LAUNCHER_TASK_CLEAR_ALL(649),
        LAUNCHER_TASK_PREVIEW_LONGPRESS(Workspace.REORDER_TIMEOUT),
        LAUNCHER_SWIPE_DOWN_WORKSPACE_NOTISHADE_OPEN(651),
        LAUNCHER_NOTIFICATION_DISMISSED(652),
        LAUNCHER_GRID_SIZE_6(930),
        LAUNCHER_GRID_SIZE_5(662),
        LAUNCHER_GRID_SIZE_4(663),
        LAUNCHER_GRID_SIZE_3(664),
        LAUNCHER_GRID_SIZE_2(665),
        LAUNCHER_ALLAPPS_ENTRY(692),
        LAUNCHER_ALLAPPS_EXIT(693),
        LAUNCHER_ALLAPPS_KEYBOARD_CLOSED(694),
        LAUNCHER_ALLAPPS_SWIPE_TO_PERSONAL_TAB(695),
        LAUNCHER_ALLAPPS_SWIPE_TO_WORK_TAB(696),
        LAUNCHER_SLICE_DEFAULT_ACTION(700),
        LAUNCHER_SLICE_TOGGLE_ON(701),
        LAUNCHER_SLICE_TOGGLE_OFF(702),
        LAUNCHER_SLICE_BUTTON_ACTION(703),
        LAUNCHER_SLICE_SLIDER_ACTION(704),
        LAUNCHER_SLICE_CONTENT_ACTION(705),
        LAUNCHER_SLICE_SEE_MORE_ACTION(706),
        LAUNCHER_SLICE_SELECTION_ACTION(707),
        LAUNCHER_ALLAPPS_FOCUSED_ITEM_SELECTED_WITH_IME(718),
        LAUNCHER_ALLAPPS_ITEM_LONG_PRESSED(719),
        LAUNCHER_ALLAPPS_ENTRY_WITH_DEVICE_SEARCH(720),
        LAUNCHER_ALLAPPS_TAP_ON_PERSONAL_TAB(721),
        LAUNCHER_ALLAPPS_TAP_ON_WORK_TAB(722),
        LAUNCHER_ALLAPPS_VERTICAL_SWIPE_BEGIN(724),
        LAUNCHER_ALLAPPS_VERTICAL_SWIPE_END(725),
        LAUNCHER_OVERVIEW_SHARING_SHOW_URL_INDICATOR(764),
        LAUNCHER_OVERVIEW_SHARING_SHOW_IMAGE_INDICATOR(765),
        LAUNCHER_OVERVIEW_SHARING_URL_INDICATOR_TAP(766),
        LAUNCHER_OVERVIEW_SHARING_IMAGE_INDICATOR_TAP(767),
        LAUNCHER_OVERVIEW_SHARING_IMAGE_LONG_PRESS(768),
        LAUNCHER_OVERVIEW_SHARING_URL_DRAG(769),
        LAUNCHER_OVERVIEW_SHARING_IMAGE_DRAG(770),
        LAUNCHER_OVERVIEW_SHARING_DROP_URL_TO_TARGET(771),
        LAUNCHER_OVERVIEW_SHARING_DROP_IMAGE_TO_TARGET(772),
        LAUNCHER_OVERVIEW_SHARING_DROP_URL_TO_MORE(773),
        LAUNCHER_OVERVIEW_SHARING_DROP_IMAGE_TO_MORE(774),
        LAUNCHER_OVERVIEW_SHARING_TAP_TARGET_TO_SHARE_URL(775),
        LAUNCHER_OVERVIEW_SHARING_TAP_TARGET_TO_SHARE_IMAGE(776),
        LAUNCHER_OVERVIEW_SHARING_TAP_MORE_TO_SHARE_URL(777),
        LAUNCHER_OVERVIEW_SHARING_TAP_MORE_TO_SHARE_IMAGE(778),
        LAUNCHER_WIDGET_RESIZE_STARTED(820),
        LAUNCHER_WIDGET_RESIZE_COMPLETED(824),
        LAUNCHER_WIDGET_RECONFIGURED(821),
        LAUNCHER_THEMED_ICON_ENABLED(836),
        LAUNCHER_THEMED_ICON_DISABLED(837),
        LAUNCHER_TURN_ON_WORK_APPS_TAP(838),
        LAUNCHER_TURN_OFF_WORK_APPS_TAP(839),
        LAUNCHER_ITEM_DROP_FAILED_INSUFFICIENT_SPACE(872),
        LAUNCHER_TASKBAR_LONGPRESS_HIDE(896),
        LAUNCHER_TASKBAR_LONGPRESS_SHOW(897),
        LAUNCHER_ALLAPPS_SEARCHINAPP_LAUNCH(913),
        LAUNCHER_GESTURE_TUTORIAL_BACK_STEP_SHOWN(959),
        LAUNCHER_GESTURE_TUTORIAL_HOME_STEP_SHOWN(960),
        LAUNCHER_GESTURE_TUTORIAL_OVERVIEW_STEP_SHOWN(961),
        LAUNCHER_GESTURE_TUTORIAL_BACK_STEP_COMPLETED(962),
        LAUNCHER_GESTURE_TUTORIAL_HOME_STEP_COMPLETED(963),
        LAUNCHER_GESTURE_TUTORIAL_OVERVIEW_STEP_COMPLETED(964),
        LAUNCHER_GESTURE_TUTORIAL_SKIPPED(965),
        LAUNCHER_ALLAPPS_SCROLLED(985),
        LAUNCHER_TASKBAR_HOME_BUTTON_TAP(PointerIconCompat.TYPE_HELP),
        LAUNCHER_TASKBAR_BACK_BUTTON_TAP(PointerIconCompat.TYPE_WAIT),
        LAUNCHER_TASKBAR_OVERVIEW_BUTTON_TAP(1005),
        LAUNCHER_TASKBAR_IME_SWITCHER_BUTTON_TAP(PointerIconCompat.TYPE_CELL),
        LAUNCHER_TASKBAR_A11Y_BUTTON_TAP(PointerIconCompat.TYPE_CROSSHAIR),
        LAUNCHER_TASKBAR_HOME_BUTTON_LONGPRESS(PointerIconCompat.TYPE_TEXT),
        LAUNCHER_TASKBAR_BACK_BUTTON_LONGPRESS(PointerIconCompat.TYPE_VERTICAL_TEXT),
        LAUNCHER_TASKBAR_OVERVIEW_BUTTON_LONGPRESS(PointerIconCompat.TYPE_ALIAS),
        LAUNCHER_TASKBAR_A11Y_BUTTON_LONGPRESS(PointerIconCompat.TYPE_COPY),
        LAUNCHER_DISMISS_PREDICTION_UNDO(1035),
        LAUNCHER_ALLAPPS_QUICK_SEARCH_WITH_IME(1047),
        LAUNCHER_TASKBAR_ALLAPPS_BUTTON_TAP(1057),
        LAUNCHER_SYSTEM_SHORTCUT_APP_SHARE_TAP(1075);
        
        private final int mId;

        private LauncherEvent(int i) {
            this.mId = i;
        }

        public int getId() {
            return this.mId;
        }
    }

    public enum LauncherRankingEvent implements EventEnum {
        UNKNOWN(0);
        
        private final int mId;

        private LauncherRankingEvent(int i) {
            this.mId = i;
        }

        public int getId() {
            return this.mId;
        }
    }

    public interface StatsLatencyLogger {
        void log(EventEnum eventEnum) {
        }

        StatsLatencyLogger withInstanceId(InstanceId instanceId) {
            return this;
        }

        StatsLatencyLogger withLatency(long j) {
            return this;
        }

        StatsLatencyLogger withPackageId(int i) {
            return this;
        }

        StatsLatencyLogger withQueryLength(int i) {
            return this;
        }

        StatsLatencyLogger withType(LatencyType latencyType) {
            return this;
        }

        public enum LatencyType {
            UNKNOWN(0),
            COLD(1),
            HOT(2),
            TIMEOUT(3),
            FAIL(4),
            COLD_USERWAITING(5);
            
            private final int mId;

            private LatencyType(int i) {
                this.mId = i;
            }

            public int getId() {
                return this.mId;
            }
        }
    }

    public StatsLogger logger() {
        StatsLogger createLogger = createLogger();
        InstanceId instanceId = this.mInstanceId;
        if (instanceId != null) {
            createLogger.withInstanceId(instanceId);
        }
        return createLogger;
    }

    public StatsLatencyLogger latencyLogger() {
        StatsLatencyLogger createLatencyLogger = createLatencyLogger();
        InstanceId instanceId = this.mInstanceId;
        if (instanceId != null) {
            createLatencyLogger.withInstanceId(instanceId);
        }
        return createLatencyLogger;
    }

    /* access modifiers changed from: protected */
    public StatsLogger createLogger() {
        return new StatsLogger() {
        };
    }

    /* access modifiers changed from: protected */
    public StatsLatencyLogger createLatencyLogger() {
        return new StatsLatencyLogger() {
        };
    }

    public StatsLogManager withDefaultInstanceId(InstanceId instanceId) {
        this.mInstanceId = instanceId;
        return this;
    }

    public static StatsLogManager newInstance(Context context) {
        StatsLogManager statsLogManager = (StatsLogManager) ResourceBasedOverride.Overrides.getObject(StatsLogManager.class, context.getApplicationContext(), R.string.stats_log_manager_class);
        statsLogManager.mActivityContext = (ActivityContext) ActivityContext.lookupContextNoThrow(context);
        return statsLogManager;
    }
}
