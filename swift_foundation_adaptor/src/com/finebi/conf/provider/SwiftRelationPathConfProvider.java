package com.finebi.conf.provider;

import com.finebi.base.constant.FineEngineType;
import com.finebi.common.service.engine.relation.AbstractDirectRelationPathManager;
import com.finebi.conf.constant.BICommonConstants;
import com.finebi.conf.structure.path.FineBusinessTableRelationPath;
import com.finebi.conf.structure.relation.FineBusinessTableRelation;
import com.fr.general.ComparatorUtils;
import com.fr.swift.driver.SwiftDriverRegister;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/04/08
 * @description
 */
public class SwiftRelationPathConfProvider extends AbstractDirectRelationPathManager {


    public SwiftRelationPathConfProvider() {
        SwiftDriverRegister.registerIfNeed();
    }

    @Override
    public List<FineBusinessTableRelationPath> getRelationPaths(String fromTable, String toTable) {
        List<FineBusinessTableRelationPath> configs = getAllRelationPaths();
        List<FineBusinessTableRelationPath> target = new ArrayList<FineBusinessTableRelationPath>();
        for (FineBusinessTableRelationPath path : configs) {
            String firstTable;
            String lastTable;
            List<FineBusinessTableRelation> relations = path.getFineBusinessTableRelations();
            FineBusinessTableRelation firstRelation = relations.get(0);
            FineBusinessTableRelation lastRelation = relations.get(relations.size() - 1);
            if (firstRelation.getRelationType() == BICommonConstants.RELATION_TYPE.MANY_TO_ONE) {
                firstTable = firstRelation.getForeignBusinessTable().getName();
            } else {
                firstTable = firstRelation.getPrimaryBusinessTable().getName();
            }
            if (lastRelation.getRelationType() == BICommonConstants.RELATION_TYPE.MANY_TO_ONE) {
                lastTable = firstRelation.getPrimaryBusinessTable().getName();
            } else {
                lastTable = firstRelation.getForeignBusinessTable().getName();
            }
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
}
