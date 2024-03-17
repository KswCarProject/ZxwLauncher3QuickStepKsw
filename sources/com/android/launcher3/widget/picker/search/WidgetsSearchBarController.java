package com.android.launcher3.widget.picker.search;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import com.android.launcher3.ExtendedEditText;
import com.android.launcher3.search.SearchAlgorithm;
import com.android.launcher3.search.SearchCallback;
import com.android.launcher3.widget.model.WidgetsListBaseEntry;
import java.util.ArrayList;

public class WidgetsSearchBarController implements TextWatcher, SearchCallback<WidgetsListBaseEntry>, ExtendedEditText.OnBackKeyListener, View.OnKeyListener {
    private static final boolean DEBUG = false;
    private static final String TAG = "WidgetsSearchBarController";
    protected ImageButton mCancelButton;
    protected ExtendedEditText mInput;
    protected String mQuery;
    protected SearchAlgorithm<WidgetsListBaseEntry> mSearchAlgorithm;
    protected SearchModeListener mSearchModeListener;

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public WidgetsSearchBarController(SearchAlgorithm<WidgetsListBaseEntry> searchAlgorithm, ExtendedEditText extendedEditText, ImageButton imageButton, SearchModeListener searchModeListener) {
        this.mSearchAlgorithm = searchAlgorithm;
        this.mInput = extendedEditText;
        extendedEditText.addTextChangedListener(this);
        this.mInput.setOnBackKeyListener(this);
        this.mInput.setOnKeyListener(this);
        this.mCancelButton = imageButton;
        imageButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                WidgetsSearchBarController.this.lambda$new$0$WidgetsSearchBarController(view);
            }
        });
        this.mSearchModeListener = searchModeListener;
    }

    public /* synthetic */ void lambda$new$0$WidgetsSearchBarController(View view) {
        clearSearchResult();
    }

    public void afterTextChanged(Editable editable) {
        String obj = editable.toString();
        this.mQuery = obj;
        if (obj.isEmpty()) {
            this.mSearchAlgorithm.cancel(true);
            this.mSearchModeListener.exitSearchMode();
            this.mCancelButton.setVisibility(8);
            return;
        }
        this.mSearchAlgorithm.cancel(false);
        this.mSearchModeListener.enterSearchMode();
        this.mSearchAlgorithm.doSearch(this.mQuery, this);
        this.mCancelButton.setVisibility(0);
    }

    public void onSearchResult(String str, ArrayList<WidgetsListBaseEntry> arrayList) {
        this.mSearchModeListener.onSearchResults(arrayList);
    }

    public void clearSearchResult() {
        this.mInput.setText("");
    }

    public void onDestroy() {
        this.mSearchAlgorithm.destroy();
    }

    public boolean onBackKey() {
        clearFocus();
        return true;
    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i != 66 || keyEvent.getAction() != 1) {
            return false;
        }
        clearFocus();
        return true;
    }

    public void clearFocus() {
        this.mInput.clearFocus();
        this.mInput.hideKeyboard();
    }
}
