package com.android.launcher3.tracing;

import com.android.launcher3.tracing.SwipeHandlerProto;
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

public final class InputConsumerProto extends GeneratedMessageLite<InputConsumerProto, Builder> implements InputConsumerProtoOrBuilder {
    /* access modifiers changed from: private */
    public static final InputConsumerProto DEFAULT_INSTANCE;
    public static final int NAME_FIELD_NUMBER = 1;
    private static volatile Parser<InputConsumerProto> PARSER = null;
    public static final int SWIPE_HANDLER_FIELD_NUMBER = 2;
    private int bitField0_;
    private String name_ = "";
    private SwipeHandlerProto swipeHandler_;

    private InputConsumerProto() {
    }

    public boolean hasName() {
        return (this.bitField0_ & 1) == 1;
    }

    public String getName() {
        return this.name_;
    }

    public ByteString getNameBytes() {
        return ByteString.copyFromUtf8(this.name_);
    }

    /* access modifiers changed from: private */
    public void setName(String str) {
        Objects.requireNonNull(str);
        this.bitField0_ |= 1;
        this.name_ = str;
    }

    /* access modifiers changed from: private */
    public void clearName() {
        this.bitField0_ &= -2;
        this.name_ = getDefaultInstance().getName();
    }

    /* access modifiers changed from: private */
    public void setNameBytes(ByteString byteString) {
        Objects.requireNonNull(byteString);
        this.bitField0_ |= 1;
        this.name_ = byteString.toStringUtf8();
    }

    public boolean hasSwipeHandler() {
        return (this.bitField0_ & 2) == 2;
    }

    public SwipeHandlerProto getSwipeHandler() {
        SwipeHandlerProto swipeHandlerProto = this.swipeHandler_;
        return swipeHandlerProto == null ? SwipeHandlerProto.getDefaultInstance() : swipeHandlerProto;
    }

    /* access modifiers changed from: private */
    public void setSwipeHandler(SwipeHandlerProto swipeHandlerProto) {
        Objects.requireNonNull(swipeHandlerProto);
        this.swipeHandler_ = swipeHandlerProto;
        this.bitField0_ |= 2;
    }

    /* access modifiers changed from: private */
    public void setSwipeHandler(SwipeHandlerProto.Builder builder) {
        this.swipeHandler_ = (SwipeHandlerProto) builder.build();
        this.bitField0_ |= 2;
    }

    /* access modifiers changed from: private */
    public void mergeSwipeHandler(SwipeHandlerProto swipeHandlerProto) {
        SwipeHandlerProto swipeHandlerProto2 = this.swipeHandler_;
        if (swipeHandlerProto2 == null || swipeHandlerProto2 == SwipeHandlerProto.getDefaultInstance()) {
            this.swipeHandler_ = swipeHandlerProto;
        } else {
            this.swipeHandler_ = (SwipeHandlerProto) ((SwipeHandlerProto.Builder) SwipeHandlerProto.newBuilder(this.swipeHandler_).mergeFrom(swipeHandlerProto)).buildPartial();
        }
        this.bitField0_ |= 2;
    }

    /* access modifiers changed from: private */
    public void clearSwipeHandler() {
        this.swipeHandler_ = null;
        this.bitField0_ &= -3;
    }

    public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
        if ((this.bitField0_ & 1) == 1) {
            codedOutputStream.writeString(1, getName());
        }
        if ((this.bitField0_ & 2) == 2) {
            codedOutputStream.writeMessage(2, getSwipeHandler());
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
            i2 = 0 + CodedOutputStream.computeStringSize(1, getName());
        }
        if ((this.bitField0_ & 2) == 2) {
            i2 += CodedOutputStream.computeMessageSize(2, getSwipeHandler());
        }
        int serializedSize = i2 + this.unknownFields.getSerializedSize();
        this.memoizedSerializedSize = serializedSize;
        return serializedSize;
    }

    public static InputConsumerProto parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
        return (InputConsumerProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
    }

    public static InputConsumerProto parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (InputConsumerProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
    }

    public static InputConsumerProto parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
        return (InputConsumerProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
    }

    public static InputConsumerProto parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (InputConsumerProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
    }

    public static InputConsumerProto parseFrom(InputStream inputStream) throws IOException {
        return (InputConsumerProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
    }

    public static InputConsumerProto parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (InputConsumerProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
    }

    public static InputConsumerProto parseDelimitedFrom(InputStream inputStream) throws IOException {
        return (InputConsumerProto) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
    }

    public static InputConsumerProto parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (InputConsumerProto) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
    }

    public static InputConsumerProto parseFrom(CodedInputStream codedInputStream) throws IOException {
        return (InputConsumerProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
    }

    public static InputConsumerProto parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (InputConsumerProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
    }

    public static Builder newBuilder() {
        return (Builder) DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(InputConsumerProto inputConsumerProto) {
        return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(inputConsumerProto);
    }

    public static final class Builder extends GeneratedMessageLite.Builder<InputConsumerProto, Builder> implements InputConsumerProtoOrBuilder {
        /* synthetic */ Builder(AnonymousClass1 r1) {
            this();
        }

        private Builder() {
            super(InputConsumerProto.DEFAULT_INSTANCE);
        }

        public boolean hasName() {
            return ((InputConsumerProto) this.instance).hasName();
        }

        public String getName() {
            return ((InputConsumerProto) this.instance).getName();
        }

        public ByteString getNameBytes() {
            return ((InputConsumerProto) this.instance).getNameBytes();
        }

        public Builder setName(String str) {
            copyOnWrite();
            ((InputConsumerProto) this.instance).setName(str);
            return this;
        }

        public Builder clearName() {
            copyOnWrite();
            ((InputConsumerProto) this.instance).clearName();
            return this;
        }

        public Builder setNameBytes(ByteString byteString) {
            copyOnWrite();
            ((InputConsumerProto) this.instance).setNameBytes(byteString);
            return this;
        }

        public boolean hasSwipeHandler() {
            return ((InputConsumerProto) this.instance).hasSwipeHandler();
        }

        public SwipeHandlerProto getSwipeHandler() {
            return ((InputConsumerProto) this.instance).getSwipeHandler();
        }

        public Builder setSwipeHandler(SwipeHandlerProto swipeHandlerProto) {
            copyOnWrite();
            ((InputConsumerProto) this.instance).setSwipeHandler(swipeHandlerProto);
            return this;
        }

        public Builder setSwipeHandler(SwipeHandlerProto.Builder builder) {
            copyOnWrite();
            ((InputConsumerProto) this.instance).setSwipeHandler(builder);
            return this;
        }

        public Builder mergeSwipeHandler(SwipeHandlerProto swipeHandlerProto) {
            copyOnWrite();
            ((InputConsumerProto) this.instance).mergeSwipeHandler(swipeHandlerProto);
            return this;
        }

        public Builder clearSwipeHandler() {
            copyOnWrite();
            ((InputConsumerProto) this.instance).clearSwipeHandler();
            return this;
        }
    }

    /* renamed from: com.android.launcher3.tracing.InputConsumerProto$1  reason: invalid class name */
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.tracing.InputConsumerProto.AnonymousClass1.<clinit>():void");
        }
    }

    /* access modifiers changed from: protected */
    public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
        switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
            case 1:
                return new InputConsumerProto();
            case 2:
                return DEFAULT_INSTANCE;
            case 3:
                return null;
            case 4:
                return new Builder((AnonymousClass1) null);
            case 5:
                GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                InputConsumerProto inputConsumerProto = (InputConsumerProto) obj2;
                this.name_ = visitor.visitString(hasName(), this.name_, inputConsumerProto.hasName(), inputConsumerProto.name_);
                this.swipeHandler_ = (SwipeHandlerProto) visitor.visitMessage(this.swipeHandler_, inputConsumerProto.swipeHandler_);
                if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                    this.bitField0_ |= inputConsumerProto.bitField0_;
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
                                String readString = codedInputStream.readString();
                                this.bitField0_ = 1 | this.bitField0_;
                                this.name_ = readString;
                            } else if (readTag == 18) {
                                SwipeHandlerProto.Builder builder = (this.bitField0_ & 2) == 2 ? (SwipeHandlerProto.Builder) this.swipeHandler_.toBuilder() : null;
                                SwipeHandlerProto swipeHandlerProto = (SwipeHandlerProto) codedInputStream.readMessage(SwipeHandlerProto.parser(), extensionRegistryLite);
                                this.swipeHandler_ = swipeHandlerProto;
                                if (builder != null) {
                                    builder.mergeFrom(swipeHandlerProto);
                                    this.swipeHandler_ = (SwipeHandlerProto) builder.buildPartial();
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
                    synchronized (InputConsumerProto.class) {
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
        InputConsumerProto inputConsumerProto = new InputConsumerProto();
        DEFAULT_INSTANCE = inputConsumerProto;
        inputConsumerProto.makeImmutable();
    }

    public static InputConsumerProto getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<InputConsumerProto> parser() {
        return DEFAULT_INSTANCE.getParserForType();
    }
}
