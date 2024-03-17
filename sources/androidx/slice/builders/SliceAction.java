package androidx.slice.builders;

import android.app.PendingIntent;
import android.graphics.drawable.Icon;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.core.SliceActionImpl;

public class SliceAction implements androidx.slice.core.SliceAction {
    private SliceActionImpl mSliceAction;

    public SliceAction(PendingIntent pendingIntent, Icon icon, CharSequence charSequence) {
        this(pendingIntent, icon, 0, charSequence);
    }

    public SliceAction(PendingIntent pendingIntent, Icon icon, int i, CharSequence charSequence) {
        this(pendingIntent, IconCompat.createFromIcon(icon), i, charSequence);
    }

    public SliceAction(PendingIntent pendingIntent, Icon icon, CharSequence charSequence, boolean z) {
        this(pendingIntent, IconCompat.createFromIcon(icon), charSequence, z);
    }

    public SliceAction(PendingIntent pendingIntent, IconCompat iconCompat, CharSequence charSequence) {
        this(pendingIntent, iconCompat, 0, charSequence);
    }

    public SliceAction(PendingIntent pendingIntent, IconCompat iconCompat, int i, CharSequence charSequence) {
        this.mSliceAction = new SliceActionImpl(pendingIntent, iconCompat, i, charSequence);
    }

    public SliceAction(PendingIntent pendingIntent, IconCompat iconCompat, CharSequence charSequence, boolean z) {
        this.mSliceAction = new SliceActionImpl(pendingIntent, iconCompat, charSequence, z);
    }

    public SliceAction(PendingIntent pendingIntent, CharSequence charSequence, boolean z) {
        this.mSliceAction = new SliceActionImpl(pendingIntent, charSequence, z);
    }

    public static SliceAction create(PendingIntent pendingIntent, IconCompat iconCompat, int i, CharSequence charSequence) {
        return new SliceAction(pendingIntent, iconCompat, i, charSequence);
    }

    public static SliceAction createDeeplink(PendingIntent pendingIntent, IconCompat iconCompat, int i, CharSequence charSequence) {
        SliceAction sliceAction = new SliceAction(pendingIntent, iconCompat, i, charSequence);
        sliceAction.mSliceAction.setActivity(true);
        return sliceAction;
    }

    public static SliceAction createToggle(PendingIntent pendingIntent, CharSequence charSequence, boolean z) {
        return new SliceAction(pendingIntent, charSequence, z);
    }

    public static SliceAction createToggle(PendingIntent pendingIntent, IconCompat iconCompat, CharSequence charSequence, boolean z) {
        return new SliceAction(pendingIntent, iconCompat, charSequence, z);
    }

    public SliceAction setContentDescription(CharSequence charSequence) {
        this.mSliceAction.setContentDescription(charSequence);
        return this;
    }

    public SliceAction setChecked(boolean z) {
        this.mSliceAction.setChecked(z);
        return this;
    }

    public SliceAction setPriority(int i) {
        this.mSliceAction.setPriority(i);
        return this;
    }

    public PendingIntent getAction() {
        return this.mSliceAction.getAction();
    }

    public IconCompat getIcon() {
        return this.mSliceAction.getIcon();
    }

    public CharSequence getTitle() {
        return this.mSliceAction.getTitle();
    }

    public boolean isActivity() {
        return this.mSliceAction.isActivity();
    }

    public CharSequence getContentDescription() {
        return this.mSliceAction.getContentDescription();
    }

    public int getPriority() {
        return this.mSliceAction.getPriority();
    }

    public boolean isToggle() {
        return this.mSliceAction.isToggle();
    }

    public boolean isChecked() {
        return this.mSliceAction.isChecked();
    }

    public int getImageMode() {
        return this.mSliceAction.getImageMode();
    }

    public boolean isDefaultToggle() {
        return this.mSliceAction.isDefaultToggle();
    }

    public Slice buildSlice(Slice.Builder builder) {
        return this.mSliceAction.buildSlice(builder);
    }

    public SliceActionImpl getImpl() {
        return this.mSliceAction;
    }

    public void setPrimaryAction(Slice.Builder builder) {
        builder.addAction(this.mSliceAction.getAction(), this.mSliceAction.buildPrimaryActionSlice(builder), this.mSliceAction.getSubtype());
    }
}
