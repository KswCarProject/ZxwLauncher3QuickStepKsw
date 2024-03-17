package com.szchoiceway.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import com.android.launcher3.InsettableFrameLayout;

public class CustomerView extends InsettableFrameLayout {
    private View mCustomerView;

    public CustomerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        createCustomerView();
    }

    /* access modifiers changed from: protected */
    public void createCustomerView() {
        Object systemService;
        try {
            Context createPackageContext = getContext().createPackageContext("com.szchoiceway.customerui", 3);
            if (createPackageContext != null && (systemService = createPackageContext.getSystemService("layout_inflater")) != null) {
                Resources resources = createPackageContext.getResources();
                this.mCustomerView = ((LayoutInflater) systemService).inflate(resources.getLayout(resources.getIdentifier("layout_launcher_zxw", "layout", "com.szchoiceway.customerui")), this);
            }
        } catch (Exception e) {
            this.mCustomerView = null;
            e.printStackTrace();
        }
    }
}
