package com.fr.bi.etl.analysis.data;

import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.List;

/**
 * Created by 小灰灰 on 2015/12/14.
 */
public interface AnalysisCubeTableSource extends CubeTableSource {
    UserCubeTableSource createUserTableSource(long userId);

    List<AnalysisETLSourceField> getFieldsList();
}