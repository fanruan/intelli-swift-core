package com.fr.swift.cloud.analyse;

import com.fr.swift.cloud.result.Pagination;
import com.fr.swift.cloud.result.SwiftResultSet;
import com.fr.swift.cloud.source.Row;
import com.fr.swift.cloud.source.SwiftMetaData;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

/**
 * @Author: lucifer
 * @Description: 实现SwiftResultSet是为了 process handler那里的兼容，方便点，方法都是不支持的
 * @Date: Created in 2020/12/16
 */
public class CalcPage implements Pagination<List<Row>>, SwiftResultSet, Serializable {
    private static final long serialVersionUID = 6776482829440515793L;

    private int fetchSize;
    private List<Row> page;
    private boolean hasNextPage;
    private int rowCount;
    private transient SyncInvoker invoker;


    public CalcPage(int fetchSize, int rowCount, List<Row> page, boolean hasNextPage) {
        this.fetchSize = fetchSize;
        this.rowCount = rowCount;
        this.page = page;
        this.hasNextPage = hasNextPage;
    }

    @Override
    public int getFetchSize() {
        return fetchSize;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        throw new UnsupportedOperationException("un support operate:getMetaData");
    }

    @Override
    public boolean hasNext() throws SQLException {
        throw new UnsupportedOperationException("un support operate:hasNext");
    }

    @Override
    public Row getNextRow() throws SQLException {
        throw new UnsupportedOperationException("un support operate:getNextRow");
    }

    public void setInvoker(SyncInvoker invoker) {
        this.invoker = invoker;
    }

    @Override
    public List<Row> getPage() {
        List<Row> result = page;
        page = null;
        if (hasNextPage() && invoker != null) {
            CalcPage invoke = invoker.invoke();
            page = invoke.page;
            hasNextPage = invoke.hasNextPage;
        } else {
            hasNextPage = false;
        }

        return result;
    }

    @Override
    public boolean hasNextPage() {
        return page != null || hasNextPage;
    }

    @Override
    public void close() {

    }

    public int getRowCount() {
        return rowCount;
    }

    public interface SyncInvoker {

        CalcPage invoke();
    }
}
