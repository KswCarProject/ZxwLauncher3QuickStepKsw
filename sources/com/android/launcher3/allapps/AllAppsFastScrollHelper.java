package com.android.launcher3.allapps;

import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.allapps.AlphabeticalAppsList;

public class AllAppsFastScrollHelper {
    private static final int NO_POSITION = -1;
    /* access modifiers changed from: private */
    public RecyclerView.ViewHolder mLastSelectedViewHolder;
    /* access modifiers changed from: private */
    public AllAppsRecyclerView mRv;
    /* access modifiers changed from: private */
    public int mTargetFastScrollPosition = -1;

    public AllAppsFastScrollHelper(AllAppsRecyclerView allAppsRecyclerView) {
        this.mRv = allAppsRecyclerView;
    }

    public void smoothScrollToSection(AlphabeticalAppsList.FastScrollSectionInfo fastScrollSectionInfo) {
        if (this.mTargetFastScrollPosition != fastScrollSectionInfo.position) {
            this.mTargetFastScrollPosition = fastScrollSectionInfo.position;
            this.mRv.getLayoutManager().startSmoothScroll(new MyScroller(this.mTargetFastScrollPosition));
        }
    }

    public void onFastScrollCompleted() {
        this.mTargetFastScrollPosition = -1;
        setLastHolderSelected(false);
        this.mLastSelectedViewHolder = null;
    }

    /* access modifiers changed from: private */
    public void setLastHolderSelected(boolean z) {
        RecyclerView.ViewHolder viewHolder = this.mLastSelectedViewHolder;
        if (viewHolder != null) {
            viewHolder.itemView.setActivated(z);
            this.mLastSelectedViewHolder.setIsRecyclable(!z);
        }
    }

    private class MyScroller extends LinearSmoothScroller {
        private final int mTargetPosition;

        /* access modifiers changed from: protected */
        public int getVerticalSnapPreference() {
            return -1;
        }

        public MyScroller(int i) {
            super(AllAppsFastScrollHelper.this.mRv.getContext());
            this.mTargetPosition = i;
            setTargetPosition(i);
        }

        /* access modifiers changed from: protected */
        public void onStop() {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition;
            super.onStop();
            if (this.mTargetPosition == AllAppsFastScrollHelper.this.mTargetFastScrollPosition && (findViewHolderForAdapterPosition = AllAppsFastScrollHelper.this.mRv.findViewHolderForAdapterPosition(this.mTargetPosition)) != AllAppsFastScrollHelper.this.mLastSelectedViewHolder) {
                AllAppsFastScrollHelper.this.setLastHolderSelected(false);
                RecyclerView.ViewHolder unused = AllAppsFastScrollHelper.this.mLastSelectedViewHolder = findViewHolderForAdapterPosition;
                AllAppsFastScrollHelper.this.setLastHolderSelected(true);
            }
        }

        /* access modifiers changed from: protected */
        public void onStart() {
            super.onStart();
            if (this.mTargetPosition != AllAppsFastScrollHelper.this.mTargetFastScrollPosition) {
                AllAppsFastScrollHelper.this.setLastHolderSelected(false);
                RecyclerView.ViewHolder unused = AllAppsFastScrollHelper.this.mLastSelectedViewHolder = null;
            }
        }
    }
}
