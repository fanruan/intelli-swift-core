//package com.fr.swift.utils;
//
//import com.fr.general.ComparatorUtils;
//import com.fr.swift.ClusterNodeService;
//import com.fr.swift.basics.Invoker;
//import com.fr.swift.basics.ProxyFactory;
//import com.fr.swift.basics.Result;
//import com.fr.swift.basics.RpcFuture;
//import com.fr.swift.basic.URL;
//import com.fr.swift.basics.base.SwiftInvocation;
//import com.fr.swift.basics.base.selector.ProxySelector;
//import com.fr.swift.basics.base.selector.UrlSelector;
//import com.fr.swift.config.bean.SwiftServiceInfoBean;
//import com.fr.swift.config.service.SwiftServiceInfoService;
//import com.fr.swift.beans.SwiftContext;
//import com.fr.swift.event.base.SwiftRpcEvent;
//import com.fr.swift.selector.ClusterSelector;
//import com.fr.swift.service.listener.SwiftServiceListenerHandler;
//
//import java.io.Serializable;
//import java.lang.reflect.Method;
//import java.util.List;
//
///**
// * @author yee
// * @date 2018/8/9
// */
//public class ClusterCommonUtils {
//    public static URL getMasterURL() {
//        List<SwiftServiceInfoBean> swiftServiceInfoBeans = SwiftContext.get().getBean(SwiftServiceInfoService.class)
//                .getServiceInfoByService(ClusterNodeService.SERVICE);
//        SwiftServiceInfoBean swiftServiceInfoBean = swiftServiceInfoBeans.get(0);
//        return UrlSelector.getInstance().getFactory().getURL(swiftServiceInfoBean.getServiceInfo());
//    }
//
//    public static RpcFuture runAsyncRpc(String address, Class clazz, Method method, Object... args) throws Exception {
//        URL url = UrlSelector.getInstance().getFactory().getURL(address);
//        return runAsyncRpc(url, clazz, method, args);
//    }
//
//    public static <T> RpcFuture runAsyncRpc(URL url, Class<T> clazz, Method method, Object... args) throws Exception {
//        ProxyFactory factory = null;
//        if (ComparatorUtils.equals(ClusterSelector.getInstance().getFactory().getCurrentId(),
//                url.getDestination().getId())) {
//            factory = new LocalProxyFactory();
//        } else {
//            factory = ProxySelector.getInstance().getFactory();
//        }
//        Invoker invoker = factory.getInvoker(SwiftContext.get().getBean(clazz), clazz, url, false);
//        Result invokeResult = invoker.invoke(new SwiftInvocation(method, args));
//        RpcFuture future = (RpcFuture) invokeResult.getValue();
//        if (null != future) {
//            return future;
//        }
//        throw new Exception(invokeResult.getException());
//    }
//
//    public static RpcFuture asyncCallMaster(SwiftRpcEvent event) throws Exception {
//        return runAsyncRpc(getMasterURL(), SwiftServiceListenerHandler.class, SwiftServiceListenerHandler.class.getMethod("trigger", SwiftRpcEvent.class), event);
//    }
//
//    public static Serializable runSyncMaster(SwiftRpcEvent event) {
//        URL url = getMasterURL();
//        ProxyFactory factory = null;
//        if (ComparatorUtils.equals(ClusterSelector.getInstance().getFactory().getCurrentId(),
//                url.getDestination().getId())) {
//            factory = new LocalProxyFactory();
//        } else {
//            factory = ProxySelector.getInstance().getFactory();
//        }
//        SwiftServiceListenerHandler remoteServiceSender = SwiftContext.get().getBean("remoteServiceSender", SwiftServiceListenerHandler.class);
//
//        SwiftServiceListenerHandler proxy = factory.getProxy(remoteServiceSender, SwiftServiceListenerHandler.class, url);
//        return proxy.trigger(event);
//    }
//
//}
