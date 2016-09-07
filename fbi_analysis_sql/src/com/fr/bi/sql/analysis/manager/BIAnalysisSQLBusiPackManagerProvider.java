package com.fr.bi.sql.analysis.manager;

import com.finebi.cube.conf.BISystemPackageConfigurationProvider;
import com.fr.bi.sql.analysis.conf.AnalysisSQLBusiTable;
import com.fr.bi.stable.exception.BITableAbsentException;

/**
 * Created by 小灰灰 on 2015/12/11.
 */
public interface BIAnalysisSQLBusiPackManagerProvider extends BISystemPackageConfigurationProvider {

    void addTable(AnalysisSQLBusiTable table);

    void removeTable(String tableId, long userId);

    AnalysisSQLBusiTable getTable(String tableId, long userId) throws BITableAbsentException;

}