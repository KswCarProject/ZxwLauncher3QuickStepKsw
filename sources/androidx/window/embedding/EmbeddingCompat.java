package androidx.window.embedding;

import android.util.Log;
import androidx.window.embedding.EmbeddingInterfaceCompat;
import androidx.window.extensions.WindowExtensionsProvider;
import androidx.window.extensions.embedding.ActivityEmbeddingComponent;
import java.util.Set;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0001\u0018\u0000 \u00102\u00020\u0001:\u0001\u0010B\u0007\b\u0016¢\u0006\u0002\u0010\u0002B\u0015\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0002\u0010\u0007J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0016J\u0016\u0010\f\u001a\u00020\t2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eH\u0016R\u000e\u0010\u0005\u001a\u00020\u0006X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0003\u001a\u00020\u0004X\u0004¢\u0006\u0002\n\u0000¨\u0006\u0011"}, d2 = {"Landroidx/window/embedding/EmbeddingCompat;", "Landroidx/window/embedding/EmbeddingInterfaceCompat;", "()V", "embeddingExtension", "Landroidx/window/extensions/embedding/ActivityEmbeddingComponent;", "adapter", "Landroidx/window/embedding/EmbeddingAdapter;", "(Landroidx/window/extensions/embedding/ActivityEmbeddingComponent;Landroidx/window/embedding/EmbeddingAdapter;)V", "setEmbeddingCallback", "", "embeddingCallback", "Landroidx/window/embedding/EmbeddingInterfaceCompat$EmbeddingCallbackInterface;", "setSplitRules", "rules", "", "Landroidx/window/embedding/EmbeddingRule;", "Companion", "window_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: EmbeddingCompat.kt */
public final class EmbeddingCompat implements EmbeddingInterfaceCompat {
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    public static final boolean DEBUG = true;
    private static final String TAG = "EmbeddingCompat";
    private final EmbeddingAdapter adapter;
    private final ActivityEmbeddingComponent embeddingExtension;

    public EmbeddingCompat(ActivityEmbeddingComponent activityEmbeddingComponent, EmbeddingAdapter embeddingAdapter) {
        Intrinsics.checkNotNullParameter(activityEmbeddingComponent, "embeddingExtension");
        Intrinsics.checkNotNullParameter(embeddingAdapter, "adapter");
        this.embeddingExtension = activityEmbeddingComponent;
        this.adapter = embeddingAdapter;
    }

    public EmbeddingCompat() {
        this(Companion.embeddingComponent(), new EmbeddingAdapter());
    }

    public void setSplitRules(Set<? extends EmbeddingRule> set) {
        Intrinsics.checkNotNullParameter(set, "rules");
        this.embeddingExtension.setEmbeddingRules(this.adapter.translate(set));
    }

    public void setEmbeddingCallback(EmbeddingInterfaceCompat.EmbeddingCallbackInterface embeddingCallbackInterface) {
        Intrinsics.checkNotNullParameter(embeddingCallbackInterface, "embeddingCallback");
        this.embeddingExtension.setSplitInfoCallback(new EmbeddingTranslatingCallback(embeddingCallbackInterface, this.adapter));
    }

    @Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0006\u0010\u0007\u001a\u00020\bJ\r\u0010\t\u001a\u0004\u0018\u00010\n¢\u0006\u0002\u0010\u000bJ\u0006\u0010\f\u001a\u00020\u0004R\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006XT¢\u0006\u0002\n\u0000¨\u0006\r"}, d2 = {"Landroidx/window/embedding/EmbeddingCompat$Companion;", "", "()V", "DEBUG", "", "TAG", "", "embeddingComponent", "Landroidx/window/extensions/embedding/ActivityEmbeddingComponent;", "getExtensionApiLevel", "", "()Ljava/lang/Integer;", "isEmbeddingAvailable", "window_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: EmbeddingCompat.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final Integer getExtensionApiLevel() {
            try {
                return Integer.valueOf(WindowExtensionsProvider.getWindowExtensions().getVendorApiLevel());
            } catch (NoClassDefFoundError unused) {
                Log.d(EmbeddingCompat.TAG, "Embedding extension version not found");
                return null;
            } catch (UnsupportedOperationException unused2) {
                Log.d(EmbeddingCompat.TAG, "Stub Extension");
                return null;
            }
        }

        public final boolean isEmbeddingAvailable() {
            try {
                return WindowExtensionsProvider.getWindowExtensions().getActivityEmbeddingComponent() != null;
            } catch (NoClassDefFoundError unused) {
                Log.d(EmbeddingCompat.TAG, "Embedding extension version not found");
                return false;
            } catch (UnsupportedOperationException unused2) {
                Log.d(EmbeddingCompat.TAG, "Stub Extension");
                return false;
            }
        }

        public final ActivityEmbeddingComponent embeddingComponent() {
            if (!isEmbeddingAvailable()) {
                return new EmptyEmbeddingComponent();
            }
            ActivityEmbeddingComponent activityEmbeddingComponent = WindowExtensionsProvider.getWindowExtensions().getActivityEmbeddingComponent();
            if (activityEmbeddingComponent == null) {
                return new EmptyEmbeddingComponent();
            }
            return activityEmbeddingComponent;
        }
    }
}
