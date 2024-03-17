package com.android.launcher3.compat;

import android.content.Context;
import android.icu.text.AlphabeticIndex;
import android.os.LocaleList;
import com.android.launcher3.Utilities;
import java.util.Locale;

public class AlphabeticIndexCompat {
    private static final String MID_DOT = "∙";
    private final AlphabeticIndex.ImmutableIndex mBaseIndex;
    private final String mDefaultMiscLabel;

    public AlphabeticIndexCompat(Context context) {
        this(context.getResources().getConfiguration().getLocales());
    }

    public AlphabeticIndexCompat(LocaleList localeList) {
        int size = localeList.size();
        Locale locale = size == 0 ? Locale.ENGLISH : localeList.get(0);
        AlphabeticIndex alphabeticIndex = new AlphabeticIndex(locale);
        for (int i = 1; i < size; i++) {
            alphabeticIndex.addLabels(new Locale[]{localeList.get(i)});
        }
        alphabeticIndex.addLabels(new Locale[]{Locale.ENGLISH});
        this.mBaseIndex = alphabeticIndex.buildImmutableIndex();
        if (locale.getLanguage().equals(Locale.JAPANESE.getLanguage())) {
            this.mDefaultMiscLabel = "他";
        } else {
            this.mDefaultMiscLabel = MID_DOT;
        }
    }

    public String computeSectionName(CharSequence charSequence) {
        String trim = Utilities.trim(charSequence);
        AlphabeticIndex.ImmutableIndex immutableIndex = this.mBaseIndex;
        String label = immutableIndex.getBucket(immutableIndex.getBucketIndex(trim)).getLabel();
        if (!Utilities.trim(label).isEmpty() || trim.length() <= 0) {
            return label;
        }
        int codePointAt = trim.codePointAt(0);
        if (Character.isDigit(codePointAt)) {
            return "#";
        }
        return Character.isLetter(codePointAt) ? this.mDefaultMiscLabel : MID_DOT;
    }
}
