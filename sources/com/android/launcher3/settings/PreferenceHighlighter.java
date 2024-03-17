package com.android.launcher3.settings;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Property;
import android.view.View;
import androidx.preference.Preference;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.icons.GraphicsUtils;
import com.android.launcher3.util.Themes;

public class PreferenceHighlighter extends RecyclerView.ItemDecoration implements Runnable {
    private static final int END_COLOR = GraphicsUtils.setColorAlphaBound(-1, 0);
    private static final Property<PreferenceHighlighter, Integer> HIGHLIGHT_COLOR = new Property<PreferenceHighlighter, Integer>(Integer.TYPE, "highlightColor") {
        public Integer get(PreferenceHighlighter preferenceHighlighter) {
            return Integer.valueOf(preferenceHighlighter.mHighlightColor);
        }

        public void set(PreferenceHighlighter preferenceHighlighter, Integer num) {
            int unused = preferenceHighlighter.mHighlightColor = num.intValue();
            preferenceHighlighter.mRv.invalidateItemDecorations();
        }
    };
    private static final long HIGHLIGHT_DURATION = 15000;
    private static final long HIGHLIGHT_FADE_IN_DURATION = 200;
    private static final long HIGHLIGHT_FADE_OUT_DURATION = 500;
    private final RectF mDrawRect = new RectF();
    private boolean mHighLightStarted = false;
    /* access modifiers changed from: private */
    public int mHighlightColor = END_COLOR;
    private final int mIndex;
    private final Paint mPaint = new Paint();
    private final Preference mPreference;
    /* access modifiers changed from: private */
    public final RecyclerView mRv;

    public interface HighlightDelegate {
        void offsetHighlight(View view, RectF rectF);
    }

    public PreferenceHighlighter(RecyclerView recyclerView, int i, Preference preference) {
        this.mRv = recyclerView;
        this.mIndex = i;
        this.mPreference = preference;
    }

    public void run() {
        this.mRv.addItemDecoration(this);
        this.mRv.smoothScrollToPosition(this.mIndex);
    }

    public void onDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = recyclerView.findViewHolderForAdapterPosition(this.mIndex);
        if (findViewHolderForAdapterPosition != null) {
            if (this.mHighLightStarted || state.getRemainingScrollVertical() == 0) {
                if (!this.mHighLightStarted) {
                    int colorAlphaBound = GraphicsUtils.setColorAlphaBound(Themes.getColorAccent(this.mRv.getContext()), 66);
                    ObjectAnimator ofArgb = ObjectAnimator.ofArgb(this, HIGHLIGHT_COLOR, new int[]{END_COLOR, colorAlphaBound});
                    ofArgb.setDuration(HIGHLIGHT_FADE_IN_DURATION);
                    ofArgb.setRepeatMode(2);
                    ofArgb.setRepeatCount(4);
                    ofArgb.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            PreferenceHighlighter.this.removeHighlight();
                        }
                    });
                    ofArgb.start();
                    this.mHighLightStarted = true;
                }
                View view = findViewHolderForAdapterPosition.itemView;
                this.mPaint.setColor(this.mHighlightColor);
                this.mDrawRect.set(0.0f, view.getY(), (float) recyclerView.getWidth(), view.getY() + ((float) view.getHeight()));
                Preference preference = this.mPreference;
                if (preference instanceof HighlightDelegate) {
                    ((HighlightDelegate) preference).offsetHighlight(view, this.mDrawRect);
                }
                canvas.drawRect(this.mDrawRect, this.mPaint);
            }
        }
    }

    /* access modifiers changed from: private */
    public void removeHighlight() {
        ObjectAnimator ofArgb = ObjectAnimator.ofArgb(this, HIGHLIGHT_COLOR, new int[]{this.mHighlightColor, END_COLOR});
        ofArgb.setDuration(HIGHLIGHT_FADE_OUT_DURATION);
        ofArgb.setStartDelay(HIGHLIGHT_DURATION);
        ofArgb.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                PreferenceHighlighter.this.mRv.removeItemDecoration(PreferenceHighlighter.this);
            }
        });
        ofArgb.start();
    }
}
