package com.android.launcher3.notification;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.widget.FrameLayout;
import com.android.launcher3.R;
import com.android.launcher3.popup.PopupContainerWithArrow;
import com.android.launcher3.touch.OverScroll;
import com.android.launcher3.touch.SingleAxisSwipeDetector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NotificationContainer extends FrameLayout implements SingleAxisSwipeDetector.Listener {
    private static final FloatProperty<NotificationContainer> DRAG_TRANSLATION_X = new FloatProperty<NotificationContainer>("notificationProgress") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(NotificationContainer notificationContainer, float f) {
            notificationContainer.setDragTranslationX(f);
        }

        public Float get(NotificationContainer notificationContainer) {
            return Float.valueOf(notificationContainer.mDragTranslationX);
        }
    };
    private static final Rect sTempRect = new Rect();
    private final ObjectAnimator mContentTranslateAnimator;
    /* access modifiers changed from: private */
    public float mDragTranslationX;
    private boolean mIgnoreTouch;
    private final List<NotificationInfo> mNotificationInfos;
    /* access modifiers changed from: private */
    public PopupContainerWithArrow mPopupContainer;
    private final NotificationMainView mPrimaryView;
    private final NotificationMainView mSecondaryView;
    /* access modifiers changed from: private */
    public final SingleAxisSwipeDetector mSwipeDetector;

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return super.generateDefaultLayoutParams();
    }

    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return super.generateLayoutParams(attributeSet);
    }

    public /* bridge */ /* synthetic */ ViewOverlay getOverlay() {
        return super.getOverlay();
    }

    public NotificationContainer(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public NotificationContainer(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public NotificationContainer(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mNotificationInfos = new ArrayList();
        this.mIgnoreTouch = false;
        this.mDragTranslationX = 0.0f;
        SingleAxisSwipeDetector singleAxisSwipeDetector = new SingleAxisSwipeDetector(getContext(), this, SingleAxisSwipeDetector.HORIZONTAL);
        this.mSwipeDetector = singleAxisSwipeDetector;
        singleAxisSwipeDetector.setDetectableScrollConditions(3, false);
        this.mContentTranslateAnimator = ObjectAnimator.ofFloat(this, DRAG_TRANSLATION_X, new float[]{0.0f});
        NotificationMainView notificationMainView = (NotificationMainView) View.inflate(getContext(), R.layout.notification_content, (ViewGroup) null);
        this.mPrimaryView = notificationMainView;
        NotificationMainView notificationMainView2 = (NotificationMainView) View.inflate(getContext(), R.layout.notification_content, (ViewGroup) null);
        this.mSecondaryView = notificationMainView2;
        notificationMainView2.setAlpha(0.0f);
        addView(notificationMainView2);
        addView(notificationMainView);
    }

    public void setPopupView(PopupContainerWithArrow popupContainerWithArrow) {
        this.mPopupContainer = popupContainerWithArrow;
    }

    public boolean onInterceptSwipeEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            Rect rect = sTempRect;
            rect.set(getLeft(), getTop(), getRight(), getBottom());
            boolean z = !rect.contains((int) motionEvent.getX(), (int) motionEvent.getY());
            this.mIgnoreTouch = z;
            if (!z) {
                this.mPopupContainer.getParent().requestDisallowInterceptTouchEvent(true);
            }
        }
        if (this.mIgnoreTouch || this.mPrimaryView.getNotificationInfo() == null) {
            return false;
        }
        this.mSwipeDetector.onTouchEvent(motionEvent);
        return this.mSwipeDetector.isDraggingOrSettling();
    }

    public boolean onSwipeEvent(MotionEvent motionEvent) {
        if (!this.mIgnoreTouch && this.mPrimaryView.getNotificationInfo() != null) {
            return this.mSwipeDetector.onTouchEvent(motionEvent);
        }
        return false;
    }

    public void applyNotificationInfos(List<NotificationInfo> list) {
        this.mNotificationInfos.clear();
        NotificationInfo notificationInfo = null;
        if (list.isEmpty()) {
            this.mPrimaryView.applyNotificationInfo((NotificationInfo) null);
            this.mSecondaryView.applyNotificationInfo((NotificationInfo) null);
            return;
        }
        this.mNotificationInfos.addAll(list);
        this.mPrimaryView.applyNotificationInfo(list.get(0));
        NotificationMainView notificationMainView = this.mSecondaryView;
        if (list.size() > 1) {
            notificationInfo = list.get(1);
        }
        notificationMainView.applyNotificationInfo(notificationInfo);
    }

    public void trimNotifications(List<String> list) {
        Iterator<NotificationInfo> it = this.mNotificationInfos.iterator();
        while (it.hasNext()) {
            if (!list.contains(it.next().notificationKey)) {
                it.remove();
            }
        }
        NotificationInfo notificationInfo = null;
        NotificationInfo notificationInfo2 = this.mNotificationInfos.size() > 0 ? this.mNotificationInfos.get(0) : null;
        if (this.mNotificationInfos.size() > 1) {
            notificationInfo = this.mNotificationInfos.get(1);
        }
        this.mPrimaryView.applyNotificationInfo(notificationInfo2);
        this.mSecondaryView.applyNotificationInfo(notificationInfo);
        this.mPrimaryView.onPrimaryDrag(0.0f);
        this.mSecondaryView.onSecondaryDrag(0.0f);
    }

    /* access modifiers changed from: private */
    public void setDragTranslationX(float f) {
        this.mDragTranslationX = f;
        float width = f / ((float) getWidth());
        this.mPrimaryView.onPrimaryDrag(width);
        if (this.mSecondaryView.getNotificationInfo() == null) {
            this.mSecondaryView.setAlpha(0.0f);
        } else {
            this.mSecondaryView.onSecondaryDrag(width);
        }
    }

    public void onDragStart(boolean z, float f) {
        this.mPopupContainer.showArrow(false);
    }

    public boolean onDrag(float f) {
        if (!this.mPrimaryView.canChildBeDismissed()) {
            f = (float) OverScroll.dampedScroll(f, getWidth());
        }
        float width = f / ((float) getWidth());
        this.mPrimaryView.onPrimaryDrag(width);
        if (this.mSecondaryView.getNotificationInfo() == null) {
            this.mSecondaryView.setAlpha(0.0f);
        } else {
            this.mSecondaryView.onSecondaryDrag(width);
        }
        this.mContentTranslateAnimator.cancel();
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0038, code lost:
        if (r0 < 0.0f) goto L_0x0024;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0022, code lost:
        if (r9 < 0.0f) goto L_0x0024;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0027, code lost:
        r5 = r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDragEnd(float r9) {
        /*
            r8 = this;
            com.android.launcher3.notification.NotificationMainView r0 = r8.mPrimaryView
            float r0 = r0.getTranslationX()
            int r1 = r8.getWidth()
            float r1 = (float) r1
            com.android.launcher3.notification.NotificationMainView r2 = r8.mPrimaryView
            boolean r2 = r2.canChildBeDismissed()
            r3 = 1
            r4 = 0
            r5 = 0
            if (r2 != 0) goto L_0x0018
        L_0x0016:
            r2 = r4
            goto L_0x003b
        L_0x0018:
            com.android.launcher3.touch.SingleAxisSwipeDetector r2 = r8.mSwipeDetector
            boolean r2 = r2.isFling(r9)
            if (r2 == 0) goto L_0x002a
            int r2 = (r9 > r5 ? 1 : (r9 == r5 ? 0 : -1))
            if (r2 >= 0) goto L_0x0027
        L_0x0024:
            float r2 = -r1
            r5 = r2
            goto L_0x0028
        L_0x0027:
            r5 = r1
        L_0x0028:
            r2 = r3
            goto L_0x003b
        L_0x002a:
            float r2 = java.lang.Math.abs(r0)
            r6 = 1073741824(0x40000000, float:2.0)
            float r6 = r1 / r6
            int r2 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r2 <= 0) goto L_0x0016
            int r2 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r2 >= 0) goto L_0x0027
            goto L_0x0024
        L_0x003b:
            float r6 = r5 - r0
            float r6 = r6 / r1
            long r6 = com.android.launcher3.touch.BaseSwipeDetector.calculateDuration(r9, r6)
            android.animation.ObjectAnimator r1 = r8.mContentTranslateAnimator
            r1.removeAllListeners()
            android.animation.ObjectAnimator r1 = r8.mContentTranslateAnimator
            android.animation.ObjectAnimator r1 = r1.setDuration(r6)
            android.view.animation.Interpolator r9 = com.android.launcher3.anim.Interpolators.scrollInterpolatorForVelocity(r9)
            r1.setInterpolator(r9)
            android.animation.ObjectAnimator r9 = r8.mContentTranslateAnimator
            r1 = 2
            float[] r1 = new float[r1]
            r1[r4] = r0
            r1[r3] = r5
            r9.setFloatValues(r1)
            com.android.launcher3.notification.NotificationMainView r9 = r8.mPrimaryView
            android.animation.ObjectAnimator r0 = r8.mContentTranslateAnimator
            com.android.launcher3.notification.NotificationContainer$2 r1 = new com.android.launcher3.notification.NotificationContainer$2
            r1.<init>(r2, r9)
            r0.addListener(r1)
            android.animation.ObjectAnimator r9 = r8.mContentTranslateAnimator
            r9.start()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.notification.NotificationContainer.onDragEnd(float):void");
    }

    public void updateBackgroundColor(int i, AnimatorSet animatorSet) {
        this.mPrimaryView.updateBackgroundColor(i, animatorSet);
        this.mSecondaryView.updateBackgroundColor(i, animatorSet);
    }

    public void updateHeader(int i) {
        this.mPrimaryView.updateHeader(i);
        this.mSecondaryView.updateHeader(i - 1);
    }
}
