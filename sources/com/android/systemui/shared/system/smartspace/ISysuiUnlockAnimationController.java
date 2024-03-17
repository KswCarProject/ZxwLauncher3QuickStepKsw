package com.android.systemui.shared.system.smartspace;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.android.systemui.shared.system.smartspace.ILauncherUnlockAnimationController;

public interface ISysuiUnlockAnimationController extends IInterface {

    public static class Default implements ISysuiUnlockAnimationController {
        public IBinder asBinder() {
            return null;
        }

        public void onLauncherSmartspaceStateUpdated(SmartspaceState smartspaceState) throws RemoteException {
        }

        public void setLauncherUnlockController(ILauncherUnlockAnimationController iLauncherUnlockAnimationController) throws RemoteException {
        }
    }

    void onLauncherSmartspaceStateUpdated(SmartspaceState smartspaceState) throws RemoteException;

    void setLauncherUnlockController(ILauncherUnlockAnimationController iLauncherUnlockAnimationController) throws RemoteException;

    public static abstract class Stub extends Binder implements ISysuiUnlockAnimationController {
        private static final String DESCRIPTOR = "com.android.systemui.shared.system.smartspace.ISysuiUnlockAnimationController";
        static final int TRANSACTION_onLauncherSmartspaceStateUpdated = 2;
        static final int TRANSACTION_setLauncherUnlockController = 1;

        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISysuiUnlockAnimationController asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (queryLocalInterface == null || !(queryLocalInterface instanceof ISysuiUnlockAnimationController)) {
                return new Proxy(iBinder);
            }
            return (ISysuiUnlockAnimationController) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1) {
                parcel.enforceInterface(DESCRIPTOR);
                setLauncherUnlockController(ILauncherUnlockAnimationController.Stub.asInterface(parcel.readStrongBinder()));
                return true;
            } else if (i == 2) {
                parcel.enforceInterface(DESCRIPTOR);
                onLauncherSmartspaceStateUpdated(parcel.readInt() != 0 ? SmartspaceState.CREATOR.createFromParcel(parcel) : null);
                return true;
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements ISysuiUnlockAnimationController {
            public static ISysuiUnlockAnimationController sDefaultImpl;
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

            public void setLauncherUnlockController(ILauncherUnlockAnimationController iLauncherUnlockAnimationController) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iLauncherUnlockAnimationController != null ? iLauncherUnlockAnimationController.asBinder() : null);
                    if (this.mRemote.transact(1, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().setLauncherUnlockController(iLauncherUnlockAnimationController);
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void onLauncherSmartspaceStateUpdated(SmartspaceState smartspaceState) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (smartspaceState != null) {
                        obtain.writeInt(1);
                        smartspaceState.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(2, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().onLauncherSmartspaceStateUpdated(smartspaceState);
                    }
                } finally {
                    obtain.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(ISysuiUnlockAnimationController iSysuiUnlockAnimationController) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            } else if (iSysuiUnlockAnimationController == null) {
                return false;
            } else {
                Proxy.sDefaultImpl = iSysuiUnlockAnimationController;
                return true;
            }
        }

        public static ISysuiUnlockAnimationController getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
