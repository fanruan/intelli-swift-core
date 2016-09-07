package com.fr.bi.sql.analysis.manager;

import com.finebi.cube.conf.BIAliasManagerProvider;
import com.fr.stable.bridge.StableFactory;

/**
 * Created by 小灰灰 on 2016/4/7.
 */
public class BIAnalysisSQLManagerCenter {
    public static BIAnalysisSQLBusiPackManagerProvider getBusiPackManager(){
        return StableFactory.getMarkedObject(BIAnalysisSQLBusiPackManagerProvider.XML_TAG, BIAnalysisSQLBusiPackManagerProvider.class);
    }

    public static BIAnalysisSQLDataSourceManagerProvider getDataSourceManager(){
        return StableFactory.getMarkedObject(BIAnalysisSQLDataSourceManagerProvider.XML_TAG, BIAnalysisSQLDataSourceManagerProvider.class);
    }

    public static UserSQLDataManagerProvider getUserETLCubeManagerProvider(){
        return StableFactory.getMarkedObject(UserSQLDataManagerProvider.class.getName(), UserSQLDataManagerProvider.class);
    }

    public static BIAliasManagerProvider getAliasManagerProvider(){
        return StableFactory.getMarkedObject(BIAnalysisSQLAliasManager.class.getName(), BIAliasManagerProvider.class);
    }
}
