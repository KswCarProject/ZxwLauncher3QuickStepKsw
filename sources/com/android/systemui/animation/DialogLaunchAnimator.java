package com.android.systemui.animation;

import android.app.Dialog;
import android.os.Looper;
import android.service.dreams.IDreamManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import com.android.launcher3.systemui.R;
import com.android.systemui.animation.ActivityLaunchAnimator;
import com.android.systemui.animation.LaunchAnimator;
import java.util.HashSet;
import java.util.Iterator;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u0000 \u001d2\u00020\u0001:\u0001\u001dB#\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007¢\u0006\u0002\u0010\bJ#\u0010\r\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u0012H\u0007¢\u0006\u0002\u0010\u0013J\u0006\u0010\u0014\u001a\u00020\u0015J\u000e\u0010\u0016\u001a\u00020\u00152\u0006\u0010\u0017\u001a\u00020\u0018J \u0010\u0019\u001a\u00020\u00152\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u001a\u001a\u00020\u00182\b\b\u0002\u0010\u001b\u001a\u00020\u0007J\"\u0010\u001c\u001a\u00020\u00152\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\u001b\u001a\u00020\u0007H\u0007R\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0004¢\u0006\u0002\n\u0000R\u001e\u0010\t\u001a\u0012\u0012\u0004\u0012\u00020\u000b0\nj\b\u0012\u0004\u0012\u00020\u000b`\fX\u0004¢\u0006\u0002\n\u0000¨\u0006\u001e"}, d2 = {"Lcom/android/systemui/animation/DialogLaunchAnimator;", "", "dreamManager", "Landroid/service/dreams/IDreamManager;", "launchAnimator", "Lcom/android/systemui/animation/LaunchAnimator;", "isForTesting", "", "(Landroid/service/dreams/IDreamManager;Lcom/android/systemui/animation/LaunchAnimator;Z)V", "openedDialogs", "Ljava/util/HashSet;", "Lcom/android/systemui/animation/AnimatedDialog;", "Lkotlin/collections/HashSet;", "createActivityLaunchController", "Lcom/android/systemui/animation/ActivityLaunchAnimator$Controller;", "view", "Landroid/view/View;", "cujType", "", "(Landroid/view/View;Ljava/lang/Integer;)Lcom/android/systemui/animation/ActivityLaunchAnimator$Controller;", "disableAllCurrentDialogsExitAnimations", "", "dismissStack", "dialog", "Landroid/app/Dialog;", "showFromDialog", "animateFrom", "animateBackgroundBoundsChange", "showFromView", "Companion", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: DialogLaunchAnimator.kt */
public final class DialogLaunchAnimator {
    private static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    @Deprecated
    private static final LaunchAnimator.Interpolators INTERPOLATORS = LaunchAnimator.Interpolators.copy$default(ActivityLaunchAnimator.Companion.getINTERPOLATORS(), (Interpolator) null, ActivityLaunchAnimator.Companion.getINTERPOLATORS().getPositionInterpolator(), (Interpolator) null, (Interpolator) null, 13, (Object) null);
    @Deprecated
    private static final int TAG_LAUNCH_ANIMATION_RUNNING = R.id.tag_launch_animation_running;
    @Deprecated
    private static final LaunchAnimator.Timings TIMINGS = ActivityLaunchAnimator.TIMINGS;
    private final IDreamManager dreamManager;
    private final boolean isForTesting;
    private final LaunchAnimator launchAnimator;
    /* access modifiers changed from: private */
    public final HashSet<AnimatedDialog> openedDialogs;

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public DialogLaunchAnimator(IDreamManager iDreamManager) {
        this(iDreamManager, (LaunchAnimator) null, false, 6, (DefaultConstructorMarker) null);
        Intrinsics.checkNotNullParameter(iDreamManager, "dreamManager");
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public DialogLaunchAnimator(IDreamManager iDreamManager, LaunchAnimator launchAnimator2) {
        this(iDreamManager, launchAnimator2, false, 4, (DefaultConstructorMarker) null);
        Intrinsics.checkNotNullParameter(iDreamManager, "dreamManager");
        Intrinsics.checkNotNullParameter(launchAnimator2, "launchAnimator");
    }

    public final ActivityLaunchAnimator.Controller createActivityLaunchController(View view) {
        Intrinsics.checkNotNullParameter(view, "view");
        return createActivityLaunchController$default(this, view, (Integer) null, 2, (Object) null);
    }

    public final void showFromView(Dialog dialog, View view) {
        Intrinsics.checkNotNullParameter(dialog, "dialog");
        Intrinsics.checkNotNullParameter(view, "view");
        showFromView$default(this, dialog, view, false, 4, (Object) null);
    }

    public DialogLaunchAnimator(IDreamManager iDreamManager, LaunchAnimator launchAnimator2, boolean z) {
        Intrinsics.checkNotNullParameter(iDreamManager, "dreamManager");
        Intrinsics.checkNotNullParameter(launchAnimator2, "launchAnimator");
        this.dreamManager = iDreamManager;
        this.launchAnimator = launchAnimator2;
        this.isForTesting = z;
        this.openedDialogs = new HashSet<>();
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public /* synthetic */ DialogLaunchAnimator(IDreamManager iDreamManager, LaunchAnimator launchAnimator2, boolean z, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(iDreamManager, (i & 2) != 0 ? new LaunchAnimator(TIMINGS, INTERPOLATORS) : launchAnimator2, (i & 4) != 0 ? false : z);
    }

    @Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0004¢\u0006\u0002\n\u0000¨\u0006\t"}, d2 = {"Lcom/android/systemui/animation/DialogLaunchAnimator$Companion;", "", "()V", "INTERPOLATORS", "Lcom/android/systemui/animation/LaunchAnimator$Interpolators;", "TAG_LAUNCH_ANIMATION_RUNNING", "", "TIMINGS", "Lcom/android/systemui/animation/LaunchAnimator$Timings;", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: DialogLaunchAnimator.kt */
    private static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    public static /* synthetic */ void showFromView$default(DialogLaunchAnimator dialogLaunchAnimator, Dialog dialog, View view, boolean z, int i, Object obj) {
        if ((i & 4) != 0) {
            z = false;
        }
        dialogLaunchAnimator.showFromView(dialog, view, z);
    }

    public final void showFromView(Dialog dialog, View view, boolean z) {
        Object obj;
        ViewGroup dialogContentWithBackground;
        Intrinsics.checkNotNullParameter(dialog, "dialog");
        Intrinsics.checkNotNullParameter(view, "view");
        if (Intrinsics.areEqual((Object) Looper.myLooper(), (Object) Looper.getMainLooper())) {
            Iterator it = this.openedDialogs.iterator();
            while (true) {
                if (!it.hasNext()) {
                    obj = null;
                    break;
                }
                obj = it.next();
                if (Intrinsics.areEqual((Object) ((AnimatedDialog) obj).getDialog().getWindow().getDecorView().getViewRootImpl(), (Object) view.getViewRootImpl())) {
                    break;
                }
            }
            AnimatedDialog animatedDialog = (AnimatedDialog) obj;
            if (!(animatedDialog == null || (dialogContentWithBackground = animatedDialog.getDialogContentWithBackground()) == null)) {
                view = dialogContentWithBackground;
            }
            View view2 = view;
            int i = TAG_LAUNCH_ANIMATION_RUNNING;
            if (view2.getTag(i) != null) {
                Log.e("DialogLaunchAnimator", "Not running dialog launch animation as there is already one running");
                dialog.show();
                return;
            }
            view2.setTag(i, true);
            AnimatedDialog animatedDialog2 = new AnimatedDialog(this.launchAnimator, this.dreamManager, view2, new DialogLaunchAnimator$showFromView$animatedDialog$1(this), dialog, z, animatedDialog, this.isForTesting);
            this.openedDialogs.add(animatedDialog2);
            animatedDialog2.start();
            return;
        }
        throw new IllegalStateException("showFromView must be called from the main thread and dialog must be created in the main thread");
    }

    public static /* synthetic */ void showFromDialog$default(DialogLaunchAnimator dialogLaunchAnimator, Dialog dialog, Dialog dialog2, boolean z, int i, Object obj) {
        if ((i & 4) != 0) {
            z = false;
        }
        dialogLaunchAnimator.showFromDialog(dialog, dialog2, z);
    }

    public final void showFromDialog(Dialog dialog, Dialog dialog2, boolean z) {
        ViewGroup viewGroup;
        Object obj;
        Intrinsics.checkNotNullParameter(dialog, "dialog");
        Intrinsics.checkNotNullParameter(dialog2, "animateFrom");
        Iterator it = this.openedDialogs.iterator();
        while (true) {
            viewGroup = null;
            if (!it.hasNext()) {
                obj = null;
                break;
            }
            obj = it.next();
            if (Intrinsics.areEqual((Object) ((AnimatedDialog) obj).getDialog(), (Object) dialog2)) {
                break;
            }
        }
        AnimatedDialog animatedDialog = (AnimatedDialog) obj;
        if (animatedDialog != null) {
            viewGroup = animatedDialog.getDialogContentWithBackground();
        }
        if (viewGroup != null) {
            showFromView(dialog, viewGroup, z);
            return;
        }
        throw new IllegalStateException("The animateFrom dialog was not animated using DialogLaunchAnimator.showFrom(View|Dialog)");
    }

    public static /* synthetic */ ActivityLaunchAnimator.Controller createActivityLaunchController$default(DialogLaunchAnimator dialogLaunchAnimator, View view, Integer num, int i, Object obj) {
        if ((i & 2) != 0) {
            num = null;
        }
        return dialogLaunchAnimator.createActivityLaunchController(view, num);
    }

    public final ActivityLaunchAnimator.Controller createActivityLaunchController(View view, Integer num) {
        Object obj;
        ViewGroup dialogContentWithBackground;
        ActivityLaunchAnimator.Controller fromView;
        Intrinsics.checkNotNullParameter(view, "view");
        Iterator it = this.openedDialogs.iterator();
        while (true) {
            if (!it.hasNext()) {
                obj = null;
                break;
            }
            obj = it.next();
            if (Intrinsics.areEqual((Object) ((AnimatedDialog) obj).getDialog().getWindow().getDecorView().getViewRootImpl(), (Object) view.getViewRootImpl())) {
                break;
            }
        }
        AnimatedDialog animatedDialog = (AnimatedDialog) obj;
        if (animatedDialog == null) {
            return null;
        }
        animatedDialog.setExitAnimationDisabled(true);
        Dialog dialog = animatedDialog.getDialog();
        if (!dialog.isShowing() || (dialogContentWithBackground = animatedDialog.getDialogContentWithBackground()) == null || (fromView = ActivityLaunchAnimator.Controller.Companion.fromView(dialogContentWithBackground, num)) == null) {
            return null;
        }
        return new DialogLaunchAnimator$createActivityLaunchController$1(fromView, dialog, animatedDialog);
    }

    public final void disableAllCurrentDialogsExitAnimations() {
        for (AnimatedDialog exitAnimationDisabled : this.openedDialogs) {
            exitAnimationDisabled.setExitAnimationDisabled(true);
        }
    }

    public final void dismissStack(Dialog dialog) {
        Object obj;
        Intrinsics.checkNotNullParameter(dialog, "dialog");
        Iterator it = this.openedDialogs.iterator();
        while (true) {
            if (!it.hasNext()) {
                obj = null;
                break;
            }
            obj = it.next();
            if (Intrinsics.areEqual((Object) ((AnimatedDialog) obj).getDialog(), (Object) dialog)) {
                break;
            }
        }
        AnimatedDialog animatedDialog = (AnimatedDialog) obj;
        if (animatedDialog != null) {
            animatedDialog.setTouchSurface(animatedDialog.prepareForStackDismiss());
        }
        dialog.dismiss();
    }
}
