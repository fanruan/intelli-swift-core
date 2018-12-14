package com.fr.swift.core.rpc;

import com.fr.swift.basic.URL;
import com.fr.swift.basics.UrlFactory;

/**
 * This class created on 2018/7/18
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class FRUrlFactory implements UrlFactory<String> {
    @Override
    public URL getURL(String destNodeId) {
        return new FRUrl(new FRDestination(destNodeId));
    }
}
