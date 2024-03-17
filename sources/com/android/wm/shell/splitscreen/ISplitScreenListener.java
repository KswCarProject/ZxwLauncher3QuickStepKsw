package com.android.wm.shell.splitscreen;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface ISplitScreenListener extends IInterface {
    public static final String DESCRIPTOR = "com.android.wm.shell.splitscreen.ISplitScreenListener";

    public static class Default implements ISplitScreenListener {
        public IBinder asBinder() {
            return null;
        }

        public void onStagePositionChanged(int i, int i2) throws RemoteException {
        }

        public void onTaskStageChanged(int i, int i2, boolean z) throws RemoteException {
        }
    }

    void onStagePositionChanged(int i, int i2) throws RemoteException;

    void onTaskStageChanged(int i, int i2, boolean z) throws RemoteException;

    public static abstract class Stub extends Binder implements ISplitScreenListener {
        static final int TRANSACTION_onStagePositionChanged = 1;
        static final int TRANSACTION_onTaskStageChanged = 2;

        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, ISplitScreenListener.DESCRIPTOR);
        }

        public static ISplitScreenListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(ISplitScreenListener.DESCRIPTOR);
            if (queryLocalInterface == null || !(queryLocalInterface instanceof ISplitScreenListener)) {
                return new Proxy(iBinder);
            }
            return (ISplitScreenListener) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(ISplitScreenListener.DESCRIPTOR);
            }
            if (i != 1598968902) {
                if (i == 1) {
                    int readInt = parcel.readInt();
                    int readInt2 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    onStagePositionChanged(readInt, readInt2);
                } else if (i != 2) {
                    return super.onTransact(i, parcel, parcel2, i2);
                } else {
                    int readInt3 = parcel.readInt();
                    int readInt4 = parcel.readInt();
                    boolean readBoolean = parcel.readBoolean();
                    parcel.enforceNoDataAvail();
                    onTaskStageChanged(readInt3, readInt4, readBoolean);
                }
                return true;
            }
            parcel2.writeString(ISplitScreenListener.DESCRIPTOR);
            return true;
        }

        private static class Proxy implements ISplitScreenListener {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return ISplitScreenListener.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void onStagePositionChanged(int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISplitScreenListener.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(1, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void onTaskStageChanged(int i, int i2, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISplitScreenListener.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeBoolean(z);
                    this.mRemote.transact(2, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }
        }
    }
}
