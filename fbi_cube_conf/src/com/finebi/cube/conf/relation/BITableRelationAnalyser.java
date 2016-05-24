package com.finebi.cube.conf.relation;

import com.finebi.cube.conf.relation.relation.*;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.stable.exception.BIRelationAbsentException;
import com.fr.bi.stable.exception.BIRelationDuplicateException;
import com.fr.bi.stable.exception.BITableAbsentException;

import java.util.List;
import java.util.Map;

/**
 * 所有表的关系集合，包括主表集合和外键表
 * Created by Connery on 2016/1/12.
 */
@BIMandatedObject(factory = IFactoryService.CONF_XML, implement = BITableRelationAnalysisService.class)
public class BITableRelationAnalyser implements BITableRelationAnalysisService {

    private BIRelationContainerService relationContainerService;
    private BITable2ForeignRelationsContainer table2ForeignRelationsManager;
    private BITable2PrimaryRelationsContainer table2PrimaryRelationsManager;

    public BITableRelationAnalyser() {
        relationContainerService = new BIRelationContainerManager();
        table2PrimaryRelationsManager = new BITable2PrimaryRelationsContainer();
        table2ForeignRelationsManager = new BITable2ForeignRelationsContainer();
    }

    @Override
    public void addRelation(BITableRelation relation) throws BIRelationDuplicateException {
        synchronized (relationContainerService) {
            relationContainerService.addRelation(relation);
            table2ForeignRelationsManager.addForeignRelation(relation);
            table2PrimaryRelationsManager.addPrimaryRelation(relation);
        }
    }

    @Override
    public void removeRelation(BITableRelation relation) throws BIRelationAbsentException, BITableAbsentException {
        synchronized (relationContainerService) {
            relationContainerService.removeRelation(relation);
            table2ForeignRelationsManager.removeForeignRelation(relation);
            table2PrimaryRelationsManager.removePrimaryRelation(relation);
        }
    }

    @Override
    public boolean contain(BITableRelation relation) {
        synchronized (relationContainerService) {
            return relationContainerService.contain(relation);
        }
    }

    @Override
    public IRelationContainer getPrimaryRelation(BusinessTable table) throws BITableAbsentException {
        synchronized (relationContainerService) {
            if (table2PrimaryRelationsManager.containTable(table)) {
                return table2PrimaryRelationsManager.getTablePrimaryRelationContainer(table);
            } else {
                return new BIRelationContainer();
            }
        }
    }

    @Override
    public IRelationContainer getForeignRelation(BusinessTable table) throws BITableAbsentException {
        synchronized (relationContainerService) {
                return table2ForeignRelationsManager.getTableForeignRelationContainer(table);


        }
    }

    @Override
    public boolean containTablePrimaryRelation(BusinessTable table) {
        synchronized (relationContainerService) {
            return table2PrimaryRelationsManager.containTable(table);
        }
    }

    @Override
    public boolean containTableForeignRelation(BusinessTable table) {
        synchronized (relationContainerService) {
            return table2ForeignRelationsManager.containTable(table);
        }
    }

    @Override
    public void clear() {
        synchronized (relationContainerService) {
            table2ForeignRelationsManager.clear();
            table2PrimaryRelationsManager.clear();
            relationContainerService.clear();
        }
    }

    @Override
    public Boolean isChanged(BIRelationContainer targetRelationContainer) {
        return relationContainerService.isChanged(targetRelationContainer);
    }

    @Override
    public BIRelationContainer getRelationContainer() {
        return relationContainerService.getRelationContainer();
    }

    @Override
    public List<BITableRelation> getRelation(BusinessTable primaryTable, BusinessTable foreignTable) throws BITableAbsentException {
        return table2PrimaryRelationsManager.getRelation(primaryTable, foreignTable);

    }

    @Override
    public Map<BusinessTable, IRelationContainer> getAllTable2PrimaryRelation() {
        return this.table2PrimaryRelationsManager.getAllTable2Relation();
    }

    @Override
    public Map<BusinessTable, IRelationContainer> getAllTable2ForeignRelation() {
        return this.table2ForeignRelationsManager.getAllTable2Relation();
    }
}