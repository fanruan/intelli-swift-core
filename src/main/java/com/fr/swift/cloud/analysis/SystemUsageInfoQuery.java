package com.fr.swift.cloud.analysis;

import com.fr.swift.cloud.result.table.SystemUsageInfo;
import com.fr.swift.cloud.result.table.downtime.DowntimeResult;
import com.fr.swift.query.QueryRunnerProvider;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.AndFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.InFilterBean;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.Row;

import java.util.Arrays;
import java.util.List;

/**
 * This class created on 2019/6/12
 *
 * @author Lucifer
 * @description
 */
public class SystemUsageInfoQuery {

    public SystemUsageInfo query(String appId, String yearMonth, List<DowntimeResult> downtimeResultList) throws Exception {
        FilterInfoBean filter = new AndFilterBean(
                Arrays.<FilterInfoBean>asList(
                        new InFilterBean("appId", appId),
                        new InFilterBean("yearMonth", yearMonth)
                ));

        GroupQueryInfoBean maxTimeBean = GroupQueryInfoBean.builder("real_time_usage")
                .setDimensions(SystemUsageInfo.getDimensions())
                .setAggregations(SystemUsageInfo.getAggregations())
                .setFilter(filter).build();
        SwiftResultSet maxTimeResultSet = QueryRunnerProvider.getInstance().query(QueryBeanFactory.queryBean2String(maxTimeBean));
        Row row = null;
        while (maxTimeResultSet.hasNext()) {
            row = maxTimeResultSet.getNextRow();
            break;
        }
        int downTime = 0;
        int stopTime = 0;
        for (DowntimeResult downtimeResult : downtimeResultList) {
            if (DowntimeResult.SignalName.valueOf(downtimeResult.getPredictDownType()) != DowntimeResult.SignalName.TERM) {
                downTime++;
            }
            stopTime++;
        }
        return new SystemUsageInfo(row, appId, yearMonth, downTime, stopTime);
    }
}
