package com.android.launcher3.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Process;
import android.util.Log;
import com.android.launcher3.Utilities;
import com.android.launcher3.icons.BitmapInfo;
import com.android.launcher3.icons.LauncherIcons;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.util.IntArray;
import com.android.launcher3.util.IntSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

public class ModelUtils {
    private static final String TAG = "ModelUtils";

    public static <T extends ItemInfo> void filterCurrentWorkspaceItems(IntSet intSet, ArrayList<T> arrayList, ArrayList<T> arrayList2, ArrayList<T> arrayList3) {
        arrayList.removeIf($$Lambda$ModelUtils$mAhh12hDBSVR_m3mqIPeWauRCM.INSTANCE);
        IntSet intSet2 = new IntSet();
        Collections.sort(arrayList, $$Lambda$ModelUtils$b6rGSqQCp07jIFgI4tObSfJrI6M.INSTANCE);
        Iterator<T> it = arrayList.iterator();
        while (it.hasNext()) {
            ItemInfo itemInfo = (ItemInfo) it.next();
            if (itemInfo.container == -100) {
                if (TestProtocol.sDebugTracing) {
                    Log.d(TestProtocol.NULL_INT_SET, "filterCurrentWorkspaceItems: " + intSet);
                }
                if (intSet.contains(itemInfo.screenId)) {
                    arrayList2.add(itemInfo);
                    intSet2.add(itemInfo.id);
                } else {
                    arrayList3.add(itemInfo);
                }
            } else if (itemInfo.container == -101) {
                arrayList2.add(itemInfo);
                intSet2.add(itemInfo.id);
            } else if (intSet2.contains(itemInfo.container)) {
                arrayList2.add(itemInfo);
                intSet2.add(itemInfo.id);
            } else {
                arrayList3.add(itemInfo);
            }
        }
    }

    public static IntArray getMissingHotseatRanks(List<ItemInfo> list, int i) {
        IntSet intSet = new IntSet();
        list.stream().filter($$Lambda$ModelUtils$zR9mAH1J0r6Z52dXS3hAHnxYCYQ.INSTANCE).forEach(new Consumer() {
            public final void accept(Object obj) {
                IntSet.this.add(((ItemInfo) obj).screenId);
            }
        });
        IntArray intArray = new IntArray(i);
        IntStream filter = IntStream.range(0, i).filter(new IntPredicate() {
            public final boolean test(int i) {
                return ModelUtils.lambda$getMissingHotseatRanks$3(IntSet.this, i);
            }
        });
        Objects.requireNonNull(intArray);
        filter.forEach(new IntConsumer() {
            public final void accept(int i) {
                IntArray.this.add(i);
            }
        });
        return intArray;
    }

    static /* synthetic */ boolean lambda$getMissingHotseatRanks$1(ItemInfo itemInfo) {
        return itemInfo.container == -101;
    }

    static /* synthetic */ boolean lambda$getMissingHotseatRanks$3(IntSet intSet, int i) {
        return !intSet.contains(i);
    }

    public static WorkspaceItemInfo fromLegacyShortcutIntent(Context context, Intent intent) {
        BitmapInfo bitmapInfo;
        if (!Utilities.isValidExtraType(intent, "android.intent.extra.shortcut.INTENT", Intent.class) || !Utilities.isValidExtraType(intent, "android.intent.extra.shortcut.ICON_RESOURCE", Intent.ShortcutIconResource.class) || !Utilities.isValidExtraType(intent, "android.intent.extra.shortcut.ICON", Bitmap.class)) {
            Log.e(TAG, "Invalid install shortcut intent");
            return null;
        }
        Intent intent2 = (Intent) intent.getParcelableExtra("android.intent.extra.shortcut.INTENT");
        String stringExtra = intent.getStringExtra("android.intent.extra.shortcut.NAME");
        if (intent2 == null || stringExtra == null) {
            Log.e(TAG, "Invalid install shortcut intent");
            return null;
        }
        WorkspaceItemInfo workspaceItemInfo = new WorkspaceItemInfo();
        workspaceItemInfo.user = Process.myUserHandle();
        LauncherIcons obtain = LauncherIcons.obtain(context);
        try {
            Bitmap bitmap = (Bitmap) intent.getParcelableExtra("android.intent.extra.shortcut.ICON");
            if (bitmap != null) {
                bitmapInfo = obtain.createIconBitmap(bitmap);
            } else {
                workspaceItemInfo.iconResource = (Intent.ShortcutIconResource) intent.getParcelableExtra("android.intent.extra.shortcut.ICON_RESOURCE");
                bitmapInfo = workspaceItemInfo.iconResource != null ? obtain.createIconBitmap(workspaceItemInfo.iconResource) : null;
            }
            if (obtain != null) {
                obtain.close();
            }
            if (bitmapInfo == null) {
                Log.e(TAG, "Invalid icon by the app");
                return null;
            }
            workspaceItemInfo.bitmap = bitmapInfo;
            String trim = Utilities.trim(stringExtra);
            workspaceItemInfo.title = trim;
            workspaceItemInfo.contentDescription = trim;
            workspaceItemInfo.intent = intent2;
            return workspaceItemInfo;
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }
}
