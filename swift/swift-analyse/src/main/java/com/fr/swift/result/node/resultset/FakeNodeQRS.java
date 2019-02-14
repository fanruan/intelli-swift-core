package com.fr.swift.result.node.resultset;

import com.fr.swift.result.BaseNodeQRS;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.result.SwiftRowOperator;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Lyon
 * @date 2018/6/13
 */
public class FakeNodeQRS extends BaseNodeQRS {

    private SwiftRowOperator<Row> operator;
    private QueryResultSet<SwiftNode> source;

    public FakeNodeQRS(SwiftRowOperator<Row> operator, QueryResultSet<SwiftNode> source) {
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
        return ChainedNodeQRS.create(getFetchSize(), iterator, metaData);
    }
}
