package com.fr.bi.conf.base.relation.relation;

import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.exception.BIRelationAbsentException;
import com.fr.bi.stable.exception.BIRelationDuplicateException;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.stable.relation.BITableRelation;

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

    public IRelationContainer getTableForeignRelationContainer(Table table) throws BITableAbsentException {
        return getTableRelationContainer(table);
    }


}