package com.fr.bi.etl.analysis.data;

import com.fr.bi.stable.data.source.ICubeTableSource;

import java.util.List;

/**
 * Created by 小灰灰 on 2015/12/14.
 */
public interface AnalysisCubeTableSource extends ICubeTableSource {
    UserCubeTableSource createUserTableSource(long userId);

    List<AnalysisETLSourceField> getFieldsList();
}