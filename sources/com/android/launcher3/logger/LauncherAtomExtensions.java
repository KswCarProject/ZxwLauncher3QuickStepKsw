package com.android.launcher3.logger;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import com.google.protobuf.Parser;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public final class LauncherAtomExtensions {

    public interface DeviceSearchResultContainerOrBuilder extends MessageLiteOrBuilder {
        int getGridX();

        int getQueryLength();

        DeviceSearchResultContainer.SearchAttributes getSearchAttributes();

        boolean hasGridX();

        boolean hasQueryLength();

        boolean hasSearchAttributes();
    }

    public interface ExtendedContainersOrBuilder extends MessageLiteOrBuilder {
        ExtendedContainers.ContainerCase getContainerCase();

        DeviceSearchResultContainer getDeviceSearchResultContainer();

        boolean hasDeviceSearchResultContainer();
    }

    public static void registerAllExtensions(ExtensionRegistryLite extensionRegistryLite) {
    }

    private LauncherAtomExtensions() {
    }

    public static final class ExtendedContainers extends GeneratedMessageLite<ExtendedContainers, Builder> implements ExtendedContainersOrBuilder {
        /* access modifiers changed from: private */
        public static final ExtendedContainers DEFAULT_INSTANCE;
        public static final int DEVICE_SEARCH_RESULT_CONTAINER_FIELD_NUMBER = 1;
        private static volatile Parser<ExtendedContainers> PARSER;
        private int bitField0_;
        private int containerCase_ = 0;
        private Object container_;

        private ExtendedContainers() {
        }

        public enum ContainerCase implements Internal.EnumLite {
            DEVICE_SEARCH_RESULT_CONTAINER(1),
            CONTAINER_NOT_SET(0);
            
            private final int value;

            private ContainerCase(int i) {
                this.value = i;
            }

            @Deprecated
            public static ContainerCase valueOf(int i) {
                return forNumber(i);
            }

            public static ContainerCase forNumber(int i) {
                if (i == 0) {
                    return CONTAINER_NOT_SET;
                }
                if (i != 1) {
                    return null;
                }
                return DEVICE_SEARCH_RESULT_CONTAINER;
            }

            public int getNumber() {
                return this.value;
            }
        }

        public ContainerCase getContainerCase() {
            return ContainerCase.forNumber(this.containerCase_);
        }

        /* access modifiers changed from: private */
        public void clearContainer() {
            this.containerCase_ = 0;
            this.container_ = null;
        }

        public boolean hasDeviceSearchResultContainer() {
            return this.containerCase_ == 1;
        }

        public DeviceSearchResultContainer getDeviceSearchResultContainer() {
            if (this.containerCase_ == 1) {
                return (DeviceSearchResultContainer) this.container_;
            }
            return DeviceSearchResultContainer.getDefaultInstance();
        }

        /* access modifiers changed from: private */
        public void setDeviceSearchResultContainer(DeviceSearchResultContainer deviceSearchResultContainer) {
            Objects.requireNonNull(deviceSearchResultContainer);
            this.container_ = deviceSearchResultContainer;
            this.containerCase_ = 1;
        }

        /* access modifiers changed from: private */
        public void setDeviceSearchResultContainer(DeviceSearchResultContainer.Builder builder) {
            this.container_ = builder.build();
            this.containerCase_ = 1;
        }

        /* access modifiers changed from: private */
        public void mergeDeviceSearchResultContainer(DeviceSearchResultContainer deviceSearchResultContainer) {
            if (this.containerCase_ != 1 || this.container_ == DeviceSearchResultContainer.getDefaultInstance()) {
                this.container_ = deviceSearchResultContainer;
            } else {
                this.container_ = ((DeviceSearchResultContainer.Builder) DeviceSearchResultContainer.newBuilder((DeviceSearchResultContainer) this.container_).mergeFrom(deviceSearchResultContainer)).buildPartial();
            }
            this.containerCase_ = 1;
        }

        /* access modifiers changed from: private */
        public void clearDeviceSearchResultContainer() {
            if (this.containerCase_ == 1) {
                this.containerCase_ = 0;
                this.container_ = null;
            }
        }

        public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
            if (this.containerCase_ == 1) {
                codedOutputStream.writeMessage(1, (DeviceSearchResultContainer) this.container_);
            }
            this.unknownFields.writeTo(codedOutputStream);
        }

        public int getSerializedSize() {
            int i = this.memoizedSerializedSize;
            if (i != -1) {
                return i;
            }
            int i2 = 0;
            if (this.containerCase_ == 1) {
                i2 = 0 + CodedOutputStream.computeMessageSize(1, (DeviceSearchResultContainer) this.container_);
            }
            int serializedSize = i2 + this.unknownFields.getSerializedSize();
            this.memoizedSerializedSize = serializedSize;
            return serializedSize;
        }

        public static ExtendedContainers parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (ExtendedContainers) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static ExtendedContainers parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (ExtendedContainers) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static ExtendedContainers parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (ExtendedContainers) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static ExtendedContainers parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (ExtendedContainers) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static ExtendedContainers parseFrom(InputStream inputStream) throws IOException {
            return (ExtendedContainers) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static ExtendedContainers parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (ExtendedContainers) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static ExtendedContainers parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (ExtendedContainers) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static ExtendedContainers parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (ExtendedContainers) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static ExtendedContainers parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (ExtendedContainers) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static ExtendedContainers parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (ExtendedContainers) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(ExtendedContainers extendedContainers) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(extendedContainers);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<ExtendedContainers, Builder> implements ExtendedContainersOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 r1) {
                this();
            }

            private Builder() {
                super(ExtendedContainers.DEFAULT_INSTANCE);
            }

            public ContainerCase getContainerCase() {
                return ((ExtendedContainers) this.instance).getContainerCase();
            }

            public Builder clearContainer() {
                copyOnWrite();
                ((ExtendedContainers) this.instance).clearContainer();
                return this;
            }

            public boolean hasDeviceSearchResultContainer() {
                return ((ExtendedContainers) this.instance).hasDeviceSearchResultContainer();
            }

            public DeviceSearchResultContainer getDeviceSearchResultContainer() {
                return ((ExtendedContainers) this.instance).getDeviceSearchResultContainer();
            }

            public Builder setDeviceSearchResultContainer(DeviceSearchResultContainer deviceSearchResultContainer) {
                copyOnWrite();
                ((ExtendedContainers) this.instance).setDeviceSearchResultContainer(deviceSearchResultContainer);
                return this;
            }

            public Builder setDeviceSearchResultContainer(DeviceSearchResultContainer.Builder builder) {
                copyOnWrite();
                ((ExtendedContainers) this.instance).setDeviceSearchResultContainer(builder);
                return this;
            }

            public Builder mergeDeviceSearchResultContainer(DeviceSearchResultContainer deviceSearchResultContainer) {
                copyOnWrite();
                ((ExtendedContainers) this.instance).mergeDeviceSearchResultContainer(deviceSearchResultContainer);
                return this;
            }

            public Builder clearDeviceSearchResultContainer() {
                copyOnWrite();
                ((ExtendedContainers) this.instance).clearDeviceSearchResultContainer();
                return this;
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            boolean z = false;
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new ExtendedContainers();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                    ExtendedContainers extendedContainers = (ExtendedContainers) obj2;
                    int i = AnonymousClass1.$SwitchMap$com$android$launcher3$logger$LauncherAtomExtensions$ExtendedContainers$ContainerCase[extendedContainers.getContainerCase().ordinal()];
                    if (i == 1) {
                        if (this.containerCase_ == 1) {
                            z = true;
                        }
                        this.container_ = visitor.visitOneofMessage(z, this.container_, extendedContainers.container_);
                    } else if (i == 2) {
                        if (this.containerCase_ != 0) {
                            z = true;
                        }
                        visitor.visitOneofNotSet(z);
                    }
                    if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                        int i2 = extendedContainers.containerCase_;
                        if (i2 != 0) {
                            this.containerCase_ = i2;
                        }
                        this.bitField0_ |= extendedContainers.bitField0_;
                    }
                    return this;
                case 6:
                    CodedInputStream codedInputStream = (CodedInputStream) obj;
                    ExtensionRegistryLite extensionRegistryLite = (ExtensionRegistryLite) obj2;
                    while (!z) {
                        try {
                            int readTag = codedInputStream.readTag();
                            if (readTag != 0) {
                                if (readTag == 10) {
                                    DeviceSearchResultContainer.Builder builder = this.containerCase_ == 1 ? (DeviceSearchResultContainer.Builder) ((DeviceSearchResultContainer) this.container_).toBuilder() : null;
                                    MessageLite readMessage = codedInputStream.readMessage(DeviceSearchResultContainer.parser(), extensionRegistryLite);
                                    this.container_ = readMessage;
                                    if (builder != null) {
                                        builder.mergeFrom((DeviceSearchResultContainer) readMessage);
                                        this.container_ = builder.buildPartial();
                                    }
                                    this.containerCase_ = 1;
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
                        synchronized (ExtendedContainers.class) {
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
            ExtendedContainers extendedContainers = new ExtendedContainers();
            DEFAULT_INSTANCE = extendedContainers;
            extendedContainers.makeImmutable();
        }

        public static ExtendedContainers getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<ExtendedContainers> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    /* renamed from: com.android.launcher3.logger.LauncherAtomExtensions$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$launcher3$logger$LauncherAtomExtensions$ExtendedContainers$ContainerCase;
        static final /* synthetic */ int[] $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke;

        /* JADX WARNING: Can't wrap try/catch for region: R(21:0|(2:1|2)|3|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|21|22|(3:23|24|26)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(22:0|1|2|3|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|21|22|(3:23|24|26)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x0033 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x0049 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:17:0x0054 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:23:0x0071 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0028 */
        static {
            /*
                com.google.protobuf.GeneratedMessageLite$MethodToInvoke[] r0 = com.google.protobuf.GeneratedMessageLite.MethodToInvoke.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke = r0
                r1 = 1
                com.google.protobuf.GeneratedMessageLite$MethodToInvoke r2 = com.google.protobuf.GeneratedMessageLite.MethodToInvoke.NEW_MUTABLE_INSTANCE     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r2 = r2.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r0[r2] = r1     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                r0 = 2
                int[] r2 = $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke     // Catch:{ NoSuchFieldError -> 0x001d }
                com.google.protobuf.GeneratedMessageLite$MethodToInvoke r3 = com.google.protobuf.GeneratedMessageLite.MethodToInvoke.IS_INITIALIZED     // Catch:{ NoSuchFieldError -> 0x001d }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2[r3] = r0     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r2 = $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.google.protobuf.GeneratedMessageLite$MethodToInvoke r3 = com.google.protobuf.GeneratedMessageLite.MethodToInvoke.MAKE_IMMUTABLE     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r4 = 3
                r2[r3] = r4     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r2 = $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.google.protobuf.GeneratedMessageLite$MethodToInvoke r3 = com.google.protobuf.GeneratedMessageLite.MethodToInvoke.NEW_BUILDER     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r4 = 4
                r2[r3] = r4     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                int[] r2 = $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke     // Catch:{ NoSuchFieldError -> 0x003e }
                com.google.protobuf.GeneratedMessageLite$MethodToInvoke r3 = com.google.protobuf.GeneratedMessageLite.MethodToInvoke.VISIT     // Catch:{ NoSuchFieldError -> 0x003e }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r4 = 5
                r2[r3] = r4     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                int[] r2 = $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke     // Catch:{ NoSuchFieldError -> 0x0049 }
                com.google.protobuf.GeneratedMessageLite$MethodToInvoke r3 = com.google.protobuf.GeneratedMessageLite.MethodToInvoke.MERGE_FROM_STREAM     // Catch:{ NoSuchFieldError -> 0x0049 }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x0049 }
                r4 = 6
                r2[r3] = r4     // Catch:{ NoSuchFieldError -> 0x0049 }
            L_0x0049:
                int[] r2 = $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke     // Catch:{ NoSuchFieldError -> 0x0054 }
                com.google.protobuf.GeneratedMessageLite$MethodToInvoke r3 = com.google.protobuf.GeneratedMessageLite.MethodToInvoke.GET_DEFAULT_INSTANCE     // Catch:{ NoSuchFieldError -> 0x0054 }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x0054 }
                r4 = 7
                r2[r3] = r4     // Catch:{ NoSuchFieldError -> 0x0054 }
            L_0x0054:
                int[] r2 = $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke     // Catch:{ NoSuchFieldError -> 0x0060 }
                com.google.protobuf.GeneratedMessageLite$MethodToInvoke r3 = com.google.protobuf.GeneratedMessageLite.MethodToInvoke.GET_PARSER     // Catch:{ NoSuchFieldError -> 0x0060 }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x0060 }
                r4 = 8
                r2[r3] = r4     // Catch:{ NoSuchFieldError -> 0x0060 }
            L_0x0060:
                com.android.launcher3.logger.LauncherAtomExtensions$ExtendedContainers$ContainerCase[] r2 = com.android.launcher3.logger.LauncherAtomExtensions.ExtendedContainers.ContainerCase.values()
                int r2 = r2.length
                int[] r2 = new int[r2]
                $SwitchMap$com$android$launcher3$logger$LauncherAtomExtensions$ExtendedContainers$ContainerCase = r2
                com.android.launcher3.logger.LauncherAtomExtensions$ExtendedContainers$ContainerCase r3 = com.android.launcher3.logger.LauncherAtomExtensions.ExtendedContainers.ContainerCase.DEVICE_SEARCH_RESULT_CONTAINER     // Catch:{ NoSuchFieldError -> 0x0071 }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x0071 }
                r2[r3] = r1     // Catch:{ NoSuchFieldError -> 0x0071 }
            L_0x0071:
                int[] r1 = $SwitchMap$com$android$launcher3$logger$LauncherAtomExtensions$ExtendedContainers$ContainerCase     // Catch:{ NoSuchFieldError -> 0x007b }
                com.android.launcher3.logger.LauncherAtomExtensions$ExtendedContainers$ContainerCase r2 = com.android.launcher3.logger.LauncherAtomExtensions.ExtendedContainers.ContainerCase.CONTAINER_NOT_SET     // Catch:{ NoSuchFieldError -> 0x007b }
                int r2 = r2.ordinal()     // Catch:{ NoSuchFieldError -> 0x007b }
                r1[r2] = r0     // Catch:{ NoSuchFieldError -> 0x007b }
            L_0x007b:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.logger.LauncherAtomExtensions.AnonymousClass1.<clinit>():void");
        }
    }

    public static final class DeviceSearchResultContainer extends GeneratedMessageLite<DeviceSearchResultContainer, Builder> implements DeviceSearchResultContainerOrBuilder {
        /* access modifiers changed from: private */
        public static final DeviceSearchResultContainer DEFAULT_INSTANCE;
        public static final int GRID_X_FIELD_NUMBER = 3;
        private static volatile Parser<DeviceSearchResultContainer> PARSER = null;
        public static final int QUERY_LENGTH_FIELD_NUMBER = 1;
        public static final int SEARCH_ATTRIBUTES_FIELD_NUMBER = 2;
        private int bitField0_;
        private int gridX_;
        private int queryLength_;
        private SearchAttributes searchAttributes_;

        public interface SearchAttributesOrBuilder extends MessageLiteOrBuilder {
            boolean getCorrectedQuery();

            boolean getDirectMatch();

            SearchAttributes.EntryState getEntryState();

            boolean hasCorrectedQuery();

            boolean hasDirectMatch();

            boolean hasEntryState();
        }

        private DeviceSearchResultContainer() {
        }

        public static final class SearchAttributes extends GeneratedMessageLite<SearchAttributes, Builder> implements SearchAttributesOrBuilder {
            public static final int CORRECTED_QUERY_FIELD_NUMBER = 1;
            /* access modifiers changed from: private */
            public static final SearchAttributes DEFAULT_INSTANCE;
            public static final int DIRECT_MATCH_FIELD_NUMBER = 2;
            public static final int ENTRY_STATE_FIELD_NUMBER = 3;
            private static volatile Parser<SearchAttributes> PARSER;
            private int bitField0_;
            private boolean correctedQuery_;
            private boolean directMatch_;
            private int entryState_;

            private SearchAttributes() {
            }

            public enum EntryState implements Internal.EnumLite {
                ENTRY_STATE_UNKNOWN(0),
                ALL_APPS(1),
                QSB(2);
                
                public static final int ALL_APPS_VALUE = 1;
                public static final int ENTRY_STATE_UNKNOWN_VALUE = 0;
                public static final int QSB_VALUE = 2;
                private static final Internal.EnumLiteMap<EntryState> internalValueMap = null;
                private final int value;

                static {
                    internalValueMap = new Internal.EnumLiteMap<EntryState>() {
                        public EntryState findValueByNumber(int i) {
                            return EntryState.forNumber(i);
                        }
                    };
                }

                public final int getNumber() {
                    return this.value;
                }

                @Deprecated
                public static EntryState valueOf(int i) {
                    return forNumber(i);
                }

                public static EntryState forNumber(int i) {
                    if (i == 0) {
                        return ENTRY_STATE_UNKNOWN;
                    }
                    if (i == 1) {
                        return ALL_APPS;
                    }
                    if (i != 2) {
                        return null;
                    }
                    return QSB;
                }

                public static Internal.EnumLiteMap<EntryState> internalGetValueMap() {
                    return internalValueMap;
                }

                private EntryState(int i) {
                    this.value = i;
                }
            }

            public boolean hasCorrectedQuery() {
                return (this.bitField0_ & 1) == 1;
            }

            public boolean getCorrectedQuery() {
                return this.correctedQuery_;
            }

            /* access modifiers changed from: private */
            public void setCorrectedQuery(boolean z) {
                this.bitField0_ |= 1;
                this.correctedQuery_ = z;
            }

            /* access modifiers changed from: private */
            public void clearCorrectedQuery() {
                this.bitField0_ &= -2;
                this.correctedQuery_ = false;
            }

            public boolean hasDirectMatch() {
                return (this.bitField0_ & 2) == 2;
            }

            public boolean getDirectMatch() {
                return this.directMatch_;
            }

            /* access modifiers changed from: private */
            public void setDirectMatch(boolean z) {
                this.bitField0_ |= 2;
                this.directMatch_ = z;
            }

            /* access modifiers changed from: private */
            public void clearDirectMatch() {
                this.bitField0_ &= -3;
                this.directMatch_ = false;
            }

            public boolean hasEntryState() {
                return (this.bitField0_ & 4) == 4;
            }

            public EntryState getEntryState() {
                EntryState forNumber = EntryState.forNumber(this.entryState_);
                return forNumber == null ? EntryState.ENTRY_STATE_UNKNOWN : forNumber;
            }

            /* access modifiers changed from: private */
            public void setEntryState(EntryState entryState) {
                Objects.requireNonNull(entryState);
                this.bitField0_ |= 4;
                this.entryState_ = entryState.getNumber();
            }

            /* access modifiers changed from: private */
            public void clearEntryState() {
                this.bitField0_ &= -5;
                this.entryState_ = 0;
            }

            public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
                if ((this.bitField0_ & 1) == 1) {
                    codedOutputStream.writeBool(1, this.correctedQuery_);
                }
                if ((this.bitField0_ & 2) == 2) {
                    codedOutputStream.writeBool(2, this.directMatch_);
                }
                if ((this.bitField0_ & 4) == 4) {
                    codedOutputStream.writeEnum(3, this.entryState_);
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
                    i2 = 0 + CodedOutputStream.computeBoolSize(1, this.correctedQuery_);
                }
                if ((this.bitField0_ & 2) == 2) {
                    i2 += CodedOutputStream.computeBoolSize(2, this.directMatch_);
                }
                if ((this.bitField0_ & 4) == 4) {
                    i2 += CodedOutputStream.computeEnumSize(3, this.entryState_);
                }
                int serializedSize = i2 + this.unknownFields.getSerializedSize();
                this.memoizedSerializedSize = serializedSize;
                return serializedSize;
            }

            public static SearchAttributes parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
                return (SearchAttributes) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
            }

            public static SearchAttributes parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
                return (SearchAttributes) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
            }

            public static SearchAttributes parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
                return (SearchAttributes) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
            }

            public static SearchAttributes parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
                return (SearchAttributes) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
            }

            public static SearchAttributes parseFrom(InputStream inputStream) throws IOException {
                return (SearchAttributes) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
            }

            public static SearchAttributes parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
                return (SearchAttributes) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
            }

            public static SearchAttributes parseDelimitedFrom(InputStream inputStream) throws IOException {
                return (SearchAttributes) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
            }

            public static SearchAttributes parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
                return (SearchAttributes) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
            }

            public static SearchAttributes parseFrom(CodedInputStream codedInputStream) throws IOException {
                return (SearchAttributes) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
            }

            public static SearchAttributes parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
                return (SearchAttributes) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
            }

            public static Builder newBuilder() {
                return (Builder) DEFAULT_INSTANCE.toBuilder();
            }

            public static Builder newBuilder(SearchAttributes searchAttributes) {
                return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(searchAttributes);
            }

            public static final class Builder extends GeneratedMessageLite.Builder<SearchAttributes, Builder> implements SearchAttributesOrBuilder {
                /* synthetic */ Builder(AnonymousClass1 r1) {
                    this();
                }

                private Builder() {
                    super(SearchAttributes.DEFAULT_INSTANCE);
                }

                public boolean hasCorrectedQuery() {
                    return ((SearchAttributes) this.instance).hasCorrectedQuery();
                }

                public boolean getCorrectedQuery() {
                    return ((SearchAttributes) this.instance).getCorrectedQuery();
                }

                public Builder setCorrectedQuery(boolean z) {
                    copyOnWrite();
                    ((SearchAttributes) this.instance).setCorrectedQuery(z);
                    return this;
                }

                public Builder clearCorrectedQuery() {
                    copyOnWrite();
                    ((SearchAttributes) this.instance).clearCorrectedQuery();
                    return this;
                }

                public boolean hasDirectMatch() {
                    return ((SearchAttributes) this.instance).hasDirectMatch();
                }

                public boolean getDirectMatch() {
                    return ((SearchAttributes) this.instance).getDirectMatch();
                }

                public Builder setDirectMatch(boolean z) {
                    copyOnWrite();
                    ((SearchAttributes) this.instance).setDirectMatch(z);
                    return this;
                }

                public Builder clearDirectMatch() {
                    copyOnWrite();
                    ((SearchAttributes) this.instance).clearDirectMatch();
                    return this;
                }

                public boolean hasEntryState() {
                    return ((SearchAttributes) this.instance).hasEntryState();
                }

                public EntryState getEntryState() {
                    return ((SearchAttributes) this.instance).getEntryState();
                }

                public Builder setEntryState(EntryState entryState) {
                    copyOnWrite();
                    ((SearchAttributes) this.instance).setEntryState(entryState);
                    return this;
                }

                public Builder clearEntryState() {
                    copyOnWrite();
                    ((SearchAttributes) this.instance).clearEntryState();
                    return this;
                }
            }

            /* access modifiers changed from: protected */
            public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
                switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                    case 1:
                        return new SearchAttributes();
                    case 2:
                        return DEFAULT_INSTANCE;
                    case 3:
                        return null;
                    case 4:
                        return new Builder((AnonymousClass1) null);
                    case 5:
                        GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                        SearchAttributes searchAttributes = (SearchAttributes) obj2;
                        this.correctedQuery_ = visitor.visitBoolean(hasCorrectedQuery(), this.correctedQuery_, searchAttributes.hasCorrectedQuery(), searchAttributes.correctedQuery_);
                        this.directMatch_ = visitor.visitBoolean(hasDirectMatch(), this.directMatch_, searchAttributes.hasDirectMatch(), searchAttributes.directMatch_);
                        this.entryState_ = visitor.visitInt(hasEntryState(), this.entryState_, searchAttributes.hasEntryState(), searchAttributes.entryState_);
                        if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                            this.bitField0_ |= searchAttributes.bitField0_;
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
                                        this.correctedQuery_ = codedInputStream.readBool();
                                    } else if (readTag == 16) {
                                        this.bitField0_ |= 2;
                                        this.directMatch_ = codedInputStream.readBool();
                                    } else if (readTag == 24) {
                                        int readEnum = codedInputStream.readEnum();
                                        if (EntryState.forNumber(readEnum) == null) {
                                            super.mergeVarintField(3, readEnum);
                                        } else {
                                            this.bitField0_ |= 4;
                                            this.entryState_ = readEnum;
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
                            synchronized (SearchAttributes.class) {
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
                SearchAttributes searchAttributes = new SearchAttributes();
                DEFAULT_INSTANCE = searchAttributes;
                searchAttributes.makeImmutable();
            }

            public static SearchAttributes getDefaultInstance() {
                return DEFAULT_INSTANCE;
            }

            public static Parser<SearchAttributes> parser() {
                return DEFAULT_INSTANCE.getParserForType();
            }
        }

        public boolean hasQueryLength() {
            return (this.bitField0_ & 1) == 1;
        }

        public int getQueryLength() {
            return this.queryLength_;
        }

        /* access modifiers changed from: private */
        public void setQueryLength(int i) {
            this.bitField0_ |= 1;
            this.queryLength_ = i;
        }

        /* access modifiers changed from: private */
        public void clearQueryLength() {
            this.bitField0_ &= -2;
            this.queryLength_ = 0;
        }

        public boolean hasSearchAttributes() {
            return (this.bitField0_ & 2) == 2;
        }

        public SearchAttributes getSearchAttributes() {
            SearchAttributes searchAttributes = this.searchAttributes_;
            return searchAttributes == null ? SearchAttributes.getDefaultInstance() : searchAttributes;
        }

        /* access modifiers changed from: private */
        public void setSearchAttributes(SearchAttributes searchAttributes) {
            Objects.requireNonNull(searchAttributes);
            this.searchAttributes_ = searchAttributes;
            this.bitField0_ |= 2;
        }

        /* access modifiers changed from: private */
        public void setSearchAttributes(SearchAttributes.Builder builder) {
            this.searchAttributes_ = (SearchAttributes) builder.build();
            this.bitField0_ |= 2;
        }

        /* access modifiers changed from: private */
        public void mergeSearchAttributes(SearchAttributes searchAttributes) {
            SearchAttributes searchAttributes2 = this.searchAttributes_;
            if (searchAttributes2 == null || searchAttributes2 == SearchAttributes.getDefaultInstance()) {
                this.searchAttributes_ = searchAttributes;
            } else {
                this.searchAttributes_ = (SearchAttributes) ((SearchAttributes.Builder) SearchAttributes.newBuilder(this.searchAttributes_).mergeFrom(searchAttributes)).buildPartial();
            }
            this.bitField0_ |= 2;
        }

        /* access modifiers changed from: private */
        public void clearSearchAttributes() {
            this.searchAttributes_ = null;
            this.bitField0_ &= -3;
        }

        public boolean hasGridX() {
            return (this.bitField0_ & 4) == 4;
        }

        public int getGridX() {
            return this.gridX_;
        }

        /* access modifiers changed from: private */
        public void setGridX(int i) {
            this.bitField0_ |= 4;
            this.gridX_ = i;
        }

        /* access modifiers changed from: private */
        public void clearGridX() {
            this.bitField0_ &= -5;
            this.gridX_ = 0;
        }

        public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
            if ((this.bitField0_ & 1) == 1) {
                codedOutputStream.writeInt32(1, this.queryLength_);
            }
            if ((this.bitField0_ & 2) == 2) {
                codedOutputStream.writeMessage(2, getSearchAttributes());
            }
            if ((this.bitField0_ & 4) == 4) {
                codedOutputStream.writeInt32(3, this.gridX_);
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
                i2 = 0 + CodedOutputStream.computeInt32Size(1, this.queryLength_);
            }
            if ((this.bitField0_ & 2) == 2) {
                i2 += CodedOutputStream.computeMessageSize(2, getSearchAttributes());
            }
            if ((this.bitField0_ & 4) == 4) {
                i2 += CodedOutputStream.computeInt32Size(3, this.gridX_);
            }
            int serializedSize = i2 + this.unknownFields.getSerializedSize();
            this.memoizedSerializedSize = serializedSize;
            return serializedSize;
        }

        public static DeviceSearchResultContainer parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (DeviceSearchResultContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static DeviceSearchResultContainer parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (DeviceSearchResultContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static DeviceSearchResultContainer parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (DeviceSearchResultContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static DeviceSearchResultContainer parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (DeviceSearchResultContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static DeviceSearchResultContainer parseFrom(InputStream inputStream) throws IOException {
            return (DeviceSearchResultContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static DeviceSearchResultContainer parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (DeviceSearchResultContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static DeviceSearchResultContainer parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (DeviceSearchResultContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static DeviceSearchResultContainer parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (DeviceSearchResultContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static DeviceSearchResultContainer parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (DeviceSearchResultContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static DeviceSearchResultContainer parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (DeviceSearchResultContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return (Builder) DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(DeviceSearchResultContainer deviceSearchResultContainer) {
            return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(deviceSearchResultContainer);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<DeviceSearchResultContainer, Builder> implements DeviceSearchResultContainerOrBuilder {
            /* synthetic */ Builder(AnonymousClass1 r1) {
                this();
            }

            private Builder() {
                super(DeviceSearchResultContainer.DEFAULT_INSTANCE);
            }

            public boolean hasQueryLength() {
                return ((DeviceSearchResultContainer) this.instance).hasQueryLength();
            }

            public int getQueryLength() {
                return ((DeviceSearchResultContainer) this.instance).getQueryLength();
            }

            public Builder setQueryLength(int i) {
                copyOnWrite();
                ((DeviceSearchResultContainer) this.instance).setQueryLength(i);
                return this;
            }

            public Builder clearQueryLength() {
                copyOnWrite();
                ((DeviceSearchResultContainer) this.instance).clearQueryLength();
                return this;
            }

            public boolean hasSearchAttributes() {
                return ((DeviceSearchResultContainer) this.instance).hasSearchAttributes();
            }

            public SearchAttributes getSearchAttributes() {
                return ((DeviceSearchResultContainer) this.instance).getSearchAttributes();
            }

            public Builder setSearchAttributes(SearchAttributes searchAttributes) {
                copyOnWrite();
                ((DeviceSearchResultContainer) this.instance).setSearchAttributes(searchAttributes);
                return this;
            }

            public Builder setSearchAttributes(SearchAttributes.Builder builder) {
                copyOnWrite();
                ((DeviceSearchResultContainer) this.instance).setSearchAttributes(builder);
                return this;
            }

            public Builder mergeSearchAttributes(SearchAttributes searchAttributes) {
                copyOnWrite();
                ((DeviceSearchResultContainer) this.instance).mergeSearchAttributes(searchAttributes);
                return this;
            }

            public Builder clearSearchAttributes() {
                copyOnWrite();
                ((DeviceSearchResultContainer) this.instance).clearSearchAttributes();
                return this;
            }

            public boolean hasGridX() {
                return ((DeviceSearchResultContainer) this.instance).hasGridX();
            }

            public int getGridX() {
                return ((DeviceSearchResultContainer) this.instance).getGridX();
            }

            public Builder setGridX(int i) {
                copyOnWrite();
                ((DeviceSearchResultContainer) this.instance).setGridX(i);
                return this;
            }

            public Builder clearGridX() {
                copyOnWrite();
                ((DeviceSearchResultContainer) this.instance).clearGridX();
                return this;
            }
        }

        /* access modifiers changed from: protected */
        public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new DeviceSearchResultContainer();
                case 2:
                    return DEFAULT_INSTANCE;
                case 3:
                    return null;
                case 4:
                    return new Builder((AnonymousClass1) null);
                case 5:
                    GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                    DeviceSearchResultContainer deviceSearchResultContainer = (DeviceSearchResultContainer) obj2;
                    this.queryLength_ = visitor.visitInt(hasQueryLength(), this.queryLength_, deviceSearchResultContainer.hasQueryLength(), deviceSearchResultContainer.queryLength_);
                    this.searchAttributes_ = (SearchAttributes) visitor.visitMessage(this.searchAttributes_, deviceSearchResultContainer.searchAttributes_);
                    this.gridX_ = visitor.visitInt(hasGridX(), this.gridX_, deviceSearchResultContainer.hasGridX(), deviceSearchResultContainer.gridX_);
                    if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                        this.bitField0_ |= deviceSearchResultContainer.bitField0_;
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
                                    this.queryLength_ = codedInputStream.readInt32();
                                } else if (readTag == 18) {
                                    SearchAttributes.Builder builder = (this.bitField0_ & 2) == 2 ? (SearchAttributes.Builder) this.searchAttributes_.toBuilder() : null;
                                    SearchAttributes searchAttributes = (SearchAttributes) codedInputStream.readMessage(SearchAttributes.parser(), extensionRegistryLite);
                                    this.searchAttributes_ = searchAttributes;
                                    if (builder != null) {
                                        builder.mergeFrom(searchAttributes);
                                        this.searchAttributes_ = (SearchAttributes) builder.buildPartial();
                                    }
                                    this.bitField0_ |= 2;
                                } else if (readTag == 24) {
                                    this.bitField0_ |= 4;
                                    this.gridX_ = codedInputStream.readInt32();
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
                        synchronized (DeviceSearchResultContainer.class) {
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
            DeviceSearchResultContainer deviceSearchResultContainer = new DeviceSearchResultContainer();
            DEFAULT_INSTANCE = deviceSearchResultContainer;
            deviceSearchResultContainer.makeImmutable();
        }

        public static DeviceSearchResultContainer getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<DeviceSearchResultContainer> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }
}
