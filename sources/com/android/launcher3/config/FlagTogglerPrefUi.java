package com.android.launcher3.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Process;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.SwitchPreference;
import com.android.launcher3.R;
import com.android.launcher3.config.FeatureFlags;

public final class FlagTogglerPrefUi {
    private static final String TAG = "FlagTogglerPrefFrag";
    /* access modifiers changed from: private */
    public final Context mContext;
    private final PreferenceDataStore mDataStore = new PreferenceDataStore() {
        public void putBoolean(String str, boolean z) {
            for (FeatureFlags.DebugFlag next : FeatureFlags.getDebugFlags()) {
                if (next.key.equals(str)) {
                    SharedPreferences.Editor edit = FlagTogglerPrefUi.this.mContext.getSharedPreferences(FeatureFlags.FLAGS_PREF_NAME, 0).edit();
                    if (z == next.defaultValue) {
                        edit.remove(str).apply();
                    } else {
                        edit.putBoolean(str, z).apply();
                    }
                    FlagTogglerPrefUi.this.updateMenu();
                }
            }
        }

        public boolean getBoolean(String str, boolean z) {
            for (FeatureFlags.DebugFlag next : FeatureFlags.getDebugFlags()) {
                if (next.key.equals(str)) {
                    return FlagTogglerPrefUi.this.mContext.getSharedPreferences(FeatureFlags.FLAGS_PREF_NAME, 0).getBoolean(str, next.defaultValue);
                }
            }
            return z;
        }
    };
    private final PreferenceFragmentCompat mFragment;
    private final SharedPreferences mSharedPreferences;

    public FlagTogglerPrefUi(PreferenceFragmentCompat preferenceFragmentCompat) {
        this.mFragment = preferenceFragmentCompat;
        FragmentActivity activity = preferenceFragmentCompat.getActivity();
        this.mContext = activity;
        this.mSharedPreferences = activity.getSharedPreferences(FeatureFlags.FLAGS_PREF_NAME, 0);
    }

    public void applyTo(PreferenceGroup preferenceGroup) {
        for (FeatureFlags.DebugFlag next : FeatureFlags.getDebugFlags()) {
            SwitchPreference switchPreference = new SwitchPreference(this.mContext);
            switchPreference.setKey(next.key);
            switchPreference.setDefaultValue(Boolean.valueOf(next.defaultValue));
            switchPreference.setChecked(getFlagStateFromSharedPrefs(next));
            switchPreference.setTitle((CharSequence) next.key);
            updateSummary(switchPreference, next);
            switchPreference.setPreferenceDataStore(this.mDataStore);
            preferenceGroup.addPreference(switchPreference);
        }
        updateMenu();
    }

    private void updateSummary(SwitchPreference switchPreference, FeatureFlags.DebugFlag debugFlag) {
        String str = "";
        String str2 = debugFlag.defaultValue ? str : "<b>OVERRIDDEN</b><br>";
        if (debugFlag.defaultValue) {
            str = "<b>OVERRIDDEN</b><br>";
        }
        switchPreference.setSummaryOn((CharSequence) Html.fromHtml(str2 + debugFlag.description));
        switchPreference.setSummaryOff((CharSequence) Html.fromHtml(str + debugFlag.description));
    }

    /* access modifiers changed from: private */
    public void updateMenu() {
        this.mFragment.setHasOptionsMenu(anyChanged());
        this.mFragment.getActivity().invalidateOptionsMenu();
    }

    public void onCreateOptionsMenu(Menu menu) {
        if (anyChanged()) {
            menu.add(0, R.id.menu_apply_flags, 0, "Apply").setShowAsAction(2);
        }
    }

    public void onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.menu_apply_flags) {
            this.mSharedPreferences.edit().commit();
            Log.e(TAG, "Killing launcher process " + Process.myPid() + " to apply new flag values");
            System.exit(0);
        }
    }

    public void onStop() {
        if (anyChanged()) {
            Toast.makeText(this.mContext, "Flag won't be applied until you restart launcher", 1).show();
        }
    }

    private boolean getFlagStateFromSharedPrefs(FeatureFlags.DebugFlag debugFlag) {
        return this.mDataStore.getBoolean(debugFlag.key, debugFlag.defaultValue);
    }

    private boolean anyChanged() {
        for (FeatureFlags.DebugFlag next : FeatureFlags.getDebugFlags()) {
            if (getFlagStateFromSharedPrefs(next) != next.get()) {
                return true;
            }
        }
        return false;
    }
}
