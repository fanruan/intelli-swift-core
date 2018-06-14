package com.fr.swift.query.info.bean.parser;

import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.info.bean.query.DetailQueryBean;
import com.fr.swift.query.info.bean.query.GroupQueryBean;
import com.fr.swift.query.info.bean.query.QueryBean;
import com.fr.swift.query.info.bean.query.ResultJoinQueryBean;
import com.fr.swift.query.info.detail.DetailQueryInfo;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.info.element.metric.Metric;
import com.fr.swift.query.info.group.GroupQueryInfoImpl;
import com.fr.swift.query.info.group.ResultJoinQueryInfoImpl;
import com.fr.swift.query.info.group.post.PostQueryInfo;
import com.fr.swift.query.query.QueryInfo;
import com.fr.swift.query.query.QueryType;
import com.fr.swift.source.SourceKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2018/6/7.
 */
public class QueryInfoParser {

    public static QueryInfo parse(QueryBean queryBean) {
        QueryType type = queryBean.getQueryType();
        switch (type) {
            case GROUP:
                return parseGroupQueryInfo((GroupQueryBean) queryBean);
            case RESULT_JOIN:
                return parseResultJoinQueryInfo((ResultJoinQueryBean) queryBean);
            default:
                return parseDetailQueryInfo((DetailQueryBean) queryBean);
        }
    }

    private static QueryInfo parseGroupQueryInfo(GroupQueryBean bean) {
        String queryId = bean.getQueryId();
        // TODO: 2018/6/7 table2sourceKey
        SourceKey table = new SourceKey(bean.getTableName());
        FilterInfo filterInfo = FilterInfoParser.parse(bean.getFilterInfoBean());
        List<Dimension> dimensions = DimensionParser.parse(bean.getDimensionBeans(), bean.getSortBeans());
        List<Metric> metrics = MetricParser.parse(bean.getMetricBeans());
        List<PostQueryInfo> postQueryInfoList = PostQueryInfoParser.parse(bean.getPostQueryInfoBeans(), dimensions, metrics);
        return new GroupQueryInfoImpl(queryId, table, filterInfo, dimensions, metrics, postQueryInfoList);
    }

    private static QueryInfo parseResultJoinQueryInfo(ResultJoinQueryBean bean) {
        String queryId = bean.getQueryId();
        List<QueryBean> queryBeans = bean.getQueryBeans();
        List<QueryInfo> queryInfoList = new ArrayList<QueryInfo>();
        for (QueryBean queryBean : queryBeans) {
            queryInfoList.add(parse(queryBean));
        }
        List<Dimension> dimensions = DimensionParser.parse(bean.getJoinedFields());
        List<PostQueryInfo> postQueryInfoList = PostQueryInfoParser.parsePostQueryInfoOfResultJoinQuery(
                bean.getPostQueryInfoBeans(), dimensions, queryBeans);
        return new ResultJoinQueryInfoImpl(queryId, queryInfoList, dimensions, postQueryInfoList);
    }

    private static QueryInfo parseDetailQueryInfo(DetailQueryBean bean) {
        String queryId = bean.getQueryId();
        // TODO: 2018/6/7
        SourceKey table = new SourceKey(bean.getTableName());
        FilterInfo filterInfo = FilterInfoParser.parse(bean.getFilterInfoBean());
        List<Dimension> dimensions = DimensionParser.parse(bean.getDimensionBeans(), bean.getSortBeans());
        return new DetailQueryInfo(queryId, table, filterInfo, dimensions);
    }
}
