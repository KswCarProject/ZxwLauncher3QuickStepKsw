package com.android.launcher3.widget;

import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.IntProperty;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.R;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.widget.util.WidgetsTableUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WidgetsBottomSheet extends BaseWidgetSheet {
    private static final int DEFAULT_CLOSE_DURATION = 200;
    private static final long EDUCATION_TIP_DELAY_MS = 300;
    private static final IntProperty<View> PADDING_BOTTOM = new IntProperty<View>("paddingBottom") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Integer) obj2);
        }

        public void setValue(View view, int i) {
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), i);
        }

        public Integer get(View view) {
            return Integer.valueOf(view.getPaddingBottom());
        }
    };
    private static final String TAG = "WidgetsBottomSheet";
    private final View.OnLayoutChangeListener mLayoutChangeListenerToShowTips;
    private int mMaxHorizontalSpan;
    private ItemInfo mOriginalItemInfo;
    /* access modifiers changed from: private */
    public final Runnable mShowEducationTipTask;
    private final int mWidgetCellHorizontalPadding;

    /* access modifiers changed from: protected */
    public boolean isOfType(int i) {
        return (i & 4) != 0;
    }

    public /* synthetic */ void lambda$new$0$WidgetsBottomSheet() {
        if (hasSeenEducationTip()) {
            removeOnLayoutChangeListener(this.mLayoutChangeListenerToShowTips);
        } else if (showEducationTipOnViewIfPossible(((ViewGroup) ((TableLayout) findViewById(R.id.widgets_table)).getChildAt(0)).getChildAt(0)) != null) {
            removeOnLayoutChangeListener(this.mLayoutChangeListenerToShowTips);
        }
    }

    public WidgetsBottomSheet(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public WidgetsBottomSheet(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mMaxHorizontalSpan = 4;
        AnonymousClass2 r1 = new View.OnLayoutChangeListener() {
            public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                if (WidgetsBottomSheet.this.hasSeenEducationTip()) {
                    WidgetsBottomSheet.this.removeOnLayoutChangeListener(this);
                    return;
                }
                WidgetsBottomSheet widgetsBottomSheet = WidgetsBottomSheet.this;
                widgetsBottomSheet.removeCallbacks(widgetsBottomSheet.mShowEducationTipTask);
                WidgetsBottomSheet widgetsBottomSheet2 = WidgetsBottomSheet.this;
                widgetsBottomSheet2.postDelayed(widgetsBottomSheet2.mShowEducationTipTask, 300);
            }
        };
        this.mLayoutChangeListenerToShowTips = r1;
        this.mShowEducationTipTask = new Runnable() {
            public final void run() {
                WidgetsBottomSheet.this.lambda$new$0$WidgetsBottomSheet();
            }
        };
        setWillNotDraw(false);
        if (!hasSeenEducationTip()) {
            addOnLayoutChangeListener(r1);
        }
        this.mWidgetCellHorizontalPadding = getResources().getDimensionPixelSize(R.dimen.widget_cell_horizontal_padding);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mContent = (ViewGroup) findViewById(R.id.widgets_bottom_sheet);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        doMeasure(i, i2);
        if (updateMaxSpansPerRow()) {
            doMeasure(i, i2);
        }
    }

    private boolean updateMaxSpansPerRow() {
        int computeMaxHorizontalSpans;
        if (getMeasuredWidth() == 0 || this.mMaxHorizontalSpan == (computeMaxHorizontalSpans = computeMaxHorizontalSpans(this.mContent, this.mWidgetCellHorizontalPadding))) {
            return false;
        }
        this.mMaxHorizontalSpan = computeMaxHorizontalSpans;
        onWidgetsBound();
        return true;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5 = i4 - i2;
        int measuredWidth = this.mContent.getMeasuredWidth();
        int i6 = (((((i3 - i) - measuredWidth) - this.mInsets.left) - this.mInsets.right) / 2) + this.mInsets.left;
        this.mContent.layout(i6, i5 - this.mContent.getMeasuredHeight(), measuredWidth + i6, i5);
        setTranslationShift(this.mTranslationShift);
        if (((TableLayout) findViewById(R.id.widgets_table)).getMeasuredHeight() > ((ScrollView) findViewById(R.id.widgets_table_scroll_view)).getMeasuredHeight()) {
            findViewById(R.id.collapse_handle).setVisibility(0);
        }
    }

    public void populateAndShow(ItemInfo itemInfo) {
        this.mOriginalItemInfo = itemInfo;
        ((TextView) findViewById(R.id.title)).setText(this.mOriginalItemInfo.title);
        onWidgetsBound();
        attachToContainer();
        this.mIsOpen = false;
        animateOpen();
    }

    public void onWidgetsBound() {
        List<WidgetItem> widgetsForPackageUser = ((Launcher) this.mActivityContext).getPopupDataProvider().getWidgetsForPackageUser(new PackageUserKey(this.mOriginalItemInfo.getTargetComponent().getPackageName(), this.mOriginalItemInfo.user));
        TableLayout tableLayout = (TableLayout) findViewById(R.id.widgets_table);
        tableLayout.removeAllViews();
        WidgetsTableUtils.groupWidgetItemsIntoTableWithReordering(widgetsForPackageUser, this.mMaxHorizontalSpan).forEach(new Consumer(tableLayout) {
            public final /* synthetic */ TableLayout f$1;

            {
                this.f$1 = r2;
            }

            public final void accept(Object obj) {
                WidgetsBottomSheet.this.lambda$onWidgetsBound$2$WidgetsBottomSheet(this.f$1, (ArrayList) obj);
            }
        });
    }

    public /* synthetic */ void lambda$onWidgetsBound$2$WidgetsBottomSheet(TableLayout tableLayout, ArrayList arrayList) {
        TableRow tableRow = new TableRow(getContext());
        tableRow.setGravity(48);
        arrayList.forEach(new Consumer(tableRow) {
            public final /* synthetic */ TableRow f$1;

            {
                this.f$1 = r2;
            }

            public final void accept(Object obj) {
                WidgetsBottomSheet.this.lambda$onWidgetsBound$1$WidgetsBottomSheet(this.f$1, (WidgetItem) obj);
            }
        });
        tableLayout.addView(tableRow);
    }

    public /* synthetic */ void lambda$onWidgetsBound$1$WidgetsBottomSheet(TableRow tableRow, WidgetItem widgetItem) {
        addItemCell(tableRow).applyFromCellItem(widgetItem);
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            this.mNoIntercept = false;
            ScrollView scrollView = (ScrollView) findViewById(R.id.widgets_table_scroll_view);
            if (getPopupContainer().isEventOverView(scrollView, motionEvent) && scrollView.getScrollY() > 0) {
                this.mNoIntercept = true;
            }
        }
        return super.onControllerInterceptTouchEvent(motionEvent);
    }

    /* access modifiers changed from: protected */
    public WidgetCell addItemCell(ViewGroup viewGroup) {
        WidgetCell widgetCell = (WidgetCell) LayoutInflater.from(getContext()).inflate(R.layout.widget_cell, viewGroup, false);
        View findViewById = widgetCell.findViewById(R.id.widget_preview_container);
        findViewById.setOnClickListener(this);
        findViewById.setOnLongClickListener(this);
        widgetCell.setAnimatePreview(false);
        widgetCell.setSourceContainer(LauncherSettings.Favorites.CONTAINER_BOTTOM_WIDGETS_TRAY);
        viewGroup.addView(widgetCell);
        return widgetCell;
    }

    private void animateOpen() {
        if (!this.mIsOpen && !this.mOpenCloseAnimator.isRunning()) {
            this.mIsOpen = true;
            setupNavBarColor();
            this.mOpenCloseAnimator.setValues(new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(TRANSLATION_SHIFT, new float[]{0.0f})});
            this.mOpenCloseAnimator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
            this.mOpenCloseAnimator.start();
        }
    }

    /* access modifiers changed from: protected */
    public void handleClose(boolean z) {
        handleClose(z, 200);
    }

    public void setInsets(Rect rect) {
        super.setInsets(rect);
        int max = Math.max(rect.bottom, this.mNavBarScrimHeight);
        this.mContent.setPadding(this.mContent.getPaddingStart(), this.mContent.getPaddingTop(), this.mContent.getPaddingEnd(), max);
        if (max > 0) {
            setupNavBarColor();
        } else {
            clearNavBarColor();
        }
    }

    /* access modifiers changed from: protected */
    public void onContentHorizontalMarginChanged(int i) {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) findViewById(R.id.widgets_table).getLayoutParams();
        marginLayoutParams.setMarginStart(i);
        marginLayoutParams.setMarginEnd(i);
    }

    /* access modifiers changed from: protected */
    public Pair<View, String> getAccessibilityTarget() {
        return Pair.create(findViewById(R.id.title), getContext().getString(this.mIsOpen ? R.string.widgets_list : R.string.widgets_list_closed));
    }

    public void addHintCloseAnim(float f, Interpolator interpolator, PendingAnimation pendingAnimation) {
        pendingAnimation.setInt(this, PADDING_BOTTOM, (int) (f + ((float) this.mInsets.bottom)), interpolator);
    }
}
