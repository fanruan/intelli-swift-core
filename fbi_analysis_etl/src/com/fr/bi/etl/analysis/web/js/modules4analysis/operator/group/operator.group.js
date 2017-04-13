/**
 * Created by windy on 2017/4/7.
 */
BI.AnalysisETLOperatorGroup = BI.inherit(BI.AnalysisOperatorAbstarctPane, {

    props: {
        extraCls: "bi-analysis-etl-operator-select-data",
        value:ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.GROUP
    },

    _init: function () {
        BI.AnalysisETLOperatorGroup.superclass._init.apply(this, arguments)
    }
});

BI.shortcut(ETLCst.ANALYSIS_ETL_PAGES.GROUP_SUMMARY, BI.AnalysisETLOperatorGroup);