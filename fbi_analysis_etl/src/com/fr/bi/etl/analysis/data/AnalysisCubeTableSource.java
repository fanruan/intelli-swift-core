package com.fr.bi.etl.analysis.data;

import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.List;
import java.util.Set;

/**
 * Created by 小灰灰 on 2015/12/14.
 */
public interface AnalysisCubeTableSource extends CubeTableSource {
    UserCubeTableSource createUserTableSource(long userId);

    List<AnalysisETLSourceField> getFieldsList();

    void getSourceUsedAnalysisETLSource(Set<AnalysisCubeTableSource> set);
}