package com.fr.bi.etl.analysis.manager;

import com.fr.bi.etl.analysis.conf.AnalysisBusiTable;
import com.fr.bi.stable.exception.BITableAbsentException;

/**
 * Created by 小灰灰 on 2015/12/11.
 */
public interface BIAnalysisBusiPackManagerProvider extends BISystemPackageConfigurationProvider {

    String XML_TAG = "BIAnalysisBusiPackManager";

    void addTable(AnalysisBusiTable table);

    void removeTable(String tableId, long userId);

    AnalysisBusiTable getTable(String tableId, long userId) throws BITableAbsentException;

}