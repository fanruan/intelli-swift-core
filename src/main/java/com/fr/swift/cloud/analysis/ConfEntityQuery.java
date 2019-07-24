package com.fr.swift.cloud.analysis;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.cloud.result.table.ConfEntity;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2019/6/12
 *
 * @author Lucifer
 * @description
 */
@SwiftBean
@CloudQuery(name = "confEntityQuery", tables = {"conf_entity"})
public class ConfEntityQuery extends AbstractSaveQueryResult implements ICloudQuery {

    public void queryAndSave(String appId, String yearMonth) throws Exception {
        SwiftLoggers.getLogger().info("start ConfEntityQuery analysis task with appId: {}, yearMonth: {}", appId, yearMonth);

        FilterInfoBean filter = new AndFilterBean(
                Arrays.<FilterInfoBean>asList(
                        new InFilterBean("appId", appId),
                        new InFilterBean("yearMonth", yearMonth)
                ));

        DetailQueryInfoBean bean = DetailQueryInfoBean.builder(ConfEntity.tableName).setDimensions(ConfEntity.getDimensions()).setFilter(filter).build();
        SwiftResultSet resultSet = QueryRunnerProvider.getInstance().query(QueryBeanFactory.queryBean2String(bean));
        Map<String, ConfEntity> map = new HashMap<>();
        while (resultSet.hasNext()) {
            ConfEntity confEntity = new ConfEntity(resultSet.getNextRow(), appId, yearMonth);
            if (!map.containsKey(confEntity.getId())) {
                map.put(confEntity.getId(), confEntity);
            } else {
                if (map.get(confEntity.getId()).getTime() < confEntity.getTime()) {
                    map.put(confEntity.getId(), confEntity);
                }
            }
        }
        List<ConfEntity> confEntityList = new ArrayList<>(map.values());
        super.saveResult(confEntityList);

        SwiftLoggers.getLogger().info("finished ConfEntityQuery analysis task with appId: {}, yearMonth: {}", appId, yearMonth);

    }
}