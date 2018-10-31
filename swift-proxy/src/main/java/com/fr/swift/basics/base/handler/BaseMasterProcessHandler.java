package com.fr.swift.basics.base.handler;

import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.URL;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.ProxyServiceRegistry;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.basics.handler.MasterProcessHandler;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.local.LocalInvoker;
import com.fr.swift.util.MonitorUtil;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @author yee
 * @date 2018/10/24
 */
public abstract class BaseMasterProcessHandler extends AbstractProcessHandler implements MasterProcessHandler {
    @Override
    public URL processMasterURL() {
        return processUrl(Target.NONE).get(0);
    }

    @Override
    public Object processResult(Method method, Target target, Object... args) throws Throwable {
        MonitorUtil.start();
        Class proxyClass = method.getDeclaringClass();
        Class<?>[] parameterTypes = method.getParameterTypes();
        URL masterUrl = processMasterURL();
        String methodName = method.getName();
        if (null == masterUrl) {
            Invoker invoker = new LocalInvoker(ProxyServiceRegistry.INSTANCE.getService(proxyClass), proxyClass, null);
            return invoke(invoker, proxyClass, method, methodName, parameterTypes, args);
        }
        Invoker invoker = createInvoker(proxyClass, masterUrl);
        Object object = handleAsyncResult(invoke(invoker, proxyClass, method, methodName, parameterTypes, args));
        MonitorUtil.finish(methodName);
        return object;
    }

    @Override
    public List<URL> processUrl(Target target) {
        List<SwiftServiceInfoBean> swiftServiceInfoBeans = SwiftContext.get().getBean(SwiftServiceInfoService.class)
                .getServiceInfoByService(SwiftServiceInfoService.SERVICE);
        SwiftServiceInfoBean swiftServiceInfoBean = swiftServiceInfoBeans.get(0);
        URL url = UrlSelector.getInstance().getFactory().getURL(swiftServiceInfoBean.getServiceInfo());
        return Arrays.asList(url);
    }

    /**
     * 创建Invoker
     *
     * @param tClass
     * @param url
     * @return
     */
    protected abstract Invoker createInvoker(Class tClass, URL url);
}
