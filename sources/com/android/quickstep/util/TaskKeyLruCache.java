package com.android.quickstep.util;

import android.util.Log;
import com.android.quickstep.util.TaskKeyLruCache;
import com.android.systemui.shared.recents.model.Task;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

public class TaskKeyLruCache<V> {
    private final MyLinkedHashMap<V> mMap;

    public TaskKeyLruCache(int i) {
        this.mMap = new MyLinkedHashMap<>(i);
    }

    public synchronized void evictAll() {
        this.mMap.clear();
    }

    public synchronized void remove(Task.TaskKey taskKey) {
        this.mMap.remove(Integer.valueOf(taskKey.id));
    }

    public synchronized void removeAll(Predicate<Task.TaskKey> predicate) {
        this.mMap.entrySet().removeIf(new Predicate(predicate) {
            public final /* synthetic */ Predicate f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return this.f$0.test(((TaskKeyLruCache.Entry) ((Map.Entry) obj).getValue()).mKey);
            }
        });
    }

    public synchronized V getAndInvalidateIfModified(Task.TaskKey taskKey) {
        Entry entry = (Entry) this.mMap.get(Integer.valueOf(taskKey.id));
        if (entry != null && entry.mKey.windowingMode == taskKey.windowingMode && entry.mKey.lastActiveTime == taskKey.lastActiveTime) {
            return entry.mValue;
        }
        remove(taskKey);
        return null;
    }

    public final synchronized void put(Task.TaskKey taskKey, V v) {
        if (taskKey == null || v == null) {
            Log.e("TaskKeyCache", "Unexpected null key or value: " + taskKey + ", " + v);
        } else {
            this.mMap.put(Integer.valueOf(taskKey.id), new Entry(taskKey, v));
        }
    }

    public synchronized void updateIfAlreadyInCache(int i, V v) {
        Entry entry = (Entry) this.mMap.get(Integer.valueOf(i));
        if (entry != null) {
            entry.mValue = v;
        }
    }

    private static class Entry<V> {
        final Task.TaskKey mKey;
        V mValue;

        Entry(Task.TaskKey taskKey, V v) {
            this.mKey = taskKey;
            this.mValue = v;
        }

        public int hashCode() {
            return this.mKey.id;
        }
    }

    private static class MyLinkedHashMap<V> extends LinkedHashMap<Integer, Entry<V>> {
        private final int mMaxSize;

        MyLinkedHashMap(int i) {
            super(0, 0.75f, true);
            this.mMaxSize = i;
        }

        /* access modifiers changed from: protected */
        public boolean removeEldestEntry(Map.Entry<Integer, Entry<V>> entry) {
            return size() > this.mMaxSize;
        }
    }
}
