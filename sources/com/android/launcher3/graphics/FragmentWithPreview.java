package com.android.launcher3.graphics;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

public class FragmentWithPreview extends Fragment {
    private Context mPreviewContext;

    public void onInit(Bundle bundle) {
    }

    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        onInit(bundle);
    }

    public Context getContext() {
        Context context = this.mPreviewContext;
        return context != null ? context : getActivity();
    }

    /* access modifiers changed from: package-private */
    public void enterPreviewMode(Context context) {
        this.mPreviewContext = context;
    }

    public boolean isInPreviewMode() {
        return this.mPreviewContext != null;
    }
}
