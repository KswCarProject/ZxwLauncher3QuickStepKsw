package com.android.systemui.shared.rotation;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0005\u0018\u00002\u00020\u0001:\u0001\u000fB\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003¢\u0006\u0002\u0010\u0006J\u001e\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000bJ\u0010\u0010\r\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\u0003H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000¨\u0006\u0010"}, d2 = {"Lcom/android/systemui/shared/rotation/FloatingRotationButtonPositionCalculator;", "", "defaultMargin", "", "taskbarMarginLeft", "taskbarMarginBottom", "(III)V", "calculatePosition", "Lcom/android/systemui/shared/rotation/FloatingRotationButtonPositionCalculator$Position;", "currentRotation", "taskbarVisible", "", "taskbarStashed", "resolveGravity", "rotation", "Position", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: FloatingRotationButtonPositionCalculator.kt */
public final class FloatingRotationButtonPositionCalculator {
    private final int defaultMargin;
    private final int taskbarMarginBottom;
    private final int taskbarMarginLeft;

    public FloatingRotationButtonPositionCalculator(int i, int i2, int i3) {
        this.defaultMargin = i;
        this.taskbarMarginLeft = i2;
        this.taskbarMarginBottom = i3;
    }

    public final Position calculatePosition(int i, boolean z, boolean z2) {
        int i2;
        boolean z3 = false;
        if ((i == 0 || i == 1) && z && !z2) {
            z3 = true;
        }
        int resolveGravity = resolveGravity(i);
        int i3 = z3 ? this.taskbarMarginLeft : this.defaultMargin;
        if (z3) {
            i2 = this.taskbarMarginBottom;
        } else {
            i2 = this.defaultMargin;
        }
        if ((resolveGravity & 5) == 5) {
            i3 = -i3;
        }
        if ((resolveGravity & 80) == 80) {
            i2 = -i2;
        }
        return new Position(resolveGravity, i3, i2);
    }

    @Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003¢\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003HÆ\u0003J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\t\u0010\r\u001a\u00020\u0003HÆ\u0003J'\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0012\u001a\u00020\u0003HÖ\u0001J\t\u0010\u0013\u001a\u00020\u0014HÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\bR\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\b¨\u0006\u0015"}, d2 = {"Lcom/android/systemui/shared/rotation/FloatingRotationButtonPositionCalculator$Position;", "", "gravity", "", "translationX", "translationY", "(III)V", "getGravity", "()I", "getTranslationX", "getTranslationY", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "toString", "", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: FloatingRotationButtonPositionCalculator.kt */
    public static final class Position {
        private final int gravity;
        private final int translationX;
        private final int translationY;

        public static /* synthetic */ Position copy$default(Position position, int i, int i2, int i3, int i4, Object obj) {
            if ((i4 & 1) != 0) {
                i = position.gravity;
            }
            if ((i4 & 2) != 0) {
                i2 = position.translationX;
            }
            if ((i4 & 4) != 0) {
                i3 = position.translationY;
            }
            return position.copy(i, i2, i3);
        }

        public final int component1() {
            return this.gravity;
        }

        public final int component2() {
            return this.translationX;
        }

        public final int component3() {
            return this.translationY;
        }

        public final Position copy(int i, int i2, int i3) {
            return new Position(i, i2, i3);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Position)) {
                return false;
            }
            Position position = (Position) obj;
            return this.gravity == position.gravity && this.translationX == position.translationX && this.translationY == position.translationY;
        }

        public int hashCode() {
            return (((Integer.hashCode(this.gravity) * 31) + Integer.hashCode(this.translationX)) * 31) + Integer.hashCode(this.translationY);
        }

        public String toString() {
            return "Position(gravity=" + this.gravity + ", translationX=" + this.translationX + ", translationY=" + this.translationY + ')';
        }

        public Position(int i, int i2, int i3) {
            this.gravity = i;
            this.translationX = i2;
            this.translationY = i3;
        }

        public final int getGravity() {
            return this.gravity;
        }

        public final int getTranslationX() {
            return this.translationX;
        }

        public final int getTranslationY() {
            return this.translationY;
        }
    }

    private final int resolveGravity(int i) {
        if (i == 0) {
            return 83;
        }
        if (i == 1) {
            return 85;
        }
        if (i == 2) {
            return 53;
        }
        if (i == 3) {
            return 51;
        }
        throw new IllegalArgumentException(Intrinsics.stringPlus("Invalid rotation ", Integer.valueOf(i)));
    }
}
