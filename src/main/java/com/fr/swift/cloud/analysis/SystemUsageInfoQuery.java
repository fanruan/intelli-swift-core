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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class created on 2019/6/12
 *
 * @author Lucifer
 * @description
 */
@SwiftBean
@CloudQuery(name = "systemUsageInfoQuery")
public class SystemUsageInfoQuery extends AbstractSaveQueryResult implements ICloudQuery {

    private final static String TABLE_NAME = SystemUsageInfo.class.getSimpleName();

    public void calculate(String appId, String yearMonth) throws Exception {

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
        SystemUsageInfo systemUsageInfo = new SystemUsageInfo(row, appId, yearMonth, downTime, stopTime);
        List<SystemUsageInfo> systemUsageInfoList = Collections.singletonList(systemUsageInfo);
        super.saveResult(systemUsageInfoList);

        SwiftLoggers.getLogger().info("finished SystemUsageInfoQuery analysis task with appId: {}, yearMonth: {}", appId, yearMonth);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }


}
