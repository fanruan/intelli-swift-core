package com.fr.swift.cloud.analyse.merged;

import com.fr.swift.cloud.analyse.CalcPage;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.query.limit.Limit;
import com.fr.swift.cloud.source.Row;
import com.fr.swift.cloud.source.SwiftMetaData;
import com.fr.swift.cloud.util.exception.LambdaWrapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/12/16
 */
public class CalcDetailResultSet extends BaseDetailResultSet {

    private int fetchSize;
    private int index = 0;
    private Iterator<Row> iterator = new ArrayList<Row>().iterator();
    private List<CalcPage> calcPageList;
    private int rowCount;

    public CalcDetailResultSet(int fetchSize, List<CalcPage> calcPageList, Limit limit) {
        this.fetchSize = fetchSize;
        this.calcPageList = calcPageList;
        this.rowCount = getRowCount(calcPageList, limit);
    }

    private int getRowCount(List<CalcPage> calcPageList, Limit limit) {
        int rowCount = calcPageList.stream().mapToInt(CalcPage::getRowCount).sum();
        if (limit != null) {
            rowCount = Math.min(limit.end(), rowCount);
        }
        return rowCount;
    }

    @Override
    public int getFetchSize() {
        return fetchSize;
    }

    @Override
    public void setMetaData(SwiftMetaData swiftMetaData) {
        this.swiftMetaData = swiftMetaData;
    }

    @Override
    public boolean hasNext() throws SQLException {
        if (rowCount <= 0) {
            return false;
        }
        if (iterator.hasNext()) {
            return true;
        }
        while (index < calcPageList.size()) {
            CalcPage calcPage = calcPageList.get(index);
            if (calcPage.hasNextPage()) {
                List<Row> page = calcPage.getPage();
                if (page != null && !page.isEmpty()) {
                    iterator = page.iterator();
                    break;
                }
            }
            index++;
        }
        return iterator.hasNext();
    }

    @Override
    public Row getNextRow() throws SQLException {
        rowCount--;
        return iterator.next();
    }

    @Override
    public void close() throws SQLException {
        try {
            calcPageList.forEach(LambdaWrapper.rethrowConsumer(CalcPage::close));
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("close calcPage failed", e);
        }
    }
}
