package com.fr.swift.query.result.serialize;

import com.fr.swift.query.query.QueryType;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.source.Row;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2018/6/27.
 */
public class SwiftResultSetUtils {

    public static QueryResultSet convert2Serializable(String jsonString, QueryType queryType,
                                                      SwiftResultSet resultSet) throws SQLException {
        QueryResultSet result = null;
//        switch (queryType) {
//            case LOCAL_GROUP_ALL: {
//                NodeResultSet<SwiftNode> nodeResultSet = (NodeResultSet<SwiftNode>) resultSet;
//                result = new LocalAllNodeResultSet(nodeResultSet.getFetchSize(), jsonString, getPage(nodeResultSet), nodeResultSet.hasNext());
//                break;
//            }
//            case LOCAL_GROUP_PART: {
//                NodeMergeResultSet mergeResultSet = (NodeMergeResultSet) resultSet;
//                result = new LocalPartNodeResultSet(mergeResultSet.getFetchSize(), jsonString, mergeResultSet.getPage(), mergeResultSet.hasNextPage());
//                break;
//            }
//            case LOCAL_DETAIL: {
//                DetailResultSet detailResultSet = (DetailResultSet) resultSet;
//                result = new SerializableDetailResultSet(jsonString, detailResultSet.getMetaData(), detailResultSet.getPage(),
//                        detailResultSet.hasNextPage(), detailResultSet.getRowCount());
//                break;
//            }
//            default:
//                result = resultSet;
//        }
        return result;
    }

    private static List<Row> getPage(NodeResultSet resultSet) throws SQLException {
        List<Row> rows = new ArrayList<Row>();
        int fetchSize = resultSet.getFetchSize();
        for (int i = 0; i < fetchSize && resultSet.hasNext(); i++) {
            rows.add(resultSet.getNextRow());
        }
        return rows;
    }
}
