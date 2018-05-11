package com.fr.swift.adaptor.struct.node;

import com.finebi.conf.structure.result.table.BIGroupNode;
import com.finebi.conf.structure.result.table.BITableResult;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.page.NodeRange;
import com.fr.swift.structure.iterator.IteratorUtils;
import com.fr.swift.structure.iterator.Tree2RowIterator;

import java.util.List;

/**
 * Created by Lyon on 2018/5/10.
 */
public class SwiftTableResult implements BITableResult {

    private static int PAGE_SIZE = 20;

    private GroupNode root;
    private List<List<GroupNode>> rows;
    private int page;

    public SwiftTableResult(int dimensionSize, GroupNode root, int currentPage) {
        this.root = root;
        this.rows = IteratorUtils.iterator2List(new Tree2RowIterator(dimensionSize, root.getChildren().iterator()));
        this.page = currentPage;
    }

    /**
     * TODO 等后面能做结果集缓存之后，全部计算的情况下，缓存结果集上的分页只需要加两个辅助指针保存startRow和endRow，
     * 等下一页到来的时候清掉前一次的两行node里面的NodeRange，继续按照当前逻辑进行分页就可以了
     *
     * @return
     */
    @Override
    public BIGroupNode getNode() {
        if (rows.isEmpty()) {
            return new BIGroupNodeAdaptor(root);
        }
        if (page != 0) {
            List<GroupNode> startRow = rows.get(page * PAGE_SIZE);
            for (int i = 0; i < startRow.size(); i++) {
                NodeRange range = new NodeRange();
                range.setStartIndexIncluded(startRow.get(i).getIndex());
                if (i == 0) {
                    root.setNodeRange(range);
                } else {
                    startRow.get(i - 1).setNodeRange(range);
                }
            }
        }
        int endIndex = Math.min((page + 1) * PAGE_SIZE - 1, rows.size() - 1);
        List<GroupNode> endRow = rows.get(endIndex);
        for (int i = 0; i < endRow.size(); i++) {
            NodeRange range = new NodeRange();
            range.setEndIndexIncluded(endRow.get(i).getIndex());
            if (i == 0) {
                root.setNodeRange(range);
            } else {
                endRow.get(i - 1).setNodeRange(range);
            }
        }
        return new BIGroupNodeAdaptor(root);
    }

    @Override
    public boolean hasNextPage() {
        return (page + 1) * PAGE_SIZE < rows.size();
    }

    @Override
    public boolean hasPreviousPage() {
        return page > 0;
    }

    @Override
    public ResultType getResultType() {
        return ResultType.BIGROUP;
    }
}
