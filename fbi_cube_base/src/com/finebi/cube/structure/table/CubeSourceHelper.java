package com.finebi.cube.structure.table;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.structure.BITableKey;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.fs.control.UserControl;

import java.util.Set;

/**
 * Created by kary on 2016/10/20.
 */
public class CubeSourceHelper {
    public static CubeTableSource getSource(BITableKey biTableKey) {
        for (BusinessTable businessTable : BICubeConfigureCenter.getPackageManager().getAllTables(UserControl.getInstance().getSuperManagerID())) {
            for (Integer integer : businessTable.getTableSource().createGenerateTablesMap().keySet()) {
                Set<CubeTableSource> sources = businessTable.getTableSource().createGenerateTablesMap().get(integer);
                for (CubeTableSource source : sources) {
                    if (source.getSourceID().equals(biTableKey.getSourceID())) {
                        return source;
                    }

                }
            }
        }

        return null;
    }
}
