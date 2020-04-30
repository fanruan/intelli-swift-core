package com.fr.swift.cluster.base.trigger;

import com.fr.swift.cluster.base.handler.ClusterListenerHandler;
import com.fr.swift.util.Crasher;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2020/4/28
 *
 * @author Kuifang.Liu
 */
public class MasterServiceInitiator {
    private static List<SwiftInitiator> INITIATORS = new ArrayList<>();

    public static void register(SwiftInitiator initiator) {
        synchronized (INITIATORS) {
            INITIATORS.add(initiator);
        }
    }

    public static void unregister(SwiftInitiator initiator) {
        synchronized (INITIATORS) {
            INITIATORS.remove(initiator);
        }
    }

    public static void init(ClusterListenerHandler.ClusterEventData data) {
        synchronized (INITIATORS) {
            INITIATORS.forEach(initiator -> {
                try {
                    initiator.init(data);
                } catch (Exception e) {
                    Crasher.crash(e);
                }
            });
        }
    }
}
