package com.fr.swift.api.rpc.holder;

/**
 * @author yee
 * @date 2018/8/23
 */
public interface ServiceAddressHolder {
    String nextAnalyseAddress();

    String nextRealTimeAddress();

    boolean isDetected();

    void reDetect();
}
