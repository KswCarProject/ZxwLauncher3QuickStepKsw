package com.android.launcher3;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.android.launcher3.util.UiThreadHelper;
import com.android.launcher3.views.ActivityContext;

public class ExtendedEditText extends EditText {
    private OnBackKeyListener mBackKeyListener;
    private boolean mForceDisableSuggestions = false;
    private boolean mShowImeAfterFirstLayout;

    public interface OnBackKeyListener {
        boolean onBackKey();
    }

    public boolean onDragEvent(DragEvent dragEvent) {
        return false;
    }

    public /* bridge */ /* synthetic */ CharSequence getText() {
        return super.getText();
    }

    public ExtendedEditText(Context context) {
        super(context);
    }

    public ExtendedEditText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ExtendedEditText(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void setOnBackKeyListener(OnBackKeyListener onBackKeyListener) {
        this.mBackKeyListener = onBackKeyListener;
    }

    public boolean onKeyPreIme(int i, KeyEvent keyEvent) {
        if (i != 4 || keyEvent.getAction() != 1) {
            return super.onKeyPreIme(i, keyEvent);
        }
        if (TextUtils.isEmpty(getText())) {
            hideKeyboard();
        }
        OnBackKeyListener onBackKeyListener = this.mBackKeyListener;
        if (onBackKeyListener != null) {
            return onBackKeyListener.onBackKey();
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.mShowImeAfterFirstLayout) {
            post(new Runnable() {
                public final void run() {
                    ExtendedEditText.this.lambda$onLayout$0$ExtendedEditText();
                }
            });
        }
    }

    public /* synthetic */ void lambda$onLayout$0$ExtendedEditText() {
        showSoftInput();
        this.mShowImeAfterFirstLayout = false;
    }

    public void showKeyboard() {
        this.mShowImeAfterFirstLayout = !showSoftInput();
    }

    public void hideKeyboard() {
        UiThreadHelper.hideKeyboardAsync((ActivityContext) ActivityContext.lookupContext(getContext()), getWindowToken());
        clearFocus();
    }

    private boolean showSoftInput() {
        if (!requestFocus() || !((InputMethodManager) getContext().getSystemService(InputMethodManager.class)).showSoftInput(this, 1)) {
            return false;
        }
        return true;
    }

    public void dispatchBackKey() {
        hideKeyboard();
        OnBackKeyListener onBackKeyListener = this.mBackKeyListener;
        if (onBackKeyListener != null) {
            onBackKeyListener.onBackKey();
        }
    }

    public void forceDisableSuggestions(boolean z) {
        this.mForceDisableSuggestions = z;
    }

    public boolean isSuggestionsEnabled() {
        return !this.mForceDisableSuggestions && super.isSuggestionsEnabled();
    }

    public void reset() {
        if (!TextUtils.isEmpty(getText())) {
            setText("");
        }
    }
}
