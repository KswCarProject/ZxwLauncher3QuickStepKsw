package com.android.launcher3.widget.picker;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.constraintlayout.solver.widgets.analyzer.BasicMeasure;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.recyclerview.ViewHolderBinder;
import com.android.launcher3.widget.model.WidgetListSpaceEntry;
import com.android.launcher3.widget.picker.WidgetsSpaceViewHolderBinder;
import java.util.List;
import java.util.function.IntSupplier;

public class WidgetsSpaceViewHolderBinder implements ViewHolderBinder<WidgetListSpaceEntry, RecyclerView.ViewHolder> {
    private final IntSupplier mEmptySpaceHeightProvider;

    public WidgetsSpaceViewHolderBinder(IntSupplier intSupplier) {
        this.mEmptySpaceHeightProvider = intSupplier;
    }

    public RecyclerView.ViewHolder newViewHolder(ViewGroup viewGroup) {
        return new RecyclerView.ViewHolder(new EmptySpaceView(viewGroup.getContext())) {
        };
    }

    public void bindViewHolder(RecyclerView.ViewHolder viewHolder, WidgetListSpaceEntry widgetListSpaceEntry, int i, List<Object> list) {
        ((EmptySpaceView) viewHolder.itemView).setFixedHeight(this.mEmptySpaceHeightProvider.getAsInt());
    }

    public static class EmptySpaceView extends View {
        private int mHeight;
        private Runnable mOnYChangeCallback;

        private EmptySpaceView(Context context) {
            super(context);
            this.mHeight = 0;
            animate().setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    WidgetsSpaceViewHolderBinder.EmptySpaceView.this.lambda$new$0$WidgetsSpaceViewHolderBinder$EmptySpaceView(valueAnimator);
                }
            });
        }

        public /* synthetic */ void lambda$new$0$WidgetsSpaceViewHolderBinder$EmptySpaceView(ValueAnimator valueAnimator) {
            notifyYChanged();
        }

        public boolean setFixedHeight(int i) {
            if (this.mHeight == i) {
                return false;
            }
            this.mHeight = i;
            requestLayout();
            return true;
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(this.mHeight, BasicMeasure.EXACTLY));
        }

        public void setOnYChangeCallback(Runnable runnable) {
            this.mOnYChangeCallback = runnable;
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            notifyYChanged();
        }

        public void offsetTopAndBottom(int i) {
            super.offsetTopAndBottom(i);
            notifyYChanged();
        }

        public void setTranslationY(float f) {
            super.setTranslationY(f);
            notifyYChanged();
        }

        private void notifyYChanged() {
            Runnable runnable = this.mOnYChangeCallback;
            if (runnable != null) {
                runnable.run();
            }
        }
    }
}
