/**
 * Created by windy on 2017/4/6.
 */
BI.AnalysisETLOperatorFilter = BI.inherit(BI.AnalysisETLOperatorAbstractPane, {

    props: {
        extraCls: "bi-analysis-etl-operator-select-data",
        value:ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.FILTER
    },

    _init: function () {
        BI.AnalysisETLOperatorFilter.superclass._init.apply(this, arguments)
    }

});

BI.shortcut(ETLCst.ANALYSIS_ETL_PAGES.FILTER, BI.AnalysisETLOperatorFilter);