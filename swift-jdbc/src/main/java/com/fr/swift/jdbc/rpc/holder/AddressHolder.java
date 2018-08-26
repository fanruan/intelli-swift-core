package com.fr.swift.jdbc.rpc.holder;

import com.fr.swift.api.rpc.DetectService;
import com.fr.swift.api.rpc.holder.ServiceAddressHolder;
import com.fr.swift.jdbc.rpc.invoke.ClientProxy;
import com.fr.swift.jdbc.rpc.nio.RpcConnector;
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
public class AddressHolder implements ServiceAddressHolder {

    private static ConcurrentHashMap<String, AddressHolder> instances = new ConcurrentHashMap<String, AddressHolder>();
    private boolean detected = false;
    private Queue<String> queryServiceAddress;
    private Queue<String> insertServiceAddress;
    private String address;
    private RpcConnector rootConnector;
    private ClientProxy proxy;

    private AddressHolder(String address) {
        this.address = address;
        queryServiceAddress = new ConcurrentLinkedQueue<String>();
        insertServiceAddress = new ConcurrentLinkedQueue<String>();
        rootConnector = new RpcConnector(address);
        proxy = new ClientProxy(rootConnector);
        detect();
    }

    private AddressHolder(String host, int port) {
        queryServiceAddress = new ConcurrentLinkedQueue<String>();
        insertServiceAddress = new ConcurrentLinkedQueue<String>();
        this.address = host + ":" + port;
        rootConnector = new RpcConnector(host, port);
        proxy = new ClientProxy(rootConnector);
        detect();
    }

    public static AddressHolder getHolder(String address) {
        if (null == instances.get(address)) {
            instances.put(address, new AddressHolder(address));
        }
        return instances.get(address);
    }

    public static AddressHolder getHolder(String host, int port) {
        port = port == -1 ? 7000 : port;
        String address = host + ":" + port;
        if (null == instances.get(address)) {
            instances.put(address, new AddressHolder(host, port));
        }
        return instances.get(address);
    }

    private boolean detect() {
        if (!detected) {
            try {
                proxy.start();
                Map<ServiceType, List<String>> addresses = proxy.getProxy(DetectService.class).detectiveAnalyseAndRealTime(address);
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
                proxy.stop();
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
