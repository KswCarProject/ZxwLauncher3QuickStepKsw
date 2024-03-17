package com.android.systemui.shared.recents;

import android.graphics.Rect;
import android.graphics.Region;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IOverviewProxy extends IInterface {

    public static class Default implements IOverviewProxy {
        public IBinder asBinder() {
            return null;
        }

        public void disable(int i, int i2, int i3, boolean z) throws RemoteException {
        }

        public void onActiveNavBarRegionChanges(Region region) throws RemoteException {
        }

        public void onAssistantAvailable(boolean z) throws RemoteException {
        }

        public void onAssistantVisibilityChanged(float f) throws RemoteException {
        }

        public void onBackAction(boolean z, int i, int i2, boolean z2, boolean z3) throws RemoteException {
        }

        public void onInitialize(Bundle bundle) throws RemoteException {
        }

        public void onNavButtonsDarkIntensityChanged(float f) throws RemoteException {
        }

        public void onOverviewHidden(boolean z, boolean z2) throws RemoteException {
        }

        public void onOverviewShown(boolean z) throws RemoteException {
        }

        public void onOverviewToggle() throws RemoteException {
        }

        public void onRotationProposal(int i, boolean z) throws RemoteException {
        }

        public void onScreenTurnedOn() throws RemoteException {
        }

        public void onSplitScreenSecondaryBoundsChanged(Rect rect, Rect rect2) throws RemoteException {
        }

        public void onSystemBarAttributesChanged(int i, int i2) throws RemoteException {
        }

        public void onSystemUiStateChanged(int i) throws RemoteException {
        }

        public void onTip(int i, int i2) throws RemoteException {
        }
    }

    void disable(int i, int i2, int i3, boolean z) throws RemoteException;

    void onActiveNavBarRegionChanges(Region region) throws RemoteException;

    void onAssistantAvailable(boolean z) throws RemoteException;

    void onAssistantVisibilityChanged(float f) throws RemoteException;

    void onBackAction(boolean z, int i, int i2, boolean z2, boolean z3) throws RemoteException;

    void onInitialize(Bundle bundle) throws RemoteException;

    void onNavButtonsDarkIntensityChanged(float f) throws RemoteException;

    void onOverviewHidden(boolean z, boolean z2) throws RemoteException;

    void onOverviewShown(boolean z) throws RemoteException;

    void onOverviewToggle() throws RemoteException;

    void onRotationProposal(int i, boolean z) throws RemoteException;

    void onScreenTurnedOn() throws RemoteException;

    void onSplitScreenSecondaryBoundsChanged(Rect rect, Rect rect2) throws RemoteException;

    void onSystemBarAttributesChanged(int i, int i2) throws RemoteException;

    void onSystemUiStateChanged(int i) throws RemoteException;

    void onTip(int i, int i2) throws RemoteException;

    public static abstract class Stub extends Binder implements IOverviewProxy {
        private static final String DESCRIPTOR = "com.android.systemui.shared.recents.IOverviewProxy";
        static final int TRANSACTION_disable = 20;
        static final int TRANSACTION_onActiveNavBarRegionChanges = 12;
        static final int TRANSACTION_onAssistantAvailable = 14;
        static final int TRANSACTION_onAssistantVisibilityChanged = 15;
        static final int TRANSACTION_onBackAction = 16;
        static final int TRANSACTION_onInitialize = 13;
        static final int TRANSACTION_onNavButtonsDarkIntensityChanged = 23;
        static final int TRANSACTION_onOverviewHidden = 9;
        static final int TRANSACTION_onOverviewShown = 8;
        static final int TRANSACTION_onOverviewToggle = 7;
        static final int TRANSACTION_onRotationProposal = 19;
        static final int TRANSACTION_onScreenTurnedOn = 22;
        static final int TRANSACTION_onSplitScreenSecondaryBoundsChanged = 18;
        static final int TRANSACTION_onSystemBarAttributesChanged = 21;
        static final int TRANSACTION_onSystemUiStateChanged = 17;
        static final int TRANSACTION_onTip = 11;

        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IOverviewProxy asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IOverviewProxy)) {
                return new Proxy(iBinder);
            }
            return (IOverviewProxy) queryLocalInterface;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: android.graphics.Region} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: android.os.Bundle} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v11, resolved type: android.graphics.Rect} */
        /* JADX WARNING: type inference failed for: r0v4 */
        /* JADX WARNING: type inference failed for: r0v14 */
        /* JADX WARNING: type inference failed for: r0v15 */
        /* JADX WARNING: type inference failed for: r0v16 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r11, android.os.Parcel r12, android.os.Parcel r13, int r14) throws android.os.RemoteException {
            /*
                r10 = this;
                r0 = 7
                r1 = 1
                java.lang.String r2 = "com.android.systemui.shared.recents.IOverviewProxy"
                if (r11 == r0) goto L_0x0143
                r0 = 8
                r3 = 0
                if (r11 == r0) goto L_0x0135
                r0 = 9
                if (r11 == r0) goto L_0x011e
                r0 = 1598968902(0x5f4e5446, float:1.4867585E19)
                if (r11 == r0) goto L_0x011a
                r0 = 0
                switch(r11) {
                    case 11: goto L_0x010b;
                    case 12: goto L_0x00f5;
                    case 13: goto L_0x00df;
                    case 14: goto L_0x00d1;
                    case 15: goto L_0x00c6;
                    case 16: goto L_0x009b;
                    case 17: goto L_0x0090;
                    case 18: goto L_0x006a;
                    case 19: goto L_0x0058;
                    case 20: goto L_0x003e;
                    case 21: goto L_0x002f;
                    case 22: goto L_0x0028;
                    case 23: goto L_0x001d;
                    default: goto L_0x0018;
                }
            L_0x0018:
                boolean r11 = super.onTransact(r11, r12, r13, r14)
                return r11
            L_0x001d:
                r12.enforceInterface(r2)
                float r11 = r12.readFloat()
                r10.onNavButtonsDarkIntensityChanged(r11)
                return r1
            L_0x0028:
                r12.enforceInterface(r2)
                r10.onScreenTurnedOn()
                return r1
            L_0x002f:
                r12.enforceInterface(r2)
                int r11 = r12.readInt()
                int r12 = r12.readInt()
                r10.onSystemBarAttributesChanged(r11, r12)
                return r1
            L_0x003e:
                r12.enforceInterface(r2)
                int r11 = r12.readInt()
                int r13 = r12.readInt()
                int r14 = r12.readInt()
                int r12 = r12.readInt()
                if (r12 == 0) goto L_0x0054
                r3 = r1
            L_0x0054:
                r10.disable(r11, r13, r14, r3)
                return r1
            L_0x0058:
                r12.enforceInterface(r2)
                int r11 = r12.readInt()
                int r12 = r12.readInt()
                if (r12 == 0) goto L_0x0066
                r3 = r1
            L_0x0066:
                r10.onRotationProposal(r11, r3)
                return r1
            L_0x006a:
                r12.enforceInterface(r2)
                int r11 = r12.readInt()
                if (r11 == 0) goto L_0x007c
                android.os.Parcelable$Creator r11 = android.graphics.Rect.CREATOR
                java.lang.Object r11 = r11.createFromParcel(r12)
                android.graphics.Rect r11 = (android.graphics.Rect) r11
                goto L_0x007d
            L_0x007c:
                r11 = r0
            L_0x007d:
                int r13 = r12.readInt()
                if (r13 == 0) goto L_0x008c
                android.os.Parcelable$Creator r13 = android.graphics.Rect.CREATOR
                java.lang.Object r12 = r13.createFromParcel(r12)
                r0 = r12
                android.graphics.Rect r0 = (android.graphics.Rect) r0
            L_0x008c:
                r10.onSplitScreenSecondaryBoundsChanged(r11, r0)
                return r1
            L_0x0090:
                r12.enforceInterface(r2)
                int r11 = r12.readInt()
                r10.onSystemUiStateChanged(r11)
                return r1
            L_0x009b:
                r12.enforceInterface(r2)
                int r11 = r12.readInt()
                if (r11 == 0) goto L_0x00a6
                r5 = r1
                goto L_0x00a7
            L_0x00a6:
                r5 = r3
            L_0x00a7:
                int r6 = r12.readInt()
                int r7 = r12.readInt()
                int r11 = r12.readInt()
                if (r11 == 0) goto L_0x00b7
                r8 = r1
                goto L_0x00b8
            L_0x00b7:
                r8 = r3
            L_0x00b8:
                int r11 = r12.readInt()
                if (r11 == 0) goto L_0x00c0
                r9 = r1
                goto L_0x00c1
            L_0x00c0:
                r9 = r3
            L_0x00c1:
                r4 = r10
                r4.onBackAction(r5, r6, r7, r8, r9)
                return r1
            L_0x00c6:
                r12.enforceInterface(r2)
                float r11 = r12.readFloat()
                r10.onAssistantVisibilityChanged(r11)
                return r1
            L_0x00d1:
                r12.enforceInterface(r2)
                int r11 = r12.readInt()
                if (r11 == 0) goto L_0x00db
                r3 = r1
            L_0x00db:
                r10.onAssistantAvailable(r3)
                return r1
            L_0x00df:
                r12.enforceInterface(r2)
                int r11 = r12.readInt()
                if (r11 == 0) goto L_0x00f1
                android.os.Parcelable$Creator r11 = android.os.Bundle.CREATOR
                java.lang.Object r11 = r11.createFromParcel(r12)
                r0 = r11
                android.os.Bundle r0 = (android.os.Bundle) r0
            L_0x00f1:
                r10.onInitialize(r0)
                return r1
            L_0x00f5:
                r12.enforceInterface(r2)
                int r11 = r12.readInt()
                if (r11 == 0) goto L_0x0107
                android.os.Parcelable$Creator r11 = android.graphics.Region.CREATOR
                java.lang.Object r11 = r11.createFromParcel(r12)
                r0 = r11
                android.graphics.Region r0 = (android.graphics.Region) r0
            L_0x0107:
                r10.onActiveNavBarRegionChanges(r0)
                return r1
            L_0x010b:
                r12.enforceInterface(r2)
                int r11 = r12.readInt()
                int r12 = r12.readInt()
                r10.onTip(r11, r12)
                return r1
            L_0x011a:
                r13.writeString(r2)
                return r1
            L_0x011e:
                r12.enforceInterface(r2)
                int r11 = r12.readInt()
                if (r11 == 0) goto L_0x0129
                r11 = r1
                goto L_0x012a
            L_0x0129:
                r11 = r3
            L_0x012a:
                int r12 = r12.readInt()
                if (r12 == 0) goto L_0x0131
                r3 = r1
            L_0x0131:
                r10.onOverviewHidden(r11, r3)
                return r1
            L_0x0135:
                r12.enforceInterface(r2)
                int r11 = r12.readInt()
                if (r11 == 0) goto L_0x013f
                r3 = r1
            L_0x013f:
                r10.onOverviewShown(r3)
                return r1
            L_0x0143:
                r12.enforceInterface(r2)
                r10.onOverviewToggle()
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.shared.recents.IOverviewProxy.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }

        private static class Proxy implements IOverviewProxy {
            public static IOverviewProxy sDefaultImpl;
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

            public void onActiveNavBarRegionChanges(Region region) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (region != null) {
                        obtain.writeInt(1);
                        region.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(12, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().onActiveNavBarRegionChanges(region);
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void onInitialize(Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(13, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().onInitialize(bundle);
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void onOverviewToggle() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(7, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().onOverviewToggle();
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void onOverviewShown(boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(8, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().onOverviewShown(z);
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void onOverviewHidden(boolean z, boolean z2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    int i = 0;
                    obtain.writeInt(z ? 1 : 0);
                    if (z2) {
                        i = 1;
                    }
                    obtain.writeInt(i);
                    if (this.mRemote.transact(9, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().onOverviewHidden(z, z2);
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void onTip(int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    if (this.mRemote.transact(11, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().onTip(i, i2);
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void onAssistantAvailable(boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(14, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().onAssistantAvailable(z);
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void onAssistantVisibilityChanged(float f) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeFloat(f);
                    if (this.mRemote.transact(15, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().onAssistantVisibilityChanged(f);
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void onBackAction(boolean z, int i, int i2, boolean z2, boolean z3) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    int i3 = 0;
                    obtain.writeInt(z ? 1 : 0);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(z2 ? 1 : 0);
                    if (z3) {
                        i3 = 1;
                    }
                    obtain.writeInt(i3);
                    if (this.mRemote.transact(16, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().onBackAction(z, i, i2, z2, z3);
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void onSystemUiStateChanged(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(17, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().onSystemUiStateChanged(i);
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void onSplitScreenSecondaryBoundsChanged(Rect rect, Rect rect2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (rect != null) {
                        obtain.writeInt(1);
                        rect.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (rect2 != null) {
                        obtain.writeInt(1);
                        rect2.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(18, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().onSplitScreenSecondaryBoundsChanged(rect, rect2);
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void onRotationProposal(int i, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(19, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().onRotationProposal(i, z);
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void disable(int i, int i2, int i3, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    obtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(20, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().disable(i, i2, i3, z);
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void onSystemBarAttributesChanged(int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    if (this.mRemote.transact(21, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().onSystemBarAttributesChanged(i, i2);
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void onScreenTurnedOn() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(22, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().onScreenTurnedOn();
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void onNavButtonsDarkIntensityChanged(float f) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeFloat(f);
                    if (this.mRemote.transact(23, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().onNavButtonsDarkIntensityChanged(f);
                    }
                } finally {
                    obtain.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IOverviewProxy iOverviewProxy) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            } else if (iOverviewProxy == null) {
                return false;
            } else {
                Proxy.sDefaultImpl = iOverviewProxy;
                return true;
            }
        }

        public static IOverviewProxy getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
