package com.fr.swift.cloud.analysis;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.cloud.analysis.downtime.DowntimeAnalyser;
import com.fr.swift.cloud.result.table.SystemUsageInfo;
import com.fr.swift.cloud.result.table.downtime.DowntimeResult;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.QueryRunnerProvider;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.AndFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.InFilterBean;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.Row;
import com.fr.swift.util.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class created on 2019/6/12
 *
 * @author Lucifer
 * @description
 */
@SwiftBean
@CloudQuery(name = "systemUsageInfoQuery", tables = {"system_usage_info", "downtime_result", "downtime_execution_result"})
public class SystemUsageInfoQuery extends AbstractSaveQueryResult implements ICloudQuery {

    public void queryAndSave(String appId, String yearMonth) throws Exception {

        SwiftLoggers.getLogger().info("start SystemUsageInfoQuery analysis task with appId: {}, yearMonth: {}", appId, yearMonth);

        List<DowntimeResult> downtimeResultList = new ArrayList<>();
        downtimeResultList.addAll(new DowntimeAnalyser().downtimeAnalyse(appId, yearMonth));

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
        List<Row> rowList = new ArrayList<>();
        while (maxTimeResultSet.hasNext()) {
            rowList.add(maxTimeResultSet.getNextRow());
        }

        List<SystemUsageInfo> systemUsageInfoList = new ArrayList<>();
        if (rowList.isEmpty()) {
            rowList.add(null);
        }
        for (Row row : rowList) {
            String node = Strings.EMPTY;
            if (row != null) {
                node = row.getValue(0) == null ? Strings.EMPTY : row.getValue(0);
            }
            int downTime = 0;
            int stopTime = 0;
            for (DowntimeResult downtimeResult : downtimeResultList) {
                if (node.equals(downtimeResult.getNode()) || node.equals(Strings.EMPTY)) {
                    if (DowntimeResult.SignalName.valueOf(downtimeResult.getPredictDownType()) != DowntimeResult.SignalName.TERM) {
                        downTime++;
                    }
                    stopTime++;
                }
            }
            systemUsageInfoList.add(new SystemUsageInfo(row, appId, yearMonth, downTime, stopTime));
        }
        super.saveResult(systemUsageInfoList);
        SwiftLoggers.getLogger().info("finished SystemUsageInfoQuery analysis task with appId: {}, yearMonth: {}", appId, yearMonth);
    }
}