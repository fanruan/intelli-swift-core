package com.fr.swift.result;

import com.fr.swift.query.group.by2.node.GroupPage;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Lyon
 * @date 2018/4/27
 *
 * todo 改名single page吧
 */
public class NodeMergeQRSImpl extends BaseNodeMergeQRS {

    private SwiftNode root;
    private List<Map<Integer, Object>> rowGlobalDictionaries;

    protected boolean hasNextPage = true;

    public NodeMergeQRSImpl(int fetchSize, SwiftNode root, List<Map<Integer, Object>> rowGlobalDictionaries) {
        super(fetchSize);
        this.root = root;
        this.rowGlobalDictionaries = rowGlobalDictionaries;
    }

    @Override
    public GroupPage getPage() {
        // 只有一页，适配ChainedResultSet
        hasNextPage = false;
        return new GroupPage(root, rowGlobalDictionaries);
    }

    @Override
    public boolean hasNextPage() {
        return hasNextPage;
    }
}
