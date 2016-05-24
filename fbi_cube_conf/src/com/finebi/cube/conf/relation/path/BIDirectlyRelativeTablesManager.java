package com.finebi.cube.conf.relation.path;

import com.fr.bi.common.container.BIHashMapContainer;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableRelation;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by Connery on 2016/1/14.
 */
abstract class BIDirectlyRelativeTablesManager extends BIHashMapContainer<BusinessTable, BIDirectlyRelativeTableContainer> {
    BIIndirectlyRelativeTablesManager indirectTablesManager;

    public BIDirectlyRelativeTablesManager(BIIndirectlyRelativeTablesManager indirectTablesManager) {
        this.indirectTablesManager = indirectTablesManager;
    }

    protected abstract BIDirectlyRelativeTableContainer generateDirectTableContainer(BusinessTable table);

    private BIDirectlyRelativeTableContainer getDirectTableContainer(BusinessTable table) {
        try {
            return getValue(table);
        } catch (BIKeyAbsentException e) {
            BIDirectlyRelativeTableContainer container = generateDirectTableContainer(table);
            try {
                putKeyValue(table, container);
            } catch (BIKeyDuplicateException ignore) {

            }
            return container;
        }
    }

    public void addBITableRelation(BITableRelation relation) {
        BusinessTable primaryTable = relation.getPrimaryTable();
        BusinessTable foreignTable = relation.getForeignTable();
        BIDirectlyRelativeTableContainer primaryDirectContainer = getDirectTableContainer(primaryTable);
        BIDirectlyRelativeTableContainer foreignDirectContainer = getDirectTableContainer(foreignTable);
        buildDirectRelation(primaryDirectContainer, foreignDirectContainer);
    }
    public boolean containRelation(BITableRelation relation) {
        BusinessTable primaryTable = relation.getPrimaryTable();
        BusinessTable foreignTable = relation.getForeignTable();
        BIDirectlyRelativeTableContainer primaryDirectContainer = getDirectTableContainer(primaryTable);
        BIDirectlyRelativeTableContainer foreignDirectContainer = getDirectTableContainer(foreignTable);
        return foreignDirectContainer.containDirectlyRelation(primaryDirectContainer);
    }
    public void removeBITableRelation(BITableRelation relation) {
        BusinessTable primaryTable = relation.getPrimaryTable();
        BusinessTable foreignTable = relation.getForeignTable();
        BIDirectlyRelativeTableContainer primaryDirectContainer = getDirectTableContainer(primaryTable);
        BIDirectlyRelativeTableContainer foreignDirectContainer = getDirectTableContainer(foreignTable);
        demolishDirectRelation(primaryDirectContainer, foreignDirectContainer);
    }

    public abstract void demolishDirectRelation(BIDirectlyRelativeTableContainer primaryDirectContainer, BIDirectlyRelativeTableContainer foreignDirectContainer);

    public abstract void buildDirectRelation(BIDirectlyRelativeTableContainer primaryDirectContainer, BIDirectlyRelativeTableContainer foreignDirectContainer);

    BITableContainer getIndirectTable(BusinessTable table) {
        return getDirectTableContainer(table).getRelativeTable(new HashSet<BIDirectlyRelativeTableContainer>());
    }

    protected void updateIndirectContainer() {
        Iterator<BusinessTable> it = keySet().iterator();
        while (it.hasNext()) {
            BusinessTable table = it.next();
            BITableContainer tableContainer = getIndirectTable(table);
            indirectTablesManager.updateTable(table, tableContainer);
        }
    }
}