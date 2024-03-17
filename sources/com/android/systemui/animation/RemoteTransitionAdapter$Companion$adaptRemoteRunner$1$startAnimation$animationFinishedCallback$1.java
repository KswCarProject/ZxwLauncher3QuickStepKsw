package com.android.systemui.animation;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.Log;
import android.view.IRemoteAnimationFinishedCallback;
import android.view.SurfaceControl;
import android.window.IRemoteTransitionFinishedCallback;
import android.window.TransitionInfo;
import android.window.WindowContainerTransaction;
import com.android.systemui.animation.RemoteTransitionAdapter;
import kotlin.Metadata;

@Metadata(d1 = {"\u0000\u0017\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\n\u0010\u0002\u001a\u0004\u0018\u00010\u0003H\u0016J\b\u0010\u0004\u001a\u00020\u0005H\u0016¨\u0006\u0006"}, d2 = {"com/android/systemui/animation/RemoteTransitionAdapter$Companion$adaptRemoteRunner$1$startAnimation$animationFinishedCallback$1", "Landroid/view/IRemoteAnimationFinishedCallback;", "asBinder", "Landroid/os/IBinder;", "onAnimationFinished", "", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: RemoteTransitionAdapter.kt */
public final class RemoteTransitionAdapter$Companion$adaptRemoteRunner$1$startAnimation$animationFinishedCallback$1 implements IRemoteAnimationFinishedCallback {
    final /* synthetic */ RemoteTransitionAdapter.CounterRotator $counterLauncher;
    final /* synthetic */ RemoteTransitionAdapter.CounterRotator $counterWallpaper;
    final /* synthetic */ IRemoteTransitionFinishedCallback $finishCallback;
    final /* synthetic */ TransitionInfo $info;
    final /* synthetic */ ArrayMap<SurfaceControl, SurfaceControl> $leashMap;

    public IBinder asBinder() {
        return null;
    }

    RemoteTransitionAdapter$Companion$adaptRemoteRunner$1$startAnimation$animationFinishedCallback$1(RemoteTransitionAdapter.CounterRotator counterRotator, RemoteTransitionAdapter.CounterRotator counterRotator2, TransitionInfo transitionInfo, ArrayMap<SurfaceControl, SurfaceControl> arrayMap, IRemoteTransitionFinishedCallback iRemoteTransitionFinishedCallback) {
        this.$counterLauncher = counterRotator;
        this.$counterWallpaper = counterRotator2;
        this.$info = transitionInfo;
        this.$leashMap = arrayMap;
        this.$finishCallback = iRemoteTransitionFinishedCallback;
    }

    public void onAnimationFinished() {
        SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
        this.$counterLauncher.cleanUp(transaction);
        this.$counterWallpaper.cleanUp(transaction);
        int size = this.$info.getChanges().size() - 1;
        if (size >= 0) {
            while (true) {
                int i = size - 1;
                ((TransitionInfo.Change) this.$info.getChanges().get(size)).getLeash().release();
                if (i < 0) {
                    break;
                }
                size = i;
            }
        }
        int size2 = this.$leashMap.size() - 1;
        if (size2 >= 0) {
            while (true) {
                int i2 = size2 - 1;
                this.$leashMap.valueAt(size2).release();
                if (i2 >= 0) {
                    size2 = i2;
                }
            }
            this.$finishCallback.onTransitionFinished((WindowContainerTransaction) null, transaction);
        }
        try {
            this.$finishCallback.onTransitionFinished((WindowContainerTransaction) null, transaction);
        } catch (RemoteException e) {
            Log.e("ActivityOptionsCompat", "Failed to call app controlled animation finished callback", e);
        }
    }
}
