package com.fr.swift.api.rpc;

import com.fr.swift.api.rpc.holder.AbstractServiceAddressHolder;
import com.fr.swift.service.ServiceType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/8/23
 */
public class ApiServiceAddressHolder extends AbstractServiceAddressHolder {

    private static ConcurrentHashMap<String, ApiServiceAddressHolder> instances = new ConcurrentHashMap<String, ApiServiceAddressHolder>();

    private ApiServiceAddressHolder(String address) {
        super(address);
        detect();
    }

    public static ApiServiceAddressHolder getHolder(String address) {
        if (null == instances.get(address)) {
            instances.put(address, new ApiServiceAddressHolder(address));
        }
        return instances.get(address);
    }


    @Override
    protected Map<ServiceType, List<String>> detectiveAddress(String address) {
        return Api.connectDetectiveApi(address).detectiveAnalyseAndRealTime(address);
    }
}
