package com.fr.swift.service.listener;

import com.fr.swift.basic.URL;

import java.util.Set;

/**
 * @author anchore
 * @date 2019/1/22
 */
public interface SwiftServiceEventHandler extends SwiftServiceListenerHandler {

    Set<URL> getNodeUrls(Class<?> proxyIface);
}