package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.basic.URL;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.UrlFactory;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.handler.AbstractProcessHandler;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.basics.handler.CollateProcessHandler;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.config.service.impl.SwiftSegmentServiceProvider;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.collate.SwiftFragmentCollectRule;
import com.fr.swift.source.SourceKey;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yee
 * @date 2019-01-21
 */
public class SwiftCollateProcessHandler extends AbstractProcessHandler<Map<URL, Map<String, List<SegmentKey>>>> implements CollateProcessHandler {
    public SwiftCollateProcessHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    @Override
    protected Map<URL, Map<String, List<SegmentKey>>> processUrl(Target[] targets, Object... args) {
        SwiftSegmentService swiftSegmentService = SwiftContext.get().getBean(SwiftSegmentServiceProvider.class);
        //所有节点的segmentKey
        Map<String, Map<String, List<SegmentKey>>> allSegmentKeys = swiftSegmentService.getAllSegLocations();
        //计算后需要collate的节点和segmentkey
        Map<URL, Map<String, List<SegmentKey>>> collateMap = new HashMap<URL, Map<String, List<SegmentKey>>>();
        //计算过程中已经算过的segKeys
        Set<SegmentKey> collatedSegKeys = new HashSet<SegmentKey>();

        UrlFactory factory = UrlSelector.getInstance().getFactory();
        for (Map.Entry<String, Map<String, List<SegmentKey>>> allEntry : allSegmentKeys.entrySet()) {
            String clusterId = allEntry.getKey();
            collateMap.put(factory.getURL(clusterId), new HashMap<String, List<SegmentKey>>());

            for (Map.Entry<String, List<SegmentKey>> sourcekeyEntry : allEntry.getValue().entrySet()) {
                String sourceKey = sourcekeyEntry.getKey();
                List<SegmentKey> segmentKeys = new ArrayList<SegmentKey>(sourcekeyEntry.getValue());
                segmentKeys.removeAll(collatedSegKeys);
                if (segmentKeys.size() >= SwiftFragmentCollectRule.FRAGMENT_NUMBER) {
                    collateMap.get(clusterId).put(sourceKey, segmentKeys);
                    collatedSegKeys.addAll(segmentKeys);
                }
            }
        }
        return collateMap;
    }

    @Override
    public Object processResult(Method method, Target[] targets, Object... args) throws Throwable {
        try {
            Class proxy = method.getDeclaringClass();
            String methodName = method.getName();
            Class[] paramClass = method.getParameterTypes();
            Map<URL, Map<String, List<SegmentKey>>> collateMap = processUrl(targets, args);
            for (Map.Entry<URL, Map<String, List<SegmentKey>>> urlMapEntry : collateMap.entrySet()) {
                URL url = urlMapEntry.getKey();
                Map<String, List<SegmentKey>> keysInfo = urlMapEntry.getValue();
                for (Map.Entry<String, List<SegmentKey>> entry : keysInfo.entrySet()) {
                    Invoker invoker = invokerCreator.createAsyncInvoker(CollateService.class, url);
                    invoke(invoker, proxy, method, methodName, paramClass, new SourceKey(entry.getKey()), entry.getValue());
                }

            }

        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
        return null;
    }
}
