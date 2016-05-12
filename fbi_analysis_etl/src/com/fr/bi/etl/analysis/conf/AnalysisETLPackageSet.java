package com.fr.bi.etl.analysis.conf;

import com.fr.bi.conf.base.pack.BIPackageContainer;
import com.fr.bi.conf.base.pack.data.BIPackageID;
import com.fr.bi.conf.data.pack.exception.BIPackageAbsentException;
import com.fr.bi.conf.data.pack.exception.BIPackageDuplicateException;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.Inter;
import com.fr.json.JSONObject;

import java.util.Locale;

/**
 * Created by 小灰灰 on 2015/12/23.
 */
public class AnalysisETLPackageSet extends BIPackageContainer {
    private static final String PACK_NAME = Inter.getLocText("BI-MYETL");
    private static final String PACK_ID = "myetlidfe62a664c";
    private transient AnalysisETLBusiPack pack;

    public AnalysisETLPackageSet(long userId) {
        super(userId);
    }

    protected AnalysisETLBusiPack createPackage(String id, String pack) {
        return new AnalysisETLBusiPack(id, pack, user, System.currentTimeMillis());
    }

    private AnalysisETLBusiPack getPack() {
        if (pack != null) {
            return pack;
        }
        synchronized (container) {
            try {
                pack = (AnalysisETLBusiPack) getPackage(new BIPackageID(PACK_ID));
            } catch (BIPackageAbsentException ignore_) {
                BILogger.getLogger().error(ignore_.getMessage());
            }
            if (pack == null) {
                try {
                    pack = createPackage(PACK_ID, PACK_NAME);
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

    public void addTable(AnalysisBusiTable table) {
        removeTable(table.getID());
        getPack().addBusinessTable(table);
    }

    public AnalysisBusiTable getTable(String tableId) throws BITableAbsentException {
        return getPack().getSpecificTable(new BITableID(tableId));
    }

    public JSONObject createJSON(Locale locale) throws Exception {
        JSONObject jo = new JSONObject();
        JSONObject pack = getPack().createJSON();
        if (pack.getJSONArray("tables").length() > 0){
            pack.put("name", Inter.getLocText(PACK_NAME, locale));
            jo.put(PACK_ID, pack);
        }
        return jo;
    }

}