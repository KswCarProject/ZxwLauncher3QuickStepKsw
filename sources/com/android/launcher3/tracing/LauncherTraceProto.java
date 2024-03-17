package com.android.launcher3.tracing;

import com.android.launcher3.tracing.TouchInteractionServiceProto;
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

public final class LauncherTraceProto extends GeneratedMessageLite<LauncherTraceProto, Builder> implements LauncherTraceProtoOrBuilder {
    /* access modifiers changed from: private */
    public static final LauncherTraceProto DEFAULT_INSTANCE;
    private static volatile Parser<LauncherTraceProto> PARSER = null;
    public static final int TOUCH_INTERACTION_SERVICE_FIELD_NUMBER = 1;
    private int bitField0_;
    private TouchInteractionServiceProto touchInteractionService_;

    private LauncherTraceProto() {
    }

    public boolean hasTouchInteractionService() {
        return (this.bitField0_ & 1) == 1;
    }

    public TouchInteractionServiceProto getTouchInteractionService() {
        TouchInteractionServiceProto touchInteractionServiceProto = this.touchInteractionService_;
        return touchInteractionServiceProto == null ? TouchInteractionServiceProto.getDefaultInstance() : touchInteractionServiceProto;
    }

    /* access modifiers changed from: private */
    public void setTouchInteractionService(TouchInteractionServiceProto touchInteractionServiceProto) {
        Objects.requireNonNull(touchInteractionServiceProto);
        this.touchInteractionService_ = touchInteractionServiceProto;
        this.bitField0_ |= 1;
    }

    /* access modifiers changed from: private */
    public void setTouchInteractionService(TouchInteractionServiceProto.Builder builder) {
        this.touchInteractionService_ = (TouchInteractionServiceProto) builder.build();
        this.bitField0_ |= 1;
    }

    /* access modifiers changed from: private */
    public void mergeTouchInteractionService(TouchInteractionServiceProto touchInteractionServiceProto) {
        TouchInteractionServiceProto touchInteractionServiceProto2 = this.touchInteractionService_;
        if (touchInteractionServiceProto2 == null || touchInteractionServiceProto2 == TouchInteractionServiceProto.getDefaultInstance()) {
            this.touchInteractionService_ = touchInteractionServiceProto;
        } else {
            this.touchInteractionService_ = (TouchInteractionServiceProto) ((TouchInteractionServiceProto.Builder) TouchInteractionServiceProto.newBuilder(this.touchInteractionService_).mergeFrom(touchInteractionServiceProto)).buildPartial();
        }
        this.bitField0_ |= 1;
    }

    /* access modifiers changed from: private */
    public void clearTouchInteractionService() {
        this.touchInteractionService_ = null;
        this.bitField0_ &= -2;
    }

    public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
        if ((this.bitField0_ & 1) == 1) {
            codedOutputStream.writeMessage(1, getTouchInteractionService());
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
            i2 = 0 + CodedOutputStream.computeMessageSize(1, getTouchInteractionService());
        }
        int serializedSize = i2 + this.unknownFields.getSerializedSize();
        this.memoizedSerializedSize = serializedSize;
        return serializedSize;
    }

    public static LauncherTraceProto parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
        return (LauncherTraceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
    }

    public static LauncherTraceProto parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (LauncherTraceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
    }

    public static LauncherTraceProto parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
        return (LauncherTraceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
    }

    public static LauncherTraceProto parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (LauncherTraceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
    }

    public static LauncherTraceProto parseFrom(InputStream inputStream) throws IOException {
        return (LauncherTraceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
    }

    public static LauncherTraceProto parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (LauncherTraceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
    }

    public static LauncherTraceProto parseDelimitedFrom(InputStream inputStream) throws IOException {
        return (LauncherTraceProto) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
    }

    public static LauncherTraceProto parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (LauncherTraceProto) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
    }

    public static LauncherTraceProto parseFrom(CodedInputStream codedInputStream) throws IOException {
        return (LauncherTraceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
    }

    public static LauncherTraceProto parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (LauncherTraceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
    }

    public static Builder newBuilder() {
        return (Builder) DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(LauncherTraceProto launcherTraceProto) {
        return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(launcherTraceProto);
    }

    public static final class Builder extends GeneratedMessageLite.Builder<LauncherTraceProto, Builder> implements LauncherTraceProtoOrBuilder {
        /* synthetic */ Builder(AnonymousClass1 r1) {
            this();
        }

        private Builder() {
            super(LauncherTraceProto.DEFAULT_INSTANCE);
        }

        public boolean hasTouchInteractionService() {
            return ((LauncherTraceProto) this.instance).hasTouchInteractionService();
        }

        public TouchInteractionServiceProto getTouchInteractionService() {
            return ((LauncherTraceProto) this.instance).getTouchInteractionService();
        }

        public Builder setTouchInteractionService(TouchInteractionServiceProto touchInteractionServiceProto) {
            copyOnWrite();
            ((LauncherTraceProto) this.instance).setTouchInteractionService(touchInteractionServiceProto);
            return this;
        }

        public Builder setTouchInteractionService(TouchInteractionServiceProto.Builder builder) {
            copyOnWrite();
            ((LauncherTraceProto) this.instance).setTouchInteractionService(builder);
            return this;
        }

        public Builder mergeTouchInteractionService(TouchInteractionServiceProto touchInteractionServiceProto) {
            copyOnWrite();
            ((LauncherTraceProto) this.instance).mergeTouchInteractionService(touchInteractionServiceProto);
            return this;
        }

        public Builder clearTouchInteractionService() {
            copyOnWrite();
            ((LauncherTraceProto) this.instance).clearTouchInteractionService();
            return this;
        }
    }

    /* renamed from: com.android.launcher3.tracing.LauncherTraceProto$1  reason: invalid class name */
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.tracing.LauncherTraceProto.AnonymousClass1.<clinit>():void");
        }
    }

    /* access modifiers changed from: protected */
    public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
        switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
            case 1:
                return new LauncherTraceProto();
            case 2:
                return DEFAULT_INSTANCE;
            case 3:
                return null;
            case 4:
                return new Builder((AnonymousClass1) null);
            case 5:
                GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                LauncherTraceProto launcherTraceProto = (LauncherTraceProto) obj2;
                this.touchInteractionService_ = (TouchInteractionServiceProto) visitor.visitMessage(this.touchInteractionService_, launcherTraceProto.touchInteractionService_);
                if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                    this.bitField0_ |= launcherTraceProto.bitField0_;
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
                            if (readTag == 10) {
                                TouchInteractionServiceProto.Builder builder = (this.bitField0_ & 1) == 1 ? (TouchInteractionServiceProto.Builder) this.touchInteractionService_.toBuilder() : null;
                                TouchInteractionServiceProto touchInteractionServiceProto = (TouchInteractionServiceProto) codedInputStream.readMessage(TouchInteractionServiceProto.parser(), extensionRegistryLite);
                                this.touchInteractionService_ = touchInteractionServiceProto;
                                if (builder != null) {
                                    builder.mergeFrom(touchInteractionServiceProto);
                                    this.touchInteractionService_ = (TouchInteractionServiceProto) builder.buildPartial();
                                }
                                this.bitField0_ |= 1;
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
                    synchronized (LauncherTraceProto.class) {
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
        LauncherTraceProto launcherTraceProto = new LauncherTraceProto();
        DEFAULT_INSTANCE = launcherTraceProto;
        launcherTraceProto.makeImmutable();
    }

    public static LauncherTraceProto getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<LauncherTraceProto> parser() {
        return DEFAULT_INSTANCE.getParserForType();
    }
}
