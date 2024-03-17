package com.android.launcher3.model.data;

import android.os.Process;
import android.text.TextUtils;
import androidx.core.util.Preconditions;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.folder.FolderNameInfos;
import com.android.launcher3.logger.LauncherAtom;
import com.android.launcher3.model.ModelWriter;
import com.android.launcher3.util.ContentWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

public class FolderInfo extends ItemInfo {
    public static final String EXTRA_FOLDER_SUGGESTIONS = "suggest";
    public static final int FLAG_ITEMS_SORTED = 1;
    public static final int FLAG_MANUAL_FOLDER_NAME = 8;
    public static final int FLAG_MULTI_PAGE_ANIMATION = 4;
    public static final int FLAG_WORK_FOLDER = 2;
    public static final int NO_FLAGS = 0;
    public ArrayList<WorkspaceItemInfo> contents = new ArrayList<>();
    private ArrayList<FolderListener> mListeners = new ArrayList<>();
    public int options;
    public FolderNameInfos suggestedFolderNames;

    public interface FolderListener {
        void onAdd(WorkspaceItemInfo workspaceItemInfo, int i);

        void onItemsChanged(boolean z);

        void onRemove(List<WorkspaceItemInfo> list);
    }

    public enum LabelState {
        UNLABELED(LauncherAtom.Attribute.UNLABELED),
        EMPTY(LauncherAtom.Attribute.EMPTY_LABEL),
        SUGGESTED(LauncherAtom.Attribute.SUGGESTED_LABEL),
        MANUAL(LauncherAtom.Attribute.MANUAL_LABEL);
        
        /* access modifiers changed from: private */
        public final LauncherAtom.Attribute mLogAttribute;

        private LabelState(LauncherAtom.Attribute attribute) {
            this.mLogAttribute = attribute;
        }
    }

    public FolderInfo() {
        this.itemType = 2;
        this.user = Process.myUserHandle();
    }

    public void add(WorkspaceItemInfo workspaceItemInfo, boolean z) {
        add(workspaceItemInfo, this.contents.size(), z);
    }

    public void add(WorkspaceItemInfo workspaceItemInfo, int i, boolean z) {
        int boundToRange = Utilities.boundToRange(i, 0, this.contents.size());
        this.contents.add(boundToRange, workspaceItemInfo);
        for (int i2 = 0; i2 < this.mListeners.size(); i2++) {
            this.mListeners.get(i2).onAdd(workspaceItemInfo, boundToRange);
        }
        itemsChanged(z);
    }

    public void remove(WorkspaceItemInfo workspaceItemInfo, boolean z) {
        removeAll(Collections.singletonList(workspaceItemInfo), z);
    }

    public void removeAll(List<WorkspaceItemInfo> list, boolean z) {
        this.contents.removeAll(list);
        for (int i = 0; i < this.mListeners.size(); i++) {
            this.mListeners.get(i).onRemove(list);
        }
        itemsChanged(z);
    }

    public void onAddToDatabase(ContentWriter contentWriter) {
        super.onAddToDatabase(contentWriter);
        contentWriter.put(LauncherSettings.Favorites.TITLE, this.title).put(LauncherSettings.Favorites.OPTIONS, Integer.valueOf(this.options));
    }

    public void addListener(FolderListener folderListener) {
        this.mListeners.add(folderListener);
    }

    public void removeListener(FolderListener folderListener) {
        this.mListeners.remove(folderListener);
    }

    public void itemsChanged(boolean z) {
        for (int i = 0; i < this.mListeners.size(); i++) {
            this.mListeners.get(i).onItemsChanged(z);
        }
    }

    public boolean hasOption(int i) {
        return (i & this.options) != 0;
    }

    public void setOption(int i, boolean z, ModelWriter modelWriter) {
        int i2 = this.options;
        if (z) {
            this.options = i | i2;
        } else {
            this.options = (~i) & i2;
        }
        if (modelWriter != null && i2 != this.options) {
            modelWriter.updateItemInDatabase(this);
        }
    }

    /* access modifiers changed from: protected */
    public String dumpProperties() {
        return String.format("%s; labelState=%s", new Object[]{super.dumpProperties(), getLabelState()});
    }

    public LauncherAtom.ItemInfo buildProto(FolderInfo folderInfo) {
        LauncherAtom.FolderIcon.Builder cardinality = LauncherAtom.FolderIcon.newBuilder().setCardinality(this.contents.size());
        if (LabelState.SUGGESTED.equals(getLabelState())) {
            cardinality.setLabelInfo(this.title.toString());
        }
        return (LauncherAtom.ItemInfo) getDefaultItemInfoBuilder().setFolderIcon(cardinality).setRank(this.rank).addItemAttributes(getLabelState().mLogAttribute).setContainerInfo(getContainerInfo()).build();
    }

    public void setTitle(CharSequence charSequence, ModelWriter modelWriter) {
        LabelState labelState;
        if (TextUtils.isEmpty(charSequence) && this.title == null) {
            return;
        }
        if (charSequence == null || !charSequence.equals(this.title)) {
            this.title = charSequence;
            if (charSequence == null) {
                labelState = LabelState.UNLABELED;
            } else if (charSequence.length() == 0) {
                labelState = LabelState.EMPTY;
            } else if (getAcceptedSuggestionIndex().isPresent()) {
                labelState = LabelState.SUGGESTED;
            } else {
                labelState = LabelState.MANUAL;
            }
            if (labelState.equals(LabelState.MANUAL)) {
                this.options |= 8;
            } else {
                this.options &= -9;
            }
            if (modelWriter != null) {
                modelWriter.updateItemInDatabase(this);
            }
        }
    }

    public LabelState getLabelState() {
        if (this.title == null) {
            return LabelState.UNLABELED;
        }
        if (this.title.length() == 0) {
            return LabelState.EMPTY;
        }
        if (hasOption(8)) {
            return LabelState.MANUAL;
        }
        return LabelState.SUGGESTED;
    }

    public ItemInfo makeShallowCopy() {
        FolderInfo folderInfo = new FolderInfo();
        folderInfo.copyFrom(this);
        folderInfo.contents = this.contents;
        return folderInfo;
    }

    public LauncherAtom.ItemInfo buildProto() {
        return buildProto((FolderInfo) null);
    }

    public OptionalInt getAcceptedSuggestionIndex() {
        String charSequence = ((CharSequence) Preconditions.checkNotNull(this.title, "Expected valid folder label, but found null")).toString();
        FolderNameInfos folderNameInfos = this.suggestedFolderNames;
        if (folderNameInfos == null || !folderNameInfos.hasSuggestions()) {
            return OptionalInt.empty();
        }
        CharSequence[] labels = this.suggestedFolderNames.getLabels();
        return IntStream.range(0, labels.length).filter(new IntPredicate(labels, charSequence) {
            public final /* synthetic */ CharSequence[] f$0;
            public final /* synthetic */ String f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final boolean test(int i) {
                return FolderInfo.lambda$getAcceptedSuggestionIndex$0(this.f$0, this.f$1, i);
            }
        }).sequential().findFirst();
    }

    static /* synthetic */ boolean lambda$getAcceptedSuggestionIndex$0(CharSequence[] charSequenceArr, String str, int i) {
        return !TextUtils.isEmpty(charSequenceArr[i]) && str.equalsIgnoreCase(charSequenceArr[i].toString());
    }

    /* renamed from: com.android.launcher3.model.data.FolderInfo$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$launcher3$model$data$FolderInfo$LabelState;

        /* JADX WARNING: Can't wrap try/catch for region: R(8:0|1|2|3|4|5|6|(3:7|8|10)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        static {
            /*
                com.android.launcher3.model.data.FolderInfo$LabelState[] r0 = com.android.launcher3.model.data.FolderInfo.LabelState.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$android$launcher3$model$data$FolderInfo$LabelState = r0
                com.android.launcher3.model.data.FolderInfo$LabelState r1 = com.android.launcher3.model.data.FolderInfo.LabelState.EMPTY     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$com$android$launcher3$model$data$FolderInfo$LabelState     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.launcher3.model.data.FolderInfo$LabelState r1 = com.android.launcher3.model.data.FolderInfo.LabelState.MANUAL     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = $SwitchMap$com$android$launcher3$model$data$FolderInfo$LabelState     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.launcher3.model.data.FolderInfo$LabelState r1 = com.android.launcher3.model.data.FolderInfo.LabelState.SUGGESTED     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = $SwitchMap$com$android$launcher3$model$data$FolderInfo$LabelState     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.android.launcher3.model.data.FolderInfo$LabelState r1 = com.android.launcher3.model.data.FolderInfo.LabelState.UNLABELED     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.data.FolderInfo.AnonymousClass1.<clinit>():void");
        }
    }

    public LauncherAtom.FromState getFromLabelState() {
        int i = AnonymousClass1.$SwitchMap$com$android$launcher3$model$data$FolderInfo$LabelState[getLabelState().ordinal()];
        if (i == 1) {
            return LauncherAtom.FromState.FROM_EMPTY;
        }
        if (i == 2) {
            return LauncherAtom.FromState.FROM_CUSTOM;
        }
        if (i != 3) {
            return LauncherAtom.FromState.FROM_STATE_UNSPECIFIED;
        }
        return LauncherAtom.FromState.FROM_SUGGESTED;
    }

    public LauncherAtom.ToState getToLabelState() {
        if (this.title == null) {
            return LauncherAtom.ToState.TO_STATE_UNSPECIFIED;
        }
        if (FeatureFlags.FOLDER_NAME_SUGGEST.get()) {
            FolderNameInfos folderNameInfos = this.suggestedFolderNames;
            if (folderNameInfos != null && folderNameInfos.hasSuggestions()) {
                FolderNameInfos folderNameInfos2 = this.suggestedFolderNames;
                boolean z = folderNameInfos2 != null && folderNameInfos2.hasPrimary();
                if (this.title.length() != 0) {
                    OptionalInt acceptedSuggestionIndex = getAcceptedSuggestionIndex();
                    if (acceptedSuggestionIndex.isPresent()) {
                        int asInt = acceptedSuggestionIndex.getAsInt();
                        if (asInt == 0) {
                            return LauncherAtom.ToState.TO_SUGGESTION0;
                        }
                        if (asInt != 1) {
                            if (asInt != 2) {
                                if (asInt != 3) {
                                    return LauncherAtom.ToState.TO_STATE_UNSPECIFIED;
                                }
                                if (z) {
                                    return LauncherAtom.ToState.TO_SUGGESTION3_WITH_VALID_PRIMARY;
                                }
                                return LauncherAtom.ToState.TO_SUGGESTION3_WITH_EMPTY_PRIMARY;
                            } else if (z) {
                                return LauncherAtom.ToState.TO_SUGGESTION2_WITH_VALID_PRIMARY;
                            } else {
                                return LauncherAtom.ToState.TO_SUGGESTION2_WITH_EMPTY_PRIMARY;
                            }
                        } else if (z) {
                            return LauncherAtom.ToState.TO_SUGGESTION1_WITH_VALID_PRIMARY;
                        } else {
                            return LauncherAtom.ToState.TO_SUGGESTION1_WITH_EMPTY_PRIMARY;
                        }
                    } else if (z) {
                        return LauncherAtom.ToState.TO_CUSTOM_WITH_VALID_PRIMARY;
                    } else {
                        return LauncherAtom.ToState.TO_CUSTOM_WITH_VALID_SUGGESTIONS_AND_EMPTY_PRIMARY;
                    }
                } else if (z) {
                    return LauncherAtom.ToState.TO_EMPTY_WITH_VALID_PRIMARY;
                } else {
                    return LauncherAtom.ToState.TO_EMPTY_WITH_VALID_SUGGESTIONS_AND_EMPTY_PRIMARY;
                }
            } else if (this.title.length() > 0) {
                return LauncherAtom.ToState.TO_CUSTOM_WITH_EMPTY_SUGGESTIONS;
            } else {
                return LauncherAtom.ToState.TO_EMPTY_WITH_EMPTY_SUGGESTIONS;
            }
        } else if (this.title.length() > 0) {
            return LauncherAtom.ToState.TO_CUSTOM_WITH_SUGGESTIONS_DISABLED;
        } else {
            return LauncherAtom.ToState.TO_EMPTY_WITH_SUGGESTIONS_DISABLED;
        }
    }
}
