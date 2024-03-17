package com.android.launcher3.util;

import android.content.Context;
import android.widget.EdgeEffect;
import com.android.launcher3.Utilities;

public class EdgeEffectCompat extends EdgeEffect {
    public EdgeEffectCompat(Context context) {
        super(context);
    }

    public float getDistance() {
        if (Utilities.ATLEAST_S) {
            return super.getDistance();
        }
        return 0.0f;
    }

    public float onPullDistance(float f, float f2) {
        if (Utilities.ATLEAST_S) {
            return super.onPullDistance(f, f2);
        }
        onPull(f, f2);
        return f;
    }
}
