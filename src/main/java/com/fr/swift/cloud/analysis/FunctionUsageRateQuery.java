package com.fr.swift.cloud.analysis;

import com.fr.swift.cloud.result.table.FunctionUsageRate;
import com.fr.swift.query.QueryRunnerProvider;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.AndFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.InFilterBean;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.Row;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class created on 2019/6/12
 *
 * @author Lucifer
 * @description
 */
public class FunctionUsageRateQuery {

    public List<FunctionUsageRate> query(String appId, String yearMonth) throws Exception {
        List<FunctionUsageRate> functionUsageRateList = new ArrayList<>();
        FilterInfoBean filter = new AndFilterBean(
                Arrays.<FilterInfoBean>asList(
                        new InFilterBean("appId", appId),
                        new InFilterBean("yearMonth", yearMonth)
                ));
        GroupQueryInfoBean bean = GroupQueryInfoBean.builder(FunctionUsageRate.tableName)
                .setDimensions(FunctionUsageRate.getDimensions())
                .setAggregations(FunctionUsageRate.getAggregations()).setFilter(filter).build();
        SwiftResultSet resultSet = QueryRunnerProvider.getInstance().query(QueryBeanFactory.queryBean2String(bean));
        while (resultSet.hasNext()) {
            Row row = resultSet.getNextRow();
            FunctionUsageRate usageRate = new FunctionUsageRate(row, appId, yearMonth);
            functionUsageRateList.add(usageRate);
        }
        return functionUsageRateList;
    }
}
