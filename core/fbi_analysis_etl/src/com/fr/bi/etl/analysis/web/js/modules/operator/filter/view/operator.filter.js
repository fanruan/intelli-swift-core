BI.AnalysisETLOperatorFilter = FR.extend(BI.AnalysisETLOperatorAbstractPane, {
    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLOperatorFilter.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-analysis-etl-operator-select-data",
            value:ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.FILTER
        })
    },

    _init: function () {
        BI.AnalysisETLOperatorFilter.superclass._init.apply(this, arguments)
    }

})

$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.FILTER, BI.AnalysisETLOperatorFilter);