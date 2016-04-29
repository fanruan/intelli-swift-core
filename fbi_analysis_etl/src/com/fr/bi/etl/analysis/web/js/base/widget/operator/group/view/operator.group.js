BI.AnalysisETLOperatorGroup = FR.extend(BI.AnalysisETLOperatorAbstractPane, {
    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLOperatorGroup.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-analysis-etl-operator-select-data",
            value:ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.GROUP
        })
    },


    _init: function () {
        BI.AnalysisETLOperatorGroup.superclass._init.apply(this, arguments)
    }
})

$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.GROUP_SUMMARY, BI.AnalysisETLOperatorGroup);