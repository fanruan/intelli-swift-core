package com.finebi.cube.conf.relation.relation;

import com.finebi.cube.conf.table.IBusinessTable;
import com.fr.bi.stable.exception.BIRelationAbsentException;
import com.fr.bi.stable.exception.BIRelationDuplicateException;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.finebi.cube.relation.BITableRelation;

/**
 * 表到关联的集合
 * 表为关联的外键表
 * Created by Connery on 2016/1/12.
 */
public class BITable2ForeignRelationsContainer extends BITable2RelationsContainer {
    public void addForeignRelation(BITableRelation tableRelation) throws BIRelationDuplicateException {
        addRelation(tableRelation.getForeignTable(), tableRelation);
    }

    public void removeForeignRelation(BITableRelation tableRelation) throws BIRelationAbsentException, BITableAbsentException {
        synchronized (container) {
            removeRelation(tableRelation.getForeignTable(), tableRelation);
        }
    }

    public IRelationContainer getTableForeignRelationContainer(IBusinessTable table) throws BITableAbsentException {
        return getTableRelationContainer(table);
    }


}