package com.fr.bi.etl.analysis.conf;

import com.fr.bi.conf.base.pack.BIPackageContainer;
import com.fr.bi.conf.base.pack.data.BIPackageID;
import com.fr.bi.conf.data.pack.exception.BIPackageAbsentException;
import com.fr.bi.conf.data.pack.exception.BIPackageDuplicateException;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.stable.utils.code.BILogger;

/**
 * Created by 小灰灰 on 2015/12/23.
 */
public class AnalysisETLPackageSet extends BIPackageContainer {
    private static final String PACK_NAME = "MYETL";
    private static final String PACK_ID = "ff8556cd-2e94-4876-b059-947cfe08aced";
    private transient AnalysisETLBusiPack pack;

    public AnalysisETLPackageSet(long userId) {
        super(userId);
    }

    protected AnalysisETLBusiPack createPackage() {
        return new AnalysisETLBusiPack("", "", user, System.currentTimeMillis());
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

}