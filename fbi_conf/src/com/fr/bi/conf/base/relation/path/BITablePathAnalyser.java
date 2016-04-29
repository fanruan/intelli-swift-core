package com.fr.bi.conf.base.relation.path;

import com.fr.bi.conf.base.relation.BITableRelationAnalysisService;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.exception.*;
import com.fr.bi.stable.relation.BITableRelationPath;
import com.fr.bi.stable.utils.code.BILogger;

import java.util.HashSet;
import java.util.Set;

/**
 * 当前节点的路径分析器
 * Created by Connery on 2016/1/13.
 */
public class BITablePathAnalyser {
    private BITablePathAnalyserNode currentNode;

    public BITablePathAnalyser(Table currentTable, BITableRelationAnalysisService biTableRelationAnalyser) {
        currentNode = new BITablePathAnalyserNode(currentTable, biTableRelationAnalyser);
        currentNode.setCurrentPath(this);

    }

    public Set<BITableRelationPath> getAllRelationPath(Table targetTailTable)
            throws BITableAbsentException, BITableRelationConfusionException, BITablePathConfusionException {
        return currentNode.getAllRelationPath(new HashSet<BITablePathAnalyserNode>(), targetTailTable);
    }

    public void buildPathRelation(BITablePathAnalyser childPathAnalyser) {
        try {
            currentNode.buildPathNodeRelation(childPathAnalyser.currentNode);
        } catch (BIPathNodeDuplicateException ignore) {
            BILogger.getLogger().error(ignore.getMessage(), ignore);
        }
    }
    public void destoryPathRelation(BITablePathAnalyser childPathAnalyser) {
        try {
            currentNode.removePathNode(childPathAnalyser.currentNode);
        } catch (BIPathNodeAbsentException ignore) {
            BILogger.getLogger().error(ignore.getMessage(), ignore);
        }
    }
    public Boolean containPathRelation(BITablePathAnalyser childPathAnalyser) {
        return currentNode.containChildNode(childPathAnalyser.currentNode);
    }

}