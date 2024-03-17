package com.android.launcher3.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Insets;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowInsets;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.FastScrollRecyclerView;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.graphics.FastScrollThumbDrawable;
import com.android.launcher3.util.Themes;
import com.android.launcher3.util.UiThreadHelper;
import java.util.Collections;
import java.util.List;

public class RecyclerViewFastScroller extends View {
    private static final boolean DEBUG = false;
    private static final int FASTSCROLL_THRESHOLD_MILLIS = 40;
    private static final int MAX_TRACK_ALPHA = 30;
    private static final int SCROLLBAR_LEFT_OFFSET_TOUCH_DELEGATE_DP = 5;
    private static final int SCROLL_BAR_VIS_DURATION = 150;
    private static final int SCROLL_DELTA_THRESHOLD_DP = 4;
    private static final List<Rect> SYSTEM_GESTURE_EXCLUSION_RECT = Collections.singletonList(new Rect());
    private static final String TAG = "RecyclerViewFastScroller";
    private static final Property<RecyclerViewFastScroller, Integer> TRACK_WIDTH = new Property<RecyclerViewFastScroller, Integer>(Integer.class, "width") {
        public Integer get(RecyclerViewFastScroller recyclerViewFastScroller) {
            return Integer.valueOf(recyclerViewFastScroller.mWidth);
        }

        public void set(RecyclerViewFastScroller recyclerViewFastScroller, Integer num) {
            recyclerViewFastScroller.setTrackWidth(num.intValue());
        }
    };
    private static final Rect sTempRect = new Rect();
    private final boolean mCanThumbDetach;
    private final ViewConfiguration mConfig;
    private final float mDeltaThreshold;
    private long mDownTimeStampMillis;
    private int mDownX;
    private int mDownY;
    /* access modifiers changed from: private */
    public int mDy;
    private boolean mIgnoreDragGesture;
    private boolean mIsDragging;
    private boolean mIsThumbDetached;
    private float mLastTouchY;
    private int mLastY;
    private final int mMaxWidth;
    private final int mMinWidth;
    private RecyclerView.OnScrollListener mOnScrollListener;
    private String mPopupSectionName;
    private TextView mPopupView;
    private boolean mPopupVisible;
    protected FastScrollRecyclerView mRv;
    protected int mRvOffsetY;
    private final float mScrollbarLeftOffsetTouchDelegate;
    private Insets mSystemGestureInsets;
    private final RectF mThumbBounds;
    private final Point mThumbDrawOffset;
    protected final int mThumbHeight;
    protected int mThumbOffsetY;
    private final int mThumbPadding;
    private final Paint mThumbPaint;
    protected int mTouchOffsetY;
    private final Paint mTrackPaint;
    /* access modifiers changed from: private */
    public int mWidth;
    private ObjectAnimator mWidthAnimator;

    public boolean hasOverlappingRendering() {
        return false;
    }

    public RecyclerViewFastScroller(Context context) {
        this(context, (AttributeSet) null);
    }

    public RecyclerViewFastScroller(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public RecyclerViewFastScroller(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mDy = 0;
        this.mThumbBounds = new RectF();
        this.mThumbDrawOffset = new Point();
        Paint paint = new Paint();
        this.mTrackPaint = paint;
        paint.setColor(Themes.getAttrColor(context, 16842806));
        paint.setAlpha(30);
        Paint paint2 = new Paint();
        this.mThumbPaint = paint2;
        paint2.setAntiAlias(true);
        paint2.setColor(Themes.getColorAccent(context));
        paint2.setStyle(Paint.Style.FILL);
        Resources resources = getResources();
        int dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.fastscroll_track_min_width);
        this.mMinWidth = dimensionPixelSize;
        this.mWidth = dimensionPixelSize;
        this.mMaxWidth = resources.getDimensionPixelSize(R.dimen.fastscroll_track_max_width);
        this.mThumbPadding = resources.getDimensionPixelSize(R.dimen.fastscroll_thumb_padding);
        this.mThumbHeight = resources.getDimensionPixelSize(R.dimen.fastscroll_thumb_height);
        this.mConfig = ViewConfiguration.get(context);
        this.mDeltaThreshold = resources.getDisplayMetrics().density * 4.0f;
        this.mScrollbarLeftOffsetTouchDelegate = resources.getDisplayMetrics().density * 5.0f;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.RecyclerViewFastScroller, i, 0);
        this.mCanThumbDetach = obtainStyledAttributes.getBoolean(0, false);
        obtainStyledAttributes.recycle();
    }

    public void setRecyclerView(FastScrollRecyclerView fastScrollRecyclerView, TextView textView) {
        RecyclerView.OnScrollListener onScrollListener;
        FastScrollRecyclerView fastScrollRecyclerView2 = this.mRv;
        if (!(fastScrollRecyclerView2 == null || (onScrollListener = this.mOnScrollListener) == null)) {
            fastScrollRecyclerView2.removeOnScrollListener(onScrollListener);
        }
        this.mRv = fastScrollRecyclerView;
        AnonymousClass2 r0 = new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                int unused = RecyclerViewFastScroller.this.mDy = i2;
                RecyclerViewFastScroller.this.mRv.onUpdateScrollbar(i2);
            }
        };
        this.mOnScrollListener = r0;
        fastScrollRecyclerView.addOnScrollListener(r0);
        this.mPopupView = textView;
        textView.setBackground(new FastScrollThumbDrawable(this.mThumbPaint, Utilities.isRtl(getResources())));
    }

    public void reattachThumbToScroll() {
        this.mIsThumbDetached = false;
    }

    public void setThumbOffsetY(int i) {
        if (this.mThumbOffsetY == i) {
            if (this.mRvOffsetY != this.mRv.getCurrentScrollY()) {
                this.mRvOffsetY = this.mRv.getCurrentScrollY();
                return;
            }
            return;
        }
        updatePopupY(i);
        this.mThumbOffsetY = i;
        invalidate();
        this.mRvOffsetY = this.mRv.getCurrentScrollY();
    }

    public int getThumbOffsetY() {
        return this.mThumbOffsetY;
    }

    /* access modifiers changed from: private */
    public void setTrackWidth(int i) {
        if (this.mWidth != i) {
            this.mWidth = i;
            invalidate();
        }
    }

    public int getThumbHeight() {
        return this.mThumbHeight;
    }

    public boolean isDraggingThumb() {
        return this.mIsDragging;
    }

    public boolean isThumbDetached() {
        return this.mIsThumbDetached;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x001e, code lost:
        if (r7 != 3) goto L_0x00c2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean handleTouchEvent(android.view.MotionEvent r6, android.graphics.Point r7) {
        /*
            r5 = this;
            float r0 = r6.getX()
            int r0 = (int) r0
            int r1 = r7.x
            int r0 = r0 - r1
            float r1 = r6.getY()
            int r1 = (int) r1
            int r7 = r7.y
            int r1 = r1 - r7
            int r7 = r6.getAction()
            if (r7 == 0) goto L_0x008f
            r2 = 1
            r3 = 0
            if (r7 == r2) goto L_0x0076
            r4 = 2
            if (r7 == r4) goto L_0x0022
            r6 = 3
            if (r7 == r6) goto L_0x0076
            goto L_0x00c2
        L_0x0022:
            r5.mLastY = r1
            int r7 = r5.mDownY
            int r7 = r1 - r7
            int r7 = java.lang.Math.abs(r7)
            int r4 = r5.mDownX
            int r0 = r0 - r4
            java.lang.Math.abs(r0)
            boolean r0 = r5.mIgnoreDragGesture
            android.view.ViewConfiguration r4 = r5.mConfig
            int r4 = r4.getScaledPagingTouchSlop()
            if (r7 <= r4) goto L_0x003d
            goto L_0x003e
        L_0x003d:
            r2 = r3
        L_0x003e:
            r7 = r0 | r2
            r5.mIgnoreDragGesture = r7
            boolean r0 = r5.mIsDragging
            if (r0 != 0) goto L_0x006e
            if (r7 != 0) goto L_0x006e
            com.android.launcher3.FastScrollRecyclerView r7 = r5.mRv
            boolean r7 = r7.supportsFastScrolling()
            if (r7 == 0) goto L_0x006e
            int r7 = r5.mDownX
            int r0 = r5.mLastY
            boolean r7 = r5.isNearThumb(r7, r0)
            if (r7 == 0) goto L_0x006e
            long r6 = r6.getEventTime()
            long r2 = r5.mDownTimeStampMillis
            long r6 = r6 - r2
            r2 = 40
            int r6 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r6 <= 0) goto L_0x006e
            int r6 = r5.mDownY
            int r7 = r5.mLastY
            r5.calcTouchOffsetAndPrepToFastScroll(r6, r7)
        L_0x006e:
            boolean r6 = r5.mIsDragging
            if (r6 == 0) goto L_0x00c2
            r5.updateFastScrollSectionNameAndThumbOffset(r1)
            goto L_0x00c2
        L_0x0076:
            com.android.launcher3.FastScrollRecyclerView r6 = r5.mRv
            r6.onFastScrollCompleted()
            r5.mTouchOffsetY = r3
            r6 = 0
            r5.mLastTouchY = r6
            r5.mIgnoreDragGesture = r3
            boolean r6 = r5.mIsDragging
            if (r6 == 0) goto L_0x00c2
            r5.mIsDragging = r3
            r5.animatePopupVisibility(r3)
            r5.showActiveScrollbar(r3)
            goto L_0x00c2
        L_0x008f:
            r5.mDownX = r0
            r5.mLastY = r1
            r5.mDownY = r1
            long r6 = r6.getDownTime()
            r5.mDownTimeStampMillis = r6
            int r6 = r5.mDy
            int r6 = java.lang.Math.abs(r6)
            float r6 = (float) r6
            float r7 = r5.mDeltaThreshold
            int r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
            if (r6 >= 0) goto L_0x00b5
            com.android.launcher3.FastScrollRecyclerView r6 = r5.mRv
            int r6 = r6.getScrollState()
            if (r6 == 0) goto L_0x00b5
            com.android.launcher3.FastScrollRecyclerView r6 = r5.mRv
            r6.stopScroll()
        L_0x00b5:
            boolean r6 = r5.isNearThumb(r0, r1)
            if (r6 == 0) goto L_0x00c2
            int r6 = r5.mDownY
            int r7 = r5.mThumbOffsetY
            int r6 = r6 - r7
            r5.mTouchOffsetY = r6
        L_0x00c2:
            boolean r6 = r5.mIsDragging
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.views.RecyclerViewFastScroller.handleTouchEvent(android.view.MotionEvent, android.graphics.Point):boolean");
    }

    private void calcTouchOffsetAndPrepToFastScroll(int i, int i2) {
        UiThreadHelper.hideKeyboardAsync((ActivityContext) ActivityContext.lookupContext(getContext()), getWindowToken());
        this.mIsDragging = true;
        if (this.mCanThumbDetach) {
            this.mIsThumbDetached = true;
        }
        this.mTouchOffsetY += i2 - i;
        animatePopupVisibility(true);
        showActiveScrollbar(true);
    }

    private void updateFastScrollSectionNameAndThumbOffset(int i) {
        int scrollbarTrackHeight = this.mRv.getScrollbarTrackHeight() - this.mThumbHeight;
        float max = (float) Math.max(0, Math.min(scrollbarTrackHeight, i - this.mTouchOffsetY));
        String scrollToPositionAtProgress = this.mRv.scrollToPositionAtProgress(max / ((float) scrollbarTrackHeight));
        if (!scrollToPositionAtProgress.equals(this.mPopupSectionName)) {
            this.mPopupSectionName = scrollToPositionAtProgress;
            this.mPopupView.setText(scrollToPositionAtProgress);
            performHapticFeedback(4);
        }
        animatePopupVisibility(!scrollToPositionAtProgress.isEmpty());
        this.mLastTouchY = max;
        setThumbOffsetY((int) max);
    }

    public void onDraw(Canvas canvas) {
        if (this.mThumbOffsetY >= 0) {
            int save = canvas.save();
            canvas.translate((float) (getWidth() / 2), (float) this.mRv.getScrollBarTop());
            this.mThumbDrawOffset.set(getWidth() / 2, this.mRv.getScrollBarTop());
            float f = (float) (this.mWidth / 2);
            float scrollbarTrackHeight = (float) this.mRv.getScrollbarTrackHeight();
            int i = this.mWidth;
            canvas.drawRoundRect(-f, 0.0f, f, scrollbarTrackHeight, (float) i, (float) i, this.mTrackPaint);
            canvas.translate(0.0f, (float) this.mThumbOffsetY);
            this.mThumbDrawOffset.y += this.mThumbOffsetY;
            float f2 = f + ((float) this.mThumbPadding);
            float scrollThumbRadius = getScrollThumbRadius();
            this.mThumbBounds.set(-f2, 0.0f, f2, (float) this.mThumbHeight);
            canvas.drawRoundRect(this.mThumbBounds, scrollThumbRadius, scrollThumbRadius, this.mThumbPaint);
            if (Utilities.ATLEAST_Q) {
                RectF rectF = this.mThumbBounds;
                List<Rect> list = SYSTEM_GESTURE_EXCLUSION_RECT;
                rectF.roundOut(list.get(0));
                list.get(0).offset(this.mThumbDrawOffset.x, this.mThumbDrawOffset.y);
                if (Utilities.ATLEAST_Q && this.mSystemGestureInsets != null) {
                    list.get(0).left = list.get(0).right - this.mSystemGestureInsets.right;
                }
                setSystemGestureExclusionRects(list);
            }
            canvas.restoreToCount(save);
        }
    }

    public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        if (Utilities.ATLEAST_Q) {
            this.mSystemGestureInsets = windowInsets.getSystemGestureInsets();
        }
        return super.onApplyWindowInsets(windowInsets);
    }

    private float getScrollThumbRadius() {
        int i = this.mWidth;
        int i2 = this.mThumbPadding;
        return (float) (i + i2 + i2);
    }

    private void showActiveScrollbar(boolean z) {
        ObjectAnimator objectAnimator = this.mWidthAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        Property<RecyclerViewFastScroller, Integer> property = TRACK_WIDTH;
        int[] iArr = new int[1];
        iArr[0] = z ? this.mMaxWidth : this.mMinWidth;
        ObjectAnimator ofInt = ObjectAnimator.ofInt(this, property, iArr);
        this.mWidthAnimator = ofInt;
        ofInt.setDuration(150);
        this.mWidthAnimator.start();
    }

    private boolean isNearThumb(int i, int i2) {
        int i3 = i2 - this.mThumbOffsetY;
        return i >= 0 && i < getWidth() && i3 >= 0 && i3 <= this.mThumbHeight;
    }

    public boolean shouldBlockIntercept(int i, int i2) {
        return isNearThumb(i, i2);
    }

    public boolean isNearScrollBar(int i) {
        return ((float) i) >= ((float) ((getWidth() - this.mMaxWidth) / 2)) - this.mScrollbarLeftOffsetTouchDelegate && i <= (getWidth() + this.mMaxWidth) / 2;
    }

    private void animatePopupVisibility(boolean z) {
        if (this.mPopupVisible != z) {
            this.mPopupVisible = z;
            this.mPopupView.animate().cancel();
            this.mPopupView.animate().alpha(z ? 1.0f : 0.0f).setDuration(z ? 200 : 150).start();
        }
    }

    private void updatePopupY(int i) {
        int height = this.mPopupView.getHeight();
        this.mPopupView.setTranslationY(Utilities.boundToRange((((float) (this.mRv.getScrollBarTop() + i)) + (getScrollThumbRadius() / 2.0f)) - (((float) height) / 2.0f), 0.0f, (float) (((getTop() + this.mRv.getScrollBarTop()) + this.mRv.getScrollbarTrackHeight()) - height)));
    }

    public boolean isHitInParent(float f, float f2, Point point) {
        if (this.mThumbOffsetY < 0) {
            return false;
        }
        Rect rect = sTempRect;
        getHitRect(rect);
        rect.top += this.mRv.getScrollBarTop();
        if (point != null) {
            point.set(rect.left, rect.top);
        }
        return rect.contains((int) f, (int) f2);
    }
}
