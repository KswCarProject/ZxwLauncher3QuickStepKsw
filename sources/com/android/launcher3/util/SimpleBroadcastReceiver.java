package com.android.launcher3.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import java.util.function.Consumer;

public class SimpleBroadcastReceiver extends BroadcastReceiver {
    private final Consumer<Intent> mIntentConsumer;

    public SimpleBroadcastReceiver(Consumer<Intent> consumer) {
        this.mIntentConsumer = consumer;
    }

    public void onReceive(Context context, Intent intent) {
        this.mIntentConsumer.accept(intent);
    }

    public void register(Context context, String... strArr) {
        register(context, 0, strArr);
    }

    public void register(Context context, int i, String... strArr) {
        IntentFilter intentFilter = new IntentFilter();
        for (String addAction : strArr) {
            intentFilter.addAction(addAction);
        }
        context.registerReceiver(this, intentFilter, i);
    }
}
