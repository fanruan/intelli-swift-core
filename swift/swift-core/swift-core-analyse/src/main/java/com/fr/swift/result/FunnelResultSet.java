package com.fr.swift.result;

import com.fr.swift.query.aggregator.FunnelAggValue;
import com.fr.swift.query.group.FunnelGroupKey;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Map;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class FunnelResultSet implements SwiftResultSet, Serializable {

    private static final long serialVersionUID = 7369105047420564136L;

    private Map<FunnelGroupKey, FunnelAggValue> result;

    public FunnelResultSet(Map<FunnelGroupKey, FunnelAggValue> result) {
        this.result = result;
    }

    public Map<FunnelGroupKey, FunnelAggValue> getResult() {
        return result;
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return null;
    }

    @Override
    public boolean hasNext() throws SQLException {
        return false;
    }

    @Override
    public Row getNextRow() throws SQLException {
        return null;
    }

    @Override
    public void close() throws SQLException {

    }
}