package com.android.systemui.flags;

import android.os.Parcel;
import android.os.Parcelable;
import com.android.systemui.flags.ParcelableFlag;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\r\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\b\b\u0018\u0000 \u001e2\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u001eB\u000f\b\u0012\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0002\u0010\u0005B#\b\u0007\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\u0002\u0012\b\b\u0002\u0010\t\u001a\u00020\n¢\u0006\u0002\u0010\u000bJ\t\u0010\u0012\u001a\u00020\u0007HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0002HÆ\u0003J\t\u0010\u0014\u001a\u00020\nHÆ\u0003J'\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00022\b\b\u0002\u0010\t\u001a\u00020\nHÆ\u0001J\u0013\u0010\u0016\u001a\u00020\n2\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018HÖ\u0003J\t\u0010\u0019\u001a\u00020\u0007HÖ\u0001J\t\u0010\u001a\u001a\u00020\u0002HÖ\u0001J\u0018\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u001d\u001a\u00020\u0007H\u0016R\u0014\u0010\b\u001a\u00020\u0002X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0014\u0010\u0006\u001a\u00020\u0007X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0014\u0010\t\u001a\u00020\nX\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011¨\u0006\u001f"}, d2 = {"Lcom/android/systemui/flags/StringFlag;", "Lcom/android/systemui/flags/ParcelableFlag;", "", "parcel", "Landroid/os/Parcel;", "(Landroid/os/Parcel;)V", "id", "", "default", "teamfood", "", "(ILjava/lang/String;Z)V", "getDefault", "()Ljava/lang/String;", "getId", "()I", "getTeamfood", "()Z", "component1", "component2", "component3", "copy", "equals", "other", "", "hashCode", "toString", "writeToParcel", "", "flags", "Companion", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: Flag.kt */
public final class StringFlag implements ParcelableFlag<String> {
    public static final Parcelable.Creator<StringFlag> CREATOR = new StringFlag$Companion$CREATOR$1();
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);

    /* renamed from: default  reason: not valid java name */
    private final String f5default;
    private final int id;
    private final boolean teamfood;

    public StringFlag(int i) {
        this(i, (String) null, false, 6, (DefaultConstructorMarker) null);
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public StringFlag(int i, String str) {
        this(i, str, false, 4, (DefaultConstructorMarker) null);
        Intrinsics.checkNotNullParameter(str, "default");
    }

    public /* synthetic */ StringFlag(Parcel parcel, DefaultConstructorMarker defaultConstructorMarker) {
        this(parcel);
    }

    public static /* synthetic */ StringFlag copy$default(StringFlag stringFlag, int i, String str, boolean z, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            i = stringFlag.getId();
        }
        if ((i2 & 2) != 0) {
            str = stringFlag.getDefault();
        }
        if ((i2 & 4) != 0) {
            z = stringFlag.getTeamfood();
        }
        return stringFlag.copy(i, str, z);
    }

    public final int component1() {
        return getId();
    }

    public final String component2() {
        return getDefault();
    }

    public final boolean component3() {
        return getTeamfood();
    }

    public final StringFlag copy(int i, String str, boolean z) {
        Intrinsics.checkNotNullParameter(str, "default");
        return new StringFlag(i, str, z);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof StringFlag)) {
            return false;
        }
        StringFlag stringFlag = (StringFlag) obj;
        return getId() == stringFlag.getId() && Intrinsics.areEqual((Object) getDefault(), (Object) stringFlag.getDefault()) && getTeamfood() == stringFlag.getTeamfood();
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
        return "StringFlag(id=" + getId() + ", default=" + getDefault() + ", teamfood=" + getTeamfood() + ')';
    }

    public StringFlag(int i, String str, boolean z) {
        Intrinsics.checkNotNullParameter(str, "default");
        this.id = i;
        this.f5default = str;
        this.teamfood = z;
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public /* synthetic */ StringFlag(int i, String str, boolean z, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this(i, (i2 & 2) != 0 ? "" : str, (i2 & 4) != 0 ? false : z);
    }

    public int describeContents() {
        return ParcelableFlag.DefaultImpls.describeContents(this);
    }

    public int getId() {
        return this.id;
    }

    public String getDefault() {
        return this.f5default;
    }

    public boolean getTeamfood() {
        return this.teamfood;
    }

    @Metadata(d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u0016\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u00048\u0006X\u0004¢\u0006\u0002\n\u0000¨\u0006\u0006"}, d2 = {"Lcom/android/systemui/flags/StringFlag$Companion;", "", "()V", "CREATOR", "Landroid/os/Parcelable$Creator;", "Lcom/android/systemui/flags/StringFlag;", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: Flag.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private StringFlag(android.os.Parcel r7) {
        /*
            r6 = this;
            int r1 = r7.readInt()
            java.lang.String r7 = r7.readString()
            if (r7 != 0) goto L_0x000c
            java.lang.String r7 = ""
        L_0x000c:
            r2 = r7
            r3 = 0
            r4 = 4
            r5 = 0
            r0 = r6
            r0.<init>(r1, r2, r3, r4, r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.flags.StringFlag.<init>(android.os.Parcel):void");
    }

    public void writeToParcel(Parcel parcel, int i) {
        Intrinsics.checkNotNullParameter(parcel, "parcel");
        parcel.writeInt(getId());
        parcel.writeString(getDefault());
    }
}
