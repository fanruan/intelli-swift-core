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
        root=new ReportNode();
        root.setId(UUID.randomUUID().toString());
        nodeTree.add(root);
    }

    public void addNode(ReportNode parent, ReportNode node) {
        if (null != parent) {
            nodeTree.add(parent);
            node.setParent(parent);
        } else {
            node.setParent(root);
        }
        nodeTree.add(node);
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
            if (itemNode.getdId().equals(nodeId)) {
                return true;
            }
        }
        return false;
    }
}


