package com.finebi.cube.conf.relation.relation;

import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.fr.bi.stable.exception.BIRelationAbsentException;
import com.fr.bi.stable.exception.BIRelationDuplicateException;
import com.fr.general.ComparatorUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Connery on 2016/1/12.
 */
public class BIRelationContainerManager implements BIRelationContainerService {
    private BIRelationContainer allRelations;

    public BIRelationContainerManager() {
        allRelations = new BIRelationContainer();
    }

    @Override
    public void addRelation(BITableRelation relation) throws BIRelationDuplicateException {
        boolean isSelfCircle = isSelfCircleRelation(relation);
        if (!contain(relation) && !isSelfCircle) {
            allRelations.add(relation);
        } else {
            throw new BIRelationDuplicateException();
        }
    }

    @Override
    public void removeRelation(BITableRelation relation) throws BIRelationAbsentException {
        if (contain(relation)) {
            allRelations.remove(relation);
        }
    }

    @Override
    public boolean contain(BITableRelation relation) {
        return allRelations.contain(relation);
    }

    @Override
    public BIRelationContainer getRelationContainer() {
        BIRelationContainer relations = new BIRelationContainer();
        Iterator<BITableRelation> iterator = allRelations.getContainer().iterator();
        while (iterator.hasNext()) {
            BITableRelation relation = iterator.next();
            boolean isSelfCircleRelation = isSelfCircleRelation(relation);
            if (!isSelfCircleRelation) {
                relations.add(relation);
            }
        }
        return relations;
    }

    private boolean isSelfCircleRelation(BITableRelation relation) {
        return ComparatorUtils.equals(relation.getPrimaryTable().getTableSource().getSourceID(), relation.getForeignTable().getTableSource().getSourceID());
    }

    @Override
    public void clear() {
        synchronized (allRelations) {
            allRelations.clear();
        }
    }

    @Override
    public Boolean isChanged(BIRelationContainer targetContainer) {
        if (targetContainer.size() != allRelations.size()) {
            return true;
        } else {
            Iterator<BITableRelation> it = allRelations.getContainer().iterator();
            while (it.hasNext()) {
                if (!targetContainer.contain(it.next())) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public List<BITableRelation> getRelationSpecificForeignTable(BusinessTable foreignTable) {
        Iterator<BITableRelation> relationIterator = allRelations.getContainer().iterator();
        List<BITableRelation> result = new ArrayList<BITableRelation>();
        while (relationIterator.hasNext()) {
            BITableRelation relation = relationIterator.next();
            if (ComparatorUtils.equals(relation.getForeignTable(), foreignTable)) {
                result.add(relation);
            }
        }
        return result;
    }

    @Override
    public List<BITableRelation> getRelationSpecificPrimaryTable(BusinessTable primaryTable) {
        Iterator<BITableRelation> relationIterator = allRelations.getContainer().iterator();
        List<BITableRelation> result = new ArrayList<BITableRelation>();
        while (relationIterator.hasNext()) {
            BITableRelation relation = relationIterator.next();
            if (ComparatorUtils.equals(relation.getPrimaryTable(), primaryTable)) {
                result.add(relation);
            }
        }
        return result;
    }
}