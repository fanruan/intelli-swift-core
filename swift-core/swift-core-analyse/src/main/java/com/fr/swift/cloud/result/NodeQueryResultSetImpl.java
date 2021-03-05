package com.fr.swift.cloud.result;

/**
 *
 * @author lyon
 * @date 2019/1/11
 */
public class NodeQueryResultSetImpl extends BaseNodeQueryResultSet {

    private SwiftNode root;
    private boolean hasNextPage = true;

    public NodeQueryResultSetImpl(int fetchSize, SwiftNode root) {
        super(fetchSize);
        this.root = root;
    }

    @Override
    public SwiftNode getPage() {
        // 只有一页
        SwiftNode ret = root;
        root = null;
        hasNextPage = false;
        return ret;
    }

    @Override
    public boolean hasNextPage() {
        return hasNextPage;
    }
}
