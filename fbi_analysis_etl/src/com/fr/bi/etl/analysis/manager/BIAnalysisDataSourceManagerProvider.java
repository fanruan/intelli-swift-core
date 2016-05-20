package com.fr.bi.etl.analysis.manager;

import com.fr.bi.conf.provider.BIDataSourceManagerProvider;
import com.fr.bi.etl.analysis.data.AnalysisCubeTableSource;

/**
 * Created by 小灰灰 on 2015/12/14.
 */
public interface BIAnalysisDataSourceManagerProvider extends BIDataSourceManagerProvider<AnalysisCubeTableSource>{
    String XML_TAG = "BIAnalysisDataSourceManager";

    void addSource(AnalysisCubeTableSource source, long userId);
}