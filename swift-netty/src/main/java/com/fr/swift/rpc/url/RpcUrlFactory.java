package com.fr.swift.rpc.url;

import com.fr.swift.URL;
import com.fr.swift.UrlFactory;

/**
 * This class created on 2018/6/12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class RpcUrlFactory implements UrlFactory<String> {

    @Override
    public URL getURL(String address) {
        return new RPCUrl(new RPCDestination(address));
    }
}
