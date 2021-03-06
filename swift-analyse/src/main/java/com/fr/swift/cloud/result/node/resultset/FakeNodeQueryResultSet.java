package com.fr.swift.cloud.result.node.resultset;

import com.fr.swift.cloud.result.BaseNodeQueryResultSet;
import com.fr.swift.cloud.result.SwiftNode;
import com.fr.swift.cloud.result.SwiftResultSet;
import com.fr.swift.cloud.result.SwiftRowOperator;
import com.fr.swift.cloud.result.qrs.QueryResultSet;
import com.fr.swift.cloud.source.Row;
import com.fr.swift.cloud.source.SwiftMetaData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Lyon
 * @date 2018/6/13
 */
public class FakeNodeQueryResultSet extends BaseNodeQueryResultSet {

    private SwiftRowOperator<Row> operator;
    private QueryResultSet<SwiftNode> source;

    public FakeNodeQueryResultSet(SwiftRowOperator<Row> operator, QueryResultSet<SwiftNode> source) {
        super(source.getFetchSize());
        this.operator = operator;
        this.source = source;
    }

    @Override
    public SwiftNode getPage() {
        throw new UnsupportedOperationException();
    }

    private List<Row> getPageData() {
        if (source.hasNextPage()) {
            return operator.operate(source.getPage());
        }
        return new ArrayList<Row>();
    }

    @Override
    public boolean hasNextPage() {
        return source.hasNextPage();
    }

    @Override
    public SwiftResultSet convert(SwiftMetaData metaData) {
        Iterator<Row> iterator = getPageData().iterator();
        return ChainedNodeQueryResultSet.create(getFetchSize(), iterator, metaData);
    }
}
