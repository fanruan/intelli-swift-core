package com.fr.swift.result.serialize;

import com.fr.swift.query.query.QueryType;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;

/**
 * Created by Lyon on 2018/6/27.
 */
public class SwiftResultSetUtils {

    public static SwiftResultSet convert2Serializable(String queryId, QueryType queryType,
                                                      SwiftResultSet resultSet) throws SQLException {
        SerializableResultSet result;
        switch (queryType) {
            case LOCAL_GROUP_ALL:
                result = new LocalAllNodeResultSet(queryId, (NodeResultSet<SwiftNode>) resultSet);
                break;
            case LOCAL_GROUP_PART:
                result = new LocalPartNodeResultSet(queryId, (NodeMergeResultSet<GroupNode>) resultSet);
                break;
            default:
                result = new SerializableDetailResultSet(queryId, (DetailResultSet) resultSet);
        }
        return result;
    }
}
