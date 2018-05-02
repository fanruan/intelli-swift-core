package com.fr.swift.cal.targetcal.group;

import com.fr.swift.cal.info.XGroupQueryInfo;
import com.fr.swift.cal.result.ResultQuery;
import com.fr.swift.cal.targetcal.AbstractTargetCalQuery;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.XLeftNode;
import com.fr.swift.result.XNodeMergeResultSet;
import com.fr.swift.result.node.cal.TargetCalculatorUtils;

import java.sql.SQLException;

/**
 * Created by Lyon on 2018/4/28.
 */
public class XGroupTargetCalQuery extends AbstractTargetCalQuery<NodeResultSet> {

    private ResultQuery<NodeResultSet> mergeQuery;
    private XGroupQueryInfo info;

    public XGroupTargetCalQuery(ResultQuery<NodeResultSet> mergeQuery,
                                XGroupQueryInfo queryInfo) {
        this.mergeQuery = mergeQuery;
        this.info = queryInfo;
    }

    @Override
    public NodeResultSet getQueryResult() throws SQLException {
        XNodeMergeResultSet resultSet = (XNodeMergeResultSet) mergeQuery.getQueryResult();
        TargetCalculatorUtils.getShowTargetsForGroupNodeAndSetNodeData(resultSet.getTopGroupNode(),
                info.getTargetInfo().getTargetsForShowList(), resultSet.getColGlobalDictionaries());
        TargetCalculatorUtils.getShowTargetsForXLeftNodeAndSetNodeData((XLeftNode) resultSet.getNode(),
                info.getTargetInfo().getTargetsForShowList(), resultSet.getRowGlobalDictionaries());
        return resultSet;
    }
}
