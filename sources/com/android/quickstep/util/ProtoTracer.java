package com.android.quickstep.util;

import android.content.Context;
import android.os.SystemClock;
import android.os.Trace;
import com.android.launcher3.tracing.LauncherTraceEntryProto;
import com.android.launcher3.tracing.LauncherTraceFileProto;
import com.android.launcher3.tracing.LauncherTraceProto;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.systemui.shared.tracing.FrameProtoTracer;
import com.android.systemui.shared.tracing.ProtoTraceable;
import com.google.protobuf.MessageLite;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;

public class ProtoTracer implements FrameProtoTracer.ProtoTraceParams<MessageLite.Builder, LauncherTraceFileProto.Builder, LauncherTraceEntryProto.Builder, LauncherTraceProto.Builder> {
    public static final MainThreadInitializedObject<ProtoTracer> INSTANCE = new MainThreadInitializedObject<>($$Lambda$iEF0GNPkeiSdz2FKTr8j3kzJhfE.INSTANCE);
    private static final long MAGIC_NUMBER_VALUE = 4851032461007867468L;
    private static final String TAG = "ProtoTracer";
    private final Context mContext;
    private final FrameProtoTracer<MessageLite.Builder, LauncherTraceFileProto.Builder, LauncherTraceEntryProto.Builder, LauncherTraceProto.Builder> mProtoTracer = new FrameProtoTracer<>(this);

    public ProtoTracer(Context context) {
        this.mContext = context;
    }

    public File getTraceFile() {
        return new File(this.mContext.getFilesDir(), "launcher_trace.pb");
    }

    public LauncherTraceFileProto.Builder getEncapsulatingTraceProto() {
        return LauncherTraceFileProto.newBuilder();
    }

    public LauncherTraceEntryProto.Builder updateBufferProto(LauncherTraceEntryProto.Builder builder, ArrayList<ProtoTraceable<LauncherTraceProto.Builder>> arrayList) {
        Trace.beginSection("ProtoTracer.updateBufferProto");
        LauncherTraceEntryProto.Builder newBuilder = LauncherTraceEntryProto.newBuilder();
        newBuilder.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        LauncherTraceProto.Builder newBuilder2 = LauncherTraceProto.newBuilder();
        Iterator<ProtoTraceable<LauncherTraceProto.Builder>> it = arrayList.iterator();
        while (it.hasNext()) {
            it.next().writeToProto(newBuilder2);
        }
        newBuilder.setLauncher(newBuilder2);
        Trace.endSection();
        return newBuilder;
    }

    public byte[] serializeEncapsulatingProto(LauncherTraceFileProto.Builder builder, Queue<LauncherTraceEntryProto.Builder> queue) {
        Trace.beginSection("ProtoTracer.serializeEncapsulatingProto");
        builder.setMagicNumber(MAGIC_NUMBER_VALUE);
        for (LauncherTraceEntryProto.Builder addEntry : queue) {
            builder.addEntry(addEntry);
        }
        byte[] byteArray = ((LauncherTraceFileProto) builder.build()).toByteArray();
        Trace.endSection();
        return byteArray;
    }

    public byte[] getProtoBytes(MessageLite.Builder builder) {
        return builder.build().toByteArray();
    }

    public int getProtoSize(MessageLite.Builder builder) {
        return builder.build().getSerializedSize();
    }

    public void start() {
        this.mProtoTracer.start();
    }

    public void stop() {
        this.mProtoTracer.stop();
    }

    public void add(ProtoTraceable<LauncherTraceProto.Builder> protoTraceable) {
        this.mProtoTracer.add(protoTraceable);
    }

    public void remove(ProtoTraceable<LauncherTraceProto.Builder> protoTraceable) {
        this.mProtoTracer.remove(protoTraceable);
    }

    public void scheduleFrameUpdate() {
        this.mProtoTracer.scheduleFrameUpdate();
    }

    public void update() {
        this.mProtoTracer.update();
    }
}
