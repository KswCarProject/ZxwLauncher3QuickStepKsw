package com.szchoiceway;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class SysProviderOpt {
    public static final int BTTYPE_FEIYITONG = 5;
    public static final int BTTYPE_IVT_BC5 = 6;
    public static final int BTTYPE_SUDING_BC5 = 1;
    public static final int BTTYPE_SUDING_BC8 = 2;
    public static final int BTTYPE_WENQIANG_BC6 = 3;
    public static final int BTTYPE_WENQIANG_BC9 = 4;
    private static final String CONTENT_NAME = "content://com.szchoiceway.eventcenter.SysVarProvider/SysVar";
    public static final String IMITAGE_ORIGINAL_CAL_STYLE_CLIENT = "IMITAGE_ORIGINAL_CAL_STYLE_CLIENT";
    public static final String IMITAGE_ORIGINAL_CAL_STYLE_INDEX = "IMITAGE_ORIGINAL_CAL_STYLE_INDEX";
    public static final String KESAIWEI_RECORD_BT_OFF = "KESAIWEI_RECORD_BT_OFF";
    public static final String KESAIWEI_RECORD_DVR = "KESAIWEI_RECORD_DVR";
    public static final String KESAIWEI_SYS_CAMERA_SELECTION = "KESAIWEI_SYS_CAMERA_SELECTION";
    public static final String KESAIWEI_SYS_MODE_SELECTION = "KESAIWEI_SYS_MODE_SELECTION";
    public static final String KSW_APPS_ICON_SELECT_INDEX = "KSW_APPS_ICON_SELECT_INDEX";
    public static final String KSW_ARL_LEFT_SHOW_INDEX = "KSW_ARL_LEFT_SHOW_INDEX";
    public static final String KSW_ARL_RIGHT_SHOW_INDEX = "KSW_ARL_RIGHT_SHOW_INDEX";
    public static final String KSW_DVR_APK_PACKAGENAME = "KSW_DVR_APK_PACKAGENAME";
    public static final String KSW_EVO_ID6_MAIN_INTERFACE_INDEX = "KSW_EVO_ID6_MAIN_INTERFACE_INDEX";
    public static final String KSW_EVO_ID7_MAIN_INTERFACE_INDEX = "KSW_EVO_ID7_MAIN_INTERFACE_INDEX";
    public static final String KSW_EVO_MAIN_INTERFACE_INDEX = "KSW_EVO_MAIN_INTERFACE_INDEX";
    public static final String KSW_EVO_MAIN_INTERFACE_SELECT_ZOOM = "KSW_EVO_MAIN_INTERFACE_SELECT_ZOOM";
    public static final String KSW_FACTORY_SET_CLIENT = "KSW_FACTORY_SET_CLIENT";
    public static final String KSW_HAVE_AUX = "KSW_HAVE_AUX";
    public static final String KSW_HAVE_DVD = "KSW_HAVE_DVD";
    public static final String KSW_HAVE_TV = "KSW_HAVE_TV";
    public static final String KSW_SUPPORT_DASHBOARD = "KSW_SUPPORT_DASHBOARD";
    public static final String LAUNCHER_APPS_CUSTOMIZE_RESUM = "LAUNCHER_APPS_CUSTOMIZE_RESUM";
    public static final String MAISILUO_SYS_GOOGLEPLAY = "MAISILUO_SYS_GOOGLEPLAY";
    public static final String MUSIC_ACTIVITYNAME = "Music_ActivityName";
    public static final String MUSIC_PACKAGENAME = "Music_PackageName";
    public static final String NAVI_ACTIVITYNAME = "Navi_ActivityName";
    public static final String NAVI_PACKAGENAME = "Navi_PackageName";
    public static final String RESOLUTION = "RESOLUTION";
    public static final String SET_Canbustype_KEY = "Set_Canbustype";
    public static final String SET_CarCanbusName_ID_KEY = "Set_CarCanbusName_ID";
    public static final String SET_Carstype_ID_KEY = "Set_Carstype_ID";
    public static final String SET_USER_UI_TYPE = "Set_User_UI_Type";
    public static final String SET_USER_UI_TYPE_INDEX = "Set_User_UI_Type_index";
    public static final String SYS_3D_DATA_RE_X = "Sys_3D_Data_Re_X";
    public static final String SYS_3D_DATA_RE_Y = "Sys_3D_Data_Re_Y";
    public static final String SYS_3D_DATA_RE_Z = "Sys_3D_Data_Re_Z";
    public static final String SYS_APP_VERSION = "Sys_AppVersion";
    public static final String SYS_BT_TYPE_KEY = "Sys_BTDeviceType";
    public static final String SYS_LAST_MODE_OFFSET = "Sys_LastModeOffset";
    public static final String SYS_MAIN_UI_CHWY_VALUE_INDEX_KEY = "Sys_MainUi_CHWY_ValueIndex";
    public static final String SYS_TV_OUT_ON_OFF_SET_VALUE_INDEX_KEY = "SYS_TV_OUT_ON_OFF_SET_VALUE_INDEX_KEY";
    private static final String TAG = "SysProviderOpt";
    public static final int UI_CHWY_1280X480 = 48;
    public static final int UI_IMITATE_ORIGINAL_CAR_STYLE = 102;
    public static final int UI_KESAIWEI_1280X480 = 41;
    public static final int UI_NORMAL_1920X720 = 101;
    public static final String VIDEO_ACTIVITYNAME = "Video_ActivityName";
    public static final String VIDEO_PACKAGENAME = "Video_PackageName";
    public static final String ZXW_ACTION_LAUNCHER_INIT = "ZXW_ACTION_LAUNCHER_INIT";
    private static SysProviderOpt mSysProviderOpt;
    private ContentResolver mCntResolver;
    private Context mContext;
    private Uri mUri = Uri.parse(CONTENT_NAME);

    public SysProviderOpt(Context context) {
        this.mCntResolver = context.getContentResolver();
    }

    public static SysProviderOpt getInstance(Context context) {
        if (mSysProviderOpt == null) {
            synchronized (SysProviderOpt.class) {
                if (mSysProviderOpt == null) {
                    mSysProviderOpt = new SysProviderOpt(context);
                }
            }
        }
        return mSysProviderOpt;
    }

    public Uri insertRecord(String str, String str2) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("keyname", str);
        contentValues.put("keyvalue", str2);
        try {
            return this.mCntResolver.insert(this.mUri, contentValues);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x0053 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0054 A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String getRecordValue(java.lang.String r9, java.lang.String r10) {
        /*
            r8 = this;
            java.lang.String r0 = ""
            java.lang.String r4 = "keyname=?"
            r1 = 1
            java.lang.String[] r5 = new java.lang.String[r1]
            r1 = 0
            r5[r1] = r9
            r9 = 0
            android.content.ContentResolver r1 = r8.mCntResolver     // Catch:{ Exception -> 0x003a }
            android.net.Uri r2 = r8.mUri     // Catch:{ Exception -> 0x003a }
            r3 = 0
            r6 = 0
            android.database.Cursor r9 = r1.query(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x003a }
            if (r9 == 0) goto L_0x002e
            int r1 = r9.getCount()     // Catch:{ Exception -> 0x003a }
            if (r1 <= 0) goto L_0x002e
            boolean r1 = r9.moveToNext()     // Catch:{ Exception -> 0x003a }
            if (r1 == 0) goto L_0x002e
            java.lang.String r1 = "keyvalue"
            int r1 = r9.getColumnIndex(r1)     // Catch:{ Exception -> 0x003a }
            java.lang.String r1 = r9.getString(r1)     // Catch:{ Exception -> 0x003a }
            goto L_0x002f
        L_0x002e:
            r1 = r0
        L_0x002f:
            if (r9 == 0) goto L_0x004b
            r9.close()     // Catch:{ Exception -> 0x0035 }
            goto L_0x004b
        L_0x0035:
            r2 = move-exception
            r7 = r2
            r2 = r1
            r1 = r7
            goto L_0x003c
        L_0x003a:
            r1 = move-exception
            r2 = r0
        L_0x003c:
            if (r9 == 0) goto L_0x0041
            r9.close()
        L_0x0041:
            java.lang.String r9 = r1.toString()
            java.lang.String r1 = "SysProviderOpt"
            android.util.Log.e(r1, r9)
            r1 = r2
        L_0x004b:
            if (r1 == 0) goto L_0x0054
            boolean r9 = r0.equals(r1)
            if (r9 != 0) goto L_0x0054
            return r1
        L_0x0054:
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.szchoiceway.SysProviderOpt.getRecordValue(java.lang.String, java.lang.String):java.lang.String");
    }

    public int getRecordInteger(String str, int i) {
        String[] strArr = {str};
        Cursor cursor = null;
        try {
            Cursor query = this.mCntResolver.query(this.mUri, (String[]) null, "keyname=?", strArr, (String) null);
            if (query != null && query.getCount() > 0 && query.moveToNext()) {
                String string = query.getString(query.getColumnIndex("keyvalue"));
                if (string.length() > 0) {
                    i = Integer.valueOf(string).intValue();
                }
            }
            if (query != null) {
                query.close();
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
            Log.e(TAG, e.toString());
        }
        return i;
    }

    public long getRecordLong(String str, long j) {
        String[] strArr = {str};
        Cursor cursor = null;
        try {
            Cursor query = this.mCntResolver.query(this.mUri, (String[]) null, "keyname=?", strArr, (String) null);
            if (query != null && query.getCount() > 0 && query.moveToNext()) {
                String string = query.getString(query.getColumnIndex("keyvalue"));
                if (string.length() > 0) {
                    j = Long.valueOf(string).longValue();
                }
            }
            if (query != null) {
                query.close();
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
            Log.e(TAG, e.toString());
        }
        return j;
    }

    public float getRecordFloat(String str, float f) {
        String[] strArr = {str};
        Cursor cursor = null;
        try {
            Cursor query = this.mCntResolver.query(this.mUri, (String[]) null, "keyname=?", strArr, (String) null);
            if (query != null && query.getCount() > 0 && query.moveToNext()) {
                String string = query.getString(query.getColumnIndex("keyvalue"));
                if (string.length() > 0) {
                    f = Float.valueOf(string).floatValue();
                }
            }
            if (query != null) {
                query.close();
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
            Log.e(TAG, e.toString());
        }
        return f;
    }

    public boolean getRecordBoolean(String str, boolean z) {
        boolean z2 = true;
        String[] strArr = {str};
        Cursor cursor = null;
        try {
            Cursor query = this.mCntResolver.query(this.mUri, (String[]) null, "keyname=?", strArr, (String) null);
            if (query != null && query.getCount() > 0 && query.moveToNext()) {
                String string = query.getString(query.getColumnIndex("keyvalue"));
                if (string.length() > 0) {
                    if (Integer.valueOf(string).intValue() != 1) {
                        z2 = false;
                    }
                    z = z2;
                }
            }
            if (query != null) {
                query.close();
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
            Log.e(TAG, e.toString());
        }
        return z;
    }

    public byte getRecordByte(String str, byte b) {
        String[] strArr = {str};
        Cursor cursor = null;
        try {
            Cursor query = this.mCntResolver.query(this.mUri, (String[]) null, "keyname=?", strArr, (String) null);
            if (query != null && query.getCount() > 0 && query.moveToNext()) {
                String string = query.getString(query.getColumnIndex("keyvalue"));
                if (string.length() > 0) {
                    b = Byte.valueOf(string).byteValue();
                }
            }
            if (query != null) {
                query.close();
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
            Log.e(TAG, e.toString());
        }
        return b;
    }

    public double getRecordDouble(String str, double d) {
        String recordValue = getRecordValue(str, "");
        return recordValue.length() > 0 ? Double.valueOf(recordValue).doubleValue() : d;
    }

    public void updateRecord(String str, String str2) {
        updateRecord(str, str2, true);
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x0044 A[SYNTHETIC, Splitter:B:20:0x0044] */
    /* JADX WARNING: Removed duplicated region for block: B:27:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateRecord(java.lang.String r11, java.lang.String r12, boolean r13) {
        /*
            r10 = this;
            java.lang.String r6 = "keyname=?"
            r0 = 1
            java.lang.String[] r7 = new java.lang.String[r0]
            r0 = 0
            r7[r0] = r11
            java.lang.String r8 = "keyvalue"
            java.lang.String[] r2 = new java.lang.String[]{r8}
            r9 = 0
            android.content.ContentResolver r0 = r10.mCntResolver     // Catch:{ Exception -> 0x0048 }
            android.net.Uri r1 = r10.mUri     // Catch:{ Exception -> 0x0048 }
            r5 = 0
            r3 = r6
            r4 = r7
            android.database.Cursor r0 = r0.query(r1, r2, r3, r4, r5)     // Catch:{ Exception -> 0x0048 }
            if (r0 == 0) goto L_0x003c
            int r1 = r0.getCount()     // Catch:{ Exception -> 0x0039 }
            if (r1 <= 0) goto L_0x003c
            android.content.ContentValues r11 = new android.content.ContentValues     // Catch:{ Exception -> 0x0039 }
            r11.<init>()     // Catch:{ Exception -> 0x0039 }
            r11.put(r8, r12)     // Catch:{ Exception -> 0x0039 }
            if (r0 == 0) goto L_0x0030
            r0.close()     // Catch:{ Exception -> 0x0039 }
            goto L_0x0031
        L_0x0030:
            r9 = r0
        L_0x0031:
            android.content.ContentResolver r12 = r10.mCntResolver     // Catch:{ Exception -> 0x0048 }
            android.net.Uri r13 = r10.mUri     // Catch:{ Exception -> 0x0048 }
            r12.update(r13, r11, r6, r7)     // Catch:{ Exception -> 0x0048 }
            goto L_0x0042
        L_0x0039:
            r11 = move-exception
            r9 = r0
            goto L_0x0049
        L_0x003c:
            if (r13 == 0) goto L_0x0041
            r10.insertRecord(r11, r12)     // Catch:{ Exception -> 0x0039 }
        L_0x0041:
            r9 = r0
        L_0x0042:
            if (r9 == 0) goto L_0x0057
            r9.close()     // Catch:{ Exception -> 0x0048 }
            goto L_0x0057
        L_0x0048:
            r11 = move-exception
        L_0x0049:
            if (r9 == 0) goto L_0x004e
            r9.close()
        L_0x004e:
            java.lang.String r11 = r11.toString()
            java.lang.String r12 = "SysProviderOpt"
            android.util.Log.e(r12, r11)
        L_0x0057:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.szchoiceway.SysProviderOpt.updateRecord(java.lang.String, java.lang.String, boolean):void");
    }

    public void setRecordDefaultValue(String str, String str2) {
        if (!checkRecordExist(str)) {
            insertRecord(str, str2);
        }
    }

    public boolean checkRecordExist(String str) {
        boolean z = true;
        boolean z2 = false;
        String[] strArr = {str};
        Cursor cursor = null;
        try {
            cursor = this.mCntResolver.query(this.mUri, new String[]{"keyvalue"}, "keyname=?", strArr, (String) null);
            if (cursor == null || cursor.getCount() <= 0) {
                z = false;
            }
            if (cursor == null) {
                return z;
            }
            try {
                cursor.close();
                return z;
            } catch (Exception e) {
                e = e;
                z2 = z;
            }
        } catch (Exception e2) {
            e = e2;
            if (cursor != null) {
                cursor.close();
            }
            Log.e(TAG, e.toString());
            return z2;
        }
    }
}
