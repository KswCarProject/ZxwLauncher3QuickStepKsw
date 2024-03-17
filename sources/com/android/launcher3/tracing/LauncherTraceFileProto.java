package com.android.launcher3.tracing;

import com.android.launcher3.tracing.LauncherTraceEntryProto;
import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class LauncherTraceFileProto extends GeneratedMessageLite<LauncherTraceFileProto, Builder> implements LauncherTraceFileProtoOrBuilder {
    /* access modifiers changed from: private */
    public static final LauncherTraceFileProto DEFAULT_INSTANCE;
    public static final int ENTRY_FIELD_NUMBER = 2;
    public static final int MAGIC_NUMBER_FIELD_NUMBER = 1;
    private static volatile Parser<LauncherTraceFileProto> PARSER;
    private int bitField0_;
    private Internal.ProtobufList<LauncherTraceEntryProto> entry_ = emptyProtobufList();
    private long magicNumber_;

    private LauncherTraceFileProto() {
    }

    public enum MagicNumber implements Internal.EnumLite {
        INVALID(0),
        MAGIC_NUMBER_L(MAGIC_NUMBER_L_VALUE),
        MAGIC_NUMBER_H(MAGIC_NUMBER_H_VALUE);
        
        public static final int INVALID_VALUE = 0;
        public static final int MAGIC_NUMBER_H_VALUE = 1129469010;
        public static final int MAGIC_NUMBER_L_VALUE = 1212370508;
        private static final Internal.EnumLiteMap<MagicNumber> internalValueMap = null;
        private final int value;

        static {
            internalValueMap = new Internal.EnumLiteMap<MagicNumber>() {
                public MagicNumber findValueByNumber(int i) {
                    return MagicNumber.forNumber(i);
                }
            };
        }

        public final int getNumber() {
            return this.value;
        }

        @Deprecated
        public static MagicNumber valueOf(int i) {
            return forNumber(i);
        }

        public static MagicNumber forNumber(int i) {
            if (i == 0) {
                return INVALID;
            }
            if (i == 1129469010) {
                return MAGIC_NUMBER_H;
            }
            if (i != 1212370508) {
                return null;
            }
            return MAGIC_NUMBER_L;
        }

        public static Internal.EnumLiteMap<MagicNumber> internalGetValueMap() {
            return internalValueMap;
        }

        private MagicNumber(int i) {
            this.value = i;
        }
    }

    public boolean hasMagicNumber() {
        return (this.bitField0_ & 1) == 1;
    }

    public long getMagicNumber() {
        return this.magicNumber_;
    }

    /* access modifiers changed from: private */
    public void setMagicNumber(long j) {
        this.bitField0_ |= 1;
        this.magicNumber_ = j;
    }

    /* access modifiers changed from: private */
    public void clearMagicNumber() {
        this.bitField0_ &= -2;
        this.magicNumber_ = 0;
    }

    public List<LauncherTraceEntryProto> getEntryList() {
        return this.entry_;
    }

    public List<? extends LauncherTraceEntryProtoOrBuilder> getEntryOrBuilderList() {
        return this.entry_;
    }

    public int getEntryCount() {
        return this.entry_.size();
    }

    public LauncherTraceEntryProto getEntry(int i) {
        return (LauncherTraceEntryProto) this.entry_.get(i);
    }

    public LauncherTraceEntryProtoOrBuilder getEntryOrBuilder(int i) {
        return (LauncherTraceEntryProtoOrBuilder) this.entry_.get(i);
    }

    private void ensureEntryIsMutable() {
        if (!this.entry_.isModifiable()) {
            this.entry_ = GeneratedMessageLite.mutableCopy(this.entry_);
        }
    }

    /* access modifiers changed from: private */
    public void setEntry(int i, LauncherTraceEntryProto launcherTraceEntryProto) {
        Objects.requireNonNull(launcherTraceEntryProto);
        ensureEntryIsMutable();
        this.entry_.set(i, launcherTraceEntryProto);
    }

    /* access modifiers changed from: private */
    public void setEntry(int i, LauncherTraceEntryProto.Builder builder) {
        ensureEntryIsMutable();
        this.entry_.set(i, (LauncherTraceEntryProto) builder.build());
    }

    /* access modifiers changed from: private */
    public void addEntry(LauncherTraceEntryProto launcherTraceEntryProto) {
        Objects.requireNonNull(launcherTraceEntryProto);
        ensureEntryIsMutable();
        this.entry_.add(launcherTraceEntryProto);
    }

    /* access modifiers changed from: private */
    public void addEntry(int i, LauncherTraceEntryProto launcherTraceEntryProto) {
        Objects.requireNonNull(launcherTraceEntryProto);
        ensureEntryIsMutable();
        this.entry_.add(i, launcherTraceEntryProto);
    }

    /* access modifiers changed from: private */
    public void addEntry(LauncherTraceEntryProto.Builder builder) {
        ensureEntryIsMutable();
        this.entry_.add((LauncherTraceEntryProto) builder.build());
    }

    /* access modifiers changed from: private */
    public void addEntry(int i, LauncherTraceEntryProto.Builder builder) {
        ensureEntryIsMutable();
        this.entry_.add(i, (LauncherTraceEntryProto) builder.build());
    }

    /* access modifiers changed from: private */
    public void addAllEntry(Iterable<? extends LauncherTraceEntryProto> iterable) {
        ensureEntryIsMutable();
        AbstractMessageLite.addAll(iterable, this.entry_);
    }

    /* access modifiers changed from: private */
    public void clearEntry() {
        this.entry_ = emptyProtobufList();
    }

    /* access modifiers changed from: private */
    public void removeEntry(int i) {
        ensureEntryIsMutable();
        this.entry_.remove(i);
    }

    public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
        if ((this.bitField0_ & 1) == 1) {
            codedOutputStream.writeFixed64(1, this.magicNumber_);
        }
        for (int i = 0; i < this.entry_.size(); i++) {
            codedOutputStream.writeMessage(2, (MessageLite) this.entry_.get(i));
        }
        this.unknownFields.writeTo(codedOutputStream);
    }

    public int getSerializedSize() {
        int i = this.memoizedSerializedSize;
        if (i != -1) {
            return i;
        }
        int computeFixed64Size = (this.bitField0_ & 1) == 1 ? CodedOutputStream.computeFixed64Size(1, this.magicNumber_) + 0 : 0;
        for (int i2 = 0; i2 < this.entry_.size(); i2++) {
            computeFixed64Size += CodedOutputStream.computeMessageSize(2, (MessageLite) this.entry_.get(i2));
        }
        int serializedSize = computeFixed64Size + this.unknownFields.getSerializedSize();
        this.memoizedSerializedSize = serializedSize;
        return serializedSize;
    }

    public static LauncherTraceFileProto parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
        return (LauncherTraceFileProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
    }

    public static LauncherTraceFileProto parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (LauncherTraceFileProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
    }

    public static LauncherTraceFileProto parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
        return (LauncherTraceFileProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
    }

    public static LauncherTraceFileProto parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (LauncherTraceFileProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
    }

    public static LauncherTraceFileProto parseFrom(InputStream inputStream) throws IOException {
        return (LauncherTraceFileProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
    }

    public static LauncherTraceFileProto parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (LauncherTraceFileProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
    }

    public static LauncherTraceFileProto parseDelimitedFrom(InputStream inputStream) throws IOException {
        return (LauncherTraceFileProto) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
    }

    public static LauncherTraceFileProto parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (LauncherTraceFileProto) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
    }

    public static LauncherTraceFileProto parseFrom(CodedInputStream codedInputStream) throws IOException {
        return (LauncherTraceFileProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
    }

    public static LauncherTraceFileProto parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (LauncherTraceFileProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
    }

    public static Builder newBuilder() {
        return (Builder) DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(LauncherTraceFileProto launcherTraceFileProto) {
        return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(launcherTraceFileProto);
    }

    public static final class Builder extends GeneratedMessageLite.Builder<LauncherTraceFileProto, Builder> implements LauncherTraceFileProtoOrBuilder {
        /* synthetic */ Builder(AnonymousClass1 r1) {
            this();
        }

        private Builder() {
            super(LauncherTraceFileProto.DEFAULT_INSTANCE);
        }

        public boolean hasMagicNumber() {
            return ((LauncherTraceFileProto) this.instance).hasMagicNumber();
        }

        public long getMagicNumber() {
            return ((LauncherTraceFileProto) this.instance).getMagicNumber();
        }

        public Builder setMagicNumber(long j) {
            copyOnWrite();
            ((LauncherTraceFileProto) this.instance).setMagicNumber(j);
            return this;
        }

        public Builder clearMagicNumber() {
            copyOnWrite();
            ((LauncherTraceFileProto) this.instance).clearMagicNumber();
            return this;
        }

        public List<LauncherTraceEntryProto> getEntryList() {
            return Collections.unmodifiableList(((LauncherTraceFileProto) this.instance).getEntryList());
        }

        public int getEntryCount() {
            return ((LauncherTraceFileProto) this.instance).getEntryCount();
        }

        public LauncherTraceEntryProto getEntry(int i) {
            return ((LauncherTraceFileProto) this.instance).getEntry(i);
        }

        public Builder setEntry(int i, LauncherTraceEntryProto launcherTraceEntryProto) {
            copyOnWrite();
            ((LauncherTraceFileProto) this.instance).setEntry(i, launcherTraceEntryProto);
            return this;
        }

        public Builder setEntry(int i, LauncherTraceEntryProto.Builder builder) {
            copyOnWrite();
            ((LauncherTraceFileProto) this.instance).setEntry(i, builder);
            return this;
        }

        public Builder addEntry(LauncherTraceEntryProto launcherTraceEntryProto) {
            copyOnWrite();
            ((LauncherTraceFileProto) this.instance).addEntry(launcherTraceEntryProto);
            return this;
        }

        public Builder addEntry(int i, LauncherTraceEntryProto launcherTraceEntryProto) {
            copyOnWrite();
            ((LauncherTraceFileProto) this.instance).addEntry(i, launcherTraceEntryProto);
            return this;
        }

        public Builder addEntry(LauncherTraceEntryProto.Builder builder) {
            copyOnWrite();
            ((LauncherTraceFileProto) this.instance).addEntry(builder);
            return this;
        }

        public Builder addEntry(int i, LauncherTraceEntryProto.Builder builder) {
            copyOnWrite();
            ((LauncherTraceFileProto) this.instance).addEntry(i, builder);
            return this;
        }

        public Builder addAllEntry(Iterable<? extends LauncherTraceEntryProto> iterable) {
            copyOnWrite();
            ((LauncherTraceFileProto) this.instance).addAllEntry(iterable);
            return this;
        }

        public Builder clearEntry() {
            copyOnWrite();
            ((LauncherTraceFileProto) this.instance).clearEntry();
            return this;
        }

        public Builder removeEntry(int i) {
            copyOnWrite();
            ((LauncherTraceFileProto) this.instance).removeEntry(i);
            return this;
        }
    }

    /* renamed from: com.android.launcher3.tracing.LauncherTraceFileProto$1  reason: invalid class name */
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.tracing.LauncherTraceFileProto.AnonymousClass1.<clinit>():void");
        }
    }

    /* access modifiers changed from: protected */
    public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
        switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
            case 1:
                return new LauncherTraceFileProto();
            case 2:
                return DEFAULT_INSTANCE;
            case 3:
                this.entry_.makeImmutable();
                return null;
            case 4:
                return new Builder((AnonymousClass1) null);
            case 5:
                GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                LauncherTraceFileProto launcherTraceFileProto = (LauncherTraceFileProto) obj2;
                this.magicNumber_ = visitor.visitLong(hasMagicNumber(), this.magicNumber_, launcherTraceFileProto.hasMagicNumber(), launcherTraceFileProto.magicNumber_);
                this.entry_ = visitor.visitList(this.entry_, launcherTraceFileProto.entry_);
                if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                    this.bitField0_ |= launcherTraceFileProto.bitField0_;
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
                                this.magicNumber_ = codedInputStream.readFixed64();
                            } else if (readTag == 18) {
                                if (!this.entry_.isModifiable()) {
                                    this.entry_ = GeneratedMessageLite.mutableCopy(this.entry_);
                                }
                                this.entry_.add((LauncherTraceEntryProto) codedInputStream.readMessage(LauncherTraceEntryProto.parser(), extensionRegistryLite));
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
                    synchronized (LauncherTraceFileProto.class) {
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
        LauncherTraceFileProto launcherTraceFileProto = new LauncherTraceFileProto();
        DEFAULT_INSTANCE = launcherTraceFileProto;
        launcherTraceFileProto.makeImmutable();
    }

    public static LauncherTraceFileProto getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<LauncherTraceFileProto> parser() {
        return DEFAULT_INSTANCE.getParserForType();
    }
}
