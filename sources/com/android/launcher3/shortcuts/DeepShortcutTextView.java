package com.android.launcher3.shortcuts;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;
import android.widget.Toast;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;

public class DeepShortcutTextView extends BubbleTextView {
    private final Rect mDragHandleBounds;
    private final int mDragHandleWidth;
    private Toast mInstructionToast;
    private final Rect mLoadingStateBounds;
    private Drawable mLoadingStatePlaceholder;
    private boolean mShowInstructionToast;
    private boolean mShowLoadingState;

    /* access modifiers changed from: protected */
    public void applyCompoundDrawables(Drawable drawable) {
    }

    /* access modifiers changed from: protected */
    public void drawDotIfNecessary(Canvas canvas) {
    }

    public DeepShortcutTextView(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public DeepShortcutTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public DeepShortcutTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mDragHandleBounds = new Rect();
        this.mShowInstructionToast = false;
        this.mLoadingStateBounds = new Rect();
        Resources resources = getResources();
        this.mDragHandleWidth = resources.getDimensionPixelSize(R.dimen.popup_padding_end) + resources.getDimensionPixelSize(R.dimen.deep_shortcut_drag_handle_size) + (resources.getDimensionPixelSize(R.dimen.deep_shortcut_drawable_padding) / 2);
        showLoadingState(true);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.mDragHandleBounds.set(0, 0, this.mDragHandleWidth, getMeasuredHeight());
        if (!Utilities.isRtl(getResources())) {
            this.mDragHandleBounds.offset(getMeasuredWidth() - this.mDragHandleBounds.width(), 0);
        }
        setLoadingBounds();
    }

    private void setLoadingBounds() {
        if (this.mLoadingStatePlaceholder != null) {
            this.mLoadingStateBounds.set(0, 0, (getMeasuredWidth() - this.mDragHandleWidth) - getPaddingStart(), this.mLoadingStatePlaceholder.getIntrinsicHeight());
            this.mLoadingStateBounds.offset(Utilities.isRtl(getResources()) ? this.mDragHandleWidth : getPaddingStart(), (int) (((float) (getMeasuredHeight() - this.mLoadingStatePlaceholder.getIntrinsicHeight())) / 2.0f));
            this.mLoadingStatePlaceholder.setBounds(this.mLoadingStateBounds);
        }
    }

    public void setText(CharSequence charSequence, TextView.BufferType bufferType) {
        super.setText(charSequence, bufferType);
        if (!TextUtils.isEmpty(charSequence)) {
            showLoadingState(false);
        }
    }

    /* access modifiers changed from: protected */
    public boolean shouldIgnoreTouchDown(float f, float f2) {
        this.mShowInstructionToast = this.mDragHandleBounds.contains((int) f, (int) f2);
        return false;
    }

    public boolean performClick() {
        if (!this.mShowInstructionToast) {
            return super.performClick();
        }
        showToast();
        return true;
    }

    public void onDraw(Canvas canvas) {
        if (!this.mShowLoadingState) {
            super.onDraw(canvas);
        } else {
            this.mLoadingStatePlaceholder.draw(canvas);
        }
    }

    private void showLoadingState(boolean z) {
        if (z != this.mShowLoadingState) {
            this.mShowLoadingState = z;
            if (z) {
                this.mLoadingStatePlaceholder = getContext().getDrawable(R.drawable.deep_shortcuts_text_placeholder);
                setLoadingBounds();
            } else {
                this.mLoadingStatePlaceholder = null;
            }
            invalidate();
        }
    }

    private void showToast() {
        Toast toast = this.mInstructionToast;
        if (toast != null) {
            toast.cancel();
        }
        Toast makeText = Toast.makeText(getContext(), Utilities.wrapForTts(getContext().getText(R.string.long_press_shortcut_to_add), getContext().getString(R.string.long_accessible_way_to_add_shortcut)), 0);
        this.mInstructionToast = makeText;
        makeText.show();
    }
}
