package kotlin.streams.jdk8;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;

@Metadata(d1 = {"\u0000.\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0006\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\u001a\u0012\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001*\u00020\u0003H\u0007\u001a\u0012\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00040\u0001*\u00020\u0005H\u0007\u001a\u0012\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00060\u0001*\u00020\u0007H\u0007\u001a\u001e\u0010\u0000\u001a\b\u0012\u0004\u0012\u0002H\b0\u0001\"\u0004\b\u0000\u0010\b*\b\u0012\u0004\u0012\u0002H\b0\tH\u0007\u001a\u001e\u0010\n\u001a\b\u0012\u0004\u0012\u0002H\b0\t\"\u0004\b\u0000\u0010\b*\b\u0012\u0004\u0012\u0002H\b0\u0001H\u0007\u001a\u0012\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00020\f*\u00020\u0003H\u0007\u001a\u0012\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00040\f*\u00020\u0005H\u0007\u001a\u0012\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00060\f*\u00020\u0007H\u0007\u001a\u001e\u0010\u000b\u001a\b\u0012\u0004\u0012\u0002H\b0\f\"\u0004\b\u0000\u0010\b*\b\u0012\u0004\u0012\u0002H\b0\tH\u0007¨\u0006\r"}, d2 = {"asSequence", "Lkotlin/sequences/Sequence;", "", "Ljava/util/stream/DoubleStream;", "", "Ljava/util/stream/IntStream;", "", "Ljava/util/stream/LongStream;", "T", "Ljava/util/stream/Stream;", "asStream", "toList", "", "kotlin-stdlib-jdk8"}, k = 2, mv = {1, 6, 0}, pn = "kotlin.streams", xi = 48)
/* compiled from: Streams.kt */
public final class StreamsKt {
    public static final <T> Sequence<T> asSequence(Stream<T> stream) {
        Intrinsics.checkNotNullParameter(stream, "<this>");
        return new StreamsKt$asSequence$$inlined$Sequence$1(stream);
    }

    public static final Sequence<Integer> asSequence(IntStream intStream) {
        Intrinsics.checkNotNullParameter(intStream, "<this>");
        return new StreamsKt$asSequence$$inlined$Sequence$2(intStream);
    }

    public static final Sequence<Long> asSequence(LongStream longStream) {
        Intrinsics.checkNotNullParameter(longStream, "<this>");
        return new StreamsKt$asSequence$$inlined$Sequence$3(longStream);
    }

    public static final Sequence<Double> asSequence(DoubleStream doubleStream) {
        Intrinsics.checkNotNullParameter(doubleStream, "<this>");
        return new StreamsKt$asSequence$$inlined$Sequence$4(doubleStream);
    }

    public static final <T> Stream<T> asStream(Sequence<? extends T> sequence) {
        Intrinsics.checkNotNullParameter(sequence, "<this>");
        Stream<T> stream = StreamSupport.stream(new Supplier() {
            public final Object get() {
                return StreamsKt.m1393asStream$lambda4(Sequence.this);
            }
        }, 16, false);
        Intrinsics.checkNotNullExpressionValue(stream, "stream({ Spliterators.sp…literator.ORDERED, false)");
        return stream;
    }

    /* access modifiers changed from: private */
    /* renamed from: asStream$lambda-4  reason: not valid java name */
    public static final Spliterator m1393asStream$lambda4(Sequence sequence) {
        Intrinsics.checkNotNullParameter(sequence, "$this_asStream");
        return Spliterators.spliteratorUnknownSize(sequence.iterator(), 16);
    }

    public static final <T> List<T> toList(Stream<T> stream) {
        Intrinsics.checkNotNullParameter(stream, "<this>");
        Object collect = stream.collect(Collectors.toList());
        Intrinsics.checkNotNullExpressionValue(collect, "collect(Collectors.toList<T>())");
        return (List) collect;
    }

    public static final List<Integer> toList(IntStream intStream) {
        Intrinsics.checkNotNullParameter(intStream, "<this>");
        int[] array = intStream.toArray();
        Intrinsics.checkNotNullExpressionValue(array, "toArray()");
        return ArraysKt.asList(array);
    }

    public static final List<Long> toList(LongStream longStream) {
        Intrinsics.checkNotNullParameter(longStream, "<this>");
        long[] array = longStream.toArray();
        Intrinsics.checkNotNullExpressionValue(array, "toArray()");
        return ArraysKt.asList(array);
    }

    public static final List<Double> toList(DoubleStream doubleStream) {
        Intrinsics.checkNotNullParameter(doubleStream, "<this>");
        double[] array = doubleStream.toArray();
        Intrinsics.checkNotNullExpressionValue(array, "toArray()");
        return ArraysKt.asList(array);
    }
}
