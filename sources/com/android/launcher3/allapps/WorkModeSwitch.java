package com.android.launcher3.allapps;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.Button;
import com.android.launcher3.Insettable;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.KeyboardInsetAnimationCallback;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.StringCache;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.workprofile.PersonalWorkSlidingTabStrip;

public class WorkModeSwitch extends Button implements Insettable, View.OnClickListener, KeyboardInsetAnimationCallback.KeyboardInsetListener, PersonalWorkSlidingTabStrip.OnActivePageChangedListener {
    private static final int FLAG_FADE_ONGOING = 2;
    private static final int FLAG_PROFILE_TOGGLE_ONGOING = 8;
    private static final int FLAG_TRANSLATION_ONGOING = 4;
    private int mFlags;
    private final Rect mInsets;
    private boolean mOnWorkTab;
    private boolean mWorkEnabled;

    public WorkModeSwitch(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public WorkModeSwitch(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public WorkModeSwitch(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mInsets = new Rect();
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        setSelected(true);
        setOnClickListener(this);
        if (Utilities.ATLEAST_R) {
            setWindowInsetsAnimationCallback(new KeyboardInsetAnimationCallback(this));
        }
        ActivityContext activityContext = (ActivityContext) ActivityContext.lookupContext(getContext());
        setInsets(activityContext.getDeviceProfile().getInsets());
        StringCache stringCache = activityContext.getStringCache();
        if (stringCache != null) {
            setText(stringCache.workProfilePauseButton);
        }
    }

    public void setInsets(Rect rect) {
        int i = rect.bottom - this.mInsets.bottom;
        this.mInsets.set(rect);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
        if (marginLayoutParams != null) {
            marginLayoutParams.bottomMargin = i + marginLayoutParams.bottomMargin;
        }
    }

    public void onActivePageChanged(int i) {
        boolean z = true;
        if (i != 1) {
            z = false;
        }
        this.mOnWorkTab = z;
        updateVisibility();
    }

    public void onClick(View view) {
        if (Utilities.ATLEAST_P && isEnabled()) {
            setFlag(8);
            ActivityContext activityContext = (ActivityContext) ActivityContext.lookupContext(getContext());
            activityContext.getStatsLogManager().logger().log(StatsLogManager.LauncherEvent.LAUNCHER_TURN_OFF_WORK_APPS_TAP);
            activityContext.getAppsView().getWorkManager().setWorkProfileEnabled(false);
        }
    }

    public boolean isEnabled() {
        return super.isEnabled() && getVisibility() == 0 && this.mFlags == 0;
    }

    public void updateCurrentState(boolean z) {
        removeFlag(8);
        if (this.mWorkEnabled != z) {
            this.mWorkEnabled = z;
            updateVisibility();
        }
    }

    private void updateVisibility() {
        clearAnimation();
        if (this.mWorkEnabled && this.mOnWorkTab) {
            setFlag(2);
            setVisibility(0);
            animate().alpha(1.0f).withEndAction(new Runnable() {
                public final void run() {
                    WorkModeSwitch.this.lambda$updateVisibility$0$WorkModeSwitch();
                }
            }).start();
        } else if (getVisibility() != 8) {
            setFlag(2);
            animate().alpha(0.0f).withEndAction(new Runnable() {
                public final void run() {
                    WorkModeSwitch.this.lambda$updateVisibility$1$WorkModeSwitch();
                }
            }).start();
        }
    }

    public /* synthetic */ void lambda$updateVisibility$0$WorkModeSwitch() {
        removeFlag(2);
    }

    public /* synthetic */ void lambda$updateVisibility$1$WorkModeSwitch() {
        removeFlag(2);
        setVisibility(8);
    }

    public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        if (Utilities.ATLEAST_R && isEnabled()) {
            setTranslationY(0.0f);
            if (windowInsets.isVisible(WindowInsets.Type.ime())) {
                setTranslationY((float) (this.mInsets.bottom - windowInsets.getInsets(WindowInsets.Type.ime()).bottom));
            }
        }
        return windowInsets;
    }

    public void onTranslationStart() {
        setFlag(4);
    }

    public void onTranslationEnd() {
        removeFlag(4);
    }

    private void setFlag(int i) {
        this.mFlags = i | this.mFlags;
    }

    private void removeFlag(int i) {
        this.mFlags = (~i) & this.mFlags;
    }
}
