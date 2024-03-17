package com.android.launcher3.tracing;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import java.io.IOException;
import java.io.InputStream;

public final class OverviewComponentObserverProto extends GeneratedMessageLite<OverviewComponentObserverProto, Builder> implements OverviewComponentObserverProtoOrBuilder {
    /* access modifiers changed from: private */
    public static final OverviewComponentObserverProto DEFAULT_INSTANCE;
    public static final int OVERVIEW_ACTIVITY_RESUMED_FIELD_NUMBER = 2;
    public static final int OVERVIEW_ACTIVITY_STARTED_FIELD_NUMBER = 1;
    private static volatile Parser<OverviewComponentObserverProto> PARSER;
    private int bitField0_;
    private boolean overviewActivityResumed_;
    private boolean overviewActivityStarted_;

    private OverviewComponentObserverProto() {
    }

    public boolean hasOverviewActivityStarted() {
        return (this.bitField0_ & 1) == 1;
    }

    public boolean getOverviewActivityStarted() {
        return this.overviewActivityStarted_;
    }

    /* access modifiers changed from: private */
    public void setOverviewActivityStarted(boolean z) {
        this.bitField0_ |= 1;
        this.overviewActivityStarted_ = z;
    }

    /* access modifiers changed from: private */
    public void clearOverviewActivityStarted() {
        this.bitField0_ &= -2;
        this.overviewActivityStarted_ = false;
    }

    public boolean hasOverviewActivityResumed() {
        return (this.bitField0_ & 2) == 2;
    }

    public boolean getOverviewActivityResumed() {
        return this.overviewActivityResumed_;
    }

    /* access modifiers changed from: private */
    public void setOverviewActivityResumed(boolean z) {
        this.bitField0_ |= 2;
        this.overviewActivityResumed_ = z;
    }

    /* access modifiers changed from: private */
    public void clearOverviewActivityResumed() {
        this.bitField0_ &= -3;
        this.overviewActivityResumed_ = false;
    }

    public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
        if ((this.bitField0_ & 1) == 1) {
            codedOutputStream.writeBool(1, this.overviewActivityStarted_);
        }
        if ((this.bitField0_ & 2) == 2) {
            codedOutputStream.writeBool(2, this.overviewActivityResumed_);
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
            i2 = 0 + CodedOutputStream.computeBoolSize(1, this.overviewActivityStarted_);
        }
        if ((this.bitField0_ & 2) == 2) {
            i2 += CodedOutputStream.computeBoolSize(2, this.overviewActivityResumed_);
        }
        int serializedSize = i2 + this.unknownFields.getSerializedSize();
        this.memoizedSerializedSize = serializedSize;
        return serializedSize;
    }

    public static OverviewComponentObserverProto parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
        return (OverviewComponentObserverProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
    }

    public static OverviewComponentObserverProto parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (OverviewComponentObserverProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
    }

    public static OverviewComponentObserverProto parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
        return (OverviewComponentObserverProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
    }

    public static OverviewComponentObserverProto parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (OverviewComponentObserverProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
    }

    public static OverviewComponentObserverProto parseFrom(InputStream inputStream) throws IOException {
        return (OverviewComponentObserverProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
    }

    public static OverviewComponentObserverProto parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (OverviewComponentObserverProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
    }

    public static OverviewComponentObserverProto parseDelimitedFrom(InputStream inputStream) throws IOException {
        return (OverviewComponentObserverProto) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
    }

    public static OverviewComponentObserverProto parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (OverviewComponentObserverProto) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
    }

    public static OverviewComponentObserverProto parseFrom(CodedInputStream codedInputStream) throws IOException {
        return (OverviewComponentObserverProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
    }

    public static OverviewComponentObserverProto parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (OverviewComponentObserverProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
    }

    public static Builder newBuilder() {
        return (Builder) DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(OverviewComponentObserverProto overviewComponentObserverProto) {
        return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(overviewComponentObserverProto);
    }

    public static final class Builder extends GeneratedMessageLite.Builder<OverviewComponentObserverProto, Builder> implements OverviewComponentObserverProtoOrBuilder {
        /* synthetic */ Builder(AnonymousClass1 r1) {
            this();
        }

        private Builder() {
            super(OverviewComponentObserverProto.DEFAULT_INSTANCE);
        }

        public boolean hasOverviewActivityStarted() {
            return ((OverviewComponentObserverProto) this.instance).hasOverviewActivityStarted();
        }

        public boolean getOverviewActivityStarted() {
            return ((OverviewComponentObserverProto) this.instance).getOverviewActivityStarted();
        }

        public Builder setOverviewActivityStarted(boolean z) {
            copyOnWrite();
            ((OverviewComponentObserverProto) this.instance).setOverviewActivityStarted(z);
            return this;
        }

        public Builder clearOverviewActivityStarted() {
            copyOnWrite();
            ((OverviewComponentObserverProto) this.instance).clearOverviewActivityStarted();
            return this;
        }

        public boolean hasOverviewActivityResumed() {
            return ((OverviewComponentObserverProto) this.instance).hasOverviewActivityResumed();
        }

        public boolean getOverviewActivityResumed() {
            return ((OverviewComponentObserverProto) this.instance).getOverviewActivityResumed();
        }

        public Builder setOverviewActivityResumed(boolean z) {
            copyOnWrite();
            ((OverviewComponentObserverProto) this.instance).setOverviewActivityResumed(z);
            return this;
        }

        public Builder clearOverviewActivityResumed() {
            copyOnWrite();
            ((OverviewComponentObserverProto) this.instance).clearOverviewActivityResumed();
            return this;
        }
    }

    /* renamed from: com.android.launcher3.tracing.OverviewComponentObserverProto$1  reason: invalid class name */
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.tracing.OverviewComponentObserverProto.AnonymousClass1.<clinit>():void");
        }
    }

    /* access modifiers changed from: protected */
    public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
        switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
            case 1:
                return new OverviewComponentObserverProto();
            case 2:
                return DEFAULT_INSTANCE;
            case 3:
                return null;
            case 4:
                return new Builder((AnonymousClass1) null);
            case 5:
                GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                OverviewComponentObserverProto overviewComponentObserverProto = (OverviewComponentObserverProto) obj2;
                this.overviewActivityStarted_ = visitor.visitBoolean(hasOverviewActivityStarted(), this.overviewActivityStarted_, overviewComponentObserverProto.hasOverviewActivityStarted(), overviewComponentObserverProto.overviewActivityStarted_);
                this.overviewActivityResumed_ = visitor.visitBoolean(hasOverviewActivityResumed(), this.overviewActivityResumed_, overviewComponentObserverProto.hasOverviewActivityResumed(), overviewComponentObserverProto.overviewActivityResumed_);
                if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                    this.bitField0_ |= overviewComponentObserverProto.bitField0_;
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
                                this.overviewActivityStarted_ = codedInputStream.readBool();
                            } else if (readTag == 16) {
                                this.bitField0_ |= 2;
                                this.overviewActivityResumed_ = codedInputStream.readBool();
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
                    synchronized (OverviewComponentObserverProto.class) {
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
        OverviewComponentObserverProto overviewComponentObserverProto = new OverviewComponentObserverProto();
        DEFAULT_INSTANCE = overviewComponentObserverProto;
        overviewComponentObserverProto.makeImmutable();
    }

    public static OverviewComponentObserverProto getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<OverviewComponentObserverProto> parser() {
        return DEFAULT_INSTANCE.getParserForType();
    }
}
