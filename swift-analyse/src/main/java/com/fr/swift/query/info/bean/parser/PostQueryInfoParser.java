package com.fr.swift.query.info.bean.parser;

import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.filter.FilterBuilder;
import com.fr.swift.query.filter.match.DetailBasedMatchFilter;
import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.query.info.bean.element.AggregationBean;
import com.fr.swift.query.info.bean.element.CalculatedFieldBean;
import com.fr.swift.query.info.bean.element.SortBean;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.post.CalculatedFieldQueryInfoBean;
import com.fr.swift.query.info.bean.post.HavingFilterQueryInfoBean;
import com.fr.swift.query.info.bean.post.PostQueryInfoBean;
import com.fr.swift.query.info.bean.post.RowSortQueryInfoBean;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.query.info.bean.type.PostQueryType;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.info.element.target.GroupTarget;
import com.fr.swift.query.info.funnel.FunnelPathsAggregationBean;
import com.fr.swift.query.info.group.post.CalculatedFieldQueryInfo;
import com.fr.swift.query.info.group.post.FunnelPostQueryInfo;
import com.fr.swift.query.info.group.post.HavingFilterQueryInfo;
import com.fr.swift.query.info.group.post.PostQueryInfo;
import com.fr.swift.query.info.group.post.RowSortQueryInfo;
import com.fr.swift.query.sort.AscSort;
import com.fr.swift.query.sort.DescSort;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.source.SourceKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/6/7.
 */
class PostQueryInfoParser {

    static List<PostQueryInfo> parse(SourceKey table, GroupQueryInfoBean queryInfoBean,
                                     List<Dimension> dimensions) {
        List<PostQueryInfoBean> postAggregations = queryInfoBean.getPostAggregations();
        List<AggregationBean> aggregations = queryInfoBean.getAggregations();
        Map<String, Integer> fieldIndexMap = getFieldIndexMap(postAggregations, aggregations);
        List<PostQueryInfo> postQueryInfoList = new ArrayList<PostQueryInfo>();
        for (PostQueryInfoBean bean : postAggregations) {
            postQueryInfoList.add(parse(table, dimensions.size(), bean, fieldIndexMap, aggregations));
        }
        return postQueryInfoList;
    }

    /**
     * 取出所有指标字段的IndexMap，当前计算指标字段的依赖关系只能是后面依赖前面（客户端写查询信息的时候要注意）
     */
    private static Map<String, Integer> getFieldIndexMap(List<PostQueryInfoBean> postQueryInfoBeans, List<AggregationBean> metrics) {
        Map<String, Integer> fieldIndexMap = new HashMap<String, Integer>();
        for (AggregationBean metric : metrics) {
            fieldIndexMap.put(metric.getAlias() == null ? metric.getColumn() : metric.getAlias(), fieldIndexMap.size());
        }
        for (PostQueryInfoBean postQueryInfoBean : postQueryInfoBeans) {
            if (postQueryInfoBean.getType() != PostQueryType.CAL_FIELD) {
                continue;
            }
            // 新增列
            CalculatedFieldBean calculatedFieldBean = ((CalculatedFieldQueryInfoBean) postQueryInfoBean).getCalField();
            fieldIndexMap.put(calculatedFieldBean.getName(), fieldIndexMap.size());
        }
        return fieldIndexMap;
    }

    private static PostQueryInfo parse(SourceKey table, int dimensionSize,
                                       PostQueryInfoBean bean, Map<String, Integer> fieldIndexMap, List<AggregationBean> aggregations) {
        PostQueryType type = bean.getType();
        switch (type) {
            case CAL_FIELD:
                GroupTarget targets = CalculatedFieldParser.parse(((CalculatedFieldQueryInfoBean) bean).getCalField(), fieldIndexMap);
                return new CalculatedFieldQueryInfo(targets);
            case ROW_SORT:
                return parseRowSortQueryInfo(dimensionSize, (RowSortQueryInfoBean) bean, fieldIndexMap);
            case HAVING_FILTER: {
                List<MatchFilter> filters = new ArrayList<MatchFilter>();
                for (int i = 0; i < dimensionSize; i++) {
                    filters.add(null);
                }
                FilterInfoBean filterInfoBean = ((HavingFilterQueryInfoBean) bean).getFilter();
                MatchFilter filter = new DetailBasedMatchFilter(fieldIndexMap.get(((HavingFilterQueryInfoBean) bean).getColumn()),
                        FilterBuilder.buildDetailFilter(null, FilterInfoParser.parse(table, filterInfoBean)));
                if (!filters.isEmpty()) {
                    filters.set(filters.size() - 1, filter);
                } else {
                    // 表示没有分组，对聚合做过滤  虽然没什么用  有点zz
                    filters.add(filter);
                }
                return new HavingFilterQueryInfo(filters);
            }
            case FUNNEL_TIME_AVG:
            case FUNNEL_CONVERSION_RATE:
            case FUNNEL_TIME_MEDIAN:
                for (AggregationBean aggregation : aggregations) {
                    AggregatorType aggregationType = aggregation.getType();
                    if (aggregationType == AggregatorType.FUNNEL || aggregationType == AggregatorType.FUNNEL_PATHS) {
                        FunnelPostQueryInfo postInfo = new FunnelPostQueryInfo(((FunnelPathsAggregationBean) aggregation).getTimeWindow());
                        postInfo.setType(type);
                        return postInfo;
                    }
                }
            default:
                return null;
        }
    }

    private static PostQueryInfo parseRowSortQueryInfo(int dimensionSize, RowSortQueryInfoBean bean,
                                                       Map<String, Integer> fieldIndexMap) {
        List<Sort> sorts = new ArrayList<Sort>();
        List<SortBean> sortBeans = bean.getSortBeans();
        if (null != sortBeans) {
            for (SortBean sortBean : sortBeans) {
                // 这边复用之前排序的代码，所以targetIndex要算上维度
                int targetIndex = dimensionSize + fieldIndexMap.get(sortBean.getName());
                sorts.add(sortBean.getType() == SortType.ASC ? new AscSort(targetIndex) : new DescSort(targetIndex));
            }
        }
        return new RowSortQueryInfo(sorts);
    }

    /**
     * 解析resultJoinQuery的PostQueryInfo
     *
     * @param postQueryInfoBeans
     * @param dimensions
     * @param queryInfoBeans
     * @return
     */
//    static List<PostQueryInfo> parsePostQueryInfoOfResultJoinQuery(List<PostQueryInfoBean> postQueryInfoBeans,
//                                                                   List<Dimension> dimensions, List<QueryInfoBean> queryInfoBeans) {
//        Map<String, Integer> fieldIndexMap = getFieldIndexMapOfResultJoinQueryInfo(postQueryInfoBeans, queryInfoBeans);
//        List<PostQueryInfo> postQueryInfoList = new ArrayList<PostQueryInfo>();
//        for (PostQueryInfoBean bean : postQueryInfoBeans) {
//            postQueryInfoList.add(parse(null, dimensions.size(), bean, fieldIndexMap));
//        }
//        return postQueryInfoList;
//    }

//    private static Map<String, Integer> getFieldIndexMapOfResultJoinQueryInfo(List<PostQueryInfoBean> postQueryInfoBeans,
//                                                                              List<QueryInfoBean> queryBeans) {
//        Map<String, Integer> fieldIndexMap = new HashMap<String, Integer>();
//        for (QueryInfoBean queryBean : queryBeans) {
//            // TODO: 2018/6/8 这边都是假定groupQuery
//            List<MetricBean> metricBeans = ((GroupQueryInfoBean) queryBean).getAggregations();
//            for (MetricBean metricBean : metricBeans) {
//                fieldIndexMap.put(metricBean.getColumn(), fieldIndexMap.size());
//            }
//            List<PostQueryInfoBean> queryInfoBeans = ((GroupQueryInfoBean) queryBean).getPostAggregations();
//            for (PostQueryInfoBean queryInfoBean : queryInfoBeans) {
//                if (queryInfoBean.getType() != PostQueryType.CAL_FIELD) {
//                    continue;
//                }
//                CalculatedFieldBean calculatedFieldBean = ((CalculatedFieldQueryInfoBean) queryBean).getCalField();
//                fieldIndexMap.put(calculatedFieldBean.getName(), fieldIndexMap.size());
//            }
//        }
//        for (PostQueryInfoBean postQueryInfoBean : postQueryInfoBeans) {
//            if (postQueryInfoBean.getType() != PostQueryType.CAL_FIELD) {
//                continue;
//            }
//            CalculatedFieldBean calculatedFieldBean = ((CalculatedFieldQueryInfoBean) postQueryInfoBean).getCalField();
//            fieldIndexMap.put(calculatedFieldBean.getName(), fieldIndexMap.size());
//        }
//        return fieldIndexMap;
//    }
}
