package com.fr.swift.query.info.bean.query;

import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.element.FilterInfoBean;
import com.fr.swift.query.info.bean.element.SortBean;
import com.fr.swift.query.info.detail.DetailQueryInfo;
import com.fr.swift.query.info.element.metric.Metric;
import com.fr.swift.query.info.group.GroupQueryInfoImpl;
import com.fr.swift.query.query.QueryInfo;
import com.fr.third.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2018/6/7.
 */
public class QueryBeanFactory {

    private static ObjectMapper MAPPER = new ObjectMapper();

    public static QueryBean create(URL url) throws IOException {
        return MAPPER.readValue(url, QueryBean.class);
    }

    public static QueryBean create(String jsonString) throws IOException {
        return MAPPER.readValue(jsonString, QueryBean.class);
    }

    public static QueryBean create(QueryInfo queryInfo) {
        // TODO 处理其他属性
        switch (queryInfo.getType()) {
            case GROUP:
            case LOCAL_GROUP_ALL:
            case LOCAL_GROUP_PART:
            case CROSS_GROUP:
                GroupQueryInfoImpl groupQueryInfo = (GroupQueryInfoImpl) queryInfo;
                List<Metric> groupMetricList = groupQueryInfo.getMetrics();
                GroupQueryBean groupQueryBean = new GroupQueryBean();
                groupQueryBean.setTableName(groupQueryInfo.getTable().getId());
                groupQueryBean.setQueryId(queryInfo.getQueryId());
                return groupQueryBean;
            case RESULT_JOIN:
                ResultJoinQueryBean resultJoinQueryBean = new ResultJoinQueryBean();
                resultJoinQueryBean.setQueryId(queryInfo.getQueryId());
                return resultJoinQueryBean;
            case DETAIL:
            case LOCAL_DETAIL:
                DetailQueryBean detailQueryBean = new DetailQueryBean();
                detailQueryBean.setQueryId(queryInfo.getQueryId());
                detailQueryBean.setTableName(((DetailQueryInfo) queryInfo).getTable().getId());
                detailQueryBean.setDimensionBeans(new ArrayList<DimensionBean>());
                detailQueryBean.setFilterInfoBean(new FilterInfoBean());
                detailQueryBean.setSortBeans(new ArrayList<SortBean>());
                return detailQueryBean;
            case NEST:
        }
        return null;
    }
}
