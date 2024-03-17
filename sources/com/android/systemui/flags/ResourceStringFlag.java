package com.android.systemui.flags;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;

@Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\f\n\u0002\u0010\u0000\n\u0002\b\u0003\b\b\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B!\b\u0007\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007¢\u0006\u0002\u0010\bJ\t\u0010\u000e\u001a\u00020\u0004HÆ\u0003J\t\u0010\u000f\u001a\u00020\u0004HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0007HÆ\u0003J'\u0010\u0011\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00042\b\b\u0002\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0006\u001a\u00020\u0007HÆ\u0001J\u0013\u0010\u0012\u001a\u00020\u00072\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014HÖ\u0003J\t\u0010\u0015\u001a\u00020\u0004HÖ\u0001J\t\u0010\u0016\u001a\u00020\u0002HÖ\u0001R\u0014\u0010\u0003\u001a\u00020\u0004X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0014\u0010\u0005\u001a\u00020\u0004X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0014\u0010\u0006\u001a\u00020\u0007X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r¨\u0006\u0017"}, d2 = {"Lcom/android/systemui/flags/ResourceStringFlag;", "Lcom/android/systemui/flags/ResourceFlag;", "", "id", "", "resourceId", "teamfood", "", "(IIZ)V", "getId", "()I", "getResourceId", "getTeamfood", "()Z", "component1", "component2", "component3", "copy", "equals", "other", "", "hashCode", "toString", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: Flag.kt */
public final class ResourceStringFlag implements ResourceFlag<String> {
    private final int id;
    private final int resourceId;
    private final boolean teamfood;

    public ResourceStringFlag(int i, int i2) {
        this(i, i2, false, 4, (DefaultConstructorMarker) null);
    }

    public static /* synthetic */ ResourceStringFlag copy$default(ResourceStringFlag resourceStringFlag, int i, int i2, boolean z, int i3, Object obj) {
        if ((i3 & 1) != 0) {
            i = resourceStringFlag.getId();
        }
        if ((i3 & 2) != 0) {
            i2 = resourceStringFlag.getResourceId();
        }
        if ((i3 & 4) != 0) {
            z = resourceStringFlag.getTeamfood();
        }
        return resourceStringFlag.copy(i, i2, z);
    }

    public final int component1() {
        return getId();
    }

    public final int component2() {
        return getResourceId();
    }

    public final boolean component3() {
        return getTeamfood();
    }

    public final ResourceStringFlag copy(int i, int i2, boolean z) {
        return new ResourceStringFlag(i, i2, z);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ResourceStringFlag)) {
            return false;
        }
        ResourceStringFlag resourceStringFlag = (ResourceStringFlag) obj;
        return getId() == resourceStringFlag.getId() && getResourceId() == resourceStringFlag.getResourceId() && getTeamfood() == resourceStringFlag.getTeamfood();
    }

    public int hashCode() {
        int hashCode = ((Integer.hashCode(getId()) * 31) + Integer.hashCode(getResourceId())) * 31;
        boolean teamfood2 = getTeamfood();
        if (teamfood2) {
            teamfood2 = true;
        }
        return hashCode + (teamfood2 ? 1 : 0);
    }

    public String toString() {
        return "ResourceStringFlag(id=" + getId() + ", resourceId=" + getResourceId() + ", teamfood=" + getTeamfood() + ')';
    }

    public ResourceStringFlag(int i, int i2, boolean z) {
        this.id = i;
        this.resourceId = i2;
        this.teamfood = z;
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public /* synthetic */ ResourceStringFlag(int i, int i2, boolean z, int i3, DefaultConstructorMarker defaultConstructorMarker) {
        this(i, i2, (i3 & 4) != 0 ? false : z);
    }

    public int getId() {
        return this.id;
    }

    public int getResourceId() {
        return this.resourceId;
    }

    public boolean getTeamfood() {
        return this.teamfood;
    }
}
