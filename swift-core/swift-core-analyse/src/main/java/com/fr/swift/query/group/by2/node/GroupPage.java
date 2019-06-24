package com.fr.swift.query.group.by2.node;

import com.fr.swift.result.SwiftNode;

import java.util.List;
import java.util.Map;

/**
 * @author anchore
 * @date 2019/6/19
 */
public class GroupPage {
    private SwiftNode root;
    private List<Map<Integer, Object>> globalDicts;

    public GroupPage(SwiftNode root, List<Map<Integer, Object>> globalDicts) {
        this.root = root;
        this.globalDicts = globalDicts;
    }

    public SwiftNode getRoot() {
        return root;
    }

    public List<Map<Integer, Object>> getGlobalDicts() {
        return globalDicts;
    }
}