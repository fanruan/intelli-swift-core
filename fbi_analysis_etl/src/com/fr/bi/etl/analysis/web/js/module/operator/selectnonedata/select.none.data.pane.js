/**
 * Created by windy on 2017/4/11.
 */
BI.AnalysisETLOperatorSelectNoneDataPane = BI.inherit(BI.Widget, {
    populate : function (m, options) {
    }
})
BI.shortcut(ETLCst.ANALYSIS_ETL_PAGES.SELECT_NONE_DATA +  ETLCst.ANALYSIS_TABLE_PANE, BI.AnalysisETLOperatorSelectNoneDataPane);
BI.shortcut(ETLCst.ANALYSIS_ETL_PAGES.SELECT_DATA +  ETLCst.ANALYSIS_TABLE_PANE, BI.AnalysisETLOperatorSelectNoneDataPane);