package com.fr.swift.analyse;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/12/16
 */
public class CalcDetailResultSet implements SwiftResultSet {

    private int fetchSize;

    private int curs = 0;

    private CalcSegment calcSegment;

    public CalcDetailResultSet(int fetchSize, CalcSegment calcSegment) {
        this.fetchSize = fetchSize;
        this.calcSegment = calcSegment;

    }

    @Override
    public int getFetchSize() {
        return fetchSize;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return calcSegment.getMetaData();
    }

    @Override
    public boolean hasNext() throws SQLException {
        return calcSegment.hasNext();
    }

    @Override
    public Row getNextRow() throws SQLException {
        try {
            return calcSegment.getNextRow();
        } finally {
            curs++;
        }
    }

    @Override
    public void close() throws SQLException {
        try {
            calcSegment.close();
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
}
