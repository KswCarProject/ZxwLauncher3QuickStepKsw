package com.android.quickstep.views;

import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.graphics.Outline;
import android.graphics.Paint;
import android.icu.text.MeasureFormat;
import android.icu.util.Measure;
import android.icu.util.MeasureUnit;
import android.os.UserHandle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.SplitConfigurationOptions;
import com.android.systemui.shared.recents.model.Task;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.Duration;
import java.util.Locale;

public final class DigitalWellBeingToast {
    static final int MINUTE_MS = 60000;
    static final Intent OPEN_APP_USAGE_SETTINGS_TEMPLATE = new Intent("android.settings.action.APP_USAGE_SETTINGS");
    private static final int SPLIT_BANNER_FULLSCREEN = 0;
    private static final int SPLIT_GRID_BANNER_LARGE = 1;
    private static final int SPLIT_GRID_BANNER_SMALL = 2;
    private static final String TAG = DigitalWellBeingToast.class.getSimpleName();
    private static final float THRESHOLD_LEFT_ICON_ONLY = 0.4f;
    private static final float THRESHOLD_RIGHT_ICON_ONLY = 0.6f;
    private final BaseDraggingActivity mActivity;
    private long mAppRemainingTimeMs;
    private View mBanner;
    private float mBannerOffsetPercentage;
    private boolean mHasLimit;
    private final LauncherApps mLauncherApps;
    /* access modifiers changed from: private */
    public float mModalOffset = 0.0f;
    /* access modifiers changed from: private */
    public ViewOutlineProvider mOldBannerOutlineProvider;
    private int mSplitBannerConfig = 0;
    private float mSplitOffsetTranslationX;
    /* access modifiers changed from: private */
    public float mSplitOffsetTranslationY;
    private SplitConfigurationOptions.StagedSplitBounds mStagedSplitBounds;
    private Task mTask;
    private final TaskView mTaskView;

    @Retention(RetentionPolicy.SOURCE)
    @interface SPLIT_BANNER_CONFIG {
    }

    public DigitalWellBeingToast(BaseDraggingActivity baseDraggingActivity, TaskView taskView) {
        this.mActivity = baseDraggingActivity;
        this.mTaskView = taskView;
        this.mLauncherApps = (LauncherApps) baseDraggingActivity.getSystemService(LauncherApps.class);
    }

    private void setNoLimit() {
        this.mHasLimit = false;
        this.mTaskView.setContentDescription(this.mTask.titleDescription);
        replaceBanner((View) null);
        this.mAppRemainingTimeMs = 0;
    }

    private void setLimit(long j, long j2) {
        this.mAppRemainingTimeMs = j2;
        this.mHasLimit = true;
        TextView textView = (TextView) this.mActivity.getViewCache().getView(R.layout.digital_wellbeing_toast, this.mActivity, this.mTaskView);
        textView.setText(Utilities.prefixTextWithIcon(this.mActivity, R.drawable.ic_hourglass_top, getText()));
        textView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                DigitalWellBeingToast.this.openAppUsageSettings(view);
            }
        });
        replaceBanner(textView);
        this.mTaskView.setContentDescription(getContentDescriptionForTask(this.mTask, j, j2));
    }

    public String getText() {
        return getText(this.mAppRemainingTimeMs, false);
    }

    public boolean hasLimit() {
        return this.mHasLimit;
    }

    public void initialize(Task task) {
        this.mTask = task;
        Executors.THREAD_POOL_EXECUTOR.execute(new Runnable(task) {
            public final /* synthetic */ Task f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                DigitalWellBeingToast.this.lambda$initialize$1$DigitalWellBeingToast(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$initialize$1$DigitalWellBeingToast(Task task) {
        LauncherApps.AppUsageLimit appUsageLimit = this.mLauncherApps.getAppUsageLimit(task.getTopComponent().getPackageName(), UserHandle.of(task.key.userId));
        long j = -1;
        long totalUsageLimit = appUsageLimit != null ? appUsageLimit.getTotalUsageLimit() : -1;
        if (appUsageLimit != null) {
            j = appUsageLimit.getUsageRemaining();
        }
        this.mTaskView.post(new Runnable(totalUsageLimit, j) {
            public final /* synthetic */ long f$1;
            public final /* synthetic */ long f$2;

            {
                this.f$1 = r2;
                this.f$2 = r4;
            }

            public final void run() {
                DigitalWellBeingToast.this.lambda$initialize$0$DigitalWellBeingToast(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$initialize$0$DigitalWellBeingToast(long j, long j2) {
        if (j < 0 || j2 < 0) {
            setNoLimit();
        } else {
            setLimit(j, j2);
        }
    }

    public void setSplitConfiguration(SplitConfigurationOptions.StagedSplitBounds stagedSplitBounds) {
        this.mStagedSplitBounds = stagedSplitBounds;
        if (stagedSplitBounds == null || !this.mActivity.getDeviceProfile().isTablet || this.mTaskView.isFocusedTask()) {
            this.mSplitBannerConfig = 0;
            return;
        }
        int i = 1;
        if (!this.mActivity.getDeviceProfile().isLandscape) {
            this.mSplitBannerConfig = 1;
        } else if (this.mTask.key.id == this.mStagedSplitBounds.leftTopTaskId) {
            if (this.mStagedSplitBounds.leftTaskPercent < 0.4f) {
                i = 2;
            }
            this.mSplitBannerConfig = i;
        } else {
            if (this.mStagedSplitBounds.leftTaskPercent > 0.6f) {
                i = 2;
            }
            this.mSplitBannerConfig = i;
        }
    }

    private String getReadableDuration(Duration duration, MeasureFormat.FormatWidth formatWidth, int i, boolean z) {
        int intExact = Math.toIntExact(duration.toHours());
        int intExact2 = Math.toIntExact(duration.minusHours((long) intExact).toMinutes());
        if (intExact > 0 && intExact2 > 0) {
            return MeasureFormat.getInstance(Locale.getDefault(), formatWidth).formatMeasures(new Measure[]{new Measure(Integer.valueOf(intExact), MeasureUnit.HOUR), new Measure(Integer.valueOf(intExact2), MeasureUnit.MINUTE)});
        } else if (intExact > 0) {
            Locale locale = Locale.getDefault();
            if (!z) {
                formatWidth = MeasureFormat.FormatWidth.WIDE;
            }
            return MeasureFormat.getInstance(locale, formatWidth).formatMeasures(new Measure[]{new Measure(Integer.valueOf(intExact), MeasureUnit.HOUR)});
        } else if (intExact2 > 0) {
            Locale locale2 = Locale.getDefault();
            if (!z) {
                formatWidth = MeasureFormat.FormatWidth.WIDE;
            }
            return MeasureFormat.getInstance(locale2, formatWidth).formatMeasures(new Measure[]{new Measure(Integer.valueOf(intExact2), MeasureUnit.MINUTE)});
        } else if (duration.compareTo(Duration.ZERO) > 0) {
            return this.mActivity.getString(i);
        } else {
            Locale locale3 = Locale.getDefault();
            if (!z) {
                formatWidth = MeasureFormat.FormatWidth.WIDE;
            }
            return MeasureFormat.getInstance(locale3, formatWidth).formatMeasures(new Measure[]{new Measure(0, MeasureUnit.MINUTE)});
        }
    }

    private String getText(long j, boolean z) {
        int i;
        if (j > 60000) {
            j = (((j + 60000) - 1) / 60000) * 60000;
        }
        String readableDuration = getReadableDuration(Duration.ofMillis(j), MeasureFormat.FormatWidth.NARROW, R.string.shorter_duration_less_than_one_minute, false);
        if (!z && (i = this.mSplitBannerConfig) != 0) {
            return i == 2 ? "" : readableDuration;
        }
        return this.mActivity.getString(R.string.time_left_for_app, new Object[]{readableDuration});
    }

    public void openAppUsageSettings(View view) {
        try {
            BaseActivity.fromContext(view.getContext()).startActivity(new Intent(OPEN_APP_USAGE_SETTINGS_TEMPLATE).putExtra("android.intent.extra.PACKAGE_NAME", this.mTask.getTopComponent().getPackageName()).addFlags(268468224), ActivityOptions.makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight()).toBundle());
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Failed to open app usage settings for task " + this.mTask.getTopComponent().getPackageName(), e);
        }
    }

    private String getContentDescriptionForTask(Task task, long j, long j2) {
        if (j < 0 || j2 < 0) {
            return task.titleDescription;
        }
        return this.mActivity.getString(R.string.task_contents_description_with_remaining_time, new Object[]{task.titleDescription, getText(j2, true)});
    }

    private void replaceBanner(View view) {
        resetOldBanner();
        setBanner(view);
    }

    private void resetOldBanner() {
        View view = this.mBanner;
        if (view != null) {
            view.setOutlineProvider(this.mOldBannerOutlineProvider);
            this.mTaskView.removeView(this.mBanner);
            this.mBanner.setOnClickListener((View.OnClickListener) null);
            this.mActivity.getViewCache().recycleView(R.layout.digital_wellbeing_toast, this.mBanner);
        }
    }

    private void setBanner(View view) {
        this.mBanner = view;
        if (view != null && this.mTaskView.getRecentsView() != null) {
            setupAndAddBanner();
            setBannerOutline();
        }
    }

    private void setupAndAddBanner() {
        DeviceProfile deviceProfile = this.mActivity.getDeviceProfile();
        ((FrameLayout.LayoutParams) this.mBanner.getLayoutParams()).bottomMargin = ((ViewGroup.MarginLayoutParams) this.mTaskView.getThumbnail().getLayoutParams()).bottomMargin;
        Pair<Float, Float> dwbLayoutTranslations = this.mTaskView.getPagedOrientationHandler().getDwbLayoutTranslations(this.mTaskView.getMeasuredWidth(), this.mTaskView.getMeasuredHeight(), this.mStagedSplitBounds, deviceProfile, this.mTaskView.getThumbnails(), this.mTask.key.id, this.mBanner);
        this.mSplitOffsetTranslationX = ((Float) dwbLayoutTranslations.first).floatValue();
        this.mSplitOffsetTranslationY = ((Float) dwbLayoutTranslations.second).floatValue();
        updateTranslationY();
        updateTranslationX();
        this.mTaskView.addView(this.mBanner);
    }

    private void setBannerOutline() {
        this.mOldBannerOutlineProvider = this.mBanner.getOutlineProvider();
        this.mBanner.setOutlineProvider(new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                DigitalWellBeingToast.this.mOldBannerOutlineProvider.getOutline(view, outline);
                outline.offset(0, Math.round((-view.getTranslationY()) + DigitalWellBeingToast.this.mModalOffset + DigitalWellBeingToast.this.mSplitOffsetTranslationY));
            }
        });
        this.mBanner.setClipToOutline(true);
    }

    /* access modifiers changed from: package-private */
    public void updateBannerOffset(float f, float f2) {
        if (this.mBanner != null && this.mBannerOffsetPercentage != f) {
            this.mModalOffset = f2;
            this.mBannerOffsetPercentage = f;
            updateTranslationY();
            this.mBanner.invalidateOutline();
        }
    }

    private void updateTranslationY() {
        View view = this.mBanner;
        if (view != null) {
            view.setTranslationY((this.mBannerOffsetPercentage * ((float) view.getHeight())) + this.mModalOffset + this.mSplitOffsetTranslationY);
        }
    }

    private void updateTranslationX() {
        View view = this.mBanner;
        if (view != null) {
            view.setTranslationX(this.mSplitOffsetTranslationX);
        }
    }

    /* access modifiers changed from: package-private */
    public void setBannerColorTint(int i, float f) {
        View view = this.mBanner;
        if (view != null) {
            if (f == 0.0f) {
                view.setLayerType(0, (Paint) null);
            }
            Paint paint = new Paint();
            paint.setColorFilter(Utilities.makeColorTintingColorFilter(i, f));
            this.mBanner.setLayerType(2, paint);
            this.mBanner.setLayerPaint(paint);
        }
    }
}
