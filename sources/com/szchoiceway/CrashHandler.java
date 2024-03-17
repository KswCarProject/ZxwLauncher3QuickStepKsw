package com.szchoiceway;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.Process;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";
    private static CrashHandler mCrashHandler = new CrashHandler();
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    private Map<String, String> infos = new HashMap();
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return mCrashHandler;
    }

    public void init(Context context) {
        this.mContext = context;
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void uncaughtException(Thread thread, Throwable th) {
        if (handleException(th) || this.mDefaultHandler == null) {
            Log.i(TAG, "uncaughtException: else");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }
            Process.killProcess(Process.myPid());
            System.exit(1);
            return;
        }
        Log.i(TAG, "uncaughtException: if");
        this.mDefaultHandler.uncaughtException(thread, th);
    }

    private boolean handleException(Throwable th) {
        Log.i(TAG, "handleException: ");
        if (th == null) {
            return false;
        }
        new Thread() {
            public void run() {
                Looper.prepare();
                Looper.loop();
            }
        }.start();
        collectDeviceInfo(this.mContext);
        saveCrashInfo2File(th);
        return true;
    }

    public void collectDeviceInfo(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 1);
            if (packageInfo != null) {
                this.infos.put("versionName", packageInfo.versionName == null ? "null" : packageInfo.versionName);
                this.infos.put("versionCode", packageInfo.versionCode + "");
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        for (Field field : Build.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                this.infos.put(field.getName(), field.get((Object) null).toString());
                Log.d(TAG, field.getName() + " : " + field.get((Object) null));
            } catch (Exception e2) {
                Log.e(TAG, "an error occured when collect crash info", e2);
            }
        }
    }

    private String saveCrashInfo2File(Throwable th) {
        StringBuffer stringBuffer = new StringBuffer();
        for (Map.Entry next : this.infos.entrySet()) {
            stringBuffer.append(((String) next.getKey()) + "=" + ((String) next.getValue()) + "\n");
        }
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        th.printStackTrace(printWriter);
        for (Throwable cause = th.getCause(); cause != null; cause = cause.getCause()) {
            cause.printStackTrace(printWriter);
        }
        printWriter.close();
        stringBuffer.append(stringWriter.toString());
        try {
            String str = "crash-" + this.formatter.format(new Date()) + "-" + System.currentTimeMillis() + ".log";
            if (Environment.getExternalStorageState().equals("mounted")) {
                if (Build.VERSION.SDK_INT > 19) {
                    File file = new File("/storage/emulated/0/log/");
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream("/storage/emulated/0/log/" + str);
                    fileOutputStream.write(stringBuffer.toString().getBytes());
                    Log.i(TAG, "crash: exception stringBuffer = " + stringBuffer.toString());
                    fileOutputStream.close();
                } else {
                    String str2 = "/mnt/usb_storage/log/";
                    File file2 = new File(str2);
                    boolean z = true;
                    if (!file2.exists()) {
                        boolean mkdirs = file2.mkdirs();
                        if (!mkdirs) {
                            str2 = "/mnt/usb_storage1/log/";
                            File file3 = new File(str2);
                            if (!file3.exists()) {
                                z = file3.mkdirs();
                            }
                        } else {
                            z = mkdirs;
                        }
                    }
                    if (z) {
                        FileOutputStream fileOutputStream2 = new FileOutputStream(str2 + str);
                        fileOutputStream2.write(stringBuffer.toString().getBytes());
                        Log.i(TAG, "crash: exception stringBuffer = " + stringBuffer.toString());
                        fileOutputStream2.close();
                    } else {
                        Log.i(TAG, "saveCrashInfo2File: mkdirs = false");
                    }
                }
            }
            return str;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
            return null;
        }
    }
}
