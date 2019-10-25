package com.fr.swift.result.node.resultset;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.service.ServiceContext;

/**
 * @author lucifer
 * @date 2019-08-06
 * @description
 * @since advanced swift 1.0
 */
public class ResultSetUtils {

    public static void close(String jsonString) {
        try {
            QueryBean queryBean = SwiftContext.get().getBean(QueryBeanFactory.class).create(jsonString);
            closeById(queryBean.getQueryId());
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    public static void closeById(String queryId) {
        try {
            ServiceContext serviceContext = ProxySelector.getProxy(ServiceContext.class);
            serviceContext.clearQuery(queryId);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }
}