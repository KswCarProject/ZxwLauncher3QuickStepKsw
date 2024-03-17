package com.android.launcher3.secondarydisplay;

import android.content.ComponentName;
import android.content.SharedPreferences;
import android.os.Process;
import android.os.UserHandle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.R;
import com.android.launcher3.allapps.AllAppsStore;
import com.android.launcher3.allapps.AppInfoComparator;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.pm.UserCache;
import com.android.launcher3.popup.SystemShortcut;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.Executors;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PinnedAppsAdapter extends BaseAdapter implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String PINNED_APPS_KEY = "pinned_apps";
    private final AllAppsStore mAllAppsList;
    private final AppInfoComparator mAppNameComparator;
    private final ArrayList<AppInfo> mItems = new ArrayList<>();
    /* access modifiers changed from: private */
    public final SecondaryDisplayLauncher mLauncher;
    private final View.OnClickListener mOnClickListener;
    private final View.OnLongClickListener mOnLongClickListener;
    /* access modifiers changed from: private */
    public final Set<ComponentKey> mPinnedApps = new HashSet();
    private final SharedPreferences mPrefs;

    public long getItemId(int i) {
        return (long) i;
    }

    public PinnedAppsAdapter(SecondaryDisplayLauncher secondaryDisplayLauncher, AllAppsStore allAppsStore, View.OnLongClickListener onLongClickListener) {
        this.mLauncher = secondaryDisplayLauncher;
        this.mOnClickListener = secondaryDisplayLauncher.getItemOnClickListener();
        this.mOnLongClickListener = onLongClickListener;
        this.mAllAppsList = allAppsStore;
        this.mPrefs = secondaryDisplayLauncher.getSharedPreferences(PINNED_APPS_KEY, 0);
        this.mAppNameComparator = new AppInfoComparator(secondaryDisplayLauncher);
        allAppsStore.addUpdateListener(new AllAppsStore.OnUpdateListener() {
            public final void onAppsUpdated() {
                PinnedAppsAdapter.this.createFilteredAppsList();
            }
        });
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
        if (PINNED_APPS_KEY.equals(str)) {
            Executors.MODEL_EXECUTOR.submit(new Runnable(sharedPreferences, str) {
                public final /* synthetic */ SharedPreferences f$1;
                public final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    PinnedAppsAdapter.this.lambda$onSharedPreferenceChanged$1$PinnedAppsAdapter(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$onSharedPreferenceChanged$1$PinnedAppsAdapter(SharedPreferences sharedPreferences, String str) {
        Executors.MAIN_EXECUTOR.submit(new Runnable((Set) sharedPreferences.getStringSet(str, Collections.emptySet()).stream().map(new Function() {
            public final Object apply(Object obj) {
                return PinnedAppsAdapter.this.parseComponentKey((String) obj);
            }
        }).filter($$Lambda$PinnedAppsAdapter$1orkdRGvdVM6GVglQyGzM7beo.INSTANCE).collect(Collectors.toSet())) {
            public final /* synthetic */ Set f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                PinnedAppsAdapter.this.lambda$onSharedPreferenceChanged$0$PinnedAppsAdapter(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$onSharedPreferenceChanged$0$PinnedAppsAdapter(Set set) {
        this.mPinnedApps.clear();
        this.mPinnedApps.addAll(set);
        createFilteredAppsList();
    }

    public int getCount() {
        return this.mItems.size();
    }

    public AppInfo getItem(int i) {
        return this.mItems.get(i);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        BubbleTextView bubbleTextView;
        if (view instanceof BubbleTextView) {
            bubbleTextView = (BubbleTextView) view;
        } else {
            bubbleTextView = (BubbleTextView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.app_icon, viewGroup, false);
            bubbleTextView.setOnClickListener(this.mOnClickListener);
            bubbleTextView.setOnLongClickListener(this.mOnLongClickListener);
            bubbleTextView.setLongPressTimeoutFactor(1.0f);
            int i2 = this.mLauncher.getDeviceProfile().edgeMarginPx;
            bubbleTextView.setPadding(i2, i2, i2, i2);
        }
        bubbleTextView.applyFromApplicationInfo(this.mItems.get(i));
        return bubbleTextView;
    }

    /* access modifiers changed from: private */
    public void createFilteredAppsList() {
        this.mItems.clear();
        Stream stream = this.mPinnedApps.stream();
        AllAppsStore allAppsStore = this.mAllAppsList;
        Objects.requireNonNull(allAppsStore);
        Stream filter = stream.map(new Function() {
            public final Object apply(Object obj) {
                return AllAppsStore.this.getApp((ComponentKey) obj);
            }
        }).filter($$Lambda$PinnedAppsAdapter$nZG1jnOxUsmR5NjHtDiBKtfyzo.INSTANCE);
        ArrayList<AppInfo> arrayList = this.mItems;
        Objects.requireNonNull(arrayList);
        filter.forEach(new Consumer(arrayList) {
            public final /* synthetic */ ArrayList f$0;

            {
                this.f$0 = r1;
            }

            public final void accept(Object obj) {
                boolean unused = this.f$0.add((AppInfo) obj);
            }
        });
        this.mItems.sort(this.mAppNameComparator);
        notifyDataSetChanged();
    }

    public void init() {
        this.mPrefs.registerOnSharedPreferenceChangeListener(this);
        onSharedPreferenceChanged(this.mPrefs, PINNED_APPS_KEY);
    }

    public void destroy() {
        this.mPrefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    /* access modifiers changed from: private */
    public void update(ItemInfo itemInfo, Function<ComponentKey, Boolean> function) {
        if (function.apply(new ComponentKey(itemInfo.getTargetComponent(), itemInfo.user)).booleanValue()) {
            createFilteredAppsList();
            Executors.MODEL_EXECUTOR.submit(new Runnable(new HashSet(this.mPinnedApps)) {
                public final /* synthetic */ Set f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    PinnedAppsAdapter.this.lambda$update$2$PinnedAppsAdapter(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$update$2$PinnedAppsAdapter(Set set) {
        this.mPrefs.edit().putStringSet(PINNED_APPS_KEY, (Set) set.stream().map(new Function() {
            public final Object apply(Object obj) {
                return PinnedAppsAdapter.this.encode((ComponentKey) obj);
            }
        }).collect(Collectors.toSet())).apply();
    }

    /* access modifiers changed from: private */
    public ComponentKey parseComponentKey(String str) {
        UserHandle userHandle;
        try {
            String[] split = str.split("#");
            if (split.length > 2) {
                userHandle = UserCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mLauncher).getUserForSerialNumber(Long.parseLong(split[2]));
            } else {
                userHandle = Process.myUserHandle();
            }
            return new ComponentKey(ComponentName.unflattenFromString(split[0]), userHandle);
        } catch (Exception unused) {
            return null;
        }
    }

    /* access modifiers changed from: private */
    public String encode(ComponentKey componentKey) {
        return componentKey.componentName.flattenToShortString() + "#" + UserCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mLauncher).getSerialNumberForUser(componentKey.user);
    }

    public SystemShortcut getSystemShortcut(ItemInfo itemInfo, View view) {
        return new PinUnPinShortcut(this.mLauncher, itemInfo, view, this.mPinnedApps.contains(new ComponentKey(itemInfo.getTargetComponent(), itemInfo.user)));
    }

    private class PinUnPinShortcut extends SystemShortcut<SecondaryDisplayLauncher> {
        private final boolean mIsPinned;

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        PinUnPinShortcut(SecondaryDisplayLauncher secondaryDisplayLauncher, ItemInfo itemInfo, View view, boolean z) {
            super(z ? R.drawable.ic_remove_no_shadow : R.drawable.ic_pin, z ? R.string.remove_drop_target_label : R.string.action_add_to_workspace, secondaryDisplayLauncher, itemInfo, view);
            this.mIsPinned = z;
        }

        public void onClick(View view) {
            if (this.mIsPinned) {
                PinnedAppsAdapter pinnedAppsAdapter = PinnedAppsAdapter.this;
                ItemInfo itemInfo = this.mItemInfo;
                Set access$000 = PinnedAppsAdapter.this.mPinnedApps;
                Objects.requireNonNull(access$000);
                pinnedAppsAdapter.update(itemInfo, new Function(access$000) {
                    public final /* synthetic */ Set f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final Object apply(Object obj) {
                        return Boolean.valueOf(this.f$0.remove((ComponentKey) obj));
                    }
                });
            } else {
                PinnedAppsAdapter pinnedAppsAdapter2 = PinnedAppsAdapter.this;
                ItemInfo itemInfo2 = this.mItemInfo;
                Set access$0002 = PinnedAppsAdapter.this.mPinnedApps;
                Objects.requireNonNull(access$0002);
                pinnedAppsAdapter2.update(itemInfo2, new Function(access$0002) {
                    public final /* synthetic */ Set f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final Object apply(Object obj) {
                        return Boolean.valueOf(this.f$0.add((ComponentKey) obj));
                    }
                });
            }
            AbstractFloatingView.closeAllOpenViews(PinnedAppsAdapter.this.mLauncher);
        }
    }
}
