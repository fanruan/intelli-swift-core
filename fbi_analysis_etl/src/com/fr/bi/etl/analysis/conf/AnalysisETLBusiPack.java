package com.fr.bi.etl.analysis.conf;

import com.fr.bi.base.BIUser;
import com.fr.bi.conf.base.pack.data.BIBusinessPackage;
import com.fr.bi.conf.base.pack.data.BIPackageID;
import com.fr.bi.conf.base.pack.data.BIPackageName;

/**
 * Created by 小灰灰 on 2015/12/23.
 */
public class AnalysisETLBusiPack extends BIBusinessPackage<AnalysisBusiTable> {

    public AnalysisETLBusiPack(String id, String name, BIUser user, long position) {
        super(new BIPackageID(id), new BIPackageName(name), user, position);
    }

    @Override
    protected AnalysisBusiTable createTable() {
        return new AnalysisBusiTable("", owner.getUserId());
    }

}