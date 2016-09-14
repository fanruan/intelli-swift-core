/**
 * Created by 小灰灰 on 2016/4/15.
 */
BI.AnalysisETLOperatorSelectNoneDataPane = BI.inherit(BI.Widget, {
    populate : function (model, controller) {
    }
})
$.shortcut(SQLCst.ANALYSIS_ETL_PAGES.SELECT_NONE_DATA +  SQLCst.ANALYSIS_TABLE_PANE, BI.AnalysisETLOperatorSelectNoneDataPane);
$.shortcut(SQLCst.ANALYSIS_ETL_PAGES.SELECT_DATA +  SQLCst.ANALYSIS_TABLE_PANE, BI.AnalysisETLOperatorSelectNoneDataPane);