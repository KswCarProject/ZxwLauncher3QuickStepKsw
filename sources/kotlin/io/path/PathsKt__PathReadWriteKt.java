package kotlin.io.path;

import com.android.launcher3.LauncherSettings;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import kotlin.text.Charsets;

@Metadata(d1 = {"\u0000\u0001\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0010\u001c\n\u0002\u0010\r\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u0015\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004H\b\u001a%\u0010\u0005\u001a\u00020\u0002*\u00020\u00022\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\b\b\u0002\u0010\t\u001a\u00020\nH\b\u001a%\u0010\u0005\u001a\u00020\u0002*\u00020\u00022\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u000b2\b\b\u0002\u0010\t\u001a\u00020\nH\b\u001a\u001e\u0010\f\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\r\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\nH\u0007\u001a:\u0010\u000e\u001a\u00020\u000f*\u00020\u00022\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u0010\u001a\u00020\u00112\u0012\u0010\u0012\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00140\u0013\"\u00020\u0014H\b¢\u0006\u0002\u0010\u0015\u001a:\u0010\u0016\u001a\u00020\u0017*\u00020\u00022\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u0010\u001a\u00020\u00112\u0012\u0010\u0012\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00140\u0013\"\u00020\u0014H\b¢\u0006\u0002\u0010\u0018\u001a=\u0010\u0019\u001a\u00020\u0001*\u00020\u00022\b\b\u0002\u0010\t\u001a\u00020\n2!\u0010\u001a\u001a\u001d\u0012\u0013\u0012\u00110\u001c¢\u0006\f\b\u001d\u0012\b\b\u001e\u0012\u0004\b\b(\u001f\u0012\u0004\u0012\u00020\u00010\u001bH\bø\u0001\u0000\u001a&\u0010 \u001a\u00020!*\u00020\u00022\u0012\u0010\u0012\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00140\u0013\"\u00020\u0014H\b¢\u0006\u0002\u0010\"\u001a&\u0010#\u001a\u00020$*\u00020\u00022\u0012\u0010\u0012\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00140\u0013\"\u00020\u0014H\b¢\u0006\u0002\u0010%\u001a\r\u0010&\u001a\u00020\u0004*\u00020\u0002H\b\u001a\u001d\u0010'\u001a\b\u0012\u0004\u0012\u00020\u001c0(*\u00020\u00022\b\b\u0002\u0010\t\u001a\u00020\nH\b\u001a\u0016\u0010)\u001a\u00020\u001c*\u00020\u00022\b\b\u0002\u0010\t\u001a\u00020\nH\u0007\u001a0\u0010*\u001a\u00020+*\u00020\u00022\b\b\u0002\u0010\t\u001a\u00020\n2\u0012\u0010\u0012\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00140\u0013\"\u00020\u0014H\b¢\u0006\u0002\u0010,\u001a?\u0010-\u001a\u0002H.\"\u0004\b\u0000\u0010.*\u00020\u00022\b\b\u0002\u0010\t\u001a\u00020\n2\u0018\u0010/\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001c0\u000b\u0012\u0004\u0012\u0002H.0\u001bH\bø\u0001\u0000¢\u0006\u0002\u00100\u001a.\u00101\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\u0012\u0010\u0012\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00140\u0013\"\u00020\u0014H\b¢\u0006\u0002\u00102\u001a>\u00103\u001a\u00020\u0002*\u00020\u00022\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\b\b\u0002\u0010\t\u001a\u00020\n2\u0012\u0010\u0012\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00140\u0013\"\u00020\u0014H\b¢\u0006\u0002\u00104\u001a>\u00103\u001a\u00020\u0002*\u00020\u00022\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u000b2\b\b\u0002\u0010\t\u001a\u00020\n2\u0012\u0010\u0012\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00140\u0013\"\u00020\u0014H\b¢\u0006\u0002\u00105\u001a7\u00106\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\r\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\n2\u0012\u0010\u0012\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00140\u0013\"\u00020\u0014H\u0007¢\u0006\u0002\u00107\u001a0\u00108\u001a\u000209*\u00020\u00022\b\b\u0002\u0010\t\u001a\u00020\n2\u0012\u0010\u0012\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00140\u0013\"\u00020\u0014H\b¢\u0006\u0002\u0010:\u0002\u0007\n\u0005\b20\u0001¨\u0006;"}, d2 = {"appendBytes", "", "Ljava/nio/file/Path;", "array", "", "appendLines", "lines", "", "", "charset", "Ljava/nio/charset/Charset;", "Lkotlin/sequences/Sequence;", "appendText", "text", "bufferedReader", "Ljava/io/BufferedReader;", "bufferSize", "", "options", "", "Ljava/nio/file/OpenOption;", "(Ljava/nio/file/Path;Ljava/nio/charset/Charset;I[Ljava/nio/file/OpenOption;)Ljava/io/BufferedReader;", "bufferedWriter", "Ljava/io/BufferedWriter;", "(Ljava/nio/file/Path;Ljava/nio/charset/Charset;I[Ljava/nio/file/OpenOption;)Ljava/io/BufferedWriter;", "forEachLine", "action", "Lkotlin/Function1;", "", "Lkotlin/ParameterName;", "name", "line", "inputStream", "Ljava/io/InputStream;", "(Ljava/nio/file/Path;[Ljava/nio/file/OpenOption;)Ljava/io/InputStream;", "outputStream", "Ljava/io/OutputStream;", "(Ljava/nio/file/Path;[Ljava/nio/file/OpenOption;)Ljava/io/OutputStream;", "readBytes", "readLines", "", "readText", "reader", "Ljava/io/InputStreamReader;", "(Ljava/nio/file/Path;Ljava/nio/charset/Charset;[Ljava/nio/file/OpenOption;)Ljava/io/InputStreamReader;", "useLines", "T", "block", "(Ljava/nio/file/Path;Ljava/nio/charset/Charset;Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "writeBytes", "(Ljava/nio/file/Path;[B[Ljava/nio/file/OpenOption;)V", "writeLines", "(Ljava/nio/file/Path;Ljava/lang/Iterable;Ljava/nio/charset/Charset;[Ljava/nio/file/OpenOption;)Ljava/nio/file/Path;", "(Ljava/nio/file/Path;Lkotlin/sequences/Sequence;Ljava/nio/charset/Charset;[Ljava/nio/file/OpenOption;)Ljava/nio/file/Path;", "writeText", "(Ljava/nio/file/Path;Ljava/lang/CharSequence;Ljava/nio/charset/Charset;[Ljava/nio/file/OpenOption;)V", "writer", "Ljava/io/OutputStreamWriter;", "(Ljava/nio/file/Path;Ljava/nio/charset/Charset;[Ljava/nio/file/OpenOption;)Ljava/io/OutputStreamWriter;", "kotlin-stdlib-jdk7"}, k = 5, mv = {1, 6, 0}, xi = 49, xs = "kotlin/io/path/PathsKt")
/* compiled from: PathReadWrite.kt */
class PathsKt__PathReadWriteKt {
    static /* synthetic */ InputStreamReader reader$default(Path path, Charset charset, OpenOption[] openOptionArr, int i, Object obj) throws IOException {
        if ((i & 1) != 0) {
            charset = Charsets.UTF_8;
        }
        Intrinsics.checkNotNullParameter(path, "<this>");
        Intrinsics.checkNotNullParameter(charset, "charset");
        Intrinsics.checkNotNullParameter(openOptionArr, LauncherSettings.Favorites.OPTIONS);
        return new InputStreamReader(Files.newInputStream(path, (OpenOption[]) Arrays.copyOf(openOptionArr, openOptionArr.length)), charset);
    }

    private static final InputStreamReader reader(Path path, Charset charset, OpenOption... openOptionArr) throws IOException {
        Intrinsics.checkNotNullParameter(path, "<this>");
        Intrinsics.checkNotNullParameter(charset, "charset");
        Intrinsics.checkNotNullParameter(openOptionArr, LauncherSettings.Favorites.OPTIONS);
        return new InputStreamReader(Files.newInputStream(path, (OpenOption[]) Arrays.copyOf(openOptionArr, openOptionArr.length)), charset);
    }

    static /* synthetic */ BufferedReader bufferedReader$default(Path path, Charset charset, int i, OpenOption[] openOptionArr, int i2, Object obj) throws IOException {
        if ((i2 & 1) != 0) {
            charset = Charsets.UTF_8;
        }
        if ((i2 & 2) != 0) {
            i = 8192;
        }
        Intrinsics.checkNotNullParameter(path, "<this>");
        Intrinsics.checkNotNullParameter(charset, "charset");
        Intrinsics.checkNotNullParameter(openOptionArr, LauncherSettings.Favorites.OPTIONS);
        return new BufferedReader(new InputStreamReader(Files.newInputStream(path, (OpenOption[]) Arrays.copyOf(openOptionArr, openOptionArr.length)), charset), i);
    }

    private static final BufferedReader bufferedReader(Path path, Charset charset, int i, OpenOption... openOptionArr) throws IOException {
        Intrinsics.checkNotNullParameter(path, "<this>");
        Intrinsics.checkNotNullParameter(charset, "charset");
        Intrinsics.checkNotNullParameter(openOptionArr, LauncherSettings.Favorites.OPTIONS);
        return new BufferedReader(new InputStreamReader(Files.newInputStream(path, (OpenOption[]) Arrays.copyOf(openOptionArr, openOptionArr.length)), charset), i);
    }

    static /* synthetic */ OutputStreamWriter writer$default(Path path, Charset charset, OpenOption[] openOptionArr, int i, Object obj) throws IOException {
        if ((i & 1) != 0) {
            charset = Charsets.UTF_8;
        }
        Intrinsics.checkNotNullParameter(path, "<this>");
        Intrinsics.checkNotNullParameter(charset, "charset");
        Intrinsics.checkNotNullParameter(openOptionArr, LauncherSettings.Favorites.OPTIONS);
        return new OutputStreamWriter(Files.newOutputStream(path, (OpenOption[]) Arrays.copyOf(openOptionArr, openOptionArr.length)), charset);
    }

    private static final OutputStreamWriter writer(Path path, Charset charset, OpenOption... openOptionArr) throws IOException {
        Intrinsics.checkNotNullParameter(path, "<this>");
        Intrinsics.checkNotNullParameter(charset, "charset");
        Intrinsics.checkNotNullParameter(openOptionArr, LauncherSettings.Favorites.OPTIONS);
        return new OutputStreamWriter(Files.newOutputStream(path, (OpenOption[]) Arrays.copyOf(openOptionArr, openOptionArr.length)), charset);
    }

    static /* synthetic */ BufferedWriter bufferedWriter$default(Path path, Charset charset, int i, OpenOption[] openOptionArr, int i2, Object obj) throws IOException {
        if ((i2 & 1) != 0) {
            charset = Charsets.UTF_8;
        }
        if ((i2 & 2) != 0) {
            i = 8192;
        }
        Intrinsics.checkNotNullParameter(path, "<this>");
        Intrinsics.checkNotNullParameter(charset, "charset");
        Intrinsics.checkNotNullParameter(openOptionArr, LauncherSettings.Favorites.OPTIONS);
        return new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(path, (OpenOption[]) Arrays.copyOf(openOptionArr, openOptionArr.length)), charset), i);
    }

    private static final BufferedWriter bufferedWriter(Path path, Charset charset, int i, OpenOption... openOptionArr) throws IOException {
        Intrinsics.checkNotNullParameter(path, "<this>");
        Intrinsics.checkNotNullParameter(charset, "charset");
        Intrinsics.checkNotNullParameter(openOptionArr, LauncherSettings.Favorites.OPTIONS);
        return new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(path, (OpenOption[]) Arrays.copyOf(openOptionArr, openOptionArr.length)), charset), i);
    }

    private static final byte[] readBytes(Path path) throws IOException {
        Intrinsics.checkNotNullParameter(path, "<this>");
        byte[] readAllBytes = Files.readAllBytes(path);
        Intrinsics.checkNotNullExpressionValue(readAllBytes, "readAllBytes(this)");
        return readAllBytes;
    }

    private static final void writeBytes(Path path, byte[] bArr, OpenOption... openOptionArr) throws IOException {
        Intrinsics.checkNotNullParameter(path, "<this>");
        Intrinsics.checkNotNullParameter(bArr, "array");
        Intrinsics.checkNotNullParameter(openOptionArr, LauncherSettings.Favorites.OPTIONS);
        Files.write(path, bArr, (OpenOption[]) Arrays.copyOf(openOptionArr, openOptionArr.length));
    }

    private static final void appendBytes(Path path, byte[] bArr) throws IOException {
        Intrinsics.checkNotNullParameter(path, "<this>");
        Intrinsics.checkNotNullParameter(bArr, "array");
        Files.write(path, bArr, new OpenOption[]{StandardOpenOption.APPEND});
    }

    public static /* synthetic */ String readText$default(Path path, Charset charset, int i, Object obj) throws IOException {
        if ((i & 1) != 0) {
            charset = Charsets.UTF_8;
        }
        return PathsKt.readText(path, charset);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0032, code lost:
        throw r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x002e, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x002f, code lost:
        kotlin.io.CloseableKt.closeFinally(r2, r3);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final java.lang.String readText(java.nio.file.Path r3, java.nio.charset.Charset r4) throws java.io.IOException {
        /*
            java.lang.String r0 = "<this>"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r3, r0)
            java.lang.String r0 = "charset"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r4, r0)
            r0 = 0
            java.nio.file.OpenOption[] r1 = new java.nio.file.OpenOption[r0]
            java.io.InputStreamReader r2 = new java.io.InputStreamReader
            java.lang.Object[] r0 = java.util.Arrays.copyOf(r1, r0)
            java.nio.file.OpenOption[] r0 = (java.nio.file.OpenOption[]) r0
            java.io.InputStream r3 = java.nio.file.Files.newInputStream(r3, r0)
            r2.<init>(r3, r4)
            java.io.Closeable r2 = (java.io.Closeable) r2
            r3 = r2
            java.io.InputStreamReader r3 = (java.io.InputStreamReader) r3     // Catch:{ all -> 0x002c }
            java.io.Reader r3 = (java.io.Reader) r3     // Catch:{ all -> 0x002c }
            java.lang.String r3 = kotlin.io.TextStreamsKt.readText(r3)     // Catch:{ all -> 0x002c }
            r4 = 0
            kotlin.io.CloseableKt.closeFinally(r2, r4)
            return r3
        L_0x002c:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x002e }
        L_0x002e:
            r4 = move-exception
            kotlin.io.CloseableKt.closeFinally(r2, r3)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.io.path.PathsKt__PathReadWriteKt.readText(java.nio.file.Path, java.nio.charset.Charset):java.lang.String");
    }

    public static /* synthetic */ void writeText$default(Path path, CharSequence charSequence, Charset charset, OpenOption[] openOptionArr, int i, Object obj) throws IOException {
        if ((i & 2) != 0) {
            charset = Charsets.UTF_8;
        }
        PathsKt.writeText(path, charSequence, charset, openOptionArr);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x003c, code lost:
        throw r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0038, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0039, code lost:
        kotlin.io.CloseableKt.closeFinally(r4, r1);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final void writeText(java.nio.file.Path r1, java.lang.CharSequence r2, java.nio.charset.Charset r3, java.nio.file.OpenOption... r4) throws java.io.IOException {
        /*
            java.lang.String r0 = "<this>"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r1, r0)
            java.lang.String r0 = "text"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r2, r0)
            java.lang.String r0 = "charset"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r3, r0)
            java.lang.String r0 = "options"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r4, r0)
            int r0 = r4.length
            java.lang.Object[] r4 = java.util.Arrays.copyOf(r4, r0)
            java.nio.file.OpenOption[] r4 = (java.nio.file.OpenOption[]) r4
            java.io.OutputStream r1 = java.nio.file.Files.newOutputStream(r1, r4)
            java.lang.String r4 = "newOutputStream(this, *options)"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r1, r4)
            java.io.OutputStreamWriter r4 = new java.io.OutputStreamWriter
            r4.<init>(r1, r3)
            java.io.Closeable r4 = (java.io.Closeable) r4
            r1 = r4
            java.io.OutputStreamWriter r1 = (java.io.OutputStreamWriter) r1     // Catch:{ all -> 0x0036 }
            r1.append(r2)     // Catch:{ all -> 0x0036 }
            r1 = 0
            kotlin.io.CloseableKt.closeFinally(r4, r1)
            return
        L_0x0036:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0038 }
        L_0x0038:
            r2 = move-exception
            kotlin.io.CloseableKt.closeFinally(r4, r1)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.io.path.PathsKt__PathReadWriteKt.writeText(java.nio.file.Path, java.lang.CharSequence, java.nio.charset.Charset, java.nio.file.OpenOption[]):void");
    }

    public static /* synthetic */ void appendText$default(Path path, CharSequence charSequence, Charset charset, int i, Object obj) throws IOException {
        if ((i & 2) != 0) {
            charset = Charsets.UTF_8;
        }
        PathsKt.appendText(path, charSequence, charset);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x003a, code lost:
        throw r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0036, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0037, code lost:
        kotlin.io.CloseableKt.closeFinally(r0, r3);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final void appendText(java.nio.file.Path r3, java.lang.CharSequence r4, java.nio.charset.Charset r5) throws java.io.IOException {
        /*
            java.lang.String r0 = "<this>"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r3, r0)
            java.lang.String r0 = "text"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r4, r0)
            java.lang.String r0 = "charset"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r5, r0)
            r0 = 1
            java.nio.file.OpenOption[] r0 = new java.nio.file.OpenOption[r0]
            java.nio.file.StandardOpenOption r1 = java.nio.file.StandardOpenOption.APPEND
            java.nio.file.OpenOption r1 = (java.nio.file.OpenOption) r1
            r2 = 0
            r0[r2] = r1
            java.io.OutputStream r3 = java.nio.file.Files.newOutputStream(r3, r0)
            java.lang.String r0 = "newOutputStream(this, StandardOpenOption.APPEND)"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r3, r0)
            java.io.OutputStreamWriter r0 = new java.io.OutputStreamWriter
            r0.<init>(r3, r5)
            java.io.Closeable r0 = (java.io.Closeable) r0
            r3 = r0
            java.io.OutputStreamWriter r3 = (java.io.OutputStreamWriter) r3     // Catch:{ all -> 0x0034 }
            r3.append(r4)     // Catch:{ all -> 0x0034 }
            r3 = 0
            kotlin.io.CloseableKt.closeFinally(r0, r3)
            return
        L_0x0034:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x0036 }
        L_0x0036:
            r4 = move-exception
            kotlin.io.CloseableKt.closeFinally(r0, r3)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.io.path.PathsKt__PathReadWriteKt.appendText(java.nio.file.Path, java.lang.CharSequence, java.nio.charset.Charset):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x004c, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x004d, code lost:
        kotlin.jvm.internal.InlineMarker.finallyStart(1);
        kotlin.io.CloseableKt.closeFinally(r1, r2);
        kotlin.jvm.internal.InlineMarker.finallyEnd(1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0056, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void forEachLine$default(java.nio.file.Path r1, java.nio.charset.Charset r2, kotlin.jvm.functions.Function1 r3, int r4, java.lang.Object r5) throws java.io.IOException {
        /*
            r5 = 1
            r4 = r4 & r5
            if (r4 == 0) goto L_0x0006
            java.nio.charset.Charset r2 = kotlin.text.Charsets.UTF_8
        L_0x0006:
            java.lang.String r4 = "<this>"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r1, r4)
            java.lang.String r4 = "charset"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r2, r4)
            java.lang.String r4 = "action"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r3, r4)
            java.io.BufferedReader r1 = java.nio.file.Files.newBufferedReader(r1, r2)
            java.lang.String r2 = "newBufferedReader(this, charset)"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r1, r2)
            java.io.Reader r1 = (java.io.Reader) r1
            java.io.BufferedReader r1 = (java.io.BufferedReader) r1
            java.io.Closeable r1 = (java.io.Closeable) r1
            r2 = 0
            r4 = r1
            java.io.BufferedReader r4 = (java.io.BufferedReader) r4     // Catch:{ all -> 0x004a }
            kotlin.sequences.Sequence r4 = kotlin.io.TextStreamsKt.lineSequence(r4)     // Catch:{ all -> 0x004a }
            java.util.Iterator r4 = r4.iterator()     // Catch:{ all -> 0x004a }
        L_0x0030:
            boolean r0 = r4.hasNext()     // Catch:{ all -> 0x004a }
            if (r0 == 0) goto L_0x003e
            java.lang.Object r0 = r4.next()     // Catch:{ all -> 0x004a }
            r3.invoke(r0)     // Catch:{ all -> 0x004a }
            goto L_0x0030
        L_0x003e:
            kotlin.Unit r3 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x004a }
            kotlin.jvm.internal.InlineMarker.finallyStart(r5)
            kotlin.io.CloseableKt.closeFinally(r1, r2)
            kotlin.jvm.internal.InlineMarker.finallyEnd(r5)
            return
        L_0x004a:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x004c }
        L_0x004c:
            r3 = move-exception
            kotlin.jvm.internal.InlineMarker.finallyStart(r5)
            kotlin.io.CloseableKt.closeFinally(r1, r2)
            kotlin.jvm.internal.InlineMarker.finallyEnd(r5)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.io.path.PathsKt__PathReadWriteKt.forEachLine$default(java.nio.file.Path, java.nio.charset.Charset, kotlin.jvm.functions.Function1, int, java.lang.Object):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0047, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0048, code lost:
        kotlin.jvm.internal.InlineMarker.finallyStart(1);
        kotlin.io.CloseableKt.closeFinally(r2, r4);
        kotlin.jvm.internal.InlineMarker.finallyEnd(1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0051, code lost:
        throw r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static final void forEachLine(java.nio.file.Path r2, java.nio.charset.Charset r3, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> r4) throws java.io.IOException {
        /*
            java.lang.String r0 = "<this>"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r2, r0)
            java.lang.String r0 = "charset"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r3, r0)
            java.lang.String r0 = "action"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r4, r0)
            java.io.BufferedReader r2 = java.nio.file.Files.newBufferedReader(r2, r3)
            java.lang.String r3 = "newBufferedReader(this, charset)"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r2, r3)
            java.io.Reader r2 = (java.io.Reader) r2
            java.io.BufferedReader r2 = (java.io.BufferedReader) r2
            java.io.Closeable r2 = (java.io.Closeable) r2
            r3 = 1
            r0 = r2
            java.io.BufferedReader r0 = (java.io.BufferedReader) r0     // Catch:{ all -> 0x0045 }
            kotlin.sequences.Sequence r0 = kotlin.io.TextStreamsKt.lineSequence(r0)     // Catch:{ all -> 0x0045 }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ all -> 0x0045 }
        L_0x002a:
            boolean r1 = r0.hasNext()     // Catch:{ all -> 0x0045 }
            if (r1 == 0) goto L_0x0038
            java.lang.Object r1 = r0.next()     // Catch:{ all -> 0x0045 }
            r4.invoke(r1)     // Catch:{ all -> 0x0045 }
            goto L_0x002a
        L_0x0038:
            kotlin.Unit r4 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x0045 }
            kotlin.jvm.internal.InlineMarker.finallyStart(r3)
            r4 = 0
            kotlin.io.CloseableKt.closeFinally(r2, r4)
            kotlin.jvm.internal.InlineMarker.finallyEnd(r3)
            return
        L_0x0045:
            r4 = move-exception
            throw r4     // Catch:{ all -> 0x0047 }
        L_0x0047:
            r0 = move-exception
            kotlin.jvm.internal.InlineMarker.finallyStart(r3)
            kotlin.io.CloseableKt.closeFinally(r2, r4)
            kotlin.jvm.internal.InlineMarker.finallyEnd(r3)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.io.path.PathsKt__PathReadWriteKt.forEachLine(java.nio.file.Path, java.nio.charset.Charset, kotlin.jvm.functions.Function1):void");
    }

    private static final InputStream inputStream(Path path, OpenOption... openOptionArr) throws IOException {
        Intrinsics.checkNotNullParameter(path, "<this>");
        Intrinsics.checkNotNullParameter(openOptionArr, LauncherSettings.Favorites.OPTIONS);
        InputStream newInputStream = Files.newInputStream(path, (OpenOption[]) Arrays.copyOf(openOptionArr, openOptionArr.length));
        Intrinsics.checkNotNullExpressionValue(newInputStream, "newInputStream(this, *options)");
        return newInputStream;
    }

    private static final OutputStream outputStream(Path path, OpenOption... openOptionArr) throws IOException {
        Intrinsics.checkNotNullParameter(path, "<this>");
        Intrinsics.checkNotNullParameter(openOptionArr, LauncherSettings.Favorites.OPTIONS);
        OutputStream newOutputStream = Files.newOutputStream(path, (OpenOption[]) Arrays.copyOf(openOptionArr, openOptionArr.length));
        Intrinsics.checkNotNullExpressionValue(newOutputStream, "newOutputStream(this, *options)");
        return newOutputStream;
    }

    static /* synthetic */ List readLines$default(Path path, Charset charset, int i, Object obj) throws IOException {
        if ((i & 1) != 0) {
            charset = Charsets.UTF_8;
        }
        Intrinsics.checkNotNullParameter(path, "<this>");
        Intrinsics.checkNotNullParameter(charset, "charset");
        List<String> readAllLines = Files.readAllLines(path, charset);
        Intrinsics.checkNotNullExpressionValue(readAllLines, "readAllLines(this, charset)");
        return readAllLines;
    }

    private static final List<String> readLines(Path path, Charset charset) throws IOException {
        Intrinsics.checkNotNullParameter(path, "<this>");
        Intrinsics.checkNotNullParameter(charset, "charset");
        List<String> readAllLines = Files.readAllLines(path, charset);
        Intrinsics.checkNotNullExpressionValue(readAllLines, "readAllLines(this, charset)");
        return readAllLines;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0038, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0039, code lost:
        kotlin.jvm.internal.InlineMarker.finallyStart(1);
        kotlin.io.CloseableKt.closeFinally(r1, r2);
        kotlin.jvm.internal.InlineMarker.finallyEnd(1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0042, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ java.lang.Object useLines$default(java.nio.file.Path r1, java.nio.charset.Charset r2, kotlin.jvm.functions.Function1 r3, int r4, java.lang.Object r5) throws java.io.IOException {
        /*
            r5 = 1
            r4 = r4 & r5
            if (r4 == 0) goto L_0x0006
            java.nio.charset.Charset r2 = kotlin.text.Charsets.UTF_8
        L_0x0006:
            java.lang.String r4 = "<this>"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r1, r4)
            java.lang.String r4 = "charset"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r2, r4)
            java.lang.String r4 = "block"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r3, r4)
            java.io.BufferedReader r1 = java.nio.file.Files.newBufferedReader(r1, r2)
            java.io.Closeable r1 = (java.io.Closeable) r1
            r2 = 0
            r4 = r1
            java.io.BufferedReader r4 = (java.io.BufferedReader) r4     // Catch:{ all -> 0x0036 }
            java.lang.String r0 = "it"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r4, r0)     // Catch:{ all -> 0x0036 }
            kotlin.sequences.Sequence r4 = kotlin.io.TextStreamsKt.lineSequence(r4)     // Catch:{ all -> 0x0036 }
            java.lang.Object r3 = r3.invoke(r4)     // Catch:{ all -> 0x0036 }
            kotlin.jvm.internal.InlineMarker.finallyStart(r5)
            kotlin.io.CloseableKt.closeFinally(r1, r2)
            kotlin.jvm.internal.InlineMarker.finallyEnd(r5)
            return r3
        L_0x0036:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0038 }
        L_0x0038:
            r3 = move-exception
            kotlin.jvm.internal.InlineMarker.finallyStart(r5)
            kotlin.io.CloseableKt.closeFinally(r1, r2)
            kotlin.jvm.internal.InlineMarker.finallyEnd(r5)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.io.path.PathsKt__PathReadWriteKt.useLines$default(java.nio.file.Path, java.nio.charset.Charset, kotlin.jvm.functions.Function1, int, java.lang.Object):java.lang.Object");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x003d, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0033, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0034, code lost:
        kotlin.jvm.internal.InlineMarker.finallyStart(1);
        kotlin.io.CloseableKt.closeFinally(r2, r4);
        kotlin.jvm.internal.InlineMarker.finallyEnd(1);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static final <T> T useLines(java.nio.file.Path r2, java.nio.charset.Charset r3, kotlin.jvm.functions.Function1<? super kotlin.sequences.Sequence<java.lang.String>, ? extends T> r4) throws java.io.IOException {
        /*
            java.lang.String r0 = "<this>"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r2, r0)
            java.lang.String r0 = "charset"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r3, r0)
            java.lang.String r0 = "block"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r4, r0)
            java.io.BufferedReader r2 = java.nio.file.Files.newBufferedReader(r2, r3)
            java.io.Closeable r2 = (java.io.Closeable) r2
            r3 = 1
            r0 = r2
            java.io.BufferedReader r0 = (java.io.BufferedReader) r0     // Catch:{ all -> 0x0031 }
            java.lang.String r1 = "it"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r0, r1)     // Catch:{ all -> 0x0031 }
            kotlin.sequences.Sequence r0 = kotlin.io.TextStreamsKt.lineSequence(r0)     // Catch:{ all -> 0x0031 }
            java.lang.Object r4 = r4.invoke(r0)     // Catch:{ all -> 0x0031 }
            kotlin.jvm.internal.InlineMarker.finallyStart(r3)
            r0 = 0
            kotlin.io.CloseableKt.closeFinally(r2, r0)
            kotlin.jvm.internal.InlineMarker.finallyEnd(r3)
            return r4
        L_0x0031:
            r4 = move-exception
            throw r4     // Catch:{ all -> 0x0033 }
        L_0x0033:
            r0 = move-exception
            kotlin.jvm.internal.InlineMarker.finallyStart(r3)
            kotlin.io.CloseableKt.closeFinally(r2, r4)
            kotlin.jvm.internal.InlineMarker.finallyEnd(r3)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.io.path.PathsKt__PathReadWriteKt.useLines(java.nio.file.Path, java.nio.charset.Charset, kotlin.jvm.functions.Function1):java.lang.Object");
    }

    static /* synthetic */ Path writeLines$default(Path path, Iterable iterable, Charset charset, OpenOption[] openOptionArr, int i, Object obj) throws IOException {
        if ((i & 2) != 0) {
            charset = Charsets.UTF_8;
        }
        Intrinsics.checkNotNullParameter(path, "<this>");
        Intrinsics.checkNotNullParameter(iterable, "lines");
        Intrinsics.checkNotNullParameter(charset, "charset");
        Intrinsics.checkNotNullParameter(openOptionArr, LauncherSettings.Favorites.OPTIONS);
        Path write = Files.write(path, iterable, charset, (OpenOption[]) Arrays.copyOf(openOptionArr, openOptionArr.length));
        Intrinsics.checkNotNullExpressionValue(write, "write(this, lines, charset, *options)");
        return write;
    }

    private static final Path writeLines(Path path, Iterable<? extends CharSequence> iterable, Charset charset, OpenOption... openOptionArr) throws IOException {
        Intrinsics.checkNotNullParameter(path, "<this>");
        Intrinsics.checkNotNullParameter(iterable, "lines");
        Intrinsics.checkNotNullParameter(charset, "charset");
        Intrinsics.checkNotNullParameter(openOptionArr, LauncherSettings.Favorites.OPTIONS);
        Path write = Files.write(path, iterable, charset, (OpenOption[]) Arrays.copyOf(openOptionArr, openOptionArr.length));
        Intrinsics.checkNotNullExpressionValue(write, "write(this, lines, charset, *options)");
        return write;
    }

    static /* synthetic */ Path writeLines$default(Path path, Sequence sequence, Charset charset, OpenOption[] openOptionArr, int i, Object obj) throws IOException {
        if ((i & 2) != 0) {
            charset = Charsets.UTF_8;
        }
        Intrinsics.checkNotNullParameter(path, "<this>");
        Intrinsics.checkNotNullParameter(sequence, "lines");
        Intrinsics.checkNotNullParameter(charset, "charset");
        Intrinsics.checkNotNullParameter(openOptionArr, LauncherSettings.Favorites.OPTIONS);
        Path write = Files.write(path, SequencesKt.asIterable(sequence), charset, (OpenOption[]) Arrays.copyOf(openOptionArr, openOptionArr.length));
        Intrinsics.checkNotNullExpressionValue(write, "write(this, lines.asIterable(), charset, *options)");
        return write;
    }

    private static final Path writeLines(Path path, Sequence<? extends CharSequence> sequence, Charset charset, OpenOption... openOptionArr) throws IOException {
        Intrinsics.checkNotNullParameter(path, "<this>");
        Intrinsics.checkNotNullParameter(sequence, "lines");
        Intrinsics.checkNotNullParameter(charset, "charset");
        Intrinsics.checkNotNullParameter(openOptionArr, LauncherSettings.Favorites.OPTIONS);
        Path write = Files.write(path, SequencesKt.asIterable(sequence), charset, (OpenOption[]) Arrays.copyOf(openOptionArr, openOptionArr.length));
        Intrinsics.checkNotNullExpressionValue(write, "write(this, lines.asIterable(), charset, *options)");
        return write;
    }

    static /* synthetic */ Path appendLines$default(Path path, Iterable iterable, Charset charset, int i, Object obj) throws IOException {
        if ((i & 2) != 0) {
            charset = Charsets.UTF_8;
        }
        Intrinsics.checkNotNullParameter(path, "<this>");
        Intrinsics.checkNotNullParameter(iterable, "lines");
        Intrinsics.checkNotNullParameter(charset, "charset");
        Path write = Files.write(path, iterable, charset, new OpenOption[]{StandardOpenOption.APPEND});
        Intrinsics.checkNotNullExpressionValue(write, "write(this, lines, chars…tandardOpenOption.APPEND)");
        return write;
    }

    private static final Path appendLines(Path path, Iterable<? extends CharSequence> iterable, Charset charset) throws IOException {
        Intrinsics.checkNotNullParameter(path, "<this>");
        Intrinsics.checkNotNullParameter(iterable, "lines");
        Intrinsics.checkNotNullParameter(charset, "charset");
        Path write = Files.write(path, iterable, charset, new OpenOption[]{StandardOpenOption.APPEND});
        Intrinsics.checkNotNullExpressionValue(write, "write(this, lines, chars…tandardOpenOption.APPEND)");
        return write;
    }

    static /* synthetic */ Path appendLines$default(Path path, Sequence sequence, Charset charset, int i, Object obj) throws IOException {
        if ((i & 2) != 0) {
            charset = Charsets.UTF_8;
        }
        Intrinsics.checkNotNullParameter(path, "<this>");
        Intrinsics.checkNotNullParameter(sequence, "lines");
        Intrinsics.checkNotNullParameter(charset, "charset");
        Path write = Files.write(path, SequencesKt.asIterable(sequence), charset, new OpenOption[]{StandardOpenOption.APPEND});
        Intrinsics.checkNotNullExpressionValue(write, "write(this, lines.asIter…tandardOpenOption.APPEND)");
        return write;
    }

    private static final Path appendLines(Path path, Sequence<? extends CharSequence> sequence, Charset charset) throws IOException {
        Intrinsics.checkNotNullParameter(path, "<this>");
        Intrinsics.checkNotNullParameter(sequence, "lines");
        Intrinsics.checkNotNullParameter(charset, "charset");
        Path write = Files.write(path, SequencesKt.asIterable(sequence), charset, new OpenOption[]{StandardOpenOption.APPEND});
        Intrinsics.checkNotNullExpressionValue(write, "write(this, lines.asIter…tandardOpenOption.APPEND)");
        return write;
    }
}
