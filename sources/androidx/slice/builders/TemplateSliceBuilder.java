package androidx.slice.builders;

import android.content.Context;
import android.net.Uri;
import android.util.Pair;
import androidx.slice.Clock;
import androidx.slice.Slice;
import androidx.slice.SliceManager;
import androidx.slice.SliceProvider;
import androidx.slice.SliceSpec;
import androidx.slice.SystemClock;
import androidx.slice.builders.impl.TemplateBuilderImpl;
import java.util.ArrayList;
import java.util.List;

public abstract class TemplateSliceBuilder {
    private static final String TAG = "TemplateSliceBuilder";
    private final Slice.Builder mBuilder;
    private final Context mContext;
    private final TemplateBuilderImpl mImpl;
    private List<SliceSpec> mSpecs;

    /* access modifiers changed from: protected */
    public TemplateBuilderImpl selectImpl(Uri uri) {
        return null;
    }

    /* access modifiers changed from: package-private */
    public abstract void setImpl(TemplateBuilderImpl templateBuilderImpl);

    protected TemplateSliceBuilder(TemplateBuilderImpl templateBuilderImpl) {
        this.mContext = null;
        this.mBuilder = null;
        this.mImpl = templateBuilderImpl;
        setImpl(templateBuilderImpl);
    }

    public TemplateSliceBuilder(Context context, Uri uri) {
        this.mBuilder = new Slice.Builder(uri);
        this.mContext = context;
        this.mSpecs = getSpecs(uri);
        TemplateBuilderImpl selectImpl = selectImpl(uri);
        this.mImpl = selectImpl;
        if (selectImpl != null) {
            setImpl(selectImpl);
            return;
        }
        throw new IllegalArgumentException("No valid specs found");
    }

    public Slice build() {
        return this.mImpl.build();
    }

    /* access modifiers changed from: protected */
    public Slice.Builder getBuilder() {
        return this.mBuilder;
    }

    /* access modifiers changed from: protected */
    public boolean checkCompatible(SliceSpec sliceSpec, Uri uri) {
        int size = this.mSpecs.size();
        for (int i = 0; i < size; i++) {
            if (this.mSpecs.get(i).canRender(sliceSpec)) {
                return true;
            }
        }
        return false;
    }

    private List<SliceSpec> getSpecs(Uri uri) {
        if (SliceProvider.getCurrentSpecs() != null) {
            return new ArrayList(SliceProvider.getCurrentSpecs());
        }
        return new ArrayList(SliceManager.getInstance(this.mContext).getPinnedSpecs(uri));
    }

    /* access modifiers changed from: protected */
    public Clock getClock() {
        if (SliceProvider.getClock() != null) {
            return SliceProvider.getClock();
        }
        return new SystemClock();
    }

    static <T> Pair<SliceSpec, Class<? extends TemplateBuilderImpl>> pair(SliceSpec sliceSpec, Class<T> cls) {
        return new Pair<>(sliceSpec, cls);
    }
}
