package com.android.launcher3.tracing;

import com.google.protobuf.MessageLiteOrBuilder;

public interface LauncherTraceEntryProtoOrBuilder extends MessageLiteOrBuilder {
    long getElapsedRealtimeNanos();

    LauncherTraceProto getLauncher();

    boolean hasElapsedRealtimeNanos();

    boolean hasLauncher();
}
