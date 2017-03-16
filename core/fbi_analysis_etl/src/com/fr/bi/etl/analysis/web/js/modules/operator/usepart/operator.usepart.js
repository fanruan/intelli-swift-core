BI.AnalysisETLOperatorUsePart = FR.extend(BI.AnalysisETLOperatorAbstractPane, {
    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLOperatorUsePart.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-analysis-etl-operator-select-data",
            value:ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.USE_PART_FIELDS
        })
    },


    _initController : function () {
        return BI.AnalysisETLOperatorUsePartController;
    },

    _initModel : function () {
        return BI.AnalysisETLOperatorUsePartPaneModel;
    }
})

$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.USE_PART_FIELDS, BI.AnalysisETLOperatorUsePart);