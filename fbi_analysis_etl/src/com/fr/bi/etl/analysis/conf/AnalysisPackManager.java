package com.fr.bi.etl.analysis.conf;

import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.json.JSONCreator;
import com.fr.json.JSONObject;

/**
 * Created by 小灰灰 on 2015/12/15.
 */
public class AnalysisPackManager implements JSONCreator{
    private static final String XML_TAG = "AnalysisPackManager";
    private AnalysisETLPackageSet set;

    public AnalysisPackManager(long userId) {
        set = new AnalysisETLPackageSet(userId);
    }


    public AnalysisETLPackageSet getAnylysis() {
        return set;
    }

    public void removeTable(String tableId) {
        set.removeTable(new BITableID(tableId));
    }

    public void addTable(AnalysisBusiTable table) {
        set.addTable(table);
    }

    public AnalysisBusiTable getTable(String tableId) throws BITableAbsentException {
        return set.getTable(tableId);
    }

    public JSONObject createJSON() throws Exception {
        return set.createJSON();
    }
}