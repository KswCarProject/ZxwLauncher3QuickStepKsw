package com.android.launcher3.model.data;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Process;
import android.os.UserHandle;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.Workspace;
import com.android.launcher3.logger.LauncherAtom;
import com.android.launcher3.logger.LauncherAtomExtensions;
import com.android.launcher3.model.ModelWriter;
import com.android.launcher3.util.ContentWriter;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class ItemInfo {
    public static final boolean DEBUG = false;
    public static final int NO_ID = -1;
    public static final int NO_MATCHING_ID = Integer.MIN_VALUE;
    public int cellX;
    public int cellY;
    public int container;
    public CharSequence contentDescription;
    public int id;
    public int itemType;
    private ComponentName mComponentName;
    public int minSpanX;
    public int minSpanY;
    public int rank;
    public int screenId;
    public int spanX;
    public int spanY;
    public CharSequence title;
    public UserHandle user;

    public Intent getIntent() {
        return null;
    }

    public boolean isDisabled() {
        return false;
    }

    public ItemInfo() {
        this.id = -1;
        this.container = -1;
        this.screenId = -1;
        this.cellX = -1;
        this.cellY = -1;
        this.spanX = 1;
        this.spanY = 1;
        this.minSpanX = 1;
        this.minSpanY = 1;
        this.rank = 0;
        this.user = Process.myUserHandle();
    }

    protected ItemInfo(ItemInfo itemInfo) {
        this.id = -1;
        this.container = -1;
        this.screenId = -1;
        this.cellX = -1;
        this.cellY = -1;
        this.spanX = 1;
        this.spanY = 1;
        this.minSpanX = 1;
        this.minSpanY = 1;
        this.rank = 0;
        copyFrom(itemInfo);
    }

    public void copyFrom(ItemInfo itemInfo) {
        this.id = itemInfo.id;
        this.title = itemInfo.title;
        this.cellX = itemInfo.cellX;
        this.cellY = itemInfo.cellY;
        this.spanX = itemInfo.spanX;
        this.spanY = itemInfo.spanY;
        this.minSpanX = itemInfo.minSpanX;
        this.minSpanY = itemInfo.minSpanY;
        this.rank = itemInfo.rank;
        this.screenId = itemInfo.screenId;
        this.itemType = itemInfo.itemType;
        this.container = itemInfo.container;
        this.user = itemInfo.user;
        this.contentDescription = itemInfo.contentDescription;
        this.mComponentName = itemInfo.getTargetComponent();
    }

    public ComponentName getTargetComponent() {
        return (ComponentName) Optional.ofNullable(getIntent()).map($$Lambda$ItemInfo$QFy4jyVg1GPdUxgf6UDtruJD7Es.INSTANCE).orElse(this.mComponentName);
    }

    public String getTargetPackage() {
        ComponentName targetComponent = getTargetComponent();
        Intent intent = getIntent();
        if (targetComponent != null) {
            return targetComponent.getPackageName();
        }
        if (intent != null) {
            return intent.getPackage();
        }
        return null;
    }

    public void writeToValues(ContentWriter contentWriter) {
        contentWriter.put(LauncherSettings.Favorites.ITEM_TYPE, Integer.valueOf(this.itemType)).put(LauncherSettings.Favorites.CONTAINER, Integer.valueOf(this.container)).put(LauncherSettings.Favorites.SCREEN, Integer.valueOf(this.screenId)).put(LauncherSettings.Favorites.CELLX, Integer.valueOf(this.cellX)).put(LauncherSettings.Favorites.CELLY, Integer.valueOf(this.cellY)).put(LauncherSettings.Favorites.SPANX, Integer.valueOf(this.spanX)).put(LauncherSettings.Favorites.SPANY, Integer.valueOf(this.spanY)).put(LauncherSettings.Favorites.RANK, Integer.valueOf(this.rank));
    }

    public void readFromValues(ContentValues contentValues) {
        this.itemType = contentValues.getAsInteger(LauncherSettings.Favorites.ITEM_TYPE).intValue();
        this.container = contentValues.getAsInteger(LauncherSettings.Favorites.CONTAINER).intValue();
        this.screenId = contentValues.getAsInteger(LauncherSettings.Favorites.SCREEN).intValue();
        this.cellX = contentValues.getAsInteger(LauncherSettings.Favorites.CELLX).intValue();
        this.cellY = contentValues.getAsInteger(LauncherSettings.Favorites.CELLY).intValue();
        this.spanX = contentValues.getAsInteger(LauncherSettings.Favorites.SPANX).intValue();
        this.spanY = contentValues.getAsInteger(LauncherSettings.Favorites.SPANY).intValue();
        this.rank = contentValues.getAsInteger(LauncherSettings.Favorites.RANK).intValue();
    }

    public void onAddToDatabase(ContentWriter contentWriter) {
        if (!Workspace.EXTRA_EMPTY_SCREEN_IDS.contains(this.screenId)) {
            writeToValues(contentWriter);
            contentWriter.put("profileId", this.user);
            return;
        }
        throw new RuntimeException("Screen id should not be extra empty screen: " + this.screenId);
    }

    public final String toString() {
        return getClass().getSimpleName() + "(" + dumpProperties() + ")";
    }

    /* access modifiers changed from: protected */
    public String dumpProperties() {
        return "id=" + this.id + " type=" + LauncherSettings.Favorites.itemTypeToString(this.itemType) + " container=" + getContainerInfo() + " targetComponent=" + getTargetComponent() + " screen=" + this.screenId + " cell(" + this.cellX + "," + this.cellY + ") span(" + this.spanX + "," + this.spanY + ") minSpan(" + this.minSpanX + "," + this.minSpanY + ") rank=" + this.rank + " user=" + this.user + " title=" + this.title;
    }

    public int getViewId() {
        return this.id;
    }

    public boolean isPredictedItem() {
        int i = this.container;
        return i == -103 || i == -102;
    }

    public LauncherAtom.ItemInfo buildProto() {
        return buildProto((FolderInfo) null);
    }

    public LauncherAtom.ItemInfo buildProto(FolderInfo folderInfo) {
        LauncherAtom.ItemInfo.Builder defaultItemInfoBuilder = getDefaultItemInfoBuilder();
        Optional ofNullable = Optional.ofNullable(getTargetComponent());
        int i = this.itemType;
        if (i == 0) {
            defaultItemInfoBuilder.setApplication((LauncherAtom.Application.Builder) ofNullable.map($$Lambda$ItemInfo$0HL0lEvHmMYCXXgRKXb844tNmM.INSTANCE).orElse(LauncherAtom.Application.newBuilder()));
        } else if (i == 1) {
            defaultItemInfoBuilder.setShortcut((LauncherAtom.Shortcut.Builder) ofNullable.map($$Lambda$ItemInfo$ITDL92r46xJHkGVs_m2blddPMcI.INSTANCE).orElse(LauncherAtom.Shortcut.newBuilder()));
        } else if (i == 4) {
            defaultItemInfoBuilder.setWidget(((LauncherAtom.Widget.Builder) ofNullable.map($$Lambda$ItemInfo$BNXK1GCjh5afrzKvd2Boil2I8Zs.INSTANCE).orElse(LauncherAtom.Widget.newBuilder())).setSpanX(this.spanX).setSpanY(this.spanY));
        } else if (i == 6) {
            defaultItemInfoBuilder.setShortcut((LauncherAtom.Shortcut.Builder) ofNullable.map(new Function() {
                public final Object apply(Object obj) {
                    return ItemInfo.this.lambda$buildProto$2$ItemInfo((ComponentName) obj);
                }
            }).orElse(LauncherAtom.Shortcut.newBuilder()));
        } else if (i == 7) {
            defaultItemInfoBuilder.setTask(LauncherAtom.Task.newBuilder().setComponentName(getTargetComponent().flattenToShortString()).setIndex(this.screenId));
        }
        if (folderInfo != null) {
            LauncherAtom.FolderContainer.Builder newBuilder = LauncherAtom.FolderContainer.newBuilder();
            newBuilder.setGridX(this.cellX).setGridY(this.cellY).setPageIndex(this.screenId);
            int i2 = folderInfo.container;
            if (i2 == -103 || i2 == -101) {
                newBuilder.setHotseat(LauncherAtom.HotseatContainer.newBuilder().setIndex(folderInfo.screenId));
            } else if (i2 == -100) {
                newBuilder.setWorkspace(LauncherAtom.WorkspaceContainer.newBuilder().setPageIndex(folderInfo.screenId).setGridX(folderInfo.cellX).setGridY(folderInfo.cellY));
            }
            defaultItemInfoBuilder.setContainerInfo(LauncherAtom.ContainerInfo.newBuilder().setFolder(newBuilder));
        } else {
            LauncherAtom.ContainerInfo containerInfo = getContainerInfo();
            if (!containerInfo.getContainerCase().equals(LauncherAtom.ContainerInfo.ContainerCase.CONTAINER_NOT_SET)) {
                defaultItemInfoBuilder.setContainerInfo(containerInfo);
            }
        }
        return (LauncherAtom.ItemInfo) defaultItemInfoBuilder.build();
    }

    public /* synthetic */ LauncherAtom.Shortcut.Builder lambda$buildProto$2$ItemInfo(ComponentName componentName) {
        LauncherAtom.Shortcut.Builder shortcutName = LauncherAtom.Shortcut.newBuilder().setShortcutName(componentName.flattenToShortString());
        Optional map = Optional.ofNullable(getIntent()).map($$Lambda$ItemInfo$oQmwRQ06JULBwJpOr3rLrhHcXKM.INSTANCE);
        Objects.requireNonNull(shortcutName);
        map.ifPresent(new Consumer() {
            public final void accept(Object obj) {
                LauncherAtom.Shortcut.Builder.this.setShortcutId((String) obj);
            }
        });
        return shortcutName;
    }

    /* access modifiers changed from: protected */
    public LauncherAtom.ItemInfo.Builder getDefaultItemInfoBuilder() {
        LauncherAtom.ItemInfo.Builder newBuilder = LauncherAtom.ItemInfo.newBuilder();
        newBuilder.setIsWork(!Process.myUserHandle().equals(this.user));
        newBuilder.setRank(this.rank);
        return newBuilder;
    }

    public LauncherAtom.ContainerInfo getContainerInfo() {
        int i = this.container;
        if (i == -200) {
            return (LauncherAtom.ContainerInfo) LauncherAtom.ContainerInfo.newBuilder().setExtendedContainers(getExtendedContainer()).build();
        }
        if (i == -114) {
            return (LauncherAtom.ContainerInfo) LauncherAtom.ContainerInfo.newBuilder().setWallpapersContainer(LauncherAtom.WallpapersContainer.getDefaultInstance()).build();
        }
        switch (i) {
            case LauncherSettings.Favorites.CONTAINER_TASKSWITCHER:
                return (LauncherAtom.ContainerInfo) LauncherAtom.ContainerInfo.newBuilder().setTaskSwitcherContainer(LauncherAtom.TaskSwitcherContainer.getDefaultInstance()).build();
            case LauncherSettings.Favorites.CONTAINER_SETTINGS:
                return (LauncherAtom.ContainerInfo) LauncherAtom.ContainerInfo.newBuilder().setSettingsContainer(LauncherAtom.SettingsContainer.getDefaultInstance()).build();
            case LauncherSettings.Favorites.CONTAINER_SHORTCUTS:
                return (LauncherAtom.ContainerInfo) LauncherAtom.ContainerInfo.newBuilder().setShortcutsContainer(LauncherAtom.ShortcutsContainer.getDefaultInstance()).build();
            case LauncherSettings.Favorites.CONTAINER_SEARCH_RESULTS:
                return (LauncherAtom.ContainerInfo) LauncherAtom.ContainerInfo.newBuilder().setSearchResultContainer(LauncherAtom.SearchResultContainer.getDefaultInstance()).build();
            case LauncherSettings.Favorites.CONTAINER_WIDGETS_TRAY:
                return (LauncherAtom.ContainerInfo) LauncherAtom.ContainerInfo.newBuilder().setWidgetsContainer(LauncherAtom.WidgetsContainer.getDefaultInstance()).build();
            case LauncherSettings.Favorites.CONTAINER_ALL_APPS:
                return (LauncherAtom.ContainerInfo) LauncherAtom.ContainerInfo.newBuilder().setAllAppsContainer(LauncherAtom.AllAppsContainer.getDefaultInstance()).build();
            case LauncherSettings.Favorites.CONTAINER_HOTSEAT_PREDICTION:
                return (LauncherAtom.ContainerInfo) LauncherAtom.ContainerInfo.newBuilder().setPredictedHotseatContainer(LauncherAtom.PredictedHotseatContainer.newBuilder().setIndex(this.screenId)).build();
            case LauncherSettings.Favorites.CONTAINER_PREDICTION:
                return (LauncherAtom.ContainerInfo) LauncherAtom.ContainerInfo.newBuilder().setPredictionContainer(LauncherAtom.PredictionContainer.getDefaultInstance()).build();
            case LauncherSettings.Favorites.CONTAINER_HOTSEAT:
                return (LauncherAtom.ContainerInfo) LauncherAtom.ContainerInfo.newBuilder().setHotseat(LauncherAtom.HotseatContainer.newBuilder().setIndex(this.screenId)).build();
            case -100:
                return (LauncherAtom.ContainerInfo) LauncherAtom.ContainerInfo.newBuilder().setWorkspace(LauncherAtom.WorkspaceContainer.newBuilder().setGridX(this.cellX).setGridY(this.cellY).setPageIndex(this.screenId)).build();
            default:
                return LauncherAtom.ContainerInfo.getDefaultInstance();
        }
    }

    /* access modifiers changed from: protected */
    public LauncherAtomExtensions.ExtendedContainers getExtendedContainer() {
        return LauncherAtomExtensions.ExtendedContainers.getDefaultInstance();
    }

    public ItemInfo makeShallowCopy() {
        ItemInfo itemInfo = new ItemInfo();
        itemInfo.copyFrom(this);
        return itemInfo;
    }

    public void setTitle(CharSequence charSequence, ModelWriter modelWriter) {
        this.title = charSequence;
    }
}
