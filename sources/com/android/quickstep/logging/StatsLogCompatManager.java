package com.android.quickstep.logging;

import android.content.Context;
import android.util.Log;
import android.util.StatsEvent;
import android.view.View;
import androidx.core.util.Preconditions;
import androidx.slice.SliceItem;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.Utilities;
import com.android.launcher3.logger.LauncherAtom;
import com.android.launcher3.logger.LauncherAtomExtensions;
import com.android.launcher3.logging.InstanceId;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.AllAppsList;
import com.android.launcher3.model.BaseModelUpdateTask;
import com.android.launcher3.model.BgDataModel;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.util.Executors;
import com.android.launcher3.views.ActivityContext;
import com.android.quickstep.logging.StatsLogCompatManager;
import com.android.systemui.shared.system.InteractionJankMonitorWrapper;
import com.android.systemui.shared.system.SysUiStatsLog;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.stream.Stream;

public class StatsLogCompatManager extends StatsLogManager {
    private static final int ALL_APPS_HIERARCHY_OFFSET = 400;
    /* access modifiers changed from: private */
    public static final InstanceId DEFAULT_INSTANCE_ID = InstanceId.fakeInstanceId(0);
    private static final int DEFAULT_PAGE_INDEX = -2;
    private static final int EXTENDED_CONTAINERS_HIERARCHY_OFFSET = 300;
    private static final int FOLDER_HIERARCHY_OFFSET = 100;
    /* access modifiers changed from: private */
    public static final boolean IS_VERBOSE = Utilities.isPropertyEnabled("StatsLog");
    private static final String LATENCY_TAG = "StatsLatencyLog";
    public static final CopyOnWriteArrayList<StatsLogConsumer> LOGS_CONSUMER = new CopyOnWriteArrayList<>();
    private static final int SEARCH_ATTRIBUTES_CORRECTED_QUERY = 1;
    private static final int SEARCH_ATTRIBUTES_DIRECT_MATCH = 2;
    private static final int SEARCH_ATTRIBUTES_ENTRY_STATE_ALL_APPS = 4;
    private static final int SEARCH_ATTRIBUTES_ENTRY_STATE_QSB = 8;
    private static final int SEARCH_RESULT_HIERARCHY_OFFSET = 200;
    private static final String TAG = "StatsLog";
    private final Context mContext;

    public interface StatsLogConsumer {
        void consume(StatsLogManager.EventEnum eventEnum, LauncherAtom.ItemInfo itemInfo);
    }

    /* access modifiers changed from: private */
    public static String getStateString(int i) {
        return i != 1 ? i != 2 ? i != 3 ? i != 4 ? "INVALID" : "ALLAPPS" : "OVERVIEW" : "HOME" : "BACKGROUND";
    }

    public StatsLogCompatManager(Context context) {
        this.mContext = context;
    }

    /* access modifiers changed from: protected */
    public StatsLogManager.StatsLogger createLogger() {
        return new StatsCompatLogger(this.mContext, this.mActivityContext);
    }

    /* access modifiers changed from: protected */
    public StatsLogManager.StatsLatencyLogger createLatencyLogger() {
        return new StatsCompatLatencyLogger(this.mContext, this.mActivityContext);
    }

    public static void writeSnapshot(LauncherAtom.ItemInfo itemInfo, InstanceId instanceId) {
        LauncherAtom.ItemInfo itemInfo2 = itemInfo;
        if (IS_VERBOSE) {
            Log.d("StatsLog", String.format("\nwriteSnapshot(%d):\n%s", new Object[]{Integer.valueOf(instanceId.getId()), itemInfo2}));
        }
        if (Utilities.ATLEAST_R && !Utilities.IS_RUNNING_IN_TEST_HARNESS) {
            SysUiStatsLog.write((int) SysUiStatsLog.LAUNCHER_SNAPSHOT, StatsLogManager.LauncherEvent.LAUNCHER_WORKSPACE_SNAPSHOT.getId(), itemInfo.getItemCase().getNumber(), instanceId.getId(), 0, getPackageName(itemInfo), getComponentName(itemInfo), getGridX(itemInfo2, false), getGridY(itemInfo2, false), getPageId(itemInfo), getGridX(itemInfo2, true), getGridY(itemInfo2, true), getParentPageId(itemInfo), getHierarchy(itemInfo), itemInfo.getIsWork(), 0, getCardinality(itemInfo), itemInfo.getWidget().getSpanX(), itemInfo.getWidget().getSpanY(), getFeatures(itemInfo), getAttributes(itemInfo));
        }
    }

    /* access modifiers changed from: private */
    public static byte[] getAttributes(LauncherAtom.ItemInfo itemInfo) {
        LauncherAtom.LauncherAttributes.Builder newBuilder = LauncherAtom.LauncherAttributes.newBuilder();
        Stream map = itemInfo.getItemAttributesList().stream().map($$Lambda$Ms2fdWgcA9soA8_4ucPpkK1QMTM.INSTANCE);
        Objects.requireNonNull(newBuilder);
        map.forEach(new Consumer() {
            public final void accept(Object obj) {
                LauncherAtom.LauncherAttributes.Builder.this.addItemAttributes(((Integer) obj).intValue());
            }
        });
        return ((LauncherAtom.LauncherAttributes) newBuilder.build()).toByteArray();
    }

    public static StatsEvent buildStatsEvent(LauncherAtom.ItemInfo itemInfo, InstanceId instanceId) {
        int i;
        LauncherAtom.ItemInfo itemInfo2 = itemInfo;
        int id = StatsLogManager.LauncherEvent.LAUNCHER_WORKSPACE_SNAPSHOT.getId();
        int number = itemInfo.getItemCase().getNumber();
        if (instanceId == null) {
            i = 0;
        } else {
            i = instanceId.getId();
        }
        return SysUiStatsLog.buildStatsEvent(SysUiStatsLog.LAUNCHER_LAYOUT_SNAPSHOT, id, number, i, 0, getPackageName(itemInfo), getComponentName(itemInfo), getGridX(itemInfo2, false), getGridY(itemInfo2, false), getPageId(itemInfo), getGridX(itemInfo2, true), getGridY(itemInfo2, true), getParentPageId(itemInfo), getHierarchy(itemInfo), itemInfo.getIsWork(), 0, getCardinality(itemInfo), itemInfo.getWidget().getSpanX(), itemInfo.getWidget().getSpanY(), getAttributes(itemInfo));
    }

    private static class StatsCompatLogger implements StatsLogManager.StatsLogger {
        private static final ItemInfo DEFAULT_ITEM_INFO;
        private final Optional<ActivityContext> mActivityContext;
        private Optional<LauncherAtom.ContainerInfo> mContainerInfo = Optional.empty();
        private final Context mContext;
        private int mDstState = 0;
        private Optional<String> mEditText = Optional.empty();
        private Optional<LauncherAtom.FromState> mFromState = Optional.empty();
        private InstanceId mInstanceId = StatsLogCompatManager.DEFAULT_INSTANCE_ID;
        /* access modifiers changed from: private */
        public ItemInfo mItemInfo = DEFAULT_ITEM_INFO;
        private OptionalInt mRank = OptionalInt.empty();
        private LauncherAtom.Slice mSlice;
        private SliceItem mSliceItem;
        private int mSrcState = 0;
        private Optional<LauncherAtom.ToState> mToState = Optional.empty();

        static {
            ItemInfo itemInfo = new ItemInfo();
            DEFAULT_ITEM_INFO = itemInfo;
            itemInfo.itemType = -1;
        }

        StatsCompatLogger(Context context, ActivityContext activityContext) {
            this.mContext = context;
            this.mActivityContext = Optional.ofNullable(activityContext);
        }

        public StatsLogManager.StatsLogger withItemInfo(ItemInfo itemInfo) {
            if (!this.mContainerInfo.isPresent()) {
                this.mItemInfo = itemInfo;
                return this;
            }
            throw new IllegalArgumentException("ItemInfo and ContainerInfo are mutual exclusive; cannot log both.");
        }

        public StatsLogManager.StatsLogger withInstanceId(InstanceId instanceId) {
            this.mInstanceId = instanceId;
            return this;
        }

        public StatsLogManager.StatsLogger withRank(int i) {
            this.mRank = OptionalInt.of(i);
            return this;
        }

        public StatsLogManager.StatsLogger withSrcState(int i) {
            this.mSrcState = i;
            return this;
        }

        public StatsLogManager.StatsLogger withDstState(int i) {
            this.mDstState = i;
            return this;
        }

        public StatsLogManager.StatsLogger withContainerInfo(LauncherAtom.ContainerInfo containerInfo) {
            Preconditions.checkState(this.mItemInfo == DEFAULT_ITEM_INFO, "ItemInfo and ContainerInfo are mutual exclusive; cannot log both.");
            this.mContainerInfo = Optional.of(containerInfo);
            return this;
        }

        public StatsLogManager.StatsLogger withFromState(LauncherAtom.FromState fromState) {
            this.mFromState = Optional.of(fromState);
            return this;
        }

        public StatsLogManager.StatsLogger withToState(LauncherAtom.ToState toState) {
            this.mToState = Optional.of(toState);
            return this;
        }

        public StatsLogManager.StatsLogger withEditText(String str) {
            this.mEditText = Optional.of(str);
            return this;
        }

        public StatsLogManager.StatsLogger withSliceItem(SliceItem sliceItem) {
            Preconditions.checkState(this.mItemInfo == DEFAULT_ITEM_INFO && this.mSlice == null, "ItemInfo, Slice and SliceItem are mutual exclusive; cannot set more than one of them.");
            this.mSliceItem = (SliceItem) Preconditions.checkNotNull(sliceItem, "expected valid sliceItem but received null");
            return this;
        }

        public StatsLogManager.StatsLogger withSlice(LauncherAtom.Slice slice) {
            Preconditions.checkState(this.mItemInfo == DEFAULT_ITEM_INFO && this.mSliceItem == null, "ItemInfo, Slice and SliceItem are mutual exclusive; cannot set more than one of them.");
            Preconditions.checkNotNull(slice, "expected valid slice but received null");
            Preconditions.checkNotNull(slice.getUri(), "expected valid slice uri but received null");
            this.mSlice = slice;
            return this;
        }

        public void log(final StatsLogManager.EventEnum eventEnum) {
            if (Utilities.ATLEAST_R) {
                LauncherAppState instanceNoCreate = LauncherAppState.getInstanceNoCreate();
                if (this.mSlice == null && this.mSliceItem != null) {
                    this.mSlice = (LauncherAtom.Slice) LauncherAtom.Slice.newBuilder().setUri(this.mSliceItem.getSlice().getUri().toString()).build();
                }
                if (this.mSlice != null) {
                    Executors.MODEL_EXECUTOR.execute(new Runnable(eventEnum) {
                        public final /* synthetic */ StatsLogManager.EventEnum f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            StatsLogCompatManager.StatsCompatLogger.this.lambda$log$0$StatsLogCompatManager$StatsCompatLogger(this.f$1);
                        }
                    });
                } else if (this.mItemInfo.container < 0 || instanceNoCreate == null) {
                    Executors.MODEL_EXECUTOR.execute(new Runnable(eventEnum) {
                        public final /* synthetic */ StatsLogManager.EventEnum f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            StatsLogCompatManager.StatsCompatLogger.this.lambda$log$1$StatsLogCompatManager$StatsCompatLogger(this.f$1);
                        }
                    });
                } else {
                    instanceNoCreate.getModel().enqueueModelUpdateTask(new BaseModelUpdateTask() {
                        public void execute(LauncherAppState launcherAppState, BgDataModel bgDataModel, AllAppsList allAppsList) {
                            StatsCompatLogger statsCompatLogger = StatsCompatLogger.this;
                            statsCompatLogger.write(eventEnum, statsCompatLogger.applyOverwrites(statsCompatLogger.mItemInfo.buildProto((FolderInfo) bgDataModel.folders.get(StatsCompatLogger.this.mItemInfo.container))));
                        }
                    });
                }
            }
        }

        public /* synthetic */ void lambda$log$0$StatsLogCompatManager$StatsCompatLogger(StatsLogManager.EventEnum eventEnum) {
            LauncherAtom.ItemInfo.Builder slice = LauncherAtom.ItemInfo.newBuilder().setSlice(this.mSlice);
            Optional<LauncherAtom.ContainerInfo> optional = this.mContainerInfo;
            Objects.requireNonNull(slice);
            optional.ifPresent(new Consumer() {
                public final void accept(Object obj) {
                    LauncherAtom.ItemInfo.Builder.this.setContainerInfo((LauncherAtom.ContainerInfo) obj);
                }
            });
            write(eventEnum, applyOverwrites((LauncherAtom.ItemInfo) slice.build()));
        }

        public /* synthetic */ void lambda$log$1$StatsLogCompatManager$StatsCompatLogger(StatsLogManager.EventEnum eventEnum) {
            write(eventEnum, applyOverwrites(this.mItemInfo.buildProto()));
        }

        public void sendToInteractionJankMonitor(StatsLogManager.EventEnum eventEnum, View view) {
            if (eventEnum instanceof StatsLogManager.LauncherEvent) {
                int i = AnonymousClass1.$SwitchMap$com$android$launcher3$logging$StatsLogManager$LauncherEvent[((StatsLogManager.LauncherEvent) eventEnum).ordinal()];
                if (i == 1) {
                    InteractionJankMonitorWrapper.begin(view, 26);
                } else if (i == 2) {
                    InteractionJankMonitorWrapper.end(26);
                }
            }
        }

        /* access modifiers changed from: private */
        public LauncherAtom.ItemInfo applyOverwrites(LauncherAtom.ItemInfo itemInfo) {
            LauncherAtom.ItemInfo.Builder builder = (LauncherAtom.ItemInfo.Builder) itemInfo.toBuilder();
            OptionalInt optionalInt = this.mRank;
            Objects.requireNonNull(builder);
            optionalInt.ifPresent(new IntConsumer() {
                public final void accept(int i) {
                    LauncherAtom.ItemInfo.Builder.this.setRank(i);
                }
            });
            Optional<LauncherAtom.ContainerInfo> optional = this.mContainerInfo;
            Objects.requireNonNull(builder);
            optional.ifPresent(new Consumer() {
                public final void accept(Object obj) {
                    LauncherAtom.ItemInfo.Builder.this.setContainerInfo((LauncherAtom.ContainerInfo) obj);
                }
            });
            this.mActivityContext.ifPresent(new Consumer() {
                public final void accept(Object obj) {
                    ((ActivityContext) obj).applyOverwritesToLogItem(LauncherAtom.ItemInfo.Builder.this);
                }
            });
            if (this.mFromState.isPresent() || this.mToState.isPresent() || this.mEditText.isPresent()) {
                LauncherAtom.FolderIcon.Builder builder2 = (LauncherAtom.FolderIcon.Builder) builder.getFolderIcon().toBuilder();
                Optional<LauncherAtom.FromState> optional2 = this.mFromState;
                Objects.requireNonNull(builder2);
                optional2.ifPresent(new Consumer() {
                    public final void accept(Object obj) {
                        LauncherAtom.FolderIcon.Builder.this.setFromLabelState((LauncherAtom.FromState) obj);
                    }
                });
                Optional<LauncherAtom.ToState> optional3 = this.mToState;
                Objects.requireNonNull(builder2);
                optional3.ifPresent(new Consumer() {
                    public final void accept(Object obj) {
                        LauncherAtom.FolderIcon.Builder.this.setToLabelState((LauncherAtom.ToState) obj);
                    }
                });
                Optional<String> optional4 = this.mEditText;
                Objects.requireNonNull(builder2);
                optional4.ifPresent(new Consumer() {
                    public final void accept(Object obj) {
                        LauncherAtom.FolderIcon.Builder.this.setLabelInfo((String) obj);
                    }
                });
                builder.setFolderIcon(builder2);
            }
            return (LauncherAtom.ItemInfo) builder.build();
        }

        /* access modifiers changed from: private */
        public void write(StatsLogManager.EventEnum eventEnum, LauncherAtom.ItemInfo itemInfo) {
            String str;
            StatsLogManager.EventEnum eventEnum2 = eventEnum;
            LauncherAtom.ItemInfo itemInfo2 = itemInfo;
            InstanceId instanceId = this.mInstanceId;
            int i = this.mSrcState;
            int i2 = this.mDstState;
            if (StatsLogCompatManager.IS_VERBOSE) {
                if (eventEnum2 instanceof Enum) {
                    str = ((Enum) eventEnum2).name();
                } else {
                    str = eventEnum.getId() + "";
                }
                StringBuilder sb = new StringBuilder("\n");
                if (instanceId != StatsLogCompatManager.DEFAULT_INSTANCE_ID) {
                    sb.append(String.format("InstanceId:%s ", new Object[]{instanceId}));
                }
                sb.append(str);
                if (!(i == 0 && i2 == 0)) {
                    sb.append(String.format("(State:%s->%s)", new Object[]{StatsLogCompatManager.getStateString(i), StatsLogCompatManager.getStateString(i2)}));
                }
                if (itemInfo.hasContainerInfo()) {
                    sb.append("\n").append(itemInfo2);
                }
                Log.d("StatsLog", sb.toString());
            }
            Iterator<StatsLogConsumer> it = StatsLogCompatManager.LOGS_CONSUMER.iterator();
            while (it.hasNext()) {
                it.next().consume(eventEnum2, itemInfo2);
            }
            if (!Utilities.IS_RUNNING_IN_TEST_HARNESS) {
                SysUiStatsLog.write(19, 0, i, i2, (byte[]) null, false, eventEnum.getId(), itemInfo.getItemCase().getNumber(), instanceId.getId(), 0, StatsLogCompatManager.getPackageName(itemInfo), StatsLogCompatManager.getComponentName(itemInfo), StatsLogCompatManager.getGridX(itemInfo2, false), StatsLogCompatManager.getGridY(itemInfo2, false), StatsLogCompatManager.getPageId(itemInfo), StatsLogCompatManager.getGridX(itemInfo2, true), StatsLogCompatManager.getGridY(itemInfo2, true), StatsLogCompatManager.getParentPageId(itemInfo), StatsLogCompatManager.getHierarchy(itemInfo), itemInfo.getIsWork(), itemInfo.getRank(), itemInfo.getFolderIcon().getFromLabelState().getNumber(), itemInfo.getFolderIcon().getToLabelState().getNumber(), itemInfo.getFolderIcon().getLabelInfo(), StatsLogCompatManager.getCardinality(itemInfo), StatsLogCompatManager.getFeatures(itemInfo), StatsLogCompatManager.getSearchAttributes(itemInfo), StatsLogCompatManager.getAttributes(itemInfo));
            }
        }
    }

    private static class StatsCompatLatencyLogger implements StatsLogManager.StatsLatencyLogger {
        private final Optional<ActivityContext> mActivityContext;
        private final Context mContext;
        private InstanceId mInstanceId = StatsLogCompatManager.DEFAULT_INSTANCE_ID;
        private long mLatencyInMillis;
        private int mPackageId = 0;
        private int mQueryLength = -1;
        private StatsLogManager.StatsLatencyLogger.LatencyType mType = StatsLogManager.StatsLatencyLogger.LatencyType.UNKNOWN;

        StatsCompatLatencyLogger(Context context, ActivityContext activityContext) {
            this.mContext = context;
            this.mActivityContext = Optional.ofNullable(activityContext);
        }

        public StatsLogManager.StatsLatencyLogger withInstanceId(InstanceId instanceId) {
            this.mInstanceId = instanceId;
            return this;
        }

        public StatsLogManager.StatsLatencyLogger withType(StatsLogManager.StatsLatencyLogger.LatencyType latencyType) {
            this.mType = latencyType;
            return this;
        }

        public StatsLogManager.StatsLatencyLogger withPackageId(int i) {
            this.mPackageId = i;
            return this;
        }

        public StatsLogManager.StatsLatencyLogger withLatency(long j) {
            this.mLatencyInMillis = j;
            return this;
        }

        public StatsLogManager.StatsLatencyLogger withQueryLength(int i) {
            this.mQueryLength = i;
            return this;
        }

        public void log(StatsLogManager.EventEnum eventEnum) {
            String str;
            if (StatsLogCompatManager.IS_VERBOSE) {
                if (eventEnum instanceof Enum) {
                    str = ((Enum) eventEnum).name();
                } else {
                    str = eventEnum.getId() + "";
                }
                Log.d(StatsLogCompatManager.LATENCY_TAG, "\n" + String.format("InstanceId:%s ", new Object[]{this.mInstanceId}) + String.format("%s=%sms", new Object[]{str, Long.valueOf(this.mLatencyInMillis)}));
            }
            SysUiStatsLog.write(SysUiStatsLog.LAUNCHER_LATENCY, eventEnum.getId(), this.mInstanceId.getId(), this.mPackageId, this.mLatencyInMillis, this.mType.getId(), this.mQueryLength);
        }
    }

    /* access modifiers changed from: private */
    public static int getCardinality(LauncherAtom.ItemInfo itemInfo) {
        if (Utilities.IS_RUNNING_IN_TEST_HARNESS) {
            return 0;
        }
        int i = AnonymousClass1.$SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase[itemInfo.getContainerInfo().getContainerCase().ordinal()];
        if (i == 1) {
            return itemInfo.getContainerInfo().getPredictedHotseatContainer().getCardinality();
        }
        if (i == 2) {
            return itemInfo.getContainerInfo().getTaskBarContainer().getCardinality();
        }
        if (i == 3) {
            return itemInfo.getContainerInfo().getSearchResultContainer().getQueryLength();
        }
        if (i == 4) {
            LauncherAtomExtensions.ExtendedContainers extendedContainers = itemInfo.getContainerInfo().getExtendedContainers();
            if (extendedContainers.getContainerCase() == LauncherAtomExtensions.ExtendedContainers.ContainerCase.DEVICE_SEARCH_RESULT_CONTAINER) {
                LauncherAtomExtensions.DeviceSearchResultContainer deviceSearchResultContainer = extendedContainers.getDeviceSearchResultContainer();
                if (deviceSearchResultContainer.hasQueryLength()) {
                    return deviceSearchResultContainer.getQueryLength();
                }
                return -1;
            }
        }
        return itemInfo.getFolderIcon().getCardinality();
    }

    /* renamed from: com.android.quickstep.logging.StatsLogCompatManager$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase;
        static final /* synthetic */ int[] $SwitchMap$com$android$launcher3$logger$LauncherAtom$ItemInfo$ItemCase;
        static final /* synthetic */ int[] $SwitchMap$com$android$launcher3$logging$StatsLogManager$LauncherEvent;

        /* JADX WARNING: Can't wrap try/catch for region: R(31:0|(2:1|2)|3|(2:5|6)|7|9|10|11|(2:13|14)|15|(2:17|18)|19|21|22|23|25|26|27|28|29|30|31|32|33|34|35|36|37|39|40|(3:41|42|44)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(33:0|(2:1|2)|3|(2:5|6)|7|9|10|11|(2:13|14)|15|(2:17|18)|19|21|22|23|25|26|27|28|29|30|31|32|33|34|35|36|37|39|40|41|42|44) */
        /* JADX WARNING: Can't wrap try/catch for region: R(35:0|1|2|3|(2:5|6)|7|9|10|11|13|14|15|(2:17|18)|19|21|22|23|25|26|27|28|29|30|31|32|33|34|35|36|37|39|40|41|42|44) */
        /* JADX WARNING: Can't wrap try/catch for region: R(37:0|1|2|3|5|6|7|9|10|11|13|14|15|17|18|19|21|22|23|25|26|27|28|29|30|31|32|33|34|35|36|37|39|40|41|42|44) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:27:0x005a */
        /* JADX WARNING: Missing exception handler attribute for start block: B:29:0x0064 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:31:0x006e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:33:0x0078 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:35:0x0082 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:41:0x009d */
        static {
            /*
                com.android.launcher3.logger.LauncherAtom$ItemInfo$ItemCase[] r0 = com.android.launcher3.logger.LauncherAtom.ItemInfo.ItemCase.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$android$launcher3$logger$LauncherAtom$ItemInfo$ItemCase = r0
                r1 = 1
                com.android.launcher3.logger.LauncherAtom$ItemInfo$ItemCase r2 = com.android.launcher3.logger.LauncherAtom.ItemInfo.ItemCase.APPLICATION     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r2 = r2.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r0[r2] = r1     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                r0 = 2
                int[] r2 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ItemInfo$ItemCase     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.launcher3.logger.LauncherAtom$ItemInfo$ItemCase r3 = com.android.launcher3.logger.LauncherAtom.ItemInfo.ItemCase.SHORTCUT     // Catch:{ NoSuchFieldError -> 0x001d }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2[r3] = r0     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                r2 = 3
                int[] r3 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ItemInfo$ItemCase     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.launcher3.logger.LauncherAtom$ItemInfo$ItemCase r4 = com.android.launcher3.logger.LauncherAtom.ItemInfo.ItemCase.WIDGET     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r4 = r4.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r3[r4] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                r3 = 4
                int[] r4 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ItemInfo$ItemCase     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.android.launcher3.logger.LauncherAtom$ItemInfo$ItemCase r5 = com.android.launcher3.logger.LauncherAtom.ItemInfo.ItemCase.TASK     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r4[r5] = r3     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                r4 = 5
                int[] r5 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ItemInfo$ItemCase     // Catch:{ NoSuchFieldError -> 0x003e }
                com.android.launcher3.logger.LauncherAtom$ItemInfo$ItemCase r6 = com.android.launcher3.logger.LauncherAtom.ItemInfo.ItemCase.SEARCH_ACTION_ITEM     // Catch:{ NoSuchFieldError -> 0x003e }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r5[r6] = r4     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                r5 = 6
                int[] r6 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ItemInfo$ItemCase     // Catch:{ NoSuchFieldError -> 0x0049 }
                com.android.launcher3.logger.LauncherAtom$ItemInfo$ItemCase r7 = com.android.launcher3.logger.LauncherAtom.ItemInfo.ItemCase.SLICE     // Catch:{ NoSuchFieldError -> 0x0049 }
                int r7 = r7.ordinal()     // Catch:{ NoSuchFieldError -> 0x0049 }
                r6[r7] = r5     // Catch:{ NoSuchFieldError -> 0x0049 }
            L_0x0049:
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase[] r6 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.values()
                int r6 = r6.length
                int[] r6 = new int[r6]
                $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase = r6
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r7 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.PREDICTED_HOTSEAT_CONTAINER     // Catch:{ NoSuchFieldError -> 0x005a }
                int r7 = r7.ordinal()     // Catch:{ NoSuchFieldError -> 0x005a }
                r6[r7] = r1     // Catch:{ NoSuchFieldError -> 0x005a }
            L_0x005a:
                int[] r6 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase     // Catch:{ NoSuchFieldError -> 0x0064 }
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r7 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.TASK_BAR_CONTAINER     // Catch:{ NoSuchFieldError -> 0x0064 }
                int r7 = r7.ordinal()     // Catch:{ NoSuchFieldError -> 0x0064 }
                r6[r7] = r0     // Catch:{ NoSuchFieldError -> 0x0064 }
            L_0x0064:
                int[] r6 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase     // Catch:{ NoSuchFieldError -> 0x006e }
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r7 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.SEARCH_RESULT_CONTAINER     // Catch:{ NoSuchFieldError -> 0x006e }
                int r7 = r7.ordinal()     // Catch:{ NoSuchFieldError -> 0x006e }
                r6[r7] = r2     // Catch:{ NoSuchFieldError -> 0x006e }
            L_0x006e:
                int[] r2 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase     // Catch:{ NoSuchFieldError -> 0x0078 }
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r6 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.EXTENDED_CONTAINERS     // Catch:{ NoSuchFieldError -> 0x0078 }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x0078 }
                r2[r6] = r3     // Catch:{ NoSuchFieldError -> 0x0078 }
            L_0x0078:
                int[] r2 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase     // Catch:{ NoSuchFieldError -> 0x0082 }
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r3 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.FOLDER     // Catch:{ NoSuchFieldError -> 0x0082 }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x0082 }
                r2[r3] = r4     // Catch:{ NoSuchFieldError -> 0x0082 }
            L_0x0082:
                int[] r2 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase     // Catch:{ NoSuchFieldError -> 0x008c }
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r3 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.HOTSEAT     // Catch:{ NoSuchFieldError -> 0x008c }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x008c }
                r2[r3] = r5     // Catch:{ NoSuchFieldError -> 0x008c }
            L_0x008c:
                com.android.launcher3.logging.StatsLogManager$LauncherEvent[] r2 = com.android.launcher3.logging.StatsLogManager.LauncherEvent.values()
                int r2 = r2.length
                int[] r2 = new int[r2]
                $SwitchMap$com$android$launcher3$logging$StatsLogManager$LauncherEvent = r2
                com.android.launcher3.logging.StatsLogManager$LauncherEvent r3 = com.android.launcher3.logging.StatsLogManager.LauncherEvent.LAUNCHER_ALLAPPS_VERTICAL_SWIPE_BEGIN     // Catch:{ NoSuchFieldError -> 0x009d }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x009d }
                r2[r3] = r1     // Catch:{ NoSuchFieldError -> 0x009d }
            L_0x009d:
                int[] r1 = $SwitchMap$com$android$launcher3$logging$StatsLogManager$LauncherEvent     // Catch:{ NoSuchFieldError -> 0x00a7 }
                com.android.launcher3.logging.StatsLogManager$LauncherEvent r2 = com.android.launcher3.logging.StatsLogManager.LauncherEvent.LAUNCHER_ALLAPPS_VERTICAL_SWIPE_END     // Catch:{ NoSuchFieldError -> 0x00a7 }
                int r2 = r2.ordinal()     // Catch:{ NoSuchFieldError -> 0x00a7 }
                r1[r2] = r0     // Catch:{ NoSuchFieldError -> 0x00a7 }
            L_0x00a7:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.logging.StatsLogCompatManager.AnonymousClass1.<clinit>():void");
        }
    }

    /* access modifiers changed from: private */
    public static String getPackageName(LauncherAtom.ItemInfo itemInfo) {
        int i = AnonymousClass1.$SwitchMap$com$android$launcher3$logger$LauncherAtom$ItemInfo$ItemCase[itemInfo.getItemCase().ordinal()];
        if (i == 1) {
            return itemInfo.getApplication().getPackageName();
        }
        if (i == 2) {
            return itemInfo.getShortcut().getShortcutName();
        }
        if (i == 3) {
            return itemInfo.getWidget().getPackageName();
        }
        if (i == 4) {
            return itemInfo.getTask().getPackageName();
        }
        if (i != 5) {
            return null;
        }
        return itemInfo.getSearchActionItem().getPackageName();
    }

    /* access modifiers changed from: private */
    public static String getComponentName(LauncherAtom.ItemInfo itemInfo) {
        switch (AnonymousClass1.$SwitchMap$com$android$launcher3$logger$LauncherAtom$ItemInfo$ItemCase[itemInfo.getItemCase().ordinal()]) {
            case 1:
                return itemInfo.getApplication().getComponentName();
            case 2:
                return itemInfo.getShortcut().getShortcutName();
            case 3:
                return itemInfo.getWidget().getComponentName();
            case 4:
                return itemInfo.getTask().getComponentName();
            case 5:
                return itemInfo.getSearchActionItem().getTitle();
            case 6:
                return itemInfo.getSlice().getUri();
            default:
                return null;
        }
    }

    /* access modifiers changed from: private */
    public static int getGridX(LauncherAtom.ItemInfo itemInfo, boolean z) {
        LauncherAtom.ContainerInfo containerInfo = itemInfo.getContainerInfo();
        if (containerInfo.getContainerCase() == LauncherAtom.ContainerInfo.ContainerCase.FOLDER) {
            if (z) {
                return containerInfo.getFolder().getWorkspace().getGridX();
            }
            return containerInfo.getFolder().getGridX();
        } else if (containerInfo.getContainerCase() == LauncherAtom.ContainerInfo.ContainerCase.EXTENDED_CONTAINERS) {
            return containerInfo.getExtendedContainers().getDeviceSearchResultContainer().getGridX();
        } else {
            return containerInfo.getWorkspace().getGridX();
        }
    }

    /* access modifiers changed from: private */
    public static int getGridY(LauncherAtom.ItemInfo itemInfo, boolean z) {
        if (itemInfo.getContainerInfo().getContainerCase() != LauncherAtom.ContainerInfo.ContainerCase.FOLDER) {
            return itemInfo.getContainerInfo().getWorkspace().getGridY();
        }
        if (z) {
            return itemInfo.getContainerInfo().getFolder().getWorkspace().getGridY();
        }
        return itemInfo.getContainerInfo().getFolder().getGridY();
    }

    /* access modifiers changed from: private */
    public static int getPageId(LauncherAtom.ItemInfo itemInfo) {
        if (itemInfo.hasTask()) {
            return itemInfo.getTask().getIndex();
        }
        int i = AnonymousClass1.$SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase[itemInfo.getContainerInfo().getContainerCase().ordinal()];
        if (i == 1) {
            return itemInfo.getContainerInfo().getPredictedHotseatContainer().getIndex();
        }
        if (i == 2) {
            return itemInfo.getContainerInfo().getTaskBarContainer().getIndex();
        }
        if (i != 5) {
            return i != 6 ? itemInfo.getContainerInfo().getWorkspace().getPageIndex() : itemInfo.getContainerInfo().getHotseat().getIndex();
        }
        return itemInfo.getContainerInfo().getFolder().getPageIndex();
    }

    /* access modifiers changed from: private */
    public static int getParentPageId(LauncherAtom.ItemInfo itemInfo) {
        int i = AnonymousClass1.$SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase[itemInfo.getContainerInfo().getContainerCase().ordinal()];
        if (i == 3) {
            return itemInfo.getContainerInfo().getSearchResultContainer().getWorkspace().getPageIndex();
        }
        if (i != 5) {
            return itemInfo.getContainerInfo().getWorkspace().getPageIndex();
        }
        if (itemInfo.getContainerInfo().getFolder().getParentContainerCase() == LauncherAtom.FolderContainer.ParentContainerCase.HOTSEAT) {
            return itemInfo.getContainerInfo().getFolder().getHotseat().getIndex();
        }
        return itemInfo.getContainerInfo().getFolder().getWorkspace().getPageIndex();
    }

    /* access modifiers changed from: private */
    public static int getHierarchy(LauncherAtom.ItemInfo itemInfo) {
        if (Utilities.IS_RUNNING_IN_TEST_HARNESS) {
            return 0;
        }
        if (itemInfo.getContainerInfo().getContainerCase() == LauncherAtom.ContainerInfo.ContainerCase.FOLDER) {
            return itemInfo.getContainerInfo().getFolder().getParentContainerCase().getNumber() + 100;
        }
        if (itemInfo.getContainerInfo().getContainerCase() == LauncherAtom.ContainerInfo.ContainerCase.SEARCH_RESULT_CONTAINER) {
            return itemInfo.getContainerInfo().getSearchResultContainer().getParentContainerCase().getNumber() + 200;
        }
        if (itemInfo.getContainerInfo().getContainerCase() == LauncherAtom.ContainerInfo.ContainerCase.EXTENDED_CONTAINERS) {
            return itemInfo.getContainerInfo().getExtendedContainers().getContainerCase().getNumber() + 300;
        }
        if (itemInfo.getContainerInfo().getContainerCase() == LauncherAtom.ContainerInfo.ContainerCase.ALL_APPS_CONTAINER) {
            return itemInfo.getContainerInfo().getAllAppsContainer().getParentContainerCase().getNumber() + 400;
        }
        return itemInfo.getContainerInfo().getContainerCase().getNumber();
    }

    /* access modifiers changed from: private */
    public static int getFeatures(LauncherAtom.ItemInfo itemInfo) {
        if (itemInfo.getItemCase().equals(LauncherAtom.ItemInfo.ItemCase.WIDGET)) {
            return itemInfo.getWidget().getWidgetFeatures();
        }
        return 0;
    }

    /* access modifiers changed from: private */
    public static int getSearchAttributes(LauncherAtom.ItemInfo itemInfo) {
        if (Utilities.IS_RUNNING_IN_TEST_HARNESS) {
            return 0;
        }
        LauncherAtom.ContainerInfo containerInfo = itemInfo.getContainerInfo();
        if (containerInfo.getContainerCase() == LauncherAtom.ContainerInfo.ContainerCase.EXTENDED_CONTAINERS && containerInfo.getExtendedContainers().getContainerCase() == LauncherAtomExtensions.ExtendedContainers.ContainerCase.DEVICE_SEARCH_RESULT_CONTAINER && containerInfo.getExtendedContainers().getDeviceSearchResultContainer().hasSearchAttributes()) {
            return searchAttributesToInt(containerInfo.getExtendedContainers().getDeviceSearchResultContainer().getSearchAttributes());
        }
        return 0;
    }

    private static int searchAttributesToInt(LauncherAtomExtensions.DeviceSearchResultContainer.SearchAttributes searchAttributes) {
        int i = searchAttributes.getCorrectedQuery() ? 1 : 0;
        if (searchAttributes.getDirectMatch()) {
            i |= 2;
        }
        if (searchAttributes.getEntryState() == LauncherAtomExtensions.DeviceSearchResultContainer.SearchAttributes.EntryState.ALL_APPS) {
            return i | 4;
        }
        return searchAttributes.getEntryState() == LauncherAtomExtensions.DeviceSearchResultContainer.SearchAttributes.EntryState.QSB ? i | 8 : i;
    }
}
