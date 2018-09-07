package com.fr.swift.api.rpc.impl;

import com.fr.swift.annotation.RpcService;
import com.fr.swift.api.rpc.SelectService;
import com.fr.swift.api.rpc.TableService;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.query.info.bean.query.AbstractSingleTableQueryInfoBean;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryBeanFactory;
import com.fr.swift.service.AnalyseService;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.util.ServiceBeanFactory;
import com.fr.third.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018/8/23
 */
@RpcService(value = SelectService.class, type = RpcService.RpcServiceType.EXTERNAL)
class SelectServiceImpl implements SelectService {
    @Autowired(required = false)
    private SwiftMetaDataService metaDataService;
    @Autowired(required = false)
    private TableService tableService;

    @Override
    public SwiftResultSet query(SwiftDatabase database, String queryJson) {
        try {
            QueryBean queryBean = SwiftContext.get().getBean(QueryBeanFactory.class).create(queryJson);
            if (queryBean instanceof AbstractSingleTableQueryInfoBean) {
                String tableName = ((AbstractSingleTableQueryInfoBean) queryBean).getTableName();
                SwiftMetaData metaData = metaDataService.getMetaDataByKey(tableName);
                if (null != metaData && metaData.getSwiftDatabase() == database) {
                    return getAnalyseService().getQueryResult(queryBean);
                }
                tableService.detectiveMetaData(database, tableName);
                return getAnalyseService().getQueryResult(queryBean);
            }
            throw new UnsupportedOperationException();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private AnalyseService getAnalyseService() {
        List<com.fr.swift.service.SwiftService> services = ServiceBeanFactory.getSwiftServiceByNames(Collections.singleton("analyse"));
        if (services.isEmpty()) {
            throw new RuntimeException("Can not find any RealTime service!");
        }
        return (AnalyseService) services.get(0);
    }
}
