package androidx.slice.builders;

import android.app.PendingIntent;
import android.net.Uri;
import androidx.core.graphics.drawable.IconCompat;
import androidx.core.util.Consumer;
import androidx.core.util.Pair;
import java.util.ArrayList;
import java.util.List;

public class GridRowBuilder {
    private final List<CellBuilder> mCells = new ArrayList();
    private CharSequence mDescription;
    private boolean mHasSeeMore;
    private int mLayoutDirection = -1;
    private SliceAction mPrimaryAction;
    private CellBuilder mSeeMoreCell;
    private PendingIntent mSeeMoreIntent;

    public GridRowBuilder() {
    }

    public GridRowBuilder(ListBuilder listBuilder) {
    }

    public GridRowBuilder addCell(CellBuilder cellBuilder) {
        this.mCells.add(cellBuilder);
        return this;
    }

    public GridRowBuilder addCell(Consumer<CellBuilder> consumer) {
        CellBuilder cellBuilder = new CellBuilder(this);
        consumer.accept(cellBuilder);
        return addCell(cellBuilder);
    }

    public GridRowBuilder setSeeMoreCell(CellBuilder cellBuilder) {
        if (!this.mHasSeeMore) {
            this.mSeeMoreCell = cellBuilder;
            this.mHasSeeMore = true;
            return this;
        }
        throw new IllegalStateException("Trying to add see more cell when one has already been added");
    }

    public GridRowBuilder setSeeMoreCell(Consumer<CellBuilder> consumer) {
        CellBuilder cellBuilder = new CellBuilder(this);
        consumer.accept(cellBuilder);
        return setSeeMoreCell(cellBuilder);
    }

    public GridRowBuilder setSeeMoreAction(PendingIntent pendingIntent) {
        if (!this.mHasSeeMore) {
            this.mSeeMoreIntent = pendingIntent;
            this.mHasSeeMore = true;
            return this;
        }
        throw new IllegalStateException("Trying to add see more action when one has already been added");
    }

    public GridRowBuilder setPrimaryAction(SliceAction sliceAction) {
        this.mPrimaryAction = sliceAction;
        return this;
    }

    public GridRowBuilder setContentDescription(CharSequence charSequence) {
        this.mDescription = charSequence;
        return this;
    }

    public GridRowBuilder setLayoutDirection(int i) {
        this.mLayoutDirection = i;
        return this;
    }

    public SliceAction getPrimaryAction() {
        return this.mPrimaryAction;
    }

    public List<CellBuilder> getCells() {
        return this.mCells;
    }

    public CellBuilder getSeeMoreCell() {
        return this.mSeeMoreCell;
    }

    public PendingIntent getSeeMoreIntent() {
        return this.mSeeMoreIntent;
    }

    public CharSequence getDescription() {
        return this.mDescription;
    }

    public int getLayoutDirection() {
        return this.mLayoutDirection;
    }

    public static class CellBuilder {
        public static final int TYPE_IMAGE = 2;
        public static final int TYPE_TEXT = 0;
        public static final int TYPE_TITLE = 1;
        private CharSequence mCellDescription;
        private PendingIntent mContentIntent;
        private List<Boolean> mLoadings = new ArrayList();
        private List<Object> mObjects = new ArrayList();
        private List<Integer> mTypes = new ArrayList();

        public CellBuilder() {
        }

        public CellBuilder(GridRowBuilder gridRowBuilder) {
        }

        public CellBuilder(GridRowBuilder gridRowBuilder, Uri uri) {
        }

        public CellBuilder addText(CharSequence charSequence) {
            return addText(charSequence, false);
        }

        public CellBuilder addText(CharSequence charSequence, boolean z) {
            this.mObjects.add(charSequence);
            this.mTypes.add(0);
            this.mLoadings.add(Boolean.valueOf(z));
            return this;
        }

        public CellBuilder addTitleText(CharSequence charSequence) {
            return addTitleText(charSequence, false);
        }

        public CellBuilder addTitleText(CharSequence charSequence, boolean z) {
            this.mObjects.add(charSequence);
            this.mTypes.add(1);
            this.mLoadings.add(Boolean.valueOf(z));
            return this;
        }

        public CellBuilder addImage(IconCompat iconCompat, int i) {
            return addImage(iconCompat, i, false);
        }

        public CellBuilder addImage(IconCompat iconCompat, int i, boolean z) {
            this.mObjects.add(new Pair(iconCompat, Integer.valueOf(i)));
            this.mTypes.add(2);
            this.mLoadings.add(Boolean.valueOf(z));
            return this;
        }

        public CellBuilder setContentIntent(PendingIntent pendingIntent) {
            this.mContentIntent = pendingIntent;
            return this;
        }

        public CellBuilder setContentDescription(CharSequence charSequence) {
            this.mCellDescription = charSequence;
            return this;
        }

        public List<Object> getObjects() {
            return this.mObjects;
        }

        public List<Integer> getTypes() {
            return this.mTypes;
        }

        public List<Boolean> getLoadings() {
            return this.mLoadings;
        }

        public CharSequence getCellDescription() {
            return this.mCellDescription;
        }

        public PendingIntent getContentIntent() {
            return this.mContentIntent;
        }

        public CharSequence getTitle() {
            for (int i = 0; i < this.mObjects.size(); i++) {
                if (this.mTypes.get(i).intValue() == 1) {
                    return (CharSequence) this.mObjects.get(i);
                }
            }
            return null;
        }

        public CharSequence getSubtitle() {
            for (int i = 0; i < this.mObjects.size(); i++) {
                if (this.mTypes.get(i).intValue() == 0) {
                    return (CharSequence) this.mObjects.get(i);
                }
            }
            return null;
        }
    }
}
