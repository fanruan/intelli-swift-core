package com.fr.swift.utils;

import com.fr.swift.ClusterNodeService;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.Result;
import com.fr.swift.basics.RpcFuture;
import com.fr.swift.basics.URL;
import com.fr.swift.basics.base.SwiftInvocation;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.service.listener.SwiftServiceListenerHandler;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author yee
 * @date 2018/8/9
 */
public class ClusterCommonUtils {
    public static URL getMasterURL() {
        List<SwiftServiceInfoBean> swiftServiceInfoBeans = SwiftContext.get().getBean(SwiftServiceInfoService.class).getServiceInfoByService(ClusterNodeService.SERVICE);
        SwiftServiceInfoBean swiftServiceInfoBean = swiftServiceInfoBeans.get(0);
        return UrlSelector.getInstance().getFactory().getURL(swiftServiceInfoBean.getServiceInfo());
    }

    public static RpcFuture runRpc(URL target, Method method, Object... args) throws Exception {
        URL masterURL = target;
        ProxyFactory factory = ProxySelector.getInstance().getFactory();
        Invoker invoker = factory.getInvoker(null, SwiftServiceListenerHandler.class, masterURL, false);
        Result invokeResult = invoker.invoke(new SwiftInvocation(method, args));
        RpcFuture future = (RpcFuture) invokeResult.getValue();
        if (null != future) {
            return future;
        }
        throw new Exception(invokeResult.getException());
    }
}
