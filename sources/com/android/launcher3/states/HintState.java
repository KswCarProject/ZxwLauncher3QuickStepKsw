package com.android.launcher3.states;

import android.content.Context;
import androidx.core.graphics.ColorUtils;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.R;
import com.android.launcher3.util.Themes;

public class HintState extends LauncherState {
    private static final int STATE_FLAGS = ((FLAG_WORKSPACE_INACCESSIBLE | 2) | FLAG_HAS_SYS_UI_SCRIM);

    /* access modifiers changed from: protected */
    public float getDepthUnchecked(Context context) {
        return 0.15f;
    }

    public int getTransitionDuration(Context context, boolean z) {
        return 80;
    }

    public HintState(int i) {
        this(i, 2);
    }

    public HintState(int i, int i2) {
        super(i, i2, STATE_FLAGS);
    }

    public int getWorkspaceScrimColor(Launcher launcher) {
        return ColorUtils.setAlphaComponent(Themes.getAttrColor(launcher, R.attr.overviewScrimColor), 100);
    }

    public LauncherState.ScaleAndTranslation getWorkspaceScaleAndTranslation(Launcher launcher) {
        return new LauncherState.ScaleAndTranslation(0.92f, 0.0f, 0.0f);
    }
}
