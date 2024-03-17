package androidx.preference;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.activity.ViewTreeOnBackPressedDispatcherOwner;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.preference.PreferenceFragmentCompat;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;
import java.util.Objects;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\b&\u0018\u00002\u00020\u00012\u00020\u0002:\u0001&B\u0005¢\u0006\u0002\u0010\u0003J\u0010\u0010\n\u001a\u00020\u00072\u0006\u0010\u000b\u001a\u00020\fH\u0002J\u0010\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010H\u0017J\n\u0010\u0011\u001a\u0004\u0018\u00010\u0001H\u0016J\b\u0010\u0012\u001a\u00020\u0013H&J$\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u000b\u001a\u00020\f2\b\u0010\u0016\u001a\u0004\u0018\u00010\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0019H\u0017J\u0018\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u00132\u0006\u0010\u001d\u001a\u00020\u001eH\u0017J\u001a\u0010\u001f\u001a\u00020\u000e2\u0006\u0010 \u001a\u00020\u00152\b\u0010\u0018\u001a\u0004\u0018\u00010\u0019H\u0017J\u0012\u0010!\u001a\u00020\u000e2\b\u0010\u0018\u001a\u0004\u0018\u00010\u0019H\u0016J\u0012\u0010\"\u001a\u00020\u000e2\b\u0010#\u001a\u0004\u0018\u00010$H\u0002J\u0010\u0010\"\u001a\u00020\u000e2\u0006\u0010%\u001a\u00020\u001eH\u0002R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u000e¢\u0006\u0002\n\u0000R\u0011\u0010\u0006\u001a\u00020\u00078F¢\u0006\u0006\u001a\u0004\b\b\u0010\t¨\u0006'"}, d2 = {"Landroidx/preference/PreferenceHeaderFragmentCompat;", "Landroidx/fragment/app/Fragment;", "Landroidx/preference/PreferenceFragmentCompat$OnPreferenceStartFragmentCallback;", "()V", "onBackPressedCallback", "Landroidx/activity/OnBackPressedCallback;", "slidingPaneLayout", "Landroidx/slidingpanelayout/widget/SlidingPaneLayout;", "getSlidingPaneLayout", "()Landroidx/slidingpanelayout/widget/SlidingPaneLayout;", "buildContentView", "inflater", "Landroid/view/LayoutInflater;", "onAttach", "", "context", "Landroid/content/Context;", "onCreateInitialDetailFragment", "onCreatePreferenceHeader", "Landroidx/preference/PreferenceFragmentCompat;", "onCreateView", "Landroid/view/View;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onPreferenceStartFragment", "", "caller", "pref", "Landroidx/preference/Preference;", "onViewCreated", "view", "onViewStateRestored", "openPreferenceHeader", "intent", "Landroid/content/Intent;", "header", "InnerOnBackPressedCallback", "preference_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: PreferenceHeaderFragmentCompat.kt */
public abstract class PreferenceHeaderFragmentCompat extends Fragment implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    /* access modifiers changed from: private */
    public OnBackPressedCallback onBackPressedCallback;

    public abstract PreferenceFragmentCompat onCreatePreferenceHeader();

    public final SlidingPaneLayout getSlidingPaneLayout() {
        return (SlidingPaneLayout) requireView();
    }

    public boolean onPreferenceStartFragment(PreferenceFragmentCompat preferenceFragmentCompat, Preference preference) {
        Intrinsics.checkNotNullParameter(preferenceFragmentCompat, "caller");
        Intrinsics.checkNotNullParameter(preference, "pref");
        if (preferenceFragmentCompat.getId() == R.id.preferences_header) {
            openPreferenceHeader(preference);
            return true;
        } else if (preferenceFragmentCompat.getId() != R.id.preferences_detail) {
            return false;
        } else {
            FragmentFactory fragmentFactory = getChildFragmentManager().getFragmentFactory();
            ClassLoader classLoader = requireContext().getClassLoader();
            String fragment = preference.getFragment();
            Intrinsics.checkNotNull(fragment);
            Fragment instantiate = fragmentFactory.instantiate(classLoader, fragment);
            Intrinsics.checkNotNullExpressionValue(instantiate, "childFragmentManager.fra….fragment!!\n            )");
            instantiate.setArguments(preference.getExtras());
            FragmentManager childFragmentManager = getChildFragmentManager();
            Intrinsics.checkNotNullExpressionValue(childFragmentManager, "childFragmentManager");
            FragmentTransaction beginTransaction = childFragmentManager.beginTransaction();
            Intrinsics.checkNotNullExpressionValue(beginTransaction, "beginTransaction()");
            beginTransaction.setReorderingAllowed(true);
            beginTransaction.replace(R.id.preferences_detail, instantiate);
            beginTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            beginTransaction.addToBackStack((String) null);
            beginTransaction.commit();
            return true;
        }
    }

    @Metadata(d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0000\b\u0002\u0018\u00002\u00020\u00012\u00020\u0002B\r\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0002\u0010\u0005J\b\u0010\u0006\u001a\u00020\u0007H\u0016J\u0010\u0010\b\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\nH\u0016J\u0010\u0010\u000b\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\nH\u0016J\u0018\u0010\f\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\u000eH\u0016R\u000e\u0010\u0003\u001a\u00020\u0004X\u0004¢\u0006\u0002\n\u0000¨\u0006\u000f"}, d2 = {"Landroidx/preference/PreferenceHeaderFragmentCompat$InnerOnBackPressedCallback;", "Landroidx/activity/OnBackPressedCallback;", "Landroidx/slidingpanelayout/widget/SlidingPaneLayout$PanelSlideListener;", "caller", "Landroidx/preference/PreferenceHeaderFragmentCompat;", "(Landroidx/preference/PreferenceHeaderFragmentCompat;)V", "handleOnBackPressed", "", "onPanelClosed", "panel", "Landroid/view/View;", "onPanelOpened", "onPanelSlide", "slideOffset", "", "preference_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: PreferenceHeaderFragmentCompat.kt */
    private static final class InnerOnBackPressedCallback extends OnBackPressedCallback implements SlidingPaneLayout.PanelSlideListener {
        private final PreferenceHeaderFragmentCompat caller;

        public void onPanelSlide(View view, float f) {
            Intrinsics.checkNotNullParameter(view, "panel");
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public InnerOnBackPressedCallback(PreferenceHeaderFragmentCompat preferenceHeaderFragmentCompat) {
            super(true);
            Intrinsics.checkNotNullParameter(preferenceHeaderFragmentCompat, "caller");
            this.caller = preferenceHeaderFragmentCompat;
            preferenceHeaderFragmentCompat.getSlidingPaneLayout().addPanelSlideListener(this);
        }

        public void handleOnBackPressed() {
            this.caller.getSlidingPaneLayout().closePane();
        }

        public void onPanelOpened(View view) {
            Intrinsics.checkNotNullParameter(view, "panel");
            setEnabled(true);
        }

        public void onPanelClosed(View view) {
            Intrinsics.checkNotNullParameter(view, "panel");
            setEnabled(false);
        }
    }

    public void onAttach(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        super.onAttach(context);
        FragmentManager parentFragmentManager = getParentFragmentManager();
        Intrinsics.checkNotNullExpressionValue(parentFragmentManager, "parentFragmentManager");
        FragmentTransaction beginTransaction = parentFragmentManager.beginTransaction();
        Intrinsics.checkNotNullExpressionValue(beginTransaction, "beginTransaction()");
        beginTransaction.setPrimaryNavigationFragment(this);
        beginTransaction.commit();
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Intrinsics.checkNotNullParameter(layoutInflater, "inflater");
        SlidingPaneLayout buildContentView = buildContentView(layoutInflater);
        if (getChildFragmentManager().findFragmentById(R.id.preferences_header) == null) {
            PreferenceFragmentCompat onCreatePreferenceHeader = onCreatePreferenceHeader();
            FragmentManager childFragmentManager = getChildFragmentManager();
            Intrinsics.checkNotNullExpressionValue(childFragmentManager, "childFragmentManager");
            FragmentTransaction beginTransaction = childFragmentManager.beginTransaction();
            Intrinsics.checkNotNullExpressionValue(beginTransaction, "beginTransaction()");
            beginTransaction.setReorderingAllowed(true);
            beginTransaction.add(R.id.preferences_header, (Fragment) onCreatePreferenceHeader);
            beginTransaction.commit();
        }
        buildContentView.setLockMode(3);
        return buildContentView;
    }

    private final SlidingPaneLayout buildContentView(LayoutInflater layoutInflater) {
        SlidingPaneLayout slidingPaneLayout = new SlidingPaneLayout(layoutInflater.getContext());
        slidingPaneLayout.setId(R.id.preferences_sliding_pane_layout);
        FragmentContainerView fragmentContainerView = new FragmentContainerView(layoutInflater.getContext());
        fragmentContainerView.setId(R.id.preferences_header);
        SlidingPaneLayout.LayoutParams layoutParams = new SlidingPaneLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.preferences_header_width), -1);
        layoutParams.weight = (float) getResources().getInteger(R.integer.preferences_header_pane_weight);
        slidingPaneLayout.addView(fragmentContainerView, layoutParams);
        FragmentContainerView fragmentContainerView2 = new FragmentContainerView(layoutInflater.getContext());
        fragmentContainerView2.setId(R.id.preferences_detail);
        SlidingPaneLayout.LayoutParams layoutParams2 = new SlidingPaneLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.preferences_detail_width), -1);
        layoutParams2.weight = (float) getResources().getInteger(R.integer.preferences_detail_pane_weight);
        slidingPaneLayout.addView(fragmentContainerView2, layoutParams2);
        return slidingPaneLayout;
    }

    public void onViewCreated(View view, Bundle bundle) {
        OnBackPressedDispatcher onBackPressedDispatcher;
        Intrinsics.checkNotNullParameter(view, "view");
        super.onViewCreated(view, bundle);
        this.onBackPressedCallback = new InnerOnBackPressedCallback(this);
        View slidingPaneLayout = getSlidingPaneLayout();
        if (!ViewCompat.isLaidOut(slidingPaneLayout) || slidingPaneLayout.isLayoutRequested()) {
            slidingPaneLayout.addOnLayoutChangeListener(new PreferenceHeaderFragmentCompat$onViewCreated$$inlined$doOnLayout$1(this));
        } else {
            OnBackPressedCallback access$getOnBackPressedCallback$p = this.onBackPressedCallback;
            Intrinsics.checkNotNull(access$getOnBackPressedCallback$p);
            access$getOnBackPressedCallback$p.setEnabled(getSlidingPaneLayout().isSlideable() && getSlidingPaneLayout().isOpen());
        }
        getChildFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            public final void onBackStackChanged() {
                PreferenceHeaderFragmentCompat.m9onViewCreated$lambda10(PreferenceHeaderFragmentCompat.this);
            }
        });
        OnBackPressedDispatcherOwner onBackPressedDispatcherOwner = ViewTreeOnBackPressedDispatcherOwner.get(view);
        if (onBackPressedDispatcherOwner != null && (onBackPressedDispatcher = onBackPressedDispatcherOwner.getOnBackPressedDispatcher()) != null) {
            LifecycleOwner viewLifecycleOwner = getViewLifecycleOwner();
            OnBackPressedCallback onBackPressedCallback2 = this.onBackPressedCallback;
            Intrinsics.checkNotNull(onBackPressedCallback2);
            onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback2);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onViewCreated$lambda-10  reason: not valid java name */
    public static final void m9onViewCreated$lambda10(PreferenceHeaderFragmentCompat preferenceHeaderFragmentCompat) {
        Intrinsics.checkNotNullParameter(preferenceHeaderFragmentCompat, "this$0");
        OnBackPressedCallback onBackPressedCallback2 = preferenceHeaderFragmentCompat.onBackPressedCallback;
        Intrinsics.checkNotNull(onBackPressedCallback2);
        onBackPressedCallback2.setEnabled(preferenceHeaderFragmentCompat.getChildFragmentManager().getBackStackEntryCount() == 0);
    }

    public void onViewStateRestored(Bundle bundle) {
        Fragment onCreateInitialDetailFragment;
        super.onViewStateRestored(bundle);
        if (bundle == null && (onCreateInitialDetailFragment = onCreateInitialDetailFragment()) != null) {
            FragmentManager childFragmentManager = getChildFragmentManager();
            Intrinsics.checkNotNullExpressionValue(childFragmentManager, "childFragmentManager");
            FragmentTransaction beginTransaction = childFragmentManager.beginTransaction();
            Intrinsics.checkNotNullExpressionValue(beginTransaction, "beginTransaction()");
            beginTransaction.setReorderingAllowed(true);
            beginTransaction.replace(R.id.preferences_detail, onCreateInitialDetailFragment);
            beginTransaction.commit();
        }
    }

    public Fragment onCreateInitialDetailFragment() {
        Fragment findFragmentById = getChildFragmentManager().findFragmentById(R.id.preferences_header);
        Objects.requireNonNull(findFragmentById, "null cannot be cast to non-null type androidx.preference.PreferenceFragmentCompat");
        PreferenceFragmentCompat preferenceFragmentCompat = (PreferenceFragmentCompat) findFragmentById;
        Fragment fragment = null;
        if (preferenceFragmentCompat.getPreferenceScreen().getPreferenceCount() <= 0) {
            return null;
        }
        int i = 0;
        int preferenceCount = preferenceFragmentCompat.getPreferenceScreen().getPreferenceCount();
        while (true) {
            if (i >= preferenceCount) {
                break;
            }
            int i2 = i + 1;
            Preference preference = preferenceFragmentCompat.getPreferenceScreen().getPreference(i);
            Intrinsics.checkNotNullExpressionValue(preference, "headerFragment.preferenc…reen.getPreference(index)");
            if (preference.getFragment() == null) {
                i = i2;
            } else {
                String fragment2 = preference.getFragment();
                if (fragment2 != null) {
                    fragment = getChildFragmentManager().getFragmentFactory().instantiate(requireContext().getClassLoader(), fragment2);
                }
                if (fragment != null) {
                    fragment.setArguments(preference.getExtras());
                }
            }
        }
        return fragment;
    }

    private final void openPreferenceHeader(Preference preference) {
        Fragment fragment;
        if (preference.getFragment() == null) {
            openPreferenceHeader(preference.getIntent());
            return;
        }
        String fragment2 = preference.getFragment();
        if (fragment2 == null) {
            fragment = null;
        } else {
            fragment = getChildFragmentManager().getFragmentFactory().instantiate(requireContext().getClassLoader(), fragment2);
        }
        if (fragment != null) {
            fragment.setArguments(preference.getExtras());
        }
        if (getChildFragmentManager().getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry backStackEntryAt = getChildFragmentManager().getBackStackEntryAt(0);
            Intrinsics.checkNotNullExpressionValue(backStackEntryAt, "childFragmentManager.getBackStackEntryAt(0)");
            getChildFragmentManager().popBackStack(backStackEntryAt.getId(), 1);
        }
        FragmentManager childFragmentManager = getChildFragmentManager();
        Intrinsics.checkNotNullExpressionValue(childFragmentManager, "childFragmentManager");
        FragmentTransaction beginTransaction = childFragmentManager.beginTransaction();
        Intrinsics.checkNotNullExpressionValue(beginTransaction, "beginTransaction()");
        beginTransaction.setReorderingAllowed(true);
        int i = R.id.preferences_detail;
        Intrinsics.checkNotNull(fragment);
        beginTransaction.replace(i, fragment);
        if (getSlidingPaneLayout().isOpen()) {
            beginTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        }
        getSlidingPaneLayout().openPane();
        beginTransaction.commit();
    }

    private final void openPreferenceHeader(Intent intent) {
        if (intent != null) {
            startActivity(intent);
        }
    }
}
