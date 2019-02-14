//package com.fr.swift.query.builder;
//
//import com.fr.swift.query.info.ResultJoinQueryInfo;
//import com.fr.swift.query.info.bean.parser.QueryInfoParser;
//import com.fr.swift.query.info.bean.query.QueryInfoBean;
//import com.fr.swift.query.info.bean.query.ResultJoinQueryInfoBean;
//import com.fr.swift.query.post.PostQuery;
//import com.fr.swift.query.post.ResultJoinQuery;
//import com.fr.swift.query.query.Query;
//import com.fr.swift.query.query.QueryType;
//import com.fr.swift.result.NodeResultSet;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by Lyon on 2018/5/31.
// */
//class ResultJoinQueryBuilder {
//
//    static Query<NodeResultSet> buildQuery(ResultJoinQueryInfoBean bean) throws Exception {
//        List<QueryInfoBean> infoList = bean.getQueryInfoBeans();
//        List<Query<NodeResultSet>> queries = new ArrayList<Query<NodeResultSet>>();
//        for (QueryInfoBean queryInfo : infoList) {
//            // 限制都是group类型的查询，各个group查询的维度的排序都是索引默认的PINYIN_ASC
//            assert queryInfo.getQueryType() == QueryType.GROUP;
//            queries.add((Query) QueryBuilder.buildAllQuery(queryInfo));
//        }
//        ResultJoinQueryInfo info = (ResultJoinQueryInfo) QueryInfoParser.parse(bean);
//        PostQuery<NodeResultSet> tmpQuery = new ResultJoinQuery(queries, info.getJoinedDimensions());
//        tmpQuery = PostQueryBuilder.buildQuery(tmpQuery, info.getPostQueryInfoList());
//        // TODO: 2018/6/7 更新metadata
//        return tmpQuery;
//    }
//}
