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

//import com.fr.swift.adaptor.struct.paging.Paging;

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
    private int columnSize;
    //这些参数之后合成一个paging类，pagesize是是功能传过来的
    private int totalRows;
    private int rowSize;
    private int rowCount = 0;
    private int currentPage;
    private int startRow;
    private int endRow;
    private final int pageSize = 100;

    public SwiftDetailTableResult(SwiftResultSet swiftResultSet, int totalRows, int currentPage) throws SQLException {
        this.swiftResultSet = swiftResultSet;
        this.columnSize = swiftResultSet.getMetaData().getColumnCount();
        this.currentPage = currentPage;
        this.totalRows = totalRows;
        init();

    }

    public void init() {
        startRow = (currentPage - 1) * pageSize;
        totalRows = Math.min(swiftResultSet instanceof SwiftEmptyResult ? 0 : ((DetailResultSet) swiftResultSet).getRowCount(), totalRows);
        rowSize = totalRows - startRow >= pageSize ? pageSize : totalRows - startRow;
        endRow = startRow + rowSize;
    }

    @Override
    public void remove() {
    }

    @Override
    public boolean hasNext() {
        try {
            if (currentPage == -1) {
                return swiftResultSet.next();
            }
            while (swiftResultSet.next()) {
                if (checkPage(rowCount) == null) {
                    break;
                }
                swiftResultSet.getRowData();
                rowCount++;
            }
            return ++rowCount <= endRow;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }
    }

    private Boolean checkPage(int row) {
        if (row < startRow) {
            return true;
        }
        if (row >= endRow) {
            return false;
        }
        return null;
    }

    @Override
    public List<BIDetailCell> next() {
        try {
                List<BIDetailCell> detailCellList = new ArrayList<BIDetailCell>();
                Row row = swiftResultSet.getRowData();
                for (int i = 0; i < columnSize; i++) {
                    BIDetailCell detailCell = new SwiftDetailCell(row.getValue(i));
                    if (detailCell.getData() == null) {
                        detailCell = new SwiftDetailCell("");
                    }
                    detailCellList.add(detailCell);
                }
                return detailCellList;
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
        return totalRows - currentPage * pageSize > 0;
    }

    @Override
    public boolean hasPreviousPage() {
        return currentPage > 1;
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
