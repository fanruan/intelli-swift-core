package com.fr.swift.api.result;

import com.fr.swift.result.DetailResultSet;
import com.fr.swift.result.SwiftRowIterator;
import com.fr.swift.result.SwiftRowIteratorImpl;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.Crasher;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018-12-12
 */
public abstract class BaseApiResultSet<T> implements SwiftApiResultSet<T> {
    private static final long serialVersionUID = -4562401863852149125L;
    private List<Row> rows;
    private SwiftMetaData metaData;
    private int rowCount;
    private boolean hasNextPage = true;
    private boolean originHasNextPage;
    private transient SwiftRowIterator rowIterator;
    private T queryObject;

    public BaseApiResultSet(T queryObject, SwiftMetaData metaData, List<Row> rows, int rowCount, boolean originHasNextPage) {
        this.queryObject = queryObject;
        this.metaData = metaData;
        this.rows = rows;
        this.rowCount = rowCount;
        this.originHasNextPage = originHasNextPage;
    }

    @Override
    public Map<String, Integer> getLabel2Index() throws SQLException {
        Map<String, Integer> label2Index = new HashMap<String, Integer>();
        if (null != metaData) {
            List<String> fieldNames = metaData.getFieldNames();
            for (String fieldName : fieldNames) {
                label2Index.put(fieldName, metaData.getColumnIndex(fieldName));
            }
        }
        return label2Index;
    }

    @Override
    public List<Row> getRows() {
        return rows;
    }

    @Override
    public boolean isOriginHasNextPage() {
        return originHasNextPage;
    }

    @Override
    public List<Row> getPage() {
        hasNextPage = false;
        List<Row> ret = rows;
        if (originHasNextPage) {
            try {
                SwiftApiResultSet resultSet = queryNextPage(queryObject);
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

    @Override
    public boolean hasNextPage() {
        return hasNextPage || originHasNextPage;
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftMetaData getMetaData() {
        return metaData;
    }

    @Override
    public boolean hasNext() {
        if (rowIterator == null) {
            rowIterator = new SwiftRowIteratorImpl<DetailResultSet>(this);
        }
        return rowIterator.hasNext();
    }

    @Override
    public Row getNextRow() {
        return rowIterator.next();
    }

    @Override
    public void close() {
        // TODO 这里的这里close rowIterator会有Stack Overflow，这里要考虑怎么close
//        IoUtil.close(rowIterator);
    }
}
