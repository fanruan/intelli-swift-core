package com.fr.swift.adaptor.struct;

import com.finebi.conf.structure.result.BIDetailCell;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
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

    public SwiftDetailTableResult(SwiftResultSet swiftResultSet) throws SQLException {
        this.swiftResultSet = swiftResultSet;
        this.columnSize = swiftResultSet.getMetaData().getColumnCount();
    }

    @Override
    public void remove() {
    }

    @Override
    public boolean hasNext() {
        try {
            return swiftResultSet.next();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<BIDetailCell> next() {
        try {
            List<BIDetailCell> detailCellList = new ArrayList<BIDetailCell>();
            for (int i = 0; i < columnSize; i++) {
                BIDetailCell detailCell = new SwiftDetailCell(swiftResultSet.getRowData().getValue(i));
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
        return 0;
    }

    @Override
    public int columnSize() {
        return columnSize;
    }

    @Override
    public ResultType getResultType() {
        return ResultType.DETAIL;
    }
}
