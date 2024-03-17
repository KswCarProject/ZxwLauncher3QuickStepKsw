package com.android.launcher3.uioverrides;

import android.app.Person;
import android.content.Context;
import android.content.pm.ShortcutInfo;
import android.content.res.Resources;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.DisplayController;

public class ApiWrapper {
    public static final boolean TASKBAR_DRAWN_IN_PROCESS = true;

    public static Person[] getPersons(ShortcutInfo shortcutInfo) {
        Person[] persons = shortcutInfo.getPersons();
        return persons == null ? Utilities.EMPTY_PERSON_ARRAY : persons;
    }

    public static int getHotseatEndOffset(Context context) {
        if (DisplayController.getNavigationMode(context) != DisplayController.NavigationMode.THREE_BUTTONS) {
            return 0;
        }
        Resources resources = context.getResources();
        return (resources.getDimensionPixelSize(R.dimen.taskbar_nav_buttons_size) * 3) + resources.getDimensionPixelSize(R.dimen.taskbar_contextual_button_margin) + resources.getDimensionPixelSize(R.dimen.taskbar_hotseat_nav_spacing);
    }
}
