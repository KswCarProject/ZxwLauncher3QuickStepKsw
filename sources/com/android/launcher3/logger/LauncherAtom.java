package com.android.launcher3.logger;

import com.android.launcher3.logger.LauncherAtomExtensions;
import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import com.google.protobuf.Parser;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class LauncherAtom {

    public interface AllAppsContainerOrBuilder extends MessageLiteOrBuilder {
        AllAppsContainer.ParentContainerCase getParentContainerCase();

        TaskBarContainer getTaskbarContainer();

        boolean hasTaskbarContainer();
    }

    public interface ApplicationOrBuilder extends MessageLiteOrBuilder {
        String getComponentName();

        ByteString getComponentNameBytes();

        String getPackageName();

        ByteString getPackageNameBytes();

        boolean hasComponentName();

        boolean hasPackageName();
    }

    public interface ContainerInfoOrBuilder extends MessageLiteOrBuilder {
        AllAppsContainer getAllAppsContainer();

        ContainerInfo.ContainerCase getContainerCase();

        LauncherAtomExtensions.ExtendedContainers getExtendedContainers();

        FolderContainer getFolder();

        HotseatContainer getHotseat();

        PredictedHotseatContainer getPredictedHotseatContainer();

        PredictionContainer getPredictionContainer();

        SearchResultContainer getSearchResultContainer();

        SettingsContainer getSettingsContainer();

        ShortcutsContainer getShortcutsContainer();

        TaskBarContainer getTaskBarContainer();

        TaskSwitcherContainer getTaskSwitcherContainer();

        WallpapersContainer getWallpapersContainer();

        WidgetsContainer getWidgetsContainer();

        WorkspaceContainer getWorkspace();

        boolean hasAllAppsContainer();

        boolean hasExtendedContainers();

        boolean hasFolder();

        boolean hasHotseat();

        boolean hasPredictedHotseatContainer();

        boolean hasPredictionContainer();

        boolean hasSearchResultContainer();

        boolean hasSettingsContainer();

        boolean hasShortcutsContainer();

        boolean hasTaskBarContainer();

        boolean hasTaskSwitcherContainer();

        boolean hasWallpapersContainer();

        boolean hasWidgetsContainer();

        boolean hasWorkspace();
    }

    public interface FolderContainerOrBuilder extends MessageLiteOrBuilder {
        int getGridX();

        int getGridY();

        HotseatContainer getHotseat();

        int getPageIndex();

        FolderContainer.ParentContainerCase getParentContainerCase();

        TaskBarContainer getTaskbar();

        WorkspaceContainer getWorkspace();

        boolean hasGridX();

        boolean hasGridY();

        boolean hasHotseat();

        boolean hasPageIndex();

        boolean hasTaskbar();

        boolean hasWorkspace();
    }

    public interface FolderIconOrBuilder extends MessageLiteOrBuilder {
        int getCardinality();

        FromState getFromLabelState();

        String getLabelInfo();

        ByteString getLabelInfoBytes();

        ToState getToLabelState();

        boolean hasCardinality();

        boolean hasFromLabelState();

        boolean hasLabelInfo();

        boolean hasToLabelState();
    }

    public interface HotseatContainerOrBuilder extends MessageLiteOrBuilder {
        int getIndex();

        boolean hasIndex();
    }

    public interface ItemInfoOrBuilder extends MessageLiteOrBuilder {
        Application getApplication();

        ContainerInfo getContainerInfo();

        FolderIcon getFolderIcon();

        boolean getIsWork();

        Attribute getItemAttributes(int i);

        int getItemAttributesCount();

        List<Attribute> getItemAttributesList();

        ItemInfo.ItemCase getItemCase();

        int getRank();

        SearchActionItem getSearchActionItem();

        Shortcut getShortcut();

        Slice getSlice();

        Task getTask();

        Widget getWidget();

        boolean hasApplication();

        boolean hasContainerInfo();

        boolean hasFolderIcon();

        boolean hasIsWork();

        boolean hasRank();

        boolean hasSearchActionItem();

        boolean hasShortcut();

        boolean hasSlice();

        boolean hasTask();

        boolean hasWidget();
    }

    public interface LauncherAttributesOrBuilder extends MessageLiteOrBuilder {
        int getItemAttributes(int i);

        int getItemAttributesCount();

        List<Integer> getItemAttributesList();
    }

    public interface PredictedHotseatContainerOrBuilder extends MessageLiteOrBuilder {
        int getCardinality();

        int getIndex();

        boolean hasCardinality();

        boolean hasIndex();
    }

    public interface PredictionContainerOrBuilder extends MessageLiteOrBuilder {
        PredictionContainer.ParentContainerCase getParentContainerCase();

        TaskBarContainer getTaskbarContainer();

        boolean hasTaskbarContainer();
    }

    public interface SearchActionItemOrBuilder extends MessageLiteOrBuilder {
        String getPackageName();

        ByteString getPackageNameBytes();

        String getTitle();

        ByteString getTitleBytes();

        boolean hasPackageName();

        boolean hasTitle();
    }

    public interface SearchResultContainerOrBuilder extends MessageLiteOrBuilder {
        AllAppsContainer getAllAppsContainer();

        SearchResultContainer.ParentContainerCase getParentContainerCase();

        int getQueryLength();

        WorkspaceContainer getWorkspace();

        boolean hasAllAppsContainer();

        boolean hasQueryLength();

        boolean hasWorkspace();
    }

    public interface SettingsContainerOrBuilder extends MessageLiteOrBuilder {
    }

    public interface ShortcutOrBuilder extends MessageLiteOrBuilder {
        String getShortcutId();

        ByteString getShortcutIdBytes();

        String getShortcutName();

        ByteString getShortcutNameBytes();

        boolean hasShortcutId();

        boolean hasShortcutName();
    }

    public interface ShortcutsContainerOrBuilder extends MessageLiteOrBuilder {
    }

    public interface SliceOrBuilder extends MessageLiteOrBuilder {
        String getUri();

        ByteString getUriBytes();

        boolean hasUri();
    }

    public interface TaskBarContainerOrBuilder extends MessageLiteOrBuilder {
        int getCardinality();

        int getIndex();

        boolean hasCardinality();

        boolean hasIndex();
    }

    public interface TaskOrBuilder extends MessageLiteOrBuilder {
        String getComponentName();

        ByteString getComponentNameBytes();

        int getIndex();

        String getPackageName();

        ByteString getPackageNameBytes();

        boolean hasComponentName();

        boolean hasIndex();

        boolean hasPackageName();
    }

    public interface TaskSwitcherContainerOrBuilder extends MessageLiteOrBuilder {
    }

    public interface WallpapersContainerOrBuilder extends MessageLiteOrBuilder {
        int getCardinality();

        boolean hasCardinality();
    }

    public interface WidgetOrBuilder extends MessageLiteOrBuilder {
        int getAppWidgetId();

        String getComponentName();

        ByteString getComponentNameBytes();

        String getPackageName();

        ByteString getPackageNameBytes();

        int getSpanX();

        int getSpanY();

        int getWidgetFeatures();

        boolean hasAppWidgetId();

        boolean hasComponentName();

        boolean hasPackageName();

        boolean hasSpanX();

        boolean hasSpanY();

        boolean hasWidgetFeatures();
    }

    public interface WidgetsContainerOrBuilder extends MessageLiteOrBuilder {
    }

    public interface WorkspaceContainerOrBuilder extends MessageLiteOrBuilder {
        int getGridX();

        int getGridY();

        int getPageIndex();

        boolean hasGridX();

        boolean hasGridY();

        boolean hasPageIndex();
    }

    public static void registerAllExtensions(ExtensionRegistryLite extensionRegistryLite) {
    }

    private LauncherAtom() {
    }

    public enum Attribute implements Internal.EnumLite {
        UNKNOWN(0),
        DEFAULT_LAYOUT(1),
        BACKUP_RESTORE(2),
        PINITEM(3),
        ALLAPPS_ATOZ(4),
        WIDGETS(5),
        ADD_TO_HOMESCREEN(6),
        ALLAPPS_PREDICTION(7),
        HOTSEAT_PREDICTION(8),
        SUGGESTED_LABEL(9),
        MANUAL_LABEL(10),
        UNLABELED(11),
        EMPTY_LABEL(12),
        ALL_APPS_SEARCH_RESULT_APPLICATION(13),
        ALL_APPS_SEARCH_RESULT_SHORTCUT(14),
        ALL_APPS_SEARCH_RESULT_PEOPLE(15),
        ALL_APPS_SEARCH_RESULT_ACTION(16),
        ALL_APPS_SEARCH_RESULT_SETTING(17),
        ALL_APPS_SEARCH_RESULT_SCREENSHOT(18),
        ALL_APPS_SEARCH_RESULT_SLICE(19),
        ALL_APPS_SEARCH_RESULT_WIDGETS(20),
        ALL_APPS_SEARCH_RESULT_PLAY(21),
        ALL_APPS_SEARCH_RESULT_SUGGEST(22),
        ALL_APPS_SEARCH_RESULT_ASSISTANT(23),
        ALL_APPS_SEARCH_RESULT_CHROMETAB(24),
        ALL_APPS_SEARCH_RESULT_NAVVYSITE(25),
        ALL_APPS_SEARCH_RESULT_TIPS(26),
        ALL_APPS_SEARCH_RESULT_PEOPLE_TILE(27),
        ALL_APPS_SEARCH_RESULT_LEGACY_SHORTCUT(30),
        ALL_APPS_SEARCH_RESULT_ASSISTANT_MEMORY(31),
        ALL_APPS_SEARCH_RESULT_WEB_SUGGEST(39),
        WEB_SEARCH_RESULT_QUERY(32),
        WEB_SEARCH_RESULT_TRENDING(33),
        WEB_SEARCH_RESULT_ENTITY(34),
        WEB_SEARCH_RESULT_ANSWER(35),
        WEB_SEARCH_RESULT_PERSONAL(36),
        WEB_SEARCH_RESULT_CALCULATOR(37),
        WEB_SEARCH_RESULT_URL(38),
        WIDGETS_BOTTOM_TRAY(28),
        WIDGETS_TRAY_PREDICTION(29);
        
        public static final int ADD_TO_HOMESCREEN_VALUE = 6;
        public static final int ALLAPPS_ATOZ_VALUE = 4;
        public static final int ALLAPPS_PREDICTION_VALUE = 7;
        public static final int ALL_APPS_SEARCH_RESULT_ACTION_VALUE = 16;
        public static final int ALL_APPS_SEARCH_RESULT_APPLICATION_VALUE = 13;
        public static final int ALL_APPS_SEARCH_RESULT_ASSISTANT_MEMORY_VALUE = 31;
        public static final int ALL_APPS_SEARCH_RESULT_ASSISTANT_VALUE = 23;
        public static final int ALL_APPS_SEARCH_RESULT_CHROMETAB_VALUE = 24;
        public static final int ALL_APPS_SEARCH_RESULT_LEGACY_SHORTCUT_VALUE = 30;
        public static final int ALL_APPS_SEARCH_RESULT_NAVVYSITE_VALUE = 25;
        public static final int ALL_APPS_SEARCH_RESULT_PEOPLE_TILE_VALUE = 27;
        public static final int ALL_APPS_SEARCH_RESULT_PEOPLE_VALUE = 15;
        public static final int ALL_APPS_SEARCH_RESULT_PLAY_VALUE = 21;
        public static final int ALL_APPS_SEARCH_RESULT_SCREENSHOT_VALUE = 18;
        public static final int ALL_APPS_SEARCH_RESULT_SETTING_VALUE = 17;
        public static final int ALL_APPS_SEARCH_RESULT_SHORTCUT_VALUE = 14;
        public static final int ALL_APPS_SEARCH_RESULT_SLICE_VALUE = 19;
        public static final int ALL_APPS_SEARCH_RESULT_SUGGEST_VALUE = 22;
        public static final int ALL_APPS_SEARCH_RESULT_TIPS_VALUE = 26;
        public static final int ALL_APPS_SEARCH_RESULT_WEB_SUGGEST_VALUE = 39;
        public static final int ALL_APPS_SEARCH_RESULT_WIDGETS_VALUE = 20;
        public static final int BACKUP_RESTORE_VALUE = 2;
        public static final int DEFAULT_LAYOUT_VALUE = 1;
        public static final int EMPTY_LABEL_VALUE = 12;
        public static final int HOTSEAT_PREDICTION_VALUE = 8;
        public static final int MANUAL_LABEL_VALUE = 10;
        public static final int PINITEM_VALUE = 3;
        public static final int SUGGESTED_LABEL_VALUE = 9;
        public static final int UNKNOWN_VALUE = 0;
        public static final int UNLABELED_VALUE = 11;
        public static final int WEB_SEARCH_RESULT_ANSWER_VALUE = 35;
        public static final int WEB_SEARCH_RESULT_CALCULATOR_VALUE = 37;
        public static final int WEB_SEARCH_RESULT_ENTITY_VALUE = 34;
        public static final int WEB_SEARCH_RESULT_PERSONAL_VALUE = 36;
        public static final int WEB_SEARCH_RESULT_QUERY_VALUE = 32;
        public static final int WEB_SEARCH_RESULT_TRENDING_VALUE = 33;
        public static final int WEB_SEARCH_RESULT_URL_VALUE = 38;
        public static final int WIDGETS_BOTTOM_TRAY_VALUE = 28;
        public static final int WIDGETS_TRAY_PREDICTION_VALUE = 29;
        public static final int WIDGETS_VALUE = 5;
        private static final Internal.EnumLiteMap<Attribute> internalValueMap = null;
        private final int value;

        static {
            internalValueMap = new Internal.EnumLiteMap<Attribute>() {
                public Attribute findValueByNumber(int i) {
                    return Attribute.forNumber(i);
                }
            };
        }

        public final int getNumber() {
            return this.value;
        }

        @Deprecated
        public static Attribute valueOf(int i) {
            return forNumber(i);
        }

        public static Attribute forNumber(int i) {
            switch (i) {
                case 0:
                    return UNKNOWN;
                case 1:
                    return DEFAULT_LAYOUT;
                case 2:
                    return BACKUP_RESTORE;
                case 3:
                    return PINITEM;
                case 4:
                    return ALLAPPS_ATOZ;
                case 5:
                    return WIDGETS;
                case 6:
                    return ADD_TO_HOMESCREEN;
                case 7:
                    return ALLAPPS_PREDICTION;
                case 8:
                    return HOTSEAT_PREDICTION;
                case 9:
                    return SUGGESTED_LABEL;
                case 10:
                    return MANUAL_LABEL;
                case 11:
                    return UNLABELED;
                case 12:
                    return EMPTY_LABEL;
                case 13:
                    return ALL_APPS_SEARCH_RESULT_APPLICATION;
                case 14:
                    return ALL_APPS_SEARCH_RESULT_SHORTCUT;
                case 15:
                    return ALL_APPS_SEARCH_RESULT_PEOPLE;
                case 16:
                    return ALL_APPS_SEARCH_RESULT_ACTION;
                case 17:
                    return ALL_APPS_SEARCH_RESULT_SETTING;
                case 18:
                    return ALL_APPS_SEARCH_RESULT_SCREENSHOT;
                case 19:
                    return ALL_APPS_SEARCH_RESULT_SLICE;
                case 20:
                    return ALL_APPS_SEARCH_RESULT_WIDGETS;
                case 21:
                    return ALL_APPS_SEARCH_RESULT_PLAY;
                case 22:
                    return ALL_APPS_SEARCH_RESULT_SUGGEST;
                case 23:
                    return ALL_APPS_SEARCH_RESULT_ASSISTANT;
                case 24:
                    return ALL_APPS_SEARCH_RESULT_CHROMETAB;
                case 25:
                    return ALL_APPS_SEARCH_RESULT_NAVVYSITE;
                case 26:
                    return ALL_APPS_SEARCH_RESULT_TIPS;
                case 27:
                    return ALL_APPS_SEARCH_RESULT_PEOPLE_TILE;
                case 28:
                    return WIDGETS_BOTTOM_TRAY;
                case 29:
                    return WIDGETS_TRAY_PREDICTION;
                case 30:
                    return ALL_APPS_SEARCH_RESULT_LEGACY_SHORTCUT;
                case 31:
                    return ALL_APPS_SEARCH_RESULT_ASSISTANT_MEMORY;
                case 32:
                    return WEB_SEARCH_RESULT_QUERY;
                case 33:
                    return WEB_SEARCH_RESULT_TRENDING;
                case 34:
                    return WEB_SEARCH_RESULT_ENTITY;
                case 35:
                    return WEB_SEARCH_RESULT_ANSWER;
                case 36:
                    return WEB_SEARCH_RESULT_PERSONAL;
                case 37:
                    return WEB_SEARCH_RESULT_CALCULATOR;
                case 38:
                    return WEB_SEARCH_RESULT_URL;
                case 39:
                    return ALL_APPS_SEARCH_RESULT_WEB_SUGGEST;
                default:
                    return null;
            }
        }

        public static Internal.EnumLiteMap<Attribute> internalGetValueMap() {
            return internalValueMap;
        }

        private Attribute(int i) {
            this.value = i;
        }
    }

    public enum FromState implements Internal.EnumLite {
        FROM_STATE_UNSPECIFIED(0),
        FROM_EMPTY(1),
        FROM_CUSTOM(2),
        FROM_SUGGESTED(3);
        
        public static final int FROM_CUSTOM_VALUE = 2;
        public static final int FROM_EMPTY_VALUE = 1;
        public static final int FROM_STATE_UNSPECIFIED_VALUE = 0;
        public static final int FROM_SUGGESTED_VALUE = 3;
        private static final Internal.EnumLiteMap<FromState> internalValueMap = null;
        private final int value;

        static {
            internalValueMap = new Internal.EnumLiteMap<FromState>() {
                public FromState findValueByNumber(int i) {
                    return FromState.forNumber(i);
                }
            };
        }

        public final int getNumber() {
            return this.value;
        }

        @Deprecated
        public static FromState valueOf(int i) {
            return forNumber(i);
        }

        public static FromState forNumber(int i) {
            if (i == 0) {
                return FROM_STATE_UNSPECIFIED;
            }
            if (i == 1) {
                return FROM_EMPTY;
            }
            if (i == 2) {
                return FROM_CUSTOM;
            }
            if (i != 3) {
                return null;
            }
            return FROM_SUGGESTED;
        }

        public static Internal.EnumLiteMap<FromState> internalGetValueMap() {
            return internalValueMap;
        }

        private FromState(int i) {
            this.value = i;
        }
    }

    public enum ToState implements Internal.EnumLite {
        TO_STATE_UNSPECIFIED(0),
        UNCHANGED(1),
        TO_SUGGESTION0(2),
        TO_SUGGESTION1_WITH_VALID_PRIMARY(3),
        TO_SUGGESTION1_WITH_EMPTY_PRIMARY(4),
        TO_SUGGESTION2_WITH_VALID_PRIMARY(5),
        TO_SUGGESTION2_WITH_EMPTY_PRIMARY(6),
        TO_SUGGESTION3_WITH_VALID_PRIMARY(7),
        TO_SUGGESTION3_WITH_EMPTY_PRIMARY(8),
        TO_EMPTY_WITH_VALID_PRIMARY(9),
        TO_EMPTY_WITH_VALID_SUGGESTIONS_AND_EMPTY_PRIMARY(10),
        TO_EMPTY_WITH_EMPTY_SUGGESTIONS(11),
        TO_EMPTY_WITH_SUGGESTIONS_DISABLED(12),
        TO_CUSTOM_WITH_VALID_PRIMARY(13),
        TO_CUSTOM_WITH_VALID_SUGGESTIONS_AND_EMPTY_PRIMARY(14),
        TO_CUSTOM_WITH_EMPTY_SUGGESTIONS(15),
        TO_CUSTOM_WITH_SUGGESTIONS_DISABLED(16);
        
        public static final int TO_CUSTOM_WITH_EMPTY_SUGGESTIONS_VALUE = 15;
        public static final int TO_CUSTOM_WITH_SUGGESTIONS_DISABLED_VALUE = 16;
        public static final int TO_CUSTOM_WITH_VALID_PRIMARY_VALUE = 13;
        public static final int TO_CUSTOM_WITH_VALID_SUGGESTIONS_AND_EMPTY_PRIMARY_VALUE = 14;
        public static final int TO_EMPTY_WITH_EMPTY_SUGGESTIONS_VALUE = 11;
        public static final int TO_EMPTY_WITH_SUGGESTIONS_DISABLED_VALUE = 12;
        public static final int TO_EMPTY_WITH_VALID_PRIMARY_VALUE = 9;
        public static final int TO_EMPTY_WITH_VALID_SUGGESTIONS_AND_EMPTY_PRIMARY_VALUE = 10;
        public static final int TO_STATE_UNSPECIFIED_VALUE = 0;
        public static final int TO_SUGGESTION0_VALUE = 2;
        public static final int TO_SUGGESTION1_WITH_EMPTY_PRIMARY_VALUE = 4;
        public static final int TO_SUGGESTION1_WITH_VALID_PRIMARY_VALUE = 3;
        public static final int TO_SUGGESTION2_WITH_EMPTY_PRIMARY_VALUE = 6;
        public static final int TO_SUGGESTION2_WITH_VALID_PRIMARY_VALUE = 5;
        public static final int TO_SUGGESTION3_WITH_EMPTY_PRIMARY_VALUE = 8;
        public static final int TO_SUGGESTION3_WITH_VALID_PRIMARY_VALUE = 7;
        public static final int UNCHANGED_VALUE = 1;
        private static final Internal.EnumLiteMap<ToState> internalValueMap = null;
        private final int value;

        static {
            internalValueMap = new Internal.EnumLiteMap<ToState>() {
                public ToState findValueByNumber(int i) {
                    return ToState.forNumber(i);
                }
            };
        }

        public final int getNumber() {
            return this.value;
        }

        @Deprecated
        public static ToState valueOf(int i) {
            return forNumber(i);
        }

        public static ToState forNumber(int i) {
            switch (i) {
                case 0:
                    return TO_STATE_UNSPECIFIED;
                case 1:
                    return UNCHANGED;
                case 2:
                    return TO_SUGGESTION0;
                case 3:
                    return TO_SUGGESTION1_WITH_VALID_PRIMARY;
                case 4:
                    return TO_SUGGESTION1_WITH_EMPTY_PRIMARY;
                case 5:
                    return TO_SUGGESTION2_WITH_VALID_PRIMARY;
                case 6:
                    return TO_SUGGESTION2_WITH_EMPTY_PRIMARY;
                case 7:
                    return TO_SUGGESTION3_WITH_VALID_PRIMARY;
                case 8:
                    return TO_SUGGESTION3_WITH_EMPTY_PRIMARY;
                case 9:
                    return TO_EMPTY_WITH_VALID_PRIMARY;
                case 10:
                    return TO_EMPTY_WITH_VALID_SUGGESTIONS_AND_EMPTY_PRIMARY;
                case 11:
                    return TO_EMPTY_WITH_EMPTY_SUGGESTIONS;
                case 12:
                    return TO_EMPTY_WITH_SUGGESTIONS_DISABLED;
                case 13:
                    return TO_CUSTOM_WITH_VALID_PRIMARY;
                case 14:
                    return TO_CUSTOM_WITH_VALID_SUGGESTIONS_AND_EMPTY_PRIMARY;
                case 15:
                    return TO_CUSTOM_WITH_EMPTY_SUGGESTIONS;
                case 16:
                    return TO_CUSTOM_WITH_SUGGESTIONS_DISABLED;
                default:
                    return null;
            }
        }

        public static Internal.EnumLiteMap<ToState> internalGetValueMap() {
            return internalValueMap;
        }

        private ToState(int i) {
            this.value = i;
        }
    }

    public static final class ItemInfo extends GeneratedMessageLite<ItemInfo, Builder> implements ItemInfoOrBuilder {
        public static final int APPLICATION_FIELD_NUMBER = 1;
        public static final int CONTAINER_INFO_FIELD_NUMBER = 7;
        /* access modifiers changed from: private */
        public static final ItemInfo DEFAULT_INSTANCE;
        public static final int FOLDER_ICON_FIELD_NUMBER = 9;
        public static final int IS_WORK_FIELD_NUMBER = 6;
        public static final int ITEM_ATTRIBUTES_FIELD_NUMBER = 12;
        private static volatile Parser<ItemInfo> PARSER = null;
        public static final int RANK_FIELD_NUMBER = 5;
        public static final int SEARCH_ACTION_ITEM_FIELD_NUMBER = 11;
        public static final int SHORTCUT_FIELD_NUMBER = 3;
        public static final int SLICE_FIELD_NUMBER = 10;
        public static final int TASK_FIELD_NUMBER = 2;
        public static final int WIDGET_FIELD_NUMBER = 4;
        private static final Internal.ListAdapter.Converter<Integer, Attribute> itemAttributes_converter_ = new Internal.ListAdapter.Converter<Integer, Attribute>() {
            public Attribute convert(Integer num) {
                Attribute forNumber = Attribute.forNumber(num.intValue());
                return forNumber == null ? Attribute.UNKNOWN : forNumber;
            }
        };
        private int bitField0_;
        private ContainerInfo containerInfo_;
        private boolean isWork_;
        private Internal.IntList itemAttributes_ = emptyIntList();
        private int itemCase_ = 0;
        private Object item_;
        private int rank_;

        private ItemInfo() {
        }

        public enum ItemCase implements Internal.EnumLite {
            APPLICATION(1),
            TASK(2),
            SHORTCUT(3),
            WIDGET(4),
            FOLDER_ICON(9),
            SLICE(10),
            SEARCH_ACTION_ITEM(11),
            ITEM_NOT_SET(0);
            
            private final int value;

            private ItemCase(int i) {
                this.value = i;
            }

            @Deprecated
            public static ItemCase valueOf(int i) {
                return forNumber(i);
            }

            public static ItemCase forNumber(int i) {
                if (i == 0) {
                    return ITEM_NOT_SET;
                }
                if (i == 1) {
                    return APPLICATION;
                }
                if (i == 2) {
                    return TASK;
                }
                if (i == 3) {
                    return SHORTCUT;
                }
                if (i == 4) {
                    return WIDGET;
                }
                switch (i) {
                    case 9:
                        return FOLDER_ICON;
                    case 10:
                        return SLICE;
                    case 11:
                        return SEARCH_ACTION_ITEM;
                    default:
                        return null;
                }
            }

            public int getNumber() {
                return this.value;
            }
        }

        public ItemCase getItemCase() {
            return ItemCase.forNumber(this.itemCase_);
        }

        /* access modifiers changed from: private */
        public void clearItem() {
            this.itemCase_ = 0;
            this.item_ = null;
        }

        public boolean hasApplication() {
            return this.itemCase_ == 1;
        }

        public Application getApplication() {
            if (this.itemCase_ == 1) {
                return (Application) this.item_;
            }
            return Application.getDefaultInstance();
        }

        /* access modifiers changed from: private */
        public void setApplication(Application application) {
            Objects.requireNonNull(application);
            this.item_ = application;
            this.itemCase_ = 1;
        }

        /* access modifiers changed from: private */
        public void setApplication(Application.Builder builder) {
            this.item_ = builder.build();
            this.itemCase_ = 1;
        }

        /* access modifiers changed from: private */
        public void mergeApplication(Application application) {
            if (this.itemCase_ != 1 || this.item_ == Application.getDefaultInstance()) {
                this.item_ = application;
            } else {
                this.item_ = ((Application.Builder) Application.newBuilder((Application) this.item_).mergeFrom(application)).buildPartial();
            }
            this.itemCase_ = 1;
        }

        /* access modifiers changed from: private */
        public void clearApplication() {
            if (this.itemCase_ == 1) {
                this.itemCase_ = 0;
                this.item_ = null;
            }
        }

        public boolean hasTask() {
            return this.itemCase_ == 2;
        }

        public Task getTask() {
            if (this.itemCase_ == 2) {
                return (Task) this.item_;
            }
            return Task.getDefaultInstance();
        }

        /* access modifiers changed from: private */
        public void setTask(Task task) {
            Objects.requireNonNull(task);
            this.item_ = task;
            this.itemCase_ = 2;
        }

        /* access modifiers changed from: private */
        public void setTask(Task.Builder builder) {
            this.item_ = builder.build();
            this.itemCase_ = 2;
        }

        /* access modifiers changed from: private */
        public void mergeTask(Task task) {
            if (this.itemCase_ != 2 || this.item_ == Task.getDefaultInstance()) {
                this.item_ = task;
            } else {
                this.item_ = ((Task.Builder) Task.newBuilder((Task) this.item_).mergeFrom(task)).buildPartial();
            }
            this.itemCase_ = 2;
        }

        /* access modifiers changed from: private */
        public void clearTask() {
            if (this.itemCase_ == 2) {
                this.itemCase_ = 0;
                this.item_ = null;
            }
        }

        public boolean hasShortcut() {
            return this.itemCase_ == 3;
        }

        public Shortcut getShortcut() {
            if (this.itemCase_ == 3) {
                return (Shortcut) this.item_;
            }
            return Shortcut.getDefaultInstance();
        }

        /* access modifiers changed from: private */
        public void setShortcut(Shortcut shortcut) {
            Objects.requireNonNull(shortcut);
            this.item_ = shortcut;
            this.itemCase_ = 3;
        }

        /* access modifiers changed from: private */
        public void setShortcut(Shortcut.Builder builder) {
            this.item_ = builder.build();
            this.itemCase_ = 3;
        }

        /* access modifiers changed from: private */
        public void mergeShortcut(Shortcut shortcut) {
            if (this.itemCase_ != 3 || this.item_ == Shortcut.getDefaultInstance()) {
                this.item_ = shortcut;
            } else {
                this.item_ = ((Shortcut.Builder) Shortcut.newBuilder((Shortcut) this.item_).mergeFrom(shortcut)).buildPartial();
            }
            this.itemCase_ = 3;
        }

        /* access modifiers changed from: private */
        public void clearShortcut() {
            if (this.itemCase_ == 3) {
                this.itemCase_ = 0;
                this.item_ = null;
            }
        }

        public boolean hasWidget() {
            return this.itemCase_ == 4;
        }

        public Widget getWidget() {
            if (this.itemCase_ == 4) {
                return (Widget) this.item_;
            }
            return Widget.getDefaultInstance();
        }

        /* access modifiers changed from: private */
        public void setWidget(Widget widget) {
            Objects.requireNonNull(widget);
            this.item_ = widget;
            this.itemCase_ = 4;
        }

        /* access modifiers changed from: private */
        public void setWidget(Widget.Builder builder) {
            this.item_ = builder.build();
            this.itemCase_ = 4;
        }

        /* access modifiers changed from: private */
        public void mergeWidget(Widget widget) {
            if (this.itemCase_ != 4 || this.item_ == Widget.getDefaultInstance()) {
                this.item_ = widget;
            } else {
                this.item_ = ((Widget.Builder) Widget.newBuilder((Widget) this.item_).mergeFrom(widget)).buildPartial();
            }
            this.itemCase_ = 4;
        }

        /* access modifiers changed from: private */
        public void clearWidget() {
            if (this.itemCase_ == 4) {
                this.itemCase_ = 0;
                this.item_ = null;
            }
        }

        public boolean hasFolderIcon() {
            return this.itemCase_ == 9;
        }

        public FolderIcon getFolderIcon() {
            if (this.itemCase_ == 9) {
                return (FolderIcon) this.item_;
            }
            return FolderIcon.getDefaultInstance();
        }

        /* access modifiers changed from: private */
        public void setFolderIcon(FolderIcon folderIcon) {
            Objects.requireNonNull(folderIcon);
            this.item_ = folderIcon;
            this.itemCase_ = 9;
        }

        /* access modifiers changed from: private */
        public void setFolderIcon(FolderIcon.Builder builder) {
            this.item_ = builder.build();
            this.itemCase_ = 9;
        }

        /* access modifiers changed from: private */
        public void mergeFolderIcon(FolderIcon folderIcon) {
            if (this.itemCase_ != 9 || this.item_ == FolderIcon.getDefaultInstance()) {
                this.item_ = folderIcon;
            } else {
                this.item_ = ((FolderIcon.Builder) FolderIcon.newBuilder((FolderIcon) this.item_).mergeFrom(folderIcon)).buildPartial();
            }
            this.itemCase_ = 9;
        }

        /* access modifiers changed from: private */
        public void clearFolderIcon() {
            if (this.itemCase_ == 9) {
                this.itemCase_ = 0;
                this.item_ = null;
            }
        }

        public boolean hasSlice() {
            return this.itemCase_ == 10;
        }

        public Slice getSlice() {
            if (this.itemCase_ == 10) {
                return (Slice) this.item_;
            }
            return Slice.getDefaultInstance();
        }

        /* access modifiers changed from: private */
        public void setSlice(Slice slice) {
            Objects.requireNonNull(slice);
            this.item_ = slice;
            this.itemCase_ = 10;
        }

        /* access modifiers changed from: private */
        public void setSlice(Slice.Builder builder) {
            this.item_ = builder.build();
            this.itemCase_ = 10;
        }

        /* access modifiers changed from: private */
        public void mergeSlice(Slice slice) {
            if (this.itemCase_ != 10 || this.item_ == Slice.getDefaultInstance()) {
                this.item_ = slice;
            } else {
                this.item_ = ((Slice.Builder) Slice.newBuilder((Slice) this.item_).mergeFrom(slice)).buildPartial();
            }
            this.itemCase_ = 10;
        }

        /* access modifiers changed from: private */
        public void clearSlice() {
            if (this.itemCase_ == 10) {
                this.itemCase_ = 0;
                this.item_ = null;
            }
        }

        public boolean hasSearchActionItem() {
            return this.itemCase_ == 11;
        }

        public SearchActionItem getSearchActionItem() {
            if (this.itemCase_ == 11) {
                return (SearchActionItem) this.item_;
            }
            return SearchActionItem.getDefaultInstance();
        }

        /* access modifiers changed from: private */
        public void setSearchActionItem(SearchActionItem searchActionItem) {
            Objects.requireNonNull(searchActionItem);
            this.item_ = searchActionItem;
            this.itemCase_ = 11;
        }

        /* access modifiers changed from: private */
        public void setSearchActionItem(SearchActionItem.Builder builder) {
            this.item_ = builder.build();
            this.itemCase_ = 11;
        }

        /* access modifiers changed from: private */
        public void mergeSearchActionItem(SearchActionItem searchActionItem) {
            if (this.itemCase_ != 11 || this.item_ == SearchActionItem.getDefaultInstance()) {
                this.item_ = searchActionItem;
            } else {
                this.item_ = ((SearchActionItem.Builder) SearchActionItem.newBuilder((SearchActionItem) this.item_).mergeFrom(searchActionItem)).buildPartial();
            }
            this.itemCase_ = 11;
        }

        /* access modifiers changed from: private */
        public void clearSearchActionItem() {
            if (this.itemCase_ == 11) {
                this.itemCase_ = 0;
                this.item_ = null;
            }
        }

        public boolean hasRank() {
            return (this.bitField0_ & 128) == 128;
        }

        public int getRank() {
            return this.rank_;
        }

        /* access modifiers changed from: private */
        public void setRank(int i) {
            this.bitField0_ |= 128;
            this.rank_ = i;
        }

        /* access modifiers changed from: private */
        public void clearRank() {
            this.bitField0_ &= -129;
            this.rank_ = 0;
        }

        public boolean hasIsWork() {
            return (this.bitField0_ & 256) == 256;
        }

        public boolean getIsWork() {
            return this.isWork_;
        }

        /* access modifiers changed from: private */
        public void setIsWork(boolean z) {
            this.bitField0_ |= 256;
            this.isWork_ = z;
        }

        /* access modifiers changed from: private */
        public void clearIsWork() {
            this.bitField0_ &= -257;
            this.isWork_ = false;
        }

        public boolean hasContainerInfo() {
            return (this.bitField0_ & 512) == 512;
        }

        public ContainerInfo getContainerInfo() {
            ContainerInfo containerInfo = this.containerInfo_;
            return containerInfo == null ? ContainerInfo.getDefaultInstance() : containerInfo;
        }

        /* access modifiers changed from: private */
        public void setContainerInfo(ContainerInfo containerInfo) {
            Objects.requireNonNull(containerInfo);
            this.containerInfo_ = containerInfo;
            this.bitField0_ |= 512;
        }

        /* access modifiers changed from: private */
        public void setContainerInfo(ContainerInfo.Builder builder) {
            this.containerInfo_ = (ContainerInfo) builder.build();
            this.bitField0_ |= 512;
        }

        /* access modifiers changed from: private */
        public void mergeContainerInfo(ContainerInfo containerInfo) {
            ContainerInfo containerInfo2 = this.containerInfo_;
            if (containerInfo2 == null || containerInfo2 == ContainerInfo.getDefaultInstance()) {
                this.containerInfo_ = containerInfo;
            } else {
                this.containerInfo_ = (ContainerInfo) ((ContainerInfo.Builder) ContainerInfo.newBuilder(this.containerInfo_).mergeFrom(containerInfo)).buildPartial();
            }
            this.bitField0_ |= 512;
        }

        /* access modifiers changed from: private */
        public void clearContainerInfo() {
            this.containerInfo_ = null;
            this.bitField0_ &= -513;
        }

        static {
            ItemInfo itemInfo = new ItemInfo();
            DEFAULT_INSTANCE = itemInfo;
            itemInfo.makeImmutable();
        }

        public List<Attribute> getItemAttributesList() {
            return new Internal.ListAdapter(this.itemAttributes_, itemAttributes_converter_);
        }

        public int getItemAttributesCount() {
            return this.itemAttributes_.size();
        }

        public Attribute getItemAttributes(int i) {
            return itemAttributes_converter_.convert(Integer.valueOf(this.itemAttributes_.getInt(i)));
        }

        private void ensureItemAttributesIsMutable() {
            if (!this.itemAttributes_.isModifiable()) {
                this.itemAttributes_ = GeneratedMessageLite.mutableCopy(this.itemAttributes_);
            }
        }

        /* access modifiers changed from: private */
        public void setItemAttributes(int i, Attribute attribute) {
            Objects.requireNonNull(attribute);
            ensureItemAttributesIsMutable();
            this.itemAttributes_.setInt(i, attribute.getNumber());
        }

        /* access modifiers changed from: private */
        public void addItemAttributes(Attribute attribute) {
            Objects.requireNonNull(attribute);
            ensureItemAttributesIsMutable();
            this.itemAttributes_.addInt(attribute.getNumber());
        }

        /* access modifiers changed from: private */
        public void addAllItemAttributes(Iterable<? extends Attribute> iterable) {
            ensureItemAttributesIsMutable();
            for (Attribute number : iterable) {
                this.itemAttributes_.addInt(number.getNumber());
            }
        }

        /* access modifiers changed from: private */
        public void clearItemAttributes() {
            this.itemAttributes_ = emptyIntList();
        }

        public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
            if (this.itemCase_ == 1) {
                codedOutputStream.writeMessage(1, (Application) this.item_);
            }
            if (this.itemCase_ == 2) {
                codedOutputStream.writeMessage(2, (Task) this.item_);
            }
            if (this.itemCase_ == 3) {
                codedOutputStream.writeMessage(3, (Shortcut) this.item_);
            }
            if (this.itemCase_ == 4) {
                codedOutputStream.writeMessage(4, (Widget) this.item_);
            }
            if ((this.bitField0_ & 128) == 128) {
                codedOutputStream.writeInt32(5, this.rank_);
            }
            if ((this.bitField0_ & 256) == 256) {
                codedOutputStream.writeBool(6, this.isWork_);
            }
            if ((this.bitField0_ & 512) == 512) {
                codedOutputStream.writeMessage(7, getContainerInfo());
            }
            if (this.itemCase_ == 9) {
                codedOutputStream.writeMessage(9, (FolderIcon) this.item_);
            }
            if (this.itemCase_ == 10) {
                codedOutputStream.writeMessage(10, (Slice) this.item_);
            }
            if (this.itemCase_ == 11) {
                codedOutputStream.writeMessage(11, (SearchActionItem) this.item_);
            }
            for (int i = 0; i < this.itemAttributes_.size(); i++) {
                codedOutputStream.writeEnum(12, this.itemAttributes_.getInt(i));
            }
            this.unknownFields.writeTo(codedOutputStream);
        }

        public int getSerializedSize() {
            int i = this.memoizedSerializedSize;
            if (i != -1) {
                return i;
            }
            int computeMessageSize = this.itemCase_ == 1 ? CodedOutputStream.computeMessageSize(1, (Application) this.item_) + 0 : 0;
            if (this.itemCase_ == 2) {
                computeMessageSize += CodedOutputStream.computeMessageSize(2, (Task) this.item_);
            }
            if (this.itemCase_ == 3) {
                computeMessageSize += CodedOutputStream.computeMessageSize(3, (Shortcut) this.item_);
            }
            if (this.itemCase_ == 4) {
                computeMessageSize += CodedOutputStream.computeMessageSize(4, (Widget) this.item_);
            }
            if ((this.bitField0_ & 128) == 128) {
                computeMessageSize += CodedOutputStream.computeInt32Size(5, this.rank_);
            }
            if ((this.bitField0_ & 256) == 256) {
                computeMessageSize += CodedOutputStream.computeBoolSize(6, this.isWork_);
            }
            if ((this.bitField0_ & 512) == 512) {
                computeMessageSize += CodedOutputStream.computeMessageSize(7, getContainerInfo());
            }
            if (this.itemCase_ == 9) {
                computeMessageSize += CodedOutputStream.computeMessageSize(9, (FolderIcon) this.item_);
            }
            if (this.itemCase_ == 10) {
                computeMessageSize += CodedOutputStream.computeMessageSize(10, (Slice) this.item_);
            }
            if (this.itemCase_ == 11) {
                computeMessageSize += CodedOutputStream.computeMessageSize(11, (SearchActionItem) this.item_);
            }
            int i2 = 0;
            for (int i3 = 0; i3 < this.itemAttributes_.size(); i3++) {
                i2 += CodedOutputStream.computeEnumSizeNoTag(this.itemAttributes_.getInt(i3));
            }
            int size = computeMessageSize + i2 + (this.itemAttributes_.size() * 1) + this.unknownFields.getSerializedSize();
            this.memoizedSerializedSize = size;
            return size;
        }

        public static ItemInfo parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (ItemInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static ItemInfo parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (ItemInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static ItemInfo parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (ItemInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static ItemInfo parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (ItemInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static ItemInfo parseFrom(InputStream inputStream) throws IOException {
            return (ItemInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static ItemInfo parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (ItemInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static ItemInfo parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (ItemInfo) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static ItemInfo parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (ItemInfo) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static ItemInfo parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (ItemInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static ItemInfo parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (ItemInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(ItemInfo itemInfo) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(itemInfo);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<ItemInfo, Builder> implements ItemInfoOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 r1) {
                this();
            }

            private Builder() {
                super(ItemInfo.DEFAULT_INSTANCE);
            }

            public ItemCase getItemCase() {
                return ((ItemInfo) this.instance).getItemCase();
            }

            public Builder clearItem() {
                copyOnWrite();
                ((ItemInfo) this.instance).clearItem();
                return this;
            }

            public boolean hasApplication() {
                return ((ItemInfo) this.instance).hasApplication();
            }

            public Application getApplication() {
                return ((ItemInfo) this.instance).getApplication();
            }

            public Builder setApplication(Application application) {
                copyOnWrite();
                ((ItemInfo) this.instance).setApplication(application);
                return this;
            }

            public Builder setApplication(Application.Builder builder) {
                copyOnWrite();
                ((ItemInfo) this.instance).setApplication(builder);
                return this;
            }

            public Builder mergeApplication(Application application) {
                copyOnWrite();
                ((ItemInfo) this.instance).mergeApplication(application);
                return this;
            }

            public Builder clearApplication() {
                copyOnWrite();
                ((ItemInfo) this.instance).clearApplication();
                return this;
            }

            public boolean hasTask() {
                return ((ItemInfo) this.instance).hasTask();
            }

            public Task getTask() {
                return ((ItemInfo) this.instance).getTask();
            }

            public Builder setTask(Task task) {
                copyOnWrite();
                ((ItemInfo) this.instance).setTask(task);
                return this;
            }

            public Builder setTask(Task.Builder builder) {
                copyOnWrite();
                ((ItemInfo) this.instance).setTask(builder);
                return this;
            }

            public Builder mergeTask(Task task) {
                copyOnWrite();
                ((ItemInfo) this.instance).mergeTask(task);
                return this;
            }

            public Builder clearTask() {
                copyOnWrite();
                ((ItemInfo) this.instance).clearTask();
                return this;
            }

            public boolean hasShortcut() {
                return ((ItemInfo) this.instance).hasShortcut();
            }

            public Shortcut getShortcut() {
                return ((ItemInfo) this.instance).getShortcut();
            }

            public Builder setShortcut(Shortcut shortcut) {
                copyOnWrite();
                ((ItemInfo) this.instance).setShortcut(shortcut);
                return this;
            }

            public Builder setShortcut(Shortcut.Builder builder) {
                copyOnWrite();
                ((ItemInfo) this.instance).setShortcut(builder);
                return this;
            }

            public Builder mergeShortcut(Shortcut shortcut) {
                copyOnWrite();
                ((ItemInfo) this.instance).mergeShortcut(shortcut);
                return this;
            }

            public Builder clearShortcut() {
                copyOnWrite();
                ((ItemInfo) this.instance).clearShortcut();
                return this;
            }

            public boolean hasWidget() {
                return ((ItemInfo) this.instance).hasWidget();
            }

            public Widget getWidget() {
                return ((ItemInfo) this.instance).getWidget();
            }

            public Builder setWidget(Widget widget) {
                copyOnWrite();
                ((ItemInfo) this.instance).setWidget(widget);
                return this;
            }

            public Builder setWidget(Widget.Builder builder) {
                copyOnWrite();
                ((ItemInfo) this.instance).setWidget(builder);
                return this;
            }

            public Builder mergeWidget(Widget widget) {
                copyOnWrite();
                ((ItemInfo) this.instance).mergeWidget(widget);
                return this;
            }

            public Builder clearWidget() {
                copyOnWrite();
                ((ItemInfo) this.instance).clearWidget();
                return this;
            }

            public boolean hasFolderIcon() {
                return ((ItemInfo) this.instance).hasFolderIcon();
            }

            public FolderIcon getFolderIcon() {
                return ((ItemInfo) this.instance).getFolderIcon();
            }

            public Builder setFolderIcon(FolderIcon folderIcon) {
                copyOnWrite();
                ((ItemInfo) this.instance).setFolderIcon(folderIcon);
                return this;
            }

            public Builder setFolderIcon(FolderIcon.Builder builder) {
                copyOnWrite();
                ((ItemInfo) this.instance).setFolderIcon(builder);
                return this;
            }

            public Builder mergeFolderIcon(FolderIcon folderIcon) {
                copyOnWrite();
                ((ItemInfo) this.instance).mergeFolderIcon(folderIcon);
                return this;
            }

            public Builder clearFolderIcon() {
                copyOnWrite();
                ((ItemInfo) this.instance).clearFolderIcon();
                return this;
            }

            public boolean hasSlice() {
                return ((ItemInfo) this.instance).hasSlice();
            }

            public Slice getSlice() {
                return ((ItemInfo) this.instance).getSlice();
            }

            public Builder setSlice(Slice slice) {
                copyOnWrite();
                ((ItemInfo) this.instance).setSlice(slice);
                return this;
            }

            public Builder setSlice(Slice.Builder builder) {
                copyOnWrite();
                ((ItemInfo) this.instance).setSlice(builder);
                return this;
            }

            public Builder mergeSlice(Slice slice) {
                copyOnWrite();
                ((ItemInfo) this.instance).mergeSlice(slice);
                return this;
            }

            public Builder clearSlice() {
                copyOnWrite();
                ((ItemInfo) this.instance).clearSlice();
                return this;
            }

            public boolean hasSearchActionItem() {
                return ((ItemInfo) this.instance).hasSearchActionItem();
            }

            public SearchActionItem getSearchActionItem() {
                return ((ItemInfo) this.instance).getSearchActionItem();
            }

            public Builder setSearchActionItem(SearchActionItem searchActionItem) {
                copyOnWrite();
                ((ItemInfo) this.instance).setSearchActionItem(searchActionItem);
                return this;
            }

            public Builder setSearchActionItem(SearchActionItem.Builder builder) {
                copyOnWrite();
                ((ItemInfo) this.instance).setSearchActionItem(builder);
                return this;
            }

            public Builder mergeSearchActionItem(SearchActionItem searchActionItem) {
                copyOnWrite();
                ((ItemInfo) this.instance).mergeSearchActionItem(searchActionItem);
                return this;
            }

            public Builder clearSearchActionItem() {
                copyOnWrite();
                ((ItemInfo) this.instance).clearSearchActionItem();
                return this;
            }

            public boolean hasRank() {
                return ((ItemInfo) this.instance).hasRank();
            }

            public int getRank() {
                return ((ItemInfo) this.instance).getRank();
            }

            public Builder setRank(int i) {
                copyOnWrite();
                ((ItemInfo) this.instance).setRank(i);
                return this;
            }

            public Builder clearRank() {
                copyOnWrite();
                ((ItemInfo) this.instance).clearRank();
                return this;
            }

            public boolean hasIsWork() {
                return ((ItemInfo) this.instance).hasIsWork();
            }

            public boolean getIsWork() {
                return ((ItemInfo) this.instance).getIsWork();
            }

            public Builder setIsWork(boolean z) {
                copyOnWrite();
                ((ItemInfo) this.instance).setIsWork(z);
                return this;
            }

            public Builder clearIsWork() {
                copyOnWrite();
                ((ItemInfo) this.instance).clearIsWork();
                return this;
            }

            public boolean hasContainerInfo() {
                return ((ItemInfo) this.instance).hasContainerInfo();
            }

            public ContainerInfo getContainerInfo() {
                return ((ItemInfo) this.instance).getContainerInfo();
            }

            public Builder setContainerInfo(ContainerInfo containerInfo) {
                copyOnWrite();
                ((ItemInfo) this.instance).setContainerInfo(containerInfo);
                return this;
            }

            public Builder setContainerInfo(ContainerInfo.Builder builder) {
                copyOnWrite();
                ((ItemInfo) this.instance).setContainerInfo(builder);
                return this;
            }

            public Builder mergeContainerInfo(ContainerInfo containerInfo) {
                copyOnWrite();
                ((ItemInfo) this.instance).mergeContainerInfo(containerInfo);
                return this;
            }

            public Builder clearContainerInfo() {
                copyOnWrite();
                ((ItemInfo) this.instance).clearContainerInfo();
                return this;
            }

            public List<Attribute> getItemAttributesList() {
                return ((ItemInfo) this.instance).getItemAttributesList();
            }

            public int getItemAttributesCount() {
                return ((ItemInfo) this.instance).getItemAttributesCount();
            }

            public Attribute getItemAttributes(int i) {
                return ((ItemInfo) this.instance).getItemAttributes(i);
            }

            public Builder setItemAttributes(int i, Attribute attribute) {
                copyOnWrite();
                ((ItemInfo) this.instance).setItemAttributes(i, attribute);
                return this;
            }

            public Builder addItemAttributes(Attribute attribute) {
                copyOnWrite();
                ((ItemInfo) this.instance).addItemAttributes(attribute);
                return this;
            }

            public Builder addAllItemAttributes(Iterable<? extends Attribute> iterable) {
                copyOnWrite();
                ((ItemInfo) this.instance).addAllItemAttributes(iterable);
                return this;
            }

            public Builder clearItemAttributes() {
                copyOnWrite();
                ((ItemInfo) this.instance).clearItemAttributes();
                return this;
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            boolean z = false;
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new ItemInfo();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    this.itemAttributes_.makeImmutable();
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                    ItemInfo itemInfo = (ItemInfo) obj2;
                    this.rank_ = visitor.visitInt(hasRank(), this.rank_, itemInfo.hasRank(), itemInfo.rank_);
                    this.isWork_ = visitor.visitBoolean(hasIsWork(), this.isWork_, itemInfo.hasIsWork(), itemInfo.isWork_);
                    this.containerInfo_ = (ContainerInfo) visitor.visitMessage(this.containerInfo_, itemInfo.containerInfo_);
                    this.itemAttributes_ = visitor.visitIntList(this.itemAttributes_, itemInfo.itemAttributes_);
                    switch (AnonymousClass1.$SwitchMap$com$android$launcher3$logger$LauncherAtom$ItemInfo$ItemCase[itemInfo.getItemCase().ordinal()]) {
                        case 1:
                            if (this.itemCase_ == 1) {
                                z = true;
                            }
                            this.item_ = visitor.visitOneofMessage(z, this.item_, itemInfo.item_);
                            break;
                        case 2:
                            if (this.itemCase_ == 2) {
                                z = true;
                            }
                            this.item_ = visitor.visitOneofMessage(z, this.item_, itemInfo.item_);
                            break;
                        case 3:
                            if (this.itemCase_ == 3) {
                                z = true;
                            }
                            this.item_ = visitor.visitOneofMessage(z, this.item_, itemInfo.item_);
                            break;
                        case 4:
                            if (this.itemCase_ == 4) {
                                z = true;
                            }
                            this.item_ = visitor.visitOneofMessage(z, this.item_, itemInfo.item_);
                            break;
                        case 5:
                            if (this.itemCase_ == 9) {
                                z = true;
                            }
                            this.item_ = visitor.visitOneofMessage(z, this.item_, itemInfo.item_);
                            break;
                        case 6:
                            if (this.itemCase_ == 10) {
                                z = true;
                            }
                            this.item_ = visitor.visitOneofMessage(z, this.item_, itemInfo.item_);
                            break;
                        case 7:
                            if (this.itemCase_ == 11) {
                                z = true;
                            }
                            this.item_ = visitor.visitOneofMessage(z, this.item_, itemInfo.item_);
                            break;
                        case 8:
                            if (this.itemCase_ != 0) {
                                z = true;
                            }
                            visitor.visitOneofNotSet(z);
                            break;
                    }
                    if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                        int i = itemInfo.itemCase_;
                        if (i != 0) {
                            this.itemCase_ = i;
                        }
                        this.bitField0_ |= itemInfo.bitField0_;
                    }
                    return this;
                case 6:
                    CodedInputStream codedInputStream = (CodedInputStream) obj;
                    ExtensionRegistryLite extensionRegistryLite = (ExtensionRegistryLite) obj2;
                    while (!z) {
                        try {
                            int readTag = codedInputStream.readTag();
                            switch (readTag) {
                                case 0:
                                    z = true;
                                    break;
                                case 10:
                                    Application.Builder builder = this.itemCase_ == 1 ? (Application.Builder) ((Application) this.item_).toBuilder() : null;
                                    MessageLite readMessage = codedInputStream.readMessage(Application.parser(), extensionRegistryLite);
                                    this.item_ = readMessage;
                                    if (builder != null) {
                                        builder.mergeFrom((Application) readMessage);
                                        this.item_ = builder.buildPartial();
                                    }
                                    this.itemCase_ = 1;
                                    break;
                                case 18:
                                    Task.Builder builder2 = this.itemCase_ == 2 ? (Task.Builder) ((Task) this.item_).toBuilder() : null;
                                    MessageLite readMessage2 = codedInputStream.readMessage(Task.parser(), extensionRegistryLite);
                                    this.item_ = readMessage2;
                                    if (builder2 != null) {
                                        builder2.mergeFrom((Task) readMessage2);
                                        this.item_ = builder2.buildPartial();
                                    }
                                    this.itemCase_ = 2;
                                    break;
                                case 26:
                                    Shortcut.Builder builder3 = this.itemCase_ == 3 ? (Shortcut.Builder) ((Shortcut) this.item_).toBuilder() : null;
                                    MessageLite readMessage3 = codedInputStream.readMessage(Shortcut.parser(), extensionRegistryLite);
                                    this.item_ = readMessage3;
                                    if (builder3 != null) {
                                        builder3.mergeFrom((Shortcut) readMessage3);
                                        this.item_ = builder3.buildPartial();
                                    }
                                    this.itemCase_ = 3;
                                    break;
                                case 34:
                                    Widget.Builder builder4 = this.itemCase_ == 4 ? (Widget.Builder) ((Widget) this.item_).toBuilder() : null;
                                    MessageLite readMessage4 = codedInputStream.readMessage(Widget.parser(), extensionRegistryLite);
                                    this.item_ = readMessage4;
                                    if (builder4 != null) {
                                        builder4.mergeFrom((Widget) readMessage4);
                                        this.item_ = builder4.buildPartial();
                                    }
                                    this.itemCase_ = 4;
                                    break;
                                case 40:
                                    this.bitField0_ |= 128;
                                    this.rank_ = codedInputStream.readInt32();
                                    break;
                                case 48:
                                    this.bitField0_ |= 256;
                                    this.isWork_ = codedInputStream.readBool();
                                    break;
                                case 58:
                                    ContainerInfo.Builder builder5 = (this.bitField0_ & 512) == 512 ? (ContainerInfo.Builder) this.containerInfo_.toBuilder() : null;
                                    ContainerInfo containerInfo = (ContainerInfo) codedInputStream.readMessage(ContainerInfo.parser(), extensionRegistryLite);
                                    this.containerInfo_ = containerInfo;
                                    if (builder5 != null) {
                                        builder5.mergeFrom(containerInfo);
                                        this.containerInfo_ = (ContainerInfo) builder5.buildPartial();
                                    }
                                    this.bitField0_ |= 512;
                                    break;
                                case 74:
                                    FolderIcon.Builder builder6 = this.itemCase_ == 9 ? (FolderIcon.Builder) ((FolderIcon) this.item_).toBuilder() : null;
                                    MessageLite readMessage5 = codedInputStream.readMessage(FolderIcon.parser(), extensionRegistryLite);
                                    this.item_ = readMessage5;
                                    if (builder6 != null) {
                                        builder6.mergeFrom((FolderIcon) readMessage5);
                                        this.item_ = builder6.buildPartial();
                                    }
                                    this.itemCase_ = 9;
                                    break;
                                case 82:
                                    Slice.Builder builder7 = this.itemCase_ == 10 ? (Slice.Builder) ((Slice) this.item_).toBuilder() : null;
                                    MessageLite readMessage6 = codedInputStream.readMessage(Slice.parser(), extensionRegistryLite);
                                    this.item_ = readMessage6;
                                    if (builder7 != null) {
                                        builder7.mergeFrom((Slice) readMessage6);
                                        this.item_ = builder7.buildPartial();
                                    }
                                    this.itemCase_ = 10;
                                    break;
                                case 90:
                                    SearchActionItem.Builder builder8 = this.itemCase_ == 11 ? (SearchActionItem.Builder) ((SearchActionItem) this.item_).toBuilder() : null;
                                    MessageLite readMessage7 = codedInputStream.readMessage(SearchActionItem.parser(), extensionRegistryLite);
                                    this.item_ = readMessage7;
                                    if (builder8 != null) {
                                        builder8.mergeFrom((SearchActionItem) readMessage7);
                                        this.item_ = builder8.buildPartial();
                                    }
                                    this.itemCase_ = 11;
                                    break;
                                case 96:
                                    if (!this.itemAttributes_.isModifiable()) {
                                        this.itemAttributes_ = GeneratedMessageLite.mutableCopy(this.itemAttributes_);
                                    }
                                    int readEnum = codedInputStream.readEnum();
                                    if (Attribute.forNumber(readEnum) != null) {
                                        this.itemAttributes_.addInt(readEnum);
                                        break;
                                    } else {
                                        super.mergeVarintField(12, readEnum);
                                        break;
                                    }
                                case 98:
                                    if (!this.itemAttributes_.isModifiable()) {
                                        this.itemAttributes_ = GeneratedMessageLite.mutableCopy(this.itemAttributes_);
                                    }
                                    int pushLimit = codedInputStream.pushLimit(codedInputStream.readRawVarint32());
                                    while (codedInputStream.getBytesUntilLimit() > 0) {
                                        int readEnum2 = codedInputStream.readEnum();
                                        if (Attribute.forNumber(readEnum2) == null) {
                                            super.mergeVarintField(12, readEnum2);
                                        } else {
                                            this.itemAttributes_.addInt(readEnum2);
                                        }
                                    }
                                    codedInputStream.popLimit(pushLimit);
                                    break;
                                default:
                                    if (parseUnknownField(readTag, codedInputStream)) {
                                        break;
                                    }
                                    z = true;
                                    break;
                            }
                        } catch (InvalidProtocolBufferException e) {
                            throw new RuntimeException(e.setUnfinishedMessage(this));
                        } catch (IOException e2) {
                            throw new RuntimeException(new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this));
                        }
                    }
                    break;
                case 7:
                    break;
                case 8:
                    if (PARSER == null) {
                        synchronized (ItemInfo.class) {
                            if (PARSER == null) {
                                PARSER = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                            }
                        }
                    }
                    return PARSER;
                default:
                    throw new UnsupportedOperationException();
            }
            return DEFAULT_INSTANCE;
        }

        public static ItemInfo getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<ItemInfo> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class LauncherAttributes extends GeneratedMessageLite<LauncherAttributes, Builder> implements LauncherAttributesOrBuilder {
        /* access modifiers changed from: private */
        public static final LauncherAttributes DEFAULT_INSTANCE;
        public static final int ITEM_ATTRIBUTES_FIELD_NUMBER = 1;
        private static volatile Parser<LauncherAttributes> PARSER;
        private Internal.IntList itemAttributes_ = emptyIntList();

        private LauncherAttributes() {
        }

        public List<Integer> getItemAttributesList() {
            return this.itemAttributes_;
        }

        public int getItemAttributesCount() {
            return this.itemAttributes_.size();
        }

        public int getItemAttributes(int i) {
            return this.itemAttributes_.getInt(i);
        }

        private void ensureItemAttributesIsMutable() {
            if (!this.itemAttributes_.isModifiable()) {
                this.itemAttributes_ = GeneratedMessageLite.mutableCopy(this.itemAttributes_);
            }
        }

        /* access modifiers changed from: private */
        public void setItemAttributes(int i, int i2) {
            ensureItemAttributesIsMutable();
            this.itemAttributes_.setInt(i, i2);
        }

        /* access modifiers changed from: private */
        public void addItemAttributes(int i) {
            ensureItemAttributesIsMutable();
            this.itemAttributes_.addInt(i);
        }

        /* access modifiers changed from: private */
        public void addAllItemAttributes(Iterable<? extends Integer> iterable) {
            ensureItemAttributesIsMutable();
            AbstractMessageLite.addAll(iterable, this.itemAttributes_);
        }

        /* access modifiers changed from: private */
        public void clearItemAttributes() {
            this.itemAttributes_ = emptyIntList();
        }

        public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
            for (int i = 0; i < this.itemAttributes_.size(); i++) {
                codedOutputStream.writeInt32(1, this.itemAttributes_.getInt(i));
            }
            this.unknownFields.writeTo(codedOutputStream);
        }

        public int getSerializedSize() {
            int i = this.memoizedSerializedSize;
            if (i != -1) {
                return i;
            }
            int i2 = 0;
            for (int i3 = 0; i3 < this.itemAttributes_.size(); i3++) {
                i2 += CodedOutputStream.computeInt32SizeNoTag(this.itemAttributes_.getInt(i3));
            }
            int size = 0 + i2 + (getItemAttributesList().size() * 1) + this.unknownFields.getSerializedSize();
            this.memoizedSerializedSize = size;
            return size;
        }

        public static LauncherAttributes parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (LauncherAttributes) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static LauncherAttributes parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (LauncherAttributes) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static LauncherAttributes parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (LauncherAttributes) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static LauncherAttributes parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (LauncherAttributes) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static LauncherAttributes parseFrom(InputStream inputStream) throws IOException {
            return (LauncherAttributes) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static LauncherAttributes parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (LauncherAttributes) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static LauncherAttributes parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (LauncherAttributes) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static LauncherAttributes parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (LauncherAttributes) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static LauncherAttributes parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (LauncherAttributes) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static LauncherAttributes parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (LauncherAttributes) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(LauncherAttributes launcherAttributes) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(launcherAttributes);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<LauncherAttributes, Builder> implements LauncherAttributesOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 r1) {
                this();
            }

            private Builder() {
                super(LauncherAttributes.DEFAULT_INSTANCE);
            }

            public List<Integer> getItemAttributesList() {
                return Collections.unmodifiableList(((LauncherAttributes) this.instance).getItemAttributesList());
            }

            public int getItemAttributesCount() {
                return ((LauncherAttributes) this.instance).getItemAttributesCount();
            }

            public int getItemAttributes(int i) {
                return ((LauncherAttributes) this.instance).getItemAttributes(i);
            }

            public Builder setItemAttributes(int i, int i2) {
                copyOnWrite();
                ((LauncherAttributes) this.instance).setItemAttributes(i, i2);
                return this;
            }

            public Builder addItemAttributes(int i) {
                copyOnWrite();
                ((LauncherAttributes) this.instance).addItemAttributes(i);
                return this;
            }

            public Builder addAllItemAttributes(Iterable<? extends Integer> iterable) {
                copyOnWrite();
                ((LauncherAttributes) this.instance).addAllItemAttributes(iterable);
                return this;
            }

            public Builder clearItemAttributes() {
                copyOnWrite();
                ((LauncherAttributes) this.instance).clearItemAttributes();
                return this;
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new LauncherAttributes();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    this.itemAttributes_.makeImmutable();
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    this.itemAttributes_ = ((GeneratedMessageLite.Visitor) obj).visitIntList(this.itemAttributes_, ((LauncherAttributes) obj2).itemAttributes_);
                    GeneratedMessageLite.MergeFromVisitor mergeFromVisitor = GeneratedMessageLite.MergeFromVisitor.INSTANCE;
                    return this;
                case 6:
                    CodedInputStream codedInputStream = (CodedInputStream) obj;
                    ExtensionRegistryLite extensionRegistryLite = (ExtensionRegistryLite) obj2;
                    boolean z = false;
                    while (!z) {
                        try {
                            int readTag = codedInputStream.readTag();
                            if (readTag != 0) {
                                if (readTag == 8) {
                                    if (!this.itemAttributes_.isModifiable()) {
                                        this.itemAttributes_ = GeneratedMessageLite.mutableCopy(this.itemAttributes_);
                                    }
                                    this.itemAttributes_.addInt(codedInputStream.readInt32());
                                } else if (readTag == 10) {
                                    int pushLimit = codedInputStream.pushLimit(codedInputStream.readRawVarint32());
                                    if (!this.itemAttributes_.isModifiable() && codedInputStream.getBytesUntilLimit() > 0) {
                                        this.itemAttributes_ = GeneratedMessageLite.mutableCopy(this.itemAttributes_);
                                    }
                                    while (codedInputStream.getBytesUntilLimit() > 0) {
                                        this.itemAttributes_.addInt(codedInputStream.readInt32());
                                    }
                                    codedInputStream.popLimit(pushLimit);
                                } else if (!parseUnknownField(readTag, codedInputStream)) {
                                }
                            }
                            z = true;
                        } catch (InvalidProtocolBufferException e) {
                            throw new RuntimeException(e.setUnfinishedMessage(this));
                        } catch (IOException e2) {
                            throw new RuntimeException(new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this));
                        }
                    }
                    break;
                case 7:
                    break;
                case 8:
                    if (PARSER == null) {
                        synchronized (LauncherAttributes.class) {
                            if (PARSER == null) {
                                PARSER = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                            }
                        }
                    }
                    return PARSER;
                default:
                    throw new UnsupportedOperationException();
            }
            return DEFAULT_INSTANCE;
        }

        static {
            LauncherAttributes launcherAttributes = new LauncherAttributes();
            DEFAULT_INSTANCE = launcherAttributes;
            launcherAttributes.makeImmutable();
        }

        public static LauncherAttributes getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<LauncherAttributes> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class ContainerInfo extends GeneratedMessageLite<ContainerInfo, Builder> implements ContainerInfoOrBuilder {
        public static final int ALL_APPS_CONTAINER_FIELD_NUMBER = 4;
        /* access modifiers changed from: private */
        public static final ContainerInfo DEFAULT_INSTANCE;
        public static final int EXTENDED_CONTAINERS_FIELD_NUMBER = 20;
        public static final int FOLDER_FIELD_NUMBER = 3;
        public static final int HOTSEAT_FIELD_NUMBER = 2;
        private static volatile Parser<ContainerInfo> PARSER = null;
        public static final int PREDICTED_HOTSEAT_CONTAINER_FIELD_NUMBER = 10;
        public static final int PREDICTION_CONTAINER_FIELD_NUMBER = 6;
        public static final int SEARCH_RESULT_CONTAINER_FIELD_NUMBER = 7;
        public static final int SETTINGS_CONTAINER_FIELD_NUMBER = 9;
        public static final int SHORTCUTS_CONTAINER_FIELD_NUMBER = 8;
        public static final int TASK_BAR_CONTAINER_FIELD_NUMBER = 12;
        public static final int TASK_SWITCHER_CONTAINER_FIELD_NUMBER = 11;
        public static final int WALLPAPERS_CONTAINER_FIELD_NUMBER = 13;
        public static final int WIDGETS_CONTAINER_FIELD_NUMBER = 5;
        public static final int WORKSPACE_FIELD_NUMBER = 1;
        private int bitField0_;
        private int containerCase_ = 0;
        private Object container_;

        private ContainerInfo() {
        }

        public enum ContainerCase implements Internal.EnumLite {
            WORKSPACE(1),
            HOTSEAT(2),
            FOLDER(3),
            ALL_APPS_CONTAINER(4),
            WIDGETS_CONTAINER(5),
            PREDICTION_CONTAINER(6),
            SEARCH_RESULT_CONTAINER(7),
            SHORTCUTS_CONTAINER(8),
            SETTINGS_CONTAINER(9),
            PREDICTED_HOTSEAT_CONTAINER(10),
            TASK_SWITCHER_CONTAINER(11),
            TASK_BAR_CONTAINER(12),
            WALLPAPERS_CONTAINER(13),
            EXTENDED_CONTAINERS(20),
            CONTAINER_NOT_SET(0);
            
            private final int value;

            private ContainerCase(int i) {
                this.value = i;
            }

            @Deprecated
            public static ContainerCase valueOf(int i) {
                return forNumber(i);
            }

            public static ContainerCase forNumber(int i) {
                if (i == 20) {
                    return EXTENDED_CONTAINERS;
                }
                switch (i) {
                    case 0:
                        return CONTAINER_NOT_SET;
                    case 1:
                        return WORKSPACE;
                    case 2:
                        return HOTSEAT;
                    case 3:
                        return FOLDER;
                    case 4:
                        return ALL_APPS_CONTAINER;
                    case 5:
                        return WIDGETS_CONTAINER;
                    case 6:
                        return PREDICTION_CONTAINER;
                    case 7:
                        return SEARCH_RESULT_CONTAINER;
                    case 8:
                        return SHORTCUTS_CONTAINER;
                    case 9:
                        return SETTINGS_CONTAINER;
                    case 10:
                        return PREDICTED_HOTSEAT_CONTAINER;
                    case 11:
                        return TASK_SWITCHER_CONTAINER;
                    case 12:
                        return TASK_BAR_CONTAINER;
                    case 13:
                        return WALLPAPERS_CONTAINER;
                    default:
                        return null;
                }
            }

            public int getNumber() {
                return this.value;
            }
        }

        public ContainerCase getContainerCase() {
            return ContainerCase.forNumber(this.containerCase_);
        }

        /* access modifiers changed from: private */
        public void clearContainer() {
            this.containerCase_ = 0;
            this.container_ = null;
        }

        public boolean hasWorkspace() {
            return this.containerCase_ == 1;
        }

        public WorkspaceContainer getWorkspace() {
            if (this.containerCase_ == 1) {
                return (WorkspaceContainer) this.container_;
            }
            return WorkspaceContainer.getDefaultInstance();
        }

        /* access modifiers changed from: private */
        public void setWorkspace(WorkspaceContainer workspaceContainer) {
            Objects.requireNonNull(workspaceContainer);
            this.container_ = workspaceContainer;
            this.containerCase_ = 1;
        }

        /* access modifiers changed from: private */
        public void setWorkspace(WorkspaceContainer.Builder builder) {
            this.container_ = builder.build();
            this.containerCase_ = 1;
        }

        /* access modifiers changed from: private */
        public void mergeWorkspace(WorkspaceContainer workspaceContainer) {
            if (this.containerCase_ != 1 || this.container_ == WorkspaceContainer.getDefaultInstance()) {
                this.container_ = workspaceContainer;
            } else {
                this.container_ = ((WorkspaceContainer.Builder) WorkspaceContainer.newBuilder((WorkspaceContainer) this.container_).mergeFrom(workspaceContainer)).buildPartial();
            }
            this.containerCase_ = 1;
        }

        /* access modifiers changed from: private */
        public void clearWorkspace() {
            if (this.containerCase_ == 1) {
                this.containerCase_ = 0;
                this.container_ = null;
            }
        }

        public boolean hasHotseat() {
            return this.containerCase_ == 2;
        }

        public HotseatContainer getHotseat() {
            if (this.containerCase_ == 2) {
                return (HotseatContainer) this.container_;
            }
            return HotseatContainer.getDefaultInstance();
        }

        /* access modifiers changed from: private */
        public void setHotseat(HotseatContainer hotseatContainer) {
            Objects.requireNonNull(hotseatContainer);
            this.container_ = hotseatContainer;
            this.containerCase_ = 2;
        }

        /* access modifiers changed from: private */
        public void setHotseat(HotseatContainer.Builder builder) {
            this.container_ = builder.build();
            this.containerCase_ = 2;
        }

        /* access modifiers changed from: private */
        public void mergeHotseat(HotseatContainer hotseatContainer) {
            if (this.containerCase_ != 2 || this.container_ == HotseatContainer.getDefaultInstance()) {
                this.container_ = hotseatContainer;
            } else {
                this.container_ = ((HotseatContainer.Builder) HotseatContainer.newBuilder((HotseatContainer) this.container_).mergeFrom(hotseatContainer)).buildPartial();
            }
            this.containerCase_ = 2;
        }

        /* access modifiers changed from: private */
        public void clearHotseat() {
            if (this.containerCase_ == 2) {
                this.containerCase_ = 0;
                this.container_ = null;
            }
        }

        public boolean hasFolder() {
            return this.containerCase_ == 3;
        }

        public FolderContainer getFolder() {
            if (this.containerCase_ == 3) {
                return (FolderContainer) this.container_;
            }
            return FolderContainer.getDefaultInstance();
        }

        /* access modifiers changed from: private */
        public void setFolder(FolderContainer folderContainer) {
            Objects.requireNonNull(folderContainer);
            this.container_ = folderContainer;
            this.containerCase_ = 3;
        }

        /* access modifiers changed from: private */
        public void setFolder(FolderContainer.Builder builder) {
            this.container_ = builder.build();
            this.containerCase_ = 3;
        }

        /* access modifiers changed from: private */
        public void mergeFolder(FolderContainer folderContainer) {
            if (this.containerCase_ != 3 || this.container_ == FolderContainer.getDefaultInstance()) {
                this.container_ = folderContainer;
            } else {
                this.container_ = ((FolderContainer.Builder) FolderContainer.newBuilder((FolderContainer) this.container_).mergeFrom(folderContainer)).buildPartial();
            }
            this.containerCase_ = 3;
        }

        /* access modifiers changed from: private */
        public void clearFolder() {
            if (this.containerCase_ == 3) {
                this.containerCase_ = 0;
                this.container_ = null;
            }
        }

        public boolean hasAllAppsContainer() {
            return this.containerCase_ == 4;
        }

        public AllAppsContainer getAllAppsContainer() {
            if (this.containerCase_ == 4) {
                return (AllAppsContainer) this.container_;
            }
            return AllAppsContainer.getDefaultInstance();
        }

        /* access modifiers changed from: private */
        public void setAllAppsContainer(AllAppsContainer allAppsContainer) {
            Objects.requireNonNull(allAppsContainer);
            this.container_ = allAppsContainer;
            this.containerCase_ = 4;
        }

        /* access modifiers changed from: private */
        public void setAllAppsContainer(AllAppsContainer.Builder builder) {
            this.container_ = builder.build();
            this.containerCase_ = 4;
        }

        /* access modifiers changed from: private */
        public void mergeAllAppsContainer(AllAppsContainer allAppsContainer) {
            if (this.containerCase_ != 4 || this.container_ == AllAppsContainer.getDefaultInstance()) {
                this.container_ = allAppsContainer;
            } else {
                this.container_ = ((AllAppsContainer.Builder) AllAppsContainer.newBuilder((AllAppsContainer) this.container_).mergeFrom(allAppsContainer)).buildPartial();
            }
            this.containerCase_ = 4;
        }

        /* access modifiers changed from: private */
        public void clearAllAppsContainer() {
            if (this.containerCase_ == 4) {
                this.containerCase_ = 0;
                this.container_ = null;
            }
        }

        public boolean hasWidgetsContainer() {
            return this.containerCase_ == 5;
        }

        public WidgetsContainer getWidgetsContainer() {
            if (this.containerCase_ == 5) {
                return (WidgetsContainer) this.container_;
            }
            return WidgetsContainer.getDefaultInstance();
        }

        /* access modifiers changed from: private */
        public void setWidgetsContainer(WidgetsContainer widgetsContainer) {
            Objects.requireNonNull(widgetsContainer);
            this.container_ = widgetsContainer;
            this.containerCase_ = 5;
        }

        /* access modifiers changed from: private */
        public void setWidgetsContainer(WidgetsContainer.Builder builder) {
            this.container_ = builder.build();
            this.containerCase_ = 5;
        }

        /* access modifiers changed from: private */
        public void mergeWidgetsContainer(WidgetsContainer widgetsContainer) {
            if (this.containerCase_ != 5 || this.container_ == WidgetsContainer.getDefaultInstance()) {
                this.container_ = widgetsContainer;
            } else {
                this.container_ = ((WidgetsContainer.Builder) WidgetsContainer.newBuilder((WidgetsContainer) this.container_).mergeFrom(widgetsContainer)).buildPartial();
            }
            this.containerCase_ = 5;
        }

        /* access modifiers changed from: private */
        public void clearWidgetsContainer() {
            if (this.containerCase_ == 5) {
                this.containerCase_ = 0;
                this.container_ = null;
            }
        }

        public boolean hasPredictionContainer() {
            return this.containerCase_ == 6;
        }

        public PredictionContainer getPredictionContainer() {
            if (this.containerCase_ == 6) {
                return (PredictionContainer) this.container_;
            }
            return PredictionContainer.getDefaultInstance();
        }

        /* access modifiers changed from: private */
        public void setPredictionContainer(PredictionContainer predictionContainer) {
            Objects.requireNonNull(predictionContainer);
            this.container_ = predictionContainer;
            this.containerCase_ = 6;
        }

        /* access modifiers changed from: private */
        public void setPredictionContainer(PredictionContainer.Builder builder) {
            this.container_ = builder.build();
            this.containerCase_ = 6;
        }

        /* access modifiers changed from: private */
        public void mergePredictionContainer(PredictionContainer predictionContainer) {
            if (this.containerCase_ != 6 || this.container_ == PredictionContainer.getDefaultInstance()) {
                this.container_ = predictionContainer;
            } else {
                this.container_ = ((PredictionContainer.Builder) PredictionContainer.newBuilder((PredictionContainer) this.container_).mergeFrom(predictionContainer)).buildPartial();
            }
            this.containerCase_ = 6;
        }

        /* access modifiers changed from: private */
        public void clearPredictionContainer() {
            if (this.containerCase_ == 6) {
                this.containerCase_ = 0;
                this.container_ = null;
            }
        }

        public boolean hasSearchResultContainer() {
            return this.containerCase_ == 7;
        }

        public SearchResultContainer getSearchResultContainer() {
            if (this.containerCase_ == 7) {
                return (SearchResultContainer) this.container_;
            }
            return SearchResultContainer.getDefaultInstance();
        }

        /* access modifiers changed from: private */
        public void setSearchResultContainer(SearchResultContainer searchResultContainer) {
            Objects.requireNonNull(searchResultContainer);
            this.container_ = searchResultContainer;
            this.containerCase_ = 7;
        }

        /* access modifiers changed from: private */
        public void setSearchResultContainer(SearchResultContainer.Builder builder) {
            this.container_ = builder.build();
            this.containerCase_ = 7;
        }

        /* access modifiers changed from: private */
        public void mergeSearchResultContainer(SearchResultContainer searchResultContainer) {
            if (this.containerCase_ != 7 || this.container_ == SearchResultContainer.getDefaultInstance()) {
                this.container_ = searchResultContainer;
            } else {
                this.container_ = ((SearchResultContainer.Builder) SearchResultContainer.newBuilder((SearchResultContainer) this.container_).mergeFrom(searchResultContainer)).buildPartial();
            }
            this.containerCase_ = 7;
        }

        /* access modifiers changed from: private */
        public void clearSearchResultContainer() {
            if (this.containerCase_ == 7) {
                this.containerCase_ = 0;
                this.container_ = null;
            }
        }

        public boolean hasShortcutsContainer() {
            return this.containerCase_ == 8;
        }

        public ShortcutsContainer getShortcutsContainer() {
            if (this.containerCase_ == 8) {
                return (ShortcutsContainer) this.container_;
            }
            return ShortcutsContainer.getDefaultInstance();
        }

        /* access modifiers changed from: private */
        public void setShortcutsContainer(ShortcutsContainer shortcutsContainer) {
            Objects.requireNonNull(shortcutsContainer);
            this.container_ = shortcutsContainer;
            this.containerCase_ = 8;
        }

        /* access modifiers changed from: private */
        public void setShortcutsContainer(ShortcutsContainer.Builder builder) {
            this.container_ = builder.build();
            this.containerCase_ = 8;
        }

        /* access modifiers changed from: private */
        public void mergeShortcutsContainer(ShortcutsContainer shortcutsContainer) {
            if (this.containerCase_ != 8 || this.container_ == ShortcutsContainer.getDefaultInstance()) {
                this.container_ = shortcutsContainer;
            } else {
                this.container_ = ((ShortcutsContainer.Builder) ShortcutsContainer.newBuilder((ShortcutsContainer) this.container_).mergeFrom(shortcutsContainer)).buildPartial();
            }
            this.containerCase_ = 8;
        }

        /* access modifiers changed from: private */
        public void clearShortcutsContainer() {
            if (this.containerCase_ == 8) {
                this.containerCase_ = 0;
                this.container_ = null;
            }
        }

        public boolean hasSettingsContainer() {
            return this.containerCase_ == 9;
        }

        public SettingsContainer getSettingsContainer() {
            if (this.containerCase_ == 9) {
                return (SettingsContainer) this.container_;
            }
            return SettingsContainer.getDefaultInstance();
        }

        /* access modifiers changed from: private */
        public void setSettingsContainer(SettingsContainer settingsContainer) {
            Objects.requireNonNull(settingsContainer);
            this.container_ = settingsContainer;
            this.containerCase_ = 9;
        }

        /* access modifiers changed from: private */
        public void setSettingsContainer(SettingsContainer.Builder builder) {
            this.container_ = builder.build();
            this.containerCase_ = 9;
        }

        /* access modifiers changed from: private */
        public void mergeSettingsContainer(SettingsContainer settingsContainer) {
            if (this.containerCase_ != 9 || this.container_ == SettingsContainer.getDefaultInstance()) {
                this.container_ = settingsContainer;
            } else {
                this.container_ = ((SettingsContainer.Builder) SettingsContainer.newBuilder((SettingsContainer) this.container_).mergeFrom(settingsContainer)).buildPartial();
            }
            this.containerCase_ = 9;
        }

        /* access modifiers changed from: private */
        public void clearSettingsContainer() {
            if (this.containerCase_ == 9) {
                this.containerCase_ = 0;
                this.container_ = null;
            }
        }

        public boolean hasPredictedHotseatContainer() {
            return this.containerCase_ == 10;
        }

        public PredictedHotseatContainer getPredictedHotseatContainer() {
            if (this.containerCase_ == 10) {
                return (PredictedHotseatContainer) this.container_;
            }
            return PredictedHotseatContainer.getDefaultInstance();
        }

        /* access modifiers changed from: private */
        public void setPredictedHotseatContainer(PredictedHotseatContainer predictedHotseatContainer) {
            Objects.requireNonNull(predictedHotseatContainer);
            this.container_ = predictedHotseatContainer;
            this.containerCase_ = 10;
        }

        /* access modifiers changed from: private */
        public void setPredictedHotseatContainer(PredictedHotseatContainer.Builder builder) {
            this.container_ = builder.build();
            this.containerCase_ = 10;
        }

        /* access modifiers changed from: private */
        public void mergePredictedHotseatContainer(PredictedHotseatContainer predictedHotseatContainer) {
            if (this.containerCase_ != 10 || this.container_ == PredictedHotseatContainer.getDefaultInstance()) {
                this.container_ = predictedHotseatContainer;
            } else {
                this.container_ = ((PredictedHotseatContainer.Builder) PredictedHotseatContainer.newBuilder((PredictedHotseatContainer) this.container_).mergeFrom(predictedHotseatContainer)).buildPartial();
            }
            this.containerCase_ = 10;
        }

        /* access modifiers changed from: private */
        public void clearPredictedHotseatContainer() {
            if (this.containerCase_ == 10) {
                this.containerCase_ = 0;
                this.container_ = null;
            }
        }

        public boolean hasTaskSwitcherContainer() {
            return this.containerCase_ == 11;
        }

        public TaskSwitcherContainer getTaskSwitcherContainer() {
            if (this.containerCase_ == 11) {
                return (TaskSwitcherContainer) this.container_;
            }
            return TaskSwitcherContainer.getDefaultInstance();
        }

        /* access modifiers changed from: private */
        public void setTaskSwitcherContainer(TaskSwitcherContainer taskSwitcherContainer) {
            Objects.requireNonNull(taskSwitcherContainer);
            this.container_ = taskSwitcherContainer;
            this.containerCase_ = 11;
        }

        /* access modifiers changed from: private */
        public void setTaskSwitcherContainer(TaskSwitcherContainer.Builder builder) {
            this.container_ = builder.build();
            this.containerCase_ = 11;
        }

        /* access modifiers changed from: private */
        public void mergeTaskSwitcherContainer(TaskSwitcherContainer taskSwitcherContainer) {
            if (this.containerCase_ != 11 || this.container_ == TaskSwitcherContainer.getDefaultInstance()) {
                this.container_ = taskSwitcherContainer;
            } else {
                this.container_ = ((TaskSwitcherContainer.Builder) TaskSwitcherContainer.newBuilder((TaskSwitcherContainer) this.container_).mergeFrom(taskSwitcherContainer)).buildPartial();
            }
            this.containerCase_ = 11;
        }

        /* access modifiers changed from: private */
        public void clearTaskSwitcherContainer() {
            if (this.containerCase_ == 11) {
                this.containerCase_ = 0;
                this.container_ = null;
            }
        }

        public boolean hasTaskBarContainer() {
            return this.containerCase_ == 12;
        }

        public TaskBarContainer getTaskBarContainer() {
            if (this.containerCase_ == 12) {
                return (TaskBarContainer) this.container_;
            }
            return TaskBarContainer.getDefaultInstance();
        }

        /* access modifiers changed from: private */
        public void setTaskBarContainer(TaskBarContainer taskBarContainer) {
            Objects.requireNonNull(taskBarContainer);
            this.container_ = taskBarContainer;
            this.containerCase_ = 12;
        }

        /* access modifiers changed from: private */
        public void setTaskBarContainer(TaskBarContainer.Builder builder) {
            this.container_ = builder.build();
            this.containerCase_ = 12;
        }

        /* access modifiers changed from: private */
        public void mergeTaskBarContainer(TaskBarContainer taskBarContainer) {
            if (this.containerCase_ != 12 || this.container_ == TaskBarContainer.getDefaultInstance()) {
                this.container_ = taskBarContainer;
            } else {
                this.container_ = ((TaskBarContainer.Builder) TaskBarContainer.newBuilder((TaskBarContainer) this.container_).mergeFrom(taskBarContainer)).buildPartial();
            }
            this.containerCase_ = 12;
        }

        /* access modifiers changed from: private */
        public void clearTaskBarContainer() {
            if (this.containerCase_ == 12) {
                this.containerCase_ = 0;
                this.container_ = null;
            }
        }

        public boolean hasWallpapersContainer() {
            return this.containerCase_ == 13;
        }

        public WallpapersContainer getWallpapersContainer() {
            if (this.containerCase_ == 13) {
                return (WallpapersContainer) this.container_;
            }
            return WallpapersContainer.getDefaultInstance();
        }

        /* access modifiers changed from: private */
        public void setWallpapersContainer(WallpapersContainer wallpapersContainer) {
            Objects.requireNonNull(wallpapersContainer);
            this.container_ = wallpapersContainer;
            this.containerCase_ = 13;
        }

        /* access modifiers changed from: private */
        public void setWallpapersContainer(WallpapersContainer.Builder builder) {
            this.container_ = builder.build();
            this.containerCase_ = 13;
        }

        /* access modifiers changed from: private */
        public void mergeWallpapersContainer(WallpapersContainer wallpapersContainer) {
            if (this.containerCase_ != 13 || this.container_ == WallpapersContainer.getDefaultInstance()) {
                this.container_ = wallpapersContainer;
            } else {
                this.container_ = ((WallpapersContainer.Builder) WallpapersContainer.newBuilder((WallpapersContainer) this.container_).mergeFrom(wallpapersContainer)).buildPartial();
            }
            this.containerCase_ = 13;
        }

        /* access modifiers changed from: private */
        public void clearWallpapersContainer() {
            if (this.containerCase_ == 13) {
                this.containerCase_ = 0;
                this.container_ = null;
            }
        }

        public boolean hasExtendedContainers() {
            return this.containerCase_ == 20;
        }

        public LauncherAtomExtensions.ExtendedContainers getExtendedContainers() {
            if (this.containerCase_ == 20) {
                return (LauncherAtomExtensions.ExtendedContainers) this.container_;
            }
            return LauncherAtomExtensions.ExtendedContainers.getDefaultInstance();
        }

        /* access modifiers changed from: private */
        public void setExtendedContainers(LauncherAtomExtensions.ExtendedContainers extendedContainers) {
            Objects.requireNonNull(extendedContainers);
            this.container_ = extendedContainers;
            this.containerCase_ = 20;
        }

        /* access modifiers changed from: private */
        public void setExtendedContainers(LauncherAtomExtensions.ExtendedContainers.Builder builder) {
            this.container_ = builder.build();
            this.containerCase_ = 20;
        }

        /* access modifiers changed from: private */
        public void mergeExtendedContainers(LauncherAtomExtensions.ExtendedContainers extendedContainers) {
            if (this.containerCase_ != 20 || this.container_ == LauncherAtomExtensions.ExtendedContainers.getDefaultInstance()) {
                this.container_ = extendedContainers;
            } else {
                this.container_ = ((LauncherAtomExtensions.ExtendedContainers.Builder) LauncherAtomExtensions.ExtendedContainers.newBuilder((LauncherAtomExtensions.ExtendedContainers) this.container_).mergeFrom(extendedContainers)).buildPartial();
            }
            this.containerCase_ = 20;
        }

        /* access modifiers changed from: private */
        public void clearExtendedContainers() {
            if (this.containerCase_ == 20) {
                this.containerCase_ = 0;
                this.container_ = null;
            }
        }

        public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
            if (this.containerCase_ == 1) {
                codedOutputStream.writeMessage(1, (WorkspaceContainer) this.container_);
            }
            if (this.containerCase_ == 2) {
                codedOutputStream.writeMessage(2, (HotseatContainer) this.container_);
            }
            if (this.containerCase_ == 3) {
                codedOutputStream.writeMessage(3, (FolderContainer) this.container_);
            }
            if (this.containerCase_ == 4) {
                codedOutputStream.writeMessage(4, (AllAppsContainer) this.container_);
            }
            if (this.containerCase_ == 5) {
                codedOutputStream.writeMessage(5, (WidgetsContainer) this.container_);
            }
            if (this.containerCase_ == 6) {
                codedOutputStream.writeMessage(6, (PredictionContainer) this.container_);
            }
            if (this.containerCase_ == 7) {
                codedOutputStream.writeMessage(7, (SearchResultContainer) this.container_);
            }
            if (this.containerCase_ == 8) {
                codedOutputStream.writeMessage(8, (ShortcutsContainer) this.container_);
            }
            if (this.containerCase_ == 9) {
                codedOutputStream.writeMessage(9, (SettingsContainer) this.container_);
            }
            if (this.containerCase_ == 10) {
                codedOutputStream.writeMessage(10, (PredictedHotseatContainer) this.container_);
            }
            if (this.containerCase_ == 11) {
                codedOutputStream.writeMessage(11, (TaskSwitcherContainer) this.container_);
            }
            if (this.containerCase_ == 12) {
                codedOutputStream.writeMessage(12, (TaskBarContainer) this.container_);
            }
            if (this.containerCase_ == 13) {
                codedOutputStream.writeMessage(13, (WallpapersContainer) this.container_);
            }
            if (this.containerCase_ == 20) {
                codedOutputStream.writeMessage(20, (LauncherAtomExtensions.ExtendedContainers) this.container_);
            }
            this.unknownFields.writeTo(codedOutputStream);
        }

        public int getSerializedSize() {
            int i = this.memoizedSerializedSize;
            if (i != -1) {
                return i;
            }
            int i2 = 0;
            if (this.containerCase_ == 1) {
                i2 = 0 + CodedOutputStream.computeMessageSize(1, (WorkspaceContainer) this.container_);
            }
            if (this.containerCase_ == 2) {
                i2 += CodedOutputStream.computeMessageSize(2, (HotseatContainer) this.container_);
            }
            if (this.containerCase_ == 3) {
                i2 += CodedOutputStream.computeMessageSize(3, (FolderContainer) this.container_);
            }
            if (this.containerCase_ == 4) {
                i2 += CodedOutputStream.computeMessageSize(4, (AllAppsContainer) this.container_);
            }
            if (this.containerCase_ == 5) {
                i2 += CodedOutputStream.computeMessageSize(5, (WidgetsContainer) this.container_);
            }
            if (this.containerCase_ == 6) {
                i2 += CodedOutputStream.computeMessageSize(6, (PredictionContainer) this.container_);
            }
            if (this.containerCase_ == 7) {
                i2 += CodedOutputStream.computeMessageSize(7, (SearchResultContainer) this.container_);
            }
            if (this.containerCase_ == 8) {
                i2 += CodedOutputStream.computeMessageSize(8, (ShortcutsContainer) this.container_);
            }
            if (this.containerCase_ == 9) {
                i2 += CodedOutputStream.computeMessageSize(9, (SettingsContainer) this.container_);
            }
            if (this.containerCase_ == 10) {
                i2 += CodedOutputStream.computeMessageSize(10, (PredictedHotseatContainer) this.container_);
            }
            if (this.containerCase_ == 11) {
                i2 += CodedOutputStream.computeMessageSize(11, (TaskSwitcherContainer) this.container_);
            }
            if (this.containerCase_ == 12) {
                i2 += CodedOutputStream.computeMessageSize(12, (TaskBarContainer) this.container_);
            }
            if (this.containerCase_ == 13) {
                i2 += CodedOutputStream.computeMessageSize(13, (WallpapersContainer) this.container_);
            }
            if (this.containerCase_ == 20) {
                i2 += CodedOutputStream.computeMessageSize(20, (LauncherAtomExtensions.ExtendedContainers) this.container_);
            }
            int serializedSize = i2 + this.unknownFields.getSerializedSize();
            this.memoizedSerializedSize = serializedSize;
            return serializedSize;
        }

        public static ContainerInfo parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (ContainerInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static ContainerInfo parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (ContainerInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static ContainerInfo parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (ContainerInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static ContainerInfo parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (ContainerInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static ContainerInfo parseFrom(InputStream inputStream) throws IOException {
            return (ContainerInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static ContainerInfo parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (ContainerInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static ContainerInfo parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (ContainerInfo) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static ContainerInfo parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (ContainerInfo) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static ContainerInfo parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (ContainerInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static ContainerInfo parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (ContainerInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(ContainerInfo containerInfo) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(containerInfo);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<ContainerInfo, Builder> implements ContainerInfoOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 r1) {
                this();
            }

            private Builder() {
                super(ContainerInfo.DEFAULT_INSTANCE);
            }

            public ContainerCase getContainerCase() {
                return ((ContainerInfo) this.instance).getContainerCase();
            }

            public Builder clearContainer() {
                copyOnWrite();
                ((ContainerInfo) this.instance).clearContainer();
                return this;
            }

            public boolean hasWorkspace() {
                return ((ContainerInfo) this.instance).hasWorkspace();
            }

            public WorkspaceContainer getWorkspace() {
                return ((ContainerInfo) this.instance).getWorkspace();
            }

            public Builder setWorkspace(WorkspaceContainer workspaceContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setWorkspace(workspaceContainer);
                return this;
            }

            public Builder setWorkspace(WorkspaceContainer.Builder builder) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setWorkspace(builder);
                return this;
            }

            public Builder mergeWorkspace(WorkspaceContainer workspaceContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).mergeWorkspace(workspaceContainer);
                return this;
            }

            public Builder clearWorkspace() {
                copyOnWrite();
                ((ContainerInfo) this.instance).clearWorkspace();
                return this;
            }

            public boolean hasHotseat() {
                return ((ContainerInfo) this.instance).hasHotseat();
            }

            public HotseatContainer getHotseat() {
                return ((ContainerInfo) this.instance).getHotseat();
            }

            public Builder setHotseat(HotseatContainer hotseatContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setHotseat(hotseatContainer);
                return this;
            }

            public Builder setHotseat(HotseatContainer.Builder builder) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setHotseat(builder);
                return this;
            }

            public Builder mergeHotseat(HotseatContainer hotseatContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).mergeHotseat(hotseatContainer);
                return this;
            }

            public Builder clearHotseat() {
                copyOnWrite();
                ((ContainerInfo) this.instance).clearHotseat();
                return this;
            }

            public boolean hasFolder() {
                return ((ContainerInfo) this.instance).hasFolder();
            }

            public FolderContainer getFolder() {
                return ((ContainerInfo) this.instance).getFolder();
            }

            public Builder setFolder(FolderContainer folderContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setFolder(folderContainer);
                return this;
            }

            public Builder setFolder(FolderContainer.Builder builder) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setFolder(builder);
                return this;
            }

            public Builder mergeFolder(FolderContainer folderContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).mergeFolder(folderContainer);
                return this;
            }

            public Builder clearFolder() {
                copyOnWrite();
                ((ContainerInfo) this.instance).clearFolder();
                return this;
            }

            public boolean hasAllAppsContainer() {
                return ((ContainerInfo) this.instance).hasAllAppsContainer();
            }

            public AllAppsContainer getAllAppsContainer() {
                return ((ContainerInfo) this.instance).getAllAppsContainer();
            }

            public Builder setAllAppsContainer(AllAppsContainer allAppsContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setAllAppsContainer(allAppsContainer);
                return this;
            }

            public Builder setAllAppsContainer(AllAppsContainer.Builder builder) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setAllAppsContainer(builder);
                return this;
            }

            public Builder mergeAllAppsContainer(AllAppsContainer allAppsContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).mergeAllAppsContainer(allAppsContainer);
                return this;
            }

            public Builder clearAllAppsContainer() {
                copyOnWrite();
                ((ContainerInfo) this.instance).clearAllAppsContainer();
                return this;
            }

            public boolean hasWidgetsContainer() {
                return ((ContainerInfo) this.instance).hasWidgetsContainer();
            }

            public WidgetsContainer getWidgetsContainer() {
                return ((ContainerInfo) this.instance).getWidgetsContainer();
            }

            public Builder setWidgetsContainer(WidgetsContainer widgetsContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setWidgetsContainer(widgetsContainer);
                return this;
            }

            public Builder setWidgetsContainer(WidgetsContainer.Builder builder) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setWidgetsContainer(builder);
                return this;
            }

            public Builder mergeWidgetsContainer(WidgetsContainer widgetsContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).mergeWidgetsContainer(widgetsContainer);
                return this;
            }

            public Builder clearWidgetsContainer() {
                copyOnWrite();
                ((ContainerInfo) this.instance).clearWidgetsContainer();
                return this;
            }

            public boolean hasPredictionContainer() {
                return ((ContainerInfo) this.instance).hasPredictionContainer();
            }

            public PredictionContainer getPredictionContainer() {
                return ((ContainerInfo) this.instance).getPredictionContainer();
            }

            public Builder setPredictionContainer(PredictionContainer predictionContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setPredictionContainer(predictionContainer);
                return this;
            }

            public Builder setPredictionContainer(PredictionContainer.Builder builder) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setPredictionContainer(builder);
                return this;
            }

            public Builder mergePredictionContainer(PredictionContainer predictionContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).mergePredictionContainer(predictionContainer);
                return this;
            }

            public Builder clearPredictionContainer() {
                copyOnWrite();
                ((ContainerInfo) this.instance).clearPredictionContainer();
                return this;
            }

            public boolean hasSearchResultContainer() {
                return ((ContainerInfo) this.instance).hasSearchResultContainer();
            }

            public SearchResultContainer getSearchResultContainer() {
                return ((ContainerInfo) this.instance).getSearchResultContainer();
            }

            public Builder setSearchResultContainer(SearchResultContainer searchResultContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setSearchResultContainer(searchResultContainer);
                return this;
            }

            public Builder setSearchResultContainer(SearchResultContainer.Builder builder) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setSearchResultContainer(builder);
                return this;
            }

            public Builder mergeSearchResultContainer(SearchResultContainer searchResultContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).mergeSearchResultContainer(searchResultContainer);
                return this;
            }

            public Builder clearSearchResultContainer() {
                copyOnWrite();
                ((ContainerInfo) this.instance).clearSearchResultContainer();
                return this;
            }

            public boolean hasShortcutsContainer() {
                return ((ContainerInfo) this.instance).hasShortcutsContainer();
            }

            public ShortcutsContainer getShortcutsContainer() {
                return ((ContainerInfo) this.instance).getShortcutsContainer();
            }

            public Builder setShortcutsContainer(ShortcutsContainer shortcutsContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setShortcutsContainer(shortcutsContainer);
                return this;
            }

            public Builder setShortcutsContainer(ShortcutsContainer.Builder builder) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setShortcutsContainer(builder);
                return this;
            }

            public Builder mergeShortcutsContainer(ShortcutsContainer shortcutsContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).mergeShortcutsContainer(shortcutsContainer);
                return this;
            }

            public Builder clearShortcutsContainer() {
                copyOnWrite();
                ((ContainerInfo) this.instance).clearShortcutsContainer();
                return this;
            }

            public boolean hasSettingsContainer() {
                return ((ContainerInfo) this.instance).hasSettingsContainer();
            }

            public SettingsContainer getSettingsContainer() {
                return ((ContainerInfo) this.instance).getSettingsContainer();
            }

            public Builder setSettingsContainer(SettingsContainer settingsContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setSettingsContainer(settingsContainer);
                return this;
            }

            public Builder setSettingsContainer(SettingsContainer.Builder builder) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setSettingsContainer(builder);
                return this;
            }

            public Builder mergeSettingsContainer(SettingsContainer settingsContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).mergeSettingsContainer(settingsContainer);
                return this;
            }

            public Builder clearSettingsContainer() {
                copyOnWrite();
                ((ContainerInfo) this.instance).clearSettingsContainer();
                return this;
            }

            public boolean hasPredictedHotseatContainer() {
                return ((ContainerInfo) this.instance).hasPredictedHotseatContainer();
            }

            public PredictedHotseatContainer getPredictedHotseatContainer() {
                return ((ContainerInfo) this.instance).getPredictedHotseatContainer();
            }

            public Builder setPredictedHotseatContainer(PredictedHotseatContainer predictedHotseatContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setPredictedHotseatContainer(predictedHotseatContainer);
                return this;
            }

            public Builder setPredictedHotseatContainer(PredictedHotseatContainer.Builder builder) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setPredictedHotseatContainer(builder);
                return this;
            }

            public Builder mergePredictedHotseatContainer(PredictedHotseatContainer predictedHotseatContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).mergePredictedHotseatContainer(predictedHotseatContainer);
                return this;
            }

            public Builder clearPredictedHotseatContainer() {
                copyOnWrite();
                ((ContainerInfo) this.instance).clearPredictedHotseatContainer();
                return this;
            }

            public boolean hasTaskSwitcherContainer() {
                return ((ContainerInfo) this.instance).hasTaskSwitcherContainer();
            }

            public TaskSwitcherContainer getTaskSwitcherContainer() {
                return ((ContainerInfo) this.instance).getTaskSwitcherContainer();
            }

            public Builder setTaskSwitcherContainer(TaskSwitcherContainer taskSwitcherContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setTaskSwitcherContainer(taskSwitcherContainer);
                return this;
            }

            public Builder setTaskSwitcherContainer(TaskSwitcherContainer.Builder builder) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setTaskSwitcherContainer(builder);
                return this;
            }

            public Builder mergeTaskSwitcherContainer(TaskSwitcherContainer taskSwitcherContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).mergeTaskSwitcherContainer(taskSwitcherContainer);
                return this;
            }

            public Builder clearTaskSwitcherContainer() {
                copyOnWrite();
                ((ContainerInfo) this.instance).clearTaskSwitcherContainer();
                return this;
            }

            public boolean hasTaskBarContainer() {
                return ((ContainerInfo) this.instance).hasTaskBarContainer();
            }

            public TaskBarContainer getTaskBarContainer() {
                return ((ContainerInfo) this.instance).getTaskBarContainer();
            }

            public Builder setTaskBarContainer(TaskBarContainer taskBarContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setTaskBarContainer(taskBarContainer);
                return this;
            }

            public Builder setTaskBarContainer(TaskBarContainer.Builder builder) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setTaskBarContainer(builder);
                return this;
            }

            public Builder mergeTaskBarContainer(TaskBarContainer taskBarContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).mergeTaskBarContainer(taskBarContainer);
                return this;
            }

            public Builder clearTaskBarContainer() {
                copyOnWrite();
                ((ContainerInfo) this.instance).clearTaskBarContainer();
                return this;
            }

            public boolean hasWallpapersContainer() {
                return ((ContainerInfo) this.instance).hasWallpapersContainer();
            }

            public WallpapersContainer getWallpapersContainer() {
                return ((ContainerInfo) this.instance).getWallpapersContainer();
            }

            public Builder setWallpapersContainer(WallpapersContainer wallpapersContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setWallpapersContainer(wallpapersContainer);
                return this;
            }

            public Builder setWallpapersContainer(WallpapersContainer.Builder builder) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setWallpapersContainer(builder);
                return this;
            }

            public Builder mergeWallpapersContainer(WallpapersContainer wallpapersContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).mergeWallpapersContainer(wallpapersContainer);
                return this;
            }

            public Builder clearWallpapersContainer() {
                copyOnWrite();
                ((ContainerInfo) this.instance).clearWallpapersContainer();
                return this;
            }

            public boolean hasExtendedContainers() {
                return ((ContainerInfo) this.instance).hasExtendedContainers();
            }

            public LauncherAtomExtensions.ExtendedContainers getExtendedContainers() {
                return ((ContainerInfo) this.instance).getExtendedContainers();
            }

            public Builder setExtendedContainers(LauncherAtomExtensions.ExtendedContainers extendedContainers) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setExtendedContainers(extendedContainers);
                return this;
            }

            public Builder setExtendedContainers(LauncherAtomExtensions.ExtendedContainers.Builder builder) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setExtendedContainers(builder);
                return this;
            }

            public Builder mergeExtendedContainers(LauncherAtomExtensions.ExtendedContainers extendedContainers) {
                copyOnWrite();
                ((ContainerInfo) this.instance).mergeExtendedContainers(extendedContainers);
                return this;
            }

            public Builder clearExtendedContainers() {
                copyOnWrite();
                ((ContainerInfo) this.instance).clearExtendedContainers();
                return this;
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            boolean z = false;
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new ContainerInfo();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                    ContainerInfo containerInfo = (ContainerInfo) obj2;
                    switch (AnonymousClass1.$SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase[containerInfo.getContainerCase().ordinal()]) {
                        case 1:
                            boolean z2 = true;
                            if (this.containerCase_ != 1) {
                                z2 = false;
                            }
                            this.container_ = visitor.visitOneofMessage(z2, this.container_, containerInfo.container_);
                            break;
                        case 2:
                            this.container_ = visitor.visitOneofMessage(this.containerCase_ == 2, this.container_, containerInfo.container_);
                            break;
                        case 3:
                            this.container_ = visitor.visitOneofMessage(this.containerCase_ == 3, this.container_, containerInfo.container_);
                            break;
                        case 4:
                            this.container_ = visitor.visitOneofMessage(this.containerCase_ == 4, this.container_, containerInfo.container_);
                            break;
                        case 5:
                            this.container_ = visitor.visitOneofMessage(this.containerCase_ == 5, this.container_, containerInfo.container_);
                            break;
                        case 6:
                            this.container_ = visitor.visitOneofMessage(this.containerCase_ == 6, this.container_, containerInfo.container_);
                            break;
                        case 7:
                            this.container_ = visitor.visitOneofMessage(this.containerCase_ == 7, this.container_, containerInfo.container_);
                            break;
                        case 8:
                            this.container_ = visitor.visitOneofMessage(this.containerCase_ == 8, this.container_, containerInfo.container_);
                            break;
                        case 9:
                            this.container_ = visitor.visitOneofMessage(this.containerCase_ == 9, this.container_, containerInfo.container_);
                            break;
                        case 10:
                            this.container_ = visitor.visitOneofMessage(this.containerCase_ == 10, this.container_, containerInfo.container_);
                            break;
                        case 11:
                            this.container_ = visitor.visitOneofMessage(this.containerCase_ == 11, this.container_, containerInfo.container_);
                            break;
                        case 12:
                            this.container_ = visitor.visitOneofMessage(this.containerCase_ == 12, this.container_, containerInfo.container_);
                            break;
                        case 13:
                            this.container_ = visitor.visitOneofMessage(this.containerCase_ == 13, this.container_, containerInfo.container_);
                            break;
                        case 14:
                            this.container_ = visitor.visitOneofMessage(this.containerCase_ == 20, this.container_, containerInfo.container_);
                            break;
                        case 15:
                            visitor.visitOneofNotSet(this.containerCase_ != 0);
                            break;
                    }
                    if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                        int i = containerInfo.containerCase_;
                        if (i != 0) {
                            this.containerCase_ = i;
                        }
                        this.bitField0_ |= containerInfo.bitField0_;
                    }
                    return this;
                case 6:
                    CodedInputStream codedInputStream = (CodedInputStream) obj;
                    ExtensionRegistryLite extensionRegistryLite = (ExtensionRegistryLite) obj2;
                    while (!z) {
                        try {
                            int readTag = codedInputStream.readTag();
                            switch (readTag) {
                                case 0:
                                    z = true;
                                    break;
                                case 10:
                                    WorkspaceContainer.Builder builder = this.containerCase_ == 1 ? (WorkspaceContainer.Builder) ((WorkspaceContainer) this.container_).toBuilder() : null;
                                    MessageLite readMessage = codedInputStream.readMessage(WorkspaceContainer.parser(), extensionRegistryLite);
                                    this.container_ = readMessage;
                                    if (builder != null) {
                                        builder.mergeFrom((WorkspaceContainer) readMessage);
                                        this.container_ = builder.buildPartial();
                                    }
                                    this.containerCase_ = 1;
                                    break;
                                case 18:
                                    HotseatContainer.Builder builder2 = this.containerCase_ == 2 ? (HotseatContainer.Builder) ((HotseatContainer) this.container_).toBuilder() : null;
                                    MessageLite readMessage2 = codedInputStream.readMessage(HotseatContainer.parser(), extensionRegistryLite);
                                    this.container_ = readMessage2;
                                    if (builder2 != null) {
                                        builder2.mergeFrom((HotseatContainer) readMessage2);
                                        this.container_ = builder2.buildPartial();
                                    }
                                    this.containerCase_ = 2;
                                    break;
                                case 26:
                                    FolderContainer.Builder builder3 = this.containerCase_ == 3 ? (FolderContainer.Builder) ((FolderContainer) this.container_).toBuilder() : null;
                                    MessageLite readMessage3 = codedInputStream.readMessage(FolderContainer.parser(), extensionRegistryLite);
                                    this.container_ = readMessage3;
                                    if (builder3 != null) {
                                        builder3.mergeFrom((FolderContainer) readMessage3);
                                        this.container_ = builder3.buildPartial();
                                    }
                                    this.containerCase_ = 3;
                                    break;
                                case 34:
                                    AllAppsContainer.Builder builder4 = this.containerCase_ == 4 ? (AllAppsContainer.Builder) ((AllAppsContainer) this.container_).toBuilder() : null;
                                    MessageLite readMessage4 = codedInputStream.readMessage(AllAppsContainer.parser(), extensionRegistryLite);
                                    this.container_ = readMessage4;
                                    if (builder4 != null) {
                                        builder4.mergeFrom((AllAppsContainer) readMessage4);
                                        this.container_ = builder4.buildPartial();
                                    }
                                    this.containerCase_ = 4;
                                    break;
                                case 42:
                                    WidgetsContainer.Builder builder5 = this.containerCase_ == 5 ? (WidgetsContainer.Builder) ((WidgetsContainer) this.container_).toBuilder() : null;
                                    MessageLite readMessage5 = codedInputStream.readMessage(WidgetsContainer.parser(), extensionRegistryLite);
                                    this.container_ = readMessage5;
                                    if (builder5 != null) {
                                        builder5.mergeFrom((WidgetsContainer) readMessage5);
                                        this.container_ = builder5.buildPartial();
                                    }
                                    this.containerCase_ = 5;
                                    break;
                                case 50:
                                    PredictionContainer.Builder builder6 = this.containerCase_ == 6 ? (PredictionContainer.Builder) ((PredictionContainer) this.container_).toBuilder() : null;
                                    MessageLite readMessage6 = codedInputStream.readMessage(PredictionContainer.parser(), extensionRegistryLite);
                                    this.container_ = readMessage6;
                                    if (builder6 != null) {
                                        builder6.mergeFrom((PredictionContainer) readMessage6);
                                        this.container_ = builder6.buildPartial();
                                    }
                                    this.containerCase_ = 6;
                                    break;
                                case 58:
                                    SearchResultContainer.Builder builder7 = this.containerCase_ == 7 ? (SearchResultContainer.Builder) ((SearchResultContainer) this.container_).toBuilder() : null;
                                    MessageLite readMessage7 = codedInputStream.readMessage(SearchResultContainer.parser(), extensionRegistryLite);
                                    this.container_ = readMessage7;
                                    if (builder7 != null) {
                                        builder7.mergeFrom((SearchResultContainer) readMessage7);
                                        this.container_ = builder7.buildPartial();
                                    }
                                    this.containerCase_ = 7;
                                    break;
                                case 66:
                                    ShortcutsContainer.Builder builder8 = this.containerCase_ == 8 ? (ShortcutsContainer.Builder) ((ShortcutsContainer) this.container_).toBuilder() : null;
                                    MessageLite readMessage8 = codedInputStream.readMessage(ShortcutsContainer.parser(), extensionRegistryLite);
                                    this.container_ = readMessage8;
                                    if (builder8 != null) {
                                        builder8.mergeFrom((ShortcutsContainer) readMessage8);
                                        this.container_ = builder8.buildPartial();
                                    }
                                    this.containerCase_ = 8;
                                    break;
                                case 74:
                                    SettingsContainer.Builder builder9 = this.containerCase_ == 9 ? (SettingsContainer.Builder) ((SettingsContainer) this.container_).toBuilder() : null;
                                    MessageLite readMessage9 = codedInputStream.readMessage(SettingsContainer.parser(), extensionRegistryLite);
                                    this.container_ = readMessage9;
                                    if (builder9 != null) {
                                        builder9.mergeFrom((SettingsContainer) readMessage9);
                                        this.container_ = builder9.buildPartial();
                                    }
                                    this.containerCase_ = 9;
                                    break;
                                case 82:
                                    PredictedHotseatContainer.Builder builder10 = this.containerCase_ == 10 ? (PredictedHotseatContainer.Builder) ((PredictedHotseatContainer) this.container_).toBuilder() : null;
                                    MessageLite readMessage10 = codedInputStream.readMessage(PredictedHotseatContainer.parser(), extensionRegistryLite);
                                    this.container_ = readMessage10;
                                    if (builder10 != null) {
                                        builder10.mergeFrom((PredictedHotseatContainer) readMessage10);
                                        this.container_ = builder10.buildPartial();
                                    }
                                    this.containerCase_ = 10;
                                    break;
                                case 90:
                                    TaskSwitcherContainer.Builder builder11 = this.containerCase_ == 11 ? (TaskSwitcherContainer.Builder) ((TaskSwitcherContainer) this.container_).toBuilder() : null;
                                    MessageLite readMessage11 = codedInputStream.readMessage(TaskSwitcherContainer.parser(), extensionRegistryLite);
                                    this.container_ = readMessage11;
                                    if (builder11 != null) {
                                        builder11.mergeFrom((TaskSwitcherContainer) readMessage11);
                                        this.container_ = builder11.buildPartial();
                                    }
                                    this.containerCase_ = 11;
                                    break;
                                case 98:
                                    TaskBarContainer.Builder builder12 = this.containerCase_ == 12 ? (TaskBarContainer.Builder) ((TaskBarContainer) this.container_).toBuilder() : null;
                                    MessageLite readMessage12 = codedInputStream.readMessage(TaskBarContainer.parser(), extensionRegistryLite);
                                    this.container_ = readMessage12;
                                    if (builder12 != null) {
                                        builder12.mergeFrom((TaskBarContainer) readMessage12);
                                        this.container_ = builder12.buildPartial();
                                    }
                                    this.containerCase_ = 12;
                                    break;
                                case 106:
                                    WallpapersContainer.Builder builder13 = this.containerCase_ == 13 ? (WallpapersContainer.Builder) ((WallpapersContainer) this.container_).toBuilder() : null;
                                    MessageLite readMessage13 = codedInputStream.readMessage(WallpapersContainer.parser(), extensionRegistryLite);
                                    this.container_ = readMessage13;
                                    if (builder13 != null) {
                                        builder13.mergeFrom((WallpapersContainer) readMessage13);
                                        this.container_ = builder13.buildPartial();
                                    }
                                    this.containerCase_ = 13;
                                    break;
                                case 162:
                                    LauncherAtomExtensions.ExtendedContainers.Builder builder14 = this.containerCase_ == 20 ? (LauncherAtomExtensions.ExtendedContainers.Builder) ((LauncherAtomExtensions.ExtendedContainers) this.container_).toBuilder() : null;
                                    MessageLite readMessage14 = codedInputStream.readMessage(LauncherAtomExtensions.ExtendedContainers.parser(), extensionRegistryLite);
                                    this.container_ = readMessage14;
                                    if (builder14 != null) {
                                        builder14.mergeFrom((LauncherAtomExtensions.ExtendedContainers) readMessage14);
                                        this.container_ = builder14.buildPartial();
                                    }
                                    this.containerCase_ = 20;
                                    break;
                                default:
                                    if (parseUnknownField(readTag, codedInputStream)) {
                                        break;
                                    }
                                    z = true;
                                    break;
                            }
                        } catch (InvalidProtocolBufferException e) {
                            throw new RuntimeException(e.setUnfinishedMessage(this));
                        } catch (IOException e2) {
                            throw new RuntimeException(new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this));
                        } catch (Throwable th) {
                            throw th;
                        }
                    }
                    break;
                case 7:
                    break;
                case 8:
                    if (PARSER == null) {
                        synchronized (ContainerInfo.class) {
                            try {
                                if (PARSER == null) {
                                    PARSER = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                                }
                            } catch (Throwable th2) {
                                throw th2;
                            }
                        }
                    }
                    return PARSER;
                default:
                    throw new UnsupportedOperationException();
            }
            return DEFAULT_INSTANCE;
        }

        static {
            ContainerInfo containerInfo = new ContainerInfo();
            DEFAULT_INSTANCE = containerInfo;
            containerInfo.makeImmutable();
        }

        public static ContainerInfo getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<ContainerInfo> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class AllAppsContainer extends GeneratedMessageLite<AllAppsContainer, Builder> implements AllAppsContainerOrBuilder {
        /* access modifiers changed from: private */
        public static final AllAppsContainer DEFAULT_INSTANCE;
        private static volatile Parser<AllAppsContainer> PARSER = null;
        public static final int TASKBAR_CONTAINER_FIELD_NUMBER = 1;
        private int bitField0_;
        private int parentContainerCase_ = 0;
        private Object parentContainer_;

        private AllAppsContainer() {
        }

        public enum ParentContainerCase implements Internal.EnumLite {
            TASKBAR_CONTAINER(1),
            PARENTCONTAINER_NOT_SET(0);
            
            private final int value;

            private ParentContainerCase(int i) {
                this.value = i;
            }

            @Deprecated
            public static ParentContainerCase valueOf(int i) {
                return forNumber(i);
            }

            public static ParentContainerCase forNumber(int i) {
                if (i == 0) {
                    return PARENTCONTAINER_NOT_SET;
                }
                if (i != 1) {
                    return null;
                }
                return TASKBAR_CONTAINER;
            }

            public int getNumber() {
                return this.value;
            }
        }

        public ParentContainerCase getParentContainerCase() {
            return ParentContainerCase.forNumber(this.parentContainerCase_);
        }

        /* access modifiers changed from: private */
        public void clearParentContainer() {
            this.parentContainerCase_ = 0;
            this.parentContainer_ = null;
        }

        public boolean hasTaskbarContainer() {
            return this.parentContainerCase_ == 1;
        }

        public TaskBarContainer getTaskbarContainer() {
            if (this.parentContainerCase_ == 1) {
                return (TaskBarContainer) this.parentContainer_;
            }
            return TaskBarContainer.getDefaultInstance();
        }

        /* access modifiers changed from: private */
        public void setTaskbarContainer(TaskBarContainer taskBarContainer) {
            Objects.requireNonNull(taskBarContainer);
            this.parentContainer_ = taskBarContainer;
            this.parentContainerCase_ = 1;
        }

        /* access modifiers changed from: private */
        public void setTaskbarContainer(TaskBarContainer.Builder builder) {
            this.parentContainer_ = builder.build();
            this.parentContainerCase_ = 1;
        }

        /* access modifiers changed from: private */
        public void mergeTaskbarContainer(TaskBarContainer taskBarContainer) {
            if (this.parentContainerCase_ != 1 || this.parentContainer_ == TaskBarContainer.getDefaultInstance()) {
                this.parentContainer_ = taskBarContainer;
            } else {
                this.parentContainer_ = ((TaskBarContainer.Builder) TaskBarContainer.newBuilder((TaskBarContainer) this.parentContainer_).mergeFrom(taskBarContainer)).buildPartial();
            }
            this.parentContainerCase_ = 1;
        }

        /* access modifiers changed from: private */
        public void clearTaskbarContainer() {
            if (this.parentContainerCase_ == 1) {
                this.parentContainerCase_ = 0;
                this.parentContainer_ = null;
            }
        }

        public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
            if (this.parentContainerCase_ == 1) {
                codedOutputStream.writeMessage(1, (TaskBarContainer) this.parentContainer_);
            }
            this.unknownFields.writeTo(codedOutputStream);
        }

        public int getSerializedSize() {
            int i = this.memoizedSerializedSize;
            if (i != -1) {
                return i;
            }
            int i2 = 0;
            if (this.parentContainerCase_ == 1) {
                i2 = 0 + CodedOutputStream.computeMessageSize(1, (TaskBarContainer) this.parentContainer_);
            }
            int serializedSize = i2 + this.unknownFields.getSerializedSize();
            this.memoizedSerializedSize = serializedSize;
            return serializedSize;
        }

        public static AllAppsContainer parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (AllAppsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static AllAppsContainer parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (AllAppsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static AllAppsContainer parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (AllAppsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static AllAppsContainer parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (AllAppsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static AllAppsContainer parseFrom(InputStream inputStream) throws IOException {
            return (AllAppsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static AllAppsContainer parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (AllAppsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static AllAppsContainer parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (AllAppsContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static AllAppsContainer parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (AllAppsContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static AllAppsContainer parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (AllAppsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static AllAppsContainer parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (AllAppsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(AllAppsContainer allAppsContainer) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(allAppsContainer);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<AllAppsContainer, Builder> implements AllAppsContainerOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 r1) {
                this();
            }

            private Builder() {
                super(AllAppsContainer.DEFAULT_INSTANCE);
            }

            public ParentContainerCase getParentContainerCase() {
                return ((AllAppsContainer) this.instance).getParentContainerCase();
            }

            public Builder clearParentContainer() {
                copyOnWrite();
                ((AllAppsContainer) this.instance).clearParentContainer();
                return this;
            }

            public boolean hasTaskbarContainer() {
                return ((AllAppsContainer) this.instance).hasTaskbarContainer();
            }

            public TaskBarContainer getTaskbarContainer() {
                return ((AllAppsContainer) this.instance).getTaskbarContainer();
            }

            public Builder setTaskbarContainer(TaskBarContainer taskBarContainer) {
                copyOnWrite();
                ((AllAppsContainer) this.instance).setTaskbarContainer(taskBarContainer);
                return this;
            }

            public Builder setTaskbarContainer(TaskBarContainer.Builder builder) {
                copyOnWrite();
                ((AllAppsContainer) this.instance).setTaskbarContainer(builder);
                return this;
            }

            public Builder mergeTaskbarContainer(TaskBarContainer taskBarContainer) {
                copyOnWrite();
                ((AllAppsContainer) this.instance).mergeTaskbarContainer(taskBarContainer);
                return this;
            }

            public Builder clearTaskbarContainer() {
                copyOnWrite();
                ((AllAppsContainer) this.instance).clearTaskbarContainer();
                return this;
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            boolean z = false;
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new AllAppsContainer();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                    AllAppsContainer allAppsContainer = (AllAppsContainer) obj2;
                    int i = AnonymousClass1.$SwitchMap$com$android$launcher3$logger$LauncherAtom$AllAppsContainer$ParentContainerCase[allAppsContainer.getParentContainerCase().ordinal()];
                    if (i == 1) {
                        if (this.parentContainerCase_ == 1) {
                            z = true;
                        }
                        this.parentContainer_ = visitor.visitOneofMessage(z, this.parentContainer_, allAppsContainer.parentContainer_);
                    } else if (i == 2) {
                        if (this.parentContainerCase_ != 0) {
                            z = true;
                        }
                        visitor.visitOneofNotSet(z);
                    }
                    if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                        int i2 = allAppsContainer.parentContainerCase_;
                        if (i2 != 0) {
                            this.parentContainerCase_ = i2;
                        }
                        this.bitField0_ |= allAppsContainer.bitField0_;
                    }
                    return this;
                case 6:
                    CodedInputStream codedInputStream = (CodedInputStream) obj;
                    ExtensionRegistryLite extensionRegistryLite = (ExtensionRegistryLite) obj2;
                    while (!z) {
                        try {
                            int readTag = codedInputStream.readTag();
                            if (readTag != 0) {
                                if (readTag == 10) {
                                    TaskBarContainer.Builder builder = this.parentContainerCase_ == 1 ? (TaskBarContainer.Builder) ((TaskBarContainer) this.parentContainer_).toBuilder() : null;
                                    MessageLite readMessage = codedInputStream.readMessage(TaskBarContainer.parser(), extensionRegistryLite);
                                    this.parentContainer_ = readMessage;
                                    if (builder != null) {
                                        builder.mergeFrom((TaskBarContainer) readMessage);
                                        this.parentContainer_ = builder.buildPartial();
                                    }
                                    this.parentContainerCase_ = 1;
                                } else if (!parseUnknownField(readTag, codedInputStream)) {
                                }
                            }
                            z = true;
                        } catch (InvalidProtocolBufferException e) {
                            throw new RuntimeException(e.setUnfinishedMessage(this));
                        } catch (IOException e2) {
                            throw new RuntimeException(new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this));
                        }
                    }
                    break;
                case 7:
                    break;
                case 8:
                    if (PARSER == null) {
                        synchronized (AllAppsContainer.class) {
                            if (PARSER == null) {
                                PARSER = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                            }
                        }
                    }
                    return PARSER;
                default:
                    throw new UnsupportedOperationException();
            }
            return DEFAULT_INSTANCE;
        }

        static {
            AllAppsContainer allAppsContainer = new AllAppsContainer();
            DEFAULT_INSTANCE = allAppsContainer;
            allAppsContainer.makeImmutable();
        }

        public static AllAppsContainer getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<AllAppsContainer> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class WidgetsContainer extends GeneratedMessageLite<WidgetsContainer, Builder> implements WidgetsContainerOrBuilder {
        /* access modifiers changed from: private */
        public static final WidgetsContainer DEFAULT_INSTANCE;
        private static volatile Parser<WidgetsContainer> PARSER;

        private WidgetsContainer() {
        }

        public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
            this.unknownFields.writeTo(codedOutputStream);
        }

        public int getSerializedSize() {
            int i = this.memoizedSerializedSize;
            if (i != -1) {
                return i;
            }
            int serializedSize = this.unknownFields.getSerializedSize() + 0;
            this.memoizedSerializedSize = serializedSize;
            return serializedSize;
        }

        public static WidgetsContainer parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (WidgetsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static WidgetsContainer parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (WidgetsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static WidgetsContainer parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (WidgetsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static WidgetsContainer parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (WidgetsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static WidgetsContainer parseFrom(InputStream inputStream) throws IOException {
            return (WidgetsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static WidgetsContainer parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (WidgetsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static WidgetsContainer parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (WidgetsContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static WidgetsContainer parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (WidgetsContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static WidgetsContainer parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (WidgetsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static WidgetsContainer parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (WidgetsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(WidgetsContainer widgetsContainer) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(widgetsContainer);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<WidgetsContainer, Builder> implements WidgetsContainerOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 r1) {
                this();
            }

            private Builder() {
                super(WidgetsContainer.DEFAULT_INSTANCE);
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new WidgetsContainer();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                    WidgetsContainer widgetsContainer = (WidgetsContainer) obj2;
                    GeneratedMessageLite.MergeFromVisitor mergeFromVisitor = GeneratedMessageLite.MergeFromVisitor.INSTANCE;
                    return this;
                case 6:
                    CodedInputStream codedInputStream = (CodedInputStream) obj;
                    ExtensionRegistryLite extensionRegistryLite = (ExtensionRegistryLite) obj2;
                    boolean z = false;
                    while (!z) {
                        try {
                            int readTag = codedInputStream.readTag();
                            if (readTag == 0 || !parseUnknownField(readTag, codedInputStream)) {
                                z = true;
                            }
                        } catch (InvalidProtocolBufferException e) {
                            throw new RuntimeException(e.setUnfinishedMessage(this));
                        } catch (IOException e2) {
                            throw new RuntimeException(new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this));
                        }
                    }
                    break;
                case 7:
                    break;
                case 8:
                    if (PARSER == null) {
                        synchronized (WidgetsContainer.class) {
                            if (PARSER == null) {
                                PARSER = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                            }
                        }
                    }
                    return PARSER;
                default:
                    throw new UnsupportedOperationException();
            }
            return DEFAULT_INSTANCE;
        }

        static {
            WidgetsContainer widgetsContainer = new WidgetsContainer();
            DEFAULT_INSTANCE = widgetsContainer;
            widgetsContainer.makeImmutable();
        }

        public static WidgetsContainer getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<WidgetsContainer> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class PredictionContainer extends GeneratedMessageLite<PredictionContainer, Builder> implements PredictionContainerOrBuilder {
        /* access modifiers changed from: private */
        public static final PredictionContainer DEFAULT_INSTANCE;
        private static volatile Parser<PredictionContainer> PARSER = null;
        public static final int TASKBAR_CONTAINER_FIELD_NUMBER = 1;
        private int bitField0_;
        private int parentContainerCase_ = 0;
        private Object parentContainer_;

        private PredictionContainer() {
        }

        public enum ParentContainerCase implements Internal.EnumLite {
            TASKBAR_CONTAINER(1),
            PARENTCONTAINER_NOT_SET(0);
            
            private final int value;

            private ParentContainerCase(int i) {
                this.value = i;
            }

            @Deprecated
            public static ParentContainerCase valueOf(int i) {
                return forNumber(i);
            }

            public static ParentContainerCase forNumber(int i) {
                if (i == 0) {
                    return PARENTCONTAINER_NOT_SET;
                }
                if (i != 1) {
                    return null;
                }
                return TASKBAR_CONTAINER;
            }

            public int getNumber() {
                return this.value;
            }
        }

        public ParentContainerCase getParentContainerCase() {
            return ParentContainerCase.forNumber(this.parentContainerCase_);
        }

        /* access modifiers changed from: private */
        public void clearParentContainer() {
            this.parentContainerCase_ = 0;
            this.parentContainer_ = null;
        }

        public boolean hasTaskbarContainer() {
            return this.parentContainerCase_ == 1;
        }

        public TaskBarContainer getTaskbarContainer() {
            if (this.parentContainerCase_ == 1) {
                return (TaskBarContainer) this.parentContainer_;
            }
            return TaskBarContainer.getDefaultInstance();
        }

        /* access modifiers changed from: private */
        public void setTaskbarContainer(TaskBarContainer taskBarContainer) {
            Objects.requireNonNull(taskBarContainer);
            this.parentContainer_ = taskBarContainer;
            this.parentContainerCase_ = 1;
        }

        /* access modifiers changed from: private */
        public void setTaskbarContainer(TaskBarContainer.Builder builder) {
            this.parentContainer_ = builder.build();
            this.parentContainerCase_ = 1;
        }

        /* access modifiers changed from: private */
        public void mergeTaskbarContainer(TaskBarContainer taskBarContainer) {
            if (this.parentContainerCase_ != 1 || this.parentContainer_ == TaskBarContainer.getDefaultInstance()) {
                this.parentContainer_ = taskBarContainer;
            } else {
                this.parentContainer_ = ((TaskBarContainer.Builder) TaskBarContainer.newBuilder((TaskBarContainer) this.parentContainer_).mergeFrom(taskBarContainer)).buildPartial();
            }
            this.parentContainerCase_ = 1;
        }

        /* access modifiers changed from: private */
        public void clearTaskbarContainer() {
            if (this.parentContainerCase_ == 1) {
                this.parentContainerCase_ = 0;
                this.parentContainer_ = null;
            }
        }

        public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
            if (this.parentContainerCase_ == 1) {
                codedOutputStream.writeMessage(1, (TaskBarContainer) this.parentContainer_);
            }
            this.unknownFields.writeTo(codedOutputStream);
        }

        public int getSerializedSize() {
            int i = this.memoizedSerializedSize;
            if (i != -1) {
                return i;
            }
            int i2 = 0;
            if (this.parentContainerCase_ == 1) {
                i2 = 0 + CodedOutputStream.computeMessageSize(1, (TaskBarContainer) this.parentContainer_);
            }
            int serializedSize = i2 + this.unknownFields.getSerializedSize();
            this.memoizedSerializedSize = serializedSize;
            return serializedSize;
        }

        public static PredictionContainer parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (PredictionContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static PredictionContainer parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (PredictionContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static PredictionContainer parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (PredictionContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static PredictionContainer parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (PredictionContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static PredictionContainer parseFrom(InputStream inputStream) throws IOException {
            return (PredictionContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static PredictionContainer parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (PredictionContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static PredictionContainer parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (PredictionContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static PredictionContainer parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (PredictionContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static PredictionContainer parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (PredictionContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static PredictionContainer parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (PredictionContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(PredictionContainer predictionContainer) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(predictionContainer);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<PredictionContainer, Builder> implements PredictionContainerOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 r1) {
                this();
            }

            private Builder() {
                super(PredictionContainer.DEFAULT_INSTANCE);
            }

            public ParentContainerCase getParentContainerCase() {
                return ((PredictionContainer) this.instance).getParentContainerCase();
            }

            public Builder clearParentContainer() {
                copyOnWrite();
                ((PredictionContainer) this.instance).clearParentContainer();
                return this;
            }

            public boolean hasTaskbarContainer() {
                return ((PredictionContainer) this.instance).hasTaskbarContainer();
            }

            public TaskBarContainer getTaskbarContainer() {
                return ((PredictionContainer) this.instance).getTaskbarContainer();
            }

            public Builder setTaskbarContainer(TaskBarContainer taskBarContainer) {
                copyOnWrite();
                ((PredictionContainer) this.instance).setTaskbarContainer(taskBarContainer);
                return this;
            }

            public Builder setTaskbarContainer(TaskBarContainer.Builder builder) {
                copyOnWrite();
                ((PredictionContainer) this.instance).setTaskbarContainer(builder);
                return this;
            }

            public Builder mergeTaskbarContainer(TaskBarContainer taskBarContainer) {
                copyOnWrite();
                ((PredictionContainer) this.instance).mergeTaskbarContainer(taskBarContainer);
                return this;
            }

            public Builder clearTaskbarContainer() {
                copyOnWrite();
                ((PredictionContainer) this.instance).clearTaskbarContainer();
                return this;
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            boolean z = false;
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new PredictionContainer();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                    PredictionContainer predictionContainer = (PredictionContainer) obj2;
                    int i = AnonymousClass1.$SwitchMap$com$android$launcher3$logger$LauncherAtom$PredictionContainer$ParentContainerCase[predictionContainer.getParentContainerCase().ordinal()];
                    if (i == 1) {
                        if (this.parentContainerCase_ == 1) {
                            z = true;
                        }
                        this.parentContainer_ = visitor.visitOneofMessage(z, this.parentContainer_, predictionContainer.parentContainer_);
                    } else if (i == 2) {
                        if (this.parentContainerCase_ != 0) {
                            z = true;
                        }
                        visitor.visitOneofNotSet(z);
                    }
                    if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                        int i2 = predictionContainer.parentContainerCase_;
                        if (i2 != 0) {
                            this.parentContainerCase_ = i2;
                        }
                        this.bitField0_ |= predictionContainer.bitField0_;
                    }
                    return this;
                case 6:
                    CodedInputStream codedInputStream = (CodedInputStream) obj;
                    ExtensionRegistryLite extensionRegistryLite = (ExtensionRegistryLite) obj2;
                    while (!z) {
                        try {
                            int readTag = codedInputStream.readTag();
                            if (readTag != 0) {
                                if (readTag == 10) {
                                    TaskBarContainer.Builder builder = this.parentContainerCase_ == 1 ? (TaskBarContainer.Builder) ((TaskBarContainer) this.parentContainer_).toBuilder() : null;
                                    MessageLite readMessage = codedInputStream.readMessage(TaskBarContainer.parser(), extensionRegistryLite);
                                    this.parentContainer_ = readMessage;
                                    if (builder != null) {
                                        builder.mergeFrom((TaskBarContainer) readMessage);
                                        this.parentContainer_ = builder.buildPartial();
                                    }
                                    this.parentContainerCase_ = 1;
                                } else if (!parseUnknownField(readTag, codedInputStream)) {
                                }
                            }
                            z = true;
                        } catch (InvalidProtocolBufferException e) {
                            throw new RuntimeException(e.setUnfinishedMessage(this));
                        } catch (IOException e2) {
                            throw new RuntimeException(new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this));
                        }
                    }
                    break;
                case 7:
                    break;
                case 8:
                    if (PARSER == null) {
                        synchronized (PredictionContainer.class) {
                            if (PARSER == null) {
                                PARSER = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                            }
                        }
                    }
                    return PARSER;
                default:
                    throw new UnsupportedOperationException();
            }
            return DEFAULT_INSTANCE;
        }

        static {
            PredictionContainer predictionContainer = new PredictionContainer();
            DEFAULT_INSTANCE = predictionContainer;
            predictionContainer.makeImmutable();
        }

        public static PredictionContainer getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<PredictionContainer> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class SearchResultContainer extends GeneratedMessageLite<SearchResultContainer, Builder> implements SearchResultContainerOrBuilder {
        public static final int ALL_APPS_CONTAINER_FIELD_NUMBER = 3;
        /* access modifiers changed from: private */
        public static final SearchResultContainer DEFAULT_INSTANCE;
        private static volatile Parser<SearchResultContainer> PARSER = null;
        public static final int QUERY_LENGTH_FIELD_NUMBER = 1;
        public static final int WORKSPACE_FIELD_NUMBER = 2;
        private int bitField0_;
        private int parentContainerCase_ = 0;
        private Object parentContainer_;
        private int queryLength_;

        private SearchResultContainer() {
        }

        public enum ParentContainerCase implements Internal.EnumLite {
            WORKSPACE(2),
            ALL_APPS_CONTAINER(3),
            PARENTCONTAINER_NOT_SET(0);
            
            private final int value;

            private ParentContainerCase(int i) {
                this.value = i;
            }

            @Deprecated
            public static ParentContainerCase valueOf(int i) {
                return forNumber(i);
            }

            public static ParentContainerCase forNumber(int i) {
                if (i == 0) {
                    return PARENTCONTAINER_NOT_SET;
                }
                if (i == 2) {
                    return WORKSPACE;
                }
                if (i != 3) {
                    return null;
                }
                return ALL_APPS_CONTAINER;
            }

            public int getNumber() {
                return this.value;
            }
        }

        public ParentContainerCase getParentContainerCase() {
            return ParentContainerCase.forNumber(this.parentContainerCase_);
        }

        /* access modifiers changed from: private */
        public void clearParentContainer() {
            this.parentContainerCase_ = 0;
            this.parentContainer_ = null;
        }

        public boolean hasQueryLength() {
            return (this.bitField0_ & 1) == 1;
        }

        public int getQueryLength() {
            return this.queryLength_;
        }

        /* access modifiers changed from: private */
        public void setQueryLength(int i) {
            this.bitField0_ |= 1;
            this.queryLength_ = i;
        }

        /* access modifiers changed from: private */
        public void clearQueryLength() {
            this.bitField0_ &= -2;
            this.queryLength_ = 0;
        }

        public boolean hasWorkspace() {
            return this.parentContainerCase_ == 2;
        }

        public WorkspaceContainer getWorkspace() {
            if (this.parentContainerCase_ == 2) {
                return (WorkspaceContainer) this.parentContainer_;
            }
            return WorkspaceContainer.getDefaultInstance();
        }

        /* access modifiers changed from: private */
        public void setWorkspace(WorkspaceContainer workspaceContainer) {
            Objects.requireNonNull(workspaceContainer);
            this.parentContainer_ = workspaceContainer;
            this.parentContainerCase_ = 2;
        }

        /* access modifiers changed from: private */
        public void setWorkspace(WorkspaceContainer.Builder builder) {
            this.parentContainer_ = builder.build();
            this.parentContainerCase_ = 2;
        }

        /* access modifiers changed from: private */
        public void mergeWorkspace(WorkspaceContainer workspaceContainer) {
            if (this.parentContainerCase_ != 2 || this.parentContainer_ == WorkspaceContainer.getDefaultInstance()) {
                this.parentContainer_ = workspaceContainer;
            } else {
                this.parentContainer_ = ((WorkspaceContainer.Builder) WorkspaceContainer.newBuilder((WorkspaceContainer) this.parentContainer_).mergeFrom(workspaceContainer)).buildPartial();
            }
            this.parentContainerCase_ = 2;
        }

        /* access modifiers changed from: private */
        public void clearWorkspace() {
            if (this.parentContainerCase_ == 2) {
                this.parentContainerCase_ = 0;
                this.parentContainer_ = null;
            }
        }

        public boolean hasAllAppsContainer() {
            return this.parentContainerCase_ == 3;
        }

        public AllAppsContainer getAllAppsContainer() {
            if (this.parentContainerCase_ == 3) {
                return (AllAppsContainer) this.parentContainer_;
            }
            return AllAppsContainer.getDefaultInstance();
        }

        /* access modifiers changed from: private */
        public void setAllAppsContainer(AllAppsContainer allAppsContainer) {
            Objects.requireNonNull(allAppsContainer);
            this.parentContainer_ = allAppsContainer;
            this.parentContainerCase_ = 3;
        }

        /* access modifiers changed from: private */
        public void setAllAppsContainer(AllAppsContainer.Builder builder) {
            this.parentContainer_ = builder.build();
            this.parentContainerCase_ = 3;
        }

        /* access modifiers changed from: private */
        public void mergeAllAppsContainer(AllAppsContainer allAppsContainer) {
            if (this.parentContainerCase_ != 3 || this.parentContainer_ == AllAppsContainer.getDefaultInstance()) {
                this.parentContainer_ = allAppsContainer;
            } else {
                this.parentContainer_ = ((AllAppsContainer.Builder) AllAppsContainer.newBuilder((AllAppsContainer) this.parentContainer_).mergeFrom(allAppsContainer)).buildPartial();
            }
            this.parentContainerCase_ = 3;
        }

        /* access modifiers changed from: private */
        public void clearAllAppsContainer() {
            if (this.parentContainerCase_ == 3) {
                this.parentContainerCase_ = 0;
                this.parentContainer_ = null;
            }
        }

        public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
            if ((this.bitField0_ & 1) == 1) {
                codedOutputStream.writeInt32(1, this.queryLength_);
            }
            if (this.parentContainerCase_ == 2) {
                codedOutputStream.writeMessage(2, (WorkspaceContainer) this.parentContainer_);
            }
            if (this.parentContainerCase_ == 3) {
                codedOutputStream.writeMessage(3, (AllAppsContainer) this.parentContainer_);
            }
            this.unknownFields.writeTo(codedOutputStream);
        }

        public int getSerializedSize() {
            int i = this.memoizedSerializedSize;
            if (i != -1) {
                return i;
            }
            int i2 = 0;
            if ((this.bitField0_ & 1) == 1) {
                i2 = 0 + CodedOutputStream.computeInt32Size(1, this.queryLength_);
            }
            if (this.parentContainerCase_ == 2) {
                i2 += CodedOutputStream.computeMessageSize(2, (WorkspaceContainer) this.parentContainer_);
            }
            if (this.parentContainerCase_ == 3) {
                i2 += CodedOutputStream.computeMessageSize(3, (AllAppsContainer) this.parentContainer_);
            }
            int serializedSize = i2 + this.unknownFields.getSerializedSize();
            this.memoizedSerializedSize = serializedSize;
            return serializedSize;
        }

        public static SearchResultContainer parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (SearchResultContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static SearchResultContainer parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (SearchResultContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static SearchResultContainer parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (SearchResultContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static SearchResultContainer parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (SearchResultContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static SearchResultContainer parseFrom(InputStream inputStream) throws IOException {
            return (SearchResultContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static SearchResultContainer parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (SearchResultContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static SearchResultContainer parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (SearchResultContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static SearchResultContainer parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (SearchResultContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static SearchResultContainer parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (SearchResultContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static SearchResultContainer parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (SearchResultContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(SearchResultContainer searchResultContainer) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(searchResultContainer);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<SearchResultContainer, Builder> implements SearchResultContainerOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 r1) {
                this();
            }

            private Builder() {
                super(SearchResultContainer.DEFAULT_INSTANCE);
            }

            public ParentContainerCase getParentContainerCase() {
                return ((SearchResultContainer) this.instance).getParentContainerCase();
            }

            public Builder clearParentContainer() {
                copyOnWrite();
                ((SearchResultContainer) this.instance).clearParentContainer();
                return this;
            }

            public boolean hasQueryLength() {
                return ((SearchResultContainer) this.instance).hasQueryLength();
            }

            public int getQueryLength() {
                return ((SearchResultContainer) this.instance).getQueryLength();
            }

            public Builder setQueryLength(int i) {
                copyOnWrite();
                ((SearchResultContainer) this.instance).setQueryLength(i);
                return this;
            }

            public Builder clearQueryLength() {
                copyOnWrite();
                ((SearchResultContainer) this.instance).clearQueryLength();
                return this;
            }

            public boolean hasWorkspace() {
                return ((SearchResultContainer) this.instance).hasWorkspace();
            }

            public WorkspaceContainer getWorkspace() {
                return ((SearchResultContainer) this.instance).getWorkspace();
            }

            public Builder setWorkspace(WorkspaceContainer workspaceContainer) {
                copyOnWrite();
                ((SearchResultContainer) this.instance).setWorkspace(workspaceContainer);
                return this;
            }

            public Builder setWorkspace(WorkspaceContainer.Builder builder) {
                copyOnWrite();
                ((SearchResultContainer) this.instance).setWorkspace(builder);
                return this;
            }

            public Builder mergeWorkspace(WorkspaceContainer workspaceContainer) {
                copyOnWrite();
                ((SearchResultContainer) this.instance).mergeWorkspace(workspaceContainer);
                return this;
            }

            public Builder clearWorkspace() {
                copyOnWrite();
                ((SearchResultContainer) this.instance).clearWorkspace();
                return this;
            }

            public boolean hasAllAppsContainer() {
                return ((SearchResultContainer) this.instance).hasAllAppsContainer();
            }

            public AllAppsContainer getAllAppsContainer() {
                return ((SearchResultContainer) this.instance).getAllAppsContainer();
            }

            public Builder setAllAppsContainer(AllAppsContainer allAppsContainer) {
                copyOnWrite();
                ((SearchResultContainer) this.instance).setAllAppsContainer(allAppsContainer);
                return this;
            }

            public Builder setAllAppsContainer(AllAppsContainer.Builder builder) {
                copyOnWrite();
                ((SearchResultContainer) this.instance).setAllAppsContainer(builder);
                return this;
            }

            public Builder mergeAllAppsContainer(AllAppsContainer allAppsContainer) {
                copyOnWrite();
                ((SearchResultContainer) this.instance).mergeAllAppsContainer(allAppsContainer);
                return this;
            }

            public Builder clearAllAppsContainer() {
                copyOnWrite();
                ((SearchResultContainer) this.instance).clearAllAppsContainer();
                return this;
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            boolean z = false;
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new SearchResultContainer();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                    SearchResultContainer searchResultContainer = (SearchResultContainer) obj2;
                    this.queryLength_ = visitor.visitInt(hasQueryLength(), this.queryLength_, searchResultContainer.hasQueryLength(), searchResultContainer.queryLength_);
                    int i = AnonymousClass1.$SwitchMap$com$android$launcher3$logger$LauncherAtom$SearchResultContainer$ParentContainerCase[searchResultContainer.getParentContainerCase().ordinal()];
                    if (i == 1) {
                        if (this.parentContainerCase_ == 2) {
                            z = true;
                        }
                        this.parentContainer_ = visitor.visitOneofMessage(z, this.parentContainer_, searchResultContainer.parentContainer_);
                    } else if (i == 2) {
                        if (this.parentContainerCase_ == 3) {
                            z = true;
                        }
                        this.parentContainer_ = visitor.visitOneofMessage(z, this.parentContainer_, searchResultContainer.parentContainer_);
                    } else if (i == 3) {
                        if (this.parentContainerCase_ != 0) {
                            z = true;
                        }
                        visitor.visitOneofNotSet(z);
                    }
                    if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                        int i2 = searchResultContainer.parentContainerCase_;
                        if (i2 != 0) {
                            this.parentContainerCase_ = i2;
                        }
                        this.bitField0_ |= searchResultContainer.bitField0_;
                    }
                    return this;
                case 6:
                    CodedInputStream codedInputStream = (CodedInputStream) obj;
                    ExtensionRegistryLite extensionRegistryLite = (ExtensionRegistryLite) obj2;
                    while (!z) {
                        try {
                            int readTag = codedInputStream.readTag();
                            if (readTag != 0) {
                                if (readTag == 8) {
                                    this.bitField0_ |= 1;
                                    this.queryLength_ = codedInputStream.readInt32();
                                } else if (readTag == 18) {
                                    WorkspaceContainer.Builder builder = this.parentContainerCase_ == 2 ? (WorkspaceContainer.Builder) ((WorkspaceContainer) this.parentContainer_).toBuilder() : null;
                                    MessageLite readMessage = codedInputStream.readMessage(WorkspaceContainer.parser(), extensionRegistryLite);
                                    this.parentContainer_ = readMessage;
                                    if (builder != null) {
                                        builder.mergeFrom((WorkspaceContainer) readMessage);
                                        this.parentContainer_ = builder.buildPartial();
                                    }
                                    this.parentContainerCase_ = 2;
                                } else if (readTag == 26) {
                                    AllAppsContainer.Builder builder2 = this.parentContainerCase_ == 3 ? (AllAppsContainer.Builder) ((AllAppsContainer) this.parentContainer_).toBuilder() : null;
                                    MessageLite readMessage2 = codedInputStream.readMessage(AllAppsContainer.parser(), extensionRegistryLite);
                                    this.parentContainer_ = readMessage2;
                                    if (builder2 != null) {
                                        builder2.mergeFrom((AllAppsContainer) readMessage2);
                                        this.parentContainer_ = builder2.buildPartial();
                                    }
                                    this.parentContainerCase_ = 3;
                                } else if (!parseUnknownField(readTag, codedInputStream)) {
                                }
                            }
                            z = true;
                        } catch (InvalidProtocolBufferException e) {
                            throw new RuntimeException(e.setUnfinishedMessage(this));
                        } catch (IOException e2) {
                            throw new RuntimeException(new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this));
                        }
                    }
                    break;
                case 7:
                    break;
                case 8:
                    if (PARSER == null) {
                        synchronized (SearchResultContainer.class) {
                            if (PARSER == null) {
                                PARSER = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                            }
                        }
                    }
                    return PARSER;
                default:
                    throw new UnsupportedOperationException();
            }
            return DEFAULT_INSTANCE;
        }

        static {
            SearchResultContainer searchResultContainer = new SearchResultContainer();
            DEFAULT_INSTANCE = searchResultContainer;
            searchResultContainer.makeImmutable();
        }

        public static SearchResultContainer getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<SearchResultContainer> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class ShortcutsContainer extends GeneratedMessageLite<ShortcutsContainer, Builder> implements ShortcutsContainerOrBuilder {
        /* access modifiers changed from: private */
        public static final ShortcutsContainer DEFAULT_INSTANCE;
        private static volatile Parser<ShortcutsContainer> PARSER;

        private ShortcutsContainer() {
        }

        public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
            this.unknownFields.writeTo(codedOutputStream);
        }

        public int getSerializedSize() {
            int i = this.memoizedSerializedSize;
            if (i != -1) {
                return i;
            }
            int serializedSize = this.unknownFields.getSerializedSize() + 0;
            this.memoizedSerializedSize = serializedSize;
            return serializedSize;
        }

        public static ShortcutsContainer parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (ShortcutsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static ShortcutsContainer parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (ShortcutsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static ShortcutsContainer parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (ShortcutsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static ShortcutsContainer parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (ShortcutsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static ShortcutsContainer parseFrom(InputStream inputStream) throws IOException {
            return (ShortcutsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static ShortcutsContainer parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (ShortcutsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static ShortcutsContainer parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (ShortcutsContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static ShortcutsContainer parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (ShortcutsContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static ShortcutsContainer parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (ShortcutsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static ShortcutsContainer parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (ShortcutsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(ShortcutsContainer shortcutsContainer) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(shortcutsContainer);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<ShortcutsContainer, Builder> implements ShortcutsContainerOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 r1) {
                this();
            }

            private Builder() {
                super(ShortcutsContainer.DEFAULT_INSTANCE);
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new ShortcutsContainer();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                    ShortcutsContainer shortcutsContainer = (ShortcutsContainer) obj2;
                    GeneratedMessageLite.MergeFromVisitor mergeFromVisitor = GeneratedMessageLite.MergeFromVisitor.INSTANCE;
                    return this;
                case 6:
                    CodedInputStream codedInputStream = (CodedInputStream) obj;
                    ExtensionRegistryLite extensionRegistryLite = (ExtensionRegistryLite) obj2;
                    boolean z = false;
                    while (!z) {
                        try {
                            int readTag = codedInputStream.readTag();
                            if (readTag == 0 || !parseUnknownField(readTag, codedInputStream)) {
                                z = true;
                            }
                        } catch (InvalidProtocolBufferException e) {
                            throw new RuntimeException(e.setUnfinishedMessage(this));
                        } catch (IOException e2) {
                            throw new RuntimeException(new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this));
                        }
                    }
                    break;
                case 7:
                    break;
                case 8:
                    if (PARSER == null) {
                        synchronized (ShortcutsContainer.class) {
                            if (PARSER == null) {
                                PARSER = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                            }
                        }
                    }
                    return PARSER;
                default:
                    throw new UnsupportedOperationException();
            }
            return DEFAULT_INSTANCE;
        }

        static {
            ShortcutsContainer shortcutsContainer = new ShortcutsContainer();
            DEFAULT_INSTANCE = shortcutsContainer;
            shortcutsContainer.makeImmutable();
        }

        public static ShortcutsContainer getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<ShortcutsContainer> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class SettingsContainer extends GeneratedMessageLite<SettingsContainer, Builder> implements SettingsContainerOrBuilder {
        /* access modifiers changed from: private */
        public static final SettingsContainer DEFAULT_INSTANCE;
        private static volatile Parser<SettingsContainer> PARSER;

        private SettingsContainer() {
        }

        public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
            this.unknownFields.writeTo(codedOutputStream);
        }

        public int getSerializedSize() {
            int i = this.memoizedSerializedSize;
            if (i != -1) {
                return i;
            }
            int serializedSize = this.unknownFields.getSerializedSize() + 0;
            this.memoizedSerializedSize = serializedSize;
            return serializedSize;
        }

        public static SettingsContainer parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (SettingsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static SettingsContainer parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (SettingsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static SettingsContainer parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (SettingsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static SettingsContainer parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (SettingsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static SettingsContainer parseFrom(InputStream inputStream) throws IOException {
            return (SettingsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static SettingsContainer parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (SettingsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static SettingsContainer parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (SettingsContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static SettingsContainer parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (SettingsContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static SettingsContainer parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (SettingsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static SettingsContainer parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (SettingsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(SettingsContainer settingsContainer) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(settingsContainer);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<SettingsContainer, Builder> implements SettingsContainerOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 r1) {
                this();
            }

            private Builder() {
                super(SettingsContainer.DEFAULT_INSTANCE);
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new SettingsContainer();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                    SettingsContainer settingsContainer = (SettingsContainer) obj2;
                    GeneratedMessageLite.MergeFromVisitor mergeFromVisitor = GeneratedMessageLite.MergeFromVisitor.INSTANCE;
                    return this;
                case 6:
                    CodedInputStream codedInputStream = (CodedInputStream) obj;
                    ExtensionRegistryLite extensionRegistryLite = (ExtensionRegistryLite) obj2;
                    boolean z = false;
                    while (!z) {
                        try {
                            int readTag = codedInputStream.readTag();
                            if (readTag == 0 || !parseUnknownField(readTag, codedInputStream)) {
                                z = true;
                            }
                        } catch (InvalidProtocolBufferException e) {
                            throw new RuntimeException(e.setUnfinishedMessage(this));
                        } catch (IOException e2) {
                            throw new RuntimeException(new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this));
                        }
                    }
                    break;
                case 7:
                    break;
                case 8:
                    if (PARSER == null) {
                        synchronized (SettingsContainer.class) {
                            if (PARSER == null) {
                                PARSER = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                            }
                        }
                    }
                    return PARSER;
                default:
                    throw new UnsupportedOperationException();
            }
            return DEFAULT_INSTANCE;
        }

        static {
            SettingsContainer settingsContainer = new SettingsContainer();
            DEFAULT_INSTANCE = settingsContainer;
            settingsContainer.makeImmutable();
        }

        public static SettingsContainer getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<SettingsContainer> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class TaskSwitcherContainer extends GeneratedMessageLite<TaskSwitcherContainer, Builder> implements TaskSwitcherContainerOrBuilder {
        /* access modifiers changed from: private */
        public static final TaskSwitcherContainer DEFAULT_INSTANCE;
        private static volatile Parser<TaskSwitcherContainer> PARSER;

        private TaskSwitcherContainer() {
        }

        public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
            this.unknownFields.writeTo(codedOutputStream);
        }

        public int getSerializedSize() {
            int i = this.memoizedSerializedSize;
            if (i != -1) {
                return i;
            }
            int serializedSize = this.unknownFields.getSerializedSize() + 0;
            this.memoizedSerializedSize = serializedSize;
            return serializedSize;
        }

        public static TaskSwitcherContainer parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (TaskSwitcherContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static TaskSwitcherContainer parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (TaskSwitcherContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static TaskSwitcherContainer parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (TaskSwitcherContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static TaskSwitcherContainer parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (TaskSwitcherContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static TaskSwitcherContainer parseFrom(InputStream inputStream) throws IOException {
            return (TaskSwitcherContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static TaskSwitcherContainer parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (TaskSwitcherContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static TaskSwitcherContainer parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (TaskSwitcherContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static TaskSwitcherContainer parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (TaskSwitcherContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static TaskSwitcherContainer parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (TaskSwitcherContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static TaskSwitcherContainer parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (TaskSwitcherContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(TaskSwitcherContainer taskSwitcherContainer) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(taskSwitcherContainer);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<TaskSwitcherContainer, Builder> implements TaskSwitcherContainerOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 r1) {
                this();
            }

            private Builder() {
                super(TaskSwitcherContainer.DEFAULT_INSTANCE);
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new TaskSwitcherContainer();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                    TaskSwitcherContainer taskSwitcherContainer = (TaskSwitcherContainer) obj2;
                    GeneratedMessageLite.MergeFromVisitor mergeFromVisitor = GeneratedMessageLite.MergeFromVisitor.INSTANCE;
                    return this;
                case 6:
                    CodedInputStream codedInputStream = (CodedInputStream) obj;
                    ExtensionRegistryLite extensionRegistryLite = (ExtensionRegistryLite) obj2;
                    boolean z = false;
                    while (!z) {
                        try {
                            int readTag = codedInputStream.readTag();
                            if (readTag == 0 || !parseUnknownField(readTag, codedInputStream)) {
                                z = true;
                            }
                        } catch (InvalidProtocolBufferException e) {
                            throw new RuntimeException(e.setUnfinishedMessage(this));
                        } catch (IOException e2) {
                            throw new RuntimeException(new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this));
                        }
                    }
                    break;
                case 7:
                    break;
                case 8:
                    if (PARSER == null) {
                        synchronized (TaskSwitcherContainer.class) {
                            if (PARSER == null) {
                                PARSER = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                            }
                        }
                    }
                    return PARSER;
                default:
                    throw new UnsupportedOperationException();
            }
            return DEFAULT_INSTANCE;
        }

        static {
            TaskSwitcherContainer taskSwitcherContainer = new TaskSwitcherContainer();
            DEFAULT_INSTANCE = taskSwitcherContainer;
            taskSwitcherContainer.makeImmutable();
        }

        public static TaskSwitcherContainer getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<TaskSwitcherContainer> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class TaskBarContainer extends GeneratedMessageLite<TaskBarContainer, Builder> implements TaskBarContainerOrBuilder {
        public static final int CARDINALITY_FIELD_NUMBER = 2;
        /* access modifiers changed from: private */
        public static final TaskBarContainer DEFAULT_INSTANCE;
        public static final int INDEX_FIELD_NUMBER = 1;
        private static volatile Parser<TaskBarContainer> PARSER;
        private int bitField0_;
        private int cardinality_;
        private int index_;

        private TaskBarContainer() {
        }

        public boolean hasIndex() {
            return (this.bitField0_ & 1) == 1;
        }

        public int getIndex() {
            return this.index_;
        }

        /* access modifiers changed from: private */
        public void setIndex(int i) {
            this.bitField0_ |= 1;
            this.index_ = i;
        }

        /* access modifiers changed from: private */
        public void clearIndex() {
            this.bitField0_ &= -2;
            this.index_ = 0;
        }

        public boolean hasCardinality() {
            return (this.bitField0_ & 2) == 2;
        }

        public int getCardinality() {
            return this.cardinality_;
        }

        /* access modifiers changed from: private */
        public void setCardinality(int i) {
            this.bitField0_ |= 2;
            this.cardinality_ = i;
        }

        /* access modifiers changed from: private */
        public void clearCardinality() {
            this.bitField0_ &= -3;
            this.cardinality_ = 0;
        }

        public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
            if ((this.bitField0_ & 1) == 1) {
                codedOutputStream.writeInt32(1, this.index_);
            }
            if ((this.bitField0_ & 2) == 2) {
                codedOutputStream.writeInt32(2, this.cardinality_);
            }
            this.unknownFields.writeTo(codedOutputStream);
        }

        public int getSerializedSize() {
            int i = this.memoizedSerializedSize;
            if (i != -1) {
                return i;
            }
            int i2 = 0;
            if ((this.bitField0_ & 1) == 1) {
                i2 = 0 + CodedOutputStream.computeInt32Size(1, this.index_);
            }
            if ((this.bitField0_ & 2) == 2) {
                i2 += CodedOutputStream.computeInt32Size(2, this.cardinality_);
            }
            int serializedSize = i2 + this.unknownFields.getSerializedSize();
            this.memoizedSerializedSize = serializedSize;
            return serializedSize;
        }

        public static TaskBarContainer parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (TaskBarContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static TaskBarContainer parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (TaskBarContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static TaskBarContainer parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (TaskBarContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static TaskBarContainer parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (TaskBarContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static TaskBarContainer parseFrom(InputStream inputStream) throws IOException {
            return (TaskBarContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static TaskBarContainer parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (TaskBarContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static TaskBarContainer parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (TaskBarContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static TaskBarContainer parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (TaskBarContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static TaskBarContainer parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (TaskBarContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static TaskBarContainer parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (TaskBarContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(TaskBarContainer taskBarContainer) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(taskBarContainer);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<TaskBarContainer, Builder> implements TaskBarContainerOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 r1) {
                this();
            }

            private Builder() {
                super(TaskBarContainer.DEFAULT_INSTANCE);
            }

            public boolean hasIndex() {
                return ((TaskBarContainer) this.instance).hasIndex();
            }

            public int getIndex() {
                return ((TaskBarContainer) this.instance).getIndex();
            }

            public Builder setIndex(int i) {
                copyOnWrite();
                ((TaskBarContainer) this.instance).setIndex(i);
                return this;
            }

            public Builder clearIndex() {
                copyOnWrite();
                ((TaskBarContainer) this.instance).clearIndex();
                return this;
            }

            public boolean hasCardinality() {
                return ((TaskBarContainer) this.instance).hasCardinality();
            }

            public int getCardinality() {
                return ((TaskBarContainer) this.instance).getCardinality();
            }

            public Builder setCardinality(int i) {
                copyOnWrite();
                ((TaskBarContainer) this.instance).setCardinality(i);
                return this;
            }

            public Builder clearCardinality() {
                copyOnWrite();
                ((TaskBarContainer) this.instance).clearCardinality();
                return this;
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new TaskBarContainer();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                    TaskBarContainer taskBarContainer = (TaskBarContainer) obj2;
                    this.index_ = visitor.visitInt(hasIndex(), this.index_, taskBarContainer.hasIndex(), taskBarContainer.index_);
                    this.cardinality_ = visitor.visitInt(hasCardinality(), this.cardinality_, taskBarContainer.hasCardinality(), taskBarContainer.cardinality_);
                    if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                        this.bitField0_ |= taskBarContainer.bitField0_;
                    }
                    return this;
                case 6:
                    CodedInputStream codedInputStream = (CodedInputStream) obj;
                    ExtensionRegistryLite extensionRegistryLite = (ExtensionRegistryLite) obj2;
                    boolean z = false;
                    while (!z) {
                        try {
                            int readTag = codedInputStream.readTag();
                            if (readTag != 0) {
                                if (readTag == 8) {
                                    this.bitField0_ |= 1;
                                    this.index_ = codedInputStream.readInt32();
                                } else if (readTag == 16) {
                                    this.bitField0_ |= 2;
                                    this.cardinality_ = codedInputStream.readInt32();
                                } else if (!parseUnknownField(readTag, codedInputStream)) {
                                }
                            }
                            z = true;
                        } catch (InvalidProtocolBufferException e) {
                            throw new RuntimeException(e.setUnfinishedMessage(this));
                        } catch (IOException e2) {
                            throw new RuntimeException(new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this));
                        }
                    }
                    break;
                case 7:
                    break;
                case 8:
                    if (PARSER == null) {
                        synchronized (TaskBarContainer.class) {
                            if (PARSER == null) {
                                PARSER = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                            }
                        }
                    }
                    return PARSER;
                default:
                    throw new UnsupportedOperationException();
            }
            return DEFAULT_INSTANCE;
        }

        static {
            TaskBarContainer taskBarContainer = new TaskBarContainer();
            DEFAULT_INSTANCE = taskBarContainer;
            taskBarContainer.makeImmutable();
        }

        public static TaskBarContainer getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<TaskBarContainer> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class Application extends GeneratedMessageLite<Application, Builder> implements ApplicationOrBuilder {
        public static final int COMPONENT_NAME_FIELD_NUMBER = 2;
        /* access modifiers changed from: private */
        public static final Application DEFAULT_INSTANCE;
        public static final int PACKAGE_NAME_FIELD_NUMBER = 1;
        private static volatile Parser<Application> PARSER;
        private int bitField0_;
        private String componentName_ = "";
        private String packageName_ = "";

        private Application() {
        }

        public boolean hasPackageName() {
            return (this.bitField0_ & 1) == 1;
        }

        public String getPackageName() {
            return this.packageName_;
        }

        public ByteString getPackageNameBytes() {
            return ByteString.copyFromUtf8(this.packageName_);
        }

        /* access modifiers changed from: private */
        public void setPackageName(String str) {
            Objects.requireNonNull(str);
            this.bitField0_ |= 1;
            this.packageName_ = str;
        }

        /* access modifiers changed from: private */
        public void clearPackageName() {
            this.bitField0_ &= -2;
            this.packageName_ = getDefaultInstance().getPackageName();
        }

        /* access modifiers changed from: private */
        public void setPackageNameBytes(ByteString byteString) {
            Objects.requireNonNull(byteString);
            this.bitField0_ |= 1;
            this.packageName_ = byteString.toStringUtf8();
        }

        public boolean hasComponentName() {
            return (this.bitField0_ & 2) == 2;
        }

        public String getComponentName() {
            return this.componentName_;
        }

        public ByteString getComponentNameBytes() {
            return ByteString.copyFromUtf8(this.componentName_);
        }

        /* access modifiers changed from: private */
        public void setComponentName(String str) {
            Objects.requireNonNull(str);
            this.bitField0_ |= 2;
            this.componentName_ = str;
        }

        /* access modifiers changed from: private */
        public void clearComponentName() {
            this.bitField0_ &= -3;
            this.componentName_ = getDefaultInstance().getComponentName();
        }

        /* access modifiers changed from: private */
        public void setComponentNameBytes(ByteString byteString) {
            Objects.requireNonNull(byteString);
            this.bitField0_ |= 2;
            this.componentName_ = byteString.toStringUtf8();
        }

        public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
            if ((this.bitField0_ & 1) == 1) {
                codedOutputStream.writeString(1, getPackageName());
            }
            if ((this.bitField0_ & 2) == 2) {
                codedOutputStream.writeString(2, getComponentName());
            }
            this.unknownFields.writeTo(codedOutputStream);
        }

        public int getSerializedSize() {
            int i = this.memoizedSerializedSize;
            if (i != -1) {
                return i;
            }
            int i2 = 0;
            if ((this.bitField0_ & 1) == 1) {
                i2 = 0 + CodedOutputStream.computeStringSize(1, getPackageName());
            }
            if ((this.bitField0_ & 2) == 2) {
                i2 += CodedOutputStream.computeStringSize(2, getComponentName());
            }
            int serializedSize = i2 + this.unknownFields.getSerializedSize();
            this.memoizedSerializedSize = serializedSize;
            return serializedSize;
        }

        public static Application parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (Application) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static Application parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (Application) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static Application parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (Application) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static Application parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (Application) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static Application parseFrom(InputStream inputStream) throws IOException {
            return (Application) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static Application parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Application) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static Application parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (Application) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static Application parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Application) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static Application parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (Application) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static Application parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Application) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(Application application) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(application);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<Application, Builder> implements ApplicationOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 r1) {
                this();
            }

            private Builder() {
                super(Application.DEFAULT_INSTANCE);
            }

            public boolean hasPackageName() {
                return ((Application) this.instance).hasPackageName();
            }

            public String getPackageName() {
                return ((Application) this.instance).getPackageName();
            }

            public ByteString getPackageNameBytes() {
                return ((Application) this.instance).getPackageNameBytes();
            }

            public Builder setPackageName(String str) {
                copyOnWrite();
                ((Application) this.instance).setPackageName(str);
                return this;
            }

            public Builder clearPackageName() {
                copyOnWrite();
                ((Application) this.instance).clearPackageName();
                return this;
            }

            public Builder setPackageNameBytes(ByteString byteString) {
                copyOnWrite();
                ((Application) this.instance).setPackageNameBytes(byteString);
                return this;
            }

            public boolean hasComponentName() {
                return ((Application) this.instance).hasComponentName();
            }

            public String getComponentName() {
                return ((Application) this.instance).getComponentName();
            }

            public ByteString getComponentNameBytes() {
                return ((Application) this.instance).getComponentNameBytes();
            }

            public Builder setComponentName(String str) {
                copyOnWrite();
                ((Application) this.instance).setComponentName(str);
                return this;
            }

            public Builder clearComponentName() {
                copyOnWrite();
                ((Application) this.instance).clearComponentName();
                return this;
            }

            public Builder setComponentNameBytes(ByteString byteString) {
                copyOnWrite();
                ((Application) this.instance).setComponentNameBytes(byteString);
                return this;
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new Application();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                    Application application = (Application) obj2;
                    this.packageName_ = visitor.visitString(hasPackageName(), this.packageName_, application.hasPackageName(), application.packageName_);
                    this.componentName_ = visitor.visitString(hasComponentName(), this.componentName_, application.hasComponentName(), application.componentName_);
                    if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                        this.bitField0_ |= application.bitField0_;
                    }
                    return this;
                case 6:
                    CodedInputStream codedInputStream = (CodedInputStream) obj;
                    ExtensionRegistryLite extensionRegistryLite = (ExtensionRegistryLite) obj2;
                    boolean z = false;
                    while (!z) {
                        try {
                            int readTag = codedInputStream.readTag();
                            if (readTag != 0) {
                                if (readTag == 10) {
                                    String readString = codedInputStream.readString();
                                    this.bitField0_ = 1 | this.bitField0_;
                                    this.packageName_ = readString;
                                } else if (readTag == 18) {
                                    String readString2 = codedInputStream.readString();
                                    this.bitField0_ |= 2;
                                    this.componentName_ = readString2;
                                } else if (!parseUnknownField(readTag, codedInputStream)) {
                                }
                            }
                            z = true;
                        } catch (InvalidProtocolBufferException e) {
                            throw new RuntimeException(e.setUnfinishedMessage(this));
                        } catch (IOException e2) {
                            throw new RuntimeException(new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this));
                        }
                    }
                    break;
                case 7:
                    break;
                case 8:
                    if (PARSER == null) {
                        synchronized (Application.class) {
                            if (PARSER == null) {
                                PARSER = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                            }
                        }
                    }
                    return PARSER;
                default:
                    throw new UnsupportedOperationException();
            }
            return DEFAULT_INSTANCE;
        }

        static {
            Application application = new Application();
            DEFAULT_INSTANCE = application;
            application.makeImmutable();
        }

        public static Application getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<Application> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class Shortcut extends GeneratedMessageLite<Shortcut, Builder> implements ShortcutOrBuilder {
        /* access modifiers changed from: private */
        public static final Shortcut DEFAULT_INSTANCE;
        private static volatile Parser<Shortcut> PARSER = null;
        public static final int SHORTCUT_ID_FIELD_NUMBER = 2;
        public static final int SHORTCUT_NAME_FIELD_NUMBER = 1;
        private int bitField0_;
        private String shortcutId_ = "";
        private String shortcutName_ = "";

        private Shortcut() {
        }

        public boolean hasShortcutName() {
            return (this.bitField0_ & 1) == 1;
        }

        public String getShortcutName() {
            return this.shortcutName_;
        }

        public ByteString getShortcutNameBytes() {
            return ByteString.copyFromUtf8(this.shortcutName_);
        }

        /* access modifiers changed from: private */
        public void setShortcutName(String str) {
            Objects.requireNonNull(str);
            this.bitField0_ |= 1;
            this.shortcutName_ = str;
        }

        /* access modifiers changed from: private */
        public void clearShortcutName() {
            this.bitField0_ &= -2;
            this.shortcutName_ = getDefaultInstance().getShortcutName();
        }

        /* access modifiers changed from: private */
        public void setShortcutNameBytes(ByteString byteString) {
            Objects.requireNonNull(byteString);
            this.bitField0_ |= 1;
            this.shortcutName_ = byteString.toStringUtf8();
        }

        public boolean hasShortcutId() {
            return (this.bitField0_ & 2) == 2;
        }

        public String getShortcutId() {
            return this.shortcutId_;
        }

        public ByteString getShortcutIdBytes() {
            return ByteString.copyFromUtf8(this.shortcutId_);
        }

        /* access modifiers changed from: private */
        public void setShortcutId(String str) {
            Objects.requireNonNull(str);
            this.bitField0_ |= 2;
            this.shortcutId_ = str;
        }

        /* access modifiers changed from: private */
        public void clearShortcutId() {
            this.bitField0_ &= -3;
            this.shortcutId_ = getDefaultInstance().getShortcutId();
        }

        /* access modifiers changed from: private */
        public void setShortcutIdBytes(ByteString byteString) {
            Objects.requireNonNull(byteString);
            this.bitField0_ |= 2;
            this.shortcutId_ = byteString.toStringUtf8();
        }

        public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
            if ((this.bitField0_ & 1) == 1) {
                codedOutputStream.writeString(1, getShortcutName());
            }
            if ((this.bitField0_ & 2) == 2) {
                codedOutputStream.writeString(2, getShortcutId());
            }
            this.unknownFields.writeTo(codedOutputStream);
        }

        public int getSerializedSize() {
            int i = this.memoizedSerializedSize;
            if (i != -1) {
                return i;
            }
            int i2 = 0;
            if ((this.bitField0_ & 1) == 1) {
                i2 = 0 + CodedOutputStream.computeStringSize(1, getShortcutName());
            }
            if ((this.bitField0_ & 2) == 2) {
                i2 += CodedOutputStream.computeStringSize(2, getShortcutId());
            }
            int serializedSize = i2 + this.unknownFields.getSerializedSize();
            this.memoizedSerializedSize = serializedSize;
            return serializedSize;
        }

        public static Shortcut parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (Shortcut) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static Shortcut parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (Shortcut) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static Shortcut parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (Shortcut) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static Shortcut parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (Shortcut) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static Shortcut parseFrom(InputStream inputStream) throws IOException {
            return (Shortcut) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static Shortcut parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Shortcut) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static Shortcut parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (Shortcut) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static Shortcut parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Shortcut) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static Shortcut parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (Shortcut) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static Shortcut parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Shortcut) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(Shortcut shortcut) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(shortcut);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<Shortcut, Builder> implements ShortcutOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 r1) {
                this();
            }

            private Builder() {
                super(Shortcut.DEFAULT_INSTANCE);
            }

            public boolean hasShortcutName() {
                return ((Shortcut) this.instance).hasShortcutName();
            }

            public String getShortcutName() {
                return ((Shortcut) this.instance).getShortcutName();
            }

            public ByteString getShortcutNameBytes() {
                return ((Shortcut) this.instance).getShortcutNameBytes();
            }

            public Builder setShortcutName(String str) {
                copyOnWrite();
                ((Shortcut) this.instance).setShortcutName(str);
                return this;
            }

            public Builder clearShortcutName() {
                copyOnWrite();
                ((Shortcut) this.instance).clearShortcutName();
                return this;
            }

            public Builder setShortcutNameBytes(ByteString byteString) {
                copyOnWrite();
                ((Shortcut) this.instance).setShortcutNameBytes(byteString);
                return this;
            }

            public boolean hasShortcutId() {
                return ((Shortcut) this.instance).hasShortcutId();
            }

            public String getShortcutId() {
                return ((Shortcut) this.instance).getShortcutId();
            }

            public ByteString getShortcutIdBytes() {
                return ((Shortcut) this.instance).getShortcutIdBytes();
            }

            public Builder setShortcutId(String str) {
                copyOnWrite();
                ((Shortcut) this.instance).setShortcutId(str);
                return this;
            }

            public Builder clearShortcutId() {
                copyOnWrite();
                ((Shortcut) this.instance).clearShortcutId();
                return this;
            }

            public Builder setShortcutIdBytes(ByteString byteString) {
                copyOnWrite();
                ((Shortcut) this.instance).setShortcutIdBytes(byteString);
                return this;
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new Shortcut();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                    Shortcut shortcut = (Shortcut) obj2;
                    this.shortcutName_ = visitor.visitString(hasShortcutName(), this.shortcutName_, shortcut.hasShortcutName(), shortcut.shortcutName_);
                    this.shortcutId_ = visitor.visitString(hasShortcutId(), this.shortcutId_, shortcut.hasShortcutId(), shortcut.shortcutId_);
                    if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                        this.bitField0_ |= shortcut.bitField0_;
                    }
                    return this;
                case 6:
                    CodedInputStream codedInputStream = (CodedInputStream) obj;
                    ExtensionRegistryLite extensionRegistryLite = (ExtensionRegistryLite) obj2;
                    boolean z = false;
                    while (!z) {
                        try {
                            int readTag = codedInputStream.readTag();
                            if (readTag != 0) {
                                if (readTag == 10) {
                                    String readString = codedInputStream.readString();
                                    this.bitField0_ = 1 | this.bitField0_;
                                    this.shortcutName_ = readString;
                                } else if (readTag == 18) {
                                    String readString2 = codedInputStream.readString();
                                    this.bitField0_ |= 2;
                                    this.shortcutId_ = readString2;
                                } else if (!parseUnknownField(readTag, codedInputStream)) {
                                }
                            }
                            z = true;
                        } catch (InvalidProtocolBufferException e) {
                            throw new RuntimeException(e.setUnfinishedMessage(this));
                        } catch (IOException e2) {
                            throw new RuntimeException(new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this));
                        }
                    }
                    break;
                case 7:
                    break;
                case 8:
                    if (PARSER == null) {
                        synchronized (Shortcut.class) {
                            if (PARSER == null) {
                                PARSER = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                            }
                        }
                    }
                    return PARSER;
                default:
                    throw new UnsupportedOperationException();
            }
            return DEFAULT_INSTANCE;
        }

        static {
            Shortcut shortcut = new Shortcut();
            DEFAULT_INSTANCE = shortcut;
            shortcut.makeImmutable();
        }

        public static Shortcut getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<Shortcut> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class Widget extends GeneratedMessageLite<Widget, Builder> implements WidgetOrBuilder {
        public static final int APP_WIDGET_ID_FIELD_NUMBER = 3;
        public static final int COMPONENT_NAME_FIELD_NUMBER = 5;
        /* access modifiers changed from: private */
        public static final Widget DEFAULT_INSTANCE;
        public static final int PACKAGE_NAME_FIELD_NUMBER = 4;
        private static volatile Parser<Widget> PARSER = null;
        public static final int SPAN_X_FIELD_NUMBER = 1;
        public static final int SPAN_Y_FIELD_NUMBER = 2;
        public static final int WIDGET_FEATURES_FIELD_NUMBER = 6;
        private int appWidgetId_;
        private int bitField0_;
        private String componentName_ = "";
        private String packageName_ = "";
        private int spanX_ = 1;
        private int spanY_ = 1;
        private int widgetFeatures_;

        private Widget() {
        }

        public boolean hasSpanX() {
            return (this.bitField0_ & 1) == 1;
        }

        public int getSpanX() {
            return this.spanX_;
        }

        /* access modifiers changed from: private */
        public void setSpanX(int i) {
            this.bitField0_ |= 1;
            this.spanX_ = i;
        }

        /* access modifiers changed from: private */
        public void clearSpanX() {
            this.bitField0_ &= -2;
            this.spanX_ = 1;
        }

        public boolean hasSpanY() {
            return (this.bitField0_ & 2) == 2;
        }

        public int getSpanY() {
            return this.spanY_;
        }

        /* access modifiers changed from: private */
        public void setSpanY(int i) {
            this.bitField0_ |= 2;
            this.spanY_ = i;
        }

        /* access modifiers changed from: private */
        public void clearSpanY() {
            this.bitField0_ &= -3;
            this.spanY_ = 1;
        }

        public boolean hasAppWidgetId() {
            return (this.bitField0_ & 4) == 4;
        }

        public int getAppWidgetId() {
            return this.appWidgetId_;
        }

        /* access modifiers changed from: private */
        public void setAppWidgetId(int i) {
            this.bitField0_ |= 4;
            this.appWidgetId_ = i;
        }

        /* access modifiers changed from: private */
        public void clearAppWidgetId() {
            this.bitField0_ &= -5;
            this.appWidgetId_ = 0;
        }

        public boolean hasPackageName() {
            return (this.bitField0_ & 8) == 8;
        }

        public String getPackageName() {
            return this.packageName_;
        }

        public ByteString getPackageNameBytes() {
            return ByteString.copyFromUtf8(this.packageName_);
        }

        /* access modifiers changed from: private */
        public void setPackageName(String str) {
            Objects.requireNonNull(str);
            this.bitField0_ |= 8;
            this.packageName_ = str;
        }

        /* access modifiers changed from: private */
        public void clearPackageName() {
            this.bitField0_ &= -9;
            this.packageName_ = getDefaultInstance().getPackageName();
        }

        /* access modifiers changed from: private */
        public void setPackageNameBytes(ByteString byteString) {
            Objects.requireNonNull(byteString);
            this.bitField0_ |= 8;
            this.packageName_ = byteString.toStringUtf8();
        }

        public boolean hasComponentName() {
            return (this.bitField0_ & 16) == 16;
        }

        public String getComponentName() {
            return this.componentName_;
        }

        public ByteString getComponentNameBytes() {
            return ByteString.copyFromUtf8(this.componentName_);
        }

        /* access modifiers changed from: private */
        public void setComponentName(String str) {
            Objects.requireNonNull(str);
            this.bitField0_ |= 16;
            this.componentName_ = str;
        }

        /* access modifiers changed from: private */
        public void clearComponentName() {
            this.bitField0_ &= -17;
            this.componentName_ = getDefaultInstance().getComponentName();
        }

        /* access modifiers changed from: private */
        public void setComponentNameBytes(ByteString byteString) {
            Objects.requireNonNull(byteString);
            this.bitField0_ |= 16;
            this.componentName_ = byteString.toStringUtf8();
        }

        public boolean hasWidgetFeatures() {
            return (this.bitField0_ & 32) == 32;
        }

        public int getWidgetFeatures() {
            return this.widgetFeatures_;
        }

        /* access modifiers changed from: private */
        public void setWidgetFeatures(int i) {
            this.bitField0_ |= 32;
            this.widgetFeatures_ = i;
        }

        /* access modifiers changed from: private */
        public void clearWidgetFeatures() {
            this.bitField0_ &= -33;
            this.widgetFeatures_ = 0;
        }

        public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
            if ((this.bitField0_ & 1) == 1) {
                codedOutputStream.writeInt32(1, this.spanX_);
            }
            if ((this.bitField0_ & 2) == 2) {
                codedOutputStream.writeInt32(2, this.spanY_);
            }
            if ((this.bitField0_ & 4) == 4) {
                codedOutputStream.writeInt32(3, this.appWidgetId_);
            }
            if ((this.bitField0_ & 8) == 8) {
                codedOutputStream.writeString(4, getPackageName());
            }
            if ((this.bitField0_ & 16) == 16) {
                codedOutputStream.writeString(5, getComponentName());
            }
            if ((this.bitField0_ & 32) == 32) {
                codedOutputStream.writeInt32(6, this.widgetFeatures_);
            }
            this.unknownFields.writeTo(codedOutputStream);
        }

        public int getSerializedSize() {
            int i = this.memoizedSerializedSize;
            if (i != -1) {
                return i;
            }
            int i2 = 0;
            if ((this.bitField0_ & 1) == 1) {
                i2 = 0 + CodedOutputStream.computeInt32Size(1, this.spanX_);
            }
            if ((this.bitField0_ & 2) == 2) {
                i2 += CodedOutputStream.computeInt32Size(2, this.spanY_);
            }
            if ((this.bitField0_ & 4) == 4) {
                i2 += CodedOutputStream.computeInt32Size(3, this.appWidgetId_);
            }
            if ((this.bitField0_ & 8) == 8) {
                i2 += CodedOutputStream.computeStringSize(4, getPackageName());
            }
            if ((this.bitField0_ & 16) == 16) {
                i2 += CodedOutputStream.computeStringSize(5, getComponentName());
            }
            if ((this.bitField0_ & 32) == 32) {
                i2 += CodedOutputStream.computeInt32Size(6, this.widgetFeatures_);
            }
            int serializedSize = i2 + this.unknownFields.getSerializedSize();
            this.memoizedSerializedSize = serializedSize;
            return serializedSize;
        }

        public static Widget parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (Widget) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static Widget parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (Widget) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static Widget parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (Widget) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static Widget parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (Widget) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static Widget parseFrom(InputStream inputStream) throws IOException {
            return (Widget) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static Widget parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Widget) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static Widget parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (Widget) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static Widget parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Widget) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static Widget parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (Widget) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static Widget parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Widget) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(Widget widget) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(widget);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<Widget, Builder> implements WidgetOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 r1) {
                this();
            }

            private Builder() {
                super(Widget.DEFAULT_INSTANCE);
            }

            public boolean hasSpanX() {
                return ((Widget) this.instance).hasSpanX();
            }

            public int getSpanX() {
                return ((Widget) this.instance).getSpanX();
            }

            public Builder setSpanX(int i) {
                copyOnWrite();
                ((Widget) this.instance).setSpanX(i);
                return this;
            }

            public Builder clearSpanX() {
                copyOnWrite();
                ((Widget) this.instance).clearSpanX();
                return this;
            }

            public boolean hasSpanY() {
                return ((Widget) this.instance).hasSpanY();
            }

            public int getSpanY() {
                return ((Widget) this.instance).getSpanY();
            }

            public Builder setSpanY(int i) {
                copyOnWrite();
                ((Widget) this.instance).setSpanY(i);
                return this;
            }

            public Builder clearSpanY() {
                copyOnWrite();
                ((Widget) this.instance).clearSpanY();
                return this;
            }

            public boolean hasAppWidgetId() {
                return ((Widget) this.instance).hasAppWidgetId();
            }

            public int getAppWidgetId() {
                return ((Widget) this.instance).getAppWidgetId();
            }

            public Builder setAppWidgetId(int i) {
                copyOnWrite();
                ((Widget) this.instance).setAppWidgetId(i);
                return this;
            }

            public Builder clearAppWidgetId() {
                copyOnWrite();
                ((Widget) this.instance).clearAppWidgetId();
                return this;
            }

            public boolean hasPackageName() {
                return ((Widget) this.instance).hasPackageName();
            }

            public String getPackageName() {
                return ((Widget) this.instance).getPackageName();
            }

            public ByteString getPackageNameBytes() {
                return ((Widget) this.instance).getPackageNameBytes();
            }

            public Builder setPackageName(String str) {
                copyOnWrite();
                ((Widget) this.instance).setPackageName(str);
                return this;
            }

            public Builder clearPackageName() {
                copyOnWrite();
                ((Widget) this.instance).clearPackageName();
                return this;
            }

            public Builder setPackageNameBytes(ByteString byteString) {
                copyOnWrite();
                ((Widget) this.instance).setPackageNameBytes(byteString);
                return this;
            }

            public boolean hasComponentName() {
                return ((Widget) this.instance).hasComponentName();
            }

            public String getComponentName() {
                return ((Widget) this.instance).getComponentName();
            }

            public ByteString getComponentNameBytes() {
                return ((Widget) this.instance).getComponentNameBytes();
            }

            public Builder setComponentName(String str) {
                copyOnWrite();
                ((Widget) this.instance).setComponentName(str);
                return this;
            }

            public Builder clearComponentName() {
                copyOnWrite();
                ((Widget) this.instance).clearComponentName();
                return this;
            }

            public Builder setComponentNameBytes(ByteString byteString) {
                copyOnWrite();
                ((Widget) this.instance).setComponentNameBytes(byteString);
                return this;
            }

            public boolean hasWidgetFeatures() {
                return ((Widget) this.instance).hasWidgetFeatures();
            }

            public int getWidgetFeatures() {
                return ((Widget) this.instance).getWidgetFeatures();
            }

            public Builder setWidgetFeatures(int i) {
                copyOnWrite();
                ((Widget) this.instance).setWidgetFeatures(i);
                return this;
            }

            public Builder clearWidgetFeatures() {
                copyOnWrite();
                ((Widget) this.instance).clearWidgetFeatures();
                return this;
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new Widget();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                    Widget widget = (Widget) obj2;
                    this.spanX_ = visitor.visitInt(hasSpanX(), this.spanX_, widget.hasSpanX(), widget.spanX_);
                    this.spanY_ = visitor.visitInt(hasSpanY(), this.spanY_, widget.hasSpanY(), widget.spanY_);
                    this.appWidgetId_ = visitor.visitInt(hasAppWidgetId(), this.appWidgetId_, widget.hasAppWidgetId(), widget.appWidgetId_);
                    this.packageName_ = visitor.visitString(hasPackageName(), this.packageName_, widget.hasPackageName(), widget.packageName_);
                    this.componentName_ = visitor.visitString(hasComponentName(), this.componentName_, widget.hasComponentName(), widget.componentName_);
                    this.widgetFeatures_ = visitor.visitInt(hasWidgetFeatures(), this.widgetFeatures_, widget.hasWidgetFeatures(), widget.widgetFeatures_);
                    if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                        this.bitField0_ |= widget.bitField0_;
                    }
                    return this;
                case 6:
                    CodedInputStream codedInputStream = (CodedInputStream) obj;
                    ExtensionRegistryLite extensionRegistryLite = (ExtensionRegistryLite) obj2;
                    boolean z = false;
                    while (!z) {
                        try {
                            int readTag = codedInputStream.readTag();
                            if (readTag != 0) {
                                if (readTag == 8) {
                                    this.bitField0_ |= 1;
                                    this.spanX_ = codedInputStream.readInt32();
                                } else if (readTag == 16) {
                                    this.bitField0_ |= 2;
                                    this.spanY_ = codedInputStream.readInt32();
                                } else if (readTag == 24) {
                                    this.bitField0_ |= 4;
                                    this.appWidgetId_ = codedInputStream.readInt32();
                                } else if (readTag == 34) {
                                    String readString = codedInputStream.readString();
                                    this.bitField0_ |= 8;
                                    this.packageName_ = readString;
                                } else if (readTag == 42) {
                                    String readString2 = codedInputStream.readString();
                                    this.bitField0_ |= 16;
                                    this.componentName_ = readString2;
                                } else if (readTag == 48) {
                                    this.bitField0_ |= 32;
                                    this.widgetFeatures_ = codedInputStream.readInt32();
                                } else if (!parseUnknownField(readTag, codedInputStream)) {
                                }
                            }
                            z = true;
                        } catch (InvalidProtocolBufferException e) {
                            throw new RuntimeException(e.setUnfinishedMessage(this));
                        } catch (IOException e2) {
                            throw new RuntimeException(new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this));
                        }
                    }
                    break;
                case 7:
                    break;
                case 8:
                    if (PARSER == null) {
                        synchronized (Widget.class) {
                            if (PARSER == null) {
                                PARSER = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                            }
                        }
                    }
                    return PARSER;
                default:
                    throw new UnsupportedOperationException();
            }
            return DEFAULT_INSTANCE;
        }

        static {
            Widget widget = new Widget();
            DEFAULT_INSTANCE = widget;
            widget.makeImmutable();
        }

        public static Widget getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<Widget> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class Task extends GeneratedMessageLite<Task, Builder> implements TaskOrBuilder {
        public static final int COMPONENT_NAME_FIELD_NUMBER = 2;
        /* access modifiers changed from: private */
        public static final Task DEFAULT_INSTANCE;
        public static final int INDEX_FIELD_NUMBER = 3;
        public static final int PACKAGE_NAME_FIELD_NUMBER = 1;
        private static volatile Parser<Task> PARSER;
        private int bitField0_;
        private String componentName_ = "";
        private int index_;
        private String packageName_ = "";

        private Task() {
        }

        public boolean hasPackageName() {
            return (this.bitField0_ & 1) == 1;
        }

        public String getPackageName() {
            return this.packageName_;
        }

        public ByteString getPackageNameBytes() {
            return ByteString.copyFromUtf8(this.packageName_);
        }

        /* access modifiers changed from: private */
        public void setPackageName(String str) {
            Objects.requireNonNull(str);
            this.bitField0_ |= 1;
            this.packageName_ = str;
        }

        /* access modifiers changed from: private */
        public void clearPackageName() {
            this.bitField0_ &= -2;
            this.packageName_ = getDefaultInstance().getPackageName();
        }

        /* access modifiers changed from: private */
        public void setPackageNameBytes(ByteString byteString) {
            Objects.requireNonNull(byteString);
            this.bitField0_ |= 1;
            this.packageName_ = byteString.toStringUtf8();
        }

        public boolean hasComponentName() {
            return (this.bitField0_ & 2) == 2;
        }

        public String getComponentName() {
            return this.componentName_;
        }

        public ByteString getComponentNameBytes() {
            return ByteString.copyFromUtf8(this.componentName_);
        }

        /* access modifiers changed from: private */
        public void setComponentName(String str) {
            Objects.requireNonNull(str);
            this.bitField0_ |= 2;
            this.componentName_ = str;
        }

        /* access modifiers changed from: private */
        public void clearComponentName() {
            this.bitField0_ &= -3;
            this.componentName_ = getDefaultInstance().getComponentName();
        }

        /* access modifiers changed from: private */
        public void setComponentNameBytes(ByteString byteString) {
            Objects.requireNonNull(byteString);
            this.bitField0_ |= 2;
            this.componentName_ = byteString.toStringUtf8();
        }

        public boolean hasIndex() {
            return (this.bitField0_ & 4) == 4;
        }

        public int getIndex() {
            return this.index_;
        }

        /* access modifiers changed from: private */
        public void setIndex(int i) {
            this.bitField0_ |= 4;
            this.index_ = i;
        }

        /* access modifiers changed from: private */
        public void clearIndex() {
            this.bitField0_ &= -5;
            this.index_ = 0;
        }

        public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
            if ((this.bitField0_ & 1) == 1) {
                codedOutputStream.writeString(1, getPackageName());
            }
            if ((this.bitField0_ & 2) == 2) {
                codedOutputStream.writeString(2, getComponentName());
            }
            if ((this.bitField0_ & 4) == 4) {
                codedOutputStream.writeInt32(3, this.index_);
            }
            this.unknownFields.writeTo(codedOutputStream);
        }

        public int getSerializedSize() {
            int i = this.memoizedSerializedSize;
            if (i != -1) {
                return i;
            }
            int i2 = 0;
            if ((this.bitField0_ & 1) == 1) {
                i2 = 0 + CodedOutputStream.computeStringSize(1, getPackageName());
            }
            if ((this.bitField0_ & 2) == 2) {
                i2 += CodedOutputStream.computeStringSize(2, getComponentName());
            }
            if ((this.bitField0_ & 4) == 4) {
                i2 += CodedOutputStream.computeInt32Size(3, this.index_);
            }
            int serializedSize = i2 + this.unknownFields.getSerializedSize();
            this.memoizedSerializedSize = serializedSize;
            return serializedSize;
        }

        public static Task parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (Task) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static Task parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (Task) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static Task parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (Task) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static Task parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (Task) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static Task parseFrom(InputStream inputStream) throws IOException {
            return (Task) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static Task parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Task) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static Task parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (Task) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static Task parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Task) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static Task parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (Task) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static Task parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Task) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(Task task) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(task);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<Task, Builder> implements TaskOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 r1) {
                this();
            }

            private Builder() {
                super(Task.DEFAULT_INSTANCE);
            }

            public boolean hasPackageName() {
                return ((Task) this.instance).hasPackageName();
            }

            public String getPackageName() {
                return ((Task) this.instance).getPackageName();
            }

            public ByteString getPackageNameBytes() {
                return ((Task) this.instance).getPackageNameBytes();
            }

            public Builder setPackageName(String str) {
                copyOnWrite();
                ((Task) this.instance).setPackageName(str);
                return this;
            }

            public Builder clearPackageName() {
                copyOnWrite();
                ((Task) this.instance).clearPackageName();
                return this;
            }

            public Builder setPackageNameBytes(ByteString byteString) {
                copyOnWrite();
                ((Task) this.instance).setPackageNameBytes(byteString);
                return this;
            }

            public boolean hasComponentName() {
                return ((Task) this.instance).hasComponentName();
            }

            public String getComponentName() {
                return ((Task) this.instance).getComponentName();
            }

            public ByteString getComponentNameBytes() {
                return ((Task) this.instance).getComponentNameBytes();
            }

            public Builder setComponentName(String str) {
                copyOnWrite();
                ((Task) this.instance).setComponentName(str);
                return this;
            }

            public Builder clearComponentName() {
                copyOnWrite();
                ((Task) this.instance).clearComponentName();
                return this;
            }

            public Builder setComponentNameBytes(ByteString byteString) {
                copyOnWrite();
                ((Task) this.instance).setComponentNameBytes(byteString);
                return this;
            }

            public boolean hasIndex() {
                return ((Task) this.instance).hasIndex();
            }

            public int getIndex() {
                return ((Task) this.instance).getIndex();
            }

            public Builder setIndex(int i) {
                copyOnWrite();
                ((Task) this.instance).setIndex(i);
                return this;
            }

            public Builder clearIndex() {
                copyOnWrite();
                ((Task) this.instance).clearIndex();
                return this;
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new Task();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                    Task task = (Task) obj2;
                    this.packageName_ = visitor.visitString(hasPackageName(), this.packageName_, task.hasPackageName(), task.packageName_);
                    this.componentName_ = visitor.visitString(hasComponentName(), this.componentName_, task.hasComponentName(), task.componentName_);
                    this.index_ = visitor.visitInt(hasIndex(), this.index_, task.hasIndex(), task.index_);
                    if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                        this.bitField0_ |= task.bitField0_;
                    }
                    return this;
                case 6:
                    CodedInputStream codedInputStream = (CodedInputStream) obj;
                    ExtensionRegistryLite extensionRegistryLite = (ExtensionRegistryLite) obj2;
                    boolean z = false;
                    while (!z) {
                        try {
                            int readTag = codedInputStream.readTag();
                            if (readTag != 0) {
                                if (readTag == 10) {
                                    String readString = codedInputStream.readString();
                                    this.bitField0_ = 1 | this.bitField0_;
                                    this.packageName_ = readString;
                                } else if (readTag == 18) {
                                    String readString2 = codedInputStream.readString();
                                    this.bitField0_ |= 2;
                                    this.componentName_ = readString2;
                                } else if (readTag == 24) {
                                    this.bitField0_ |= 4;
                                    this.index_ = codedInputStream.readInt32();
                                } else if (!parseUnknownField(readTag, codedInputStream)) {
                                }
                            }
                            z = true;
                        } catch (InvalidProtocolBufferException e) {
                            throw new RuntimeException(e.setUnfinishedMessage(this));
                        } catch (IOException e2) {
                            throw new RuntimeException(new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this));
                        }
                    }
                    break;
                case 7:
                    break;
                case 8:
                    if (PARSER == null) {
                        synchronized (Task.class) {
                            if (PARSER == null) {
                                PARSER = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                            }
                        }
                    }
                    return PARSER;
                default:
                    throw new UnsupportedOperationException();
            }
            return DEFAULT_INSTANCE;
        }

        static {
            Task task = new Task();
            DEFAULT_INSTANCE = task;
            task.makeImmutable();
        }

        public static Task getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<Task> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class FolderIcon extends GeneratedMessageLite<FolderIcon, Builder> implements FolderIconOrBuilder {
        public static final int CARDINALITY_FIELD_NUMBER = 1;
        /* access modifiers changed from: private */
        public static final FolderIcon DEFAULT_INSTANCE;
        public static final int FROM_LABEL_STATE_FIELD_NUMBER = 2;
        public static final int LABEL_INFO_FIELD_NUMBER = 4;
        private static volatile Parser<FolderIcon> PARSER = null;
        public static final int TO_LABEL_STATE_FIELD_NUMBER = 3;
        private int bitField0_;
        private int cardinality_;
        private int fromLabelState_;
        private String labelInfo_ = "";
        private int toLabelState_;

        private FolderIcon() {
        }

        public boolean hasCardinality() {
            return (this.bitField0_ & 1) == 1;
        }

        public int getCardinality() {
            return this.cardinality_;
        }

        /* access modifiers changed from: private */
        public void setCardinality(int i) {
            this.bitField0_ |= 1;
            this.cardinality_ = i;
        }

        /* access modifiers changed from: private */
        public void clearCardinality() {
            this.bitField0_ &= -2;
            this.cardinality_ = 0;
        }

        public boolean hasFromLabelState() {
            return (this.bitField0_ & 2) == 2;
        }

        public FromState getFromLabelState() {
            FromState forNumber = FromState.forNumber(this.fromLabelState_);
            return forNumber == null ? FromState.FROM_STATE_UNSPECIFIED : forNumber;
        }

        /* access modifiers changed from: private */
        public void setFromLabelState(FromState fromState) {
            Objects.requireNonNull(fromState);
            this.bitField0_ |= 2;
            this.fromLabelState_ = fromState.getNumber();
        }

        /* access modifiers changed from: private */
        public void clearFromLabelState() {
            this.bitField0_ &= -3;
            this.fromLabelState_ = 0;
        }

        public boolean hasToLabelState() {
            return (this.bitField0_ & 4) == 4;
        }

        public ToState getToLabelState() {
            ToState forNumber = ToState.forNumber(this.toLabelState_);
            return forNumber == null ? ToState.TO_STATE_UNSPECIFIED : forNumber;
        }

        /* access modifiers changed from: private */
        public void setToLabelState(ToState toState) {
            Objects.requireNonNull(toState);
            this.bitField0_ |= 4;
            this.toLabelState_ = toState.getNumber();
        }

        /* access modifiers changed from: private */
        public void clearToLabelState() {
            this.bitField0_ &= -5;
            this.toLabelState_ = 0;
        }

        public boolean hasLabelInfo() {
            return (this.bitField0_ & 8) == 8;
        }

        public String getLabelInfo() {
            return this.labelInfo_;
        }

        public ByteString getLabelInfoBytes() {
            return ByteString.copyFromUtf8(this.labelInfo_);
        }

        /* access modifiers changed from: private */
        public void setLabelInfo(String str) {
            Objects.requireNonNull(str);
            this.bitField0_ |= 8;
            this.labelInfo_ = str;
        }

        /* access modifiers changed from: private */
        public void clearLabelInfo() {
            this.bitField0_ &= -9;
            this.labelInfo_ = getDefaultInstance().getLabelInfo();
        }

        /* access modifiers changed from: private */
        public void setLabelInfoBytes(ByteString byteString) {
            Objects.requireNonNull(byteString);
            this.bitField0_ |= 8;
            this.labelInfo_ = byteString.toStringUtf8();
        }

        public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
            if ((this.bitField0_ & 1) == 1) {
                codedOutputStream.writeInt32(1, this.cardinality_);
            }
            if ((this.bitField0_ & 2) == 2) {
                codedOutputStream.writeEnum(2, this.fromLabelState_);
            }
            if ((this.bitField0_ & 4) == 4) {
                codedOutputStream.writeEnum(3, this.toLabelState_);
            }
            if ((this.bitField0_ & 8) == 8) {
                codedOutputStream.writeString(4, getLabelInfo());
            }
            this.unknownFields.writeTo(codedOutputStream);
        }

        public int getSerializedSize() {
            int i = this.memoizedSerializedSize;
            if (i != -1) {
                return i;
            }
            int i2 = 0;
            if ((this.bitField0_ & 1) == 1) {
                i2 = 0 + CodedOutputStream.computeInt32Size(1, this.cardinality_);
            }
            if ((this.bitField0_ & 2) == 2) {
                i2 += CodedOutputStream.computeEnumSize(2, this.fromLabelState_);
            }
            if ((this.bitField0_ & 4) == 4) {
                i2 += CodedOutputStream.computeEnumSize(3, this.toLabelState_);
            }
            if ((this.bitField0_ & 8) == 8) {
                i2 += CodedOutputStream.computeStringSize(4, getLabelInfo());
            }
            int serializedSize = i2 + this.unknownFields.getSerializedSize();
            this.memoizedSerializedSize = serializedSize;
            return serializedSize;
        }

        public static FolderIcon parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (FolderIcon) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static FolderIcon parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (FolderIcon) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static FolderIcon parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (FolderIcon) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static FolderIcon parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (FolderIcon) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static FolderIcon parseFrom(InputStream inputStream) throws IOException {
            return (FolderIcon) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static FolderIcon parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (FolderIcon) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static FolderIcon parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (FolderIcon) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static FolderIcon parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (FolderIcon) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static FolderIcon parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (FolderIcon) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static FolderIcon parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (FolderIcon) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(FolderIcon folderIcon) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(folderIcon);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<FolderIcon, Builder> implements FolderIconOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 r1) {
                this();
            }

            private Builder() {
                super(FolderIcon.DEFAULT_INSTANCE);
            }

            public boolean hasCardinality() {
                return ((FolderIcon) this.instance).hasCardinality();
            }

            public int getCardinality() {
                return ((FolderIcon) this.instance).getCardinality();
            }

            public Builder setCardinality(int i) {
                copyOnWrite();
                ((FolderIcon) this.instance).setCardinality(i);
                return this;
            }

            public Builder clearCardinality() {
                copyOnWrite();
                ((FolderIcon) this.instance).clearCardinality();
                return this;
            }

            public boolean hasFromLabelState() {
                return ((FolderIcon) this.instance).hasFromLabelState();
            }

            public FromState getFromLabelState() {
                return ((FolderIcon) this.instance).getFromLabelState();
            }

            public Builder setFromLabelState(FromState fromState) {
                copyOnWrite();
                ((FolderIcon) this.instance).setFromLabelState(fromState);
                return this;
            }

            public Builder clearFromLabelState() {
                copyOnWrite();
                ((FolderIcon) this.instance).clearFromLabelState();
                return this;
            }

            public boolean hasToLabelState() {
                return ((FolderIcon) this.instance).hasToLabelState();
            }

            public ToState getToLabelState() {
                return ((FolderIcon) this.instance).getToLabelState();
            }

            public Builder setToLabelState(ToState toState) {
                copyOnWrite();
                ((FolderIcon) this.instance).setToLabelState(toState);
                return this;
            }

            public Builder clearToLabelState() {
                copyOnWrite();
                ((FolderIcon) this.instance).clearToLabelState();
                return this;
            }

            public boolean hasLabelInfo() {
                return ((FolderIcon) this.instance).hasLabelInfo();
            }

            public String getLabelInfo() {
                return ((FolderIcon) this.instance).getLabelInfo();
            }

            public ByteString getLabelInfoBytes() {
                return ((FolderIcon) this.instance).getLabelInfoBytes();
            }

            public Builder setLabelInfo(String str) {
                copyOnWrite();
                ((FolderIcon) this.instance).setLabelInfo(str);
                return this;
            }

            public Builder clearLabelInfo() {
                copyOnWrite();
                ((FolderIcon) this.instance).clearLabelInfo();
                return this;
            }

            public Builder setLabelInfoBytes(ByteString byteString) {
                copyOnWrite();
                ((FolderIcon) this.instance).setLabelInfoBytes(byteString);
                return this;
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new FolderIcon();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                    FolderIcon folderIcon = (FolderIcon) obj2;
                    this.cardinality_ = visitor.visitInt(hasCardinality(), this.cardinality_, folderIcon.hasCardinality(), folderIcon.cardinality_);
                    this.fromLabelState_ = visitor.visitInt(hasFromLabelState(), this.fromLabelState_, folderIcon.hasFromLabelState(), folderIcon.fromLabelState_);
                    this.toLabelState_ = visitor.visitInt(hasToLabelState(), this.toLabelState_, folderIcon.hasToLabelState(), folderIcon.toLabelState_);
                    this.labelInfo_ = visitor.visitString(hasLabelInfo(), this.labelInfo_, folderIcon.hasLabelInfo(), folderIcon.labelInfo_);
                    if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                        this.bitField0_ |= folderIcon.bitField0_;
                    }
                    return this;
                case 6:
                    CodedInputStream codedInputStream = (CodedInputStream) obj;
                    ExtensionRegistryLite extensionRegistryLite = (ExtensionRegistryLite) obj2;
                    boolean z = false;
                    while (!z) {
                        try {
                            int readTag = codedInputStream.readTag();
                            if (readTag != 0) {
                                if (readTag == 8) {
                                    this.bitField0_ |= 1;
                                    this.cardinality_ = codedInputStream.readInt32();
                                } else if (readTag == 16) {
                                    int readEnum = codedInputStream.readEnum();
                                    if (FromState.forNumber(readEnum) == null) {
                                        super.mergeVarintField(2, readEnum);
                                    } else {
                                        this.bitField0_ |= 2;
                                        this.fromLabelState_ = readEnum;
                                    }
                                } else if (readTag == 24) {
                                    int readEnum2 = codedInputStream.readEnum();
                                    if (ToState.forNumber(readEnum2) == null) {
                                        super.mergeVarintField(3, readEnum2);
                                    } else {
                                        this.bitField0_ |= 4;
                                        this.toLabelState_ = readEnum2;
                                    }
                                } else if (readTag == 34) {
                                    String readString = codedInputStream.readString();
                                    this.bitField0_ |= 8;
                                    this.labelInfo_ = readString;
                                } else if (!parseUnknownField(readTag, codedInputStream)) {
                                }
                            }
                            z = true;
                        } catch (InvalidProtocolBufferException e) {
                            throw new RuntimeException(e.setUnfinishedMessage(this));
                        } catch (IOException e2) {
                            throw new RuntimeException(new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this));
                        }
                    }
                    break;
                case 7:
                    break;
                case 8:
                    if (PARSER == null) {
                        synchronized (FolderIcon.class) {
                            if (PARSER == null) {
                                PARSER = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                            }
                        }
                    }
                    return PARSER;
                default:
                    throw new UnsupportedOperationException();
            }
            return DEFAULT_INSTANCE;
        }

        static {
            FolderIcon folderIcon = new FolderIcon();
            DEFAULT_INSTANCE = folderIcon;
            folderIcon.makeImmutable();
        }

        public static FolderIcon getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<FolderIcon> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class Slice extends GeneratedMessageLite<Slice, Builder> implements SliceOrBuilder {
        /* access modifiers changed from: private */
        public static final Slice DEFAULT_INSTANCE;
        private static volatile Parser<Slice> PARSER = null;
        public static final int URI_FIELD_NUMBER = 1;
        private int bitField0_;
        private String uri_ = "";

        private Slice() {
        }

        public boolean hasUri() {
            return (this.bitField0_ & 1) == 1;
        }

        public String getUri() {
            return this.uri_;
        }

        public ByteString getUriBytes() {
            return ByteString.copyFromUtf8(this.uri_);
        }

        /* access modifiers changed from: private */
        public void setUri(String str) {
            Objects.requireNonNull(str);
            this.bitField0_ |= 1;
            this.uri_ = str;
        }

        /* access modifiers changed from: private */
        public void clearUri() {
            this.bitField0_ &= -2;
            this.uri_ = getDefaultInstance().getUri();
        }

        /* access modifiers changed from: private */
        public void setUriBytes(ByteString byteString) {
            Objects.requireNonNull(byteString);
            this.bitField0_ |= 1;
            this.uri_ = byteString.toStringUtf8();
        }

        public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
            if ((this.bitField0_ & 1) == 1) {
                codedOutputStream.writeString(1, getUri());
            }
            this.unknownFields.writeTo(codedOutputStream);
        }

        public int getSerializedSize() {
            int i = this.memoizedSerializedSize;
            if (i != -1) {
                return i;
            }
            int i2 = 0;
            if ((this.bitField0_ & 1) == 1) {
                i2 = 0 + CodedOutputStream.computeStringSize(1, getUri());
            }
            int serializedSize = i2 + this.unknownFields.getSerializedSize();
            this.memoizedSerializedSize = serializedSize;
            return serializedSize;
        }

        public static Slice parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (Slice) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static Slice parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (Slice) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static Slice parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (Slice) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static Slice parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (Slice) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static Slice parseFrom(InputStream inputStream) throws IOException {
            return (Slice) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static Slice parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Slice) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static Slice parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (Slice) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static Slice parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Slice) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static Slice parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (Slice) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static Slice parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Slice) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(Slice slice) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(slice);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<Slice, Builder> implements SliceOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 r1) {
                this();
            }

            private Builder() {
                super(Slice.DEFAULT_INSTANCE);
            }

            public boolean hasUri() {
                return ((Slice) this.instance).hasUri();
            }

            public String getUri() {
                return ((Slice) this.instance).getUri();
            }

            public ByteString getUriBytes() {
                return ((Slice) this.instance).getUriBytes();
            }

            public Builder setUri(String str) {
                copyOnWrite();
                ((Slice) this.instance).setUri(str);
                return this;
            }

            public Builder clearUri() {
                copyOnWrite();
                ((Slice) this.instance).clearUri();
                return this;
            }

            public Builder setUriBytes(ByteString byteString) {
                copyOnWrite();
                ((Slice) this.instance).setUriBytes(byteString);
                return this;
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new Slice();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                    Slice slice = (Slice) obj2;
                    this.uri_ = visitor.visitString(hasUri(), this.uri_, slice.hasUri(), slice.uri_);
                    if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                        this.bitField0_ |= slice.bitField0_;
                    }
                    return this;
                case 6:
                    CodedInputStream codedInputStream = (CodedInputStream) obj;
                    ExtensionRegistryLite extensionRegistryLite = (ExtensionRegistryLite) obj2;
                    boolean z = false;
                    while (!z) {
                        try {
                            int readTag = codedInputStream.readTag();
                            if (readTag != 0) {
                                if (readTag == 10) {
                                    String readString = codedInputStream.readString();
                                    this.bitField0_ = 1 | this.bitField0_;
                                    this.uri_ = readString;
                                } else if (!parseUnknownField(readTag, codedInputStream)) {
                                }
                            }
                            z = true;
                        } catch (InvalidProtocolBufferException e) {
                            throw new RuntimeException(e.setUnfinishedMessage(this));
                        } catch (IOException e2) {
                            throw new RuntimeException(new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this));
                        }
                    }
                    break;
                case 7:
                    break;
                case 8:
                    if (PARSER == null) {
                        synchronized (Slice.class) {
                            if (PARSER == null) {
                                PARSER = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                            }
                        }
                    }
                    return PARSER;
                default:
                    throw new UnsupportedOperationException();
            }
            return DEFAULT_INSTANCE;
        }

        static {
            Slice slice = new Slice();
            DEFAULT_INSTANCE = slice;
            slice.makeImmutable();
        }

        public static Slice getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<Slice> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class SearchActionItem extends GeneratedMessageLite<SearchActionItem, Builder> implements SearchActionItemOrBuilder {
        /* access modifiers changed from: private */
        public static final SearchActionItem DEFAULT_INSTANCE;
        public static final int PACKAGE_NAME_FIELD_NUMBER = 1;
        private static volatile Parser<SearchActionItem> PARSER = null;
        public static final int TITLE_FIELD_NUMBER = 2;
        private int bitField0_;
        private String packageName_ = "";
        private String title_ = "";

        private SearchActionItem() {
        }

        public boolean hasPackageName() {
            return (this.bitField0_ & 1) == 1;
        }

        public String getPackageName() {
            return this.packageName_;
        }

        public ByteString getPackageNameBytes() {
            return ByteString.copyFromUtf8(this.packageName_);
        }

        /* access modifiers changed from: private */
        public void setPackageName(String str) {
            Objects.requireNonNull(str);
            this.bitField0_ |= 1;
            this.packageName_ = str;
        }

        /* access modifiers changed from: private */
        public void clearPackageName() {
            this.bitField0_ &= -2;
            this.packageName_ = getDefaultInstance().getPackageName();
        }

        /* access modifiers changed from: private */
        public void setPackageNameBytes(ByteString byteString) {
            Objects.requireNonNull(byteString);
            this.bitField0_ |= 1;
            this.packageName_ = byteString.toStringUtf8();
        }

        public boolean hasTitle() {
            return (this.bitField0_ & 2) == 2;
        }

        public String getTitle() {
            return this.title_;
        }

        public ByteString getTitleBytes() {
            return ByteString.copyFromUtf8(this.title_);
        }

        /* access modifiers changed from: private */
        public void setTitle(String str) {
            Objects.requireNonNull(str);
            this.bitField0_ |= 2;
            this.title_ = str;
        }

        /* access modifiers changed from: private */
        public void clearTitle() {
            this.bitField0_ &= -3;
            this.title_ = getDefaultInstance().getTitle();
        }

        /* access modifiers changed from: private */
        public void setTitleBytes(ByteString byteString) {
            Objects.requireNonNull(byteString);
            this.bitField0_ |= 2;
            this.title_ = byteString.toStringUtf8();
        }

        public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
            if ((this.bitField0_ & 1) == 1) {
                codedOutputStream.writeString(1, getPackageName());
            }
            if ((this.bitField0_ & 2) == 2) {
                codedOutputStream.writeString(2, getTitle());
            }
            this.unknownFields.writeTo(codedOutputStream);
        }

        public int getSerializedSize() {
            int i = this.memoizedSerializedSize;
            if (i != -1) {
                return i;
            }
            int i2 = 0;
            if ((this.bitField0_ & 1) == 1) {
                i2 = 0 + CodedOutputStream.computeStringSize(1, getPackageName());
            }
            if ((this.bitField0_ & 2) == 2) {
                i2 += CodedOutputStream.computeStringSize(2, getTitle());
            }
            int serializedSize = i2 + this.unknownFields.getSerializedSize();
            this.memoizedSerializedSize = serializedSize;
            return serializedSize;
        }

        public static SearchActionItem parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (SearchActionItem) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static SearchActionItem parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (SearchActionItem) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static SearchActionItem parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (SearchActionItem) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static SearchActionItem parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (SearchActionItem) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static SearchActionItem parseFrom(InputStream inputStream) throws IOException {
            return (SearchActionItem) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static SearchActionItem parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (SearchActionItem) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static SearchActionItem parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (SearchActionItem) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static SearchActionItem parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (SearchActionItem) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static SearchActionItem parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (SearchActionItem) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static SearchActionItem parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (SearchActionItem) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(SearchActionItem searchActionItem) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(searchActionItem);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<SearchActionItem, Builder> implements SearchActionItemOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 r1) {
                this();
            }

            private Builder() {
                super(SearchActionItem.DEFAULT_INSTANCE);
            }

            public boolean hasPackageName() {
                return ((SearchActionItem) this.instance).hasPackageName();
            }

            public String getPackageName() {
                return ((SearchActionItem) this.instance).getPackageName();
            }

            public ByteString getPackageNameBytes() {
                return ((SearchActionItem) this.instance).getPackageNameBytes();
            }

            public Builder setPackageName(String str) {
                copyOnWrite();
                ((SearchActionItem) this.instance).setPackageName(str);
                return this;
            }

            public Builder clearPackageName() {
                copyOnWrite();
                ((SearchActionItem) this.instance).clearPackageName();
                return this;
            }

            public Builder setPackageNameBytes(ByteString byteString) {
                copyOnWrite();
                ((SearchActionItem) this.instance).setPackageNameBytes(byteString);
                return this;
            }

            public boolean hasTitle() {
                return ((SearchActionItem) this.instance).hasTitle();
            }

            public String getTitle() {
                return ((SearchActionItem) this.instance).getTitle();
            }

            public ByteString getTitleBytes() {
                return ((SearchActionItem) this.instance).getTitleBytes();
            }

            public Builder setTitle(String str) {
                copyOnWrite();
                ((SearchActionItem) this.instance).setTitle(str);
                return this;
            }

            public Builder clearTitle() {
                copyOnWrite();
                ((SearchActionItem) this.instance).clearTitle();
                return this;
            }

            public Builder setTitleBytes(ByteString byteString) {
                copyOnWrite();
                ((SearchActionItem) this.instance).setTitleBytes(byteString);
                return this;
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new SearchActionItem();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                    SearchActionItem searchActionItem = (SearchActionItem) obj2;
                    this.packageName_ = visitor.visitString(hasPackageName(), this.packageName_, searchActionItem.hasPackageName(), searchActionItem.packageName_);
                    this.title_ = visitor.visitString(hasTitle(), this.title_, searchActionItem.hasTitle(), searchActionItem.title_);
                    if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                        this.bitField0_ |= searchActionItem.bitField0_;
                    }
                    return this;
                case 6:
                    CodedInputStream codedInputStream = (CodedInputStream) obj;
                    ExtensionRegistryLite extensionRegistryLite = (ExtensionRegistryLite) obj2;
                    boolean z = false;
                    while (!z) {
                        try {
                            int readTag = codedInputStream.readTag();
                            if (readTag != 0) {
                                if (readTag == 10) {
                                    String readString = codedInputStream.readString();
                                    this.bitField0_ = 1 | this.bitField0_;
                                    this.packageName_ = readString;
                                } else if (readTag == 18) {
                                    String readString2 = codedInputStream.readString();
                                    this.bitField0_ |= 2;
                                    this.title_ = readString2;
                                } else if (!parseUnknownField(readTag, codedInputStream)) {
                                }
                            }
                            z = true;
                        } catch (InvalidProtocolBufferException e) {
                            throw new RuntimeException(e.setUnfinishedMessage(this));
                        } catch (IOException e2) {
                            throw new RuntimeException(new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this));
                        }
                    }
                    break;
                case 7:
                    break;
                case 8:
                    if (PARSER == null) {
                        synchronized (SearchActionItem.class) {
                            if (PARSER == null) {
                                PARSER = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                            }
                        }
                    }
                    return PARSER;
                default:
                    throw new UnsupportedOperationException();
            }
            return DEFAULT_INSTANCE;
        }

        static {
            SearchActionItem searchActionItem = new SearchActionItem();
            DEFAULT_INSTANCE = searchActionItem;
            searchActionItem.makeImmutable();
        }

        public static SearchActionItem getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<SearchActionItem> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class WorkspaceContainer extends GeneratedMessageLite<WorkspaceContainer, Builder> implements WorkspaceContainerOrBuilder {
        /* access modifiers changed from: private */
        public static final WorkspaceContainer DEFAULT_INSTANCE;
        public static final int GRID_X_FIELD_NUMBER = 2;
        public static final int GRID_Y_FIELD_NUMBER = 3;
        public static final int PAGE_INDEX_FIELD_NUMBER = 1;
        private static volatile Parser<WorkspaceContainer> PARSER;
        private int bitField0_;
        private int gridX_ = -1;
        private int gridY_ = -1;
        private int pageIndex_ = -2;

        private WorkspaceContainer() {
        }

        public boolean hasPageIndex() {
            return (this.bitField0_ & 1) == 1;
        }

        public int getPageIndex() {
            return this.pageIndex_;
        }

        /* access modifiers changed from: private */
        public void setPageIndex(int i) {
            this.bitField0_ |= 1;
            this.pageIndex_ = i;
        }

        /* access modifiers changed from: private */
        public void clearPageIndex() {
            this.bitField0_ &= -2;
            this.pageIndex_ = -2;
        }

        public boolean hasGridX() {
            return (this.bitField0_ & 2) == 2;
        }

        public int getGridX() {
            return this.gridX_;
        }

        /* access modifiers changed from: private */
        public void setGridX(int i) {
            this.bitField0_ |= 2;
            this.gridX_ = i;
        }

        /* access modifiers changed from: private */
        public void clearGridX() {
            this.bitField0_ &= -3;
            this.gridX_ = -1;
        }

        public boolean hasGridY() {
            return (this.bitField0_ & 4) == 4;
        }

        public int getGridY() {
            return this.gridY_;
        }

        /* access modifiers changed from: private */
        public void setGridY(int i) {
            this.bitField0_ |= 4;
            this.gridY_ = i;
        }

        /* access modifiers changed from: private */
        public void clearGridY() {
            this.bitField0_ &= -5;
            this.gridY_ = -1;
        }

        public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
            if ((this.bitField0_ & 1) == 1) {
                codedOutputStream.writeInt32(1, this.pageIndex_);
            }
            if ((this.bitField0_ & 2) == 2) {
                codedOutputStream.writeInt32(2, this.gridX_);
            }
            if ((this.bitField0_ & 4) == 4) {
                codedOutputStream.writeInt32(3, this.gridY_);
            }
            this.unknownFields.writeTo(codedOutputStream);
        }

        public int getSerializedSize() {
            int i = this.memoizedSerializedSize;
            if (i != -1) {
                return i;
            }
            int i2 = 0;
            if ((this.bitField0_ & 1) == 1) {
                i2 = 0 + CodedOutputStream.computeInt32Size(1, this.pageIndex_);
            }
            if ((this.bitField0_ & 2) == 2) {
                i2 += CodedOutputStream.computeInt32Size(2, this.gridX_);
            }
            if ((this.bitField0_ & 4) == 4) {
                i2 += CodedOutputStream.computeInt32Size(3, this.gridY_);
            }
            int serializedSize = i2 + this.unknownFields.getSerializedSize();
            this.memoizedSerializedSize = serializedSize;
            return serializedSize;
        }

        public static WorkspaceContainer parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (WorkspaceContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static WorkspaceContainer parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (WorkspaceContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static WorkspaceContainer parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (WorkspaceContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static WorkspaceContainer parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (WorkspaceContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static WorkspaceContainer parseFrom(InputStream inputStream) throws IOException {
            return (WorkspaceContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static WorkspaceContainer parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (WorkspaceContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static WorkspaceContainer parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (WorkspaceContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static WorkspaceContainer parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (WorkspaceContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static WorkspaceContainer parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (WorkspaceContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static WorkspaceContainer parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (WorkspaceContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(WorkspaceContainer workspaceContainer) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(workspaceContainer);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<WorkspaceContainer, Builder> implements WorkspaceContainerOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 r1) {
                this();
            }

            private Builder() {
                super(WorkspaceContainer.DEFAULT_INSTANCE);
            }

            public boolean hasPageIndex() {
                return ((WorkspaceContainer) this.instance).hasPageIndex();
            }

            public int getPageIndex() {
                return ((WorkspaceContainer) this.instance).getPageIndex();
            }

            public Builder setPageIndex(int i) {
                copyOnWrite();
                ((WorkspaceContainer) this.instance).setPageIndex(i);
                return this;
            }

            public Builder clearPageIndex() {
                copyOnWrite();
                ((WorkspaceContainer) this.instance).clearPageIndex();
                return this;
            }

            public boolean hasGridX() {
                return ((WorkspaceContainer) this.instance).hasGridX();
            }

            public int getGridX() {
                return ((WorkspaceContainer) this.instance).getGridX();
            }

            public Builder setGridX(int i) {
                copyOnWrite();
                ((WorkspaceContainer) this.instance).setGridX(i);
                return this;
            }

            public Builder clearGridX() {
                copyOnWrite();
                ((WorkspaceContainer) this.instance).clearGridX();
                return this;
            }

            public boolean hasGridY() {
                return ((WorkspaceContainer) this.instance).hasGridY();
            }

            public int getGridY() {
                return ((WorkspaceContainer) this.instance).getGridY();
            }

            public Builder setGridY(int i) {
                copyOnWrite();
                ((WorkspaceContainer) this.instance).setGridY(i);
                return this;
            }

            public Builder clearGridY() {
                copyOnWrite();
                ((WorkspaceContainer) this.instance).clearGridY();
                return this;
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new WorkspaceContainer();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                    WorkspaceContainer workspaceContainer = (WorkspaceContainer) obj2;
                    this.pageIndex_ = visitor.visitInt(hasPageIndex(), this.pageIndex_, workspaceContainer.hasPageIndex(), workspaceContainer.pageIndex_);
                    this.gridX_ = visitor.visitInt(hasGridX(), this.gridX_, workspaceContainer.hasGridX(), workspaceContainer.gridX_);
                    this.gridY_ = visitor.visitInt(hasGridY(), this.gridY_, workspaceContainer.hasGridY(), workspaceContainer.gridY_);
                    if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                        this.bitField0_ |= workspaceContainer.bitField0_;
                    }
                    return this;
                case 6:
                    CodedInputStream codedInputStream = (CodedInputStream) obj;
                    ExtensionRegistryLite extensionRegistryLite = (ExtensionRegistryLite) obj2;
                    boolean z = false;
                    while (!z) {
                        try {
                            int readTag = codedInputStream.readTag();
                            if (readTag != 0) {
                                if (readTag == 8) {
                                    this.bitField0_ |= 1;
                                    this.pageIndex_ = codedInputStream.readInt32();
                                } else if (readTag == 16) {
                                    this.bitField0_ |= 2;
                                    this.gridX_ = codedInputStream.readInt32();
                                } else if (readTag == 24) {
                                    this.bitField0_ |= 4;
                                    this.gridY_ = codedInputStream.readInt32();
                                } else if (!parseUnknownField(readTag, codedInputStream)) {
                                }
                            }
                            z = true;
                        } catch (InvalidProtocolBufferException e) {
                            throw new RuntimeException(e.setUnfinishedMessage(this));
                        } catch (IOException e2) {
                            throw new RuntimeException(new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this));
                        }
                    }
                    break;
                case 7:
                    break;
                case 8:
                    if (PARSER == null) {
                        synchronized (WorkspaceContainer.class) {
                            if (PARSER == null) {
                                PARSER = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                            }
                        }
                    }
                    return PARSER;
                default:
                    throw new UnsupportedOperationException();
            }
            return DEFAULT_INSTANCE;
        }

        static {
            WorkspaceContainer workspaceContainer = new WorkspaceContainer();
            DEFAULT_INSTANCE = workspaceContainer;
            workspaceContainer.makeImmutable();
        }

        public static WorkspaceContainer getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<WorkspaceContainer> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class HotseatContainer extends GeneratedMessageLite<HotseatContainer, Builder> implements HotseatContainerOrBuilder {
        /* access modifiers changed from: private */
        public static final HotseatContainer DEFAULT_INSTANCE;
        public static final int INDEX_FIELD_NUMBER = 1;
        private static volatile Parser<HotseatContainer> PARSER;
        private int bitField0_;
        private int index_;

        private HotseatContainer() {
        }

        public boolean hasIndex() {
            return (this.bitField0_ & 1) == 1;
        }

        public int getIndex() {
            return this.index_;
        }

        /* access modifiers changed from: private */
        public void setIndex(int i) {
            this.bitField0_ |= 1;
            this.index_ = i;
        }

        /* access modifiers changed from: private */
        public void clearIndex() {
            this.bitField0_ &= -2;
            this.index_ = 0;
        }

        public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
            if ((this.bitField0_ & 1) == 1) {
                codedOutputStream.writeInt32(1, this.index_);
            }
            this.unknownFields.writeTo(codedOutputStream);
        }

        public int getSerializedSize() {
            int i = this.memoizedSerializedSize;
            if (i != -1) {
                return i;
            }
            int i2 = 0;
            if ((this.bitField0_ & 1) == 1) {
                i2 = 0 + CodedOutputStream.computeInt32Size(1, this.index_);
            }
            int serializedSize = i2 + this.unknownFields.getSerializedSize();
            this.memoizedSerializedSize = serializedSize;
            return serializedSize;
        }

        public static HotseatContainer parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (HotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static HotseatContainer parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (HotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static HotseatContainer parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (HotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static HotseatContainer parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (HotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static HotseatContainer parseFrom(InputStream inputStream) throws IOException {
            return (HotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static HotseatContainer parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (HotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static HotseatContainer parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (HotseatContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static HotseatContainer parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (HotseatContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static HotseatContainer parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (HotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static HotseatContainer parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (HotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(HotseatContainer hotseatContainer) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(hotseatContainer);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<HotseatContainer, Builder> implements HotseatContainerOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 r1) {
                this();
            }

            private Builder() {
                super(HotseatContainer.DEFAULT_INSTANCE);
            }

            public boolean hasIndex() {
                return ((HotseatContainer) this.instance).hasIndex();
            }

            public int getIndex() {
                return ((HotseatContainer) this.instance).getIndex();
            }

            public Builder setIndex(int i) {
                copyOnWrite();
                ((HotseatContainer) this.instance).setIndex(i);
                return this;
            }

            public Builder clearIndex() {
                copyOnWrite();
                ((HotseatContainer) this.instance).clearIndex();
                return this;
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new HotseatContainer();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                    HotseatContainer hotseatContainer = (HotseatContainer) obj2;
                    this.index_ = visitor.visitInt(hasIndex(), this.index_, hotseatContainer.hasIndex(), hotseatContainer.index_);
                    if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                        this.bitField0_ |= hotseatContainer.bitField0_;
                    }
                    return this;
                case 6:
                    CodedInputStream codedInputStream = (CodedInputStream) obj;
                    ExtensionRegistryLite extensionRegistryLite = (ExtensionRegistryLite) obj2;
                    boolean z = false;
                    while (!z) {
                        try {
                            int readTag = codedInputStream.readTag();
                            if (readTag != 0) {
                                if (readTag == 8) {
                                    this.bitField0_ |= 1;
                                    this.index_ = codedInputStream.readInt32();
                                } else if (!parseUnknownField(readTag, codedInputStream)) {
                                }
                            }
                            z = true;
                        } catch (InvalidProtocolBufferException e) {
                            throw new RuntimeException(e.setUnfinishedMessage(this));
                        } catch (IOException e2) {
                            throw new RuntimeException(new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this));
                        }
                    }
                    break;
                case 7:
                    break;
                case 8:
                    if (PARSER == null) {
                        synchronized (HotseatContainer.class) {
                            if (PARSER == null) {
                                PARSER = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                            }
                        }
                    }
                    return PARSER;
                default:
                    throw new UnsupportedOperationException();
            }
            return DEFAULT_INSTANCE;
        }

        static {
            HotseatContainer hotseatContainer = new HotseatContainer();
            DEFAULT_INSTANCE = hotseatContainer;
            hotseatContainer.makeImmutable();
        }

        public static HotseatContainer getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<HotseatContainer> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class PredictedHotseatContainer extends GeneratedMessageLite<PredictedHotseatContainer, Builder> implements PredictedHotseatContainerOrBuilder {
        public static final int CARDINALITY_FIELD_NUMBER = 2;
        /* access modifiers changed from: private */
        public static final PredictedHotseatContainer DEFAULT_INSTANCE;
        public static final int INDEX_FIELD_NUMBER = 1;
        private static volatile Parser<PredictedHotseatContainer> PARSER;
        private int bitField0_;
        private int cardinality_;
        private int index_;

        private PredictedHotseatContainer() {
        }

        public boolean hasIndex() {
            return (this.bitField0_ & 1) == 1;
        }

        public int getIndex() {
            return this.index_;
        }

        /* access modifiers changed from: private */
        public void setIndex(int i) {
            this.bitField0_ |= 1;
            this.index_ = i;
        }

        /* access modifiers changed from: private */
        public void clearIndex() {
            this.bitField0_ &= -2;
            this.index_ = 0;
        }

        public boolean hasCardinality() {
            return (this.bitField0_ & 2) == 2;
        }

        public int getCardinality() {
            return this.cardinality_;
        }

        /* access modifiers changed from: private */
        public void setCardinality(int i) {
            this.bitField0_ |= 2;
            this.cardinality_ = i;
        }

        /* access modifiers changed from: private */
        public void clearCardinality() {
            this.bitField0_ &= -3;
            this.cardinality_ = 0;
        }

        public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
            if ((this.bitField0_ & 1) == 1) {
                codedOutputStream.writeInt32(1, this.index_);
            }
            if ((this.bitField0_ & 2) == 2) {
                codedOutputStream.writeInt32(2, this.cardinality_);
            }
            this.unknownFields.writeTo(codedOutputStream);
        }

        public int getSerializedSize() {
            int i = this.memoizedSerializedSize;
            if (i != -1) {
                return i;
            }
            int i2 = 0;
            if ((this.bitField0_ & 1) == 1) {
                i2 = 0 + CodedOutputStream.computeInt32Size(1, this.index_);
            }
            if ((this.bitField0_ & 2) == 2) {
                i2 += CodedOutputStream.computeInt32Size(2, this.cardinality_);
            }
            int serializedSize = i2 + this.unknownFields.getSerializedSize();
            this.memoizedSerializedSize = serializedSize;
            return serializedSize;
        }

        public static PredictedHotseatContainer parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (PredictedHotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static PredictedHotseatContainer parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (PredictedHotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static PredictedHotseatContainer parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (PredictedHotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static PredictedHotseatContainer parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (PredictedHotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static PredictedHotseatContainer parseFrom(InputStream inputStream) throws IOException {
            return (PredictedHotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static PredictedHotseatContainer parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (PredictedHotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static PredictedHotseatContainer parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (PredictedHotseatContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static PredictedHotseatContainer parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (PredictedHotseatContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static PredictedHotseatContainer parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (PredictedHotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static PredictedHotseatContainer parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (PredictedHotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(PredictedHotseatContainer predictedHotseatContainer) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(predictedHotseatContainer);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<PredictedHotseatContainer, Builder> implements PredictedHotseatContainerOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 r1) {
                this();
            }

            private Builder() {
                super(PredictedHotseatContainer.DEFAULT_INSTANCE);
            }

            public boolean hasIndex() {
                return ((PredictedHotseatContainer) this.instance).hasIndex();
            }

            public int getIndex() {
                return ((PredictedHotseatContainer) this.instance).getIndex();
            }

            public Builder setIndex(int i) {
                copyOnWrite();
                ((PredictedHotseatContainer) this.instance).setIndex(i);
                return this;
            }

            public Builder clearIndex() {
                copyOnWrite();
                ((PredictedHotseatContainer) this.instance).clearIndex();
                return this;
            }

            public boolean hasCardinality() {
                return ((PredictedHotseatContainer) this.instance).hasCardinality();
            }

            public int getCardinality() {
                return ((PredictedHotseatContainer) this.instance).getCardinality();
            }

            public Builder setCardinality(int i) {
                copyOnWrite();
                ((PredictedHotseatContainer) this.instance).setCardinality(i);
                return this;
            }

            public Builder clearCardinality() {
                copyOnWrite();
                ((PredictedHotseatContainer) this.instance).clearCardinality();
                return this;
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new PredictedHotseatContainer();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                    PredictedHotseatContainer predictedHotseatContainer = (PredictedHotseatContainer) obj2;
                    this.index_ = visitor.visitInt(hasIndex(), this.index_, predictedHotseatContainer.hasIndex(), predictedHotseatContainer.index_);
                    this.cardinality_ = visitor.visitInt(hasCardinality(), this.cardinality_, predictedHotseatContainer.hasCardinality(), predictedHotseatContainer.cardinality_);
                    if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                        this.bitField0_ |= predictedHotseatContainer.bitField0_;
                    }
                    return this;
                case 6:
                    CodedInputStream codedInputStream = (CodedInputStream) obj;
                    ExtensionRegistryLite extensionRegistryLite = (ExtensionRegistryLite) obj2;
                    boolean z = false;
                    while (!z) {
                        try {
                            int readTag = codedInputStream.readTag();
                            if (readTag != 0) {
                                if (readTag == 8) {
                                    this.bitField0_ |= 1;
                                    this.index_ = codedInputStream.readInt32();
                                } else if (readTag == 16) {
                                    this.bitField0_ |= 2;
                                    this.cardinality_ = codedInputStream.readInt32();
                                } else if (!parseUnknownField(readTag, codedInputStream)) {
                                }
                            }
                            z = true;
                        } catch (InvalidProtocolBufferException e) {
                            throw new RuntimeException(e.setUnfinishedMessage(this));
                        } catch (IOException e2) {
                            throw new RuntimeException(new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this));
                        }
                    }
                    break;
                case 7:
                    break;
                case 8:
                    if (PARSER == null) {
                        synchronized (PredictedHotseatContainer.class) {
                            if (PARSER == null) {
                                PARSER = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                            }
                        }
                    }
                    return PARSER;
                default:
                    throw new UnsupportedOperationException();
            }
            return DEFAULT_INSTANCE;
        }

        static {
            PredictedHotseatContainer predictedHotseatContainer = new PredictedHotseatContainer();
            DEFAULT_INSTANCE = predictedHotseatContainer;
            predictedHotseatContainer.makeImmutable();
        }

        public static PredictedHotseatContainer getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<PredictedHotseatContainer> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class FolderContainer extends GeneratedMessageLite<FolderContainer, Builder> implements FolderContainerOrBuilder {
        /* access modifiers changed from: private */
        public static final FolderContainer DEFAULT_INSTANCE;
        public static final int GRID_X_FIELD_NUMBER = 2;
        public static final int GRID_Y_FIELD_NUMBER = 3;
        public static final int HOTSEAT_FIELD_NUMBER = 5;
        public static final int PAGE_INDEX_FIELD_NUMBER = 1;
        private static volatile Parser<FolderContainer> PARSER = null;
        public static final int TASKBAR_FIELD_NUMBER = 6;
        public static final int WORKSPACE_FIELD_NUMBER = 4;
        private int bitField0_;
        private int gridX_ = -1;
        private int gridY_ = -1;
        private int pageIndex_ = -1;
        private int parentContainerCase_ = 0;
        private Object parentContainer_;

        private FolderContainer() {
        }

        public enum ParentContainerCase implements Internal.EnumLite {
            WORKSPACE(4),
            HOTSEAT(5),
            TASKBAR(6),
            PARENTCONTAINER_NOT_SET(0);
            
            private final int value;

            private ParentContainerCase(int i) {
                this.value = i;
            }

            @Deprecated
            public static ParentContainerCase valueOf(int i) {
                return forNumber(i);
            }

            public static ParentContainerCase forNumber(int i) {
                if (i == 0) {
                    return PARENTCONTAINER_NOT_SET;
                }
                if (i == 4) {
                    return WORKSPACE;
                }
                if (i == 5) {
                    return HOTSEAT;
                }
                if (i != 6) {
                    return null;
                }
                return TASKBAR;
            }

            public int getNumber() {
                return this.value;
            }
        }

        public ParentContainerCase getParentContainerCase() {
            return ParentContainerCase.forNumber(this.parentContainerCase_);
        }

        /* access modifiers changed from: private */
        public void clearParentContainer() {
            this.parentContainerCase_ = 0;
            this.parentContainer_ = null;
        }

        public boolean hasPageIndex() {
            return (this.bitField0_ & 1) == 1;
        }

        public int getPageIndex() {
            return this.pageIndex_;
        }

        /* access modifiers changed from: private */
        public void setPageIndex(int i) {
            this.bitField0_ |= 1;
            this.pageIndex_ = i;
        }

        /* access modifiers changed from: private */
        public void clearPageIndex() {
            this.bitField0_ &= -2;
            this.pageIndex_ = -1;
        }

        public boolean hasGridX() {
            return (this.bitField0_ & 2) == 2;
        }

        public int getGridX() {
            return this.gridX_;
        }

        /* access modifiers changed from: private */
        public void setGridX(int i) {
            this.bitField0_ |= 2;
            this.gridX_ = i;
        }

        /* access modifiers changed from: private */
        public void clearGridX() {
            this.bitField0_ &= -3;
            this.gridX_ = -1;
        }

        public boolean hasGridY() {
            return (this.bitField0_ & 4) == 4;
        }

        public int getGridY() {
            return this.gridY_;
        }

        /* access modifiers changed from: private */
        public void setGridY(int i) {
            this.bitField0_ |= 4;
            this.gridY_ = i;
        }

        /* access modifiers changed from: private */
        public void clearGridY() {
            this.bitField0_ &= -5;
            this.gridY_ = -1;
        }

        public boolean hasWorkspace() {
            return this.parentContainerCase_ == 4;
        }

        public WorkspaceContainer getWorkspace() {
            if (this.parentContainerCase_ == 4) {
                return (WorkspaceContainer) this.parentContainer_;
            }
            return WorkspaceContainer.getDefaultInstance();
        }

        /* access modifiers changed from: private */
        public void setWorkspace(WorkspaceContainer workspaceContainer) {
            Objects.requireNonNull(workspaceContainer);
            this.parentContainer_ = workspaceContainer;
            this.parentContainerCase_ = 4;
        }

        /* access modifiers changed from: private */
        public void setWorkspace(WorkspaceContainer.Builder builder) {
            this.parentContainer_ = builder.build();
            this.parentContainerCase_ = 4;
        }

        /* access modifiers changed from: private */
        public void mergeWorkspace(WorkspaceContainer workspaceContainer) {
            if (this.parentContainerCase_ != 4 || this.parentContainer_ == WorkspaceContainer.getDefaultInstance()) {
                this.parentContainer_ = workspaceContainer;
            } else {
                this.parentContainer_ = ((WorkspaceContainer.Builder) WorkspaceContainer.newBuilder((WorkspaceContainer) this.parentContainer_).mergeFrom(workspaceContainer)).buildPartial();
            }
            this.parentContainerCase_ = 4;
        }

        /* access modifiers changed from: private */
        public void clearWorkspace() {
            if (this.parentContainerCase_ == 4) {
                this.parentContainerCase_ = 0;
                this.parentContainer_ = null;
            }
        }

        public boolean hasHotseat() {
            return this.parentContainerCase_ == 5;
        }

        public HotseatContainer getHotseat() {
            if (this.parentContainerCase_ == 5) {
                return (HotseatContainer) this.parentContainer_;
            }
            return HotseatContainer.getDefaultInstance();
        }

        /* access modifiers changed from: private */
        public void setHotseat(HotseatContainer hotseatContainer) {
            Objects.requireNonNull(hotseatContainer);
            this.parentContainer_ = hotseatContainer;
            this.parentContainerCase_ = 5;
        }

        /* access modifiers changed from: private */
        public void setHotseat(HotseatContainer.Builder builder) {
            this.parentContainer_ = builder.build();
            this.parentContainerCase_ = 5;
        }

        /* access modifiers changed from: private */
        public void mergeHotseat(HotseatContainer hotseatContainer) {
            if (this.parentContainerCase_ != 5 || this.parentContainer_ == HotseatContainer.getDefaultInstance()) {
                this.parentContainer_ = hotseatContainer;
            } else {
                this.parentContainer_ = ((HotseatContainer.Builder) HotseatContainer.newBuilder((HotseatContainer) this.parentContainer_).mergeFrom(hotseatContainer)).buildPartial();
            }
            this.parentContainerCase_ = 5;
        }

        /* access modifiers changed from: private */
        public void clearHotseat() {
            if (this.parentContainerCase_ == 5) {
                this.parentContainerCase_ = 0;
                this.parentContainer_ = null;
            }
        }

        public boolean hasTaskbar() {
            return this.parentContainerCase_ == 6;
        }

        public TaskBarContainer getTaskbar() {
            if (this.parentContainerCase_ == 6) {
                return (TaskBarContainer) this.parentContainer_;
            }
            return TaskBarContainer.getDefaultInstance();
        }

        /* access modifiers changed from: private */
        public void setTaskbar(TaskBarContainer taskBarContainer) {
            Objects.requireNonNull(taskBarContainer);
            this.parentContainer_ = taskBarContainer;
            this.parentContainerCase_ = 6;
        }

        /* access modifiers changed from: private */
        public void setTaskbar(TaskBarContainer.Builder builder) {
            this.parentContainer_ = builder.build();
            this.parentContainerCase_ = 6;
        }

        /* access modifiers changed from: private */
        public void mergeTaskbar(TaskBarContainer taskBarContainer) {
            if (this.parentContainerCase_ != 6 || this.parentContainer_ == TaskBarContainer.getDefaultInstance()) {
                this.parentContainer_ = taskBarContainer;
            } else {
                this.parentContainer_ = ((TaskBarContainer.Builder) TaskBarContainer.newBuilder((TaskBarContainer) this.parentContainer_).mergeFrom(taskBarContainer)).buildPartial();
            }
            this.parentContainerCase_ = 6;
        }

        /* access modifiers changed from: private */
        public void clearTaskbar() {
            if (this.parentContainerCase_ == 6) {
                this.parentContainerCase_ = 0;
                this.parentContainer_ = null;
            }
        }

        public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
            if ((this.bitField0_ & 1) == 1) {
                codedOutputStream.writeInt32(1, this.pageIndex_);
            }
            if ((this.bitField0_ & 2) == 2) {
                codedOutputStream.writeInt32(2, this.gridX_);
            }
            if ((this.bitField0_ & 4) == 4) {
                codedOutputStream.writeInt32(3, this.gridY_);
            }
            if (this.parentContainerCase_ == 4) {
                codedOutputStream.writeMessage(4, (WorkspaceContainer) this.parentContainer_);
            }
            if (this.parentContainerCase_ == 5) {
                codedOutputStream.writeMessage(5, (HotseatContainer) this.parentContainer_);
            }
            if (this.parentContainerCase_ == 6) {
                codedOutputStream.writeMessage(6, (TaskBarContainer) this.parentContainer_);
            }
            this.unknownFields.writeTo(codedOutputStream);
        }

        public int getSerializedSize() {
            int i = this.memoizedSerializedSize;
            if (i != -1) {
                return i;
            }
            int i2 = 0;
            if ((this.bitField0_ & 1) == 1) {
                i2 = 0 + CodedOutputStream.computeInt32Size(1, this.pageIndex_);
            }
            if ((this.bitField0_ & 2) == 2) {
                i2 += CodedOutputStream.computeInt32Size(2, this.gridX_);
            }
            if ((this.bitField0_ & 4) == 4) {
                i2 += CodedOutputStream.computeInt32Size(3, this.gridY_);
            }
            if (this.parentContainerCase_ == 4) {
                i2 += CodedOutputStream.computeMessageSize(4, (WorkspaceContainer) this.parentContainer_);
            }
            if (this.parentContainerCase_ == 5) {
                i2 += CodedOutputStream.computeMessageSize(5, (HotseatContainer) this.parentContainer_);
            }
            if (this.parentContainerCase_ == 6) {
                i2 += CodedOutputStream.computeMessageSize(6, (TaskBarContainer) this.parentContainer_);
            }
            int serializedSize = i2 + this.unknownFields.getSerializedSize();
            this.memoizedSerializedSize = serializedSize;
            return serializedSize;
        }

        public static FolderContainer parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (FolderContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static FolderContainer parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (FolderContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static FolderContainer parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (FolderContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static FolderContainer parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (FolderContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static FolderContainer parseFrom(InputStream inputStream) throws IOException {
            return (FolderContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static FolderContainer parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (FolderContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static FolderContainer parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (FolderContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static FolderContainer parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (FolderContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static FolderContainer parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (FolderContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static FolderContainer parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (FolderContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(FolderContainer folderContainer) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(folderContainer);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<FolderContainer, Builder> implements FolderContainerOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 r1) {
                this();
            }

            private Builder() {
                super(FolderContainer.DEFAULT_INSTANCE);
            }

            public ParentContainerCase getParentContainerCase() {
                return ((FolderContainer) this.instance).getParentContainerCase();
            }

            public Builder clearParentContainer() {
                copyOnWrite();
                ((FolderContainer) this.instance).clearParentContainer();
                return this;
            }

            public boolean hasPageIndex() {
                return ((FolderContainer) this.instance).hasPageIndex();
            }

            public int getPageIndex() {
                return ((FolderContainer) this.instance).getPageIndex();
            }

            public Builder setPageIndex(int i) {
                copyOnWrite();
                ((FolderContainer) this.instance).setPageIndex(i);
                return this;
            }

            public Builder clearPageIndex() {
                copyOnWrite();
                ((FolderContainer) this.instance).clearPageIndex();
                return this;
            }

            public boolean hasGridX() {
                return ((FolderContainer) this.instance).hasGridX();
            }

            public int getGridX() {
                return ((FolderContainer) this.instance).getGridX();
            }

            public Builder setGridX(int i) {
                copyOnWrite();
                ((FolderContainer) this.instance).setGridX(i);
                return this;
            }

            public Builder clearGridX() {
                copyOnWrite();
                ((FolderContainer) this.instance).clearGridX();
                return this;
            }

            public boolean hasGridY() {
                return ((FolderContainer) this.instance).hasGridY();
            }

            public int getGridY() {
                return ((FolderContainer) this.instance).getGridY();
            }

            public Builder setGridY(int i) {
                copyOnWrite();
                ((FolderContainer) this.instance).setGridY(i);
                return this;
            }

            public Builder clearGridY() {
                copyOnWrite();
                ((FolderContainer) this.instance).clearGridY();
                return this;
            }

            public boolean hasWorkspace() {
                return ((FolderContainer) this.instance).hasWorkspace();
            }

            public WorkspaceContainer getWorkspace() {
                return ((FolderContainer) this.instance).getWorkspace();
            }

            public Builder setWorkspace(WorkspaceContainer workspaceContainer) {
                copyOnWrite();
                ((FolderContainer) this.instance).setWorkspace(workspaceContainer);
                return this;
            }

            public Builder setWorkspace(WorkspaceContainer.Builder builder) {
                copyOnWrite();
                ((FolderContainer) this.instance).setWorkspace(builder);
                return this;
            }

            public Builder mergeWorkspace(WorkspaceContainer workspaceContainer) {
                copyOnWrite();
                ((FolderContainer) this.instance).mergeWorkspace(workspaceContainer);
                return this;
            }

            public Builder clearWorkspace() {
                copyOnWrite();
                ((FolderContainer) this.instance).clearWorkspace();
                return this;
            }

            public boolean hasHotseat() {
                return ((FolderContainer) this.instance).hasHotseat();
            }

            public HotseatContainer getHotseat() {
                return ((FolderContainer) this.instance).getHotseat();
            }

            public Builder setHotseat(HotseatContainer hotseatContainer) {
                copyOnWrite();
                ((FolderContainer) this.instance).setHotseat(hotseatContainer);
                return this;
            }

            public Builder setHotseat(HotseatContainer.Builder builder) {
                copyOnWrite();
                ((FolderContainer) this.instance).setHotseat(builder);
                return this;
            }

            public Builder mergeHotseat(HotseatContainer hotseatContainer) {
                copyOnWrite();
                ((FolderContainer) this.instance).mergeHotseat(hotseatContainer);
                return this;
            }

            public Builder clearHotseat() {
                copyOnWrite();
                ((FolderContainer) this.instance).clearHotseat();
                return this;
            }

            public boolean hasTaskbar() {
                return ((FolderContainer) this.instance).hasTaskbar();
            }

            public TaskBarContainer getTaskbar() {
                return ((FolderContainer) this.instance).getTaskbar();
            }

            public Builder setTaskbar(TaskBarContainer taskBarContainer) {
                copyOnWrite();
                ((FolderContainer) this.instance).setTaskbar(taskBarContainer);
                return this;
            }

            public Builder setTaskbar(TaskBarContainer.Builder builder) {
                copyOnWrite();
                ((FolderContainer) this.instance).setTaskbar(builder);
                return this;
            }

            public Builder mergeTaskbar(TaskBarContainer taskBarContainer) {
                copyOnWrite();
                ((FolderContainer) this.instance).mergeTaskbar(taskBarContainer);
                return this;
            }

            public Builder clearTaskbar() {
                copyOnWrite();
                ((FolderContainer) this.instance).clearTaskbar();
                return this;
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            boolean z = false;
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new FolderContainer();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                    FolderContainer folderContainer = (FolderContainer) obj2;
                    this.pageIndex_ = visitor.visitInt(hasPageIndex(), this.pageIndex_, folderContainer.hasPageIndex(), folderContainer.pageIndex_);
                    this.gridX_ = visitor.visitInt(hasGridX(), this.gridX_, folderContainer.hasGridX(), folderContainer.gridX_);
                    this.gridY_ = visitor.visitInt(hasGridY(), this.gridY_, folderContainer.hasGridY(), folderContainer.gridY_);
                    int i = AnonymousClass1.$SwitchMap$com$android$launcher3$logger$LauncherAtom$FolderContainer$ParentContainerCase[folderContainer.getParentContainerCase().ordinal()];
                    if (i == 1) {
                        if (this.parentContainerCase_ == 4) {
                            z = true;
                        }
                        this.parentContainer_ = visitor.visitOneofMessage(z, this.parentContainer_, folderContainer.parentContainer_);
                    } else if (i == 2) {
                        if (this.parentContainerCase_ == 5) {
                            z = true;
                        }
                        this.parentContainer_ = visitor.visitOneofMessage(z, this.parentContainer_, folderContainer.parentContainer_);
                    } else if (i == 3) {
                        if (this.parentContainerCase_ == 6) {
                            z = true;
                        }
                        this.parentContainer_ = visitor.visitOneofMessage(z, this.parentContainer_, folderContainer.parentContainer_);
                    } else if (i == 4) {
                        if (this.parentContainerCase_ != 0) {
                            z = true;
                        }
                        visitor.visitOneofNotSet(z);
                    }
                    if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                        int i2 = folderContainer.parentContainerCase_;
                        if (i2 != 0) {
                            this.parentContainerCase_ = i2;
                        }
                        this.bitField0_ |= folderContainer.bitField0_;
                    }
                    return this;
                case 6:
                    CodedInputStream codedInputStream = (CodedInputStream) obj;
                    ExtensionRegistryLite extensionRegistryLite = (ExtensionRegistryLite) obj2;
                    while (!z) {
                        try {
                            int readTag = codedInputStream.readTag();
                            if (readTag != 0) {
                                if (readTag == 8) {
                                    this.bitField0_ |= 1;
                                    this.pageIndex_ = codedInputStream.readInt32();
                                } else if (readTag == 16) {
                                    this.bitField0_ |= 2;
                                    this.gridX_ = codedInputStream.readInt32();
                                } else if (readTag == 24) {
                                    this.bitField0_ |= 4;
                                    this.gridY_ = codedInputStream.readInt32();
                                } else if (readTag == 34) {
                                    WorkspaceContainer.Builder builder = this.parentContainerCase_ == 4 ? (WorkspaceContainer.Builder) ((WorkspaceContainer) this.parentContainer_).toBuilder() : null;
                                    MessageLite readMessage = codedInputStream.readMessage(WorkspaceContainer.parser(), extensionRegistryLite);
                                    this.parentContainer_ = readMessage;
                                    if (builder != null) {
                                        builder.mergeFrom((WorkspaceContainer) readMessage);
                                        this.parentContainer_ = builder.buildPartial();
                                    }
                                    this.parentContainerCase_ = 4;
                                } else if (readTag == 42) {
                                    HotseatContainer.Builder builder2 = this.parentContainerCase_ == 5 ? (HotseatContainer.Builder) ((HotseatContainer) this.parentContainer_).toBuilder() : null;
                                    MessageLite readMessage2 = codedInputStream.readMessage(HotseatContainer.parser(), extensionRegistryLite);
                                    this.parentContainer_ = readMessage2;
                                    if (builder2 != null) {
                                        builder2.mergeFrom((HotseatContainer) readMessage2);
                                        this.parentContainer_ = builder2.buildPartial();
                                    }
                                    this.parentContainerCase_ = 5;
                                } else if (readTag == 50) {
                                    TaskBarContainer.Builder builder3 = this.parentContainerCase_ == 6 ? (TaskBarContainer.Builder) ((TaskBarContainer) this.parentContainer_).toBuilder() : null;
                                    MessageLite readMessage3 = codedInputStream.readMessage(TaskBarContainer.parser(), extensionRegistryLite);
                                    this.parentContainer_ = readMessage3;
                                    if (builder3 != null) {
                                        builder3.mergeFrom((TaskBarContainer) readMessage3);
                                        this.parentContainer_ = builder3.buildPartial();
                                    }
                                    this.parentContainerCase_ = 6;
                                } else if (!parseUnknownField(readTag, codedInputStream)) {
                                }
                            }
                            z = true;
                        } catch (InvalidProtocolBufferException e) {
                            throw new RuntimeException(e.setUnfinishedMessage(this));
                        } catch (IOException e2) {
                            throw new RuntimeException(new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this));
                        }
                    }
                    break;
                case 7:
                    break;
                case 8:
                    if (PARSER == null) {
                        synchronized (FolderContainer.class) {
                            if (PARSER == null) {
                                PARSER = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                            }
                        }
                    }
                    return PARSER;
                default:
                    throw new UnsupportedOperationException();
            }
            return DEFAULT_INSTANCE;
        }

        static {
            FolderContainer folderContainer = new FolderContainer();
            DEFAULT_INSTANCE = folderContainer;
            folderContainer.makeImmutable();
        }

        public static FolderContainer getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<FolderContainer> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    /* renamed from: com.android.launcher3.logger.LauncherAtom$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$launcher3$logger$LauncherAtom$AllAppsContainer$ParentContainerCase;
        static final /* synthetic */ int[] $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase;
        static final /* synthetic */ int[] $SwitchMap$com$android$launcher3$logger$LauncherAtom$FolderContainer$ParentContainerCase;
        static final /* synthetic */ int[] $SwitchMap$com$android$launcher3$logger$LauncherAtom$ItemInfo$ItemCase;
        static final /* synthetic */ int[] $SwitchMap$com$android$launcher3$logger$LauncherAtom$PredictionContainer$ParentContainerCase;
        static final /* synthetic */ int[] $SwitchMap$com$android$launcher3$logger$LauncherAtom$SearchResultContainer$ParentContainerCase;
        static final /* synthetic */ int[] $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke;

        /* JADX WARNING: Can't wrap try/catch for region: R(88:0|(2:1|2)|3|(2:5|6)|7|(2:9|10)|11|(2:13|14)|15|17|18|19|20|(2:21|22)|23|25|26|27|28|29|31|32|(2:33|34)|35|37|38|39|40|41|42|(2:43|44)|45|47|48|49|(2:51|52)|53|(2:55|56)|57|59|60|61|62|63|64|65|66|67|68|69|70|71|72|(2:73|74)|75|77|78|79|80|81|82|83|84|85|86|87|88|89|90|(2:91|92)|93|95|96|97|98|99|100|101|102|103|104|105|106|107|108|109|110|112) */
        /* JADX WARNING: Can't wrap try/catch for region: R(90:0|(2:1|2)|3|(2:5|6)|7|(2:9|10)|11|(2:13|14)|15|17|18|19|20|(2:21|22)|23|25|26|27|28|29|31|32|(2:33|34)|35|37|38|39|40|41|42|43|44|45|47|48|49|(2:51|52)|53|(2:55|56)|57|59|60|61|62|63|64|65|66|67|68|69|70|71|72|73|74|75|77|78|79|80|81|82|83|84|85|86|87|88|89|90|(2:91|92)|93|95|96|97|98|99|100|101|102|103|104|105|106|107|108|109|110|112) */
        /* JADX WARNING: Can't wrap try/catch for region: R(91:0|(2:1|2)|3|(2:5|6)|7|(2:9|10)|11|13|14|15|17|18|19|20|(2:21|22)|23|25|26|27|28|29|31|32|(2:33|34)|35|37|38|39|40|41|42|43|44|45|47|48|49|(2:51|52)|53|(2:55|56)|57|59|60|61|62|63|64|65|66|67|68|69|70|71|72|73|74|75|77|78|79|80|81|82|83|84|85|86|87|88|89|90|(2:91|92)|93|95|96|97|98|99|100|101|102|103|104|105|106|107|108|109|110|112) */
        /* JADX WARNING: Can't wrap try/catch for region: R(92:0|(2:1|2)|3|(2:5|6)|7|9|10|11|13|14|15|17|18|19|20|(2:21|22)|23|25|26|27|28|29|31|32|(2:33|34)|35|37|38|39|40|41|42|43|44|45|47|48|49|(2:51|52)|53|(2:55|56)|57|59|60|61|62|63|64|65|66|67|68|69|70|71|72|73|74|75|77|78|79|80|81|82|83|84|85|86|87|88|89|90|(2:91|92)|93|95|96|97|98|99|100|101|102|103|104|105|106|107|108|109|110|112) */
        /* JADX WARNING: Can't wrap try/catch for region: R(94:0|(2:1|2)|3|5|6|7|9|10|11|13|14|15|17|18|19|20|(2:21|22)|23|25|26|27|28|29|31|32|(2:33|34)|35|37|38|39|40|41|42|43|44|45|47|48|49|(2:51|52)|53|55|56|57|59|60|61|62|63|64|65|66|67|68|69|70|71|72|73|74|75|77|78|79|80|81|82|83|84|85|86|87|88|89|90|(2:91|92)|93|95|96|97|98|99|100|101|102|103|104|105|106|107|108|109|110|112) */
        /* JADX WARNING: Can't wrap try/catch for region: R(98:0|1|2|3|5|6|7|9|10|11|13|14|15|17|18|19|20|21|22|23|25|26|27|28|29|31|32|(2:33|34)|35|37|38|39|40|41|42|43|44|45|47|48|49|51|52|53|55|56|57|59|60|61|62|63|64|65|66|67|68|69|70|71|72|73|74|75|77|78|79|80|81|82|83|84|85|86|87|88|89|90|91|92|93|95|96|97|98|99|100|101|102|103|104|105|106|107|108|109|110|112) */
        /* JADX WARNING: Can't wrap try/catch for region: R(99:0|1|2|3|5|6|7|9|10|11|13|14|15|17|18|19|20|21|22|23|25|26|27|28|29|31|32|33|34|35|37|38|39|40|41|42|43|44|45|47|48|49|51|52|53|55|56|57|59|60|61|62|63|64|65|66|67|68|69|70|71|72|73|74|75|77|78|79|80|81|82|83|84|85|86|87|88|89|90|91|92|93|95|96|97|98|99|100|101|102|103|104|105|106|107|108|109|110|112) */
        /* JADX WARNING: Code restructure failed: missing block: B:113:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:101:0x01ba */
        /* JADX WARNING: Missing exception handler attribute for start block: B:103:0x01c4 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:105:0x01ce */
        /* JADX WARNING: Missing exception handler attribute for start block: B:107:0x01d8 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:109:0x01e2 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:19:0x0044 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:21:0x004e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:27:0x0069 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:33:0x0084 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:39:0x009f */
        /* JADX WARNING: Missing exception handler attribute for start block: B:41:0x00a9 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:43:0x00b3 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:61:0x00ea */
        /* JADX WARNING: Missing exception handler attribute for start block: B:63:0x00f6 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:65:0x0102 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:67:0x010e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:69:0x011a */
        /* JADX WARNING: Missing exception handler attribute for start block: B:71:0x0126 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:73:0x0132 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:79:0x014f */
        /* JADX WARNING: Missing exception handler attribute for start block: B:81:0x0159 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:83:0x0163 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:85:0x016d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:87:0x0177 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:89:0x0181 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:91:0x018b */
        /* JADX WARNING: Missing exception handler attribute for start block: B:97:0x01a6 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:99:0x01b0 */
        static {
            /*
                com.android.launcher3.logger.LauncherAtom$FolderContainer$ParentContainerCase[] r0 = com.android.launcher3.logger.LauncherAtom.FolderContainer.ParentContainerCase.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$android$launcher3$logger$LauncherAtom$FolderContainer$ParentContainerCase = r0
                r1 = 1
                com.android.launcher3.logger.LauncherAtom$FolderContainer$ParentContainerCase r2 = com.android.launcher3.logger.LauncherAtom.FolderContainer.ParentContainerCase.WORKSPACE     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r2 = r2.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r0[r2] = r1     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                r0 = 2
                int[] r2 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$FolderContainer$ParentContainerCase     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.launcher3.logger.LauncherAtom$FolderContainer$ParentContainerCase r3 = com.android.launcher3.logger.LauncherAtom.FolderContainer.ParentContainerCase.HOTSEAT     // Catch:{ NoSuchFieldError -> 0x001d }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2[r3] = r0     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                r2 = 3
                int[] r3 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$FolderContainer$ParentContainerCase     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.launcher3.logger.LauncherAtom$FolderContainer$ParentContainerCase r4 = com.android.launcher3.logger.LauncherAtom.FolderContainer.ParentContainerCase.TASKBAR     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r4 = r4.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r3[r4] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                r3 = 4
                int[] r4 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$FolderContainer$ParentContainerCase     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.android.launcher3.logger.LauncherAtom$FolderContainer$ParentContainerCase r5 = com.android.launcher3.logger.LauncherAtom.FolderContainer.ParentContainerCase.PARENTCONTAINER_NOT_SET     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r4[r5] = r3     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                com.android.launcher3.logger.LauncherAtom$SearchResultContainer$ParentContainerCase[] r4 = com.android.launcher3.logger.LauncherAtom.SearchResultContainer.ParentContainerCase.values()
                int r4 = r4.length
                int[] r4 = new int[r4]
                $SwitchMap$com$android$launcher3$logger$LauncherAtom$SearchResultContainer$ParentContainerCase = r4
                com.android.launcher3.logger.LauncherAtom$SearchResultContainer$ParentContainerCase r5 = com.android.launcher3.logger.LauncherAtom.SearchResultContainer.ParentContainerCase.WORKSPACE     // Catch:{ NoSuchFieldError -> 0x0044 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0044 }
                r4[r5] = r1     // Catch:{ NoSuchFieldError -> 0x0044 }
            L_0x0044:
                int[] r4 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$SearchResultContainer$ParentContainerCase     // Catch:{ NoSuchFieldError -> 0x004e }
                com.android.launcher3.logger.LauncherAtom$SearchResultContainer$ParentContainerCase r5 = com.android.launcher3.logger.LauncherAtom.SearchResultContainer.ParentContainerCase.ALL_APPS_CONTAINER     // Catch:{ NoSuchFieldError -> 0x004e }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x004e }
                r4[r5] = r0     // Catch:{ NoSuchFieldError -> 0x004e }
            L_0x004e:
                int[] r4 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$SearchResultContainer$ParentContainerCase     // Catch:{ NoSuchFieldError -> 0x0058 }
                com.android.launcher3.logger.LauncherAtom$SearchResultContainer$ParentContainerCase r5 = com.android.launcher3.logger.LauncherAtom.SearchResultContainer.ParentContainerCase.PARENTCONTAINER_NOT_SET     // Catch:{ NoSuchFieldError -> 0x0058 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0058 }
                r4[r5] = r2     // Catch:{ NoSuchFieldError -> 0x0058 }
            L_0x0058:
                com.android.launcher3.logger.LauncherAtom$PredictionContainer$ParentContainerCase[] r4 = com.android.launcher3.logger.LauncherAtom.PredictionContainer.ParentContainerCase.values()
                int r4 = r4.length
                int[] r4 = new int[r4]
                $SwitchMap$com$android$launcher3$logger$LauncherAtom$PredictionContainer$ParentContainerCase = r4
                com.android.launcher3.logger.LauncherAtom$PredictionContainer$ParentContainerCase r5 = com.android.launcher3.logger.LauncherAtom.PredictionContainer.ParentContainerCase.TASKBAR_CONTAINER     // Catch:{ NoSuchFieldError -> 0x0069 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0069 }
                r4[r5] = r1     // Catch:{ NoSuchFieldError -> 0x0069 }
            L_0x0069:
                int[] r4 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$PredictionContainer$ParentContainerCase     // Catch:{ NoSuchFieldError -> 0x0073 }
                com.android.launcher3.logger.LauncherAtom$PredictionContainer$ParentContainerCase r5 = com.android.launcher3.logger.LauncherAtom.PredictionContainer.ParentContainerCase.PARENTCONTAINER_NOT_SET     // Catch:{ NoSuchFieldError -> 0x0073 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0073 }
                r4[r5] = r0     // Catch:{ NoSuchFieldError -> 0x0073 }
            L_0x0073:
                com.android.launcher3.logger.LauncherAtom$AllAppsContainer$ParentContainerCase[] r4 = com.android.launcher3.logger.LauncherAtom.AllAppsContainer.ParentContainerCase.values()
                int r4 = r4.length
                int[] r4 = new int[r4]
                $SwitchMap$com$android$launcher3$logger$LauncherAtom$AllAppsContainer$ParentContainerCase = r4
                com.android.launcher3.logger.LauncherAtom$AllAppsContainer$ParentContainerCase r5 = com.android.launcher3.logger.LauncherAtom.AllAppsContainer.ParentContainerCase.TASKBAR_CONTAINER     // Catch:{ NoSuchFieldError -> 0x0084 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0084 }
                r4[r5] = r1     // Catch:{ NoSuchFieldError -> 0x0084 }
            L_0x0084:
                int[] r4 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$AllAppsContainer$ParentContainerCase     // Catch:{ NoSuchFieldError -> 0x008e }
                com.android.launcher3.logger.LauncherAtom$AllAppsContainer$ParentContainerCase r5 = com.android.launcher3.logger.LauncherAtom.AllAppsContainer.ParentContainerCase.PARENTCONTAINER_NOT_SET     // Catch:{ NoSuchFieldError -> 0x008e }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x008e }
                r4[r5] = r0     // Catch:{ NoSuchFieldError -> 0x008e }
            L_0x008e:
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase[] r4 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.values()
                int r4 = r4.length
                int[] r4 = new int[r4]
                $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase = r4
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r5 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.WORKSPACE     // Catch:{ NoSuchFieldError -> 0x009f }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x009f }
                r4[r5] = r1     // Catch:{ NoSuchFieldError -> 0x009f }
            L_0x009f:
                int[] r4 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase     // Catch:{ NoSuchFieldError -> 0x00a9 }
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r5 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.HOTSEAT     // Catch:{ NoSuchFieldError -> 0x00a9 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x00a9 }
                r4[r5] = r0     // Catch:{ NoSuchFieldError -> 0x00a9 }
            L_0x00a9:
                int[] r4 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase     // Catch:{ NoSuchFieldError -> 0x00b3 }
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r5 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.FOLDER     // Catch:{ NoSuchFieldError -> 0x00b3 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x00b3 }
                r4[r5] = r2     // Catch:{ NoSuchFieldError -> 0x00b3 }
            L_0x00b3:
                int[] r4 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase     // Catch:{ NoSuchFieldError -> 0x00bd }
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r5 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.ALL_APPS_CONTAINER     // Catch:{ NoSuchFieldError -> 0x00bd }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x00bd }
                r4[r5] = r3     // Catch:{ NoSuchFieldError -> 0x00bd }
            L_0x00bd:
                r4 = 5
                int[] r5 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase     // Catch:{ NoSuchFieldError -> 0x00c8 }
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r6 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.WIDGETS_CONTAINER     // Catch:{ NoSuchFieldError -> 0x00c8 }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x00c8 }
                r5[r6] = r4     // Catch:{ NoSuchFieldError -> 0x00c8 }
            L_0x00c8:
                r5 = 6
                int[] r6 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase     // Catch:{ NoSuchFieldError -> 0x00d3 }
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r7 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.PREDICTION_CONTAINER     // Catch:{ NoSuchFieldError -> 0x00d3 }
                int r7 = r7.ordinal()     // Catch:{ NoSuchFieldError -> 0x00d3 }
                r6[r7] = r5     // Catch:{ NoSuchFieldError -> 0x00d3 }
            L_0x00d3:
                r6 = 7
                int[] r7 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase     // Catch:{ NoSuchFieldError -> 0x00de }
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r8 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.SEARCH_RESULT_CONTAINER     // Catch:{ NoSuchFieldError -> 0x00de }
                int r8 = r8.ordinal()     // Catch:{ NoSuchFieldError -> 0x00de }
                r7[r8] = r6     // Catch:{ NoSuchFieldError -> 0x00de }
            L_0x00de:
                r7 = 8
                int[] r8 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase     // Catch:{ NoSuchFieldError -> 0x00ea }
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r9 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.SHORTCUTS_CONTAINER     // Catch:{ NoSuchFieldError -> 0x00ea }
                int r9 = r9.ordinal()     // Catch:{ NoSuchFieldError -> 0x00ea }
                r8[r9] = r7     // Catch:{ NoSuchFieldError -> 0x00ea }
            L_0x00ea:
                int[] r8 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase     // Catch:{ NoSuchFieldError -> 0x00f6 }
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r9 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.SETTINGS_CONTAINER     // Catch:{ NoSuchFieldError -> 0x00f6 }
                int r9 = r9.ordinal()     // Catch:{ NoSuchFieldError -> 0x00f6 }
                r10 = 9
                r8[r9] = r10     // Catch:{ NoSuchFieldError -> 0x00f6 }
            L_0x00f6:
                int[] r8 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase     // Catch:{ NoSuchFieldError -> 0x0102 }
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r9 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.PREDICTED_HOTSEAT_CONTAINER     // Catch:{ NoSuchFieldError -> 0x0102 }
                int r9 = r9.ordinal()     // Catch:{ NoSuchFieldError -> 0x0102 }
                r10 = 10
                r8[r9] = r10     // Catch:{ NoSuchFieldError -> 0x0102 }
            L_0x0102:
                int[] r8 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase     // Catch:{ NoSuchFieldError -> 0x010e }
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r9 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.TASK_SWITCHER_CONTAINER     // Catch:{ NoSuchFieldError -> 0x010e }
                int r9 = r9.ordinal()     // Catch:{ NoSuchFieldError -> 0x010e }
                r10 = 11
                r8[r9] = r10     // Catch:{ NoSuchFieldError -> 0x010e }
            L_0x010e:
                int[] r8 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase     // Catch:{ NoSuchFieldError -> 0x011a }
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r9 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.TASK_BAR_CONTAINER     // Catch:{ NoSuchFieldError -> 0x011a }
                int r9 = r9.ordinal()     // Catch:{ NoSuchFieldError -> 0x011a }
                r10 = 12
                r8[r9] = r10     // Catch:{ NoSuchFieldError -> 0x011a }
            L_0x011a:
                int[] r8 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase     // Catch:{ NoSuchFieldError -> 0x0126 }
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r9 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.WALLPAPERS_CONTAINER     // Catch:{ NoSuchFieldError -> 0x0126 }
                int r9 = r9.ordinal()     // Catch:{ NoSuchFieldError -> 0x0126 }
                r10 = 13
                r8[r9] = r10     // Catch:{ NoSuchFieldError -> 0x0126 }
            L_0x0126:
                int[] r8 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase     // Catch:{ NoSuchFieldError -> 0x0132 }
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r9 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.EXTENDED_CONTAINERS     // Catch:{ NoSuchFieldError -> 0x0132 }
                int r9 = r9.ordinal()     // Catch:{ NoSuchFieldError -> 0x0132 }
                r10 = 14
                r8[r9] = r10     // Catch:{ NoSuchFieldError -> 0x0132 }
            L_0x0132:
                int[] r8 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase     // Catch:{ NoSuchFieldError -> 0x013e }
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r9 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.CONTAINER_NOT_SET     // Catch:{ NoSuchFieldError -> 0x013e }
                int r9 = r9.ordinal()     // Catch:{ NoSuchFieldError -> 0x013e }
                r10 = 15
                r8[r9] = r10     // Catch:{ NoSuchFieldError -> 0x013e }
            L_0x013e:
                com.google.protobuf.GeneratedMessageLite$MethodToInvoke[] r8 = com.google.protobuf.GeneratedMessageLite.MethodToInvoke.values()
                int r8 = r8.length
                int[] r8 = new int[r8]
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke = r8
                com.google.protobuf.GeneratedMessageLite$MethodToInvoke r9 = com.google.protobuf.GeneratedMessageLite.MethodToInvoke.NEW_MUTABLE_INSTANCE     // Catch:{ NoSuchFieldError -> 0x014f }
                int r9 = r9.ordinal()     // Catch:{ NoSuchFieldError -> 0x014f }
                r8[r9] = r1     // Catch:{ NoSuchFieldError -> 0x014f }
            L_0x014f:
                int[] r8 = $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke     // Catch:{ NoSuchFieldError -> 0x0159 }
                com.google.protobuf.GeneratedMessageLite$MethodToInvoke r9 = com.google.protobuf.GeneratedMessageLite.MethodToInvoke.IS_INITIALIZED     // Catch:{ NoSuchFieldError -> 0x0159 }
                int r9 = r9.ordinal()     // Catch:{ NoSuchFieldError -> 0x0159 }
                r8[r9] = r0     // Catch:{ NoSuchFieldError -> 0x0159 }
            L_0x0159:
                int[] r8 = $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke     // Catch:{ NoSuchFieldError -> 0x0163 }
                com.google.protobuf.GeneratedMessageLite$MethodToInvoke r9 = com.google.protobuf.GeneratedMessageLite.MethodToInvoke.MAKE_IMMUTABLE     // Catch:{ NoSuchFieldError -> 0x0163 }
                int r9 = r9.ordinal()     // Catch:{ NoSuchFieldError -> 0x0163 }
                r8[r9] = r2     // Catch:{ NoSuchFieldError -> 0x0163 }
            L_0x0163:
                int[] r8 = $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke     // Catch:{ NoSuchFieldError -> 0x016d }
                com.google.protobuf.GeneratedMessageLite$MethodToInvoke r9 = com.google.protobuf.GeneratedMessageLite.MethodToInvoke.NEW_BUILDER     // Catch:{ NoSuchFieldError -> 0x016d }
                int r9 = r9.ordinal()     // Catch:{ NoSuchFieldError -> 0x016d }
                r8[r9] = r3     // Catch:{ NoSuchFieldError -> 0x016d }
            L_0x016d:
                int[] r8 = $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke     // Catch:{ NoSuchFieldError -> 0x0177 }
                com.google.protobuf.GeneratedMessageLite$MethodToInvoke r9 = com.google.protobuf.GeneratedMessageLite.MethodToInvoke.VISIT     // Catch:{ NoSuchFieldError -> 0x0177 }
                int r9 = r9.ordinal()     // Catch:{ NoSuchFieldError -> 0x0177 }
                r8[r9] = r4     // Catch:{ NoSuchFieldError -> 0x0177 }
            L_0x0177:
                int[] r8 = $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke     // Catch:{ NoSuchFieldError -> 0x0181 }
                com.google.protobuf.GeneratedMessageLite$MethodToInvoke r9 = com.google.protobuf.GeneratedMessageLite.MethodToInvoke.MERGE_FROM_STREAM     // Catch:{ NoSuchFieldError -> 0x0181 }
                int r9 = r9.ordinal()     // Catch:{ NoSuchFieldError -> 0x0181 }
                r8[r9] = r5     // Catch:{ NoSuchFieldError -> 0x0181 }
            L_0x0181:
                int[] r8 = $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke     // Catch:{ NoSuchFieldError -> 0x018b }
                com.google.protobuf.GeneratedMessageLite$MethodToInvoke r9 = com.google.protobuf.GeneratedMessageLite.MethodToInvoke.GET_DEFAULT_INSTANCE     // Catch:{ NoSuchFieldError -> 0x018b }
                int r9 = r9.ordinal()     // Catch:{ NoSuchFieldError -> 0x018b }
                r8[r9] = r6     // Catch:{ NoSuchFieldError -> 0x018b }
            L_0x018b:
                int[] r8 = $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke     // Catch:{ NoSuchFieldError -> 0x0195 }
                com.google.protobuf.GeneratedMessageLite$MethodToInvoke r9 = com.google.protobuf.GeneratedMessageLite.MethodToInvoke.GET_PARSER     // Catch:{ NoSuchFieldError -> 0x0195 }
                int r9 = r9.ordinal()     // Catch:{ NoSuchFieldError -> 0x0195 }
                r8[r9] = r7     // Catch:{ NoSuchFieldError -> 0x0195 }
            L_0x0195:
                com.android.launcher3.logger.LauncherAtom$ItemInfo$ItemCase[] r8 = com.android.launcher3.logger.LauncherAtom.ItemInfo.ItemCase.values()
                int r8 = r8.length
                int[] r8 = new int[r8]
                $SwitchMap$com$android$launcher3$logger$LauncherAtom$ItemInfo$ItemCase = r8
                com.android.launcher3.logger.LauncherAtom$ItemInfo$ItemCase r9 = com.android.launcher3.logger.LauncherAtom.ItemInfo.ItemCase.APPLICATION     // Catch:{ NoSuchFieldError -> 0x01a6 }
                int r9 = r9.ordinal()     // Catch:{ NoSuchFieldError -> 0x01a6 }
                r8[r9] = r1     // Catch:{ NoSuchFieldError -> 0x01a6 }
            L_0x01a6:
                int[] r1 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ItemInfo$ItemCase     // Catch:{ NoSuchFieldError -> 0x01b0 }
                com.android.launcher3.logger.LauncherAtom$ItemInfo$ItemCase r8 = com.android.launcher3.logger.LauncherAtom.ItemInfo.ItemCase.TASK     // Catch:{ NoSuchFieldError -> 0x01b0 }
                int r8 = r8.ordinal()     // Catch:{ NoSuchFieldError -> 0x01b0 }
                r1[r8] = r0     // Catch:{ NoSuchFieldError -> 0x01b0 }
            L_0x01b0:
                int[] r0 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ItemInfo$ItemCase     // Catch:{ NoSuchFieldError -> 0x01ba }
                com.android.launcher3.logger.LauncherAtom$ItemInfo$ItemCase r1 = com.android.launcher3.logger.LauncherAtom.ItemInfo.ItemCase.SHORTCUT     // Catch:{ NoSuchFieldError -> 0x01ba }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x01ba }
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x01ba }
            L_0x01ba:
                int[] r0 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ItemInfo$ItemCase     // Catch:{ NoSuchFieldError -> 0x01c4 }
                com.android.launcher3.logger.LauncherAtom$ItemInfo$ItemCase r1 = com.android.launcher3.logger.LauncherAtom.ItemInfo.ItemCase.WIDGET     // Catch:{ NoSuchFieldError -> 0x01c4 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x01c4 }
                r0[r1] = r3     // Catch:{ NoSuchFieldError -> 0x01c4 }
            L_0x01c4:
                int[] r0 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ItemInfo$ItemCase     // Catch:{ NoSuchFieldError -> 0x01ce }
                com.android.launcher3.logger.LauncherAtom$ItemInfo$ItemCase r1 = com.android.launcher3.logger.LauncherAtom.ItemInfo.ItemCase.FOLDER_ICON     // Catch:{ NoSuchFieldError -> 0x01ce }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x01ce }
                r0[r1] = r4     // Catch:{ NoSuchFieldError -> 0x01ce }
            L_0x01ce:
                int[] r0 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ItemInfo$ItemCase     // Catch:{ NoSuchFieldError -> 0x01d8 }
                com.android.launcher3.logger.LauncherAtom$ItemInfo$ItemCase r1 = com.android.launcher3.logger.LauncherAtom.ItemInfo.ItemCase.SLICE     // Catch:{ NoSuchFieldError -> 0x01d8 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x01d8 }
                r0[r1] = r5     // Catch:{ NoSuchFieldError -> 0x01d8 }
            L_0x01d8:
                int[] r0 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ItemInfo$ItemCase     // Catch:{ NoSuchFieldError -> 0x01e2 }
                com.android.launcher3.logger.LauncherAtom$ItemInfo$ItemCase r1 = com.android.launcher3.logger.LauncherAtom.ItemInfo.ItemCase.SEARCH_ACTION_ITEM     // Catch:{ NoSuchFieldError -> 0x01e2 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x01e2 }
                r0[r1] = r6     // Catch:{ NoSuchFieldError -> 0x01e2 }
            L_0x01e2:
                int[] r0 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ItemInfo$ItemCase     // Catch:{ NoSuchFieldError -> 0x01ec }
                com.android.launcher3.logger.LauncherAtom$ItemInfo$ItemCase r1 = com.android.launcher3.logger.LauncherAtom.ItemInfo.ItemCase.ITEM_NOT_SET     // Catch:{ NoSuchFieldError -> 0x01ec }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x01ec }
                r0[r1] = r7     // Catch:{ NoSuchFieldError -> 0x01ec }
            L_0x01ec:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.logger.LauncherAtom.AnonymousClass1.<clinit>():void");
        }
    }

    public static final class WallpapersContainer extends GeneratedMessageLite<WallpapersContainer, Builder> implements WallpapersContainerOrBuilder {
        public static final int CARDINALITY_FIELD_NUMBER = 1;
        /* access modifiers changed from: private */
        public static final WallpapersContainer DEFAULT_INSTANCE;
        private static volatile Parser<WallpapersContainer> PARSER;
        private int bitField0_;
        private int cardinality_;

        private WallpapersContainer() {
        }

        public boolean hasCardinality() {
            return (this.bitField0_ & 1) == 1;
        }

        public int getCardinality() {
            return this.cardinality_;
        }

        /* access modifiers changed from: private */
        public void setCardinality(int i) {
            this.bitField0_ |= 1;
            this.cardinality_ = i;
        }

        /* access modifiers changed from: private */
        public void clearCardinality() {
            this.bitField0_ &= -2;
            this.cardinality_ = 0;
        }

        public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
            if ((this.bitField0_ & 1) == 1) {
                codedOutputStream.writeInt32(1, this.cardinality_);
            }
            this.unknownFields.writeTo(codedOutputStream);
        }

        public int getSerializedSize() {
            int i = this.memoizedSerializedSize;
            if (i != -1) {
                return i;
            }
            int i2 = 0;
            if ((this.bitField0_ & 1) == 1) {
                i2 = 0 + CodedOutputStream.computeInt32Size(1, this.cardinality_);
            }
            int serializedSize = i2 + this.unknownFields.getSerializedSize();
            this.memoizedSerializedSize = serializedSize;
            return serializedSize;
        }

        public static WallpapersContainer parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (WallpapersContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static WallpapersContainer parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (WallpapersContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static WallpapersContainer parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (WallpapersContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static WallpapersContainer parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (WallpapersContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static WallpapersContainer parseFrom(InputStream inputStream) throws IOException {
            return (WallpapersContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static WallpapersContainer parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (WallpapersContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static WallpapersContainer parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (WallpapersContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static WallpapersContainer parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (WallpapersContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static WallpapersContainer parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (WallpapersContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static WallpapersContainer parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (WallpapersContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(WallpapersContainer wallpapersContainer) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(wallpapersContainer);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<WallpapersContainer, Builder> implements WallpapersContainerOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 r1) {
                this();
            }

            private Builder() {
                super(WallpapersContainer.DEFAULT_INSTANCE);
            }

            public boolean hasCardinality() {
                return ((WallpapersContainer) this.instance).hasCardinality();
            }

            public int getCardinality() {
                return ((WallpapersContainer) this.instance).getCardinality();
            }

            public Builder setCardinality(int i) {
                copyOnWrite();
                ((WallpapersContainer) this.instance).setCardinality(i);
                return this;
            }

            public Builder clearCardinality() {
                copyOnWrite();
                ((WallpapersContainer) this.instance).clearCardinality();
                return this;
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new WallpapersContainer();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                    WallpapersContainer wallpapersContainer = (WallpapersContainer) obj2;
                    this.cardinality_ = visitor.visitInt(hasCardinality(), this.cardinality_, wallpapersContainer.hasCardinality(), wallpapersContainer.cardinality_);
                    if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                        this.bitField0_ |= wallpapersContainer.bitField0_;
                    }
                    return this;
                case 6:
                    CodedInputStream codedInputStream = (CodedInputStream) obj;
                    ExtensionRegistryLite extensionRegistryLite = (ExtensionRegistryLite) obj2;
                    boolean z = false;
                    while (!z) {
                        try {
                            int readTag = codedInputStream.readTag();
                            if (readTag != 0) {
                                if (readTag == 8) {
                                    this.bitField0_ |= 1;
                                    this.cardinality_ = codedInputStream.readInt32();
                                } else if (!parseUnknownField(readTag, codedInputStream)) {
                                }
                            }
                            z = true;
                        } catch (InvalidProtocolBufferException e) {
                            throw new RuntimeException(e.setUnfinishedMessage(this));
                        } catch (IOException e2) {
                            throw new RuntimeException(new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this));
                        }
                    }
                    break;
                case 7:
                    break;
                case 8:
                    if (PARSER == null) {
                        synchronized (WallpapersContainer.class) {
                            if (PARSER == null) {
                                PARSER = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                            }
                        }
                    }
                    return PARSER;
                default:
                    throw new UnsupportedOperationException();
            }
            return DEFAULT_INSTANCE;
        }

        static {
            WallpapersContainer wallpapersContainer = new WallpapersContainer();
            DEFAULT_INSTANCE = wallpapersContainer;
            wallpapersContainer.makeImmutable();
        }

        public static WallpapersContainer getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<WallpapersContainer> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }
}
