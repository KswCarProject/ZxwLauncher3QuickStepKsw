package androidx.slice.core;

import android.app.PendingIntent;
import androidx.core.graphics.drawable.IconCompat;

public interface SliceAction {
    PendingIntent getAction();

    CharSequence getContentDescription();

    IconCompat getIcon();

    int getImageMode();

    int getPriority();

    CharSequence getTitle();

    boolean isActivity();

    boolean isChecked();

    boolean isDefaultToggle();

    boolean isToggle();

    SliceAction setChecked(boolean z);

    SliceAction setContentDescription(CharSequence charSequence);

    SliceAction setPriority(int i);
}
