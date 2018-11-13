package com.fr.swift.basics.base.handler;

import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreater;
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
import java.util.List;

/**
 * @author yee
 * @date 2018/10/24
 */
public class SwiftMasterProcessHandler extends AbstractProcessHandler<URL> implements MasterProcessHandler {

    public SwiftMasterProcessHandler(InvokerCreater invokerCreater) {
        super(invokerCreater);
    }

    /**
     * 处理master的远程调用
     *
     * @param method
     * @param target
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object processResult(Method method, Target target, Object... args) throws Throwable {
        Class proxyClass = method.getDeclaringClass();
        Class<?>[] parameterTypes = method.getParameterTypes();
        String methodName = method.getName();
        try {
            MonitorUtil.start();
            URL masterUrl = processUrl(target, args);
            if (null == masterUrl) {
                Invoker invoker = new LocalInvoker(ProxyServiceRegistry.INSTANCE.getService(proxyClass), proxyClass, null);
                return invoke(invoker, proxyClass, method, methodName, parameterTypes, args);
            }
            Invoker invoker = invokerCreater.createSyncInvoker(proxyClass, masterUrl);
            Object object = invoke(invoker, proxyClass, method, methodName, parameterTypes, args);
            return object;
        } finally {
            MonitorUtil.finish(methodName);
        }
    }

    /**
     * 计算master的url
     *
     * @param target
     * @param args
     * @return
     */
    @Override
    public URL processUrl(Target target, Object... args) {
        List<SwiftServiceInfoBean> swiftServiceInfoBeans = SwiftContext.get().getBean(SwiftServiceInfoService.class)
                .getServiceInfoByService(SwiftServiceInfoService.SERVICE);
        SwiftServiceInfoBean swiftServiceInfoBean = swiftServiceInfoBeans.get(0);
        URL url = UrlSelector.getInstance().getFactory().getURL(swiftServiceInfoBean.getServiceInfo());
        return url;
    }
}
