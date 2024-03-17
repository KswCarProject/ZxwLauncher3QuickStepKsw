package com.android.launcher3.taskbar.unfold;

import com.android.systemui.unfold.util.ScopedUnfoldTransitionProgressProvider;

public class NonDestroyableScopedUnfoldTransitionProgressProvider extends ScopedUnfoldTransitionProgressProvider {
    public void destroy() {
    }
}
