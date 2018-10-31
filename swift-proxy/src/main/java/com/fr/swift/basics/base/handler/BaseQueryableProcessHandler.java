package com.fr.swift.basics.base.handler;

import com.fr.swift.basics.URL;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.basics.handler.QueryableProcessHandler;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryBeanFactory;
import com.fr.swift.segment.SegmentDestination;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018/10/25
 */
public abstract class BaseQueryableProcessHandler extends BaseProcessHandler implements QueryableProcessHandler {

    private QueryBean queryBean;

    @Override
    public Object processResult(Method method, Target target, Object... args) throws Throwable {
        String queryJson = (String) args[0];
        queryBean = SwiftContext.get().getBean(QueryBeanFactory.class).create(queryJson);
        return super.processResult(method, target, args);
    }

    @Override
    protected Object mergeResult(List resultList) throws Throwable {
        Object o = resultList.get(0);
        return handleAsyncResult(o);
    }

    @Override
    public List<URL> processUrl(Target target) {
        if (null != queryBean) {
            SegmentDestination destination = queryBean.getQueryDestination();
            if (null == destination) {
                return null;
            }
            URL url = UrlSelector.getInstance().getFactory().getURL(destination.getClusterId());
            return Collections.singletonList(url);
        }
        return null;
    }
}
