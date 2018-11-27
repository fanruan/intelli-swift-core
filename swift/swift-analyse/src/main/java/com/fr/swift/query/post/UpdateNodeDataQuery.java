//package com.fr.swift.query.post;
//
//import com.fr.swift.query.result.ResultQuery;
//import com.fr.swift.result.GroupNode;
//import com.fr.swift.result.NodeMergeResultSet;
//import com.fr.swift.result.NodeResultSet;
//import com.fr.swift.result.QueryResultSet;
//import com.fr.swift.result.SwiftNode;
//import com.fr.swift.result.SwiftNodeOperator;
//import com.fr.swift.result.node.GroupNodeUtils;
//import com.fr.swift.result.node.resultset.ChainedNodeResultSet;
//import com.fr.swift.structure.Pair;
//
//import java.sql.SQLException;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by Lyon on 2018/5/31.
// */
//public class UpdateNodeDataQuery extends AbstractPostQuery<QueryResultSet<SwiftNode>> {
//
//    private ResultQuery<NodeResultSet> query;
//
//    public UpdateNodeDataQuery(ResultQuery<NodeResultSet> query) {
//        this.query = query;
//    }
//
//    @Override
//    public QueryResultSet<SwiftNode> getQueryResult() throws SQLException {
//        SwiftNodeOperator operator = new SwiftNodeOperator() {
//            @Override
//            public Pair<SwiftNode, List<Map<Integer, Object>>> apply(Pair<? extends SwiftNode, List<Map<Integer, Object>>> p) {
//                GroupNodeUtils.updateNodeData((GroupNode) p.getKey(), p.getValue());
//                return Pair.of(p.getKey(), p.getValue());
//            }
//        };
//        NodeMergeResultSet mergeResult = (NodeMergeResultSet) query.getQueryResult();
//        // TODO: 2018/11/27
//        return (QueryResultSet<SwiftNode>) new ChainedNodeResultSet(operator, mergeResult);
//    }
//}
