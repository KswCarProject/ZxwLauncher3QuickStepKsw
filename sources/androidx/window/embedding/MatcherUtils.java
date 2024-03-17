package androidx.window.embedding;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import androidx.slice.core.SliceHints;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

@Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\bÁ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u001d\u0010\u0007\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0000¢\u0006\u0002\b\fJ\u001f\u0010\r\u001a\u00020\u00042\b\u0010\u000e\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\n\u001a\u00020\u000bH\u0000¢\u0006\u0002\b\u000fJ\u0018\u0010\u0010\u001a\u00020\u00042\u0006\u0010\u0011\u001a\u00020\u00062\u0006\u0010\u0012\u001a\u00020\u0006H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006XT¢\u0006\u0002\n\u0000¨\u0006\u0013"}, d2 = {"Landroidx/window/embedding/MatcherUtils;", "", "()V", "sDebugMatchers", "", "sMatchersTag", "", "areActivityOrIntentComponentsMatching", "activity", "Landroid/app/Activity;", "ruleComponent", "Landroid/content/ComponentName;", "areActivityOrIntentComponentsMatching$window_release", "areComponentsMatching", "activityComponent", "areComponentsMatching$window_release", "wildcardMatch", "name", "pattern", "window_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: MatcherUtils.kt */
public final class MatcherUtils {
    public static final MatcherUtils INSTANCE = new MatcherUtils();
    public static final boolean sDebugMatchers = false;
    public static final String sMatchersTag = "SplitRuleResolution";

    private MatcherUtils() {
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0070  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x008e A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final boolean areComponentsMatching$window_release(android.content.ComponentName r7, android.content.ComponentName r8) {
        /*
            r6 = this;
            java.lang.String r0 = "ruleComponent"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r8, r0)
            java.lang.String r0 = "*"
            r1 = 1
            r2 = 0
            if (r7 != 0) goto L_0x0022
            java.lang.String r7 = r8.getPackageName()
            boolean r7 = kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r7, (java.lang.Object) r0)
            if (r7 == 0) goto L_0x0020
            java.lang.String r7 = r8.getClassName()
            boolean r7 = kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r7, (java.lang.Object) r0)
            if (r7 == 0) goto L_0x0020
            goto L_0x0021
        L_0x0020:
            r1 = r2
        L_0x0021:
            return r1
        L_0x0022:
            java.lang.String r3 = r7.toString()
            java.lang.String r4 = "activityComponent.toString()"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r3, r4)
            java.lang.CharSequence r3 = (java.lang.CharSequence) r3
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            r4 = 2
            r5 = 0
            boolean r0 = kotlin.text.StringsKt.contains$default((java.lang.CharSequence) r3, (java.lang.CharSequence) r0, (boolean) r2, (int) r4, (java.lang.Object) r5)
            r0 = r0 ^ r1
            if (r0 == 0) goto L_0x0093
            java.lang.String r0 = r7.getPackageName()
            java.lang.String r3 = r8.getPackageName()
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r0, (java.lang.Object) r3)
            if (r0 != 0) goto L_0x0061
            java.lang.String r0 = r7.getPackageName()
            java.lang.String r3 = "activityComponent.packageName"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r0, r3)
            java.lang.String r3 = r8.getPackageName()
            java.lang.String r4 = "ruleComponent.packageName"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r3, r4)
            boolean r0 = r6.wildcardMatch(r0, r3)
            if (r0 == 0) goto L_0x005f
            goto L_0x0061
        L_0x005f:
            r0 = r2
            goto L_0x0062
        L_0x0061:
            r0 = r1
        L_0x0062:
            java.lang.String r3 = r7.getClassName()
            java.lang.String r4 = r8.getClassName()
            boolean r3 = kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r3, (java.lang.Object) r4)
            if (r3 != 0) goto L_0x008b
            java.lang.String r7 = r7.getClassName()
            java.lang.String r3 = "activityComponent.className"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r7, r3)
            java.lang.String r8 = r8.getClassName()
            java.lang.String r3 = "ruleComponent.className"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r8, r3)
            boolean r7 = r6.wildcardMatch(r7, r8)
            if (r7 == 0) goto L_0x0089
            goto L_0x008b
        L_0x0089:
            r7 = r2
            goto L_0x008c
        L_0x008b:
            r7 = r1
        L_0x008c:
            if (r0 == 0) goto L_0x0091
            if (r7 == 0) goto L_0x0091
            goto L_0x0092
        L_0x0091:
            r1 = r2
        L_0x0092:
            return r1
        L_0x0093:
            java.lang.IllegalArgumentException r7 = new java.lang.IllegalArgumentException
            java.lang.String r8 = "Wildcard can only be part of the rule."
            java.lang.String r8 = r8.toString()
            r7.<init>(r8)
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.window.embedding.MatcherUtils.areComponentsMatching$window_release(android.content.ComponentName, android.content.ComponentName):boolean");
    }

    public final boolean areActivityOrIntentComponentsMatching$window_release(Activity activity, ComponentName componentName) {
        ComponentName component;
        Intrinsics.checkNotNullParameter(activity, SliceHints.HINT_ACTIVITY);
        Intrinsics.checkNotNullParameter(componentName, "ruleComponent");
        if (areComponentsMatching$window_release(activity.getComponentName(), componentName)) {
            return true;
        }
        Intent intent = activity.getIntent();
        if (intent == null || (component = intent.getComponent()) == null) {
            return false;
        }
        return INSTANCE.areComponentsMatching$window_release(component, componentName);
    }

    private final boolean wildcardMatch(String str, String str2) {
        CharSequence charSequence = str2;
        if (!StringsKt.contains$default(charSequence, (CharSequence) "*", false, 2, (Object) null)) {
            return false;
        }
        if (Intrinsics.areEqual((Object) str2, (Object) "*")) {
            return true;
        }
        CharSequence charSequence2 = charSequence;
        if (StringsKt.indexOf$default(charSequence2, "*", 0, false, 6, (Object) null) == StringsKt.lastIndexOf$default(charSequence2, "*", 0, false, 6, (Object) null) && StringsKt.endsWith$default(str2, "*", false, 2, (Object) null)) {
            String substring = str2.substring(0, str2.length() - 1);
            Intrinsics.checkNotNullExpressionValue(substring, "this as java.lang.String…ing(startIndex, endIndex)");
            return StringsKt.startsWith$default(str, substring, false, 2, (Object) null);
        }
        throw new IllegalArgumentException("Name pattern with a wildcard must only contain a single wildcard in the end".toString());
    }
}
