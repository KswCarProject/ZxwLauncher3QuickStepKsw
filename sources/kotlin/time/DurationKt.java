package kotlin.time;

import kotlin.Deprecated;
import kotlin.DeprecatedSinceKotlin;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.math.MathKt;
import kotlin.ranges.RangesKt;

@Metadata(d1 = {"\u0000>\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0006\n\u0002\b*\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0010\f\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a \u0010#\u001a\u00020\u00072\u0006\u0010$\u001a\u00020\u00012\u0006\u0010%\u001a\u00020\u0005H\u0002ø\u0001\u0000¢\u0006\u0002\u0010&\u001a\u0018\u0010'\u001a\u00020\u00072\u0006\u0010(\u001a\u00020\u0001H\u0002ø\u0001\u0000¢\u0006\u0002\u0010\u0010\u001a\u0018\u0010)\u001a\u00020\u00072\u0006\u0010*\u001a\u00020\u0001H\u0002ø\u0001\u0000¢\u0006\u0002\u0010\u0010\u001a\u0018\u0010+\u001a\u00020\u00072\u0006\u0010,\u001a\u00020\u0001H\u0002ø\u0001\u0000¢\u0006\u0002\u0010\u0010\u001a\u0018\u0010-\u001a\u00020\u00072\u0006\u0010.\u001a\u00020\u0001H\u0002ø\u0001\u0000¢\u0006\u0002\u0010\u0010\u001a\u0010\u0010/\u001a\u00020\u00012\u0006\u0010*\u001a\u00020\u0001H\u0002\u001a\u0010\u00100\u001a\u00020\u00012\u0006\u0010.\u001a\u00020\u0001H\u0002\u001a \u00101\u001a\u00020\u00072\u0006\u00102\u001a\u0002032\u0006\u00104\u001a\u000205H\u0002ø\u0001\u0000¢\u0006\u0002\u00106\u001a\u0010\u00107\u001a\u00020\u00012\u0006\u00102\u001a\u000203H\u0002\u001a)\u00108\u001a\u00020\u0005*\u0002032\u0006\u00109\u001a\u00020\u00052\u0012\u0010:\u001a\u000e\u0012\u0004\u0012\u00020<\u0012\u0004\u0012\u0002050;H\b\u001a)\u0010=\u001a\u000203*\u0002032\u0006\u00109\u001a\u00020\u00052\u0012\u0010:\u001a\u000e\u0012\u0004\u0012\u00020<\u0012\u0004\u0012\u0002050;H\b\u001a\u001f\u0010>\u001a\u00020\u0007*\u00020\b2\u0006\u0010?\u001a\u00020\u0007H\nø\u0001\u0000¢\u0006\u0004\b@\u0010A\u001a\u001f\u0010>\u001a\u00020\u0007*\u00020\u00052\u0006\u0010?\u001a\u00020\u0007H\nø\u0001\u0000¢\u0006\u0004\bB\u0010C\u001a\u001c\u0010D\u001a\u00020\u0007*\u00020\b2\u0006\u0010E\u001a\u00020FH\u0007ø\u0001\u0000¢\u0006\u0002\u0010G\u001a\u001c\u0010D\u001a\u00020\u0007*\u00020\u00052\u0006\u0010E\u001a\u00020FH\u0007ø\u0001\u0000¢\u0006\u0002\u0010H\u001a\u001c\u0010D\u001a\u00020\u0007*\u00020\u00012\u0006\u0010E\u001a\u00020FH\u0007ø\u0001\u0000¢\u0006\u0002\u0010I\"\u000e\u0010\u0000\u001a\u00020\u0001XT¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001XT¢\u0006\u0002\n\u0000\"\u000e\u0010\u0003\u001a\u00020\u0001XT¢\u0006\u0002\n\u0000\"\u000e\u0010\u0004\u001a\u00020\u0005XT¢\u0006\u0002\n\u0000\"!\u0010\u0006\u001a\u00020\u0007*\u00020\b8FX\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\t\u0010\n\u001a\u0004\b\u000b\u0010\f\"!\u0010\u0006\u001a\u00020\u0007*\u00020\u00058FX\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\t\u0010\r\u001a\u0004\b\u000b\u0010\u000e\"!\u0010\u0006\u001a\u00020\u0007*\u00020\u00018FX\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\t\u0010\u000f\u001a\u0004\b\u000b\u0010\u0010\"!\u0010\u0011\u001a\u00020\u0007*\u00020\b8FX\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\u0012\u0010\n\u001a\u0004\b\u0013\u0010\f\"!\u0010\u0011\u001a\u00020\u0007*\u00020\u00058FX\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\u0012\u0010\r\u001a\u0004\b\u0013\u0010\u000e\"!\u0010\u0011\u001a\u00020\u0007*\u00020\u00018FX\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\u0012\u0010\u000f\u001a\u0004\b\u0013\u0010\u0010\"!\u0010\u0014\u001a\u00020\u0007*\u00020\b8FX\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\u0015\u0010\n\u001a\u0004\b\u0016\u0010\f\"!\u0010\u0014\u001a\u00020\u0007*\u00020\u00058FX\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\u0015\u0010\r\u001a\u0004\b\u0016\u0010\u000e\"!\u0010\u0014\u001a\u00020\u0007*\u00020\u00018FX\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\u0015\u0010\u000f\u001a\u0004\b\u0016\u0010\u0010\"!\u0010\u0017\u001a\u00020\u0007*\u00020\b8FX\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\u0018\u0010\n\u001a\u0004\b\u0019\u0010\f\"!\u0010\u0017\u001a\u00020\u0007*\u00020\u00058FX\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\u0018\u0010\r\u001a\u0004\b\u0019\u0010\u000e\"!\u0010\u0017\u001a\u00020\u0007*\u00020\u00018FX\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\u0018\u0010\u000f\u001a\u0004\b\u0019\u0010\u0010\"!\u0010\u001a\u001a\u00020\u0007*\u00020\b8FX\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\u001b\u0010\n\u001a\u0004\b\u001c\u0010\f\"!\u0010\u001a\u001a\u00020\u0007*\u00020\u00058FX\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\u001b\u0010\r\u001a\u0004\b\u001c\u0010\u000e\"!\u0010\u001a\u001a\u00020\u0007*\u00020\u00018FX\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\u001b\u0010\u000f\u001a\u0004\b\u001c\u0010\u0010\"!\u0010\u001d\u001a\u00020\u0007*\u00020\b8FX\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\u001e\u0010\n\u001a\u0004\b\u001f\u0010\f\"!\u0010\u001d\u001a\u00020\u0007*\u00020\u00058FX\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\u001e\u0010\r\u001a\u0004\b\u001f\u0010\u000e\"!\u0010\u001d\u001a\u00020\u0007*\u00020\u00018FX\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b\u001e\u0010\u000f\u001a\u0004\b\u001f\u0010\u0010\"!\u0010 \u001a\u00020\u0007*\u00020\b8FX\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b!\u0010\n\u001a\u0004\b\"\u0010\f\"!\u0010 \u001a\u00020\u0007*\u00020\u00058FX\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b!\u0010\r\u001a\u0004\b\"\u0010\u000e\"!\u0010 \u001a\u00020\u0007*\u00020\u00018FX\u0004ø\u0001\u0000¢\u0006\f\u0012\u0004\b!\u0010\u000f\u001a\u0004\b\"\u0010\u0010\u0002\u0004\n\u0002\b\u0019¨\u0006J"}, d2 = {"MAX_MILLIS", "", "MAX_NANOS", "MAX_NANOS_IN_MILLIS", "NANOS_IN_MILLIS", "", "days", "Lkotlin/time/Duration;", "", "getDays$annotations", "(D)V", "getDays", "(D)J", "(I)V", "(I)J", "(J)V", "(J)J", "hours", "getHours$annotations", "getHours", "microseconds", "getMicroseconds$annotations", "getMicroseconds", "milliseconds", "getMilliseconds$annotations", "getMilliseconds", "minutes", "getMinutes$annotations", "getMinutes", "nanoseconds", "getNanoseconds$annotations", "getNanoseconds", "seconds", "getSeconds$annotations", "getSeconds", "durationOf", "normalValue", "unitDiscriminator", "(JI)J", "durationOfMillis", "normalMillis", "durationOfMillisNormalized", "millis", "durationOfNanos", "normalNanos", "durationOfNanosNormalized", "nanos", "millisToNanos", "nanosToMillis", "parseDuration", "value", "", "strictIso", "", "(Ljava/lang/String;Z)J", "parseOverLongIsoComponent", "skipWhile", "startIndex", "predicate", "Lkotlin/Function1;", "", "substringWhile", "times", "duration", "times-kIfJnKk", "(DJ)J", "times-mvk6XK0", "(IJ)J", "toDuration", "unit", "Lkotlin/time/DurationUnit;", "(DLkotlin/time/DurationUnit;)J", "(ILkotlin/time/DurationUnit;)J", "(JLkotlin/time/DurationUnit;)J", "kotlin-stdlib"}, k = 2, mv = {1, 6, 0}, xi = 48)
/* compiled from: Duration.kt */
public final class DurationKt {
    public static final long MAX_MILLIS = 4611686018427387903L;
    public static final long MAX_NANOS = 4611686018426999999L;
    private static final long MAX_NANOS_IN_MILLIS = 4611686018426L;
    public static final int NANOS_IN_MILLIS = 1000000;

    @Deprecated(message = "Use 'Double.days' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.days", imports = {"kotlin.time.Duration.Companion.days"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getDays$annotations(double d) {
    }

    @Deprecated(message = "Use 'Int.days' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.days", imports = {"kotlin.time.Duration.Companion.days"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getDays$annotations(int i) {
    }

    @Deprecated(message = "Use 'Long.days' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.days", imports = {"kotlin.time.Duration.Companion.days"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getDays$annotations(long j) {
    }

    @Deprecated(message = "Use 'Double.hours' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.hours", imports = {"kotlin.time.Duration.Companion.hours"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getHours$annotations(double d) {
    }

    @Deprecated(message = "Use 'Int.hours' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.hours", imports = {"kotlin.time.Duration.Companion.hours"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getHours$annotations(int i) {
    }

    @Deprecated(message = "Use 'Long.hours' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.hours", imports = {"kotlin.time.Duration.Companion.hours"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getHours$annotations(long j) {
    }

    @Deprecated(message = "Use 'Double.microseconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.microseconds", imports = {"kotlin.time.Duration.Companion.microseconds"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getMicroseconds$annotations(double d) {
    }

    @Deprecated(message = "Use 'Int.microseconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.microseconds", imports = {"kotlin.time.Duration.Companion.microseconds"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getMicroseconds$annotations(int i) {
    }

    @Deprecated(message = "Use 'Long.microseconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.microseconds", imports = {"kotlin.time.Duration.Companion.microseconds"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getMicroseconds$annotations(long j) {
    }

    @Deprecated(message = "Use 'Double.milliseconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.milliseconds", imports = {"kotlin.time.Duration.Companion.milliseconds"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getMilliseconds$annotations(double d) {
    }

    @Deprecated(message = "Use 'Int.milliseconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.milliseconds", imports = {"kotlin.time.Duration.Companion.milliseconds"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getMilliseconds$annotations(int i) {
    }

    @Deprecated(message = "Use 'Long.milliseconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.milliseconds", imports = {"kotlin.time.Duration.Companion.milliseconds"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getMilliseconds$annotations(long j) {
    }

    @Deprecated(message = "Use 'Double.minutes' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.minutes", imports = {"kotlin.time.Duration.Companion.minutes"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getMinutes$annotations(double d) {
    }

    @Deprecated(message = "Use 'Int.minutes' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.minutes", imports = {"kotlin.time.Duration.Companion.minutes"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getMinutes$annotations(int i) {
    }

    @Deprecated(message = "Use 'Long.minutes' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.minutes", imports = {"kotlin.time.Duration.Companion.minutes"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getMinutes$annotations(long j) {
    }

    @Deprecated(message = "Use 'Double.nanoseconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.nanoseconds", imports = {"kotlin.time.Duration.Companion.nanoseconds"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getNanoseconds$annotations(double d) {
    }

    @Deprecated(message = "Use 'Int.nanoseconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.nanoseconds", imports = {"kotlin.time.Duration.Companion.nanoseconds"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getNanoseconds$annotations(int i) {
    }

    @Deprecated(message = "Use 'Long.nanoseconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.nanoseconds", imports = {"kotlin.time.Duration.Companion.nanoseconds"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getNanoseconds$annotations(long j) {
    }

    @Deprecated(message = "Use 'Double.seconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.seconds", imports = {"kotlin.time.Duration.Companion.seconds"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getSeconds$annotations(double d) {
    }

    @Deprecated(message = "Use 'Int.seconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.seconds", imports = {"kotlin.time.Duration.Companion.seconds"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getSeconds$annotations(int i) {
    }

    @Deprecated(message = "Use 'Long.seconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.seconds", imports = {"kotlin.time.Duration.Companion.seconds"}))
    @DeprecatedSinceKotlin(warningSince = "1.5")
    public static /* synthetic */ void getSeconds$annotations(long j) {
    }

    /* access modifiers changed from: private */
    public static final long millisToNanos(long j) {
        return j * ((long) NANOS_IN_MILLIS);
    }

    public static final long toDuration(int i, DurationUnit durationUnit) {
        Intrinsics.checkNotNullParameter(durationUnit, "unit");
        if (durationUnit.compareTo(DurationUnit.SECONDS) <= 0) {
            return durationOfNanos(DurationUnitKt.convertDurationUnitOverflow((long) i, durationUnit, DurationUnit.NANOSECONDS));
        }
        return toDuration((long) i, durationUnit);
    }

    public static final long toDuration(long j, DurationUnit durationUnit) {
        Intrinsics.checkNotNullParameter(durationUnit, "unit");
        long convertDurationUnitOverflow = DurationUnitKt.convertDurationUnitOverflow(MAX_NANOS, DurationUnit.NANOSECONDS, durationUnit);
        boolean z = false;
        if ((-convertDurationUnitOverflow) <= j && j <= convertDurationUnitOverflow) {
            z = true;
        }
        if (z) {
            return durationOfNanos(DurationUnitKt.convertDurationUnitOverflow(j, durationUnit, DurationUnit.NANOSECONDS));
        }
        return durationOfMillis(RangesKt.coerceIn(DurationUnitKt.convertDurationUnit(j, durationUnit, DurationUnit.MILLISECONDS), -4611686018427387903L, (long) MAX_MILLIS));
    }

    public static final long toDuration(double d, DurationUnit durationUnit) {
        Intrinsics.checkNotNullParameter(durationUnit, "unit");
        double convertDurationUnit = DurationUnitKt.convertDurationUnit(d, durationUnit, DurationUnit.NANOSECONDS);
        boolean z = true;
        if (!Double.isNaN(convertDurationUnit)) {
            long roundToLong = MathKt.roundToLong(convertDurationUnit);
            if (-4611686018426999999L > roundToLong || roundToLong >= 4611686018427000000L) {
                z = false;
            }
            if (z) {
                return durationOfNanos(roundToLong);
            }
            return durationOfMillisNormalized(MathKt.roundToLong(DurationUnitKt.convertDurationUnit(d, durationUnit, DurationUnit.MILLISECONDS)));
        }
        throw new IllegalArgumentException("Duration value cannot be NaN.".toString());
    }

    public static final long getNanoseconds(int i) {
        return toDuration(i, DurationUnit.NANOSECONDS);
    }

    public static final long getNanoseconds(long j) {
        return toDuration(j, DurationUnit.NANOSECONDS);
    }

    public static final long getNanoseconds(double d) {
        return toDuration(d, DurationUnit.NANOSECONDS);
    }

    public static final long getMicroseconds(int i) {
        return toDuration(i, DurationUnit.MICROSECONDS);
    }

    public static final long getMicroseconds(long j) {
        return toDuration(j, DurationUnit.MICROSECONDS);
    }

    public static final long getMicroseconds(double d) {
        return toDuration(d, DurationUnit.MICROSECONDS);
    }

    public static final long getMilliseconds(int i) {
        return toDuration(i, DurationUnit.MILLISECONDS);
    }

    public static final long getMilliseconds(long j) {
        return toDuration(j, DurationUnit.MILLISECONDS);
    }

    public static final long getMilliseconds(double d) {
        return toDuration(d, DurationUnit.MILLISECONDS);
    }

    public static final long getSeconds(int i) {
        return toDuration(i, DurationUnit.SECONDS);
    }

    public static final long getSeconds(long j) {
        return toDuration(j, DurationUnit.SECONDS);
    }

    public static final long getSeconds(double d) {
        return toDuration(d, DurationUnit.SECONDS);
    }

    public static final long getMinutes(int i) {
        return toDuration(i, DurationUnit.MINUTES);
    }

    public static final long getMinutes(long j) {
        return toDuration(j, DurationUnit.MINUTES);
    }

    public static final long getMinutes(double d) {
        return toDuration(d, DurationUnit.MINUTES);
    }

    public static final long getHours(int i) {
        return toDuration(i, DurationUnit.HOURS);
    }

    public static final long getHours(long j) {
        return toDuration(j, DurationUnit.HOURS);
    }

    public static final long getHours(double d) {
        return toDuration(d, DurationUnit.HOURS);
    }

    public static final long getDays(int i) {
        return toDuration(i, DurationUnit.DAYS);
    }

    public static final long getDays(long j) {
        return toDuration(j, DurationUnit.DAYS);
    }

    public static final long getDays(double d) {
        return toDuration(d, DurationUnit.DAYS);
    }

    /* renamed from: times-mvk6XK0  reason: not valid java name */
    private static final long m1544timesmvk6XK0(int i, long j) {
        return Duration.m1456timesUwyO8pc(j, i);
    }

    /* renamed from: times-kIfJnKk  reason: not valid java name */
    private static final long m1543timeskIfJnKk(double d, long j) {
        return Duration.m1455timesUwyO8pc(j, d);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x00a6 A[EDGE_INSN: B:172:0x00a6->B:49:0x00a6 ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x009a A[LOOP:1: B:33:0x006c->B:47:0x009a, LOOP_END] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final long parseDuration(java.lang.String r27, boolean r28) {
        /*
            r6 = r27
            int r7 = r27.length()
            if (r7 == 0) goto L_0x02c7
            kotlin.time.Duration$Companion r0 = kotlin.time.Duration.Companion
            long r8 = r0.m1520getZEROUwyO8pc()
            r10 = 0
            char r0 = r6.charAt(r10)
            r1 = 43
            r2 = 45
            r11 = 1
            if (r0 != r1) goto L_0x001c
        L_0x001a:
            r12 = r11
            goto L_0x0020
        L_0x001c:
            if (r0 != r2) goto L_0x001f
            goto L_0x001a
        L_0x001f:
            r12 = r10
        L_0x0020:
            if (r12 <= 0) goto L_0x0024
            r13 = r11
            goto L_0x0025
        L_0x0024:
            r13 = r10
        L_0x0025:
            r0 = 2
            r14 = 0
            if (r13 == 0) goto L_0x0034
            r1 = r6
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            boolean r1 = kotlin.text.StringsKt.startsWith$default((java.lang.CharSequence) r1, (char) r2, (boolean) r10, (int) r0, (java.lang.Object) r14)
            if (r1 == 0) goto L_0x0034
            r15 = r11
            goto L_0x0035
        L_0x0034:
            r15 = r10
        L_0x0035:
            java.lang.String r5 = "No components"
            if (r7 <= r12) goto L_0x02c0
            char r1 = r6.charAt(r12)
            r2 = 80
            java.lang.String r4 = "this as java.lang.String).substring(startIndex)"
            java.lang.String r3 = "Unexpected order of duration components"
            r16 = r5
            r5 = 58
            r0 = 48
            java.lang.String r10 = "this as java.lang.String…ing(startIndex, endIndex)"
            if (r1 != r2) goto L_0x0164
            int r12 = r12 + r11
            if (r12 == r7) goto L_0x015e
            r2 = r14
            r1 = 0
        L_0x0052:
            if (r12 >= r7) goto L_0x015a
            char r13 = r6.charAt(r12)
            r11 = 84
            if (r13 != r11) goto L_0x006b
            if (r1 != 0) goto L_0x0065
            int r12 = r12 + 1
            if (r12 == r7) goto L_0x0065
            r1 = 1
            r11 = 1
            goto L_0x0052
        L_0x0065:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            r0.<init>()
            throw r0
        L_0x006b:
            r11 = r12
        L_0x006c:
            int r13 = r27.length()
            if (r11 >= r13) goto L_0x00a3
            char r13 = r6.charAt(r11)
            if (r0 > r13) goto L_0x007d
            if (r13 >= r5) goto L_0x007d
            r16 = 1
            goto L_0x007f
        L_0x007d:
            r16 = 0
        L_0x007f:
            if (r16 != 0) goto L_0x0094
            java.lang.String r16 = "+-."
            r0 = r16
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            r17 = r15
            r5 = 2
            r15 = 0
            boolean r0 = kotlin.text.StringsKt.contains$default((java.lang.CharSequence) r0, (char) r13, (boolean) r15, (int) r5, (java.lang.Object) r14)
            if (r0 == 0) goto L_0x0092
            goto L_0x0097
        L_0x0092:
            r0 = 0
            goto L_0x0098
        L_0x0094:
            r17 = r15
            r5 = 2
        L_0x0097:
            r0 = 1
        L_0x0098:
            if (r0 == 0) goto L_0x00a6
            int r11 = r11 + 1
            r15 = r17
            r0 = 48
            r5 = 58
            goto L_0x006c
        L_0x00a3:
            r17 = r15
            r5 = 2
        L_0x00a6:
            java.lang.String r0 = r6.substring(r12, r11)
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r0, r10)
            r20 = r0
            java.lang.CharSequence r20 = (java.lang.CharSequence) r20
            int r11 = r20.length()
            if (r11 != 0) goto L_0x00b9
            r11 = 1
            goto L_0x00ba
        L_0x00b9:
            r11 = 0
        L_0x00ba:
            if (r11 != 0) goto L_0x0154
            int r11 = r0.length()
            int r12 = r12 + r11
            r11 = r6
            java.lang.CharSequence r11 = (java.lang.CharSequence) r11
            if (r12 < 0) goto L_0x013b
            int r13 = kotlin.text.StringsKt.getLastIndex(r11)
            if (r12 > r13) goto L_0x013b
            char r11 = r11.charAt(r12)
            int r12 = r12 + 1
            kotlin.time.DurationUnit r11 = kotlin.time.DurationUnitKt.durationUnitByIsoChar(r11, r1)
            if (r2 == 0) goto L_0x00e8
            r13 = r11
            java.lang.Enum r13 = (java.lang.Enum) r13
            int r2 = r2.compareTo(r13)
            if (r2 <= 0) goto L_0x00e2
            goto L_0x00e8
        L_0x00e2:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            r0.<init>(r3)
            throw r0
        L_0x00e8:
            r21 = 46
            r22 = 0
            r23 = 0
            r24 = 6
            r25 = 0
            int r2 = kotlin.text.StringsKt.indexOf$default((java.lang.CharSequence) r20, (char) r21, (int) r22, (boolean) r23, (int) r24, (java.lang.Object) r25)
            kotlin.time.DurationUnit r13 = kotlin.time.DurationUnit.SECONDS
            if (r11 != r13) goto L_0x0124
            if (r2 <= 0) goto L_0x0124
            r13 = 0
            java.lang.String r15 = r0.substring(r13, r2)
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r15, r10)
            long r14 = parseOverLongIsoComponent(r15)
            long r13 = toDuration((long) r14, (kotlin.time.DurationUnit) r11)
            long r8 = kotlin.time.Duration.m1454plusLRDsOJo(r8, r13)
            java.lang.String r0 = r0.substring(r2)
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r0, r4)
            double r13 = java.lang.Double.parseDouble(r0)
            long r13 = toDuration((double) r13, (kotlin.time.DurationUnit) r11)
            long r8 = kotlin.time.Duration.m1454plusLRDsOJo(r8, r13)
            goto L_0x0130
        L_0x0124:
            long r13 = parseOverLongIsoComponent(r0)
            long r13 = toDuration((long) r13, (kotlin.time.DurationUnit) r11)
            long r8 = kotlin.time.Duration.m1454plusLRDsOJo(r8, r13)
        L_0x0130:
            r2 = r11
            r15 = r17
            r0 = 48
            r5 = 58
            r11 = 1
            r14 = 0
            goto L_0x0052
        L_0x013b:
            java.lang.IllegalArgumentException r1 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Missing unit for value "
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.StringBuilder r0 = r2.append(r0)
            java.lang.String r0 = r0.toString()
            r1.<init>(r0)
            throw r1
        L_0x0154:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            r0.<init>()
            throw r0
        L_0x015a:
            r17 = r15
            goto L_0x02b3
        L_0x015e:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            r0.<init>()
            throw r0
        L_0x0164:
            r17 = r15
            if (r28 != 0) goto L_0x02ba
            r5 = 0
            int r0 = r7 - r12
            r1 = 8
            int r11 = java.lang.Math.max(r0, r1)
            r14 = 1
            java.lang.String r2 = "Infinity"
            r15 = 48
            r0 = r27
            r1 = r12
            r26 = r3
            r3 = r5
            r5 = r4
            r4 = r11
            r11 = r5
            r15 = r16
            r5 = r14
            boolean r0 = kotlin.text.StringsKt.regionMatches((java.lang.String) r0, (int) r1, (java.lang.String) r2, (int) r3, (int) r4, (boolean) r5)
            if (r0 == 0) goto L_0x0190
            kotlin.time.Duration$Companion r0 = kotlin.time.Duration.Companion
            long r8 = r0.m1518getINFINITEUwyO8pc()
            goto L_0x02b3
        L_0x0190:
            r0 = r13 ^ 1
            if (r13 == 0) goto L_0x01b5
            char r1 = r6.charAt(r12)
            r2 = 40
            if (r1 != r2) goto L_0x01b5
            r1 = r6
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            char r1 = kotlin.text.StringsKt.last(r1)
            r2 = 41
            if (r1 != r2) goto L_0x01b5
            int r12 = r12 + 1
            int r7 = r7 + -1
            if (r12 == r7) goto L_0x01af
            r0 = 1
            goto L_0x01b5
        L_0x01af:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            r0.<init>(r15)
            throw r0
        L_0x01b5:
            r14 = 0
            r15 = 0
        L_0x01b7:
            if (r12 >= r7) goto L_0x02b3
            if (r15 == 0) goto L_0x01d3
            if (r0 == 0) goto L_0x01d3
        L_0x01bd:
            int r1 = r27.length()
            if (r12 >= r1) goto L_0x01d3
            char r1 = r6.charAt(r12)
            r2 = 32
            if (r1 != r2) goto L_0x01cd
            r15 = 1
            goto L_0x01ce
        L_0x01cd:
            r15 = 0
        L_0x01ce:
            if (r15 == 0) goto L_0x01d3
            int r12 = r12 + 1
            goto L_0x01bd
        L_0x01d3:
            r1 = r12
        L_0x01d4:
            int r2 = r27.length()
            if (r1 >= r2) goto L_0x01f8
            char r2 = r6.charAt(r1)
            r3 = 48
            r4 = 58
            if (r3 > r2) goto L_0x01e8
            if (r2 >= r4) goto L_0x01e8
            r15 = 1
            goto L_0x01e9
        L_0x01e8:
            r15 = 0
        L_0x01e9:
            if (r15 != 0) goto L_0x01f2
            r5 = 46
            if (r2 != r5) goto L_0x01f0
            goto L_0x01f2
        L_0x01f0:
            r15 = 0
            goto L_0x01f3
        L_0x01f2:
            r15 = 1
        L_0x01f3:
            if (r15 == 0) goto L_0x01fc
            int r1 = r1 + 1
            goto L_0x01d4
        L_0x01f8:
            r3 = 48
            r4 = 58
        L_0x01fc:
            java.lang.String r1 = r6.substring(r12, r1)
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r1, r10)
            r18 = r1
            java.lang.CharSequence r18 = (java.lang.CharSequence) r18
            int r2 = r18.length()
            if (r2 != 0) goto L_0x020f
            r15 = 1
            goto L_0x0210
        L_0x020f:
            r15 = 0
        L_0x0210:
            if (r15 != 0) goto L_0x02ad
            int r2 = r1.length()
            int r12 = r12 + r2
            r2 = r12
        L_0x0218:
            int r5 = r27.length()
            if (r2 >= r5) goto L_0x0232
            char r5 = r6.charAt(r2)
            r13 = 97
            if (r13 > r5) goto L_0x022c
            r13 = 123(0x7b, float:1.72E-43)
            if (r5 >= r13) goto L_0x022c
            r15 = 1
            goto L_0x022d
        L_0x022c:
            r15 = 0
        L_0x022d:
            if (r15 == 0) goto L_0x0232
            int r2 = r2 + 1
            goto L_0x0218
        L_0x0232:
            java.lang.String r2 = r6.substring(r12, r2)
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r2, r10)
            int r5 = r2.length()
            int r12 = r12 + r5
            kotlin.time.DurationUnit r2 = kotlin.time.DurationUnitKt.durationUnitByShortName(r2)
            if (r14 == 0) goto L_0x0256
            r5 = r2
            java.lang.Enum r5 = (java.lang.Enum) r5
            int r5 = r14.compareTo(r5)
            if (r5 <= 0) goto L_0x024e
            goto L_0x0256
        L_0x024e:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            r5 = r26
            r0.<init>(r5)
            throw r0
        L_0x0256:
            r5 = r26
            r19 = 46
            r20 = 0
            r21 = 0
            r22 = 6
            r23 = 0
            int r13 = kotlin.text.StringsKt.indexOf$default((java.lang.CharSequence) r18, (char) r19, (int) r20, (boolean) r21, (int) r22, (java.lang.Object) r23)
            if (r13 <= 0) goto L_0x029a
            r14 = 0
            java.lang.String r15 = r1.substring(r14, r13)
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r15, r10)
            long r3 = java.lang.Long.parseLong(r15)
            long r3 = toDuration((long) r3, (kotlin.time.DurationUnit) r2)
            long r3 = kotlin.time.Duration.m1454plusLRDsOJo(r8, r3)
            java.lang.String r1 = r1.substring(r13)
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r1, r11)
            double r8 = java.lang.Double.parseDouble(r1)
            long r8 = toDuration((double) r8, (kotlin.time.DurationUnit) r2)
            long r8 = kotlin.time.Duration.m1454plusLRDsOJo(r3, r8)
            if (r12 < r7) goto L_0x0292
            goto L_0x02a7
        L_0x0292:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r1 = "Fractional component must be last"
            r0.<init>(r1)
            throw r0
        L_0x029a:
            r14 = 0
            long r3 = java.lang.Long.parseLong(r1)
            long r3 = toDuration((long) r3, (kotlin.time.DurationUnit) r2)
            long r8 = kotlin.time.Duration.m1454plusLRDsOJo(r8, r3)
        L_0x02a7:
            r14 = r2
            r26 = r5
            r15 = 1
            goto L_0x01b7
        L_0x02ad:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            r0.<init>()
            throw r0
        L_0x02b3:
            if (r17 == 0) goto L_0x02b9
            long r8 = kotlin.time.Duration.m1470unaryMinusUwyO8pc(r8)
        L_0x02b9:
            return r8
        L_0x02ba:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            r0.<init>()
            throw r0
        L_0x02c0:
            r15 = r5
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            r0.<init>(r15)
            throw r0
        L_0x02c7:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r1 = "The string is empty"
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.time.DurationKt.parseDuration(java.lang.String, boolean):long");
    }

    /* JADX WARNING: Removed duplicated region for block: B:25:0x0062  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static final long parseOverLongIsoComponent(java.lang.String r7) {
        /*
            int r0 = r7.length()
            r1 = 0
            r2 = 2
            r3 = 1
            r4 = 0
            if (r0 <= 0) goto L_0x001a
            java.lang.String r5 = "+-"
            java.lang.CharSequence r5 = (java.lang.CharSequence) r5
            char r6 = r7.charAt(r4)
            boolean r5 = kotlin.text.StringsKt.contains$default((java.lang.CharSequence) r5, (char) r6, (boolean) r4, (int) r2, (java.lang.Object) r1)
            if (r5 == 0) goto L_0x001a
            r5 = r3
            goto L_0x001b
        L_0x001a:
            r5 = r4
        L_0x001b:
            int r0 = r0 - r5
            r6 = 16
            if (r0 <= r6) goto L_0x0073
            kotlin.ranges.IntRange r0 = new kotlin.ranges.IntRange
            r6 = r7
            java.lang.CharSequence r6 = (java.lang.CharSequence) r6
            int r6 = kotlin.text.StringsKt.getLastIndex(r6)
            r0.<init>(r5, r6)
            java.lang.Iterable r0 = (java.lang.Iterable) r0
            boolean r5 = r0 instanceof java.util.Collection
            if (r5 == 0) goto L_0x003d
            r5 = r0
            java.util.Collection r5 = (java.util.Collection) r5
            boolean r5 = r5.isEmpty()
            if (r5 == 0) goto L_0x003d
        L_0x003b:
            r0 = r3
            goto L_0x0060
        L_0x003d:
            java.util.Iterator r0 = r0.iterator()
        L_0x0041:
            boolean r5 = r0.hasNext()
            if (r5 == 0) goto L_0x003b
            r5 = r0
            kotlin.collections.IntIterator r5 = (kotlin.collections.IntIterator) r5
            int r5 = r5.nextInt()
            char r5 = r7.charAt(r5)
            r6 = 48
            if (r6 > r5) goto L_0x005c
            r6 = 58
            if (r5 >= r6) goto L_0x005c
            r5 = r3
            goto L_0x005d
        L_0x005c:
            r5 = r4
        L_0x005d:
            if (r5 != 0) goto L_0x0041
            r0 = r4
        L_0x0060:
            if (r0 == 0) goto L_0x0073
            char r7 = r7.charAt(r4)
            r0 = 45
            if (r7 != r0) goto L_0x006d
            r0 = -9223372036854775808
            goto L_0x0072
        L_0x006d:
            r0 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
        L_0x0072:
            return r0
        L_0x0073:
            java.lang.String r0 = "+"
            boolean r0 = kotlin.text.StringsKt.startsWith$default(r7, r0, r4, r2, r1)
            if (r0 == 0) goto L_0x007f
            java.lang.String r7 = kotlin.text.StringsKt.drop((java.lang.String) r7, (int) r3)
        L_0x007f:
            long r0 = java.lang.Long.parseLong(r7)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.time.DurationKt.parseOverLongIsoComponent(java.lang.String):long");
    }

    private static final int skipWhile(String str, int i, Function1<? super Character, Boolean> function1) {
        while (i < str.length() && function1.invoke(Character.valueOf(str.charAt(i))).booleanValue()) {
            i++;
        }
        return i;
    }

    /* access modifiers changed from: private */
    public static final long nanosToMillis(long j) {
        return j / ((long) NANOS_IN_MILLIS);
    }

    /* access modifiers changed from: private */
    public static final long durationOfNanos(long j) {
        return Duration.m1418constructorimpl(j << 1);
    }

    /* access modifiers changed from: private */
    public static final long durationOfMillis(long j) {
        return Duration.m1418constructorimpl((j << 1) + 1);
    }

    /* access modifiers changed from: private */
    public static final long durationOf(long j, int i) {
        return Duration.m1418constructorimpl((j << 1) + ((long) i));
    }

    /* access modifiers changed from: private */
    public static final long durationOfNanosNormalized(long j) {
        boolean z = false;
        if (-4611686018426999999L <= j && j < 4611686018427000000L) {
            z = true;
        }
        if (z) {
            return durationOfNanos(j);
        }
        return durationOfMillis(nanosToMillis(j));
    }

    /* access modifiers changed from: private */
    public static final long durationOfMillisNormalized(long j) {
        boolean z = false;
        if (-4611686018426L <= j && j < 4611686018427L) {
            z = true;
        }
        if (z) {
            return durationOfNanos(millisToNanos(j));
        }
        return durationOfMillis(RangesKt.coerceIn(j, -4611686018427387903L, (long) MAX_MILLIS));
    }

    private static final String substringWhile(String str, int i, Function1<? super Character, Boolean> function1) {
        int i2 = i;
        while (i2 < str.length() && function1.invoke(Character.valueOf(str.charAt(i2))).booleanValue()) {
            i2++;
        }
        String substring = str.substring(i, i2);
        Intrinsics.checkNotNullExpressionValue(substring, "this as java.lang.String…ing(startIndex, endIndex)");
        return substring;
    }
}
