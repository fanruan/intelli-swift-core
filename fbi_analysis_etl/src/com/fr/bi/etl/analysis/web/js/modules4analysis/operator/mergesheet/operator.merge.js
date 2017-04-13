/**
 * Created by windy on 2017/4/12.
 */
BI.AnalysisETLOperatorMergeSheet = BI.inherit(BI.AnalysisETLOperatorAbstractPane, {
    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLOperatorMergeSheet.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-analysis-etl-operator-select-data",
            value:ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.MERGED_TABLE,
        })
    },


    _init: function () {
        BI.AnalysisETLOperatorMergeSheet.superclass._init.apply(this, arguments);
        var self = this;
        this.center.on(BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_CHANGE, function () {
            self.fireEvent(BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_CHANGE, arguments)
        })

    }


})

BI.shortcut(ETLCst.ANALYSIS_ETL_PAGES.MERGE_SHEET, BI.AnalysisETLOperatorMergeSheet);