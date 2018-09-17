package com.fr.swift.result.serialize;

import com.fr.swift.query.query.QueryType;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;

/**
 * Created by Lyon on 2018/6/27.
 */
public class SwiftResultSetUtils {

    public static SwiftResultSet convert2Serializable(String jsonString, QueryType queryType,
                                                      SwiftResultSet resultSet) throws SQLException {
        SwiftResultSet result;
        switch (queryType) {
            case LOCAL_GROUP_ALL: {
                NodeResultSet<SwiftNode> nodeResultSet = (NodeResultSet<SwiftNode>) resultSet;
                result = new LocalAllNodeResultSet(nodeResultSet.getFetchSize(), jsonString, nodeResultSet.getPage().getKey(), nodeResultSet.hasNextPage());
                break;
            }
            case LOCAL_GROUP_PART: {
                NodeMergeResultSet mergeResultSet = (NodeMergeResultSet) resultSet;
                result = new LocalPartNodeResultSet(mergeResultSet.getFetchSize(), jsonString, mergeResultSet.getPage(), mergeResultSet.hasNextPage());
                break;
            }
            case LOCAL_DETAIL: {
                DetailResultSet detailResultSet = (DetailResultSet) resultSet;
                result = new SerializableDetailResultSet(jsonString, detailResultSet.getMetaData(), detailResultSet.getPage(),
                        detailResultSet.hasNextPage(), detailResultSet.getRowCount());
                break;
            }
            default:
                result = resultSet;
        }
        return result;
    }
}
