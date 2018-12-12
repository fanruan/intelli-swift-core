package com.fr.swift.query.result.serialize;

import com.fr.swift.result.DetailResultSet;
import com.fr.swift.result.SerializableResultSet;
import com.fr.swift.result.SwiftRowIteratorImpl;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/11
 */
public class SerializableDetailResultSet implements DetailResultSet, SerializableResultSet {
    private static final long serialVersionUID = -2306723089258907631L;

    protected String jsonString;
    protected SwiftMetaData metaData;
    protected List<Row> rows;
    protected int rowCount;
    protected boolean hasNextPage = true;
    protected boolean originHasNextPage;
    private transient Iterator<Row> rowIterator;

    public SerializableDetailResultSet(String jsonString, SwiftMetaData metaData, List<Row> rows,
                                       boolean originHasNextPage, int rowCount) {
        this.jsonString = jsonString;
        this.metaData = metaData;
        this.rows = rows;
        this.originHasNextPage = originHasNextPage;
        this.rowCount = rowCount;
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public List<Row> getPage() {
        hasNextPage = false;
        List<Row> ret = rows;
        if (originHasNextPage) {
//            try {
//                AnalyseService service = ProxySelector.getInstance().getFactory().getProxy(AnalyseService.class);
//                SerializableDetailResultSet resultSet = (SerializableDetailResultSet) service.getRemoteQueryResult(jsonString, null);
//                hasNextPage = true;
//                this.rows = resultSet.rows;
//                this.originHasNextPage = resultSet.originHasNextPage;
//            } catch (SQLException e) {
//                Crasher.crash(e);
//            }
        }
        return ret;
    }

    @Override
    public boolean hasNextPage() {
        return hasNextPage || originHasNextPage;
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return metaData;
    }

    @Override
    public void setMetaData(SwiftMetaData metaData) {
    }

    @Override
    public boolean hasNext() throws SQLException {
        if (rowIterator == null) {
            rowIterator = new SwiftRowIteratorImpl(this);
        }
        return rowIterator.hasNext();
    }

    @Override
    public Row getNextRow() throws SQLException {
        return rowIterator.next();
    }

    @Override
    public void close() throws SQLException {

    }

    public String getJsonString() {
        return jsonString;
    }

    public List<Row> getRows() {
        return rows;
    }

    public boolean isOriginHasNextPage() {
        return originHasNextPage;
    }
}
