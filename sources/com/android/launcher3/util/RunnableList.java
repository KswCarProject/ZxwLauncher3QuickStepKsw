package com.android.launcher3.util;

import java.util.ArrayList;

public class RunnableList {
    private boolean mDestroyed = false;
    private ArrayList<Runnable> mList = null;

    public void add(Runnable runnable) {
        if (runnable != null) {
            if (this.mDestroyed) {
                runnable.run();
                return;
            }
            if (this.mList == null) {
                this.mList = new ArrayList<>();
            }
            this.mList.add(runnable);
        }
    }

    public void executeAllAndDestroy() {
        this.mDestroyed = true;
        executeAllAndClear();
    }

    public void executeAllAndClear() {
        ArrayList<Runnable> arrayList = this.mList;
        if (arrayList != null) {
            this.mList = null;
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                arrayList.get(i).run();
            }
        }
    }
}
