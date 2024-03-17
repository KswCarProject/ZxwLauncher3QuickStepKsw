package com.android.wm.shell.back;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.window.IOnBackInvokedCallback;

public interface IBackAnimation extends IInterface {
    public static final String DESCRIPTOR = "com.android.wm.shell.back.IBackAnimation";

    public static class Default implements IBackAnimation {
        public IBinder asBinder() {
            return null;
        }

        public void clearBackToLauncherCallback() throws RemoteException {
        }

        public void onBackToLauncherAnimationFinished() throws RemoteException {
        }

        public void setBackToLauncherCallback(IOnBackInvokedCallback iOnBackInvokedCallback) throws RemoteException {
        }
    }

    void clearBackToLauncherCallback() throws RemoteException;

    void onBackToLauncherAnimationFinished() throws RemoteException;

    void setBackToLauncherCallback(IOnBackInvokedCallback iOnBackInvokedCallback) throws RemoteException;

    public static abstract class Stub extends Binder implements IBackAnimation {
        static final int TRANSACTION_clearBackToLauncherCallback = 2;
        static final int TRANSACTION_onBackToLauncherAnimationFinished = 3;
        static final int TRANSACTION_setBackToLauncherCallback = 1;

        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IBackAnimation.DESCRIPTOR);
        }

        public static IBackAnimation asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IBackAnimation.DESCRIPTOR);
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IBackAnimation)) {
                return new Proxy(iBinder);
            }
            return (IBackAnimation) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IBackAnimation.DESCRIPTOR);
            }
            if (i != 1598968902) {
                if (i == 1) {
                    IOnBackInvokedCallback asInterface = IOnBackInvokedCallback.Stub.asInterface(parcel.readStrongBinder());
                    parcel.enforceNoDataAvail();
                    setBackToLauncherCallback(asInterface);
                    parcel2.writeNoException();
                } else if (i == 2) {
                    clearBackToLauncherCallback();
                    parcel2.writeNoException();
                } else if (i != 3) {
                    return super.onTransact(i, parcel, parcel2, i2);
                } else {
                    onBackToLauncherAnimationFinished();
                    parcel2.writeNoException();
                }
                return true;
            }
            parcel2.writeString(IBackAnimation.DESCRIPTOR);
            return true;
        }

        private static class Proxy implements IBackAnimation {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return IBackAnimation.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void setBackToLauncherCallback(IOnBackInvokedCallback iOnBackInvokedCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IBackAnimation.DESCRIPTOR);
                    obtain.writeStrongInterface(iOnBackInvokedCallback);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void clearBackToLauncherCallback() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IBackAnimation.DESCRIPTOR);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onBackToLauncherAnimationFinished() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IBackAnimation.DESCRIPTOR);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }
    }
}
