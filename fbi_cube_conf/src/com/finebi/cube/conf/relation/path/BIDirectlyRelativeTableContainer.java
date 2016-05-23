package com.finebi.cube.conf.relation.path;

import com.finebi.cube.conf.table.IBusinessTable;
import com.fr.bi.common.container.BISetContainer;
import com.fr.bi.stable.exception.BITableDuplicateException;
import com.fr.general.ComparatorUtils;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by Connery on 2016/1/14.
 */
class BIDirectlyRelativeTableContainer extends BISetContainer<BIDirectlyRelativeTableContainer> {
    IBusinessTable currentTable;

    public BIDirectlyRelativeTableContainer(IBusinessTable currentTable) {
        this.currentTable = currentTable;
    }

    public void addDirectlyRelativeContainer(BIDirectlyRelativeTableContainer directlyRelativeTableContainer) {
        if (!containDirectlyRelation(directlyRelativeTableContainer)) {
            add(directlyRelativeTableContainer);
        }
    }

    public void removeDirectlyRelativeContainer(BIDirectlyRelativeTableContainer directlyRelativeTableContainer) {
        if (containDirectlyRelation(directlyRelativeTableContainer)) {
            remove(directlyRelativeTableContainer);
        }
    }

    public Boolean containDirectlyRelation(BIDirectlyRelativeTableContainer sonTableContainer) {
        return contain(sonTableContainer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BIDirectlyRelativeTableContainer)) {
            return false;
        }

        BIDirectlyRelativeTableContainer that = (BIDirectlyRelativeTableContainer) o;

        return !(currentTable != null ? !ComparatorUtils.equals(currentTable, that.currentTable) : that.currentTable != null);

    }

    @Override
    public int hashCode() {
        return currentTable != null ? currentTable.hashCode() : 0;
    }

    BITableContainer getRelativeTable(Set<BIDirectlyRelativeTableContainer> scannedTable) {
        scannedTable.add(this);
        BITableContainer relativeTableContainer = new BITableContainer();
        Iterator<BIDirectlyRelativeTableContainer> sonsIt = container.iterator();
        while (sonsIt.hasNext()) {
            BIDirectlyRelativeTableContainer son = sonsIt.next();
            if (!isScanned(scannedTable, son)) {
                BITableContainer indirectlyRelative = son.getRelativeTable(scannedTable);
                try {
                    relativeTableContainer.addBITable(son.currentTable);
                    relativeTableContainer.addTableContainer(indirectlyRelative);
                } catch (BITableDuplicateException ignore) {

                }
            }
        }
        return relativeTableContainer;
    }

    private boolean isScanned(Set<BIDirectlyRelativeTableContainer> scannedTable, BIDirectlyRelativeTableContainer target) {
        return scannedTable.contains(target);
    }
}