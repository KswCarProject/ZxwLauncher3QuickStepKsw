package com.android.launcher3.tracing;

import com.android.launcher3.tracing.LauncherTraceProto;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public final class LauncherTraceEntryProto extends GeneratedMessageLite<LauncherTraceEntryProto, Builder> implements LauncherTraceEntryProtoOrBuilder {
    /* access modifiers changed from: private */
    public static final LauncherTraceEntryProto DEFAULT_INSTANCE;
    public static final int ELAPSED_REALTIME_NANOS_FIELD_NUMBER = 1;
    public static final int LAUNCHER_FIELD_NUMBER = 3;
    private static volatile Parser<LauncherTraceEntryProto> PARSER;
    private int bitField0_;
    private long elapsedRealtimeNanos_;
    private LauncherTraceProto launcher_;

    private LauncherTraceEntryProto() {
    }

    public boolean hasElapsedRealtimeNanos() {
        return (this.bitField0_ & 1) == 1;
    }

    public long getElapsedRealtimeNanos() {
        return this.elapsedRealtimeNanos_;
    }

    /* access modifiers changed from: private */
    public void setElapsedRealtimeNanos(long j) {
        this.bitField0_ |= 1;
        this.elapsedRealtimeNanos_ = j;
    }

    /* access modifiers changed from: private */
    public void clearElapsedRealtimeNanos() {
        this.bitField0_ &= -2;
        this.elapsedRealtimeNanos_ = 0;
    }

    public boolean hasLauncher() {
        return (this.bitField0_ & 2) == 2;
    }

    public LauncherTraceProto getLauncher() {
        LauncherTraceProto launcherTraceProto = this.launcher_;
        return launcherTraceProto == null ? LauncherTraceProto.getDefaultInstance() : launcherTraceProto;
    }

    /* access modifiers changed from: private */
    public void setLauncher(LauncherTraceProto launcherTraceProto) {
        Objects.requireNonNull(launcherTraceProto);
        this.launcher_ = launcherTraceProto;
        this.bitField0_ |= 2;
    }

    /* access modifiers changed from: private */
    public void setLauncher(LauncherTraceProto.Builder builder) {
        this.launcher_ = (LauncherTraceProto) builder.build();
        this.bitField0_ |= 2;
    }

    /* access modifiers changed from: private */
    public void mergeLauncher(LauncherTraceProto launcherTraceProto) {
        LauncherTraceProto launcherTraceProto2 = this.launcher_;
        if (launcherTraceProto2 == null || launcherTraceProto2 == LauncherTraceProto.getDefaultInstance()) {
            this.launcher_ = launcherTraceProto;
        } else {
            this.launcher_ = (LauncherTraceProto) ((LauncherTraceProto.Builder) LauncherTraceProto.newBuilder(this.launcher_).mergeFrom(launcherTraceProto)).buildPartial();
        }
        this.bitField0_ |= 2;
    }

    /* access modifiers changed from: private */
    public void clearLauncher() {
        this.launcher_ = null;
        this.bitField0_ &= -3;
    }

    public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
        if ((this.bitField0_ & 1) == 1) {
            codedOutputStream.writeFixed64(1, this.elapsedRealtimeNanos_);
        }
        if ((this.bitField0_ & 2) == 2) {
            codedOutputStream.writeMessage(3, getLauncher());
        }
        this.unknownFields.writeTo(codedOutputStream);
    }

    public int getSerializedSize() {
        int i = this.memoizedSerializedSize;
        if (i != -1) {
            return i;
        }
        int i2 = 0;
        if ((this.bitField0_ & 1) == 1) {
            i2 = 0 + CodedOutputStream.computeFixed64Size(1, this.elapsedRealtimeNanos_);
        }
        if ((this.bitField0_ & 2) == 2) {
            i2 += CodedOutputStream.computeMessageSize(3, getLauncher());
        }
        int serializedSize = i2 + this.unknownFields.getSerializedSize();
        this.memoizedSerializedSize = serializedSize;
        return serializedSize;
    }

    public static LauncherTraceEntryProto parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
        return (LauncherTraceEntryProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
    }

    public static LauncherTraceEntryProto parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (LauncherTraceEntryProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
    }

    public static LauncherTraceEntryProto parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
        return (LauncherTraceEntryProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
    }

    public static LauncherTraceEntryProto parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (LauncherTraceEntryProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
    }

    public static LauncherTraceEntryProto parseFrom(InputStream inputStream) throws IOException {
        return (LauncherTraceEntryProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
    }

    public static LauncherTraceEntryProto parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (LauncherTraceEntryProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
    }

    public static LauncherTraceEntryProto parseDelimitedFrom(InputStream inputStream) throws IOException {
        return (LauncherTraceEntryProto) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
    }

    public static LauncherTraceEntryProto parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (LauncherTraceEntryProto) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
    }

    public static LauncherTraceEntryProto parseFrom(CodedInputStream codedInputStream) throws IOException {
        return (LauncherTraceEntryProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
    }

    public static LauncherTraceEntryProto parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (LauncherTraceEntryProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
    }

    public static Builder newBuilder() {
        return (Builder) DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(LauncherTraceEntryProto launcherTraceEntryProto) {
        return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(launcherTraceEntryProto);
    }

    public static final class Builder extends GeneratedMessageLite.Builder<LauncherTraceEntryProto, Builder> implements LauncherTraceEntryProtoOrBuilder {
        /* synthetic */ Builder(AnonymousClass1 r1) {
            this();
        }

        private Builder() {
            super(LauncherTraceEntryProto.DEFAULT_INSTANCE);
        }

        public boolean hasElapsedRealtimeNanos() {
            return ((LauncherTraceEntryProto) this.instance).hasElapsedRealtimeNanos();
        }

        public long getElapsedRealtimeNanos() {
            return ((LauncherTraceEntryProto) this.instance).getElapsedRealtimeNanos();
        }

        public Builder setElapsedRealtimeNanos(long j) {
            copyOnWrite();
            ((LauncherTraceEntryProto) this.instance).setElapsedRealtimeNanos(j);
            return this;
        }

        public Builder clearElapsedRealtimeNanos() {
            copyOnWrite();
            ((LauncherTraceEntryProto) this.instance).clearElapsedRealtimeNanos();
            return this;
        }

        public boolean hasLauncher() {
            return ((LauncherTraceEntryProto) this.instance).hasLauncher();
        }

        public LauncherTraceProto getLauncher() {
            return ((LauncherTraceEntryProto) this.instance).getLauncher();
        }

        public Builder setLauncher(LauncherTraceProto launcherTraceProto) {
            copyOnWrite();
            ((LauncherTraceEntryProto) this.instance).setLauncher(launcherTraceProto);
            return this;
        }

        public Builder setLauncher(LauncherTraceProto.Builder builder) {
            copyOnWrite();
            ((LauncherTraceEntryProto) this.instance).setLauncher(builder);
            return this;
        }

        public Builder mergeLauncher(LauncherTraceProto launcherTraceProto) {
            copyOnWrite();
            ((LauncherTraceEntryProto) this.instance).mergeLauncher(launcherTraceProto);
            return this;
        }

        public Builder clearLauncher() {
            copyOnWrite();
            ((LauncherTraceEntryProto) this.instance).clearLauncher();
            return this;
        }
    }

    /* renamed from: com.android.launcher3.tracing.LauncherTraceEntryProto$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke;

        /* JADX WARNING: Can't wrap try/catch for region: R(18:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|18) */
        /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x0049 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x0054 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0033 */
        static {
            /*
                com.google.protobuf.GeneratedMessageLite$MethodToInvoke[] r0 = com.google.protobuf.GeneratedMessageLite.MethodToInvoke.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke = r0
                com.google.protobuf.GeneratedMessageLite$MethodToInvoke r1 = com.google.protobuf.GeneratedMessageLite.MethodToInvoke.NEW_MUTABLE_INSTANCE     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke     // Catch:{ NoSuchFieldError -> 0x001d }
                com.google.protobuf.GeneratedMessageLite$MethodToInvoke r1 = com.google.protobuf.GeneratedMessageLite.MethodToInvoke.IS_INITIALIZED     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.google.protobuf.GeneratedMessageLite$MethodToInvoke r1 = com.google.protobuf.GeneratedMessageLite.MethodToInvoke.MAKE_IMMUTABLE     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.google.protobuf.GeneratedMessageLite$MethodToInvoke r1 = com.google.protobuf.GeneratedMessageLite.MethodToInvoke.NEW_BUILDER     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                int[] r0 = $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke     // Catch:{ NoSuchFieldError -> 0x003e }
                com.google.protobuf.GeneratedMessageLite$MethodToInvoke r1 = com.google.protobuf.GeneratedMessageLite.MethodToInvoke.VISIT     // Catch:{ NoSuchFieldError -> 0x003e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                int[] r0 = $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke     // Catch:{ NoSuchFieldError -> 0x0049 }
                com.google.protobuf.GeneratedMessageLite$MethodToInvoke r1 = com.google.protobuf.GeneratedMessageLite.MethodToInvoke.MERGE_FROM_STREAM     // Catch:{ NoSuchFieldError -> 0x0049 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0049 }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0049 }
            L_0x0049:
                int[] r0 = $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke     // Catch:{ NoSuchFieldError -> 0x0054 }
                com.google.protobuf.GeneratedMessageLite$MethodToInvoke r1 = com.google.protobuf.GeneratedMessageLite.MethodToInvoke.GET_DEFAULT_INSTANCE     // Catch:{ NoSuchFieldError -> 0x0054 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0054 }
                r2 = 7
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0054 }
            L_0x0054:
                int[] r0 = $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke     // Catch:{ NoSuchFieldError -> 0x0060 }
                com.google.protobuf.GeneratedMessageLite$MethodToInvoke r1 = com.google.protobuf.GeneratedMessageLite.MethodToInvoke.GET_PARSER     // Catch:{ NoSuchFieldError -> 0x0060 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0060 }
                r2 = 8
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0060 }
            L_0x0060:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.tracing.LauncherTraceEntryProto.AnonymousClass1.<clinit>():void");
        }
    }

    /* access modifiers changed from: protected */
    public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
        switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
            case 1:
                return new LauncherTraceEntryProto();
            case 2:
                return DEFAULT_INSTANCE;
            case 3:
                return null;
            case 4:
                return new Builder((AnonymousClass1) null);
            case 5:
                GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                LauncherTraceEntryProto launcherTraceEntryProto = (LauncherTraceEntryProto) obj2;
                this.elapsedRealtimeNanos_ = visitor.visitLong(hasElapsedRealtimeNanos(), this.elapsedRealtimeNanos_, launcherTraceEntryProto.hasElapsedRealtimeNanos(), launcherTraceEntryProto.elapsedRealtimeNanos_);
                this.launcher_ = (LauncherTraceProto) visitor.visitMessage(this.launcher_, launcherTraceEntryProto.launcher_);
                if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                    this.bitField0_ |= launcherTraceEntryProto.bitField0_;
                }
                return this;
            case 6:
                CodedInputStream codedInputStream = (CodedInputStream) obj;
                ExtensionRegistryLite extensionRegistryLite = (ExtensionRegistryLite) obj2;
                boolean z = false;
                while (!z) {
                    try {
                        int readTag = codedInputStream.readTag();
                        if (readTag != 0) {
                            if (readTag == 9) {
                                this.bitField0_ |= 1;
                                this.elapsedRealtimeNanos_ = codedInputStream.readFixed64();
                            } else if (readTag == 26) {
                                LauncherTraceProto.Builder builder = (this.bitField0_ & 2) == 2 ? (LauncherTraceProto.Builder) this.launcher_.toBuilder() : null;
                                LauncherTraceProto launcherTraceProto = (LauncherTraceProto) codedInputStream.readMessage(LauncherTraceProto.parser(), extensionRegistryLite);
                                this.launcher_ = launcherTraceProto;
                                if (builder != null) {
                                    builder.mergeFrom(launcherTraceProto);
                                    this.launcher_ = (LauncherTraceProto) builder.buildPartial();
                                }
                                this.bitField0_ |= 2;
                            } else if (!parseUnknownField(readTag, codedInputStream)) {
                            }
                        }
                        z = true;
                    } catch (InvalidProtocolBufferException e) {
                        throw new RuntimeException(e.setUnfinishedMessage(this));
                    } catch (IOException e2) {
                        throw new RuntimeException(new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this));
                    }
                }
                break;
            case 7:
                break;
            case 8:
                if (PARSER == null) {
                    synchronized (LauncherTraceEntryProto.class) {
                        if (PARSER == null) {
                            PARSER = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                        }
                    }
                }
                return PARSER;
            default:
                throw new UnsupportedOperationException();
        }
        return DEFAULT_INSTANCE;
    }

    static {
        LauncherTraceEntryProto launcherTraceEntryProto = new LauncherTraceEntryProto();
        DEFAULT_INSTANCE = launcherTraceEntryProto;
        launcherTraceEntryProto.makeImmutable();
    }

    public static LauncherTraceEntryProto getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<LauncherTraceEntryProto> parser() {
        return DEFAULT_INSTANCE.getParserForType();
    }
}
