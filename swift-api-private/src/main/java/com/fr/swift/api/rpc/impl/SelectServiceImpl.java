package com.fr.swift.api.rpc.impl;

import com.fr.swift.annotation.RpcService;
import com.fr.swift.annotation.RpcServiceType;
import com.fr.swift.api.rpc.SelectService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.query.info.bean.query.QueryInfoBeanFactory;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryBeanFactory;
import com.fr.swift.result.serialize.SwiftResultSetUtils;
import com.fr.swift.service.AnalyseService;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.util.ServiceBeanFactory;

import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018/8/23
 */
@RpcService(value = SelectService.class, type = RpcServiceType.EXTERNAL)
class SelectServiceImpl implements SelectService {
    @Override
    public SwiftResultSet query(String queryJson) {
        try {
            QueryBean queryBean = SwiftContext.get().getBean(QueryBeanFactory.class).create(queryJson);
            SwiftResultSet resultSet = getAnalyseService().getQueryResult(queryBean);
            QueryBean bean = new QueryInfoBeanFactory().create(queryJson);
            return SwiftResultSetUtils.convert2Serializable(queryJson, bean.getQueryType(), resultSet);
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
