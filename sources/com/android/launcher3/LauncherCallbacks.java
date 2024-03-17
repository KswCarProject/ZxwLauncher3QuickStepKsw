package com.android.launcher3;

import android.os.Bundle;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public interface LauncherCallbacks {
    void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);

    void onCreate(Bundle bundle);

    void onHomeIntent(boolean z);

    boolean startSearch(String str, boolean z, Bundle bundle);
}
