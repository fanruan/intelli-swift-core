package com.fr.swift.netty.rpc.url;

import com.fr.swift.basic.Destination;
import com.fr.swift.basic.URL;

/**
 * This class created on 2018/6/7
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class RPCUrl implements URL {

    private Destination destination;

    public RPCUrl(Destination destination) {
        this.destination = destination;
    }

    @Override
    public Destination getDestination() {
        return destination;
    }
}
