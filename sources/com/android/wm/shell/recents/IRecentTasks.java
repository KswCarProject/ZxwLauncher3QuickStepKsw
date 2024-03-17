package com.android.wm.shell.recents;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.android.wm.shell.recents.IRecentTasksListener;
import com.android.wm.shell.util.GroupedRecentTaskInfo;

public interface IRecentTasks extends IInterface {
    public static final String DESCRIPTOR = "com.android.wm.shell.recents.IRecentTasks";

    public static class Default implements IRecentTasks {
        public IBinder asBinder() {
            return null;
        }

        public GroupedRecentTaskInfo[] getRecentTasks(int i, int i2, int i3) throws RemoteException {
            return null;
        }

        public void registerRecentTasksListener(IRecentTasksListener iRecentTasksListener) throws RemoteException {
        }

        public void unregisterRecentTasksListener(IRecentTasksListener iRecentTasksListener) throws RemoteException {
        }
    }

    GroupedRecentTaskInfo[] getRecentTasks(int i, int i2, int i3) throws RemoteException;

    void registerRecentTasksListener(IRecentTasksListener iRecentTasksListener) throws RemoteException;

    void unregisterRecentTasksListener(IRecentTasksListener iRecentTasksListener) throws RemoteException;

    public static abstract class Stub extends Binder implements IRecentTasks {
        static final int TRANSACTION_getRecentTasks = 4;
        static final int TRANSACTION_registerRecentTasksListener = 2;
        static final int TRANSACTION_unregisterRecentTasksListener = 3;

        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IRecentTasks.DESCRIPTOR);
        }

        public static IRecentTasks asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IRecentTasks.DESCRIPTOR);
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IRecentTasks)) {
                return new Proxy(iBinder);
            }
            return (IRecentTasks) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IRecentTasks.DESCRIPTOR);
            }
            if (i != 1598968902) {
                if (i == 2) {
                    IRecentTasksListener asInterface = IRecentTasksListener.Stub.asInterface(parcel.readStrongBinder());
                    parcel.enforceNoDataAvail();
                    registerRecentTasksListener(asInterface);
                } else if (i == 3) {
                    IRecentTasksListener asInterface2 = IRecentTasksListener.Stub.asInterface(parcel.readStrongBinder());
                    parcel.enforceNoDataAvail();
                    unregisterRecentTasksListener(asInterface2);
                } else if (i != 4) {
                    return super.onTransact(i, parcel, parcel2, i2);
                } else {
                    int readInt = parcel.readInt();
                    int readInt2 = parcel.readInt();
                    int readInt3 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    GroupedRecentTaskInfo[] recentTasks = getRecentTasks(readInt, readInt2, readInt3);
                    parcel2.writeNoException();
                    parcel2.writeTypedArray(recentTasks, 1);
                }
                return true;
            }
            parcel2.writeString(IRecentTasks.DESCRIPTOR);
            return true;
        }

        private static class Proxy implements IRecentTasks {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return IRecentTasks.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void registerRecentTasksListener(IRecentTasksListener iRecentTasksListener) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IRecentTasks.DESCRIPTOR);
                    obtain.writeStrongInterface(iRecentTasksListener);
                    this.mRemote.transact(2, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void unregisterRecentTasksListener(IRecentTasksListener iRecentTasksListener) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IRecentTasks.DESCRIPTOR);
                    obtain.writeStrongInterface(iRecentTasksListener);
                    this.mRemote.transact(3, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public GroupedRecentTaskInfo[] getRecentTasks(int i, int i2, int i3) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IRecentTasks.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                    return (GroupedRecentTaskInfo[]) obtain2.createTypedArray(GroupedRecentTaskInfo.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }
    }
}
