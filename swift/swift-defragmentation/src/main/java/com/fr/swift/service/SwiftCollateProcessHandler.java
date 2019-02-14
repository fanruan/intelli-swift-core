package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.basic.URL;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.UrlFactory;
import com.fr.swift.basics.annotation.RegisteredHandler;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.handler.AbstractProcessHandler;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.basics.handler.CollateProcessHandler;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.config.bean.SegLocationBean;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.collate.SwiftFragmentCollectRule;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.Util;

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
@SwiftBean
@SwiftScope("prototype")
@RegisteredHandler(CollateProcessHandler.class)
public class SwiftCollateProcessHandler extends AbstractProcessHandler<Map<URL, List<SegmentKey>>> implements CollateProcessHandler {
    public SwiftCollateProcessHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    @Override
    protected Map<URL, List<SegmentKey>> processUrl(Target[] targets, Object... args) {
        SourceKey sourceKey = (SourceKey) args[0];
        List<SegmentKey> segmentKeys = (List<SegmentKey>) args[1];

        SwiftSegmentLocationService locationService = SwiftContext.get().getBean(SwiftSegmentLocationService.class);
        List<SegLocationBean> segLocationBeanList = locationService.findBySourceKey(sourceKey);
        //clusterid-->segkeys
        Map<String, List<SegmentKey>> segkeysByClusterIdMap = new HashMap<String, List<SegmentKey>>();
        for (SegLocationBean segLocationBean : segLocationBeanList) {
            String clusterId = segLocationBean.getClusterId();
            if (!segkeysByClusterIdMap.containsKey(clusterId)) {
                segkeysByClusterIdMap.put(clusterId, new ArrayList<SegmentKey>());
            }
            for (SegmentKey segmentKey : segmentKeys) {
                if (Util.equals(segLocationBean.getSegmentId(), segmentKey.getId())) {
                    segkeysByClusterIdMap.get(clusterId).add(segmentKey);
                    break;
                }
            }
        }
        //计算后需要collate的节点和segmentkeys
        Map<URL, List<SegmentKey>> collateMap = new HashMap<URL, List<SegmentKey>>();
        //计算过程中已经算过的segKeys
        Set<SegmentKey> collatedSegKeys = new HashSet<SegmentKey>();
        UrlFactory factory = UrlSelector.getInstance().getFactory();


        for (Map.Entry<String, List<SegmentKey>> allEntry : segkeysByClusterIdMap.entrySet()) {
            URL url = factory.getURL(allEntry.getKey());
            List<SegmentKey> segmentKeyList = allEntry.getValue();
            segmentKeyList.removeAll(collatedSegKeys);
            if (segmentKeyList.size() >= SwiftFragmentCollectRule.FRAGMENT_NUMBER) {
                collateMap.put(url, new ArrayList<SegmentKey>());
                collateMap.get(url).addAll(segmentKeyList);
                collatedSegKeys.addAll(segmentKeyList);
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
            SourceKey sourceKey = (SourceKey) args[0];
            Map<URL, List<SegmentKey>> collateMap = processUrl(targets, args);

            for (Map.Entry<URL, List<SegmentKey>> urlListEntry : collateMap.entrySet()) {
                try {
                    URL url = urlListEntry.getKey();
                    List<SegmentKey> segmentKeyList = urlListEntry.getValue();
                    Invoker invoker = invokerCreator.createAsyncInvoker(CollateService.class, url);
                    invoke(invoker, proxy, method, methodName, paramClass, sourceKey, segmentKeyList);
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error(e);
                }
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
        return null;
    }
}
