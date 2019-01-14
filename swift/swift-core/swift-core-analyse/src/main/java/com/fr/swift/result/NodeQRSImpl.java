package com.fr.swift.result;

/**
 * Created by lyon on 2019/1/11.
 */
public class NodeQRSImpl extends BaseNodeQRS {

    private SwiftNode root;
    private boolean hasNextPage = true;

    public NodeQRSImpl(int fetchSize, SwiftNode root) {
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
