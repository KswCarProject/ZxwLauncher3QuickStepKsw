package com.android.launcher3.tracing;

import com.android.launcher3.tracing.InputConsumerProto;
import com.android.launcher3.tracing.OverviewComponentObserverProto;
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

public final class TouchInteractionServiceProto extends GeneratedMessageLite<TouchInteractionServiceProto, Builder> implements TouchInteractionServiceProtoOrBuilder {
    /* access modifiers changed from: private */
    public static final TouchInteractionServiceProto DEFAULT_INSTANCE;
    public static final int INPUT_CONSUMER_FIELD_NUMBER = 3;
    public static final int OVERVIEW_COMPONENT_OBVSERVER_FIELD_NUMBER = 2;
    private static volatile Parser<TouchInteractionServiceProto> PARSER = null;
    public static final int SERVICE_CONNECTED_FIELD_NUMBER = 1;
    private int bitField0_;
    private InputConsumerProto inputConsumer_;
    private OverviewComponentObserverProto overviewComponentObvserver_;
    private boolean serviceConnected_;

    private TouchInteractionServiceProto() {
    }

    public boolean hasServiceConnected() {
        return (this.bitField0_ & 1) == 1;
    }

    public boolean getServiceConnected() {
        return this.serviceConnected_;
    }

    /* access modifiers changed from: private */
    public void setServiceConnected(boolean z) {
        this.bitField0_ |= 1;
        this.serviceConnected_ = z;
    }

    /* access modifiers changed from: private */
    public void clearServiceConnected() {
        this.bitField0_ &= -2;
        this.serviceConnected_ = false;
    }

    public boolean hasOverviewComponentObvserver() {
        return (this.bitField0_ & 2) == 2;
    }

    public OverviewComponentObserverProto getOverviewComponentObvserver() {
        OverviewComponentObserverProto overviewComponentObserverProto = this.overviewComponentObvserver_;
        return overviewComponentObserverProto == null ? OverviewComponentObserverProto.getDefaultInstance() : overviewComponentObserverProto;
    }

    /* access modifiers changed from: private */
    public void setOverviewComponentObvserver(OverviewComponentObserverProto overviewComponentObserverProto) {
        Objects.requireNonNull(overviewComponentObserverProto);
        this.overviewComponentObvserver_ = overviewComponentObserverProto;
        this.bitField0_ |= 2;
    }

    /* access modifiers changed from: private */
    public void setOverviewComponentObvserver(OverviewComponentObserverProto.Builder builder) {
        this.overviewComponentObvserver_ = (OverviewComponentObserverProto) builder.build();
        this.bitField0_ |= 2;
    }

    /* access modifiers changed from: private */
    public void mergeOverviewComponentObvserver(OverviewComponentObserverProto overviewComponentObserverProto) {
        OverviewComponentObserverProto overviewComponentObserverProto2 = this.overviewComponentObvserver_;
        if (overviewComponentObserverProto2 == null || overviewComponentObserverProto2 == OverviewComponentObserverProto.getDefaultInstance()) {
            this.overviewComponentObvserver_ = overviewComponentObserverProto;
        } else {
            this.overviewComponentObvserver_ = (OverviewComponentObserverProto) ((OverviewComponentObserverProto.Builder) OverviewComponentObserverProto.newBuilder(this.overviewComponentObvserver_).mergeFrom(overviewComponentObserverProto)).buildPartial();
        }
        this.bitField0_ |= 2;
    }

    /* access modifiers changed from: private */
    public void clearOverviewComponentObvserver() {
        this.overviewComponentObvserver_ = null;
        this.bitField0_ &= -3;
    }

    public boolean hasInputConsumer() {
        return (this.bitField0_ & 4) == 4;
    }

    public InputConsumerProto getInputConsumer() {
        InputConsumerProto inputConsumerProto = this.inputConsumer_;
        return inputConsumerProto == null ? InputConsumerProto.getDefaultInstance() : inputConsumerProto;
    }

    /* access modifiers changed from: private */
    public void setInputConsumer(InputConsumerProto inputConsumerProto) {
        Objects.requireNonNull(inputConsumerProto);
        this.inputConsumer_ = inputConsumerProto;
        this.bitField0_ |= 4;
    }

    /* access modifiers changed from: private */
    public void setInputConsumer(InputConsumerProto.Builder builder) {
        this.inputConsumer_ = (InputConsumerProto) builder.build();
        this.bitField0_ |= 4;
    }

    /* access modifiers changed from: private */
    public void mergeInputConsumer(InputConsumerProto inputConsumerProto) {
        InputConsumerProto inputConsumerProto2 = this.inputConsumer_;
        if (inputConsumerProto2 == null || inputConsumerProto2 == InputConsumerProto.getDefaultInstance()) {
            this.inputConsumer_ = inputConsumerProto;
        } else {
            this.inputConsumer_ = (InputConsumerProto) ((InputConsumerProto.Builder) InputConsumerProto.newBuilder(this.inputConsumer_).mergeFrom(inputConsumerProto)).buildPartial();
        }
        this.bitField0_ |= 4;
    }

    /* access modifiers changed from: private */
    public void clearInputConsumer() {
        this.inputConsumer_ = null;
        this.bitField0_ &= -5;
    }

    public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
        if ((this.bitField0_ & 1) == 1) {
            codedOutputStream.writeBool(1, this.serviceConnected_);
        }
        if ((this.bitField0_ & 2) == 2) {
            codedOutputStream.writeMessage(2, getOverviewComponentObvserver());
        }
        if ((this.bitField0_ & 4) == 4) {
            codedOutputStream.writeMessage(3, getInputConsumer());
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
            i2 = 0 + CodedOutputStream.computeBoolSize(1, this.serviceConnected_);
        }
        if ((this.bitField0_ & 2) == 2) {
            i2 += CodedOutputStream.computeMessageSize(2, getOverviewComponentObvserver());
        }
        if ((this.bitField0_ & 4) == 4) {
            i2 += CodedOutputStream.computeMessageSize(3, getInputConsumer());
        }
        int serializedSize = i2 + this.unknownFields.getSerializedSize();
        this.memoizedSerializedSize = serializedSize;
        return serializedSize;
    }

    public static TouchInteractionServiceProto parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
        return (TouchInteractionServiceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
    }

    public static TouchInteractionServiceProto parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (TouchInteractionServiceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
    }

    public static TouchInteractionServiceProto parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
        return (TouchInteractionServiceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
    }

    public static TouchInteractionServiceProto parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (TouchInteractionServiceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
    }

    public static TouchInteractionServiceProto parseFrom(InputStream inputStream) throws IOException {
        return (TouchInteractionServiceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
    }

    public static TouchInteractionServiceProto parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (TouchInteractionServiceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
    }

    public static TouchInteractionServiceProto parseDelimitedFrom(InputStream inputStream) throws IOException {
        return (TouchInteractionServiceProto) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
    }

    public static TouchInteractionServiceProto parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (TouchInteractionServiceProto) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
    }

    public static TouchInteractionServiceProto parseFrom(CodedInputStream codedInputStream) throws IOException {
        return (TouchInteractionServiceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
    }

    public static TouchInteractionServiceProto parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (TouchInteractionServiceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
    }

    public static Builder newBuilder() {
        return (Builder) DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(TouchInteractionServiceProto touchInteractionServiceProto) {
        return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(touchInteractionServiceProto);
    }

    public static final class Builder extends GeneratedMessageLite.Builder<TouchInteractionServiceProto, Builder> implements TouchInteractionServiceProtoOrBuilder {
        /* synthetic */ Builder(AnonymousClass1 r1) {
            this();
        }

        private Builder() {
            super(TouchInteractionServiceProto.DEFAULT_INSTANCE);
        }

        public boolean hasServiceConnected() {
            return ((TouchInteractionServiceProto) this.instance).hasServiceConnected();
        }

        public boolean getServiceConnected() {
            return ((TouchInteractionServiceProto) this.instance).getServiceConnected();
        }

        public Builder setServiceConnected(boolean z) {
            copyOnWrite();
            ((TouchInteractionServiceProto) this.instance).setServiceConnected(z);
            return this;
        }

        public Builder clearServiceConnected() {
            copyOnWrite();
            ((TouchInteractionServiceProto) this.instance).clearServiceConnected();
            return this;
        }

        public boolean hasOverviewComponentObvserver() {
            return ((TouchInteractionServiceProto) this.instance).hasOverviewComponentObvserver();
        }

        public OverviewComponentObserverProto getOverviewComponentObvserver() {
            return ((TouchInteractionServiceProto) this.instance).getOverviewComponentObvserver();
        }

        public Builder setOverviewComponentObvserver(OverviewComponentObserverProto overviewComponentObserverProto) {
            copyOnWrite();
            ((TouchInteractionServiceProto) this.instance).setOverviewComponentObvserver(overviewComponentObserverProto);
            return this;
        }

        public Builder setOverviewComponentObvserver(OverviewComponentObserverProto.Builder builder) {
            copyOnWrite();
            ((TouchInteractionServiceProto) this.instance).setOverviewComponentObvserver(builder);
            return this;
        }

        public Builder mergeOverviewComponentObvserver(OverviewComponentObserverProto overviewComponentObserverProto) {
            copyOnWrite();
            ((TouchInteractionServiceProto) this.instance).mergeOverviewComponentObvserver(overviewComponentObserverProto);
            return this;
        }

        public Builder clearOverviewComponentObvserver() {
            copyOnWrite();
            ((TouchInteractionServiceProto) this.instance).clearOverviewComponentObvserver();
            return this;
        }

        public boolean hasInputConsumer() {
            return ((TouchInteractionServiceProto) this.instance).hasInputConsumer();
        }

        public InputConsumerProto getInputConsumer() {
            return ((TouchInteractionServiceProto) this.instance).getInputConsumer();
        }

        public Builder setInputConsumer(InputConsumerProto inputConsumerProto) {
            copyOnWrite();
            ((TouchInteractionServiceProto) this.instance).setInputConsumer(inputConsumerProto);
            return this;
        }

        public Builder setInputConsumer(InputConsumerProto.Builder builder) {
            copyOnWrite();
            ((TouchInteractionServiceProto) this.instance).setInputConsumer(builder);
            return this;
        }

        public Builder mergeInputConsumer(InputConsumerProto inputConsumerProto) {
            copyOnWrite();
            ((TouchInteractionServiceProto) this.instance).mergeInputConsumer(inputConsumerProto);
            return this;
        }

        public Builder clearInputConsumer() {
            copyOnWrite();
            ((TouchInteractionServiceProto) this.instance).clearInputConsumer();
            return this;
        }
    }

    /* renamed from: com.android.launcher3.tracing.TouchInteractionServiceProto$1  reason: invalid class name */
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.tracing.TouchInteractionServiceProto.AnonymousClass1.<clinit>():void");
        }
    }

    /* access modifiers changed from: protected */
    public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
        switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
            case 1:
                return new TouchInteractionServiceProto();
            case 2:
                return DEFAULT_INSTANCE;
            case 3:
                return null;
            case 4:
                return new Builder((AnonymousClass1) null);
            case 5:
                GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                TouchInteractionServiceProto touchInteractionServiceProto = (TouchInteractionServiceProto) obj2;
                this.serviceConnected_ = visitor.visitBoolean(hasServiceConnected(), this.serviceConnected_, touchInteractionServiceProto.hasServiceConnected(), touchInteractionServiceProto.serviceConnected_);
                this.overviewComponentObvserver_ = (OverviewComponentObserverProto) visitor.visitMessage(this.overviewComponentObvserver_, touchInteractionServiceProto.overviewComponentObvserver_);
                this.inputConsumer_ = (InputConsumerProto) visitor.visitMessage(this.inputConsumer_, touchInteractionServiceProto.inputConsumer_);
                if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                    this.bitField0_ |= touchInteractionServiceProto.bitField0_;
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
                            if (readTag == 8) {
                                this.bitField0_ |= 1;
                                this.serviceConnected_ = codedInputStream.readBool();
                            } else if (readTag == 18) {
                                OverviewComponentObserverProto.Builder builder = (this.bitField0_ & 2) == 2 ? (OverviewComponentObserverProto.Builder) this.overviewComponentObvserver_.toBuilder() : null;
                                OverviewComponentObserverProto overviewComponentObserverProto = (OverviewComponentObserverProto) codedInputStream.readMessage(OverviewComponentObserverProto.parser(), extensionRegistryLite);
                                this.overviewComponentObvserver_ = overviewComponentObserverProto;
                                if (builder != null) {
                                    builder.mergeFrom(overviewComponentObserverProto);
                                    this.overviewComponentObvserver_ = (OverviewComponentObserverProto) builder.buildPartial();
                                }
                                this.bitField0_ |= 2;
                            } else if (readTag == 26) {
                                InputConsumerProto.Builder builder2 = (this.bitField0_ & 4) == 4 ? (InputConsumerProto.Builder) this.inputConsumer_.toBuilder() : null;
                                InputConsumerProto inputConsumerProto = (InputConsumerProto) codedInputStream.readMessage(InputConsumerProto.parser(), extensionRegistryLite);
                                this.inputConsumer_ = inputConsumerProto;
                                if (builder2 != null) {
                                    builder2.mergeFrom(inputConsumerProto);
                                    this.inputConsumer_ = (InputConsumerProto) builder2.buildPartial();
                                }
                                this.bitField0_ |= 4;
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
                    synchronized (TouchInteractionServiceProto.class) {
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
        TouchInteractionServiceProto touchInteractionServiceProto = new TouchInteractionServiceProto();
        DEFAULT_INSTANCE = touchInteractionServiceProto;
        touchInteractionServiceProto.makeImmutable();
    }

    public static TouchInteractionServiceProto getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<TouchInteractionServiceProto> parser() {
        return DEFAULT_INSTANCE.getParserForType();
    }
}
