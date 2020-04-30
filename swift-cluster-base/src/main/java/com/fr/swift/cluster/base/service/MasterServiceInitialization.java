package com.fr.swift.cluster.base.service;

import com.fr.swift.cluster.base.handler.ClusterListenerHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2020/4/26
 *
 * @author Kuifang.Liu
 */
public class MasterServiceInitialization<T> {
    private final static List<String> SERVICES = new ArrayList<>();

    public static void register(String service) {
        synchronized (SERVICES) {
            SERVICES.add(service);
        }
    }

    public static void unregister(String service) {
        synchronized (SERVICES) {
            SERVICES.remove(service);
        }
    }

    public static void start(ClusterListenerHandler.ClusterEventData data) {
        synchronized (SERVICES) {
            for (String service : SERVICES) {
                System.out.println(service);
            }
        }
    }
}
