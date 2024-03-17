package com.android.launcher3.tracing;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public final class GestureStateProto extends GeneratedMessageLite<GestureStateProto, Builder> implements GestureStateProtoOrBuilder {
    /* access modifiers changed from: private */
    public static final GestureStateProto DEFAULT_INSTANCE;
    public static final int ENDTARGET_FIELD_NUMBER = 1;
    private static volatile Parser<GestureStateProto> PARSER;
    private int bitField0_;
    private int endTarget_;

    private GestureStateProto() {
    }

    public enum GestureEndTarget implements Internal.EnumLite {
        UNSET(0),
        HOME(1),
        RECENTS(2),
        NEW_TASK(3),
        LAST_TASK(4);
        
        public static final int HOME_VALUE = 1;
        public static final int LAST_TASK_VALUE = 4;
        public static final int NEW_TASK_VALUE = 3;
        public static final int RECENTS_VALUE = 2;
        public static final int UNSET_VALUE = 0;
        private static final Internal.EnumLiteMap<GestureEndTarget> internalValueMap = null;
        private final int value;

        static {
            internalValueMap = new Internal.EnumLiteMap<GestureEndTarget>() {
                public GestureEndTarget findValueByNumber(int i) {
                    return GestureEndTarget.forNumber(i);
                }
            };
        }

        public final int getNumber() {
            return this.value;
        }

        @Deprecated
        public static GestureEndTarget valueOf(int i) {
            return forNumber(i);
        }

        public static GestureEndTarget forNumber(int i) {
            if (i == 0) {
                return UNSET;
            }
            if (i == 1) {
                return HOME;
            }
            if (i == 2) {
                return RECENTS;
            }
            if (i == 3) {
                return NEW_TASK;
            }
            if (i != 4) {
                return null;
            }
            return LAST_TASK;
        }

        public static Internal.EnumLiteMap<GestureEndTarget> internalGetValueMap() {
            return internalValueMap;
        }

        private GestureEndTarget(int i) {
            this.value = i;
        }
    }

    public boolean hasEndTarget() {
        return (this.bitField0_ & 1) == 1;
    }

    public GestureEndTarget getEndTarget() {
        GestureEndTarget forNumber = GestureEndTarget.forNumber(this.endTarget_);
        return forNumber == null ? GestureEndTarget.UNSET : forNumber;
    }

    /* access modifiers changed from: private */
    public void setEndTarget(GestureEndTarget gestureEndTarget) {
        Objects.requireNonNull(gestureEndTarget);
        this.bitField0_ |= 1;
        this.endTarget_ = gestureEndTarget.getNumber();
    }

    /* access modifiers changed from: private */
    public void clearEndTarget() {
        this.bitField0_ &= -2;
        this.endTarget_ = 0;
    }

    public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
        if ((this.bitField0_ & 1) == 1) {
            codedOutputStream.writeEnum(1, this.endTarget_);
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
            i2 = 0 + CodedOutputStream.computeEnumSize(1, this.endTarget_);
        }
        int serializedSize = i2 + this.unknownFields.getSerializedSize();
        this.memoizedSerializedSize = serializedSize;
        return serializedSize;
    }

    public static GestureStateProto parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
        return (GestureStateProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
    }

    public static GestureStateProto parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (GestureStateProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
    }

    public static GestureStateProto parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
        return (GestureStateProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
    }

    public static GestureStateProto parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (GestureStateProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
    }

    public static GestureStateProto parseFrom(InputStream inputStream) throws IOException {
        return (GestureStateProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
    }

    public static GestureStateProto parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (GestureStateProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
    }

    public static GestureStateProto parseDelimitedFrom(InputStream inputStream) throws IOException {
        return (GestureStateProto) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
    }

    public static GestureStateProto parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (GestureStateProto) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
    }

    public static GestureStateProto parseFrom(CodedInputStream codedInputStream) throws IOException {
        return (GestureStateProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
    }

    public static GestureStateProto parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (GestureStateProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
    }

    public static Builder newBuilder() {
        return (Builder) DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(GestureStateProto gestureStateProto) {
        return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(gestureStateProto);
    }

    public static final class Builder extends GeneratedMessageLite.Builder<GestureStateProto, Builder> implements GestureStateProtoOrBuilder {
        /* synthetic */ Builder(AnonymousClass1 r1) {
            this();
        }

        private Builder() {
            super(GestureStateProto.DEFAULT_INSTANCE);
        }

        public boolean hasEndTarget() {
            return ((GestureStateProto) this.instance).hasEndTarget();
        }

        public GestureEndTarget getEndTarget() {
            return ((GestureStateProto) this.instance).getEndTarget();
        }

        public Builder setEndTarget(GestureEndTarget gestureEndTarget) {
            copyOnWrite();
            ((GestureStateProto) this.instance).setEndTarget(gestureEndTarget);
            return this;
        }

        public Builder clearEndTarget() {
            copyOnWrite();
            ((GestureStateProto) this.instance).clearEndTarget();
            return this;
        }
    }

    /* renamed from: com.android.launcher3.tracing.GestureStateProto$1  reason: invalid class name */
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.tracing.GestureStateProto.AnonymousClass1.<clinit>():void");
        }
    }

    /* access modifiers changed from: protected */
    public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
        switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
            case 1:
                return new GestureStateProto();
            case 2:
                return DEFAULT_INSTANCE;
            case 3:
                return null;
            case 4:
                return new Builder((AnonymousClass1) null);
            case 5:
                GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                GestureStateProto gestureStateProto = (GestureStateProto) obj2;
                this.endTarget_ = visitor.visitInt(hasEndTarget(), this.endTarget_, gestureStateProto.hasEndTarget(), gestureStateProto.endTarget_);
                if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                    this.bitField0_ |= gestureStateProto.bitField0_;
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
                                int readEnum = codedInputStream.readEnum();
                                if (GestureEndTarget.forNumber(readEnum) == null) {
                                    super.mergeVarintField(1, readEnum);
                                } else {
                                    this.bitField0_ = 1 | this.bitField0_;
                                    this.endTarget_ = readEnum;
                                }
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
                    synchronized (GestureStateProto.class) {
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
        GestureStateProto gestureStateProto = new GestureStateProto();
        DEFAULT_INSTANCE = gestureStateProto;
        gestureStateProto.makeImmutable();
    }

    public static GestureStateProto getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<GestureStateProto> parser() {
        return DEFAULT_INSTANCE.getParserForType();
    }
}
