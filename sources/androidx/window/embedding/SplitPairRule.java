package androidx.window.embedding;

import java.util.LinkedHashSet;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\n\n\u0002\u0010\u0000\n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001BY\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0006\u0012\b\b\u0002\u0010\b\u001a\u00020\u0006\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\n\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u0012\b\b\u0002\u0010\u000e\u001a\u00020\n¢\u0006\u0002\u0010\u000fJ\u0013\u0010\u0016\u001a\u00020\u00062\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018H\u0002J\b\u0010\u0019\u001a\u00020\nH\u0016J\u0016\u0010\u001a\u001a\u00020\u00002\u0006\u0010\u001b\u001a\u00020\u0004H\u0002¢\u0006\u0002\b\u001cR\u0011\u0010\b\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0011R\u0011\u0010\u0007\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0011¨\u0006\u001d"}, d2 = {"Landroidx/window/embedding/SplitPairRule;", "Landroidx/window/embedding/SplitRule;", "filters", "", "Landroidx/window/embedding/SplitPairFilter;", "finishPrimaryWithSecondary", "", "finishSecondaryWithPrimary", "clearTop", "minWidth", "", "minSmallestWidth", "splitRatio", "", "layoutDir", "(Ljava/util/Set;ZZZIIFI)V", "getClearTop", "()Z", "getFilters", "()Ljava/util/Set;", "getFinishPrimaryWithSecondary", "getFinishSecondaryWithPrimary", "equals", "other", "", "hashCode", "plus", "filter", "plus$window_release", "window_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: SplitPairRule.kt */
public final class SplitPairRule extends SplitRule {
    private final boolean clearTop;
    private final Set<SplitPairFilter> filters;
    private final boolean finishPrimaryWithSecondary;
    private final boolean finishSecondaryWithPrimary;

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ SplitPairRule(java.util.Set r8, boolean r9, boolean r10, boolean r11, int r12, int r13, float r14, int r15, int r16, kotlin.jvm.internal.DefaultConstructorMarker r17) {
        /*
            r7 = this;
            r0 = r16
            r1 = r0 & 2
            r2 = 0
            if (r1 == 0) goto L_0x0009
            r1 = r2
            goto L_0x000a
        L_0x0009:
            r1 = r9
        L_0x000a:
            r3 = r0 & 4
            if (r3 == 0) goto L_0x0010
            r3 = 1
            goto L_0x0011
        L_0x0010:
            r3 = r10
        L_0x0011:
            r4 = r0 & 8
            if (r4 == 0) goto L_0x0017
            r4 = r2
            goto L_0x0018
        L_0x0017:
            r4 = r11
        L_0x0018:
            r5 = r0 & 16
            if (r5 == 0) goto L_0x001e
            r5 = r2
            goto L_0x001f
        L_0x001e:
            r5 = r12
        L_0x001f:
            r6 = r0 & 32
            if (r6 == 0) goto L_0x0024
            goto L_0x0025
        L_0x0024:
            r2 = r13
        L_0x0025:
            r6 = r0 & 64
            if (r6 == 0) goto L_0x002c
            r6 = 1056964608(0x3f000000, float:0.5)
            goto L_0x002d
        L_0x002c:
            r6 = r14
        L_0x002d:
            r0 = r0 & 128(0x80, float:1.794E-43)
            if (r0 == 0) goto L_0x0033
            r0 = 3
            goto L_0x0034
        L_0x0033:
            r0 = r15
        L_0x0034:
            r9 = r7
            r10 = r8
            r11 = r1
            r12 = r3
            r13 = r4
            r14 = r5
            r15 = r2
            r16 = r6
            r17 = r0
            r9.<init>(r10, r11, r12, r13, r14, r15, r16, r17)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.window.embedding.SplitPairRule.<init>(java.util.Set, boolean, boolean, boolean, int, int, float, int, int, kotlin.jvm.internal.DefaultConstructorMarker):void");
    }

    public final boolean getFinishPrimaryWithSecondary() {
        return this.finishPrimaryWithSecondary;
    }

    public final boolean getFinishSecondaryWithPrimary() {
        return this.finishSecondaryWithPrimary;
    }

    public final boolean getClearTop() {
        return this.clearTop;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public SplitPairRule(Set<SplitPairFilter> set, boolean z, boolean z2, boolean z3, int i, int i2, float f, int i3) {
        super(i, i2, f, i3);
        Intrinsics.checkNotNullParameter(set, "filters");
        this.finishPrimaryWithSecondary = z;
        this.finishSecondaryWithPrimary = z2;
        this.clearTop = z3;
        this.filters = CollectionsKt.toSet(set);
    }

    public final Set<SplitPairFilter> getFilters() {
        return this.filters;
    }

    public final SplitPairRule plus$window_release(SplitPairFilter splitPairFilter) {
        Intrinsics.checkNotNullParameter(splitPairFilter, "filter");
        Set linkedHashSet = new LinkedHashSet();
        linkedHashSet.addAll(this.filters);
        linkedHashSet.add(splitPairFilter);
        return new SplitPairRule(CollectionsKt.toSet(linkedHashSet), this.finishPrimaryWithSecondary, this.finishSecondaryWithPrimary, this.clearTop, getMinWidth(), getMinSmallestWidth(), getSplitRatio(), getLayoutDirection());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SplitPairRule) || !super.equals(obj)) {
            return false;
        }
        SplitPairRule splitPairRule = (SplitPairRule) obj;
        return Intrinsics.areEqual((Object) this.filters, (Object) splitPairRule.filters) && this.finishPrimaryWithSecondary == splitPairRule.finishPrimaryWithSecondary && this.finishSecondaryWithPrimary == splitPairRule.finishSecondaryWithPrimary && this.clearTop == splitPairRule.clearTop;
    }

    public int hashCode() {
        return (((((((super.hashCode() * 31) + this.filters.hashCode()) * 31) + Boolean.hashCode(this.finishPrimaryWithSecondary)) * 31) + Boolean.hashCode(this.finishSecondaryWithPrimary)) * 31) + Boolean.hashCode(this.clearTop);
    }
}
