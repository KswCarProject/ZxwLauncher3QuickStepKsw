package com.android.launcher3.proxy;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

public class ProxyActivityStarter extends Activity {
    public static final String EXTRA_PARAMS = "start-activity-params";
    private static final String TAG = "ProxyActivityStarter";
    private StartActivityParams mParams;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setVisible(false);
        StartActivityParams startActivityParams = (StartActivityParams) getIntent().getParcelableExtra(EXTRA_PARAMS);
        this.mParams = startActivityParams;
        if (startActivityParams == null) {
            Log.d(TAG, "Proxy activity started without params");
            finishAndRemoveTask();
        } else if (bundle == null) {
            try {
                if (startActivityParams.intent != null) {
                    startActivityForResult(this.mParams.intent, this.mParams.requestCode, this.mParams.options);
                    return;
                }
                if (this.mParams.intentSender != null) {
                    startIntentSenderForResult(this.mParams.intentSender, this.mParams.requestCode, this.mParams.fillInIntent, this.mParams.flagsMask, this.mParams.flagsValues, this.mParams.extraFlags, this.mParams.options);
                    return;
                }
                finishAndRemoveTask();
            } catch (ActivityNotFoundException | IntentSender.SendIntentException | NullPointerException | SecurityException unused) {
                this.mParams.deliverResult(this, 0, (Intent) null);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == this.mParams.requestCode) {
            this.mParams.deliverResult(this, i2, intent);
        }
        finishAndRemoveTask();
    }

    public static Intent getLaunchIntent(Context context, StartActivityParams startActivityParams) {
        return new Intent(context, ProxyActivityStarter.class).putExtra(EXTRA_PARAMS, startActivityParams).addFlags(270565376);
    }
}
