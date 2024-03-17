package com.android.launcher3.folder;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.view.inputmethod.InputMethodManager;
import com.android.launcher3.ExtendedEditText;
import java.util.List;

public class FolderNameEditText extends ExtendedEditText {
    private static final boolean DEBUG = false;
    private static final String TAG = "FolderNameEditText";
    /* access modifiers changed from: private */
    public boolean mEnteredCompose = false;

    public FolderNameEditText(Context context) {
        super(context);
    }

    public FolderNameEditText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public FolderNameEditText(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        return new FolderNameEditTextInputConnection(super.onCreateInputConnection(editorInfo), true);
    }

    public void displayCompletions(List<String> list) {
        int min = Math.min(list.size(), 4);
        CompletionInfo[] completionInfoArr = new CompletionInfo[min];
        for (int i = 0; i < min; i++) {
            completionInfoArr[i] = new CompletionInfo((long) i, i, list.get(i));
        }
        postDelayed(new Runnable(completionInfoArr) {
            public final /* synthetic */ CompletionInfo[] f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                FolderNameEditText.this.lambda$displayCompletions$0$FolderNameEditText(this.f$1);
            }
        }, 40);
    }

    public /* synthetic */ void lambda$displayCompletions$0$FolderNameEditText(CompletionInfo[] completionInfoArr) {
        ((InputMethodManager) getContext().getSystemService(InputMethodManager.class)).displayCompletions(this, completionInfoArr);
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        if (i == 0 && i3 == 0 && i2 > 0) {
            this.mEnteredCompose = true;
        }
    }

    public void onCommitCompletion(CompletionInfo completionInfo) {
        setText(completionInfo.getText());
        setSelection(completionInfo.getText().length());
        this.mEnteredCompose = false;
    }

    /* access modifiers changed from: protected */
    public void setEnteredCompose(boolean z) {
        this.mEnteredCompose = z;
    }

    private class FolderNameEditTextInputConnection extends InputConnectionWrapper {
        FolderNameEditTextInputConnection(InputConnection inputConnection, boolean z) {
            super(inputConnection, z);
        }

        public boolean setComposingText(CharSequence charSequence, int i) {
            boolean unused = FolderNameEditText.this.mEnteredCompose = true;
            return super.setComposingText(charSequence, i);
        }
    }

    public void reset() {
        View focusSearch;
        super.reset();
        if (isFocused() && (focusSearch = focusSearch(130)) != null) {
            focusSearch.requestFocus();
        }
        hideKeyboard();
    }
}
