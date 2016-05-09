/**
 * Created by 小灰灰 on 2016/4/15.
 */
BI.AnalysisETLOperatorSelectNoneDataPane = BI.inherit(BI.Widget, {
    populate : function (model, controller) {
    }
})
$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.SELECT_NONE_DATA +  ETLCst.ANALYSIS_TABLE_PANE, BI.AnalysisETLOperatorSelectNoneDataPane);
$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.SELECT_DATA +  ETLCst.ANALYSIS_TABLE_PANE, BI.AnalysisETLOperatorSelectNoneDataPane);