package com.android.launcher3.allapps;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.R;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.touch.ItemLongClickListener;
import com.android.launcher3.views.ActivityContext;
import java.util.Arrays;
import java.util.function.Predicate;

public abstract class BaseAllAppsAdapter<T extends Context & ActivityContext> extends RecyclerView.Adapter<ViewHolder> {
    public static final String TAG = "BaseAllAppsAdapter";
    public static final int VIEW_TYPE_ALL_APPS_DIVIDER = 16;
    public static final int VIEW_TYPE_EMPTY_SEARCH = 4;
    public static final int VIEW_TYPE_ICON = 2;
    public static final int VIEW_TYPE_MASK_DIVIDER = 16;
    public static final int VIEW_TYPE_MASK_ICON = 2;
    public static final int VIEW_TYPE_SEARCH_MARKET = 8;
    protected final T mActivityContext;
    protected final BaseAdapterProvider[] mAdapterProviders;
    protected final AlphabeticalAppsList<T> mApps;
    protected int mAppsPerRow;
    protected String mEmptySearchMessage;
    private final int mExtraHeight;
    protected View.OnFocusChangeListener mIconFocusListener;
    protected final LayoutInflater mLayoutInflater;
    private View.OnClickListener mMarketSearchClickListener;
    protected final View.OnClickListener mOnIconClickListener;
    protected View.OnLongClickListener mOnIconLongClickListener = ItemLongClickListener.INSTANCE_ALL_APPS;

    protected static boolean isViewType(int i, int i2) {
        return (i & i2) != 0;
    }

    public abstract RecyclerView.LayoutManager getLayoutManager();

    public boolean onFailedToRecycleView(ViewHolder viewHolder) {
        return true;
    }

    public abstract void setAppsPerRow(int i);

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
        }
    }

    public static class AdapterItem {
        public AppInfo itemInfo = null;
        public int rowAppIndex;
        public int rowIndex;
        public final int viewType;

        public AdapterItem(int i) {
            this.viewType = i;
        }

        public static AdapterItem asApp(AppInfo appInfo) {
            AdapterItem adapterItem = new AdapterItem(2);
            adapterItem.itemInfo = appInfo;
            return adapterItem;
        }

        /* access modifiers changed from: protected */
        public boolean isCountedForAccessibility() {
            int i = this.viewType;
            return i == 2 || i == 8;
        }

        public boolean isSameAs(AdapterItem adapterItem) {
            return adapterItem.viewType == this.viewType && adapterItem.getClass() == getClass();
        }

        public boolean isContentSame(AdapterItem adapterItem) {
            return this.itemInfo == null && adapterItem.itemInfo == null;
        }
    }

    public BaseAllAppsAdapter(T t, LayoutInflater layoutInflater, AlphabeticalAppsList<T> alphabeticalAppsList, BaseAdapterProvider[] baseAdapterProviderArr) {
        Resources resources = t.getResources();
        this.mActivityContext = t;
        this.mApps = alphabeticalAppsList;
        this.mEmptySearchMessage = resources.getString(R.string.all_apps_loading_message);
        this.mLayoutInflater = layoutInflater;
        this.mOnIconClickListener = ((ActivityContext) t).getItemOnClickListener();
        this.mAdapterProviders = baseAdapterProviderArr;
        this.mExtraHeight = resources.getDimensionPixelSize(R.dimen.all_apps_height_extra);
    }

    public void setOnIconLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.mOnIconLongClickListener = onLongClickListener;
    }

    public static boolean isDividerViewType(int i) {
        return isViewType(i, 16);
    }

    public static boolean isIconViewType(int i) {
        return isViewType(i, 2);
    }

    public void setIconFocusListener(View.OnFocusChangeListener onFocusChangeListener) {
        this.mIconFocusListener = onFocusChangeListener;
    }

    public void setLastSearchQuery(String str, View.OnClickListener onClickListener) {
        this.mEmptySearchMessage = this.mActivityContext.getResources().getString(R.string.all_apps_no_search_results, new Object[]{str});
        this.mMarketSearchClickListener = onClickListener;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == 2) {
            BubbleTextView bubbleTextView = (BubbleTextView) this.mLayoutInflater.inflate(!FeatureFlags.ENABLE_TWOLINE_ALLAPPS.get() ? R.layout.all_apps_icon : R.layout.all_apps_icon_twoline, viewGroup, false);
            bubbleTextView.setLongPressTimeoutFactor(1.0f);
            bubbleTextView.setOnFocusChangeListener(this.mIconFocusListener);
            bubbleTextView.setOnClickListener(this.mOnIconClickListener);
            bubbleTextView.setOnLongClickListener(this.mOnIconLongClickListener);
            bubbleTextView.getLayoutParams().height = ((ActivityContext) this.mActivityContext).getDeviceProfile().allAppsCellHeightPx;
            if (FeatureFlags.ENABLE_TWOLINE_ALLAPPS.get()) {
                bubbleTextView.getLayoutParams().height += this.mExtraHeight;
            }
            return new ViewHolder(bubbleTextView);
        } else if (i == 4) {
            return new ViewHolder(this.mLayoutInflater.inflate(R.layout.all_apps_empty_search, viewGroup, false));
        } else {
            if (i == 8) {
                View inflate = this.mLayoutInflater.inflate(R.layout.all_apps_search_market, viewGroup, false);
                inflate.setOnClickListener(this.mMarketSearchClickListener);
                return new ViewHolder(inflate);
            } else if (i == 16) {
                return new ViewHolder(this.mLayoutInflater.inflate(R.layout.all_apps_divider, viewGroup, false));
            } else {
                BaseAdapterProvider adapterProvider = getAdapterProvider(i);
                if (adapterProvider != null) {
                    return adapterProvider.onCreateViewHolder(this.mLayoutInflater, viewGroup, i);
                }
                throw new RuntimeException("Unexpected view type" + i);
            }
        }
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        BaseAdapterProvider adapterProvider;
        int itemViewType = viewHolder.getItemViewType();
        if (itemViewType == 2) {
            BubbleTextView bubbleTextView = (BubbleTextView) viewHolder.itemView;
            bubbleTextView.reset();
            bubbleTextView.applyFromApplicationInfo(this.mApps.getAdapterItems().get(i).itemInfo);
        } else if (itemViewType == 4) {
            TextView textView = (TextView) viewHolder.itemView;
            textView.setText(this.mEmptySearchMessage);
            textView.setGravity(this.mApps.hasNoFilteredResults() ? 17 : 8388627);
        } else if (itemViewType == 8) {
            TextView textView2 = (TextView) viewHolder.itemView;
            if (this.mMarketSearchClickListener != null) {
                textView2.setVisibility(0);
            } else {
                textView2.setVisibility(8);
            }
        } else if (itemViewType != 16 && (adapterProvider = getAdapterProvider(viewHolder.getItemViewType())) != null) {
            adapterProvider.onBindView(viewHolder, i);
        }
    }

    public void onViewRecycled(ViewHolder viewHolder) {
        super.onViewRecycled(viewHolder);
    }

    public int getItemCount() {
        return this.mApps.getAdapterItems().size();
    }

    public int getItemViewType(int i) {
        return this.mApps.getAdapterItems().get(i).viewType;
    }

    /* access modifiers changed from: protected */
    public BaseAdapterProvider getAdapterProvider(int i) {
        return (BaseAdapterProvider) Arrays.stream(this.mAdapterProviders).filter(new Predicate(i) {
            public final /* synthetic */ int f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return ((BaseAdapterProvider) obj).isViewSupported(this.f$0);
            }
        }).findFirst().orElse((Object) null);
    }
}
