package com.fr.swift.api.rpc;

import java.util.Queue;

/**
 * @author yee
 * @date 2018/8/23
 */
public class ApiServiceAddressHolder {
    private Queue<String> realtimeAddresses;
    private Queue<String> analyseAddresses;


    public void setRealtimeAddresses(Queue<String> realtimeAddresses) {
        this.realtimeAddresses = realtimeAddresses;
    }

    public void setAnalyseAddresses(Queue<String> analyseAddresses) {
        this.analyseAddresses = analyseAddresses;
    }

    synchronized
    public String nextRealTime() {
        String address = realtimeAddresses.poll();
        if (null == address) {
            throw new RuntimeException("Insert service address not found");
        }
        realtimeAddresses.offer(address);
        return address;
    }

    synchronized
    public String nextAnalyse() {
        String address = analyseAddresses.poll();
        if (null == address) {
            throw new RuntimeException("Analyse service address not found");
        }
        analyseAddresses.offer(address);
        return address;
    }
}
