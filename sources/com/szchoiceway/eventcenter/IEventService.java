package com.szchoiceway.eventcenter;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.szchoiceway.eventcenter.ICallbackfn;

public interface IEventService extends IInterface {

    public static class Default implements IEventService {
        public int GetBTStatus() throws RemoteException {
            return 0;
        }

        public int GetCurrDim() throws RemoteException {
            return 0;
        }

        public int GetSignalStatus() throws RemoteException {
            return 0;
        }

        public boolean IsBackcarConnected() throws RemoteException {
            return false;
        }

        public boolean IsBrakeConneted() throws RemoteException {
            return false;
        }

        public boolean IsDiscConneted() throws RemoteException {
            return false;
        }

        public boolean IsMCUUpgradeWriteErr() throws RemoteException {
            return false;
        }

        public boolean IsMuteOn() throws RemoteException {
            return false;
        }

        public int LoadNLightVal() throws RemoteException {
            return 0;
        }

        public void OpenVideo(boolean z) throws RemoteException {
        }

        public boolean Send8902McuUpgradeData(byte[] bArr, int i) throws RemoteException {
            return false;
        }

        public void SendAudioSetToMCU(int i, int i2, int i3, int i4, int i5, int i6) throws RemoteException {
        }

        public void SendBALFADVal(byte b, byte b2) throws RemoteException {
        }

        public void SendBLVal(byte b, byte b2) throws RemoteException {
        }

        public void SendBlackState(boolean z) throws RemoteException {
        }

        public void SendDVRKey(byte b) throws RemoteException {
        }

        public void SendFactorySet() throws RemoteException {
        }

        public void SendFactorySetToMCU(byte b, byte b2, byte b3, byte b4, byte b5, byte b6) throws RemoteException {
        }

        public void SendGPSVolToMCU(byte b) throws RemoteException {
        }

        public void SendMainVol(byte b) throws RemoteException {
        }

        public void SendSysRTCTimeMCU(int i, int i2, int i3, int i4, int i5, int i6) throws RemoteException {
        }

        public void SendVol_KSW(boolean z, int i, int i2, int i3) throws RemoteException {
        }

        public void SendWheelKey(int i) throws RemoteException {
        }

        public void SetCurrDim(int i) throws RemoteException {
        }

        public void SetDVDVideoCH(int i) throws RemoteException {
        }

        public void SetVideoCH(int i) throws RemoteException {
        }

        public void SetVideoSize(int i, int i2, int i3, int i4) throws RemoteException {
        }

        public void appySetting() throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }

        public void beep() throws RemoteException {
        }

        public void commitSetting() throws RemoteException {
        }

        public void enterCanUpgradeMode() throws RemoteException {
        }

        public void enterUpgradeMode() throws RemoteException {
        }

        public void exitCanUpgradeMode() throws RemoteException {
        }

        public void exitCurMode(int i) throws RemoteException {
        }

        public void exitUpgradeMode() throws RemoteException {
        }

        public byte getBALVal() throws RemoteException {
            return 0;
        }

        public byte getBassFre() throws RemoteException {
            return 0;
        }

        public byte getBassVal() throws RemoteException {
            return 0;
        }

        public int getCameraOwner() throws RemoteException {
            return 0;
        }

        public byte getEQMode() throws RemoteException {
            return 0;
        }

        public byte getFADVal() throws RemoteException {
            return 0;
        }

        public boolean getLoudStatus() throws RemoteException {
            return false;
        }

        public String getMCUVer() throws RemoteException {
            return null;
        }

        public byte getMainVolval() throws RemoteException {
            return 0;
        }

        public boolean getMcuInitStatus() throws RemoteException {
            return false;
        }

        public byte getMiddleFre() throws RemoteException {
            return 0;
        }

        public byte getMiddleVal() throws RemoteException {
            return 0;
        }

        public boolean getMuteStatus() throws RemoteException {
            return false;
        }

        public boolean getRadioAFState() throws RemoteException {
            return false;
        }

        public boolean getRadioAMSState() throws RemoteException {
            return false;
        }

        public boolean getRadioAPSState() throws RemoteException {
            return false;
        }

        public int getRadioBand() throws RemoteException {
            return 0;
        }

        public boolean getRadioDXLOCState() throws RemoteException {
            return false;
        }

        public int getRadioFreq() throws RemoteException {
            return 0;
        }

        public int[] getRadioFreqList() throws RemoteException {
            return null;
        }

        public int getRadioNum() throws RemoteException {
            return 0;
        }

        public String getRadioPTYName() throws RemoteException {
            return null;
        }

        public int getRadioPTYNum() throws RemoteException {
            return 0;
        }

        public boolean getRadioPTYState() throws RemoteException {
            return false;
        }

        public boolean getRadioRDSState() throws RemoteException {
            return false;
        }

        public boolean getRadioSTMonoState() throws RemoteException {
            return false;
        }

        public boolean getRadioSteroIconState() throws RemoteException {
            return false;
        }

        public boolean getRadioTAState() throws RemoteException {
            return false;
        }

        public boolean getRadioTPIconState() throws RemoteException {
            return false;
        }

        public boolean getRadioTrafficState() throws RemoteException {
            return false;
        }

        public boolean getSettingBoolean(String str, boolean z) throws RemoteException {
            return false;
        }

        public float getSettingFloat(String str, float f) throws RemoteException {
            return 0.0f;
        }

        public int getSettingInt(String str, int i) throws RemoteException {
            return 0;
        }

        public long getSettingLong(String str, long j) throws RemoteException {
            return 0;
        }

        public String getSettingString(String str, String str2) throws RemoteException {
            return null;
        }

        public String getTFTVer() throws RemoteException {
            return null;
        }

        public byte getTrebleFre() throws RemoteException {
            return 0;
        }

        public byte getTrebleVal() throws RemoteException {
            return 0;
        }

        public byte getUserBassVal() throws RemoteException {
            return 0;
        }

        public byte getUserMiddleVal() throws RemoteException {
            return 0;
        }

        public byte getUserTrebleVal() throws RemoteException {
            return 0;
        }

        public int getValidCurFolder() throws RemoteException {
            return 0;
        }

        public int getValidCurTime() throws RemoteException {
            return 0;
        }

        public int getValidCurTrack() throws RemoteException {
            return 0;
        }

        public int getValidLoopMode() throws RemoteException {
            return 0;
        }

        public int getValidMode() throws RemoteException {
            return 0;
        }

        public String getValidModeAblumInfor() throws RemoteException {
            return null;
        }

        public String getValidModeArtistInfor() throws RemoteException {
            return null;
        }

        public String getValidModeTitleInfor() throws RemoteException {
            return null;
        }

        public int getValidPlayStatus() throws RemoteException {
            return 0;
        }

        public int getValidRepeatMode() throws RemoteException {
            return 0;
        }

        public int getValidTotFolder() throws RemoteException {
            return 0;
        }

        public int getValidTotTime() throws RemoteException {
            return 0;
        }

        public int getValidTotTrack() throws RemoteException {
            return 0;
        }

        public boolean get_kesaiwei_bPark() throws RemoteException {
            return false;
        }

        public boolean get_kesaiwei_chk_Video_Driving_Ban() throws RemoteException {
            return false;
        }

        public int get_m_i_easyconn_state_KSW() throws RemoteException {
            return 0;
        }

        public boolean getmIsAddMouseView() throws RemoteException {
            return false;
        }

        public void initRadioZone(byte b) throws RemoteException {
        }

        public boolean isUpgradeMode() throws RemoteException {
            return false;
        }

        public void openTVout(int i, boolean z) throws RemoteException {
        }

        public void putSettingBoolean(String str, boolean z) throws RemoteException {
        }

        public void putSettingFloat(String str, float f) throws RemoteException {
        }

        public void putSettingInt(String str, int i) throws RemoteException {
        }

        public void putSettingLong(String str, long j) throws RemoteException {
        }

        public void putSettingStr(String str, String str2) throws RemoteException {
        }

        public void responseCanUpgradeDataEvent(byte b) throws RemoteException {
        }

        public void responseCanUpgradeEvent(byte b) throws RemoteException {
        }

        public void responseCanUpgradeMode() throws RemoteException {
        }

        public void sendBTState(int i) throws RemoteException {
        }

        public void sendCanbusData(byte[] bArr) throws RemoteException {
        }

        public void sendCanbusUpgradeData(byte[] bArr) throws RemoteException {
        }

        public void sendDvdDataToMcu(byte[] bArr) throws RemoteException {
        }

        public void sendEQMode(int i) throws RemoteException {
        }

        public void sendKeyDownUpSync(int i) throws RemoteException {
        }

        public void sendMcuData_KSW(byte[] bArr) throws RemoteException {
        }

        public boolean sendMcuUpgradeData(long j, byte[] bArr, int i, int i2, boolean z) throws RemoteException {
            return false;
        }

        public boolean sendMcuUpgradeMode(int i) throws RemoteException {
            return false;
        }

        public void sendMode(int i, boolean z) throws RemoteException {
        }

        public void sendMuteState(boolean z) throws RemoteException {
        }

        public void sendPlayState(boolean z) throws RemoteException {
        }

        public void sendRadioKey(int i) throws RemoteException {
        }

        public void sendResetDVD(boolean z) throws RemoteException {
        }

        public void sendSetup(byte b, byte b2) throws RemoteException {
        }

        public void sendSystemKey(int i) throws RemoteException {
        }

        public void sendTVKey(int i) throws RemoteException {
        }

        public void sendToOSData() throws RemoteException {
        }

        public void sendTouchPos(int i, int i2, boolean z) throws RemoteException {
        }

        public void sendUserFreq(int i) throws RemoteException {
        }

        public void send_KSW_page2_vol_info(int i, int i2, int i3, int i4) throws RemoteException {
        }

        public void setAUXCallback(ICallbackfn iCallbackfn) throws RemoteException {
        }

        public void setCameraOwner(int i) throws RemoteException {
        }

        public void setCarMediaCallback(ICallbackfn iCallbackfn) throws RemoteException {
        }

        public void setCurModeCallback(int i, ICallbackfn iCallbackfn) throws RemoteException {
        }

        public void setDashBoardCallback(ICallbackfn iCallbackfn) throws RemoteException {
        }

        public void setGpsFocusCallback(ICallbackfn iCallbackfn) throws RemoteException {
        }

        public void setMcuInitStatus(boolean z) throws RemoteException {
        }

        public void setRadioCallback(ICallbackfn iCallbackfn) throws RemoteException {
        }

        public void setTVCallback(ICallbackfn iCallbackfn) throws RemoteException {
        }

        public void setValidModeInfor(String str, String str2, String str3, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) throws RemoteException {
        }
    }

    int GetBTStatus() throws RemoteException;

    int GetCurrDim() throws RemoteException;

    int GetSignalStatus() throws RemoteException;

    boolean IsBackcarConnected() throws RemoteException;

    boolean IsBrakeConneted() throws RemoteException;

    boolean IsDiscConneted() throws RemoteException;

    boolean IsMCUUpgradeWriteErr() throws RemoteException;

    boolean IsMuteOn() throws RemoteException;

    int LoadNLightVal() throws RemoteException;

    void OpenVideo(boolean z) throws RemoteException;

    boolean Send8902McuUpgradeData(byte[] bArr, int i) throws RemoteException;

    void SendAudioSetToMCU(int i, int i2, int i3, int i4, int i5, int i6) throws RemoteException;

    void SendBALFADVal(byte b, byte b2) throws RemoteException;

    void SendBLVal(byte b, byte b2) throws RemoteException;

    void SendBlackState(boolean z) throws RemoteException;

    void SendDVRKey(byte b) throws RemoteException;

    void SendFactorySet() throws RemoteException;

    void SendFactorySetToMCU(byte b, byte b2, byte b3, byte b4, byte b5, byte b6) throws RemoteException;

    void SendGPSVolToMCU(byte b) throws RemoteException;

    void SendMainVol(byte b) throws RemoteException;

    void SendSysRTCTimeMCU(int i, int i2, int i3, int i4, int i5, int i6) throws RemoteException;

    void SendVol_KSW(boolean z, int i, int i2, int i3) throws RemoteException;

    void SendWheelKey(int i) throws RemoteException;

    void SetCurrDim(int i) throws RemoteException;

    void SetDVDVideoCH(int i) throws RemoteException;

    void SetVideoCH(int i) throws RemoteException;

    void SetVideoSize(int i, int i2, int i3, int i4) throws RemoteException;

    void appySetting() throws RemoteException;

    void beep() throws RemoteException;

    void commitSetting() throws RemoteException;

    void enterCanUpgradeMode() throws RemoteException;

    void enterUpgradeMode() throws RemoteException;

    void exitCanUpgradeMode() throws RemoteException;

    void exitCurMode(int i) throws RemoteException;

    void exitUpgradeMode() throws RemoteException;

    byte getBALVal() throws RemoteException;

    byte getBassFre() throws RemoteException;

    byte getBassVal() throws RemoteException;

    int getCameraOwner() throws RemoteException;

    byte getEQMode() throws RemoteException;

    byte getFADVal() throws RemoteException;

    boolean getLoudStatus() throws RemoteException;

    String getMCUVer() throws RemoteException;

    byte getMainVolval() throws RemoteException;

    boolean getMcuInitStatus() throws RemoteException;

    byte getMiddleFre() throws RemoteException;

    byte getMiddleVal() throws RemoteException;

    boolean getMuteStatus() throws RemoteException;

    boolean getRadioAFState() throws RemoteException;

    boolean getRadioAMSState() throws RemoteException;

    boolean getRadioAPSState() throws RemoteException;

    int getRadioBand() throws RemoteException;

    boolean getRadioDXLOCState() throws RemoteException;

    int getRadioFreq() throws RemoteException;

    int[] getRadioFreqList() throws RemoteException;

    int getRadioNum() throws RemoteException;

    String getRadioPTYName() throws RemoteException;

    int getRadioPTYNum() throws RemoteException;

    boolean getRadioPTYState() throws RemoteException;

    boolean getRadioRDSState() throws RemoteException;

    boolean getRadioSTMonoState() throws RemoteException;

    boolean getRadioSteroIconState() throws RemoteException;

    boolean getRadioTAState() throws RemoteException;

    boolean getRadioTPIconState() throws RemoteException;

    boolean getRadioTrafficState() throws RemoteException;

    boolean getSettingBoolean(String str, boolean z) throws RemoteException;

    float getSettingFloat(String str, float f) throws RemoteException;

    int getSettingInt(String str, int i) throws RemoteException;

    long getSettingLong(String str, long j) throws RemoteException;

    String getSettingString(String str, String str2) throws RemoteException;

    String getTFTVer() throws RemoteException;

    byte getTrebleFre() throws RemoteException;

    byte getTrebleVal() throws RemoteException;

    byte getUserBassVal() throws RemoteException;

    byte getUserMiddleVal() throws RemoteException;

    byte getUserTrebleVal() throws RemoteException;

    int getValidCurFolder() throws RemoteException;

    int getValidCurTime() throws RemoteException;

    int getValidCurTrack() throws RemoteException;

    int getValidLoopMode() throws RemoteException;

    int getValidMode() throws RemoteException;

    String getValidModeAblumInfor() throws RemoteException;

    String getValidModeArtistInfor() throws RemoteException;

    String getValidModeTitleInfor() throws RemoteException;

    int getValidPlayStatus() throws RemoteException;

    int getValidRepeatMode() throws RemoteException;

    int getValidTotFolder() throws RemoteException;

    int getValidTotTime() throws RemoteException;

    int getValidTotTrack() throws RemoteException;

    boolean get_kesaiwei_bPark() throws RemoteException;

    boolean get_kesaiwei_chk_Video_Driving_Ban() throws RemoteException;

    int get_m_i_easyconn_state_KSW() throws RemoteException;

    boolean getmIsAddMouseView() throws RemoteException;

    void initRadioZone(byte b) throws RemoteException;

    boolean isUpgradeMode() throws RemoteException;

    void openTVout(int i, boolean z) throws RemoteException;

    void putSettingBoolean(String str, boolean z) throws RemoteException;

    void putSettingFloat(String str, float f) throws RemoteException;

    void putSettingInt(String str, int i) throws RemoteException;

    void putSettingLong(String str, long j) throws RemoteException;

    void putSettingStr(String str, String str2) throws RemoteException;

    void responseCanUpgradeDataEvent(byte b) throws RemoteException;

    void responseCanUpgradeEvent(byte b) throws RemoteException;

    void responseCanUpgradeMode() throws RemoteException;

    void sendBTState(int i) throws RemoteException;

    void sendCanbusData(byte[] bArr) throws RemoteException;

    void sendCanbusUpgradeData(byte[] bArr) throws RemoteException;

    void sendDvdDataToMcu(byte[] bArr) throws RemoteException;

    void sendEQMode(int i) throws RemoteException;

    void sendKeyDownUpSync(int i) throws RemoteException;

    void sendMcuData_KSW(byte[] bArr) throws RemoteException;

    boolean sendMcuUpgradeData(long j, byte[] bArr, int i, int i2, boolean z) throws RemoteException;

    boolean sendMcuUpgradeMode(int i) throws RemoteException;

    void sendMode(int i, boolean z) throws RemoteException;

    void sendMuteState(boolean z) throws RemoteException;

    void sendPlayState(boolean z) throws RemoteException;

    void sendRadioKey(int i) throws RemoteException;

    void sendResetDVD(boolean z) throws RemoteException;

    void sendSetup(byte b, byte b2) throws RemoteException;

    void sendSystemKey(int i) throws RemoteException;

    void sendTVKey(int i) throws RemoteException;

    void sendToOSData() throws RemoteException;

    void sendTouchPos(int i, int i2, boolean z) throws RemoteException;

    void sendUserFreq(int i) throws RemoteException;

    void send_KSW_page2_vol_info(int i, int i2, int i3, int i4) throws RemoteException;

    void setAUXCallback(ICallbackfn iCallbackfn) throws RemoteException;

    void setCameraOwner(int i) throws RemoteException;

    void setCarMediaCallback(ICallbackfn iCallbackfn) throws RemoteException;

    void setCurModeCallback(int i, ICallbackfn iCallbackfn) throws RemoteException;

    void setDashBoardCallback(ICallbackfn iCallbackfn) throws RemoteException;

    void setGpsFocusCallback(ICallbackfn iCallbackfn) throws RemoteException;

    void setMcuInitStatus(boolean z) throws RemoteException;

    void setRadioCallback(ICallbackfn iCallbackfn) throws RemoteException;

    void setTVCallback(ICallbackfn iCallbackfn) throws RemoteException;

    void setValidModeInfor(String str, String str2, String str3, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) throws RemoteException;

    public static abstract class Stub extends Binder implements IEventService {
        private static final String DESCRIPTOR = "com.szchoiceway.eventcenter.IEventService";
        static final int TRANSACTION_GetBTStatus = 108;
        static final int TRANSACTION_GetCurrDim = 112;
        static final int TRANSACTION_GetSignalStatus = 76;
        static final int TRANSACTION_IsBackcarConnected = 109;
        static final int TRANSACTION_IsBrakeConneted = 73;
        static final int TRANSACTION_IsDiscConneted = 97;
        static final int TRANSACTION_IsMCUUpgradeWriteErr = 74;
        static final int TRANSACTION_IsMuteOn = 79;
        static final int TRANSACTION_LoadNLightVal = 110;
        static final int TRANSACTION_OpenVideo = 77;
        static final int TRANSACTION_Send8902McuUpgradeData = 114;
        static final int TRANSACTION_SendAudioSetToMCU = 63;
        static final int TRANSACTION_SendBALFADVal = 71;
        static final int TRANSACTION_SendBLVal = 68;
        static final int TRANSACTION_SendBlackState = 72;
        static final int TRANSACTION_SendDVRKey = 66;
        static final int TRANSACTION_SendFactorySet = 107;
        static final int TRANSACTION_SendFactorySetToMCU = 47;
        static final int TRANSACTION_SendGPSVolToMCU = 106;
        static final int TRANSACTION_SendMainVol = 67;
        static final int TRANSACTION_SendSysRTCTimeMCU = 65;
        static final int TRANSACTION_SendVol_KSW = 115;
        static final int TRANSACTION_SendWheelKey = 64;
        static final int TRANSACTION_SetCurrDim = 111;
        static final int TRANSACTION_SetDVDVideoCH = 95;
        static final int TRANSACTION_SetVideoCH = 75;
        static final int TRANSACTION_SetVideoSize = 78;
        static final int TRANSACTION_appySetting = 39;
        static final int TRANSACTION_beep = 7;
        static final int TRANSACTION_commitSetting = 38;
        static final int TRANSACTION_enterCanUpgradeMode = 130;
        static final int TRANSACTION_enterUpgradeMode = 101;
        static final int TRANSACTION_exitCanUpgradeMode = 131;
        static final int TRANSACTION_exitCurMode = 31;
        static final int TRANSACTION_exitUpgradeMode = 102;
        static final int TRANSACTION_getBALVal = 55;
        static final int TRANSACTION_getBassFre = 52;
        static final int TRANSACTION_getBassVal = 49;
        static final int TRANSACTION_getCameraOwner = 124;
        static final int TRANSACTION_getEQMode = 57;
        static final int TRANSACTION_getFADVal = 56;
        static final int TRANSACTION_getLoudStatus = 62;
        static final int TRANSACTION_getMCUVer = 32;
        static final int TRANSACTION_getMainVolval = 48;
        static final int TRANSACTION_getMcuInitStatus = 100;
        static final int TRANSACTION_getMiddleFre = 53;
        static final int TRANSACTION_getMiddleVal = 51;
        static final int TRANSACTION_getMuteStatus = 61;
        static final int TRANSACTION_getRadioAFState = 20;
        static final int TRANSACTION_getRadioAMSState = 24;
        static final int TRANSACTION_getRadioAPSState = 25;
        static final int TRANSACTION_getRadioBand = 14;
        static final int TRANSACTION_getRadioDXLOCState = 23;
        static final int TRANSACTION_getRadioFreq = 12;
        static final int TRANSACTION_getRadioFreqList = 13;
        static final int TRANSACTION_getRadioNum = 15;
        static final int TRANSACTION_getRadioPTYName = 19;
        static final int TRANSACTION_getRadioPTYNum = 18;
        static final int TRANSACTION_getRadioPTYState = 17;
        static final int TRANSACTION_getRadioRDSState = 16;
        static final int TRANSACTION_getRadioSTMonoState = 22;
        static final int TRANSACTION_getRadioSteroIconState = 26;
        static final int TRANSACTION_getRadioTAState = 21;
        static final int TRANSACTION_getRadioTPIconState = 27;
        static final int TRANSACTION_getRadioTrafficState = 28;
        static final int TRANSACTION_getSettingBoolean = 40;
        static final int TRANSACTION_getSettingFloat = 41;
        static final int TRANSACTION_getSettingInt = 42;
        static final int TRANSACTION_getSettingLong = 43;
        static final int TRANSACTION_getSettingString = 44;
        static final int TRANSACTION_getTFTVer = 113;
        static final int TRANSACTION_getTrebleFre = 54;
        static final int TRANSACTION_getTrebleVal = 50;
        static final int TRANSACTION_getUserBassVal = 58;
        static final int TRANSACTION_getUserMiddleVal = 60;
        static final int TRANSACTION_getUserTrebleVal = 59;
        static final int TRANSACTION_getValidCurFolder = 90;
        static final int TRANSACTION_getValidCurTime = 88;
        static final int TRANSACTION_getValidCurTrack = 86;
        static final int TRANSACTION_getValidLoopMode = 92;
        static final int TRANSACTION_getValidMode = 46;
        static final int TRANSACTION_getValidModeAblumInfor = 84;
        static final int TRANSACTION_getValidModeArtistInfor = 85;
        static final int TRANSACTION_getValidModeTitleInfor = 83;
        static final int TRANSACTION_getValidPlayStatus = 94;
        static final int TRANSACTION_getValidRepeatMode = 93;
        static final int TRANSACTION_getValidTotFolder = 91;
        static final int TRANSACTION_getValidTotTime = 89;
        static final int TRANSACTION_getValidTotTrack = 87;
        static final int TRANSACTION_get_kesaiwei_bPark = 117;
        static final int TRANSACTION_get_kesaiwei_chk_Video_Driving_Ban = 116;
        static final int TRANSACTION_get_m_i_easyconn_state_KSW = 122;
        static final int TRANSACTION_getmIsAddMouseView = 128;
        static final int TRANSACTION_initRadioZone = 80;
        static final int TRANSACTION_isUpgradeMode = 105;
        static final int TRANSACTION_openTVout = 126;
        static final int TRANSACTION_putSettingBoolean = 37;
        static final int TRANSACTION_putSettingFloat = 36;
        static final int TRANSACTION_putSettingInt = 34;
        static final int TRANSACTION_putSettingLong = 35;
        static final int TRANSACTION_putSettingStr = 33;
        static final int TRANSACTION_responseCanUpgradeDataEvent = 135;
        static final int TRANSACTION_responseCanUpgradeEvent = 134;
        static final int TRANSACTION_responseCanUpgradeMode = 133;
        static final int TRANSACTION_sendBTState = 11;
        static final int TRANSACTION_sendCanbusData = 81;
        static final int TRANSACTION_sendCanbusUpgradeData = 132;
        static final int TRANSACTION_sendDvdDataToMcu = 118;
        static final int TRANSACTION_sendEQMode = 5;
        static final int TRANSACTION_sendKeyDownUpSync = 129;
        static final int TRANSACTION_sendMcuData_KSW = 119;
        static final int TRANSACTION_sendMcuUpgradeData = 104;
        static final int TRANSACTION_sendMcuUpgradeMode = 103;
        static final int TRANSACTION_sendMode = 1;
        static final int TRANSACTION_sendMuteState = 8;
        static final int TRANSACTION_sendPlayState = 9;
        static final int TRANSACTION_sendRadioKey = 2;
        static final int TRANSACTION_sendResetDVD = 96;
        static final int TRANSACTION_sendSetup = 10;
        static final int TRANSACTION_sendSystemKey = 4;
        static final int TRANSACTION_sendTVKey = 3;
        static final int TRANSACTION_sendToOSData = 127;
        static final int TRANSACTION_sendTouchPos = 45;
        static final int TRANSACTION_sendUserFreq = 6;
        static final int TRANSACTION_send_KSW_page2_vol_info = 123;
        static final int TRANSACTION_setAUXCallback = 70;
        static final int TRANSACTION_setCameraOwner = 125;
        static final int TRANSACTION_setCarMediaCallback = 98;
        static final int TRANSACTION_setCurModeCallback = 30;
        static final int TRANSACTION_setDashBoardCallback = 120;
        static final int TRANSACTION_setGpsFocusCallback = 121;
        static final int TRANSACTION_setMcuInitStatus = 99;
        static final int TRANSACTION_setRadioCallback = 29;
        static final int TRANSACTION_setTVCallback = 69;
        static final int TRANSACTION_setValidModeInfor = 82;

        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IEventService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IEventService)) {
                return new Proxy(iBinder);
            }
            return (IEventService) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            int i3 = i;
            Parcel parcel3 = parcel;
            Parcel parcel4 = parcel2;
            if (i3 != 1598968902) {
                boolean z = false;
                switch (i3) {
                    case 1:
                        parcel3.enforceInterface(DESCRIPTOR);
                        int readInt = parcel.readInt();
                        if (parcel.readInt() != 0) {
                            z = true;
                        }
                        sendMode(readInt, z);
                        parcel2.writeNoException();
                        return true;
                    case 2:
                        parcel3.enforceInterface(DESCRIPTOR);
                        sendRadioKey(parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 3:
                        parcel3.enforceInterface(DESCRIPTOR);
                        sendTVKey(parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 4:
                        parcel3.enforceInterface(DESCRIPTOR);
                        sendSystemKey(parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 5:
                        parcel3.enforceInterface(DESCRIPTOR);
                        sendEQMode(parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 6:
                        parcel3.enforceInterface(DESCRIPTOR);
                        sendUserFreq(parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 7:
                        parcel3.enforceInterface(DESCRIPTOR);
                        beep();
                        parcel2.writeNoException();
                        return true;
                    case 8:
                        parcel3.enforceInterface(DESCRIPTOR);
                        if (parcel.readInt() != 0) {
                            z = true;
                        }
                        sendMuteState(z);
                        parcel2.writeNoException();
                        return true;
                    case 9:
                        parcel3.enforceInterface(DESCRIPTOR);
                        if (parcel.readInt() != 0) {
                            z = true;
                        }
                        sendPlayState(z);
                        parcel2.writeNoException();
                        return true;
                    case 10:
                        parcel3.enforceInterface(DESCRIPTOR);
                        sendSetup(parcel.readByte(), parcel.readByte());
                        parcel2.writeNoException();
                        return true;
                    case 11:
                        parcel3.enforceInterface(DESCRIPTOR);
                        sendBTState(parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 12:
                        parcel3.enforceInterface(DESCRIPTOR);
                        int radioFreq = getRadioFreq();
                        parcel2.writeNoException();
                        parcel4.writeInt(radioFreq);
                        return true;
                    case 13:
                        parcel3.enforceInterface(DESCRIPTOR);
                        int[] radioFreqList = getRadioFreqList();
                        parcel2.writeNoException();
                        parcel4.writeIntArray(radioFreqList);
                        return true;
                    case 14:
                        parcel3.enforceInterface(DESCRIPTOR);
                        int radioBand = getRadioBand();
                        parcel2.writeNoException();
                        parcel4.writeInt(radioBand);
                        return true;
                    case 15:
                        parcel3.enforceInterface(DESCRIPTOR);
                        int radioNum = getRadioNum();
                        parcel2.writeNoException();
                        parcel4.writeInt(radioNum);
                        return true;
                    case 16:
                        parcel3.enforceInterface(DESCRIPTOR);
                        boolean radioRDSState = getRadioRDSState();
                        parcel2.writeNoException();
                        parcel4.writeInt(radioRDSState ? 1 : 0);
                        return true;
                    case 17:
                        parcel3.enforceInterface(DESCRIPTOR);
                        boolean radioPTYState = getRadioPTYState();
                        parcel2.writeNoException();
                        parcel4.writeInt(radioPTYState ? 1 : 0);
                        return true;
                    case 18:
                        parcel3.enforceInterface(DESCRIPTOR);
                        int radioPTYNum = getRadioPTYNum();
                        parcel2.writeNoException();
                        parcel4.writeInt(radioPTYNum);
                        return true;
                    case 19:
                        parcel3.enforceInterface(DESCRIPTOR);
                        String radioPTYName = getRadioPTYName();
                        parcel2.writeNoException();
                        parcel4.writeString(radioPTYName);
                        return true;
                    case 20:
                        parcel3.enforceInterface(DESCRIPTOR);
                        boolean radioAFState = getRadioAFState();
                        parcel2.writeNoException();
                        parcel4.writeInt(radioAFState ? 1 : 0);
                        return true;
                    case 21:
                        parcel3.enforceInterface(DESCRIPTOR);
                        boolean radioTAState = getRadioTAState();
                        parcel2.writeNoException();
                        parcel4.writeInt(radioTAState ? 1 : 0);
                        return true;
                    case 22:
                        parcel3.enforceInterface(DESCRIPTOR);
                        boolean radioSTMonoState = getRadioSTMonoState();
                        parcel2.writeNoException();
                        parcel4.writeInt(radioSTMonoState ? 1 : 0);
                        return true;
                    case 23:
                        parcel3.enforceInterface(DESCRIPTOR);
                        boolean radioDXLOCState = getRadioDXLOCState();
                        parcel2.writeNoException();
                        parcel4.writeInt(radioDXLOCState ? 1 : 0);
                        return true;
                    case 24:
                        parcel3.enforceInterface(DESCRIPTOR);
                        boolean radioAMSState = getRadioAMSState();
                        parcel2.writeNoException();
                        parcel4.writeInt(radioAMSState ? 1 : 0);
                        return true;
                    case 25:
                        parcel3.enforceInterface(DESCRIPTOR);
                        boolean radioAPSState = getRadioAPSState();
                        parcel2.writeNoException();
                        parcel4.writeInt(radioAPSState ? 1 : 0);
                        return true;
                    case 26:
                        parcel3.enforceInterface(DESCRIPTOR);
                        boolean radioSteroIconState = getRadioSteroIconState();
                        parcel2.writeNoException();
                        parcel4.writeInt(radioSteroIconState ? 1 : 0);
                        return true;
                    case 27:
                        parcel3.enforceInterface(DESCRIPTOR);
                        boolean radioTPIconState = getRadioTPIconState();
                        parcel2.writeNoException();
                        parcel4.writeInt(radioTPIconState ? 1 : 0);
                        return true;
                    case 28:
                        parcel3.enforceInterface(DESCRIPTOR);
                        boolean radioTrafficState = getRadioTrafficState();
                        parcel2.writeNoException();
                        parcel4.writeInt(radioTrafficState ? 1 : 0);
                        return true;
                    case 29:
                        parcel3.enforceInterface(DESCRIPTOR);
                        setRadioCallback(ICallbackfn.Stub.asInterface(parcel.readStrongBinder()));
                        parcel2.writeNoException();
                        return true;
                    case 30:
                        parcel3.enforceInterface(DESCRIPTOR);
                        setCurModeCallback(parcel.readInt(), ICallbackfn.Stub.asInterface(parcel.readStrongBinder()));
                        parcel2.writeNoException();
                        return true;
                    case 31:
                        parcel3.enforceInterface(DESCRIPTOR);
                        exitCurMode(parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 32:
                        parcel3.enforceInterface(DESCRIPTOR);
                        String mCUVer = getMCUVer();
                        parcel2.writeNoException();
                        parcel4.writeString(mCUVer);
                        return true;
                    case 33:
                        parcel3.enforceInterface(DESCRIPTOR);
                        putSettingStr(parcel.readString(), parcel.readString());
                        parcel2.writeNoException();
                        return true;
                    case 34:
                        parcel3.enforceInterface(DESCRIPTOR);
                        putSettingInt(parcel.readString(), parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 35:
                        parcel3.enforceInterface(DESCRIPTOR);
                        putSettingLong(parcel.readString(), parcel.readLong());
                        parcel2.writeNoException();
                        return true;
                    case 36:
                        parcel3.enforceInterface(DESCRIPTOR);
                        putSettingFloat(parcel.readString(), parcel.readFloat());
                        parcel2.writeNoException();
                        return true;
                    case 37:
                        parcel3.enforceInterface(DESCRIPTOR);
                        String readString = parcel.readString();
                        if (parcel.readInt() != 0) {
                            z = true;
                        }
                        putSettingBoolean(readString, z);
                        parcel2.writeNoException();
                        return true;
                    case 38:
                        parcel3.enforceInterface(DESCRIPTOR);
                        commitSetting();
                        parcel2.writeNoException();
                        return true;
                    case 39:
                        parcel3.enforceInterface(DESCRIPTOR);
                        appySetting();
                        parcel2.writeNoException();
                        return true;
                    case 40:
                        parcel3.enforceInterface(DESCRIPTOR);
                        String readString2 = parcel.readString();
                        if (parcel.readInt() != 0) {
                            z = true;
                        }
                        boolean settingBoolean = getSettingBoolean(readString2, z);
                        parcel2.writeNoException();
                        parcel4.writeInt(settingBoolean ? 1 : 0);
                        return true;
                    case 41:
                        parcel3.enforceInterface(DESCRIPTOR);
                        float settingFloat = getSettingFloat(parcel.readString(), parcel.readFloat());
                        parcel2.writeNoException();
                        parcel4.writeFloat(settingFloat);
                        return true;
                    case 42:
                        parcel3.enforceInterface(DESCRIPTOR);
                        int settingInt = getSettingInt(parcel.readString(), parcel.readInt());
                        parcel2.writeNoException();
                        parcel4.writeInt(settingInt);
                        return true;
                    case 43:
                        parcel3.enforceInterface(DESCRIPTOR);
                        long settingLong = getSettingLong(parcel.readString(), parcel.readLong());
                        parcel2.writeNoException();
                        parcel4.writeLong(settingLong);
                        return true;
                    case 44:
                        parcel3.enforceInterface(DESCRIPTOR);
                        String settingString = getSettingString(parcel.readString(), parcel.readString());
                        parcel2.writeNoException();
                        parcel4.writeString(settingString);
                        return true;
                    case 45:
                        parcel3.enforceInterface(DESCRIPTOR);
                        int readInt2 = parcel.readInt();
                        int readInt3 = parcel.readInt();
                        if (parcel.readInt() != 0) {
                            z = true;
                        }
                        sendTouchPos(readInt2, readInt3, z);
                        parcel2.writeNoException();
                        return true;
                    case 46:
                        parcel3.enforceInterface(DESCRIPTOR);
                        int validMode = getValidMode();
                        parcel2.writeNoException();
                        parcel4.writeInt(validMode);
                        return true;
                    case 47:
                        parcel3.enforceInterface(DESCRIPTOR);
                        SendFactorySetToMCU(parcel.readByte(), parcel.readByte(), parcel.readByte(), parcel.readByte(), parcel.readByte(), parcel.readByte());
                        parcel2.writeNoException();
                        return true;
                    case 48:
                        parcel3.enforceInterface(DESCRIPTOR);
                        byte mainVolval = getMainVolval();
                        parcel2.writeNoException();
                        parcel4.writeByte(mainVolval);
                        return true;
                    case 49:
                        parcel3.enforceInterface(DESCRIPTOR);
                        byte bassVal = getBassVal();
                        parcel2.writeNoException();
                        parcel4.writeByte(bassVal);
                        return true;
                    case 50:
                        parcel3.enforceInterface(DESCRIPTOR);
                        byte trebleVal = getTrebleVal();
                        parcel2.writeNoException();
                        parcel4.writeByte(trebleVal);
                        return true;
                    case 51:
                        parcel3.enforceInterface(DESCRIPTOR);
                        byte middleVal = getMiddleVal();
                        parcel2.writeNoException();
                        parcel4.writeByte(middleVal);
                        return true;
                    case 52:
                        parcel3.enforceInterface(DESCRIPTOR);
                        byte bassFre = getBassFre();
                        parcel2.writeNoException();
                        parcel4.writeByte(bassFre);
                        return true;
                    case 53:
                        parcel3.enforceInterface(DESCRIPTOR);
                        byte middleFre = getMiddleFre();
                        parcel2.writeNoException();
                        parcel4.writeByte(middleFre);
                        return true;
                    case 54:
                        parcel3.enforceInterface(DESCRIPTOR);
                        byte trebleFre = getTrebleFre();
                        parcel2.writeNoException();
                        parcel4.writeByte(trebleFre);
                        return true;
                    case 55:
                        parcel3.enforceInterface(DESCRIPTOR);
                        byte bALVal = getBALVal();
                        parcel2.writeNoException();
                        parcel4.writeByte(bALVal);
                        return true;
                    case 56:
                        parcel3.enforceInterface(DESCRIPTOR);
                        byte fADVal = getFADVal();
                        parcel2.writeNoException();
                        parcel4.writeByte(fADVal);
                        return true;
                    case 57:
                        parcel3.enforceInterface(DESCRIPTOR);
                        byte eQMode = getEQMode();
                        parcel2.writeNoException();
                        parcel4.writeByte(eQMode);
                        return true;
                    case 58:
                        parcel3.enforceInterface(DESCRIPTOR);
                        byte userBassVal = getUserBassVal();
                        parcel2.writeNoException();
                        parcel4.writeByte(userBassVal);
                        return true;
                    case 59:
                        parcel3.enforceInterface(DESCRIPTOR);
                        byte userTrebleVal = getUserTrebleVal();
                        parcel2.writeNoException();
                        parcel4.writeByte(userTrebleVal);
                        return true;
                    case 60:
                        parcel3.enforceInterface(DESCRIPTOR);
                        byte userMiddleVal = getUserMiddleVal();
                        parcel2.writeNoException();
                        parcel4.writeByte(userMiddleVal);
                        return true;
                    case 61:
                        parcel3.enforceInterface(DESCRIPTOR);
                        boolean muteStatus = getMuteStatus();
                        parcel2.writeNoException();
                        parcel4.writeInt(muteStatus ? 1 : 0);
                        return true;
                    case 62:
                        parcel3.enforceInterface(DESCRIPTOR);
                        boolean loudStatus = getLoudStatus();
                        parcel2.writeNoException();
                        parcel4.writeInt(loudStatus ? 1 : 0);
                        return true;
                    case 63:
                        parcel3.enforceInterface(DESCRIPTOR);
                        SendAudioSetToMCU(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 64:
                        parcel3.enforceInterface(DESCRIPTOR);
                        SendWheelKey(parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 65:
                        parcel3.enforceInterface(DESCRIPTOR);
                        SendSysRTCTimeMCU(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 66:
                        parcel3.enforceInterface(DESCRIPTOR);
                        SendDVRKey(parcel.readByte());
                        parcel2.writeNoException();
                        return true;
                    case 67:
                        parcel3.enforceInterface(DESCRIPTOR);
                        SendMainVol(parcel.readByte());
                        parcel2.writeNoException();
                        return true;
                    case 68:
                        parcel3.enforceInterface(DESCRIPTOR);
                        SendBLVal(parcel.readByte(), parcel.readByte());
                        parcel2.writeNoException();
                        return true;
                    case 69:
                        parcel3.enforceInterface(DESCRIPTOR);
                        setTVCallback(ICallbackfn.Stub.asInterface(parcel.readStrongBinder()));
                        parcel2.writeNoException();
                        return true;
                    case 70:
                        parcel3.enforceInterface(DESCRIPTOR);
                        setAUXCallback(ICallbackfn.Stub.asInterface(parcel.readStrongBinder()));
                        parcel2.writeNoException();
                        return true;
                    case 71:
                        parcel3.enforceInterface(DESCRIPTOR);
                        SendBALFADVal(parcel.readByte(), parcel.readByte());
                        parcel2.writeNoException();
                        return true;
                    case 72:
                        parcel3.enforceInterface(DESCRIPTOR);
                        if (parcel.readInt() != 0) {
                            z = true;
                        }
                        SendBlackState(z);
                        parcel2.writeNoException();
                        return true;
                    case 73:
                        parcel3.enforceInterface(DESCRIPTOR);
                        boolean IsBrakeConneted = IsBrakeConneted();
                        parcel2.writeNoException();
                        parcel4.writeInt(IsBrakeConneted ? 1 : 0);
                        return true;
                    case 74:
                        parcel3.enforceInterface(DESCRIPTOR);
                        boolean IsMCUUpgradeWriteErr = IsMCUUpgradeWriteErr();
                        parcel2.writeNoException();
                        parcel4.writeInt(IsMCUUpgradeWriteErr ? 1 : 0);
                        return true;
                    case 75:
                        parcel3.enforceInterface(DESCRIPTOR);
                        SetVideoCH(parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 76:
                        parcel3.enforceInterface(DESCRIPTOR);
                        int GetSignalStatus = GetSignalStatus();
                        parcel2.writeNoException();
                        parcel4.writeInt(GetSignalStatus);
                        return true;
                    case 77:
                        parcel3.enforceInterface(DESCRIPTOR);
                        if (parcel.readInt() != 0) {
                            z = true;
                        }
                        OpenVideo(z);
                        parcel2.writeNoException();
                        return true;
                    case 78:
                        parcel3.enforceInterface(DESCRIPTOR);
                        SetVideoSize(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 79:
                        parcel3.enforceInterface(DESCRIPTOR);
                        boolean IsMuteOn = IsMuteOn();
                        parcel2.writeNoException();
                        parcel4.writeInt(IsMuteOn ? 1 : 0);
                        return true;
                    case 80:
                        parcel3.enforceInterface(DESCRIPTOR);
                        initRadioZone(parcel.readByte());
                        parcel2.writeNoException();
                        return true;
                    case 81:
                        parcel3.enforceInterface(DESCRIPTOR);
                        sendCanbusData(parcel.createByteArray());
                        parcel2.writeNoException();
                        return true;
                    case 82:
                        parcel3.enforceInterface(DESCRIPTOR);
                        setValidModeInfor(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 83:
                        parcel3.enforceInterface(DESCRIPTOR);
                        String validModeTitleInfor = getValidModeTitleInfor();
                        parcel2.writeNoException();
                        parcel4.writeString(validModeTitleInfor);
                        return true;
                    case 84:
                        parcel3.enforceInterface(DESCRIPTOR);
                        String validModeAblumInfor = getValidModeAblumInfor();
                        parcel2.writeNoException();
                        parcel4.writeString(validModeAblumInfor);
                        return true;
                    case 85:
                        parcel3.enforceInterface(DESCRIPTOR);
                        String validModeArtistInfor = getValidModeArtistInfor();
                        parcel2.writeNoException();
                        parcel4.writeString(validModeArtistInfor);
                        return true;
                    case 86:
                        parcel3.enforceInterface(DESCRIPTOR);
                        int validCurTrack = getValidCurTrack();
                        parcel2.writeNoException();
                        parcel4.writeInt(validCurTrack);
                        return true;
                    case 87:
                        parcel3.enforceInterface(DESCRIPTOR);
                        int validTotTrack = getValidTotTrack();
                        parcel2.writeNoException();
                        parcel4.writeInt(validTotTrack);
                        return true;
                    case 88:
                        parcel3.enforceInterface(DESCRIPTOR);
                        int validCurTime = getValidCurTime();
                        parcel2.writeNoException();
                        parcel4.writeInt(validCurTime);
                        return true;
                    case 89:
                        parcel3.enforceInterface(DESCRIPTOR);
                        int validTotTime = getValidTotTime();
                        parcel2.writeNoException();
                        parcel4.writeInt(validTotTime);
                        return true;
                    case 90:
                        parcel3.enforceInterface(DESCRIPTOR);
                        int validCurFolder = getValidCurFolder();
                        parcel2.writeNoException();
                        parcel4.writeInt(validCurFolder);
                        return true;
                    case 91:
                        parcel3.enforceInterface(DESCRIPTOR);
                        int validTotFolder = getValidTotFolder();
                        parcel2.writeNoException();
                        parcel4.writeInt(validTotFolder);
                        return true;
                    case 92:
                        parcel3.enforceInterface(DESCRIPTOR);
                        int validLoopMode = getValidLoopMode();
                        parcel2.writeNoException();
                        parcel4.writeInt(validLoopMode);
                        return true;
                    case 93:
                        parcel3.enforceInterface(DESCRIPTOR);
                        int validRepeatMode = getValidRepeatMode();
                        parcel2.writeNoException();
                        parcel4.writeInt(validRepeatMode);
                        return true;
                    case 94:
                        parcel3.enforceInterface(DESCRIPTOR);
                        int validPlayStatus = getValidPlayStatus();
                        parcel2.writeNoException();
                        parcel4.writeInt(validPlayStatus);
                        return true;
                    case 95:
                        parcel3.enforceInterface(DESCRIPTOR);
                        SetDVDVideoCH(parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 96:
                        parcel3.enforceInterface(DESCRIPTOR);
                        if (parcel.readInt() != 0) {
                            z = true;
                        }
                        sendResetDVD(z);
                        parcel2.writeNoException();
                        return true;
                    case 97:
                        parcel3.enforceInterface(DESCRIPTOR);
                        boolean IsDiscConneted = IsDiscConneted();
                        parcel2.writeNoException();
                        parcel4.writeInt(IsDiscConneted ? 1 : 0);
                        return true;
                    case 98:
                        parcel3.enforceInterface(DESCRIPTOR);
                        setCarMediaCallback(ICallbackfn.Stub.asInterface(parcel.readStrongBinder()));
                        parcel2.writeNoException();
                        return true;
                    case 99:
                        parcel3.enforceInterface(DESCRIPTOR);
                        if (parcel.readInt() != 0) {
                            z = true;
                        }
                        setMcuInitStatus(z);
                        parcel2.writeNoException();
                        return true;
                    case 100:
                        parcel3.enforceInterface(DESCRIPTOR);
                        boolean mcuInitStatus = getMcuInitStatus();
                        parcel2.writeNoException();
                        parcel4.writeInt(mcuInitStatus ? 1 : 0);
                        return true;
                    case 101:
                        parcel3.enforceInterface(DESCRIPTOR);
                        enterUpgradeMode();
                        parcel2.writeNoException();
                        return true;
                    case 102:
                        parcel3.enforceInterface(DESCRIPTOR);
                        exitUpgradeMode();
                        parcel2.writeNoException();
                        return true;
                    case 103:
                        parcel3.enforceInterface(DESCRIPTOR);
                        boolean sendMcuUpgradeMode = sendMcuUpgradeMode(parcel.readInt());
                        parcel2.writeNoException();
                        parcel4.writeInt(sendMcuUpgradeMode ? 1 : 0);
                        return true;
                    case 104:
                        parcel3.enforceInterface(DESCRIPTOR);
                        boolean sendMcuUpgradeData = sendMcuUpgradeData(parcel.readLong(), parcel.createByteArray(), parcel.readInt(), parcel.readInt(), parcel.readInt() != 0);
                        parcel2.writeNoException();
                        parcel4.writeInt(sendMcuUpgradeData ? 1 : 0);
                        return true;
                    case 105:
                        parcel3.enforceInterface(DESCRIPTOR);
                        boolean isUpgradeMode = isUpgradeMode();
                        parcel2.writeNoException();
                        parcel4.writeInt(isUpgradeMode ? 1 : 0);
                        return true;
                    case 106:
                        parcel3.enforceInterface(DESCRIPTOR);
                        SendGPSVolToMCU(parcel.readByte());
                        parcel2.writeNoException();
                        return true;
                    case 107:
                        parcel3.enforceInterface(DESCRIPTOR);
                        SendFactorySet();
                        parcel2.writeNoException();
                        return true;
                    case 108:
                        parcel3.enforceInterface(DESCRIPTOR);
                        int GetBTStatus = GetBTStatus();
                        parcel2.writeNoException();
                        parcel4.writeInt(GetBTStatus);
                        return true;
                    case 109:
                        parcel3.enforceInterface(DESCRIPTOR);
                        boolean IsBackcarConnected = IsBackcarConnected();
                        parcel2.writeNoException();
                        parcel4.writeInt(IsBackcarConnected ? 1 : 0);
                        return true;
                    case 110:
                        parcel3.enforceInterface(DESCRIPTOR);
                        int LoadNLightVal = LoadNLightVal();
                        parcel2.writeNoException();
                        parcel4.writeInt(LoadNLightVal);
                        return true;
                    case 111:
                        parcel3.enforceInterface(DESCRIPTOR);
                        SetCurrDim(parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 112:
                        parcel3.enforceInterface(DESCRIPTOR);
                        int GetCurrDim = GetCurrDim();
                        parcel2.writeNoException();
                        parcel4.writeInt(GetCurrDim);
                        return true;
                    case 113:
                        parcel3.enforceInterface(DESCRIPTOR);
                        String tFTVer = getTFTVer();
                        parcel2.writeNoException();
                        parcel4.writeString(tFTVer);
                        return true;
                    case 114:
                        parcel3.enforceInterface(DESCRIPTOR);
                        boolean Send8902McuUpgradeData = Send8902McuUpgradeData(parcel.createByteArray(), parcel.readInt());
                        parcel2.writeNoException();
                        parcel4.writeInt(Send8902McuUpgradeData ? 1 : 0);
                        return true;
                    case 115:
                        parcel3.enforceInterface(DESCRIPTOR);
                        if (parcel.readInt() != 0) {
                            z = true;
                        }
                        SendVol_KSW(z, parcel.readInt(), parcel.readInt(), parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 116:
                        parcel3.enforceInterface(DESCRIPTOR);
                        boolean z2 = get_kesaiwei_chk_Video_Driving_Ban();
                        parcel2.writeNoException();
                        parcel4.writeInt(z2 ? 1 : 0);
                        return true;
                    case 117:
                        parcel3.enforceInterface(DESCRIPTOR);
                        boolean z3 = get_kesaiwei_bPark();
                        parcel2.writeNoException();
                        parcel4.writeInt(z3 ? 1 : 0);
                        return true;
                    case 118:
                        parcel3.enforceInterface(DESCRIPTOR);
                        sendDvdDataToMcu(parcel.createByteArray());
                        parcel2.writeNoException();
                        return true;
                    case 119:
                        parcel3.enforceInterface(DESCRIPTOR);
                        sendMcuData_KSW(parcel.createByteArray());
                        parcel2.writeNoException();
                        return true;
                    case 120:
                        parcel3.enforceInterface(DESCRIPTOR);
                        setDashBoardCallback(ICallbackfn.Stub.asInterface(parcel.readStrongBinder()));
                        parcel2.writeNoException();
                        return true;
                    case 121:
                        parcel3.enforceInterface(DESCRIPTOR);
                        setGpsFocusCallback(ICallbackfn.Stub.asInterface(parcel.readStrongBinder()));
                        parcel2.writeNoException();
                        return true;
                    case 122:
                        parcel3.enforceInterface(DESCRIPTOR);
                        int i4 = get_m_i_easyconn_state_KSW();
                        parcel2.writeNoException();
                        parcel4.writeInt(i4);
                        return true;
                    case 123:
                        parcel3.enforceInterface(DESCRIPTOR);
                        send_KSW_page2_vol_info(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 124:
                        parcel3.enforceInterface(DESCRIPTOR);
                        int cameraOwner = getCameraOwner();
                        parcel2.writeNoException();
                        parcel4.writeInt(cameraOwner);
                        return true;
                    case TRANSACTION_setCameraOwner /*125*/:
                        parcel3.enforceInterface(DESCRIPTOR);
                        setCameraOwner(parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case TRANSACTION_openTVout /*126*/:
                        parcel3.enforceInterface(DESCRIPTOR);
                        int readInt4 = parcel.readInt();
                        if (parcel.readInt() != 0) {
                            z = true;
                        }
                        openTVout(readInt4, z);
                        parcel2.writeNoException();
                        return true;
                    case 127:
                        parcel3.enforceInterface(DESCRIPTOR);
                        sendToOSData();
                        parcel2.writeNoException();
                        return true;
                    case 128:
                        parcel3.enforceInterface(DESCRIPTOR);
                        boolean z4 = getmIsAddMouseView();
                        parcel2.writeNoException();
                        parcel4.writeInt(z4 ? 1 : 0);
                        return true;
                    case TRANSACTION_sendKeyDownUpSync /*129*/:
                        parcel3.enforceInterface(DESCRIPTOR);
                        sendKeyDownUpSync(parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case TRANSACTION_enterCanUpgradeMode /*130*/:
                        parcel3.enforceInterface(DESCRIPTOR);
                        enterCanUpgradeMode();
                        parcel2.writeNoException();
                        return true;
                    case TRANSACTION_exitCanUpgradeMode /*131*/:
                        parcel3.enforceInterface(DESCRIPTOR);
                        exitCanUpgradeMode();
                        parcel2.writeNoException();
                        return true;
                    case TRANSACTION_sendCanbusUpgradeData /*132*/:
                        parcel3.enforceInterface(DESCRIPTOR);
                        sendCanbusUpgradeData(parcel.createByteArray());
                        parcel2.writeNoException();
                        return true;
                    case 133:
                        parcel3.enforceInterface(DESCRIPTOR);
                        responseCanUpgradeMode();
                        parcel2.writeNoException();
                        return true;
                    case TRANSACTION_responseCanUpgradeEvent /*134*/:
                        parcel3.enforceInterface(DESCRIPTOR);
                        responseCanUpgradeEvent(parcel.readByte());
                        parcel2.writeNoException();
                        return true;
                    case TRANSACTION_responseCanUpgradeDataEvent /*135*/:
                        parcel3.enforceInterface(DESCRIPTOR);
                        responseCanUpgradeDataEvent(parcel.readByte());
                        parcel2.writeNoException();
                        return true;
                    default:
                        return super.onTransact(i, parcel, parcel2, i2);
                }
            } else {
                parcel4.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements IEventService {
            public static IEventService sDefaultImpl;
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void sendMode(int i, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(1, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().sendMode(i, z);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void sendRadioKey(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(2, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().sendRadioKey(i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void sendTVKey(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(3, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().sendTVKey(i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void sendSystemKey(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(4, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().sendSystemKey(i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void sendEQMode(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(5, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().sendEQMode(i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void sendUserFreq(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(6, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().sendUserFreq(i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void beep() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(7, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().beep();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void sendMuteState(boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(8, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().sendMuteState(z);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void sendPlayState(boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(9, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().sendPlayState(z);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void sendSetup(byte b, byte b2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeByte(b);
                    obtain.writeByte(b2);
                    if (this.mRemote.transact(10, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().sendSetup(b, b2);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void sendBTState(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(11, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().sendBTState(i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getRadioFreq() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(12, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getRadioFreq();
                    }
                    obtain2.readException();
                    int readInt = obtain2.readInt();
                    obtain2.recycle();
                    obtain.recycle();
                    return readInt;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int[] getRadioFreqList() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(13, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getRadioFreqList();
                    }
                    obtain2.readException();
                    int[] createIntArray = obtain2.createIntArray();
                    obtain2.recycle();
                    obtain.recycle();
                    return createIntArray;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getRadioBand() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(14, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getRadioBand();
                    }
                    obtain2.readException();
                    int readInt = obtain2.readInt();
                    obtain2.recycle();
                    obtain.recycle();
                    return readInt;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getRadioNum() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(15, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getRadioNum();
                    }
                    obtain2.readException();
                    int readInt = obtain2.readInt();
                    obtain2.recycle();
                    obtain.recycle();
                    return readInt;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean getRadioRDSState() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(16, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getRadioRDSState();
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean getRadioPTYState() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(17, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getRadioPTYState();
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getRadioPTYNum() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(18, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getRadioPTYNum();
                    }
                    obtain2.readException();
                    int readInt = obtain2.readInt();
                    obtain2.recycle();
                    obtain.recycle();
                    return readInt;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getRadioPTYName() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(19, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getRadioPTYName();
                    }
                    obtain2.readException();
                    String readString = obtain2.readString();
                    obtain2.recycle();
                    obtain.recycle();
                    return readString;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean getRadioAFState() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(20, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getRadioAFState();
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean getRadioTAState() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(21, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getRadioTAState();
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean getRadioSTMonoState() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(22, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getRadioSTMonoState();
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean getRadioDXLOCState() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(23, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getRadioDXLOCState();
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean getRadioAMSState() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(24, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getRadioAMSState();
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean getRadioAPSState() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(25, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getRadioAPSState();
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean getRadioSteroIconState() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(26, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getRadioSteroIconState();
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean getRadioTPIconState() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(27, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getRadioTPIconState();
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean getRadioTrafficState() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(28, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getRadioTrafficState();
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setRadioCallback(ICallbackfn iCallbackfn) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iCallbackfn != null ? iCallbackfn.asBinder() : null);
                    if (this.mRemote.transact(29, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setRadioCallback(iCallbackfn);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setCurModeCallback(int i, ICallbackfn iCallbackfn) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeStrongBinder(iCallbackfn != null ? iCallbackfn.asBinder() : null);
                    if (this.mRemote.transact(30, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setCurModeCallback(i, iCallbackfn);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void exitCurMode(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(31, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().exitCurMode(i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getMCUVer() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(32, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getMCUVer();
                    }
                    obtain2.readException();
                    String readString = obtain2.readString();
                    obtain2.recycle();
                    obtain.recycle();
                    return readString;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void putSettingStr(String str, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    if (this.mRemote.transact(33, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().putSettingStr(str, str2);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void putSettingInt(String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(34, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().putSettingInt(str, i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void putSettingLong(String str, long j) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeLong(j);
                    if (this.mRemote.transact(35, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().putSettingLong(str, j);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void putSettingFloat(String str, float f) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeFloat(f);
                    if (this.mRemote.transact(36, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().putSettingFloat(str, f);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void putSettingBoolean(String str, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(37, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().putSettingBoolean(str, z);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void commitSetting() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(38, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().commitSetting();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void appySetting() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(39, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().appySetting();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean getSettingBoolean(String str, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    boolean z2 = true;
                    obtain.writeInt(z ? 1 : 0);
                    if (!this.mRemote.transact(40, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getSettingBoolean(str, z);
                    }
                    obtain2.readException();
                    if (obtain2.readInt() == 0) {
                        z2 = false;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z2;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public float getSettingFloat(String str, float f) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeFloat(f);
                    if (!this.mRemote.transact(41, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getSettingFloat(str, f);
                    }
                    obtain2.readException();
                    float readFloat = obtain2.readFloat();
                    obtain2.recycle();
                    obtain.recycle();
                    return readFloat;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getSettingInt(String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    if (!this.mRemote.transact(42, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getSettingInt(str, i);
                    }
                    obtain2.readException();
                    int readInt = obtain2.readInt();
                    obtain2.recycle();
                    obtain.recycle();
                    return readInt;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public long getSettingLong(String str, long j) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeLong(j);
                    if (!this.mRemote.transact(43, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getSettingLong(str, j);
                    }
                    obtain2.readException();
                    long readLong = obtain2.readLong();
                    obtain2.recycle();
                    obtain.recycle();
                    return readLong;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getSettingString(String str, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    if (!this.mRemote.transact(44, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getSettingString(str, str2);
                    }
                    obtain2.readException();
                    String readString = obtain2.readString();
                    obtain2.recycle();
                    obtain.recycle();
                    return readString;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void sendTouchPos(int i, int i2, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(45, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().sendTouchPos(i, i2, z);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getValidMode() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(46, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getValidMode();
                    }
                    obtain2.readException();
                    int readInt = obtain2.readInt();
                    obtain2.recycle();
                    obtain.recycle();
                    return readInt;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void SendFactorySetToMCU(byte b, byte b2, byte b3, byte b4, byte b5, byte b6) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    byte b7 = b;
                    obtain.writeByte(b);
                    byte b8 = b2;
                    obtain.writeByte(b2);
                    byte b9 = b3;
                    obtain.writeByte(b3);
                    obtain.writeByte(b4);
                    obtain.writeByte(b5);
                    obtain.writeByte(b6);
                    try {
                        if (this.mRemote.transact(47, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                            obtain2.readException();
                            obtain2.recycle();
                            obtain.recycle();
                            return;
                        }
                        Stub.getDefaultImpl().SendFactorySetToMCU(b, b2, b3, b4, b5, b6);
                        obtain2.recycle();
                        obtain.recycle();
                    } catch (Throwable th) {
                        th = th;
                        obtain2.recycle();
                        obtain.recycle();
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    obtain2.recycle();
                    obtain.recycle();
                    throw th;
                }
            }

            public byte getMainVolval() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(48, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getMainVolval();
                    }
                    obtain2.readException();
                    byte readByte = obtain2.readByte();
                    obtain2.recycle();
                    obtain.recycle();
                    return readByte;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public byte getBassVal() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(49, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getBassVal();
                    }
                    obtain2.readException();
                    byte readByte = obtain2.readByte();
                    obtain2.recycle();
                    obtain.recycle();
                    return readByte;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public byte getTrebleVal() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(50, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getTrebleVal();
                    }
                    obtain2.readException();
                    byte readByte = obtain2.readByte();
                    obtain2.recycle();
                    obtain.recycle();
                    return readByte;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public byte getMiddleVal() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(51, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getMiddleVal();
                    }
                    obtain2.readException();
                    byte readByte = obtain2.readByte();
                    obtain2.recycle();
                    obtain.recycle();
                    return readByte;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public byte getBassFre() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(52, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getBassFre();
                    }
                    obtain2.readException();
                    byte readByte = obtain2.readByte();
                    obtain2.recycle();
                    obtain.recycle();
                    return readByte;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public byte getMiddleFre() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(53, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getMiddleFre();
                    }
                    obtain2.readException();
                    byte readByte = obtain2.readByte();
                    obtain2.recycle();
                    obtain.recycle();
                    return readByte;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public byte getTrebleFre() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(54, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getTrebleFre();
                    }
                    obtain2.readException();
                    byte readByte = obtain2.readByte();
                    obtain2.recycle();
                    obtain.recycle();
                    return readByte;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public byte getBALVal() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(55, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getBALVal();
                    }
                    obtain2.readException();
                    byte readByte = obtain2.readByte();
                    obtain2.recycle();
                    obtain.recycle();
                    return readByte;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public byte getFADVal() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(56, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getFADVal();
                    }
                    obtain2.readException();
                    byte readByte = obtain2.readByte();
                    obtain2.recycle();
                    obtain.recycle();
                    return readByte;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public byte getEQMode() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(57, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getEQMode();
                    }
                    obtain2.readException();
                    byte readByte = obtain2.readByte();
                    obtain2.recycle();
                    obtain.recycle();
                    return readByte;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public byte getUserBassVal() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(58, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getUserBassVal();
                    }
                    obtain2.readException();
                    byte readByte = obtain2.readByte();
                    obtain2.recycle();
                    obtain.recycle();
                    return readByte;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public byte getUserTrebleVal() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(59, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getUserTrebleVal();
                    }
                    obtain2.readException();
                    byte readByte = obtain2.readByte();
                    obtain2.recycle();
                    obtain.recycle();
                    return readByte;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public byte getUserMiddleVal() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(60, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getUserMiddleVal();
                    }
                    obtain2.readException();
                    byte readByte = obtain2.readByte();
                    obtain2.recycle();
                    obtain.recycle();
                    return readByte;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean getMuteStatus() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(61, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getMuteStatus();
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean getLoudStatus() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(62, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getLoudStatus();
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void SendAudioSetToMCU(int i, int i2, int i3, int i4, int i5, int i6) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    int i7 = i;
                    obtain.writeInt(i);
                    int i8 = i2;
                    obtain.writeInt(i2);
                    int i9 = i3;
                    obtain.writeInt(i3);
                    obtain.writeInt(i4);
                    obtain.writeInt(i5);
                    obtain.writeInt(i6);
                    try {
                        if (this.mRemote.transact(63, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                            obtain2.readException();
                            obtain2.recycle();
                            obtain.recycle();
                            return;
                        }
                        Stub.getDefaultImpl().SendAudioSetToMCU(i, i2, i3, i4, i5, i6);
                        obtain2.recycle();
                        obtain.recycle();
                    } catch (Throwable th) {
                        th = th;
                        obtain2.recycle();
                        obtain.recycle();
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    obtain2.recycle();
                    obtain.recycle();
                    throw th;
                }
            }

            public void SendWheelKey(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(64, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().SendWheelKey(i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void SendSysRTCTimeMCU(int i, int i2, int i3, int i4, int i5, int i6) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    int i7 = i;
                    obtain.writeInt(i);
                    int i8 = i2;
                    obtain.writeInt(i2);
                    int i9 = i3;
                    obtain.writeInt(i3);
                    obtain.writeInt(i4);
                    obtain.writeInt(i5);
                    obtain.writeInt(i6);
                    try {
                        if (this.mRemote.transact(65, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                            obtain2.readException();
                            obtain2.recycle();
                            obtain.recycle();
                            return;
                        }
                        Stub.getDefaultImpl().SendSysRTCTimeMCU(i, i2, i3, i4, i5, i6);
                        obtain2.recycle();
                        obtain.recycle();
                    } catch (Throwable th) {
                        th = th;
                        obtain2.recycle();
                        obtain.recycle();
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    obtain2.recycle();
                    obtain.recycle();
                    throw th;
                }
            }

            public void SendDVRKey(byte b) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeByte(b);
                    if (this.mRemote.transact(66, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().SendDVRKey(b);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void SendMainVol(byte b) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeByte(b);
                    if (this.mRemote.transact(67, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().SendMainVol(b);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void SendBLVal(byte b, byte b2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeByte(b);
                    obtain.writeByte(b2);
                    if (this.mRemote.transact(68, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().SendBLVal(b, b2);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setTVCallback(ICallbackfn iCallbackfn) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iCallbackfn != null ? iCallbackfn.asBinder() : null);
                    if (this.mRemote.transact(69, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setTVCallback(iCallbackfn);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setAUXCallback(ICallbackfn iCallbackfn) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iCallbackfn != null ? iCallbackfn.asBinder() : null);
                    if (this.mRemote.transact(70, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setAUXCallback(iCallbackfn);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void SendBALFADVal(byte b, byte b2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeByte(b);
                    obtain.writeByte(b2);
                    if (this.mRemote.transact(71, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().SendBALFADVal(b, b2);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void SendBlackState(boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(72, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().SendBlackState(z);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean IsBrakeConneted() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(73, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().IsBrakeConneted();
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean IsMCUUpgradeWriteErr() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(74, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().IsMCUUpgradeWriteErr();
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void SetVideoCH(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(75, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().SetVideoCH(i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int GetSignalStatus() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(76, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().GetSignalStatus();
                    }
                    obtain2.readException();
                    int readInt = obtain2.readInt();
                    obtain2.recycle();
                    obtain.recycle();
                    return readInt;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void OpenVideo(boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(77, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().OpenVideo(z);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void SetVideoSize(int i, int i2, int i3, int i4) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    obtain.writeInt(i4);
                    if (this.mRemote.transact(78, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().SetVideoSize(i, i2, i3, i4);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean IsMuteOn() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(79, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().IsMuteOn();
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void initRadioZone(byte b) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeByte(b);
                    if (this.mRemote.transact(80, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().initRadioZone(b);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void sendCanbusData(byte[] bArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeByteArray(bArr);
                    if (this.mRemote.transact(81, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().sendCanbusData(bArr);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setValidModeInfor(String str, String str2, String str3, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    obtain.writeInt(i4);
                    obtain.writeInt(i5);
                    obtain.writeInt(i6);
                    obtain.writeInt(i7);
                    obtain.writeInt(i8);
                    obtain.writeInt(i9);
                    if (this.mRemote.transact(82, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setValidModeInfor(str, str2, str3, i, i2, i3, i4, i5, i6, i7, i8, i9);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getValidModeTitleInfor() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(83, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getValidModeTitleInfor();
                    }
                    obtain2.readException();
                    String readString = obtain2.readString();
                    obtain2.recycle();
                    obtain.recycle();
                    return readString;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getValidModeAblumInfor() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(84, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getValidModeAblumInfor();
                    }
                    obtain2.readException();
                    String readString = obtain2.readString();
                    obtain2.recycle();
                    obtain.recycle();
                    return readString;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getValidModeArtistInfor() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(85, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getValidModeArtistInfor();
                    }
                    obtain2.readException();
                    String readString = obtain2.readString();
                    obtain2.recycle();
                    obtain.recycle();
                    return readString;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getValidCurTrack() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(86, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getValidCurTrack();
                    }
                    obtain2.readException();
                    int readInt = obtain2.readInt();
                    obtain2.recycle();
                    obtain.recycle();
                    return readInt;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getValidTotTrack() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(87, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getValidTotTrack();
                    }
                    obtain2.readException();
                    int readInt = obtain2.readInt();
                    obtain2.recycle();
                    obtain.recycle();
                    return readInt;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getValidCurTime() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(88, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getValidCurTime();
                    }
                    obtain2.readException();
                    int readInt = obtain2.readInt();
                    obtain2.recycle();
                    obtain.recycle();
                    return readInt;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getValidTotTime() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(89, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getValidTotTime();
                    }
                    obtain2.readException();
                    int readInt = obtain2.readInt();
                    obtain2.recycle();
                    obtain.recycle();
                    return readInt;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getValidCurFolder() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(90, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getValidCurFolder();
                    }
                    obtain2.readException();
                    int readInt = obtain2.readInt();
                    obtain2.recycle();
                    obtain.recycle();
                    return readInt;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getValidTotFolder() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(91, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getValidTotFolder();
                    }
                    obtain2.readException();
                    int readInt = obtain2.readInt();
                    obtain2.recycle();
                    obtain.recycle();
                    return readInt;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getValidLoopMode() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(92, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getValidLoopMode();
                    }
                    obtain2.readException();
                    int readInt = obtain2.readInt();
                    obtain2.recycle();
                    obtain.recycle();
                    return readInt;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getValidRepeatMode() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(93, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getValidRepeatMode();
                    }
                    obtain2.readException();
                    int readInt = obtain2.readInt();
                    obtain2.recycle();
                    obtain.recycle();
                    return readInt;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getValidPlayStatus() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(94, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getValidPlayStatus();
                    }
                    obtain2.readException();
                    int readInt = obtain2.readInt();
                    obtain2.recycle();
                    obtain.recycle();
                    return readInt;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void SetDVDVideoCH(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(95, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().SetDVDVideoCH(i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void sendResetDVD(boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(96, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().sendResetDVD(z);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean IsDiscConneted() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(97, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().IsDiscConneted();
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setCarMediaCallback(ICallbackfn iCallbackfn) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iCallbackfn != null ? iCallbackfn.asBinder() : null);
                    if (this.mRemote.transact(98, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setCarMediaCallback(iCallbackfn);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setMcuInitStatus(boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(99, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setMcuInitStatus(z);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean getMcuInitStatus() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(100, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getMcuInitStatus();
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void enterUpgradeMode() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(101, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().enterUpgradeMode();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void exitUpgradeMode() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(102, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().exitUpgradeMode();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean sendMcuUpgradeMode(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    boolean z = false;
                    if (!this.mRemote.transact(103, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().sendMcuUpgradeMode(i);
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean sendMcuUpgradeData(long j, byte[] bArr, int i, int i2, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    long j2 = j;
                    obtain.writeLong(j);
                    byte[] bArr2 = bArr;
                    obtain.writeByteArray(bArr);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    boolean z2 = true;
                    obtain.writeInt(z ? 1 : 0);
                    try {
                        if (this.mRemote.transact(104, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                            obtain2.readException();
                            if (obtain2.readInt() == 0) {
                                z2 = false;
                            }
                            obtain2.recycle();
                            obtain.recycle();
                            return z2;
                        }
                        boolean sendMcuUpgradeData = Stub.getDefaultImpl().sendMcuUpgradeData(j, bArr, i, i2, z);
                        obtain2.recycle();
                        obtain.recycle();
                        return sendMcuUpgradeData;
                    } catch (Throwable th) {
                        th = th;
                        obtain2.recycle();
                        obtain.recycle();
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    obtain2.recycle();
                    obtain.recycle();
                    throw th;
                }
            }

            public boolean isUpgradeMode() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(105, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().isUpgradeMode();
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void SendGPSVolToMCU(byte b) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeByte(b);
                    if (this.mRemote.transact(106, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().SendGPSVolToMCU(b);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void SendFactorySet() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(107, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().SendFactorySet();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int GetBTStatus() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(108, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().GetBTStatus();
                    }
                    obtain2.readException();
                    int readInt = obtain2.readInt();
                    obtain2.recycle();
                    obtain.recycle();
                    return readInt;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean IsBackcarConnected() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(109, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().IsBackcarConnected();
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int LoadNLightVal() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(110, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().LoadNLightVal();
                    }
                    obtain2.readException();
                    int readInt = obtain2.readInt();
                    obtain2.recycle();
                    obtain.recycle();
                    return readInt;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void SetCurrDim(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(111, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().SetCurrDim(i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int GetCurrDim() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(112, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().GetCurrDim();
                    }
                    obtain2.readException();
                    int readInt = obtain2.readInt();
                    obtain2.recycle();
                    obtain.recycle();
                    return readInt;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getTFTVer() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(113, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getTFTVer();
                    }
                    obtain2.readException();
                    String readString = obtain2.readString();
                    obtain2.recycle();
                    obtain.recycle();
                    return readString;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean Send8902McuUpgradeData(byte[] bArr, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeByteArray(bArr);
                    obtain.writeInt(i);
                    boolean z = false;
                    if (!this.mRemote.transact(114, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().Send8902McuUpgradeData(bArr, i);
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void SendVol_KSW(boolean z, int i, int i2, int i3) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(z ? 1 : 0);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    if (this.mRemote.transact(115, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().SendVol_KSW(z, i, i2, i3);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean get_kesaiwei_chk_Video_Driving_Ban() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(116, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().get_kesaiwei_chk_Video_Driving_Ban();
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean get_kesaiwei_bPark() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(117, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().get_kesaiwei_bPark();
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void sendDvdDataToMcu(byte[] bArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeByteArray(bArr);
                    if (this.mRemote.transact(118, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().sendDvdDataToMcu(bArr);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void sendMcuData_KSW(byte[] bArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeByteArray(bArr);
                    if (this.mRemote.transact(119, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().sendMcuData_KSW(bArr);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setDashBoardCallback(ICallbackfn iCallbackfn) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iCallbackfn != null ? iCallbackfn.asBinder() : null);
                    if (this.mRemote.transact(120, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setDashBoardCallback(iCallbackfn);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setGpsFocusCallback(ICallbackfn iCallbackfn) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iCallbackfn != null ? iCallbackfn.asBinder() : null);
                    if (this.mRemote.transact(121, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setGpsFocusCallback(iCallbackfn);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int get_m_i_easyconn_state_KSW() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(122, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().get_m_i_easyconn_state_KSW();
                    }
                    obtain2.readException();
                    int readInt = obtain2.readInt();
                    obtain2.recycle();
                    obtain.recycle();
                    return readInt;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void send_KSW_page2_vol_info(int i, int i2, int i3, int i4) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    obtain.writeInt(i4);
                    if (this.mRemote.transact(123, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().send_KSW_page2_vol_info(i, i2, i3, i4);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getCameraOwner() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(124, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getCameraOwner();
                    }
                    obtain2.readException();
                    int readInt = obtain2.readInt();
                    obtain2.recycle();
                    obtain.recycle();
                    return readInt;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setCameraOwner(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(Stub.TRANSACTION_setCameraOwner, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setCameraOwner(i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void openTVout(int i, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(Stub.TRANSACTION_openTVout, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().openTVout(i, z);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void sendToOSData() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(127, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().sendToOSData();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean getmIsAddMouseView() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(128, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getmIsAddMouseView();
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void sendKeyDownUpSync(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(Stub.TRANSACTION_sendKeyDownUpSync, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().sendKeyDownUpSync(i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void enterCanUpgradeMode() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(Stub.TRANSACTION_enterCanUpgradeMode, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().enterCanUpgradeMode();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void exitCanUpgradeMode() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(Stub.TRANSACTION_exitCanUpgradeMode, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().exitCanUpgradeMode();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void sendCanbusUpgradeData(byte[] bArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeByteArray(bArr);
                    if (this.mRemote.transact(Stub.TRANSACTION_sendCanbusUpgradeData, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().sendCanbusUpgradeData(bArr);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void responseCanUpgradeMode() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(133, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().responseCanUpgradeMode();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void responseCanUpgradeEvent(byte b) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeByte(b);
                    if (this.mRemote.transact(Stub.TRANSACTION_responseCanUpgradeEvent, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().responseCanUpgradeEvent(b);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void responseCanUpgradeDataEvent(byte b) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeByte(b);
                    if (this.mRemote.transact(Stub.TRANSACTION_responseCanUpgradeDataEvent, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().responseCanUpgradeDataEvent(b);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IEventService iEventService) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            } else if (iEventService == null) {
                return false;
            } else {
                Proxy.sDefaultImpl = iEventService;
                return true;
            }
        }

        public static IEventService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
