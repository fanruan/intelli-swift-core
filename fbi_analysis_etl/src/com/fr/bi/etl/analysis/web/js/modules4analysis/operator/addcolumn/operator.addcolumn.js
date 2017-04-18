/**
 * Created by windy on 2017/4/10.
 */
BI.AnalysisETLOperatorAddColumn = BI.inherit(BI.AnalysisOperatorAbstractPane, {
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
BI.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN, BI.AnalysisETLOperatorAddColumn);