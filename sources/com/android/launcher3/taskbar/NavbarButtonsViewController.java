package com.android.launcher3.taskbar;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.PaintDrawable;
import android.inputmethodservice.InputMethodService;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.core.view.GravityCompat;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AlphaUpdateListener;
import com.android.launcher3.qsb.QsbContainerView;
import com.android.launcher3.taskbar.TaskbarControllers;
import com.android.launcher3.util.MultiValueAlpha;
import com.android.launcher3.util.TouchController;
import com.android.launcher3.views.BaseDragLayer;
import com.android.quickstep.AnimatedFloat;
import com.android.systemui.shared.rotation.FloatingRotationButton;
import com.android.systemui.shared.rotation.RotationButton;
import com.android.systemui.shared.rotation.RotationButtonController;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.shared.system.ViewTreeObserverWrapper;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringJoiner;
import java.util.function.IntPredicate;
import java.util.function.Supplier;

public class NavbarButtonsViewController implements TaskbarControllers.LoggableTaskbarController {
    public static final int ALPHA_INDEX_IMMERSIVE_MODE = 0;
    public static final int ALPHA_INDEX_KEYGUARD_OR_DISABLE = 1;
    private static final int FLAG_A11Y_VISIBLE = 8;
    private static final int FLAG_DISABLE_BACK = 512;
    private static final int FLAG_DISABLE_HOME = 128;
    private static final int FLAG_DISABLE_RECENTS = 256;
    private static final int FLAG_IME_VISIBLE = 2;
    private static final int FLAG_KEYGUARD_OCCLUDED = 64;
    private static final int FLAG_KEYGUARD_VISIBLE = 32;
    private static final int FLAG_NOTIFICATION_SHADE_EXPANDED = 1024;
    private static final int FLAG_ONLY_BACK_FOR_BOUNCER_VISIBLE = 16;
    private static final int FLAG_ROTATION_BUTTON_VISIBLE = 4;
    private static final int FLAG_SCREEN_PINNING_ACTIVE = 2048;
    private static final int FLAG_SWITCHER_SUPPORTED = 1;
    private static final int MASK_IME_SWITCHER_VISIBLE = 3;
    private static final String NAV_BUTTONS_SEPARATE_WINDOW_TITLE = "Taskbar Nav Buttons";
    private static final int NUM_ALPHA_CHANNELS = 2;
    private View mA11yButton;
    private final ArrayList<ImageView> mAllButtons = new ArrayList<>();
    private boolean mAreNavButtonsInSeparateWindow = false;
    private View mBackButton;
    private MultiValueAlpha mBackButtonAlpha;
    private final TaskbarActivityContext mContext;
    private TaskbarControllers mControllers;
    private final int mDarkIconColor;
    private final ViewGroup mEndContextualContainer;
    /* access modifiers changed from: private */
    public FloatingRotationButton mFloatingRotationButton;
    /* access modifiers changed from: private */
    public final Rect mFloatingRotationButtonBounds = new Rect();
    private final RecentsHitboxExtender mHitboxExtender = new RecentsHitboxExtender();
    private View mHomeButton;
    private MultiValueAlpha mHomeButtonAlpha;
    private boolean mIsImeRenderingNavButtons;
    private final int mLightIconColor;
    private final ViewGroup mNavButtonContainer;
    private final AnimatedFloat mNavButtonDarkIntensityMultiplier = new AnimatedFloat(new Runnable() {
        public final void run() {
            NavbarButtonsViewController.this.updateNavButtonDarkIntensity();
        }
    });
    private final AnimatedFloat mNavButtonInAppDisplayProgressForSysui = new AnimatedFloat(new Runnable() {
        public final void run() {
            NavbarButtonsViewController.this.updateNavButtonInAppDisplayProgressForSysui();
        }
    });
    private final FrameLayout mNavButtonsView;
    private final ArrayList<StatePropertyHolder> mPropertyHolders = new ArrayList<>();
    private final RotationButtonListener mRotationButtonListener = new RotationButtonListener();
    /* access modifiers changed from: private */
    public final ViewTreeObserverWrapper.OnComputeInsetsListener mSeparateWindowInsetsComputer = new ViewTreeObserverWrapper.OnComputeInsetsListener() {
        public final void onComputeInsets(ViewTreeObserverWrapper.InsetsInfo insetsInfo) {
            NavbarButtonsViewController.this.onComputeInsetsForSeparateWindow(insetsInfo);
        }
    };
    /* access modifiers changed from: private */
    public BaseDragLayer<TaskbarActivityContext> mSeparateWindowParent;
    private final ViewGroup mStartContextualContainer;
    private int mState;
    private int mSysuiStateFlags;
    private final AnimatedFloat mTaskbarNavButtonDarkIntensity = new AnimatedFloat(new Runnable() {
        public final void run() {
            NavbarButtonsViewController.this.updateNavButtonDarkIntensity();
        }
    });
    private final AnimatedFloat mTaskbarNavButtonTranslationY = new AnimatedFloat(new Runnable() {
        public final void run() {
            NavbarButtonsViewController.this.updateNavButtonTranslationY();
        }
    });
    private final AnimatedFloat mTaskbarNavButtonTranslationYForIme = new AnimatedFloat(new Runnable() {
        public final void run() {
            NavbarButtonsViewController.this.updateNavButtonTranslationY();
        }
    });
    private final AnimatedFloat mTaskbarNavButtonTranslationYForInAppDisplay = new AnimatedFloat(new Runnable() {
        public final void run() {
            NavbarButtonsViewController.this.updateNavButtonTranslationY();
        }
    });
    private final Rect mTempRect = new Rect();

    static /* synthetic */ boolean lambda$init$0(int i) {
        return (i & 3) == 3 && (i & 4) == 0;
    }

    static /* synthetic */ boolean lambda$init$1(int i) {
        return (i & 32) == 0 && (i & 2048) == 0;
    }

    static /* synthetic */ boolean lambda$init$2(int i) {
        return (i & 32) == 0;
    }

    static /* synthetic */ boolean lambda$init$3(int i, int i2) {
        return (i & i2) != 0;
    }

    static /* synthetic */ boolean lambda$init$4(boolean z, int i) {
        return (i & 2) != 0 && !z;
    }

    static /* synthetic */ boolean lambda$init$5(int i) {
        return ((i & 1024) != 0 && (i & 32) == 0) || (i & 16) != 0;
    }

    static /* synthetic */ boolean lambda$init$6(int i) {
        return (i & 2) != 0;
    }

    static /* synthetic */ boolean lambda$initButtons$10(int i) {
        return (i & 32) == 0 && (i & 128) == 0;
    }

    static /* synthetic */ boolean lambda$initButtons$14(int i) {
        return (i & 8) != 0 && (i & 4) == 0;
    }

    static /* synthetic */ boolean lambda$initButtons$7(int i) {
        int i2 = i & 32;
        boolean z = (i2 != 0 && (i & 16) == 0 && (i & 64) == 0) ? false : true;
        if ((i & 512) == 0) {
            return i2 == 0 || z;
        }
        return false;
    }

    static /* synthetic */ boolean lambda$initButtons$9(int i) {
        return ((i & 16) == 0 && (i & 32) == 0) ? false : true;
    }

    static /* synthetic */ int access$572(NavbarButtonsViewController navbarButtonsViewController, int i) {
        int i2 = i & navbarButtonsViewController.mState;
        navbarButtonsViewController.mState = i2;
        return i2;
    }

    static /* synthetic */ int access$576(NavbarButtonsViewController navbarButtonsViewController, int i) {
        int i2 = i | navbarButtonsViewController.mState;
        navbarButtonsViewController.mState = i2;
        return i2;
    }

    public NavbarButtonsViewController(TaskbarActivityContext taskbarActivityContext, FrameLayout frameLayout) {
        this.mContext = taskbarActivityContext;
        this.mNavButtonsView = frameLayout;
        this.mNavButtonContainer = (ViewGroup) frameLayout.findViewById(R.id.end_nav_buttons);
        this.mEndContextualContainer = (ViewGroup) frameLayout.findViewById(R.id.end_contextual_buttons);
        this.mStartContextualContainer = (ViewGroup) frameLayout.findViewById(R.id.start_contextual_buttons);
        this.mLightIconColor = taskbarActivityContext.getColor(R.color.taskbar_nav_icon_light_color);
        this.mDarkIconColor = taskbarActivityContext.getColor(R.color.taskbar_nav_icon_dark_color);
    }

    public void init(TaskbarControllers taskbarControllers) {
        this.mControllers = taskbarControllers;
        this.mNavButtonsView.getLayoutParams().height = this.mContext.getDeviceProfile().taskbarSize;
        boolean isThreeButtonNav = this.mContext.isThreeButtonNav();
        boolean z = true;
        boolean z2 = InputMethodService.canImeRenderGesturalNavButtons() && this.mContext.imeDrawsImeNavBar();
        this.mIsImeRenderingNavButtons = z2;
        if (!z2) {
            this.mPropertyHolders.add(new StatePropertyHolder((View) addButton(R.drawable.ic_ime_switcher, 8, isThreeButtonNav ? this.mStartContextualContainer : this.mEndContextualContainer, this.mControllers.navButtonController, R.id.ime_switcher), (IntPredicate) $$Lambda$NavbarButtonsViewController$K8zRjIrjvTajPm1fc9k0ez1o74s.INSTANCE));
        }
        this.mPropertyHolders.add(new StatePropertyHolder(this.mControllers.taskbarViewController.getTaskbarIconAlpha().getProperty(1), (IntPredicate) $$Lambda$NavbarButtonsViewController$LAa41El1vZiDhnelh_c2okGj64.INSTANCE));
        this.mPropertyHolders.add(new StatePropertyHolder(this.mControllers.taskbarDragLayerController.getKeyguardBgTaskbar(), (IntPredicate) $$Lambda$NavbarButtonsViewController$l456hOAxGpoB5FIt47UrW6SbEUA.INSTANCE));
        boolean z3 = !this.mContext.isUserSetupComplete();
        boolean isNavBarKidsModeActive = this.mContext.isNavBarKidsModeActive();
        boolean z4 = isThreeButtonNav || z3;
        this.mPropertyHolders.add(new StatePropertyHolder(this.mNavButtonInAppDisplayProgressForSysui, new IntPredicate(QsbContainerView.QsbFragment.QSB_WIDGET_HOST_ID) {
            public final /* synthetic */ int f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(int i) {
                return NavbarButtonsViewController.lambda$init$3(this.f$0, i);
            }
        }, AnimatedFloat.VALUE, 1.0f, 0.0f));
        float taskbarHeightForIme = ((float) (this.mContext.getDeviceProfile().taskbarSize - this.mControllers.taskbarInsetsController.getTaskbarHeightForIme())) / 2.0f;
        float f = 0.0f;
        this.mPropertyHolders.add(new StatePropertyHolder(this.mTaskbarNavButtonTranslationYForIme, new IntPredicate(isNavBarKidsModeActive) {
            public final /* synthetic */ boolean f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(int i) {
                return NavbarButtonsViewController.lambda$init$4(this.f$0, i);
            }
        }, AnimatedFloat.VALUE, taskbarHeightForIme, z4 ? 0.0f : taskbarHeightForIme));
        if (z4) {
            initButtons(this.mNavButtonContainer, this.mEndContextualContainer, this.mControllers.navButtonController);
            if (z3) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mNavButtonContainer.getLayoutParams();
                layoutParams.setMarginStart(layoutParams.getMarginEnd());
                layoutParams.setMarginEnd(0);
                layoutParams.gravity = GravityCompat.START;
                this.mNavButtonContainer.requestLayout();
                if ((this.mContext.getResources().getConfiguration().uiMode & 48) != 32) {
                    z = false;
                }
                AnimatedFloat animatedFloat = this.mTaskbarNavButtonDarkIntensity;
                if (!z) {
                    f = 1.0f;
                }
                animatedFloat.updateValue(f);
            } else if (isNavBarKidsModeActive) {
                int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(R.dimen.taskbar_icon_size_kids);
                int dimensionPixelSize2 = this.mContext.getResources().getDimensionPixelSize(R.dimen.taskbar_nav_buttons_width_kids);
                int dimensionPixelSize3 = this.mContext.getResources().getDimensionPixelSize(R.dimen.taskbar_nav_buttons_height_kids);
                int dimensionPixelSize4 = this.mContext.getResources().getDimensionPixelSize(R.dimen.taskbar_nav_buttons_corner_radius_kids);
                int i = (dimensionPixelSize2 - dimensionPixelSize) / 2;
                int i2 = (dimensionPixelSize3 - dimensionPixelSize) / 2;
                View view = this.mBackButton;
                ((ImageView) view).setImageDrawable(view.getContext().getDrawable(R.drawable.ic_sysbar_back_kids));
                ((ImageView) this.mBackButton).setScaleType(ImageView.ScaleType.FIT_CENTER);
                this.mBackButton.setPadding(i, i2, i, i2);
                View view2 = this.mHomeButton;
                ((ImageView) view2).setImageDrawable(view2.getContext().getDrawable(R.drawable.ic_sysbar_home_kids));
                ((ImageView) this.mHomeButton).setScaleType(ImageView.ScaleType.FIT_CENTER);
                this.mHomeButton.setPadding(i, i2, i, i2);
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(dimensionPixelSize2, dimensionPixelSize3);
                layoutParams2.setMargins(this.mContext.getResources().getDimensionPixelSize(R.dimen.taskbar_home_button_left_margin_kids), 0, 0, 0);
                this.mHomeButton.setLayoutParams(layoutParams2);
                LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(dimensionPixelSize2, dimensionPixelSize3);
                layoutParams3.setMargins(this.mContext.getResources().getDimensionPixelSize(R.dimen.taskbar_back_button_left_margin_kids), 0, 0, 0);
                this.mBackButton.setLayoutParams(layoutParams3);
                PaintDrawable paintDrawable = new PaintDrawable(Color.argb(0.1f, 1.0f, 1.0f, 1.0f));
                paintDrawable.setCornerRadius((float) dimensionPixelSize4);
                this.mHomeButton.setBackground(paintDrawable);
                this.mBackButton.setBackground(paintDrawable);
                FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) this.mNavButtonContainer.getLayoutParams();
                layoutParams4.setMarginStart(layoutParams4.getMarginEnd() / 2);
                layoutParams4.setMarginEnd(layoutParams4.getMarginStart());
                layoutParams4.gravity = 17;
                this.mNavButtonContainer.requestLayout();
                this.mHomeButton.setOnLongClickListener((View.OnLongClickListener) null);
            }
            this.mPropertyHolders.add(new StatePropertyHolder(this.mControllers.taskbarDragLayerController.getNavbarBackgroundAlpha(), (IntPredicate) $$Lambda$NavbarButtonsViewController$7zpAXx1eWNXXt72LvbkbvzUun0.INSTANCE));
            RotationButtonImpl rotationButtonImpl = new RotationButtonImpl(addButton(this.mEndContextualContainer, R.id.rotate_suggestion, R.layout.taskbar_contextual_button));
            rotationButtonImpl.hide();
            this.mControllers.rotationButtonController.setRotationButton(rotationButtonImpl, (RotationButton.RotationButtonUpdatesCallback) null);
        } else {
            this.mFloatingRotationButton = new FloatingRotationButton(this.mContext, R.string.accessibility_rotate_button, R.layout.rotate_suggestion, R.id.rotate_suggestion, R.dimen.floating_rotation_button_min_margin, R.dimen.rounded_corner_content_padding, R.dimen.floating_rotation_button_taskbar_left_margin, R.dimen.floating_rotation_button_taskbar_bottom_margin, R.dimen.floating_rotation_button_diameter, R.dimen.key_button_ripple_max_width);
            this.mControllers.rotationButtonController.setRotationButton(this.mFloatingRotationButton, this.mRotationButtonListener);
            if (!this.mIsImeRenderingNavButtons) {
                ImageView addButton = addButton(R.drawable.ic_sysbar_back, 1, this.mStartContextualContainer, this.mControllers.navButtonController, R.id.back);
                addButton.setRotation(Utilities.isRtl(this.mContext.getResources()) ? 90.0f : -90.0f);
                this.mPropertyHolders.add(new StatePropertyHolder((View) addButton, (IntPredicate) $$Lambda$NavbarButtonsViewController$l0Cu2mrjYquuXoXogzBRCvNYHNI.INSTANCE));
            }
        }
        applyState();
        this.mPropertyHolders.forEach($$Lambda$k71Fq_EeM8U_0S3CDOlmzv5fc.INSTANCE);
        AnonymousClass1 r0 = new BaseDragLayer<TaskbarActivityContext>(this.mContext, (AttributeSet) null, 0) {
            /* access modifiers changed from: protected */
            public boolean canFindActiveController() {
                return false;
            }

            public void recreateControllers() {
                this.mControllers = new TouchController[0];
            }
        };
        this.mSeparateWindowParent = r0;
        r0.recreateControllers();
    }

    private void initButtons(ViewGroup viewGroup, ViewGroup viewGroup2, TaskbarNavButtonController taskbarNavButtonController) {
        this.mBackButton = addButton(R.drawable.ic_sysbar_back, 1, this.mNavButtonContainer, this.mControllers.navButtonController, R.id.back);
        MultiValueAlpha multiValueAlpha = new MultiValueAlpha(this.mBackButton, 2);
        this.mBackButtonAlpha = multiValueAlpha;
        multiValueAlpha.setUpdateVisibility(true);
        this.mPropertyHolders.add(new StatePropertyHolder(this.mBackButtonAlpha.getProperty(1), (IntPredicate) $$Lambda$NavbarButtonsViewController$NmD6VeLOMHJRdAJ5LIrANyHFqPo.INSTANCE));
        boolean isRtl = Utilities.isRtl(this.mContext.getResources());
        this.mPropertyHolders.add(new StatePropertyHolder(this.mBackButton, new IntPredicate() {
            public final boolean test(int i) {
                return NavbarButtonsViewController.this.lambda$initButtons$8$NavbarButtonsViewController(i);
            }
        }, View.ROTATION, isRtl ? 90.0f : -90.0f, 0.0f));
        this.mPropertyHolders.add(new StatePropertyHolder(this.mBackButton, $$Lambda$NavbarButtonsViewController$VilFZig03UFKYOT88iilXlLOw8.INSTANCE, LauncherAnimUtils.VIEW_TRANSLATE_X, (float) (this.mContext.getResources().getDimensionPixelSize(R.dimen.taskbar_nav_buttons_size) * (isRtl ? -2 : 2)), 0.0f));
        TaskbarNavButtonController taskbarNavButtonController2 = taskbarNavButtonController;
        this.mHomeButton = addButton(R.drawable.ic_sysbar_home, 2, viewGroup, taskbarNavButtonController2, R.id.home);
        MultiValueAlpha multiValueAlpha2 = new MultiValueAlpha(this.mHomeButton, 2);
        this.mHomeButtonAlpha = multiValueAlpha2;
        multiValueAlpha2.setUpdateVisibility(true);
        this.mPropertyHolders.add(new StatePropertyHolder(this.mHomeButtonAlpha.getProperty(1), (IntPredicate) $$Lambda$NavbarButtonsViewController$JYTVdeW50f0VwbiT9zes2LAbGQ.INSTANCE));
        ImageView addButton = addButton(R.drawable.ic_sysbar_recent, 4, viewGroup, taskbarNavButtonController2, R.id.recent_apps);
        this.mHitboxExtender.init(addButton, this.mNavButtonsView, this.mContext.getDeviceProfile(), new Supplier(addButton) {
            public final /* synthetic */ View f$1;

            {
                this.f$1 = r2;
            }

            public final Object get() {
                return NavbarButtonsViewController.this.lambda$initButtons$11$NavbarButtonsViewController(this.f$1);
            }
        }, new Handler());
        addButton.setOnClickListener(new View.OnClickListener(taskbarNavButtonController2) {
            public final /* synthetic */ TaskbarNavButtonController f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                NavbarButtonsViewController.this.lambda$initButtons$12$NavbarButtonsViewController(this.f$1, view);
            }
        });
        this.mPropertyHolders.add(new StatePropertyHolder((View) addButton, (IntPredicate) new IntPredicate() {
            public final boolean test(int i) {
                return NavbarButtonsViewController.this.lambda$initButtons$13$NavbarButtonsViewController(i);
            }
        }));
        this.mA11yButton = addButton(R.drawable.ic_sysbar_accessibility_button, 16, viewGroup2, taskbarNavButtonController2, R.id.accessibility_button, R.layout.taskbar_contextual_button);
        this.mPropertyHolders.add(new StatePropertyHolder(this.mA11yButton, (IntPredicate) $$Lambda$NavbarButtonsViewController$3geTClCVagpfYEQbKbrddEGu6o0.INSTANCE));
    }

    public /* synthetic */ boolean lambda$initButtons$8$NavbarButtonsViewController(int i) {
        return (i & 2) != 0 && !this.mContext.isNavBarKidsModeActive();
    }

    public /* synthetic */ float[] lambda$initButtons$11$NavbarButtonsViewController(View view) {
        float[] fArr = new float[2];
        Utilities.getDescendantCoordRelativeToAncestor(view, this.mNavButtonsView, fArr, false);
        return fArr;
    }

    public /* synthetic */ void lambda$initButtons$12$NavbarButtonsViewController(TaskbarNavButtonController taskbarNavButtonController, View view) {
        taskbarNavButtonController.onButtonClick(4);
        this.mHitboxExtender.onRecentsButtonClicked();
    }

    public /* synthetic */ boolean lambda$initButtons$13$NavbarButtonsViewController(int i) {
        return (i & 32) == 0 && (i & 256) == 0 && !this.mContext.isNavBarKidsModeActive();
    }

    private void parseSystemUiFlags(int i) {
        this.mSysuiStateFlags = i;
        boolean z = false;
        boolean z2 = (262144 & i) != 0;
        boolean z3 = (1048576 & i) != 0;
        boolean z4 = (i & 16) != 0;
        boolean z5 = (i & 256) != 0;
        boolean z6 = (i & 128) != 0;
        boolean z7 = (4194304 & i) != 0;
        boolean z8 = (i & 2052) != 0;
        boolean z9 = (i & 1) != 0;
        updateStateForFlag(2, z2);
        updateStateForFlag(1, z3);
        updateStateForFlag(8, z4);
        updateStateForFlag(128, z5);
        updateStateForFlag(256, z6);
        updateStateForFlag(512, z7);
        updateStateForFlag(1024, z8);
        updateStateForFlag(2048, z9);
        View view = this.mA11yButton;
        if (view != null) {
            if ((i & 32) != 0) {
                z = true;
            }
            view.setLongClickable(z);
        }
    }

    public void updateStateForSysuiFlags(int i, boolean z) {
        if (i != this.mSysuiStateFlags) {
            parseSystemUiFlags(i);
            applyState();
            if (z) {
                this.mPropertyHolders.forEach($$Lambda$k71Fq_EeM8U_0S3CDOlmzv5fc.INSTANCE);
            }
        }
    }

    public void setBackForBouncer(boolean z) {
        updateStateForFlag(16, z);
        applyState();
    }

    public void setKeyguardVisible(boolean z, boolean z2) {
        updateStateForFlag(32, z || z2);
        updateStateForFlag(64, z2);
        applyState();
    }

    public boolean isImeVisible() {
        return (this.mState & 2) != 0;
    }

    public boolean isHomeDisabled() {
        return (this.mState & 128) != 0;
    }

    public boolean isRecentsDisabled() {
        return (this.mState & 256) != 0;
    }

    public void addVisibleButtonsRegion(BaseDragLayer<?> baseDragLayer, Region region) {
        int size = this.mAllButtons.size();
        for (int i = 0; i < size; i++) {
            View view = this.mAllButtons.get(i);
            if (view.getVisibility() == 0) {
                baseDragLayer.getDescendantRectRelativeToSelf(view, this.mTempRect);
                if (this.mHitboxExtender.extendedHitboxEnabled()) {
                    this.mTempRect.bottom += this.mContext.getDeviceProfile().getTaskbarOffsetY();
                }
                region.op(this.mTempRect, Region.Op.UNION);
            }
        }
    }

    public MultiValueAlpha getBackButtonAlpha() {
        return this.mBackButtonAlpha;
    }

    public MultiValueAlpha getHomeButtonAlpha() {
        return this.mHomeButtonAlpha;
    }

    public AnimatedFloat getTaskbarNavButtonTranslationY() {
        return this.mTaskbarNavButtonTranslationY;
    }

    public AnimatedFloat getTaskbarNavButtonTranslationYForInAppDisplay() {
        return this.mTaskbarNavButtonTranslationYForInAppDisplay;
    }

    public AnimatedFloat getTaskbarNavButtonDarkIntensity() {
        return this.mTaskbarNavButtonDarkIntensity;
    }

    public AnimatedFloat getNavButtonDarkIntensityMultiplier() {
        return this.mNavButtonDarkIntensityMultiplier;
    }

    private void updateStateForFlag(int i, boolean z) {
        if (z) {
            this.mState = i | this.mState;
            return;
        }
        this.mState = (~i) & this.mState;
    }

    /* access modifiers changed from: private */
    public void applyState() {
        int size = this.mPropertyHolders.size();
        for (int i = 0; i < size; i++) {
            this.mPropertyHolders.get(i).setState(this.mState);
        }
    }

    /* access modifiers changed from: private */
    public void updateNavButtonInAppDisplayProgressForSysui() {
        TaskbarUIController taskbarUIController = this.mControllers.uiController;
        if (taskbarUIController instanceof LauncherTaskbarUIController) {
            ((LauncherTaskbarUIController) taskbarUIController).onTaskbarInAppDisplayProgressUpdate(this.mNavButtonInAppDisplayProgressForSysui.value, 3);
        }
    }

    /* access modifiers changed from: private */
    public void updateNavButtonTranslationY() {
        float f = this.mTaskbarNavButtonTranslationY.value;
        float f2 = this.mTaskbarNavButtonTranslationYForIme.value;
        TaskbarUIController taskbarUIController = this.mControllers.uiController;
        this.mNavButtonsView.setTranslationY(f + f2 + ((!(taskbarUIController instanceof LauncherTaskbarUIController) || !((LauncherTaskbarUIController) taskbarUIController).shouldUseInAppLayout()) ? 0.0f : this.mTaskbarNavButtonTranslationYForInAppDisplay.value));
    }

    /* access modifiers changed from: private */
    public void updateNavButtonDarkIntensity() {
        int intValue = ((Integer) ArgbEvaluator.getInstance().evaluate(this.mTaskbarNavButtonDarkIntensity.value * this.mNavButtonDarkIntensityMultiplier.value, Integer.valueOf(this.mLightIconColor), Integer.valueOf(this.mDarkIconColor))).intValue();
        Iterator<ImageView> it = this.mAllButtons.iterator();
        while (it.hasNext()) {
            it.next().setImageTintList(ColorStateList.valueOf(intValue));
        }
    }

    /* access modifiers changed from: protected */
    public ImageView addButton(int i, int i2, ViewGroup viewGroup, TaskbarNavButtonController taskbarNavButtonController, int i3) {
        return addButton(i, i2, viewGroup, taskbarNavButtonController, i3, R.layout.taskbar_nav_button);
    }

    private ImageView addButton(int i, int i2, ViewGroup viewGroup, TaskbarNavButtonController taskbarNavButtonController, int i3, int i4) {
        ImageView addButton = addButton(viewGroup, i3, i4);
        addButton.setImageResource(i);
        addButton.setContentDescription(viewGroup.getContext().getString(taskbarNavButtonController.getButtonContentDescription(i2)));
        addButton.setOnClickListener(new View.OnClickListener(i2) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                TaskbarNavButtonController.this.onButtonClick(this.f$1);
            }
        });
        addButton.setOnLongClickListener(new View.OnLongClickListener(i2) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final boolean onLongClick(View view) {
                return TaskbarNavButtonController.this.onButtonLongClick(this.f$1);
            }
        });
        return addButton;
    }

    private ImageView addButton(ViewGroup viewGroup, int i, int i2) {
        ImageView imageView = (ImageView) this.mContext.getLayoutInflater().inflate(i2, viewGroup, false);
        imageView.setId(i);
        viewGroup.addView(imageView);
        this.mAllButtons.add(imageView);
        return imageView;
    }

    public boolean isEventOverAnyItem(MotionEvent motionEvent) {
        return this.mFloatingRotationButtonBounds.contains((int) motionEvent.getX(), (int) motionEvent.getY());
    }

    public void onConfigurationChanged(int i) {
        FloatingRotationButton floatingRotationButton = this.mFloatingRotationButton;
        if (floatingRotationButton != null) {
            floatingRotationButton.onConfigurationChanged(i);
        }
    }

    public void onDestroy() {
        this.mPropertyHolders.clear();
        this.mControllers.rotationButtonController.unregisterListeners();
        FloatingRotationButton floatingRotationButton = this.mFloatingRotationButton;
        if (floatingRotationButton != null) {
            floatingRotationButton.hide();
        }
        moveNavButtonsBackToTaskbarWindow();
    }

    public void moveNavButtonsToNewWindow() {
        if (!this.mAreNavButtonsInSeparateWindow && !this.mIsImeRenderingNavButtons) {
            this.mSeparateWindowParent.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                public void onViewAttachedToWindow(View view) {
                    ViewTreeObserverWrapper.addOnComputeInsetsListener(NavbarButtonsViewController.this.mSeparateWindowParent.getViewTreeObserver(), NavbarButtonsViewController.this.mSeparateWindowInsetsComputer);
                }

                public void onViewDetachedFromWindow(View view) {
                    NavbarButtonsViewController.this.mSeparateWindowParent.removeOnAttachStateChangeListener(this);
                    ViewTreeObserverWrapper.removeOnComputeInsetsListener(NavbarButtonsViewController.this.mSeparateWindowInsetsComputer);
                }
            });
            this.mAreNavButtonsInSeparateWindow = true;
            this.mContext.getDragLayer().removeView(this.mNavButtonsView);
            this.mSeparateWindowParent.addView(this.mNavButtonsView);
            WindowManager.LayoutParams createDefaultWindowLayoutParams = this.mContext.createDefaultWindowLayoutParams();
            createDefaultWindowLayoutParams.setTitle(NAV_BUTTONS_SEPARATE_WINDOW_TITLE);
            this.mContext.addWindowView(this.mSeparateWindowParent, createDefaultWindowLayoutParams);
        }
    }

    public void moveNavButtonsBackToTaskbarWindow() {
        if (this.mAreNavButtonsInSeparateWindow) {
            this.mAreNavButtonsInSeparateWindow = false;
            this.mContext.removeWindowView(this.mSeparateWindowParent);
            this.mSeparateWindowParent.removeView(this.mNavButtonsView);
            this.mContext.getDragLayer().addView(this.mNavButtonsView);
        }
    }

    /* access modifiers changed from: private */
    public void onComputeInsetsForSeparateWindow(ViewTreeObserverWrapper.InsetsInfo insetsInfo) {
        addVisibleButtonsRegion(this.mSeparateWindowParent, insetsInfo.touchableRegion);
        insetsInfo.setTouchableInsets(3);
    }

    public void dumpLogs(String str, PrintWriter printWriter) {
        printWriter.println(str + "NavbarButtonsViewController:");
        printWriter.println(String.format("%s\tmState=%s", new Object[]{str, getStateString(this.mState)}));
        printWriter.println(String.format("%s\tmLightIconColor=0x%s", new Object[]{str, Integer.toHexString(this.mLightIconColor)}));
        printWriter.println(String.format("%s\tmDarkIconColor=0x%s", new Object[]{str, Integer.toHexString(this.mDarkIconColor)}));
        printWriter.println(String.format("%s\tmFloatingRotationButtonBounds=%s", new Object[]{str, this.mFloatingRotationButtonBounds}));
        printWriter.println(String.format("%s\tmSysuiStateFlags=%s", new Object[]{str, QuickStepContract.getSystemUiStateString(this.mSysuiStateFlags)}));
    }

    private static String getStateString(int i) {
        StringJoiner stringJoiner = new StringJoiner("|");
        Utilities.appendFlag(stringJoiner, i, 1, "FLAG_SWITCHER_SUPPORTED");
        Utilities.appendFlag(stringJoiner, i, 2, "FLAG_IME_VISIBLE");
        Utilities.appendFlag(stringJoiner, i, 4, "FLAG_ROTATION_BUTTON_VISIBLE");
        Utilities.appendFlag(stringJoiner, i, 8, "FLAG_A11Y_VISIBLE");
        Utilities.appendFlag(stringJoiner, i, 16, "FLAG_ONLY_BACK_FOR_BOUNCER_VISIBLE");
        Utilities.appendFlag(stringJoiner, i, 32, "FLAG_KEYGUARD_VISIBLE");
        Utilities.appendFlag(stringJoiner, i, 64, "FLAG_KEYGUARD_OCCLUDED");
        Utilities.appendFlag(stringJoiner, i, 128, "FLAG_DISABLE_HOME");
        Utilities.appendFlag(stringJoiner, i, 256, "FLAG_DISABLE_RECENTS");
        Utilities.appendFlag(stringJoiner, i, 512, "FLAG_DISABLE_BACK");
        Utilities.appendFlag(stringJoiner, i, 1024, "FLAG_NOTIFICATION_SHADE_EXPANDED");
        Utilities.appendFlag(stringJoiner, i, 2048, "FLAG_SCREEN_PINNING_ACTIVE");
        return stringJoiner.toString();
    }

    public TouchController getTouchController() {
        return this.mHitboxExtender;
    }

    public void updateTaskbarAlignment(float f) {
        this.mHitboxExtender.onAnimationProgressToOverview(f);
    }

    private class RotationButtonListener implements RotationButton.RotationButtonUpdatesCallback {
        private RotationButtonListener() {
        }

        public void onVisibilityChanged(boolean z) {
            if (z) {
                NavbarButtonsViewController.this.mFloatingRotationButton.getCurrentView().getBoundsOnScreen(NavbarButtonsViewController.this.mFloatingRotationButtonBounds);
            } else {
                NavbarButtonsViewController.this.mFloatingRotationButtonBounds.setEmpty();
            }
        }
    }

    private class RotationButtonImpl implements RotationButton {
        private final ImageView mButton;
        private AnimatedVectorDrawable mImageDrawable;

        public void setDarkIntensity(float f) {
        }

        public void updateIcon(int i, int i2) {
        }

        RotationButtonImpl(ImageView imageView) {
            this.mButton = imageView;
        }

        public void setRotationButtonController(RotationButtonController rotationButtonController) {
            AnimatedVectorDrawable animatedVectorDrawable = (AnimatedVectorDrawable) this.mButton.getContext().getDrawable(rotationButtonController.getIconResId());
            this.mImageDrawable = animatedVectorDrawable;
            this.mButton.setImageDrawable(animatedVectorDrawable);
            ImageView imageView = this.mButton;
            imageView.setContentDescription(imageView.getResources().getString(R.string.accessibility_rotate_button));
            this.mImageDrawable.setCallback(this.mButton);
        }

        public View getCurrentView() {
            return this.mButton;
        }

        public boolean show() {
            this.mButton.setVisibility(0);
            NavbarButtonsViewController.access$576(NavbarButtonsViewController.this, 4);
            NavbarButtonsViewController.this.applyState();
            return true;
        }

        public boolean hide() {
            this.mButton.setVisibility(8);
            NavbarButtonsViewController.access$572(NavbarButtonsViewController.this, -5);
            NavbarButtonsViewController.this.applyState();
            return true;
        }

        public boolean isVisible() {
            return this.mButton.getVisibility() == 0;
        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            this.mButton.setOnClickListener(onClickListener);
        }

        public void setOnHoverListener(View.OnHoverListener onHoverListener) {
            this.mButton.setOnHoverListener(onHoverListener);
        }

        public AnimatedVectorDrawable getImageDrawable() {
            return this.mImageDrawable;
        }

        public boolean acceptRotationProposal() {
            return this.mButton.isAttachedToWindow();
        }
    }

    private static class StatePropertyHolder {
        private final ObjectAnimator mAnimator;
        private final float mDisabledValue;
        private final IntPredicate mEnableCondition;
        private final float mEnabledValue;
        private boolean mIsEnabled;

        StatePropertyHolder(View view, IntPredicate intPredicate) {
            this(view, intPredicate, LauncherAnimUtils.VIEW_ALPHA, 1.0f, 0.0f);
            this.mAnimator.addListener(new AlphaUpdateListener(view));
        }

        StatePropertyHolder(MultiValueAlpha.AlphaProperty alphaProperty, IntPredicate intPredicate) {
            this(alphaProperty, intPredicate, MultiValueAlpha.VALUE, 1.0f, 0.0f);
        }

        StatePropertyHolder(AnimatedFloat animatedFloat, IntPredicate intPredicate) {
            this(animatedFloat, intPredicate, AnimatedFloat.VALUE, 1.0f, 0.0f);
        }

        <T> StatePropertyHolder(T t, IntPredicate intPredicate, Property<T, Float> property, float f, float f2) {
            this.mIsEnabled = true;
            this.mEnableCondition = intPredicate;
            this.mEnabledValue = f;
            this.mDisabledValue = f2;
            this.mAnimator = ObjectAnimator.ofFloat(t, property, new float[]{f, f2});
        }

        public void setState(int i) {
            boolean test = this.mEnableCondition.test(i);
            if (this.mIsEnabled != test) {
                this.mIsEnabled = test;
                this.mAnimator.cancel();
                ObjectAnimator objectAnimator = this.mAnimator;
                float[] fArr = new float[1];
                fArr[0] = this.mIsEnabled ? this.mEnabledValue : this.mDisabledValue;
                objectAnimator.setFloatValues(fArr);
                this.mAnimator.start();
            }
        }

        public void endAnimation() {
            if (this.mAnimator.isRunning()) {
                this.mAnimator.end();
            }
        }
    }
}
