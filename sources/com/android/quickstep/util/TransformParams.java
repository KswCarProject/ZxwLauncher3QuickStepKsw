package com.android.quickstep.util;

import android.util.FloatProperty;
import android.view.SurfaceControl;
import androidx.core.app.NotificationCompat;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.Interpolators;
import com.android.quickstep.RemoteAnimationTargets;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.android.systemui.shared.system.SyncRtSurfaceTransactionApplierCompat;
import com.android.systemui.shared.system.TransactionCompat;

public class TransformParams {
    public static FloatProperty<TransformParams> PROGRESS = new FloatProperty<TransformParams>(NotificationCompat.CATEGORY_PROGRESS) {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(TransformParams transformParams, float f) {
            transformParams.setProgress(f);
        }

        public Float get(TransformParams transformParams) {
            return Float.valueOf(transformParams.getProgress());
        }
    };
    public static FloatProperty<TransformParams> TARGET_ALPHA = new FloatProperty<TransformParams>("targetAlpha") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(TransformParams transformParams, float f) {
            transformParams.setTargetAlpha(f);
        }

        public Float get(TransformParams transformParams) {
            return Float.valueOf(transformParams.getTargetAlpha());
        }
    };
    private BuilderProxy mBaseBuilderProxy = BuilderProxy.ALWAYS_VISIBLE;
    private float mCornerRadius = -1.0f;
    private BuilderProxy mHomeBuilderProxy = BuilderProxy.ALWAYS_VISIBLE;
    private float mProgress = 0.0f;
    private SurfaceControl mRecentsSurface;
    private SurfaceTransactionApplier mSyncTransactionApplier;
    private float mTargetAlpha = 1.0f;
    private RemoteAnimationTargets mTargetSet;

    @FunctionalInterface
    public interface BuilderProxy {
        public static final BuilderProxy ALWAYS_VISIBLE = $$Lambda$TransformParams$BuilderProxy$gXLTVBbGuGJEHxzIT3nClxE6Nyc.INSTANCE;
        public static final BuilderProxy NO_OP = $$Lambda$TransformParams$BuilderProxy$A1CnuTXoprljMKeogP9ktzvwVVY.INSTANCE;

        static /* synthetic */ void lambda$static$0(SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder, RemoteAnimationTargetCompat remoteAnimationTargetCompat, TransformParams transformParams) {
        }

        void onBuildTargetParams(SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder, RemoteAnimationTargetCompat remoteAnimationTargetCompat, TransformParams transformParams);
    }

    public TransformParams setProgress(float f) {
        this.mProgress = f;
        return this;
    }

    public TransformParams setCornerRadius(float f) {
        this.mCornerRadius = f;
        return this;
    }

    public TransformParams setTargetAlpha(float f) {
        this.mTargetAlpha = f;
        return this;
    }

    public TransformParams setTargetSet(RemoteAnimationTargets remoteAnimationTargets) {
        this.mTargetSet = remoteAnimationTargets;
        return this;
    }

    public TransformParams setSyncTransactionApplier(SurfaceTransactionApplier surfaceTransactionApplier) {
        this.mSyncTransactionApplier = surfaceTransactionApplier;
        return this;
    }

    public TransformParams setBaseBuilderProxy(BuilderProxy builderProxy) {
        this.mBaseBuilderProxy = builderProxy;
        return this;
    }

    public TransformParams setHomeBuilderProxy(BuilderProxy builderProxy) {
        this.mHomeBuilderProxy = builderProxy;
        return this;
    }

    public SyncRtSurfaceTransactionApplierCompat.SurfaceParams[] createSurfaceParams(BuilderProxy builderProxy) {
        RemoteAnimationTargets remoteAnimationTargets = this.mTargetSet;
        SyncRtSurfaceTransactionApplierCompat.SurfaceParams[] surfaceParamsArr = new SyncRtSurfaceTransactionApplierCompat.SurfaceParams[remoteAnimationTargets.unfilteredApps.length];
        this.mRecentsSurface = getRecentsSurface(remoteAnimationTargets);
        for (int i = 0; i < remoteAnimationTargets.unfilteredApps.length; i++) {
            RemoteAnimationTargetCompat remoteAnimationTargetCompat = remoteAnimationTargets.unfilteredApps[i];
            SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder = new SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder(remoteAnimationTargetCompat.leash);
            if (remoteAnimationTargetCompat.mode != remoteAnimationTargets.targetMode) {
                this.mBaseBuilderProxy.onBuildTargetParams(builder, remoteAnimationTargetCompat, this);
            } else if (remoteAnimationTargetCompat.activityType == 2) {
                this.mHomeBuilderProxy.onBuildTargetParams(builder, remoteAnimationTargetCompat, this);
            } else {
                if (remoteAnimationTargetCompat.activityType != 4 || !remoteAnimationTargetCompat.isNotInRecents) {
                    builder.withAlpha(getTargetAlpha());
                } else {
                    builder.withAlpha(1.0f - Interpolators.DEACCEL_2_5.getInterpolation(Utilities.boundToRange(getProgress(), 0.0f, 1.0f)));
                }
                builderProxy.onBuildTargetParams(builder, remoteAnimationTargetCompat, this);
            }
            surfaceParamsArr[i] = builder.build();
        }
        return surfaceParamsArr;
    }

    private static SurfaceControl getRecentsSurface(RemoteAnimationTargets remoteAnimationTargets) {
        for (RemoteAnimationTargetCompat remoteAnimationTargetCompat : remoteAnimationTargets.unfilteredApps) {
            if (remoteAnimationTargetCompat.mode != remoteAnimationTargets.targetMode) {
                return remoteAnimationTargetCompat.leash;
            }
            if (remoteAnimationTargetCompat.activityType == 3) {
                return remoteAnimationTargetCompat.leash;
            }
        }
        return null;
    }

    public float getProgress() {
        return this.mProgress;
    }

    public float getTargetAlpha() {
        return this.mTargetAlpha;
    }

    public float getCornerRadius() {
        return this.mCornerRadius;
    }

    public SurfaceControl getRecentsSurface() {
        return this.mRecentsSurface;
    }

    public RemoteAnimationTargets getTargetSet() {
        return this.mTargetSet;
    }

    public void applySurfaceParams(SyncRtSurfaceTransactionApplierCompat.SurfaceParams... surfaceParamsArr) {
        SurfaceTransactionApplier surfaceTransactionApplier = this.mSyncTransactionApplier;
        if (surfaceTransactionApplier != null) {
            surfaceTransactionApplier.scheduleApply(surfaceParamsArr);
            return;
        }
        TransactionCompat transactionCompat = new TransactionCompat();
        for (SyncRtSurfaceTransactionApplierCompat.SurfaceParams applyParams : surfaceParamsArr) {
            SyncRtSurfaceTransactionApplierCompat.applyParams(transactionCompat, applyParams);
        }
        transactionCompat.apply();
    }
}
