package com.android.systemui.flags;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.provider.Settings;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0007\u001a\u00020\u0006J\u001e\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eJ\u000e\u0010\u000f\u001a\u00020\t2\u0006\u0010\r\u001a\u00020\u000eR\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000¨\u0006\u0010"}, d2 = {"Lcom/android/systemui/flags/FlagSettingsHelper;", "", "contentResolver", "Landroid/content/ContentResolver;", "(Landroid/content/ContentResolver;)V", "getString", "", "key", "registerContentObserver", "", "name", "notifyForDescendants", "", "observer", "Landroid/database/ContentObserver;", "unregisterContentObserver", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: FlagSettingsHelper.kt */
public final class FlagSettingsHelper {
    private final ContentResolver contentResolver;

    public FlagSettingsHelper(ContentResolver contentResolver2) {
        Intrinsics.checkNotNullParameter(contentResolver2, "contentResolver");
        this.contentResolver = contentResolver2;
    }

    public final String getString(String str) {
        Intrinsics.checkNotNullParameter(str, "key");
        return Settings.Secure.getString(this.contentResolver, str);
    }

    public final void registerContentObserver(String str, boolean z, ContentObserver contentObserver) {
        Intrinsics.checkNotNullParameter(str, "name");
        Intrinsics.checkNotNullParameter(contentObserver, "observer");
        this.contentResolver.registerContentObserver(Settings.Secure.getUriFor(str), z, contentObserver);
    }

    public final void unregisterContentObserver(ContentObserver contentObserver) {
        Intrinsics.checkNotNullParameter(contentObserver, "observer");
        this.contentResolver.unregisterContentObserver(contentObserver);
    }
}
