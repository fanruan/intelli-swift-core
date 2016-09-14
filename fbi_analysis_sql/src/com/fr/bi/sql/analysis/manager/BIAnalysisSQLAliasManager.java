package com.fr.bi.sql.analysis.manager;

import com.finebi.cube.conf.trans.BIAliasManager;

import java.io.File;

/**
 * Created by 小灰灰 on 2016/6/8.
 */
public class BIAnalysisSQLAliasManager extends BIAliasManager {
    @Override
    public String managerTag() {
        return "BIAnalysisSQLAliasManager";
    }

    @Override
    public String persistUserDataName(long key) {
        return "sus" + File.separator + "alisa" + key;
    }


}
