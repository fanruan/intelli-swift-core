package com.fr.bi.etl.analysis.data;

import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.etl.analysis.monitor.SimpleTable;
import com.fr.bi.etl.analysis.monitor.TableRelationTree;
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

    void getSourceNeedCheckSource(Set<AnalysisCubeTableSource> set);

    void refreshWidget();

    Set<BIWidget> getWidgets();

    void reSetWidgetDetailGetter();

    void getParentAnalysisBaseTableIds(Set<SimpleTable> set);

    TableRelationTree getAllProcessAnalysisTablesWithRelation();


    void resetTargetsMap();
}