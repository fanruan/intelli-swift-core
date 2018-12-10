package com.fr.swift.netty.rpc.url;

import com.fr.swift.basic.Destination;

/**
 * This class created on 2018/6/7
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class RPCDestination implements Destination {

    private String address;

    public RPCDestination(String address) {
        this.address = address;
    }

    @Override
    public String getId() {
        return address;
    }
}
