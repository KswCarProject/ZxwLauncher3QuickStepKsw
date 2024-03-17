package androidx.slice;

import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;
import android.text.format.DateUtils;
import androidx.core.graphics.drawable.IconCompat;
import androidx.core.util.Pair;
import androidx.slice.compat.SliceProviderCompat;
import androidx.slice.core.SliceHints;
import androidx.versionedparcelable.CustomVersionedParcelable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import kotlin.text.Typography;

public final class SliceItem extends CustomVersionedParcelable {
    private static final String FORMAT = "format";
    private static final String HINTS = "hints";
    private static final String OBJ = "obj";
    private static final String OBJ_2 = "obj_2";
    private static final String SUBTYPE = "subtype";
    String mFormat;
    protected String[] mHints;
    SliceItemHolder mHolder;
    Object mObj;
    String mSubType;

    public interface ActionHandler {
        void onAction(SliceItem sliceItem, Context context, Intent intent);
    }

    public @interface SliceType {
    }

    public SliceItem(Object obj, String str, String str2, String[] strArr) {
        this.mHints = new String[0];
        this.mHints = strArr;
        this.mFormat = str;
        this.mSubType = str2;
        this.mObj = obj;
    }

    public SliceItem(Object obj, String str, String str2, List<String> list) {
        this(obj, str, str2, (String[]) list.toArray(new String[list.size()]));
    }

    public SliceItem() {
        this.mHints = new String[0];
    }

    public SliceItem(PendingIntent pendingIntent, Slice slice, String str, String str2, String[] strArr) {
        this((Object) new Pair(pendingIntent, slice), str, str2, strArr);
    }

    public SliceItem(ActionHandler actionHandler, Slice slice, String str, String str2, String[] strArr) {
        this((Object) new Pair(actionHandler, slice), str, str2, strArr);
    }

    public List<String> getHints() {
        return Arrays.asList(this.mHints);
    }

    public void addHint(String str) {
        this.mHints = (String[]) ArrayUtils.appendElement(String.class, this.mHints, str);
    }

    public String getFormat() {
        return this.mFormat;
    }

    public String getSubType() {
        return this.mSubType;
    }

    public CharSequence getText() {
        return (CharSequence) this.mObj;
    }

    public IconCompat getIcon() {
        return (IconCompat) this.mObj;
    }

    public PendingIntent getAction() {
        F f = ((Pair) this.mObj).first;
        if (f instanceof PendingIntent) {
            return (PendingIntent) f;
        }
        return null;
    }

    public void fireAction(Context context, Intent intent) throws PendingIntent.CanceledException {
        fireActionInternal(context, intent);
    }

    public boolean fireActionInternal(Context context, Intent intent) throws PendingIntent.CanceledException {
        F f = ((Pair) this.mObj).first;
        if (f instanceof PendingIntent) {
            ((PendingIntent) f).send(context, 0, intent, (PendingIntent.OnFinished) null, (Handler) null);
            return false;
        }
        ((ActionHandler) f).onAction(this, context, intent);
        return true;
    }

    public RemoteInput getRemoteInput() {
        return (RemoteInput) this.mObj;
    }

    public int getInt() {
        return ((Integer) this.mObj).intValue();
    }

    public Slice getSlice() {
        if ("action".equals(getFormat())) {
            return (Slice) ((Pair) this.mObj).second;
        }
        return (Slice) this.mObj;
    }

    public long getLong() {
        return ((Long) this.mObj).longValue();
    }

    public long getTimestamp() {
        return ((Long) this.mObj).longValue();
    }

    public boolean hasHint(String str) {
        return ArrayUtils.contains(this.mHints, str);
    }

    public SliceItem(Bundle bundle) {
        this.mHints = new String[0];
        this.mHints = bundle.getStringArray(HINTS);
        this.mFormat = bundle.getString(FORMAT);
        this.mSubType = bundle.getString(SUBTYPE);
        this.mObj = readObj(this.mFormat, bundle);
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putStringArray(HINTS, this.mHints);
        bundle.putString(FORMAT, this.mFormat);
        bundle.putString(SUBTYPE, this.mSubType);
        writeObj(bundle, this.mObj, this.mFormat);
        return bundle;
    }

    public boolean hasHints(String[] strArr) {
        if (strArr == null) {
            return true;
        }
        for (String str : strArr) {
            if (!TextUtils.isEmpty(str) && !ArrayUtils.contains(this.mHints, str)) {
                return false;
            }
        }
        return true;
    }

    public boolean hasAnyHints(String... strArr) {
        if (strArr == null) {
            return false;
        }
        for (String contains : strArr) {
            if (ArrayUtils.contains(this.mHints, contains)) {
                return true;
            }
        }
        return false;
    }

    private void writeObj(Bundle bundle, Object obj, String str) {
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
                bundle.putParcelable(OBJ, (PendingIntent) pair.first);
                bundle.putBundle(OBJ_2, ((Slice) pair.second).toBundle());
                return;
            case 1:
                bundle.putInt(OBJ, ((Integer) this.mObj).intValue());
                return;
            case 2:
                bundle.putLong(OBJ, ((Long) this.mObj).longValue());
                return;
            case 3:
                bundle.putCharSequence(OBJ, (CharSequence) obj);
                return;
            case 4:
                bundle.putBundle(OBJ, ((IconCompat) obj).toBundle());
                return;
            case 5:
                bundle.putParcelable(OBJ, (Parcelable) obj);
                return;
            case 6:
                bundle.putParcelable(OBJ, ((Slice) obj).toBundle());
                return;
            default:
                return;
        }
    }

    private static Object readObj(String str, Bundle bundle) {
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
                return new Pair(bundle.getParcelable(OBJ), new Slice(bundle.getBundle(OBJ_2)));
            case 1:
                return Integer.valueOf(bundle.getInt(OBJ));
            case 2:
                return Long.valueOf(bundle.getLong(OBJ));
            case 3:
                return bundle.getCharSequence(OBJ);
            case 4:
                return IconCompat.createFromBundle(bundle.getBundle(OBJ));
            case 5:
                return bundle.getParcelable(OBJ);
            case 6:
                return new Slice(bundle.getBundle(OBJ));
            default:
                throw new RuntimeException("Unsupported type " + str);
        }
    }

    public static String typeToString(String str) {
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
                return "Action";
            case 1:
                return "Int";
            case 2:
                return "Long";
            case 3:
                return "Text";
            case 4:
                return "Image";
            case 5:
                return "RemoteInput";
            case 6:
                return "Slice";
            default:
                return "Unrecognized format: " + str;
        }
    }

    public String toString() {
        return toString("");
    }

    public String toString(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(getFormat());
        if (getSubType() != null) {
            sb.append(Typography.less);
            sb.append(getSubType());
            sb.append(Typography.greater);
        }
        sb.append(' ');
        String[] strArr = this.mHints;
        if (strArr.length > 0) {
            Slice.appendHints(sb, strArr);
            sb.append(' ');
        }
        String str2 = str + "  ";
        String format = getFormat();
        format.hashCode();
        char c = 65535;
        switch (format.hashCode()) {
            case -1422950858:
                if (format.equals("action")) {
                    c = 0;
                    break;
                }
                break;
            case 104431:
                if (format.equals("int")) {
                    c = 1;
                    break;
                }
                break;
            case 3327612:
                if (format.equals("long")) {
                    c = 2;
                    break;
                }
                break;
            case 3556653:
                if (format.equals("text")) {
                    c = 3;
                    break;
                }
                break;
            case 100313435:
                if (format.equals("image")) {
                    c = 4;
                    break;
                }
                break;
            case 109526418:
                if (format.equals(SliceProviderCompat.EXTRA_SLICE)) {
                    c = 5;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                sb.append('[').append(((Pair) this.mObj).first).append("] ");
                sb.append("{\n");
                sb.append(getSlice().toString(str2));
                sb.append(10).append(str).append('}');
                break;
            case 1:
                if (!"color".equals(getSubType())) {
                    if (!"layout_direction".equals(getSubType())) {
                        sb.append(getInt());
                        break;
                    } else {
                        sb.append(layoutDirectionToString(getInt()));
                        break;
                    }
                } else {
                    int i = getInt();
                    sb.append(String.format("a=0x%02x r=0x%02x g=0x%02x b=0x%02x", new Object[]{Integer.valueOf(Color.alpha(i)), Integer.valueOf(Color.red(i)), Integer.valueOf(Color.green(i)), Integer.valueOf(Color.blue(i))}));
                    break;
                }
            case 2:
                if (SliceHints.SUBTYPE_MILLIS.equals(getSubType())) {
                    if (getLong() != -1) {
                        sb.append(DateUtils.getRelativeTimeSpanString(getLong(), Calendar.getInstance().getTimeInMillis(), 1000, 262144));
                        break;
                    } else {
                        sb.append("INFINITY");
                        break;
                    }
                } else {
                    sb.append(getLong()).append('L');
                    break;
                }
            case 3:
                sb.append(Typography.quote).append(getText()).append(Typography.quote);
                break;
            case 4:
                sb.append(getIcon());
                break;
            case 5:
                sb.append("{\n");
                sb.append(getSlice().toString(str2));
                sb.append(10).append(str).append('}');
                break;
            default:
                sb.append(typeToString(getFormat()));
                break;
        }
        sb.append("\n");
        return sb.toString();
    }

    public void onPreParceling(boolean z) {
        this.mHolder = new SliceItemHolder(this.mFormat, this.mObj, z);
    }

    public void onPostParceling() {
        this.mObj = this.mHolder.getObj(this.mFormat);
        this.mHolder = null;
    }

    private static String layoutDirectionToString(int i) {
        if (i == 0) {
            return "LTR";
        }
        if (i == 1) {
            return "RTL";
        }
        if (i != 2) {
            return i != 3 ? Integer.toString(i) : "LOCALE";
        }
        return "INHERIT";
    }
}
