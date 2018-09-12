package com.fr.swift.api.rpc.holder;

import com.fr.swift.api.rpc.exception.AddressAbsentException;
import com.fr.swift.api.rpc.exception.ConnectException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.ServiceType;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author yee
 * @date 2018/9/6
 */
public abstract class AbstractServiceAddressHolder implements ServiceAddressHolder {
    private boolean detected = false;
    private Queue<String> queryServiceAddress;
    private Queue<String> insertServiceAddress;
    private String address;

    protected AbstractServiceAddressHolder(String address) {
        this.address = address;
        queryServiceAddress = new ConcurrentLinkedQueue<String>();
        insertServiceAddress = new ConcurrentLinkedQueue<String>();
    }

    protected boolean detect() {
        if (!detected) {
            try {
                Map<ServiceType, List<String>> addresses = detectiveAddress(address);
                if (addresses.isEmpty()) {
                    detected = false;
                    SwiftLoggers.getLogger().warn("Cannot find service address.");
                } else {
                    detected = true;
                    queryServiceAddress.clear();
                    queryServiceAddress.addAll(addresses.get(ServiceType.ANALYSE));
                    insertServiceAddress.clear();
                    insertServiceAddress.addAll(addresses.get(ServiceType.REAL_TIME));
                }
            } catch (Exception e) {
                throw new ConnectException(address, e);
            }
        }
        return detected;
    }

    /**
     * 获取服务地址
     *
     * @param address
     * @return
     * @throws Exception
     */
    protected abstract Map<ServiceType, List<String>> detectiveAddress(String address) throws Exception;

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
        if (null == address) {
            throw new AddressAbsentException();
        }
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
