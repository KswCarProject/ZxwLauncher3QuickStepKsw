package com.android.launcher3.model;

import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;

public class LoaderMemoryLogger {
    private static final String TAG = "LoaderMemoryLogger";
    private final ArrayList<LogEntry> mLogEntries = new ArrayList<>();

    protected LoaderMemoryLogger() {
    }

    /* access modifiers changed from: protected */
    public void addLog(int i, String str, String str2) {
        addLog(i, str, str2, (Exception) null);
    }

    /* access modifiers changed from: protected */
    public void addLog(int i, String str, String str2, Exception exc) {
        switch (i) {
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                this.mLogEntries.add(new LogEntry(i, str, str2, exc));
                return;
            default:
                throw new IllegalArgumentException("Invalid log level provided: " + i);
        }
    }

    /* access modifiers changed from: protected */
    public void clearLogs() {
        this.mLogEntries.clear();
    }

    /* access modifiers changed from: protected */
    public void printLogs() {
        String str;
        Iterator<LogEntry> it = this.mLogEntries.iterator();
        while (it.hasNext()) {
            LogEntry next = it.next();
            String format = String.format("%s: %s", new Object[]{TAG, next.mLogTag});
            if (next.mStackStrace == null) {
                str = next.mLogString;
            } else {
                str = String.format("%s\n%s", new Object[]{next.mLogString, Log.getStackTraceString(next.mStackStrace)});
            }
            Log.println(next.mLogLevel, format, str);
        }
        clearLogs();
    }

    private static class LogEntry {
        protected final int mLogLevel;
        protected final String mLogString;
        protected final String mLogTag;
        protected final Exception mStackStrace;

        protected LogEntry(int i, String str, String str2, Exception exc) {
            this.mLogLevel = i;
            this.mLogTag = str;
            this.mLogString = str2;
            this.mStackStrace = exc;
        }
    }
}
