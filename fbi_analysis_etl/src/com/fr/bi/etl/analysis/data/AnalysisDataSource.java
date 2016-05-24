package com.fr.bi.etl.analysis.data;


import com.finebi.cube.conf.datasource.BIDataSource;

/**
 * Created by 小灰灰 on 2015/12/14.
 */
public interface AnalysisDataSource extends BIDataSource<AnalysisCubeTableSource> {
    void addCoreSource(AnalysisCubeTableSource source);
}