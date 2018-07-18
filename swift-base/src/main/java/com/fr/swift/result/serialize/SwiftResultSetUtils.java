package com.fr.swift.result.serialize;

import com.fr.swift.query.query.QueryType;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;

/**
 * Created by Lyon on 2018/6/27.
 */
public class SwiftResultSetUtils {

    public static SwiftResultSet convert2Serializable(String jsonString, QueryType queryType,
                                                      SwiftResultSet resultSet) throws SQLException {
        SerializableResultSet result;
        switch (queryType) {
            case LOCAL_GROUP_ALL: {
                NodeResultSet nodeResultSet = (NodeResultSet) resultSet;
                result = new LocalAllNodeResultSet(jsonString, nodeResultSet.getNode(), nodeResultSet.hasNextPage());
                break;
            }
            case LOCAL_GROUP_PART: {
                NodeMergeResultSet mergeResultSet = (NodeMergeResultSet) resultSet;
                result = new LocalPartNodeResultSet(jsonString, mergeResultSet.getNode(),
                        mergeResultSet.getRowGlobalDictionaries(), mergeResultSet.hasNextPage());
                break;
            }
            default: {
                DetailResultSet detailResultSet = (DetailResultSet) resultSet;
                result = new SerializableDetailResultSet(jsonString, detailResultSet.getMetaData(), detailResultSet.getPage(),
                        detailResultSet.hasNextPage(), detailResultSet.getRowCount());
            }
        }
        return result;
    }
}
