package com.android.launcher3.util;

public interface FlagOp {
    public static final FlagOp NO_OP = $$Lambda$FlagOp$1c6G2SMqOpqJgTve8Qb8m29oSGo.INSTANCE;

    static /* synthetic */ int lambda$static$0(int i) {
        return i;
    }

    int apply(int i);

    static /* synthetic */ int lambda$addFlag$1(FlagOp _this, int i, int i2) {
        return i | _this.apply(i2);
    }

    FlagOp addFlag(int i) {
        return new FlagOp(i) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final int apply(int i) {
                return FlagOp.lambda$addFlag$1(FlagOp.this, this.f$1, i);
            }
        };
    }

    static /* synthetic */ int lambda$removeFlag$2(FlagOp _this, int i, int i2) {
        return (~i) & _this.apply(i2);
    }

    FlagOp removeFlag(int i) {
        return new FlagOp(i) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final int apply(int i) {
                return FlagOp.lambda$removeFlag$2(FlagOp.this, this.f$1, i);
            }
        };
    }

    FlagOp setFlag(int i, boolean z) {
        return z ? addFlag(i) : removeFlag(i);
    }
}
