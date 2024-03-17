package com.android.wm.shell.startingsurface;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.android.wm.shell.startingsurface.IStartingWindowListener;

public interface IStartingWindow extends IInterface {
    public static final String DESCRIPTOR = "com.android.wm.shell.startingsurface.IStartingWindow";

    public static class Default implements IStartingWindow {
        public IBinder asBinder() {
            return null;
        }

        public void setStartingWindowListener(IStartingWindowListener iStartingWindowListener) throws RemoteException {
        }
    }

    void setStartingWindowListener(IStartingWindowListener iStartingWindowListener) throws RemoteException;

    public static abstract class Stub extends Binder implements IStartingWindow {
        static final int TRANSACTION_setStartingWindowListener = 44;

        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IStartingWindow.DESCRIPTOR);
        }

        public static IStartingWindow asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IStartingWindow.DESCRIPTOR);
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IStartingWindow)) {
                return new Proxy(iBinder);
            }
            return (IStartingWindow) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IStartingWindow.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(IStartingWindow.DESCRIPTOR);
                return true;
            } else if (i != 44) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                IStartingWindowListener asInterface = IStartingWindowListener.Stub.asInterface(parcel.readStrongBinder());
                parcel.enforceNoDataAvail();
                setStartingWindowListener(asInterface);
                return true;
            }
        }

        private static class Proxy implements IStartingWindow {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return IStartingWindow.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void setStartingWindowListener(IStartingWindowListener iStartingWindowListener) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IStartingWindow.DESCRIPTOR);
                    obtain.writeStrongInterface(iStartingWindowListener);
                    this.mRemote.transact(44, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }
        }
    }
}
