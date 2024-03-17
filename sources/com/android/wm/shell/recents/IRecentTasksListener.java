package com.android.wm.shell.recents;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IRecentTasksListener extends IInterface {
    public static final String DESCRIPTOR = "com.android.wm.shell.recents.IRecentTasksListener";

    public static class Default implements IRecentTasksListener {
        public IBinder asBinder() {
            return null;
        }

        public void onRecentTasksChanged() throws RemoteException {
        }
    }

    void onRecentTasksChanged() throws RemoteException;

    public static abstract class Stub extends Binder implements IRecentTasksListener {
        static final int TRANSACTION_onRecentTasksChanged = 1;

        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IRecentTasksListener.DESCRIPTOR);
        }

        public static IRecentTasksListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IRecentTasksListener.DESCRIPTOR);
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IRecentTasksListener)) {
                return new Proxy(iBinder);
            }
            return (IRecentTasksListener) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IRecentTasksListener.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(IRecentTasksListener.DESCRIPTOR);
                return true;
            } else if (i != 1) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                onRecentTasksChanged();
                return true;
            }
        }

        private static class Proxy implements IRecentTasksListener {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return IRecentTasksListener.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void onRecentTasksChanged() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IRecentTasksListener.DESCRIPTOR);
                    this.mRemote.transact(1, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }
        }
    }
}
