package com.android.launcher3.model;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.os.EnvironmentCompat;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LauncherProvider;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.Utilities;
import com.android.launcher3.logging.FileLog;
import com.android.launcher3.model.BgDataModel;
import com.android.launcher3.model.ModelWriter;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.util.ContentWriter;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.ItemInfoMatcher;
import com.android.launcher3.util.LooperExecutor;
import com.android.launcher3.widget.LauncherAppWidgetHost;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ModelWriter {
    private static final String TAG = "ModelWriter";
    /* access modifiers changed from: private */
    public final BgDataModel mBgDataModel;
    /* access modifiers changed from: private */
    public final Context mContext;
    private final List<Runnable> mDeleteRunnables = new ArrayList();
    private final boolean mHasVerticalHotseat;
    /* access modifiers changed from: private */
    public final LauncherModel mModel;
    private final BgDataModel.Callbacks mOwner;
    private boolean mPreparingToUndo;
    /* access modifiers changed from: private */
    public final LooperExecutor mUiExecutor;
    /* access modifiers changed from: private */
    public final boolean mVerifyChanges;

    public ModelWriter(Context context, LauncherModel launcherModel, BgDataModel bgDataModel, boolean z, boolean z2, BgDataModel.Callbacks callbacks) {
        this.mContext = context;
        this.mModel = launcherModel;
        this.mBgDataModel = bgDataModel;
        this.mHasVerticalHotseat = z;
        this.mVerifyChanges = z2;
        this.mOwner = callbacks;
        this.mUiExecutor = Executors.MAIN_EXECUTOR;
    }

    private void updateItemInfoProps(ItemInfo itemInfo, int i, int i2, int i3, int i4) {
        itemInfo.container = i;
        itemInfo.cellX = i3;
        itemInfo.cellY = i4;
        if (i == -101) {
            if (this.mHasVerticalHotseat) {
                i3 = (LauncherAppState.getIDP(this.mContext).numDatabaseHotseatIcons - i4) - 1;
            }
            itemInfo.screenId = i3;
            return;
        }
        itemInfo.screenId = i2;
    }

    public void addOrMoveItemInDatabase(ItemInfo itemInfo, int i, int i2, int i3, int i4) {
        if (itemInfo.id == -1) {
            addItemToDatabase(itemInfo, i, i2, i3, i4);
        } else {
            moveItemInDatabase(itemInfo, i, i2, i3, i4);
        }
    }

    /* access modifiers changed from: private */
    public void checkItemInfoLocked(int i, ItemInfo itemInfo, StackTraceElement[] stackTraceElementArr) {
        ItemInfo itemInfo2 = (ItemInfo) this.mBgDataModel.itemsIdMap.get(i);
        if (itemInfo2 != null && itemInfo != itemInfo2) {
            if (Utilities.IS_DEBUG_DEVICE || !(itemInfo2 instanceof WorkspaceItemInfo) || !(itemInfo instanceof WorkspaceItemInfo) || !itemInfo2.title.toString().equals(itemInfo.title.toString()) || !itemInfo2.getIntent().filterEquals(itemInfo.getIntent()) || itemInfo2.id != itemInfo.id || itemInfo2.itemType != itemInfo.itemType || itemInfo2.container != itemInfo.container || itemInfo2.screenId != itemInfo.screenId || itemInfo2.cellX != itemInfo.cellX || itemInfo2.cellY != itemInfo.cellY || itemInfo2.spanX != itemInfo.spanX || itemInfo2.spanY != itemInfo.spanY) {
                String str = "null";
                StringBuilder append = new StringBuilder().append("item: ").append(itemInfo != null ? itemInfo.toString() : str).append("modelItem: ");
                if (itemInfo2 != null) {
                    str = itemInfo2.toString();
                }
                RuntimeException runtimeException = new RuntimeException(append.append(str).append("Error: ItemInfo passed to checkItemInfo doesn't match original").toString());
                if (stackTraceElementArr != null) {
                    runtimeException.setStackTrace(stackTraceElementArr);
                }
                throw runtimeException;
            }
        }
    }

    public void moveItemInDatabase(ItemInfo itemInfo, int i, int i2, int i3, int i4) {
        updateItemInfoProps(itemInfo, i, i2, i3, i4);
        notifyItemModified(itemInfo);
        enqueueDeleteRunnable(new UpdateItemRunnable(itemInfo, new Supplier(itemInfo) {
            public final /* synthetic */ ItemInfo f$1;

            {
                this.f$1 = r2;
            }

            public final Object get() {
                return ModelWriter.this.lambda$moveItemInDatabase$0$ModelWriter(this.f$1);
            }
        }));
    }

    public /* synthetic */ ContentWriter lambda$moveItemInDatabase$0$ModelWriter(ItemInfo itemInfo) {
        return new ContentWriter(this.mContext).put(LauncherSettings.Favorites.CONTAINER, Integer.valueOf(itemInfo.container)).put(LauncherSettings.Favorites.CELLX, Integer.valueOf(itemInfo.cellX)).put(LauncherSettings.Favorites.CELLY, Integer.valueOf(itemInfo.cellY)).put(LauncherSettings.Favorites.RANK, Integer.valueOf(itemInfo.rank)).put(LauncherSettings.Favorites.SCREEN, Integer.valueOf(itemInfo.screenId));
    }

    public void moveItemsInDatabase(ArrayList<ItemInfo> arrayList, int i, int i2) {
        ArrayList arrayList2 = new ArrayList();
        int size = arrayList.size();
        notifyOtherCallbacks(new LauncherModel.CallbackTask(arrayList) {
            public final /* synthetic */ ArrayList f$0;

            {
                this.f$0 = r1;
            }

            public final void execute(BgDataModel.Callbacks callbacks) {
                callbacks.bindItemsModified(this.f$0);
            }
        });
        for (int i3 = 0; i3 < size; i3++) {
            ItemInfo itemInfo = arrayList.get(i3);
            updateItemInfoProps(itemInfo, i, i2, itemInfo.cellX, itemInfo.cellY);
            ContentValues contentValues = new ContentValues();
            contentValues.put(LauncherSettings.Favorites.CONTAINER, Integer.valueOf(itemInfo.container));
            contentValues.put(LauncherSettings.Favorites.CELLX, Integer.valueOf(itemInfo.cellX));
            contentValues.put(LauncherSettings.Favorites.CELLY, Integer.valueOf(itemInfo.cellY));
            contentValues.put(LauncherSettings.Favorites.RANK, Integer.valueOf(itemInfo.rank));
            contentValues.put(LauncherSettings.Favorites.SCREEN, Integer.valueOf(itemInfo.screenId));
            arrayList2.add(contentValues);
        }
        enqueueDeleteRunnable(new UpdateItemsRunnable(arrayList, arrayList2));
    }

    public void modifyItemInDatabase(ItemInfo itemInfo, int i, int i2, int i3, int i4, int i5, int i6) {
        updateItemInfoProps(itemInfo, i, i2, i3, i4);
        itemInfo.spanX = i5;
        itemInfo.spanY = i6;
        notifyItemModified(itemInfo);
        Executors.MODEL_EXECUTOR.execute(new UpdateItemRunnable(itemInfo, new Supplier(itemInfo) {
            public final /* synthetic */ ItemInfo f$1;

            {
                this.f$1 = r2;
            }

            public final Object get() {
                return ModelWriter.this.lambda$modifyItemInDatabase$2$ModelWriter(this.f$1);
            }
        }));
    }

    public /* synthetic */ ContentWriter lambda$modifyItemInDatabase$2$ModelWriter(ItemInfo itemInfo) {
        return new ContentWriter(this.mContext).put(LauncherSettings.Favorites.CONTAINER, Integer.valueOf(itemInfo.container)).put(LauncherSettings.Favorites.CELLX, Integer.valueOf(itemInfo.cellX)).put(LauncherSettings.Favorites.CELLY, Integer.valueOf(itemInfo.cellY)).put(LauncherSettings.Favorites.RANK, Integer.valueOf(itemInfo.rank)).put(LauncherSettings.Favorites.SPANX, Integer.valueOf(itemInfo.spanX)).put(LauncherSettings.Favorites.SPANY, Integer.valueOf(itemInfo.spanY)).put(LauncherSettings.Favorites.SCREEN, Integer.valueOf(itemInfo.screenId));
    }

    public void updateItemInDatabase(ItemInfo itemInfo) {
        notifyItemModified(itemInfo);
        Executors.MODEL_EXECUTOR.execute(new UpdateItemRunnable(itemInfo, new Supplier(itemInfo) {
            public final /* synthetic */ ItemInfo f$1;

            {
                this.f$1 = r2;
            }

            public final Object get() {
                return ModelWriter.this.lambda$updateItemInDatabase$3$ModelWriter(this.f$1);
            }
        }));
    }

    public /* synthetic */ ContentWriter lambda$updateItemInDatabase$3$ModelWriter(ItemInfo itemInfo) {
        ContentWriter contentWriter = new ContentWriter(this.mContext);
        itemInfo.onAddToDatabase(contentWriter);
        return contentWriter;
    }

    private void notifyItemModified(ItemInfo itemInfo) {
        notifyOtherCallbacks(new LauncherModel.CallbackTask() {
            public final void execute(BgDataModel.Callbacks callbacks) {
                callbacks.bindItemsModified(Collections.singletonList(ItemInfo.this));
            }
        });
    }

    public void addItemToDatabase(ItemInfo itemInfo, int i, int i2, int i3, int i4) {
        updateItemInfoProps(itemInfo, i, i2, i3, i4);
        ContentResolver contentResolver = this.mContext.getContentResolver();
        itemInfo.id = LauncherSettings.Settings.call(contentResolver, LauncherSettings.Settings.METHOD_NEW_ITEM_ID).getInt("value");
        notifyOtherCallbacks(new LauncherModel.CallbackTask() {
            public final void execute(BgDataModel.Callbacks callbacks) {
                callbacks.bindItems(Collections.singletonList(ItemInfo.this), false);
            }
        });
        ModelVerifier modelVerifier = new ModelVerifier();
        Executors.MODEL_EXECUTOR.execute(new Runnable(itemInfo, contentResolver, new Throwable().getStackTrace(), modelVerifier) {
            public final /* synthetic */ ItemInfo f$1;
            public final /* synthetic */ ContentResolver f$2;
            public final /* synthetic */ StackTraceElement[] f$3;
            public final /* synthetic */ ModelWriter.ModelVerifier f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                ModelWriter.this.lambda$addItemToDatabase$6$ModelWriter(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$addItemToDatabase$6$ModelWriter(ItemInfo itemInfo, ContentResolver contentResolver, StackTraceElement[] stackTraceElementArr, ModelVerifier modelVerifier) {
        ContentWriter contentWriter = new ContentWriter(this.mContext);
        itemInfo.onAddToDatabase(contentWriter);
        contentWriter.put("_id", Integer.valueOf(itemInfo.id));
        contentResolver.insert(LauncherSettings.Favorites.CONTENT_URI, contentWriter.getValues(this.mContext));
        synchronized (this.mBgDataModel) {
            checkItemInfoLocked(itemInfo.id, itemInfo, stackTraceElementArr);
            this.mBgDataModel.addItem(this.mContext, itemInfo, true);
            modelVerifier.verifyModel();
        }
    }

    public void deleteItemFromDatabase(ItemInfo itemInfo, String str) {
        deleteItemsFromDatabase((Collection<? extends ItemInfo>) Arrays.asList(new ItemInfo[]{itemInfo}), str);
    }

    public void deleteItemsFromDatabase(Predicate<ItemInfo> predicate, String str) {
        deleteItemsFromDatabase((Collection<? extends ItemInfo>) (Collection) StreamSupport.stream(this.mBgDataModel.itemsIdMap.spliterator(), false).filter(predicate).collect(Collectors.toList()), str);
    }

    public void deleteItemsFromDatabase(Collection<? extends ItemInfo> collection, String str) {
        ModelVerifier modelVerifier = new ModelVerifier();
        StringBuilder append = new StringBuilder().append("removing items from db ").append((String) collection.stream().map($$Lambda$ModelWriter$CtPgYsrFbBWIKzy0QdlnjZEy0.INSTANCE).collect(Collectors.joining(","))).append(". Reason: [");
        if (TextUtils.isEmpty(str)) {
            str = EnvironmentCompat.MEDIA_UNKNOWN;
        }
        FileLog.d(TAG, append.append(str).append("]").toString());
        notifyDelete(collection);
        enqueueDeleteRunnable(new Runnable(collection, modelVerifier) {
            public final /* synthetic */ Collection f$1;
            public final /* synthetic */ ModelWriter.ModelVerifier f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ModelWriter.this.lambda$deleteItemsFromDatabase$8$ModelWriter(this.f$1, this.f$2);
            }
        });
    }

    static /* synthetic */ String lambda$deleteItemsFromDatabase$7(ItemInfo itemInfo) {
        if (itemInfo.getTargetComponent() == null) {
            return "";
        }
        return itemInfo.getTargetComponent().getPackageName();
    }

    public /* synthetic */ void lambda$deleteItemsFromDatabase$8$ModelWriter(Collection collection, ModelVerifier modelVerifier) {
        Iterator it = collection.iterator();
        while (it.hasNext()) {
            ItemInfo itemInfo = (ItemInfo) it.next();
            this.mContext.getContentResolver().delete(LauncherSettings.Favorites.getContentUri(itemInfo.id), (String) null, (String[]) null);
            this.mBgDataModel.removeItem(this.mContext, itemInfo);
            modelVerifier.verifyModel();
        }
    }

    public void deleteFolderAndContentsFromDatabase(FolderInfo folderInfo) {
        ModelVerifier modelVerifier = new ModelVerifier();
        notifyDelete(Collections.singleton(folderInfo));
        enqueueDeleteRunnable(new Runnable(folderInfo, modelVerifier) {
            public final /* synthetic */ FolderInfo f$1;
            public final /* synthetic */ ModelWriter.ModelVerifier f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ModelWriter.this.lambda$deleteFolderAndContentsFromDatabase$9$ModelWriter(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$deleteFolderAndContentsFromDatabase$9$ModelWriter(FolderInfo folderInfo, ModelVerifier modelVerifier) {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        contentResolver.delete(LauncherSettings.Favorites.CONTENT_URI, "container=" + folderInfo.id, (String[]) null);
        this.mBgDataModel.removeItem(this.mContext, (Iterable<? extends ItemInfo>) folderInfo.contents);
        folderInfo.contents.clear();
        contentResolver.delete(LauncherSettings.Favorites.getContentUri(folderInfo.id), (String) null, (String[]) null);
        this.mBgDataModel.removeItem(this.mContext, folderInfo);
        modelVerifier.verifyModel();
    }

    public void deleteWidgetInfo(LauncherAppWidgetInfo launcherAppWidgetInfo, LauncherAppWidgetHost launcherAppWidgetHost, String str) {
        notifyDelete(Collections.singleton(launcherAppWidgetInfo));
        if (launcherAppWidgetHost != null && !launcherAppWidgetInfo.isCustomWidget() && launcherAppWidgetInfo.isWidgetIdAllocated()) {
            enqueueDeleteRunnable(new Runnable(launcherAppWidgetInfo) {
                public final /* synthetic */ LauncherAppWidgetInfo f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    LauncherAppWidgetHost.this.deleteAppWidgetId(this.f$1.appWidgetId);
                }
            });
        }
        deleteItemFromDatabase(launcherAppWidgetInfo, str);
    }

    private void notifyDelete(Collection<? extends ItemInfo> collection) {
        notifyOtherCallbacks(new LauncherModel.CallbackTask(collection) {
            public final /* synthetic */ Collection f$0;

            {
                this.f$0 = r1;
            }

            public final void execute(BgDataModel.Callbacks callbacks) {
                callbacks.bindWorkspaceComponentsRemoved(ItemInfoMatcher.ofItems(this.f$0));
            }
        });
    }

    public void prepareToUndoDelete() {
        if (!this.mPreparingToUndo) {
            this.mDeleteRunnables.isEmpty();
            this.mDeleteRunnables.clear();
            this.mPreparingToUndo = true;
        }
    }

    private void enqueueDeleteRunnable(Runnable runnable) {
        if (this.mPreparingToUndo) {
            this.mDeleteRunnables.add(runnable);
        } else {
            Executors.MODEL_EXECUTOR.execute(runnable);
        }
    }

    public void commitDelete() {
        this.mPreparingToUndo = false;
        for (Runnable execute : this.mDeleteRunnables) {
            Executors.MODEL_EXECUTOR.execute(execute);
        }
        this.mDeleteRunnables.clear();
    }

    public void abortDelete() {
        this.mPreparingToUndo = false;
        this.mDeleteRunnables.clear();
        this.mModel.forceReload();
    }

    private void notifyOtherCallbacks(LauncherModel.CallbackTask callbackTask) {
        if (this.mOwner != null) {
            this.mUiExecutor.execute(new Runnable(callbackTask) {
                public final /* synthetic */ LauncherModel.CallbackTask f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ModelWriter.this.lambda$notifyOtherCallbacks$12$ModelWriter(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$notifyOtherCallbacks$12$ModelWriter(LauncherModel.CallbackTask callbackTask) {
        for (BgDataModel.Callbacks callbacks : this.mModel.getCallbacks()) {
            if (callbacks != this.mOwner) {
                callbackTask.execute(callbacks);
            }
        }
    }

    private class UpdateItemRunnable extends UpdateItemBaseRunnable {
        private final ItemInfo mItem;
        private final int mItemId;
        private final Supplier<ContentWriter> mWriter;

        UpdateItemRunnable(ItemInfo itemInfo, Supplier<ContentWriter> supplier) {
            super();
            this.mItem = itemInfo;
            this.mWriter = supplier;
            this.mItemId = itemInfo.id;
        }

        public void run() {
            ModelWriter.this.mContext.getContentResolver().update(LauncherSettings.Favorites.getContentUri(this.mItemId), this.mWriter.get().getValues(ModelWriter.this.mContext), (String) null, (String[]) null);
            updateItemArrays(this.mItem, this.mItemId);
        }
    }

    private class UpdateItemsRunnable extends UpdateItemBaseRunnable {
        private final ArrayList<ItemInfo> mItems;
        private final ArrayList<ContentValues> mValues;

        UpdateItemsRunnable(ArrayList<ItemInfo> arrayList, ArrayList<ContentValues> arrayList2) {
            super();
            this.mValues = arrayList2;
            this.mItems = arrayList;
        }

        public void run() {
            ArrayList arrayList = new ArrayList();
            int size = this.mItems.size();
            for (int i = 0; i < size; i++) {
                ItemInfo itemInfo = this.mItems.get(i);
                int i2 = itemInfo.id;
                arrayList.add(ContentProviderOperation.newUpdate(LauncherSettings.Favorites.getContentUri(i2)).withValues(this.mValues.get(i)).build());
                updateItemArrays(itemInfo, i2);
            }
            try {
                ModelWriter.this.mContext.getContentResolver().applyBatch(LauncherProvider.AUTHORITY, arrayList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private abstract class UpdateItemBaseRunnable implements Runnable {
        private final StackTraceElement[] mStackTrace = new Throwable().getStackTrace();
        private final ModelVerifier mVerifier;

        UpdateItemBaseRunnable() {
            this.mVerifier = new ModelVerifier();
        }

        /* access modifiers changed from: protected */
        public void updateItemArrays(ItemInfo itemInfo, int i) {
            synchronized (ModelWriter.this.mBgDataModel) {
                ModelWriter.this.checkItemInfoLocked(i, itemInfo, this.mStackTrace);
                if (!(itemInfo.container == -100 || itemInfo.container == -101 || ModelWriter.this.mBgDataModel.folders.containsKey(itemInfo.container))) {
                    Log.e(ModelWriter.TAG, "item: " + itemInfo + " container being set to: " + itemInfo.container + ", not in the list of folders");
                }
                ItemInfo itemInfo2 = (ItemInfo) ModelWriter.this.mBgDataModel.itemsIdMap.get(i);
                if (itemInfo2 == null || !(itemInfo2.container == -100 || itemInfo2.container == -101)) {
                    ModelWriter.this.mBgDataModel.workspaceItems.remove(itemInfo2);
                } else {
                    int i2 = itemInfo2.itemType;
                    if (i2 == 0 || i2 == 1 || i2 == 2 || i2 == 6) {
                        if (!ModelWriter.this.mBgDataModel.workspaceItems.contains(itemInfo2)) {
                            ModelWriter.this.mBgDataModel.workspaceItems.add(itemInfo2);
                        }
                    }
                }
                this.mVerifier.verifyModel();
            }
        }
    }

    public class ModelVerifier {
        final int startId;

        ModelVerifier() {
            this.startId = ModelWriter.this.mBgDataModel.lastBindId;
        }

        /* access modifiers changed from: package-private */
        public void verifyModel() {
            if (ModelWriter.this.mVerifyChanges && ModelWriter.this.mModel.hasCallbacks()) {
                ModelWriter.this.mUiExecutor.post(new Runnable(ModelWriter.this.mBgDataModel.lastBindId) {
                    public final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        ModelWriter.ModelVerifier.this.lambda$verifyModel$0$ModelWriter$ModelVerifier(this.f$1);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$verifyModel$0$ModelWriter$ModelVerifier(int i) {
            if (ModelWriter.this.mBgDataModel.lastBindId <= i && i != this.startId) {
                ModelWriter.this.mModel.rebindCallbacks();
            }
        }
    }
}
