package com.android.systemui.animation;

import android.view.RemoteAnimationAdapter;
import com.android.systemui.animation.ActivityLaunchAnimator;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Lambda;

@Metadata(d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u00012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003H\n¢\u0006\u0004\b\u0004\u0010\u0005"}, d2 = {"<anonymous>", "", "it", "Landroid/view/RemoteAnimationAdapter;", "invoke", "(Landroid/view/RemoteAnimationAdapter;)Ljava/lang/Integer;"}, k = 3, mv = {1, 6, 0}, xi = 48)
/* compiled from: ActivityLaunchAnimator.kt */
final class ActivityLaunchAnimator$startPendingIntentWithAnimation$1 extends Lambda implements Function1<RemoteAnimationAdapter, Integer> {
    final /* synthetic */ ActivityLaunchAnimator.PendingIntentStarter $intentStarter;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    ActivityLaunchAnimator$startPendingIntentWithAnimation$1(ActivityLaunchAnimator.PendingIntentStarter pendingIntentStarter) {
        super(1);
        this.$intentStarter = pendingIntentStarter;
    }

    public final Integer invoke(RemoteAnimationAdapter remoteAnimationAdapter) {
        return Integer.valueOf(this.$intentStarter.startPendingIntent(remoteAnimationAdapter));
    }
}
