package com.fr.swift.result;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/4/28.
 */
public class XNodeMergeResultSetImpl implements XNodeMergeResultSet {

    private XLeftNode leftRoot;
    private TopGroupNode topRoot;
    private List<Map<Integer, Object>> rowGlobalDictionaries;
    private List<Map<Integer, Object>> colGlobalDictionaries;

    public XNodeMergeResultSetImpl(XLeftNode leftRoot, TopGroupNode topRoot,
                                   List<Map<Integer, Object>> rowGlobalDictionaries,
                                   List<Map<Integer, Object>> colGlobalDictionaries) {
        this.leftRoot = leftRoot;
        this.topRoot = topRoot;
        this.rowGlobalDictionaries = rowGlobalDictionaries;
        this.colGlobalDictionaries = colGlobalDictionaries;
    }

    @Override
    public List<Map<Integer, Object>> getColGlobalDictionaries() {
        return colGlobalDictionaries;
    }

    @Override
    public TopGroupNode getTopGroupNode() {
        return topRoot;
    }

    @Override
    public List<Map<Integer, Object>> getRowGlobalDictionaries() {
        return rowGlobalDictionaries;
    }

    @Override
    public SwiftNode<XLeftNode> getNode() {
        return leftRoot;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return null;
    }

    @Override
    public boolean next() throws SQLException {
        return false;
    }

    @Override
    public Row getRowData() throws SQLException {
        return null;
    }

    @Override
    public void close() throws SQLException {

    }
}
