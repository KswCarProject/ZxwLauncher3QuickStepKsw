package com.android.launcher3.model.data;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Process;
import android.os.UserHandle;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.logger.LauncherAtom;
import java.util.function.Supplier;

public class SearchActionItemInfo extends ItemInfoWithIcon implements WorkspaceItemFactory {
    public static final int FLAG_ALLOW_PINNING = 64;
    public static final int FLAG_BADGE_WITH_COMPONENT_NAME = 32;
    public static final int FLAG_BADGE_WITH_PACKAGE = 8;
    public static final int FLAG_PRIMARY_ICON_FROM_TITLE = 16;
    public static final int FLAG_SEARCH_IN_APP = 128;
    public static final int FLAG_SHOULD_START = 2;
    public static final int FLAG_SHOULD_START_FOR_RESULT = 6;
    private String mFallbackPackageName;
    private int mFlags = 0;
    private Icon mIcon;
    private Intent mIntent;
    private boolean mIsPersonalTitle;
    private PendingIntent mPendingIntent;

    public SearchActionItemInfo(Icon icon, String str, UserHandle userHandle, CharSequence charSequence, boolean z) {
        this.mIsPersonalTitle = z;
        this.itemType = 7;
        this.user = userHandle == null ? Process.myUserHandle() : userHandle;
        this.title = charSequence;
        this.container = -200;
        this.mFallbackPackageName = str;
        this.mIcon = icon;
    }

    private SearchActionItemInfo(SearchActionItemInfo searchActionItemInfo) {
        super(searchActionItemInfo);
    }

    public void copyFrom(ItemInfo itemInfo) {
        super.copyFrom(itemInfo);
        SearchActionItemInfo searchActionItemInfo = (SearchActionItemInfo) itemInfo;
        this.mFallbackPackageName = searchActionItemInfo.mFallbackPackageName;
        this.mIcon = searchActionItemInfo.mIcon;
        this.mFlags = searchActionItemInfo.mFlags;
        this.mIsPersonalTitle = searchActionItemInfo.mIsPersonalTitle;
    }

    public boolean hasFlags(int i) {
        return (i & this.mFlags) != 0;
    }

    public void setFlags(int i) {
        this.mFlags = i | this.mFlags;
    }

    public Intent getIntent() {
        return this.mIntent;
    }

    public void setIntent(Intent intent) {
        if (this.mPendingIntent == null || intent == null) {
            this.mIntent = intent;
            return;
        }
        throw new RuntimeException("SearchActionItemInfo can only have either an Intent or a PendingIntent");
    }

    public PendingIntent getPendingIntent() {
        return this.mPendingIntent;
    }

    public void setPendingIntent(PendingIntent pendingIntent) {
        if (this.mIntent == null || pendingIntent == null) {
            this.mPendingIntent = pendingIntent;
            return;
        }
        throw new RuntimeException("SearchActionItemInfo can only have either an Intent or a PendingIntent");
    }

    public Icon getIcon() {
        return this.mIcon;
    }

    public ItemInfoWithIcon clone() {
        return new SearchActionItemInfo(this);
    }

    public LauncherAtom.ItemInfo buildProto(FolderInfo folderInfo) {
        LauncherAtom.SearchActionItem.Builder packageName = LauncherAtom.SearchActionItem.newBuilder().setPackageName(this.mFallbackPackageName);
        if (!this.mIsPersonalTitle) {
            packageName.setTitle(this.title.toString());
        }
        return (LauncherAtom.ItemInfo) getDefaultItemInfoBuilder().setSearchActionItem(packageName).setContainerInfo(getContainerInfo()).build();
    }

    public boolean supportsPinning() {
        return hasFlags(64) && getIntentPackageName() != null;
    }

    public WorkspaceItemInfo makeWorkspaceItem(Context context) {
        WorkspaceItemInfo workspaceItemInfo = new WorkspaceItemInfo();
        workspaceItemInfo.title = this.title;
        workspaceItemInfo.bitmap = this.bitmap;
        workspaceItemInfo.intent = this.mIntent;
        if (hasFlags(6)) {
            workspaceItemInfo.options |= 16;
        }
        LauncherAppState instance = LauncherAppState.getInstance(context);
        instance.getModel().updateAndBindWorkspaceItem(new Supplier(instance, workspaceItemInfo) {
            public final /* synthetic */ LauncherAppState f$1;
            public final /* synthetic */ WorkspaceItemInfo f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final Object get() {
                return SearchActionItemInfo.this.lambda$makeWorkspaceItem$0$SearchActionItemInfo(this.f$1, this.f$2);
            }
        });
        return workspaceItemInfo;
    }

    public /* synthetic */ WorkspaceItemInfo lambda$makeWorkspaceItem$0$SearchActionItemInfo(LauncherAppState launcherAppState, WorkspaceItemInfo workspaceItemInfo) {
        PackageItemInfo packageItemInfo = new PackageItemInfo(getIntentPackageName(), this.user);
        launcherAppState.getIconCache().getTitleAndIconForApp(packageItemInfo, false);
        workspaceItemInfo.bitmap = workspaceItemInfo.bitmap.withBadgeInfo(packageItemInfo.bitmap);
        return workspaceItemInfo;
    }

    private String getIntentPackageName() {
        Intent intent = this.mIntent;
        if (intent == null) {
            return null;
        }
        if (intent.getPackage() != null) {
            return this.mIntent.getPackage();
        }
        return this.mFallbackPackageName;
    }
}
