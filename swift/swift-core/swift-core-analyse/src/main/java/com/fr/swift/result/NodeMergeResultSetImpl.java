package com.fr.swift.result;

import com.fr.swift.structure.Pair;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Lyon
 * @date 2018/4/27
 */
public class NodeMergeResultSetImpl<T extends GroupNode> extends BaseNodeMergeResultSet<T> {

    private T root;
    private List<Map<Integer, Object>> rowGlobalDictionaries;

    private boolean hasNextPage = true;

    public NodeMergeResultSetImpl(int fetchSize, T root, List<Map<Integer, Object>> rowGlobalDictionaries) {
        super(fetchSize);
        this.root = root;
        this.rowGlobalDictionaries = rowGlobalDictionaries;
    }

    @Override
    public Pair<T, List<Map<Integer, Object>>> getPage() {
        // 只有一页，适配ChainedResultSet
        hasNextPage = false;
        return Pair.of(root, rowGlobalDictionaries);
    }

    @Override
    public boolean hasNextPage() {
        return hasNextPage;
    }
}
