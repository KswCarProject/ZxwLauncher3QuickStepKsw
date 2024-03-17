package androidx.window.embedding;

import android.app.Activity;
import android.content.Intent;
import android.util.Pair;
import android.view.WindowMetrics;
import androidx.slice.core.SliceHints;
import androidx.window.extensions.embedding.ActivityRule;
import androidx.window.extensions.embedding.ActivityStack;
import androidx.window.extensions.embedding.EmbeddingRule;
import androidx.window.extensions.embedding.SplitInfo;
import androidx.window.extensions.embedding.SplitPairRule;
import androidx.window.extensions.embedding.SplitPlaceholderRule;
import com.android.launcher3.LauncherSettings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0001\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0002J\u001a\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00040\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00060\u0007J\u001a\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\n0\t2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\tJ(\u0010\r\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u000f0\u000e2\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\tH\u0007J(\u0010\u0014\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00100\u000f0\u000e2\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\tH\u0007J\u001c\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00100\u000e2\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00170\tH\u0007J\u001c\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00110\u000e2\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00170\tH\u0007J\u0016\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u001a0\u000e2\u0006\u0010\u001b\u001a\u00020\u001cH\u0007J*\u0010\u001d\u001a\u0002H\u001e\"\u0004\b\u0000\u0010\u001e\"\u0004\b\u0001\u0010\u001f*\u000e\u0012\u0004\u0012\u0002H\u001e\u0012\u0004\u0012\u0002H\u001f0\u000fH\u0002¢\u0006\u0002\u0010 J*\u0010!\u001a\u0002H\u001f\"\u0004\b\u0000\u0010\u001e\"\u0004\b\u0001\u0010\u001f*\u000e\u0012\u0004\u0012\u0002H\u001e\u0012\u0004\u0012\u0002H\u001f0\u000fH\u0002¢\u0006\u0002\u0010 ¨\u0006\""}, d2 = {"Landroidx/window/embedding/EmbeddingAdapter;", "", "()V", "translate", "Landroidx/window/embedding/SplitInfo;", "splitInfo", "Landroidx/window/extensions/embedding/SplitInfo;", "", "splitInfoList", "", "Landroidx/window/extensions/embedding/EmbeddingRule;", "rules", "Landroidx/window/embedding/EmbeddingRule;", "translateActivityIntentPredicates", "Ljava/util/function/Predicate;", "Landroid/util/Pair;", "Landroid/app/Activity;", "Landroid/content/Intent;", "splitPairFilters", "Landroidx/window/embedding/SplitPairFilter;", "translateActivityPairPredicates", "translateActivityPredicates", "activityFilters", "Landroidx/window/embedding/ActivityFilter;", "translateIntentPredicates", "translateParentMetricsPredicate", "Landroid/view/WindowMetrics;", "splitRule", "Landroidx/window/embedding/SplitRule;", "component1", "F", "S", "(Landroid/util/Pair;)Ljava/lang/Object;", "component2", "window_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: EmbeddingAdapter.kt */
public final class EmbeddingAdapter {
    public final List<SplitInfo> translate(List<? extends SplitInfo> list) {
        Intrinsics.checkNotNullParameter(list, "splitInfoList");
        Iterable<SplitInfo> iterable = list;
        Collection arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10));
        for (SplitInfo translate : iterable) {
            arrayList.add(translate(translate));
        }
        return (List) arrayList;
    }

    private final SplitInfo translate(SplitInfo splitInfo) {
        boolean z;
        ActivityStack primaryActivityStack = splitInfo.getPrimaryActivityStack();
        Intrinsics.checkNotNullExpressionValue(primaryActivityStack, "splitInfo.primaryActivityStack");
        boolean z2 = false;
        try {
            z = primaryActivityStack.isEmpty();
        } catch (NoSuchMethodError unused) {
            z = false;
        }
        List activities = primaryActivityStack.getActivities();
        Intrinsics.checkNotNullExpressionValue(activities, "primaryActivityStack.activities");
        ActivityStack activityStack = new ActivityStack(activities, z);
        ActivityStack secondaryActivityStack = splitInfo.getSecondaryActivityStack();
        Intrinsics.checkNotNullExpressionValue(secondaryActivityStack, "splitInfo.secondaryActivityStack");
        try {
            z2 = secondaryActivityStack.isEmpty();
        } catch (NoSuchMethodError unused2) {
        }
        List activities2 = secondaryActivityStack.getActivities();
        Intrinsics.checkNotNullExpressionValue(activities2, "secondaryActivityStack.activities");
        return new SplitInfo(activityStack, new ActivityStack(activities2, z2), splitInfo.getSplitRatio());
    }

    public final Predicate<Pair<Activity, Activity>> translateActivityPairPredicates(Set<SplitPairFilter> set) {
        Intrinsics.checkNotNullParameter(set, "splitPairFilters");
        return new Predicate(set) {
            public final /* synthetic */ Set f$1;

            {
                this.f$1 = r2;
            }

            public final boolean test(Object obj) {
                return EmbeddingAdapter.m15translateActivityPairPredicates$lambda1(EmbeddingAdapter.this, this.f$1, (Pair) obj);
            }
        };
    }

    /* access modifiers changed from: private */
    /* renamed from: translateActivityPairPredicates$lambda-1  reason: not valid java name */
    public static final boolean m15translateActivityPairPredicates$lambda1(EmbeddingAdapter embeddingAdapter, Set set, Pair pair) {
        Intrinsics.checkNotNullParameter(embeddingAdapter, "this$0");
        Intrinsics.checkNotNullParameter(set, "$splitPairFilters");
        Intrinsics.checkNotNullExpressionValue(pair, "(first, second)");
        Activity activity = (Activity) embeddingAdapter.component1(pair);
        Activity activity2 = (Activity) embeddingAdapter.component2(pair);
        Iterable<SplitPairFilter> iterable = set;
        if ((iterable instanceof Collection) && ((Collection) iterable).isEmpty()) {
            return false;
        }
        for (SplitPairFilter matchesActivityPair : iterable) {
            if (matchesActivityPair.matchesActivityPair(activity, activity2)) {
                return true;
            }
        }
        return false;
    }

    public final Predicate<Pair<Activity, Intent>> translateActivityIntentPredicates(Set<SplitPairFilter> set) {
        Intrinsics.checkNotNullParameter(set, "splitPairFilters");
        return new Predicate(set) {
            public final /* synthetic */ Set f$1;

            {
                this.f$1 = r2;
            }

            public final boolean test(Object obj) {
                return EmbeddingAdapter.m14translateActivityIntentPredicates$lambda3(EmbeddingAdapter.this, this.f$1, (Pair) obj);
            }
        };
    }

    /* access modifiers changed from: private */
    /* renamed from: translateActivityIntentPredicates$lambda-3  reason: not valid java name */
    public static final boolean m14translateActivityIntentPredicates$lambda3(EmbeddingAdapter embeddingAdapter, Set set, Pair pair) {
        Intrinsics.checkNotNullParameter(embeddingAdapter, "this$0");
        Intrinsics.checkNotNullParameter(set, "$splitPairFilters");
        Intrinsics.checkNotNullExpressionValue(pair, "(first, second)");
        Activity activity = (Activity) embeddingAdapter.component1(pair);
        Intent intent = (Intent) embeddingAdapter.component2(pair);
        Iterable<SplitPairFilter> iterable = set;
        if ((iterable instanceof Collection) && ((Collection) iterable).isEmpty()) {
            return false;
        }
        for (SplitPairFilter matchesActivityIntentPair : iterable) {
            if (matchesActivityIntentPair.matchesActivityIntentPair(activity, intent)) {
                return true;
            }
        }
        return false;
    }

    public final Predicate<WindowMetrics> translateParentMetricsPredicate(SplitRule splitRule) {
        Intrinsics.checkNotNullParameter(splitRule, "splitRule");
        return new Predicate() {
            public final boolean test(Object obj) {
                return EmbeddingAdapter.m18translateParentMetricsPredicate$lambda4(SplitRule.this, (WindowMetrics) obj);
            }
        };
    }

    /* access modifiers changed from: private */
    /* renamed from: translateParentMetricsPredicate$lambda-4  reason: not valid java name */
    public static final boolean m18translateParentMetricsPredicate$lambda4(SplitRule splitRule, WindowMetrics windowMetrics) {
        Intrinsics.checkNotNullParameter(splitRule, "$splitRule");
        Intrinsics.checkNotNullExpressionValue(windowMetrics, "windowMetrics");
        return splitRule.checkParentMetrics(windowMetrics);
    }

    public final Predicate<Activity> translateActivityPredicates(Set<ActivityFilter> set) {
        Intrinsics.checkNotNullParameter(set, "activityFilters");
        return new Predicate(set) {
            public final /* synthetic */ Set f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return EmbeddingAdapter.m16translateActivityPredicates$lambda6(this.f$0, (Activity) obj);
            }
        };
    }

    /* access modifiers changed from: private */
    /* renamed from: translateActivityPredicates$lambda-6  reason: not valid java name */
    public static final boolean m16translateActivityPredicates$lambda6(Set set, Activity activity) {
        Intrinsics.checkNotNullParameter(set, "$activityFilters");
        Iterable<ActivityFilter> iterable = set;
        if ((iterable instanceof Collection) && ((Collection) iterable).isEmpty()) {
            return false;
        }
        for (ActivityFilter matchesActivity : iterable) {
            Intrinsics.checkNotNullExpressionValue(activity, SliceHints.HINT_ACTIVITY);
            if (matchesActivity.matchesActivity(activity)) {
                return true;
            }
        }
        return false;
    }

    public final Predicate<Intent> translateIntentPredicates(Set<ActivityFilter> set) {
        Intrinsics.checkNotNullParameter(set, "activityFilters");
        return new Predicate(set) {
            public final /* synthetic */ Set f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return EmbeddingAdapter.m17translateIntentPredicates$lambda8(this.f$0, (Intent) obj);
            }
        };
    }

    /* access modifiers changed from: private */
    /* renamed from: translateIntentPredicates$lambda-8  reason: not valid java name */
    public static final boolean m17translateIntentPredicates$lambda8(Set set, Intent intent) {
        Intrinsics.checkNotNullParameter(set, "$activityFilters");
        Iterable<ActivityFilter> iterable = set;
        if ((iterable instanceof Collection) && ((Collection) iterable).isEmpty()) {
            return false;
        }
        for (ActivityFilter matchesIntent : iterable) {
            Intrinsics.checkNotNullExpressionValue(intent, LauncherSettings.Favorites.INTENT);
            if (matchesIntent.matchesIntent(intent)) {
                return true;
            }
        }
        return false;
    }

    public final Set<EmbeddingRule> translate(Set<? extends EmbeddingRule> set) {
        SplitPairRule splitPairRule;
        Intrinsics.checkNotNullParameter(set, "rules");
        Iterable<EmbeddingRule> iterable = set;
        Collection arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10));
        for (EmbeddingRule embeddingRule : iterable) {
            if (embeddingRule instanceof SplitPairRule) {
                SplitPairRule splitPairRule2 = (SplitPairRule) embeddingRule;
                splitPairRule = new SplitPairRule.Builder(translateActivityPairPredicates(splitPairRule2.getFilters()), translateActivityIntentPredicates(splitPairRule2.getFilters()), translateParentMetricsPredicate((SplitRule) embeddingRule)).setSplitRatio(splitPairRule2.getSplitRatio()).setLayoutDirection(splitPairRule2.getLayoutDirection()).setShouldFinishPrimaryWithSecondary(splitPairRule2.getFinishPrimaryWithSecondary()).setShouldFinishSecondaryWithPrimary(splitPairRule2.getFinishSecondaryWithPrimary()).setShouldClearTop(splitPairRule2.getClearTop()).build();
                Intrinsics.checkNotNullExpressionValue(splitPairRule, "SplitPairRuleBuilder(\n  …                 .build()");
            } else if (embeddingRule instanceof SplitPlaceholderRule) {
                SplitPlaceholderRule splitPlaceholderRule = (SplitPlaceholderRule) embeddingRule;
                splitPairRule = new SplitPlaceholderRule.Builder(splitPlaceholderRule.getPlaceholderIntent(), translateActivityPredicates(splitPlaceholderRule.getFilters()), translateIntentPredicates(splitPlaceholderRule.getFilters()), translateParentMetricsPredicate((SplitRule) embeddingRule)).setSplitRatio(splitPlaceholderRule.getSplitRatio()).setLayoutDirection(splitPlaceholderRule.getLayoutDirection()).build();
                Intrinsics.checkNotNullExpressionValue(splitPairRule, "SplitPlaceholderRuleBuil…                 .build()");
            } else if (embeddingRule instanceof ActivityRule) {
                ActivityRule activityRule = (ActivityRule) embeddingRule;
                splitPairRule = new ActivityRule.Builder(translateActivityPredicates(activityRule.getFilters()), translateIntentPredicates(activityRule.getFilters())).setShouldAlwaysExpand(activityRule.getAlwaysExpand()).build();
                Intrinsics.checkNotNullExpressionValue(splitPairRule, "ActivityRuleBuilder(\n   …                 .build()");
            } else {
                throw new IllegalArgumentException("Unsupported rule type");
            }
            arrayList.add((EmbeddingRule) splitPairRule);
        }
        return CollectionsKt.toSet((List) arrayList);
    }

    private final <F, S> F component1(Pair<F, S> pair) {
        Intrinsics.checkNotNullParameter(pair, "<this>");
        return pair.first;
    }

    private final <F, S> S component2(Pair<F, S> pair) {
        Intrinsics.checkNotNullParameter(pair, "<this>");
        return pair.second;
    }
}
