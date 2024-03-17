package com.android.wm.shell.transition;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.window.RemoteTransition;
import android.window.TransitionFilter;

public interface IShellTransitions extends IInterface {
    public static final String DESCRIPTOR = "com.android.wm.shell.transition.IShellTransitions";

    public static class Default implements IShellTransitions {
        public IBinder asBinder() {
            return null;
        }

        public void registerRemote(TransitionFilter transitionFilter, RemoteTransition remoteTransition) throws RemoteException {
        }

        public void unregisterRemote(RemoteTransition remoteTransition) throws RemoteException {
        }
    }

    void registerRemote(TransitionFilter transitionFilter, RemoteTransition remoteTransition) throws RemoteException;

    void unregisterRemote(RemoteTransition remoteTransition) throws RemoteException;

    public static abstract class Stub extends Binder implements IShellTransitions {
        static final int TRANSACTION_registerRemote = 2;
        static final int TRANSACTION_unregisterRemote = 3;

        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IShellTransitions.DESCRIPTOR);
        }

        public static IShellTransitions asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IShellTransitions.DESCRIPTOR);
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IShellTransitions)) {
                return new Proxy(iBinder);
            }
            return (IShellTransitions) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IShellTransitions.DESCRIPTOR);
            }
            if (i != 1598968902) {
                if (i == 2) {
                    parcel.enforceNoDataAvail();
                    registerRemote((TransitionFilter) parcel.readTypedObject(TransitionFilter.CREATOR), (RemoteTransition) parcel.readTypedObject(RemoteTransition.CREATOR));
                } else if (i != 3) {
                    return super.onTransact(i, parcel, parcel2, i2);
                } else {
                    parcel.enforceNoDataAvail();
                    unregisterRemote((RemoteTransition) parcel.readTypedObject(RemoteTransition.CREATOR));
                }
                return true;
            }
            parcel2.writeString(IShellTransitions.DESCRIPTOR);
            return true;
        }

        private static class Proxy implements IShellTransitions {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return IShellTransitions.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void registerRemote(TransitionFilter transitionFilter, RemoteTransition remoteTransition) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IShellTransitions.DESCRIPTOR);
                    obtain.writeTypedObject(transitionFilter, 0);
                    obtain.writeTypedObject(remoteTransition, 0);
                    this.mRemote.transact(2, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void unregisterRemote(RemoteTransition remoteTransition) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IShellTransitions.DESCRIPTOR);
                    obtain.writeTypedObject(remoteTransition, 0);
                    this.mRemote.transact(3, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }
        }
    }
}
