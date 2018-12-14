//package com.fr.swift.query.post;
//
//import com.fr.swift.query.post.meta.SwiftMetaDataUtils;
//import com.fr.swift.query.query.Query;
//import com.fr.swift.query.query.QueryBean;
//import com.fr.swift.query.query.QueryType;
//import com.fr.swift.query.result.serialize.LocalAllNodeResultSet;
//import com.fr.swift.result.DetailResultSet;
//import com.fr.swift.result.NodeResultSet;
//import com.fr.swift.result.node.resultset.Node2RowResultSet;
//import com.fr.swift.source.SwiftMetaData;
//import com.fr.swift.result.SwiftResultSet;
//
//import java.sql.SQLException;
//
///**
// * Created by Lyon on 2018/6/1.
// */
//public class PrepareMetaDataQuery<T extends SwiftResultSet> extends AbstractPostQuery<T> {
//
//    private Query<T> query;
//    private QueryBean bean;
//
//    public PrepareMetaDataQuery(Query<T> query, QueryBean bean) {
//        this.query = query;
//        this.bean = bean;
//    }
//
//    @Override
//    public T getQueryResult() throws SQLException {
//        SwiftMetaData metaData = SwiftMetaDataUtils.createMetaData(bean);
//        T resultSet = query.getQueryResult();
//        if (bean.getQueryType() == QueryType.DETAIL || bean.getQueryType() == QueryType.LOCAL_DETAIL) {
//            ((DetailResultSet) resultSet).setMetaData(metaData);
//            return resultSet;
//        } else if (resultSet instanceof LocalAllNodeResultSet) {
//            // TODO: 2018/10/8 resultSet内部更新下一页数据（包括本地和远程）可能需要重新设计一下
//            // 需要考虑处理一些比较特殊的resultSet，比如分组结果按行排序之类的
//            ((LocalAllNodeResultSet) resultSet).setMetaData(metaData);
//            return resultSet;
//        } else {
//            return (T) new Node2RowResultSet((NodeResultSet) resultSet, metaData);
//        }
//    }
//}
