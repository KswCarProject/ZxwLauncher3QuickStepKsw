package com.android.launcher3.tracing;

import com.google.protobuf.MessageLiteOrBuilder;
import java.util.List;

public interface LauncherTraceFileProtoOrBuilder extends MessageLiteOrBuilder {
    LauncherTraceEntryProto getEntry(int i);

    int getEntryCount();

    List<LauncherTraceEntryProto> getEntryList();

    long getMagicNumber();

    boolean hasMagicNumber();
}
