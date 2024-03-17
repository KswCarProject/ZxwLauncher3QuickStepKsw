package com.android.launcher3.widget;

import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Reorderable;
import com.android.launcher3.dragndrop.DraggableView;
import com.android.launcher3.views.ActivityContext;
import java.util.ArrayList;

public abstract class NavigableAppWidgetHostView extends AppWidgetHostView implements DraggableView, Reorderable {
    protected final ActivityContext mActivity;
    @ViewDebug.ExportedProperty(category = "launcher")
    private boolean mChildrenFocused;
    private float mScaleForReorderBounce = 1.0f;
    private float mScaleToFit = 1.0f;
    private final Rect mTempRect = new Rect();
    private final PointF mTranslationForCentering = new PointF(0.0f, 0.0f);
    private final PointF mTranslationForMoveFromCenterAnimation = new PointF(0.0f, 0.0f);
    private final PointF mTranslationForReorderBounce = new PointF(0.0f, 0.0f);
    private final PointF mTranslationForReorderPreview = new PointF(0.0f, 0.0f);

    public View getView() {
        return this;
    }

    public int getViewType() {
        return 1;
    }

    /* access modifiers changed from: protected */
    public abstract boolean shouldAllowDirectClick();

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

    public NavigableAppWidgetHostView(Context context) {
        super(context);
        this.mActivity = (ActivityContext) ActivityContext.lookupContext(context);
    }

    public int getDescendantFocusability() {
        return this.mChildrenFocused ? 131072 : 393216;
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (!this.mChildrenFocused || keyEvent.getKeyCode() != 111 || keyEvent.getAction() != 1) {
            return super.dispatchKeyEvent(keyEvent);
        }
        this.mChildrenFocused = false;
        requestFocus();
        return true;
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (this.mChildrenFocused || i != 66) {
            return super.onKeyDown(i, keyEvent);
        }
        keyEvent.startTracking();
        return true;
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (keyEvent.isTracking() && !this.mChildrenFocused && i == 66) {
            this.mChildrenFocused = true;
            ArrayList focusables = getFocusables(2);
            focusables.remove(this);
            int size = focusables.size();
            if (size == 0) {
                this.mChildrenFocused = false;
            } else if (size == 1 && shouldAllowDirectClick()) {
                ((View) focusables.get(0)).performClick();
                this.mChildrenFocused = false;
                return true;
            } else {
                ((View) focusables.get(0)).requestFocus();
                return true;
            }
        }
        return super.onKeyUp(i, keyEvent);
    }

    /* access modifiers changed from: protected */
    public void onFocusChanged(boolean z, int i, Rect rect) {
        if (z) {
            this.mChildrenFocused = false;
            dispatchChildFocus(false);
        }
        super.onFocusChanged(z, i, rect);
    }

    public void requestChildFocus(View view, View view2) {
        super.requestChildFocus(view, view2);
        dispatchChildFocus(this.mChildrenFocused && view2 != null);
        if (view2 != null) {
            view2.setFocusableInTouchMode(false);
        }
    }

    public void clearChildFocus(View view) {
        super.clearChildFocus(view);
        dispatchChildFocus(false);
    }

    public boolean dispatchUnhandledMove(View view, int i) {
        return this.mChildrenFocused;
    }

    private void dispatchChildFocus(boolean z) {
        setSelected(z);
    }

    private void updateTranslation() {
        super.setTranslationX(this.mTranslationForReorderBounce.x + this.mTranslationForReorderPreview.x + this.mTranslationForCentering.x + this.mTranslationForMoveFromCenterAnimation.x);
        super.setTranslationY(this.mTranslationForReorderBounce.y + this.mTranslationForReorderPreview.y + this.mTranslationForCentering.y + this.mTranslationForMoveFromCenterAnimation.y);
    }

    public void setTranslationForCentering(float f, float f2) {
        this.mTranslationForCentering.set(f, f2);
        updateTranslation();
    }

    public void setTranslationForMoveFromCenterAnimation(float f, float f2) {
        this.mTranslationForMoveFromCenterAnimation.set(f, f2);
        updateTranslation();
    }

    public void setReorderBounceOffset(float f, float f2) {
        this.mTranslationForReorderBounce.set(f, f2);
        updateTranslation();
    }

    public void getReorderBounceOffset(PointF pointF) {
        pointF.set(this.mTranslationForReorderBounce);
    }

    public void setReorderPreviewOffset(float f, float f2) {
        this.mTranslationForReorderPreview.set(f, f2);
        updateTranslation();
    }

    public void getReorderPreviewOffset(PointF pointF) {
        pointF.set(this.mTranslationForReorderPreview);
    }

    private void updateScale() {
        super.setScaleX(this.mScaleToFit * this.mScaleForReorderBounce);
        super.setScaleY(this.mScaleToFit * this.mScaleForReorderBounce);
    }

    public void setReorderBounceScale(float f) {
        this.mScaleForReorderBounce = f;
        updateScale();
    }

    public float getReorderBounceScale() {
        return this.mScaleForReorderBounce;
    }

    public void setScaleToFit(float f) {
        this.mScaleToFit = f;
        updateScale();
    }

    public float getScaleToFit() {
        return this.mScaleToFit;
    }

    public void getWorkspaceVisualDragBounds(Rect rect) {
        getWidgetInset(this.mActivity.getDeviceProfile(), this.mTempRect);
        rect.set(this.mTempRect.left, this.mTempRect.top, ((int) (((float) getMeasuredWidth()) * this.mScaleToFit)) - this.mTempRect.right, ((int) (((float) getMeasuredHeight()) * this.mScaleToFit)) - this.mTempRect.bottom);
    }

    public void getWidgetInset(DeviceProfile deviceProfile, Rect rect) {
        if (!deviceProfile.shouldInsetWidgets()) {
            rect.setEmpty();
            return;
        }
        AppWidgetProviderInfo appWidgetInfo = getAppWidgetInfo();
        if (appWidgetInfo == null) {
            rect.set(deviceProfile.inv.defaultWidgetPadding);
        } else {
            AppWidgetHostView.getDefaultPaddingForWidget(getContext(), appWidgetInfo.provider, rect);
        }
    }
}
