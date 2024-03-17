package com.android.systemui.shared.system.smartspace;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\u0018\u0000 \u001f2\u00020\u0001:\u0001\u001fB\u000f\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004B\u0005¢\u0006\u0002\u0010\u0005J\b\u0010\u0018\u001a\u00020\rH\u0016J\b\u0010\u0019\u001a\u00020\u001aH\u0016J\u001a\u0010\u001b\u001a\u00020\u001c2\b\u0010\u001d\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u001e\u001a\u00020\rH\u0016R\u001a\u0010\u0006\u001a\u00020\u0007X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u001a\u0010\f\u001a\u00020\rX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R\u001a\u0010\u0012\u001a\u00020\u0013X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017¨\u0006 "}, d2 = {"Lcom/android/systemui/shared/system/smartspace/SmartspaceState;", "Landroid/os/Parcelable;", "parcel", "Landroid/os/Parcel;", "(Landroid/os/Parcel;)V", "()V", "boundsOnScreen", "Landroid/graphics/Rect;", "getBoundsOnScreen", "()Landroid/graphics/Rect;", "setBoundsOnScreen", "(Landroid/graphics/Rect;)V", "selectedPage", "", "getSelectedPage", "()I", "setSelectedPage", "(I)V", "visibleOnScreen", "", "getVisibleOnScreen", "()Z", "setVisibleOnScreen", "(Z)V", "describeContents", "toString", "", "writeToParcel", "", "dest", "flags", "CREATOR", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: SmartspaceState.kt */
public final class SmartspaceState implements Parcelable {
    public static final CREATOR CREATOR = new CREATOR((DefaultConstructorMarker) null);
    private Rect boundsOnScreen;
    private int selectedPage;
    private boolean visibleOnScreen;

    public int describeContents() {
        return 0;
    }

    public SmartspaceState() {
        this.boundsOnScreen = new Rect();
    }

    public final Rect getBoundsOnScreen() {
        return this.boundsOnScreen;
    }

    public final void setBoundsOnScreen(Rect rect) {
        Intrinsics.checkNotNullParameter(rect, "<set-?>");
        this.boundsOnScreen = rect;
    }

    public final int getSelectedPage() {
        return this.selectedPage;
    }

    public final void setSelectedPage(int i) {
        this.selectedPage = i;
    }

    public final boolean getVisibleOnScreen() {
        return this.visibleOnScreen;
    }

    public final void setVisibleOnScreen(boolean z) {
        this.visibleOnScreen = z;
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public SmartspaceState(Parcel parcel) {
        this();
        Intrinsics.checkNotNullParameter(parcel, "parcel");
        Parcelable readParcelable = parcel.readParcelable(AnonymousClass1.INSTANCE.getClass().getClassLoader());
        Intrinsics.checkNotNullExpressionValue(readParcelable, "parcel.readParcelable(Re…ss.javaClass.classLoader)");
        this.boundsOnScreen = (Rect) readParcelable;
        this.selectedPage = parcel.readInt();
        this.visibleOnScreen = parcel.readBoolean();
    }

    public void writeToParcel(Parcel parcel, int i) {
        if (parcel != null) {
            parcel.writeParcelable(this.boundsOnScreen, 0);
        }
        if (parcel != null) {
            parcel.writeInt(this.selectedPage);
        }
        if (parcel != null) {
            parcel.writeBoolean(this.visibleOnScreen);
        }
    }

    public String toString() {
        return "boundsOnScreen: " + this.boundsOnScreen + ", selectedPage: " + this.selectedPage + ", visibleOnScreen: " + this.visibleOnScreen;
    }

    @Metadata(d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0003\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00022\u0006\u0010\u0005\u001a\u00020\u0006H\u0016J\u001d\u0010\u0007\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0016¢\u0006\u0002\u0010\u000b¨\u0006\f"}, d2 = {"Lcom/android/systemui/shared/system/smartspace/SmartspaceState$CREATOR;", "Landroid/os/Parcelable$Creator;", "Lcom/android/systemui/shared/system/smartspace/SmartspaceState;", "()V", "createFromParcel", "parcel", "Landroid/os/Parcel;", "newArray", "", "size", "", "(I)[Lcom/android/systemui/shared/system/smartspace/SmartspaceState;", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: SmartspaceState.kt */
    public static final class CREATOR implements Parcelable.Creator<SmartspaceState> {
        public /* synthetic */ CREATOR(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private CREATOR() {
        }

        public SmartspaceState createFromParcel(Parcel parcel) {
            Intrinsics.checkNotNullParameter(parcel, "parcel");
            return new SmartspaceState(parcel);
        }

        public SmartspaceState[] newArray(int i) {
            return new SmartspaceState[i];
        }
    }
}
