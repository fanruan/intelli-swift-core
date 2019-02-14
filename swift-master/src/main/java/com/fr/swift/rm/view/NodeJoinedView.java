package com.fr.swift.rm.view;

import java.util.HashSet;
import java.util.Set;

/**
 * This class created on 2018/11/9
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class NodeJoinedView {

    private final static NodeJoinedView INSTANCE = new NodeJoinedView();

    private Set<String> nodeIds;

    private NodeJoinedView() {
        nodeIds = new HashSet<String>();
    }

    public static NodeJoinedView getInstance() {
        return INSTANCE;
    }

    public synchronized void nodeJoin(String nodeId) {
        nodeIds.add(nodeId);
    }

    public synchronized void nodeLeft(String nodeId) {
        nodeIds.remove(nodeId);
    }

    public synchronized void nodesRemove(Set<String> nodeIds) {
        this.nodeIds.removeAll(nodeIds);
    }

    public synchronized boolean isEmpty() {
        return nodeIds.isEmpty();
    }

    public synchronized Set<String> getNodes() {
        return new HashSet<String>(nodeIds);
    }
}
