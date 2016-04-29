package com.fr.bi.etl.analysis.conf;

import com.fr.bi.conf.base.pack.BIPackageContainer;
import com.fr.bi.conf.base.pack.data.BIPackageID;
import com.fr.bi.conf.data.pack.exception.BIPackageAbsentException;
import com.fr.bi.conf.data.pack.exception.BIPackageDuplicateException;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.exception.BITableAbsentException;

import java.util.UUID;

/**
 * Created by 小灰灰 on 2015/12/23.
 */
public class AnalysisETLPackageSet extends BIPackageContainer {
    private static final String PACK_NAME = "MYETL";
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
            if (pack == null) {
                try {
                    addPackage(createPackage(UUID.randomUUID().toString(), PACK_NAME));
                } catch (BIPackageDuplicateException ignore) {
                    try {
                        pack = (AnalysisETLBusiPack) getPackage(new BIPackageID(PACK_NAME));
                    } catch (BIPackageAbsentException ignore_) {

                    }

                }

            }
            return pack;
        }
    }

    public void removeTable(String tableId) {
        getPack().removeBusinessTable(new AnalysisBusiTable(tableId, user.getUserId()));
    }

    public void addTable(AnalysisBusiTable table) {
        getPack().addBusinessTable(table);
    }

    public AnalysisBusiTable getTable(String tableId) throws BITableAbsentException {
        return getPack().getSpecificTable(new BITableID(tableId));
    }

}