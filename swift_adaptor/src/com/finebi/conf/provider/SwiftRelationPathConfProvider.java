package com.finebi.conf.provider;

import com.finebi.base.constant.FineEngineType;
import com.finebi.common.service.engine.relation.AbstractDirectRelationPathManager;
import com.finebi.conf.constant.BICommonConstants;
import com.finebi.conf.structure.path.FineBusinessTableRelationPath;
import com.finebi.conf.structure.relation.FineBusinessTableRelation;

import java.util.ArrayList;
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
    public FineEngineType getEngineType() {
        return FineEngineType.Cube;
    }
}
