package com.fr.bi.cal.analyze.report.report.widget.chart.style.excelExport.layout.nodeTree;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Kary on 2017/2/15.
 */
public class ExportNodeTree {
    private ExportNode root;
    private Set<ExportNode> container=new HashSet<>();

    public ExportNode getRoot() {
        return root;
    }

    public void setRoot(ExportNode root) {
        this.root = root;
    }

    public ExportNodeTree() {
        root=new ExportNode();
        root.setId(UUID.randomUUID().toString());
        container.add(root);
    }

    public void addNode(ExportNode parent, ExportNode node) {
        if (null != parent) {
            container.add(parent);
            node.setParent(parent);
        } else {
            node.setParent(root);
        }
        container.add(node);
    }

    public ExportNode getNode(String nodeId) {
        for (ExportNode exportNode : container) {
            if (exportNode.getId().equals(nodeId)) {
                return exportNode;
            }
        }
        return null;
    }

   public boolean isNodeExisted(String nodeId) {
        for (ExportNode exportNode : container) {
            if (exportNode.getdId().equals(nodeId)) {
                return true;
            }
        }
        return false;
    }
}


