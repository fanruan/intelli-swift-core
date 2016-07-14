package com.fr.bi.etl.analysis.conf;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.json.JSONObject;

import java.util.Locale;
import java.util.Set;

/**
 * Created by 小灰灰 on 2015/12/15.
 */
public class AnalysisPackManager{
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

    public JSONObject createJSON(Locale locale) throws Exception {
        return set.createJSON(locale);
    }


    public Set<BusinessTable> getAllTables(){
        return set.getAllTables();
    }
}