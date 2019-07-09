package com.fr.swift.cloud.analysis;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.cloud.result.table.PluginUsage;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.QueryRunnerProvider;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.AndFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.InFilterBean;
import com.fr.swift.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.result.SwiftResultSet;

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
@CloudQuery(name = "pluginUsageQuery")
public class PluginUsageQuery extends AbstractSaveQueryResult implements ICloudQuery {

    private final static String TABLE_NAME = PluginUsage.class.getSimpleName();

    public void calculate(String appId, String yearMonth) throws Exception {

        SwiftLoggers.getLogger().info("start PluginUsageQuery analysis task with appId: {}, yearMonth: {}", appId, yearMonth);

        List<PluginUsage> pluginUsageList = new ArrayList<>();
        FilterInfoBean filter = new AndFilterBean(
                Arrays.<FilterInfoBean>asList(
                        new InFilterBean("appId", appId),
                        new InFilterBean("yearMonth", yearMonth)
                ));
        DetailQueryInfoBean bean = DetailQueryInfoBean.builder(PluginUsage.tableName).setDimensions(PluginUsage.getDimensions()).setFilter(filter).build();
        SwiftResultSet resultSet = QueryRunnerProvider.getInstance().query(QueryBeanFactory.queryBean2String(bean));
        while (resultSet.hasNext()) {
            PluginUsage pluginUsage = new PluginUsage(resultSet.getNextRow(), appId, yearMonth);
            pluginUsageList.add(pluginUsage);
        }
        super.saveResult(pluginUsageList);
        SwiftLoggers.getLogger().info("finished PluginUsageQuery analysis task with appId: {}, yearMonth: {}", appId, yearMonth);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }


}
