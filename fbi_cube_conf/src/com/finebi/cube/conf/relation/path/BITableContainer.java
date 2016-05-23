package com.finebi.cube.conf.relation.path;

import com.fr.bi.common.container.BISetContainer;
import com.finebi.cube.conf.table.IBusinessTable;
import com.fr.bi.stable.exception.BITableDuplicateException;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.Set;

/**
 * Created by Connery on 2016/1/13.
 */
public class BITableContainer extends BISetContainer<IBusinessTable> {

    protected void addBITable(IBusinessTable table) throws BITableDuplicateException {
        if (!contain(table)) {
            add(table);
        } else {
            throw new BITableDuplicateException();
        }
    }

    @Override
    protected Boolean contain(IBusinessTable element) {
        return super.contain(element);
    }

    @Override
    public Set<IBusinessTable> getContainer() {
        return super.getContainer();
    }

    public void addTableContainer(BITableContainer biTableContainer) {
        BINonValueUtils.checkNull(biTableContainer);
        container.addAll(biTableContainer.container);
    }

    @Override
    protected void clear() {
        super.clear();
    }


}