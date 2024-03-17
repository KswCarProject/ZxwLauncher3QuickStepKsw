package com.android.quickstep.interaction;

import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.logging.StatsLogManager;
import com.android.quickstep.TouchInteractionService;
import com.android.quickstep.interaction.TutorialController;
import com.android.quickstep.util.TISBindHelper;
import java.util.Arrays;
import java.util.function.Consumer;

public class GestureSandboxActivity extends FragmentActivity {
    private static final String KEY_CURRENT_STEP = "current_step";
    private static final String KEY_GESTURE_COMPLETE = "gesture_complete";
    private static final String KEY_TUTORIAL_STEPS = "tutorial_steps";
    private TouchInteractionService.TISBinder mBinder;
    private int mCurrentStep;
    private TutorialController.TutorialType mCurrentTutorialStep;
    private TutorialFragment mFragment;
    private int mNumSteps;
    private SharedPreferences mSharedPrefs;
    private StatsLogManager mStatsLogManager;
    private TISBindHelper mTISBindHelper;
    private TutorialController.TutorialType[] mTutorialSteps;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        setContentView((int) R.layout.gesture_tutorial_activity);
        this.mSharedPrefs = Utilities.getPrefs(this);
        this.mStatsLogManager = StatsLogManager.newInstance(getApplicationContext());
        if (bundle == null) {
            bundle = getIntent().getExtras();
        }
        TutorialController.TutorialType[] tutorialSteps = getTutorialSteps(bundle);
        this.mTutorialSteps = tutorialSteps;
        TutorialController.TutorialType tutorialType = tutorialSteps[this.mCurrentStep - 1];
        this.mCurrentTutorialStep = tutorialType;
        this.mFragment = TutorialFragment.newInstance(tutorialType, bundle.getBoolean(KEY_GESTURE_COMPLETE, false));
        getSupportFragmentManager().beginTransaction().add((int) R.id.gesture_tutorial_fragment_container, (Fragment) this.mFragment).commit();
        this.mTISBindHelper = new TISBindHelper(this, new Consumer() {
            public final void accept(Object obj) {
                GestureSandboxActivity.this.onTISConnected((TouchInteractionService.TISBinder) obj);
            }
        });
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        disableSystemGestures();
        this.mFragment.onAttachedToWindow();
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mFragment.onDetachedFromWindow();
    }

    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        if (z) {
            hideSystemUI();
        }
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putStringArray(KEY_TUTORIAL_STEPS, getTutorialStepNames());
        bundle.putInt(KEY_CURRENT_STEP, this.mCurrentStep);
        bundle.putBoolean(KEY_GESTURE_COMPLETE, this.mFragment.isGestureComplete());
        super.onSaveInstanceState(bundle);
    }

    /* access modifiers changed from: protected */
    public SharedPreferences getSharedPrefs() {
        return this.mSharedPrefs;
    }

    /* access modifiers changed from: protected */
    public StatsLogManager getStatsLogManager() {
        return this.mStatsLogManager;
    }

    public boolean isTutorialComplete() {
        return this.mCurrentStep >= this.mNumSteps;
    }

    public int getCurrentStep() {
        return this.mCurrentStep;
    }

    public int getNumSteps() {
        return this.mNumSteps;
    }

    public void continueTutorial() {
        if (isTutorialComplete()) {
            this.mFragment.closeTutorial();
            return;
        }
        TutorialController.TutorialType tutorialType = this.mTutorialSteps[this.mCurrentStep];
        this.mCurrentTutorialStep = tutorialType;
        this.mFragment = TutorialFragment.newInstance(tutorialType, false);
        getSupportFragmentManager().beginTransaction().replace(R.id.gesture_tutorial_fragment_container, this.mFragment).runOnCommit(new Runnable() {
            public final void run() {
                GestureSandboxActivity.this.lambda$continueTutorial$0$GestureSandboxActivity();
            }
        }).commit();
        this.mCurrentStep++;
    }

    public /* synthetic */ void lambda$continueTutorial$0$GestureSandboxActivity() {
        this.mFragment.onAttachedToWindow();
    }

    private String[] getTutorialStepNames() {
        TutorialController.TutorialType[] tutorialTypeArr = this.mTutorialSteps;
        String[] strArr = new String[tutorialTypeArr.length];
        int length = tutorialTypeArr.length;
        int i = 0;
        int i2 = 0;
        while (i < length) {
            strArr[i2] = tutorialTypeArr[i].name();
            i++;
            i2++;
        }
        return strArr;
    }

    private TutorialController.TutorialType[] getTutorialSteps(Bundle bundle) {
        String[] strArr;
        TutorialController.TutorialType[] tutorialTypeArr = {TutorialController.TutorialType.BACK_NAVIGATION};
        this.mCurrentStep = 1;
        this.mNumSteps = 1;
        if (bundle != null && bundle.containsKey(KEY_TUTORIAL_STEPS)) {
            Object obj = bundle.get(KEY_TUTORIAL_STEPS);
            int i = bundle.getInt(KEY_CURRENT_STEP, -1);
            if (obj instanceof String) {
                String str = (String) obj;
                if (TextUtils.isEmpty(str)) {
                    strArr = null;
                } else {
                    strArr = str.split(",");
                }
            } else if (obj instanceof String[]) {
                strArr = (String[]) obj;
            }
            if (strArr == null) {
                return tutorialTypeArr;
            }
            int length = strArr.length;
            TutorialController.TutorialType[] tutorialTypeArr2 = new TutorialController.TutorialType[length];
            for (int i2 = 0; i2 < strArr.length; i2++) {
                tutorialTypeArr2[i2] = TutorialController.TutorialType.valueOf(strArr[i2]);
            }
            this.mCurrentStep = Math.max(i, 1);
            this.mNumSteps = length;
            return tutorialTypeArr2;
        }
        return tutorialTypeArr;
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(5894);
        getWindow().setNavigationBarColor(0);
    }

    private void disableSystemGestures() {
        Display display = getDisplay();
        if (display != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            display.getMetrics(displayMetrics);
            getWindow().setSystemGestureExclusionRects(Arrays.asList(new Rect[]{new Rect(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)}));
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        updateServiceState(true);
    }

    /* access modifiers changed from: private */
    public void onTISConnected(TouchInteractionService.TISBinder tISBinder) {
        this.mBinder = tISBinder;
        updateServiceState(isResumed());
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        updateServiceState(false);
    }

    private void updateServiceState(boolean z) {
        TouchInteractionService.TISBinder tISBinder = this.mBinder;
        if (tISBinder != null) {
            tISBinder.setGestureBlockedTaskId(z ? getTaskId() : -1);
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        this.mTISBindHelper.onDestroy();
        updateServiceState(false);
    }
}
