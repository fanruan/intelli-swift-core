BI.AnalysisETLOperatorAddColumn = FR.extend(BI.AnalysisETLOperatorAbstractPane, {
    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLOperatorAddColumn.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-analysis-etl-operator-select-data",
            value:ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.ADD_COLUMN
        })
    },


    _init: function () {
        BI.AnalysisETLOperatorAddColumn.superclass._init.apply(this, arguments)
    }

})
$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN, BI.AnalysisETLOperatorAddColumn);