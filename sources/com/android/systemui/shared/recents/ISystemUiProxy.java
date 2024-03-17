package com.android.systemui.shared.recents;

import android.graphics.Bitmap;
import android.graphics.Insets;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.MotionEvent;
import com.android.systemui.shared.recents.model.Task;

public interface ISystemUiProxy extends IInterface {

    public static class Default implements ISystemUiProxy {
        public IBinder asBinder() {
            return null;
        }

        public void expandNotificationPanel() throws RemoteException {
        }

        public Rect getNonMinimizedSplitScreenSecondaryBounds() throws RemoteException {
            return null;
        }

        public void handleImageAsScreenshot(Bitmap bitmap, Rect rect, Insets insets, int i) throws RemoteException {
        }

        public void handleImageBundleAsScreenshot(Bundle bundle, Rect rect, Insets insets, Task.TaskKey taskKey) throws RemoteException {
        }

        public void notifyAccessibilityButtonClicked(int i) throws RemoteException {
        }

        public void notifyAccessibilityButtonLongClicked() throws RemoteException {
        }

        public void notifyPrioritizedRotation(int i) throws RemoteException {
        }

        public void notifySwipeToHomeFinished() throws RemoteException {
        }

        public void notifySwipeUpGestureStarted() throws RemoteException {
        }

        public void notifyTaskbarAutohideSuspend(boolean z) throws RemoteException {
        }

        public void notifyTaskbarStatus(boolean z, boolean z2) throws RemoteException {
        }

        public void onAssistantGestureCompletion(float f) throws RemoteException {
        }

        public void onAssistantProgress(float f) throws RemoteException {
        }

        public void onBackPressed() throws RemoteException {
        }

        public void onImeSwitcherPressed() throws RemoteException {
        }

        public void onOverviewShown(boolean z) throws RemoteException {
        }

        public void onStatusBarMotionEvent(MotionEvent motionEvent) throws RemoteException {
        }

        public void setHomeRotationEnabled(boolean z) throws RemoteException {
        }

        public void setNavBarButtonAlpha(float f, boolean z) throws RemoteException {
        }

        public void setSplitScreenMinimized(boolean z) throws RemoteException {
        }

        public void startAssistant(Bundle bundle) throws RemoteException {
        }

        public void startScreenPinning(int i) throws RemoteException {
        }

        public void stopScreenPinning() throws RemoteException {
        }

        public void toggleNotificationPanel() throws RemoteException {
        }
    }

    void expandNotificationPanel() throws RemoteException;

    Rect getNonMinimizedSplitScreenSecondaryBounds() throws RemoteException;

    void handleImageAsScreenshot(Bitmap bitmap, Rect rect, Insets insets, int i) throws RemoteException;

    void handleImageBundleAsScreenshot(Bundle bundle, Rect rect, Insets insets, Task.TaskKey taskKey) throws RemoteException;

    void notifyAccessibilityButtonClicked(int i) throws RemoteException;

    void notifyAccessibilityButtonLongClicked() throws RemoteException;

    void notifyPrioritizedRotation(int i) throws RemoteException;

    void notifySwipeToHomeFinished() throws RemoteException;

    void notifySwipeUpGestureStarted() throws RemoteException;

    void notifyTaskbarAutohideSuspend(boolean z) throws RemoteException;

    void notifyTaskbarStatus(boolean z, boolean z2) throws RemoteException;

    void onAssistantGestureCompletion(float f) throws RemoteException;

    void onAssistantProgress(float f) throws RemoteException;

    void onBackPressed() throws RemoteException;

    void onImeSwitcherPressed() throws RemoteException;

    void onOverviewShown(boolean z) throws RemoteException;

    void onStatusBarMotionEvent(MotionEvent motionEvent) throws RemoteException;

    void setHomeRotationEnabled(boolean z) throws RemoteException;

    void setNavBarButtonAlpha(float f, boolean z) throws RemoteException;

    void setSplitScreenMinimized(boolean z) throws RemoteException;

    void startAssistant(Bundle bundle) throws RemoteException;

    void startScreenPinning(int i) throws RemoteException;

    void stopScreenPinning() throws RemoteException;

    void toggleNotificationPanel() throws RemoteException;

    public static abstract class Stub extends Binder implements ISystemUiProxy {
        private static final String DESCRIPTOR = "com.android.systemui.shared.recents.ISystemUiProxy";
        static final int TRANSACTION_expandNotificationPanel = 30;
        static final int TRANSACTION_getNonMinimizedSplitScreenSecondaryBounds = 8;
        static final int TRANSACTION_handleImageAsScreenshot = 22;
        static final int TRANSACTION_handleImageBundleAsScreenshot = 29;
        static final int TRANSACTION_notifyAccessibilityButtonClicked = 16;
        static final int TRANSACTION_notifyAccessibilityButtonLongClicked = 17;
        static final int TRANSACTION_notifyPrioritizedRotation = 26;
        static final int TRANSACTION_notifySwipeToHomeFinished = 24;
        static final int TRANSACTION_notifySwipeUpGestureStarted = 47;
        static final int TRANSACTION_notifyTaskbarAutohideSuspend = 49;
        static final int TRANSACTION_notifyTaskbarStatus = 48;
        static final int TRANSACTION_onAssistantGestureCompletion = 19;
        static final int TRANSACTION_onAssistantProgress = 13;
        static final int TRANSACTION_onBackPressed = 45;
        static final int TRANSACTION_onImeSwitcherPressed = 50;
        static final int TRANSACTION_onOverviewShown = 7;
        static final int TRANSACTION_onStatusBarMotionEvent = 10;
        static final int TRANSACTION_setHomeRotationEnabled = 46;
        static final int TRANSACTION_setNavBarButtonAlpha = 20;
        static final int TRANSACTION_setSplitScreenMinimized = 23;
        static final int TRANSACTION_startAssistant = 14;
        static final int TRANSACTION_startScreenPinning = 2;
        static final int TRANSACTION_stopScreenPinning = 18;
        static final int TRANSACTION_toggleNotificationPanel = 51;

        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISystemUiProxy asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (queryLocalInterface == null || !(queryLocalInterface instanceof ISystemUiProxy)) {
                return new Proxy(iBinder);
            }
            return (ISystemUiProxy) queryLocalInterface;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v1, resolved type: android.view.MotionEvent} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v4, resolved type: android.os.Bundle} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v7, resolved type: com.android.systemui.shared.recents.model.Task$TaskKey} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v10, resolved type: android.graphics.Insets} */
        /* JADX WARNING: type inference failed for: r3v0 */
        /* JADX WARNING: type inference failed for: r3v13 */
        /* JADX WARNING: type inference failed for: r3v14 */
        /* JADX WARNING: type inference failed for: r3v15 */
        /* JADX WARNING: type inference failed for: r3v16 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r6, android.os.Parcel r7, android.os.Parcel r8, int r9) throws android.os.RemoteException {
            /*
                r5 = this;
                r0 = 2
                r1 = 1
                java.lang.String r2 = "com.android.systemui.shared.recents.ISystemUiProxy"
                if (r6 == r0) goto L_0x01ff
                r0 = 10
                r3 = 0
                if (r6 == r0) goto L_0x01e6
                r0 = 26
                if (r6 == r0) goto L_0x01d8
                r0 = 1598968902(0x5f4e5446, float:1.4867585E19)
                if (r6 == r0) goto L_0x01d4
                r0 = 7
                r4 = 0
                if (r6 == r0) goto L_0x01c3
                r0 = 8
                if (r6 == r0) goto L_0x01ac
                r0 = 13
                if (r6 == r0) goto L_0x019e
                r0 = 14
                if (r6 == r0) goto L_0x0185
                r0 = 29
                if (r6 == r0) goto L_0x013c
                r0 = 30
                if (r6 == r0) goto L_0x0132
                switch(r6) {
                    case 16: goto L_0x0124;
                    case 17: goto L_0x011a;
                    case 18: goto L_0x0110;
                    case 19: goto L_0x0102;
                    case 20: goto L_0x00ed;
                    default: goto L_0x002f;
                }
            L_0x002f:
                switch(r6) {
                    case 22: goto L_0x00b0;
                    case 23: goto L_0x009f;
                    case 24: goto L_0x0095;
                    default: goto L_0x0032;
                }
            L_0x0032:
                switch(r6) {
                    case 45: goto L_0x008b;
                    case 46: goto L_0x007a;
                    case 47: goto L_0x0073;
                    case 48: goto L_0x005c;
                    case 49: goto L_0x004e;
                    case 50: goto L_0x0044;
                    case 51: goto L_0x003a;
                    default: goto L_0x0035;
                }
            L_0x0035:
                boolean r6 = super.onTransact(r6, r7, r8, r9)
                return r6
            L_0x003a:
                r7.enforceInterface(r2)
                r5.toggleNotificationPanel()
                r8.writeNoException()
                return r1
            L_0x0044:
                r7.enforceInterface(r2)
                r5.onImeSwitcherPressed()
                r8.writeNoException()
                return r1
            L_0x004e:
                r7.enforceInterface(r2)
                int r6 = r7.readInt()
                if (r6 == 0) goto L_0x0058
                r4 = r1
            L_0x0058:
                r5.notifyTaskbarAutohideSuspend(r4)
                return r1
            L_0x005c:
                r7.enforceInterface(r2)
                int r6 = r7.readInt()
                if (r6 == 0) goto L_0x0067
                r6 = r1
                goto L_0x0068
            L_0x0067:
                r6 = r4
            L_0x0068:
                int r7 = r7.readInt()
                if (r7 == 0) goto L_0x006f
                r4 = r1
            L_0x006f:
                r5.notifyTaskbarStatus(r6, r4)
                return r1
            L_0x0073:
                r7.enforceInterface(r2)
                r5.notifySwipeUpGestureStarted()
                return r1
            L_0x007a:
                r7.enforceInterface(r2)
                int r6 = r7.readInt()
                if (r6 == 0) goto L_0x0084
                r4 = r1
            L_0x0084:
                r5.setHomeRotationEnabled(r4)
                r8.writeNoException()
                return r1
            L_0x008b:
                r7.enforceInterface(r2)
                r5.onBackPressed()
                r8.writeNoException()
                return r1
            L_0x0095:
                r7.enforceInterface(r2)
                r5.notifySwipeToHomeFinished()
                r8.writeNoException()
                return r1
            L_0x009f:
                r7.enforceInterface(r2)
                int r6 = r7.readInt()
                if (r6 == 0) goto L_0x00a9
                r4 = r1
            L_0x00a9:
                r5.setSplitScreenMinimized(r4)
                r8.writeNoException()
                return r1
            L_0x00b0:
                r7.enforceInterface(r2)
                int r6 = r7.readInt()
                if (r6 == 0) goto L_0x00c2
                android.os.Parcelable$Creator r6 = android.graphics.Bitmap.CREATOR
                java.lang.Object r6 = r6.createFromParcel(r7)
                android.graphics.Bitmap r6 = (android.graphics.Bitmap) r6
                goto L_0x00c3
            L_0x00c2:
                r6 = r3
            L_0x00c3:
                int r9 = r7.readInt()
                if (r9 == 0) goto L_0x00d2
                android.os.Parcelable$Creator r9 = android.graphics.Rect.CREATOR
                java.lang.Object r9 = r9.createFromParcel(r7)
                android.graphics.Rect r9 = (android.graphics.Rect) r9
                goto L_0x00d3
            L_0x00d2:
                r9 = r3
            L_0x00d3:
                int r0 = r7.readInt()
                if (r0 == 0) goto L_0x00e2
                android.os.Parcelable$Creator r0 = android.graphics.Insets.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                r3 = r0
                android.graphics.Insets r3 = (android.graphics.Insets) r3
            L_0x00e2:
                int r7 = r7.readInt()
                r5.handleImageAsScreenshot(r6, r9, r3, r7)
                r8.writeNoException()
                return r1
            L_0x00ed:
                r7.enforceInterface(r2)
                float r6 = r7.readFloat()
                int r7 = r7.readInt()
                if (r7 == 0) goto L_0x00fb
                r4 = r1
            L_0x00fb:
                r5.setNavBarButtonAlpha(r6, r4)
                r8.writeNoException()
                return r1
            L_0x0102:
                r7.enforceInterface(r2)
                float r6 = r7.readFloat()
                r5.onAssistantGestureCompletion(r6)
                r8.writeNoException()
                return r1
            L_0x0110:
                r7.enforceInterface(r2)
                r5.stopScreenPinning()
                r8.writeNoException()
                return r1
            L_0x011a:
                r7.enforceInterface(r2)
                r5.notifyAccessibilityButtonLongClicked()
                r8.writeNoException()
                return r1
            L_0x0124:
                r7.enforceInterface(r2)
                int r6 = r7.readInt()
                r5.notifyAccessibilityButtonClicked(r6)
                r8.writeNoException()
                return r1
            L_0x0132:
                r7.enforceInterface(r2)
                r5.expandNotificationPanel()
                r8.writeNoException()
                return r1
            L_0x013c:
                r7.enforceInterface(r2)
                int r6 = r7.readInt()
                if (r6 == 0) goto L_0x014e
                android.os.Parcelable$Creator r6 = android.os.Bundle.CREATOR
                java.lang.Object r6 = r6.createFromParcel(r7)
                android.os.Bundle r6 = (android.os.Bundle) r6
                goto L_0x014f
            L_0x014e:
                r6 = r3
            L_0x014f:
                int r9 = r7.readInt()
                if (r9 == 0) goto L_0x015e
                android.os.Parcelable$Creator r9 = android.graphics.Rect.CREATOR
                java.lang.Object r9 = r9.createFromParcel(r7)
                android.graphics.Rect r9 = (android.graphics.Rect) r9
                goto L_0x015f
            L_0x015e:
                r9 = r3
            L_0x015f:
                int r0 = r7.readInt()
                if (r0 == 0) goto L_0x016e
                android.os.Parcelable$Creator r0 = android.graphics.Insets.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                android.graphics.Insets r0 = (android.graphics.Insets) r0
                goto L_0x016f
            L_0x016e:
                r0 = r3
            L_0x016f:
                int r2 = r7.readInt()
                if (r2 == 0) goto L_0x017e
                android.os.Parcelable$Creator<com.android.systemui.shared.recents.model.Task$TaskKey> r2 = com.android.systemui.shared.recents.model.Task.TaskKey.CREATOR
                java.lang.Object r7 = r2.createFromParcel(r7)
                r3 = r7
                com.android.systemui.shared.recents.model.Task$TaskKey r3 = (com.android.systemui.shared.recents.model.Task.TaskKey) r3
            L_0x017e:
                r5.handleImageBundleAsScreenshot(r6, r9, r0, r3)
                r8.writeNoException()
                return r1
            L_0x0185:
                r7.enforceInterface(r2)
                int r6 = r7.readInt()
                if (r6 == 0) goto L_0x0197
                android.os.Parcelable$Creator r6 = android.os.Bundle.CREATOR
                java.lang.Object r6 = r6.createFromParcel(r7)
                r3 = r6
                android.os.Bundle r3 = (android.os.Bundle) r3
            L_0x0197:
                r5.startAssistant(r3)
                r8.writeNoException()
                return r1
            L_0x019e:
                r7.enforceInterface(r2)
                float r6 = r7.readFloat()
                r5.onAssistantProgress(r6)
                r8.writeNoException()
                return r1
            L_0x01ac:
                r7.enforceInterface(r2)
                android.graphics.Rect r6 = r5.getNonMinimizedSplitScreenSecondaryBounds()
                r8.writeNoException()
                if (r6 == 0) goto L_0x01bf
                r8.writeInt(r1)
                r6.writeToParcel(r8, r1)
                goto L_0x01c2
            L_0x01bf:
                r8.writeInt(r4)
            L_0x01c2:
                return r1
            L_0x01c3:
                r7.enforceInterface(r2)
                int r6 = r7.readInt()
                if (r6 == 0) goto L_0x01cd
                r4 = r1
            L_0x01cd:
                r5.onOverviewShown(r4)
                r8.writeNoException()
                return r1
            L_0x01d4:
                r8.writeString(r2)
                return r1
            L_0x01d8:
                r7.enforceInterface(r2)
                int r6 = r7.readInt()
                r5.notifyPrioritizedRotation(r6)
                r8.writeNoException()
                return r1
            L_0x01e6:
                r7.enforceInterface(r2)
                int r6 = r7.readInt()
                if (r6 == 0) goto L_0x01f8
                android.os.Parcelable$Creator r6 = android.view.MotionEvent.CREATOR
                java.lang.Object r6 = r6.createFromParcel(r7)
                r3 = r6
                android.view.MotionEvent r3 = (android.view.MotionEvent) r3
            L_0x01f8:
                r5.onStatusBarMotionEvent(r3)
                r8.writeNoException()
                return r1
            L_0x01ff:
                r7.enforceInterface(r2)
                int r6 = r7.readInt()
                r5.startScreenPinning(r6)
                r8.writeNoException()
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.shared.recents.ISystemUiProxy.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }

        private static class Proxy implements ISystemUiProxy {
            public static ISystemUiProxy sDefaultImpl;
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

            public void startScreenPinning(int i) throws RemoteException {
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
                    Stub.getDefaultImpl().startScreenPinning(i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onOverviewShown(boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(7, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onOverviewShown(z);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public Rect getNonMinimizedSplitScreenSecondaryBounds() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(8, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getNonMinimizedSplitScreenSecondaryBounds();
                    }
                    obtain2.readException();
                    Rect rect = obtain2.readInt() != 0 ? (Rect) Rect.CREATOR.createFromParcel(obtain2) : null;
                    obtain2.recycle();
                    obtain.recycle();
                    return rect;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setNavBarButtonAlpha(float f, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeFloat(f);
                    obtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(20, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setNavBarButtonAlpha(f, z);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onStatusBarMotionEvent(MotionEvent motionEvent) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (motionEvent != null) {
                        obtain.writeInt(1);
                        motionEvent.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(10, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onStatusBarMotionEvent(motionEvent);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onAssistantProgress(float f) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeFloat(f);
                    if (this.mRemote.transact(13, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onAssistantProgress(f);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onAssistantGestureCompletion(float f) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeFloat(f);
                    if (this.mRemote.transact(19, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onAssistantGestureCompletion(f);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void startAssistant(Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(14, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().startAssistant(bundle);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void notifyAccessibilityButtonClicked(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(16, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().notifyAccessibilityButtonClicked(i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void notifyAccessibilityButtonLongClicked() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(17, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().notifyAccessibilityButtonLongClicked();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void stopScreenPinning() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(18, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().stopScreenPinning();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void handleImageAsScreenshot(Bitmap bitmap, Rect rect, Insets insets, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bitmap != null) {
                        obtain.writeInt(1);
                        bitmap.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (rect != null) {
                        obtain.writeInt(1);
                        rect.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (insets != null) {
                        obtain.writeInt(1);
                        insets.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeInt(i);
                    if (this.mRemote.transact(22, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().handleImageAsScreenshot(bitmap, rect, insets, i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setSplitScreenMinimized(boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(23, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setSplitScreenMinimized(z);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void notifySwipeToHomeFinished() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(24, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().notifySwipeToHomeFinished();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void notifyPrioritizedRotation(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(26, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().notifyPrioritizedRotation(i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void handleImageBundleAsScreenshot(Bundle bundle, Rect rect, Insets insets, Task.TaskKey taskKey) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (rect != null) {
                        obtain.writeInt(1);
                        rect.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (insets != null) {
                        obtain.writeInt(1);
                        insets.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (taskKey != null) {
                        obtain.writeInt(1);
                        taskKey.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(29, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().handleImageBundleAsScreenshot(bundle, rect, insets, taskKey);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void expandNotificationPanel() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(30, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().expandNotificationPanel();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onBackPressed() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(45, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onBackPressed();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setHomeRotationEnabled(boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(46, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setHomeRotationEnabled(z);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void notifySwipeUpGestureStarted() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(47, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().notifySwipeUpGestureStarted();
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void notifyTaskbarStatus(boolean z, boolean z2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    int i = 0;
                    obtain.writeInt(z ? 1 : 0);
                    if (z2) {
                        i = 1;
                    }
                    obtain.writeInt(i);
                    if (this.mRemote.transact(48, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().notifyTaskbarStatus(z, z2);
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void notifyTaskbarAutohideSuspend(boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(49, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().notifyTaskbarAutohideSuspend(z);
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void onImeSwitcherPressed() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(50, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onImeSwitcherPressed();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void toggleNotificationPanel() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(51, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().toggleNotificationPanel();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(ISystemUiProxy iSystemUiProxy) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            } else if (iSystemUiProxy == null) {
                return false;
            } else {
                Proxy.sDefaultImpl = iSystemUiProxy;
                return true;
            }
        }

        public static ISystemUiProxy getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
