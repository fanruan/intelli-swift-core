package com.fr.bi.etl.analysis.manager;

import com.fr.stable.bridge.StableFactory;

/**
 * Created by 小灰灰 on 2016/4/7.
 */
public class BIAnalysisETLManagerCenter {
    public static BIAnalysisBusiPackManagerProvider getBusiPackManager(){
        return StableFactory.getMarkedObject(BIAnalysisBusiPackManagerProvider.XML_TAG, BIAnalysisBusiPackManagerProvider.class);
    }

    public static BIAnalysisDataSourceManagerProvider getDataSourceManager(){
        return StableFactory.getMarkedObject(BIAnalysisDataSourceManagerProvider.XML_TAG, BIAnalysisDataSourceManagerProvider.class);
    }
}
