package com.fr.swift.service;

import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreater;
import com.fr.swift.basics.URL;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.handler.AbstractProcessHandler;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.basics.handler.CommonLoadProcessHandler;
import com.fr.swift.cluster.entity.ClusterEntity;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.util.MonitorUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class created on 2018/11/6
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftCommonLoadProcessHandler extends AbstractProcessHandler implements CommonLoadProcessHandler {

    public SwiftCommonLoadProcessHandler(InvokerCreater invokerCreater) {
        super(invokerCreater);
    }

    /**
     * args：需要load的seg信息
     * args[0]:sourcekey
     * args[1]:<segkey,uri>
     * 根据传入的seg信息，遍历所有history节点，找到每个history节点的seg
     * 检验该seg是否在需要args中，是则needload，否则不需要。
     *
     * @param method
     * @param target
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object processResult(Method method, Target target, Object... args) throws Throwable {
        MonitorUtil.start();
        Class proxyClass = method.getDeclaringClass();
        Class<?>[] parameterTypes = method.getParameterTypes();
        String methodName = method.getName();
        SwiftClusterSegmentService clusterSegmentService = SwiftContext.get().getBean(SwiftClusterSegmentService.class);

        String sourceKey = (String) args[0];
        Map<String, List<String>> uris = (Map<String, List<String>>) args[1];

        List<URL> urlList = processUrl(target);
        for (URL url : urlList) {
            String clusterId = url.getDestination().getId();
            Map<String, List<SegmentKey>> map = clusterSegmentService.getOwnSegments(clusterId);
            List<SegmentKey> list = map.get(sourceKey);
            Set<String> needLoad = new HashSet<String>();
            if (!list.isEmpty()) {
                for (SegmentKey segmentKey : list) {
                    String segKey = segmentKey.toString();
                    if (uris.containsKey(segKey)) {
                        needLoad.addAll(uris.get(segKey));
                    }
                }
            }
            if (!needLoad.isEmpty()) {
                Map<String, List<String>> loadMap = new HashMap<String, List<String>>();
                loadMap.put(sourceKey, new ArrayList<String>(needLoad));
                Invoker invoker = invokerCreater.createInvoker(proxyClass, url, false);
                handleAsyncResult(invoke(invoker, proxyClass, method, methodName, parameterTypes, sourceKey, loadMap));
            }
        }

        MonitorUtil.finish(methodName);
        return null;

    }

    @Override
    public List<URL> processUrl(Target target) {

        Map<String, ClusterEntity> services = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.HISTORY);
        if (null == services || services.isEmpty()) {
            throw new RuntimeException("Cannot find history service");
        }
        List<URL> urlList = new ArrayList<URL>();
        Iterator<String> urlIterator = services.keySet().iterator();
        while (urlIterator.hasNext()) {
            urlList.add(UrlSelector.getInstance().getFactory().getURL(urlIterator.next()));
        }
        return urlList;
    }
}
