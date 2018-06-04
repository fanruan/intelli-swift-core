package com.finebi.web.action.v5.conf;

import com.fr.swift.Invocation;
import com.fr.swift.Invoker;
import com.fr.swift.ProxyFactory;
import com.fr.swift.Result;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftConfigServiceProvider;
import com.fr.swift.frrpc.ClusterNodeManager;
import com.fr.swift.frrpc.FRDestination;
import com.fr.swift.frrpc.FRProxyCache;
import com.fr.swift.frrpc.FRUrl;
import com.fr.swift.invocation.SwiftInvocation;
import com.fr.swift.proxy.LocalProxyFactory;
import com.fr.swift.selector.ProxySelector;
import com.fr.swift.url.LocalUrl;
import com.fr.third.springframework.stereotype.Controller;
import com.fr.third.springframework.web.bind.annotation.RequestBody;
import com.fr.third.springframework.web.bind.annotation.RequestMapping;
import com.fr.third.springframework.web.bind.annotation.RequestMethod;
import com.fr.third.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;


/**
 * This class created on 2018/5/30
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Controller
public class SwiftTestAction {


    /**
     * swift测试接口
     *
     * @param map
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/v5/conf/swift/rpc/test", method = RequestMethod.POST)
    public Object saveTable(@RequestBody() Map map) throws Exception {

        String className = (String) map.get("className");
        Class classType = Class.forName(className);
        String methodName = (String) map.get("methodName");

        Object[] paramsClassNames = ((List<String>) map.get("paramsClassNames")).toArray();
        Class[] paramsClassTypes = new Class[paramsClassNames.length];
        for (int i = 0; i < paramsClassNames.length; i++) {
            paramsClassTypes[i] = Class.forName(String.valueOf(paramsClassNames[i]));
        }
        Object[] params = ((List<String>) map.get("params")).toArray();

        if (ClusterNodeManager.getInstance().isCluster()) {
            ProxyFactory proxyFactory = ProxySelector.getInstance().getFactory();
            String masterId = (String) map.get("masterId");
            Invoker invoker = proxyFactory.getInvoker(FRProxyCache.getInstance(classType), classType, new FRUrl(masterId == null ? null : new FRDestination(masterId)));
            Invocation invocation = new SwiftInvocation(methodName, paramsClassTypes, params);
            Result result = invoker.invoke(invocation);
            return result;
        } else {
            ProxyFactory proxyFactory = new LocalProxyFactory();
            Invoker invoker = proxyFactory.getInvoker(null, classType, new LocalUrl());
            Invocation invocation = new SwiftInvocation(methodName, paramsClassTypes, params);
            Result result = invoker.invoke(invocation);
            return result;
        }
    }
}
