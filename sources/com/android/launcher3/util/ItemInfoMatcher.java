package com.android.launcher3.util;

import android.content.ComponentName;
import android.os.UserHandle;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.shortcuts.ShortcutKey;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class ItemInfoMatcher {
    private static final ComponentName EMPTY_COMPONENT = new ComponentName("", "");

    static /* synthetic */ boolean lambda$ofUser$0(UserHandle userHandle, ItemInfo itemInfo) {
        return itemInfo != null && itemInfo.user.equals(userHandle);
    }

    public static Predicate<ItemInfo> ofUser(UserHandle userHandle) {
        return new Predicate(userHandle) {
            public final /* synthetic */ UserHandle f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return ItemInfoMatcher.lambda$ofUser$0(this.f$0, (ItemInfo) obj);
            }
        };
    }

    static /* synthetic */ boolean lambda$ofComponents$1(UserHandle userHandle, HashSet hashSet, ItemInfo itemInfo) {
        return itemInfo != null && itemInfo.user.equals(userHandle) && hashSet.contains(getNonNullComponent(itemInfo));
    }

    public static Predicate<ItemInfo> ofComponents(HashSet<ComponentName> hashSet, UserHandle userHandle) {
        return new Predicate(userHandle, hashSet) {
            public final /* synthetic */ UserHandle f$0;
            public final /* synthetic */ HashSet f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final boolean test(Object obj) {
                return ItemInfoMatcher.lambda$ofComponents$1(this.f$0, this.f$1, (ItemInfo) obj);
            }
        };
    }

    static /* synthetic */ boolean lambda$ofPackages$2(UserHandle userHandle, Set set, ItemInfo itemInfo) {
        return itemInfo != null && itemInfo.user.equals(userHandle) && set.contains(getNonNullComponent(itemInfo).getPackageName());
    }

    public static Predicate<ItemInfo> ofPackages(Set<String> set, UserHandle userHandle) {
        return new Predicate(userHandle, set) {
            public final /* synthetic */ UserHandle f$0;
            public final /* synthetic */ Set f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final boolean test(Object obj) {
                return ItemInfoMatcher.lambda$ofPackages$2(this.f$0, this.f$1, (ItemInfo) obj);
            }
        };
    }

    static /* synthetic */ boolean lambda$ofShortcutKeys$3(Set set, ItemInfo itemInfo) {
        return itemInfo != null && itemInfo.itemType == 6 && set.contains(ShortcutKey.fromItemInfo(itemInfo));
    }

    public static Predicate<ItemInfo> ofShortcutKeys(Set<ShortcutKey> set) {
        return new Predicate(set) {
            public final /* synthetic */ Set f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return ItemInfoMatcher.lambda$ofShortcutKeys$3(this.f$0, (ItemInfo) obj);
            }
        };
    }

    public static Predicate<ItemInfo> forFolderMatch(Predicate<ItemInfo> predicate) {
        return new Predicate(predicate) {
            public final /* synthetic */ Predicate f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return ItemInfoMatcher.lambda$forFolderMatch$4(this.f$0, (ItemInfo) obj);
            }
        };
    }

    static /* synthetic */ boolean lambda$forFolderMatch$4(Predicate predicate, ItemInfo itemInfo) {
        return (itemInfo instanceof FolderInfo) && ((FolderInfo) itemInfo).contents.stream().anyMatch(predicate);
    }

    static /* synthetic */ boolean lambda$ofItemIds$5(IntSet intSet, ItemInfo itemInfo) {
        return itemInfo != null && intSet.contains(itemInfo.id);
    }

    public static Predicate<ItemInfo> ofItemIds(IntSet intSet) {
        return new Predicate() {
            public final boolean test(Object obj) {
                return ItemInfoMatcher.lambda$ofItemIds$5(IntSet.this, (ItemInfo) obj);
            }
        };
    }

    public static Predicate<ItemInfo> ofItems(Collection<? extends ItemInfo> collection) {
        IntSet intSet = new IntSet();
        collection.forEach(new Consumer() {
            public final void accept(Object obj) {
                IntSet.this.add(((ItemInfo) obj).id);
            }
        });
        return ofItemIds(intSet);
    }

    private static ComponentName getNonNullComponent(ItemInfo itemInfo) {
        ComponentName targetComponent = itemInfo.getTargetComponent();
        return targetComponent != null ? targetComponent : EMPTY_COMPONENT;
    }
}
