package com.android.launcher3.logging;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class EventLogArray {
    private static final int TYPE_BOOL_FALSE = 4;
    private static final int TYPE_BOOL_TRUE = 3;
    private static final int TYPE_FLOAT = 1;
    private static final int TYPE_INTEGER = 2;
    private static final int TYPE_ONE_OFF = 0;
    private final EventEntry[] logs;
    private int mLogId;
    private final String name;
    private int nextIndex = 0;

    static /* synthetic */ EventEntry lambda$clear$0(int i) {
        return null;
    }

    public EventLogArray(String str, int i) {
        this.name = str;
        this.logs = new EventEntry[i];
    }

    public void addLog(String str) {
        addLog(0, str, 0.0f);
    }

    public void addLog(String str, int i) {
        addLog(2, str, (float) i);
    }

    public void addLog(String str, boolean z) {
        addLog(z ? 3 : 4, str, 0.0f);
    }

    private void addLog(int i, String str, float f) {
        int i2 = this.nextIndex;
        EventEntry[] eventEntryArr = this.logs;
        int length = ((eventEntryArr.length + i2) - 1) % eventEntryArr.length;
        int length2 = ((i2 + eventEntryArr.length) - 2) % eventEntryArr.length;
        if (!isEntrySame(eventEntryArr[length], i, str) || !isEntrySame(this.logs[length2], i, str)) {
            EventEntry[] eventEntryArr2 = this.logs;
            int i3 = this.nextIndex;
            if (eventEntryArr2[i3] == null) {
                eventEntryArr2[i3] = new EventEntry();
            }
            this.logs[this.nextIndex].update(i, str, f, this.mLogId);
            this.nextIndex = (this.nextIndex + 1) % this.logs.length;
            return;
        }
        this.logs[length].update(i, str, f, this.mLogId);
        EventEntry.access$008(this.logs[length2]);
    }

    public void clear() {
        Arrays.setAll(this.logs, $$Lambda$EventLogArray$rbMJNvq9ZP8CRcMigRnsnisDA.INSTANCE);
    }

    public void dump(String str, PrintWriter printWriter) {
        printWriter.println(str + "EventLog (" + this.name + ") history:");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("  HH:mm:ss.SSSZ  ", Locale.US);
        Date date = new Date();
        int i = 0;
        while (true) {
            EventEntry[] eventEntryArr = this.logs;
            if (i < eventEntryArr.length) {
                EventEntry eventEntry = eventEntryArr[(((this.nextIndex + eventEntryArr.length) - i) - 1) % eventEntryArr.length];
                if (eventEntry != null) {
                    date.setTime(eventEntry.time);
                    StringBuilder append = new StringBuilder(str).append(simpleDateFormat.format(date)).append(eventEntry.event);
                    int access$400 = eventEntry.type;
                    if (access$400 == 1) {
                        append.append(": ").append(eventEntry.extras);
                    } else if (access$400 == 2) {
                        append.append(": ").append((int) eventEntry.extras);
                    } else if (access$400 == 3) {
                        append.append(": true");
                    } else if (access$400 == 4) {
                        append.append(": false");
                    }
                    if (eventEntry.duplicateCount > 0) {
                        append.append(" & ").append(eventEntry.duplicateCount).append(" similar events");
                    }
                    append.append(" traceId: ").append(eventEntry.traceId);
                    printWriter.println(append);
                }
                i++;
            } else {
                return;
            }
        }
    }

    public int generateAndSetLogId() {
        int nextInt = new Random().nextInt(900) + 100;
        this.mLogId = nextInt;
        return nextInt;
    }

    private boolean isEntrySame(EventEntry eventEntry, int i, String str) {
        return eventEntry != null && eventEntry.type == i && eventEntry.event.equals(str);
    }

    private static class EventEntry {
        /* access modifiers changed from: private */
        public int duplicateCount;
        /* access modifiers changed from: private */
        public String event;
        /* access modifiers changed from: private */
        public float extras;
        /* access modifiers changed from: private */
        public long time;
        /* access modifiers changed from: private */
        public int traceId;
        /* access modifiers changed from: private */
        public int type;

        private EventEntry() {
        }

        static /* synthetic */ int access$008(EventEntry eventEntry) {
            int i = eventEntry.duplicateCount;
            eventEntry.duplicateCount = i + 1;
            return i;
        }

        public void update(int i, String str, float f, int i2) {
            this.type = i;
            this.event = str;
            this.extras = f;
            this.traceId = i2;
            this.time = System.currentTimeMillis();
            this.duplicateCount = 0;
        }
    }
}
