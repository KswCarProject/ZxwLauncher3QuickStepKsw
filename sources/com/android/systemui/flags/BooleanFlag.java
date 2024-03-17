package com.android.systemui.flags;

import android.os.Parcel;
import android.os.Parcelable;
import com.android.systemui.flags.ParcelableFlag;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u000f\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\b\b\u0018\u0000 \u001e2\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u001eB\u000f\b\u0012\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0002\u0010\u0005B#\b\u0007\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\u0002\u0012\b\b\u0002\u0010\t\u001a\u00020\u0002¢\u0006\u0002\u0010\nJ\t\u0010\u0011\u001a\u00020\u0007HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0002HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0002HÆ\u0003J'\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00022\b\b\u0002\u0010\t\u001a\u00020\u0002HÆ\u0001J\u0013\u0010\u0015\u001a\u00020\u00022\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017HÖ\u0003J\t\u0010\u0018\u001a\u00020\u0007HÖ\u0001J\t\u0010\u0019\u001a\u00020\u001aHÖ\u0001J\u0018\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u001d\u001a\u00020\u0007H\u0016R\u0014\u0010\b\u001a\u00020\u0002X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0014\u0010\u0006\u001a\u00020\u0007X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0014\u0010\t\u001a\u00020\u0002X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010¨\u0006\u001f"}, d2 = {"Lcom/android/systemui/flags/BooleanFlag;", "Lcom/android/systemui/flags/ParcelableFlag;", "", "parcel", "Landroid/os/Parcel;", "(Landroid/os/Parcel;)V", "id", "", "default", "teamfood", "(IZZ)V", "getDefault", "()Ljava/lang/Boolean;", "getId", "()I", "getTeamfood", "()Z", "component1", "component2", "component3", "copy", "equals", "other", "", "hashCode", "toString", "", "writeToParcel", "", "flags", "Companion", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: Flag.kt */
public final class BooleanFlag implements ParcelableFlag<Boolean> {
    public static final Parcelable.Creator<BooleanFlag> CREATOR = new BooleanFlag$Companion$CREATOR$1();
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);

    /* renamed from: default  reason: not valid java name */
    private final boolean f0default;
    private final int id;
    private final boolean teamfood;

    public BooleanFlag(int i) {
        this(i, false, false, 6, (DefaultConstructorMarker) null);
    }

    public BooleanFlag(int i, boolean z) {
        this(i, z, false, 4, (DefaultConstructorMarker) null);
    }

    public /* synthetic */ BooleanFlag(Parcel parcel, DefaultConstructorMarker defaultConstructorMarker) {
        this(parcel);
    }

    public static /* synthetic */ BooleanFlag copy$default(BooleanFlag booleanFlag, int i, boolean z, boolean z2, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            i = booleanFlag.getId();
        }
        if ((i2 & 2) != 0) {
            z = booleanFlag.getDefault().booleanValue();
        }
        if ((i2 & 4) != 0) {
            z2 = booleanFlag.getTeamfood();
        }
        return booleanFlag.copy(i, z, z2);
    }

    public final int component1() {
        return getId();
    }

    public final boolean component2() {
        return getDefault().booleanValue();
    }

    public final boolean component3() {
        return getTeamfood();
    }

    public final BooleanFlag copy(int i, boolean z, boolean z2) {
        return new BooleanFlag(i, z, z2);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BooleanFlag)) {
            return false;
        }
        BooleanFlag booleanFlag = (BooleanFlag) obj;
        return getId() == booleanFlag.getId() && getDefault().booleanValue() == booleanFlag.getDefault().booleanValue() && getTeamfood() == booleanFlag.getTeamfood();
    }

    public int hashCode() {
        int hashCode = ((Integer.hashCode(getId()) * 31) + getDefault().hashCode()) * 31;
        boolean teamfood2 = getTeamfood();
        if (teamfood2) {
            teamfood2 = true;
        }
        return hashCode + (teamfood2 ? 1 : 0);
    }

    public String toString() {
        return "BooleanFlag(id=" + getId() + ", default=" + getDefault().booleanValue() + ", teamfood=" + getTeamfood() + ')';
    }

    public BooleanFlag(int i, boolean z, boolean z2) {
        this.id = i;
        this.f0default = z;
        this.teamfood = z2;
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public /* synthetic */ BooleanFlag(int i, boolean z, boolean z2, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this(i, (i2 & 2) != 0 ? false : z, (i2 & 4) != 0 ? false : z2);
    }

    public int describeContents() {
        return ParcelableFlag.DefaultImpls.describeContents(this);
    }

    public int getId() {
        return this.id;
    }

    public Boolean getDefault() {
        return Boolean.valueOf(this.f0default);
    }

    public boolean getTeamfood() {
        return this.teamfood;
    }

    @Metadata(d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u0016\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u00048\u0006X\u0004¢\u0006\u0002\n\u0000¨\u0006\u0006"}, d2 = {"Lcom/android/systemui/flags/BooleanFlag$Companion;", "", "()V", "CREATOR", "Landroid/os/Parcelable$Creator;", "Lcom/android/systemui/flags/BooleanFlag;", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: Flag.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    private BooleanFlag(Parcel parcel) {
        this(parcel.readInt(), parcel.readBoolean(), false, 4, (DefaultConstructorMarker) null);
    }

    public void writeToParcel(Parcel parcel, int i) {
        Intrinsics.checkNotNullParameter(parcel, "parcel");
        parcel.writeInt(getId());
        parcel.writeBoolean(getDefault().booleanValue());
    }
}
