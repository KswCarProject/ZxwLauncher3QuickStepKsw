package androidx.slice.core;

import android.app.PendingIntent;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.SliceItem;
import com.android.launcher3.LauncherSettings;

public class SliceActionImpl implements SliceAction {
    private PendingIntent mAction;
    private SliceItem mActionItem;
    private CharSequence mContentDescription;
    private IconCompat mIcon;
    private int mImageMode;
    private boolean mIsActivity;
    private boolean mIsChecked;
    private boolean mIsToggle;
    private int mPriority;
    private SliceItem mSliceItem;
    private CharSequence mTitle;

    public SliceActionImpl(PendingIntent pendingIntent, IconCompat iconCompat, CharSequence charSequence) {
        this(pendingIntent, iconCompat, 0, charSequence);
    }

    public SliceActionImpl(PendingIntent pendingIntent, IconCompat iconCompat, int i, CharSequence charSequence) {
        this.mImageMode = 3;
        this.mPriority = -1;
        this.mAction = pendingIntent;
        this.mIcon = iconCompat;
        this.mTitle = charSequence;
        this.mImageMode = i;
    }

    public SliceActionImpl(PendingIntent pendingIntent, IconCompat iconCompat, CharSequence charSequence, boolean z) {
        this(pendingIntent, iconCompat, 0, charSequence);
        this.mIsChecked = z;
        this.mIsToggle = true;
    }

    public SliceActionImpl(PendingIntent pendingIntent, CharSequence charSequence, boolean z) {
        this.mImageMode = 3;
        this.mPriority = -1;
        this.mAction = pendingIntent;
        this.mTitle = charSequence;
        this.mIsToggle = true;
        this.mIsChecked = z;
    }

    public SliceActionImpl(SliceItem sliceItem) {
        this.mImageMode = 3;
        int i = -1;
        this.mPriority = -1;
        this.mSliceItem = sliceItem;
        SliceItem find = SliceQuery.find(sliceItem, "action");
        if (find != null) {
            this.mActionItem = find;
            this.mAction = find.getAction();
            SliceItem find2 = SliceQuery.find(find.getSlice(), "image");
            if (find2 != null) {
                this.mIcon = find2.getIcon();
                this.mImageMode = find2.hasHint("no_tint") ? find2.hasHint("large") ? 2 : 1 : 0;
            }
            SliceItem find3 = SliceQuery.find(find.getSlice(), "text", LauncherSettings.Favorites.TITLE, (String) null);
            if (find3 != null) {
                this.mTitle = find3.getText();
            }
            SliceItem findSubtype = SliceQuery.findSubtype(find.getSlice(), "text", "content_description");
            if (findSubtype != null) {
                this.mContentDescription = findSubtype.getText();
            }
            boolean equals = "toggle".equals(find.getSubType());
            this.mIsToggle = equals;
            if (equals) {
                this.mIsChecked = find.hasHint("selected");
            }
            this.mIsActivity = this.mSliceItem.hasHint(SliceHints.HINT_ACTIVITY);
            SliceItem findSubtype2 = SliceQuery.findSubtype(find.getSlice(), "int", "priority");
            this.mPriority = findSubtype2 != null ? findSubtype2.getInt() : i;
        }
    }

    public SliceActionImpl setContentDescription(CharSequence charSequence) {
        this.mContentDescription = charSequence;
        return this;
    }

    public SliceActionImpl setChecked(boolean z) {
        this.mIsChecked = z;
        return this;
    }

    public SliceActionImpl setPriority(int i) {
        this.mPriority = i;
        return this;
    }

    public PendingIntent getAction() {
        PendingIntent pendingIntent = this.mAction;
        return pendingIntent != null ? pendingIntent : this.mActionItem.getAction();
    }

    public SliceItem getActionItem() {
        return this.mActionItem;
    }

    public IconCompat getIcon() {
        return this.mIcon;
    }

    public CharSequence getTitle() {
        return this.mTitle;
    }

    public CharSequence getContentDescription() {
        return this.mContentDescription;
    }

    public int getPriority() {
        return this.mPriority;
    }

    public boolean isToggle() {
        return this.mIsToggle;
    }

    public boolean isChecked() {
        return this.mIsChecked;
    }

    public int getImageMode() {
        return this.mImageMode;
    }

    public boolean isDefaultToggle() {
        return this.mIsToggle && this.mIcon == null;
    }

    public SliceItem getSliceItem() {
        return this.mSliceItem;
    }

    public boolean isActivity() {
        return this.mIsActivity;
    }

    public Slice buildSlice(Slice.Builder builder) {
        return builder.addHints("shortcut").addAction(this.mAction, buildSliceContent(builder).build(), getSubtype()).build();
    }

    public Slice buildPrimaryActionSlice(Slice.Builder builder) {
        return buildSliceContent(builder).addHints("shortcut", LauncherSettings.Favorites.TITLE).build();
    }

    private Slice.Builder buildSliceContent(Slice.Builder builder) {
        Slice.Builder builder2 = new Slice.Builder(builder);
        IconCompat iconCompat = this.mIcon;
        if (iconCompat != null) {
            builder2.addIcon(iconCompat, (String) null, this.mImageMode == 0 ? new String[0] : new String[]{"no_tint"});
        }
        CharSequence charSequence = this.mTitle;
        if (charSequence != null) {
            builder2.addText(charSequence, (String) null, LauncherSettings.Favorites.TITLE);
        }
        CharSequence charSequence2 = this.mContentDescription;
        if (charSequence2 != null) {
            builder2.addText(charSequence2, "content_description", new String[0]);
        }
        if (this.mIsToggle && this.mIsChecked) {
            builder2.addHints("selected");
        }
        int i = this.mPriority;
        if (i != -1) {
            builder2.addInt(i, "priority", new String[0]);
        }
        if (this.mIsActivity) {
            builder.addHints(SliceHints.HINT_ACTIVITY);
        }
        return builder2;
    }

    public String getSubtype() {
        if (this.mIsToggle) {
            return "toggle";
        }
        return null;
    }

    public void setActivity(boolean z) {
        this.mIsActivity = z;
    }
}
