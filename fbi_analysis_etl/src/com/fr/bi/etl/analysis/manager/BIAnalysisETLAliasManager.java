package com.fr.bi.etl.analysis.manager;

import com.finebi.cube.conf.trans.BIAliasManager;

import java.io.File;

/**
 * Created by 小灰灰 on 2016/6/8.
 */
public class BIAnalysisETLAliasManager extends BIAliasManager {
    @Override
    public String managerTag() {
        return "BIAnalysisETLAliasManager";
    }

    @Override
    public String persistUserDataName(long key) {
        return "sue" + File.separator + "alisa" + key;
    }


}
