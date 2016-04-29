BI.AnalysisETLOperatorSelectNoneData = BI.inherit(BI.AnalysisETLOperatorAbstractPane, {
    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLOperatorSelectNoneData.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-analysis-etl-operator-select-data",
            value:ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.CHOOSE_NONE_FIELD,
            showContent :false
        })
    },

    _initController : function(){
        return BI.AnalysisETLOperatorSelectNoneDataController
    }
})

$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.SELECT_NONE_DATA, BI.AnalysisETLOperatorSelectNoneData);