package com.android.launcher3.tracing;

import com.google.protobuf.MessageLiteOrBuilder;

public interface TouchInteractionServiceProtoOrBuilder extends MessageLiteOrBuilder {
    InputConsumerProto getInputConsumer();

    OverviewComponentObserverProto getOverviewComponentObvserver();

    boolean getServiceConnected();

    boolean hasInputConsumer();

    boolean hasOverviewComponentObvserver();

    boolean hasServiceConnected();
}
