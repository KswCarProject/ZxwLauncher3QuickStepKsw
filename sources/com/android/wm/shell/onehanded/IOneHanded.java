package com.android.wm.shell.onehanded;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IOneHanded extends IInterface {
    public static final String DESCRIPTOR = "com.android.wm.shell.onehanded.IOneHanded";

    public static class Default implements IOneHanded {
        public IBinder asBinder() {
            return null;
        }

        public void startOneHanded() throws RemoteException {
        }

        public void stopOneHanded() throws RemoteException {
        }
    }

    void startOneHanded() throws RemoteException;

    void stopOneHanded() throws RemoteException;

    public static abstract class Stub extends Binder implements IOneHanded {
        static final int TRANSACTION_startOneHanded = 2;
        static final int TRANSACTION_stopOneHanded = 3;

        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IOneHanded.DESCRIPTOR);
        }

        public static IOneHanded asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IOneHanded.DESCRIPTOR);
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IOneHanded)) {
                return new Proxy(iBinder);
            }
            return (IOneHanded) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IOneHanded.DESCRIPTOR);
            }
            if (i != 1598968902) {
                if (i == 2) {
                    startOneHanded();
                } else if (i != 3) {
                    return super.onTransact(i, parcel, parcel2, i2);
                } else {
                    stopOneHanded();
                }
                return true;
            }
            parcel2.writeString(IOneHanded.DESCRIPTOR);
            return true;
        }

        private static class Proxy implements IOneHanded {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return IOneHanded.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void startOneHanded() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IOneHanded.DESCRIPTOR);
                    this.mRemote.transact(2, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void stopOneHanded() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IOneHanded.DESCRIPTOR);
                    this.mRemote.transact(3, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }
        }
    }
}
