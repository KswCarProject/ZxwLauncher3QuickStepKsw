package com.android.launcher3.tracing;

import com.google.protobuf.MessageLiteOrBuilder;

public interface OverviewComponentObserverProtoOrBuilder extends MessageLiteOrBuilder {
    boolean getOverviewActivityResumed();

    boolean getOverviewActivityStarted();

    boolean hasOverviewActivityResumed();

    boolean hasOverviewActivityStarted();
}
