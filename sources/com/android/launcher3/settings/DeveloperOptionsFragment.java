package com.android.launcher3.settings;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.widget.EditText;
import android.widget.Toast;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreference;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.config.FlagTogglerPrefUi;
import com.android.launcher3.settings.DeveloperOptionsFragment;
import com.android.launcher3.uioverrides.plugins.PluginManagerWrapper;
import com.android.launcher3.util.OnboardingPrefs;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DeveloperOptionsFragment extends PreferenceFragmentCompat {
    private static final String ACTION_PLUGIN_SETTINGS = "com.android.systemui.action.PLUGIN_SETTINGS";
    private static final String PLUGIN_PERMISSION = "com.android.systemui.permission.PLUGIN";
    private FlagTogglerPrefUi mFlagTogglerPrefUi;
    private final BroadcastReceiver mPluginReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            DeveloperOptionsFragment.this.loadPluginPrefs();
        }
    };
    private PreferenceCategory mPluginsCategory;
    /* access modifiers changed from: private */
    public PreferenceScreen mPreferenceScreen;

    public void onCreatePreferences(Bundle bundle, String str) {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addDataScheme("package");
        getContext().registerReceiver(this.mPluginReceiver, intentFilter);
        getContext().registerReceiver(this.mPluginReceiver, new IntentFilter("android.intent.action.USER_UNLOCKED"));
        PreferenceScreen createPreferenceScreen = getPreferenceManager().createPreferenceScreen(getContext());
        this.mPreferenceScreen = createPreferenceScreen;
        setPreferenceScreen(createPreferenceScreen);
        initFlags();
        loadPluginPrefs();
        maybeAddSandboxCategory();
        addOnboardingPrefsCatergory();
        if (getActivity() != null) {
            getActivity().setTitle("Developer Options");
        }
    }

    /* access modifiers changed from: private */
    public void filterPreferences(String str, PreferenceGroup preferenceGroup) {
        int preferenceCount = preferenceGroup.getPreferenceCount();
        boolean z = false;
        int i = 0;
        for (int i2 = 0; i2 < preferenceCount; i2++) {
            Preference preference = preferenceGroup.getPreference(i2);
            if (preference instanceof PreferenceGroup) {
                filterPreferences(str, (PreferenceGroup) preference);
            } else {
                String replace = preference.getTitle().toString().toLowerCase().replace("_", " ");
                if (str.isEmpty() || replace.contains(str)) {
                    preference.setVisible(true);
                } else {
                    preference.setVisible(false);
                    i++;
                }
            }
        }
        if (i != preferenceCount) {
            z = true;
        }
        preferenceGroup.setVisible(z);
    }

    public void onViewCreated(View view, Bundle bundle) {
        String string;
        super.onViewCreated(view, bundle);
        EditText editText = (EditText) view.findViewById(R.id.filter_box);
        editText.setVisibility(0);
        editText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                String replace = editable.toString().toLowerCase().replace("_", " ");
                DeveloperOptionsFragment developerOptionsFragment = DeveloperOptionsFragment.this;
                developerOptionsFragment.filterPreferences(replace, developerOptionsFragment.mPreferenceScreen);
            }
        });
        if (!(getArguments() == null || (string = getArguments().getString(SettingsActivity.EXTRA_FRAGMENT_ARG_KEY)) == null)) {
            editText.setText(string);
        }
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

    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(this.mPluginReceiver);
    }

    private PreferenceCategory newCategory(String str) {
        PreferenceCategory preferenceCategory = new PreferenceCategory(getContext());
        preferenceCategory.setOrder(Integer.MAX_VALUE);
        preferenceCategory.setTitle((CharSequence) str);
        this.mPreferenceScreen.addPreference(preferenceCategory);
        return preferenceCategory;
    }

    private void initFlags() {
        if (FeatureFlags.showFlagTogglerUi(getContext())) {
            FlagTogglerPrefUi flagTogglerPrefUi = new FlagTogglerPrefUi(this);
            this.mFlagTogglerPrefUi = flagTogglerPrefUi;
            flagTogglerPrefUi.applyTo(newCategory("Feature flags"));
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        FlagTogglerPrefUi flagTogglerPrefUi = this.mFlagTogglerPrefUi;
        if (flagTogglerPrefUi != null) {
            flagTogglerPrefUi.onCreateOptionsMenu(menu);
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        FlagTogglerPrefUi flagTogglerPrefUi = this.mFlagTogglerPrefUi;
        if (flagTogglerPrefUi != null) {
            flagTogglerPrefUi.onOptionsItemSelected(menuItem);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void onStop() {
        FlagTogglerPrefUi flagTogglerPrefUi = this.mFlagTogglerPrefUi;
        if (flagTogglerPrefUi != null) {
            flagTogglerPrefUi.onStop();
        }
        super.onStop();
    }

    /* access modifiers changed from: private */
    public void loadPluginPrefs() {
        PreferenceCategory preferenceCategory = this.mPluginsCategory;
        if (preferenceCategory != null) {
            this.mPreferenceScreen.removePreference(preferenceCategory);
        }
        if (!PluginManagerWrapper.hasPlugins(getActivity())) {
            this.mPluginsCategory = null;
            return;
        }
        this.mPluginsCategory = newCategory("Plugins");
        PluginManagerWrapper pluginManagerWrapper = PluginManagerWrapper.INSTANCE.lambda$get$1$MainThreadInitializedObject(getContext());
        Context context = getContext();
        PackageManager packageManager = getContext().getPackageManager();
        Set<String> pluginActions = pluginManagerWrapper.getPluginActions();
        ArrayMap arrayMap = new ArrayMap();
        Set set = (Set) packageManager.getPackagesHoldingPermissions(new String[]{"com.android.systemui.permission.PLUGIN"}, 512).stream().map($$Lambda$DeveloperOptionsFragment$m3vEPJhhFLCcSJXTyudpRfbB0p0.INSTANCE).collect(Collectors.toSet());
        for (String next : pluginActions) {
            String name = toName(next);
            for (ResolveInfo next2 : packageManager.queryIntentServices(new Intent(next), 576)) {
                String str = next2.serviceInfo.packageName;
                if (set.contains(str)) {
                    Pair create = Pair.create(str, next2.serviceInfo.processName);
                    if (!arrayMap.containsKey(create)) {
                        arrayMap.put(create, new ArrayList());
                    }
                    ((ArrayList) arrayMap.get(create)).add(Pair.create(name, next2));
                }
            }
        }
        arrayMap.forEach(new BiConsumer(context, pluginManagerWrapper.getPluginEnabler()) {
            public final /* synthetic */ Context f$1;
            public final /* synthetic */ PreferenceDataStore f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void accept(Object obj, Object obj2) {
                DeveloperOptionsFragment.this.lambda$loadPluginPrefs$4$DeveloperOptionsFragment(this.f$1, this.f$2, (Pair) obj, (ArrayList) obj2);
            }
        });
    }

    public /* synthetic */ void lambda$loadPluginPrefs$4$DeveloperOptionsFragment(Context context, PreferenceDataStore preferenceDataStore, Pair pair, ArrayList arrayList) {
        List list = (List) arrayList.stream().map(new Function((String) pair.first) {
            public final /* synthetic */ String f$0;

            {
                this.f$0 = r1;
            }

            public final Object apply(Object obj) {
                return DeveloperOptionsFragment.lambda$loadPluginPrefs$2(this.f$0, (Pair) obj);
            }
        }).collect(Collectors.toList());
        if (!list.isEmpty()) {
            PluginPreference pluginPreference = new PluginPreference(context, (ResolveInfo) ((Pair) arrayList.get(0)).second, preferenceDataStore, list);
            pluginPreference.setSummary((CharSequence) "Plugins: " + ((String) arrayList.stream().map($$Lambda$DeveloperOptionsFragment$eG59A67HO4695P1q8Jk_JaKyWFM.INSTANCE).collect(Collectors.joining(", "))));
            this.mPluginsCategory.addPreference(pluginPreference);
        }
    }

    static /* synthetic */ ComponentName lambda$loadPluginPrefs$2(String str, Pair pair) {
        return new ComponentName(str, ((ResolveInfo) pair.second).serviceInfo.name);
    }

    static /* synthetic */ String lambda$loadPluginPrefs$3(Pair pair) {
        return (String) pair.first;
    }

    private void maybeAddSandboxCategory() {
        Context context = getContext();
        if (context != null) {
            Intent addFlags = new Intent("com.android.quickstep.action.GESTURE_SANDBOX").setPackage(context.getPackageName()).addFlags(268435456);
            if (addFlags.resolveActivity(context.getPackageManager()) != null) {
                PreferenceCategory newCategory = newCategory("Gesture Navigation Sandbox");
                newCategory.setSummary((CharSequence) "Learn and practice navigation gestures");
                Preference preference = new Preference(context);
                preference.setKey("launchOnboardingTutorial");
                preference.setTitle((CharSequence) "Launch Onboarding Tutorial");
                preference.setSummary((CharSequence) "Learn the basic navigation gestures.");
                preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(addFlags) {
                    public final /* synthetic */ Intent f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final boolean onPreferenceClick(Preference preference) {
                        return DeveloperOptionsFragment.this.lambda$maybeAddSandboxCategory$5$DeveloperOptionsFragment(this.f$1, preference);
                    }
                });
                newCategory.addPreference(preference);
                Preference preference2 = new Preference(context);
                preference2.setKey("launchBackTutorial");
                preference2.setTitle((CharSequence) "Launch Back Tutorial");
                preference2.setSummary((CharSequence) "Learn how to use the Back gesture");
                preference2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(addFlags) {
                    public final /* synthetic */ Intent f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final boolean onPreferenceClick(Preference preference) {
                        return DeveloperOptionsFragment.this.lambda$maybeAddSandboxCategory$6$DeveloperOptionsFragment(this.f$1, preference);
                    }
                });
                newCategory.addPreference(preference2);
                Preference preference3 = new Preference(context);
                preference3.setKey("launchHomeTutorial");
                preference3.setTitle((CharSequence) "Launch Home Tutorial");
                preference3.setSummary((CharSequence) "Learn how to use the Home gesture");
                preference3.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(addFlags) {
                    public final /* synthetic */ Intent f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final boolean onPreferenceClick(Preference preference) {
                        return DeveloperOptionsFragment.this.lambda$maybeAddSandboxCategory$7$DeveloperOptionsFragment(this.f$1, preference);
                    }
                });
                newCategory.addPreference(preference3);
                Preference preference4 = new Preference(context);
                preference4.setKey("launchOverviewTutorial");
                preference4.setTitle((CharSequence) "Launch Overview Tutorial");
                preference4.setSummary((CharSequence) "Learn how to use the Overview gesture");
                preference4.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(addFlags) {
                    public final /* synthetic */ Intent f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final boolean onPreferenceClick(Preference preference) {
                        return DeveloperOptionsFragment.this.lambda$maybeAddSandboxCategory$8$DeveloperOptionsFragment(this.f$1, preference);
                    }
                });
                newCategory.addPreference(preference4);
                Preference preference5 = new Preference(context);
                preference5.setKey("launchAssistantTutorial");
                preference5.setTitle((CharSequence) "Launch Assistant Tutorial");
                preference5.setSummary((CharSequence) "Learn how to use the Assistant gesture");
                preference5.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(addFlags) {
                    public final /* synthetic */ Intent f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final boolean onPreferenceClick(Preference preference) {
                        return DeveloperOptionsFragment.this.lambda$maybeAddSandboxCategory$9$DeveloperOptionsFragment(this.f$1, preference);
                    }
                });
                newCategory.addPreference(preference5);
                Preference preference6 = new Preference(context);
                preference6.setKey("launchSandboxMode");
                preference6.setTitle((CharSequence) "Launch Sandbox Mode");
                preference6.setSummary((CharSequence) "Practice navigation gestures");
                preference6.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(addFlags) {
                    public final /* synthetic */ Intent f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final boolean onPreferenceClick(Preference preference) {
                        return DeveloperOptionsFragment.this.lambda$maybeAddSandboxCategory$10$DeveloperOptionsFragment(this.f$1, preference);
                    }
                });
                newCategory.addPreference(preference6);
            }
        }
    }

    public /* synthetic */ boolean lambda$maybeAddSandboxCategory$5$DeveloperOptionsFragment(Intent intent, Preference preference) {
        startActivity(intent.putExtra("tutorial_steps", new String[]{"HOME_NAVIGATION", "BACK_NAVIGATION", "OVERVIEW_NAVIGATION"}));
        return true;
    }

    public /* synthetic */ boolean lambda$maybeAddSandboxCategory$6$DeveloperOptionsFragment(Intent intent, Preference preference) {
        startActivity(intent.putExtra("tutorial_steps", new String[]{"BACK_NAVIGATION"}));
        return true;
    }

    public /* synthetic */ boolean lambda$maybeAddSandboxCategory$7$DeveloperOptionsFragment(Intent intent, Preference preference) {
        startActivity(intent.putExtra("tutorial_steps", new String[]{"HOME_NAVIGATION"}));
        return true;
    }

    public /* synthetic */ boolean lambda$maybeAddSandboxCategory$8$DeveloperOptionsFragment(Intent intent, Preference preference) {
        startActivity(intent.putExtra("tutorial_steps", new String[]{"OVERVIEW_NAVIGATION"}));
        return true;
    }

    public /* synthetic */ boolean lambda$maybeAddSandboxCategory$9$DeveloperOptionsFragment(Intent intent, Preference preference) {
        startActivity(intent.putExtra("tutorial_steps", new String[]{"ASSISTANT"}));
        return true;
    }

    public /* synthetic */ boolean lambda$maybeAddSandboxCategory$10$DeveloperOptionsFragment(Intent intent, Preference preference) {
        startActivity(intent.putExtra("tutorial_steps", new String[]{"SANDBOX_MODE"}));
        return true;
    }

    private void addOnboardingPrefsCatergory() {
        PreferenceCategory newCategory = newCategory("Onboarding Flows");
        newCategory.setSummary((CharSequence) "Reset these if you want to see the education again.");
        for (Map.Entry next : OnboardingPrefs.ALL_PREF_KEYS.entrySet()) {
            String str = (String) next.getKey();
            Preference preference = new Preference(getContext());
            preference.setTitle((CharSequence) str);
            preference.setSummary((CharSequence) "Tap to reset");
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener((String[]) next.getValue(), str) {
                public final /* synthetic */ String[] f$1;
                public final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final boolean onPreferenceClick(Preference preference) {
                    return DeveloperOptionsFragment.this.lambda$addOnboardingPrefsCatergory$11$DeveloperOptionsFragment(this.f$1, this.f$2, preference);
                }
            });
            newCategory.addPreference(preference);
        }
    }

    public /* synthetic */ boolean lambda$addOnboardingPrefsCatergory$11$DeveloperOptionsFragment(String[] strArr, String str, Preference preference) {
        SharedPreferences.Editor edit = Utilities.getPrefs(getContext()).edit();
        for (String remove : strArr) {
            edit.remove(remove);
        }
        edit.apply();
        Toast.makeText(getContext(), "Reset " + str, 0).show();
        return true;
    }

    private String toName(String str) {
        String replace = str.replace("com.android.systemui.action.PLUGIN_", "").replace("com.android.launcher3.action.PLUGIN_", "");
        StringBuilder sb = new StringBuilder();
        for (String str2 : replace.split("_")) {
            if (sb.length() != 0) {
                sb.append(' ');
            }
            sb.append(str2.substring(0, 1));
            sb.append(str2.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    private static class PluginPreference extends SwitchPreference {
        private final List<ComponentName> mComponentNames;
        private final String mPackageName;
        private final PreferenceDataStore mPluginEnabler;
        private final ResolveInfo mSettingsInfo;

        PluginPreference(Context context, ResolveInfo resolveInfo, PreferenceDataStore preferenceDataStore, List<ComponentName> list) {
            super(context);
            PackageManager packageManager = context.getPackageManager();
            String str = resolveInfo.serviceInfo.applicationInfo.packageName;
            this.mPackageName = str;
            Intent intent = new Intent(DeveloperOptionsFragment.ACTION_PLUGIN_SETTINGS).setPackage(str);
            List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 64);
            if (resolveInfo.filter != null) {
                Iterator<ResolveInfo> it = queryIntentActivities.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    ResolveInfo next = it.next();
                    if (next.filter != null && next.filter.countCategories() > 0) {
                        intent.addCategory(resolveInfo.filter.getAction(0));
                        break;
                    }
                }
            }
            this.mSettingsInfo = packageManager.resolveActivity(intent, 0);
            this.mPluginEnabler = preferenceDataStore;
            this.mComponentNames = list;
            setTitle(resolveInfo.loadLabel(packageManager));
            setChecked(isPluginEnabled());
            setWidgetLayoutResource(R.layout.switch_preference_with_settings);
        }

        private boolean isEnabled(ComponentName componentName) {
            return this.mPluginEnabler.getBoolean(PluginManagerWrapper.pluginEnabledKey(componentName), true);
        }

        private boolean isPluginEnabled() {
            for (ComponentName isEnabled : this.mComponentNames) {
                if (!isEnabled(isEnabled)) {
                    return false;
                }
            }
            return true;
        }

        /* access modifiers changed from: protected */
        public boolean persistBoolean(boolean z) {
            boolean z2 = false;
            for (ComponentName next : this.mComponentNames) {
                if (isEnabled(next) != z) {
                    this.mPluginEnabler.putBoolean(PluginManagerWrapper.pluginEnabledKey(next), z);
                    z2 = true;
                }
            }
            if (z2) {
                String str = this.mPackageName;
                Uri uri = null;
                if (str != null) {
                    uri = Uri.fromParts("package", str, (String) null);
                }
                getContext().sendBroadcast(new Intent("com.android.systemui.action.PLUGIN_CHANGED", uri));
            }
            setChecked(z);
            return true;
        }

        public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
            super.onBindViewHolder(preferenceViewHolder);
            int i = 0;
            boolean z = this.mSettingsInfo != null;
            preferenceViewHolder.findViewById(R.id.settings).setVisibility(z ? 0 : 8);
            View findViewById = preferenceViewHolder.findViewById(R.id.divider);
            if (!z) {
                i = 8;
            }
            findViewById.setVisibility(i);
            preferenceViewHolder.findViewById(R.id.settings).setOnClickListener(new View.OnClickListener(z) {
                public final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    DeveloperOptionsFragment.PluginPreference.this.lambda$onBindViewHolder$0$DeveloperOptionsFragment$PluginPreference(this.f$1, view);
                }
            });
            preferenceViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                public final boolean onLongClick(View view) {
                    return DeveloperOptionsFragment.PluginPreference.this.lambda$onBindViewHolder$1$DeveloperOptionsFragment$PluginPreference(view);
                }
            });
        }

        public /* synthetic */ void lambda$onBindViewHolder$0$DeveloperOptionsFragment$PluginPreference(boolean z, View view) {
            if (z) {
                view.getContext().startActivity(new Intent().setComponent(new ComponentName(this.mSettingsInfo.activityInfo.packageName, this.mSettingsInfo.activityInfo.name)));
            }
        }

        public /* synthetic */ boolean lambda$onBindViewHolder$1$DeveloperOptionsFragment$PluginPreference(View view) {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", this.mPackageName, (String) null));
            getContext().startActivity(intent);
            return true;
        }
    }
}
