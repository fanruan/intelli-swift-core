package com.fr.swift.api.rpc.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.annotation.SwiftApi;
import com.fr.swift.api.result.OnePageApiResultSet;
import com.fr.swift.api.result.SwiftApiResultSet;
import com.fr.swift.api.rpc.SelectService;
import com.fr.swift.api.rpc.TableService;
import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.basics.base.ProxyServiceRegistry;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.query.info.bean.query.AbstractSingleTableQueryInfoBean;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.result.SwiftResultSetUtils;
import com.fr.swift.service.AnalyseService;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/8/23
 */
@ProxyService(value = SelectService.class, type = ProxyService.ServiceType.EXTERNAL)
@SwiftApi
@SwiftBean
public class SelectServiceImpl implements SelectService {
    private SwiftMetaDataService metaDataService = SwiftContext.get().getBean(SwiftMetaDataService.class);
    private TableService tableService = ProxyServiceRegistry.get().getExternalService(TableService.class);

    @Override
    @SwiftApi
    public SwiftApiResultSet query(SwiftDatabase database, String queryJson) {
        try {
            QueryBean queryBean = QueryBeanFactory.create(queryJson);
            if (queryBean instanceof AbstractSingleTableQueryInfoBean) {
                String tableName = queryBean.getTableName();
                SwiftMetaData metaData = metaDataService.getMetaDataByKey(tableName);
                AnalyseService service = ProxySelector.getInstance().getFactory().getProxy(AnalyseService.class);
                SwiftResultSet resultSet = null;
                if (null != metaData && metaData.getSwiftDatabase() == database) {
                    resultSet = SwiftResultSetUtils.toSwiftResultSet(service.getQueryResult(QueryBeanFactory.queryBean2String(queryBean)));
                } else {
                    metaData = tableService.detectiveMetaData(database, tableName);
                    ((AbstractSingleTableQueryInfoBean) queryBean).setTableName(metaData.getId());
                    resultSet = SwiftResultSetUtils.toSwiftResultSet(service.getQueryResult(QueryBeanFactory.queryBean2String(queryBean)));
                }
                return getPageResultSet(resultSet);
            }
            throw new UnsupportedOperationException();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 不管分组还是明细，api返回的都是行结果
    private SwiftApiResultSet getPageResultSet(SwiftResultSet resultSet) throws SQLException {
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
        return new OnePageApiResultSet(null, resultSet.getMetaData(), rows, rowCount, resultSet.hasNext());
    }
}
