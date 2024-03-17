package com.android.wm.shell.startingsurface;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IStartingWindowListener extends IInterface {
    public static final String DESCRIPTOR = "com.android.wm.shell.startingsurface.IStartingWindowListener";

    public static class Default implements IStartingWindowListener {
        public IBinder asBinder() {
            return null;
        }

        public void onTaskLaunching(int i, int i2, int i3) throws RemoteException {
        }
    }

    void onTaskLaunching(int i, int i2, int i3) throws RemoteException;

    public static abstract class Stub extends Binder implements IStartingWindowListener {
        static final int TRANSACTION_onTaskLaunching = 1;

        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IStartingWindowListener.DESCRIPTOR);
        }

        public static IStartingWindowListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IStartingWindowListener.DESCRIPTOR);
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IStartingWindowListener)) {
                return new Proxy(iBinder);
            }
            return (IStartingWindowListener) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IStartingWindowListener.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(IStartingWindowListener.DESCRIPTOR);
                return true;
            } else if (i != 1) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                int readInt = parcel.readInt();
                int readInt2 = parcel.readInt();
                int readInt3 = parcel.readInt();
                parcel.enforceNoDataAvail();
                onTaskLaunching(readInt, readInt2, readInt3);
                return true;
            }
        }

        private static class Proxy implements IStartingWindowListener {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return IStartingWindowListener.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void onTaskLaunching(int i, int i2, int i3) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IStartingWindowListener.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    this.mRemote.transact(1, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }
        }
    }
}
