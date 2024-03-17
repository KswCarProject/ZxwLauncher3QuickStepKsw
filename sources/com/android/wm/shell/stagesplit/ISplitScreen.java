package com.android.wm.shell.stagesplit;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.UserHandle;
import android.view.RemoteAnimationAdapter;
import android.view.RemoteAnimationTarget;
import android.window.RemoteTransition;
import com.android.wm.shell.stagesplit.ISplitScreenListener;

public interface ISplitScreen extends IInterface {
    public static final String DESCRIPTOR = "com.android.wm.shell.stagesplit.ISplitScreen";

    public static class Default implements ISplitScreen {
        public IBinder asBinder() {
            return null;
        }

        public void exitSplitScreen(int i) throws RemoteException {
        }

        public void exitSplitScreenOnHide(boolean z) throws RemoteException {
        }

        public RemoteAnimationTarget[] onGoingToRecentsLegacy(boolean z, RemoteAnimationTarget[] remoteAnimationTargetArr) throws RemoteException {
            return null;
        }

        public void registerSplitScreenListener(ISplitScreenListener iSplitScreenListener) throws RemoteException {
        }

        public void removeFromSideStage(int i) throws RemoteException {
        }

        public void setSideStageVisibility(boolean z) throws RemoteException {
        }

        public void startIntent(PendingIntent pendingIntent, Intent intent, int i, int i2, Bundle bundle) throws RemoteException {
        }

        public void startShortcut(String str, String str2, int i, int i2, Bundle bundle, UserHandle userHandle) throws RemoteException {
        }

        public void startTask(int i, int i2, int i3, Bundle bundle) throws RemoteException {
        }

        public void startTasks(int i, Bundle bundle, int i2, Bundle bundle2, int i3, RemoteTransition remoteTransition) throws RemoteException {
        }

        public void startTasksWithLegacyTransition(int i, Bundle bundle, int i2, Bundle bundle2, int i3, RemoteAnimationAdapter remoteAnimationAdapter) throws RemoteException {
        }

        public void unregisterSplitScreenListener(ISplitScreenListener iSplitScreenListener) throws RemoteException {
        }
    }

    void exitSplitScreen(int i) throws RemoteException;

    void exitSplitScreenOnHide(boolean z) throws RemoteException;

    RemoteAnimationTarget[] onGoingToRecentsLegacy(boolean z, RemoteAnimationTarget[] remoteAnimationTargetArr) throws RemoteException;

    void registerSplitScreenListener(ISplitScreenListener iSplitScreenListener) throws RemoteException;

    void removeFromSideStage(int i) throws RemoteException;

    void setSideStageVisibility(boolean z) throws RemoteException;

    void startIntent(PendingIntent pendingIntent, Intent intent, int i, int i2, Bundle bundle) throws RemoteException;

    void startShortcut(String str, String str2, int i, int i2, Bundle bundle, UserHandle userHandle) throws RemoteException;

    void startTask(int i, int i2, int i3, Bundle bundle) throws RemoteException;

    void startTasks(int i, Bundle bundle, int i2, Bundle bundle2, int i3, RemoteTransition remoteTransition) throws RemoteException;

    void startTasksWithLegacyTransition(int i, Bundle bundle, int i2, Bundle bundle2, int i3, RemoteAnimationAdapter remoteAnimationAdapter) throws RemoteException;

    void unregisterSplitScreenListener(ISplitScreenListener iSplitScreenListener) throws RemoteException;

    public static abstract class Stub extends Binder implements ISplitScreen {
        static final int TRANSACTION_exitSplitScreen = 6;
        static final int TRANSACTION_exitSplitScreenOnHide = 7;
        static final int TRANSACTION_onGoingToRecentsLegacy = 13;
        static final int TRANSACTION_registerSplitScreenListener = 2;
        static final int TRANSACTION_removeFromSideStage = 5;
        static final int TRANSACTION_setSideStageVisibility = 4;
        static final int TRANSACTION_startIntent = 10;
        static final int TRANSACTION_startShortcut = 9;
        static final int TRANSACTION_startTask = 8;
        static final int TRANSACTION_startTasks = 11;
        static final int TRANSACTION_startTasksWithLegacyTransition = 12;
        static final int TRANSACTION_unregisterSplitScreenListener = 3;

        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, ISplitScreen.DESCRIPTOR);
        }

        public static ISplitScreen asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(ISplitScreen.DESCRIPTOR);
            if (queryLocalInterface == null || !(queryLocalInterface instanceof ISplitScreen)) {
                return new Proxy(iBinder);
            }
            return (ISplitScreen) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(ISplitScreen.DESCRIPTOR);
            }
            if (i != 1598968902) {
                switch (i) {
                    case 2:
                        ISplitScreenListener asInterface = ISplitScreenListener.Stub.asInterface(parcel.readStrongBinder());
                        parcel.enforceNoDataAvail();
                        registerSplitScreenListener(asInterface);
                        break;
                    case 3:
                        ISplitScreenListener asInterface2 = ISplitScreenListener.Stub.asInterface(parcel.readStrongBinder());
                        parcel.enforceNoDataAvail();
                        unregisterSplitScreenListener(asInterface2);
                        break;
                    case 4:
                        boolean readBoolean = parcel.readBoolean();
                        parcel.enforceNoDataAvail();
                        setSideStageVisibility(readBoolean);
                        break;
                    case 5:
                        int readInt = parcel.readInt();
                        parcel.enforceNoDataAvail();
                        removeFromSideStage(readInt);
                        break;
                    case 6:
                        int readInt2 = parcel.readInt();
                        parcel.enforceNoDataAvail();
                        exitSplitScreen(readInt2);
                        break;
                    case 7:
                        boolean readBoolean2 = parcel.readBoolean();
                        parcel.enforceNoDataAvail();
                        exitSplitScreenOnHide(readBoolean2);
                        break;
                    case 8:
                        parcel.enforceNoDataAvail();
                        startTask(parcel.readInt(), parcel.readInt(), parcel.readInt(), (Bundle) parcel.readTypedObject(Bundle.CREATOR));
                        break;
                    case 9:
                        String readString = parcel.readString();
                        String readString2 = parcel.readString();
                        int readInt3 = parcel.readInt();
                        int readInt4 = parcel.readInt();
                        parcel.enforceNoDataAvail();
                        startShortcut(readString, readString2, readInt3, readInt4, (Bundle) parcel.readTypedObject(Bundle.CREATOR), (UserHandle) parcel.readTypedObject(UserHandle.CREATOR));
                        break;
                    case 10:
                        int readInt5 = parcel.readInt();
                        int readInt6 = parcel.readInt();
                        parcel.enforceNoDataAvail();
                        startIntent((PendingIntent) parcel.readTypedObject(PendingIntent.CREATOR), (Intent) parcel.readTypedObject(Intent.CREATOR), readInt5, readInt6, (Bundle) parcel.readTypedObject(Bundle.CREATOR));
                        break;
                    case 11:
                        int readInt7 = parcel.readInt();
                        int readInt8 = parcel.readInt();
                        int readInt9 = parcel.readInt();
                        parcel.enforceNoDataAvail();
                        startTasks(readInt7, (Bundle) parcel.readTypedObject(Bundle.CREATOR), readInt8, (Bundle) parcel.readTypedObject(Bundle.CREATOR), readInt9, (RemoteTransition) parcel.readTypedObject(RemoteTransition.CREATOR));
                        break;
                    case 12:
                        int readInt10 = parcel.readInt();
                        int readInt11 = parcel.readInt();
                        int readInt12 = parcel.readInt();
                        parcel.enforceNoDataAvail();
                        startTasksWithLegacyTransition(readInt10, (Bundle) parcel.readTypedObject(Bundle.CREATOR), readInt11, (Bundle) parcel.readTypedObject(Bundle.CREATOR), readInt12, (RemoteAnimationAdapter) parcel.readTypedObject(RemoteAnimationAdapter.CREATOR));
                        break;
                    case 13:
                        parcel.enforceNoDataAvail();
                        RemoteAnimationTarget[] onGoingToRecentsLegacy = onGoingToRecentsLegacy(parcel.readBoolean(), (RemoteAnimationTarget[]) parcel.createTypedArray(RemoteAnimationTarget.CREATOR));
                        parcel2.writeNoException();
                        parcel2.writeTypedArray(onGoingToRecentsLegacy, 1);
                        break;
                    default:
                        return super.onTransact(i, parcel, parcel2, i2);
                }
                return true;
            }
            parcel2.writeString(ISplitScreen.DESCRIPTOR);
            return true;
        }

        private static class Proxy implements ISplitScreen {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return ISplitScreen.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void registerSplitScreenListener(ISplitScreenListener iSplitScreenListener) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISplitScreen.DESCRIPTOR);
                    obtain.writeStrongInterface(iSplitScreenListener);
                    this.mRemote.transact(2, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void unregisterSplitScreenListener(ISplitScreenListener iSplitScreenListener) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISplitScreen.DESCRIPTOR);
                    obtain.writeStrongInterface(iSplitScreenListener);
                    this.mRemote.transact(3, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void setSideStageVisibility(boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISplitScreen.DESCRIPTOR);
                    obtain.writeBoolean(z);
                    this.mRemote.transact(4, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void removeFromSideStage(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISplitScreen.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(5, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void exitSplitScreen(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISplitScreen.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(6, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void exitSplitScreenOnHide(boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISplitScreen.DESCRIPTOR);
                    obtain.writeBoolean(z);
                    this.mRemote.transact(7, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void startTask(int i, int i2, int i3, Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISplitScreen.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    obtain.writeTypedObject(bundle, 0);
                    this.mRemote.transact(8, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void startShortcut(String str, String str2, int i, int i2, Bundle bundle, UserHandle userHandle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISplitScreen.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeTypedObject(bundle, 0);
                    obtain.writeTypedObject(userHandle, 0);
                    this.mRemote.transact(9, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void startIntent(PendingIntent pendingIntent, Intent intent, int i, int i2, Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISplitScreen.DESCRIPTOR);
                    obtain.writeTypedObject(pendingIntent, 0);
                    obtain.writeTypedObject(intent, 0);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeTypedObject(bundle, 0);
                    this.mRemote.transact(10, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void startTasks(int i, Bundle bundle, int i2, Bundle bundle2, int i3, RemoteTransition remoteTransition) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISplitScreen.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeTypedObject(bundle, 0);
                    obtain.writeInt(i2);
                    obtain.writeTypedObject(bundle2, 0);
                    obtain.writeInt(i3);
                    obtain.writeTypedObject(remoteTransition, 0);
                    this.mRemote.transact(11, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void startTasksWithLegacyTransition(int i, Bundle bundle, int i2, Bundle bundle2, int i3, RemoteAnimationAdapter remoteAnimationAdapter) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISplitScreen.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeTypedObject(bundle, 0);
                    obtain.writeInt(i2);
                    obtain.writeTypedObject(bundle2, 0);
                    obtain.writeInt(i3);
                    obtain.writeTypedObject(remoteAnimationAdapter, 0);
                    this.mRemote.transact(12, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public RemoteAnimationTarget[] onGoingToRecentsLegacy(boolean z, RemoteAnimationTarget[] remoteAnimationTargetArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISplitScreen.DESCRIPTOR);
                    obtain.writeBoolean(z);
                    obtain.writeTypedArray(remoteAnimationTargetArr, 0);
                    this.mRemote.transact(13, obtain, obtain2, 0);
                    obtain2.readException();
                    return (RemoteAnimationTarget[]) obtain2.createTypedArray(RemoteAnimationTarget.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }
    }
}
