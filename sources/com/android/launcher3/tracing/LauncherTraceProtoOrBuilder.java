package com.android.launcher3.tracing;

import com.google.protobuf.MessageLiteOrBuilder;

public interface LauncherTraceProtoOrBuilder extends MessageLiteOrBuilder {
    TouchInteractionServiceProto getTouchInteractionService();

    boolean hasTouchInteractionService();
}
