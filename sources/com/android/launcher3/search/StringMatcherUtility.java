package com.android.launcher3.search;

import java.lang.Character;
import java.text.Collator;

public class StringMatcherUtility {
    private static boolean isBreak(int i, int i2, int i3) {
        if (i2 == 0) {
            return true;
        }
        switch (i2) {
            case 12:
            case 13:
            case 14:
                return true;
            default:
                if (i != 1) {
                    if (i == 2) {
                        return i2 > 5 || i2 <= 0;
                    }
                    if (i != 3) {
                        if (i == 20) {
                            return true;
                        }
                        switch (i) {
                            case 9:
                            case 10:
                            case 11:
                                return (i2 == 9 || i2 == 10 || i2 == 11) ? false : true;
                            default:
                                switch (i) {
                                    case 24:
                                    case 25:
                                    case 26:
                                        return true;
                                    default:
                                        return false;
                                }
                        }
                    }
                } else if (i3 == 1) {
                    return true;
                }
                return i2 != 1;
        }
    }

    public static boolean matches(String str, String str2, StringMatcher stringMatcher) {
        int length = str.length();
        int length2 = str2.length();
        if (length2 >= length && length > 0) {
            if (requestSimpleFuzzySearch(str)) {
                return str2.toLowerCase().contains(str);
            }
            int type = Character.getType(str2.codePointAt(0));
            int i = length2 - length;
            int i2 = 0;
            int i3 = 0;
            while (i2 <= i) {
                int type2 = i2 < length2 + -1 ? Character.getType(str2.codePointAt(i2 + 1)) : 0;
                if (isBreak(type, i3, type2) && stringMatcher.matches(str, str2.substring(i2, i2 + length))) {
                    return true;
                }
                i2++;
                i3 = type;
                type = type2;
            }
        }
        return false;
    }

    public static class StringMatcher {
        private static final char MAX_UNICODE = '￿';
        private final Collator mCollator;

        StringMatcher() {
            Collator instance = Collator.getInstance();
            this.mCollator = instance;
            instance.setStrength(0);
            instance.setDecomposition(1);
        }

        public boolean matches(String str, String str2) {
            int compare = this.mCollator.compare(str, str2);
            if (compare != -1) {
                return compare == 0;
            }
            if (this.mCollator.compare(str + 65535, str2) > -1) {
                return true;
            }
            return false;
        }

        public static StringMatcher getInstance() {
            return new StringMatcher();
        }
    }

    private static boolean requestSimpleFuzzySearch(String str) {
        int i = 0;
        while (i < str.length()) {
            int codePointAt = str.codePointAt(i);
            i += Character.charCount(codePointAt);
            if (AnonymousClass1.$SwitchMap$java$lang$Character$UnicodeScript[Character.UnicodeScript.of(codePointAt).ordinal()] == 1) {
                return true;
            }
        }
        return false;
    }

    /* renamed from: com.android.launcher3.search.StringMatcherUtility$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$lang$Character$UnicodeScript;

        static {
            int[] iArr = new int[Character.UnicodeScript.values().length];
            $SwitchMap$java$lang$Character$UnicodeScript = iArr;
            try {
                iArr[Character.UnicodeScript.HAN.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
        }
    }
}
