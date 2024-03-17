package com.android.systemui.flags;

import android.os.Parcel;
import android.os.Parcelable;
import com.android.systemui.flags.ParcelableFlag;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\r\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\b\b\u0018\u0000 \u001f2\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u001fB\u000f\b\u0012\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0002\u0010\u0005B#\b\u0007\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\u0002\u0012\b\b\u0002\u0010\t\u001a\u00020\n¢\u0006\u0002\u0010\u000bJ\t\u0010\u0012\u001a\u00020\u0007HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0002HÆ\u0003J\t\u0010\u0014\u001a\u00020\nHÆ\u0003J'\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00022\b\b\u0002\u0010\t\u001a\u00020\nHÆ\u0001J\u0013\u0010\u0016\u001a\u00020\n2\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018HÖ\u0003J\t\u0010\u0019\u001a\u00020\u0007HÖ\u0001J\t\u0010\u001a\u001a\u00020\u001bHÖ\u0001J\u0018\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u001e\u001a\u00020\u0007H\u0016R\u0014\u0010\b\u001a\u00020\u0002X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0014\u0010\u0006\u001a\u00020\u0007X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0014\u0010\t\u001a\u00020\nX\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011¨\u0006 "}, d2 = {"Lcom/android/systemui/flags/FloatFlag;", "Lcom/android/systemui/flags/ParcelableFlag;", "", "parcel", "Landroid/os/Parcel;", "(Landroid/os/Parcel;)V", "id", "", "default", "teamfood", "", "(IFZ)V", "getDefault", "()Ljava/lang/Float;", "getId", "()I", "getTeamfood", "()Z", "component1", "component2", "component3", "copy", "equals", "other", "", "hashCode", "toString", "", "writeToParcel", "", "flags", "Companion", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: Flag.kt */
public final class FloatFlag implements ParcelableFlag<Float> {
    public static final Parcelable.Creator<FloatFlag> CREATOR = new FloatFlag$Companion$CREATOR$1();
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);

    /* renamed from: default  reason: not valid java name */
    private final float f2default;
    private final int id;
    private final boolean teamfood;

    public FloatFlag(int i) {
        this(i, 0.0f, false, 6, (DefaultConstructorMarker) null);
    }

    public FloatFlag(int i, float f) {
        this(i, f, false, 4, (DefaultConstructorMarker) null);
    }

    public /* synthetic */ FloatFlag(Parcel parcel, DefaultConstructorMarker defaultConstructorMarker) {
        this(parcel);
    }

    public static /* synthetic */ FloatFlag copy$default(FloatFlag floatFlag, int i, float f, boolean z, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            i = floatFlag.getId();
        }
        if ((i2 & 2) != 0) {
            f = floatFlag.getDefault().floatValue();
        }
        if ((i2 & 4) != 0) {
            z = floatFlag.getTeamfood();
        }
        return floatFlag.copy(i, f, z);
    }

    public final int component1() {
        return getId();
    }

    public final float component2() {
        return getDefault().floatValue();
    }

    public final boolean component3() {
        return getTeamfood();
    }

    public final FloatFlag copy(int i, float f, boolean z) {
        return new FloatFlag(i, f, z);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FloatFlag)) {
            return false;
        }
        FloatFlag floatFlag = (FloatFlag) obj;
        return getId() == floatFlag.getId() && Intrinsics.areEqual((Object) getDefault(), (Object) floatFlag.getDefault()) && getTeamfood() == floatFlag.getTeamfood();
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
        return "FloatFlag(id=" + getId() + ", default=" + getDefault().floatValue() + ", teamfood=" + getTeamfood() + ')';
    }

    public FloatFlag(int i, float f, boolean z) {
        this.id = i;
        this.f2default = f;
        this.teamfood = z;
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public /* synthetic */ FloatFlag(int i, float f, boolean z, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this(i, (i2 & 2) != 0 ? 0.0f : f, (i2 & 4) != 0 ? false : z);
    }

    public int describeContents() {
        return ParcelableFlag.DefaultImpls.describeContents(this);
    }

    public int getId() {
        return this.id;
    }

    public Float getDefault() {
        return Float.valueOf(this.f2default);
    }

    public boolean getTeamfood() {
        return this.teamfood;
    }

    @Metadata(d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u0016\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u00048\u0006X\u0004¢\u0006\u0002\n\u0000¨\u0006\u0006"}, d2 = {"Lcom/android/systemui/flags/FloatFlag$Companion;", "", "()V", "CREATOR", "Landroid/os/Parcelable$Creator;", "Lcom/android/systemui/flags/FloatFlag;", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: Flag.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    private FloatFlag(Parcel parcel) {
        this(parcel.readInt(), parcel.readFloat(), false, 4, (DefaultConstructorMarker) null);
    }

    public void writeToParcel(Parcel parcel, int i) {
        Intrinsics.checkNotNullParameter(parcel, "parcel");
        parcel.writeInt(getId());
        parcel.writeFloat(getDefault().floatValue());
    }
}
