package com.fr.bi.conf.base.relation.path;

import com.fr.bi.common.container.BIHashMapContainer;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.fr.bi.stable.relation.BITableRelation;

/**
 * Created by Connery on 2016/1/14.
 */
abstract class BIIndirectlyRelativeTablesManager extends BIHashMapContainer<Table, BIIndirectlyRelativeTableContainer> {
    BIDirectlyRelativeTablesManager biDirectlyRelativeTablesManager;

    public BIIndirectlyRelativeTablesManager() {
        biDirectlyRelativeTablesManager = generateDirectlyRelativeManager();
    }

    protected abstract BIDirectlyRelativeTablesManager generateDirectlyRelativeManager();

    protected abstract BIIndirectlyRelativeTableContainer generateIndirectlyRelativeContainer();

    public void addBITableRelation(BITableRelation tableRelation) {
        biDirectlyRelativeTablesManager.addBITableRelation(tableRelation);
        biDirectlyRelativeTablesManager.updateIndirectContainer();
    }

    public void removeBITableRelation(BITableRelation tableRelation) {
        biDirectlyRelativeTablesManager.removeBITableRelation(tableRelation);
        biDirectlyRelativeTablesManager.updateIndirectContainer();
    }
    public boolean containBITableRelation(BITableRelation tableRelation) {
      return   biDirectlyRelativeTablesManager.containRelation(tableRelation);
    }
    protected void updateTable(Table table, BITableContainer container) {
        if (containsKey(table)) {
            try {
                BIIndirectlyRelativeTableContainer indirectlyTableContainer = getSpecificTableIndirectContainer(table);
                indirectlyTableContainer.clear();
                indirectlyTableContainer.addTableContainer(container);
            } catch (BITableAbsentException ignore) {

            }
        } else {
            BIIndirectlyRelativeTableContainer indirectlyTableContainer = generateIndirectlyRelativeContainer();
            indirectlyTableContainer.addTableContainer(container);
            try {
                putKeyValue(table, indirectlyTableContainer);
            } catch (BIKeyDuplicateException ignore) {

            }
        }
    }

    protected BIIndirectlyRelativeTableContainer getSpecificTableIndirectContainer(Table table) throws BITableAbsentException {
        try {
            return getValue(table);
        } catch (BIKeyAbsentException ignore) {
            throw new BITableAbsentException();
        }
    }
}