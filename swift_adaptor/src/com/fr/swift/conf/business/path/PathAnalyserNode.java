package com.fr.swift.conf.business.path;

import com.finebi.conf.internalimp.path.FineBusinessTableRelationPathImp;
import com.finebi.conf.structure.path.FineBusinessTableRelationPath;
import com.finebi.conf.structure.relation.FineBusinessTableRelation;
import com.fr.swift.conf.business.relation.SwiftRelationDao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/3/19
 */
public class PathAnalyserNode {
    private String currentTable;
    private SwiftRelationDao relationDao;
    private List<PathAnalyserNode> childNodes;

    public PathAnalyserNode(String currentTable, SwiftRelationDao relationDao) {
        this.currentTable = currentTable;
        this.relationDao = relationDao;
        this.childNodes = new ArrayList<PathAnalyserNode>();
    }

    public void addChildNode(PathAnalyserNode node) {
        childNodes.add(node);
    }

    public boolean containChild(PathAnalyserNode child) {
        return childNodes.contains(child);
    }

    public void removeChildNode(PathAnalyserNode node) {
        if (childNodes.contains(node)) {
            childNodes.remove(node);
        }
    }

    public List<FineBusinessTableRelationPath> getTargetPath(Map<PathAnalyserNode, Integer> scannedNodes, String target) {
        List<FineBusinessTableRelationPath> result = new ArrayList<FineBusinessTableRelationPath>();
        if (currentTable.equals(target)) {
            return result;
        }
        registerScannedNode(scannedNodes, this);
        for (PathAnalyserNode child : childNodes) {
            if (child.currentTable.equals(target)) {
                List<FineBusinessTableRelation> relations = relationDao.getRelation(currentTable, target);
                for (FineBusinessTableRelation relation :
                        relations) {
                    List<FineBusinessTableRelation> relationList = new ArrayList<FineBusinessTableRelation>();
                    relationList.add(relation);
                    FineBusinessTableRelationPath path = new FineBusinessTableRelationPathImp(relationList);
                    path.refershPathName();
                    result.add(path);
                }
            } else {
                if (!isScanned(scannedNodes, child)) {
                    List<FineBusinessTableRelationPath> paths = child.getTargetPath(scannedNodes, target);
                    Iterator<FineBusinessTableRelationPath> iterator = paths.iterator();
                    while (iterator.hasNext()) {
                        FineBusinessTableRelationPath path = iterator.next();
                        List<FineBusinessTableRelation> relations = relationDao.getRelation(currentTable, child.currentTable);
                        for (FineBusinessTableRelation relation :
                                relations) {
                            List<FineBusinessTableRelation> relationList = new ArrayList<FineBusinessTableRelation>();
                            relationList.add(relation);
                            relationList.addAll(path.getFineBusinessTableRelations());
                            FineBusinessTableRelationPath targetPath = new FineBusinessTableRelationPathImp(relationList);
                            targetPath.refershPathName();
                            result.add(targetPath);
                        }
                    }
                }
            }
        }
        scannedNodes.remove(this);
        return result;
    }


    private void registerScannedNode(Map<PathAnalyserNode, Integer> scannedNodes, PathAnalyserNode currentNode) {
        if (scannedNodes.containsKey(currentNode)) {
            Integer count = scannedNodes.get(currentNode) + 1;
            scannedNodes.put(currentNode, count);
        } else {
            scannedNodes.put(currentNode, 1);
        }
    }

    private boolean isScanned(Map<PathAnalyserNode, Integer> scannedNodes, PathAnalyserNode currentNode) {
        if (scannedNodes.containsKey(currentNode)) {
            return scannedNodes.get(currentNode) > 0;
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PathAnalyserNode that = (PathAnalyserNode) o;

        if (currentTable != null ? !currentTable.equals(that.currentTable) : that.currentTable != null) {
            return false;
        }
        return childNodes != null ? childNodes.equals(that.childNodes) : that.childNodes == null;
    }

    @Override
    public int hashCode() {
        int result = currentTable != null ? currentTable.hashCode() : 0;
        result = 31 * result + (childNodes != null ? childNodes.hashCode() : 0);
        return result;
    }
}
