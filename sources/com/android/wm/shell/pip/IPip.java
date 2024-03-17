package com.android.wm.shell.pip;

import android.app.PictureInPictureParams;
import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.SurfaceControl;
import com.android.wm.shell.pip.IPipAnimationListener;

public interface IPip extends IInterface {
    public static final String DESCRIPTOR = "com.android.wm.shell.pip.IPip";

    public static class Default implements IPip {
        public IBinder asBinder() {
            return null;
        }

        public void setPinnedStackAnimationListener(IPipAnimationListener iPipAnimationListener) throws RemoteException {
        }

        public void setShelfHeight(boolean z, int i) throws RemoteException {
        }

        public Rect startSwipePipToHome(ComponentName componentName, ActivityInfo activityInfo, PictureInPictureParams pictureInPictureParams, int i, int i2) throws RemoteException {
            return null;
        }

        public void stopSwipePipToHome(int i, ComponentName componentName, Rect rect, SurfaceControl surfaceControl) throws RemoteException {
        }
    }

    void setPinnedStackAnimationListener(IPipAnimationListener iPipAnimationListener) throws RemoteException;

    void setShelfHeight(boolean z, int i) throws RemoteException;

    Rect startSwipePipToHome(ComponentName componentName, ActivityInfo activityInfo, PictureInPictureParams pictureInPictureParams, int i, int i2) throws RemoteException;

    void stopSwipePipToHome(int i, ComponentName componentName, Rect rect, SurfaceControl surfaceControl) throws RemoteException;

    public static abstract class Stub extends Binder implements IPip {
        static final int TRANSACTION_setPinnedStackAnimationListener = 4;
        static final int TRANSACTION_setShelfHeight = 5;
        static final int TRANSACTION_startSwipePipToHome = 2;
        static final int TRANSACTION_stopSwipePipToHome = 3;

        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IPip.DESCRIPTOR);
        }

        public static IPip asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IPip.DESCRIPTOR);
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IPip)) {
                return new Proxy(iBinder);
            }
            return (IPip) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IPip.DESCRIPTOR);
            }
            if (i != 1598968902) {
                if (i == 2) {
                    int readInt = parcel.readInt();
                    int readInt2 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    Rect startSwipePipToHome = startSwipePipToHome((ComponentName) parcel.readTypedObject(ComponentName.CREATOR), (ActivityInfo) parcel.readTypedObject(ActivityInfo.CREATOR), (PictureInPictureParams) parcel.readTypedObject(PictureInPictureParams.CREATOR), readInt, readInt2);
                    parcel2.writeNoException();
                    parcel2.writeTypedObject(startSwipePipToHome, 1);
                } else if (i == 3) {
                    parcel.enforceNoDataAvail();
                    stopSwipePipToHome(parcel.readInt(), (ComponentName) parcel.readTypedObject(ComponentName.CREATOR), (Rect) parcel.readTypedObject(Rect.CREATOR), (SurfaceControl) parcel.readTypedObject(SurfaceControl.CREATOR));
                } else if (i == 4) {
                    IPipAnimationListener asInterface = IPipAnimationListener.Stub.asInterface(parcel.readStrongBinder());
                    parcel.enforceNoDataAvail();
                    setPinnedStackAnimationListener(asInterface);
                } else if (i != 5) {
                    return super.onTransact(i, parcel, parcel2, i2);
                } else {
                    boolean readBoolean = parcel.readBoolean();
                    int readInt3 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    setShelfHeight(readBoolean, readInt3);
                }
                return true;
            }
            parcel2.writeString(IPip.DESCRIPTOR);
            return true;
        }

        private static class Proxy implements IPip {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return IPip.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public Rect startSwipePipToHome(ComponentName componentName, ActivityInfo activityInfo, PictureInPictureParams pictureInPictureParams, int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IPip.DESCRIPTOR);
                    obtain.writeTypedObject(componentName, 0);
                    obtain.writeTypedObject(activityInfo, 0);
                    obtain.writeTypedObject(pictureInPictureParams, 0);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return (Rect) obtain2.readTypedObject(Rect.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void stopSwipePipToHome(int i, ComponentName componentName, Rect rect, SurfaceControl surfaceControl) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IPip.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeTypedObject(componentName, 0);
                    obtain.writeTypedObject(rect, 0);
                    obtain.writeTypedObject(surfaceControl, 0);
                    this.mRemote.transact(3, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void setPinnedStackAnimationListener(IPipAnimationListener iPipAnimationListener) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IPip.DESCRIPTOR);
                    obtain.writeStrongInterface(iPipAnimationListener);
                    this.mRemote.transact(4, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void setShelfHeight(boolean z, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IPip.DESCRIPTOR);
                    obtain.writeBoolean(z);
                    obtain.writeInt(i);
                    this.mRemote.transact(5, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }
        }
    }
}
