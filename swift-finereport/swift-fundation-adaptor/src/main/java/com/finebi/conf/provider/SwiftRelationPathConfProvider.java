package com.finebi.conf.provider;

import com.finebi.base.constant.FineEngineType;
import com.finebi.common.service.engine.relation.AbstractDirectRelationPathManager;
import com.finebi.conf.constant.BICommonConstants;
import com.finebi.conf.structure.path.FineBusinessTableRelationPath;
import com.finebi.conf.structure.relation.FineBusinessTableRelation;
import com.fr.general.ComparatorUtils;
import com.fr.swift.util.Crasher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018/04/08
 * @description
 */
public class SwiftRelationPathConfProvider extends AbstractDirectRelationPathManager {

    @Override
    public List<FineBusinessTableRelationPath> getRelationPaths(String fromTable, String toTable) {
        List<FineBusinessTableRelationPath> configs = getAllRelationPaths();
        List<FineBusinessTableRelationPath> target = new ArrayList<FineBusinessTableRelationPath>();
        for (FineBusinessTableRelationPath path : configs) {
            List<FineBusinessTableRelation> relations = sortRelations(path);
            String firstTable = getPrimaryTableName(relations);
            String lastTable = getForeignTableName(relations);
            if (firstTable.equals(fromTable) && lastTable.equals(toTable)) {
                target.add(path);
            }
        }
        return target;
    }

    @Override
    public List<FineBusinessTableRelation> getRelationsByTableId(String tableId) {
        List<FineBusinessTableRelation> relations = getAllRelations();
        List<FineBusinessTableRelation> target = new ArrayList<FineBusinessTableRelation>();
        for (FineBusinessTableRelation relation : relations) {
            if (ComparatorUtils.equals(tableId, relation.getPrimaryBusinessTable().getName()) || ComparatorUtils.equals(tableId, relation.getForeignBusinessTable().getName())) {
                target.add(relation);
            }
        }
        return target;
    }

    @Override
    public List<FineBusinessTableRelation> getRelationsByTables(String primaryTableId, String foreignTableId) {
        List<FineBusinessTableRelation> relations = getAllRelations();
        List<FineBusinessTableRelation> target = new ArrayList<FineBusinessTableRelation>();
        for (FineBusinessTableRelation relation : relations) {
            String primaryTable;
            String foreignTable;
            if (relation.getRelationType() == BICommonConstants.RELATION_TYPE.MANY_TO_ONE) {
                primaryTable = relation.getForeignBusinessTable().getName();
                foreignTable = relation.getPrimaryBusinessTable().getName();
            } else {
                primaryTable = relation.getPrimaryBusinessTable().getName();
                foreignTable = relation.getForeignBusinessTable().getName();
            }
            if (ComparatorUtils.equals(primaryTable, primaryTableId) && ComparatorUtils.equals(foreignTable, foreignTableId)) {
                target.add(relation);
            }
        }
        return target;
    }

    @Override
    public boolean isRelationPathExist(String fromTableId, String toTableId) {
        List<FineBusinessTableRelation> relations = getRelationsByTables(fromTableId, toTableId);
        return !relations.isEmpty();
    }

    @Override
    public boolean isRelationExist(String primaryTableId, String foreignTableId) {
        return !getRelationsByTables(primaryTableId, foreignTableId).isEmpty();
    }

    @Override
    public FineEngineType getEngineType() {
        return FineEngineType.Cube;
    }

    private String getPrimaryTableName(List<FineBusinessTableRelation> relations) {
        FineBusinessTableRelation firstRelation = relations.get(0);
        return ComparatorUtils.equals(firstRelation.getRelationType(), BICommonConstants.RELATION_TYPE.MANY_TO_ONE) ?
                firstRelation.getForeignBusinessTable().getName() : firstRelation.getPrimaryBusinessTable().getName();
    }

    private String getForeignTableName(List<FineBusinessTableRelation> relations) {
        FineBusinessTableRelation firstRelation = relations.get(relations.size() - 1);
        return ComparatorUtils.equals(firstRelation.getRelationType(), BICommonConstants.RELATION_TYPE.MANY_TO_ONE) ?
                firstRelation.getPrimaryBusinessTable().getName() : firstRelation.getForeignBusinessTable().getName();
    }

    private List<FineBusinessTableRelation> sortRelations(FineBusinessTableRelationPath path) {
        List<FineBusinessTableRelation> relations = Collections.unmodifiableList(path.getFineBusinessTableRelations());
        if (relations.isEmpty()) {
            Crasher.crash(String.format("Path %s don't contain any relations!", path.getPathName()));
        }
        List<FineBusinessTableRelation> result = new ArrayList<FineBusinessTableRelation>();
        for (FineBusinessTableRelation relation : relations) {
            if (ComparatorUtils.equals(relation.getRelationType(), BICommonConstants.RELATION_TYPE.MANY_TO_ONE)) {
                result.add(0, relation);
            } else {
                result.add(relation);
            }
        }
        return result;
    }
}
