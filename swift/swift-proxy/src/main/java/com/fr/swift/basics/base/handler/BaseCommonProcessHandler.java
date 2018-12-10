package com.fr.swift.basics.base.handler;

import com.fr.swift.basic.URL;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.basics.handler.CommonProcessHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author yee
 * @date 2018/10/29
 */
public abstract class BaseCommonProcessHandler extends BaseProcessHandler<List<URL>> implements CommonProcessHandler {

    public BaseCommonProcessHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    @Override
    protected Object mergeResult(List resultList, Object... args) {
        return null;
    }

    @Override
    public List<URL> processUrl(Target target, Object... args) {
        Set<String> clusterIds = null;
        // TODO 获取clusterId
        switch (target) {
            case ANALYSE:
                clusterIds = new HashSet<String>();
                break;
            case HISTORY:
                clusterIds = new HashSet<String>();
                break;
            case REAL_TIME:
                clusterIds = new HashSet<String>();
                break;
            case INDEXING:
                clusterIds = new HashSet<String>();
                break;
            case ALL:
                clusterIds = new HashSet<String>();
                break;
            default:
                return null;
        }
        if (!clusterIds.isEmpty()) {
            List<URL> urls = new ArrayList<URL>();
            for (String clusterId : clusterIds) {
                urls.add(UrlSelector.getInstance().getFactory().getURL(clusterId));
            }
            return urls;
        }
        return null;
    }
}
