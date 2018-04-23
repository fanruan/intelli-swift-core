package com.fr.swift.result;

import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by pony on 2018/4/19.
 * 根据最后一个节点来遍历node，直到没有sibling
 */
public class NodeResultSetImpl<T extends SwiftNode> implements NodeResultSet {
    private SwiftNode<T> node;
    private SwiftNode<T> currentChild;

    public NodeResultSetImpl(SwiftNode<T> node) {
        this.node = node;
        initCurrentChild();
    }

    private void initCurrentChild() {
        currentChild = node;
        while (currentChild.getChildrenSize() != 0){
            currentChild = currentChild.getChild(0);
        }
    }

    @Override
    public SwiftNode<T> getNode() {
        return node;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return null;
    }

    @Override
    public boolean next() throws SQLException {
        boolean next = currentChild.getSibling() != null;
        currentChild = currentChild.getSibling();
        return next;
    }

    @Override
    public Row getRowData() throws SQLException {
        List list = new ArrayList();
        SwiftNode node = currentChild;
        while (node.getParent() != null){
            //排除根节点
            if (node.getParent() != null){
                list.add(node.getData());
            }
            node = node.getParent();
        }
        Collections.reverse(list);
        return new ListBasedRow(list);
    }

    @Override
    public void close() throws SQLException {

    }
}
