package com.fr.swift.cloud.source.resultset;

import com.fr.swift.cloud.result.DecorateResultSet;
import com.fr.swift.cloud.result.SwiftResultSet;
import com.fr.swift.cloud.source.Row;
import com.fr.swift.cloud.util.Assert;
import com.fr.swift.cloud.util.IoUtil;

import java.sql.SQLException;

/**
 * @author anchore
 * @date 2018/6/5
 */
public class LimitedResultSet extends DecorateResultSet {

    private boolean closeOrigin;

    private int cursor = 0;

    private int limit;

    public LimitedResultSet(SwiftResultSet origin, int limit) {
        this(origin, limit, true);
    }

    public LimitedResultSet(SwiftResultSet origin, int limit, boolean closeOrigin) {
        super(origin);
        Assert.isTrue(limit >= 0, "limit must be greater than or equal 0");
        this.limit = limit;
        this.closeOrigin = closeOrigin;
    }

    @Override
    public boolean hasNext() throws SQLException {
        return cursor < limit && super.hasNext();
    }

    @Override
    public Row getNextRow() throws SQLException {
        cursor++;
        return super.getNextRow();
    }

    @Override
    public void close() {
        if (closeOrigin) {
            IoUtil.close(origin);
        }
    }
}