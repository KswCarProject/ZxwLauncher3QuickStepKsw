package com.android.launcher3.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherFiles;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.settings.SettingsActivity;
import com.android.launcher3.states.RotationHelper;
import com.android.launcher3.uioverrides.plugins.PluginManagerWrapper;
import java.util.Collections;
import java.util.List;

public class SettingsActivity extends FragmentActivity implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback, PreferenceFragmentCompat.OnPreferenceStartScreenCallback, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final int DELAY_HIGHLIGHT_DURATION_MILLIS = 600;
    private static final String DEVELOPER_OPTIONS_KEY = "pref_developer_options";
    static final String EXTRA_FRAGMENT = ":settings:fragment";
    static final String EXTRA_FRAGMENT_ARGS = ":settings:fragment_args";
    public static final String EXTRA_FRAGMENT_ARG_KEY = ":settings:fragment_args_key";
    public static final String EXTRA_SHOW_FRAGMENT_ARGS = ":settings:show_fragment_args";
    private static final String FLAGS_PREFERENCE_KEY = "flag_toggler";
    private static final String NOTIFICATION_DOTS_PREFERENCE_KEY = "pref_icon_badging";
    public static final String SAVE_HIGHLIGHTED_KEY = "android:preference_highlighted";
    private static final List<String> VALID_PREFERENCE_FRAGMENTS;

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
    }

    static {
        List<String> list;
        if (!Utilities.IS_DEBUG_DEVICE) {
            list = Collections.emptyList();
        } else {
            list = Collections.singletonList(DeveloperOptionsFragment.class.getName());
        }
        VALID_PREFERENCE_FRAGMENTS = list;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.settings_activity);
        setActionBar((Toolbar) findViewById(R.id.action_bar));
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_FRAGMENT) || intent.hasExtra(EXTRA_FRAGMENT_ARGS)) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (bundle == null) {
            Bundle bundleExtra = intent.getBundleExtra(EXTRA_FRAGMENT_ARGS);
            if (bundleExtra == null) {
                bundleExtra = new Bundle();
            }
            String stringExtra = intent.getStringExtra(EXTRA_FRAGMENT_ARG_KEY);
            if (!TextUtils.isEmpty(stringExtra)) {
                bundleExtra.putString(EXTRA_FRAGMENT_ARG_KEY, stringExtra);
            }
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            Fragment instantiate = supportFragmentManager.getFragmentFactory().instantiate(getClassLoader(), getPreferenceFragment());
            instantiate.setArguments(bundleExtra);
            supportFragmentManager.beginTransaction().replace(R.id.content_frame, instantiate).commit();
        }
        Utilities.getPrefs(getApplicationContext()).registerOnSharedPreferenceChangeListener(this);
    }

    private String getPreferenceFragment() {
        String stringExtra = getIntent().getStringExtra(EXTRA_FRAGMENT);
        String string = getString(R.string.settings_fragment_name);
        if (TextUtils.isEmpty(stringExtra)) {
            return string;
        }
        if (stringExtra.equals(string) || VALID_PREFERENCE_FRAGMENTS.contains(stringExtra)) {
            return stringExtra;
        }
        throw new IllegalArgumentException("Invalid fragment for this activity: " + stringExtra);
    }

    private boolean startPreference(String str, Bundle bundle, String str2) {
        if (Utilities.ATLEAST_P && getSupportFragmentManager().isStateSaved()) {
            return false;
        }
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        Fragment instantiate = supportFragmentManager.getFragmentFactory().instantiate(getClassLoader(), str);
        if (instantiate instanceof DialogFragment) {
            instantiate.setArguments(bundle);
            ((DialogFragment) instantiate).show(supportFragmentManager, str2);
            return true;
        }
        startActivity(new Intent(this, SettingsActivity.class).putExtra(EXTRA_FRAGMENT, str).putExtra(EXTRA_FRAGMENT_ARGS, bundle));
        return true;
    }

    public boolean onPreferenceStartFragment(PreferenceFragmentCompat preferenceFragmentCompat, Preference preference) {
        return startPreference(preference.getFragment(), preference.getExtras(), preference.getKey());
    }

    public boolean onPreferenceStartScreen(PreferenceFragmentCompat preferenceFragmentCompat, PreferenceScreen preferenceScreen) {
        Bundle bundle = new Bundle();
        bundle.putString("androidx.preference.PreferenceFragmentCompat.PREFERENCE_ROOT", preferenceScreen.getKey());
        return startPreference(getString(R.string.settings_fragment_name), bundle, preferenceScreen.getKey());
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        onBackPressed();
        return true;
    }

    public static class LauncherSettingsFragment extends PreferenceFragmentCompat {
        private Preference mDeveloperOptionPref;
        private String mHighLightKey;
        private boolean mPreferenceHighlighted = false;

        /* access modifiers changed from: protected */
        public String getParentKeyForPref(String str) {
            return null;
        }

        public void onCreatePreferences(Bundle bundle, String str) {
            String str2;
            Bundle arguments = getArguments();
            if (arguments == null) {
                str2 = null;
            } else {
                str2 = arguments.getString(SettingsActivity.EXTRA_FRAGMENT_ARG_KEY);
            }
            this.mHighLightKey = str2;
            if (str == null && !TextUtils.isEmpty(str2)) {
                str = getParentKeyForPref(this.mHighLightKey);
            }
            if (bundle != null) {
                this.mPreferenceHighlighted = bundle.getBoolean(SettingsActivity.SAVE_HIGHLIGHTED_KEY);
            }
            getPreferenceManager().setSharedPreferencesName(LauncherFiles.SHARED_PREFERENCES_KEY);
            setPreferencesFromResource(R.xml.launcher_preferences, str);
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            for (int preferenceCount = preferenceScreen.getPreferenceCount() - 1; preferenceCount >= 0; preferenceCount--) {
                Preference preference = preferenceScreen.getPreference(preferenceCount);
                if (!initPreference(preference)) {
                    preferenceScreen.removePreference(preference);
                }
            }
            if (getActivity() != null && !TextUtils.isEmpty(getPreferenceScreen().getTitle())) {
                CharSequence title = getPreferenceScreen().getTitle();
                Resources resources = getResources();
                int i = R.string.search_pref_screen_title;
                if (title.equals(resources.getString(R.string.search_pref_screen_title))) {
                    DeviceProfile deviceProfile = InvariantDeviceProfile.INSTANCE.lambda$get$1$MainThreadInitializedObject(getContext()).getDeviceProfile(getContext());
                    PreferenceScreen preferenceScreen2 = getPreferenceScreen();
                    if (deviceProfile.isTablet) {
                        i = R.string.search_pref_screen_title_tablet;
                    }
                    preferenceScreen2.setTitle(i);
                }
                getActivity().setTitle(getPreferenceScreen().getTitle());
            }
        }

        public void onViewCreated(View view, Bundle bundle) {
            super.onViewCreated(view, bundle);
            RecyclerView listView = getListView();
            listView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener(listView.getPaddingBottom()) {
                public final /* synthetic */ int f$0;

                {
                    this.f$0 = r1;
                }

                public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                    return view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), this.f$0 + windowInsets.getSystemWindowInsetBottom());
                }
            });
        }

        public void onSaveInstanceState(Bundle bundle) {
            super.onSaveInstanceState(bundle);
            bundle.putBoolean(SettingsActivity.SAVE_HIGHLIGHTED_KEY, this.mPreferenceHighlighted);
        }

        /* access modifiers changed from: protected */
        public boolean initPreference(Preference preference) {
            String key = preference.getKey();
            key.hashCode();
            char c = 65535;
            switch (key.hashCode()) {
                case -1997663219:
                    if (key.equals(SettingsActivity.DEVELOPER_OPTIONS_KEY)) {
                        c = 0;
                        break;
                    }
                    break;
                case -789825333:
                    if (key.equals(RotationHelper.ALLOW_ROTATION_PREFERENCE_KEY)) {
                        c = 1;
                        break;
                    }
                    break;
                case 1623730635:
                    if (key.equals(SettingsActivity.FLAGS_PREFERENCE_KEY)) {
                        c = 2;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    this.mDeveloperOptionPref = preference;
                    return updateDeveloperOption();
                case 1:
                    DeviceProfile deviceProfile = InvariantDeviceProfile.INSTANCE.lambda$get$1$MainThreadInitializedObject(getContext()).getDeviceProfile(getContext());
                    if (deviceProfile.isTablet) {
                        return false;
                    }
                    preference.setDefaultValue(Boolean.valueOf(RotationHelper.getAllowRotationDefaultValue(deviceProfile)));
                    return true;
                case 2:
                    return FeatureFlags.showFlagTogglerUi(getContext());
                default:
                    return true;
            }
        }

        private boolean updateDeveloperOption() {
            boolean z = FeatureFlags.showFlagTogglerUi(getContext()) || PluginManagerWrapper.hasPlugins(getContext());
            Preference preference = this.mDeveloperOptionPref;
            if (preference != null) {
                preference.setEnabled(z);
                if (z) {
                    getPreferenceScreen().addPreference(this.mDeveloperOptionPref);
                } else {
                    getPreferenceScreen().removePreference(this.mDeveloperOptionPref);
                }
            }
            return z;
        }

        public void onResume() {
            super.onResume();
            updateDeveloperOption();
            if (isAdded() && !this.mPreferenceHighlighted) {
                PreferenceHighlighter createHighlighter = createHighlighter();
                if (createHighlighter != null) {
                    getView().postDelayed(createHighlighter, 600);
                    this.mPreferenceHighlighted = true;
                    return;
                }
                requestAccessibilityFocus(getListView());
            }
        }

        private PreferenceHighlighter createHighlighter() {
            PreferenceScreen preferenceScreen;
            if (TextUtils.isEmpty(this.mHighLightKey) || (preferenceScreen = getPreferenceScreen()) == null) {
                return null;
            }
            RecyclerView listView = getListView();
            int preferenceAdapterPosition = ((PreferenceGroup.PreferencePositionCallback) listView.getAdapter()).getPreferenceAdapterPosition(this.mHighLightKey);
            if (preferenceAdapterPosition >= 0) {
                return new PreferenceHighlighter(listView, preferenceAdapterPosition, preferenceScreen.findPreference(this.mHighLightKey));
            }
            return null;
        }

        private void requestAccessibilityFocus(RecyclerView recyclerView) {
            recyclerView.post(new Runnable() {
                public final void run() {
                    SettingsActivity.LauncherSettingsFragment.lambda$requestAccessibilityFocus$1(RecyclerView.this);
                }
            });
        }

        static /* synthetic */ void lambda$requestAccessibilityFocus$1(RecyclerView recyclerView) {
            if (!recyclerView.hasFocus() && recyclerView.getChildCount() > 0) {
                recyclerView.getChildAt(0).performAccessibilityAction(64, (Bundle) null);
            }
        }
    }
}
