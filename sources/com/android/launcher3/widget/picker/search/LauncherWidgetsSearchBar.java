package com.android.launcher3.widget.picker.search;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.android.launcher3.ExtendedEditText;
import com.android.launcher3.R;
import com.android.launcher3.popup.PopupDataProvider;

public class LauncherWidgetsSearchBar extends LinearLayout implements WidgetsSearchBar {
    private ImageButton mCancelButton;
    private WidgetsSearchBarController mController;
    private ExtendedEditText mEditText;

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

    public LauncherWidgetsSearchBar(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public LauncherWidgetsSearchBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public LauncherWidgetsSearchBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void initialize(PopupDataProvider popupDataProvider, SearchModeListener searchModeListener) {
        this.mController = new WidgetsSearchBarController(new SimpleWidgetsSearchAlgorithm(popupDataProvider), this.mEditText, this.mCancelButton, searchModeListener);
    }

    public void reset() {
        this.mController.clearSearchResult();
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mEditText = (ExtendedEditText) findViewById(R.id.widgets_search_bar_edit_text);
        this.mCancelButton = (ImageButton) findViewById(R.id.widgets_search_cancel_button);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mController.onDestroy();
    }

    public boolean isSearchBarFocused() {
        return this.mEditText.isFocused();
    }

    public void clearSearchBarFocus() {
        this.mController.clearFocus();
    }
}
