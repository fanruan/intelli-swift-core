package com.fr.swift.netty.rpc.url;

import com.fr.swift.basic.URL;
import com.fr.swift.basics.UrlFactory;

/**
 * This class created on 2018/6/12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class RPCUrlFactory implements UrlFactory<String> {

    @Override
    public URL getURL(String address) {
        return new RPCUrl(new RPCDestination(address));
    }
}
