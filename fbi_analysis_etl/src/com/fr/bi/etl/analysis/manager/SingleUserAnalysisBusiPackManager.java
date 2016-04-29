package com.fr.bi.etl.analysis.manager;

import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.etl.analysis.conf.AnalysisBusiTable;
import com.fr.bi.etl.analysis.conf.AnalysisPackManager;
import com.fr.bi.stable.exception.BITableAbsentException;

/**
 * Created by 小灰灰 on 2015/12/11.
 */
@BIMandatedObject(factory = IFactoryService.CONF_XML, implement = SingleUserAnalysisBusiPackManager.class)
public class SingleUserAnalysisBusiPackManager  {


    private long userId;

    private AnalysisPackManager pack;

    public SingleUserAnalysisBusiPackManager(long userId) {
        this.userId = userId;
        this.pack = new AnalysisPackManager(userId);
    }

    public void addTable(AnalysisBusiTable table) {
        pack.addTable(table);
    }

    public void removeTable(String tableId) {
        pack.removeTable(tableId);
    }

    public AnalysisBusiTable getTable(String tableId) throws BITableAbsentException {
        return pack.getTable(tableId);
    }


}