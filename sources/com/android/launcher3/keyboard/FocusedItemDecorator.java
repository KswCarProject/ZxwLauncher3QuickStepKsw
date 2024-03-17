package com.android.launcher3.keyboard;

import android.graphics.Canvas;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.keyboard.FocusIndicatorHelper;

public class FocusedItemDecorator extends RecyclerView.ItemDecoration {
    private FocusIndicatorHelper mHelper;

    public FocusedItemDecorator(View view) {
        this.mHelper = new FocusIndicatorHelper.SimpleFocusIndicatorHelper(view);
    }

    public View.OnFocusChangeListener getFocusListener() {
        return this.mHelper;
    }

    public void onDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {
        this.mHelper.draw(canvas);
    }
}
