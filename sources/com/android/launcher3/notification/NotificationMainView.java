package com.android.launcher3.notification;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewOverlay;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.popup.PopupDataProvider;
import com.android.launcher3.util.Themes;
import com.android.launcher3.views.ActivityContext;

public class NotificationMainView extends LinearLayout {
    public static final ItemInfo NOTIFICATION_ITEM_INFO = new ItemInfo();
    private static final float PRIMARY_GONE_PROGRESS = 0.7f;
    private static final float PRIMARY_MAX_PROGRESS = 0.6f;
    private static final float PRIMARY_MIN_PROGRESS = 0.4f;
    private static final float SECONDARY_CONTENT_MAX_PROGRESS = 0.6f;
    private static final float SECONDARY_MAX_PROGRESS = 0.5f;
    private static final float SECONDARY_MIN_PROGRESS = 0.3f;
    private final GradientDrawable mBackground;
    private int mBackgroundColor;
    private View mHeader;
    private TextView mHeaderCount;
    private View mIconView;
    private View mMainView;
    private final int mMaxElevation;
    private final int mMaxTransX;
    private NotificationInfo mNotificationInfo;
    private final int mNotificationSpace;
    /* access modifiers changed from: private */
    public final Rect mOutline;
    private TextView mTextView;
    private TextView mTitleView;

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return super.generateDefaultLayoutParams();
    }

    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return super.generateLayoutParams(attributeSet);
    }

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return super.generateLayoutParams(layoutParams);
    }

    public /* bridge */ /* synthetic */ ViewOverlay getOverlay() {
        return super.getOverlay();
    }

    public NotificationMainView(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public NotificationMainView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public NotificationMainView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public NotificationMainView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mOutline = new Rect();
        final float dialogCornerRadius = Themes.getDialogCornerRadius(context);
        GradientDrawable gradientDrawable = new GradientDrawable();
        this.mBackground = gradientDrawable;
        gradientDrawable.setColor(Themes.getAttrColor(context, R.attr.popupColorPrimary));
        gradientDrawable.setCornerRadius(dialogCornerRadius);
        setBackground(gradientDrawable);
        int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.deep_shortcuts_elevation);
        this.mMaxElevation = dimensionPixelSize;
        setElevation((float) dimensionPixelSize);
        this.mMaxTransX = getResources().getDimensionPixelSize(R.dimen.notification_max_trans);
        this.mNotificationSpace = getResources().getDimensionPixelSize(R.dimen.notification_space);
        setClipToOutline(true);
        setOutlineProvider(new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(NotificationMainView.this.mOutline, dialogCornerRadius);
            }
        });
    }

    public void updateHeader(int i) {
        int i2;
        String str;
        if (i <= 1) {
            i2 = 4;
            str = "";
        } else {
            str = String.valueOf(i);
            i2 = 0;
        }
        this.mHeaderCount.setText(str);
        this.mHeaderCount.setVisibility(i2);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.text_and_background);
        this.mTitleView = (TextView) viewGroup.findViewById(R.id.title);
        this.mTextView = (TextView) viewGroup.findViewById(R.id.text);
        this.mIconView = findViewById(R.id.popup_item_icon);
        this.mHeaderCount = (TextView) findViewById(R.id.notification_count);
        this.mHeader = findViewById(R.id.header);
        this.mMainView = findViewById(R.id.main_view);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.mOutline.set(0, 0, getWidth(), getHeight());
        invalidateOutline();
    }

    private void updateBackgroundColor(int i) {
        this.mBackgroundColor = i;
        this.mBackground.setColor(i);
        NotificationInfo notificationInfo = this.mNotificationInfo;
        if (notificationInfo != null) {
            this.mIconView.setBackground(notificationInfo.getIconForBackground(getContext(), this.mBackgroundColor));
        }
    }

    public void updateBackgroundColor(int i, AnimatorSet animatorSet) {
        ValueAnimator ofArgb = ValueAnimator.ofArgb(new int[]{this.mBackgroundColor, i});
        ofArgb.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                NotificationMainView.this.lambda$updateBackgroundColor$0$NotificationMainView(valueAnimator);
            }
        });
        animatorSet.play(ofArgb);
    }

    public /* synthetic */ void lambda$updateBackgroundColor$0$NotificationMainView(ValueAnimator valueAnimator) {
        updateBackgroundColor(((Integer) valueAnimator.getAnimatedValue()).intValue());
    }

    public void applyNotificationInfo(NotificationInfo notificationInfo) {
        this.mNotificationInfo = notificationInfo;
        if (notificationInfo != null) {
            NotificationListener instanceIfConnected = NotificationListener.getInstanceIfConnected();
            if (instanceIfConnected != null) {
                instanceIfConnected.setNotificationsShown(new String[]{this.mNotificationInfo.notificationKey});
            }
            CharSequence charSequence = this.mNotificationInfo.title;
            CharSequence charSequence2 = this.mNotificationInfo.text;
            if (TextUtils.isEmpty(charSequence) || TextUtils.isEmpty(charSequence2)) {
                this.mTitleView.setMaxLines(2);
                this.mTitleView.setText(TextUtils.isEmpty(charSequence) ? charSequence2.toString() : charSequence.toString());
                this.mTextView.setVisibility(8);
            } else {
                this.mTitleView.setText(charSequence.toString());
                this.mTextView.setText(charSequence2.toString());
            }
            this.mIconView.setBackground(this.mNotificationInfo.getIconForBackground(getContext(), this.mBackgroundColor));
            if (this.mNotificationInfo.intent != null) {
                setOnClickListener(this.mNotificationInfo);
            }
            setTag(NOTIFICATION_ITEM_INFO);
        }
    }

    public void setContentAlpha(float f) {
        this.mHeader.setAlpha(f);
        this.mMainView.setAlpha(f);
    }

    public void setContentTranslationX(float f) {
        this.mHeader.setTranslationX(f);
        this.mMainView.setTranslationX(f);
    }

    public void onPrimaryDrag(float f) {
        float abs = Math.abs(f);
        int width = getWidth();
        if (abs < 0.4f) {
            setAlpha(1.0f);
            setContentAlpha(1.0f);
            setElevation((float) this.mMaxElevation);
        } else if (abs < 0.6f) {
            setAlpha(1.0f);
            setContentAlpha(Utilities.mapToRange(abs, 0.4f, 0.6f, 1.0f, 0.0f, Interpolators.LINEAR));
            setElevation(Utilities.mapToRange(abs, 0.4f, 0.6f, (float) this.mMaxElevation, 0.0f, Interpolators.LINEAR));
        } else {
            setAlpha(Utilities.mapToRange(abs, 0.6f, 0.7f, 1.0f, 0.0f, Interpolators.LINEAR));
            setContentAlpha(0.0f);
            setElevation(0.0f);
        }
        setTranslationX(((float) width) * f);
    }

    public void onSecondaryDrag(float f) {
        float f2;
        float f3;
        float abs = Math.abs(f);
        if (abs < 0.3f) {
            setAlpha(0.0f);
            setContentAlpha(0.0f);
            setElevation(0.0f);
        } else if (abs < 0.5f) {
            setAlpha(Utilities.mapToRange(abs, 0.3f, 0.5f, 0.0f, 1.0f, Interpolators.LINEAR));
            setContentAlpha(0.0f);
            setElevation(0.0f);
        } else {
            setAlpha(1.0f);
            if (abs > 0.6f) {
                f3 = 1.0f;
            } else {
                f3 = Utilities.mapToRange(abs, 0.5f, 0.6f, 0.0f, 1.0f, Interpolators.LINEAR);
            }
            setContentAlpha(f3);
            setElevation(Utilities.mapToRange(abs, 0.5f, 1.0f, 0.0f, (float) this.mMaxElevation, Interpolators.LINEAR));
        }
        int width = (int) (((float) getWidth()) * abs);
        if (abs > 0.7f) {
            f2 = Utilities.mapToRange(abs, 0.7f, 1.0f, (float) this.mNotificationSpace, 0.0f, Interpolators.LINEAR);
        } else {
            f2 = (float) this.mNotificationSpace;
        }
        int i = (int) f2;
        int i2 = (f > 0.0f ? 1 : (f == 0.0f ? 0 : -1));
        if (i2 < 0) {
            this.mOutline.left = Math.max(0, (getWidth() - width) + i);
            this.mOutline.right = getWidth();
        } else {
            this.mOutline.right = Math.min(getWidth(), width - i);
            this.mOutline.left = 0;
        }
        float f4 = ((float) this.mMaxTransX) * (1.0f - abs);
        if (i2 >= 0) {
            f4 = -f4;
        }
        setContentTranslationX(f4);
        invalidateOutline();
    }

    public NotificationInfo getNotificationInfo() {
        return this.mNotificationInfo;
    }

    public boolean canChildBeDismissed() {
        NotificationInfo notificationInfo = this.mNotificationInfo;
        return notificationInfo != null && notificationInfo.dismissable;
    }

    public void onChildDismissed() {
        ActivityContext activityContext = (ActivityContext) ActivityContext.lookupContext(getContext());
        PopupDataProvider popupDataProvider = activityContext.getPopupDataProvider();
        if (popupDataProvider != null) {
            popupDataProvider.cancelNotification(this.mNotificationInfo.notificationKey);
            activityContext.getStatsLogManager().logger().log(StatsLogManager.LauncherEvent.LAUNCHER_NOTIFICATION_DISMISSED);
        }
    }
}
