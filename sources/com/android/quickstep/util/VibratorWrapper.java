package com.android.quickstep.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioAttributes;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.MainThreadInitializedObject;

public class VibratorWrapper {
    public static final VibrationEffect EFFECT_CLICK;
    public static final VibrationEffect EFFECT_TEXTURE_TICK = VibrationEffect.createPredefined(21);
    public static final MainThreadInitializedObject<VibratorWrapper> INSTANCE = new MainThreadInitializedObject<>($$Lambda$lftju_8_zBPcct0pb95LgWKGG_c.INSTANCE);
    public static final VibrationEffect OVERVIEW_HAPTIC;
    public static final AudioAttributes VIBRATION_ATTRS = new AudioAttributes.Builder().setUsage(13).setContentType(4).build();
    private final boolean mHasVibrator;
    /* access modifiers changed from: private */
    public boolean mIsHapticFeedbackEnabled;
    private final Vibrator mVibrator;

    static {
        VibrationEffect createPredefined = VibrationEffect.createPredefined(0);
        EFFECT_CLICK = createPredefined;
        OVERVIEW_HAPTIC = createPredefined;
    }

    public VibratorWrapper(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Vibrator.class);
        this.mVibrator = vibrator;
        boolean hasVibrator = vibrator.hasVibrator();
        this.mHasVibrator = hasVibrator;
        if (hasVibrator) {
            final ContentResolver contentResolver = context.getContentResolver();
            this.mIsHapticFeedbackEnabled = isHapticFeedbackEnabled(contentResolver);
            contentResolver.registerContentObserver(Settings.System.getUriFor("haptic_feedback_enabled"), false, new ContentObserver(Executors.MAIN_EXECUTOR.getHandler()) {
                public void onChange(boolean z) {
                    VibratorWrapper vibratorWrapper = VibratorWrapper.this;
                    boolean unused = vibratorWrapper.mIsHapticFeedbackEnabled = vibratorWrapper.isHapticFeedbackEnabled(contentResolver);
                }
            });
            return;
        }
        this.mIsHapticFeedbackEnabled = false;
    }

    /* access modifiers changed from: private */
    public boolean isHapticFeedbackEnabled(ContentResolver contentResolver) {
        return Settings.System.getInt(contentResolver, "haptic_feedback_enabled", 0) == 1;
    }

    public void vibrate(VibrationEffect vibrationEffect) {
        if (this.mHasVibrator && this.mIsHapticFeedbackEnabled) {
            Executors.UI_HELPER_EXECUTOR.execute(new Runnable(vibrationEffect) {
                public final /* synthetic */ VibrationEffect f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    VibratorWrapper.this.lambda$vibrate$0$VibratorWrapper(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$vibrate$0$VibratorWrapper(VibrationEffect vibrationEffect) {
        this.mVibrator.vibrate(vibrationEffect, VIBRATION_ATTRS);
    }

    public void vibrate(int i, float f, VibrationEffect vibrationEffect) {
        if (this.mHasVibrator && this.mIsHapticFeedbackEnabled) {
            Executors.UI_HELPER_EXECUTOR.execute(new Runnable(i, f, vibrationEffect) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ float f$2;
                public final /* synthetic */ VibrationEffect f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    VibratorWrapper.this.lambda$vibrate$1$VibratorWrapper(this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    public /* synthetic */ void lambda$vibrate$1$VibratorWrapper(int i, float f, VibrationEffect vibrationEffect) {
        if (Utilities.ATLEAST_R && i >= 0) {
            if (this.mVibrator.areAllPrimitivesSupported(new int[]{i})) {
                this.mVibrator.vibrate(VibrationEffect.startComposition().addPrimitive(i, f).compose(), VIBRATION_ATTRS);
                return;
            }
        }
        this.mVibrator.vibrate(vibrationEffect, VIBRATION_ATTRS);
    }
}
