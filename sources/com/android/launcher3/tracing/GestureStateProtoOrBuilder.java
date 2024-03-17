package com.android.launcher3.tracing;

import com.android.launcher3.tracing.GestureStateProto;
import com.google.protobuf.MessageLiteOrBuilder;

public interface GestureStateProtoOrBuilder extends MessageLiteOrBuilder {
    GestureStateProto.GestureEndTarget getEndTarget();

    boolean hasEndTarget();
}
