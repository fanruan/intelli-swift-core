package com.fr.swift.flow;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;

/**
 * This class created on 2018-1-15 15:37:49
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftFlowResultSet implements SwiftResultSet {

    private SwiftResultSet swiftResultSet;

    private FlowRuleController flowRuleController;

    public SwiftFlowResultSet(SwiftResultSet swiftResultSet, FlowRuleController flowRuleController) {
        this.swiftResultSet = swiftResultSet;
        if (flowRuleController == null) {
            flowRuleController = new FlowRuleController();
        }
        this.flowRuleController = flowRuleController;
        this.flowRuleController.reset();
    }

    @Override
    public void close() throws SQLException {
        swiftResultSet.close();
    }

    @Override
    public boolean hasNext() throws SQLException {
        if (flowRuleController.isEnd()) {
            return false;
        }
        return swiftResultSet.hasNext();
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return swiftResultSet.getMetaData();
    }

    @Override
    public Row getNextRow() throws SQLException {
        return swiftResultSet.getNextRow();
    }
}
