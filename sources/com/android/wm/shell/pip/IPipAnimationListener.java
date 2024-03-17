package com.android.wm.shell.pip;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IPipAnimationListener extends IInterface {
    public static final String DESCRIPTOR = "com.android.wm.shell.pip.IPipAnimationListener";

    public static class Default implements IPipAnimationListener {
        public IBinder asBinder() {
            return null;
        }

        public void onExpandPip() throws RemoteException {
        }

        public void onPipAnimationStarted() throws RemoteException {
        }

        public void onPipResourceDimensionsChanged(int i, int i2) throws RemoteException {
        }
    }

    void onExpandPip() throws RemoteException;

    void onPipAnimationStarted() throws RemoteException;

    void onPipResourceDimensionsChanged(int i, int i2) throws RemoteException;

    public static abstract class Stub extends Binder implements IPipAnimationListener {
        static final int TRANSACTION_onExpandPip = 3;
        static final int TRANSACTION_onPipAnimationStarted = 1;
        static final int TRANSACTION_onPipResourceDimensionsChanged = 2;

        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IPipAnimationListener.DESCRIPTOR);
        }

        public static IPipAnimationListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IPipAnimationListener.DESCRIPTOR);
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IPipAnimationListener)) {
                return new Proxy(iBinder);
            }
            return (IPipAnimationListener) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IPipAnimationListener.DESCRIPTOR);
            }
            if (i != 1598968902) {
                if (i == 1) {
                    onPipAnimationStarted();
                } else if (i == 2) {
                    int readInt = parcel.readInt();
                    int readInt2 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    onPipResourceDimensionsChanged(readInt, readInt2);
                } else if (i != 3) {
                    return super.onTransact(i, parcel, parcel2, i2);
                } else {
                    onExpandPip();
                }
                return true;
            }
            parcel2.writeString(IPipAnimationListener.DESCRIPTOR);
            return true;
        }

        private static class Proxy implements IPipAnimationListener {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return IPipAnimationListener.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void onPipAnimationStarted() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IPipAnimationListener.DESCRIPTOR);
                    this.mRemote.transact(1, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void onPipResourceDimensionsChanged(int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IPipAnimationListener.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(2, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void onExpandPip() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IPipAnimationListener.DESCRIPTOR);
                    this.mRemote.transact(3, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }
        }
    }
}
