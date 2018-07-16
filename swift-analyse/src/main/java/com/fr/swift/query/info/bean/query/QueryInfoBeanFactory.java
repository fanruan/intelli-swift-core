package com.fr.swift.query.info.bean.query;

import com.fr.swift.query.info.ResultJoinQueryInfo;
import com.fr.swift.query.info.bean.factory.DimensionBeanFactory;
import com.fr.swift.query.info.bean.factory.FilterInfoBeanFactory;
import com.fr.swift.query.info.bean.factory.MetricBeanFactory;
import com.fr.swift.query.info.bean.factory.PostQueryInfoFactory;
import com.fr.swift.query.info.bean.factory.SortBeanFactory;
import com.fr.swift.query.info.detail.DetailQueryInfo;
import com.fr.swift.query.info.element.metric.Metric;
import com.fr.swift.query.info.group.GroupQueryInfoImpl;
import com.fr.swift.query.query.QueryInfo;
import com.fr.swift.source.SwiftMetaData;
import com.fr.third.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lyon
 * @date 2018/6/7
 */
public class QueryInfoBeanFactory {

    private static final DimensionBeanFactory DIMENSION_BEAN_FACTORY = DimensionBeanFactory.getInstance();
    private static final SortBeanFactory SORT_BEAN_FACTORY = SortBeanFactory.getInstance();
    private static final MetricBeanFactory METRIC_BEAN_FACTORY = MetricBeanFactory.getInstance();
    private static final PostQueryInfoFactory POST_QUERY_INFO_FACTORY = PostQueryInfoFactory.getInstance();
    private static ObjectMapper MAPPER = new ObjectMapper();

    public static QueryInfoBean create(URL url) throws IOException {
        return MAPPER.readValue(url, QueryInfoBean.class);
    }

    public static QueryInfoBean create(String jsonString) throws IOException {
        return MAPPER.readValue(jsonString, QueryInfoBean.class);
    }

    private static List<QueryInfoBean> create(List<QueryInfo> queryInfo) {
        List<QueryInfoBean> result = new ArrayList<QueryInfoBean>();
        if (null != queryInfo) {
            for (QueryInfo info : queryInfo) {
                result.add(create(info));
            }
        }
        return result;
    }

    // TODO: 2018/6/28 bean结构完善之后，queryInfo到queryInfoBean的转换应该不需要了
    @Deprecated
    public static QueryInfoBean create(QueryInfo queryInfo) {
        switch (queryInfo.getType()) {
            case GROUP:
            case LOCAL_GROUP_ALL:
            case LOCAL_GROUP_PART:
            case CROSS_GROUP:
                GroupQueryInfoImpl groupQueryInfo = (GroupQueryInfoImpl) queryInfo;
                List<Metric> groupMetricList = groupQueryInfo.getMetrics();
                GroupQueryInfoBean groupQueryBean = new GroupQueryInfoBean();
                groupQueryBean.setTableName(groupQueryInfo.getTable().getId());
                groupQueryBean.setQueryId(queryInfo.getQueryId());
                groupQueryBean.setDimensionBeans(DIMENSION_BEAN_FACTORY.create(groupQueryInfo.getDimensions()));
                groupQueryBean.setFilterInfoBean(FilterInfoBeanFactory.SINGLE_FILTER_INFO_BEAN_FACTORY.create(groupQueryInfo.getFilterInfo()));
                groupQueryBean.setQuerySegment(queryInfo.getQuerySegment());
                groupQueryBean.setMetricBeans(METRIC_BEAN_FACTORY.create(groupMetricList));
                groupQueryBean.setPostQueryInfoBeans(POST_QUERY_INFO_FACTORY.create(groupQueryInfo.getPostQueryInfoList()));
                groupQueryBean.setQueryType(queryInfo.getType());
                return groupQueryBean;
            case RESULT_JOIN:
                ResultJoinQueryInfoBean resultJoinQueryBean = new ResultJoinQueryInfoBean();
                resultJoinQueryBean.setQueryId(queryInfo.getQueryId());
                resultJoinQueryBean.setPostQueryInfoBeans(POST_QUERY_INFO_FACTORY.create(((ResultJoinQueryInfo) queryInfo).getPostQueryInfoList()));
                resultJoinQueryBean.setQueryInfoBeans(create(((ResultJoinQueryInfo) queryInfo).getQueryInfoList()));
                resultJoinQueryBean.setJoinedFields(DIMENSION_BEAN_FACTORY.create(((ResultJoinQueryInfo) queryInfo).getJoinedDimensions()));
                resultJoinQueryBean.setQueryType(queryInfo.getType());
                resultJoinQueryBean.setQuerySegment(queryInfo.getQuerySegment());
                return resultJoinQueryBean;
            case DETAIL:
            case LOCAL_DETAIL:
                DetailQueryInfoBean detailQueryBean = new DetailQueryInfoBean();
                detailQueryBean.setQueryId(queryInfo.getQueryId());
                detailQueryBean.setTableName(((DetailQueryInfo) queryInfo).getTable().getId());
                detailQueryBean.setDimensionBeans(DIMENSION_BEAN_FACTORY.create(((DetailQueryInfo) queryInfo).getDimensions()));
                detailQueryBean.setFilterInfoBean(FilterInfoBeanFactory.SINGLE_FILTER_INFO_BEAN_FACTORY.create(((DetailQueryInfo) queryInfo).getFilterInfo()));
                detailQueryBean.setSortBeans(SORT_BEAN_FACTORY.create(((DetailQueryInfo) queryInfo).getSorts()));
                SwiftMetaData metaData = ((DetailQueryInfo) queryInfo).getMetaData();
                detailQueryBean.setColumns(metaData.getFieldNames());
                detailQueryBean.setQuerySegment(queryInfo.getQuerySegment());
                detailQueryBean.setQueryType(queryInfo.getType());
                return detailQueryBean;
            case NEST:
            default:
                return null;
        }
    }
}
