package com.fr.swift.api.rpc.impl;

import com.fr.swift.annotation.RpcService;
import com.fr.swift.annotation.SwiftApi;
import com.fr.swift.api.rpc.SelectService;
import com.fr.swift.api.rpc.TableService;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.query.info.bean.query.AbstractSingleTableQueryInfoBean;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryBeanFactory;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.result.serialize.SerializableDetailResultSet;
import com.fr.swift.service.AnalyseService;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.util.ServiceBeanFactory;
import com.fr.third.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018/8/23
 */
@RpcService(value = SelectService.class, type = RpcService.RpcServiceType.EXTERNAL)
@SwiftApi
class SelectServiceImpl implements SelectService {
    @Autowired(required = false)
    private SwiftMetaDataService metaDataService;
    @Autowired(required = false)
    private TableService tableService;

    @Override
    @SwiftApi
    public SwiftResultSet query(SwiftDatabase database, String queryJson) {
        try {
            QueryBean queryBean = SwiftContext.get().getBean(QueryBeanFactory.class).create(queryJson);
            if (queryBean instanceof AbstractSingleTableQueryInfoBean) {
                String tableName = ((AbstractSingleTableQueryInfoBean) queryBean).getTableName();
                SwiftMetaData metaData = metaDataService.getMetaDataByKey(tableName);
                SwiftResultSet resultSet = null;
                if (null != metaData && metaData.getSwiftDatabase() == database) {
                    resultSet = getAnalyseService().getQueryResult(queryBean);
                } else {
                    metaData = tableService.detectiveMetaData(database, tableName);
                    ((AbstractSingleTableQueryInfoBean) queryBean).setTableName(metaData.getId());
                    resultSet = getAnalyseService().getQueryResult(queryBean);
                }
                return getPageResultSet(queryJson, resultSet);
            }
            throw new UnsupportedOperationException();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 不管分组还是明细，api返回的都是行结果
    private SerializableDetailResultSet getPageResultSet(String jsonString, SwiftResultSet resultSet) throws SQLException {
        List<Row> rows = new ArrayList<Row>();
        int fetchSize = resultSet.getFetchSize();
        int count = 0;
        while (resultSet.hasNext() && count < fetchSize) {
            rows.add(resultSet.getNextRow());
            count++;
        }
        int rowCount = count;
        if (resultSet instanceof DetailResultSet) {
            rowCount = ((DetailResultSet) resultSet).getRowCount();
        }
        return new SerializableDetailResultSet(jsonString, resultSet.getMetaData(), rows, resultSet.hasNext(), rowCount);
    }

    private AnalyseService getAnalyseService() {
        List<com.fr.swift.service.SwiftService> services = ServiceBeanFactory.getSwiftServiceByNames(Collections.singleton("analyse"));
        if (services.isEmpty()) {
            throw new RuntimeException("Can not find any RealTime service!");
        }
        return (AnalyseService) services.get(0);
    }
}
