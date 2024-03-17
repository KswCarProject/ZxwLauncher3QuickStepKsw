package com.android.launcher3.widget.picker;

import android.view.MotionEvent;
import android.view.ViewGroup;
import com.android.launcher3.widget.picker.SearchAndRecommendationsScrollController;

/* renamed from: com.android.launcher3.widget.picker.-$$Lambda$SearchAndRecommendationsScrollController$mTv8BJ-5LYS3dqX5PEeTERLatUI  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$SearchAndRecommendationsScrollController$mTv8BJ5LYS3dqX5PEeTERLatUI implements SearchAndRecommendationsScrollController.MotionEventProxyMethod {
    public static final /* synthetic */ $$Lambda$SearchAndRecommendationsScrollController$mTv8BJ5LYS3dqX5PEeTERLatUI INSTANCE = new $$Lambda$SearchAndRecommendationsScrollController$mTv8BJ5LYS3dqX5PEeTERLatUI();

    private /* synthetic */ $$Lambda$SearchAndRecommendationsScrollController$mTv8BJ5LYS3dqX5PEeTERLatUI() {
    }

    public final boolean proxyEvent(ViewGroup viewGroup, MotionEvent motionEvent) {
        return viewGroup.onInterceptTouchEvent(motionEvent);
    }
}
