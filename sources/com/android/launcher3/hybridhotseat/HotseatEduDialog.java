package com.android.launcher3.hybridhotseat;

import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Insettable;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.uioverrides.ApiWrapper;
import com.android.launcher3.uioverrides.PredictedAppIcon;
import com.android.launcher3.views.AbstractSlideInView;
import com.android.launcher3.views.ActivityContext;
import java.util.List;

public class HotseatEduDialog extends AbstractSlideInView<Launcher> implements Insettable {
    private static final int DEFAULT_CLOSE_DURATION = 200;
    protected static final int FINAL_SCRIM_BG_COLOR = -2013265920;
    private static final int MIGRATION_EXPERIMENT_IDENTIFIER = 1;
    private Button mDismissBtn;
    private HotseatEduController mHotseatEduController;
    private View mHotseatWrapper;
    private final Rect mInsets;
    private CellLayout mSampleHotseat;

    /* access modifiers changed from: protected */
    public int getScrimColor(Context context) {
        return FINAL_SCRIM_BG_COLOR;
    }

    /* access modifiers changed from: protected */
    public boolean isOfType(int i) {
        return (i & 32) != 0;
    }

    public void setHotseatEduController(HotseatEduController hotseatEduController) {
        this.mHotseatEduController = hotseatEduController;
    }

    public HotseatEduDialog(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public HotseatEduDialog(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mInsets = new Rect();
        this.mContent = this;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        setTranslationShift(1.0f);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mHotseatWrapper = findViewById(R.id.hotseat_wrapper);
        this.mSampleHotseat = (CellLayout) findViewById(R.id.sample_prediction);
        Context context = getContext();
        DeviceProfile deviceProfile = ((Launcher) this.mActivityContext).getDeviceProfile();
        Rect hotseatLayoutPadding = deviceProfile.getHotseatLayoutPadding(context);
        this.mSampleHotseat.getLayoutParams().height = deviceProfile.cellHeightPx;
        this.mSampleHotseat.setGridSize(deviceProfile.numShownHotseatIcons, 1);
        this.mSampleHotseat.setPadding(hotseatLayoutPadding.left, 0, hotseatLayoutPadding.right, 0);
        ((Button) findViewById(R.id.turn_predictions_on)).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                HotseatEduDialog.this.onAccept(view);
            }
        });
        Button button = (Button) findViewById(R.id.no_thanks);
        this.mDismissBtn = button;
        button.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                HotseatEduDialog.this.onDismiss(view);
            }
        });
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.button_container);
        int hotseatEndOffset = ApiWrapper.getHotseatEndOffset(context) - linearLayout.getPaddingEnd();
        if (InvariantDeviceProfile.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).getDeviceProfile(context).isTaskbarPresent && hotseatEndOffset > 0) {
            ((LinearLayout.LayoutParams) linearLayout.getLayoutParams()).setMarginEnd(hotseatEndOffset);
        }
        if (FeatureFlags.HOTSEAT_MIGRATE_TO_FOLDER.get()) {
            ((TextView) findViewById(R.id.hotseat_edu_content)).setText(R.string.hotseat_edu_message_migrate_alt);
        }
    }

    /* access modifiers changed from: private */
    public void onAccept(View view) {
        this.mHotseatEduController.migrate();
        handleClose(true);
        this.mHotseatEduController.moveHotseatItems();
        this.mHotseatEduController.finishOnboarding();
        ((Launcher) this.mActivityContext).getStatsLogManager().logger().log(StatsLogManager.LauncherEvent.LAUNCHER_HOTSEAT_EDU_ACCEPT);
    }

    /* access modifiers changed from: private */
    public void onDismiss(View view) {
        this.mHotseatEduController.showDimissTip();
        this.mHotseatEduController.finishOnboarding();
        ((Launcher) this.mActivityContext).getStatsLogManager().logger().log(StatsLogManager.LauncherEvent.LAUNCHER_HOTSEAT_EDU_DENY);
        handleClose(true);
    }

    public void setInsets(Rect rect) {
        int i = rect.left - this.mInsets.left;
        int i2 = rect.right - this.mInsets.right;
        int i3 = rect.bottom - this.mInsets.bottom;
        this.mInsets.set(rect);
        if (((Launcher) this.mActivityContext).getOrientation() == 1) {
            setPadding(i, getPaddingTop(), i2, 0);
            View view = this.mHotseatWrapper;
            view.setPadding(view.getPaddingLeft(), getPaddingTop(), this.mHotseatWrapper.getPaddingRight(), i3);
            this.mHotseatWrapper.getLayoutParams().height = ((Launcher) this.mActivityContext).getDeviceProfile().hotseatBarSizePx + rect.bottom;
            return;
        }
        setPadding(0, getPaddingTop(), 0, 0);
        View view2 = this.mHotseatWrapper;
        view2.setPadding(view2.getPaddingLeft(), getPaddingTop(), this.mHotseatWrapper.getPaddingRight(), (int) getResources().getDimension(R.dimen.bottom_sheet_edu_padding));
        ((TextView) findViewById(R.id.hotseat_edu_heading)).setText(R.string.hotseat_edu_title_migrate_landscape);
        ((TextView) findViewById(R.id.hotseat_edu_content)).setText(R.string.hotseat_edu_message_migrate_landscape);
    }

    private void animateOpen() {
        if (!this.mIsOpen && !this.mOpenCloseAnimator.isRunning()) {
            this.mIsOpen = true;
            this.mOpenCloseAnimator.setValues(new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(TRANSLATION_SHIFT, new float[]{0.0f})});
            this.mOpenCloseAnimator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
            this.mOpenCloseAnimator.start();
        }
    }

    /* access modifiers changed from: protected */
    public void handleClose(boolean z) {
        handleClose(true, 200);
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        handleClose(false);
    }

    private void populatePreview(List<WorkspaceItemInfo> list) {
        for (int i = 0; i < ((Launcher) this.mActivityContext).getDeviceProfile().numShownHotseatIcons; i++) {
            WorkspaceItemInfo workspaceItemInfo = list.get(i);
            PredictedAppIcon createIcon = PredictedAppIcon.createIcon(this.mSampleHotseat, workspaceItemInfo);
            createIcon.setEnabled(false);
            createIcon.setImportantForAccessibility(2);
            createIcon.verifyHighRes();
            this.mSampleHotseat.addViewToCellLayout(createIcon, i, workspaceItemInfo.getViewId(), new CellLayout.LayoutParams(i, 0, 1, 1), true);
        }
    }

    public void show(List<WorkspaceItemInfo> list) {
        if (getParent() == null && list.size() >= ((Launcher) this.mActivityContext).getDeviceProfile().numShownHotseatIcons && this.mHotseatEduController != null) {
            AbstractFloatingView.closeAllOpenViews((ActivityContext) this.mActivityContext);
            attachToContainer();
            animateOpen();
            populatePreview(list);
            ((Launcher) this.mActivityContext).getStatsLogManager().logger().log(StatsLogManager.LauncherEvent.LAUNCHER_HOTSEAT_EDU_SEEN);
        }
    }

    public static HotseatEduDialog getDialog(Launcher launcher) {
        return (HotseatEduDialog) LayoutInflater.from(launcher).inflate(R.layout.predicted_hotseat_edu, launcher.getDragLayer(), false);
    }
}
