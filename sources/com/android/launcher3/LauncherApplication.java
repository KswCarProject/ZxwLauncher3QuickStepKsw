package com.android.launcher3;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.UserHandle;
import android.util.Log;
import com.szchoiceway.CrashHandler;
import com.szchoiceway.SysProviderOpt;
import com.szchoiceway.eventcenter.IEventService;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class LauncherApplication extends Application {
    protected static final String TAG = "LauncherApplication";
    public ServiceConnection mEvtSc = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            IEventService unused = LauncherApplication.this.mEvtService = IEventService.Stub.asInterface(iBinder);
            Log.i(LauncherApplication.TAG, "onServiceConnected: mEvtService = " + LauncherApplication.this.mEvtService);
        }

        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(LauncherApplication.TAG, "onServiceDisconnected: ");
            IEventService unused = LauncherApplication.this.mEvtService = null;
        }
    };
    /* access modifiers changed from: private */
    public IEventService mEvtService;
    protected SysProviderOpt mProvider;
    private int m_iUITypeVer = 41;

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        CrashHandler.getInstance().init(this);
        this.mProvider = SysProviderOpt.getInstance(this);
        startSystemService();
        Log.d(TAG, "bind eventService: " + getApplicationContext().bindService(new Intent("com.szchoiceway.eventcenter.EventService").setPackage("com.szchoiceway.eventcenter"), this.mEvtSc, 1));
        int recordInteger = this.mProvider.getRecordInteger(SysProviderOpt.SET_USER_UI_TYPE, -1);
        Log.i(TAG, "onCreate: recordInteger = " + recordInteger);
        if (recordInteger != -1) {
            this.m_iUITypeVer = recordInteger;
        } else {
            bindService(new Intent("com.szchoiceway.eventcenter.EventService").setPackage("com.szchoiceway.eventcenter"), this.mEvtSc, 1);
            recordDeleteCount();
        }
        Map<String, String> loadXmlFile = loadXmlFile();
        this.m_iUITypeVer = Integer.parseInt(loadXmlFile.get("m_iUITypeVer"));
        this.mProvider.updateRecord(SysProviderOpt.SET_USER_UI_TYPE, this.m_iUITypeVer + "");
        String str = loadXmlFile.get("AppVersion");
        Log.i(TAG, "onCreate: appVersion = " + str);
        if (str != null && str.length() > 0) {
            this.mProvider.updateRecord(SysProviderOpt.SYS_APP_VERSION, str);
        }
        Log.d(TAG, "onCreate ,m_iModeSet:  " + this.mProvider.getRecordInteger(SysProviderOpt.KESAIWEI_SYS_MODE_SELECTION, 0));
    }

    public void onTerminate() {
        super.onTerminate();
        unbindService(this.mEvtSc);
    }

    public SysProviderOpt getProvider() {
        return this.mProvider;
    }

    private void startSystemService() {
        startServiceAsUser(new Intent("com.szchoiceway.eventcenter.EventService").setPackage("com.szchoiceway.eventcenter"), UserHandle.CURRENT);
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.carletter.car", "com.carletter.car.service.CarletterService"));
        startService(intent);
    }

    /* JADX WARNING: type inference failed for: r0v0 */
    /* JADX WARNING: type inference failed for: r0v1, types: [java.io.FileOutputStream] */
    /* JADX WARNING: type inference failed for: r0v5, types: [java.io.FileOutputStream] */
    /* JADX WARNING: type inference failed for: r0v7, types: [java.io.FileInputStream] */
    /* JADX WARNING: type inference failed for: r0v8 */
    /* JADX WARNING: type inference failed for: r0v9 */
    /* JADX WARNING: type inference failed for: r0v11 */
    /* JADX WARNING: type inference failed for: r0v12 */
    /* JADX WARNING: type inference failed for: r0v13 */
    /* JADX WARNING: type inference failed for: r0v14 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00b3 A[SYNTHETIC, Splitter:B:48:0x00b3] */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00bd A[SYNTHETIC, Splitter:B:53:0x00bd] */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x00c9 A[SYNTHETIC, Splitter:B:59:0x00c9] */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x00d3 A[SYNTHETIC, Splitter:B:64:0x00d3] */
    /* JADX WARNING: Removed duplicated region for block: B:71:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void recordDeleteCount() {
        /*
            r10 = this;
            r0 = 0
            java.io.File r1 = new java.io.File     // Catch:{ IOException -> 0x00ac, all -> 0x00a9 }
            java.lang.String r2 = "data/local/recordDeleteCount.txt"
            r1.<init>(r2)     // Catch:{ IOException -> 0x00ac, all -> 0x00a9 }
            boolean r2 = r1.exists()     // Catch:{ IOException -> 0x00ac, all -> 0x00a9 }
            r3 = 0
            java.lang.String r4 = "utf-8"
            if (r2 != 0) goto L_0x0034
            r1.createNewFile()     // Catch:{ IOException -> 0x00ac, all -> 0x00a9 }
            java.io.FileOutputStream r2 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x00ac, all -> 0x00a9 }
            r2.<init>(r1)     // Catch:{ IOException -> 0x00ac, all -> 0x00a9 }
            java.lang.String r1 = "1"
            byte[] r1 = r1.getBytes(r4)     // Catch:{ IOException -> 0x002e, all -> 0x0028 }
            int r4 = r1.length     // Catch:{ IOException -> 0x002e, all -> 0x0028 }
            r2.write(r1, r3, r4)     // Catch:{ IOException -> 0x002e, all -> 0x0028 }
            r2.flush()     // Catch:{ IOException -> 0x002e, all -> 0x0028 }
            goto L_0x0091
        L_0x0028:
            r1 = move-exception
            r9 = r2
            r2 = r0
            r0 = r9
            goto L_0x00c7
        L_0x002e:
            r1 = move-exception
            r9 = r2
            r2 = r0
            r0 = r9
            goto L_0x00ae
        L_0x0034:
            java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch:{ IOException -> 0x00ac, all -> 0x00a9 }
            r2.<init>(r1)     // Catch:{ IOException -> 0x00ac, all -> 0x00a9 }
            java.io.BufferedReader r5 = new java.io.BufferedReader     // Catch:{ IOException -> 0x00a7 }
            java.io.InputStreamReader r6 = new java.io.InputStreamReader     // Catch:{ IOException -> 0x00a7 }
            r6.<init>(r2)     // Catch:{ IOException -> 0x00a7 }
            r5.<init>(r6)     // Catch:{ IOException -> 0x00a7 }
            java.lang.String r5 = r5.readLine()     // Catch:{ IOException -> 0x00a7 }
            java.lang.String r6 = "LauncherApplication"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x00a7 }
            r7.<init>()     // Catch:{ IOException -> 0x00a7 }
            java.lang.String r8 = "recordDeleteCount: countStr = "
            java.lang.StringBuilder r7 = r7.append(r8)     // Catch:{ IOException -> 0x00a7 }
            java.lang.StringBuilder r7 = r7.append(r5)     // Catch:{ IOException -> 0x00a7 }
            java.lang.String r7 = r7.toString()     // Catch:{ IOException -> 0x00a7 }
            android.util.Log.i(r6, r7)     // Catch:{ IOException -> 0x00a7 }
            java.lang.String r6 = ""
            if (r5 == 0) goto L_0x006d
            boolean r7 = r6.equals(r5)     // Catch:{ IOException -> 0x00a7 }
            if (r7 != 0) goto L_0x006d
            int r3 = java.lang.Integer.parseInt(r5)     // Catch:{ IOException -> 0x00a7 }
        L_0x006d:
            int r3 = r3 + 1
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x00a7 }
            r5.<init>()     // Catch:{ IOException -> 0x00a7 }
            java.lang.StringBuilder r3 = r5.append(r3)     // Catch:{ IOException -> 0x00a7 }
            java.lang.StringBuilder r3 = r3.append(r6)     // Catch:{ IOException -> 0x00a7 }
            java.lang.String r3 = r3.toString()     // Catch:{ IOException -> 0x00a7 }
            java.io.FileOutputStream r5 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x00a7 }
            r5.<init>(r1)     // Catch:{ IOException -> 0x00a7 }
            byte[] r0 = r3.getBytes(r4)     // Catch:{ IOException -> 0x00a4, all -> 0x00a1 }
            r5.write(r0)     // Catch:{ IOException -> 0x00a4, all -> 0x00a1 }
            r5.flush()     // Catch:{ IOException -> 0x00a4, all -> 0x00a1 }
            r0 = r2
            r2 = r5
        L_0x0091:
            if (r2 == 0) goto L_0x009b
            r2.close()     // Catch:{ IOException -> 0x0097 }
            goto L_0x009b
        L_0x0097:
            r1 = move-exception
            r1.printStackTrace()
        L_0x009b:
            if (r0 == 0) goto L_0x00c5
            r0.close()     // Catch:{ IOException -> 0x00c1 }
            goto L_0x00c5
        L_0x00a1:
            r1 = move-exception
            r0 = r5
            goto L_0x00c7
        L_0x00a4:
            r1 = move-exception
            r0 = r5
            goto L_0x00ae
        L_0x00a7:
            r1 = move-exception
            goto L_0x00ae
        L_0x00a9:
            r1 = move-exception
            r2 = r0
            goto L_0x00c7
        L_0x00ac:
            r1 = move-exception
            r2 = r0
        L_0x00ae:
            r1.printStackTrace()     // Catch:{ all -> 0x00c6 }
            if (r0 == 0) goto L_0x00bb
            r0.close()     // Catch:{ IOException -> 0x00b7 }
            goto L_0x00bb
        L_0x00b7:
            r0 = move-exception
            r0.printStackTrace()
        L_0x00bb:
            if (r2 == 0) goto L_0x00c5
            r2.close()     // Catch:{ IOException -> 0x00c1 }
            goto L_0x00c5
        L_0x00c1:
            r0 = move-exception
            r0.printStackTrace()
        L_0x00c5:
            return
        L_0x00c6:
            r1 = move-exception
        L_0x00c7:
            if (r0 == 0) goto L_0x00d1
            r0.close()     // Catch:{ IOException -> 0x00cd }
            goto L_0x00d1
        L_0x00cd:
            r0 = move-exception
            r0.printStackTrace()
        L_0x00d1:
            if (r2 == 0) goto L_0x00db
            r2.close()     // Catch:{ IOException -> 0x00d7 }
            goto L_0x00db
        L_0x00d7:
            r0 = move-exception
            r0.printStackTrace()
        L_0x00db:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.LauncherApplication.recordDeleteCount():void");
    }

    private Map<String, String> loadXmlFile() {
        DocumentBuilder documentBuilder;
        Log.i(TAG, "loadXmlFile: strResolution = " + this.mProvider.getRecordValue(SysProviderOpt.RESOLUTION, "1920x720"));
        if (this.mProvider.getRecordInteger(SysProviderOpt.KESAIWEI_SYS_MODE_SELECTION, 0) == 0) {
            this.mProvider.updateRecord(SysProviderOpt.KESAIWEI_SYS_MODE_SELECTION, "16");
        }
        HashMap hashMap = new HashMap();
        FileInputStream fileInputStream = null;
        try {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            documentBuilder = null;
        }
        try {
            fileInputStream = new FileInputStream("/product/app/" + "customer_1920x720.xml");
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
            Log.d(TAG, "customer.xml is not exist");
        }
        if (!(fileInputStream == null || documentBuilder == null)) {
            try {
                NodeList elementsByTagName = documentBuilder.parse(fileInputStream).getElementsByTagName("customer");
                if (elementsByTagName.getLength() > 0) {
                    NodeList elementsByTagName2 = ((Element) elementsByTagName.item(0)).getElementsByTagName("item");
                    for (int i = 0; i < elementsByTagName2.getLength(); i++) {
                        Element element = (Element) elementsByTagName2.item(i);
                        hashMap.put(element.getAttribute("name"), element.getAttribute("value"));
                        Log.i("loadXmlFile", "name =" + element.getAttribute("name") + " value=" + element.getAttribute("value"));
                    }
                }
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        }
        if (fileInputStream != null) {
            try {
                fileInputStream.close();
            } catch (IOException e4) {
                e4.printStackTrace();
            }
        }
        return hashMap;
    }
}
