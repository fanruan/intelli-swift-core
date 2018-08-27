package com.fr.swift.api.rpc.result;

import com.fr.swift.result.serialize.SerializableDetailResultSet;
import com.fr.swift.source.Row;
import com.fr.swift.util.Crasher;

import java.sql.SQLException;
import java.util.List;

/**
 * @author yee
 * @date 2018/8/27
 */
public abstract class AbstractSwiftResultSet extends SerializableDetailResultSet {
    public AbstractSwiftResultSet(SerializableDetailResultSet resultSet) throws SQLException {
        super(resultSet.getJsonString(), resultSet.getMetaData(), resultSet.getRows(), resultSet.isOriginHasNextPage(), resultSet.getRowCount());
    }

    @Override
    public List<Row> getPage() {
        hasNextPage = false;
        List<Row> ret = rows;
        if (originHasNextPage) {
            try {
                AbstractSwiftResultSet resultSet = queryNextPage(jsonString);
                if (null != resultSet) {
                    hasNextPage = true;
                    this.rows = resultSet.getRows();
                    this.originHasNextPage = resultSet.isOriginHasNextPage();
                } else {
                    hasNextPage = false;
                    this.originHasNextPage = false;
                }
            } catch (Exception e) {
                Crasher.crash(e);
            }
        }
        return ret;
    }

    protected abstract AbstractSwiftResultSet queryNextPage(String queryJson);
}
