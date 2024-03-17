package com.android.launcher3.proxy;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class StartActivityParams implements Parcelable {
    public static final Parcelable.Creator<StartActivityParams> CREATOR = new Parcelable.Creator<StartActivityParams>() {
        public StartActivityParams createFromParcel(Parcel parcel) {
            return new StartActivityParams(parcel);
        }

        public StartActivityParams[] newArray(int i) {
            return new StartActivityParams[i];
        }
    };
    private static final String TAG = "StartActivityParams";
    public int extraFlags;
    public Intent fillInIntent;
    public int flagsMask;
    public int flagsValues;
    public Intent intent;
    public IntentSender intentSender;
    private final PendingIntent mPICallback;
    public Bundle options;
    public final int requestCode;

    public int describeContents() {
        return 0;
    }

    public StartActivityParams(Activity activity, int i) {
        this(activity.createPendingResult(i, new Intent(), 1241513984), i);
    }

    public StartActivityParams(PendingIntent pendingIntent, int i) {
        this.mPICallback = pendingIntent;
        this.requestCode = i;
    }

    private StartActivityParams(Parcel parcel) {
        this.mPICallback = (PendingIntent) parcel.readTypedObject(PendingIntent.CREATOR);
        this.requestCode = parcel.readInt();
        this.intent = (Intent) parcel.readTypedObject(Intent.CREATOR);
        this.intentSender = (IntentSender) parcel.readTypedObject(IntentSender.CREATOR);
        this.fillInIntent = (Intent) parcel.readTypedObject(Intent.CREATOR);
        this.flagsMask = parcel.readInt();
        this.flagsValues = parcel.readInt();
        this.extraFlags = parcel.readInt();
        this.options = parcel.readBundle();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedObject(this.mPICallback, i);
        parcel.writeInt(this.requestCode);
        parcel.writeTypedObject(this.intent, i);
        parcel.writeTypedObject(this.intentSender, i);
        parcel.writeTypedObject(this.fillInIntent, i);
        parcel.writeInt(this.flagsMask);
        parcel.writeInt(this.flagsValues);
        parcel.writeInt(this.extraFlags);
        parcel.writeBundle(this.options);
    }

    public void deliverResult(Context context, int i, Intent intent2) {
        try {
            PendingIntent pendingIntent = this.mPICallback;
            if (pendingIntent != null) {
                pendingIntent.send(context, i, intent2);
            }
        } catch (PendingIntent.CanceledException e) {
            Log.e(TAG, "Unable to send back result", e);
        }
    }
}
