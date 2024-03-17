package com.android.launcher3.tracing;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLiteOrBuilder;

public interface InputConsumerProtoOrBuilder extends MessageLiteOrBuilder {
    String getName();

    ByteString getNameBytes();

    SwipeHandlerProto getSwipeHandler();

    boolean hasName();

    boolean hasSwipeHandler();
}
