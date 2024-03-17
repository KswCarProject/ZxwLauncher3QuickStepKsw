package com.android.launcher3.model;

import com.android.launcher3.model.data.ItemInfo;
import java.util.function.Predicate;

/* renamed from: com.android.launcher3.model.-$$Lambda$3OuuS3VRjeQqVClpMT_zkjgRPDU  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$3OuuS3VRjeQqVClpMT_zkjgRPDU implements Predicate {
    public static final /* synthetic */ $$Lambda$3OuuS3VRjeQqVClpMT_zkjgRPDU INSTANCE = new $$Lambda$3OuuS3VRjeQqVClpMT_zkjgRPDU();

    private /* synthetic */ $$Lambda$3OuuS3VRjeQqVClpMT_zkjgRPDU() {
    }

    public final boolean test(Object obj) {
        return PredictionHelper.isTrackedForWidgetPrediction((ItemInfo) obj);
    }
}
