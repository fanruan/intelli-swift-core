package com.fr.swift.api.rpc.holder;

import com.fr.swift.api.rpc.DetectService;
import com.fr.swift.api.rpc.invoke.ApiProxyFactory;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.ServiceType;
import com.fr.swift.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author yee
 * @date 2018/8/23
 */
public class InternalServiceAddressHolder implements ServiceAddressHolder {

    private static ConcurrentHashMap<String, InternalServiceAddressHolder> instances = new ConcurrentHashMap<String, InternalServiceAddressHolder>();
    private boolean detected = false;
    private Queue<String> queryServiceAddress;
    private Queue<String> insertServiceAddress;
    private String address;

    private InternalServiceAddressHolder(String address) {
        this.address = address;
        queryServiceAddress = new ConcurrentLinkedQueue<String>();
        insertServiceAddress = new ConcurrentLinkedQueue<String>();
        detect();
    }

    public static InternalServiceAddressHolder getHolder(String address) {
        if (null == instances.get(address)) {
            instances.put(address, new InternalServiceAddressHolder(address));
        }
        return instances.get(address);
    }

    private boolean detect() {
        if (!detected) {
            try {
                Map<ServiceType, List<String>> addresses = ApiProxyFactory.getProxy(DetectService.class, address).detectiveAnalyseAndRealTime(address);
                if (addresses.isEmpty()) {
                    detected = false;
                    SwiftLoggers.getLogger().warn("Cannot find service address. Retry at 10s later.");
                } else {
                    detected = true;
                    queryServiceAddress.clear();
                    queryServiceAddress.addAll(addresses.get(ServiceType.ANALYSE));
                    insertServiceAddress.clear();
                    insertServiceAddress.addAll(addresses.get(ServiceType.REAL_TIME));
                }
            } catch (Exception e) {
                SwiftLoggers.getLogger().error("Detect service address with an exception.", e);
                detected = false;
            }
        }
        return detected;
    }

    @Override
    public String nextAnalyseAddress() {
        return getAddress(queryServiceAddress);
    }

    @Override
    public String nextRealTimeAddress() {
        return getAddress(insertServiceAddress);
    }

    @Override
    public String rootAddress() {
        return address;
    }

    private String getAddress(Queue<String> addresses) {
        detect();
        String address = addresses.poll();
        Assert.notNull(address);
        addresses.add(address);
        return address;
    }

    @Override
    public boolean isDetected() {
        return detected;
    }

    @Override
    public void reDetect() {
        detected = false;
        detect();
    }
}
