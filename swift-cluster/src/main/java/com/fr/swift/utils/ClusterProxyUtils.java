package com.fr.swift.utils;

import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.URL;
import com.fr.swift.basics.UrlFactory;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.exception.SwiftProxyException;
import com.fr.swift.heart.HeartBeatInfo;
import com.fr.swift.property.SwiftProperty;

/**
 * This class created on 2018/7/17
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ClusterProxyUtils {
    /**
     * 获取指定类在master节点的代理
     *
     * @param interfaceClass
     * @param <T>
     * @return
     */
    public static <T> T getMasterProxy(Class<T> interfaceClass) throws SwiftProxyException {
        UrlFactory urlFactory = UrlSelector.getInstance().getFactory();
        URL url = urlFactory.getURL(SwiftContext.get().getBean("swiftProperty", SwiftProperty.class).getMasterAddress());
        return getProxy(interfaceClass, url);
    }

    /**
     * 获得指定类在心跳节点的代理
     *
     * @param interfaceClass
     * @param heartBeatInfo
     * @param <T>
     * @return
     */
    public static <T> T getSlaveProxy(Class<T> interfaceClass, HeartBeatInfo heartBeatInfo) throws SwiftProxyException {
        UrlFactory urlFactory = UrlSelector.getInstance().getFactory();
        URL url = urlFactory.getURL(heartBeatInfo.getAddress());
        return getProxy(interfaceClass, url);
    }

    private static <T> T getProxy(Class<T> interfaceClass, URL url) throws SwiftProxyException {
        ProxyFactory proxyFactory = ProxySelector.getInstance().getFactory();
        if (!interfaceClass.isInterface()) {
            throw new SwiftProxyException("Class " + interfaceClass + " is not interface !!! Can't make dynamic proxy!");
        }
        T obj = SwiftContext.get().getBean(interfaceClass);
        if (obj == null) {
            if (!interfaceClass.isInterface()) {
                throw new SwiftProxyException("Class " + interfaceClass + " doesn't has an Implementation !!! Can't make dynamic proxy!");
            }
        }
        return proxyFactory.getProxy(obj, interfaceClass, url);
    }
}
