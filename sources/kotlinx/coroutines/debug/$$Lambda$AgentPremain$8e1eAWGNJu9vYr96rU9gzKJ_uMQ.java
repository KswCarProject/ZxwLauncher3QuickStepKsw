package kotlinx.coroutines.debug;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/* renamed from: kotlinx.coroutines.debug.-$$Lambda$AgentPremain$8e1eAWGNJu9vYr96rU9gzKJ_uMQ  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$AgentPremain$8e1eAWGNJu9vYr96rU9gzKJ_uMQ implements SignalHandler {
    public static final /* synthetic */ $$Lambda$AgentPremain$8e1eAWGNJu9vYr96rU9gzKJ_uMQ INSTANCE = new $$Lambda$AgentPremain$8e1eAWGNJu9vYr96rU9gzKJ_uMQ();

    private /* synthetic */ $$Lambda$AgentPremain$8e1eAWGNJu9vYr96rU9gzKJ_uMQ() {
    }

    public final void handle(Signal signal) {
        AgentPremain.m1600installSignalHandler$lambda1(signal);
    }
}
