package com.android.launcher3.logging;

import java.security.SecureRandom;
import java.util.Random;

public class InstanceIdSequence {
    protected final int mInstanceIdMax;
    private final Random mRandom;

    public InstanceIdSequence(int i) {
        this.mRandom = new SecureRandom();
        this.mInstanceIdMax = Math.min(Math.max(1, i), 1048576);
    }

    public InstanceIdSequence() {
        this(1048576);
    }

    public InstanceId newInstanceId() {
        return newInstanceIdInternal(this.mRandom.nextInt(this.mInstanceIdMax) + 1);
    }

    /* access modifiers changed from: protected */
    public InstanceId newInstanceIdInternal(int i) {
        return new InstanceId(i);
    }
}
