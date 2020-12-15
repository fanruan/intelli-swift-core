package com.fr.swift.query.builder;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.filter.FilterBuilder;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.group.info.IndexInfo;
import com.fr.swift.query.info.bean.parser.QueryInfoParser;
import com.fr.swift.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.query.info.detail.DetailQueryInfo;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.limit.Limit;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.result.detail.DetailResultQuery;
import com.fr.swift.query.segment.detail.DetailSegmentQuery;
import com.fr.swift.result.DetailQueryResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2017/12/13.
 */
class DetailQueryBuilder extends BaseQueryBuilder {


    DetailQueryInfo detailQueryInfo;

    DetailQueryBuilder(DetailQueryInfo detailQueryInfo) {
        this.detailQueryInfo = detailQueryInfo;
    }

    static DetailQueryBuilder of(DetailQueryInfoBean detailQueryInfoBean) {
        DetailQueryInfo queryInfo = (DetailQueryInfo) QueryInfoParser.parse(detailQueryInfoBean);
        return queryInfo.hasSort() ?
                new SortedDetailQueryBuilder(queryInfo) :
                new DetailQueryBuilder(queryInfo);
    }

    /**
     * 给最外层查询节点（查询服务节点）条用并构建query，根据segment分布信息区分本地query和远程query
     */
    Query<DetailQueryResultSet> buildQuery() throws SwiftMetaDataException {
        List<Query<DetailQueryResultSet>> queries = new ArrayList<Query<DetailQueryResultSet>>();
        //根据分开规则提前区分segment
        List<Segment> segments = filterQuerySegs(detailQueryInfo);
        List<Dimension> dimensions = detailQueryInfo.getDimensions();
        for (Segment seg : segments) {
            try {
                List<FilterInfo> filterInfos = new ArrayList<FilterInfo>();
                List<Pair<Column, IndexInfo>> columns = getDimensionSegments(seg, dimensions);
                if (detailQueryInfo.getFilterInfo() != null) {
                    filterInfos.add(detailQueryInfo.getFilterInfo());
                }
                queries.add(getSegmentQuery(seg, columns, filterInfos, detailQueryInfo.getLimit()));
            } catch (Exception ignore) {
                SwiftLoggers.getLogger().error(ignore);
            }
        }
        return getResultQuery(queries);
    }


    /**
     * 子类SortedDetailQueryBuilder会重写此方法
     *
     * @param seg         seg
     * @param columns     columns
     * @param filterInfos filterInfos
     * @return segment query
     */
    Query<DetailQueryResultSet> getSegmentQuery(Segment seg, List<Pair<Column, IndexInfo>> columns, List<FilterInfo> filterInfos, Limit limit) {
        return new DetailSegmentQuery(
                detailQueryInfo.getFetchSize(),
                columns,
                FilterBuilder.buildDetailFilter(seg, new GeneralFilterInfo(filterInfos, GeneralFilterInfo.AND)),
                limit);
    }

    /**
     * 子类SortedDetailQueryBuilder会重写此方法
     *
     * @param queries all segment queries
     * @return merged result query
     */
    Query<DetailQueryResultSet> getResultQuery(List<Query<DetailQueryResultSet>> queries) {
        return new DetailResultQuery(detailQueryInfo.getFetchSize(), queries);
    }


}
