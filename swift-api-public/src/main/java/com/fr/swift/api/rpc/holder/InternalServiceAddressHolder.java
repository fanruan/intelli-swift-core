package com.fr.swift.api.rpc.holder;

import com.fr.swift.event.global.GetAnalyseAndRealTimeAddrEvent;
import com.fr.swift.exception.SwiftProxyException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.listener.SwiftServiceListenerHandler;
import com.fr.swift.util.Assert;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;
import com.fr.swift.utils.ClusterProxyUtils;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yee
 * @date 2018/8/23
 */
public class InternalServiceAddressHolder implements ServiceAddressHolder {

    private static InternalServiceAddressHolder holder;
    private boolean detected = false;
    private Queue<String> queryServiceAddress;
    private Queue<String> insertServiceAddress;
    private ScheduledExecutorService reDetectService = SwiftExecutors.newSingleThreadScheduledExecutor(new PoolThreadFactory(InternalServiceAddressHolder.class));
    private AtomicInteger tryTime = new AtomicInteger(3);

    private InternalServiceAddressHolder() {
        queryServiceAddress = new ConcurrentLinkedQueue<String>();
        insertServiceAddress = new ConcurrentLinkedQueue<String>();
        detect();
    }

    public static InternalServiceAddressHolder getHolder() {
        if (null == holder) {
            synchronized (InternalServiceAddressHolder.class) {
                if (null == holder) {
                    holder = new InternalServiceAddressHolder();
                }
            }
        }
        return holder;
    }

    private boolean detect() {
        if (!detected) {
            if (tryTime.decrementAndGet() < 0) {
                throw new RuntimeException("Cannot detect service address!");
            }
            try {
                Map<ServiceType, List<String>> addresses = (Map<ServiceType, List<String>>) ClusterProxyUtils.getMasterProxy(SwiftServiceListenerHandler.class).trigger(new GetAnalyseAndRealTimeAddrEvent());
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
            } catch (SwiftProxyException e) {
                SwiftLoggers.getLogger().error("Detect service address with an exception. Retry at 10s later.", e);
                detected = false;
            }
            if (!detected) {
                reDetectService.schedule(new Runnable() {
                    @Override
                    public void run() {
                        detect();
                    }
                }, 10, TimeUnit.SECONDS);
            }
        }
        return detected;
    }

    @Override
    public String nextAnalyseAddress() {
        String address = queryServiceAddress.poll();
        Assert.notNull(address);
        queryServiceAddress.add(address);
        return address;
    }

    @Override
    public String nextRealTimeAddress() {
        String address = insertServiceAddress.poll();
        Assert.notNull(address);
        insertServiceAddress.add(address);
        return address;
    }

    @Override
    public boolean isDetected() {
        return detected;
    }

    @Override
    public void reDetect() {
        detected = false;
        tryTime.set(3);
        detect();
    }
}
