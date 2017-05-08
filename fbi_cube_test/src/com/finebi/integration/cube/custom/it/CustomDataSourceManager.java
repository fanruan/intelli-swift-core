package com.finebi.integration.cube.custom.it;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.datasource.BIDataSourceManager;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.fs.control.UserControl;

import java.util.Set;

/**
 * Created by Lucifer on 2017-3-20.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class CustomDataSourceManager extends BIDataSourceManager {

    public CustomDataSourceManager() {
    }

    @Override
    public CubeTableSource getTableSource(BusinessTable businessTable) throws BIKeyAbsentException {
        Set<BusinessTable> set = BICubeConfigureCenter.getPackageManager().getAllTables(UserControl.getInstance().getSuperManagerID());
        for (BusinessTable businessTable1 : set) {
            if (businessTable.getID().equals(businessTable1.getID())) {
                return businessTable1.getTableSource();
            }
        }
        return null;
    }
}
