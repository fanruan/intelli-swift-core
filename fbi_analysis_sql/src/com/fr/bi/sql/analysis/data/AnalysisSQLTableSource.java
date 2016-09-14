package com.fr.bi.sql.analysis.data;

import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.List;
import java.util.Set;

/**
 * Created by 小灰灰 on 2015/12/14.
 */
public interface AnalysisSQLTableSource extends CubeTableSource {
    List<AnalysisSQLSourceField> getFieldsList();

    String toSql();

    void getSourceUsedAnalysisSQLSource(Set<AnalysisSQLTableSource> sources, Set<AnalysisSQLTableSource> helper);
}