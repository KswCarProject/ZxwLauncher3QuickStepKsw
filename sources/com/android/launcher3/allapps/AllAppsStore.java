package com.android.launcher3.allapps;

import android.os.UserHandle;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.ItemInfoWithIcon;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.PackageUserKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class AllAppsStore {
    public static final int DEFER_UPDATES_NEXT_DRAW = 1;
    public static final int DEFER_UPDATES_TEST = 2;
    private AppInfo[] mApps = AppInfo.EMPTY_ARRAY;
    private int mDeferUpdatesFlags = 0;
    private final ArrayList<ViewGroup> mIconContainers = new ArrayList<>();
    private int mModelFlags;
    private AppInfo mTempInfo = new AppInfo();
    private PackageUserKey mTempKey = new PackageUserKey((String) null, (UserHandle) null);
    private final List<OnUpdateListener> mUpdateListeners = new CopyOnWriteArrayList();
    private boolean mUpdatePending = false;

    public interface OnUpdateListener {
        void onAppsUpdated();
    }

    public AppInfo[] getApps() {
        return this.mApps;
    }

    public void setApps(AppInfo[] appInfoArr, int i) {
        this.mApps = appInfoArr;
        this.mModelFlags = i;
        notifyUpdate();
    }

    public boolean hasModelFlag(int i) {
        return (i & this.mModelFlags) != 0;
    }

    public AppInfo getApp(ComponentKey componentKey) {
        this.mTempInfo.componentName = componentKey.componentName;
        this.mTempInfo.user = componentKey.user;
        int binarySearch = Arrays.binarySearch(this.mApps, this.mTempInfo, AppInfo.COMPONENT_KEY_COMPARATOR);
        if (binarySearch < 0) {
            return null;
        }
        return this.mApps[binarySearch];
    }

    public void enableDeferUpdates(int i) {
        this.mDeferUpdatesFlags = i | this.mDeferUpdatesFlags;
    }

    public void disableDeferUpdates(int i) {
        int i2 = (~i) & this.mDeferUpdatesFlags;
        this.mDeferUpdatesFlags = i2;
        if (i2 == 0 && this.mUpdatePending) {
            notifyUpdate();
            this.mUpdatePending = false;
        }
    }

    public void disableDeferUpdatesSilently(int i) {
        this.mDeferUpdatesFlags = (~i) & this.mDeferUpdatesFlags;
    }

    public int getDeferUpdatesFlags() {
        return this.mDeferUpdatesFlags;
    }

    private void notifyUpdate() {
        if (this.mDeferUpdatesFlags != 0) {
            this.mUpdatePending = true;
            return;
        }
        for (OnUpdateListener onAppsUpdated : this.mUpdateListeners) {
            onAppsUpdated.onAppsUpdated();
        }
    }

    public void addUpdateListener(OnUpdateListener onUpdateListener) {
        this.mUpdateListeners.add(onUpdateListener);
    }

    public void removeUpdateListener(OnUpdateListener onUpdateListener) {
        this.mUpdateListeners.remove(onUpdateListener);
    }

    public void registerIconContainer(ViewGroup viewGroup) {
        if (viewGroup != null && !this.mIconContainers.contains(viewGroup)) {
            this.mIconContainers.add(viewGroup);
        }
    }

    public void unregisterIconContainer(ViewGroup viewGroup) {
        this.mIconContainers.remove(viewGroup);
    }

    public void updateNotificationDots(Predicate<PackageUserKey> predicate) {
        updateAllIcons(new Consumer(predicate) {
            public final /* synthetic */ Predicate f$1;

            {
                this.f$1 = r2;
            }

            public final void accept(Object obj) {
                AllAppsStore.this.lambda$updateNotificationDots$0$AllAppsStore(this.f$1, (BubbleTextView) obj);
            }
        });
    }

    public /* synthetic */ void lambda$updateNotificationDots$0$AllAppsStore(Predicate predicate, BubbleTextView bubbleTextView) {
        if (bubbleTextView.getTag() instanceof ItemInfo) {
            ItemInfo itemInfo = (ItemInfo) bubbleTextView.getTag();
            if (this.mTempKey.updateFromItemInfo(itemInfo) && predicate.test(this.mTempKey)) {
                bubbleTextView.applyDotState(itemInfo, true);
            }
        }
    }

    public void updateProgressBar(AppInfo appInfo) {
        updateAllIcons(new Consumer() {
            public final void accept(Object obj) {
                AllAppsStore.lambda$updateProgressBar$1(AppInfo.this, (BubbleTextView) obj);
            }
        });
    }

    static /* synthetic */ void lambda$updateProgressBar$1(AppInfo appInfo, BubbleTextView bubbleTextView) {
        if (bubbleTextView.getTag() != appInfo) {
            return;
        }
        if ((appInfo.runtimeStatusFlags & ItemInfoWithIcon.FLAG_SHOW_DOWNLOAD_PROGRESS_MASK) == 0) {
            bubbleTextView.applyFromApplicationInfo(appInfo);
        } else {
            bubbleTextView.applyProgressLevel();
        }
    }

    private void updateAllIcons(Consumer<BubbleTextView> consumer) {
        for (int size = this.mIconContainers.size() - 1; size >= 0; size--) {
            ViewGroup viewGroup = this.mIconContainers.get(size);
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (childAt instanceof BubbleTextView) {
                    consumer.accept((BubbleTextView) childAt);
                }
            }
        }
    }
}
