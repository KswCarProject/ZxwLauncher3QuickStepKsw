package com.android.launcher3.tracing;

import com.android.launcher3.tracing.GestureStateProto;
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

public final class SwipeHandlerProto extends GeneratedMessageLite<SwipeHandlerProto, Builder> implements SwipeHandlerProtoOrBuilder {
    public static final int APP_TO_OVERVIEW_PROGRESS_FIELD_NUMBER = 4;
    /* access modifiers changed from: private */
    public static final SwipeHandlerProto DEFAULT_INSTANCE;
    public static final int GESTURE_STATE_FIELD_NUMBER = 1;
    public static final int IS_RECENTS_ATTACHED_TO_APP_WINDOW_FIELD_NUMBER = 2;
    private static volatile Parser<SwipeHandlerProto> PARSER = null;
    public static final int SCROLL_OFFSET_FIELD_NUMBER = 3;
    private float appToOverviewProgress_;
    private int bitField0_;
    private GestureStateProto gestureState_;
    private boolean isRecentsAttachedToAppWindow_;
    private int scrollOffset_;

    private SwipeHandlerProto() {
    }

    public boolean hasGestureState() {
        return (this.bitField0_ & 1) == 1;
    }

    public GestureStateProto getGestureState() {
        GestureStateProto gestureStateProto = this.gestureState_;
        return gestureStateProto == null ? GestureStateProto.getDefaultInstance() : gestureStateProto;
    }

    /* access modifiers changed from: private */
    public void setGestureState(GestureStateProto gestureStateProto) {
        Objects.requireNonNull(gestureStateProto);
        this.gestureState_ = gestureStateProto;
        this.bitField0_ |= 1;
    }

    /* access modifiers changed from: private */
    public void setGestureState(GestureStateProto.Builder builder) {
        this.gestureState_ = (GestureStateProto) builder.build();
        this.bitField0_ |= 1;
    }

    /* access modifiers changed from: private */
    public void mergeGestureState(GestureStateProto gestureStateProto) {
        GestureStateProto gestureStateProto2 = this.gestureState_;
        if (gestureStateProto2 == null || gestureStateProto2 == GestureStateProto.getDefaultInstance()) {
            this.gestureState_ = gestureStateProto;
        } else {
            this.gestureState_ = (GestureStateProto) ((GestureStateProto.Builder) GestureStateProto.newBuilder(this.gestureState_).mergeFrom(gestureStateProto)).buildPartial();
        }
        this.bitField0_ |= 1;
    }

    /* access modifiers changed from: private */
    public void clearGestureState() {
        this.gestureState_ = null;
        this.bitField0_ &= -2;
    }

    public boolean hasIsRecentsAttachedToAppWindow() {
        return (this.bitField0_ & 2) == 2;
    }

    public boolean getIsRecentsAttachedToAppWindow() {
        return this.isRecentsAttachedToAppWindow_;
    }

    /* access modifiers changed from: private */
    public void setIsRecentsAttachedToAppWindow(boolean z) {
        this.bitField0_ |= 2;
        this.isRecentsAttachedToAppWindow_ = z;
    }

    /* access modifiers changed from: private */
    public void clearIsRecentsAttachedToAppWindow() {
        this.bitField0_ &= -3;
        this.isRecentsAttachedToAppWindow_ = false;
    }

    public boolean hasScrollOffset() {
        return (this.bitField0_ & 4) == 4;
    }

    public int getScrollOffset() {
        return this.scrollOffset_;
    }

    /* access modifiers changed from: private */
    public void setScrollOffset(int i) {
        this.bitField0_ |= 4;
        this.scrollOffset_ = i;
    }

    /* access modifiers changed from: private */
    public void clearScrollOffset() {
        this.bitField0_ &= -5;
        this.scrollOffset_ = 0;
    }

    public boolean hasAppToOverviewProgress() {
        return (this.bitField0_ & 8) == 8;
    }

    public float getAppToOverviewProgress() {
        return this.appToOverviewProgress_;
    }

    /* access modifiers changed from: private */
    public void setAppToOverviewProgress(float f) {
        this.bitField0_ |= 8;
        this.appToOverviewProgress_ = f;
    }

    /* access modifiers changed from: private */
    public void clearAppToOverviewProgress() {
        this.bitField0_ &= -9;
        this.appToOverviewProgress_ = 0.0f;
    }

    public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
        if ((this.bitField0_ & 1) == 1) {
            codedOutputStream.writeMessage(1, getGestureState());
        }
        if ((this.bitField0_ & 2) == 2) {
            codedOutputStream.writeBool(2, this.isRecentsAttachedToAppWindow_);
        }
        if ((this.bitField0_ & 4) == 4) {
            codedOutputStream.writeInt32(3, this.scrollOffset_);
        }
        if ((this.bitField0_ & 8) == 8) {
            codedOutputStream.writeFloat(4, this.appToOverviewProgress_);
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
            i2 = 0 + CodedOutputStream.computeMessageSize(1, getGestureState());
        }
        if ((this.bitField0_ & 2) == 2) {
            i2 += CodedOutputStream.computeBoolSize(2, this.isRecentsAttachedToAppWindow_);
        }
        if ((this.bitField0_ & 4) == 4) {
            i2 += CodedOutputStream.computeInt32Size(3, this.scrollOffset_);
        }
        if ((this.bitField0_ & 8) == 8) {
            i2 += CodedOutputStream.computeFloatSize(4, this.appToOverviewProgress_);
        }
        int serializedSize = i2 + this.unknownFields.getSerializedSize();
        this.memoizedSerializedSize = serializedSize;
        return serializedSize;
    }

    public static SwipeHandlerProto parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
        return (SwipeHandlerProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
    }

    public static SwipeHandlerProto parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (SwipeHandlerProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
    }

    public static SwipeHandlerProto parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
        return (SwipeHandlerProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
    }

    public static SwipeHandlerProto parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (SwipeHandlerProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
    }

    public static SwipeHandlerProto parseFrom(InputStream inputStream) throws IOException {
        return (SwipeHandlerProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
    }

    public static SwipeHandlerProto parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (SwipeHandlerProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
    }

    public static SwipeHandlerProto parseDelimitedFrom(InputStream inputStream) throws IOException {
        return (SwipeHandlerProto) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
    }

    public static SwipeHandlerProto parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (SwipeHandlerProto) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
    }

    public static SwipeHandlerProto parseFrom(CodedInputStream codedInputStream) throws IOException {
        return (SwipeHandlerProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
    }

    public static SwipeHandlerProto parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (SwipeHandlerProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
    }

    public static Builder newBuilder() {
        return (Builder) DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(SwipeHandlerProto swipeHandlerProto) {
        return (Builder) ((Builder) DEFAULT_INSTANCE.toBuilder()).mergeFrom(swipeHandlerProto);
    }

    public static final class Builder extends GeneratedMessageLite.Builder<SwipeHandlerProto, Builder> implements SwipeHandlerProtoOrBuilder {
        /* synthetic */ Builder(AnonymousClass1 r1) {
            this();
        }

        private Builder() {
            super(SwipeHandlerProto.DEFAULT_INSTANCE);
        }

        public boolean hasGestureState() {
            return ((SwipeHandlerProto) this.instance).hasGestureState();
        }

        public GestureStateProto getGestureState() {
            return ((SwipeHandlerProto) this.instance).getGestureState();
        }

        public Builder setGestureState(GestureStateProto gestureStateProto) {
            copyOnWrite();
            ((SwipeHandlerProto) this.instance).setGestureState(gestureStateProto);
            return this;
        }

        public Builder setGestureState(GestureStateProto.Builder builder) {
            copyOnWrite();
            ((SwipeHandlerProto) this.instance).setGestureState(builder);
            return this;
        }

        public Builder mergeGestureState(GestureStateProto gestureStateProto) {
            copyOnWrite();
            ((SwipeHandlerProto) this.instance).mergeGestureState(gestureStateProto);
            return this;
        }

        public Builder clearGestureState() {
            copyOnWrite();
            ((SwipeHandlerProto) this.instance).clearGestureState();
            return this;
        }

        public boolean hasIsRecentsAttachedToAppWindow() {
            return ((SwipeHandlerProto) this.instance).hasIsRecentsAttachedToAppWindow();
        }

        public boolean getIsRecentsAttachedToAppWindow() {
            return ((SwipeHandlerProto) this.instance).getIsRecentsAttachedToAppWindow();
        }

        public Builder setIsRecentsAttachedToAppWindow(boolean z) {
            copyOnWrite();
            ((SwipeHandlerProto) this.instance).setIsRecentsAttachedToAppWindow(z);
            return this;
        }

        public Builder clearIsRecentsAttachedToAppWindow() {
            copyOnWrite();
            ((SwipeHandlerProto) this.instance).clearIsRecentsAttachedToAppWindow();
            return this;
        }

        public boolean hasScrollOffset() {
            return ((SwipeHandlerProto) this.instance).hasScrollOffset();
        }

        public int getScrollOffset() {
            return ((SwipeHandlerProto) this.instance).getScrollOffset();
        }

        public Builder setScrollOffset(int i) {
            copyOnWrite();
            ((SwipeHandlerProto) this.instance).setScrollOffset(i);
            return this;
        }

        public Builder clearScrollOffset() {
            copyOnWrite();
            ((SwipeHandlerProto) this.instance).clearScrollOffset();
            return this;
        }

        public boolean hasAppToOverviewProgress() {
            return ((SwipeHandlerProto) this.instance).hasAppToOverviewProgress();
        }

        public float getAppToOverviewProgress() {
            return ((SwipeHandlerProto) this.instance).getAppToOverviewProgress();
        }

        public Builder setAppToOverviewProgress(float f) {
            copyOnWrite();
            ((SwipeHandlerProto) this.instance).setAppToOverviewProgress(f);
            return this;
        }

        public Builder clearAppToOverviewProgress() {
            copyOnWrite();
            ((SwipeHandlerProto) this.instance).clearAppToOverviewProgress();
            return this;
        }
    }

    /* renamed from: com.android.launcher3.tracing.SwipeHandlerProto$1  reason: invalid class name */
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.tracing.SwipeHandlerProto.AnonymousClass1.<clinit>():void");
        }
    }

    /* access modifiers changed from: protected */
    public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
        switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
            case 1:
                return new SwipeHandlerProto();
            case 2:
                return DEFAULT_INSTANCE;
            case 3:
                return null;
            case 4:
                return new Builder((AnonymousClass1) null);
            case 5:
                GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                SwipeHandlerProto swipeHandlerProto = (SwipeHandlerProto) obj2;
                this.gestureState_ = (GestureStateProto) visitor.visitMessage(this.gestureState_, swipeHandlerProto.gestureState_);
                this.isRecentsAttachedToAppWindow_ = visitor.visitBoolean(hasIsRecentsAttachedToAppWindow(), this.isRecentsAttachedToAppWindow_, swipeHandlerProto.hasIsRecentsAttachedToAppWindow(), swipeHandlerProto.isRecentsAttachedToAppWindow_);
                this.scrollOffset_ = visitor.visitInt(hasScrollOffset(), this.scrollOffset_, swipeHandlerProto.hasScrollOffset(), swipeHandlerProto.scrollOffset_);
                this.appToOverviewProgress_ = visitor.visitFloat(hasAppToOverviewProgress(), this.appToOverviewProgress_, swipeHandlerProto.hasAppToOverviewProgress(), swipeHandlerProto.appToOverviewProgress_);
                if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                    this.bitField0_ |= swipeHandlerProto.bitField0_;
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
                                GestureStateProto.Builder builder = (this.bitField0_ & 1) == 1 ? (GestureStateProto.Builder) this.gestureState_.toBuilder() : null;
                                GestureStateProto gestureStateProto = (GestureStateProto) codedInputStream.readMessage(GestureStateProto.parser(), extensionRegistryLite);
                                this.gestureState_ = gestureStateProto;
                                if (builder != null) {
                                    builder.mergeFrom(gestureStateProto);
                                    this.gestureState_ = (GestureStateProto) builder.buildPartial();
                                }
                                this.bitField0_ |= 1;
                            } else if (readTag == 16) {
                                this.bitField0_ |= 2;
                                this.isRecentsAttachedToAppWindow_ = codedInputStream.readBool();
                            } else if (readTag == 24) {
                                this.bitField0_ |= 4;
                                this.scrollOffset_ = codedInputStream.readInt32();
                            } else if (readTag == 37) {
                                this.bitField0_ |= 8;
                                this.appToOverviewProgress_ = codedInputStream.readFloat();
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
                    synchronized (SwipeHandlerProto.class) {
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
        SwipeHandlerProto swipeHandlerProto = new SwipeHandlerProto();
        DEFAULT_INSTANCE = swipeHandlerProto;
        swipeHandlerProto.makeImmutable();
    }

    public static SwipeHandlerProto getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<SwipeHandlerProto> parser() {
        return DEFAULT_INSTANCE.getParserForType();
    }
}
