package com.fr.bi.sql.analysis.conf;


import com.finebi.cube.conf.pack.data.BIPackageID;
import com.finebi.cube.conf.pack.imp.BIPackageContainer;
import com.fr.bi.conf.data.pack.exception.BIPackageAbsentException;
import com.fr.bi.conf.data.pack.exception.BIPackageDuplicateException;
import com.fr.bi.sql.analysis.Constants;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.Inter;
import com.fr.json.JSONObject;

import java.util.Locale;

/**
 * Created by 小灰灰 on 2015/12/23.
 */
public class AnalysisSQLPackageSet extends BIPackageContainer {
    private static final String PACK_NAME = Inter.getLocText("BI-MYSQL");
    private transient AnalysisSQLBusiPack pack;

    public AnalysisSQLPackageSet(long userId) {
        super(userId);
    }

    protected AnalysisSQLBusiPack createPackage(String id, String pack) {
        return new AnalysisSQLBusiPack(id, pack, user, System.currentTimeMillis());
    }

    private AnalysisSQLBusiPack getPack() {
        if (pack != null) {
            return pack;
        }
        synchronized (container) {
            try {
                pack = (AnalysisSQLBusiPack) getPackage(new BIPackageID(Constants.PACK_ID));
            } catch (BIPackageAbsentException ignore_) {
                BILogger.getLogger().error(ignore_.getMessage());
            }
            if (pack == null) {
                try {
                    pack = createPackage(Constants.PACK_ID, PACK_NAME);
                    addPackage(pack);
                } catch (BIPackageDuplicateException ignore) {

                }

            }
            return pack;
        }
    }

    public void removeTable(BITableID tableId) {
        getPack().removeBusinessTableByID(tableId);
    }

    public void addTable(AnalysisSQLBusiTable table) {
        removeTable(table.getID());
        getPack().addBusinessTable(table);
    }

    public AnalysisSQLBusiTable getTable(String tableId) throws BITableAbsentException {
        return getPack().getSpecificTable(new BITableID(tableId));
    }

    public JSONObject createJSON(Locale locale) throws Exception {
        JSONObject jo = new JSONObject();
        JSONObject pack = getPack().createJSON();
        if (pack.getJSONArray("tables").length() > 0){
            pack.put("name", Inter.getLocText(PACK_NAME, locale));
            jo.put(Constants.PACK_ID, pack);
        }
        return jo;
    }

}