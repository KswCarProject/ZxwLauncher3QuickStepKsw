package com.android.launcher3.model.data;

import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import com.android.launcher3.icons.BitmapInfo;
import com.android.launcher3.icons.LauncherIcons;
import com.android.launcher3.model.data.ItemInfoWithIcon;

public class IconRequestInfo<T extends ItemInfoWithIcon> {
    private static final String TAG = "IconRequestInfo";
    public final byte[] iconBlob;
    public final T itemInfo;
    public final LauncherActivityInfo launcherActivityInfo;
    public final String packageName;
    public final String resourceName;
    public final boolean useLowResIcon;

    public IconRequestInfo(T t, LauncherActivityInfo launcherActivityInfo2, boolean z) {
        this(t, launcherActivityInfo2, (String) null, (String) null, (byte[]) null, z);
    }

    public IconRequestInfo(T t, LauncherActivityInfo launcherActivityInfo2, String str, String str2, byte[] bArr, boolean z) {
        this.itemInfo = t;
        this.launcherActivityInfo = launcherActivityInfo2;
        this.packageName = str;
        this.resourceName = str2;
        this.iconBlob = bArr;
        this.useLowResIcon = z;
    }

    public boolean loadWorkspaceIcon(Context context) {
        WorkspaceItemInfo workspaceItemInfo;
        if (this.itemInfo instanceof WorkspaceItemInfo) {
            LauncherIcons obtain = LauncherIcons.obtain(context);
            try {
                T t = this.itemInfo;
                workspaceItemInfo = (WorkspaceItemInfo) t;
                if (t.itemType == 1 && (!TextUtils.isEmpty(this.packageName) || !TextUtils.isEmpty(this.resourceName))) {
                    workspaceItemInfo.iconResource = new Intent.ShortcutIconResource();
                    workspaceItemInfo.iconResource.packageName = this.packageName;
                    workspaceItemInfo.iconResource.resourceName = this.resourceName;
                    BitmapInfo createIconBitmap = obtain.createIconBitmap(workspaceItemInfo.iconResource);
                    if (createIconBitmap != null) {
                        workspaceItemInfo.bitmap = createIconBitmap;
                        if (obtain != null) {
                            obtain.close();
                        }
                        return true;
                    }
                }
                byte[] bArr = this.iconBlob;
                if (bArr == null) {
                    if (obtain != null) {
                        obtain.close();
                    }
                    return false;
                }
                workspaceItemInfo.bitmap = obtain.createIconBitmap(BitmapFactory.decodeByteArray(bArr, 0, bArr.length));
                if (obtain != null) {
                    obtain.close();
                }
                return true;
            } catch (Exception e) {
                Log.e(TAG, "Failed to decode byte array for info " + workspaceItemInfo, e);
                if (obtain != null) {
                    obtain.close();
                }
                return false;
            } catch (Throwable th) {
                if (obtain != null) {
                    try {
                        obtain.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } else {
            throw new IllegalStateException("loadWorkspaceIcon should only be use for a WorkspaceItemInfos: " + this.itemInfo);
        }
    }
}
