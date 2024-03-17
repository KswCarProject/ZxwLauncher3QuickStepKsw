package com.android.launcher3.logging;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.IOUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class FileLog {
    private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance(3, 3);
    protected static final boolean ENABLED = true;
    private static final String FILE_NAME_PREFIX = "log-";
    public static final int LOG_DAYS = 4;
    private static final long MAX_LOG_FILE_SIZE = 8388608;
    /* access modifiers changed from: private */
    public static Handler sHandler = null;
    /* access modifiers changed from: private */
    public static File sLogsDirectory = null;

    public static void setDir(File file) {
        synchronized (DATE_FORMAT) {
            if (sHandler != null && !file.equals(sLogsDirectory)) {
                ((HandlerThread) sHandler.getLooper().getThread()).quit();
                sHandler = null;
            }
        }
        sLogsDirectory = file;
    }

    public static void d(String str, String str2, Exception exc) {
        Log.d(str, str2, exc);
        print(str, str2, exc);
    }

    public static void d(String str, String str2) {
        Log.d(str, str2);
        print(str, str2);
    }

    public static void e(String str, String str2, Exception exc) {
        Log.e(str, str2, exc);
        print(str, str2, exc);
    }

    public static void e(String str, String str2) {
        Log.e(str, str2);
        print(str, str2);
    }

    public static void print(String str, String str2) {
        print(str, str2, (Exception) null);
    }

    public static void print(String str, String str2, Exception exc) {
        String format = String.format("%s %s %s", new Object[]{DATE_FORMAT.format(new Date()), str, str2});
        if (exc != null) {
            format = format + "\n" + Log.getStackTraceString(exc);
        }
        Message.obtain(getHandler(), 1, format).sendToTarget();
    }

    static Handler getHandler() {
        synchronized (DATE_FORMAT) {
            if (sHandler == null) {
                sHandler = new Handler(Executors.createAndStartNewLooper("file-logger"), new LogWriterCallback());
            }
        }
        return sHandler;
    }

    public static boolean flushAll(PrintWriter printWriter) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Message.obtain(getHandler(), 3, Pair.create(printWriter, countDownLatch)).sendToTarget();
        countDownLatch.await(2, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 0) {
            return true;
        }
        return false;
    }

    private static class LogWriterCallback implements Handler.Callback {
        private static final long CLOSE_DELAY = 5000;
        private static final int MSG_CLOSE = 2;
        private static final int MSG_FLUSH = 3;
        private static final int MSG_WRITE = 1;
        private String mCurrentFileName;
        private PrintWriter mCurrentWriter;

        private LogWriterCallback() {
            this.mCurrentFileName = null;
            this.mCurrentWriter = null;
        }

        private void closeWriter() {
            IOUtils.closeSilently(this.mCurrentWriter);
            this.mCurrentWriter = null;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v1, resolved type: boolean} */
        /* JADX WARNING: type inference failed for: r4v0 */
        /* JADX WARNING: type inference failed for: r4v2 */
        /* JADX WARNING: type inference failed for: r4v3, types: [int] */
        /* JADX WARNING: type inference failed for: r4v5 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean handleMessage(android.os.Message r11) {
            /*
                r10 = this;
                java.io.File r0 = com.android.launcher3.logging.FileLog.sLogsDirectory
                r1 = 1
                if (r0 == 0) goto L_0x00df
                int r0 = r11.what
                r2 = 4
                java.lang.String r3 = "log-"
                r4 = 0
                r5 = 2
                if (r0 == r1) goto L_0x004a
                if (r0 == r5) goto L_0x0046
                r5 = 3
                if (r0 == r5) goto L_0x0016
                return r1
            L_0x0016:
                r10.closeWriter()
                java.lang.Object r11 = r11.obj
                android.util.Pair r11 = (android.util.Pair) r11
                java.lang.Object r0 = r11.first
                if (r0 == 0) goto L_0x003e
            L_0x0021:
                if (r4 >= r2) goto L_0x003e
                java.lang.Object r0 = r11.first
                java.io.PrintWriter r0 = (java.io.PrintWriter) r0
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.StringBuilder r5 = r5.append(r3)
                java.lang.StringBuilder r5 = r5.append(r4)
                java.lang.String r5 = r5.toString()
                com.android.launcher3.logging.FileLog.dumpFile(r0, r5)
                int r4 = r4 + 1
                goto L_0x0021
            L_0x003e:
                java.lang.Object r11 = r11.second
                java.util.concurrent.CountDownLatch r11 = (java.util.concurrent.CountDownLatch) r11
                r11.countDown()
                return r1
            L_0x0046:
                r10.closeWriter()
                return r1
            L_0x004a:
                java.util.Calendar r0 = java.util.Calendar.getInstance()
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                java.lang.StringBuilder r3 = r6.append(r3)
                r6 = 6
                int r6 = r0.get(r6)
                int r6 = r6 % r2
                java.lang.StringBuilder r2 = r3.append(r6)
                java.lang.String r2 = r2.toString()
                java.lang.String r3 = r10.mCurrentFileName
                boolean r3 = r2.equals(r3)
                if (r3 != 0) goto L_0x0070
                r10.closeWriter()
            L_0x0070:
                java.io.PrintWriter r3 = r10.mCurrentWriter     // Catch:{ Exception -> 0x00d4 }
                if (r3 != 0) goto L_0x00b5
                r10.mCurrentFileName = r2     // Catch:{ Exception -> 0x00d4 }
                java.io.File r3 = new java.io.File     // Catch:{ Exception -> 0x00d4 }
                java.io.File r6 = com.android.launcher3.logging.FileLog.sLogsDirectory     // Catch:{ Exception -> 0x00d4 }
                r3.<init>(r6, r2)     // Catch:{ Exception -> 0x00d4 }
                boolean r2 = r3.exists()     // Catch:{ Exception -> 0x00d4 }
                if (r2 == 0) goto L_0x00a9
                java.util.Calendar r2 = java.util.Calendar.getInstance()     // Catch:{ Exception -> 0x00d4 }
                long r6 = r3.lastModified()     // Catch:{ Exception -> 0x00d4 }
                r2.setTimeInMillis(r6)     // Catch:{ Exception -> 0x00d4 }
                r6 = 10
                r7 = 36
                r2.add(r6, r7)     // Catch:{ Exception -> 0x00d4 }
                boolean r0 = r0.before(r2)     // Catch:{ Exception -> 0x00d4 }
                if (r0 == 0) goto L_0x00a9
                long r6 = r3.length()     // Catch:{ Exception -> 0x00d4 }
                r8 = 8388608(0x800000, double:4.144523E-317)
                int r0 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
                if (r0 >= 0) goto L_0x00a9
                r4 = r1
            L_0x00a9:
                java.io.PrintWriter r0 = new java.io.PrintWriter     // Catch:{ Exception -> 0x00d4 }
                java.io.FileWriter r2 = new java.io.FileWriter     // Catch:{ Exception -> 0x00d4 }
                r2.<init>(r3, r4)     // Catch:{ Exception -> 0x00d4 }
                r0.<init>(r2)     // Catch:{ Exception -> 0x00d4 }
                r10.mCurrentWriter = r0     // Catch:{ Exception -> 0x00d4 }
            L_0x00b5:
                java.io.PrintWriter r0 = r10.mCurrentWriter     // Catch:{ Exception -> 0x00d4 }
                java.lang.Object r11 = r11.obj     // Catch:{ Exception -> 0x00d4 }
                java.lang.String r11 = (java.lang.String) r11     // Catch:{ Exception -> 0x00d4 }
                r0.println(r11)     // Catch:{ Exception -> 0x00d4 }
                java.io.PrintWriter r11 = r10.mCurrentWriter     // Catch:{ Exception -> 0x00d4 }
                r11.flush()     // Catch:{ Exception -> 0x00d4 }
                android.os.Handler r11 = com.android.launcher3.logging.FileLog.sHandler     // Catch:{ Exception -> 0x00d4 }
                r11.removeMessages(r5)     // Catch:{ Exception -> 0x00d4 }
                android.os.Handler r11 = com.android.launcher3.logging.FileLog.sHandler     // Catch:{ Exception -> 0x00d4 }
                r2 = 5000(0x1388, double:2.4703E-320)
                r11.sendEmptyMessageDelayed(r5, r2)     // Catch:{ Exception -> 0x00d4 }
                goto L_0x00df
            L_0x00d4:
                r11 = move-exception
                java.lang.String r0 = "FileLog"
                java.lang.String r2 = "Error writing logs to file"
                android.util.Log.e(r0, r2, r11)
                r10.closeWriter()
            L_0x00df:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.logging.FileLog.LogWriterCallback.handleMessage(android.os.Message):boolean");
        }
    }

    /* access modifiers changed from: private */
    public static void dumpFile(PrintWriter printWriter, String str) {
        File file = new File(sLogsDirectory, str);
        if (file.exists()) {
            BufferedReader bufferedReader = null;
            try {
                BufferedReader bufferedReader2 = new BufferedReader(new FileReader(file));
                try {
                    printWriter.println();
                    printWriter.println("--- logfile: " + str + " ---");
                    while (true) {
                        String readLine = bufferedReader2.readLine();
                        if (readLine != null) {
                            printWriter.println(readLine);
                        } else {
                            IOUtils.closeSilently(bufferedReader2);
                            return;
                        }
                    }
                } catch (Exception unused) {
                    bufferedReader = bufferedReader2;
                    IOUtils.closeSilently(bufferedReader);
                } catch (Throwable th) {
                    th = th;
                    bufferedReader = bufferedReader2;
                    IOUtils.closeSilently(bufferedReader);
                    throw th;
                }
            } catch (Exception unused2) {
                IOUtils.closeSilently(bufferedReader);
            } catch (Throwable th2) {
                th = th2;
                IOUtils.closeSilently(bufferedReader);
                throw th;
            }
        }
    }

    public static File[] getLogFiles() {
        try {
            flushAll((PrintWriter) null);
        } catch (InterruptedException unused) {
        }
        File[] fileArr = new File[4];
        for (int i = 0; i < 4; i++) {
            fileArr[i] = new File(sLogsDirectory, FILE_NAME_PREFIX + i);
        }
        return fileArr;
    }
}
