package com.android.launcher3.appprediction;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import androidx.core.content.ContextCompat;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.R;
import com.android.launcher3.allapps.FloatingHeaderRow;
import com.android.launcher3.allapps.FloatingHeaderView;
import com.android.launcher3.util.OnboardingPrefs;
import com.android.launcher3.util.Themes;
import com.android.launcher3.views.ActivityContext;

public class AppsDividerView extends View implements FloatingHeaderRow {
    private Layout mAllAppsLabelLayout;
    private final int mAllAppsLabelTextColor;
    private final int[] mDividerSize;
    private DividerType mDividerType;
    private boolean mIsScrolledOut;
    private final TextPaint mPaint;
    private FloatingHeaderView mParent;
    private FloatingHeaderRow[] mRows;
    private boolean mShowAllAppsLabel;
    private final int mStrokeColor;
    private boolean mTabsHidden;

    public enum DividerType {
        NONE,
        LINE,
        ALL_APPS_LABEL
    }

    public View getFocusedChild() {
        return null;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public boolean hasVisibleContent() {
        return false;
    }

    public AppsDividerView(Context context) {
        this(context, (AttributeSet) null);
    }

    public AppsDividerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AppsDividerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mPaint = new TextPaint();
        this.mDividerType = DividerType.NONE;
        this.mRows = FloatingHeaderRow.NO_ROWS;
        this.mIsScrolledOut = false;
        boolean attrBoolean = Themes.getAttrBoolean(context, R.attr.isMainColorDark);
        this.mDividerSize = new int[]{getResources().getDimensionPixelSize(R.dimen.all_apps_divider_width), getResources().getDimensionPixelSize(R.dimen.all_apps_divider_height)};
        this.mStrokeColor = ContextCompat.getColor(context, attrBoolean ? R.color.all_apps_prediction_row_separator_dark : R.color.all_apps_prediction_row_separator);
        this.mAllAppsLabelTextColor = ContextCompat.getColor(context, attrBoolean ? R.color.all_apps_label_text_dark : R.color.all_apps_label_text);
        this.mShowAllAppsLabel = !((ActivityContext) ActivityContext.lookupContext(getContext())).getOnboardingPrefs().hasReachedMaxCount(OnboardingPrefs.ALL_APPS_VISITED_COUNT);
    }

    public void setup(FloatingHeaderView floatingHeaderView, FloatingHeaderRow[] floatingHeaderRowArr, boolean z) {
        this.mParent = floatingHeaderView;
        this.mTabsHidden = z;
        this.mRows = floatingHeaderRowArr;
        updateDividerType();
    }

    public void setShowAllAppsLabel(boolean z) {
        if (z != this.mShowAllAppsLabel) {
            this.mShowAllAppsLabel = z;
            updateDividerType();
        }
    }

    public int getExpectedHeight() {
        return getPaddingTop() + getPaddingBottom();
    }

    public boolean shouldDraw() {
        return this.mDividerType != DividerType.NONE;
    }

    private void updateDividerType() {
        DividerType dividerType;
        int i;
        int i2 = 0;
        if (!this.mTabsHidden) {
            dividerType = DividerType.NONE;
        } else {
            int i3 = 0;
            for (FloatingHeaderRow floatingHeaderRow : this.mRows) {
                if (floatingHeaderRow == this) {
                    break;
                }
                if (floatingHeaderRow.shouldDraw()) {
                    i3++;
                }
            }
            if (this.mShowAllAppsLabel && i3 > 0) {
                dividerType = DividerType.ALL_APPS_LABEL;
            } else if (i3 == 1) {
                dividerType = DividerType.LINE;
            } else {
                dividerType = DividerType.NONE;
            }
        }
        if (this.mDividerType != dividerType) {
            this.mDividerType = dividerType;
            int i4 = AnonymousClass1.$SwitchMap$com$android$launcher3$appprediction$AppsDividerView$DividerType[dividerType.ordinal()];
            if (i4 == 1) {
                i = getResources().getDimensionPixelSize(R.dimen.all_apps_prediction_row_divider_height);
                this.mPaint.setColor(this.mStrokeColor);
            } else if (i4 != 2) {
                i = 0;
            } else {
                i2 = getAllAppsLabelLayout().getHeight() + getResources().getDimensionPixelSize(R.dimen.all_apps_label_top_padding);
                i = getResources().getDimensionPixelSize(R.dimen.all_apps_label_bottom_padding);
                this.mPaint.setColor(this.mAllAppsLabelTextColor);
            }
            setPadding(getPaddingLeft(), i2, getPaddingRight(), i);
            updateViewVisibility();
            invalidate();
            requestLayout();
            FloatingHeaderView floatingHeaderView = this.mParent;
            if (floatingHeaderView != null) {
                floatingHeaderView.onHeightUpdated();
            }
        }
    }

    /* renamed from: com.android.launcher3.appprediction.AppsDividerView$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$launcher3$appprediction$AppsDividerView$DividerType;

        /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|(3:5|6|8)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        static {
            /*
                com.android.launcher3.appprediction.AppsDividerView$DividerType[] r0 = com.android.launcher3.appprediction.AppsDividerView.DividerType.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$android$launcher3$appprediction$AppsDividerView$DividerType = r0
                com.android.launcher3.appprediction.AppsDividerView$DividerType r1 = com.android.launcher3.appprediction.AppsDividerView.DividerType.LINE     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$com$android$launcher3$appprediction$AppsDividerView$DividerType     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.launcher3.appprediction.AppsDividerView$DividerType r1 = com.android.launcher3.appprediction.AppsDividerView.DividerType.ALL_APPS_LABEL     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = $SwitchMap$com$android$launcher3$appprediction$AppsDividerView$DividerType     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.launcher3.appprediction.AppsDividerView$DividerType r1 = com.android.launcher3.appprediction.AppsDividerView.DividerType.NONE     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.appprediction.AppsDividerView.AnonymousClass1.<clinit>():void");
        }
    }

    private void updateViewVisibility() {
        int i;
        if (this.mDividerType == DividerType.NONE) {
            i = 8;
        } else {
            i = this.mIsScrolledOut ? 4 : 0;
        }
        setVisibility(i);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.mDividerType == DividerType.LINE) {
            int width = (getWidth() - this.mDividerSize[0]) / 2;
            int height = getHeight() - (getPaddingBottom() / 2);
            int[] iArr = this.mDividerSize;
            float f = (float) iArr[1];
            canvas.drawRoundRect((float) width, (float) height, (float) (width + iArr[0]), (float) (height + iArr[1]), f, f, this.mPaint);
        } else if (this.mDividerType == DividerType.ALL_APPS_LABEL) {
            Layout allAppsLabelLayout = getAllAppsLabelLayout();
            int width2 = (getWidth() / 2) - (allAppsLabelLayout.getWidth() / 2);
            int height2 = (getHeight() - getPaddingBottom()) - allAppsLabelLayout.getHeight();
            canvas.translate((float) width2, (float) height2);
            allAppsLabelLayout.draw(canvas);
            canvas.translate((float) (-width2), (float) (-height2));
        }
    }

    private Layout getAllAppsLabelLayout() {
        if (this.mAllAppsLabelLayout == null) {
            this.mPaint.setAntiAlias(true);
            this.mPaint.setTypeface(Typeface.create("google-sans", 0));
            this.mPaint.setTextSize((float) getResources().getDimensionPixelSize(R.dimen.all_apps_label_text_size));
            CharSequence text = getResources().getText(R.string.all_apps_label);
            int length = text.length();
            TextPaint textPaint = this.mPaint;
            this.mAllAppsLabelLayout = StaticLayout.Builder.obtain(text, 0, length, textPaint, Math.round(textPaint.measureText(text.toString()))).setAlignment(Layout.Alignment.ALIGN_CENTER).setMaxLines(1).setIncludePad(true).build();
        }
        return this.mAllAppsLabelLayout;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), i), getPaddingBottom() + getPaddingTop());
    }

    public void setInsets(Rect rect, DeviceProfile deviceProfile) {
        int i = deviceProfile.allAppsLeftRightPadding;
        setPadding(i, getPaddingTop(), i, getPaddingBottom());
    }

    public void setVerticalScroll(int i, boolean z) {
        setTranslationY((float) i);
        this.mIsScrolledOut = z;
        updateViewVisibility();
    }

    public Class<AppsDividerView> getTypeClass() {
        return AppsDividerView.class;
    }
}
