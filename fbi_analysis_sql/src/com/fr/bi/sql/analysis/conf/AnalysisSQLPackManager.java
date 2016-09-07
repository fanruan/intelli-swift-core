package com.fr.bi.sql.analysis.conf;

import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.json.JSONObject;

import java.util.Locale;

/**
 * Created by 小灰灰 on 2015/12/15.
 */
public class AnalysisSQLPackManager {
    private static final String XML_TAG = "AnalysisSQLPackManager";
    private AnalysisSQLPackageSet set;

    public AnalysisSQLPackManager(long userId) {
        set = new AnalysisSQLPackageSet(userId);
    }


    public AnalysisSQLPackageSet getAnylysis() {
        return set;
    }

    public void removeTable(String tableId) {
        set.removeTable(new BITableID(tableId));
    }

    public void addTable(AnalysisSQLBusiTable table) {
        set.addTable(table);
    }

    public AnalysisSQLBusiTable getTable(String tableId) throws BITableAbsentException {
        return set.getTable(tableId);
    }

    public JSONObject createJSON(Locale locale) throws Exception {
        return set.createJSON(locale);
    }
}