package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.node;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Kary on 2017/2/15.
 */
public class ReportNodeTree {
    private ReportNode root;
    private Set<ReportNode> nodeTree = new HashSet<ReportNode>();

    public ReportNode getRoot() {
        return root;
    }

    public void setRoot(ReportNode root) {
        this.root = root;
    }

    public ReportNodeTree() {
        root = new ReportNode();
        root.setId("root"+UUID.randomUUID().toString());
        nodeTree.add(root);
    }

    public void addNode(ReportNode node, ReportNode newNode) {
        if (null == newNode) {
            node.setParent(root);
            nodeTree.add(node);
        } else if (null == node) {
            newNode.setParent(root);
            nodeTree.add(newNode);
        } else {
            newNode.setParent(node);
            nodeTree.add(newNode);
        }
    }

    public ReportNode getNode(String nodeId) {
        for (ReportNode itemNode : nodeTree) {
            if (itemNode.getId().equals(nodeId)) {
                return itemNode;
            }
        }
        return null;
    }

    public boolean isNodeExisted(String nodeId) {
        for (ReportNode itemNode : nodeTree) {
            if (itemNode.getId().equals(nodeId)) {
                return true;
            }
        }
        return false;
    }
}


