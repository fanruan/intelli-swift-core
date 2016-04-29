package com.fr.bi.conf.base.relation.relation;

import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.exception.BIRelationAbsentException;
import com.fr.bi.stable.exception.BIRelationDuplicateException;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.stable.relation.BITableRelation;

import java.util.List;

/**
 * 表到关联的集合
 * 表为关联的主键表
 * Created by Connery on 2016/1/12.
 */
public class BITable2PrimaryRelationsContainer extends BITable2RelationsContainer {
    public void addPrimaryRelation(BITableRelation tableRelation) throws BIRelationDuplicateException {
        addRelation(tableRelation.getPrimaryTable(), tableRelation);
    }

    public void removePrimaryRelation(BITableRelation tableRelation) throws BIRelationAbsentException, BITableAbsentException {
        synchronized (container) {
            removeRelation(tableRelation.getPrimaryTable(), tableRelation);
        }
    }

    public IRelationContainer getTablePrimaryRelationContainer(Table table) throws BITableAbsentException {
        return getTableRelationContainer(table);
    }

    public List<BITableRelation> getRelation(Table primaryTable, Table foreignTable) throws BITableAbsentException {
        BIRelationContainerService relationContainer = getTableRelationContainerService(primaryTable);
        List<BITableRelation> relations = relationContainer.getRelationSpecificForeignTable(foreignTable);
        /**
         * 关联中包含字段，所有两张表之间可能有多个关联
         */
        return relations;

    }
}