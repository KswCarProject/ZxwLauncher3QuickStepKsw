package com.android.systemui.plugins;

import android.os.Parcelable;
import com.android.systemui.plugins.annotations.ProvidesInterface;
import java.util.ArrayList;

@ProvidesInterface(action = "com.android.systemui.action.PLUGIN_ONE_SEARCH", version = 6)
public interface OneSearch extends Plugin {
    public static final String ACTION = "com.android.systemui.action.PLUGIN_ONE_SEARCH";
    public static final int VERSION = 6;

    Parcelable getImageBitmap(String str);

    ArrayList<Parcelable> getSuggests(Parcelable parcelable);

    void notifyEvent(Parcelable parcelable);

    void setSuggestOnChrome(boolean z);

    void warmUp();
}
