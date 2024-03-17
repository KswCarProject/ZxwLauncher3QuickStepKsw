package com.android.launcher3.folder;

import android.text.TextUtils;
import java.util.Arrays;
import java.util.function.Predicate;

public class FolderNameInfos {
    public static final int ERROR_ALL_APP_LOOKUP_FAILED = 32;
    public static final int ERROR_ALL_LABEL_LOOKUP_FAILED = 256;
    public static final int ERROR_APP_LOOKUP_FAILED = 16;
    public static final int ERROR_LABEL_LOOKUP_FAILED = 128;
    public static final int ERROR_NO_LABELS_GENERATED = 64;
    public static final int ERROR_NO_PACKAGES = 512;
    public static final int ERROR_NO_PROVIDER = 8;
    public static final int HAS_PRIMARY = 2;
    public static final int HAS_SUGGESTIONS = 4;
    public static final int SUCCESS = 1;
    private final CharSequence[] mLabels = new CharSequence[4];
    private final Float[] mScores = new Float[4];
    private int mStatus = 0;

    public void setStatus(int i) {
        this.mStatus = i | this.mStatus;
    }

    public int status() {
        return this.mStatus;
    }

    public boolean hasPrimary() {
        return (this.mStatus & 2) > 0 && this.mLabels[0] != null;
    }

    public boolean hasSuggestions() {
        for (CharSequence charSequence : this.mLabels) {
            if (charSequence != null && !TextUtils.isEmpty(charSequence)) {
                return true;
            }
        }
        return false;
    }

    public void setLabel(int i, CharSequence charSequence, Float f) {
        CharSequence[] charSequenceArr = this.mLabels;
        if (i < charSequenceArr.length) {
            charSequenceArr[i] = charSequence;
            this.mScores[i] = f;
        }
    }

    public boolean contains(CharSequence charSequence) {
        return Arrays.stream(this.mLabels).filter($$Lambda$FolderNameInfos$7h7UCwt0omQ5tvXZAQRgoe6uRo.INSTANCE).anyMatch(new Predicate(charSequence) {
            public final /* synthetic */ CharSequence f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return ((CharSequence) obj).toString().equalsIgnoreCase(this.f$0.toString());
            }
        });
    }

    public CharSequence[] getLabels() {
        return this.mLabels;
    }

    public Float[] getScores() {
        return this.mScores;
    }

    public String toString() {
        return String.format("status=%s, labels=%s", new Object[]{Integer.toBinaryString(this.mStatus), Arrays.toString(this.mLabels)});
    }
}
