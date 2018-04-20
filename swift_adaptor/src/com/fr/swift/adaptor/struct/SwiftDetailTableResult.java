package com.fr.swift.adaptor.struct;

import com.finebi.conf.structure.result.BIDetailCell;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018-1-2 10:11:05
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftDetailTableResult implements BIDetailTableResult {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftDetailTableResult.class);
    private SwiftResultSet swiftResultSet;
    private int columnSize = -1;
    private int totalRows = -1;
    private int rowSize = -1;
    private int rowCount = 0;

    public SwiftDetailTableResult(SwiftResultSet swiftResultSet, int totalRows) throws SQLException {
        this.swiftResultSet = swiftResultSet;
        this.columnSize = swiftResultSet.getMetaData().getColumnCount();
        this.rowSize = swiftResultSet instanceof SwiftEmptyResult ? 0 :((DetailResultSet) swiftResultSet).getRowSize();
        this.totalRows = Math.min(rowSize, totalRows);
    }

    @Override
    public void remove() {
    }

    @Override
    public boolean hasNext() {
        return rowCount < totalRows;
//        try {
//            return swiftResultSet.next();
//        } catch (SQLException e) {
//            LOGGER.error(e.getMessage(), e);
//            return false;
//        }
    }

    @Override
    public List<BIDetailCell> next() {
        try {
            while (swiftResultSet.next()) {
                List<BIDetailCell> detailCellList = new ArrayList<BIDetailCell>();
                Row row = swiftResultSet.getRowData();
                for (int i = 0; i < columnSize; i++) {
                    BIDetailCell detailCell = new SwiftDetailCell(row.getValue(i));
                    //todo 临时处理，这个应该和SwiftSegmentDetailResult一样，不处理null值吧。。
                    if (detailCell.getData() == null) {
                        detailCell = new SwiftDetailCell("");
                    }
                    detailCellList.add(detailCell);
                }
                rowCount++;
                return detailCellList;
            }
            return null;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        } catch (Exception ee) {
            LOGGER.error(ee.getMessage(), ee);
            return null;
        }
    }

    @Override
    public int rowSize() {
        return rowSize;
    }

    @Override
    public int columnSize() {
        return columnSize;
    }

    @Override
    public boolean hasNextPage() {
        return false;
    }

    @Override
    public boolean hasPreviousPage() {
        return false;
    }

    @Override
    public int totalRowSize() {
        return totalRows;
    }

    @Override
    public ResultType getResultType() {
        return ResultType.DETAIL;
    }
}
