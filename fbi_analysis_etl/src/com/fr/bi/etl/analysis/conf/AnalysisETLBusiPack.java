package com.fr.bi.etl.analysis.conf;

import com.finebi.cube.conf.pack.data.BIBusinessPackage;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.finebi.cube.conf.pack.data.BIPackageName;
import com.fr.bi.base.BIUser;
import com.fr.general.ComparatorUtils;


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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AnalysisETLBusiPack)) {
            return false;
        }

        AnalysisETLBusiPack that = (AnalysisETLBusiPack) o;

        return ComparatorUtils.equals(ID, that.ID) && ComparatorUtils.equals(owner, that.owner);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (ID != null ? ID.hashCode() : 0);
        result = prime * result + (owner != null ? owner.hashCode() : 0);
        return result;
    }

}