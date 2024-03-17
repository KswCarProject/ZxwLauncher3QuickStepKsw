package com.android.launcher3.tracing;

import com.google.protobuf.MessageLiteOrBuilder;

public interface SwipeHandlerProtoOrBuilder extends MessageLiteOrBuilder {
    float getAppToOverviewProgress();

    GestureStateProto getGestureState();

    boolean getIsRecentsAttachedToAppWindow();

    int getScrollOffset();

    boolean hasAppToOverviewProgress();

    boolean hasGestureState();

    boolean hasIsRecentsAttachedToAppWindow();

    boolean hasScrollOffset();
}
