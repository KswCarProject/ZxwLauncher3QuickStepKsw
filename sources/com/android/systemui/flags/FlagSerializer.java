package com.android.systemui.flags;

import android.util.Log;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import org.json.JSONException;
import org.json.JSONObject;

@Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\b&\u0018\u0000*\u0004\b\u0000\u0010\u00012\u00020\u0002BG\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u001e\u0010\u0005\u001a\u001a\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00020\b0\u0006\u0012\u0018\u0010\t\u001a\u0014\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00028\u00000\n¢\u0006\u0002\u0010\u000bJ\u0017\u0010\f\u001a\u0004\u0018\u00018\u00002\b\u0010\r\u001a\u0004\u0018\u00010\u0004¢\u0006\u0002\u0010\u000eJ\u0015\u0010\u000f\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0010\u001a\u00028\u0000¢\u0006\u0002\u0010\u0011R \u0010\t\u001a\u0014\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00028\u00000\nX\u0004¢\u0006\u0002\n\u0000R&\u0010\u0005\u001a\u001a\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00020\b0\u0006X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0003\u001a\u00020\u0004X\u0004¢\u0006\u0002\n\u0000¨\u0006\u0012"}, d2 = {"Lcom/android/systemui/flags/FlagSerializer;", "T", "", "type", "", "setter", "Lkotlin/Function3;", "Lorg/json/JSONObject;", "", "getter", "Lkotlin/Function2;", "(Ljava/lang/String;Lkotlin/jvm/functions/Function3;Lkotlin/jvm/functions/Function2;)V", "fromSettingsData", "data", "(Ljava/lang/String;)Ljava/lang/Object;", "toSettingsData", "value", "(Ljava/lang/Object;)Ljava/lang/String;", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: FlagSerializer.kt */
public abstract class FlagSerializer<T> {
    private final Function2<JSONObject, String, T> getter;
    private final Function3<JSONObject, String, T, Unit> setter;
    private final String type;

    public FlagSerializer(String str, Function3<? super JSONObject, ? super String, ? super T, Unit> function3, Function2<? super JSONObject, ? super String, ? extends T> function2) {
        Intrinsics.checkNotNullParameter(str, "type");
        Intrinsics.checkNotNullParameter(function3, "setter");
        Intrinsics.checkNotNullParameter(function2, "getter");
        this.type = str;
        this.setter = function3;
        this.getter = function2;
    }

    public final String toSettingsData(T t) {
        try {
            JSONObject put = new JSONObject().put("type", this.type);
            Function3<JSONObject, String, T, Unit> function3 = this.setter;
            Intrinsics.checkNotNullExpressionValue(put, "it");
            function3.invoke(put, "value", t);
            return put.toString();
        } catch (JSONException e) {
            Log.w("FlagSerializer", "write error", e);
            return null;
        }
    }

    public final T fromSettingsData(String str) {
        if (str != null) {
            if (!(str.length() == 0)) {
                try {
                    JSONObject jSONObject = new JSONObject(str);
                    if (Intrinsics.areEqual((Object) jSONObject.getString("type"), (Object) this.type)) {
                        return this.getter.invoke(jSONObject, "value");
                    }
                    return null;
                } catch (JSONException e) {
                    Log.w("FlagSerializer", "read error", e);
                    throw new InvalidFlagStorageException();
                }
            }
        }
        return null;
    }
}
