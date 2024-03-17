package androidx.slice.builders;

import android.app.PendingIntent;
import android.content.Context;
import android.net.Uri;
import androidx.core.graphics.drawable.IconCompat;
import androidx.core.util.Consumer;
import androidx.core.util.Pair;
import androidx.slice.Slice;
import androidx.slice.SliceSpecs;
import androidx.slice.builders.impl.ListBuilderBasicImpl;
import androidx.slice.builders.impl.ListBuilderV1Impl;
import androidx.slice.builders.impl.TemplateBuilderImpl;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListBuilder extends TemplateSliceBuilder {
    public static final int ICON_IMAGE = 0;
    public static final long INFINITY = -1;
    public static final int LARGE_IMAGE = 2;
    public static final int SMALL_IMAGE = 1;
    public static final int UNKNOWN_IMAGE = 3;
    private boolean mHasSeeMore;
    private androidx.slice.builders.impl.ListBuilder mImpl;

    @Retention(RetentionPolicy.SOURCE)
    public @interface ImageMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface LayoutDirection {
    }

    public ListBuilder(Context context, Uri uri) {
        super(context, uri);
    }

    public ListBuilder(Context context, Uri uri, long j) {
        super(context, uri);
        this.mImpl.setTtl(j);
    }

    public ListBuilder(Context context, Uri uri, Duration duration) {
        super(context, uri);
        this.mImpl.setTtl(duration);
    }

    public Slice build() {
        return ((TemplateBuilderImpl) this.mImpl).build();
    }

    /* access modifiers changed from: package-private */
    public void setImpl(TemplateBuilderImpl templateBuilderImpl) {
        this.mImpl = (androidx.slice.builders.impl.ListBuilder) templateBuilderImpl;
    }

    public ListBuilder addRow(RowBuilder rowBuilder) {
        this.mImpl.addRow(rowBuilder);
        return this;
    }

    public ListBuilder addRow(Consumer<RowBuilder> consumer) {
        RowBuilder rowBuilder = new RowBuilder(this);
        consumer.accept(rowBuilder);
        return addRow(rowBuilder);
    }

    public ListBuilder addGridRow(GridRowBuilder gridRowBuilder) {
        this.mImpl.addGridRow(gridRowBuilder);
        return this;
    }

    public ListBuilder addGridRow(Consumer<GridRowBuilder> consumer) {
        GridRowBuilder gridRowBuilder = new GridRowBuilder(this);
        consumer.accept(gridRowBuilder);
        return addGridRow(gridRowBuilder);
    }

    public ListBuilder setHeader(HeaderBuilder headerBuilder) {
        this.mImpl.setHeader(headerBuilder);
        return this;
    }

    public ListBuilder setHeader(Consumer<HeaderBuilder> consumer) {
        HeaderBuilder headerBuilder = new HeaderBuilder(this);
        consumer.accept(headerBuilder);
        return setHeader(headerBuilder);
    }

    public ListBuilder addAction(SliceAction sliceAction) {
        this.mImpl.addAction(sliceAction);
        return this;
    }

    public ListBuilder setColor(int i) {
        return setAccentColor(i);
    }

    public ListBuilder setAccentColor(int i) {
        this.mImpl.setColor(i);
        return this;
    }

    public ListBuilder setKeywords(List<String> list) {
        if (list != null) {
            this.mImpl.setKeywords(new HashSet(list));
        }
        return this;
    }

    public ListBuilder setKeywords(Set<String> set) {
        this.mImpl.setKeywords(set);
        return this;
    }

    public ListBuilder setLayoutDirection(int i) {
        this.mImpl.setLayoutDirection(i);
        return this;
    }

    public ListBuilder setSeeMoreRow(RowBuilder rowBuilder) {
        if (!this.mHasSeeMore) {
            this.mImpl.setSeeMoreRow(rowBuilder);
            this.mHasSeeMore = true;
            return this;
        }
        throw new IllegalArgumentException("Trying to add see more row when one has already been added");
    }

    public ListBuilder setSeeMoreRow(Consumer<RowBuilder> consumer) {
        RowBuilder rowBuilder = new RowBuilder(this);
        consumer.accept(rowBuilder);
        if (!this.mHasSeeMore) {
            this.mImpl.setSeeMoreRow(rowBuilder);
            this.mHasSeeMore = true;
            return this;
        }
        throw new IllegalArgumentException("Trying to add see more row when one has already been added");
    }

    public ListBuilder setSeeMoreAction(PendingIntent pendingIntent) {
        if (!this.mHasSeeMore) {
            this.mImpl.setSeeMoreAction(pendingIntent);
            this.mHasSeeMore = true;
            return this;
        }
        throw new IllegalArgumentException("Trying to add see more action when one has already been added");
    }

    public ListBuilder setIsError(boolean z) {
        this.mImpl.setIsError(z);
        return this;
    }

    /* access modifiers changed from: protected */
    public TemplateBuilderImpl selectImpl(Uri uri) {
        if (checkCompatible(SliceSpecs.LIST, uri)) {
            return new ListBuilderV1Impl(getBuilder(), SliceSpecs.LIST, getClock());
        }
        if (checkCompatible(SliceSpecs.BASIC, uri)) {
            return new ListBuilderBasicImpl(getBuilder(), SliceSpecs.BASIC);
        }
        return null;
    }

    public androidx.slice.builders.impl.ListBuilder getImpl() {
        return this.mImpl;
    }

    public ListBuilder addInputRange(InputRangeBuilder inputRangeBuilder) {
        this.mImpl.addInputRange(inputRangeBuilder);
        return this;
    }

    public ListBuilder addInputRange(Consumer<InputRangeBuilder> consumer) {
        InputRangeBuilder inputRangeBuilder = new InputRangeBuilder(this);
        consumer.accept(inputRangeBuilder);
        return addInputRange(inputRangeBuilder);
    }

    public ListBuilder addRange(RangeBuilder rangeBuilder) {
        this.mImpl.addRange(rangeBuilder);
        return this;
    }

    public ListBuilder addRange(Consumer<RangeBuilder> consumer) {
        RangeBuilder rangeBuilder = new RangeBuilder(this);
        consumer.accept(rangeBuilder);
        return addRange(rangeBuilder);
    }

    public static class RangeBuilder {
        private CharSequence mContentDescription;
        private int mLayoutDirection = -1;
        private int mMax = 100;
        private SliceAction mPrimaryAction;
        private CharSequence mSubtitle;
        private CharSequence mTitle;
        private int mValue;
        private boolean mValueSet = false;

        public RangeBuilder() {
        }

        public RangeBuilder(ListBuilder listBuilder) {
        }

        public RangeBuilder setMax(int i) {
            this.mMax = i;
            return this;
        }

        public RangeBuilder setValue(int i) {
            this.mValueSet = true;
            this.mValue = i;
            return this;
        }

        public RangeBuilder setTitle(CharSequence charSequence) {
            this.mTitle = charSequence;
            return this;
        }

        public RangeBuilder setSubtitle(CharSequence charSequence) {
            this.mSubtitle = charSequence;
            return this;
        }

        public RangeBuilder setPrimaryAction(SliceAction sliceAction) {
            this.mPrimaryAction = sliceAction;
            return this;
        }

        public RangeBuilder setContentDescription(CharSequence charSequence) {
            this.mContentDescription = charSequence;
            return this;
        }

        public RangeBuilder setLayoutDirection(int i) {
            this.mLayoutDirection = i;
            return this;
        }

        public int getValue() {
            return this.mValue;
        }

        public int getMax() {
            return this.mMax;
        }

        public boolean isValueSet() {
            return this.mValueSet;
        }

        public CharSequence getTitle() {
            return this.mTitle;
        }

        public CharSequence getSubtitle() {
            return this.mSubtitle;
        }

        public SliceAction getPrimaryAction() {
            return this.mPrimaryAction;
        }

        public CharSequence getContentDescription() {
            return this.mContentDescription;
        }

        public int getLayoutDirection() {
            return this.mLayoutDirection;
        }
    }

    public static class InputRangeBuilder {
        private PendingIntent mAction;
        private CharSequence mContentDescription;
        private PendingIntent mInputAction;
        private int mLayoutDirection = -1;
        private int mMax = 100;
        private int mMin = 0;
        private SliceAction mPrimaryAction;
        private CharSequence mSubtitle;
        private IconCompat mThumb;
        private CharSequence mTitle;
        private int mValue = 0;
        private boolean mValueSet = false;

        public InputRangeBuilder() {
        }

        public InputRangeBuilder(ListBuilder listBuilder) {
        }

        public InputRangeBuilder setMin(int i) {
            this.mMin = i;
            return this;
        }

        public InputRangeBuilder setMax(int i) {
            this.mMax = i;
            return this;
        }

        public InputRangeBuilder setValue(int i) {
            this.mValueSet = true;
            this.mValue = i;
            return this;
        }

        public InputRangeBuilder setTitle(CharSequence charSequence) {
            this.mTitle = charSequence;
            return this;
        }

        public InputRangeBuilder setSubtitle(CharSequence charSequence) {
            this.mSubtitle = charSequence;
            return this;
        }

        public InputRangeBuilder setInputAction(PendingIntent pendingIntent) {
            this.mInputAction = pendingIntent;
            return this;
        }

        public InputRangeBuilder setThumb(IconCompat iconCompat) {
            this.mThumb = iconCompat;
            return this;
        }

        public InputRangeBuilder setPrimaryAction(SliceAction sliceAction) {
            this.mPrimaryAction = sliceAction;
            return this;
        }

        public InputRangeBuilder setContentDescription(CharSequence charSequence) {
            this.mContentDescription = charSequence;
            return this;
        }

        public InputRangeBuilder setLayoutDirection(int i) {
            this.mLayoutDirection = i;
            return this;
        }

        public int getMin() {
            return this.mMin;
        }

        public int getMax() {
            return this.mMax;
        }

        public int getValue() {
            return this.mValue;
        }

        public boolean isValueSet() {
            return this.mValueSet;
        }

        public CharSequence getTitle() {
            return this.mTitle;
        }

        public CharSequence getSubtitle() {
            return this.mSubtitle;
        }

        public PendingIntent getAction() {
            return this.mAction;
        }

        public PendingIntent getInputAction() {
            return this.mInputAction;
        }

        public IconCompat getThumb() {
            return this.mThumb;
        }

        public SliceAction getPrimaryAction() {
            return this.mPrimaryAction;
        }

        public CharSequence getContentDescription() {
            return this.mContentDescription;
        }

        public int getLayoutDirection() {
            return this.mLayoutDirection;
        }
    }

    public static class RowBuilder {
        public static final int TYPE_ACTION = 2;
        public static final int TYPE_ICON = 1;
        public static final int TYPE_TIMESTAMP = 0;
        private CharSequence mContentDescription;
        private List<Object> mEndItems;
        private List<Boolean> mEndLoads;
        private List<Integer> mEndTypes;
        private boolean mHasDefaultToggle;
        private boolean mHasEndActionOrToggle;
        private boolean mHasEndImage;
        private boolean mHasTimestamp;
        private int mLayoutDirection;
        private SliceAction mPrimaryAction;
        private CharSequence mSubtitle;
        private boolean mSubtitleLoading;
        private long mTimeStamp;
        private CharSequence mTitle;
        private SliceAction mTitleAction;
        private boolean mTitleActionLoading;
        private IconCompat mTitleIcon;
        private int mTitleImageMode;
        private boolean mTitleItemLoading;
        private boolean mTitleLoading;
        private final Uri mUri;

        public RowBuilder() {
            this.mTimeStamp = -1;
            this.mLayoutDirection = -1;
            this.mEndItems = new ArrayList();
            this.mEndTypes = new ArrayList();
            this.mEndLoads = new ArrayList();
            this.mUri = null;
        }

        public RowBuilder(Uri uri) {
            this.mTimeStamp = -1;
            this.mLayoutDirection = -1;
            this.mEndItems = new ArrayList();
            this.mEndTypes = new ArrayList();
            this.mEndLoads = new ArrayList();
            this.mUri = uri;
        }

        public RowBuilder(ListBuilder listBuilder) {
            this();
        }

        public RowBuilder(ListBuilder listBuilder, Uri uri) {
            this(uri);
        }

        public RowBuilder(Context context, Uri uri) {
            this(uri);
        }

        public RowBuilder setTitleItem(long j) {
            if (!this.mHasTimestamp) {
                this.mTimeStamp = j;
                this.mHasTimestamp = true;
                return this;
            }
            throw new IllegalArgumentException("Trying to add a timestamp when one has already been added");
        }

        public RowBuilder setTitleItem(IconCompat iconCompat, int i) {
            return setTitleItem(iconCompat, i, false);
        }

        public RowBuilder setTitleItem(IconCompat iconCompat, int i, boolean z) {
            this.mTitleAction = null;
            this.mTitleIcon = iconCompat;
            this.mTitleImageMode = i;
            this.mTitleItemLoading = z;
            return this;
        }

        public RowBuilder setTitleItem(SliceAction sliceAction) {
            return setTitleItem(sliceAction, false);
        }

        public RowBuilder setTitleItem(SliceAction sliceAction, boolean z) {
            this.mTitleAction = sliceAction;
            this.mTitleIcon = null;
            this.mTitleImageMode = 0;
            this.mTitleActionLoading = z;
            return this;
        }

        public RowBuilder setPrimaryAction(SliceAction sliceAction) {
            this.mPrimaryAction = sliceAction;
            return this;
        }

        public RowBuilder setTitle(CharSequence charSequence) {
            return setTitle(charSequence, false);
        }

        public RowBuilder setTitle(CharSequence charSequence, boolean z) {
            this.mTitle = charSequence;
            this.mTitleLoading = z;
            return this;
        }

        public RowBuilder setSubtitle(CharSequence charSequence) {
            return setSubtitle(charSequence, false);
        }

        public RowBuilder setSubtitle(CharSequence charSequence, boolean z) {
            this.mSubtitle = charSequence;
            this.mSubtitleLoading = z;
            return this;
        }

        public RowBuilder addEndItem(long j) {
            if (!this.mHasTimestamp) {
                this.mEndItems.add(Long.valueOf(j));
                this.mEndTypes.add(0);
                this.mEndLoads.add(false);
                this.mHasTimestamp = true;
                return this;
            }
            throw new IllegalArgumentException("Trying to add a timestamp when one has already been added");
        }

        public RowBuilder addEndItem(IconCompat iconCompat, int i) {
            return addEndItem(iconCompat, i, false);
        }

        public RowBuilder addEndItem(IconCompat iconCompat, int i, boolean z) {
            if (!this.mHasEndActionOrToggle) {
                this.mEndItems.add(new Pair(iconCompat, Integer.valueOf(i)));
                this.mEndTypes.add(1);
                this.mEndLoads.add(Boolean.valueOf(z));
                this.mHasEndImage = true;
                return this;
            }
            throw new IllegalArgumentException("Trying to add an icon to end items when anaction has already been added. End items cannot have a mixture of actions and icons.");
        }

        public RowBuilder addEndItem(SliceAction sliceAction) {
            return addEndItem(sliceAction, false);
        }

        public RowBuilder addEndItem(SliceAction sliceAction, boolean z) {
            if (this.mHasEndImage) {
                throw new IllegalArgumentException("Trying to add an action to end items when anicon has already been added. End items cannot have a mixture of actions and icons.");
            } else if (!this.mHasDefaultToggle) {
                this.mEndItems.add(sliceAction);
                this.mEndTypes.add(2);
                this.mEndLoads.add(Boolean.valueOf(z));
                this.mHasDefaultToggle = sliceAction.getImpl().isDefaultToggle();
                this.mHasEndActionOrToggle = true;
                return this;
            } else {
                throw new IllegalStateException("Only one non-custom toggle can be added in a single row. If you would like to include multiple toggles in a row, set a custom icon for each toggle.");
            }
        }

        public RowBuilder setContentDescription(CharSequence charSequence) {
            this.mContentDescription = charSequence;
            return this;
        }

        public RowBuilder setLayoutDirection(int i) {
            this.mLayoutDirection = i;
            return this;
        }

        public Uri getUri() {
            return this.mUri;
        }

        public boolean hasEndActionOrToggle() {
            return this.mHasEndActionOrToggle;
        }

        public boolean hasEndImage() {
            return this.mHasEndImage;
        }

        public boolean hasDefaultToggle() {
            return this.mHasDefaultToggle;
        }

        public boolean hasTimestamp() {
            return this.mHasTimestamp;
        }

        public long getTimeStamp() {
            return this.mTimeStamp;
        }

        public boolean isTitleItemLoading() {
            return this.mTitleItemLoading;
        }

        public int getTitleImageMode() {
            return this.mTitleImageMode;
        }

        public IconCompat getTitleIcon() {
            return this.mTitleIcon;
        }

        public SliceAction getTitleAction() {
            return this.mTitleAction;
        }

        public SliceAction getPrimaryAction() {
            return this.mPrimaryAction;
        }

        public CharSequence getTitle() {
            return this.mTitle;
        }

        public boolean isTitleLoading() {
            return this.mTitleLoading;
        }

        public CharSequence getSubtitle() {
            return this.mSubtitle;
        }

        public boolean isSubtitleLoading() {
            return this.mSubtitleLoading;
        }

        public CharSequence getContentDescription() {
            return this.mContentDescription;
        }

        public int getLayoutDirection() {
            return this.mLayoutDirection;
        }

        public List<Object> getEndItems() {
            return this.mEndItems;
        }

        public List<Integer> getEndTypes() {
            return this.mEndTypes;
        }

        public List<Boolean> getEndLoads() {
            return this.mEndLoads;
        }

        public boolean isTitleActionLoading() {
            return this.mTitleActionLoading;
        }
    }

    public static class HeaderBuilder {
        private CharSequence mContentDescription;
        private int mLayoutDirection;
        private SliceAction mPrimaryAction;
        private CharSequence mSubtitle;
        private boolean mSubtitleLoading;
        private CharSequence mSummary;
        private boolean mSummaryLoading;
        private CharSequence mTitle;
        private boolean mTitleLoading;
        private final Uri mUri;

        public HeaderBuilder() {
            this.mUri = null;
        }

        public HeaderBuilder(Uri uri) {
            this.mUri = uri;
        }

        public HeaderBuilder(ListBuilder listBuilder) {
            this();
        }

        public HeaderBuilder(ListBuilder listBuilder, Uri uri) {
            this(uri);
        }

        public HeaderBuilder setTitle(CharSequence charSequence) {
            return setTitle(charSequence, false);
        }

        public HeaderBuilder setTitle(CharSequence charSequence, boolean z) {
            this.mTitle = charSequence;
            this.mTitleLoading = z;
            return this;
        }

        public HeaderBuilder setSubtitle(CharSequence charSequence) {
            return setSubtitle(charSequence, false);
        }

        public HeaderBuilder setSubtitle(CharSequence charSequence, boolean z) {
            this.mSubtitle = charSequence;
            this.mSubtitleLoading = z;
            return this;
        }

        public HeaderBuilder setSummary(CharSequence charSequence) {
            return setSummary(charSequence, false);
        }

        public HeaderBuilder setSummary(CharSequence charSequence, boolean z) {
            this.mSummary = charSequence;
            this.mSummaryLoading = z;
            return this;
        }

        public HeaderBuilder setPrimaryAction(SliceAction sliceAction) {
            this.mPrimaryAction = sliceAction;
            return this;
        }

        public HeaderBuilder setContentDescription(CharSequence charSequence) {
            this.mContentDescription = charSequence;
            return this;
        }

        public HeaderBuilder setLayoutDirection(int i) {
            this.mLayoutDirection = i;
            return this;
        }

        public Uri getUri() {
            return this.mUri;
        }

        public CharSequence getTitle() {
            return this.mTitle;
        }

        public boolean isTitleLoading() {
            return this.mTitleLoading;
        }

        public CharSequence getSubtitle() {
            return this.mSubtitle;
        }

        public boolean isSubtitleLoading() {
            return this.mSubtitleLoading;
        }

        public CharSequence getSummary() {
            return this.mSummary;
        }

        public boolean isSummaryLoading() {
            return this.mSummaryLoading;
        }

        public SliceAction getPrimaryAction() {
            return this.mPrimaryAction;
        }

        public CharSequence getContentDescription() {
            return this.mContentDescription;
        }

        public int getLayoutDirection() {
            return this.mLayoutDirection;
        }
    }
}
