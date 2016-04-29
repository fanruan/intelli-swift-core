package com.fr.bi.conf.base.relation.path;

import com.fr.bi.common.container.BICollectionContainer;
import com.fr.bi.common.container.BISetContainer;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.conf.base.relation.BITableRelationAnalysisService;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.exception.*;
import com.fr.bi.stable.relation.BITableRelation;
import com.fr.bi.stable.relation.BITableRelationPath;
import com.fr.general.ComparatorUtils;

import java.util.*;

/**
 * Created by Connery on 2016/1/13.
 */
public class BITablePathAnalyserNode extends BISetContainer<BITablePathAnalyserNode> {

    private Table currentNodeTable;
    private BITablePathAnalyser currentPath;
    private BITableRelationAnalysisService tableRelationAnalyser;

    public BITablePathAnalyserNode(Table currentNodeTable, BITableRelationAnalysisService tableRelationAnalyser) {
        this.currentNodeTable = currentNodeTable;
        this.tableRelationAnalyser = tableRelationAnalyser;
    }

    protected void setCurrentPath(BITablePathAnalyser currentPath) {
        this.currentPath = currentPath;
    }

    public void buildPathNodeRelation(BITablePathAnalyserNode node) throws BIPathNodeDuplicateException {
        if (!containChildNode(node)) {
            add(node);
        } else {
            throw new BIPathNodeDuplicateException();
        }
    }

    public boolean containChildNode(BITablePathAnalyserNode node) {
        return contain(node);
    }

    /**
     * 从当前Node路径出发，向下遍历子Node。寻找所有到指定表的路径
     *
     * @param scannedNodes
     * @param targetTable
     * @return
     * @throws BITableAbsentException
     * @throws BITableRelationConfusionException
     * @throws BITablePathConfusionException
     */
    protected Set<BITableRelationPath> getAllRelationPath(Set<BITablePathAnalyserNode> scannedNodes, Table targetTable)
            throws BITableAbsentException, BITableRelationConfusionException, BITablePathConfusionException {
        /**
         * 获得全部的直接子节点；
         */
        Iterator<BITablePathAnalyserNode> childNodesIt = getContainer().iterator();
        Set<BITableRelationPath> result = new HashSet<BITableRelationPath>();
        scannedNodes.add(this);
        while (childNodesIt.hasNext()) {
            BITablePathAnalyserNode childNode = childNodesIt.next();
            if (isSpecificTable(childNode.currentNodeTable, targetTable)) {
                Iterator<BITableRelation> it = tableRelationAnalyser.getRelation(currentNodeTable, childNode.currentNodeTable).iterator();
                while (it.hasNext()) {
                    BITableRelationPath path = BIFactoryHelper.getObject(BITableRelationPath.class);
                    path.addRelationAtHead(it.next());
                    result.add(path);
                }
            } else {
                if (!isScanned(scannedNodes, childNode)) {
                    Iterator<BITableRelationPath> it = childNode.getAllRelationPath(scannedNodes, targetTable).iterator();
                    while (it.hasNext()) {
                        BITableRelationPath child2TargetPath = it.next();
                        Iterator<BITableRelation> current2ChildRelationIt = tableRelationAnalyser.getRelation(currentNodeTable, childNode.currentNodeTable).iterator();
                        while (current2ChildRelationIt.hasNext()) {
                            BITableRelation current2ChildRelation = current2ChildRelationIt.next();
                            BITableRelationPath child2TargetPathCopy = BIFactoryHelper.getObject(BITableRelationPath.class);
                            child2TargetPathCopy.copyFrom(child2TargetPath);
                            child2TargetPathCopy.addRelationAtHead(current2ChildRelation);
                            result.add(child2TargetPathCopy);
                        }
                    }
                } else {
                    continue;
                }
            }
        }
        scannedNodes.remove(this);
        return result;
    }

    private boolean isSpecificTable(Table table, Table targetTable) {
        return ComparatorUtils.equals(table, targetTable);
    }

    private boolean isScanned(Set<BITablePathAnalyserNode> scannedNodes, BITablePathAnalyserNode currentNode) {
        return scannedNodes.contains(currentNode);
    }

    public void removePathNode(BITablePathAnalyserNode node) throws BIPathNodeAbsentException {
        if (contain(node)) {
            remove(node);
        } else {
            throw new BIPathNodeAbsentException();
        }
    }

    @Override
    protected void add(BITablePathAnalyserNode element) {
        super.add(element);
    }

    @Override
    protected Boolean contain(BITablePathAnalyserNode element) {
        return super.contain(element);
    }

    @Override
    protected void setContainer(Collection<BITablePathAnalyserNode> container) {
        super.setContainer(container);
    }

    @Override
    protected void useContent(BICollectionContainer targetContainer) {
        super.useContent(targetContainer);
    }

    @Override
    protected void remove(BITablePathAnalyserNode element) {
        super.remove(element);
    }

    @Override
    protected void clear() {
        super.clear();
    }

    @Override
    protected boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    protected int size() {
        return super.size();
    }

    @Override
    protected Set<BITablePathAnalyserNode> getContainer() {
        return super.getContainer();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BITablePathAnalyserNode)) {
            return false;
        }

        BITablePathAnalyserNode that = (BITablePathAnalyserNode) o;

        return ComparatorUtils.equals(currentNodeTable, that.currentNodeTable);

    }

    @Override
    public int hashCode() {
        return currentNodeTable.hashCode();
    }
}