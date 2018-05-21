package com.fr.swift.adaptor.struct.node.paging;

import com.finebi.conf.structure.result.table.BIGroupNode;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.structure.Pair;

/**
 * Created by Lyon on 2018/5/20.
 */
public interface NodePagingHelper {

    Pair<BIGroupNode, PagingSession> getPage(PagingInfo pageInfo);

    <T extends NodeResultSet> T getNodeResultSet();
}
