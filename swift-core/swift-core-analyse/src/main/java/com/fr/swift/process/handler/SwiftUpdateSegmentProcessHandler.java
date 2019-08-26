package com.fr.swift.process.handler;

import com.fr.swift.basic.URL;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.annotation.RegisteredHandler;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.handler.BaseProcessHandler;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.basics.handler.UpdateSegmentProcessHandler;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.exception.ExceptionInfo;
import com.fr.swift.exception.ExceptionInfoBean;
import com.fr.swift.exception.ExceptionInfoType;
import com.fr.swift.exception.PushSegmentExceptionContext;
import com.fr.swift.exception.reporter.ExceptionReporter;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.service.ServiceType;
import com.fr.swift.util.MonitorUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author anner
 * @this class created on date 2019/8/23
 * @description 参考SwiftCommonProcessHandler, 删除其他情况的代码，保证异常的处理的唯一性
 */
@SwiftBean
@SwiftScope("prototype")
@RegisteredHandler(UpdateSegmentProcessHandler.class)
public class SwiftUpdateSegmentProcessHandler extends BaseProcessHandler<List<URL>> implements UpdateSegmentProcessHandler {

    public SwiftUpdateSegmentProcessHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    @Override
    public Object processResult(Method method, Target[] targets, Object... args) throws Throwable {
        MonitorUtil.start();
        Class proxyClass = method.getDeclaringClass();
        Class<?>[] parameterTypes = method.getParameterTypes();
        List<URL> urls = processUrl(targets);
        String methodName = method.getName();
        Object returnValue = null;
        //初始化异常信息的info
        SegmentLocationInfo locationInfo = (SegmentLocationInfo) args[0];
        for (URL url : urls) {
            Invoker invoker = invokerCreator.createSyncInvoker(proxyClass, url);
            try {
                invoke(invoker, proxyClass, method, methodName, parameterTypes, args);
            } catch (Exception e) {
                reportException(locationInfo);
                throw e;
            }
        }
        MonitorUtil.finish(methodName);
        return returnValue;
    }


    @Override
    protected Object mergeResult(List resultList, Object... args) {
        return null;
    }

    @Override
    public List<URL> processUrl(Target[] targets, Object... args) {
        Set<String> clusterIds = new HashSet<String>();
        for (Target target : targets) {
            clusterIds.addAll(ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.ANALYSE).keySet());
        }
        if (!clusterIds.isEmpty()) {
            List<URL> urls = new ArrayList<URL>();
            for (String clusterId : clusterIds) {
                urls.add(UrlSelector.getInstance().getFactory().getURL(clusterId));
            }
            return urls;
        }
        return Collections.emptyList();
    }

    //报告异常的方法抽出来，避免影响原有的逻辑的展示
    private void reportException(Object exceptionContext) {
        ExceptionInfo exceptionInfo = new ExceptionInfoBean.Builder()
                .setContext(new PushSegmentExceptionContext((SegmentLocationInfo) exceptionContext))
                .setType(ExceptionInfoType.MASTER_PUSH_SEGMENT)
                .setNowAndHere().build();
        ExceptionReporter.report(exceptionInfo);
    }
}
