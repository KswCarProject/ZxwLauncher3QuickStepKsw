package com.szchoiceway.eventcenter;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface ICallbackfn extends IInterface {

    public static class Default implements ICallbackfn {
        public IBinder asBinder() {
            return null;
        }

        public void notifyEvt(int i, int i2, int i3, byte[] bArr, String str) throws RemoteException {
        }
    }

    void notifyEvt(int i, int i2, int i3, byte[] bArr, String str) throws RemoteException;

    public static abstract class Stub extends Binder implements ICallbackfn {
        private static final String DESCRIPTOR = "com.szchoiceway.eventcenter.ICallbackfn";
        static final int TRANSACTION_notifyEvt = 1;

        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ICallbackfn asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (queryLocalInterface == null || !(queryLocalInterface instanceof ICallbackfn)) {
                return new Proxy(iBinder);
            }
            return (ICallbackfn) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1) {
                parcel.enforceInterface(DESCRIPTOR);
                notifyEvt(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.createByteArray(), parcel.readString());
                parcel2.writeNoException();
                return true;
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements ICallbackfn {
            public static ICallbackfn sDefaultImpl;
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

            public void notifyEvt(int i, int i2, int i3, byte[] bArr, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    obtain.writeByteArray(bArr);
                    obtain.writeString(str);
                    if (this.mRemote.transact(1, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().notifyEvt(i, i2, i3, bArr, str);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(ICallbackfn iCallbackfn) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            } else if (iCallbackfn == null) {
                return false;
            } else {
                Proxy.sDefaultImpl = iCallbackfn;
                return true;
            }
        }

        public static ICallbackfn getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
