package com.android.launcher3.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.launcher3.R;
import com.android.launcher3.notification.NotificationListener;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.SettingsCache;

public class NotificationDotsPreference extends Preference implements SettingsCache.OnChangeListener {
    private static final String NOTIFICATION_ENABLED_LISTENERS = "enabled_notification_listeners";
    private final ContentObserver mListenerListObserver = new ContentObserver(Executors.MAIN_EXECUTOR.getHandler()) {
        public void onChange(boolean z) {
            NotificationDotsPreference.this.updateUI();
        }
    };
    private boolean mWidgetFrameVisible = false;

    public NotificationDotsPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public NotificationDotsPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public NotificationDotsPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public NotificationDotsPreference(Context context) {
        super(context);
    }

    public void onAttached() {
        super.onAttached();
        SettingsCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(getContext()).register(SettingsCache.NOTIFICATION_BADGING_URI, this);
        getContext().getContentResolver().registerContentObserver(Settings.Secure.getUriFor(NOTIFICATION_ENABLED_LISTENERS), false, this.mListenerListObserver);
        updateUI();
        Bundle bundle = new Bundle();
        bundle.putString(SettingsActivity.EXTRA_FRAGMENT_ARG_KEY, "notification_badging");
        setIntent(new Intent("android.settings.NOTIFICATION_SETTINGS").putExtra(SettingsActivity.EXTRA_SHOW_FRAGMENT_ARGS, bundle));
    }

    /* access modifiers changed from: private */
    public void updateUI() {
        onSettingsChanged(SettingsCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(getContext()).getValue(SettingsCache.NOTIFICATION_BADGING_URI));
    }

    public void onDetached() {
        super.onDetached();
        SettingsCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(getContext()).unregister(SettingsCache.NOTIFICATION_BADGING_URI, this);
        getContext().getContentResolver().unregisterContentObserver(this.mListenerListObserver);
    }

    private void setWidgetFrameVisible(boolean z) {
        if (this.mWidgetFrameVisible != z) {
            this.mWidgetFrameVisible = z;
            notifyChanged();
        }
    }

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(16908312);
        if (findViewById != null) {
            findViewById.setVisibility(this.mWidgetFrameVisible ? 0 : 8);
        }
    }

    public void onSettingsChanged(boolean z) {
        String str;
        int i = z ? R.string.notification_dots_desc_on : R.string.notification_dots_desc_off;
        boolean z2 = true;
        if (z) {
            String string = Settings.Secure.getString(getContext().getContentResolver(), NOTIFICATION_ENABLED_LISTENERS);
            ComponentName componentName = new ComponentName(getContext(), NotificationListener.class);
            if (string == null || (!string.contains(componentName.flattenToString()) && !string.contains(componentName.flattenToShortString()))) {
                z2 = false;
            }
            if (!z2) {
                i = R.string.title_missing_notification_access;
            }
        }
        setWidgetFrameVisible(!z2);
        if (z2) {
            str = null;
        } else {
            str = NotificationAccessConfirmation.class.getName();
        }
        setFragment(str);
        setSummary(i);
    }

    public static class NotificationAccessConfirmation extends DialogFragment implements DialogInterface.OnClickListener {
        public Dialog onCreateDialog(Bundle bundle) {
            FragmentActivity activity = getActivity();
            return new AlertDialog.Builder(activity).setTitle(R.string.title_missing_notification_access).setMessage(activity.getString(R.string.msg_missing_notification_access, new Object[]{activity.getString(R.string.derived_app_name)})).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.title_change_settings, this).create();
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            ComponentName componentName = new ComponentName(getActivity(), NotificationListener.class);
            Bundle bundle = new Bundle();
            bundle.putString(SettingsActivity.EXTRA_FRAGMENT_ARG_KEY, componentName.flattenToString());
            getActivity().startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS").addFlags(268435456).putExtra(SettingsActivity.EXTRA_FRAGMENT_ARG_KEY, componentName.flattenToString()).putExtra(SettingsActivity.EXTRA_SHOW_FRAGMENT_ARGS, bundle));
        }
    }
}
