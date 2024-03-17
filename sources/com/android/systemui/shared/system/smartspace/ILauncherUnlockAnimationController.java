package com.android.systemui.shared.system.smartspace;

import android.graphics.Rect;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface ILauncherUnlockAnimationController extends IInterface {

    public static class Default implements ILauncherUnlockAnimationController {
        public IBinder asBinder() {
            return null;
        }

        public void dispatchSmartspaceStateToSysui() throws RemoteException {
        }

        public void playUnlockAnimation(boolean z, long j, long j2) throws RemoteException {
        }

        public void prepareForUnlock(boolean z, Rect rect, int i) throws RemoteException {
        }

        public void setSmartspaceSelectedPage(int i) throws RemoteException {
        }

        public void setSmartspaceVisibility(int i) throws RemoteException {
        }

        public void setUnlockAmount(float f, boolean z) throws RemoteException {
        }
    }

    void dispatchSmartspaceStateToSysui() throws RemoteException;

    void playUnlockAnimation(boolean z, long j, long j2) throws RemoteException;

    void prepareForUnlock(boolean z, Rect rect, int i) throws RemoteException;

    void setSmartspaceSelectedPage(int i) throws RemoteException;

    void setSmartspaceVisibility(int i) throws RemoteException;

    void setUnlockAmount(float f, boolean z) throws RemoteException;

    public static abstract class Stub extends Binder implements ILauncherUnlockAnimationController {
        private static final String DESCRIPTOR = "com.android.systemui.shared.system.smartspace.ILauncherUnlockAnimationController";
        static final int TRANSACTION_dispatchSmartspaceStateToSysui = 6;
        static final int TRANSACTION_playUnlockAnimation = 3;
        static final int TRANSACTION_prepareForUnlock = 1;
        static final int TRANSACTION_setSmartspaceSelectedPage = 4;
        static final int TRANSACTION_setSmartspaceVisibility = 5;
        static final int TRANSACTION_setUnlockAmount = 2;

        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ILauncherUnlockAnimationController asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (queryLocalInterface == null || !(queryLocalInterface instanceof ILauncherUnlockAnimationController)) {
                return new Proxy(iBinder);
            }
            return (ILauncherUnlockAnimationController) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i != 1598968902) {
                boolean z = false;
                switch (i) {
                    case 1:
                        parcel.enforceInterface(DESCRIPTOR);
                        if (parcel.readInt() != 0) {
                            z = true;
                        }
                        prepareForUnlock(z, parcel.readInt() != 0 ? (Rect) Rect.CREATOR.createFromParcel(parcel) : null, parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 2:
                        parcel.enforceInterface(DESCRIPTOR);
                        float readFloat = parcel.readFloat();
                        if (parcel.readInt() != 0) {
                            z = true;
                        }
                        setUnlockAmount(readFloat, z);
                        return true;
                    case 3:
                        parcel.enforceInterface(DESCRIPTOR);
                        playUnlockAnimation(parcel.readInt() != 0, parcel.readLong(), parcel.readLong());
                        return true;
                    case 4:
                        parcel.enforceInterface(DESCRIPTOR);
                        setSmartspaceSelectedPage(parcel.readInt());
                        return true;
                    case 5:
                        parcel.enforceInterface(DESCRIPTOR);
                        setSmartspaceVisibility(parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 6:
                        parcel.enforceInterface(DESCRIPTOR);
                        dispatchSmartspaceStateToSysui();
                        return true;
                    default:
                        return super.onTransact(i, parcel, parcel2, i2);
                }
            } else {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements ILauncherUnlockAnimationController {
            public static ILauncherUnlockAnimationController sDefaultImpl;
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

            public void prepareForUnlock(boolean z, Rect rect, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(z ? 1 : 0);
                    if (rect != null) {
                        obtain.writeInt(1);
                        rect.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeInt(i);
                    if (this.mRemote.transact(1, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().prepareForUnlock(z, rect, i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setUnlockAmount(float f, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeFloat(f);
                    obtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(2, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().setUnlockAmount(f, z);
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void playUnlockAnimation(boolean z, long j, long j2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(z ? 1 : 0);
                    obtain.writeLong(j);
                    obtain.writeLong(j2);
                    if (this.mRemote.transact(3, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().playUnlockAnimation(z, j, j2);
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void setSmartspaceSelectedPage(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(4, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().setSmartspaceSelectedPage(i);
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void setSmartspaceVisibility(int i) throws RemoteException {
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
                    Stub.getDefaultImpl().setSmartspaceVisibility(i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void dispatchSmartspaceStateToSysui() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(6, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().dispatchSmartspaceStateToSysui();
                    }
                } finally {
                    obtain.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(ILauncherUnlockAnimationController iLauncherUnlockAnimationController) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            } else if (iLauncherUnlockAnimationController == null) {
                return false;
            } else {
                Proxy.sDefaultImpl = iLauncherUnlockAnimationController;
                return true;
            }
        }

        public static ILauncherUnlockAnimationController getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
