package com.android.quickstep.interaction;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;
import com.airbnb.lottie.LottieAnimationView;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.util.Executors;
import com.android.quickstep.AnimatedFloat;
import com.android.quickstep.GestureState;
import com.android.quickstep.TouchInteractionService;
import com.android.quickstep.interaction.AllSetActivity;
import com.android.quickstep.util.TISBindHelper;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class AllSetActivity extends Activity {
    private static final String EXTRA_ACCENT_COLOR_DARK_MODE = "suwColorAccentDark";
    private static final String EXTRA_ACCENT_COLOR_LIGHT_MODE = "suwColorAccentLight";
    private static final float HINT_BOTTOM_FACTOR = 0.060000002f;
    private static final String LOG_TAG = "AllSetActivity";
    private static final int MAX_SWIPE_DURATION = 350;
    private static final String URI_SYSTEM_NAVIGATION_SETTING = "#Intent;action=com.android.settings.SEARCH_RESULT_TRAMPOLINE;S.:settings:fragment_args_key=gesture_system_navigation_input_summary;S.:settings:show_fragment=com.android.settings.gestures.SystemNavigationGestureSettings;end";
    private LottieAnimationView mAnimatedBackground;
    private BgDrawable mBackground;
    private Animator.AnimatorListener mBackgroundAnimatorListener;
    private TouchInteractionService.TISBinder mBinder;
    private View mContentView;
    private AnimatorPlaybackController mLauncherStartAnim = null;
    private final AnimatedFloat mSwipeProgress = new AnimatedFloat(new Runnable() {
        public final void run() {
            AllSetActivity.this.onSwipeProgressUpdate();
        }
    });
    private float mSwipeUpShift;
    private TISBindHelper mTISBindHelper;
    /* access modifiers changed from: private */
    public Vibrator mVibrator;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_allset);
        findViewById(R.id.root_view).setSystemUiVisibility(1792);
        boolean z = (getResources().getConfiguration().uiMode & 48) == 32;
        int intExtra = getIntent().getIntExtra(z ? EXTRA_ACCENT_COLOR_DARK_MODE : EXTRA_ACCENT_COLOR_LIGHT_MODE, z ? -1 : ViewCompat.MEASURED_STATE_MASK);
        ((ImageView) findViewById(R.id.icon)).getDrawable().mutate().setTint(intExtra);
        this.mBackground = new BgDrawable(this);
        findViewById(R.id.root_view).setBackground(this.mBackground);
        this.mContentView = findViewById(R.id.content_view);
        this.mSwipeUpShift = getResources().getDimension(R.dimen.allset_swipe_up_shift);
        ((TextView) findViewById(R.id.subtitle)).setText(InvariantDeviceProfile.INSTANCE.lambda$get$1$MainThreadInitializedObject(getApplicationContext()).getDeviceProfile(this).isTablet ? R.string.allset_description_tablet : R.string.allset_description);
        TextView textView = (TextView) findViewById(R.id.navigation_settings);
        textView.setTextColor(intExtra);
        textView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                AllSetActivity.this.lambda$onCreate$0$AllSetActivity(view);
            }
        });
        findViewById(R.id.hint).setAccessibilityDelegate(new SkipButtonAccessibilityDelegate());
        this.mTISBindHelper = new TISBindHelper(this, new Consumer() {
            public final void accept(Object obj) {
                AllSetActivity.this.onTISConnected((TouchInteractionService.TISBinder) obj);
            }
        });
        this.mVibrator = (Vibrator) getSystemService(Vibrator.class);
        this.mAnimatedBackground = (LottieAnimationView) findViewById(R.id.animated_background);
        startBackgroundAnimation();
    }

    public /* synthetic */ void lambda$onCreate$0$AllSetActivity(View view) {
        try {
            startActivityForResult(Intent.parseUri(URI_SYSTEM_NAVIGATION_SETTING, 0), 0);
        } catch (URISyntaxException e) {
            Log.e(LOG_TAG, "Failed to parse system nav settings intent", e);
        }
        finish();
    }

    /* access modifiers changed from: private */
    public void runOnUiHelperThread(Runnable runnable) {
        Executors.UI_HELPER_EXECUTOR.execute(runnable);
    }

    private void startBackgroundAnimation() {
        Vibrator vibrator;
        if (Utilities.ATLEAST_S && (vibrator = this.mVibrator) != null) {
            if (vibrator.areAllPrimitivesSupported(new int[]{2})) {
                if (this.mBackgroundAnimatorListener == null) {
                    this.mBackgroundAnimatorListener = new Animator.AnimatorListener() {
                        public /* synthetic */ void lambda$onAnimationStart$0$AllSetActivity$1() {
                            AllSetActivity.this.mVibrator.vibrate(AllSetActivity.this.getVibrationEffect());
                        }

                        public void onAnimationStart(Animator animator) {
                            AllSetActivity.this.runOnUiHelperThread(new Runnable() {
                                public final void run() {
                                    AllSetActivity.AnonymousClass1.this.lambda$onAnimationStart$0$AllSetActivity$1();
                                }
                            });
                        }

                        public /* synthetic */ void lambda$onAnimationRepeat$1$AllSetActivity$1() {
                            AllSetActivity.this.mVibrator.vibrate(AllSetActivity.this.getVibrationEffect());
                        }

                        public void onAnimationRepeat(Animator animator) {
                            AllSetActivity.this.runOnUiHelperThread(new Runnable() {
                                public final void run() {
                                    AllSetActivity.AnonymousClass1.this.lambda$onAnimationRepeat$1$AllSetActivity$1();
                                }
                            });
                        }

                        public void onAnimationEnd(Animator animator) {
                            AllSetActivity allSetActivity = AllSetActivity.this;
                            Vibrator access$200 = allSetActivity.mVibrator;
                            Objects.requireNonNull(access$200);
                            allSetActivity.runOnUiHelperThread(new Runnable(access$200) {
                                public final /* synthetic */ Vibrator f$0;

                                {
                                    this.f$0 = r1;
                                }

                                public final void run() {
                                    this.f$0.cancel();
                                }
                            });
                        }

                        public void onAnimationCancel(Animator animator) {
                            AllSetActivity allSetActivity = AllSetActivity.this;
                            Vibrator access$200 = allSetActivity.mVibrator;
                            Objects.requireNonNull(access$200);
                            allSetActivity.runOnUiHelperThread(new Runnable(access$200) {
                                public final /* synthetic */ Vibrator f$0;

                                {
                                    this.f$0 = r1;
                                }

                                public final void run() {
                                    this.f$0.cancel();
                                }
                            });
                        }
                    };
                }
                this.mAnimatedBackground.addAnimatorListener(this.mBackgroundAnimatorListener);
            }
        }
        this.mAnimatedBackground.playAnimation();
    }

    /* access modifiers changed from: private */
    public VibrationEffect getVibrationEffect() {
        return VibrationEffect.startComposition().addPrimitive(2, 1.0f, 50).compose();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        TouchInteractionService.TISBinder tISBinder = this.mBinder;
        if (tISBinder != null) {
            tISBinder.getTaskbarManager().setSetupUIVisible(true);
            this.mBinder.setSwipeUpProxy(new Function() {
                public final Object apply(Object obj) {
                    return AllSetActivity.this.createSwipeUpProxy((GestureState) obj);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void onTISConnected(TouchInteractionService.TISBinder tISBinder) {
        this.mBinder = tISBinder;
        tISBinder.getTaskbarManager().setSetupUIVisible(isResumed());
        this.mBinder.setSwipeUpProxy(isResumed() ? new Function() {
            public final Object apply(Object obj) {
                return AllSetActivity.this.createSwipeUpProxy((GestureState) obj);
            }
        } : null);
        TouchInteractionService.TISBinder tISBinder2 = this.mBinder;
        Objects.requireNonNull(tISBinder2);
        tISBinder2.setOverviewTargetChangeListener(new Runnable() {
            public final void run() {
                TouchInteractionService.TISBinder.this.preloadOverviewForSUWAllSet();
            }
        });
        this.mBinder.preloadOverviewForSUWAllSet();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        clearBinderOverride();
        if (this.mSwipeProgress.value >= 1.0f) {
            finishAndRemoveTask();
        }
    }

    private void clearBinderOverride() {
        TouchInteractionService.TISBinder tISBinder = this.mBinder;
        if (tISBinder != null) {
            tISBinder.getTaskbarManager().setSetupUIVisible(false);
            this.mBinder.setSwipeUpProxy((Function<GestureState, AnimatedFloat>) null);
            this.mBinder.setOverviewTargetChangeListener((Runnable) null);
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        this.mTISBindHelper.onDestroy();
        clearBinderOverride();
        Animator.AnimatorListener animatorListener = this.mBackgroundAnimatorListener;
        if (animatorListener != null) {
            this.mAnimatedBackground.removeAnimatorListener(animatorListener);
        }
    }

    /* access modifiers changed from: private */
    public AnimatedFloat createSwipeUpProxy(GestureState gestureState) {
        if (!gestureState.getHomeIntent().getComponent().getPackageName().equals(getPackageName()) || gestureState.getRunningTaskId() != getTaskId()) {
            return null;
        }
        this.mSwipeProgress.updateValue(0.0f);
        return this.mSwipeProgress;
    }

    /* access modifiers changed from: private */
    public void onSwipeProgressUpdate() {
        this.mBackground.setProgress(this.mSwipeProgress.value);
        float mapBoundToRange = Utilities.mapBoundToRange(this.mSwipeProgress.value, 0.0f, HINT_BOTTOM_FACTOR, 1.0f, 0.0f, Interpolators.LINEAR);
        this.mContentView.setAlpha(mapBoundToRange);
        this.mContentView.setTranslationY((mapBoundToRange - 1.0f) * this.mSwipeUpShift);
        if (this.mLauncherStartAnim == null) {
            this.mLauncherStartAnim = this.mBinder.getTaskbarManager().createLauncherStartFromSuwAnim(350);
        }
        AnimatorPlaybackController animatorPlaybackController = this.mLauncherStartAnim;
        if (animatorPlaybackController != null) {
            animatorPlaybackController.setPlayFraction(Utilities.mapBoundToRange(this.mSwipeProgress.value, 0.0f, 1.0f, 0.0f, 1.0f, Interpolators.FAST_OUT_SLOW_IN));
        }
        if (mapBoundToRange == 0.0f) {
            this.mAnimatedBackground.pauseAnimation();
        } else if (!this.mAnimatedBackground.isAnimating()) {
            this.mAnimatedBackground.resumeAnimation();
        }
    }

    private class SkipButtonAccessibilityDelegate extends View.AccessibilityDelegate {
        private SkipButtonAccessibilityDelegate() {
        }

        public AccessibilityNodeInfo createAccessibilityNodeInfo(View view) {
            AccessibilityNodeInfo createAccessibilityNodeInfo = super.createAccessibilityNodeInfo(view);
            createAccessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
            createAccessibilityNodeInfo.setClickable(true);
            return createAccessibilityNodeInfo;
        }

        public boolean performAccessibilityAction(View view, int i, Bundle bundle) {
            if (i != AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK.getId()) {
                return super.performAccessibilityAction(view, i, bundle);
            }
            AllSetActivity.this.startActivity(Utilities.createHomeIntent());
            AllSetActivity.this.finish();
            return true;
        }
    }

    private static class BgDrawable extends Drawable {
        private static final float END_SIZE_FACTOR = 2.0f;
        private static final float GRADIENT_END_PROGRESS = 0.5f;
        private static final float START_SIZE_FACTOR = 0.5f;
        private final int mColor;
        private final ColorMatrixColorFilter mColorFilter;
        private final ColorMatrix mColorMatrix;
        private final RadialGradient mMaskGrad;
        private final Matrix mMatrix = new Matrix();
        private final Paint mPaint;
        private float mProgress;

        public int getOpacity() {
            return -3;
        }

        public void setAlpha(int i) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        BgDrawable(Context context) {
            Paint paint = new Paint();
            this.mPaint = paint;
            ColorMatrix colorMatrix = new ColorMatrix();
            this.mColorMatrix = colorMatrix;
            ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(colorMatrix);
            this.mColorFilter = colorMatrixColorFilter;
            this.mProgress = 0.0f;
            int color = context.getColor(R.color.all_set_page_background);
            this.mColor = color;
            RadialGradient radialGradient = new RadialGradient(0.0f, 0.0f, 1.0f, new int[]{ColorUtils.setAlphaComponent(color, 0), color}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
            this.mMaskGrad = radialGradient;
            paint.setShader(radialGradient);
            paint.setColorFilter(colorMatrixColorFilter);
        }

        public void draw(Canvas canvas) {
            float f = this.mProgress;
            if (f <= 0.0f) {
                canvas.drawColor(this.mColor);
                return;
            }
            float mapBoundToRange = Utilities.mapBoundToRange(f, 0.0f, 0.5f, 0.0f, 1.0f, Interpolators.LINEAR);
            Rect bounds = getBounds();
            float exactCenterX = bounds.exactCenterX();
            float height = (float) bounds.height();
            float length = PointF.length(exactCenterX, height) * Utilities.mapRange(mapBoundToRange, 0.5f, 2.0f);
            float mapRange = Utilities.mapRange(mapBoundToRange, height + length, height / 2.0f);
            this.mMatrix.setTranslate(exactCenterX, mapRange);
            this.mMatrix.postScale(length, length, exactCenterX, mapRange);
            this.mMaskGrad.setLocalMatrix(this.mMatrix);
            this.mColorMatrix.getArray()[19] = Utilities.mapBoundToRange(this.mProgress, 0.0f, 1.0f, 0.0f, -255.0f, Interpolators.LINEAR);
            this.mColorFilter.setColorMatrix(this.mColorMatrix);
            canvas.drawPaint(this.mPaint);
        }

        public void setProgress(float f) {
            if (this.mProgress != f) {
                this.mProgress = f;
                invalidateSelf();
            }
        }
    }
}
