package com.finebi.cube.conf.relation.relation;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.common.container.BIHashMapContainer;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.fr.bi.stable.exception.BIRelationAbsentException;
import com.fr.bi.stable.exception.BIRelationDuplicateException;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.finebi.cube.relation.BITableRelation;
import com.fr.bi.stable.utils.code.BILogger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 表的关联管理
 * Created by Connery on 2016/1/12.
 */
public class BITable2RelationsContainer extends BIHashMapContainer<BusinessTable, BIRelationContainerService> {
    @Override
    protected Map<BusinessTable, BIRelationContainerService> initContainer() {
        return new HashMap<BusinessTable, BIRelationContainerService>();
    }

    @Override
    protected BIRelationContainerService generateAbsentValue(BusinessTable key) {
        return null;
    }

    protected void addRelation(BusinessTable table, BITableRelation tableRelation) throws BIRelationDuplicateException {
        synchronized (container) {
            if (!containTable(table)) {
                try {
                    BIRelationContainerManager manager = new BIRelationContainerManager();
                    manager.addRelation(tableRelation);
                    putKeyValue(table, manager);
                } catch (BIKeyDuplicateException ignore) {
                    BILogger.getLogger().error(ignore.getMessage(), ignore);
                }
            } else {
                try {
                    BIRelationContainerService manager = getTableRelationContainerService(table);
                    manager.addRelation(tableRelation);
                } catch (BITableAbsentException ignore) {
                    BILogger.getLogger().error(ignore.getMessage(), ignore);
                }
            }
        }
    }

    protected void removeRelation(BusinessTable table, BITableRelation tableRelation) throws BIRelationAbsentException, BITableAbsentException {
        synchronized (container) {
            if (containTable(table)) {

                BIRelationContainerService manager = getTableRelationContainerService(table);
                manager.removeRelation(tableRelation);

            } else {
                throw new BITableAbsentException();
            }
        }
    }

    protected IRelationContainer getTableRelationContainer(BusinessTable table) throws BITableAbsentException {
        synchronized (container) {
            return getTableRelationContainerService(table).getRelationContainer();
        }
    }

    protected BIRelationContainerService getTableRelationContainerService(BusinessTable table) throws BITableAbsentException {
        synchronized (container) {
            try {
                return getValue(table);
            } catch (BIKeyAbsentException ignore) {
                throw new BITableAbsentException();
            }
        }
    }

    public Boolean containTable(BusinessTable table) {
        synchronized (container) {
            return containsKey(table);
        }
    }

    public void clear() {
        synchronized (container) {
            container.clear();
        }
    }

    public Map<BusinessTable, IRelationContainer> getAllTable2Relation() {
        Map<BusinessTable, IRelationContainer> result = new HashMap<BusinessTable, IRelationContainer>();
        Iterator<Map.Entry<BusinessTable, BIRelationContainerService>> it = getContainer().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BusinessTable, BIRelationContainerService> entry = it.next();
            BusinessTable table = entry.getKey();
            BIRelationContainerService service = entry.getValue();
            result.put(table, service.getRelationContainer());
        }
        return result;
    }
}