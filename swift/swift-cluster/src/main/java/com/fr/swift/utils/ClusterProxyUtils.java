//package com.fr.swift.utils;
//
//import com.fr.swift.basics.ProxyFactory;
//import com.fr.swift.basics.base.selector.ProxySelector;
//import com.fr.swift.basics.exception.SwiftProxyException;
//import com.fr.swift.beans.SwiftContext;
//
///**
// * This class created on 2018/7/17
// *
// * @author Lucifer
// * @description
// * @since Advanced FineBI 5.0
// */
//public class ClusterProxyUtils {
//
//    /**
//     * slave和master代理工具
//     * @param interfaceClass
//     * @param <T>
//     * @return
//     * @throws SwiftProxyException
//     */
//    public static <T> T getProxy(Class<T> interfaceClass) throws SwiftProxyException {
//        ProxyFactory proxyFactory = ProxySelector.getInstance().getFactory();
//        if (!interfaceClass.isInterface()) {
//            throw new SwiftProxyException("Class " + interfaceClass + " is not interface !!! Can't make dynamic proxy!");
//        }
//        T obj = SwiftContext.get().getBean(interfaceClass);
//        if (obj == null) {
//            if (!interfaceClass.isInterface()) {
//                throw new SwiftProxyException("Class " + interfaceClass + " doesn't has an Implementation !!! Can't make dynamic proxy!");
//            }
//        }
//        return proxyFactory.getProxy(interfaceClass);
//    }
//}
