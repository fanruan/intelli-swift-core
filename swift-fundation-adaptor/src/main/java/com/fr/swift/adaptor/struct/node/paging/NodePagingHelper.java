package com.fr.swift.adaptor.struct.node.paging;

import com.finebi.conf.structure.result.table.BIGroupNode;

/**
 * Created by Lyon on 2018/5/20.
 */
public interface NodePagingHelper<T extends BIGroupNode> {

    T getPage(PagingInfo pageInfo);
}
