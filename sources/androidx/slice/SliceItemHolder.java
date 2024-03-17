package androidx.slice;

import android.app.PendingIntent;
import android.os.Parcelable;
import android.text.Spanned;
import androidx.core.text.HtmlCompat;
import androidx.core.util.Pair;
import androidx.slice.compat.SliceProviderCompat;
import androidx.versionedparcelable.VersionedParcelable;

public class SliceItemHolder implements VersionedParcelable {
    int mInt;
    long mLong;
    Parcelable mParcelable;
    String mStr;
    VersionedParcelable mVersionedParcelable;

    public SliceItemHolder() {
    }

    public SliceItemHolder(String str, Object obj, boolean z) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1422950858:
                if (str.equals("action")) {
                    c = 0;
                    break;
                }
                break;
            case 104431:
                if (str.equals("int")) {
                    c = 1;
                    break;
                }
                break;
            case 3327612:
                if (str.equals("long")) {
                    c = 2;
                    break;
                }
                break;
            case 3556653:
                if (str.equals("text")) {
                    c = 3;
                    break;
                }
                break;
            case 100313435:
                if (str.equals("image")) {
                    c = 4;
                    break;
                }
                break;
            case 100358090:
                if (str.equals("input")) {
                    c = 5;
                    break;
                }
                break;
            case 109526418:
                if (str.equals(SliceProviderCompat.EXTRA_SLICE)) {
                    c = 6;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                Pair pair = (Pair) obj;
                if (pair.first instanceof PendingIntent) {
                    if (!z) {
                        this.mParcelable = (Parcelable) pair.first;
                    } else {
                        throw new IllegalArgumentException("Cannot write PendingIntent to stream");
                    }
                } else if (!z) {
                    throw new IllegalArgumentException("Cannot write callback to parcel");
                }
                this.mVersionedParcelable = (VersionedParcelable) pair.second;
                return;
            case 1:
                this.mInt = ((Integer) obj).intValue();
                return;
            case 2:
                this.mLong = ((Long) obj).longValue();
                return;
            case 3:
                this.mStr = obj instanceof Spanned ? HtmlCompat.toHtml((Spanned) obj, 0) : (String) obj;
                return;
            case 4:
            case 6:
                this.mVersionedParcelable = (VersionedParcelable) obj;
                return;
            case 5:
                if (!z) {
                    this.mParcelable = (Parcelable) obj;
                    return;
                }
                throw new IllegalArgumentException("Cannot write RemoteInput to stream");
            default:
                return;
        }
    }

    public Object getObj(String str) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1422950858:
                if (str.equals("action")) {
                    c = 0;
                    break;
                }
                break;
            case 104431:
                if (str.equals("int")) {
                    c = 1;
                    break;
                }
                break;
            case 3327612:
                if (str.equals("long")) {
                    c = 2;
                    break;
                }
                break;
            case 3556653:
                if (str.equals("text")) {
                    c = 3;
                    break;
                }
                break;
            case 100313435:
                if (str.equals("image")) {
                    c = 4;
                    break;
                }
                break;
            case 100358090:
                if (str.equals("input")) {
                    c = 5;
                    break;
                }
                break;
            case 109526418:
                if (str.equals(SliceProviderCompat.EXTRA_SLICE)) {
                    c = 6;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return new Pair(this.mParcelable, (Slice) this.mVersionedParcelable);
            case 1:
                return Integer.valueOf(this.mInt);
            case 2:
                return Long.valueOf(this.mLong);
            case 3:
                String str2 = this.mStr;
                return (str2 == null || str2.length() == 0) ? "" : HtmlCompat.fromHtml(this.mStr, 0);
            case 4:
            case 6:
                return this.mVersionedParcelable;
            case 5:
                return this.mParcelable;
            default:
                throw new IllegalArgumentException("Unrecognized format " + str);
        }
    }
}
