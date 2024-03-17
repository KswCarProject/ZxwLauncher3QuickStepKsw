package com.android.quickstep;

import android.os.RemoteException;
import android.util.Log;

@FunctionalInterface
public interface RemoteRunnable {
    void run() throws RemoteException;

    static void executeSafely(RemoteRunnable remoteRunnable) {
        try {
            remoteRunnable.run();
        } catch (RemoteException e) {
            Log.e("RemoteRunnable", "Error calling remote method", e);
        }
    }
}
