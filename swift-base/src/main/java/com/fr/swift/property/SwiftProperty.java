package com.fr.swift.property;

import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.beans.factory.annotation.Value;
import com.fr.third.springframework.stereotype.Service;

/**
 * This class created on 2018/6/8
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Service
public class SwiftProperty {
    private String rpcAddress;

    @Autowired
    public void setRpcAddress(@Value("${rpc.server_address}") String rpcAddress) {
        this.rpcAddress = rpcAddress;
    }

    public String getRpcAddress() {
        return rpcAddress;
    }
}
