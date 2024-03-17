package com.android.systemui.flags;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0011\n\u0002\u0010\u0000\n\u0002\b\u0003\b\b\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B!\b\u0007\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0002¢\u0006\u0002\u0010\bJ\t\u0010\u0012\u001a\u00020\u0004HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0006HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0002HÆ\u0003J'\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00042\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u0002HÆ\u0001J\u0013\u0010\u0016\u001a\u00020\u00022\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018HÖ\u0003J\t\u0010\u0019\u001a\u00020\u0004HÖ\u0001J\t\u0010\u001a\u001a\u00020\u0006HÖ\u0001R\u0014\u0010\u0007\u001a\u00020\u0002X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0014\u0010\u0003\u001a\u00020\u0004X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0014\u0010\u0005\u001a\u00020\u0006X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0014\u0010\u000f\u001a\u00020\u0002XD¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011¨\u0006\u001b"}, d2 = {"Lcom/android/systemui/flags/SysPropBooleanFlag;", "Lcom/android/systemui/flags/SysPropFlag;", "", "id", "", "name", "", "default", "(ILjava/lang/String;Z)V", "getDefault", "()Ljava/lang/Boolean;", "getId", "()I", "getName", "()Ljava/lang/String;", "teamfood", "getTeamfood", "()Z", "component1", "component2", "component3", "copy", "equals", "other", "", "hashCode", "toString", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: Flag.kt */
public final class SysPropBooleanFlag implements SysPropFlag<Boolean> {

    /* renamed from: default  reason: not valid java name */
    private final boolean f6default;
    private final int id;
    private final String name;
    private final boolean teamfood;

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public SysPropBooleanFlag(int i, String str) {
        this(i, str, false, 4, (DefaultConstructorMarker) null);
        Intrinsics.checkNotNullParameter(str, "name");
    }

    public static /* synthetic */ SysPropBooleanFlag copy$default(SysPropBooleanFlag sysPropBooleanFlag, int i, String str, boolean z, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            i = sysPropBooleanFlag.getId();
        }
        if ((i2 & 2) != 0) {
            str = sysPropBooleanFlag.getName();
        }
        if ((i2 & 4) != 0) {
            z = sysPropBooleanFlag.getDefault().booleanValue();
        }
        return sysPropBooleanFlag.copy(i, str, z);
    }

    public final int component1() {
        return getId();
    }

    public final String component2() {
        return getName();
    }

    public final boolean component3() {
        return getDefault().booleanValue();
    }

    public final SysPropBooleanFlag copy(int i, String str, boolean z) {
        Intrinsics.checkNotNullParameter(str, "name");
        return new SysPropBooleanFlag(i, str, z);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SysPropBooleanFlag)) {
            return false;
        }
        SysPropBooleanFlag sysPropBooleanFlag = (SysPropBooleanFlag) obj;
        return getId() == sysPropBooleanFlag.getId() && Intrinsics.areEqual((Object) getName(), (Object) sysPropBooleanFlag.getName()) && getDefault().booleanValue() == sysPropBooleanFlag.getDefault().booleanValue();
    }

    public int hashCode() {
        return (((Integer.hashCode(getId()) * 31) + getName().hashCode()) * 31) + getDefault().hashCode();
    }

    public String toString() {
        return "SysPropBooleanFlag(id=" + getId() + ", name=" + getName() + ", default=" + getDefault().booleanValue() + ')';
    }

    public SysPropBooleanFlag(int i, String str, boolean z) {
        Intrinsics.checkNotNullParameter(str, "name");
        this.id = i;
        this.name = str;
        this.f6default = z;
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public /* synthetic */ SysPropBooleanFlag(int i, String str, boolean z, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this(i, str, (i2 & 4) != 0 ? false : z);
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Boolean getDefault() {
        return Boolean.valueOf(this.f6default);
    }

    public boolean getTeamfood() {
        return this.teamfood;
    }
}
